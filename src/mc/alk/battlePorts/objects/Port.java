package mc.alk.battlePorts.objects;

import java.util.HashMap;
import java.util.Map;

import mc.alk.battlePorts.controllers.PermissionController;
import mc.alk.battlePorts.util.EffectUtil;
import mc.alk.battlePorts.util.InventoryUtil;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Port
{

	String name;
	Location destination;
	Location altDestination;
	BoundingBox source;
	HashMap<PortOption, String> options = null;

	public Location getDestination2()
	{
		return altDestination;
	}

	public void setDestination2(Location location) throws Exception
	{
		if (source.contains(location))
			throw new Exception("Can't set destination to be within the source");
		altDestination = location;
	}

	public Location getDestination()
	{
		return destination;
	}

	public void setDestination(Location l) throws Exception
	{
		if (source.contains(l))
			throw new Exception("Can't set destination to be within the source");
		destination = l;
	}

	public boolean withinSource(Location l)
	{
		return source.contains(l);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Location getSrcLowerCorner()
	{
		return source.getCorner1();
	}

	public Location getSrcUpperCorner()
	{
		return source.getCorner2();
	}

	public void setSourceBox(World world, Location l1, Location l2)
			throws Exception
	{
		Location c1 = new Location(world, l1.getBlockX(), l1.getBlockY(),
				l1.getBlockZ());
		Location c2 = new Location(world, l2.getBlockX(), l2.getBlockY(),
				l2.getBlockZ());
		source = new BoundingBox(c1, c2);
	}

	public World getWorld()
	{
		return source != null ? source.getCorner1().getWorld() : null;
	}

	public void addOption(PortOption po)
	{
		if (options == null)
		{
			options = new HashMap<PortOption, String>();
		}
		options.put(po, null);
	}

	public void setOptions(HashMap<PortOption, String> portOptions)
	{
		options = new HashMap<PortOption, String>(portOptions);
	}

	public void addOptions(PortOption option, String value)
	{
		if (options == null)
		{
			options = new HashMap<PortOption, String>();
		}
		options.put(option, value);
	}

	public void addOptions(String option)
	{
		String op[] = option.split("=");
		PortOption po = PortOption.valueOf(op[0].toUpperCase());
		if (op.length > 1)
		{
			addOptions(po, op[1]);
		}
		else
		{
			addOption(po);
		}
	}

	public static boolean checkOption(String option)
	{
		try
		{
			String op[] = option.split("=");
			PortOption.valueOf(op[0].toUpperCase());
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return name + " " + getWorld().getName() + getOptionString();
	}

	public void setSourceBox(Location c1, Location c2) throws Exception
	{
		source = new BoundingBox(c1, c2);
	}

	public String getDestStr()
	{
		return "&6"
				+ (source.getWorld() == destination.getWorld() ? "" : "&5"
						+ destination.getWorld().getName() + ":") + "&6"
				+ getLocString(destination);
	}

	public String getSourceStr()
	{
		return "&6" + getLocString(source.getCorner1()) + "&e<->&6"
				+ getLocString(source.getCorner2());
	}

	public String getLocString(Location l)
	{
		return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
	}

	public String getOptionString()
	{
		return options == null || options.isEmpty() ? "" : "&5"
				+ toString(options);
	}

	private String toString(HashMap<PortOption, String> options2)
	{
		StringBuilder sb = new StringBuilder("[");
		boolean first = true;
		for (PortOption po : options2.keySet())
		{
			if (!first)
				sb.append(", ");
			first = false;
			sb.append(po.getName()
					+ (po.needsValue ? "=" + options2.get(po) : ""));
		}
		sb.append("]");
		return sb.toString();
	}

	public String meetsRequirements(Player player)
	{
		if (options == null || options.isEmpty())
			return null;

		for (PortOption po : options.keySet())
		{
			switch (po)
			{
			case CLEARINVENTORY:
				InventoryUtil.closeInventory(player);
				if (InventoryUtil.hasAnyItem(player))
					return "&4!!!&c You need a &6clear inventory&c to use this port";
				break;
			case NOPOTIONS:
				InventoryUtil.closeInventory(player);
				if (InventoryUtil.hasItem(player, new ItemStack(373, 0)))
					return "&4!!!&c You cant bring &6potions!";
				break;
			case NOBUFFS:
				EffectUtil.deEnchantAll(player);
				break;
			case NOENCHANTS:
				InventoryUtil.closeInventory(player);
				if (InventoryUtil.hasEnchantedItem(player))
					return "&4!!!&c You cant bring &6enchanted items&c through this port";
				break;
			case HASGROUP:
				String group = options.get(po);
				if (!PermissionController.hasGroup(player, group))
				{
					return "&4!!!&c You need to belong to group &6" + group
							+ "&c to use this portal";
				}
				break;
			case HASPERM:
				if (!player.hasPermission(options.get(po)))
				{
					return "&4!!!&c You dont have the permissions to use this portal";
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	public Map<PortOption, String> getOptions()
	{
		return options;
	}

	public void clearOptions()
	{
		if (options != null)
			options.clear();
	}

	public boolean hasOption(PortOption option)
	{
		return options != null && options.containsKey(option);
	}

}
