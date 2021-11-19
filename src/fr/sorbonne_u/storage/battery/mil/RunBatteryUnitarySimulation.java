package fr.sorbonne_u.storage.battery.mil;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import fr.sorbonne_u.storage.battery.mil.events.*;
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

public class RunBatteryUnitarySimulation
{
    public static void	main(String[] args)
    {
        try {
            // map that will contain the atomic model descriptors to construct
            // the simulation architecture
            Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            // the Battery model simulating its electricity consumption, an
            // atomic HIOA model hence we use an AtomicHIOA_Descriptor
            atomicModelDescriptors.put(
                    BatteryElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            BatteryElectricityModel.class,
                            BatteryElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    BatteryUserModel.URI,
                    AtomicModelDescriptor.create(
                            BatteryUserModel.class,
                            BatteryUserModel.URI,
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
            submodels.add(BatteryUserModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource,EventSink[]> connections =
                    new HashMap<EventSource,EventSink[]>();

            connections.put(
                    new EventSource(BatteryUserModel.URI, ChargeBattery.class),
                    new EventSink[] {
                            new EventSink(BatteryElectricityModel.URI,
                                    ChargeBattery.class)
                    });
            connections.put(
                    new EventSource(BatteryUserModel.URI, UseBattery.class),
                    new EventSink[] {
                            new EventSink(BatteryElectricityModel.URI,
                                    UseBattery.class)
                    });

            // coupled model descriptor
            coupledModelDescriptors.put(
                    BatteryCoupledModel.URI,
                    new CoupledModelDescriptor(
                            BatteryCoupledModel.class,
                            BatteryCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            SimulationEngineCreationMode.COORDINATION_ENGINE));

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

