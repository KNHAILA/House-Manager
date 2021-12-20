/**
 * 
 */
package fr.sorbonne_u.components.washingMachine;

import java.time.Duration;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The interface <code>WashingMachineCI</code> defines the signatures of
 * the services implemented by a simple washing machine.
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
public interface WashingMachineCI extends OfferedCI, WashingMachineImplementationI, RequiredCI {
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#startWashingMachine()
	 */
	@Override
	public void			startWashingMachine() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#stopWashingMachine()
	 */
	@Override
	public void			stopWashingMachine() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#setTargetTemperature(int)
	 */
	@Override
	public void			setTargetTemperature(int target) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getTargetTemperature()
	 */
	@Override
	public int		getTargetTemperature() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getCurrentTemperature()
	 */
	@Override
	public int		getCurrentTemperature() throws Exception;
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#setSpinningNumber(int)
	 */
	@Override
	public void setSpinningNumber(int target) throws Exception;
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getSpinningNumber()
	 */
	@Override
	public int getSpinningNumber() throws Exception;
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getCurrentSpinningNumber()
	 */
	@Override
	public int getCurrentSpinningNumber() throws Exception ;
	
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#setDuration(Duration)
	 */
	@Override
	public void setDuration(Duration duration) throws Exception ;
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getDuration()
	 */
	@Override
	public Duration getDuration() throws Exception ;
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getCurrentDuration()
	 */
	@Override
	public Duration getCurrentDuration() throws Exception ;
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#setMode(Mode)
	 */
	@Override
	public void setMode(Program mode) throws Exception;
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getMode()
	 */
	@Override
	public Program getMode() throws Exception ;
	
}
// -----------------------------------------------------------------------------

