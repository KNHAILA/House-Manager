package fr.sorbonne_u.components.refrigerator.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

public class			RefrigeratorExternalTemperatureModel
        extends		AtomicHIOA
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    /** URI for a model; works when only one instance is created.			*/
    public static final String		URI = RefrigeratorExternalTemperatureModel.class.
            getSimpleName();

    /** minimal external temperature.										*/
    public static final double		MIN_EXTERNAL_TEMPERATURE = -1.0;
    /** maximal external temperature.										*/
    public static final double		MAX_EXTERNAL_TEMPERATURE = 15.0;
    /** period of the temperature variation cycle (day); the cycle begins
     *  at the minimal temperature and ends at the same temperature.		*/
    public static final double		PERIOD = 10.0;

    /** evaluation step for the equation (assumed in seconds).				*/
    protected static final double	STEP = 1.0;
    /** evaluation step as a duration, including the time unit.				*/
    protected final Duration		evaluationStep;

    /** current external temperature in Celsius.							*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	externalTemperature =
            new Value<Double>(this, 0.0, 0);
    protected double				cycleTime;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public				RefrigeratorExternalTemperatureModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.evaluationStep = new Duration(STEP, this.getSimulatedTimeUnit());
        this.setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------
    @Override
    public void			initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);

        this.cycleTime = 0.0;
    }

    @Override
    protected void		initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        this.externalTemperature.v = MIN_EXTERNAL_TEMPERATURE;

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
        StringBuffer message =
                new StringBuffer("current external temperature: ");
        message.append(this.externalTemperature.v);
        message.append(" at ");
        message.append(this.getCurrentStateTime());
        message.append("\n");
        this.logMessage(message.toString());
    }

    @Override
    public ArrayList<EventI>	output()
    {
        // the model does not export any event.
        return null;
    }

    @Override
    public Duration		timeAdvance()
    {
        // the model makes an internal transition every evaluation step
        // duration
        return this.evaluationStep;
    }

    @Override
    public void			userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);

        // compute the current time in the cycle
        this.cycleTime += elapsedTime.getSimulatedDuration();
        if (this.cycleTime > PERIOD) {
            this.cycleTime -= PERIOD;
        }

        // compute the new temperature
        double c = Math.cos((1.0 + this.cycleTime/(PERIOD/2.0))*Math.PI);
        this.externalTemperature.v =
                MIN_EXTERNAL_TEMPERATURE +
                        (MAX_EXTERNAL_TEMPERATURE - MIN_EXTERNAL_TEMPERATURE)*
                                ((1.0 + c)/2.0);
        this.externalTemperature.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("current external temperature: ");
        message.append(this.externalTemperature.v);
        message.append(" at ");
        message.append(this.getCurrentStateTime());
        message.append("\n");
        this.logMessage(message.toString());
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
