package mc.alk.battlePorts.listeners;

import mc.alk.battlePorts.BattlePorts;
import mc.alk.battlePorts.util.Log;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 * 
 * @author alkarin
 * 
 */
public class PluginLoader
{

	public static void loadPlugins()
	{
		if (BattlePorts.wep == null)
		{
			Plugin pl = Bukkit.getServer().getPluginManager()
					.getPlugin("WorldEdit");
			if (pl != null)
			{
				BattlePorts.wep = ((WorldEditPlugin) pl);
			}
			else
			{
				Log.info("[BattlePorts] WorldEdit not detected");
			}
		}
		if (BattlePorts.wgp == null)
		{
			Plugin pl = Bukkit.getServer().getPluginManager()
					.getPlugin("WorldGuard");
			if (pl != null)
			{
				BattlePorts.wgp = ((WorldGuardPlugin) pl);
			}
			else
			{
				Log.info("[BattlePorts] WorldGuard not detected");
			}
		}
	}
}
