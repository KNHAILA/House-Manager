package fr.sorbonne_u.components.washingMachine.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.HeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.StopHeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.WashingMachineEventI;
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

/**
 * The class <code>HeaterTemperatureModel</code> defines a simulation model
 * for the temperature inside a room equipped with a heater.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model is implemented as an atomic HIOA model with differential
 * equation. The differential equation defines the temperature variation
 * over time. It uses a very simple mathematical model where the derivative
 * is proportional to the difference between the current temperature and the
 * temperature that influences the current one. In fact, there are two
 * temperatures that influences the current temperature of the room:
 * </p>
 * <ol>
 * <li>the temperature outside the house (room) where the coefficient
 *   applied to the difference between the outside temperature and the
 *   current temperature models the thermal insulation of the walls
 *   ({@code INSULATION_TRANSFER_CONSTANT});</li>
 * <li>the temperature of the heater when it heats where the coefficient
 *   applied to the difference between the heater temperature
 *   ({@code STANDARD_HEATING_TEMP}) and the current temperature models the
 *   heat diffusion over the house (room)
 *   ({@code HEATING_TRANSFER_CONSTANT}).</li>
 * </ol>
 * <p>
 * The resulting differential equation is integrated using the Euler method
 * with a predefined integration step. The initial state of the model is
 * a state not heating and the initial temperature given by
 * {@code INITIAL_TEMPERATURE}.
 * </p>
 * <p>
 * Whether the current temperature evolves under the influence of the outside
 * temperature only or also the heating temperature depends upon the state,
 * which in turn is modified through the reception of imported events
 * {@code Heat} and {@code DoNotHeat}. The external temperature is imported
 * from another model simulating the environment. The current temperature is
 * exported to be used by other models.
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
@ModelExternalEvents(imported = {HeatWater.class, DoNotHeatWater.class, StopHeatWater.class})
public class WashingMachineTemperatureModel
extends		AtomicHIOAwithDE
{
	/**
	 * The enumeration <code>State</code> defines the states in which the
	 * heater can be.
	 *
	 * <p>Created on : 2021-09-24</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum	State {
		/** heater is heating.												*/
		HEATING,
		/** heater is heating.												*/
		NOT_HEATING,
		/** heater is heating.												*/
		NORMAL
	}

	private static final long		serialVersionUID = 1L;			
	/** URI for a model; works when only one instance is created.			*/
	public static final String		URI = WashingMachineTemperatureModel.class.
															getSimpleName();
	public static final double		INITIAL_TEMPERATURE = 0.0;
	/** wall insulation heat transfer constant in the differential equation.*/
	protected final double 			INSULATION_TRANSFER_CONSTANT = 100.0;
	/** heating transfer constant in the differential equation.				*/
	protected final double			HEATING_TRANSFER_CONSTANT = 5000.0;
	/** temperature of the heating plate in the heater.						*/
	protected final double			STANDARD_HEATING_TEMP = 300.0;
	/** integration step for the differential equation(assumed in seconds).	*/
	protected static final double	STEP = 0.1;
	/** integration step as a duration, including the time unit.			*/
	protected final Duration		integrationStep;
	/** current external temperature in Celsius.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>			externalTemperature;
	/** current temperature in the room.									*/
	@InternalVariable(type = Double.class)
	protected final Value<Double>	currentWaterTemperature =
											new Value<Double>(this, 0.0, 0);
	/** current state of the heater.										*/
	protected State					currentState = State.NOT_HEATING;
	/** the current derivative of the water temperature.					*/
	protected double				currentTempDerivative = 0.0;
	/** accumulator to compute the mean external temperature for the
	 *  simulation report.													*/
	protected double				temperatureAcc;
	/** the simulation time of start used to compute the mean temperature.	*/
	protected Time					start;
	/** the mean temperature over the simulation duration for the simulation
	 *  report.																*/
	protected double				meanTemperature;

	
	/**
	 * create a <code>HeaterTemperatureModel</code> instance.
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
	public				WashingMachineTemperatureModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.setLogger(new StandardLogger());
	}

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


	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.temperatureAcc = 0.0;
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

		this.currentWaterTemperature.v = this.externalTemperature.v-2;
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
	protected void		computeDerivatives()
	{
		this.currentTempDerivative = 0.0;	
		if (this.currentState == State.HEATING) {
			this.currentTempDerivative = 40;
		}
		this.currentTempDerivative +=
				(this.externalTemperature.v - this.currentWaterTemperature.v)/
												INSULATION_TRANSFER_CONSTANT;
	}

	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.temperatureAcc +=
				this.externalTemperature.v * elapsedTime.getSimulatedDuration();

		// differential equation
		if(this.currentState == State.NORMAL)
			this.currentWaterTemperature.v = this.externalTemperature.v-2;
		else
			this.currentWaterTemperature.v = this.currentWaterTemperature.v +
											this.currentTempDerivative*STEP;
		this.currentWaterTemperature.time = this.getCurrentStateTime();

		// Tracing
		String mark = this.currentState == State.HEATING ? " (h)" : " (-)";
		StringBuffer message = new StringBuffer();
		message.append(this.currentWaterTemperature.time.getSimulatedTime());
		message.append(mark);
		message.append(" : ");
		message.append(this.currentWaterTemperature.v);
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
		// and for the heater model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof WashingMachineEventI;
		assert	ce instanceof HeatWater || ce instanceof DoNotHeatWater;

		StringBuffer sb = new StringBuffer("executing the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n");
		this.logMessage(sb.toString());
		ce.executeOn(this);
		super.userDefinedExternalTransition(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.meanTemperature =
				this.temperatureAcc/
						endTime.subtract(this.start).getSimulatedDuration();

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	/**
	 * The class <code>HeaterTemperatureReport</code> implements the
	 * simulation report for the <code>HeaterTemperatureModel</code>.
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
	public static class		WashingMachineTemperatureReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	meanTemperature;

		public			WashingMachineTemperatureReport(
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

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new WashingMachineTemperatureReport(URI, this.meanTemperature);
	}
}
// -----------------------------------------------------------------------------
