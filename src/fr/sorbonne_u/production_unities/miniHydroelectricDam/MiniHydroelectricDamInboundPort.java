package fr.sorbonne_u.production_unities.miniHydroelectricDam;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class MiniHydroelectricDamInboundPort
extends		AbstractInboundPort
implements	MiniHydroelectricDamCI
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MiniHydroelectricDamInboundPort(ComponentI owner) throws Exception
	{
		super(MiniHydroelectricDamCI.class, owner);
	}

	public	MiniHydroelectricDamInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, MiniHydroelectricDamCI.class, owner);
	}

	@Override
	public void startMiniHydroelectricDam() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MiniHydroelectricDamImplementation)o).startMiniHydroelectricDam();
						return null;
					 });
	}

	@Override
	public void stopMiniHydroelectricDam() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((MiniHydroelectricDamImplementation)o).stopMiniHydroelectricDam();
						return null;
					 });
	}

	@Override
	public boolean isRunning() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((MiniHydroelectricDamImplementation)o).isRunning());
	}
}
