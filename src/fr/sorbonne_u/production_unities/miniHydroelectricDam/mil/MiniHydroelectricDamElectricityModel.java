package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.utils.Electricity;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
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
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.AbstractMiniHydroelectricDamEvent;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.DoNotMiniHydroelectricDam;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.UseMiniHydroelectricDam;

// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {DoNotMiniHydroelectricDam.class,
        UseMiniHydroelectricDam.class,
        })
// -----------------------------------------------------------------------------
public class MiniHydroelectricDamElectricityModel extends AtomicHIOA
{
    // -------------------------------------------------------------------------
    // Inner classes and types
    // -------------------------------------------------------------------------

    public static enum State {
        USE,
        NOT_USE
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long		serialVersionUID = 1L;

    public static final String		URI = MiniHydroelectricDamElectricityModel.class.getSimpleName();

    
    public static double			MODE_CONSUMPTION = 1000.0; // Watts
    public static double			MODE_PRODUCTION = 200000.0; // Watts
    public static double			TENSION = 220.0; // Volts

    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	currentIntensity_consumption =
            new Value<Double>(this, 0.0, 0);
    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	currentIntensity_production =
            new Value<Double>(this, 0.0, 0);
    
    /** current intensity in amperes; intensity is power/tension.			*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			waterSpeed;
    /** current state of the Battery.					*/
    protected State currentState = State.NOT_USE;
    /** true when the electricity consumption of the BATTERY has changed
     *  after executing an external event; the external event changes the
     *  value of <code>currentState</code> and then an internal transition
     *  will be triggered by putting through in this variable which will
     *  update the variable <code>currentIntensity</code>.					*/
    protected boolean				consumptionHasChanged = false;
    /** total consumption of the Battery during the simulation in kwh.	*/
    protected double				totalConsumption;

    /** total Production of the Battery during the simulation in kwh.	*/
    protected double				totalProduction;

    protected double				capacity = 300.0; //    ampere/h

    protected double				charge_time = 1; //    h

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a Battery MIL model instance.
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
    public	MiniHydroelectricDamElectricityModel(
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
     * set the state of the Battery.
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
    public void	setState(State s)
    {
        this.currentState = s;
    }

    /**
     * return the state of the Battery.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true		// no precondition.
     * post	{@code ret != null}
     * </pre>
     *
     * @return	the state of the Battery.
     */
    public State getState()
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

        // initially, the Battery is REST, so its consumption and production are zero.
        this.currentIntensity_consumption.v = 0.0;
        this.currentIntensity_production.v = 0.0;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
     */
    @Override
    public void	initialiseState(Time startTime)
    {
        super.initialiseState(startTime);

        // initially the Battery is off and its electricity consumption and production are
        // not about to change.
        this.currentState = State.NOT_USE;
        this.consumptionHasChanged = false;
        this.totalConsumption = 0.0;
        this.totalProduction = 0.0;

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
     */
    @Override
    public ArrayList<EventI>	output()
    {
        // the model does not export events.
        return null;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
     */
    @Override
    public Duration	timeAdvance()
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
            case NOT_USE :
                this.currentIntensity_production.v = 0.0;
                this.currentIntensity_consumption.v = 0.0;
                break;
            case USE:
            	this.currentIntensity_production.v = waterSpeed.v*5000/TENSION;
        }
        this.currentIntensity_production.time = this.getCurrentStateTime();
        this.currentIntensity_consumption.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an internal transition ");

            message.append("with current production ");
            message.append(this.currentIntensity_production.v);
            message.append(" at ");
            message.append(this.currentIntensity_production.time);

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
        // and for the current Battery model, there must be exactly one by
        // construction.
        assert	currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);

        // compute the total consumption (in kwh) for the simulation report.
        if(ce instanceof DoNotMiniHydroelectricDam) {
            this.totalConsumption +=
                    Electricity.computeConsumption(elapsedTime,
                            TENSION*this.currentIntensity_consumption.v);
        } else if(ce instanceof UseMiniHydroelectricDam) {
            this.totalProduction +=
                    Electricity.computeProduction(elapsedTime,
                            TENSION*this.currentIntensity_production.v);
        }

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an external transition ");
        message.append(ce.getClass().getSimpleName());
        message.append("(");
        message.append(ce.getTimeOfOccurrence().getSimulatedTime());
        message.append(")\n");
        this.logMessage(message.toString());

        assert	ce instanceof AbstractMiniHydroelectricDamEvent;
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
                        TENSION*this.currentIntensity_consumption.v);
        this.totalProduction +=
                Electricity.computeProduction(d,
                        TENSION*this.currentIntensity_production.v);

        
        this.logMessage(this.currentIntensity_production.v+"simulation ends.\n");
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation run parameters
    // -------------------------------------------------------------------------

    /** run parameter name for {@code MODE_CONSUMPTION}.				*/
    public static final String		MODE_CONSUMPTION_RUNPNAME =
            URI + ": MODE_CONSUMPTION";
    /** run parameter name for {@code MODE_PRODUCTION}.				*/
    public static final String		MODE_PRODUCTION_RUNPNAME =
            URI + ":MODE_PRODUCTION";
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

        if (simParams.containsKey(MODE_CONSUMPTION_RUNPNAME)) {
           MODE_CONSUMPTION =
                    (double) simParams.get(MODE_CONSUMPTION_RUNPNAME);
        }
        if (simParams.containsKey(MODE_PRODUCTION_RUNPNAME)) {
            MODE_PRODUCTION =
                    (double) simParams.get(MODE_PRODUCTION_RUNPNAME);
        }
        if (simParams.containsKey(TENSION_RUNPNAME)) {
            TENSION = (double) simParams.get(TENSION_RUNPNAME);
        }
    }


    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    
    public static class		MiniHydroelectricDamElectricityReport
            implements	SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        protected String	modelURI;
        protected double	totalConsumption; // in kwh
        protected double	totalProduction; // in kwh

        public				MiniHydroelectricDamElectricityReport(
                String modelURI,
                double totalConsumption,
                double totalProduction
        )
        {
            super();
            this.modelURI = modelURI;
            this.totalConsumption = totalConsumption;
            this.totalProduction = totalProduction;
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

            ret.append("\n");

            ret.append(indent);
            ret.append('|');
            ret.append(this.modelURI);
            ret.append(" report\n");
            ret.append(indent);
            ret.append('|');
            ret.append("total production in kwh = ");
            ret.append(this.totalProduction);
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
        return new MiniHydroelectricDamElectricityReport(URI, this.totalConsumption, this.totalProduction);
    }
}
// -----------------------------------------------------------------------------




