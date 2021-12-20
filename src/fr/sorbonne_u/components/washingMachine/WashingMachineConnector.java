package fr.sorbonne_u.components.washingMachine;

import java.time.Duration;

import fr.sorbonne_u.components.connectors.AbstractConnector;

//-----------------------------------------------------------------------------
/**
* The class <code>WashingMachineConnector</code> implements a connector for the
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

public class WashingMachineConnector extends AbstractConnector implements WashingMachineCI {

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#isRunning()
	 */
	
	@Override
	public boolean isRunning() throws Exception {
		return ((WashingMachineCI)this.offering).isRunning();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#startWashingMachine()
	 */
	
	@Override
	public void startWashingMachine() throws Exception {
		((WashingMachineCI)this.offering).startWashingMachine();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#stopWashingMachine()
	 */
	
	@Override
	public void stopWashingMachine() throws Exception {
		((WashingMachineCI)this.offering).stopWashingMachine();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setTargetTemperature(int)
	 */
	
	@Override
	public void setTargetTemperature(int target) throws Exception {
		((WashingMachineCI)this.offering).setTargetTemperature(target);
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getTargetTemperature()
	 */
	
	@Override
	public int getTargetTemperature() throws Exception {
		return ((WashingMachineCI)this.offering).getTargetTemperature();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentTemperature()
	 */
	
	@Override
	public int getCurrentTemperature() throws Exception {
		return ((WashingMachineCI)this.offering).getCurrentTemperature();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setSpinningNumber(int)
	 */
	@Override
	public void setSpinningNumber(int target) throws Exception {
		((WashingMachineCI)this.offering).setSpinningNumber(target);
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getSpinningNumber()
	 */
	
	@Override
	public int getSpinningNumber() throws Exception {
		return ((WashingMachineCI)this.offering).getSpinningNumber();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentSpinningNumber()
	 */
	
	@Override
	public int getCurrentSpinningNumber() throws Exception {
		return ((WashingMachineCI)this.offering).getCurrentSpinningNumber();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setDuration(Duration)
	 */
	
	@Override
	public void setDuration(Duration duration) throws Exception {
		((WashingMachineCI)this.offering).setDuration(duration);
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getDuration()
	 */
	
	@Override
	public Duration getDuration() throws Exception {
		return ((WashingMachineCI)this.offering).getDuration();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentDuration()
	 */
	@Override
	public Duration getCurrentDuration() throws Exception {
		return ((WashingMachineCI)this.offering).getCurrentDuration();
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setMode(Mode)
	 */
	@Override
	public void setMode(Program mode) throws Exception {
		((WashingMachineCI)this.offering).setMode(mode);
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getMode()
	 */
	
	@Override
	public Program getMode() throws Exception {
		return ((WashingMachineCI)this.offering).getMode();
	}
}
