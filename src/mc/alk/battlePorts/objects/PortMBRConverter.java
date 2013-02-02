package mc.alk.battlePorts.objects;

import mc.org.khelekore.prtree.MBRConverter;

public class PortMBRConverter implements MBRConverter<Port>
{

	@Override
	public int getDimensions()
	{
		return 2;
	}

	@Override
	public double getMax(int dim, Port port)
	{
		switch (dim)
		{
		case 0:
			return port.getSrcUpperCorner().getBlockX();
		case 1:
			return port.getSrcUpperCorner().getBlockZ();
		}
		return 0;
	}

	@Override
	public double getMin(int dim, Port port)
	{
		switch (dim)
		{
		case 0:
			return port.getSrcLowerCorner().getBlockX();
		case 1:
			return port.getSrcLowerCorner().getBlockZ();
		}
		return 0;
	}
}
