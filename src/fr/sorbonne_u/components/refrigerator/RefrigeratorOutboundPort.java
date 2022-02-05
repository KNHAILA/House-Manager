package fr.sorbonne_u.components.refrigerator;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>RefrigeratorOutboundPort</code> implements an outbound port for the
* {@code RefrigeratorCI} component interface.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
* <p>Created on : 2021-10-07</p>
* 
* * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

public class			RefrigeratorOutboundPort
extends		AbstractOutboundPort
implements	RefrigeratorCI
{
	private static final long serialVersionUID = 1L;

	public				RefrigeratorOutboundPort(ComponentI owner) throws Exception
	{
		super(RefrigeratorCI.class, owner);
	}

	public				RefrigeratorOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, RefrigeratorCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Refrigerator.RefrigeratorCI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		return ((RefrigeratorCI)this.getConnector()).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Refrigerator.RefrigeratorCI#startRefrigerator()
	 */
	@Override
	public void			startRefrigerator() throws Exception
	{
		((RefrigeratorCI)this.getConnector()).startRefrigerator();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Refrigerator.RefrigeratorCI#stopRefrigerator()
	 */
	@Override
	public void			stopRefrigerator() throws Exception
	{
		((RefrigeratorCI)this.getConnector()).stopRefrigerator();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Refrigerator.RefrigeratorCI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		((RefrigeratorCI)this.getConnector()).setTargetTemperature(target);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Refrigerator.RefrigeratorCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((RefrigeratorCI)this.getConnector()).getTargetTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.Refrigerator.RefrigeratorCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((RefrigeratorCI)this.getConnector()).getCurrentTemperature();
	}
}
