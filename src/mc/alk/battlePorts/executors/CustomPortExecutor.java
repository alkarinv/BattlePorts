package mc.alk.battlePorts.executors;

import java.util.concurrent.atomic.AtomicBoolean;

import mc.alk.battlePorts.controllers.PortController;
import mc.alk.battlePorts.objects.Port;

import org.bukkit.command.Command;

public class CustomPortExecutor extends CustomCommandExecutor{

	protected final PortController pc;

	protected CustomPortExecutor(PortController pc){
		super();
		this.pc = pc;
	}


	@Override
	protected String getUsageString(Class<?> clazz) {
		if (Port.class == clazz){
			return "<port> ";
		}
		return super.getUsageString(clazz);
	}

	@Override
	protected Object verifyArg(Class<?> clazz, Command command, String string, AtomicBoolean usedString) {
		usedString.set(true);
		if (Port.class == clazz){
			Port p = pc.getPort(string);
			if (p == null)
				throw new IllegalArgumentException("&cPort " + string +" can not be found");
			return p;
		}
		return super.verifyArg(clazz, command, string, usedString);
	}

}
