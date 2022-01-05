package fr.sorbonne_u.production_unities.miniHydroelectricDam;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { MiniHydroelectricDamCI.class })
public class MiniHydroelectricDam extends AbstractComponent implements MiniHydroelectricDamImplementation {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected static enum MiniHydroelectricDamState {
		/** winde turbine is on. */
		ON,
		/** winde turbine is off. */
		OFF
	}

	/** URI of the wind turbine in bound port used in tests. */
	public static final String Wind_Turbine_INBOUND_PORT_URI = "Wind_Turbine";
	/** when true, methods trace their actions. */
	public static final boolean VERBOSE = true;

	/** in bound port offering the <code>WindTurbineCI</code> interface. */
	protected MiniHydroelectricDamInboundPort wtip;

	protected MiniHydroelectricDamState currentState;
	
	/** fake current 	*/
	public static final double		FAKE_CURRENT_WIND_SPEED = 10.0;
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an wind turbine component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code ELECTRIC_METER_INBOUND_PORT_URI != null}
	 * pre	{@code !ELECTRIC_METER_INBOUND_PORT_URI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam() throws Exception {
		this(Wind_Turbine_INBOUND_PORT_URI);
	}

	/**
	 * create an wind turbine component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code WindTurbineInboundPortURI != null}
	 * pre	{@code !WindTurbineInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param WindTurbineInboundPortURI URI of the wind turbine inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String windTurbineInboundPortURI) throws Exception {
		this(windTurbineInboundPortURI, 1, 0);
	}

	/**
	 * create an wind turbine component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code windTurbineInboundPortURI != null}
	 * pre	{@code !windTurbineInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param windTurbineInboundPortURI URI of the wind turbine inbound port.
	 * @param nbThreads                 number of standard threads.
	 * @param nbSchedulableThreads      number of schedulable threads.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String windTurbineInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(nbThreads, nbSchedulableThreads);
		this.initialise(windTurbineInboundPortURI);
	}

	/**
	 * create an wind turbine component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code windTurbineInboundPortURI != null}
	 * pre	{@code !windTurbineInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI  URI of the reflection innbound port of the
	 *                                  component.
	 * @param windTurbineInboundPortURI URI of the wind turbine inbound port.
	 * @param nbThreads                 number of standard threads.
	 * @param nbSchedulableThreads      number of schedulable threads.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String reflectionInboundPortURI, String windTurbineInboundPortURI, int nbThreads,
			int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		this.initialise(windTurbineInboundPortURI);
	}

	/**
	 * initialise an wind turbine component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code windTurbineInboundPortURI != null}
	 * pre	{@code !windTurbineInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param windTurbineInboundPortURI URI of the wind turbine inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected void initialise(String windTurbineInboundPortURI) throws Exception {
		assert windTurbineInboundPortURI != null;
		assert !windTurbineInboundPortURI.isEmpty();

		this.wtip = new MiniHydroelectricDamInboundPort(windTurbineInboundPortURI, this);
		this.wtip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Wind Turbin component");
			this.tracer.get().setRelativePosition(0, 1);
			this.toggleTracing();
		}
	}

	public boolean internalIsRunning() {
		return this.currentState == MiniHydroelectricDamState.ON;
	}

	@Override
	public void startMiniHydroelectricDam() throws Exception {
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("WindTurbine starts.\n");
		}
		assert !this.internalIsRunning();

		this.currentState = MiniHydroelectricDamState.ON;
	}

	@Override
	public void stopMiniHydroelectricDam() throws Exception {
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("Wind turbine stops.\n");
		}
		assert this.internalIsRunning();

		this.currentState = MiniHydroelectricDamState.OFF;

	}

	@Override
	public boolean isRunning() throws Exception {
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("Wind turbine returns its state: " + this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}

	@Override
	public double getCurrentWaterVolume() throws Exception {
		double currentSpeed = FAKE_CURRENT_WIND_SPEED;
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("Wind Turbine returns the current" + " wind speed " + currentSpeed + ".\n");
		}

		return currentSpeed;

	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.wtip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
