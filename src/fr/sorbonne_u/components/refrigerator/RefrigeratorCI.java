/**
 * 
 */
package fr.sorbonne_u.components.refrigerator;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The interface <code>RefrigeratorCI</code> defines the signatures of
 * the services implemented by a simple refrigerator.
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
public interface		RefrigeratorCI
extends		RefrigeratorImplementationI,
			RequiredCI,
			OfferedCI
{
	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorImplementationI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorImplementationI#startRefrigerator()
	 */
	@Override
	public void			startRefrigerator() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorImplementationI#stopRefrigerator()
	 */
	@Override
	public void			stopRefrigerator() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorImplementationI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorImplementationI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorImplementationI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception;
}
// -----------------------------------------------------------------------------