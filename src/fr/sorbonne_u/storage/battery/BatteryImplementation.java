package fr.sorbonne_u.storage.battery;

public interface BatteryImplementation {


	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return true if the Battery is currently running.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return				true if the Battery is currently running.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		isRunning() throws Exception;

	/**
	 * start the Battery.
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
	public void			startBattery() throws Exception;

	/**
	 * stop the Battery.
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
	public void			stopBattery() throws Exception;

	/**
	 * return the current percentage.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRunning()}
	 * post	{@code return >= -50.0 && return <= 50.0}
	 * </pre>
	 *
	 * @return				the current percentage.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		getCurrentPercentage() throws Exception;

}
