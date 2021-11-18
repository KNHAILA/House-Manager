package fr.sorbonne_u;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_CoupledModel.HEM_Report;
import fr.sorbonne_u.components.fan.mil.FanElectricityModel;
import fr.sorbonne_u.components.fan.mil.FanUserModel;
import fr.sorbonne_u.components.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorExternalTemperatureModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorTemperatureModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorUnitTesterModel;
import fr.sorbonne_u.components.refrigerator.mil.events.CloseRefrigeratorDoor;
import fr.sorbonne_u.components.refrigerator.mil.events.Freezing;
import fr.sorbonne_u.components.refrigerator.mil.events.OffRefrigerator;
import fr.sorbonne_u.components.refrigerator.mil.events.OnRefrigerator;
import fr.sorbonne_u.components.refrigerator.mil.events.OpenRefrigeratorDoor;
import fr.sorbonne_u.components.refrigerator.mil.events.Resting;
import fr.sorbonne_u.components.meter.mil.ElectricMeterElectricityModel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunHEM_RT_Simulation</code> creates the real time simulator
 * for the household energy management example and then runs a typical
 * simulation in real time.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 * This class shows how to describe, construct and then run a real time
 * simulation. By comparison with {@code RunHEM_Simulation}, differences
 * help understanding the passage from a synthetic simulation time run
 * to a real time one. Recall that real time simulations force the simulation
 * time to follow the real time, hence in a standard real time run, the
 * simulation time advance at the rhythm of the real time. However, such
 * simulation runs can become either very lengthy, for examples like the
 * household energy management where simulation runs could last several days,
 * or very short, for examples like simulating microprocessors where events
 * can occur at the nanosecond time scale. So it is also possible to keep the
 * same time structure but to accelerate or decelerate the real time by some
 * factor, here defined as {@code ACCELERATION_FACTOR}. A value greater than
 * one will accelerate the simulation while a value strictly between 0 and 1
 * will decelerate it.
 * </p>
 * <p>
 * So, notice the use of real time equivalent to the model descriptors and
 * the simulation engine attached to models, as well as the acceleration
 * factor passed as parameter through the descriptors. The same acceleration
 * factor must be imposed to all models to get time coherent simulations.
 * </p>
 * <p>
 * The interest of real time simulations will become clear when simulation
 * models will be used in SIL simulations with the actual component software
 * executing in parallel to the simulations. Time coherent exchanges will then
 * become possible between the code and the simulations as the execution
 * of code instructions will occur on the same time frame as the simulations.
 * </p>
 *
 * <p><strong>Invariant</strong></p>
 *
 * <pre>
 * invariant	true
 * </pre>
 *
 * <p>Created on : 2021-09-30</p>
 *
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunHEM_RT_Simulation
{
    /** acceleration factor for the real time simulation; with a factor 2.0,
     *  the simulation runs two times faster than real time i.e., a run that
     *  is supposed to take 10 seconds in real time will take 5 seconds to
     *  execute.															*/
    protected static final double ACCELERATION_FACTOR = 1.0;

    public static void	main(String[] args)
    {

        try {
            // map that will contain the atomic model descriptors to construct
            // the simulation architecture
            Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            // atomic HIOA models require RTAtomicHIOA_Descriptor while
            // atomic models require RTAtomicModelDescriptor
            // the same acceleration factor must be used for all models

            // hair dryer models
            atomicModelDescriptors.put(
                    FanElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            FanElectricityModel.class,
                            FanElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    FanUserModel.URI,
                    RTAtomicModelDescriptor.create(
                            FanUserModel.class,
                            FanUserModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));

            // the Refrigerator models
            atomicModelDescriptors.put(
                    RefrigeratorElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            RefrigeratorElectricityModel.class,
                            RefrigeratorElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    RefrigeratorTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            RefrigeratorTemperatureModel.class,
                            RefrigeratorTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    RefrigeratorExternalTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            RefrigeratorExternalTemperatureModel.class,
                            RefrigeratorExternalTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    RefrigeratorUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            RefrigeratorUnitTesterModel.class,
                            RefrigeratorUnitTesterModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));

            // the electric meter model
            atomicModelDescriptors.put(
                    ElectricMeterElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            ElectricMeterElectricityModel.class,
                            ElectricMeterElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<String>();
            submodels.add(FanElectricityModel.URI);
            submodels.add(FanUserModel.URI);
            submodels.add(RefrigeratorElectricityModel.URI);
            submodels.add(RefrigeratorTemperatureModel.URI);
            submodels.add(RefrigeratorExternalTemperatureModel.URI);
            submodels.add(RefrigeratorUnitTesterModel.URI);

            submodels.add(ElectricMeterElectricityModel.URI);

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

            //Refrigerator
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

            // bindings among Refrigerator models
            bindings.put(
                    new VariableSource("externalTemperature",
                            Double.class,
                            RefrigeratorExternalTemperatureModel.URI),
                    new VariableSink[] {
                            new VariableSink("externalTemperature",
                                    Double.class,
                                    RefrigeratorTemperatureModel.URI)
                    });


            // bindings between hair dryer and Refrigerator models to the electric
            // meter model
            bindings.put(
                    new VariableSource("currentIntensity",
                            Double.class,
                            FanElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentFanIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });

            bindings.put(
                    new VariableSource("currentIntensity",
                            Double.class,
                            RefrigeratorElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentRefrigeratorIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });

            // coupled model descriptor: an HIOA requires a
            // RTCoupledHIOA_Descriptor
            coupledModelDescriptors.put(
                    HEM_CoupledModel.URI,
                    new RTCoupledHIOA_Descriptor(
                            HEM_CoupledModel.class,
                            HEM_CoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            SimulationEngineCreationMode.COORDINATION_RT_ENGINE,
                            null,
                            null,
                            bindings,
                            ACCELERATION_FACTOR));

            // simulation architecture
            ArchitectureI architecture =
                    new Architecture(
                            HEM_CoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            TimeUnit.SECONDS);

            // create the simulator from the simulation architecture
            SimulationEngine se = architecture.constructSimulator();

            // Optional: how to use simulation run parameters to modify
            // the behaviour of the models from runs to runs
         /*   Map<String, Object> simParams = new HashMap<String, Object>();
            simParams.put(
                    FanElectricityModel.LOW_MODE_CONSUMPTION_RUNPNAME,
                    1320.0);
            simParams.put(
                    FanElectricityModel.HIGH_MODE_CONSUMPTION_RUNPNAME,
                    2200.0);
            simParams.put(FanUserModel.STEP_MEAN_DURATION_RUNPNAME, 2.0);
            simParams.put(RefrigeratorElectricityModel.NOT_HEATING_POWER_RUNPNAME,
                    0.0);
            simParams.put(RefrigeratorElectricityModel.HEATING_POWER_RUNPNAME,
                    4400.0);

            se.setSimulationRunParameters(simParams);
*/
            // run a simulation with the simulation beginning at 0.0 and
            // ending at 10.0

            // duration of the simulation in seconds, the simulation time unit
            double simDuration = 10.0;
            // the real time of start of the simulation plus a 1s delay to give
            // the time to initialise all models in the architecture.
            long start = System.currentTimeMillis() + 1000L;

            se.startRTSimulation(start, 0.0, simDuration);

            // Optional: simulation report
            // wait until the simulation ends i.e., the start delay  plus the
            // duration of the simulation in milliseconds plus another 2s delay
            // to make sure...
            Thread.sleep(1000L
                    + ((long)((simDuration*1000.0)/ACCELERATION_FACTOR))
                    + 2000L);
            HEM_Report r = (HEM_Report) se.getFinalReport();
            System.out.println(r.printout(""));
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }
}
// -----------------------------------------------------------------------------
