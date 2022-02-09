package fr.sorbonne_u.components.vacuumCleaner;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface VacuumCleanerCI extends OfferedCI, RequiredCI, VacuumCleanerImplementation {
	
	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#getState()
	 */
	@Override
	public VacuumCleanerState	getState() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#getMode()
	 */
	@Override
	public VacuumCleanerMode	getMode() throws Exception;

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

