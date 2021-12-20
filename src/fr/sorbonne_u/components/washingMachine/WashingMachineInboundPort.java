package fr.sorbonne_u.components.washingMachine;

import java.time.Duration;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>WashingMachineInboundPort</code> implements an inbound port for the
* {@code WashingMachineCI} component interface.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
 * <p>Created on : 2021-10-16</p>
 * 
 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

public class WashingMachineInboundPort extends AbstractInboundPort implements WashingMachineCI {
	
	private static final long serialVersionUID = 1L;

	public WashingMachineInboundPort(ComponentI owner) throws Exception {
		super(WashingMachineCI.class, owner);
	}

	public WashingMachineInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, WashingMachineCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#isRunning()
	 */

	@Override
	public boolean isRunning() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).isRunning());
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#isRunning()
	 */

	@Override
	public void startWashingMachine() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineImplementationI)o).startWashingMachine();
						return null;
					 });
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#isRunning()
	 */
	
	@Override
	public void stopWashingMachine() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineImplementationI)o).stopWashingMachine();
						return null;
					 });

	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setTargetTemperature()
	 */
	
	@Override
	public void setTargetTemperature(int target) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineImplementationI)o).setTargetTemperature(target);
						return null;
					 });

	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getTargetTemperature()
	 */
	
	@Override
	public int getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getTargetTemperature());
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentTemperature()
	 */
	
	@Override
	public int getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getCurrentTemperature());
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setSpinningNumber()
	 */
	
	@Override
	public void setSpinningNumber(int target) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineImplementationI)o).setSpinningNumber(target);
						return null;
					 });
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getSpinningNumber()
	 */
	
	@Override
	public int getSpinningNumber() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getSpinningNumber());
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentSpinningNumber()
	 */
	
	@Override
	public int getCurrentSpinningNumber() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getCurrentSpinningNumber());
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setDuration()
	 */
	
	@Override
	public void setDuration(Duration duration) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineImplementationI)o).setDuration(duration);
						return null;
					 });
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getDuration()
	 */
	
	@Override
	public Duration getDuration() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getDuration());
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentDuration()
	 */
	
	@Override
	public Duration getCurrentDuration() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getCurrentDuration());
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setMode(Mode)
	 */
	
	@Override
	public void setMode(Program mode) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineImplementationI)o).setMode(mode);
						return null;
					 });
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getMode()
	 */
	@Override
	public Program getMode() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getMode());
	}
}
