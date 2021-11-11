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
/*import fr.sorbonne_u.components.cyphy.hem2021e2.equipments.heater.mil.ExternalTemperatureModel;
import fr.sorbonne_u.components.cyphy.hem2021e2.equipments.heater.mil.HeaterElectricityModel;
import fr.sorbonne_u.components.cyphy.hem2021e2.equipments.heater.mil.HeaterTemperatureModel;
import fr.sorbonne_u.components.cyphy.hem2021e2.equipments.heater.mil.HeaterUnitTesterModel;
import fr.sorbonne_u.components.cyphy.hem2021e2.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.cyphy.hem2021e2.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.cyphy.hem2021e2.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.cyphy.hem2021e2.equipments.heater.mil.events.SwitchOnHeater; */

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

public class RunHEM_RT_Simulation
{
    /** acceleration factor for the real time simulation; with a factor 2.0,
     *  the simulation runs two times faster than real time i.e., a run that
     *  is supposed to take 10 seconds in real time will take 5 seconds to
     *  execute.															*/
    protected static final double ACCELERATION_FACTOR = 1.0;

    public static void	main(String[] args)
    {

        //System.out.println("salam");

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

            // the heater models
            /*
            atomicModelDescriptors.put(
                    HeaterElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            HeaterElectricityModel.class,
                            HeaterElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    HeaterTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            HeaterTemperatureModel.class,
                            HeaterTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    ExternalTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            ExternalTemperatureModel.class,
                            ExternalTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    HeaterUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            HeaterUnitTesterModel.class,
                            HeaterUnitTesterModel.URI,
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
*/
            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<String>();
            submodels.add(FanElectricityModel.URI);
        /*    submodels.add(FanUserModel.URI);
            submodels.add(HeaterElectricityModel.URI);
            submodels.add(HeaterTemperatureModel.URI);
            submodels.add(ExternalTemperatureModel.URI);
            submodels.add(HeaterUnitTesterModel.URI);
            submodels.add(ElectricMeterElectricityModel.URI);

         */

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
/*
            connections.put(
                    new EventSource(HeaterUnitTesterModel.URI,
                            SwitchOnHeater.class),
                    new EventSink[] {
                            new EventSink(HeaterElectricityModel.URI,
                                    SwitchOnHeater.class)
                    });
            connections.put(
                    new EventSource(HeaterUnitTesterModel.URI,
                            SwitchOffHeater.class),
                    new EventSink[] {
                            new EventSink(HeaterElectricityModel.URI,
                                    SwitchOffHeater.class)
                    });
            connections.put(
                    new EventSource(HeaterUnitTesterModel.URI, Heat.class),
                    new EventSink[] {
                            new EventSink(HeaterElectricityModel.URI,
                                    Heat.class),
                            new EventSink(HeaterTemperatureModel.URI,
                                    Heat.class)
                    });
            connections.put(
                    new EventSource(HeaterUnitTesterModel.URI, DoNotHeat.class),
                    new EventSink[] {
                            new EventSink(HeaterElectricityModel.URI,
                                    DoNotHeat.class),
                            new EventSink(HeaterTemperatureModel.URI,
                                    DoNotHeat.class)
                    });
*/
            // variable bindings between exporting and importing models
            Map<VariableSource,VariableSink[]> bindings =
                    new HashMap<VariableSource,VariableSink[]>();
/*
            // bindings among heater models
            bindings.put(
                    new VariableSource("externalTemperature",
                            Double.class,
                            ExternalTemperatureModel.URI),
                    new VariableSink[] {
                            new VariableSink("externalTemperature",
                                    Double.class,
                                    HeaterTemperatureModel.URI)
                    });

 */

            // bindings between hair dryer and heater models to the electric
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
            /*
            bindings.put(
                    new VariableSource("currentIntensity",
                            Double.class,
                            HeaterElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentHeaterIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });
*/
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
            Map<String, Object> simParams = new HashMap<String, Object>();
            simParams.put(
                    FanElectricityModel.LOW_MODE_CONSUMPTION_RUNPNAME,
                    1320.0);
            simParams.put(
                    FanElectricityModel.HIGH_MODE_CONSUMPTION_RUNPNAME,
                    2200.0);
            simParams.put(FanUserModel.STEP_MEAN_DURATION_RUNPNAME, 2.0);
    /*        simParams.put(HeaterElectricityModel.NOT_HEATING_POWER_RUNPNAME,
                    0.0);
            simParams.put(HeaterElectricityModel.HEATING_POWER_RUNPNAME,
                    4400.0);
     */
            se.setSimulationRunParameters(simParams);

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

