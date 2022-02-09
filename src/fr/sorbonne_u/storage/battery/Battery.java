package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.storage.battery.mil.BatteryCoupledModel;
import fr.sorbonne_u.storage.battery.mil.events.*;
import fr.sorbonne_u.CVM_SIL;
import fr.sorbonne_u.storage.battery.sil.BatteryChargePercentageSILModel;
import fr.sorbonne_u.storage.battery.sil.BatteryStateModel;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>Battery</code> a Battery component
 * including a SIL simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The Battery is an appliance that can be suspended, hence it will
 * connect with the household energy manager. This version of the component is
 * made to act as a cyber-physical component able to use SIL simulations to test
 * the code against models of the physical world.
 * </p>
 * <p>
 * When SIL simulated, some actions made by the code must be reflected in the
 * simulation models to keep the coherence in synchronisation between the
 * code and the simulation. The first way to do so is to make the code of
 * the component emit external events towards simulation models. The method
 * {@code triggerExternalEvent} of simulator plug-ins does so to send an
 * event to a model to be executed immediately (in the simulation time).
 * It takes two parameters: the URI of the target model and a lambda
 * expression taking the current simulated time in parameter to create
 * an event instance occurring at that time. See the methods in the component
 * for example of its use.
 * </p>
 * <p>
 * This component does not implement the thermostatic control yet. To be used
 * as an actual appliance in the project, this control would need to be added.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code targetTemperature >= -50.0 && targetTemperature <= 50.0}
 * </pre>
 * 
 * <p>Created on : 2021-09-10</p>
 * 
 *  @authors	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 *              <a href="maedeh.daemi@etu.sorbonne-universite.fr">DAEMI Maedeh</a>
 */
