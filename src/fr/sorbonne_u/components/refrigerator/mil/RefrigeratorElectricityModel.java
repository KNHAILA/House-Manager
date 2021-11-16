package fr.sorbonne_u.components.refrigerator.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.components.refrigerator.mil.events.*;
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
import fr.sorbonne_u.utils.Electricity;

// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {Freezing.class,
        CloseRefrigeratorDoor.class,
        OffRefrigerator.class,
        OnRefrigerator.class,
        OpenRefrigeratorDoor.class,
        Resting.class
})
// -----------------------------------------------------------------------------
public class			RefrigeratorElectricityModel
        extends		AtomicHIOA
{
    // -------------------------------------------------------------------------
    // Inner classes and types
    // -------------------------------------------------------------------------
    
    public static enum	State {
        /** Refrigerator is on but not FREEZING.									*/
        ON,
        /** Refrigerator is on and FREEZING.										*/
        FREEZING,
        /** Refrigerator is off.													*/
        OFF
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long	serialVersionUID = 1L;
    /** URI for a model; works when only one instance is created.			*/
    public static final String	URI = RefrigeratorElectricityModel.class.
            getSimpleName();

    /** power of the Refrigerator in watts.										*/
    public static double		NOT_FREEZING_POWER = 22.0;
    /** power of the Refrigerator in watts.										*/
    public static double		FREEZING_POWER = 2200.0;
    /** nominal tension (in Volts) of the Refrigerator.							*/
    public static double		TENSION = 220.0;

    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	currentIntensity =
            new Value<Double>(this, 0.0, 0);
    /** current state of the Refrigerator.										*/
    protected State					currentState = State.OFF;
    /** true when the electricity consumption of the Refrigerator has changed
     *  after executing an external event; the external event changes the
     *  value of <code>currentState</code> and then an internal transition
     *  will be triggered by putting through in this variable which will
     *  update the variable <code>currentIntensity</code>.					*/
    protected boolean				consumptionHasChanged = false;

    /** total consumption of the Refrigerator during the simulation in kwh.		*/
    protected double				totalConsumption;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a Refrigerator MIL model instance.
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
    public				RefrigeratorElectricityModel(
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
     * set the state of the Refrigerator.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	s != null
     * post	true		// no postcondition.
     * </pre>
     *
     * @param s		the new state.
     */
    public void			setState(State s)
    {
        State old = this.currentState;
        this.currentState = s;
        if (old != this.currentState) {
            this.consumptionHasChanged = true;
        }
    }

    /**
     * return the state of the Refrigerator.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true		// no precondition.
     * post	{@code ret != null}
     * </pre>
     *
     * @return	the current state.
     */
    public State		getState()
    {
        return this.currentState;
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void			initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);

        this.currentState = State.OFF;
        this.consumptionHasChanged = false;
        this.totalConsumption = 0.0;

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    protected void		initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        // initially, the Refrigerator is off, so its consumption is zero.
        this.currentIntensity.v = 0.0;

        StringBuffer sb = new StringBuffer("new consumption: ");
        sb.append(this.currentIntensity.v);
        sb.append(" amperes at ");
        sb.append(this.currentIntensity.time);
        sb.append(" seconds.\n");
        this.logMessage(sb.toString());
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
     */
    @Override
    public ArrayList<EventI>	output()
    {
        return null;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
     */
    @Override
    public Duration		timeAdvance()
    {
        if (this.consumptionHasChanged) {
            // When the consumption has changed, an immediate (delay = 0.0)
            // internal transition must be made to update the electricity
            // consumption.
            this.consumptionHasChanged = false;
            return Duration.zero(this.getSimulatedTimeUnit());
        } else {
            // As long as the state does not change, no internal transition
            // is made (delay = infinity).
            return Duration.INFINITY;
        }
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void			userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);

        if (this.currentState == State.ON) {
            this.currentIntensity.v =
                    RefrigeratorElectricityModel.NOT_FREEZING_POWER/
                            RefrigeratorElectricityModel.TENSION;
        } else if (this.currentState == State.FREEZING) {
            this.currentIntensity.v =
                    RefrigeratorElectricityModel.FREEZING_POWER/
                            RefrigeratorElectricityModel.TENSION;
        } else {
            assert	this.currentState == State.OFF;
            this.currentIntensity.v = 0.0;
        }
        this.currentIntensity.time = this.getCurrentStateTime();

        StringBuffer sb = new StringBuffer("new consumption: ");
        sb.append(this.currentIntensity.v);
        sb.append(" amperes at ");
        sb.append(this.currentIntensity.time);
        sb.append(" seconds.\n");
        this.logMessage(sb.toString());
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
     */
    @Override
    public void userDefinedExternalTransition(Duration elapsedTime)
    {
        // get the vector of current external events
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the Refrigerator model, there will be exactly one by
        // construction.
        assert	currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert	ce instanceof RefrigeratorEventI;

        // compute the total consumption for the simulation report.
        this.totalConsumption +=
                Electricity.computeConsumption(elapsedTime,
                        TENSION*this.currentIntensity.v);

        StringBuffer sb = new StringBuffer("execute the external event: ");
        sb.append(ce.eventAsString());
        sb.append(".\n");
        this.logMessage(sb.toString());

        // the next call will update the current state of the Refrigerator and if
        // this state has changed, it put the boolean consumptionHasChanged
        // at true, which in turn will trigger an immediate internal transition
        // to update the current intensity of the Refrigerator electricity
        // consumption.
        ce.executeOn(this);

        super.userDefinedExternalTransition(elapsedTime);
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void			endSimulation(Time endTime) throws Exception
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

    /** power of the Refrigerator in watts.										*/
    public static final String	NOT_FREEZING_POWER_RUNPNAME = "NOT_FREEZING_POWER";
    /** power of the Refrigerator in watts.										*/
    public static final String	FREEZING_POWER_RUNPNAME = "FREEZING_POWER";
    /** nominal tension (in Volts) of the Refrigerator.							*/
    public static final String	TENSION_RUNPNAME = "TENSION";

    /**
     * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
     */
    @Override
    public void			setSimulationRunParameters(
            Map<String, Object> simParams
    ) throws Exception
    {
        super.setSimulationRunParameters(simParams);

        if (simParams.containsKey(NOT_FREEZING_POWER_RUNPNAME)) {
            NOT_FREEZING_POWER =
                    (double) simParams.get(NOT_FREEZING_POWER_RUNPNAME);
        }
        if (simParams.containsKey(FREEZING_POWER_RUNPNAME)) {
            FREEZING_POWER = (double) simParams.get(FREEZING_POWER_RUNPNAME);
        }
        if (simParams.containsKey(TENSION_RUNPNAME)) {
            TENSION =
                    (double) simParams.get(TENSION_RUNPNAME);
        }
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    /**
     * The class <code>RefrigeratorElectricityReport</code> implements the
     * simulation report for the <code>RefrigeratorElectricityModel</code>.
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
     * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
     */
    public static class		RefrigeratorElectricityReport
            implements	SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        protected String	modelURI;
        protected double	totalConsumption; // in kwh


        public			RefrigeratorElectricityReport(
                String modelURI,
                double totalConsumption
        )
        {
            super();
            this.modelURI = modelURI;
            this.totalConsumption = totalConsumption;
        }

        @Override
        public String	getModelURI()
        {
            return this.modelURI;
        }

        @Override
        public String	printout(String indent)
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
        return new RefrigeratorElectricityReport(URI, this.totalConsumption);
    }
}
// -----------------------------------------------------------------------------
