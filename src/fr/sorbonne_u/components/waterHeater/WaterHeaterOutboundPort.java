package fr.sorbonne_u.components.waterHeater;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>WaterHeaterOutboundPort</code> implements an outbound port for the
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
* <p>Created on : 2021-10-12</p>
* 
* * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

public class			WaterHeaterOutboundPort
extends		AbstractOutboundPort
implements	WaterHeaterCI
{
	private static final long serialVersionUID = 1L;

	public				WaterHeaterOutboundPort(ComponentI owner) throws Exception
	{
		super(WaterHeaterCI.class, owner);
	}

	public				WaterHeaterOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WaterHeaterCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.WaterHeater.WaterHeaterCI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		return ((WaterHeaterCI)this.getConnector()).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.WaterHeater.WaterHeaterCI#startWaterHeater()
	 */
	@Override
	public void			startWaterHeater() throws Exception
	{
		((WaterHeaterCI)this.getConnector()).startWaterHeater();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.WaterHeater.WaterHeaterCI#stopWaterHeater()
	 */
	@Override
	public void			stopWaterHeater() throws Exception
	{
		((WaterHeaterCI)this.getConnector()).stopWaterHeater();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.WaterHeater.WaterHeaterCI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		((WaterHeaterCI)this.getConnector()).setTargetTemperature(target);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.WaterHeater.WaterHeaterCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((WaterHeaterCI)this.getConnector()).getTargetTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.WaterHeater.WaterHeaterCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((WaterHeaterCI)this.getConnector()).getCurrentTemperature();
	}
}
