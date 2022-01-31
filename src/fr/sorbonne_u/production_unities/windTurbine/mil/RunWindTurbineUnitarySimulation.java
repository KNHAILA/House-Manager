package fr.sorbonne_u.production_unities.windTurbine.mil;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import fr.sorbonne_u.production_unities.windTurbine.WindTurbineUnitTester;
import fr.sorbonne_u.production_unities.windTurbine.mil.events.*;

public class RunWindTurbineUnitarySimulation {
	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			// the WindTurbine models simulating its electricity consumption, its
			// Percentages and the external Percentage are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					WindTurbineElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							WindTurbineElectricityModel.class,
							WindTurbineElectricityModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					WindSpeedModel.URI,
					AtomicHIOA_Descriptor.create(
							WindSpeedModel.class,
							WindSpeedModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			// the WindTurbine unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					WindTurbineUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							WindTurbineUnitTesterModel.class,
							WindTurbineUnitTesterModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(WindTurbineElectricityModel.URI);
			submodels.add(WindSpeedModel.URI);
			submodels.add(WindTurbineUnitTesterModel.URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(WindTurbineUnitTesterModel.URI,
									StartWindTurbine.class),
					new EventSink[] {
							new EventSink(WindTurbineElectricityModel.URI,
										  StartWindTurbine.class),							
					});
			connections.put(
					new EventSource(WindTurbineUnitTesterModel.URI,
									StopWindTurbine.class),
					new EventSink[] {
							new EventSink(WindTurbineElectricityModel.URI,
										  StopWindTurbine.class)
					});
			connections.put(
					new EventSource(WindTurbineUnitTesterModel.URI, UseWindTurbine.class),
					new EventSink[] {
							new EventSink(WindTurbineElectricityModel.URI,
										  UseWindTurbine.class)
					});
			connections.put(
					new EventSource(WindTurbineUnitTesterModel.URI, DoNotUseWindTurbine.class),
					new EventSink[] {
							new EventSink(WindTurbineElectricityModel.URI,
										  DoNotUseWindTurbine.class)
					});
			
			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings = new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("currentWindSpeed",
														Double.class,
														WindSpeedModel.URI),
									 new VariableSink[] {
											 new VariableSink("currentWindSpeed",
													 		  Double.class,
													 		  WindTurbineElectricityModel.URI)
			});
			// coupled model descriptor
			coupledModelDescriptors.put(
					WindTurbineCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							WindTurbineCoupledModel.class,
							WindTurbineCoupledModel.URI,
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
							WindTurbineCoupledModel.URI,
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
