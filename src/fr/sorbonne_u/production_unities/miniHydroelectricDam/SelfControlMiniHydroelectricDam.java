package fr.sorbonne_u.production_unities.miniHydroelectricDam;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamCoupledModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.*;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.sil.WaterVolumeSILModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.sil.MiniHydroelectricDamStateModel;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.CVM_SIL;
import fr.sorbonne_u.components.AbstractComponent;

//-----------------------------------------------------------------------------
/**
 * The class <code>SelfControlMiniHydroelectricDam</code> a MiniHydroelectricDam component
 * including a SIL simulation.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The MiniHydroelectricDam is an appliance that can be suspended, hence it will connect
 * with the household energy manager. This version of the component is made to
 * act as a cyber-physical component able to use SIL simulations to test the
 * code against models of the physical world.
 * </p>
 * <p>
 * When SIL simulated, some actions made by the code must be reflected in the
 * simulation models to keep the coherence in synchronisation between the code
 * and the simulation. The first way to do so is to make the code of the
 * component emit external events towards simulation models. The method
 * {@code triggerExternalEvent} of simulator plug-ins does so to send an event
 * to a model to be executed immediately (in the simulation time). It takes two
 * parameters: the URI of the target model and a lambda expression taking the
 * current simulated time in parameter to create an event instance occurring at
 * that time. See the methods in the component for example of its use.
 * </p>
 * <p>
 * This component does not implement the thermostatic control yet. To be used as
 * an actual appliance in the project, this control would need to be added.
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <p>
 * Created on : 2021-09-10
 * </p>
 * 
 * @author <a href="mailto:maedeh.daemi@etu.sorbonne-universite.fr">Maedeh
 *         Daemi</a>
 */
