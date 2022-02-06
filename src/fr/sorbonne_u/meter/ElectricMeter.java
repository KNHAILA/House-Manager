package fr.sorbonne_u.meter;

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
import fr.sorbonne_u.meter.ElectricMeterCI;
import fr.sorbonne_u.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.meter.ElectricMeterInboundPort;

import fr.sorbonne_u.components.fan.mil.events.*;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.*;
import fr.sorbonne_u.components.washingMachine.mil.events.*;
import fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.HeatWater;
import fr.sorbonne_u.components.refrigerator.mil.events.*;
import fr.sorbonne_u.components.waterHeater.mil.events.*;
import fr.sorbonne_u.production_unities.windTurbine.mil.events.*;

import fr.sorbonne_u.CVM_SIL;
import fr.sorbonne_u.meter.sil.ElectricMeterCoupledModel;
import fr.sorbonne_u.meter.sil.ElectricMeterElectricitySILModel;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeter</code> implements a simplified electric meter
 * component using a SIL simulation for testing.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-09-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered={ElectricMeterCI.class})
public class			ElectricMeter
extends		AbstractCyPhyComponent
implements	ElectricMeterImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the electric meter reflection inbound port.					*/
	public static final String	REFLECTION_INBOUND_PORT_URI =
														"ELECTRIC-METER-rip";
	/** URI of the electric meter inbound port used in tests.				*/
	public static final String	ELECTRIC_METER_INBOUND_PORT_URI =
															"ELECTRIC-METER";
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;

	/** inbound port offering the <code>ElectricMeterCI</code> interface.	*/
	protected ElectricMeterInboundPort	emip;

	// SIL simulation

	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected String				simArchitectureURI;
	/** URI of the executor service used to execute the real time
	 *  simulation.															*/
	protected static final String	SCHEDULED_EXECUTOR_SERVICE_URI = "ses";
	/** simulator plug-in that holds the SIL simulator for this component.	*/
	protected ElectricMeterRTAtomicSimulatorPlugin	simulatorPlugin;
	/** true if the component executes as a SIL simulation, false otherwise.*/
	protected boolean				isSILsimulated;
	/** true if the component executes as a unit test, false otherwise.		*/
	protected boolean				executesAsUnitTest;
	/** acceleration factor used when executing as a unit test.				*/
	protected static final double	ACC_FACTOR = 1.0;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ELECTRIC_METER_INBOUND_PORT_URI != null}
	 * pre	{@code !ELECTRIC_METER_INBOUND_PORT_URI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 * 
	 * @param simArchitectureURI	URI of the simulation architecture to be created or the empty string if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest	true if the component executes as a unit test, false otherwise.
	 * @throws Exception			<i>to do</i>.
	 */
	protected			ElectricMeter(
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		this(ELECTRIC_METER_INBOUND_PORT_URI, simArchitectureURI,
			 executesAsUnitTest);
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null}
	 * pre	{@code !electricMeterInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest			true if the component executes as a unit test, false otherwise.
	 * @throws Exception					<i>to do</i>.
	 */
	protected			ElectricMeter(
		String electricMeterInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		this(electricMeterInboundPortURI, simArchitectureURI,
			 executesAsUnitTest, 1, 1);
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null}
	 * pre	{@code !electricMeterInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest			true if the component executes as a unit test, false otherwise.
	 * @param nbThreads						number of standard threads.
	 * @param nbSchedulableThreads			number of schedulable threads.
	 * @throws Exception					<i>to do</i>.
	 */
	protected			ElectricMeter(
		String electricMeterInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest,
		int nbThreads,
		int nbSchedulableThreads
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI, nbThreads, nbSchedulableThreads);
		this.initialise(electricMeterInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code electricMeterInboundPortURI != null}
	 * pre	{@code !electricMeterInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * pre	{@code nbThreads >= 0 && nbSchedulableThreads >= 0}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI		URI of the reflection innbound port of the component.
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest			true if the component executes as a unit test, false otherwise.
	 * @param nbThreads						number of standard threads.
	 * @param nbSchedulableThreads			number of schedulable threads.
	 * @throws Exception					<i>to do</i>.
	 */
	protected			ElectricMeter(
		String reflectionInboundPortURI,
		String electricMeterInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest,
		int nbThreads,
		int nbSchedulableThreads
		) throws Exception
	{
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		this.initialise(electricMeterInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * initialise an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null}
	 * pre	{@code !electricMeterInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest			true if the component executes as a unit test, false otherwise.
	 * @throws Exception					<i>to do</i>.
	 */
	protected void		initialise(
		String electricMeterInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		assert	electricMeterInboundPortURI != null;
		assert	!electricMeterInboundPortURI.isEmpty();
		assert	simArchitectureURI != null;
		assert	!simArchitectureURI.isEmpty() || !executesAsUnitTest;

		this.simArchitectureURI = simArchitectureURI;
		this.executesAsUnitTest = executesAsUnitTest;
		this.isSILsimulated = !simArchitectureURI.isEmpty();

		this.emip =
				new ElectricMeterInboundPort(electricMeterInboundPortURI, this);
		this.emip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Electric meter component");
			this.tracer.get().setRelativePosition(1, 1);
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

		this.traceMessage("Electric meter starts.\n");

		if (this.isSILsimulated) {
			// create the simulator plug-in instance, attaching to it a
			// scheduled executor service allowing it to perform the
			// simulation steps in real time
			this.createNewExecutorService(
								SCHEDULED_EXECUTOR_SERVICE_URI, 1, true);
			this.simulatorPlugin =
					new ElectricMeterRTAtomicSimulatorPlugin();
			this.simulatorPlugin.setPluginURI(ElectricMeterCoupledModel.URI);
			this.simulatorPlugin.setSimulationExecutorService(
											SCHEDULED_EXECUTOR_SERVICE_URI);
			try {
				// the plug-in is programmed to be able to create the
				// simulation architecture and initialise itself to be able
				// to perform the simulations
				this.simulatorPlugin.initialiseSimulationArchitecture(
								this.simArchitectureURI,
								this.executesAsUnitTest ?
									ACC_FACTOR
								:	CVM_SIL.ACC_FACTOR);
				// lastly, install the plug-in on the component
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
		if (this.executesAsUnitTest) {
			this.simulatorPlugin.setSimulationRunParameters(
												new HashMap<String, Object>());
			long simStart = System.currentTimeMillis() + 1000L;
			this.simulatorPlugin.startRTSimulation(simStart, 0.0, 10.0);
			this.traceMessage("real time if start = " + simStart + "\n");

			// test scenario: code executions are scheduled to happen during
			// the simulation; SIL simulations execute in real time
			// (possibly accelerated) so that, when correctly scheduled, code
			// execution can occur on the same time reference in order to get
			// coherent exchanges between the two
			final ElectricMeterRTAtomicSimulatorPlugin sp =
														this.simulatorPlugin;
			
			//water heater
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SwitchOnHeater event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												WATER_HEATER_ELECTRICITY_MODEL_URI,
									t -> new SwitchOnWaterHeater(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 1 second, possibly accelerated
					(long)(1.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the Heat event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
									WATER_HEATER_ELECTRICITY_MODEL_URI,
									t -> new HeatWater(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 3 second, possibly accelerated
					(long)(3.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the DoNotHeat event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
									WATER_HEATER_ELECTRICITY_MODEL_URI,
									t -> new DoNotHeatWater(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 7 second, possibly accelerated
					(long)(7.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SwitchOffHeater event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
									WATER_HEATER_ELECTRICITY_MODEL_URI,
									t -> new SwitchOffWaterHeater(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 9 second, possibly accelerated
					(long)(9.0/ACC_FACTOR),
					TimeUnit.SECONDS);

			//Fan
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SwitchOnFan event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
									FAN_ELECTRICITY_MODEL_URI,
									t -> new SwitchOnFan(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 2 second, possibly accelerated
					(long)(2.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SetHighHairDryer event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
											FAN_ELECTRICITY_MODEL_URI,
									t -> new SetHighFan(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 4 second, possibly accelerated
					(long)(4.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SetLowFan event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
											FAN_ELECTRICITY_MODEL_URI,
									t -> new SetLowFan(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 5 second, possibly accelerated
					(long)(5.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SwitchOffHairDryer event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
											FAN_ELECTRICITY_MODEL_URI,
									t -> new SwitchOffFan(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 8 second, possibly accelerated
					(long)(8.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			//VacuumCleaner
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SwitchOnVacuumCleaner event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
									VACUUMCLEANER_ELECTRICITY_MODEL_URI,
									t -> new SwitchOnVacuumCleaner(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 2 second, possibly accelerated
					(long)(2.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SetHighHairDryer event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
											VACUUMCLEANER_ELECTRICITY_MODEL_URI,
									t -> new SetHighVacuumCleaner(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 4 second, possibly accelerated
					(long)(4.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SetLowVacuumCleaner event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
											VACUUMCLEANER_ELECTRICITY_MODEL_URI,
									t -> new SetLowVacuumCleaner(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 5 second, possibly accelerated
					(long)(5.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the SwitchOffHairDryer event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
											VACUUMCLEANER_ELECTRICITY_MODEL_URI,
									t -> new SwitchOffVacuumCleaner(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 8 second, possibly accelerated
					(long)(8.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			
			//Refrigerator
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												REFRIGERATOR_ELECTRICITY_MODEL_URI,
									t -> new OnRefrigerator(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(1.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												REFRIGERATOR_ELECTRICITY_MODEL_URI,
									t -> new OpenRefrigeratorDoor(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(8.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												REFRIGERATOR_ELECTRICITY_MODEL_URI,
									t -> new Freezing(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(4.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												REFRIGERATOR_ELECTRICITY_MODEL_URI,
									t -> new CloseRefrigeratorDoor(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(2.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												REFRIGERATOR_ELECTRICITY_MODEL_URI,
									t -> new Resting(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(3.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												REFRIGERATOR_ELECTRICITY_MODEL_URI,
									t -> new OffRefrigerator(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(3.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			//washing machine
			/*
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												WASHING_MACHINE_ELECTRICITY_MODEL_URI,
									t -> new SwitchOnWashingMachine(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(1.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												WASHING_MACHINE_ELECTRICITY_MODEL_URI,
									t -> new Wash(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(7.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												WASHING_MACHINE_ELECTRICITY_MODEL_URI,
									t -> new HeatWater(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(4.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												WASHING_MACHINE_ELECTRICITY_MODEL_URI,
									t -> new DoNotHeatWater(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(4.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												WASHING_MACHINE_ELECTRICITY_MODEL_URI,
									t -> new Rinse(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(7.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												WASHING_MACHINE_ELECTRICITY_MODEL_URI,
									t -> new SwitchOffWashingMachine(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					(long)(1.0/ACC_FACTOR),
					TimeUnit.SECONDS);
					*/
			
			//wind turbine
			/*
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the Start event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
												WIND_TURBINE_ELECTRICITY_MODEL_URI,
									t -> new StartWindTurbine(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 1 second, possibly accelerated
					(long)(1.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			this.scheduleTask(
					AbstractComponent.STANDARD_SCHEDULABLE_HANDLER_URI,
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								// trigger the stop event
								sp.triggerExternalEvent(
									ElectricMeterRTAtomicSimulatorPlugin.
									WIND_TURBINE_ELECTRICITY_MODEL_URI,
									t -> new StopWindTurbine(t));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					// compute the real time of occurrence of this code
					// execution: at 3 second, possibly accelerated
					(long)(3.0/ACC_FACTOR),
					TimeUnit.SECONDS);
			*/
			
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		this.traceMessage("Electric meter stops.\n");

		try {
			this.emip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.meter.ElectricMeterImplementationI#getCurrentConsumption()
	 */
	@Override
	public double		getCurrentConsumption() throws Exception
	{
		double currentConsumption =  0.0;
		if (this.isSILsimulated) {
			currentConsumption =
				(double) this.simulatorPlugin.getModelStateValue(
						ElectricMeterElectricitySILModel.URI,
						ElectricMeterRTAtomicSimulatorPlugin.
						CURRENT_CONSUMPTION);
		} 
		
		if (ElectricMeter.VERBOSE) {
			StringBuffer message =
					new StringBuffer(
						"Electric Meter returns the current consumption ");
			message.append(currentConsumption);
			message.append(".\n");
			this.traceMessage(message.toString());
		}
		return currentConsumption;
	}


	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.meter.ElectricMeterImplementationI#getCurrentProduction()
	 */
	@Override
	public double		getCurrentProduction() throws Exception
	{
		double currentProduction =  0.0;
		if (this.isSILsimulated) {
			currentProduction =
				(double) this.simulatorPlugin.getModelStateValue(
						ElectricMeterElectricitySILModel.URI,
						ElectricMeterRTAtomicSimulatorPlugin.
						CURRENT_PRODUCTION);
		} 
		
		if (ElectricMeter.VERBOSE) {
			StringBuffer message =
					new StringBuffer(
						"Electric Meter returns the current production ");
			message.append(currentProduction);
			message.append(".\n");
			this.traceMessage(message.toString());
		}
		return currentProduction;
	}
}
// -----------------------------------------------------------------------------
