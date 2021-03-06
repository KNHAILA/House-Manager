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

//Vacuum cleaner
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerUserModel;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.*;

//refrigerator
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorExternalTemperatureModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorTemperatureModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorUnitTesterModel;
import fr.sorbonne_u.components.refrigerator.mil.events.*;

//WaterHeater
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterExternalTemperatureModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterElectricityModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterTemperatureModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterUnitTesterModel;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOnWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOffWaterHeater;

//Washing machine
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineExternalTemperatureModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineElectricityModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineTemperatureModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineUnitTesterModel;
import fr.sorbonne_u.components.washingMachine.mil.events.*;

//meter
import fr.sorbonne_u.meter.mil.ElectricMeterElectricityModel;

//Battery
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;
import fr.sorbonne_u.storage.battery.mil.BatteryPercentageModel;
import fr.sorbonne_u.storage.battery.mil.BatteryUnitTesterModel;
import fr.sorbonne_u.storage.battery.mil.events.*;

//miniHydroelectricDam

import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamElectricityModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamUnitTesterModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.WaterVolumeModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.*;


//Wind turbine
import fr.sorbonne_u.production_unities.windTurbine.mil.WindSpeedModel;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineElectricityModel;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineUnitTesterModel;
import fr.sorbonne_u.production_unities.windTurbine.mil.events.*;

//devs_simulation
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
 * The class <code>RunHEM_Simulation</code> creates the simulator for the
 * household energy management example and then runs a typical simulation.
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
 * {@code doStandAloneSimulation}. Notice the use of the method
 * {@code setSimulationRunParameters} to initialise some parameters of
 * the simulation defined in the different models. This method is implemented
 * to traverse all of the models, hence each one can get its own parameters by
 * carefully defining unique names for them. Also, it shows how to get the
 * simulation reports from the models after the simulation run.
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
 * <p>Created on : 2021-09-24</p>
 *
 *  @authors	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 *              <a href="maedeh.daemi@etu.sorbonne-universite.fr">DAEMI Maedeh</a>
 */
