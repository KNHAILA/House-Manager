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
		/** Mini Hydroelectric Dam is on. */
		ON,
		/** Mini Hydroelectric Dam is off. */
		OFF
	}

	/** URI of the mini hydroelectricn dam in bound port used in tests. */
	public static final String Mini_Hydroelectric_Dam_INBOUND_PORT_URI = "Wind_Turbine";
	/** when true, methods trace their actions. */
	public static final boolean VERBOSE = true;

	/** in bound port offering the <code>MiniHydroelectricDamCI</code> interface. */
	protected MiniHydroelectricDamInboundPort mhdip;

	protected MiniHydroelectricDamState currentState;
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an Mini Hydroelectric Dam component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code Mini_Hydroelectric_Dam__INBOUND_PORT_URI != null}
	 * pre	{@code !Mini_Hydroelectric_Dam__PORT_URI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam() throws Exception {
		this(Mini_Hydroelectric_Dam_INBOUND_PORT_URI);
	}

	/**
	 * create an Mini Hydroelectric Dam component.
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
	 * @param MiniHydroelectricDamInboundPortURI URI of the mini hydroelectricn dam inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String miniHydroelectricDamInboundPortURI) throws Exception {
		this(miniHydroelectricDamInboundPortURI, 1, 0);
	}

	/**
	 * create an mini hydroelectricn dam component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code miniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !miniHydroelectricDamInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param miniHydroelectricDamInboundPortURI URI of the mini hydroelectricn dam inbound port.
	 * @param nbThreads                 number of standard threads.
	 * @param nbSchedulableThreads      number of schedulable threads.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String miniHydroelectricDamInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(nbThreads, nbSchedulableThreads);
		this.initialise(miniHydroelectricDamInboundPortURI);
	}

	/**
	 * create an mini hydroelectricn dam component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code miniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !miniHydroelectricDamInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI  URI of the reflection innbound port of the
	 *                                  component.
	 * @param miniHydroelectricDamInboundPortURI URI of the mini hydroelectricn dam inbound port.
	 * @param nbThreads                 number of standard threads.
	 * @param nbSchedulableThreads      number of schedulable threads.
	 * @throws Exception <i>to do</i>.
	 */
	protected MiniHydroelectricDam(String reflectionInboundPortURI, String miniHydroelectricDamInboundPortURI, int nbThreads,
			int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		this.initialise(miniHydroelectricDamInboundPortURI);
	}

	/**
	 * initialise an mini hydroelectricn dam component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code miniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !miniHydroelectricDamInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param miniHydroelectricDamInboundPortURI URI of the mini hydroelectricn dam inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected void initialise(String miniHydroelectricDamInboundPortURI) throws Exception {
		assert miniHydroelectricDamInboundPortURI != null;
		assert !miniHydroelectricDamInboundPortURI.isEmpty();

		this.mhdip = new MiniHydroelectricDamInboundPort(miniHydroelectricDamInboundPortURI, this);
		this.mhdip.publishPort();

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
	public void	startMiniHydroelectricDam() throws Exception {
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("MiniHydroelectricDam starts.\n");
		}
		assert	!this.internalIsRunning();

		this.currentState = MiniHydroelectricDamState.ON;
	}

	@Override
	public void stopMiniHydroelectricDam() throws Exception {
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("mini hydroelectricn dam stops.\n");
		}
		assert	this.internalIsRunning();

		this.currentState = MiniHydroelectricDamState.OFF;

	}

	@Override
	public boolean isRunning() throws Exception {
		if (MiniHydroelectricDam.VERBOSE) {
			this.traceMessage("mini hydroelectricn dam returns its state: " +
											this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}
}



