package mc.alk.battlePorts.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mc.alk.battlePorts.controllers.PermissionController;
import mc.alk.battlePorts.controllers.PortController;
import mc.alk.battlePorts.objects.BoundingBox;
import mc.alk.battlePorts.objects.PlayerInfo;
import mc.alk.battlePorts.objects.PlayerPort;
import mc.alk.battlePorts.objects.Port;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 *
 * @author alkarin
 *
 */
public class PortalExecutor extends CustomPortExecutor
{
	private WorldEditPlugin wep;
	private WorldGuardPlugin wgp;
	private static final String PORT_ALTER_DEST = "&6/portal dest <portname>";
	private static final String PORT_ALTER_SOURCE = "&6/portal source <portname>";
	private static final String PORT_INFO = "&6/portal info <portname>";
	private static final String PORT_DELETE = "&6/portal delete <portname>";
	private static final String PORT_RENAME = "&6/portal rename <portname> <new name>";
	private static final String PORT_LIST = "&6/portal list&e: shows your ports";
	private static final String PORT_CREATE = "&6/portal create <port name> [option1]...[option x]";

	public PortalExecutor(PortController pc)
	{
		super(pc);
	}

	public void setWorldPlugins(WorldEditPlugin wep, WorldGuardPlugin wgp)
	{
		this.wep = wep;
		this.wgp = wgp;
	}

	@Override
	protected void showHelp(CommandSender sender, Command command)
	{
		sendMessage(sender, PORT_CREATE);
		sendMessage(sender, PORT_ALTER_DEST);
		sendMessage(sender, PORT_DELETE);
		sendMessage(sender, PORT_RENAME);
		sendMessage(sender, PORT_LIST);
		// sendMessage(sender,PORT_LIST2);
		// sendMessage(sender,PORT_SET_OPTIONS);
		// sendMessage(sender,PORT_ADD_OPTIONS);
		sendMessage(sender, PORT_INFO);
	}

	@MCCommand(cmds = { "rename" }, alphanum = { 2 },usage = PORT_RENAME)
	public boolean portRename(Player p, PlayerPort port, String newName)
	{
		String oldName = port.getPortName();

		String realName = PlayerPort.getName(p.getName(), newName);
		pc.rename(port, realName);
		return sendMessage(p, "&ePort &6" + oldName + "&e renamed &6" + newName);
	}

	@MCCommand(cmds = { "dest", "alterDest" },usage = PORT_ALTER_DEST)
	public boolean portAlterDest(Player p, PlayerPort port)
	{
		if (!wgp.canBuild(p, p.getLocation()))
		{
			return sendMessage(p,
					"&cThe portal destination must be somewhere you have build rights to");
		}
		try
		{
			port.setDestination(p.getLocation());
		}
		catch (Exception e)
		{
			return sendMessage(p,
					"&4Cant set destination to be within the source");
		}
		return sendMessage(p, "&ePort destination set to this location");
	}

	@MCCommand(cmds = { "source", "alterSource" }, usage = PORT_ALTER_SOURCE)
	public boolean portAlterSource(Player p, PlayerPort port)
	{
		Selection sel = wep.getSelection(p);
		if (sel == null)
			return sendMessage(p, ChatColor.RED
					+ "Please select the protection area first.");

		int area = PermissionController.getAvailableArea(p);
		if (sel.getArea() > area)
		{
			return sendMessage(p, "&cYou can only make a portal of size &6"
					+ area + "&c you selected &6" + sel.getArea());
		}

		if (!wgp.canBuild(p, sel.getMinimumPoint())
				|| !wgp.canBuild(p, sel.getMaximumPoint()))
		{
			return sendMessage(p,
					"&cYou can't create a portal in a region you cant build in");
		}
		BoundingBox bb = new BoundingBox(sel.getMinimumPoint(),
				sel.getMaximumPoint());
		if (bb.contains(port.getDestination()))
		{
			return sendMessage(p,
					"&4Cant set destination to be within the source");
		}
		try
		{
			port.setSourceBox(sel.getMinimumPoint(), sel.getMaximumPoint());
		}
		catch (Exception e)
		{
			return sendMessage(p,
					"&4Cant set destination to be within the source");
		}
		pc.deletePort(port);
		pc.addPort(port);
		return sendMessage(p, "&ePort source set to the new location");
	}

