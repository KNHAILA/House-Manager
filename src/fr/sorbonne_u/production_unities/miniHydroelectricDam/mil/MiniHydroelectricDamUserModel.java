package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.DoNotMiniHydroelectricDam;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.UseMiniHydroelectricDam;


@ModelExternalEvents(exported = {DoNotMiniHydroelectricDam.class,
        UseMiniHydroelectricDam.class
        })
// -----------------------------------------------------------------------------
public class MiniHydroelectricDamUserModel extends AtomicES_Model
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long			serialVersionUID = 1L;
    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String			URI = MiniHydroelectricDamUserModel.class.
            getSimpleName();

    /** time interval between event outputs.								*/
    protected static double				STEP_MEAN_DURATION = 1.0;
    /**	the random number generator from common math library.				*/
    protected final RandomDataGenerator	rg ;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a Battery user MIL model instance.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code simulatedTimeUnit != null}
     * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
     * post	{@code getURI() != null}
     * post	{@code uri != null implies this.getURI().equals(uri)}
     * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
     * post	{@code simulationEngine != null implies getSimulationEngine().equals(simulationEngine)}
     * post	{@code !isDebugModeOn()}
     * </pre>
     *
     * @param uri				URI of the model.
     * @param simulatedTimeUnit	time unit used for the simulation time.
     * @param simulationEngine	simulation engine to which the model is attached.
     * @throws Exception		<i>to do.</i>
     */
    public				MiniHydroelectricDamUserModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.rg = new RandomDataGenerator() ;
        this.setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    protected void		generateNextEvent()
    {
        EventI current = this.eventList.peek();
        // compute the time of occurrence for the next event
        Time t = this.computeTimeOfNextEvent(current.getTimeOfOccurrence());
        // compute the next event type given the current event
        ES_EventI nextEvent = null;
        if (current instanceof UseMiniHydroelectricDam) {
            nextEvent = new DoNotMiniHydroelectricDam(t);
        } else if (current instanceof DoNotMiniHydroelectricDam) {
            nextEvent = new UseMiniHydroelectricDam(t);
        }
        // schedule the event to be executed by this model
        this.scheduleEvent(nextEvent);
    }

    protected Time		computeTimeOfNextEvent(Time from)
    {
        // generate randomly the next time interval but force it to be
        // greater than 0 by returning at least 0.1
        double delay = Math.max(this.rg.nextGaussian(STEP_MEAN_DURATION,
                        STEP_MEAN_DURATION/2.0),
                0.1);
        // compute the new time by adding the delay to from
        Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
        return t;
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void			initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);

        this.rg.reSeedSecure();

        // compute the time of occurrence for the first event
        Time t = this.computeTimeOfNextEvent(this.getCurrentStateTime());
        // schedule the first event
        this.scheduleEvent(new DoNotMiniHydroelectricDam(t));
        // re-initialisation of the time of occurrence of the next event
        // required here after adding a new event in the schedule.
        this.nextTimeAdvance = this.timeAdvance();
        this.timeOfNextEvent =
                this.getCurrentStateTime().add(this.getNextTimeAdvance());

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
     */
    @Override
    public ArrayList<EventI>	output()
    {
        // generate and schedule the next event
        if (this.eventList.peek() != null) {
            this.generateNextEvent();
        }
        // this will extract the next event from the event list and emit it
        return super.output();
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void			endSimulation(Time endTime) throws Exception
    {
        this.logMessage("simulation ends.\n");
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation run parameters
    // -------------------------------------------------------------------------

    /** run parameter name for {@code STEP_MEAN_DURATION}.					*/
    public static final String		STEP_MEAN_DURATION_RUNPNAME =
            URI + ":STEP_MEAN_DURATION";

    /**
     * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
     */
    @Override
    public void			setSimulationRunParameters(
            Map<String, Object> simParams
    ) throws Exception
    {
        super.setSimulationRunParameters(simParams);

        if (simParams.containsKey(STEP_MEAN_DURATION_RUNPNAME)) {
            STEP_MEAN_DURATION =
                    (double) simParams.get(STEP_MEAN_DURATION_RUNPNAME);
        }
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
     */
    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return null;
    }
}
// -----------------------------------------------------------------------------


