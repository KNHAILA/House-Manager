package fr.sorbonne_u.components.vacuumCleaner;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;


@OfferedInterfaces(offered={VacuumCleanerCI.class})
public class VacuumCleaner extends AbstractComponent implements VacuumCleanerImplementation {
	

	/** URI of the  VacuumCleaner in bound port used in tests.	**/				
	public static final String vacuumCleanerIP_URL = "vacuumCleanerIP_url";
	
	/** when true, methods trace their actions.								*/
	public static final boolean	VERBOSE = true;
	public static final VacuumCleanerState	INITIAL_STATE = VacuumCleanerState.OFF;
	public static final VacuumCleanerMode	INITIAL_MODE = VacuumCleanerMode.LOW;
	
	/** current state (on, off) of the  vacuumCleaner.							*/
	protected VacuumCleanerState	currentState;
	
	/** current mode of operation (low, high) of the  vacuumCleaner.			*/
	protected VacuumCleanerMode currentMode;
	
	/** in bound port offering the <code>VacuumCleanerCI</code> interface.		*/
	protected VacuumCleanerInboundPort vacuumCleanerIP;
	

	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a vacuumCleaner component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	{@code getState() == VacuumCleanerState.OFF}
	 * post	{@code getMode() == VacuumCleanerMode.LOW}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected VacuumCleaner()
	throws Exception
	{
		super(1, 0);
		this.initialise(vacuumCleanerIP_URL);
	}

	/**
	 * create a vacuumCleaner component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code vacuumCleanerInboundPortURI != null}
	 * pre	{@code !vacuumCleanerInboundPortURI.isEmpty()}
	 * post	{@code getState() == VacuumCleanerState.OFF}
	 * post	{@code getMode() == VacuumCleanerMode.LOW}
	 * </pre>
	 * 
	 * @param vacuumCleanerInboundPortURI	URI of the  vacuumCleaner inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected VacuumCleaner(String vacuumCleanerInboundPortURI)
	throws Exception
	{
		super(1, 0);
		this.initialise(vacuumCleanerInboundPortURI);
	}

	/**
	 * create a  vacuumCleaner component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code vacuumCleanerInboundPortURI != null}
	 * pre	{@code !vacuumCleanerInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * post	{@code getState() == VacuumCleanerState.OFF}
	 * post	{@code getMode() == VacuumCleanerMode.LOW}
	 * </pre>
	 *
	 * @param vacuumCleanerInboundPortURI	URI of the  vacuumCleaner inbound port.
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected VacuumCleaner(
		String vacuumCleanerInboundPortURI,
		String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(vacuumCleanerInboundPortURI);
	}

	/**
	 * initialise the  vacuumCleaner component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code vacuumCleanerInboundPortURI != null}
	 * pre	{@code !vacuumCleanerInboundPortURI.isEmpty()}
	 * post	{@code getState() == VacuumCleanerState.OFF}
	 * post	{@code getMode() == VacuumCleanerMode.LOW}
	 * </pre>
	 * 
	 * @param vacuumCleanerInboundPortURI	URI of the  vacuumCleaner inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void	initialise(String vacuumCleanerInboundPortURI)
	throws Exception
	{
		assert	vacuumCleanerInboundPortURI != null :
					new PreconditionException(
										"vacuumCleanerInboundPortURI != null");
		assert	!vacuumCleanerInboundPortURI.isEmpty() :
					new PreconditionException(
										"!vacuumCleanerInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.vacuumCleanerIP = new VacuumCleanerInboundPort(vacuumCleanerInboundPortURI, this);
		this.vacuumCleanerIP.publishPort();

		if (VacuumCleaner.VERBOSE) {
			this.tracer.get().setTitle("vacuumCleaner component");
			this.tracer.get().setRelativePosition(2, 1);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			this.vacuumCleanerIP.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.vacuumCleaner.VacuumCleanerImplementationI#getState()
	 */
	@Override
	public VacuumCleanerState	getState() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("vacuumCleaner returns its state : " +
													this.currentState + ".\n");
		}

		return this.currentState;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.vacuumCleaner.VacuumCleanerImplementationI#getMode()
	 */
	@Override
	public VacuumCleanerMode getMode() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("vacuumCleaner returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.vacuumCleaner.VacuumCleanerImplementationI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("vacuumCleaner is turned on.\n");
		}

		assert	this.getState() == VacuumCleanerState.OFF :
					new PreconditionException(
										"getState() == VacuumCleanerState.OFF");

		this.currentState = VacuumCleanerState.ON;
		this.currentMode = VacuumCleanerMode.LOW;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.vacuumCleaner.VacuumCleanerImplementationI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("VacuumCleaner is turned off.\n");
		}

		assert	this.getState() == VacuumCleanerState.ON :
					new PreconditionException(
										"getState() == VacuumCleanerState.ON");

		this.currentState = VacuumCleanerState.OFF;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.vacuumCleaner.VacuumCleanerImplementationI#setHigh()
	 */
	@Override
	public void	setHigh() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("VacuumCleaner is set high.\n");
		}

		assert	this.getState() == VacuumCleanerState.ON :
					new PreconditionException(
										"getState() == VacuumCleanerState.ON");
		assert	this.getMode() == VacuumCleanerMode.LOW :
					new PreconditionException("getMode() == VacuumCleanerMode.LOW");

		this.currentMode = VacuumCleanerMode.HIGH;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.vacuumCleaner.VacuumCleanerImplementationI#setLow()
	 */
	@Override
	public void	setLow() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("VacuumCleaner is set low.\n");
		}

		assert	this.getState() == VacuumCleanerState.ON :
					new PreconditionException(
										"getState() == VacuumCleanerState.ON");
		assert	this.getMode() == VacuumCleanerMode.HIGH :
					new PreconditionException(
										"getMode() == VacuumCleanerMode.HIGH");

		this.currentMode = VacuumCleanerMode.LOW;
	}
}

