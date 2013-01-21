package mc.alk.battlePorts.controllers;

import java.io.File;
import java.util.Formatter;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 
 * @author alkarin
 *
 */
public class MessageController {

	public static FileConfiguration mc;
    static File f = new File("");

    public void setConfig(File f){
    	mc = new YamlConfiguration();
		try {mc.load(f);} catch (Exception e) {e.printStackTrace();}
		loadAll();
	}

	private void loadAll(){

	}
    
	public static String getMessage(String prefix,String node, Object... varArgs) {
		try{
			ConfigurationSection n = mc.getConfigurationSection(prefix);
		
			StringBuilder buf = new StringBuilder(n.getString("prefix", "[Arena]"));
			String msg = n.getString(node, "No translation for " + node);
			Formatter form = new Formatter(buf);
        
        	form.format(msg, varArgs);
        	form.close();
            return colorChat(buf.toString());
        } catch(Exception e){
        	System.err.println("Error getting message " + prefix + "." + node);
        	for (Object o: varArgs){ System.err.println("argument=" + o);}
        	e.printStackTrace();
        	return "Error getting message " + prefix + "." + node;
        }
	}

	public static String getMessageNP(String prefix,String node, Object... varArgs) {
		ConfigurationSection n = mc.getConfigurationSection(prefix);
		StringBuilder buf = new StringBuilder();
        String msg = n.getString(node, "No translation for " + node);
        Formatter form = new Formatter(buf);
        try{
        	form.format(msg, varArgs);
        	form.close();
        } catch(Exception e){
        	System.err.println("Error getting message " + prefix + "." + node);
        	for (Object o: varArgs){ System.err.println("argument=" + o);}
        	e.printStackTrace();
        }
        return colorChat(buf.toString());
	}

	public static String getMessageAddPrefix(String pprefix, String prefix,String node, Object... varArgs) {
		ConfigurationSection n = mc.getConfigurationSection(prefix);
		StringBuilder buf = new StringBuilder(pprefix);
        String msg = n.getString(node, "No translation for " + node);
        Formatter form = new Formatter(buf);
        try{
        	form.format(msg, varArgs);
        	form.close();
        } catch(Exception e){
        	System.err.println("Error getting message " + prefix + "." + node);
        	for (Object o: varArgs){ System.err.println("argument=" + o);}
        	e.printStackTrace();
        }
        return colorChat(buf.toString());
	}
	
    public static String colorChat(String msg) {
        return msg.replaceAll("&", Character.toString((char) 167));
    }

	public static boolean sendMessage(CommandSender p, String message) {
		if (message ==null) return true;
		String[] msgs = message.split("\n");
		for (String msg: msgs){
			if (p == null){
				System.out.println(MessageController.colorChat(msg));
			} else {
				p.sendMessage(MessageController.colorChat(msg));			
			}			
		}
		return true;
	}
}
