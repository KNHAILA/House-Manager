package fr.sorbonne_u.production_unities.windTurbine;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class WindTurbineOutboundPort
extends		AbstractOutboundPort
implements	WindTurbineCI
{

	private static final long serialVersionUID = 1L;

	public	WindTurbineOutboundPort(ComponentI owner) throws Exception
	{
		super(WindTurbineCI.class, owner);
	}

	public	WindTurbineOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WindTurbineCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.production_unities.windTurbine.WindTurbineCI#isRunning()
	 */
	@Override
	public boolean	isRunning() throws Exception
	{
		return ((WindTurbineCI)this.getConnector()).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.production_unities.windTurbine.WindTurbineCI#startWindTurbine()
	 */
	@Override
	public void	startWindTurbine() throws Exception
	{
		((WindTurbineCI)this.getConnector()).startWindTurbine();
	}

	/**
	 * @see fr.sorbonne_u.production_unities.windTurbine.WindTurbineCI#stopWindTurbine()
	 */
	@Override
	public void	stopWindTurbine() throws Exception
	{
		((WindTurbineCI)this.getConnector()).stopWindTurbine();
	}

	@Override
	public double getCurrentWindSpeed() throws Exception {
		return ((WindTurbineCI)this.getConnector()).getCurrentWindSpeed();
	}
}
