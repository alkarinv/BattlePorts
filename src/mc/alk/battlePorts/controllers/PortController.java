package mc.alk.battlePorts.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mc.alk.battlePorts.objects.PlayerInfo;
import mc.alk.battlePorts.objects.PlayerPort;
import mc.alk.battlePorts.objects.Port;
import mc.alk.battlePorts.objects.PortMBRConverter;
import mc.org.khelekore.prtree.MBRConverter;
import mc.org.khelekore.prtree.PRTree;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class PortController
{
	private static final int BRANCH_FACTOR = 30;
	HashMap<World, PRTree<Port>> world_ports = new HashMap<World, PRTree<Port>>();

	HashMap<String, Port> ports = new HashMap<String, Port>();

	private MBRConverter<Port> converter = new PortMBRConverter();
	public static WorldGuardPlugin wgp = null;

	public PortController()
	{

	}

	public Port getPort(final Location l)
	{
		PRTree<Port> tree = world_ports.get(l.getWorld());
		if (tree == null)
			return null;

		final int x = l.getBlockX(), z = l.getBlockZ();
		Iterable<Port> applicablePorts = tree.find(x, z, x, z);
		if (applicablePorts == null)
			return null;
		// / If there are overlapping ports, technically we should return all of
		// them
		// / but for now.. just return 1
		for (Port port : applicablePorts)
		{
			if (port.withinSource(l))
				return port;
		}
		return null;
	}

	public void addPort(Port port)
	{
		World world = port.getWorld();
		ports.put(port.getName().toLowerCase(), port);
		PRTree<Port> tree = world_ports.get(world);
		ArrayList<Port> al = new ArrayList<Port>();

		// / Get all ports
		if (tree != null)
		{
			tree.find(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE,
					Double.MAX_VALUE, al);
		}

		al.add(port);
		tree = new PRTree<Port>(converter, BRANCH_FACTOR);

		// / Add all ports
		tree.load(al);
		world_ports.put(world, tree);
		// System.out.println("adding ports " + port + "   " + tree.getHeight()
		// +"  " + tree.getNumberOfLeaves());
	}

	public Port getPort(String name)
	{
		return ports.get(name.toLowerCase());
	}

	public void deletePort(Port port)
	{
		ports.remove(port.getName().toLowerCase());
		World world = port.getWorld();
		PRTree<Port> tree = world_ports.get(world);
		if (tree == null)
			return;
		ArrayList<Port> al = new ArrayList<Port>();
		tree.find(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE, al);
		al.remove(port);
		tree = new PRTree<Port>(converter, BRANCH_FACTOR);

		// / Add all ports back
		tree.load(al);
		world_ports.put(world, tree);
	}

	public void rename(Port port, String newName)
	{
		ports.remove(port.getName().toLowerCase());
		port.setName(newName);
		ports.put(port.getName().toLowerCase(), port);
	}

	public Collection<Port> getPorts()
	{
		System.out.println("ports = " + ports.size());
		return new ArrayList<Port>(ports.values());
	}

	public Collection<Port> getPorts(World world)
	{
		PRTree<Port> tree = world_ports.get(world);
		if (tree == null)
			return null;
		ArrayList<Port> al = new ArrayList<Port>();
		tree.find(-Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE, al);
		return al;
	}

	public Collection<Port> getNearbyPorts(World world, Location l, int radius)
	{
		PRTree<Port> tree = world_ports.get(world);
		if (tree == null)
			return null;
		ArrayList<Port> al = new ArrayList<Port>();
		tree.find(l.getBlockX() - radius, l.getBlockZ() - radius, l.getBlockX()
				+ radius, l.getBlockZ() + radius, al);
		return al;
	}

	public PlayerInfo getPlayerInfo(Player p)
	{
		PlayerInfo pi = new PlayerInfo();
		pi.nports = getPlayerPortals(p).size();
		return pi;
	}

	public PlayerPort getPlayerPort(Player p, String portName)
	{
		portName = PlayerPort.getName(p.getName(), portName);
		return (PlayerPort) ports.get(portName);
	}

	public List<PlayerPort> getPlayerPortals(Player player)
	{
		List<PlayerPort> playerports = new ArrayList<PlayerPort>();
		final String pname = player.getName();
		synchronized (ports)
		{
			for (Port p : ports.values())
			{
				// System.out.println("Checking port " + p);
				if (p instanceof PlayerPort
						&& ((PlayerPort) p).getOwner().equals(pname))
					playerports.add((PlayerPort) p);
			}
		}
		return playerports;
	}

	public void clearPorts()
	{
		ports.clear();
	}

}