	@MCCommand(cmds = { "info" }, usage = PORT_INFO)
	public boolean portInfo(Player p, PlayerPort port)
	{
		sendMessage(
				p,
				"&e - &2" + port.getPortName() + "&e[&6"
						+ port.getWorld().getName() + " " + port.getSourceStr()
						+ "&e] -> [" + port.getDestStr() + "&e]"
						+ port.getOptionString());
		return true;
	}

	@MCCommand(cmds = { "list" }, usage = PORT_LIST)
	public boolean portList(Player p)
	{
		Collection<PlayerPort> ports = pc.getPlayerPortals(p);
		if (ports == null || ports.isEmpty())
		{
			return sendMessage(p, "&eNo portals were found");
		}
		HashMap<World, List<PlayerPort>> wmap = new HashMap<World, List<PlayerPort>>();
		for (PlayerPort port : ports)
		{
			List<PlayerPort> theports = wmap.get(port.getWorld());
			if (theports == null)
			{
				theports = new ArrayList<PlayerPort>();
				wmap.put(port.getWorld(), theports);
			}
			theports.add(port);
		}
		int maxPorts = PermissionController.getAvailablePorts(p);

		for (World w : wmap.keySet())
		{
			sendMessage(p, "&e--- Portals in &6" + w.getName() + "&e ---");
			List<PlayerPort> theports = wmap.get(w);
			for (PlayerPort port : theports)
			{
				sendMessage(
						p,
						"&2" + port.getPortName() + " &e[&6"
								+ port.getSourceStr() + "&e] -> ["
								+ port.getDestStr() + "&e]"
								+ port.getOptionString());
			}
		}
		sendMessage(p, "&eYou are using &6" + ports.size() + "/" + maxPorts
				+ "&e ports");
		return true;
	}

	@MCCommand(cmds = { "delete" }, usage = PORT_DELETE)
	public boolean portDelete(Player p, PlayerPort port)
	{
		pc.deletePort(port);
		return sendMessage(p, "&eYou have deleted port &6" + port.getPortName());
	}

	@MCCommand(cmds = { "create" }, usage = PORT_CREATE,
			perm = "portal.create")
	public boolean portCreate(Player p, String name)
	{
		Port port = pc.getPlayerPort(p, name);
		if (port != null)
		{
			return sendMessage(p,
					"&4There is already a port with that name. Please pick a unique name.");
		}
		PlayerInfo pi = pc.getPlayerInfo(p);
		int maxPorts = PermissionController.getAvailablePorts(p);
		if (maxPorts == 0)
		{
			return sendMessage(p, "&cYou can't build ports ");
		}
		if (pi.nports >= maxPorts)
		{
			return sendMessage(p, "&cYou already have &6" + pi.nports + "/"
					+ maxPorts + "&c ports");
		}

		Selection sel = wep.getSelection(p);
		if (sel == null)
			return sendMessage(p, ChatColor.RED
					+ "Please select the protection area first.");

		// / Why doesnt it like cuboid anymore?
		// if (!(sel instanceof CuboidRegion)) {
		// return sendMessage(sender, ChatColor.RED +
		// "Only cuboid regions are supported.");
		// }
		int area = PermissionController.getAvailableArea(p);
		if (sel.getArea() > area)
		{
			return sendMessage(p, "&cYou can only make a portal of size &6"
					+ area + "&c you selected &6" + sel.getArea());
		}

		if (!wgp.canBuild(p, sel.getMinimumPoint())
				|| !wgp.canBuild(p, sel.getMaximumPoint()))
		{
			return sendMessage(p,
					"&cYou can't create a portal in a region you cant build in");
		}
		if (!wgp.canBuild(p, p.getLocation()))
		{
			return sendMessage(p,
					"&cThe portal destination must be somewhere you have build rights to");
		}

		port = new PlayerPort(p.getName(), name);
		try
		{
			port.setSourceBox(p.getWorld(), sel.getMinimumPoint(),
					sel.getMaximumPoint());
		}
		catch (Exception e)
		{
			return sendMessage(p,
					"&4Source must have corners in the same world");
		}
		try
		{
			port.setDestination(p.getLocation());
		}
		catch (Exception e)
		{
			return sendMessage(p,
					"&4Cant set destination to be within the source");
		}
		pc.addPort(port);
		return sendMessage(p, "&2You created port &6" + name
				+ "&2 successfully.");
	}

}
