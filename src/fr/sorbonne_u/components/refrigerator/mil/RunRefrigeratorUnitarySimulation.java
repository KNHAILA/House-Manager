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

// -----------------------------------------------------------------------------
/**
 * The class <code>RunRefrigeratorUnitarySimulation</code> creates a simulator
 * for the Refrigerator and then runs a typical simulation.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 * This class shows how to use simulation model descriptors to create the
 * description of a simulation architecture and then create an instance of this
 * architecture by instantiating and connecting the models. Note how models
 * are described by atomic model descriptors and coupled model descriptors and
 * then the connections between coupled models and their submodels as well as
 * exported events and variables to imported ones are described by different
 * maps. In this example, only connections of events and bindings of variables
 * between models within this architecture are necessary, but when creating
 * coupled models, they can also import and export events and variables
 * consumed and produced by their submodels.
 * </p>
 * <p>
 * The architecture object is the root of this description and it provides
 * the method {@code constructSimulator} that instantiate the models and
 * connect them. This method returns the reference on the simulator attached
 * to the root coupled model in the architecture instance, which is then used
 * to perform simulation runs by calling the method
 * {@code doStandAloneSimulation}.
 * </p>
 * <p>
 * The descriptors and maps can be viewed as kinds of nodes in the abstract
 * syntax tree of an architectural language that does not have a concrete
 * syntax yet.
 * </p>
 *
 * <p><strong>Invariant</strong></p>
 *
 * <pre>
 * invariant	true
 * </pre>
 *
 * <p>Created on : 2021-09-23</p>
 *
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
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
                    ExternalTemperatureModel.URI,
                    AtomicHIOA_Descriptor.create(
                            ExternalTemperatureModel.class,
                            ExternalTemperatureModel.URI,
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
            submodels.add(ExternalTemperatureModel.URI);
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
                                    OffRefrigerator.class)
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
                            ExternalTemperatureModel.URI),
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
