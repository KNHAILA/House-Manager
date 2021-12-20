package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { BatteryCI.class })
public class Battery extends AbstractComponent implements BatteryImplementation {


	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected static enum BatteryState {
		/** battery is on. */
		ON,
		/** battery is off. */
		OFF
	}

	/** URI of the electric meter in bound port used in tests. */
	public static final String Battery_INBOUND_PORT_URI = "Battery";
	/** when true, methods trace their actions. */
	public static final boolean VERBOSE = true;

	/** in bound port offering the <code>BatteryCI</code> interface. */
	protected BatteryInboundPort bip;
	protected BatteryState currentState;
	protected double percentage;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an electric meter component.
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
	protected Battery() throws Exception {
		this(Battery_INBOUND_PORT_URI);
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code BatteryInboundPortURI != null}
	 * pre	{@code !BatteryInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param BatteryInboundPortURI URI of the electric meter inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected Battery(String batteryInboundPortURI) throws Exception {
		this(batteryInboundPortURI, 1, 0);
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code batteryInboundPortURI != null}
	 * pre	{@code !batteryInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param batteryInboundPortURI URI of the electric meter inbound port.
	 * @param nbThreads                 number of standard threads.
	 * @param nbSchedulableThreads      number of schedulable threads.
	 * @throws Exception <i>to do</i>.
	 */
	protected Battery(String batteryInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(nbThreads, nbSchedulableThreads);
		this.initialise(batteryInboundPortURI);
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code batteryInboundPortURI != null}
	 * pre	{@code !batteryInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI  URI of the reflection innbound port of the
	 *                                  component.
	 * @param batteryInboundPortURI URI of the electric meter inbound port.
	 * @param nbThreads                 number of standard threads.
	 * @param nbSchedulableThreads      number of schedulable threads.
	 * @throws Exception <i>to do</i>.
	 */
	protected Battery(String reflectionInboundPortURI, String batteryInboundPortURI, int nbThreads,
			int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		this.initialise(batteryInboundPortURI);
	}

	/**
	 * initialise an electric meter component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code batteryInboundPortURI != null}
	 * pre	{@code !batteryInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param batteryInboundPortURI URI of the electric meter inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected void initialise(String batteryInboundPortURI) throws Exception {
		assert batteryInboundPortURI != null;
		assert !batteryInboundPortURI.isEmpty();

		this.currentState = BatteryState.OFF;
		this.percentage = 100.00;
		
		this.bip = new BatteryInboundPort(batteryInboundPortURI, this);
		this.bip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Battery component");
			this.tracer.get().setRelativePosition(0, 0);
			this.toggleTracing();
		}
	}

	public boolean internalIsRunning() {
		return this.currentState == BatteryState.ON;
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
			this.bip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------
	
	
	@Override
	public void activeBattery() throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("Battery starts.\n");
		}
		assert	!this.internalIsRunning();

		this.currentState = BatteryState.ON;
		
	}

	@Override
	public void desactiveBattery() throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("Battery stops.\n");
		}
		assert	this.internalIsRunning();

		this.currentState = BatteryState.OFF;
		
	}

	@Override
	public boolean isUsing() throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("Battery returns its state: " +
											this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}

	@Override
	public double remainingChargePercentage() throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("Battery returns its percentage"
							+ "Percentage " + this.percentage + ".\n");
		}
		return this.percentage;
	}

	@Override
	public void chargeBattery() throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("charge Battery.\n");
		}

		this.percentage = 100.00;
	}

	@Override
	public void dechargeBattery() throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("decharge Battery.\n");
		}

		this.percentage = 0;
	}
}
