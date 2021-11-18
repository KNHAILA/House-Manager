package fr.sorbonne_u.components.vacuumCleaner.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.AbstractVacuumCleanerEvent;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.SwitchOnVacuumCleaner;
import fr.sorbonne_u.utils.Electricity;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {SwitchOnVacuumCleaner.class,
        SwitchOffVacuumCleaner.class,
        SetLowVacuumCleaner.class,
        SetHighVacuumCleaner.class})
// -----------------------------------------------------------------------------
public class VacuumCleanerElectricityModel extends AtomicHIOA
{
    // -------------------------------------------------------------------------
    // Inner classes and types
    // -------------------------------------------------------------------------

    /**
     * The enumeration <code>State</code> describes the discrete states or
     * modes of the VacuumCleaner.
     *
     * <p><strong>Description</strong></p>
     *
     * The VacuumCleaner can be <code>OFF</code> or on, and then it is either in
     * <code>LOW</code> mode (less hot and less consuming) or in
     * <code>HIGH</code> mode (hotter and more consuming).
     *
     * <p>Created on : 2019-10-10</p>
     *
     * @author	<a href="mailto:Jacques.MalenVacuumCleanert@lip6.fr">Jacques MalenVacuumCleanert</a>
     */
    public static enum State {
        OFF,
        /** low mode is less hot and less consuming.						*/
        LOW,
        /** high mode is hotter and more consuming.							*/
        HIGH
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long		serialVersionUID = 1L;

    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String		URI = fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel.class.
            getSimpleName();

    /** energy consumption (in Watts) of the VacuumCleaner in LOW mode.		*/
    public static double			LOW_MODE_CONSUMPTION = 660.0; // Watts
    /** energy consumption (in Watts) of the VacuumCleaner in HIGH mode.		*/
    public static double			HIGH_MODE_CONSUMPTION = 1100.0; // Watts
    /** nominal tension (in Volts) of the VacuumCleaner.						*/
    public static double			TENSION = 220.0; // Volts

    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentIntensity =
            new Value<Double>(this, 0.0, 0);
    /** current state (OFF, LOW, HIGH) of the VacuumCleaner.					*/
    protected fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel.State currentState = fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel.State.OFF;
    /** true when the electricity consumption of the dryer has changed
     *  after executing an external event; the external event changes the
     *  value of <code>currentState</code> and then an internal transition
     *  will be triggered by putting through in this variable which will
     *  update the variable <code>currentIntensity</code>.					*/
    protected boolean				consumptionHasChanged = false;

