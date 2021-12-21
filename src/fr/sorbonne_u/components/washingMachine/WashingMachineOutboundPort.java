package fr.sorbonne_u.components.washingMachine;

import java.time.Duration;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>WashingMachineOutboundPort</code> implements an outbound port for the
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
* <p>Created on : 2021-10-17</p>
* 
* * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

public class WashingMachineOutboundPort extends AbstractOutboundPort implements WashingMachineCI {
	
	private static final long serialVersionUID = 1L;

	public WashingMachineOutboundPort(ComponentI owner)
			throws Exception {
		super(WashingMachineCI.class, owner);
	}

	public WashingMachineOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, WashingMachineCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#isRunning()
	 */
	
	@Override
	public boolean isRunning() throws Exception {
		return ((WashingMachineCI)this.getConnector()).isRunning();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#startWashingMachine()
	 */
	@Override
	public void startWashingMachine() throws Exception {
		((WashingMachineCI)this.getConnector()).startWashingMachine();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#stopWashingMachine()
	 */
	
	@Override
	public void stopWashingMachine() throws Exception {
		((WashingMachineCI)this.getConnector()).stopWashingMachine();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setTargetTemperature(int)
	 */
	
	@Override
	public void setTargetTemperature(int target) throws Exception {
		((WashingMachineCI)this.getConnector()).setTargetTemperature(target);
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getTargetTemperature()
	 */

	@Override
	public int getTargetTemperature() throws Exception {
		return ((WashingMachineCI)this.getConnector()).getTargetTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentTemperature()
	 */
	
	@Override
	public double getCurrentTemperature() throws Exception {
		return ((WashingMachineCI)this.getConnector()).getCurrentTemperature();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setDuration(Duration)
	 */
	@Override
	public void setDuration(Duration duration) throws Exception {
		((WashingMachineCI)this.getConnector()).setDuration(duration);
	}
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getDuration()
	 */
	@Override
	public Duration getDuration() throws Exception {
		return ((WashingMachineCI)this.getConnector()).getDuration();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentDuration()
	 */
	
	@Override
	public Duration getCurrentDuration() throws Exception {
		return ((WashingMachineCI)this.getConnector()).getCurrentDuration();
	}
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setSpinningNumber(int)
	 */
	
	@Override
	public void setSpinningNumber(int target) throws Exception {
		((WashingMachineCI)this.getConnector()).setSpinningNumber(target);
	}
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getSpinningNumber()
	 */
	
	@Override
	public int getSpinningNumber() throws Exception {
		return ((WashingMachineCI)this.getConnector()).getSpinningNumber();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentSpinningNumber()
	 */
	@Override
	public int getCurrentSpinningNumber() throws Exception {
		return ((WashingMachineCI)this.getConnector()).getCurrentSpinningNumber();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setMode(Mode)
	 */
	
	@Override
	public void setMode(Program mode) throws Exception {
		((WashingMachineCI)this.getConnector()).setMode(mode);
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getMode()
	 */
	
	@Override
	public Program getMode() throws Exception {
		return ((WashingMachineCI)this.getConnector()).getMode();
	}
}
