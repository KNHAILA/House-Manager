package fr.sorbonne_u.components.vacuumCleaner;

public interface VacuumCleanerImplementation {

	public static enum	VacuumCleanerState
	{
		/**  vacuumCleaner is on.												*/
		ON,
		/**  vacuumCleaner is off.												*/
		OFF
	}

	/**
	 * The enumeration <code>VacuumCleanerMode</code> describes the operation
	 * modes of the  vacuumCleaner.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * The  vacuumCleaner can be either in <code>LOW</code> mode (warm and slow) or
	 * in <code>HIGH</code> mode (hot and fast).
	 * </p>
	 * 
	 * <p>Created on : 2021-09-09</p>
	 * 
	 * @author	<a href="mailto:Jacques.MalenvacuumCleanert@lip6.fr">Jacques MalenvacuumCleanert</a>
	 */
	public static enum	VacuumCleanerMode
	{
		/** low mode is just warm and the vacuumCleaner is slower.					*/
		LOW,			
		/** high mode is hot and the vacuumCleaner turns faster.						*/
		HIGH
	}

	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the  vacuumCleaner.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the  vacuumCleaner.
	 * @throws Exception 	<i>to do</i>.
	 */
	public VacuumCleanerState getState() throws Exception;

	/**
	 * return the current operation mode of the  vacuumCleaner.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the  vacuumCleaner.
	 * @throws Exception 	<i>to do</i>.
	 */
	public VacuumCleanerMode getMode() throws Exception;

	/**
	 * turn on the  vacuumCleaner, put in the low temperature and slow vacuumCleaner mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == VacuumCleanerState.OFF}
	 * post	{@code getMode() == VacuumCleanerMode.LOW}
	 * post	{@code getState() == VacuumCleanerState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	turnOn() throws Exception;

	/**
	 * turn off the  vacuumCleaner.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	{@code getState() == VacuumCleanerState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	turnOff() throws Exception;

	/**
	 * set the  vacuumCleaner in high mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == VacuumCleanerState.ON}
	 * pre	{@code getMode() == VacuumCleanerMode.LOW}
	 * post	{@code getMode() == VacuumCleanerMode.HIGH}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	setHigh() throws Exception;

	/**
	 * set the  vacuumCleaner in low mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == VacuumCleanerState.ON}
	 * pre	{@code getMode() == VacuumCleanerMode.HIGH}
	 * post	{@code getMode() == VacuumCleanerMode.LOW}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	setLow() throws Exception;
}
