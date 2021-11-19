package fr.sorbonne_u.components.fan.mil;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOnFan;
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

public class RunFanUnitarySimulation
{
    public static void	main(String[] args)
    {
        try {
            // map that will contain the atomic model descriptors to construct
            // the simulation architecture
            Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            // the fan model simulating its electricity consumption, an
            // atomic HIOA model hence we use an AtomicHIOA_Descriptor
            atomicModelDescriptors.put(
                    FanElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            FanElectricityModel.class,
                            FanElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    FanUserModel.URI,
                    AtomicModelDescriptor.create(
                            FanUserModel.class,
                            FanUserModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<String>();
            submodels.add(FanElectricityModel.URI);
            submodels.add(FanUserModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource,EventSink[]> connections =
                    new HashMap<EventSource,EventSink[]>();

            connections.put(
                    new EventSource(FanUserModel.URI, SwitchOnFan.class),
                    new EventSink[] {
                            new EventSink(FanElectricityModel.URI,
                                    SwitchOnFan.class)
                    });
            connections.put(
                    new EventSource(FanUserModel.URI, SwitchOffFan.class),
                    new EventSink[] {
                            new EventSink(FanElectricityModel.URI,
                                    SwitchOffFan.class)
                    });
            connections.put(
                    new EventSource(FanUserModel.URI, SetHighFan.class),
                    new EventSink[] {
                            new EventSink(FanElectricityModel.URI,
                                    SetHighFan.class)
                    });
            connections.put(
                    new EventSource(FanUserModel.URI, SetLowFan.class),
                    new EventSink[] {
                            new EventSink(FanElectricityModel.URI,
                                    SetLowFan.class)
                    });

            // coupled model descriptor
            coupledModelDescriptors.put(
                    FanCoupledModel.URI,
                    new CoupledModelDescriptor(
                            FanCoupledModel.class,
                            FanCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            SimulationEngineCreationMode.COORDINATION_ENGINE));

            // simulation architecture
            ArchitectureI architecture =
                    new Architecture(
                            FanCoupledModel.URI,
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
