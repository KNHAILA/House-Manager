package fr.sorbonne_u.components.waterHeater;

import fr.sorbonne_u.components.connectors.AbstractConnector;

//-----------------------------------------------------------------------------
/**
* The class <code>WaterHeaterConnector</code> implements a connector for the
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
* <p>Created on : 2021-10-12</p>
* 
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

public class WaterHeaterConnector extends AbstractConnector implements WaterHeaterCI {

	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#isRunning()
	 */
	
	@Override
	public boolean isRunning() throws Exception {
		return ((WaterHeaterCI)this.offering).isRunning();
	}
	
	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#startRefrigerator()
	 */
	
	@Override
	public void startWaterHeater() throws Exception {
		((WaterHeaterCI)this.offering).startWaterHeater();
	}
	
	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#stopRefrigerator()
	 */
	
	@Override
	public void stopWaterHeater() throws Exception {
		((WaterHeaterCI)this.offering).stopWaterHeater();
	}
	
	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#setTargetTemperature()
	 */
	
	@Override
	public void setTargetTemperature(double target) throws Exception {
		((WaterHeaterCI)this.offering).setTargetTemperature(target);
	}
	
	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#getTargetTemperature()
	 */
	
	@Override
	public double getTargetTemperature() throws Exception {
		return ((WaterHeaterCI)this.offering).getTargetTemperature();
	}
	
	/**
	 * @see fr.sorbonne_u.components.waterheater.WaterHeaterCI#getCurrentTemperature()
	 */
	
	@Override
	public double getCurrentTemperature() throws Exception {
		return ((WaterHeaterCI)this.offering).getCurrentTemperature();
	}
}
