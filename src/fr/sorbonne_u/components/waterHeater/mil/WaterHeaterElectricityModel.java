package fr.sorbonne_u.components.waterHeater.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.components.waterHeater.mil.events.AbstractWaterHeaterEvent;
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
import fr.sorbonne_u.utils.Electricity;
import fr.sorbonne_u.components.waterHeater.mil.events.*;

@ModelExternalEvents(imported = {SwitchOnWaterHeater.class,
								 SwitchOffWaterHeater.class
								 })
// -----------------------------------------------------------------------------
public class			WaterHeaterElectricityModel
extends		AtomicHIOA
{
	public static enum State {
		OFF,
		LOW
	}

	
	private static final long		serialVersionUID = 1L;

	public static final String		URI = WaterHeaterElectricityModel.class.getSimpleName();


	public static double			LOW_MODE_CONSUMPTION = 660.0; // Watts
	public static double			HIGH_MODE_CONSUMPTION = 1100.0; // Watts
	public static double			TENSION = 220.0; // Volts

	
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity =
											new Value<Double>(this, 0.0, 0);

	protected State					currentState = State.OFF;
		protected boolean				consumptionHasChanged = false;

	protected double				totalConsumption;

	
	public				WaterHeaterElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}

	
	public void			setState(State s)
	{
		this.currentState = s;
	}

	
	public State		getState()
	{
		return this.currentState;
	}

	
	public void			toggleConsumptionHasChanged()
	{
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
		} else {
			this.consumptionHasChanged = true;
		}
	}

	
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);
		this.currentIntensity.v = 0.0;
	}

	@Override
	public void			initialiseState(Time startTime)
	{
		super.initialiseState(startTime);
		this.currentState = State.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.consumptionHasChanged) {
			this.toggleConsumptionHasChanged();
			return new Duration(0.0, this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

	
		switch (this.currentState)
		{
			case OFF : this.currentIntensity.v = 0.0; break;
			case LOW :
				this.currentIntensity.v = LOW_MODE_CONSUMPTION/TENSION;
		}
		this.currentIntensity.time = this.getCurrentStateTime();

		StringBuffer message =
				new StringBuffer("executes an internal transition ");
		message.append("with current consumption ");
		message.append(this.currentIntensity.v);
		message.append(" at ");
		message.append(this.currentIntensity.time);
		message.append(".\n");
		this.logMessage(message.toString());
	}

	
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		this.totalConsumption +=
				Electricity.computeConsumption(elapsedTime,
											   TENSION*this.currentIntensity.v);

		// Tracing
		StringBuffer message =
				new StringBuffer("executes an external transition ");
		message.append(ce.getClass().getSimpleName());
		message.append("(");
		message.append(ce.getTimeOfOccurrence().getSimulatedTime());
		message.append(")\n");
		this.logMessage(message.toString());

		assert	ce instanceof AbstractWaterHeaterEvent;
		ce.executeOn(this);

		super.userDefinedExternalTransition(elapsedTime);
	}

	
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption +=
				Electricity.computeConsumption(d,
											   TENSION*this.currentIntensity.v);

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	public static final String		LOW_MODE_CONSUMPTION_RUNPNAME =
												URI + ":LOW_MODE_CONSUMPTION";
	public static final String		HIGH_MODE_CONSUMPTION_RUNPNAME =
												URI + ":HIGH_MODE_CONSUMPTION";			
	public static final String		TENSION_RUNPNAME = URI + ":TENSION";

	
	@Override
	public void			setSimulationRunParameters(
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

	public static class		HairDryerElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh

		public				HairDryerElectricityReport(
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
		return new HairDryerElectricityReport(URI, this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------
