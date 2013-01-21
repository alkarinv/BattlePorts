package mc.alk.battlePorts.serializers;

import java.io.File;
import java.io.IOException;

import mc.alk.battlePorts.objects.Port;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


/**
 * 
 * @author alkarin
 *
 */
public class PlayerSerializer  {

	public static YamlConfiguration config;
	static File f = null;

	public void setConfig(File f){
		PlayerSerializer.f = f;
		config = new YamlConfiguration();
		loadAll();
	}

	public void saveAll() {
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadAll(){
		try {
			config.load(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean hasPlayer(Port port, Player p){
		System.out.println("contians " + port +"  p=" + p.getName() +  " "+config.contains(port.getName()+"-"+p.getName()));
		return config.contains(port.getName()+"."+p.getName());
	}
	public static void addUse(Port port, Player p){
		System.out.println("adding " + port +"  p=" + p.getName() +  " "+config.contains(port.getName()+"-"+p.getName()));
		final String portName = port.getName();
		if (!config.contains(portName)){
			config.createSection(portName);			
		}
		if (!config.contains(portName+"." + p.getName())){
			config.createSection(portName +"." + p.getName());
		}
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
