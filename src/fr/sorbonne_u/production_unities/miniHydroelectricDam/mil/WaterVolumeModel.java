package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
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
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.*;

//-----------------------------------------------------------------------------
@ModelExternalEvents(imported = {StartMiniHydroelectricDam.class, StopMiniHydroelectricDam.class})
//-----------------------------------------------------------------------------
public class			WaterVolumeModel
extends		AtomicHIOAwithDE
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String		URI = WaterVolumeModel.class.
															getSimpleName();
	/** integration step for the differential equation(assumed in seconds).	*/
	protected static final double	STEP = 0.1;
	/** integration step as a duration, including the time unit.			*/
	protected final Duration		integrationStep;
	
	public static final double		MIN_WATER_SPEED = 0.0;
    public static final double		MAX_WATER_SPEED = 500.0;

    public static final double		PERIOD = 10.0;
    
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	waterVolume =
            new Value<Double>(this, 0.0, 0);
    
    protected double				cycleTime;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>WaterVolumeModel</code> instance.
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
	public				WaterVolumeModel(
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
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.toggleDebugMode();
		this.logMessage("simulation begins.\n");

		super.initialiseState(initialTime);
		this.cycleTime = 0.0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);

		this.waterVolume.v = MIN_WATER_SPEED;

        this.toggleDebugMode();
	    this.logMessage("simulation begins.\n");
	    StringBuffer message =
	                new StringBuffer("current wind speed: ");
	    message.append(this.waterVolume.v);
	    message.append(" at ");
	    message.append(this.getCurrentStateTime());
	    message.append("\n");
	    this.logMessage(message.toString());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithDE#initialiseDerivatives()
	 */
	@Override
	protected void		initialiseDerivatives()
	{
		this.computeDerivatives();
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
		return this.integrationStep;
	}

	@Override
	protected void		computeDerivatives() {}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		// Update water volume
		this.cycleTime += elapsedTime.getSimulatedDuration();
        if (this.cycleTime > PERIOD) {
            this.cycleTime -= PERIOD;
        }
        double c = Math.cos((1.0 + this.cycleTime/(PERIOD/2.0))*Math.PI);
        this.waterVolume.v =
                MIN_WATER_SPEED +
                        (MAX_WATER_SPEED - MIN_WATER_SPEED)*
                                ((1.0 + c)/2.0);
        this.waterVolume.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("current wind speed: ");
        message.append(this.waterVolume.v);
        message.append(" at ");
        message.append(this.getCurrentStateTime());
        message.append("\n");
        this.logMessage(message.toString());

		super.userDefinedInternalTransition(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the dam model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof MiniHydroelectricDamEventI;
		assert	ce instanceof StartMiniHydroelectricDam || ce instanceof StopMiniHydroelectricDam;

		StringBuffer sb = new StringBuffer("executing the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n");
		this.logMessage(sb.toString());

		// the next call will update the current state of the heater and if
		// this state has changed, it will toggle the boolean
		// consumptionHasChanged, which in turn will trigger an immediate
		// internal transition to update the current intensity of the
		// heater electricity consumption.
		ce.executeOn(this);

		super.userDefinedExternalTransition(elapsedTime);
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
	
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return null;
	}
}

