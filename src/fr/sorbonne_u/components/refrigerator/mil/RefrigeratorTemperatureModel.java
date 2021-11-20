package fr.sorbonne_u.components.refrigerator.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.components.refrigerator.mil.events.*;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithDE;
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
@ModelExternalEvents(imported = {Freezing.class, Resting.class, OpenRefrigeratorDoor.class, CloseRefrigeratorDoor.class, OffRefrigerator.class})
// -----------------------------------------------------------------------------
public class			RefrigeratorTemperatureModel
        extends		AtomicHIOAwithDE
{
    // -------------------------------------------------------------------------
    // Inner classes and types
    // -------------------------------------------------------------------------
    public static enum	State {
        /** Refrigerator is FREEZING.												*/
        FREEZE,
        /** Refrigerator is RESTING.											*/
        REST,
        /** Refrigerator is WARMING in open door case.											*/
        OFFSET,
        /** Refrigerator is WARMING in off case.											*/
        OFF
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long		serialVersionUID = 1L;
    /** URI for a model; works when only one instance is created.			*/
    public static final String		URI = RefrigeratorTemperatureModel.class.
            getSimpleName();
    public static final double		MIN_TEMPERATURE = -3.0;
    public static final double		MAX_TEMPERATURE = 6.0;
    /** integration step for the differential equation(assumed in seconds).	*/
    protected static final double	STEP = 0.1;
    /** integration step as a duration, including the time unit.			*/
    protected final Duration		integrationStep;
    /** current temperatur e in the room.									*/
    @InternalVariable(type = Double.class)
    protected final Value<Double>	currentTemperature =
            new Value<Double>(this, 0.0, 0);
    /** current state of the Refrigerator.										*/
    protected State					currentState = State.REST;
    /** the simulation time of start used to compute the mean temperature.	*/
    protected Time					start;
    /** accumulator to compute the mean external temperature for the
     *  simulation report.													*/
    protected double				temperatureAcc;
    /** the mean temperature over the simulation duration for the simulation
     *  report.																*/
    protected double				meanTemperature;
    /** current external temperature in Celsius.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			externalTemperature;




    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public				RefrigeratorTemperatureModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.integrationStep = new Duration(STEP, simulatedTimeUnit);
        this.setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public void			setState(State s)
    {
        this.currentState = s;
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    @Override
    public void			initialiseState(Time initialTime)
    {
        this.temperatureAcc = 0.0;
        this.start = initialTime;

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");

        super.initialiseState(initialTime);
    }

    @Override
    protected void initialiseDerivatives() {}

    @Override
    protected void computeDerivatives() {}

    @Override
    protected void		initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);
        this.currentTemperature.v = 0.0;
    }

    @Override
    public ArrayList<EventI>	output()
    {
        return null;
    }

    @Override
    public Duration		timeAdvance()
    {
        return this.integrationStep;
    }

    @Override
    public void			userDefinedInternalTransition(Duration elapsedTime)
    {
        // accumulate the temperature*time to compute the mean temperature
        this.temperatureAcc +=
                this.externalTemperature.v * elapsedTime.getSimulatedDuration();

        // update the room temperature using the Euler integration of the
        // differential equation

        if(externalTemperature.v > this.currentTemperature.v) {
            this.currentTemperature.v += 0.1;
        } else if(externalTemperature.v < this.currentTemperature.v) {
            this.currentTemperature.v += -0.1;
        }

        if (this.currentState == State.FREEZE) {
            this.currentTemperature.v += -5*STEP;
            if(currentTemperature.v < MIN_TEMPERATURE) {
                this.currentTemperature.v = MIN_TEMPERATURE;
            }
        } else if(currentState == State.OFFSET) {
            this.currentTemperature.v += 5*STEP;
            if(currentTemperature.v > externalTemperature.v) {
                this.currentTemperature.v = externalTemperature.v;
            }
        } else if(currentState == State.REST) {
            if(currentTemperature.v < MIN_TEMPERATURE) {
                this.currentTemperature.v = MIN_TEMPERATURE;
            } else if(currentTemperature.v > MAX_TEMPERATURE) {
                this.currentTemperature.v = MAX_TEMPERATURE;
            }
        }

        this.currentTemperature.time = this.getCurrentStateTime();

        // Tracing
        String mark = this.currentState == State.FREEZE ? " (f)" : " (-)";
        StringBuffer message = new StringBuffer();
        message.append(this.currentTemperature.time.getSimulatedTime());
        message.append(mark);
        message.append(" : ");
        message.append(this.currentTemperature.v);
        message.append('\n');
        this.logMessage(message.toString());

        super.userDefinedInternalTransition(elapsedTime);
    }

    @Override
    public void			userDefinedExternalTransition(Duration elapsedTime)
    {
        // get the vector of current external events
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the Refrigerator model, there will be exactly one by
        // construction.
        assert	currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert	ce instanceof RefrigeratorEventI;
        assert	ce instanceof Freezing || ce instanceof Resting || ce instanceof CloseRefrigeratorDoor || ce instanceof OpenRefrigeratorDoor || ce instanceof OffRefrigerator;

        StringBuffer sb = new StringBuffer("executing the external event: ");
        sb.append(ce.eventAsString());
        sb.append(".\n");
        this.logMessage(sb.toString());

        // the next call will update the current state of the Refrigerator and if
        // this state has changed, it will toggle the boolean
        // consumptionHasChanged, which in turn will trigger an immediate
        // internal transition to update the current intensity of the
        // Refrigerator electricity consumption.
        ce.executeOn(this);

        super.userDefinedExternalTransition(elapsedTime);
    }

    @Override
    public void			endSimulation(Time endTime) throws Exception
    {
        this.meanTemperature =
                this.temperatureAcc/
                        endTime.subtract(this.start).getSimulatedDuration();
        this.logMessage("simulation ends.\n");
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------
    public static class		RefrigeratorTemperatureReport
            implements	SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        protected String	modelURI;
        protected double	meanTemperature;

        public			RefrigeratorTemperatureReport(
                String modelURI,
                double meanTemperature
        )
        {
            super();
            this.modelURI = modelURI;
            this.meanTemperature = meanTemperature;
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
            ret.append("mean temperature = ");
            ret.append(this.meanTemperature);
            ret.append(".\n");
            ret.append(indent);
            ret.append("---\n");
            return ret.toString();
        }
    }

    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return new RefrigeratorTemperatureReport(URI, this.meanTemperature);
    }
}
// -----------------------------------------------------------------------------
