package fr.sorbonne_u.production_unities.windTurbine;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class WindTurbineInboundPort 
extends		AbstractInboundPort
implements	WindTurbineCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WindTurbineInboundPort(ComponentI owner) throws Exception
	{
		super(WindTurbineCI.class, owner);
	}

	public	WindTurbineInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WindTurbineCI.class, owner);
	}

	@Override
	public void startWindTurbine() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WindTurbineImplementation)o).startWindTurbine();
						return null;
					 });
	}

	@Override
	public void stopWindTurbine() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WindTurbineImplementation)o).stopWindTurbine();
						return null;
					 });
	}

	@Override
	public boolean isRunning() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WindTurbineImplementation)o).isRunning());
	}

	@Override
	public double getCurrentWindSpeed() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WindTurbineImplementation)o).getCurrentWindSpeed());
		
	}
}
