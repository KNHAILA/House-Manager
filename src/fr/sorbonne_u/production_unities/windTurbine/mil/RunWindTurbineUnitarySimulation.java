package fr.sorbonne_u.production_unities.windTurbine.mil;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterCoupledModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterElectricityModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterExternalTemperatureModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterTemperatureModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterUnitTesterModel;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
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
import fr.sorbonne_u.production_unities.windTurbine.mil.events.DoNotUseWindTurbine;
import fr.sorbonne_u.production_unities.windTurbine.mil.events.UseWindTurbine;

public class RunWindTurbineUnitarySimulation
{
    public static void	main(String[] args)
    {
        try {
        
            Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

         
            atomicModelDescriptors.put(
            		WindTurbineElectricityModel.URI,
                    AtomicHIOA_Descriptor.create(
                    		WindTurbineElectricityModel.class,
                    		WindTurbineElectricityModel.URI,
                            TimeUnit.SECONDS,
                            null,
                            SimulationEngineCreationMode.ATOMIC_ENGINE));
            // for atomic model, we use an AtomicModelDescriptor
            atomicModelDescriptors.put(
            		WindTurbineUserModel.URI,
                    AtomicModelDescriptor.create(
                    		WindTurbineUserModel.class,
                    		WindTurbineUserModel.URI,
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

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<String>();
            submodels.add(WindTurbineElectricityModel.URI);
            submodels.add(WindTurbineUserModel.URI);
            submodels.add(WindSpeedModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource,EventSink[]> connections =
                    new HashMap<EventSource,EventSink[]>();

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
            Map<VariableSource,VariableSink[]> bindings = new HashMap<VariableSource,VariableSink[]>();
            
            bindings.put(new VariableSource("windSpeed",
            		Double.class,
				WindSpeedModel.URI),
			 new VariableSink[] {
					 new VariableSink("windSpeed",
							 		  Double.class,
							 		 WindTurbineElectricityModel.URI)
			 });           

            // coupled model descriptor    
            coupledModelDescriptors.put(
            		WindTurbineCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							WindTurbineCoupledModel.class,
							WindTurbineCoupledModel.URI,
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
                    		WindTurbineCoupledModel.URI,
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

