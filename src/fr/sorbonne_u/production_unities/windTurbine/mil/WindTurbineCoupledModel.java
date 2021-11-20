package fr.sorbonne_u.production_unities.windTurbine.mil;

 import java.util.Map;
 import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
 import fr.sorbonne_u.devs_simulation.models.CoupledModel;
 import fr.sorbonne_u.devs_simulation.models.events.EventI;
 import fr.sorbonne_u.devs_simulation.models.events.EventSink;
 import fr.sorbonne_u.devs_simulation.models.events.EventSource;
 import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
 import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class WindTurbineCoupledModel extends CoupledModel
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String	URI = WindTurbineCoupledModel.class.
            getSimpleName();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

   
    public	WindTurbineCoupledModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine,
            ModelDescriptionI[] submodels,
            Map<Class<? extends EventI>,EventSink[]> imported,
            Map<Class<? extends EventI>,ReexportedEvent> reexported,
            Map<EventSource, EventSink[]> connections
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine, submodels,
                imported, reexported, connections);
    }
    
    public WindTurbineCoupledModel(
    		String uri,
    		TimeUnit simulatedTimeUnit,
    		SimulatorI simulationEngine,
    		ModelDescriptionI[] submodels,
    		Map<Class<? extends EventI>, EventSink[]> imported,
    		Map<Class<? extends EventI>, ReexportedEvent> reexported,
    		Map<EventSource, EventSink[]> connections,
    		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
    		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
    		Map<VariableSource, VariableSink[]> bindings
    		) throws Exception
    	{
    		super(uri, simulatedTimeUnit, simulationEngine, submodels,
    			  imported, reexported, connections,
    			  importedVars, reexportedVars, bindings);
    	}
}
// -----------------------------------------------------------------------------