@OfferedInterfaces(offered = { MiniHydroelectricDamCI.class })
public class SelfControlMiniHydroelectricDam extends AbstractCyPhyComponent implements MiniHydroelectricDamImplementation {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>MiniHydroelectricDamState</code> describes the operation states
	 * of the MiniHydroelectricDam.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * Created on : 2021-09-10
	 * </p>
	 * 
	 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static enum MiniHydroelectricDamState {
		/** MiniHydroelectricDam is on. */
		ON,
		/** MiniHydroelectricDam is off. */
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the hair dryer reflection inbound port. */
	public static final String REFLECTION_INBOUND_PORT_URI = "miniHydroelectricDam-rip";
	/** URI of the hair dryer inbound port used in tests. */
	public static final String INBOUND_PORT_URI = "MINI-HYDRO-ELECTRIC-DAM-INBOUND-PORT-URI";
	/** when true, methods trace their actions. */
	public static final boolean VERBOSE = true;

	/** current state (on, off) of the MiniHydroelectricDam. */
	protected MiniHydroelectricDamState currentState;
	/** inbound port offering the <code>MiniHydroelectricDamCI</code> interface. */
	protected MiniHydroelectricDamInboundPort hip;

	// SIL simulation

	/**
	 * URI of the simulation architecture to be created or the empty string if the
	 * component does not execute as a SIL simulation.
	 */
	protected String simArchitectureURI;
	/**
	 * URI of the executor service used to execute the real time simulation.
	 */
	protected static final String SCHEDULED_EXECUTOR_SERVICE_URI = "ses";
	/** simulator plug-in that holds the SIL simulator for this component. */
	protected SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin simulatorPlugin;
	/** true if the component executes as a SIL simulation, false otherwise. */
	protected boolean isSILsimulated;
	/** true if the component executes as a unit test, false otherwise. */
	protected boolean composesAsUnitTest;
	/** true if the component executes as a unit test, false otherwise. */
	protected boolean executesAsUnitTest;
	/** acceleration factor used when executing as a unit test. */
	protected static final double ACC_FACTOR = 1.0;
	/** actual acceleration factor. */
	protected double accFactor;
	/** Maximum tolerated water volume. miles/kilometer */
	protected double Max_tolerated_water_volume;

	// Control

	protected boolean isWorking;
	protected static long PERIOD = 500;
	protected static TimeUnit CONTROL_TIME_UNIT = TimeUnit.MILLISECONDS;
	protected static double HYSTERESIS = 1.0;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new MiniHydroelectricDam.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param simArchitectureURI URI of the simulation architecture to be created or
	 *                           the empty string if the component does not execute
	 *                           as a SIL simulation.
	 * @param executesAsUnitTest true if the component executes as a unit test,
	 *                           false otherwise.
	 * @throws Exception <i>to do </i>.
	 */
	protected SelfControlMiniHydroelectricDam(String simArchitectureURI, boolean executesAsUnitTest

	) throws Exception {
		this(INBOUND_PORT_URI, simArchitectureURI, executesAsUnitTest);
	}

	/**
	 * create a new MiniHydroelectricDam.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code MiniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !MiniHydroelectricDamInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param MiniHydroelectricDamInboundPortURI URI of the inbound port to call the
	 *                                  MiniHydroelectricDam component.
	 * @param simArchitectureURI        URI of the simulation architecture to be
	 *                                  created or the empty string if the component
	 *                                  does not execute as a SIL simulation.
	 * @param executesAsUnitTest        true if the component executes as a unit
	 *                                  test, false otherwise.
	 * @throws Exception <i>to do </i>.
	 */
	protected SelfControlMiniHydroelectricDam(String MiniHydroelectricDamInboundPortURI, String simArchitectureURI,
			boolean executesAsUnitTest) throws Exception {
		// one standard thread pool used to execute the services and one
		// schedulable pool thread to execute the controller task
		super(REFLECTION_INBOUND_PORT_URI, 1, 1);
		this.initialise(MiniHydroelectricDamInboundPortURI, simArchitectureURI, executesAsUnitTest);
	}

	/**
	 * create a new MiniHydroelectricDam.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code MiniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !MiniHydroelectricDamInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI  URI of the reflection inbound port of the
	 *                                  component.
	 * @param MiniHydroelectricDamInboundPortURI URI of the inbound port to call the
	 *                                  MiniHydroelectricDam component.
	 * @param simArchitectureURI        URI of the simulation architecture to be
	 *                                  created or the empty string if the component
	 *                                  does not execute as a SIL simulation.
	 * @param executesAsUnitTest        true if the component executes as a unit
	 *                                  test, false otherwise.
	 * @throws Exception <i>to do </i>.
	 */
	protected SelfControlMiniHydroelectricDam(String reflectionInboundPortURI, String MiniHydroelectricDamInboundPortURI,
			String simArchitectureURI, boolean executesAsUnitTest) throws Exception {
		super(reflectionInboundPortURI, 1, 1);
		this.initialise(MiniHydroelectricDamInboundPortURI, simArchitectureURI, executesAsUnitTest);
	}

	/**
	 * create a new MiniHydroelectricDam.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code MiniHydroelectricDamInboundPortURI != null}
	 * pre	{@code !MiniHydroelectricDamInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param MiniHydroelectricDamInboundPortURI URI of the inbound port to call the
	 *                                  MiniHydroelectricDam component.
	 * @param simArchitectureURI        URI of the simulation architecture to be
	 *                                  created or the empty string if the component
	 *                                  does not execute as a SIL simulation.
	 * @param executesAsUnitTest        true if the component executes as a unit
	 *                                  test, false otherwise.
	 * @throws Exception <i>to do </i>.
	 */
	protected void initialise(String MiniHydroelectricDamInboundPortURI, String simArchitectureURI, boolean executesAsUnitTest)
			throws Exception {
		assert MiniHydroelectricDamInboundPortURI != null;
		assert !MiniHydroelectricDamInboundPortURI.isEmpty();
		assert simArchitectureURI != null;
		assert !simArchitectureURI.isEmpty() || !executesAsUnitTest;

		this.simArchitectureURI = simArchitectureURI;
		this.isSILsimulated = !simArchitectureURI.isEmpty();
		this.composesAsUnitTest = simArchitectureURI
				.equals(SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin.UNIT_TEST_SIM_ARCHITECTURE_URI);
		this.executesAsUnitTest = executesAsUnitTest;
		this.currentState = MiniHydroelectricDamState.OFF;
		this.accFactor = this.composesAsUnitTest ? ACC_FACTOR : CVM_SIL.ACC_FACTOR;
		this.isWorking = false;
		Max_tolerated_water_volume = 400.0;

		this.hip = new MiniHydroelectricDamInboundPort(MiniHydroelectricDamInboundPortURI, this);
		this.hip.publishPort();

		if (SelfControlMiniHydroelectricDam.VERBOSE) {
			this.tracer.get().setTitle("MiniHydroelectricDam component");
			this.tracer.get().setRelativePosition(0, 1);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();

		this.traceMessage("MiniHydroelectricDam starts.\n");

		if (this.isSILsimulated) {
			this.createNewExecutorService(SCHEDULED_EXECUTOR_SERVICE_URI, 1, true);
			this.simulatorPlugin = new SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin();
			this.simulatorPlugin.setPluginURI(MiniHydroelectricDamCoupledModel.URI);
			this.simulatorPlugin.setSimulationExecutorService(SCHEDULED_EXECUTOR_SERVICE_URI);
			try {
				this.simulatorPlugin.initialiseSimulationArchitecture(this.simArchitectureURI, this.accFactor);
				this.installPlugin(this.simulatorPlugin);
			} catch (Exception e) {
				throw new ComponentStartException(e);
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception {
		if (this.composesAsUnitTest && this.executesAsUnitTest) {
			this.simulatorPlugin.setSimulationRunParameters(new HashMap<String, Object>());
			long simStart = System.currentTimeMillis() + 1000L;
			this.simulatorPlugin.startRTSimulation(simStart, 0.0, 10.0);
			this.traceMessage("real time if start = " + simStart + "\n");
		}
		if (this.executesAsUnitTest) {
			// test scenario: code execution is scheduled to happen during
			// the simulation; SIL simulations execute in real time
			// (possibly accelerated) so that code execution can occur on
			// the same time reference in order to get coherent exchanges
			// between the two.
			this.scheduleTask(AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI, new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((SelfControlMiniHydroelectricDam) this.getTaskOwner()).startMiniHydroelectricDam();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, (long) (2.0 / this.accFactor), TimeUnit.SECONDS);
			this.scheduleTask(AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI, new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						double t = ((SelfControlMiniHydroelectricDam) this.getTaskOwner()).getCurrentWaterVolume();
						this.getTaskOwner().traceMessage("Current water volume: " + t + "\n");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, (long) (7.0 / this.accFactor), TimeUnit.SECONDS);
			this.scheduleTask(AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI, new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((SelfControlMiniHydroelectricDam) this.getTaskOwner()).stopMiniHydroelectricDam();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, (long) (8.0 / this.accFactor), TimeUnit.SECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		this.traceMessage("MiniHydroelectricDam stops.\n");
		this.currentState = MiniHydroelectricDamState.OFF;

		try {
			this.hip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the MiniHydroelectricDam is running.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return true if the MiniHydroelectricDam is running.
	 */
	public boolean internalIsRunning() {
		return this.currentState == MiniHydroelectricDamState.ON;
	}

	/**
	 * implement the controller task that will be executed to decide when to start
	 * or stop Dam.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param period period at which the control task is executed.
	 * @param u      time unit allowing to interpret the value of {@code period}.
	 */
	protected void internalController(long period, TimeUnit u) {
		// when the MiniHydroelectricDam is on, perform the control, but if the MiniHydroelectricDam is
		// switched off, stop the controller
		
			try {
				if (this.isWorking && this.getCurrentWaterVolume() > this.Max_tolerated_water_volume + HYSTERESIS) {
					if (SelfControlMiniHydroelectricDam.VERBOSE) {
						this.traceMessage("MiniHydroelectricDam decides to stop. Water volume is too high.\n");
					}
					this.stopMiniHydroelectricDam();
				} 
				else if (!this.isWorking && this.getCurrentWaterVolume() < this.Max_tolerated_water_volume + HYSTERESIS) {
					if (SelfControlMiniHydroelectricDam.VERBOSE) {
						this.traceMessage("MiniHydroelectricDam decides to start. Water volume is good.\n");
					}
					this.startMiniHydroelectricDam();
				} else {
					if (SelfControlMiniHydroelectricDam.VERBOSE) {
						this.traceMessage("MiniHydroelectricDam decides to do nothing.\n");
					}
				}
			} catch (Exception e) {
				;
			}
			this.scheduleTask(o -> ((SelfControlMiniHydroelectricDam) o).internalController(period, u), period, u);
		
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.production_unities.miniHydroelectricDam.MiniHydroelectricDamImplementation#isRunning()
	 */
	@Override
	public boolean isRunning() throws Exception {
		if (SelfControlMiniHydroelectricDam.VERBOSE) {
			this.traceMessage("MiniHydroelectricDam returns its state: " + this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}

	/**
	 * @see fr.sorbonne_u.production_unities.MiniHydroelectricDam.MiniHydroelectricDamImplementation#startMiniHydroelectricDam()
	 */
	@Override
	public void startMiniHydroelectricDam() throws Exception {
		if (SelfControlMiniHydroelectricDam.VERBOSE) {
			this.traceMessage("MiniHydroelectricDam starts.\n");
		}
		assert !this.internalIsRunning();

		this.isWorking = true;
		this.currentState = MiniHydroelectricDamState.ON;
		
		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(MiniHydroelectricDamStateModel.URI, t -> new StartMiniHydroelectricDam(t));
		}

		// when starting the MiniHydroelectricDam, its internal controller is also started
		// to execute at the predefined period to check the current water volume
		// and decide when to start or stop Dam
		long accPeriod = (long) (PERIOD / this.accFactor);
		this.scheduleTask(o -> ((SelfControlMiniHydroelectricDam) o).internalController(accPeriod, CONTROL_TIME_UNIT), accPeriod,
				CONTROL_TIME_UNIT);
	}

	/**
	 * @see fr.sorbonne_u.production_unities.MiniHydroelectricDam.MiniHydroelectricDamImplementation#stopMiniHydroelectricDam()
	 */
	@Override
	public void stopMiniHydroelectricDam() throws Exception {
		if (SelfControlMiniHydroelectricDam.VERBOSE) {
			this.traceMessage("MiniHydroelectricDam stops.\n");
		}
		assert this.internalIsRunning();

		this.isWorking = false;
		this.currentState = MiniHydroelectricDamState.OFF;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(MiniHydroelectricDamStateModel.URI, t -> new StopMiniHydroelectricDam(t));
		}
	}

	/**
	 * @see fr.sorbonne_u.production_unities.MiniHydroelectricDam.MiniHydroelectricDamImplementation#getCurrentWaterVolume()
	 */
	@Override
	public double getCurrentWaterVolume() throws Exception {
		double currentWaterVolume = 0.0;
		if (this.isSILsimulated) {
			currentWaterVolume = (double) this.simulatorPlugin.getModelStateValue(WaterVolumeSILModel.URI,
					SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin.CURRENT_WATER_VOLUME);
		} else {
			// Temporary implementation; would need a water volume sensor.
		}
		if (SelfControlMiniHydroelectricDam.VERBOSE) {
			StringBuffer message = new StringBuffer("MiniHydroelectricDam returns the current water volume ");
			message.append(currentWaterVolume);
			message.append(".\n");
			this.traceMessage(message.toString());
		}
		return currentWaterVolume;
	}
}
//-----------------------------------------------------------------------------
