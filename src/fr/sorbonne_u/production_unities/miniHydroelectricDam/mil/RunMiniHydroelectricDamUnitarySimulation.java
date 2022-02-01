package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.*;
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;
import fr.sorbonne_u.storage.battery.mil.BatteryPercentageModel;

public class			RunMiniHydroelectricDamUnitarySimulation
{
	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			// the MiniHydroelectricDam models simulating its electricity consumption, its
			// temperatures and the external temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					MiniHydroelectricDamElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							MiniHydroelectricDamElectricityModel.class,
							MiniHydroelectricDamElectricityModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					WaterVolumeModel.URI,
					AtomicHIOA_Descriptor.create(
							WaterVolumeModel.class,
							WaterVolumeModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			// the MiniHydroelectricDam unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					MiniHydroelectricDamUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							MiniHydroelectricDamUnitTesterModel.class,
							MiniHydroelectricDamUnitTesterModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(MiniHydroelectricDamElectricityModel.URI);
			submodels.add(WaterVolumeModel.URI);
			submodels.add(MiniHydroelectricDamUnitTesterModel.URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(MiniHydroelectricDamUnitTesterModel.URI,
							StartMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricityModel.URI,
									StartMiniHydroelectricDam.class)
					});
			connections.put(
					new EventSource(MiniHydroelectricDamUnitTesterModel.URI,
							StopMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricityModel.URI,
									StopMiniHydroelectricDam.class)
					});

			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("waterVolume",
					                     Double.class,
											WaterVolumeModel.URI),
						 new VariableSink[] {
								 new VariableSink("waterVolume",
										 Double.class,
										 MiniHydroelectricDamElectricityModel.URI)
						 });

			// coupled model descriptor
			coupledModelDescriptors.put(
					MiniHydroelectricDamCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							MiniHydroelectricDamCoupledModel.class,
							MiniHydroelectricDamCoupledModel.URI,
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
							MiniHydroelectricDamCoupledModel.URI,
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
