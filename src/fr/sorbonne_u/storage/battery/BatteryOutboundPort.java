package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class BatteryOutboundPort extends AbstractOutboundPort implements BatteryCI {

	private static final long serialVersionUID = 1L;

	public				BatteryOutboundPort(ComponentI owner) throws Exception
	{
		super(BatteryCI.class, owner);
	}

	public				BatteryOutboundPort(String uri, ComponentI owner)
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
		return ((BatteryCI)this.getConnector()).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#startBattery()
	 */
	@Override
	public void			startBattery() throws Exception
	{
		((BatteryCI)this.getConnector()).startBattery();
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#stopBattery()
	 */
	@Override
	public void			stopBattery() throws Exception
	{
		((BatteryCI)this.getConnector()).stopBattery();
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#getCurrentPercentage()
	 */
	@Override
	public double		getCurrentPercentage() throws Exception
	{
		return ((BatteryCI)this.getConnector()).getCurrentPercentage();
	}
}
