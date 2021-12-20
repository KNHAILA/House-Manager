package fr.sorbonne_u.production_unities.miniHydroelectricDam;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

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
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.MiniHydroelectricDam.MiniHydroelectricDamCI#isRunning()
	 */
	@Override
	public boolean	isRunning() throws Exception
	{
		return ((MiniHydroelectricDamCI)this.getConnector()).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.MiniHydroelectricDam.MiniHydroelectricDamCI#startMiniHydroelectricDam()
	 */
	@Override
	public void	startMiniHydroelectricDam() throws Exception
	{
		((MiniHydroelectricDamCI)this.getConnector()).startMiniHydroelectricDam();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.MiniHydroelectricDam.MiniHydroelectricDamCI#stopMiniHydroelectricDam()
	 */
	@Override
	public void	stopMiniHydroelectricDam() throws Exception
	{
		((MiniHydroelectricDamCI)this.getConnector()).stopMiniHydroelectricDam();
	}
}
