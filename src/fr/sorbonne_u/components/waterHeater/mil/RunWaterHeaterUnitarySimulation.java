package fr.sorbonne_u.components.waterHeater.mil;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.waterHeater.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOffWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOnWaterHeater;
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

public class			RunWaterHeaterUnitarySimulation
{
	public static void main(String[] args)
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
					WaterHeaterTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							WaterHeaterTemperatureModel.class,
							WaterHeaterTemperatureModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					WaterHeaterExternalTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							WaterHeaterExternalTemperatureModel.class,
							WaterHeaterExternalTemperatureModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(
					WaterHeaterUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							WaterHeaterUnitTesterModel.class,
							WaterHeaterUnitTesterModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

	
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(WaterHeaterElectricityModel.URI);
			submodels.add(WaterHeaterTemperatureModel.URI);
			submodels.add(WaterHeaterExternalTemperatureModel.URI);
			submodels.add(WaterHeaterUnitTesterModel.URI);
			
			
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(WaterHeaterUnitTesterModel.URI,
									SwitchOnWaterHeater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricityModel.URI,
										  SwitchOnWaterHeater.class)
					});
			connections.put(
					new EventSource(WaterHeaterUnitTesterModel.URI,
									SwitchOffWaterHeater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricityModel.URI,
										  SwitchOffWaterHeater.class)
					});
			connections.put(
					new EventSource(WaterHeaterUnitTesterModel.URI, HeatWater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricityModel.URI,
										  HeatWater.class),
							new EventSink(WaterHeaterTemperatureModel.URI,
										  HeatWater.class)
					});
			connections.put(
					new EventSource(WaterHeaterUnitTesterModel.URI, DoNotHeatWater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricityModel.URI,
										  DoNotHeatWater.class),
							new EventSink(WaterHeaterTemperatureModel.URI,
										  DoNotHeatWater.class)
					});

			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("externalWaterTemperature",
											Double.class,
							WaterHeaterExternalTemperatureModel.URI),
						 new VariableSink[] {
								 new VariableSink("externalWaterTemperature",
										 		  Double.class,
										 		  WaterHeaterTemperatureModel.URI)
						 });

			// coupled model descriptor
			coupledModelDescriptors.put(
					WaterHeaterCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							WaterHeaterCoupledModel.class,
							WaterHeaterCoupledModel.URI,
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
							WaterHeaterCoupledModel.URI,
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
