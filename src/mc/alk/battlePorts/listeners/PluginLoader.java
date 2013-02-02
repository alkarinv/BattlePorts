package mc.alk.battlePorts.listeners;

import mc.alk.battlePorts.BattlePorts;
import mc.alk.battlePorts.controllers.PermissionController;
import mc.alk.battlePorts.util.Log;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 *
 * @author alkarin
 *
 */
public class PluginLoader implements Listener{

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getName() == "WorldEdit")
			loadWorldEdit();
		else if (event.getPlugin().getName() == "WorldGuard")
			loadWorldGuard();
		else if (event.getPlugin().getName() == "Vault")
			loadVault();

	}

	public void loadPlugins() {
		loadWorldEdit();
		loadWorldGuard();
		loadVault();
	}

	public void loadWorldEdit(){
		if (BattlePorts.wep == null){
			Plugin pl = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
			if (pl != null) {
				BattlePorts.wep = ((WorldEditPlugin) pl);
			} else {
				Log.info("[BattlePorts] WorldEdit not detected");
			}
		}
	}

	public void loadWorldGuard(){
		if (BattlePorts.wgp == null){
			Plugin pl = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
			if (pl != null) {
				BattlePorts.wgp = ((WorldGuardPlugin) pl);
			} else {
				Log.info("[BattlePorts] WorldGuard not detected");
			}
		}
	}


	public void loadVault(){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
		if (plugin != null ){
			/// Load Vault Perms
			RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
			if (chatProvider!=null && chatProvider.getProvider() != null){
				PermissionController.setPermission(chatProvider.getProvider());
			}
		}
	}
}