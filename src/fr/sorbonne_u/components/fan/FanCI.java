package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface FanCI extends OfferedCI, RequiredCI, FanImplementation {
	
	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#getState()
	 */
	@Override
	public State getState() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#getMode()
	 */
	@Override
	public Mode	getMode() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#setHigh()
	 */
	@Override
	public void	setHigh() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#setLow()
	 */
	@Override
	public void	setLow() throws Exception;
}
