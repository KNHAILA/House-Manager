package fr.sorbonne_u.components.refrigerator.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.refrigerator.mil.events.*;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(exported = {CloseRefrigeratorDoor.class,
        Freezing.class,
        OffRefrigerator.class,
        OnRefrigerator.class,
        OpenRefrigeratorDoor.class,
        Resting.class
})
// -----------------------------------------------------------------------------
public class			RefrigeratorUnitTesterModel
        extends		AtomicModel
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    /** URI for a model; works when only one instance is created.			*/
    public static final String	URI = RefrigeratorUnitTesterModel.class.
            getSimpleName();

    /** steps in the test scenario.											*/
    protected int	step;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public				RefrigeratorUnitTesterModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    @Override
    public void			initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);
        this.step = 1;
        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
    }

    @Override
    public ArrayList<EventI>	output()
    {
        // Simple way to implement a test scenario. Here each step generates
        // an event sent to the other models in the standard order.
        if (this.step > 0 && this.step < 8) {
            ArrayList<EventI> ret = new ArrayList<EventI>();
            switch (this.step) {
                case 1:
                    ret.add(new OnRefrigerator(this.getTimeOfNextEvent()));
                    break;
                case 2:
                    ret.add(new OpenRefrigeratorDoor(this.getTimeOfNextEvent()));
                    break;
                case 3:
                    ret.add(new CloseRefrigeratorDoor(this.getTimeOfNextEvent()));
                    break;
                case 4:
                    ret.add(new Resting(this.getTimeOfNextEvent()));
                    break;
                case 5:
                    ret.add(new Freezing(this.getTimeOfNextEvent()));
                    break;
                case 6:
                    ret.add(new OffRefrigerator(this.getTimeOfNextEvent()));
                    break;
            }
            return ret;
        } else {
            return null;
        }
    }

    @Override
    public Duration		timeAdvance()
    {
        // As long as events have to be created and sent, the next internal
        // transition is set at one second later, otherwise, no more internal
        // transitions are triggered (delay = infinity).
        if (this.step < 8) {
            return new Duration(1.0, this.getSimulatedTimeUnit());
        } else {
            return Duration.INFINITY;
        }
    }

    @Override
    public void			userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);

        // advance to the next step in the scenario
        this.step++;
    }

    @Override
    public void			endSimulation(Time endTime) throws Exception
    {
        this.logMessage("simulation ends.\n");
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return null;
    }
}
// -----------------------------------------------------------------------------