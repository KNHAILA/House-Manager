package fr.sorbonne_u.components.washingMachine;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.washingMachine.WashingMachineCI;
import fr.sorbonne_u.components.washingMachine.WashingMachineImplementationI;
import fr.sorbonne_u.components.washingMachine.WashingMachineInboundPort;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineCoupledModel;
import fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.HeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.SwitchOffWashingMachine;
import fr.sorbonne_u.components.washingMachine.mil.events.SwitchOnWashingMachine;
import fr.sorbonne_u.CVM_SIL;
import fr.sorbonne_u.components.washingMachine.sil.WashingMachineStateModel;
import fr.sorbonne_u.components.washingMachine.sil.WashingMachineTemperatureSILModel;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>ThermostatedHeater</code> a thermostated heater component
 * including a SIL simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The thermostated heater is an appliance that can be suspended, hence it will
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
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered={WashingMachineCI.class})
public class			ThermostatedWashingMachine
extends		AbstractCyPhyComponent
implements	WashingMachineImplementationI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>HeaterState</code> describes the operation
	 * states of the heater.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static enum	WashingMachineState
	{
		/** heater is on.													*/
		ON,
		/** heater is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the hair dryer reflection inbound port.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
												"WASHING-MACHINE-rip";
	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String		INBOUND_PORT_URI =
												"WASHING-MACHINE-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;

	/** current state (on, off) of the heater.								*/
	protected WashingMachineState			currentState;
	/** inbound port offering the <code>HeaterCI</code> interface.			*/
	protected WashingMachineInboundPort		hip;
	/** target temperature for the heating.	*/
	protected int				targetTemperature;

	// SIL simulation

	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected String				simArchitectureURI;
	/** URI of the executor service used to execute the real time
	 *  simulation.															*/
	protected static final String	SCHEDULED_EXECUTOR_SERVICE_URI = "ses";
	/** simulator plug-in that holds the SIL simulator for this component.	*/
	protected ThermostatedWashingMachineRTAtomicSimulatorPlugin	simulatorPlugin;
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

	// Control

	protected boolean				isHeating;
	protected static long			PERIOD = 500;
	protected static TimeUnit		CONTROL_TIME_UNIT = TimeUnit.MILLISECONDS;
	protected static double			HYSTERESIS = 1.0;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new thermostated heater.
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
	protected			ThermostatedWashingMachine(
		String simArchitectureURI,
		boolean executesAsUnitTest
		
		) throws Exception
	{
		this(INBOUND_PORT_URI, simArchitectureURI, executesAsUnitTest);
	}

	/**
	 * create a new thermostated heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterInboundPortURI != null}
	 * pre	{@code !heaterInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param heaterInboundPortURI	URI of the inbound port to call the heater component.
	 * @param simArchitectureURI	URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest	true if the component executes as a unit test, false otherwise.
	 * @throws Exception			<i>to do </i>.
	 */
	protected			ThermostatedWashingMachine(
		String heaterInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		// one standard thread pool used to execute the services and one
		// schedulable pool thread to execute the controller task
		super(REFLECTION_INBOUND_PORT_URI, 1, 1);
		this.initialise(heaterInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create a new thermostated heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code heaterInboundPortURI != null}
	 * pre	{@code !heaterInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param heaterInboundPortURI		URI of the inbound port to call the heater component.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest		true if the component executes as a unit test, false otherwise.
	 * @throws Exception				<i>to do </i>.
	 */
	protected			ThermostatedWashingMachine(
		String reflectionInboundPortURI,
		String heaterInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.initialise(heaterInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create a new thermostated heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterInboundPortURI != null}
	 * pre	{@code !heaterInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param heaterInboundPortURI	URI of the inbound port to call the heater component.
	 * @param simArchitectureURI	URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest	true if the component executes as a unit test, false otherwise.
	 * @throws Exception			<i>to do </i>.
	 */
	protected void		initialise(
		String heaterInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		assert	heaterInboundPortURI != null;
		assert	!heaterInboundPortURI.isEmpty();
		assert	simArchitectureURI != null;
		assert	!simArchitectureURI.isEmpty() || !executesAsUnitTest;

		this.simArchitectureURI = simArchitectureURI;
		this.isSILsimulated = !simArchitectureURI.isEmpty();
		this.composesAsUnitTest =
				simArchitectureURI.equals(
						ThermostatedWashingMachineRTAtomicSimulatorPlugin.
												UNIT_TEST_SIM_ARCHITECTURE_URI);
		this.executesAsUnitTest = executesAsUnitTest;
		this.currentState = WashingMachineState.OFF;
		this.targetTemperature = 20;
		this.accFactor = this.composesAsUnitTest ?
							ACC_FACTOR
						 :	CVM_SIL.ACC_FACTOR;
		this.isHeating = false;

		this.hip = new WashingMachineInboundPort(heaterInboundPortURI, this);
		this.hip.publishPort();

		if (ThermostatedWashingMachine.VERBOSE) {
			this.tracer.get().setTitle("Thermostated washing machine component");
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

		this.traceMessage("Washing machine starts.\n");

		if (this.isSILsimulated) {
			this.createNewExecutorService(
								SCHEDULED_EXECUTOR_SERVICE_URI, 1, true);
			this.simulatorPlugin =
							new ThermostatedWashingMachineRTAtomicSimulatorPlugin();
			this.simulatorPlugin.setPluginURI(WashingMachineCoupledModel.URI);
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
								((ThermostatedWashingMachine)this.getTaskOwner()).
															startWashingMachine();
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
								double t = ((ThermostatedWashingMachine)
												this.getTaskOwner()).
													getCurrentTemperature();
								this.getTaskOwner().traceMessage(
											"Current room temperature: " +
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
								((ThermostatedWashingMachine)this.getTaskOwner()).
															stopWashingMachine();
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
		this.traceMessage("Washing machine stops.\n");
		this.currentState = WashingMachineState.OFF;

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
	 * return true if the heater is running.
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
		return this.currentState == WashingMachineState.ON;
	}

	/**
	 * make the thermostated heater start heating; this internal method is
	 * meant to be executed by the heater thermostat when the room temperature
	 * is below the target temperature.
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
	protected void		heat() throws Exception
	{
		assert	this.internalIsRunning();

		this.isHeating = true;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(
												WashingMachineStateModel.URI,
												t -> new HeatWater(t));
		}
	}

	/**
	 * make the thermostated heater stop heating; this internal method is
	 * meant to be executed by the heater thermostat when the room temperature
	 * comes over the target temperature after a period of heating.
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
	protected void		doNotHeat() throws Exception
	{
		assert	this.internalIsRunning();

		this.isHeating = false;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(
												WashingMachineStateModel.URI,
												t -> new DoNotHeatWater(t));
		}
	}

	/**
	 * implement the controller task that will be executed to decide when to
	 * start or stop heating.
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
		// when the heater is on, perform the control, but if the heater is
		// switched off, stop the controller
		if (this.currentState == ThermostatedWashingMachine.WashingMachineState.ON) {
			try {
				if (this.isHeating &&
								this.getCurrentTemperature() >
										this.targetTemperature + HYSTERESIS) {
					if (ThermostatedWashingMachine.VERBOSE) {
						this.traceMessage(
								"Thermostated washing machine decides to heat.\n");
					}
					this.doNotHeat();
				} else if (!this.isHeating &&
								this.getCurrentTemperature() <
										this.targetTemperature - HYSTERESIS) {
					this.heat();
					if (ThermostatedWashingMachine.VERBOSE) {
						this.traceMessage(
								"Thermostated washing machine decides to heat.\n");
					}
				} else {
					if (ThermostatedWashingMachine.VERBOSE) {
						this.traceMessage(
								"Thermostated washine machine decides to do nothing.\n");
					}					
				}
			} catch (Exception e) {
				;
			}
			this.scheduleTask(
					o -> ((ThermostatedWashingMachine)o).internalController(period, u),
					period, u);
		}
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.heater.HeaterImplementationI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		if (ThermostatedWashingMachine.VERBOSE) {
			this.traceMessage("Thermostated washine machine returns its state: " +
											this.currentState + ".\n");
		}
		return this.internalIsRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.heater.HeaterImplementationI#startHeater()
	 */
	@Override
	public void			startWashingMachine() throws Exception
	{
		if (ThermostatedWashingMachine.VERBOSE) {
			this.traceMessage("Thermostated washine machine starts.\n");
		}
		assert	!this.internalIsRunning();

		this.currentState = WashingMachineState.ON;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(
												WashingMachineStateModel.URI,
												t -> new SwitchOnWashingMachine(t));
		}

		// when starting the heater, its internal controller is also started
		// to execute at the predefined period to check the current temperature
		// and decide when to start or stop heating 
		long accPeriod = (long)(PERIOD/this.accFactor);
		this.scheduleTask(
				o -> ((ThermostatedWashingMachine)o).
							internalController(accPeriod, CONTROL_TIME_UNIT),
				accPeriod, CONTROL_TIME_UNIT);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.heater.HeaterImplementationI#stopHeater()
	 */
	@Override
	public void			stopWashingMachine() throws Exception
	{
		if (ThermostatedWashingMachine.VERBOSE) {
			this.traceMessage("Thermostated washine machine stops.\n");
		}
		assert	this.internalIsRunning();

		this.currentState = WashingMachineState.OFF;

		if (this.isSILsimulated) {
			this.simulatorPlugin.triggerExternalEvent(
												WashingMachineStateModel.URI,
												t -> new SwitchOffWashingMachine(t));
		}
	}


	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.heater.HeaterImplementationI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(int target) throws Exception
	{
		if (ThermostatedWashingMachine.VERBOSE) {
			this.traceMessage("Thermostated washine machine sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	this.internalIsRunning();
		assert	target >= -50.0 && target <= 50.0;

		this.targetTemperature = target;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.heater.HeaterImplementationI#getTargetTemperature()
	 */
	@Override
	public int		getTargetTemperature() throws Exception
	{
		if (ThermostatedWashingMachine.VERBOSE) {
			this.traceMessage("Thermostated washine machine returns its target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		return this.targetTemperature;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.heater.HeaterImplementationI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		double currentTemperature =  0.0;
		if (this.isSILsimulated) {
			currentTemperature =
				(double) this.simulatorPlugin.getModelStateValue(
								WashingMachineTemperatureSILModel.URI,
								ThermostatedWashingMachineRTAtomicSimulatorPlugin.
													CURRENT_ROOM_TERMPERATURE);
		} else {
			// Temporary implementation; would need a temperature sensor.
		}
		if (ThermostatedWashingMachine.VERBOSE) {
			StringBuffer message =
					new StringBuffer(
						"Thermostated washine machine returns the current temperature ");
			message.append(currentTemperature);
			message.append(".\n");
			this.traceMessage(message.toString());
		}
		return currentTemperature;
	}
	
	@Override
	public void setSpinningNumber(int target) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSpinningNumber() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentSpinningNumber() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDuration(Duration duration) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Duration getDuration() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Duration getCurrentDuration() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMode(Program mode) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Program getMode() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
// -----------------------------------------------------------------------------
