package fr.sorbonne_u.components.waterHeater.mil;


import fr.sorbonne_u.components.waterHeater.mil.events.*;
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
/**
 * The class <code>HeaterUnitTesterModel</code> defines a model that is used
 * to test the models defining the heater simulator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code step >= 0}
 * </pre>
 * 
 * <p>Created on : 2021-09-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(exported = {SwitchOnWaterHeater.class,
								 SwitchOffWaterHeater.class})
public class			WaterHeaterUserModel
extends		AtomicES_Model
{
	private static final long			serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String			URI = WaterHeaterUserModel.class.
																getSimpleName();

	protected static double				STEP_MEAN_DURATION = 1.0;
	protected final RandomDataGenerator	rg ;
	
	/**
	 * create a <code>HeaterUnitTesterModel</code> instance.
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
	 * @throws Exception		<i>to do</i>.
	 */
	public				WaterHeaterUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator() ;
		this.setLogger(new StandardLogger());
	}


	protected void		generateNextEvent()
	{
		EventI current = this.eventList.peek();
		Time t = this.computeTimeOfNextEvent(current.getTimeOfOccurrence());
		ES_EventI nextEvent = null;
		if (current instanceof SwitchOnWaterHeater) {
			nextEvent = new SwitchOffWaterHeater(t);
		} else {
			assert	current instanceof SwitchOffWaterHeater;
			nextEvent = new SwitchOnWaterHeater(t);
		}
		this.scheduleEvent(nextEvent);
	}

	protected Time		computeTimeOfNextEvent(Time from)
	{
		double delay = Math.max(this.rg.nextGaussian(STEP_MEAN_DURATION,
													 STEP_MEAN_DURATION/2.0),
								0.1);
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);
		this.rg.reSeedSecure();

		Time t = this.computeTimeOfNextEvent(this.getCurrentStateTime());
		this.scheduleEvent(new SwitchOnWaterHeater(t));
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
		if (this.eventList.peek() != null) {
			this.generateNextEvent();
		}
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

	public static final String		STEP_MEAN_DURATION_RUNPNAME =
												URI + ":STEP_MEAN_DURATION";

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
