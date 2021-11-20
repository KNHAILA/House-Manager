package fr.sorbonne_u.components.washingMachine.mil;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.HeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.Rinse;
import fr.sorbonne_u.components.washingMachine.mil.events.Spin;
import fr.sorbonne_u.components.washingMachine.mil.events.StopHeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.SwitchOffWashingMachine;
import fr.sorbonne_u.components.washingMachine.mil.events.SwitchOnWashingMachine;
import fr.sorbonne_u.components.washingMachine.mil.events.Wash;
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

public class			RunWasingMachineUnitarySimulation
{
	public static void main(String[] args)
	{
		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			atomicModelDescriptors.put(
					WashingMachineElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							WashingMachineElectricityModel.class,
							WashingMachineElectricityModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					WashingMachineTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							WashingMachineTemperatureModel.class,
							WashingMachineTemperatureModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					WashingMachineExternalTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							WashingMachineExternalTemperatureModel.class,
							WashingMachineExternalTemperatureModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(
					WashingMachineUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							WashingMachineUnitTesterModel.class,
							WashingMachineUnitTesterModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

	
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(WashingMachineElectricityModel.URI);
			submodels.add(WashingMachineTemperatureModel.URI);
			submodels.add(WashingMachineExternalTemperatureModel.URI);
			submodels.add(WashingMachineUnitTesterModel.URI);
			
			
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
									SwitchOnWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									SwitchOnWashingMachine.class)
					});
			
			
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, Wash.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
										  Wash.class),
							new EventSink(WashingMachineTemperatureModel.URI,
										  DoNotHeatWater.class)
					});
			
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
									Rinse.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
										  Rinse.class)
					});
			
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, Rinse.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
										  Rinse.class),
							new EventSink(WashingMachineTemperatureModel.URI,
										  StopHeatWater.class)
					});
			
			
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, Spin.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
										  Spin.class),
							new EventSink(WashingMachineTemperatureModel.URI,
										  StopHeatWater.class)
					});
			
			
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
									SwitchOffWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									SwitchOffWashingMachine.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, HeatWater.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
										  HeatWater.class),
							new EventSink(WashingMachineTemperatureModel.URI,
										  HeatWater.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, DoNotHeatWater.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
										  DoNotHeatWater.class),
							new EventSink(WashingMachineTemperatureModel.URI,
										  DoNotHeatWater.class)
					});

			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("externalTemperature",
											Double.class,
							WashingMachineExternalTemperatureModel.URI),
						 new VariableSink[] {
								 new VariableSink("externalTemperature",
										 		  Double.class,
										 		 WashingMachineTemperatureModel.URI)
						 });

			// coupled model descriptor
			coupledModelDescriptors.put(
					WashingMachineCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							WashingMachineCoupledModel.class,
							WashingMachineCoupledModel.URI,
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
							WashingMachineCoupledModel.URI,
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
