package fr.sorbonne_u.storage.battery.mil;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.storage.battery.mil.events.*;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class RunBatteryUnitarySimulation
{
	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			// the Battery models simulating its electricity consumption, its
			// Percentages and the external Percentage are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					BatteryElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							BatteryElectricityModel.class,
							BatteryElectricityModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					BatteryPercentageModel.URI,
					AtomicHIOA_Descriptor.create(
							BatteryPercentageModel.class,
							BatteryPercentageModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			// the Battery unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					BatteryUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							BatteryUnitTesterModel.class,
							BatteryUnitTesterModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(BatteryElectricityModel.URI);
			submodels.add(BatteryPercentageModel.URI);
			submodels.add(BatteryUnitTesterModel.URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(BatteryUnitTesterModel.URI,
									UseBattery.class),
					new EventSink[] {
							new EventSink(BatteryElectricityModel.URI,
										  UseBattery.class),
					new EventSink(BatteryPercentageModel.URI,
									UseBattery.class)							
					});
			connections.put(
					new EventSource(BatteryUnitTesterModel.URI,
									DoNotUseBattery.class),
					new EventSink[] {
							new EventSink(BatteryElectricityModel.URI,
										  DoNotUseBattery.class),
					new EventSink(BatteryPercentageModel.URI,
							DoNotUseBattery.class)
					});
			connections.put(
					new EventSource(BatteryUnitTesterModel.URI, ChargeBattery.class),
					new EventSink[] {
							new EventSink(BatteryElectricityModel.URI,
										  ChargeBattery.class),
							new EventSink(BatteryPercentageModel.URI,
										  ChargeBattery.class)
					});
			connections.put(
					new EventSource(BatteryUnitTesterModel.URI, DoNotChargeBattery.class),
					new EventSink[] {
							new EventSink(BatteryElectricityModel.URI,
										  DoNotChargeBattery.class),
							new EventSink(BatteryPercentageModel.URI,
										  DoNotChargeBattery.class)
					});
			
			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("electricityState",
					BatteryElectricityModel.State.class,
											BatteryElectricityModel.URI),
						 new VariableSink[] {
								 new VariableSink("electricityState",
										 BatteryElectricityModel.State.class,
										 		  BatteryPercentageModel.URI)
						 });


			// coupled model descriptor
			coupledModelDescriptors.put(
					BatteryCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							BatteryCoupledModel.class,
							BatteryCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE,
							null,
							null,
							bindings));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
							BatteryCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS);

			// create the simulator from the simulation architecture
			SimulationEngine se = architecture.constructSimulator();
			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			// run a simulation with the simulation beginning at 0.0 and
			// ending at 10.0
			se.doStandAloneSimulation(0.0, 10.0);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------

