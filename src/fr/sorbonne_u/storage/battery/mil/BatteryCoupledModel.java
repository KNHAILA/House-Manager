package fr.sorbonne_u.storage.battery.mil;

 import java.util.Map;
 import java.util.concurrent.TimeUnit;
 import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
 import fr.sorbonne_u.devs_simulation.models.CoupledModel;
 import fr.sorbonne_u.devs_simulation.models.events.EventI;
 import fr.sorbonne_u.devs_simulation.models.events.EventSink;
 import fr.sorbonne_u.devs_simulation.models.events.EventSource;
 import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
 import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class BatteryCoupledModel extends CoupledModel
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String	URI = BatteryCoupledModel.class.
            getSimpleName();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public	BatteryCoupledModel(
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
}
// -----------------------------------------------------------------------------


