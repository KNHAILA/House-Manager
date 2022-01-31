package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.*;

/**
 * The class <code>RunHeaterUnitarySimulation</code> creates a simulator
 * for the heater and then runs a typical simulation.
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
public class RunMiniHydroelectricDamUnitarySimulation
{
    public static void	main(String[] args)
    {
        try {
            Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                    new HashMap<>();

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

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String,CoupledModelDescriptor> coupledModelDescriptors =
                    new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<String>();
            submodels.add(MiniHydroelectricDamElectricityModel.URI);
            submodels.add(MiniHydroelectricDamUnitTesterModel.URI);
            submodels.add(WaterVolumeModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource,EventSink[]> connections =
                    new HashMap<EventSource,EventSink[]>();

            connections.put(
                    new EventSource(MiniHydroelectricDamUnitTesterModel.URI, DoNotUseMiniHydroelectricDam.class),
                    new EventSink[] {
                            new EventSink(MiniHydroelectricDamElectricityModel.URI,
                            		DoNotUseMiniHydroelectricDam.class)
                    });
        
            
            connections.put(
					new EventSource(MiniHydroelectricDamUnitTesterModel.URI, UseMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricityModel.URI,
									UseMiniHydroelectricDam.class),
							new EventSink(WaterVolumeModel.URI,
									UseMiniHydroelectricDam.class)
					});
            
            connections.put(
					new EventSource(MiniHydroelectricDamUnitTesterModel.URI, StartMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricityModel.URI,
									StartMiniHydroelectricDam.class),
							new EventSink(WaterVolumeModel.URI,
									StartMiniHydroelectricDam.class)
					});
            
            connections.put(
					new EventSource(MiniHydroelectricDamUnitTesterModel.URI, StopMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricityModel.URI,
									StopMiniHydroelectricDam.class),
							new EventSink(WaterVolumeModel.URI,
									StopMiniHydroelectricDam.class)
					});

            
            Map<VariableSource,VariableSink[]> bindings = new HashMap<VariableSource,VariableSink[]>();
            
            bindings.put(new VariableSource("waterVolume",
            		Double.class,
            		WaterVolumeModel.URI),
			 new VariableSink[] {
					 new VariableSink("waterVolume",
							 		  Double.class,
							 		 MiniHydroelectricDamElectricityModel.URI)
			 });           

            // coupled model descriptor    
            coupledModelDescriptors.put(
            		MiniHydroelectricDamCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							MiniHydroelectricDamCoupledModel.class,
							MiniHydroelectricDamCoupledModel.URI,
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
                    		MiniHydroelectricDamCoupledModel.URI,
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

