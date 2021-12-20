package fr.sorbonne_u.components.waterHeater;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.refrigerator.RefrigeratorCI;
import fr.sorbonne_u.hem.HEM;
import fr.sorbonne_u.hem.registration.RegistrationCI;
import fr.sorbonne_u.hem.registration.RegistrationConnector;
import fr.sorbonne_u.hem.registration.RegistrationOutboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>WaterHeater</code> a water heater component.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	{@code targetTemperature >= 80.0 && targetTemperature <= 160.0}
* </pre>
* 
* <p>Created on : 2021-10-12</p>
* 
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

@OfferedInterfaces(offered={WaterHeaterCI.class})
@RequiredInterfaces(required={RegistrationCI.class})

public class WaterHeater extends		AbstractComponent
implements	WaterHeaterImplementationI {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>WaterHeaterState</code> describes the operation
	 * states of the water heater.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-10-12</p>
	 * 
	 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
	 */
	protected static enum	WaterHeaterState
	{
		/** water heater is on.													*/
		ON,
		/** water heater is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the water heater inbound port used in tests.					*/
	public static final String		INBOUND_PORT_URI =
												"WATER-HEATER-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;
	/** fake current 	*/
	public static final double		FAKE_CURRENT_TEMPERATURE = 90;

	/** current state (on, off) of the water heater.								*/
	protected WaterHeaterState currentState;
	/** inbound port offering the <code>WaterHeaterCI</code> interface.			*/
	protected WaterHeaterInboundPort	wip;
	/** target temperature for the water heating.	*/
	protected double			targetTemperature;
	protected RegistrationOutboundPort rop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new water heater.
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
	protected WaterHeater() throws Exception
	{
		this(INBOUND_PORT_URI);
	}

	/**
	 * create a new water heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code waterHeaterInboundPortURI != null}
	 * pre	{@code !waterHeaterInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param waterHeaterInboundPortURI	URI of the inbound port to call the water heater component.
	 * @throws Exception			<i>to do </i>.
	 */
	protected			WaterHeater(
		String waterHeaterInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(waterHeaterInboundPortURI);
	}

	/**
	 * create a new water heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code waterHeaterInboundPortURI != null}
	 * pre	{@code !waterHeaterInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param waterHeaterInboundPortURI		URI of the inbound port to call the water heater component.
	 * @throws Exception				<i>to do </i>.
	 */
	protected			WaterHeater(
		String reflectionInboundPortURI,
		String waterHeaterInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(waterHeaterInboundPortURI);
	}

	/**
	 * create a new water Heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code waterHeaterInboundPortURI != null}
	 * pre	{@code !waterHeaterrInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param waterHeaterInboundPortURI	URI of the inbound port to call the waterHeater component.
	 * @throws Exception			<i>to do </i>.
	 */
	protected void		initialise(String waterHeaterInboundPortURI) throws Exception
	{
		assert	waterHeaterInboundPortURI != null;
		assert	!waterHeaterInboundPortURI.isEmpty();

		this.currentState = WaterHeaterState.OFF;
		this.targetTemperature = 80.0;
		this.wip = new WaterHeaterInboundPort(waterHeaterInboundPortURI, this);
		this.wip.publishPort();

		if (WaterHeater.VERBOSE) {
			this.tracer.get().setTitle("WaterHeater component");
			this.tracer.get().setRelativePosition(1, 1);
			this.toggleTracing();		
		}
		
		this.rop = new RegistrationOutboundPort(this);
		this.rop.publishPort();
		this.doPortConnection(this.rop.getPortURI(), HEM.INBOUND_PORT_URI, RegistrationConnector.class.getCanonicalName());
		System.out.println("hereeeeee");
	    this.rop.register("", this.wip.getPortURI(), "src/fr/sorbonne_u/xml/waterHeater.xml");
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
	 * return true if the water heater is running.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return	true if the water heater is running.
	 */
	public boolean		internalIsRunning()
	{
		return this.currentState == WaterHeaterState.ON;
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.waterHeater.WaterHeaterImplementationI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		if (WaterHeater.VERBOSE) {
			this.traceMessage("Water Heater returns its state: " +
											this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.waterHeater.WaterHeaterImplementationI#startWaterHeater()
	 */
	@Override
	public void			startWaterHeater() throws Exception
	{
		if (WaterHeater.VERBOSE) {
			this.traceMessage("Water Heater starts.\n");
		}
		assert	!this.internalIsRunning();

		this.currentState = WaterHeaterState.ON;
	}

	/**
	 * @see fr.sorbonne_u.components.waterHeater.WaterHeaterImplementationI#stopWaterHeater()
	 */
	@Override
	public void			stopWaterHeater() throws Exception
	{
		if (WaterHeater.VERBOSE) {
			this.traceMessage("Water Heater stops.\n");
		}
		assert	this.internalIsRunning();

		this.currentState = WaterHeaterState.OFF;
	}

	/**
	 * @see fr.sorbonne_u.components.waterHeater.WaterHeaterImplementationI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		if (WaterHeater.VERBOSE) {
			this.traceMessage("Water Heater sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	this.internalIsRunning();
		assert	target >= 80.0 && target <=160.0;

		this.targetTemperature = target;
	}

	/**
	 * @see fr.sorbonne_u.components.waterHeater.WaterHeaterImplementationI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		if (WaterHeater.VERBOSE) {
			this.traceMessage("Water Heater returns its target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		return this.targetTemperature;
	}

	/**
	 * @see fr.sorbonne_u.components.waterHeater.WaterHeaterImplementationI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (WaterHeater.VERBOSE) {
			this.traceMessage("Water Heater returns the current"
							+ " temperature " + currentTemperature + ".\n");
			}

			return  currentTemperature;
		}
}
	// -----------------------------------------------------------------------------


