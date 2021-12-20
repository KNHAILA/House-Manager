package fr.sorbonne_u.components.washingMachine;

import java.time.Duration;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.hem.HEM;
import fr.sorbonne_u.hem.registration.RegistrationConnector;
import fr.sorbonne_u.hem.registration.RegistrationOutboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>WashingMachine</code> a washing machine component.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	{@code targetTemperature >= 10 && targetTemperature <= 95}
* </pre>
* 
* <pre>
* invariant	{@code targetSpinningNumber >= 400 && targetTemperature <= 1200}
* </pre>
* 
* <pre>
* invariant	{@code mode >= 400 && targetTemperature <= 1200}
* </pre>
* 
* <pre>
* invariant	{@code mode==Mode.COTON || mode==Mode.COTONCI || mode==Mode.MIX40C || mode==Mode.SYNTETHETIQUES || mode==Mode.COUETTE == 0}
* </pre>
* 
* <p>Created on : 2021-10-16</p>
*
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

@OfferedInterfaces(offered={WashingMachineCI.class})
public class WashingMachine extends		AbstractComponent
implements	WashingMachineImplementationI {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>WashingMachineState</code> describes the operation
	 * states of the Washing machine.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-10-16</p>
	 * 
	 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
	 */
	protected static enum	WashingMachineState
	{
		/** washing machine is on.													*/
		ON,
		/** washing machine is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the washing inbound port used in tests.					*/
	public static final String		INBOUND_PORT_URI =
												"WASHING-MACHINE-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;
	/** fake current 	*/
	public static final int		FAKE_CURRENT_TEMPERATURE = 40;

	/** fake current 	*/
	public static final int		FAKE_CURRENT_SPINNING = 400;
	
	/** fake current 	*/
	public static final Program FAKE_CURRENT_MODE =Program.COTON;
	
	/** fake current 	*/
	public static final Duration FAKE_CURRENT_DURATION =Duration.ofHours(1);
	
	/** current state (on, off) of the washing machine.								*/
	protected WashingMachineState		currentState;
	
	/** inbound port offering the <code>HeaterCI</code> interface.			*/
	protected WashingMachineInboundPort	wip;
	
	/** target temperature for the washing.	*/
	protected int targetTemperature;
	
	/** target spinning for the washing.	*/
	protected int targetSpinning;
	
	/** target mode for the washing.	*/
	protected Program targetMode;
	
	/** target duration for the washing.	*/
	protected Duration targetDuration;
	
	protected RegistrationOutboundPort rop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception <i>to do </i>.
	 */
	protected WashingMachine() throws Exception
	{
		this(INBOUND_PORT_URI);
	}

	/**
	 * create a new washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code washingMachineInboundPortURI != null}
	 * pre	{@code !washingMachineInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param washingMachineInboundPortURI	URI of the inbound port to call the washingMachine component.
	 * @throws Exception			<i>to do </i>.
	 */
	
	protected WashingMachine(
		String washingMachineInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(washingMachineInboundPortURI);
	}

	/**
	 * create a new washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code washingMachineInboundPortURI != null}
	 * pre	{@code !washingMachineInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param washingMachineInboundPortURI		URI of the inbound port to call the washing machine component.
	 * @throws Exception				<i>to do </i>.
	 */
	protected			WashingMachine(
		String reflectionInboundPortURI,
		String washingMachineInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(washingMachineInboundPortURI);
	}

	/**
	 * create a new washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code washingMachineInboundPortURI != null}
	 * pre	{@code !washingMachineInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param washingMachineInboundPortURI	URI of the inbound port to call the washingMachine component.
	 * @throws Exception			<i>to do </i>.
	 */
	protected void		initialise(String washingMachineInboundPortURI) throws Exception
	{
		assert	washingMachineInboundPortURI != null;
		assert	!washingMachineInboundPortURI.isEmpty();

		this.currentState = WashingMachineState.OFF;
		this.targetTemperature = 40;
		this.targetMode = Program.COTON;
		this.targetDuration = Duration.ofHours(1);
		this.targetSpinning = 400;
		this.wip = new WashingMachineInboundPort(washingMachineInboundPortURI, this);
		this.wip.publishPort();

		if (WashingMachine.VERBOSE) {
			this.tracer.get().setTitle("Washing machine component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();		
		}
		
		this.rop = new RegistrationOutboundPort(this);
		this.rop.publishPort();
		this.doPortConnection(this.rop.getPortURI(), HEM.INBOUND_PORT_URI, RegistrationConnector.class.getCanonicalName());
		this.rop.register("", this.wip.getPortURI(), "src/fr/sorbonne_u/xml/washingMachine.xml");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.wip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the washingMachine is running.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return	true if the washingMachine is running.
	 */
	public boolean		internalIsRunning()
	{
		return this.currentState == WashingMachineState.ON;
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#isRunning()
	 */
	
	@Override
	public boolean		isRunning() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its state: " +
											this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}


	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#startWashingMachine()
	 */
	
	@Override
	public void			startWashingMachine() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine starts.\n");
		}
		assert	!this.internalIsRunning();

		this.currentState = WashingMachineState.ON;
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#stopWashingMachine()
	 */
	@Override
	public void			stopWashingMachine() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine stops.\n");
		}
		assert	this.internalIsRunning();

		this.currentState = WashingMachineState.OFF;
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#setTargetTemperature(int)
	 */
	@Override
	public void			setTargetTemperature(int target) throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	this.internalIsRunning();
		assert	target == 10 || target ==20 || target == 30 || target ==40 ||target == 60 || target ==95;

		this.targetTemperature = target;
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getTargetTemperature()
	 */
	@Override
	public int		getTargetTemperature() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing machine returns its target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		return this.targetTemperature;
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getCurrentTemperature()
	 */
	@Override
	public int		getCurrentTemperature() throws Exception
	{
		// Temporary implementation; would need a temperature sensor.
		int currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns the current"
							+ " temperature " + currentTemperature + ".\n");
			}

			return  currentTemperature;
		}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#setSpinningNumber()
	 */
	@Override
	public void setSpinningNumber(int target) throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine sets a new target "
										+ "spinning: " + target + ".\n");
		}

		assert	this.internalIsRunning();
		assert	target == 400 || target ==800 || target == 1000 || target ==1100 ||target == 1200;

		this.targetSpinning= target;	
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getSpinningNumber()
	 */
	@Override
	public int getSpinningNumber() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing machine returns its target"
							+ " spinning " + this.targetSpinning + ".\n");
		}

		return this.targetSpinning;
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getCurrentSpinningNumber()
	 */
	@Override
	public int getCurrentSpinningNumber() throws Exception {
		int currentSpinning = FAKE_CURRENT_SPINNING;
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns the current"
							+ " spinning " + currentSpinning + ".\n");
			}

			return  currentSpinning;
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#setDuration(Duration)
	 */
	@Override
	public void setDuration(Duration duration) throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine sets a new target "
										+ "duration: " + duration + ".\n");
		}

		assert	this.internalIsRunning();
		assert	duration.getSeconds()>=0;

		this.targetDuration= duration;	
		
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getDuration()
	 */
	@Override
	public Duration getDuration() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing machine returns its target"
							+ " duration " + this.targetDuration + ".\n");
		}

		return this.targetDuration;
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getCurrentDuration()
	 */
	@Override
	public Duration getCurrentDuration() throws Exception {
		// Temporary implementation; would need a temperature sensor.
				Duration currentDuration = FAKE_CURRENT_DURATION;
				if (WashingMachine.VERBOSE) {
					this.traceMessage("Washing Machine returns the current"
									+ " duration " + currentDuration + ".\n");
					}
					return  currentDuration;
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#setMode(Mode)
	 */
	@Override
	public void setMode(Program mode) throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine sets a new target "
										+ "mode: " + mode + ".\n");
		}

		assert	this.internalIsRunning();
		assert	mode == Program.COTON || mode ==Program.COTONCI || mode == Program.COUETTE || mode ==Program.MIX40C ||mode == Program.SYNTETHETIQUES;

		this.targetMode= mode;	
		
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI#getMode()
	 */
	@Override
	public Program getMode() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing machine returns its target"
							+ " mode " + this.targetMode + ".\n");
		}

		return this.targetMode;
	}
}
	// -----------------------------------------------------------------------------


