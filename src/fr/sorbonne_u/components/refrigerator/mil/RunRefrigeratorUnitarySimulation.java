package fr.sorbonne_u.components.refrigerator.mil;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.refrigerator.mil.events.*;
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

public class			RunRefrigeratorUnitarySimulation
{
    public static void main(String[] args)
    {
        try {
            // map that will contain the atomic model descriptors to construct
            // the simulation architecture
            Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            // the Refrigerator models simulating its electricity consumption, its
            // temperatures and the external temperature are atomic HIOA models
            // hence we use an AtomicHIOA_Descriptor(s)
            atomicModelDescriptors.put(
                    RefrigeratorElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            RefrigeratorElectricityModel.class,
                            RefrigeratorElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            atomicModelDescriptors.put(
                    RefrigeratorTemperatureModel.URI,
                    AtomicHIOA_Descriptor.create(
                            RefrigeratorTemperatureModel.class,
                            RefrigeratorTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            atomicModelDescriptors.put(
                    RefrigeratorExternalTemperatureModel.URI,
                    AtomicHIOA_Descriptor.create(
                            RefrigeratorExternalTemperatureModel.class,
                            RefrigeratorExternalTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            // the Refrigerator unit tester model only exchanges event, an
            // atomic model hence we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
                    RefrigeratorUnitTesterModel.URI,
                    AtomicModelDescriptor.create(
                            RefrigeratorUnitTesterModel.class,
                            RefrigeratorUnitTesterModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<String>();
            submodels.add(RefrigeratorElectricityModel.URI);
            submodels.add(RefrigeratorTemperatureModel.URI);
            submodels.add(RefrigeratorExternalTemperatureModel.URI);
            submodels.add(RefrigeratorUnitTesterModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource,EventSink[]> connections =
                    new HashMap<EventSource,EventSink[]>();

            connections.put(
                    new EventSource(RefrigeratorUnitTesterModel.URI,
                            OnRefrigerator.class),
                    new EventSink[] {
                            new EventSink(RefrigeratorElectricityModel.URI,
                                    OnRefrigerator.class)
                    });
            connections.put(
                    new EventSource(RefrigeratorUnitTesterModel.URI,
                            OffRefrigerator.class),
                    new EventSink[] {
                            new EventSink(RefrigeratorElectricityModel.URI,
                                    OffRefrigerator.class),
                            new EventSink(RefrigeratorTemperatureModel.URI,
                                    Freezing.class)
                    });
            connections.put(
                    new EventSource(RefrigeratorUnitTesterModel.URI, Freezing.class),
                    new EventSink[] {
                            new EventSink(RefrigeratorElectricityModel.URI,
                                    Freezing.class),
                            new EventSink(RefrigeratorTemperatureModel.URI,
                                    Freezing.class)
                    });
            connections.put(
                    new EventSource(RefrigeratorUnitTesterModel.URI, Resting.class),
                    new EventSink[] {
                            new EventSink(RefrigeratorElectricityModel.URI,
                                    Resting.class),
                            new EventSink(RefrigeratorTemperatureModel.URI,
                                    Resting.class)
                    });
            connections.put(
                    new EventSource(RefrigeratorUnitTesterModel.URI, CloseRefrigeratorDoor.class),
                    new EventSink[] {
                            new EventSink(RefrigeratorElectricityModel.URI,
                                    CloseRefrigeratorDoor.class),
                            new EventSink(RefrigeratorTemperatureModel.URI,
                                    CloseRefrigeratorDoor.class)
                    });
            connections.put(
                    new EventSource(RefrigeratorUnitTesterModel.URI, OpenRefrigeratorDoor.class),
                    new EventSink[] {
                            new EventSink(RefrigeratorTemperatureModel.URI,
                                    OpenRefrigeratorDoor.class)
                    });

            // variable bindings between exporting and importing models
            Map<VariableSource,VariableSink[]> bindings =
                    new HashMap<VariableSource,VariableSink[]>();

            bindings.put(new VariableSource("externalTemperature",
                            Double.class,
                            RefrigeratorExternalTemperatureModel.URI),
                    new VariableSink[] {
                            new VariableSink("externalTemperature",
                                    Double.class,
                                    RefrigeratorTemperatureModel.URI)
                    });

            // coupled model descriptor
            coupledModelDescriptors.put(
                    RefrigeratorCoupledModel.URI,
                    new CoupledHIOA_Descriptor(
                            RefrigeratorCoupledModel.class,
                            RefrigeratorCoupledModel.URI,
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
                            RefrigeratorCoupledModel.URI,
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
