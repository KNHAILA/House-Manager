package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.storage.battery.mil.BatteryCoupledModel;
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;
import fr.sorbonne_u.storage.battery.mil.BatteryPercentageModel;
import fr.sorbonne_u.storage.battery.mil.events.*;
import fr.sorbonne_u.storage.battery.sil.BatteryChargePercentageSILModel;
import fr.sorbonne_u.storage.battery.sil.BatteryElectricitySILModel;
import fr.sorbonne_u.storage.battery.sil.BatteryStateModel;

//-----------------------------------------------------------------------------
/**
* The class <code>BatteryRTAtomicSimulatorPlugin</code> implements
* the simulation plug-in for the <code>Battery</code> component.
*
* <p><strong>Description</strong></p>
* 
* <p>
* This plug-in implementation illustrates the use of the method
* {@code setSimulationRunParameters} to set the reference to the object
* representing the owner component so that simulations models can refer
* to this component to perform some operations (tracing, calling services,
* etc.). It also illustrates the use of the method {@code getModelStateValue}
* by the component code to access values in the state of simulation models
* at run time. Here, this is used to simulate a room temperature sensor by
* getting the simulated value for this in the
* {@code BatteryTemperatureSILModel}.
* </p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
* <p>Created on : 2021-10-05</p>
* 
* @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
*/
public class			BatteryRTAtomicSimulatorPlugin
extends		RTAtomicSimulatorPlugin
{
	// -------------------------------------------------------------------------
		// Constants and variables
		// -------------------------------------------------------------------------

		private static final long	serialVersionUID = 1L;
		/** simulation architectures can have URI to name them; this is the
		 *  URI used in this example for unit tests.							*/
		public static final String	UNIT_TEST_SIM_ARCHITECTURE_URI =
																"UnitTestBattery";
		/** name used to pass the owner component reference as simulation
		 *  parameter.															*/
		public static final String	OWNER_REFERENCE_NAME = "THCRN";
		/** name used to access the current room temperature in the
		 *  {@code BatteryChargePercentageSILModel}.								 	*/
		public static final String	CURRENT_BATTERY_PERCENTAGE = "cbp";

		// -------------------------------------------------------------------------
		// DEVS simulation protocol
		// -------------------------------------------------------------------------

		/**
		 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#setSimulationRunParameters(java.util.Map)
		 */
		@Override
		public void			setSimulationRunParameters(
			Map<String, Object> simParams
			) throws Exception
		{
			// initialise the simulation parameter giving the reference to the
			// owner component before passing the parameters to the simulation
			// models
			simParams.put(OWNER_REFERENCE_NAME, this.getOwner());

			// this will pass the parameters to the simulation models that will
			// then be able to get their own parameters.
			super.setSimulationRunParameters(simParams);

			// remove the value so that the reference may not exit the context of
			// the component
			simParams.remove(OWNER_REFERENCE_NAME);
		}

		/**
		 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String, java.lang.String)
		 */
		@Override
		public Object		getModelStateValue(
			String modelURI,
			String name
			) throws Exception
		{
			assert	modelURI != null && name != null;

			// In the Battery model, the only accessible model state value is
			// the current room temperature in the BatteryTemperatureModel
			assert	modelURI.equals(BatteryChargePercentageSILModel.URI);
			assert	name.equals(CURRENT_BATTERY_PERCENTAGE);

			// Get a Java reference on the object representing the corresponding
			// simulation model.
			ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
			// The only model in this example that provides access to some value
			// is the BatteryChargePercentageSILModel.
			assert	m instanceof BatteryChargePercentageSILModel;

			return ((BatteryChargePercentageSILModel)m).getCurrentBatteryPercentage();
		}

		// -------------------------------------------------------------------------
		// Methods
		// -------------------------------------------------------------------------

		/**
		 * create and set the simulation architecture internal to this component.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code simArchURI != null}
		 * pre	{@code accFactor > 0.0}
		 * post	true		// no postcondition.
		 * </pre>
		 *
		 * @param simArchURI	URI of the simulation architecture to be created.
		 * @param accFactor		acceleration factor used in the real time simulation.
		 * @throws Exception	<i>to do</i>.
		 */
		public void			initialiseSimulationArchitecture(
			String simArchURI,
			double accFactor
			) throws Exception
		{
			Map<String,AbstractAtomicModelDescriptor>
										atomicModelDescriptors = new HashMap<>();
			Map<String,CoupledModelDescriptor>
										coupledModelDescriptors = new HashMap<>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(BatteryStateModel.URI);
			submodels.add(BatteryChargePercentageSILModel.URI);

			Map<Class<? extends EventI>,ReexportedEvent> reexported = null;
			Map<EventSource, EventSink[]> connections =
										new HashMap<EventSource, EventSink[]>();

			atomicModelDescriptors.put(
					BatteryStateModel.URI,
					RTAtomicModelDescriptor.create(
							BatteryStateModel.class,
							BatteryStateModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
							accFactor));
			atomicModelDescriptors.put(
					BatteryChargePercentageSILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							BatteryChargePercentageSILModel.class,
							BatteryChargePercentageSILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
							accFactor));

			if (UNIT_TEST_SIM_ARCHITECTURE_URI.equals(simArchURI)) {
				// when executed as a unit test, the simulation architecture
				// includes the battery electricity model and events
				// exported by the state model are directed to the electricity
				// model
				submodels.add(BatteryElectricitySILModel.URI);

				atomicModelDescriptors.put(
						BatteryElectricitySILModel.URI,
						RTAtomicHIOA_Descriptor.create(
								BatteryElectricitySILModel.class,
								BatteryElectricitySILModel.URI,
								TimeUnit.SECONDS,
								null,
								SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
								accFactor));

				connections.put(
						new EventSource(BatteryStateModel.URI,
										UseBattery.class),
						new EventSink[] {
								new EventSink(BatteryElectricitySILModel.URI,
											  UseBattery.class),
								new EventSink(BatteryChargePercentageSILModel.URI,
										UseBattery.class)
						});
				connections.put(
						new EventSource(BatteryStateModel.URI,
								 		DoNotUseBattery.class),
						new EventSink[] {
								new EventSink(BatteryElectricitySILModel.URI,
											  DoNotUseBattery.class),
								new EventSink(BatteryChargePercentageSILModel.URI,
										DoNotUseBattery.class)
						});
				connections.put(
						new EventSource(BatteryStateModel.URI,
										ChargeBattery.class),
						new EventSink[] {
								new EventSink(BatteryElectricitySILModel.URI,
										ChargeBattery.class),
								new EventSink(BatteryChargePercentageSILModel.URI,
										ChargeBattery.class)
						});
				connections.put(
						new EventSource(BatteryStateModel.URI,
										DoNotChargeBattery.class),
						new EventSink[] {
								new EventSink(BatteryElectricitySILModel.URI,
											  DoNotChargeBattery.class),
								new EventSink(BatteryChargePercentageSILModel.URI,
											  DoNotChargeBattery.class)
						});
			} else {
				// when *not* executed as a unit test, the simulation architecture
				// does not include the battery electricity model and events
				// exported by the state model are reexported by the coupled model

				connections.put(
						new EventSource(BatteryStateModel.URI,
										ChargeBattery.class),
						new EventSink[] {
								new EventSink(BatteryChargePercentageSILModel.URI,
										ChargeBattery.class)
						});
				connections.put(
						new EventSource(BatteryStateModel.URI,
										DoNotChargeBattery.class),
						new EventSink[] {
								new EventSink(BatteryChargePercentageSILModel.URI,
											  DoNotChargeBattery.class)
						});
				connections.put(
						new EventSource(BatteryStateModel.URI,
										DoNotUseBattery.class),
						new EventSink[] {
								new EventSink(BatteryChargePercentageSILModel.URI,
											  DoNotUseBattery.class)
						});
				connections.put(
						new EventSource(BatteryStateModel.URI,
										UseBattery.class),
						new EventSink[] {
								new EventSink(BatteryChargePercentageSILModel.URI,
											  UseBattery.class)
						});

				reexported =
						new HashMap<Class<? extends EventI>,ReexportedEvent>();
				reexported.put(UseBattery.class,
							   new ReexportedEvent(BatteryStateModel.URI,
									   			   UseBattery.class));
				reexported.put(DoNotUseBattery.class,
						   new ReexportedEvent(BatteryStateModel.URI,
								   			   DoNotUseBattery.class));
				reexported.put(ChargeBattery.class,
						   new ReexportedEvent(BatteryStateModel.URI,
								   ChargeBattery.class));
				reexported.put(DoNotChargeBattery.class,
						   new ReexportedEvent(BatteryStateModel.URI,
								   			   DoNotChargeBattery.class));
			}

			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("electricityState",
					BatteryElectricityModel.State.class,
					BatteryElectricitySILModel.URI),
						 new VariableSink[] {
								 new VariableSink("electricityState",
										 BatteryElectricityModel.State.class,
										 BatteryChargePercentageSILModel.URI)
						 });
			
			coupledModelDescriptors.put(
					BatteryCoupledModel.URI,
					new RTCoupledHIOA_Descriptor(
							BatteryCoupledModel.class,
							BatteryCoupledModel.URI,
							submodels,
							null,
							reexported,
							connections,
							null,
							SimulationEngineCreationMode.COORDINATION_RT_ENGINE,
							null,
							null,
							bindings,
							accFactor));

			// this sets the architecture in the plug-in for further reference
			// and use
			this.setSimulationArchitecture(
					new RTArchitecture(
							simArchURI,
							BatteryCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS,
							accFactor));
		}
}
//-----------------------------------------------------------------------------
