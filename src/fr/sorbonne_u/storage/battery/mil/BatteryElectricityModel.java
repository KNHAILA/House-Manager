package fr.sorbonne_u.storage.battery.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.storage.battery.mil.events.AbstractBatteryEvent;
import fr.sorbonne_u.storage.battery.mil.events.DonNotUseBattery;
import fr.sorbonne_u.storage.battery.mil.events.UseBattery;
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
@ModelExternalEvents(imported = { DonNotUseBattery.class,
        UseBattery.class,
        })
// -----------------------------------------------------------------------------
public class BatteryElectricityModel extends AtomicHIOA
{
    // -------------------------------------------------------------------------
    // Inner classes and types
    // -------------------------------------------------------------------------
    public static enum State {
        USE,
        /** USE mode, when manager use battery.						*/
        REST
        /** Manager don't use battery!						*/
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long		serialVersionUID = 1L;

    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String		URI = BatteryElectricityModel.class.getSimpleName();

    /** energy consumption (in Watts) of the Battery in charge mode.		*/
    public static double			CHARGE_MODE_CONSUMPTION = 1000.0; // Watts
    /** energy consumption (in Watts) of the Battery in HIGH mode.		*/
    public static double			USE_MODE_PRODUCTION = 200000.0; // Watts
    /** nominal tension (in Volts) of the Battery.						*/
    public static double			TENSION = 220.0; // Volts

    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	currentIntensity_consumption =
            new Value<Double>(this, 0.0, 0);
    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	currentIntensity_production =
            new Value<Double>(this, 0.0, 0);
    /** current state of the Battery.					*/
    protected State currentState = State.REST;
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
    public	BatteryElectricityModel(
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

        // initially, the Battery is REST, so its consumption and production are zero.
        this.currentIntensity_consumption.v = 0.0;
        this.currentIntensity_production.v = 0.0;
    }

    
    @Override
    public void	initialiseState(Time startTime)
    {
        super.initialiseState(startTime);

        // initially the Battery is off and its electricity consumption and production are
        // not about to change.
        this.currentState = State.REST;
        this.consumptionHasChanged = false;
        this.totalConsumption = 0.0;
        this.totalProduction = 0.0;

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
            case REST :
            	this.currentIntensity_consumption.v = CHARGE_MODE_CONSUMPTION/TENSION;
                break;
            case USE:
            	this.currentIntensity_consumption.v = CHARGE_MODE_CONSUMPTION/TENSION;
                this.currentIntensity_production.v = USE_MODE_PRODUCTION/TENSION;
                break;
        }
        this.currentIntensity_production.time = this.getCurrentStateTime();
        this.currentIntensity_consumption.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an internal transition ");
        if (this.currentState == State.REST) {
            message.append("with current consumption ");
            message.append(this.currentIntensity_consumption.v);
            message.append(" at ");
            message.append(this.currentIntensity_consumption.time);
        }

        else if (this.currentState == State.USE) {
            message.append("with current production ");
            message.append(this.currentIntensity_production.v);
            message.append(" at ");
            message.append(this.currentIntensity_production.time);
            
            message.append("with current consumption ");
            message.append(this.currentIntensity_consumption.v);
            message.append(" at ");
            message.append(this.currentIntensity_consumption.time);
        }

        message.append(".\n");
        this.logMessage(message.toString());
    }

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
        if(ce instanceof DonNotUseBattery) {
            this.totalConsumption +=
                    Electricity.computeConsumption(elapsedTime,
                            TENSION*this.currentIntensity_consumption.v);
        } else if(ce instanceof UseBattery) {
            this.totalProduction +=
                    Electricity.computeProduction(elapsedTime,
                            TENSION*this.currentIntensity_production.v);
            this.totalConsumption +=
                    Electricity.computeConsumption(elapsedTime,
                            TENSION*this.currentIntensity_consumption.v);
        }

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an external transition ");
        message.append(ce.getClass().getSimpleName());
        message.append("(");
        message.append(ce.getTimeOfOccurrence().getSimulatedTime());
        message.append(")\n");
        this.logMessage(message.toString());

        assert	ce instanceof AbstractBatteryEvent;
        // events have a method execute on to perform their effect on this
        // model
        ce.executeOn(this);

        super.userDefinedExternalTransition(elapsedTime);
    }

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

        this.logMessage("simulation ends.\n");
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation run parameters
    // -------------------------------------------------------------------------

    /** run parameter name for {@code CHARGE_MODE_CONSUMPTION}.				*/
    public static final String		CHARGE_MODE_CONSUMPTION_RUNPNAME =
            URI + ":CHARGE_MODE_CONSUMPTION";
    /** run parameter name for {@code USE_MODE_PRODUCTION}.				*/
    public static final String		USE_MODE_PRODUCTION_RUNPNAME =
            URI + ":USE_MODE_PRODUCTION";
    /** run parameter name for {@code TENSION}.								*/
    public static final String		TENSION_RUNPNAME = URI + ":TENSION";


    @Override
    public void	setSimulationRunParameters(
            Map<String, Object> simParams
    ) throws Exception
    {
        super.setSimulationRunParameters(simParams);

        if (simParams.containsKey(CHARGE_MODE_CONSUMPTION_RUNPNAME)) {
            CHARGE_MODE_CONSUMPTION =
                    (double) simParams.get(CHARGE_MODE_CONSUMPTION_RUNPNAME);
        }
        if (simParams.containsKey(USE_MODE_PRODUCTION_RUNPNAME)) {
            USE_MODE_PRODUCTION =
                    (double) simParams.get(USE_MODE_PRODUCTION_RUNPNAME);
        }
        if (simParams.containsKey(TENSION_RUNPNAME)) {
            TENSION = (double) simParams.get(TENSION_RUNPNAME);
        }
    }


    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------
    public static class		BatteryElectricityReport
            implements	SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        protected String	modelURI;
        protected double	totalConsumption; // in kwh
        protected double	totalProduction; // in kwh

        public				BatteryElectricityReport(
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

    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return new BatteryElectricityReport(URI, this.totalConsumption, this.totalProduction);
    }
}
// -----------------------------------------------------------------------------


