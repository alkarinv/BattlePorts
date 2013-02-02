package mc.alk.battlePorts.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mc.alk.battlePorts.BattlePorts;
import mc.alk.battlePorts.controllers.PortController;
import mc.alk.battlePorts.objects.BoundingBox;
import mc.alk.battlePorts.objects.Port;
import mc.alk.battlePorts.objects.PortOption;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

/**
 * 
 * @author alkarin
 * 
 */
public class PortExecutor extends CustomCommandExecutor
{
	private WorldEditPlugin wep;
	private final BattlePorts plugin;
	final static String PORT_SET_OPTIONS = "&6/port setoptions <portname> <option_1> ... <option_x>";
	private static final String PORT_ADD_OPTIONS = "&6/port addoptions <portname> <option_1> ... <option_x>";
	private static final String PORT_ALTER_DEST = "&6/port dest <portname>";
	private static final String PORT_INFO = "&6/port info <portname>";
	private static final String PORT_DELETE = "&6/port delete <portname>";
	private static final String PORT_RENAME = "&6/port rename <portname> <new name>";
	private static final String PORT_LIST_OPTIONS = "&6/port options&e: shows port options";
	private static final String PORT_LIST = "&6/port list&e: shows nearby ports";
	private static final String PORT_LIST2 = "&6/port list <worldname or all>&e: show ports in a world or all";
	private static final String PORT_CREATE = "&6/port create <port name> [option1]...[option x]";
	private static final String PORT_SAVE = "&6/port save";
	private static final String PORT_ALTER_SOURCE = "&6/portal source <portname>";

	public PortExecutor(PortController pc, BattlePorts main)
	{
		super(pc);
		plugin = main;
	}

	public void setWorldEditPlugin(WorldEditPlugin wep)
	{
		this.wep = wep;
	}

	@Override
	protected void showHelp(CommandSender sender, Command command)
	{
		if (!sender.isOp() && !sender.hasPermission("port.admin"))
			return;
		sendMessage(sender, PORT_CREATE);
		sendMessage(sender, PORT_ALTER_DEST);
		sendMessage(sender, PORT_DELETE);
		sendMessage(sender, PORT_RENAME);
		sendMessage(sender, PORT_LIST);
		sendMessage(sender, PORT_LIST2);
		sendMessage(sender, PORT_SET_OPTIONS);
		sendMessage(sender, PORT_ADD_OPTIONS);
		sendMessage(sender, PORT_LIST_OPTIONS);
	}

	@MCCommand(cmds = { "rename" }, op = true, usage = PORT_RENAME)
	public boolean portRename(CommandSender sender, Port port, String newName)
	{
		String oldName = port.getName();
		pc.rename(port, newName);
		return sendMessage(sender, "&ePort &6" + oldName + "&e renamed &6"
				+ newName);
	}

	@MCCommand(cmds = { "save" }, op = true, usage = PORT_SAVE)
	public boolean portSave(CommandSender sender)
	{
		plugin.saveSerializers();
		return sendMessage(sender, "&ePorts saved");
	}

	@MCCommand(cmds = { "reload" }, op = true, usage = "port reload")
	public boolean portReload(CommandSender sender)
	{
		plugin.handleExternalPlugins();
		return sendMessage(sender, "&ePorts reloaded");
	}

	@MCCommand(cmds = { "options" }, op = true, usage = PORT_LIST_OPTIONS)
	public boolean portListOptions(CommandSender sender)
	{
		sendValidOptions(sender);
		return true;
	}

	@MCCommand(cmds = { "setOptions" }, op = true, usage = PORT_SET_OPTIONS)
	public boolean portSetOptions(CommandSender sender, Port port, String[] args)
	{
		List<String> portOptions = new ArrayList<String>();
		for (int i = 2; i < args.length; i++)
		{
			String op = args[i];
			if (!Port.checkOption(op))
			{
				sendMessage(sender, "Port Option " + args[i]
						+ " is not a valid option");
				sendValidOptions(sender);
				return true;
			}
			portOptions.add(op);
		}
		port.clearOptions();
		if (port.hasOption(PortOption.FIRSTUSE))
		{
			if (!(sender instanceof Player))
			{
				return sendMessage(sender,
						"&cYou need to be ingame to use this option");
			}
			Player p = (Player) sender;
			try
			{
				port.setDestination2(p.getLocation());
			}
			catch (Exception e)
			{
				return sendMessage(sender,
						"&4Cant set destination to be within the source");
			}
		}
		for (String op : portOptions)
		{
			port.addOptions(op);
		}
		return sendMessage(sender, "&eOptions set");
	}

	@MCCommand(cmds = { "addOptions" }, op = true, usage = PORT_ADD_OPTIONS)
	public boolean portAddOptions(CommandSender sender, Port port, String[] args)
	{
		List<String> portOptions = new ArrayList<String>();
		for (int i = 2; i < args.length; i++)
		{
			String op = args[i];
			if (!Port.checkOption(op))
			{
				sendMessage(sender, "Port Option " + args[i]
						+ " is not a valid option");
				sendValidOptions(sender);
				return true;
			}
			portOptions.add(op);
		}
		if (port.hasOption(PortOption.FIRSTUSE))
		{
			if (!(sender instanceof Player))
			{
				return sendMessage(sender,
						"&cYou need to be ingame to use this option");
			}
			Player p = (Player) sender;

			try
			{
				port.setDestination2(p.getLocation());
			}
			catch (Exception e)
			{
				return sendMessage(sender,
						"&4Cant set destination to be within the source");
			}
		}

		for (String op : portOptions)
		{
			port.addOptions(op);
		}
		return sendMessage(sender, "&eAdded options " + portOptions);
	}

