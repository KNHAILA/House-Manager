package fr.sorbonne_u.components.fan.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.components.fan.mil.events.AbstractFanEvent;
import fr.sorbonne_u.components.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOnFan;
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
import org.apache.commons.math3.util.Precision;

// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {SwitchOnFan.class,
        SwitchOffFan.class,
        SetLowFan.class,
        SetHighFan.class})
// -----------------------------------------------------------------------------
public class FanElectricityModel extends AtomicHIOA
{
    // -------------------------------------------------------------------------
    // Inner classes and types
    // -------------------------------------------------------------------------
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
    public static final String		URI = FanElectricityModel.class.
            getSimpleName();

    /** energy consumption (in Watts) of the fan in LOW mode.		*/
    public static double			LOW_MODE_CONSUMPTION = 660.0; // Watts
    /** energy consumption (in Watts) of the fan in HIGH mode.		*/
    public static double			HIGH_MODE_CONSUMPTION = 1100.0; // Watts
    /** nominal tension (in Volts) of the fan.						*/
    public static double			TENSION = 220.0; // Volts

    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	currentIntensity =
            new Value<Double>(this, 0.0, 0);
    /** current state (OFF, LOW, HIGH) of the fan.					*/
    protected State					currentState = State.OFF;
    /** true when the electricity consumption of the fan has changed
     *  after executing an external event; the external event changes the
     *  value of <code>currentState</code> and then an internal transition
     *  will be triggered by putting through in this variable which will
     *  update the variable <code>currentIntensity</code>.					*/
    protected boolean				consumptionHasChanged = false;

    /** total consumption of the fan during the simulation in kwh.	*/
    protected double				totalConsumption;

    /** current consumption of the fan during the simulation in kwh.	*/
    protected double				currentConsumption;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public	FanElectricityModel(
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

    public void	setState(State s)
    {
        this.currentState = s;
    }
    public State getState()
    {
        return this.currentState;
    }

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

    @Override
    protected void	initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        // initially, the fan is off, so its consumption is zero.
        this.currentIntensity.v = 0.0;
    }

    @Override
    public void	initialiseState(Time startTime)
    {
        super.initialiseState(startTime);

        // initially the fan is off and its electricity consumption is
        // not about to change.
        this.currentState = State.OFF;
        this.consumptionHasChanged = false;
        this.totalConsumption = 0.0;

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
    }

    @Override
    public ArrayList<EventI>	output()
    {
        // the model does not export events.
        return null;
    }

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

    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);

        // set the current electricity consumption from the current state
        switch (this.currentState)
        {
            case OFF :
                this.currentIntensity.v = 0.0;
                this.currentConsumption = 0.0;
                break;
            case LOW :
                this.currentIntensity.v = LOW_MODE_CONSUMPTION/TENSION;
                this.currentConsumption = LOW_MODE_CONSUMPTION/1000;
                break;
            case HIGH :
                this.currentIntensity.v = HIGH_MODE_CONSUMPTION/TENSION;
                this.currentConsumption = HIGH_MODE_CONSUMPTION/1000;
        }
        this.currentIntensity.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an internal transition ");
        message.append("with current consumption ");
        message.append(this.currentConsumption);
        message.append(" ,and with current Intensity ");
        message.append(this.currentIntensity.v);
        message.append(" at ");
        message.append(this.currentIntensity.time);
        message.append(".\n");
        this.logMessage(message.toString());
    }

    @Override
    public void	userDefinedExternalTransition(Duration elapsedTime)
    {
        // get the vector of currently received external events
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the current fan model, there must be exactly one by
        // construction.
        assert	currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        // compute the total consumption (in kwh) for the simulation report.
        this.totalConsumption += Electricity.computeConsumption(elapsedTime, TENSION*this.currentIntensity.v);

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an external transition ");
        message.append(ce.getClass().getSimpleName());
        message.append("(");
        message.append(ce.getTimeOfOccurrence().getSimulatedTime());
        message.append(")\n");
        this.logMessage(message.toString());

        assert	ce instanceof AbstractFanEvent;
        // events have a method execute on to perform their effect on this
        // model
        ce.executeOn(this);

        super.userDefinedExternalTransition(elapsedTime);
    }

    public static double	toHours(Duration d)
    {
        long factor = d.getTimeUnit().convert(1, TimeUnit.HOURS);
        double ret = d.getSimulatedDuration()/factor;
        return ret;
    }
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

    public static class		FanElectricityReport
            implements	SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        protected String	modelURI;
        protected double	totalConsumption; // in kwh

        public				FanElectricityReport(
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

    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return new FanElectricityReport(URI, this.totalConsumption);
    }
}
// -----------------------------------------------------------------------------
