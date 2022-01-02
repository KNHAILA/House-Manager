package fr.sorbonne_u.storage.battery.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.storage.battery.mil.events.*;
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

//-----------------------------------------------------------------------------
/**
* The class <code>BatteryPercentageModel</code> defines a simulation model
* for the Percentage of the battery.
*
* <p><strong>Description</strong></p>
* 
* <p>
* The model is implemented as an atomic HIOA model. 
* </p>

* </p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	{@code STEP > 0.0}
* </pre>
* 
* <p>Created on : 2021-09-23</p>
* 
* @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
*/
//-----------------------------------------------------------------------------
@ModelExternalEvents(imported = {ChargeBattery.class, DoNotChargeBattery.class, UseBattery.class, DoNotUseBattery.class})
//-----------------------------------------------------------------------------
public class			BatteryPercentageModel
extends		AtomicHIOAwithDE
{
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>State</code> defines the states in which the
	 * heater can be.
	 *
	 * <p>Created on : 2021-09-24</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum	State {
		/** battery is heating.												*/
		CHARGING,
		/** battery is not heating.											*/
		NOT_CHARGING,
		/** battery is discharging.											*/
		DISCHARGING
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String		URI = BatteryPercentageModel.class.
															getSimpleName();
	/** integration step for the differential equation(assumed in seconds).	*/
	protected static final double	STEP = 0.1;
	/** integration step as a duration, including the time unit.			*/
	protected final Duration		integrationStep;
	/** current percentage.									*/
	@InternalVariable(type = Double.class)
	protected final Value<Double>	currentPercentage =
											new Value<Double>(this, 0.0, 0);
	/** current state of the battery.										*/
	protected State					currentState = State.NOT_CHARGING;
	/** the simulation time of start used to compute the mean Percentage.	*/
	protected Time					start;

	/** current electricity state.							*/
	@ImportedVariable(type = BatteryElectricityModel.State.class)
	public Value<BatteryElectricityModel.State>			electricityState;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>BatteryPercentageModel</code> instance.
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
	public				BatteryPercentageModel(
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

	/**
	 * set the state of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param s	the new state.
	 */
	public void			setState(State s)
	{
		this.currentState = s;
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
		this.start = initialTime;

		this.toggleDebugMode();
		this.logMessage("simulation begins.\n");

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);

		this.currentPercentage.v = 50.0;
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
		// update the battery percentage
		if (this.currentState == State.CHARGING) {
			this.currentPercentage.v = this.currentPercentage.v + 5;
		} else if(this.currentState == State.DISCHARGING) {
			this.currentPercentage.v = this.currentPercentage.v - 2;
		}
		
		if(this.currentPercentage.v > 100.0) {
			this.currentPercentage.v = 100.0;
		}
		
		if(this.currentPercentage.v < 0.0) {
			this.currentPercentage.v = 0.0;
		}
		
		this.currentPercentage.time = this.getCurrentStateTime();

		// Tracing
		String mark = this.currentState == State.CHARGING ? " (h)" : " (-)";
		StringBuffer message = new StringBuffer();
		message.append(this.currentPercentage.time.getSimulatedTime());
		message.append(mark);
		message.append(" : ");
		message.append(this.currentPercentage.v);
		message.append('\n');
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
		// and for the battery model
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof BatteryEventI;
		assert	ce instanceof ChargeBattery || ce instanceof DoNotChargeBattery || ce instanceof DoNotUseBattery || ce instanceof UseBattery;

		StringBuffer sb = new StringBuffer("executing the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n");
		this.logMessage(sb.toString());

		// the next call will update the current state of the battery and if
		// this state has changed, it will toggle the boolean
		// consumptionHasChanged, which in turn will trigger an immediate
		// internal transition to update the current intensity of the
		// battery electricity consumption.
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
}
//-----------------------------------------------------------------------------
