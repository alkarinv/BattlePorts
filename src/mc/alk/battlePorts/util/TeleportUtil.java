package mc.alk.battlePorts.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportUtil {
	public static void teleportPlayer(final Player p, final Location loc){
		/// Load the chunk if its not already loaded
		try {
			if(!loc.getWorld().isChunkLoaded(loc.getBlock().getChunk())){
				loc.getWorld().loadChunk(loc.getBlock().getChunk());}
		} catch (Exception e){}

		p.teleport(loc);
	}
}
