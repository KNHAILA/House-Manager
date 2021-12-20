package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class FanOutboundPort extends AbstractOutboundPort implements FanCI {

	private static final long serialVersionUID = 1L;

	public FanOutboundPort(ComponentI owner)
	throws Exception
	{
		super(FanCI.class, owner);
	}

	public FanOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, FanCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Fan.FanCI#getState()
	 */
	@Override
	public State	getState() throws Exception
	{
		return ((FanCI)this.getConnector()).getState();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Fan.FanCI#getMode()
	 */
	@Override
	public Mode getMode() throws Exception
	{
		return ((FanCI)this.getConnector()).getMode();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Fan.FanCI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		((FanCI)this.getConnector()).turnOn();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Fan.FanCI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		((FanCI)this.getConnector()).turnOff();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Fan.FanCI#setHigh()
	 */
	@Override
	public void	setHigh() throws Exception
	{
		((FanCI)this.getConnector()).setHigh();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Fan.FanCI#setLow()
	 */
	@Override
	public void setLow() throws Exception
	{
		((FanCI)this.getConnector()).setLow();
	}
}
