package fr.sorbonne_u.components.waterHeater;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>WaterHeaterInboundPort</code> implements an inbound port for the
* {@code WaterHeaterCI} component interface.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
 * <p>Created on : 2021-10-06</p>
 * 
 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

public class WaterHeaterInboundPort extends AbstractInboundPort implements WaterHeaterCI {
	
	private static final long serialVersionUID = 1L;

	public WaterHeaterInboundPort(ComponentI owner) throws Exception {
		super(WaterHeaterCI.class, owner);
	}

	public WaterHeaterInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, WaterHeaterCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#isRunning()
	 */

	@Override
	public boolean isRunning() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterImplementationI)o).isRunning());
	}
	
	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#startWaterHeater()
	 */

	@Override
	public void startWaterHeater() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterImplementationI)o).startWaterHeater();
						return null;
					 });
	}

	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#stopWaterHeater()
	 */
	
	@Override
	public void stopWaterHeater() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterImplementationI)o).stopWaterHeater();
						return null;
					 });

	}

	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#setTargetTemperature(double)
	 */
	
	@Override
	public void setTargetTemperature(double target) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WaterHeaterImplementationI)o).setTargetTemperature(target);
						return null;
					 });

	}
	
	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#getTargetTemperature()
	 */
	
	@Override
	public double getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterImplementationI)o).getTargetTemperature());
	}
	
	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#getCurrentTemperature()
	 */
	
	@Override
	public double getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WaterHeaterImplementationI)o).getCurrentTemperature());
	}

}
