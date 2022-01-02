package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class BatteryConnector extends AbstractConnector implements BatteryCI {

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		return ((BatteryCI)this.offering).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#startBattery()
	 */
	@Override
	public void			startBattery() throws Exception
	{
		((BatteryCI)this.offering).startBattery();
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#stopBattery()
	 */
	@Override
	public void			stopBattery() throws Exception
	{
		((BatteryCI)this.offering).stopBattery();
	}
	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentPercentage() throws Exception
	{
		return ((BatteryCI)this.offering).getCurrentPercentage();
	}
}
