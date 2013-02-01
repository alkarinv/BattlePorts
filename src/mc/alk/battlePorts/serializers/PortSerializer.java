package mc.alk.battlePorts.serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mc.alk.battlePorts.BattlePorts;
import mc.alk.battlePorts.controllers.PortController;
import mc.alk.battlePorts.objects.PlayerPort;
import mc.alk.battlePorts.objects.Port;
import mc.alk.battlePorts.objects.PortOption;
import mc.alk.battlePorts.util.Log;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * 
 * @author alkarin
 * 
 */
public class PortSerializer extends YamlSerializer
{
	PortController pc;

	public PortSerializer(PortController pc)
	{
		this.pc = pc;
	}

	@Override
	public void loadAll()
	{
		loadAll(pc);
	}

	@Override
	public void saveAll()
	{
		saveAll(pc);
	}

	private void loadAll(PortController pc)
	{
		reloadConfig();
		pc.clearPorts();
		ConfigurationSection maincs = config.getConfigurationSection("ports");
		// System.out.println("maincs" + maincs + " file=" +
		// f.getAbsolutePath());
		int count = 0;
		if (maincs != null)
		{ // / No ports
			Collection<String> keys = maincs.getKeys(false);
			// System.out.println("keys " + keys);
			if (keys != null)
			{
				for (String key : keys)
				{
					// System.out.println("key= " + key);
					try
					{
						Port p = loadPort(maincs.getConfigurationSection(key));
						if (p != null)
						{
							pc.addPort(p);
							count++;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Log.err("Couldnt load port " + key);
					}
				}
			}
			Log.info(BattlePorts.getVersion() + " Loaded " + count + " ports");
		}

		count = 0;
		maincs = config.getConfigurationSection("playerports");
		// System.out.println("maincs" + maincs + " file=" +
		// f.getAbsolutePath());
		if (maincs != null)
		{ // / No player ports
			Collection<String> keys = maincs.getKeys(false);
			// System.out.println("keys " + keys);
			if (keys != null)
			{
				for (String key : keys)
				{
					// System.out.println("key= " + key);
					try
					{
						Port p = loadPort(maincs.getConfigurationSection(key));
						if (p != null)
						{
							pc.addPort(p);
							count++;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Log.err("Couldnt load port " + key);
					}
				}
			}
			Log.info(BattlePorts.getVersion() + " Loaded " + count
					+ " player portals");

		}
	}

	private static Port loadPort(ConfigurationSection cs) throws Exception
	{
		String name = cs.getName();
		Port p = new Port();
		Location c1 = getLocation(cs.getString("srcCorner1"));
		Location c2 = getLocation(cs.getString("srcCorner2"));
		Location dest = getLocation(cs.getString("destination"));
		Location altdest = getLocation(cs.getString("destination2"));
		// System.out.println("dest=" + getLocString(dest));
		if (cs.contains("owner"))
		{
			p = new PlayerPort();
			((PlayerPort) p).setOwner(cs.getString("owner"));
		}
		else
		{
			p = new Port();
		}
		p.setName(name);
		p.setSourceBox(c1, c2);
		p.setDestination(dest);
		if (altdest != null)
			p.setDestination2(altdest);
		List<String> options = cs.getStringList("options");
		if (options != null && !options.isEmpty())
		{
			for (String option : options)
			{
				p.addOptions(option);
			}
		}
		return p;
	}

	private void saveAll(PortController pc)
	{
		List<Port> ports = new ArrayList<Port>(pc.getPorts());
		List<PlayerPort> playerPorts = new ArrayList<PlayerPort>();
		for (Port p : ports)
		{
			if (p instanceof PlayerPort)
				playerPorts.add((PlayerPort) p);
		}
		ports.removeAll(playerPorts);

		Collections.sort(ports, new PortComparotor());
		Collections.sort(playerPorts, new PortComparotor());

		ConfigurationSection maincs = config.createSection("ports");
		for (Port port : ports)
		{
			// System.out.println("port = " + port);
			try
			{
				savePort(maincs, port);
			}
			catch (Exception e)
			{
				Log.err("Couldnt save port " + port.getName());
			}
		}
		maincs = config.createSection("playerports");
		for (PlayerPort port : playerPorts)
		{
			// System.out.println("port = " + port);
			try
			{
				savePort(maincs, port);
			}
			catch (Exception e)
			{
				Log.err("Couldnt save port " + port.getName());
			}
		}

		try
		{
			config.save(f);
		}
		catch (IOException e)
		{
			Log.err("[BattlePorts] Problems saving ports");
			e.printStackTrace();
		}
	}

	private static void savePort(ConfigurationSection maincs, Port port)
	{
		ConfigurationSection cs = maincs.createSection(port.getName());
		cs.set("srcCorner1", getLocString(port.getSrcLowerCorner()));
		cs.set("srcCorner2", getLocString(port.getSrcUpperCorner()));
		cs.set("destination", getLocString(port.getDestination()));
		if (port.getDestination2() != null)
			cs.set("destination2", getLocString(port.getDestination2()));
		if (port instanceof PlayerPort)
		{
			cs.set("owner", ((PlayerPort) port).getOwner());
		}
		Map<PortOption, String> options = port.getOptions();
		if (options == null || options.isEmpty())
			return;
		ArrayList<String> al = new ArrayList<String>();
		for (PortOption po : options.keySet())
		{
			String value = options.get(po);
			if (value == null)
				al.add(po.getName());
			else
				al.add(po.getName() + "=" + value);
		}
		cs.set("options", al);
	}

	public class PortComparotor implements Comparator<Port>
	{
		@Override
		public int compare(Port arg0, Port arg1)
		{
			return arg0.getName().compareTo(arg1.getName());
		}

	}
}
