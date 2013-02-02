package mc.alk.battlePorts.controllers;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.entity.Player;

public class PermissionController
{

	static Chat chat;

	public static int getAvailablePorts(Player p)
	{
		if (chat == null)
			return 0;
		return chat.getPlayerInfoInteger(p.getWorld().getName(), p.getName(),
				"maxPortals", 0);
	}

	public static int getAvailableArea(Player p)
	{
		if (chat == null)
			return 0;
		return chat.getPlayerInfoInteger(p.getWorld().getName(), p.getName(),
				"portalSize", 0);
	}

	public static boolean hasGroup(Player player, String group)
	{
		if (chat == null)
			return false;
		return chat.playerInGroup(player, group);
	}

	public static void setPermission(Chat provider)
	{
		PermissionController.chat = provider;
	}

	public static boolean hasChat()
	{
		return chat != null;
	}

}
