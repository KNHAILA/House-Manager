package fr.sorbonne_u.components.refrigerator;

import fr.sorbonne_u.components.connectors.AbstractConnector;

//-----------------------------------------------------------------------------
/**
* The class <code>RefrigeratorConnector</code> implements a connector for the
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
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

public class RefrigeratorConnector extends AbstractConnector implements RefrigeratorCI {

	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorCI#isRunning()
	 */
	
	@Override
	public boolean isRunning() throws Exception {
		return ((RefrigeratorCI)this.offering).isRunning();
	}
	
	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorCI#startRefrigerator()
	 */
	
	@Override
	public void startRefrigerator() throws Exception {
		((RefrigeratorCI)this.offering).startRefrigerator();
	}
	
	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorCI#stopRefrigerator()
	 */
	
	@Override
	public void stopRefrigerator() throws Exception {
		((RefrigeratorCI)this.offering).stopRefrigerator();
	}
	
	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorCI#setTargetTemperature()
	 */
	
	@Override
	public void setTargetTemperature(double target) throws Exception {
		((RefrigeratorCI)this.offering).setTargetTemperature(target);
	}
	
	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorCI#getTargetTemperature()
	 */
	
	@Override
	public double getTargetTemperature() throws Exception {
		return ((RefrigeratorCI)this.offering).getTargetTemperature();
	}
	
	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorCI#getCurrentTemperature()
	 */
	
	@Override
	public double getCurrentTemperature() throws Exception {
		return ((RefrigeratorCI)this.offering).getCurrentTemperature();
	}
}
