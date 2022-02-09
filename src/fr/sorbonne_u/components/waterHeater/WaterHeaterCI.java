
package fr.sorbonne_u.components.waterHeater;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The interface <code>WaterWaterHeaterCI</code> defines the signatures of
 * the services implemented by a simple water WaterWaterHeater.
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
public interface		WaterHeaterCI
extends		WaterHeaterImplementationI,
			RequiredCI,
			OfferedCI
{
	/**
	 * @see fr.sorbonne_u.components.WaterHeater.WaterHeaterImplementationI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.WaterHeater.WaterHeaterImplementationI#startWaterHeater()
	 */
	@Override
	public void			startWaterHeater() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.WaterHeater.WaterHeaterImplementationI#stopWaterHeater()
	 */
	@Override
	public void			stopWaterHeater() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.WaterHeater.WaterHeaterImplementationI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.WaterHeater.WaterHeaterImplementationI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.WaterHeater.WaterHeaterImplementationI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception;
}
// -----------------------------------------------------------------------------
