package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface FanCI extends OfferedCI, RequiredCI, FanImplementation {
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#getState()
	 */
	@Override
	public State getState() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#getMode()
	 */
	@Override
	public Mode	getMode() throws Exception;

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
