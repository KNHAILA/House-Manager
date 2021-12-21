package fr.sorbonne_u.components.washingMachine;

import java.time.Duration;

/**
 * The interface <code>WashingMachineImplementationI</code> defines the signatures of
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
 * <p>Created on : 2021-10-13</p>
 * 
 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 */

public interface WashingMachineImplementationI {
	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return true if the washing machine is currently running.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return				true if the washing machine is currently running.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		isRunning() throws Exception;

	/**
	 * start the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isRunning()}
	 * post	{@code isRunning()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			startWashingMachine() throws Exception;

	/**
	 * stop the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post	{@code !isRunning()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			stopWashingMachine() throws Exception;

	/**
	 * set the target temperature for controlling the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * pre	{@code target == 10 || target == 20 || target == 30 || target == 40 || target == 60 || target == 95}
	 * post	{@code target == getTargetTemperature()}
	 * </pre>
	 *
	 * @param target		the new target temperature.
	 * @throws Exception	<i>to do</i>.
	 */
	public void setTargetTemperature(int target) throws Exception;

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post	{@code return == 10 || return == 20 || return == 30 || return == 40 || return == 60 || return == 95}
	 * </pre>
	 *
	 * @return				the current target temperature.
	 * @throws Exception	<i>to do</i>.
	 */
	public int getTargetTemperature() throws Exception;

	/**
	 * return the current temperature measured by the thermostat.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post {@code return == 10 || return == 20 || return == 30 || return == 40 || return == 60 || return == 95}
	 * </pre>
	 *
	 * @return				the current temperature measured by the thermostat.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getCurrentTemperature() throws Exception;
	
	/**
	 * set the target spinning number for controlling the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * pre	{@code target == 400 || target == 800 || target == 1000 || target == 1100 || target == 1200}
	 * post	{@code target == getSpinningNumber()}
	 * </pre>
	 *
	 * @param target		the new spinning number.
	 * @throws Exception	<i>to do</i>.
	 */
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
	public Program getMode() throws Exception;	
}
// -----------------------------------------------------------------------------



