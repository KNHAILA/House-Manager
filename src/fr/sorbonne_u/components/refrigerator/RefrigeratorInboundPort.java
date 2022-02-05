package fr.sorbonne_u.components.refrigerator;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>RefrigeratorInboundPort</code> implements an inbound port for the
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
 * <p>Created on : 2021-10-06</p>
 * 
 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/
public class RefrigeratorInboundPort extends AbstractInboundPort implements RefrigeratorCI {
	
	private static final long serialVersionUID = 1L;

	public RefrigeratorInboundPort(ComponentI owner) throws Exception {
		super(RefrigeratorCI.class, owner);
	}

	public RefrigeratorInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RefrigeratorCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorCI#isRunning()
	 */

	@Override
	public boolean isRunning() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigeratorImplementationI)o).isRunning());
	}
	
	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorCI#startRefrigerator()
	 */

	@Override
	public void startRefrigerator() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigeratorImplementationI)o).startRefrigerator();
						return null;
					 });
	}

	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorCI#stopRefrigerator()
	 */
	
	@Override
	public void stopRefrigerator() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigeratorImplementationI)o).stopRefrigerator();
						return null;
					 });

	}

	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorCI#setTargetTemperature(double)
	 */
	
	@Override
	public void setTargetTemperature(double target) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((RefrigeratorImplementationI)o).setTargetTemperature(target);
						return null;
					 });

	}
	
	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorCI#getTargetTemperature()
	 */
	
	@Override
	public double getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigeratorImplementationI)o).getTargetTemperature());
	}
	
	/**
	 * @see fr.sorbonne_u.components.Refrigerator.RefrigeratorCI#getCurrentTemperature()
	 */
	
	@Override
	public double getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RefrigeratorImplementationI)o).getCurrentTemperature());
	}
}