    /** total consumption of the VacuumCleaner during the simulation in kwh.	*/
    protected double				totalConsumption;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a VacuumCleaner MIL model instance.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code simulatedTimeUnit != null}
     * pre	{@code simulationEngine == null || simulationEngine instanceof HIOA_AtomicEngine}
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
     * @throws Exception		<i>to do</i>.
     */
    public	VacuumCleanerElectricityModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * set the state of the VacuumCleaner.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code s != null}
     * post	{@code getState() == s}
     * </pre>
     *
     * @param s		the new state.
     */
    public void	setState(fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel.State s)
    {
        this.currentState = s;
    }

    /**
     * return the state of the VacuumCleaner.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true		// no precondition.
     * post	{@code ret != null}
     * </pre>
     *
     * @return	the state of the VacuumCleaner.
     */
    public fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel.State getState()
    {
        return this.currentState;
    }

    /**
     * toggle the value of the state of the model telling whether the
     * electricity consumption level has just changed or not; when it changes
     * after receiving an external event, an immediate internal transition
     * is triggered to update the level of electricity consumption.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true		// no precondition.
     * post	true		// no postcondition.
     * </pre>
     *
     */
    public void	toggleConsumptionHasChanged()
    {
        if (this.consumptionHasChanged) {
            this.consumptionHasChanged = false;
        } else {
            this.consumptionHasChanged = true;
        }
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    protected void	initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        // initially, the VacuumCleaner is off, so its consumption is zero.
        this.currentIntensity.v = 0.0;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void	initialiseState(Time startTime)
    {
        super.initialiseState(startTime);

        // initially the VacuumCleaner is off and its electricity consumption is
        // not about to change.
        this.currentState = fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel.State.OFF;
        this.consumptionHasChanged = false;
        this.totalConsumption = 0.0;

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
     */
    @Override
    public ArrayList<EventI> output()
    {
        // the model does not export events.
        return null;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
     */
    @Override
    public Duration timeAdvance()
    {
        // to trigger an internal transition after an external transition, the
        // variable consumptionHasChanged is set to true, hence when it is true
        // return a zero delay otherwise return an infinite delay (no internal
        // transition expected)
        if (this.consumptionHasChanged) {
            // after triggering the internal transition, toggle the boolean
            // to prepare for the next internal transition.
            this.toggleConsumptionHasChanged();
            return new Duration(0.0, this.getSimulatedTimeUnit());
        } else {
            return Duration.INFINITY;
        }
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);

        // set the current electricity consumption from the current state
        switch (this.currentState)
        {
            case OFF : this.currentIntensity.v = 0.0; break;
            case LOW :
                this.currentIntensity.v = LOW_MODE_CONSUMPTION/TENSION;
                break;
            case HIGH :
                this.currentIntensity.v = HIGH_MODE_CONSUMPTION/TENSION;
        }
        this.currentIntensity.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an internal transition ");
        message.append("with current consumption ");
        message.append(this.currentIntensity.v);
        message.append(" at ");
        message.append(this.currentIntensity.time);
        message.append(".\n");
        this.logMessage(message.toString());
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void	userDefinedExternalTransition(Duration elapsedTime)
    {
        // get the vector of currently received external events
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the current VacuumCleaner model, there must be exactly one by
        // construction.
        assert	currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);

        // compute the total consumption (in kwh) for the simulation report.
        this.totalConsumption +=
                Electricity.computeConsumption(elapsedTime,
                        TENSION*this.currentIntensity.v);

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an external transition ");
        message.append(ce.getClass().getSimpleName());
        message.append("(");
        message.append(ce.getTimeOfOccurrence().getSimulatedTime());
        message.append(")\n");
        this.logMessage(message.toString());

        assert	ce instanceof AbstractVacuumCleanerEvent;
        // events have a method execute on to perform their effect on this
        // model
        ce.executeOn(this);

        super.userDefinedExternalTransition(elapsedTime);
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void	endSimulation(Time endTime) throws Exception
    {
        Duration d = endTime.subtract(this.getCurrentStateTime());
        this.totalConsumption +=
                Electricity.computeConsumption(d,
                        TENSION*this.currentIntensity.v);

        this.logMessage("simulation ends.\n");
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation run parameters
    // -------------------------------------------------------------------------

    /** run parameter name for {@code LOW_MODE_CONSUMPTION}.				*/
    public static final String		LOW_MODE_CONSUMPTION_RUNPNAME =
            URI + ":LOW_MODE_CONSUMPTION";
    /** run parameter name for {@code HIGH_MODE_CONSUMPTION}.				*/
    public static final String		HIGH_MODE_CONSUMPTION_RUNPNAME =
            URI + ":HIGH_MODE_CONSUMPTION";
    /** run parameter name for {@code TENSION}.								*/
    public static final String		TENSION_RUNPNAME = URI + ":TENSION";

    /**
     * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
     */
    @Override
    public void	setSimulationRunParameters(
            Map<String, Object> simParams
    ) throws Exception
    {
        super.setSimulationRunParameters(simParams);

        if (simParams.containsKey(LOW_MODE_CONSUMPTION_RUNPNAME)) {
            LOW_MODE_CONSUMPTION =
                    (double) simParams.get(LOW_MODE_CONSUMPTION_RUNPNAME);
        }
        if (simParams.containsKey(HIGH_MODE_CONSUMPTION_RUNPNAME)) {
            HIGH_MODE_CONSUMPTION =
                    (double) simParams.get(HIGH_MODE_CONSUMPTION_RUNPNAME);
        }
        if (simParams.containsKey(TENSION_RUNPNAME)) {
            TENSION = (double) simParams.get(TENSION_RUNPNAME);
        }
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    /**
     * The class <code>VacuumCleanerElectricityReport</code> implements the
     * simulation report for the <code>VacuumCleanerElectricityModel</code>.
     *
     * <p><strong>Description</strong></p>
     *
     * <p><strong>Invariant</strong></p>
     *
     * <pre>
     * invariant	true
     * </pre>
     *
     * <p>Created on : 2021-10-01</p>
     *
     * @author	<a href="mailto:Jacques.MalenVacuumCleanert@lip6.fr">Jacques MalenVacuumCleanert</a>
     */
    public static class		VacuumCleanerElectricityReport
            implements SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        protected String	modelURI;
        protected double	totalConsumption; // in kwh

        public				VacuumCleanerElectricityReport(
                String modelURI,
                double totalConsumption
        )
        {
            super();
            this.modelURI = modelURI;
            this.totalConsumption = totalConsumption;
        }

        @Override
        public String		getModelURI()
        {
            return null;
        }

        @Override
        public String		printout(String indent)
        {
            StringBuffer ret = new StringBuffer(indent);
            ret.append("---\n");
            ret.append(indent);
            ret.append('|');
            ret.append(this.modelURI);
            ret.append(" report\n");
            ret.append(indent);
            ret.append('|');
            ret.append("total consumption in kwh = ");
            ret.append(this.totalConsumption);
            ret.append(".\n");
            ret.append(indent);
            ret.append("---\n");
            return ret.toString();
        }
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
     */
    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return new fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel.VacuumCleanerElectricityReport(URI, this.totalConsumption);
    }
}
// -----------------------------------------------------------------------------
