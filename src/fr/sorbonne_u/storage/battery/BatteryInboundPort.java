package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;


public class BatteryInboundPort
extends		AbstractInboundPort
implements	BatteryCI {
	private static final long serialVersionUID = 1L;

	public				BatteryInboundPort(ComponentI owner) throws Exception
	{
		super(BatteryCI.class, owner);
	}

	public				BatteryInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, BatteryCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((BatteryImplementation)o).isRunning());
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#startBattery()
	 */
	@Override
	public void			startBattery() throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((BatteryImplementation)o).startBattery();
						return null;
					 });
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#stopBattery()
	 */
	@Override
	public void			stopBattery() throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((BatteryImplementation)o).stopBattery();
						return null;
					 });
	}

	@Override
	public double		getCurrentPercentage() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((BatteryImplementation)o).getCurrentPercentage());
	}
}
