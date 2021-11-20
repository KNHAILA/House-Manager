package fr.sorbonne_u;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_CoupledModel.HEM_Report;

//Fan
import fr.sorbonne_u.components.fan.mil.FanElectricityModel;
import fr.sorbonne_u.components.fan.mil.FanUserModel;
import fr.sorbonne_u.components.fan.mil.events.*;

//vacuumCleaner
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerUserModel;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.*;

//refrigerator
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorExternalTemperatureModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorTemperatureModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorUnitTesterModel;
import fr.sorbonne_u.components.refrigerator.mil.events.*;

//Washing machine
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineExternalTemperatureModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineElectricityModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineTemperatureModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineUnitTesterModel;
import fr.sorbonne_u.components.washingMachine.mil.events.*;

//WaterHeater
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterExternalTemperatureModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterElectricityModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterTemperatureModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterUnitTesterModel;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOnWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOffWaterHeater;

//Meter
import fr.sorbonne_u.components.meter.mil.ElectricMeterElectricityModel;

//Battery
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;
import fr.sorbonne_u.storage.battery.mil.BatteryUserModel;
import fr.sorbonne_u.storage.battery.mil.events.*;

//devs_simulation
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamElectricityModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamUserModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.WaterSpeedModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.DoNotMiniHydroelectricDam;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.UseMiniHydroelectricDam;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindSpeedModel;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineElectricityModel;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineUserModel;
import fr.sorbonne_u.production_unities.windTurbine.mil.events.DoNotUseWindTurbine;
import fr.sorbonne_u.production_unities.windTurbine.mil.events.UseWindTurbine;

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

            // fan models
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

            //Vacuum Claener Model
            atomicModelDescriptors.put(
                    VacuumCleanerElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            VacuumCleanerElectricityModel.class,
                            VacuumCleanerElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    VacuumCleanerUserModel.URI,
                    RTAtomicModelDescriptor.create(
                            VacuumCleanerUserModel.class,
                            VacuumCleanerUserModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));

            //the Water heater models
            atomicModelDescriptors.put(
                    WaterHeaterElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            WaterHeaterElectricityModel.class,
                            WaterHeaterElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    WaterHeaterTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            WaterHeaterTemperatureModel.class,
                            WaterHeaterTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    WaterHeaterExternalTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            WaterHeaterExternalTemperatureModel.class,
                            WaterHeaterExternalTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    WaterHeaterUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            WaterHeaterUnitTesterModel.class,
                            WaterHeaterUnitTesterModel.URI,
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

            // the WashingMachine models
            atomicModelDescriptors.put(
                    WashingMachineElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            WashingMachineElectricityModel.class,
                            WashingMachineElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    WashingMachineTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            WashingMachineTemperatureModel.class,
                            WashingMachineTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    WashingMachineExternalTemperatureModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            WashingMachineExternalTemperatureModel.class,
                            WashingMachineExternalTemperatureModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    WashingMachineUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            WashingMachineUnitTesterModel.class,
                            WashingMachineUnitTesterModel.URI,
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

            //Battery
            atomicModelDescriptors.put(
                    BatteryElectricityModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            BatteryElectricityModel.class,
                            BatteryElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    BatteryUserModel.URI,
                    RTAtomicModelDescriptor.create(
                            BatteryUserModel.class,
                            BatteryUserModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            // Production Unity : Mini Hydro Electric Dam
            atomicModelDescriptors.put(
            		MiniHydroelectricDamElectricityModel.URI,
            		RTAtomicHIOA_Descriptor.create(
                    		MiniHydroelectricDamElectricityModel.class,
                    		MiniHydroelectricDamElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            
            atomicModelDescriptors.put(
            		MiniHydroelectricDamUserModel.URI,
                    RTAtomicModelDescriptor.create(
                    		MiniHydroelectricDamUserModel.class,
                    		MiniHydroelectricDamUserModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            
            atomicModelDescriptors.put(
            		WaterSpeedModel.URI,
            		RTAtomicHIOA_Descriptor.create(
							WaterSpeedModel.class,
							WaterSpeedModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));

            
            // Production Unity : Wind Turbine
            
            atomicModelDescriptors.put(
            		WindTurbineElectricityModel.URI,
            		RTAtomicHIOA_Descriptor.create(
            				WindTurbineElectricityModel.class,
            				WindTurbineElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            
            atomicModelDescriptors.put(
            		WindTurbineUserModel.URI,
                    RTAtomicModelDescriptor.create(
                    		WindTurbineUserModel.class,
                    		WindTurbineUserModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
                            ACCELERATION_FACTOR));
            
            atomicModelDescriptors.put(
            		WindSpeedModel.URI,
            		RTAtomicHIOA_Descriptor.create(
            				WindSpeedModel.class,
            				WindSpeedModel.URI,
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
            //fan
            submodels.add(FanElectricityModel.URI);
            submodels.add(FanUserModel.URI);

            //Vacuum Cleaner
            submodels.add(VacuumCleanerElectricityModel.URI);
            submodels.add(VacuumCleanerUserModel.URI);

            //WaterHeater
            submodels.add(WaterHeaterElectricityModel.URI);
            submodels.add(WaterHeaterTemperatureModel.URI);
            submodels.add(WaterHeaterExternalTemperatureModel.URI);
            submodels.add(WaterHeaterUnitTesterModel.URI);

            //Refrigerator
            submodels.add(RefrigeratorElectricityModel.URI);
            submodels.add(RefrigeratorTemperatureModel.URI);
            submodels.add(RefrigeratorExternalTemperatureModel.URI);
            submodels.add(RefrigeratorUnitTesterModel.URI);

            //washing machine
            submodels.add(WashingMachineElectricityModel.URI);
            submodels.add(WashingMachineTemperatureModel.URI);
            submodels.add(WashingMachineExternalTemperatureModel.URI);
            submodels.add(WashingMachineUnitTesterModel.URI);

            //meter
            submodels.add(ElectricMeterElectricityModel.URI);

            //Battery
            submodels.add(BatteryElectricityModel.URI);
            submodels.add(BatteryUserModel.URI);
            
            // Productions unities 
            
            submodels.add(MiniHydroelectricDamElectricityModel.URI);
            submodels.add(WaterSpeedModel.URI);
            submodels.add(MiniHydroelectricDamUserModel.URI);
            
            submodels.add(WindTurbineElectricityModel.URI);
            submodels.add(WindSpeedModel.URI);
            submodels.add(WindTurbineUserModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource,EventSink[]> connections =
                    new HashMap<EventSource,EventSink[]>();

            //Fan
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

            //Vacuum Cleaner
            connections.put(
                    new EventSource(VacuumCleanerUserModel.URI, SwitchOnVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerElectricityModel.URI,
                                    SwitchOnVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerUserModel.URI, SwitchOffVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerElectricityModel.URI,
                                    SwitchOffVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerUserModel.URI, SetHighVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerElectricityModel.URI,
                                    SetHighVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerUserModel.URI, SetLowVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerElectricityModel.URI,
                                    SetLowVacuumCleaner.class)
                    });

            //Water heater
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

            //washing machine
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
                                    fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater.class)
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
                    new EventSource(WashingMachineUnitTesterModel.URI, fr.sorbonne_u.components.washingMachine.mil.events.HeatWater.class),
                    new EventSink[] {
                            new EventSink(WashingMachineElectricityModel.URI,
                                    fr.sorbonne_u.components.washingMachine.mil.events.HeatWater.class),
                            new EventSink(WashingMachineTemperatureModel.URI,
                                    fr.sorbonne_u.components.washingMachine.mil.events.HeatWater.class)
                    });
            connections.put(
                    new EventSource(WashingMachineUnitTesterModel.URI, fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater.class),
                    new EventSink[] {
                            new EventSink(WashingMachineElectricityModel.URI,
                                    fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater.class),
                            new EventSink(WashingMachineTemperatureModel.URI,
                                    fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater.class)
                    });

            //Battery
            connections.put(
                    new EventSource(BatteryUserModel.URI, UseBattery.class),
                    new EventSink[] {
                            new EventSink(BatteryElectricityModel.URI,
                                    UseBattery.class)
                    });
            connections.put(
                    new EventSource(BatteryUserModel.URI, ChargeBattery.class),
                    new EventSink[] {
                            new EventSink(BatteryElectricityModel.URI,
                                    ChargeBattery.class)
                    });

            // Productions unities
            connections.put(
                    new EventSource(MiniHydroelectricDamUserModel.URI, DoNotMiniHydroelectricDam.class),
                    new EventSink[] {
                            new EventSink(MiniHydroelectricDamElectricityModel.URI,
                            		DoNotMiniHydroelectricDam.class)
                    });
        
            
            connections.put(
					new EventSource(MiniHydroelectricDamUserModel.URI, UseMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricityModel.URI,
									UseMiniHydroelectricDam.class),
							new EventSink(WaterSpeedModel.URI,
									UseMiniHydroelectricDam.class)
					});
            
            connections.put(
                    new EventSource(WindTurbineUserModel.URI, DoNotUseWindTurbine.class),
                    new EventSink[] {
                            new EventSink(WindTurbineElectricityModel.URI,
                            		DoNotUseWindTurbine.class)
                    });
            
            connections.put(
					new EventSource(WindTurbineUserModel.URI, UseWindTurbine.class),
					new EventSink[] {
							new EventSink(WindTurbineElectricityModel.URI,
									UseWindTurbine.class),
							new EventSink(WindSpeedModel.URI,
									UseWindTurbine.class)
					});

            // variable bindings between exporting and importing models
            Map<VariableSource,VariableSink[]> bindings =
                    new HashMap<VariableSource,VariableSink[]>();

            // bindings among water heater's models
            bindings.put(
                    new VariableSource("externalWaterTemperature",
                            Double.class,
                            WaterHeaterExternalTemperatureModel.URI),
                    new VariableSink[] {
                            new VariableSink("externalWaterTemperature",
                                    Double.class,
                                    WaterHeaterTemperatureModel.URI)
                    });

            // bindings among Refrigerator's models
            bindings.put(
                    new VariableSource("externalTemperature",
                            Double.class,
                            RefrigeratorExternalTemperatureModel.URI),
                    new VariableSink[] {
                            new VariableSink("externalTemperature",
                                    Double.class,
                                    RefrigeratorTemperatureModel.URI)
                    });

            // bindings among washing machine's models
            bindings.put(new VariableSource("externalTemperature",
                            Double.class,
                            WashingMachineExternalTemperatureModel.URI),
                    new VariableSink[] {
                            new VariableSink("externalWaterTemperature",
                                    Double.class,
                                    WashingMachineTemperatureModel.URI)
                    });


            // bindings between components models to the electric meter model
            // Fan
            bindings.put(
                    new VariableSource("currentIntensity",
                            Double.class,
                            FanElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentFanIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });

            // Vacuum Cleaner
            bindings.put(
                    new VariableSource("currentIntensity",
                            Double.class,
                            VacuumCleanerElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentVacuumCleanerIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });

            //Water heater
            bindings.put(
                    new VariableSource("currentIntensity",
                            Double.class,
                            WaterHeaterElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentWaterHeaterIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });

            //Refrigerator
            bindings.put(
                    new VariableSource("currentIntensity",
                            Double.class,
                            RefrigeratorElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentRefrigeratorIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });

            //Washing machine
            bindings.put(
                    new VariableSource("currentIntensity",
                            Double.class,
                            WashingMachineElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentWashingMachineIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });

            //Battery
            bindings.put(
                    new VariableSource("currentIntensity_consumption",
                            Double.class,
                            BatteryElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentBatteryIntensity_consumption",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });
            bindings.put(
                    new VariableSource("currentIntensity_production",
                            Double.class,
                            BatteryElectricityModel.URI),
                    new VariableSink[] {
                            new VariableSink("currentBatteryIntensity_production",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    });
            
            // Productions unities
            bindings.put(new VariableSource("waterSpeed",
            		Double.class,
				WaterSpeedModel.URI),
			 new VariableSink[] {
					 new VariableSink("waterSpeed",
							 		  Double.class,
							 		 MiniHydroelectricDamElectricityModel.URI)
			 });    
            
            bindings.put(new VariableSource("windSpeed",
            		Double.class,
				WindSpeedModel.URI),
			 new VariableSink[] {
					 new VariableSink("windSpeed",
							 		  Double.class,
							 		 WindTurbineElectricityModel.URI)
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
            simParams.put(RefrigeratorElectricityModel.NOT_freezing_POWER_RUNPNAME,
                    0.0);
            simParams.put(RefrigeratorElectricityModel.freezing_POWER_RUNPNAME,
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
