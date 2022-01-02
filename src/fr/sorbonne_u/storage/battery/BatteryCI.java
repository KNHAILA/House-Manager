package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface BatteryCI extends BatteryImplementation, RequiredCI, OfferedCI {

	/**
	 * @see fr.sorbonne_u.storage.battery.HeaterImplementationI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception;

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryImplementation#startHeater()
	 */
	@Override
	public void			startBattery() throws Exception;

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryImplementation#stopHeater()
	 */
	@Override
	public void			stopBattery() throws Exception;

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryImplementation#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentPercentage() throws Exception;
}
