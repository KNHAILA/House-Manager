package fr.sorbonne_u.components.waterHeater.mil;



import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.components.waterHeater.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOffWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOnWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.WaterHeaterEventI;
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


@ModelExternalEvents(imported = {SwitchOnWaterHeater.class,
								 SwitchOffWaterHeater.class,
								 HeatWater.class,
								 DoNotHeatWater.class})
// -----------------------------------------------------------------------------
public class			WaterHeaterElectricityModel
extends		AtomicHIOA
{
	
	public static enum	State {
		ON,
		HEATING,
		OFF
	}
	
	private static final long	serialVersionUID = 1L;
	public static final String	URI = WaterHeaterElectricityModel.class.
															getSimpleName();

	public static double		NOT_HEATING_POWER = 22.0;
	public static double		HEATING_POWER = 2200.0;
	public static double		TENSION = 220.0;

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
		State old = this.currentState;
		this.currentState = s;
		if (old != this.currentState) {
			this.consumptionHasChanged = true;
		}
	}

	public State		getState()
	{
		return this.currentState;
	}

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.currentState = State.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);

		// initially, the heater is off, so its consumption is zero.
		this.currentIntensity.v = 0.0;

		StringBuffer sb = new StringBuffer("new consumption: ");
		sb.append(this.currentIntensity.v);
		sb.append(" amperes at ");
		sb.append(this.currentIntensity.time);
		sb.append(" seconds.\n");
		this.logMessage(sb.toString());
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
			this.consumptionHasChanged = false;
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		if (this.currentState == State.ON) {
			this.currentIntensity.v =
					WaterHeaterElectricityModel.NOT_HEATING_POWER/
											WaterHeaterElectricityModel.TENSION;
		} else if (this.currentState == State.HEATING) {
			this.currentIntensity.v =
					WaterHeaterElectricityModel.HEATING_POWER/
											WaterHeaterElectricityModel.TENSION;
		} else {
			assert	this.currentState == State.OFF;
			this.currentIntensity.v = 0.0;
		}
		this.currentIntensity.time = this.getCurrentStateTime();

		StringBuffer sb = new StringBuffer("new consumption: ");
		sb.append(this.currentIntensity.v);
		sb.append(" amperes at ");
		sb.append(this.currentIntensity.time);
		sb.append(" seconds.\n");
		this.logMessage(sb.toString());
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime)
	{
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();

		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof WaterHeaterEventI;
		
		this.totalConsumption +=
				Electricity.computeConsumption(elapsedTime,
											   TENSION*this.currentIntensity.v);

		StringBuffer sb = new StringBuffer("execute the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n");
		this.logMessage(sb.toString());
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

	public static final String	NOT_HEATING_POWER_RUNPNAME = "NOT_HEATING_POWER";
	public static final String	HEATING_POWER_RUNPNAME = "HEATING_POWER";
	public static final String	TENSION_RUNPNAME = "TENSION";

	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		super.setSimulationRunParameters(simParams);

		if (simParams.containsKey(NOT_HEATING_POWER_RUNPNAME)) {
			NOT_HEATING_POWER =
					(double) simParams.get(NOT_HEATING_POWER_RUNPNAME);
		}
		if (simParams.containsKey(HEATING_POWER_RUNPNAME)) {
			HEATING_POWER = (double) simParams.get(HEATING_POWER_RUNPNAME);
		}
		if (simParams.containsKey(TENSION_RUNPNAME)) {
			TENSION =
					(double) simParams.get(TENSION_RUNPNAME);
		}
	}
	
	public static class		WaterHeaterElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh


		public			WaterHeaterElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
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
		return new WaterHeaterElectricityReport(URI, this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------
