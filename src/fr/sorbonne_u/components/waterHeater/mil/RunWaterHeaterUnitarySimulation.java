package fr.sorbonne_u.components.waterHeater.mil;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOffWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOnWaterHeater;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class RunWaterHeaterUnitarySimulation
{
	public static void	main(String[] args)
	{
		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			atomicModelDescriptors.put(
					WaterHeaterElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							WaterHeaterElectricityModel.class,
							WaterHeaterElectricityModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(
					WaterHeaterUserModel.URI,
					AtomicModelDescriptor.create(
							WaterHeaterUserModel.class,
							WaterHeaterUserModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			
			Set<String> submodels = new HashSet<String>();
			submodels.add(WaterHeaterElectricityModel.URI);
			submodels.add(WaterHeaterUserModel.URI);

			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(WaterHeaterUserModel.URI, SwitchOnWaterHeater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricityModel.URI,
										  SwitchOnWaterHeater.class)
					});
			connections.put(
					new EventSource(WaterHeaterUserModel.URI, SwitchOffWaterHeater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricityModel.URI,
										  SwitchOffWaterHeater.class)
					});

			coupledModelDescriptors.put(
					WaterHeaterCoupledModel.URI,
					new CoupledModelDescriptor(
							WaterHeaterCoupledModel.class,
							WaterHeaterCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE));

			ArchitectureI architecture =
					new Architecture(
							WaterHeaterCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS);

			SimulationEngine se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			se.doStandAloneSimulation(0.0, 10.0);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
