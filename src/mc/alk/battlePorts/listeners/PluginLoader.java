package mc.alk.battlePorts.listeners;

import org.bukkit.event.Listener;

/**
 *
 * @author alkarin
 *
 */
public class PluginLoader implements Listener{
//
//	@EventHandler
//	public void onPluginEnable(PluginEnableEvent event) {
//		if (event.getPlugin().getName() == "WorldEdit")
//			loadWorldEdit();
//		else if (event.getPlugin().getName() == "WorldGuard")
//			loadWorldGuard();
//		else if (event.getPlugin().getName() == "Vault")
//			loadVault();
//
//	}
//
//	public void loadPlugins() {
//		loadWorldEdit();
//		loadWorldGuard();
//		loadVault();
//	}
//
//	public void loadWorldEdit(){
//		if (BattlePorts.wep == null){
//			Plugin pl = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
//			if (pl != null) {
//				BattlePorts.wep = ((WorldEditPlugin) pl);
//			} else {
//				Log.info("[BattlePorts] WorldEdit not detected");
//			}
//		}
//	}
//
//	public void loadWorldGuard(){
//		if (BattlePorts.wgp == null){
//			Plugin pl = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
//			if (pl != null) {
//				BattlePorts.wgp = ((WorldGuardPlugin) pl);
//			} else {
//				Log.info("[BattlePorts] WorldGuard not detected");
//			}
//		}
//	}
//
//
//	public void loadVault(){
//		Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
//		if (plugin != null ){
//			/// Load Vault Perms
//			RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
//			if (chatProvider!=null && chatProvider.getProvider() != null){
//				PermissionController.setPermission(chatProvider.getProvider());
//			}
//		}
//	}
}