package fr.sorbonne_u.components.fan;

public interface FanImplementation
{
	public static enum	State
	{
		/**  fan is on.												*/
		ON,
		/**  fan is off.												*/
		OFF
	}

	/**
	 * The enumeration <code>FanMode</code> describes the operation
	 * modes of the  fan.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * The  fan can be either in <code>LOW</code> mode (warm and slow) or
	 * in <code>HIGH</code> mode (hot and fast).
	 * </p>
	 * 
	 * <p>Created on : 2021-09-09</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum	Mode
	{
		/** low mode is just warm and the fan is slower.					*/
		LOW,			
		/** high mode is hot and the fan turns faster.						*/
		HIGH
	}

	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the  fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the  fan.
	 * @throws Exception 	<i>to do</i>.
	 */
	public State	getState() throws Exception;

	/**
	 * return the current operation mode of the  fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the  fan.
	 * @throws Exception 	<i>to do</i>.
	 */
	public Mode	getMode() throws Exception;

	/**
	 * turn on the  fan, put in the low temperature and slow fan mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == FanState.OFF}
	 * post	{@code getMode() == FanMode.LOW}
	 * post	{@code getState() == FanState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	turnOn() throws Exception;

	/**
	 * turn off the  fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	{@code getState() == FanState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	turnOff() throws Exception;

	/**
	 * set the  fan in high mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == FanState.ON}
	 * pre	{@code getMode() == FanMode.LOW}
	 * post	{@code getMode() == FanMode.HIGH}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	setHigh() throws Exception;

	/**
	 * set the  fan in low mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == FanState.ON}
	 * pre	{@code getMode() == FanMode.HIGH}
	 * post	{@code getMode() == FanMode.LOW}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	setLow() throws Exception;

}
