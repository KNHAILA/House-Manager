package fr.sorbonne_u.components.waterHeater.mil;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.waterHeater.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOffWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOnWaterHeater;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(exported = {SwitchOnWaterHeater.class,
								 SwitchOffWaterHeater.class,
								 HeatWater.class,
								 DoNotHeatWater.class})
public class			WaterHeaterUnitTesterModel
extends		AtomicModel
{

	private static final long serialVersionUID = 1L;
	
	public static final String	URI = WaterHeaterUnitTesterModel.class.
															getSimpleName();
	protected int	step;

	public				WaterHeaterUnitTesterModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);
		this.step = 1;
		this.toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	@Override
	public ArrayList<EventI>	output()
	{
		if (this.step > 0 && this.step < 5) {
			ArrayList<EventI> ret = new ArrayList<EventI>();
			switch (this.step) {
			case 1:
				ret.add(new SwitchOnWaterHeater(this.getTimeOfNextEvent()));
				break;
			case 2:
				ret.add(new HeatWater(this.getTimeOfNextEvent()));
				break;
			case 3:
				ret.add(new DoNotHeatWater(this.getTimeOfNextEvent()));
				break;
			case 4:
				ret.add(new SwitchOffWaterHeater(this.getTimeOfNextEvent()));
				break;
			}
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.step < 5) {
			return new Duration(1.0, this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}


	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);
		this.step++;
	}

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
