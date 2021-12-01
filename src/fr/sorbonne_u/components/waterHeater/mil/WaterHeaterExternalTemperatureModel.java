package fr.sorbonne_u.components.waterHeater.mil;

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

/**
 * The class <code>ExternalTemperatureModel</code> defines a simulation model
 * for the environment, namely the external temperature of the house.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model makes the temperature vary over some period representing typically
 * a day. The variation is taken as a cosine between {@code Math.PI} and
 * {@code 3*Math.PI}. The cosine (plus 1 and divided by 2 to vary between 0 and
 * 1) is taken as a coefficient applied to the maximal variation over a day and
 * then added to the minimal temperature to get the current temperature.
 * </p>
 * <p>
 * The model reevaluates the external temperature at some predefined rate i.e.,
 * the evaluation step. It exports this temperature in a variable called
 * {@code externalTemperature}.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code MAX_EXTERNAL_TEMPERATURE > MIN_EXTERNAL_TEMPERATURE}
 * invariant	{@code PERIOD > 0.0}
 * invariant	{@code STEP > 0.0}
 * invariant	{@code cycleTime >= 0.0 && cycleTime <= PERIOD}
 * </pre>
 * 
 * <p>Created on : 2021-09-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			WaterHeaterExternalTemperatureModel
        extends		AtomicHIOA
{
    private static final long serialVersionUID = 1L;

	/** URI for a model; works when only one instance is created.			*/
    public static final String URI = WaterHeaterExternalTemperatureModel.class.
            getSimpleName();
    /** minimal external temperature.										*/
    public static final double		MIN_EXTERNAL_TEMPERATURE = 0.0;
	/** maximal external temperature.										*/
    public static final double		MAX_EXTERNAL_TEMPERATURE = 15.0;
	/** period of the temperature variation cycle (day); the cycle begins
	 *  at the minimal temperature and ends at the same temperature.		*/
    public static final double		PERIOD = 10.0;
	/** evaluation step for the equation (assumed in seconds).				*/
    protected static final double	STEP = 1.0;
	/** evaluation step as a duration, including the time unit.				*/
    protected final Duration		evaluationStep;
    @ExportedVariable(type = Double.class)
	/** current external temperature in Celsius.							*/
    protected final Value<Double>	externalTemperature =
            new Value<Double>(this, 0.0, 0);
    protected double				cycleTime;


	/**
	 * create an external temperature MIL model instance.
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
    public WaterHeaterExternalTemperatureModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.evaluationStep = new Duration(STEP, this.getSimulatedTimeUnit());
        this.setLogger(new StandardLogger());
    }


    /**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
    @Override
    public void			initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);
        this.cycleTime = 0.0;
    }

    /**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
    @Override
    protected void initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        this.externalTemperature.v = MIN_EXTERNAL_TEMPERATURE;

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
        StringBuffer message =
                new StringBuffer("current external water temperature: ");
        message.append(this.externalTemperature.v);
        message.append(" at ");
        message.append(this.getCurrentStateTime());
        message.append("\n");
        this.logMessage(message.toString());
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
        return this.evaluationStep;
    }

    /**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
    @Override
    public void			userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);
        this.cycleTime += elapsedTime.getSimulatedDuration();
        if (this.cycleTime > PERIOD) {
            this.cycleTime -= PERIOD;
        }
        double c = Math.cos((1.0 + this.cycleTime/(PERIOD/2.0))*Math.PI);
        this.externalTemperature.v =
                MIN_EXTERNAL_TEMPERATURE +
                        (MAX_EXTERNAL_TEMPERATURE - MIN_EXTERNAL_TEMPERATURE)*
                                ((1.0 + c)/2.0);
        this.externalTemperature.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("current external  water temperature: ");
        message.append(this.externalTemperature.v);
        message.append(" at ");
        message.append(this.getCurrentStateTime());
        message.append("\n");
        this.logMessage(message.toString());
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

    /**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return null;
    }
}
