package mc.alk.battlePorts;

import java.io.File;

import mc.alk.battlePorts.controllers.MessageController;
import mc.alk.battlePorts.controllers.PermissionController;
import mc.alk.battlePorts.controllers.PortController;
import mc.alk.battlePorts.executors.PortExecutor;
import mc.alk.battlePorts.executors.PortalExecutor;
import mc.alk.battlePorts.listeners.BPPlayerListener;
import mc.alk.battlePorts.listeners.PluginLoader;
import mc.alk.battlePorts.serializers.ConfigSerializer;
import mc.alk.battlePorts.serializers.PlayerSerializer;
import mc.alk.battlePorts.serializers.PortSerializer;
import mc.alk.battlePorts.util.Log;
import mc.alk.battlePorts.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class BattlePorts extends JavaPlugin{

	static private String pluginname;
	static private String version;
	static private BattlePorts plugin;
	public static WorldEditPlugin wep;
	public static WorldGuardPlugin wgp;

	private static final PortController portController = new PortController();

	private PortExecutor portExecutor = new PortExecutor(portController);
	private PortalExecutor portalExecutor = null;
	private static final BPPlayerListener playerListener = new BPPlayerListener(portController);
	private static final PortSerializer portSerializer = new PortSerializer(portController);
	private static final ConfigSerializer configSerializer = new ConfigSerializer();
	private static final PlayerSerializer playerSerializer = new PlayerSerializer();

	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = plugin.getDescription();
		pluginname = pdfFile.getName();
		version = pdfFile.getVersion();
		File dir = this.getDataFolder();
		if (!dir.exists()){
			dir.mkdirs();}

		PluginLoader pluginListener = new PluginLoader();
		pluginListener.loadPlugins();
		if (wep == null){
			Log.err("[BattlePorts] Couldnt load WorldEdit, BattlePorts shutting down");
			return;
		}

		MessageController mc = new MessageController();
		configSerializer.setConfig(Util.load(getClass().getResourceAsStream("/default_files/config.yml"),dir.getPath() +"/config.yml"));
		portSerializer.setConfig(Util.load(getClass().getResourceAsStream("/default_files/ports.yml"),dir.getPath() +"/ports.yml"));
		mc.setConfig(Util.load(getClass().getResourceAsStream("/default_files/config.yml"),dir.getPath() +"/config.yml"));
		playerSerializer.setConfig(Util.load(getClass().getResourceAsStream("/default_files/players.yml"),dir.getPath() +"/players.yml"));

		load();
		getCommand("port").setExecutor(portExecutor);
		portExecutor.setWorldEditPlugin(wep);

		if (!PermissionController.hasChat()){
			Log.err("[BattlePorts] needs Vault for Player portals but was not found.  Disabling player portal commands");
		} else {
			portalExecutor = new PortalExecutor(portController);
		}
		if (wgp == null || wep == null || portalExecutor == null){
			Log.err("[BattlePorts] needs WorldGuard for Player portals but WG was not found.  Disabling player portal commands");
		} else {
			portalExecutor.setWorldPlugins(wep,wgp);
			getCommand("portal").setExecutor(portalExecutor);
		}

		Bukkit.getPluginManager().registerEvents(pluginListener, this);
		Bukkit.getPluginManager().registerEvents(playerListener, this);

		Log.info("[" + pluginname + " v" + version +"]"  + " enabled!");
	}

	@Override
	public void onDisable() {
		save();
	}

	public static String getVersion() {
		return "[" + pluginname + " v" + version +"]";
	}

	public static WorldEditPlugin getWorldEdit() {return wep;}

	public static PortController getPortController() {
		return portController;
	}

	public static void load() {
		configSerializer.loadAll();
		portSerializer.loadAll();
		playerSerializer.loadAll();
	}
	public static void save() {
		portSerializer.saveAll();
		playerSerializer.saveAll();
	}
}
