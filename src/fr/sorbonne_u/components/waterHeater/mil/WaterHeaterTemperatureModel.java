package fr.sorbonne_u.components.waterHeater.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.components.waterHeater.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.WaterHeaterEventI;
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


@ModelExternalEvents(imported = {HeatWater.class, DoNotHeatWater.class})
public class WaterHeaterTemperatureModel
extends		AtomicHIOAwithDE
{
	public static enum	State {
		HEATING,
		NOT_HEATING
	}

	private static final long		serialVersionUID = 1L;			
	public static final String		URI = WaterHeaterTemperatureModel.class.
															getSimpleName();
	public static final double		INITIAL_TEMPERATURE = 0.0;
	protected final double 			INSULATION_TRANSFER_CONSTANT = 100.0;
	protected final double			HEATING_TRANSFER_CONSTANT = 5000.0;
	protected final double			STANDARD_HEATING_TEMP = 300.0;
	protected static final double	STEP = 0.1;
	protected final Duration		integrationStep;
	@ImportedVariable(type = Double.class)
	protected Value<Double>			externalTemperature;
	@InternalVariable(type = Double.class)
	protected final Value<Double>	currentWaterTemperature =
											new Value<Double>(this, 0.0, 0);
	protected State					currentState = State.NOT_HEATING;
	protected double				currentTempDerivative = 0.0;
	protected double				temperatureAcc;
	protected Time					start;
	protected double				meanTemperature;

	
	public				WaterHeaterTemperatureModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.setLogger(new StandardLogger());
	}

	public void			setState(State s)
	{
		this.currentState = s;
	}

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
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);

		this.currentWaterTemperature.v = this.externalTemperature.v;
	}

	@Override
	protected void		initialiseDerivatives()
	{
		this.computeDerivatives();
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
	protected void		computeDerivatives()
	{
		this.currentTempDerivative = 0.0;
		if (this.currentState == State.HEATING) {
			this.currentTempDerivative =100;
		}
		this.currentTempDerivative +=
				(this.externalTemperature.v - this.currentWaterTemperature.v)/
												INSULATION_TRANSFER_CONSTANT;
		
	}

	
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.temperatureAcc +=
				this.externalTemperature.v * elapsedTime.getSimulatedDuration();

		// differential equation
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
		assert	ce instanceof WaterHeaterEventI;
		assert	ce instanceof HeatWater || ce instanceof DoNotHeatWater;

		StringBuffer sb = new StringBuffer("executing the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n");
		this.logMessage(sb.toString());
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

	public static class		HeaterTemperatureReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	meanTemperature;

		public			HeaterTemperatureReport(
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
		return new HeaterTemperatureReport(URI, this.meanTemperature);
	}
}
// -----------------------------------------------------------------------------
