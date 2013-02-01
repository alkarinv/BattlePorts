package mc.alk.battlePorts.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.bukkit.Material;

public class Util
{
	public static String colorChat(String msg)
	{
		return msg.replaceAll("&", Character.toString((char) 167));
	}

	public static String deColorChat(String msg)
	{
		return msg.replaceAll("\\&[0-9a-zA-Z]", "");
	}

	public static boolean isInt(String i)
	{
		try
		{
			Integer.parseInt(i);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static boolean isFloat(String i)
	{
		try
		{
			Float.parseFloat(i);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	// Gets the Material from bukkit enum
	public static Material getMat(String name)
	{
		if (Util.isInt(name))
		{
			return getMat(Integer.parseInt(name));
		}
		else
		{
			return Material.getMaterial(returnID(name));
		}
	}

	// Gets the Material from ID
	public static Material getMat(int id)
	{
		return Material.getMaterial(id);
	}

	public static int returnID(String name)
	{
		Material[] mat = Material.values();
		int temp = Integer.MAX_VALUE;
		Material tmp = null;
		for (Material m : mat)
		{
			if (m.name()
					.toLowerCase()
					.replaceAll("_", "")
					.startsWith(
							name.toLowerCase().replaceAll("_", "")
									.replaceAll(" ", "")))
			{
				if (m.name().length() < temp)
				{
					tmp = m;
					temp = m.name().length();
				}
			}
		}
		if (tmp != null)
		{
			return tmp.getId();
		}
		return -1;
	}

	public static File load(InputStream inputStream, String config_file)
	{
		File file = new File(config_file);
		if (!file.exists())
		{ // / Create a new config file from our default
			try
			{
				OutputStream out = new FileOutputStream(config_file);
				byte buf[] = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				out.close();
				inputStream.close();
			}
			catch (Exception e)
			{
			}
		}
		return file;
	}

	public static String convertToString(long t)
	{
		t = t / 1000;
		long s = t % 60;
		t /= 60;
		long m = t % 60;
		t /= 60;
		long h = t % 24;
		t /= 24;
		long d = t;

		if (d < 1 && m < 1 && h < 1)
		{
			return s + " seconds";
		}
		if (d < 1 && h < 1 && s == 0)
		{
			return m + " " + minOrMins(m);
		}
		if (d < 1)
		{
			return "&6" + h + "&e " + hourOrHours(h) + " &6" + m + "&e "
					+ minOrMins(m) + " and &6" + s + "&e sec";
		}
		else
		{
			return "&6" + d + "&e " + dayOrDays(d) + " &6" + h + "&e "
					+ hourOrHours(h) + " &6" + m + "&e " + minOrMins(m);
		}
	}

	private static String dayOrDays(long t)
	{
		return t > 1 || t == 0 ? "days" : "day";
	}

	private static String hourOrHours(long t)
	{
		return t > 1 || t == 0 ? "hours" : "hour";
	}

	private static String minOrMins(long t)
	{
		return t > 1 || t == 0 ? "minutes" : "minute";
	}

	public static String convertLongToDate(long time)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
		return sdf.format(time);
	}

	public static String PorP(int size)
	{
		return size == 1 ? "person" : "people";
	}
}