	@MCCommand(cmds = { "first", "firstUse" }, op = true,
			usage = PORT_ALTER_DEST)
	public boolean portAlterFirstUse(Player p, Port port)
	{
		try
		{
			port.addOption(PortOption.FIRSTUSE);
			port.setDestination2(p.getLocation());
		}
		catch (Exception e)
		{
			return sendMessage(p,
					"&4Cant set destination to be within the source");
		}
		return sendMessage(p,
				"&ePort first use destination set to this location");
	}

	@MCCommand(cmds = { "dest", "alterDest" }, op = true,
			usage = PORT_ALTER_DEST)
	public boolean portAlterDest(Player p, Port port)
	{
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

	@MCCommand(cmds = { "info" }, op = true, usage = PORT_INFO)
	public boolean portInfo(CommandSender sender, Port port)
	{
		return sendMessage(
				sender,
				"&e - &2" + port.getName() + "&e[&6"
						+ port.getWorld().getName() + " " + port.getSourceStr()
						+ "&e] -> [" + port.getDestStr() + "&e]"
						+ port.getOptionString());
	}

	@MCCommand(cmds = { "list" }, op = true, usage = PORT_LIST)
	public boolean portList(CommandSender sender, String[] args)
	{
		boolean all = false;
		World world = null;
		if (args.length >= 2)
		{
			if (args[1].equalsIgnoreCase("all"))
				all = true;
			else
			{
				world = Bukkit.getWorld(args[1]);
			}
		}
		Player p = null;
		if (p instanceof Player)
		{
			p = (Player) sender;
		}
		if (p == null)
			all = true;
		// / Get our ports
		Collection<Port> ports = null;
		if (world != null)
		{
			sendMessage(sender, "&6/port list all&e: to list all worlds");
			ports = pc.getPorts(world);
		}
		else if (all)
		{
			ports = pc.getPorts();
		}
		else
		{
			sendMessage(sender, "&eListing nearby ports");
			sendMessage(sender,
					"&6/port list <worldname or all> &e: to list a world or all worlds");
			ports = pc.getNearbyPorts(p.getWorld(), p.getLocation(), 40);
		}

		if (ports == null || ports.isEmpty())
		{
			return sendMessage(sender, "&eNo ports were found");
		}
		HashMap<World, List<Port>> wmap = new HashMap<World, List<Port>>();
		for (Port port : ports)
		{
			List<Port> theports = wmap.get(port.getWorld());
			if (theports == null)
			{
				theports = new ArrayList<Port>();
				wmap.put(port.getWorld(), theports);
			}
			theports.add(port);
		}
		for (World w : wmap.keySet())
		{
			sendMessage(sender, "&e--- Ports in &6" + w.getName() + "&e ---");
			List<Port> theports = wmap.get(w);
			for (Port port : theports)
			{
				sendMessage(
						sender,
						"&2" + port.getName() + " &e[&6" + port.getSourceStr()
								+ "&e] -> [" + port.getDestStr() + "&e]"
								+ port.getOptionString());
			}
		}
		return true;
	}

	@MCCommand(cmds = { "delete" }, op = true, usage = PORT_DELETE)
	public boolean portDelete(CommandSender sender, Port port)
	{
		pc.deletePort(port);
		return sendMessage(sender,
				"&eYou have deleted port &6" + port.getName());
	}

	@MCCommand(cmds = { "source", "alterSource" }, op = true,
			usage = PORT_ALTER_SOURCE)
	public boolean portAlterSource(Player p, Port port)
	{
		Selection sel = wep.getSelection(p);
		if (sel == null)
			return sendMessage(p, ChatColor.RED
					+ "Please select the protection area first.");

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

	@MCCommand(cmds = { "create" }, inGame = true, op = true,
			usage = PORT_CREATE)
	public boolean portCreate(Player p, String name, String[] args)
	{
		System.out.println("################    " + name);
		Port port = pc.getPort(name);
		if (port != null)
		{
			return sendMessage(p,
					"&4There is already a port with that name. Please pick a unique name.");
		}
		Selection sel = wep.getSelection(p);
		if (sel == null)
			return sendMessage(p, ChatColor.RED
					+ "Please select the protection area first.");

		port = new Port();
		port.setName(name);
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
		List<String> portOptions = new ArrayList<String>();
		for (int i = 2; i < args.length; i++)
		{
			String op = args[i];
			if (!Port.checkOption(op))
			{
				sendMessage(p, "Port Option " + args[i]
						+ " is not a valid option");
				sendValidOptions(p);
				return true;
			}
			portOptions.add(op);
		}
		for (String op : portOptions)
		{
			port.addOptions(op);
		}

		pc.addPort(port);
		sendMessage(p, ChatColor.AQUA + "The port " + port.getName()
				+ " was created successfully. options=" + port.getOptions());
		return true;
	}

	private void sendValidOptions(CommandSender sender)
	{
		sendMessage(sender, "&eValid Options are");
		for (PortOption po : PortOption.values())
		{
			String name = po.needsValue() ? po.getName() + "=<value>" : po
					.getName();
			sendMessage(sender, "&5 - &6" + name);
		}
	}

}
