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
	public double		getCurrentTemperature() throws Exception;
	
	@Override
	public void setSpinningNumber(int target) throws Exception;

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post	{@code return == 400 || return == 800 || return == 1000 || return == 1100 || return == 1200}
	 * </pre>
	 *
	 * @return				the current spinning number.
	 * @throws Exception	<i>to do</i>.
	 */
	@Override
	public int getSpinningNumber() throws Exception;

	/**
	 * return the current spinning number.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post {@code return >= 0 || return <= 1200}
	 * </pre>
	 *
	 * @return				the current spinning number.
	 * @throws Exception	<i>to do</i>.
	 */
	@Override
	public int getCurrentSpinningNumber() throws Exception;
	
	/**
	 * set the target duration for controlling the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * pre	{@code duration >= 0}
	 * post	{@code target == getDuration()}
	 * </pre>
	 *
	 * @param target		the new duration.
	 * @throws Exception	<i>to do</i>.
	 */
	@Override
	public void setDuration(Duration duration) throws Exception;

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post	{@code return>=0}
	 * </pre>
	 *
	 * @return				the current duration.
	 * @throws Exception	<i>to do</i>.
	 */
	@Override
	public Duration getDuration() throws Exception;

	/**
	 * return the current duration.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post {@code Duration>= 0}
	 * </pre>
	 *
	 * @return				the current duration.
	 * @throws Exception	<i>to do</i>.
	 */
	@Override
	public Duration getCurrentDuration() throws Exception;
	
	/**
	 * set the target mode for controlling the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * pre	{@code mode==Mode.COTON || mode==Mode.COTONCI || mode==Mode.MIX40C || mode==Mode.SYNTETHETIQUES || mode==Mode.COUETTE == 0}
	 * post	{@code target == getDuration()}
	 * </pre>
	 *
	 * @param target		the new mode.
	 * @throws Exception	<i>to do</i>.
	 */
	@Override
	public void setMode(Program mode) throws Exception;

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post	{@code return==Mode.COTON || return==Mode.COTONCI || return==Mode.MIX40C || return==Mode.SYNTETHETIQUES || return==Mode.COUETTE == 0}
	 * </pre>
	 *
	 * @return				the current mode.
	 * @throws Exception	<i>to do</i>.
	 */
	@Override
	public Program getMode() throws Exception;	

}
// -----------------------------------------------------------------------------

