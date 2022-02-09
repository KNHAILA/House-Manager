package fr.sorbonne_u.production_unities.miniHydroelectricDam;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.production_unities.windTurbine.WindTurbineCI;

public class MiniHydroelectricDamOutboundPort
extends		AbstractOutboundPort
implements	MiniHydroelectricDamCI
{
	
	private static final long serialVersionUID = 1L;

	public	MiniHydroelectricDamOutboundPort(ComponentI owner) throws Exception
	{
		super(MiniHydroelectricDamCI.class, owner);
	}

	public	MiniHydroelectricDamOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, MiniHydroelectricDamCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.MiniHydroelectricDam.MiniHydroelectricDamCI#isRunning()
	 */
	@Override
	public boolean	isRunning() throws Exception
	{
		return ((MiniHydroelectricDamCI)this.getConnector()).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.MiniHydroelectricDam.MiniHydroelectricDamCI#startMiniHydroelectricDam()
	 */
	@Override
	public void	startMiniHydroelectricDam() throws Exception
	{
		((MiniHydroelectricDamCI)this.getConnector()).startMiniHydroelectricDam();
	}

	/**
	 * @see fr.sorbonne_u.components.MiniHydroelectricDam.MiniHydroelectricDamCI#stopMiniHydroelectricDam()
	 */
	@Override
	public void	stopMiniHydroelectricDam() throws Exception
	{
		((MiniHydroelectricDamCI)this.getConnector()).stopMiniHydroelectricDam();
	}

	@Override
	public double getCurrentWaterVolume() throws Exception {
		return ((MiniHydroelectricDamCI)this.getConnector()).getCurrentWaterVolume();
	}
}
