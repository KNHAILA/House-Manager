package fr.sorbonne_u.components.refrigerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.hem.registration.RegistrationConnector;
import fr.sorbonne_u.hem.registration.RegistrationOutboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>Refrigerator</code> a refrigerator component.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	{@code targetTemperature >= 1.0 && targetTemperature <= 7.0}
* </pre>
* 
* <p>Created on : 2021-10-06</p>
* 
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

@OfferedInterfaces(offered={RefrigeratorCI.class})
public class Refrigerator extends		AbstractComponent
implements	RefrigeratorImplementationI {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>RefrigeratorState</code> describes the operation
	 * states of the refrigerator.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-10-06</p>
	 * 
	 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
	 */
	protected static enum	RefrigeratorState
	{
		/** refrigerator is on.													*/
		ON,
		/** refrigerator is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the refrigerator inbound port used in tests.					*/
	public static final String		INBOUND_PORT_URI =
												"REFRIGERATOR-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;
	/** fake current 	*/
	public static final double		FAKE_CURRENT_TEMPERATURE = 1.7;

	/** current state (on, off) of the refrigerator.								*/
	protected RefrigeratorState		currentState;
	/** inbound port offering the <code>HeaterCI</code> interface.			*/
	protected RefrigeratorInboundPort	rip;
	/** target temperature for the heating.	*/
	protected double			targetTemperature;
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
	protected Refrigerator() throws Exception
	{
		this(INBOUND_PORT_URI);
	}

	/**
	 * create a new refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code refrigeratorInboundPortURI != null}
	 * pre	{@code !refrigeratorInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param refrigeratorInboundPortURI	URI of the inbound port to call the refrigerator component.
	 * @throws Exception			<i>to do </i>.
	 */
	protected			Refrigerator(
		String refrigeratorInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(refrigeratorInboundPortURI);
	}

	/**
	 * create a new refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code refrigeratorInboundPortURI != null}
	 * pre	{@code !refrigeratorInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param refrigeratorInboundPortURI		URI of the inbound port to call the refrigerator component.
	 * @throws Exception				<i>to do </i>.
	 */
	protected			Refrigerator(
		String reflectionInboundPortURI,
		String refrigeratorInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(refrigeratorInboundPortURI);
	}

	/**
	 * create a new refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code refrigeratorInboundPortURI != null}
	 * pre	{@code !refrigeratorInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param refrigeratorInboundPortURI	URI of the inbound port to call the refrigerator component.
	 * @throws Exception			<i>to do </i>.
	 */
	protected void		initialise(String refrigeratorInboundPortURI) throws Exception
	{
		assert	refrigeratorInboundPortURI != null;
		assert	!refrigeratorInboundPortURI.isEmpty();

		this.currentState = RefrigeratorState.OFF;
		this.targetTemperature = 20.0;
		this.rip = new RefrigeratorInboundPort(refrigeratorInboundPortURI, this);
		this.rip.publishPort();

		if (Refrigerator.VERBOSE) {
			this.tracer.get().setTitle("Refrigerator component");
			this.tracer.get().setRelativePosition(1, 1);
			this.toggleTracing();		
		}
		
		this.rop = new RegistrationOutboundPort(this);
		this.rop.publishPort();
	//	this.doPortConnection(this.rop.getPortURI(), refrigeratorInboundPortURI, RegistrationConnector.class.getCanonicalName());
	//	this.rop.register(refrigeratorInboundPortURI, refrigeratorInboundPortURI, refrigeratorInboundPortURI)
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
			this.rip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the refrigerator is running.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return	true if the heater is running.
	 */
	public boolean		internalIsRunning()
	{
		return this.currentState == RefrigeratorState.ON;
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorImplementationI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator returns its state: " +
											this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorImplementationI#startRefrigerator()
	 */
	@Override
	public void			startRefrigerator() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator starts.\n");
		}
		assert	!this.internalIsRunning();

		this.currentState = RefrigeratorState.ON;
	}

	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorImplementationI#stopRefrigerator()
	 */
	@Override
	public void			stopRefrigerator() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator stops.\n");
		}
		assert	this.internalIsRunning();

		this.currentState = RefrigeratorState.OFF;
	}

	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorImplementationI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	this.internalIsRunning();
		assert	target >= 1.0 && target <=7.0;

		this.targetTemperature = target;
	}

	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorImplementationI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator returns its target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		return this.targetTemperature;
	}

	/**
	 * @see fr.sorbonne_u.components.refrigerator.RefrigeratorImplementationI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator returns the current"
							+ " temperature " + currentTemperature + ".\n");
			}

			return  currentTemperature;
		}
}
	// -----------------------------------------------------------------------------


