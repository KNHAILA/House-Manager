package fr.sorbonne_u.components.vacuumCleaner;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class VacuumCleanerOutboundPort extends AbstractOutboundPort implements VacuumCleanerCI {

	private static final long serialVersionUID = 1L;

	public VacuumCleanerOutboundPort(ComponentI owner)
	throws Exception
	{
		super(VacuumCleanerCI.class, owner);
	}

	public VacuumCleanerOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, VacuumCleanerCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#getState()
	 */
	@Override
	public VacuumCleanerState	getState() throws Exception
	{
		return ((VacuumCleanerCI)this.getConnector()).getState();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#getMode()
	 */
	@Override
	public VacuumCleanerMode getMode() throws Exception
	{
		return ((VacuumCleanerCI)this.getConnector()).getMode();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		((VacuumCleanerCI)this.getConnector()).turnOn();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		((VacuumCleanerCI)this.getConnector()).turnOff();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#setHigh()
	 */
	@Override
	public void	setHigh() throws Exception
	{
		((VacuumCleanerCI)this.getConnector()).setHigh();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#setLow()
	 */
	@Override
	public void setLow() throws Exception
	{
		((VacuumCleanerCI)this.getConnector()).setLow();
	}
}