public class			RunHEM_Simulation
{
    public static void	main(String[] args)
    {
        try {
            // map that will contain the atomic model descriptors to construct
            // the simulation architecture
            Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

            // atomic HIOA models require AtomicHIOA_Descriptor while
            // atomic models require AtomicModelDescriptor

            // fan models
            atomicModelDescriptors.put(
                    FanElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            FanElectricityModel.class,
                            FanElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            atomicModelDescriptors.put(
                    FanUserModel.URI,
                    AtomicModelDescriptor.create(
                            FanUserModel.class,
                            FanUserModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));

            // Vacuum Cleaner models
            atomicModelDescriptors.put(
                    VacuumCleanerElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            VacuumCleanerElectricityModel.class,
                            VacuumCleanerElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            atomicModelDescriptors.put(
                    VacuumCleanerUserModel.URI,
                    AtomicModelDescriptor.create(
                            VacuumCleanerUserModel.class,
                            VacuumCleanerUserModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));

            //Water heater
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

            
            // the Refrigerator models
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
            
            atomicModelDescriptors.put(
                    RefrigeratorUnitTesterModel.URI,
                    AtomicModelDescriptor.create(
                            RefrigeratorUnitTesterModel.class,
                            RefrigeratorUnitTesterModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            
            // the washing machine model
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
                            

            // the electric meter model
            atomicModelDescriptors.put(
                    ElectricMeterElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                            ElectricMeterElectricityModel.class,
                            ElectricMeterElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));

            //The battery model
            atomicModelDescriptors.put(
            		BatteryElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                    		BatteryElectricityModel.class,
                    		BatteryElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            
            atomicModelDescriptors.put(
                    BatteryUnitTesterModel.URI,
                    AtomicModelDescriptor.create(
                            BatteryUnitTesterModel.class,
                            BatteryUnitTesterModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            
            atomicModelDescriptors.put(
            		BatteryPercentageModel.URI,
                    AtomicHIOA_Descriptor.create(
                    		BatteryPercentageModel.class,
                    		BatteryPercentageModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            
            
            // Production Unit: Mini Hydro Electric Dam
            atomicModelDescriptors.put(
            		MiniHydroelectricDamElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							MiniHydroelectricDamElectricityModel.class,
							MiniHydroelectricDamElectricityModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					WaterVolumeModel.URI,
					AtomicHIOA_Descriptor.create(
							WaterVolumeModel.class,
							WaterVolumeModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

			atomicModelDescriptors.put(
					MiniHydroelectricDamUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							MiniHydroelectricDamUnitTesterModel.class,
							MiniHydroelectricDamUnitTesterModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

            
            // Production Unit: Wind Turbine
            atomicModelDescriptors.put(
					WindTurbineElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							WindTurbineElectricityModel.class,
							WindTurbineElectricityModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					WindSpeedModel.URI,
					AtomicHIOA_Descriptor.create(
							WindSpeedModel.class,
							WindSpeedModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));

			atomicModelDescriptors.put(
					WindTurbineUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							WindTurbineUnitTesterModel.class,
							WindTurbineUnitTesterModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE));
			

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            
            //Fan
            Set<String> submodels = new HashSet<String>();
            submodels.add(FanElectricityModel.URI);
            submodels.add(FanUserModel.URI);

            //VacuumCleaner
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

            //battery
            submodels.add(BatteryElectricityModel.URI);
            submodels.add(BatteryPercentageModel.URI);
            submodels.add(BatteryUnitTesterModel.URI);
            
            // MiniHydroelectricDam
            submodels.add(MiniHydroelectricDamElectricityModel.URI);
            submodels.add(WaterVolumeModel.URI);
            submodels.add(MiniHydroelectricDamUnitTesterModel.URI);
            
            
            // WindTurbine
			submodels.add(WindTurbineElectricityModel.URI);
			submodels.add(WindSpeedModel.URI);
			submodels.add(WindTurbineUnitTesterModel.URI);
            
            
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
                                    Wash.class)
                    });
            connections.put(
                    new EventSource(WashingMachineUnitTesterModel.URI,
                            Rinse.class),
                    new EventSink[] {
                            new EventSink(WashingMachineElectricityModel.URI,
                                    Rinse.class),
                            new EventSink(WashingMachineTemperatureModel.URI,
                            		Rinse.class)
                    });
            connections.put(
                    new EventSource(WashingMachineUnitTesterModel.URI, Spin.class),
                    new EventSink[] {
                            new EventSink(WashingMachineElectricityModel.URI,
                                    Spin.class),
                            new EventSink(WashingMachineTemperatureModel.URI,
                                    Spin.class)
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
                    new EventSource(BatteryUnitTesterModel.URI,
                    		UseBattery.class),
                    new EventSink[] {
                            new EventSink(BatteryElectricityModel.URI,
                            		UseBattery.class),
                            new EventSink(BatteryPercentageModel.URI,
                            		UseBattery.class)
                    });
            
            connections.put(
                    new EventSource(BatteryUnitTesterModel.URI,
                    		DoNotUseBattery.class),
                    new EventSink[] {
                            new EventSink(BatteryElectricityModel.URI,
                            		DoNotUseBattery.class),
                            new EventSink(BatteryPercentageModel.URI,
                            		DoNotUseBattery.class)
                    });
            
            connections.put(
                    new EventSource(BatteryUnitTesterModel.URI,
                    		ChargeBattery.class),
                    new EventSink[] {
                            new EventSink(BatteryElectricityModel.URI,
                            		ChargeBattery.class),
                            new EventSink(BatteryPercentageModel.URI,
                            		ChargeBattery.class)
                    });
            
            connections.put(
                    new EventSource(BatteryUnitTesterModel.URI,
                    		DoNotChargeBattery.class),
                    new EventSink[] {
                            new EventSink(BatteryElectricityModel.URI,
                            		DoNotChargeBattery.class),
                            new EventSink(BatteryPercentageModel.URI,
                            		DoNotChargeBattery.class)
                    });
            
            // MiniHydroelectricDam
            connections.put(
					new EventSource(MiniHydroelectricDamUnitTesterModel.URI, StartMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricityModel.URI,
									StartMiniHydroelectricDam.class)
					});
            
            connections.put(
					new EventSource(MiniHydroelectricDamUnitTesterModel.URI, StopMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricityModel.URI,
									StopMiniHydroelectricDam.class)
					});
            
         // Wind Turbine
        	connections.put(
					new EventSource(WindTurbineUnitTesterModel.URI,
									StartWindTurbine.class),
					new EventSink[] {
							new EventSink(WindTurbineElectricityModel.URI,
										  StartWindTurbine.class),							
					});
			connections.put(
					new EventSource(WindTurbineUnitTesterModel.URI,
									StopWindTurbine.class),
					new EventSink[] {
							new EventSink(WindTurbineElectricityModel.URI,
										  StopWindTurbine.class)
					});


            // variable bindings between exporting and importing models
            Map<VariableSource,VariableSink[]> bindings =
                    new HashMap<VariableSource,VariableSink[]>();

                    
            // bindings among water heater models
            VariableSource source1 =
                    new VariableSource("externalTemperature",
                            Double.class,
                            WaterHeaterExternalTemperatureModel.URI);
            VariableSink[] sinks1 =
                    new VariableSink[] {
                            new VariableSink("externalTemperature",
                                    Double.class,
                                    WaterHeaterTemperatureModel.URI)
                    };
            bindings.put(source1, sinks1);

            
            // bindings among Refrigerator models
            VariableSource source2 =
                    new VariableSource("externalTemperature",
                            Double.class,
                            RefrigeratorExternalTemperatureModel.URI);
            VariableSink[] sinks2 =
                    new VariableSink[] {
                            new VariableSink("externalTemperature",
                                    Double.class,
                                    RefrigeratorTemperatureModel.URI)
                    };
            bindings.put(source2, sinks2);

            // bindings among washing machine models
            VariableSource source3 =
                    new VariableSource("externalTemperature",
                            Double.class,
                            WashingMachineExternalTemperatureModel.URI);
            VariableSink[] sinks3 =
                    new VariableSink[] {
                            new VariableSink("externalTemperature",
                                    Double.class,
                                    WashingMachineTemperatureModel.URI)
                    };
            bindings.put(source3, sinks3);
            
            // bindings among battery models Battery
            VariableSource source4 =
                    new VariableSource("electricityState",
                    		BatteryElectricityModel.State.class,
                    		BatteryElectricityModel.URI);
            VariableSink[] sinks4 =
                    new VariableSink[] {
                            new VariableSink("electricityState",
                            		BatteryElectricityModel.State.class,
                            		BatteryPercentageModel.URI)
                    };
            bindings.put(source4, sinks4);
            
            
            // bindings among WindTurbine models
            VariableSource source5 =
                    new VariableSource("currentWindSpeed",
                            Double.class,
                            WindSpeedModel.URI);
            VariableSink[] sinks5 =
                    new VariableSink[] {
                            new VariableSink("currentWindSpeed",
                                    Double.class,
                                    WindTurbineElectricityModel.URI)
                    };
            bindings.put(source5, sinks5);
            

            // bindings among  MiniHydroelectricDam models
            VariableSource source6 =
                    new VariableSource("waterVolume",
                            Double.class,
                            WaterVolumeModel.URI);
            VariableSink[] sinks6 =
                    new VariableSink[] {
                            new VariableSink("waterVolume",
                                    Double.class,
                                    MiniHydroelectricDamElectricityModel.URI)
                    };
            bindings.put(source6, sinks6);
            

            // bindings between components models to the electric meter model
            //Fan
            VariableSource source7 =
                    new VariableSource("currentIntensity",
                            Double.class,
                            FanElectricityModel.URI);
            VariableSink[] sinks7 =
                    new VariableSink[] {
                            new VariableSink("currentFanIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source7, sinks7);

            // Vacuum Cleaner
            VariableSource source8 =
                    new VariableSource("currentIntensity",
                            Double.class,
                            VacuumCleanerElectricityModel.URI);
            VariableSink[] sinks8 =
                    new VariableSink[] {
                            new VariableSink("currentVacuumCleanerIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source8, sinks8);

            
            //water heater
            VariableSource source9 =
                    new VariableSource("currentIntensity",
                            Double.class,
                            WaterHeaterElectricityModel.URI);
            VariableSink[] sinks9 =
                    new VariableSink[] {
                            new VariableSink("currentWaterHeaterIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source9, sinks9);

            //Refrigerator
            VariableSource source10 =
                    new VariableSource("currentIntensity",
                            Double.class,
                            RefrigeratorElectricityModel.URI);
            VariableSink[] sinks10 =
                    new VariableSink[] {
                            new VariableSink("currentRefrigeratorIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source10, sinks10);
            
            //washing machine
            VariableSource source11 =
                    new VariableSource("currentIntensity",
                            Double.class,
                            WashingMachineElectricityModel.URI);
            VariableSink[] sinks11 =
                    new VariableSink[] {
                            new VariableSink("currentWashingMachineIntensity",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source11, sinks11);
            

            //Battery
            VariableSource source12 =
                    new VariableSource("currentIntensity_consumption",
                            Double.class,
                            BatteryElectricityModel.URI);
            VariableSink[] sinks12 =
                    new VariableSink[] {
                            new VariableSink("currentBatteryIntensity_consumption",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source12, sinks12);

            VariableSource source13 =
                    new VariableSource("currentIntensity_production",
                            Double.class,
                            BatteryElectricityModel.URI);
            VariableSink[] sinks13 =
                    new VariableSink[] {
                            new VariableSink("currentBatteryIntensity_production",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source13, sinks13);

            //Wind turbine
            VariableSource source14 =
                    new VariableSource("currentIntensity_production",
                            Double.class,
                            WindTurbineElectricityModel.URI);
            VariableSink[] sinks14 =
                    new VariableSink[] {
                            new VariableSink("currentWindTurbineIntensity_production",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source14, sinks14);
            
            //MiniHydroelectricDam
            VariableSource source15 =
                    new VariableSource("currentIntensity_production",
                            Double.class,
                            MiniHydroelectricDamElectricityModel.URI);
            VariableSink[] sinks15 =
                    new VariableSink[] {
                            new VariableSink("currentMiniHydroelectricDamIntensity_production",
                                    Double.class,
                                    ElectricMeterElectricityModel.URI)
                    };
            bindings.put(source15, sinks15);
            
            // coupled model descriptor: an HIOA requires a
            // CoupledHIOA_Descriptor
            coupledModelDescriptors.put(
                    HEM_CoupledModel.URI,
                    new CoupledHIOA_Descriptor(
                            HEM_CoupledModel.class,
                            HEM_CoupledModel.URI,
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
                            HEM_CoupledModel.URI,
                            atomicModelDescriptors,
                            coupledModelDescriptors,
                            TimeUnit.SECONDS);

            // create the simulator from the simulation architecture
            SimulationEngine se = architecture.constructSimulator();

            // Optional: how to use simulation run parameters to modify
            // the behaviour of the models from runs to runs
          /*  Map<String, Object> simParams = new HashMap<String, Object>();
            simParams.put(
                    FanElectricityModel.LOW_MODE_CONSUMPTION_RUNPNAME,
                    1320.0);
            simParams.put(
                    FanElectricityModel.HIGH_MODE_CONSUMPTION_RUNPNAME,
                    2200.0);
            simParams.put(FanUserModel.STEP_MEAN_DURATION_RUNPNAME, 0.5);
            simParams.put(RefrigeratorElectricityModel.NOT_freezing_POWER_RUNPNAME,
                    0.0);
            simParams.put(RefrigeratorElectricityModel.freezing_POWER_RUNPNAME,
                    4400.0);

            se.setSimulationRunParameters(simParams);
           */

            // this add additional time at each simulation step in
            // standard simulations (useful for debugging)
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
            // run a simulation with the simulation beginning at 0.0 and
            // ending at 10.0
            se.doStandAloneSimulation(0.0, 10.0);

            // Optional: simulation report
            HEM_Report r = (HEM_Report) se.getFinalReport();
            System.out.println(r.printout(""));
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }
}
// -----------------------------------------------------------------------------
