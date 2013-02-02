package mc.alk.battlePorts.util;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class Log
{

	private static Logger log = Bukkit.getLogger();

	public static void info(String msg)
	{
		if (log != null)
			log.info(colorChat(msg));
		else
			System.out.println(colorChat(msg));
	}

	public static void warn(String msg)
	{
		if (log != null)
			log.warning(colorChat(msg));
		else
			System.err.println(colorChat(msg));
	}

	public static void err(String msg)
	{
		if (log != null)
			log.severe(colorChat(msg));
		else
			System.err.println(colorChat(msg));
	}

	public static void debug(String msg){
		if (log != null)
			log.severe(colorChat(msg));
		else
			System.err.println(colorChat(msg));
	}

    public static String colorChat(String msg) {
        return msg.replace('&', (char) 167);
    }

}
