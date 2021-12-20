package fr.sorbonne_u.components.vacuumCleaner;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface VacuumCleanerCI extends OfferedCI, RequiredCI, VacuumCleanerImplementation {
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#getState()
	 */
	@Override
	public VacuumCleanerState	getState() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#getMode()
	 */
	@Override
	public VacuumCleanerMode	getMode() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#setHigh()
	 */
	@Override
	public void	setHigh() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#setLow()
	 */
	@Override
	public void	setLow() throws Exception;
}

