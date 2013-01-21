package mc.alk.battlePorts.listeners;

import java.util.HashMap;

import mc.alk.battlePorts.controllers.MessageController;
import mc.alk.battlePorts.controllers.PortController;
import mc.alk.battlePorts.objects.Port;
import mc.alk.battlePorts.objects.PortOption;
import mc.alk.battlePorts.serializers.PlayerSerializer;
import mc.alk.battlePorts.util.TeleportUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


/**
 *
 * @author alkarin
 *
 */
public class BPPlayerListener implements Listener  {
	PortController pc;
	///time before resetting hashmap, idea is to keep it small
	/// This means that some people will get messages before every SEND_MSG_TIME
	/// But i think its a fine tradeoff
	static long last_reset = System.currentTimeMillis();
	static final long RESET_TIME = 60000;

	static final long SEND_MSG_TIME = 3000; ///time before resending a requirement message
	HashMap<Player,Long> lastMsgTime = new HashMap<Player,Long>();

	public BPPlayerListener(PortController pc){
		this.pc = pc;
	}

	@EventHandler(ignoreCancelled=false)
	public void onPlayerMove(PlayerMoveEvent e){
		/// Same block, return out
		if(		e.getFrom().getBlockX()==e.getTo().getBlockX()&&
				e.getFrom().getBlockZ()==e.getTo().getBlockZ()&&
				e.getFrom().getBlockY()==e.getTo().getBlockY()){
			return;}
		Player player = e.getPlayer();
		/// cant handle people inside of vehicles
		if (player.isInsideVehicle()) return;
		Port port = pc.getPort(e.getTo());
		if (port != null){
			String requirementMsg = port.meetsRequirements(player);
			if (requirementMsg == null){
				/// Do we have a first time use portal
				if (port.hasOption(PortOption.FIRSTUSE) && port.getDestination2() != null){
					if (!PlayerSerializer.hasPlayer(port, player)){
						TeleportUtil.teleportPlayer(player, port.getDestination2());
						PlayerSerializer.addUse(port,player);
						return;
					}
				}
				/// normal port
				TeleportUtil.teleportPlayer(player, port.getDestination());
			} else {
				/// only send a player a message every SEND_MSG_TIME
				long now = System.currentTimeMillis();
				Long time = lastMsgTime.get(player);
				/// Try and keep hashmap small to reduce footprint
				if (now - last_reset > RESET_TIME){
					lastMsgTime = new HashMap<Player,Long>();
					last_reset = now;
				}
				if (time == null || (now - time > SEND_MSG_TIME)){
					MessageController.sendMessage(player, requirementMsg);
					lastMsgTime.put(player, now);
				}
			}
		}

	}

}