@OfferedInterfaces(offered={BatteryCI.class})
public class			Battery
extends		AbstractCyPhyComponent
implements	BatteryImplementation
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>BatteryState</code> describes the operation
	 * states of the Battery.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 *  @authors	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 *              <a href="maedeh.daemi@etu.sorbonne-universite.fr">DAEMI Maedeh</a>
	 */
	protected static enum	BatteryState
	{
		/** Battery is USE.													*/
		USE,
		/** Battery is REST.													*/
		REST
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the hair dryer reflection inbound port.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
												"Battery-rip";
	/** URI of the hair dryer inbound port used in tests.	*/
	public static final String		INBOUND_PORT_URI =
												"Battery-INBOUND-PORT-URI";
	
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;

	/** current state (on, off) of the Battery.								*/
	protected BatteryState			currentState;
	/** inbound port offering the <code>BatteryCI</code> interface.			*/
	protected BatteryInboundPort		hip;

	// SIL simulation

	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected String				simArchitectureURI;
	/** URI of the executor service used to execute the real time
	 *  simulation.															*/
	protected static final String	SCHEDULED_EXECUTOR_SERVICE_URI = "ses";
	/** simulator plug-in that holds the SIL simulator for this component.	*/
	protected BatteryRTAtomicSimulatorPlugin	simulatorPlugin;
	/** true if the component executes as a SIL simulation, false otherwise.*/
	protected boolean				isSILsimulated;
	/** true if the component executes as a unit test, false otherwise.		*/
	protected boolean				composesAsUnitTest;
	/** true if the component executes as a unit test, false otherwise.		*/
	protected boolean				executesAsUnitTest;
	/** acceleration factor used when executing as a unit test.				*/
	protected static final double	ACC_FACTOR = 1.0;
	/** actual acceleration factor.											*/
	protected double				accFactor;
	/** least acceptable percentage for the battery charge.	*/
	protected double				least_percentage = 60.0;

	// Control

	protected boolean				isCharging;
	protected static long			PERIOD = 500;
	protected static TimeUnit		CONTROL_TIME_UNIT = TimeUnit.MILLISECONDS;
	protected static double			HYSTERESIS = 1.0;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new Battery.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest	true if the component executes as a unit test, false otherwise.
	 * @throws Exception			<i>to do </i>.
	 */
	protected	Battery(
			String simArchitectureURI,
			boolean executesAsUnitTest
			
			) throws Exception
		{
			this(INBOUND_PORT_URI, simArchitectureURI, executesAsUnitTest);
		}

	/**
	 * create a new Battery.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code BatteryInboundPortURI != null}
	 * pre	{@code !BatteryInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param BatteryInboundPortURI	URI of the inbound port to call the Battery component.
	 * @param simArchitectureURI	URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest	true if the component executes as a unit test, false otherwise.
	 * @throws Exception			<i>to do </i>.
	 */
	protected			Battery(
		String batteryInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		// one standard thread pool used to execute the services and one
		// schedulable pool thread to execute the controller task
		super(REFLECTION_INBOUND_PORT_URI, 1, 1);
		this.initialise(batteryInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create a new battery.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code batteryInboundPortURI != null}
	 * pre	{@code !batteryInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param batteryInboundPortURI		URI of the inbound port to call the battery component.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest		true if the component executes as a unit test, false otherwise.
	 * @throws Exception				<i>to do </i>.
	 */
	protected			Battery(
		String reflectionInboundPortURI,
		String batteryInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.initialise(batteryInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create a new battery.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code batteryInboundPortURI != null}
	 * pre	{@code !batteryInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param batteryInboundPortURI	URI of the inbound port to call the battery component.
	 * @param simArchitectureURI	URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest	true if the component executes as a unit test, false otherwise.
	 * @throws Exception			<i>to do </i>.
	 */
	protected void		initialise(
		String batteryInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		assert	batteryInboundPortURI != null;
		assert	!batteryInboundPortURI.isEmpty();
		assert	simArchitectureURI != null;
		assert	!simArchitectureURI.isEmpty() || !executesAsUnitTest;

		this.simArchitectureURI = simArchitectureURI;
		this.isSILsimulated = !simArchitectureURI.isEmpty();
		this.composesAsUnitTest =
				simArchitectureURI.equals(
						BatteryRTAtomicSimulatorPlugin.
												UNIT_TEST_SIM_ARCHITECTURE_URI);
		this.executesAsUnitTest = executesAsUnitTest;
		this.currentState = BatteryState.USE;
		this.accFactor = this.composesAsUnitTest ?
							ACC_FACTOR
						 :	CVM_SIL.ACC_FACTOR;
		this.isCharging = false;

		this.hip = new BatteryInboundPort(batteryInboundPortURI, this);
		this.hip.publishPort();

		if (Battery.VERBOSE) {
			this.tracer.get().setTitle("battery component");
			this.tracer.get().setRelativePosition(2, 1);
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
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		this.traceMessage("Battery starts.\n");

		if (this.isSILsimulated) {
			this.createNewExecutorService(
								SCHEDULED_EXECUTOR_SERVICE_URI, 1, true);
			this.simulatorPlugin =
							new BatteryRTAtomicSimulatorPlugin();
			this.simulatorPlugin.setPluginURI(BatteryCoupledModel.URI);
			this.simulatorPlugin.setSimulationExecutorService(
											SCHEDULED_EXECUTOR_SERVICE_URI);
			try {
				this.simulatorPlugin.initialiseSimulationArchitecture(
													this.simArchitectureURI,
													this.accFactor);
				this.installPlugin(this.simulatorPlugin);
			} catch (Exception e) {
				throw new ComponentStartException(e) ;
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		if (this.composesAsUnitTest && this.executesAsUnitTest) {
			this.simulatorPlugin.setSimulationRunParameters(
												new HashMap<String, Object>());
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
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((Battery)this.getTaskOwner()).
															startBattery();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(2.0/this.accFactor),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								double t = ((Battery)
												this.getTaskOwner()).
													getCurrentPercentage();
								this.getTaskOwner().traceMessage(
											"Current battery percentage: " +
																	t + "\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(7.0/this.accFactor),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((Battery)this.getTaskOwner()).
															stopBattery();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(8.0/this.accFactor),
					TimeUnit.SECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		this.traceMessage("Using battery is no more allowed.\n");
		this.currentState = BatteryState.REST;

		try {
			this.hip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the battery is using.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return	true if the battery is running.
	 */
	public boolean		internalIsRunning()
	{
		return this.currentState == BatteryState.USE;
	}

	/**
	 * make battery to be charged; this internal method is
	 * meant to be executed by the Battery when the battery percentage
	 * is below the least acceptable percentage.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code internalIsRunning()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		charge() throws Exception
	{
		assert	this.internalIsRunning();

		this.isCharging = true;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(
												BatteryStateModel.URI,
												t -> new ChargeBattery(t));
		}
	}

	/**
	 * Battery not getting charge anymore; this internal method is
	 * meant to be executed by the Battery when the battery charge is full
	 * after a period of charging.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code internalIsRunning()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		doNotCharge() throws Exception
	{
		assert	this.internalIsRunning();

		this.isCharging = false;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(
												BatteryStateModel.URI,
												t -> new DoNotChargeBattery(t));
		}
	}

	/**
	 * implement the controller task that will be executed to decide when to
	 * start or stop charging.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param period	period at which the control task is executed.
	 * @param u			time unit allowing to interpret the value of {@code period}.
	 */
	protected void		internalController(long period, TimeUnit u)
	{
		// when the Battery is on, perform the control, but if the Battery is
		// switched off, stop the controller
		if (this.currentState == Battery.BatteryState.USE) {
			try {
				if (this.isCharging &&
								this.getCurrentPercentage() == 100.0) {
					if (Battery.VERBOSE) {
						this.traceMessage(
								"battery has decided to not be charged anymore.\n");
					}
					this.doNotCharge();
				} else if (!this.isCharging &&
								this.getCurrentPercentage() < least_percentage) {
					this.charge();
					if (Battery.VERBOSE) {
						this.traceMessage(
								"battery has decided to be charged.\n");
					}
				} else {
					if (Battery.VERBOSE) {
						this.traceMessage(
								"battery decides to do nothing.\n");
					}	
				}
			} catch (Exception e) {
				;
			}
			this.scheduleTask(
					o -> ((Battery)o).internalController(period, u),
					period, u);
		}
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryImplementationI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		if (Battery.VERBOSE) {
			this.traceMessage("battery returns its state: " +
											this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryImplementationI#startBattery()
	 */
	@Override
	public void			startBattery() throws Exception
	{
		if (Battery.VERBOSE) {
			this.traceMessage("battery starts.\n");
		}
		assert	!this.internalIsRunning();

		this.currentState = BatteryState.USE;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(
												BatteryStateModel.URI,
												t -> new UseBattery(t));
		}

		
		// when starting the battery, its internal controller is also started
		// to execute at the predefined period to check the current temperature
		// and decide when to start or stop heating 
		long accPeriod = (long)(PERIOD/this.accFactor);
		this.scheduleTask(
				o -> ((Battery)o).
							internalController(accPeriod, CONTROL_TIME_UNIT),
				accPeriod, CONTROL_TIME_UNIT);
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryImplementationI#stopBattery()
	 */
	@Override
	public void			stopBattery() throws Exception
	{
		if (Battery.VERBOSE) {
			this.traceMessage("battery stops.\n");
		}
		assert	this.internalIsRunning();

		this.currentState = BatteryState.REST;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(
												BatteryStateModel.URI,
												t -> new DoNotUseBattery(t));
		}
	}

	/**
	 * @see fr.sorbonne_u.storage.battery.BatteryImplementationI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentPercentage() throws Exception
	{
		double currentPercentage =  0.0;
		if (this.isSILsimulated) {
			currentPercentage =
				(double) this.simulatorPlugin.getModelStateValue(
								BatteryChargePercentageSILModel.URI,
								BatteryRTAtomicSimulatorPlugin.
									CURRENT_BATTERY_PERCENTAGE);
		} else {
			// Temporary implementation; would need a temperature sensor.
		}
		if (Battery.VERBOSE) {
			StringBuffer message =
					new StringBuffer(
						"battery returns the current percentage ");
			message.append(currentPercentage);
			message.append(".\n");
			this.traceMessage(message.toString());
		}
		return currentPercentage;
	}
}
// -----------------------------------------------------------------------------
