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
		/** Dame is on. */
		ON,
		/** Dame is off. */
		OFF
	}

	/** URI of the Dame in bound port used in tests. */
	public static final String Mini_Hydroelectric_Dam_INBOUND_PORT_URI = "MINI-HYDRO-ELECTRIC-DAM-INBOUND-PORT-URI";
	/** when true, methods trace their actions. */
	public static final boolean VERBOSE = true;

	/** in bound port offering the <code>MiniHydroelectricDamCI</code> interface. */
	protected MiniHydroelectricDamInboundPort mhdip;

	protected MiniHydroelectricDamState currentState;
	
	/** fake current 	*/
	public static final double		FAKE_CURRENT_WIND_SPEED = 10.0;
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an Dame component.
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
		this(Mini_Hydroelectric_Dam_INBOUND_PORT_URI);
		
	}

	/**
	 * create an Dame component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code MiniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !MiniHydroelectricDamInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param MiniHydroelectricDamInboundPortURI URI of the Dame inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String MiniHydroelectricDamInboundPortURI) throws Exception {
		this(MiniHydroelectricDamInboundPortURI, 1, 0);
	}

	/**
	 * create an Dame component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code MiniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !MiniHydroelectricDamInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param MiniHydroelectricDamInboundPortURI URI of the Dame inbound port.
	 * @param nbThreads                 number of standard threads.
	 * @param nbSchedulableThreads      number of schedulable threads.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String MiniHydroelectricDamInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(nbThreads, nbSchedulableThreads);
		this.initialise(MiniHydroelectricDamInboundPortURI);
	}

	/**
	 * create an Dame component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code MiniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !MiniHydroelectricDamInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI  URI of the reflection innbound port of the
	 *                                  component.
	 * @param MiniHydroelectricDamInboundPortURI URI of the Dame inbound port.
	 * @param nbThreads                 number of standard threads.
	 * @param nbSchedulableThreads      number of schedulable threads.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String reflectionInboundPortURI, String MiniHydroelectricDamInboundPortURI, int nbThreads,
			int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		this.initialise(MiniHydroelectricDamInboundPortURI);
	}

	/**
	 * initialise an Dame component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code MiniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !MiniHydroelectricDamInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param MiniHydroelectricDamInboundPortURI URI of the Dame inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected void initialise(String MiniHydroelectricDamInboundPortURI) throws Exception {
		assert MiniHydroelectricDamInboundPortURI != null;
		assert !MiniHydroelectricDamInboundPortURI.isEmpty();

		this.mhdip = new MiniHydroelectricDamInboundPort(MiniHydroelectricDamInboundPortURI, this);
		this.mhdip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Mini Hydro Electric Dame component");
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
			this.traceMessage("Mini Hydro Electric Dam starts.\n");
		}
		assert !this.internalIsRunning();

		this.currentState = MiniHydroelectricDamState.ON;
	}

	@Override
	public void stopMiniHydroelectricDam() throws Exception {
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("Mini Hydro Electric Dam stops.\n");
		}
		assert this.internalIsRunning();

		this.currentState = MiniHydroelectricDamState.OFF;

	}

	@Override
	public boolean isRunning() throws Exception {
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("Mini Hydro Electric Dam returns its state: " + this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}

	@Override
	public double getCurrentWaterVolume() throws Exception {
		double currentSpeed = FAKE_CURRENT_WIND_SPEED;
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("Mini Hydro Electric Dam returns the current" + " water volume " + currentSpeed + ".\n");
		}

		return currentSpeed;

	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.mhdip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
