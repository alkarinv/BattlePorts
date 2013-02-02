package mc.alk.battlePorts.objects;

import java.util.List;

public class PlayerPort extends Port
{
	String owner;
	List<String> members;

	public PlayerPort()
	{

	}

	public PlayerPort(String playerName, String portName)
	{
		owner = playerName;
		name = getName(playerName, portName);
	}

	public String getPortName()
	{
		return name.substring(3 + owner.length() + 1, name.length());
	}

	public static String getName(String playerName, String portName)
	{
		return "pp-" + playerName.toLowerCase() + "-" + portName.toLowerCase();
	}

	public void setOwner(String string)
	{
		this.owner = string;
	}

	public String getOwner()
	{
		return owner;
	}

	public String toString()
	{
		return super.toString() + " owner=" + owner;
	}
}
