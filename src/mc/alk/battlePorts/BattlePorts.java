package mc.alk.battlePorts;

import java.io.File;
import java.util.ArrayList;

import mc.alk.battlePorts.controllers.MessageController;
import mc.alk.battlePorts.controllers.PermissionController;
import mc.alk.battlePorts.controllers.PortController;
import mc.alk.battlePorts.executors.PortExecutor;
import mc.alk.battlePorts.executors.PortalExecutor;
import mc.alk.battlePorts.listeners.BPPlayerListener;
import mc.alk.battlePorts.serializers.ConfigSerializer;
import mc.alk.battlePorts.serializers.PlayerSerializer;
import mc.alk.battlePorts.serializers.PortSerializer;
import mc.alk.battlePorts.util.Log;
import mc.alk.battlePorts.util.Util;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class BattlePorts extends JavaPlugin
{

	// Main Class
	private static String pluginname;
	private static String version;
	private BattlePorts plugin;
	private PluginDescriptionFile pdfFile = null;
	private File dir = null;
	private static String logName = "[BattlePorts]";

	// External Plugins
	private boolean isVaultEnabled = false;
	@SuppressWarnings("unused")
	private boolean isWorldEditEnabled = false;
	@SuppressWarnings("unused")
	private boolean isWorldGuardEnabled = false;
	Plugin vault = null;
	Plugin worldedit = null;
	Plugin worldguard = null;
	private static WorldEditPlugin wep;
	private static WorldGuardPlugin wgp;
	private final ArrayList<String> pluginsEnabled = new ArrayList<String>();

	// Controllers
	private static PortController portController = null;
	private static final MessageController mc = new MessageController();

	// Listener
	private static BPPlayerListener playerListener = null;

	// Serializers
	private static PortSerializer portSerializer = null;
	private static final ConfigSerializer configSerializer = new ConfigSerializer();
	private static final PlayerSerializer playerSerializer = new PlayerSerializer();

	// Executors
	private PortExecutor portExecutor = null;
	private PortalExecutor portalExecutor = null;

	@Override
	public void onDisable()
	{
		saveSerializers();
	}

	@Override
	public void onEnable()
	{
		registerFirstPriority();
		handleExternalPlugins();
		loadSerializers();
		configLoading();
		registerListeners();
		registerExecutors();
		Log.info("[" + pluginname + " v" + version + "]" + " enabled!");
		Log.info("With plugins: " + getPluginsEnabled().toString());
	}

	private void registerExecutors()
	{
		if (!isVaultEnabled)
		{
			return;
		}
		getCommand("port").setExecutor(portExecutor);
		getCommand("portal").setExecutor(portalExecutor);
	}

	private void registerListeners()
	{
		if (!isVaultEnabled)
		{
			return;
		}
		playerListener = new BPPlayerListener(portController);
		Bukkit.getServer().getPluginManager()
				.registerEvents(playerListener, this);
	}

	private void configLoading()
	{
		configSerializer.setConfig(Util.load(
				getClass().getResourceAsStream("/default_files/config.yml"),
				dir.getPath() + "/config.yml"));
		portSerializer.setConfig(Util.load(
				getClass().getResourceAsStream("/default_files/ports.yml"),
				dir.getPath() + "/ports.yml"));
		mc.setConfig(Util.load(
				getClass().getResourceAsStream("/default_files/config.yml"),
				dir.getPath() + "/config.yml"));
		playerSerializer.setConfig(Util.load(
				getClass().getResourceAsStream("/default_files/players.yml"),
				dir.getPath() + "/players.yml"));
	}

	public void handleExternalPlugins()
	{
		if (vault != null)
		{
			loadVault();
			isVaultEnabled = true;
			pluginsEnabled.add("Vault");
			portController = new PortController();
			portSerializer = new PortSerializer(portController, this);
			portalExecutor = new PortalExecutor(portController);
			portExecutor = new PortExecutor(portController, this);
		}
		if (worldedit != null)
		{
			isWorldEditEnabled = true;
			wep = (WorldEditPlugin) worldedit;
			portExecutor.setWorldEditPlugin(wep);
			pluginsEnabled.add("WorldEdit");
		}
		if (worldguard != null)
		{
			isWorldGuardEnabled = true;
			wgp = (WorldGuardPlugin) worldguard;
			pluginsEnabled.add("WorldGuard");
		}
		if (worldguard != null && worldedit != null)
		{
			portalExecutor.setWorldPlugins(wep, wgp);
		}
		if (worldguard == null || worldedit == null || vault == null)
		{
			Log.info(logName
					+ " Some features may be disabled because we do not have all the plugins needed!");
		}
	}

	private void registerFirstPriority()
	{
		plugin = this;
		pdfFile = plugin.getDescription();
		pluginname = pdfFile.getName();
		version = pdfFile.getVersion();
		dir = getDataFolder();
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		vault = getServer().getPluginManager().getPlugin("Vault");
		worldedit = getServer().getPluginManager().getPlugin("WorldEdit");
		worldguard = getServer().getPluginManager().getPlugin("WorldGuard");
	}

	private boolean loadVault()
	{
		Vault vp = (Vault) Bukkit.getServer().getPluginManager()
				.getPlugin("Vault");
		if (vp != null)
		{
			RegisteredServiceProvider<Chat> chatProvider = Bukkit
					.getServicesManager().getRegistration(Chat.class);
			if (chatProvider == null || chatProvider.getProvider() == null)
			{
				return false;
			}
			else
			{
				PermissionController.setPermission(chatProvider.getProvider());
			}
		}
		return true;
	}

	public String getVersion()
	{
		return "[" + pluginname + " v" + version + "]";
	}

	public WorldEditPlugin getWorldEdit()
	{
		return wep;
	}

	public WorldGuardPlugin getWorldGuard()
	{
		return wgp;
	}

	public PortController getPortController()
	{
		return portController;
	}

	public void loadSerializers()
	{
		configSerializer.loadAll();
		portSerializer.loadAll();
		playerSerializer.loadAll();
	}

	public void saveSerializers()
	{
		portSerializer.saveAll();
		playerSerializer.saveAll();
	}

	public ArrayList<String> getPluginsEnabled()
	{
		return pluginsEnabled;
	}
}
