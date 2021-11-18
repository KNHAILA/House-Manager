package fr.sorbonne_u.components.waterHeater.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterElectricityModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

public class			HeatWater
extends		Event
implements	WaterHeaterEventI
{
	
	private static final long serialVersionUID = 1L;

	public				HeatWater(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnWaterHeater || e instanceof DoNotHeatWater) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void			executeOn(AtomicModel model)
	{
		assert	model instanceof WaterHeaterElectricityModel ||
									model instanceof WaterHeaterTemperatureModel;

		if (model instanceof WaterHeaterElectricityModel) {
			WaterHeaterElectricityModel heater = (WaterHeaterElectricityModel)model;
			assert	heater.getState() == WaterHeaterElectricityModel.State.ON;
			heater.setState(WaterHeaterElectricityModel.State.HEATING);
		} else if (model instanceof WaterHeaterTemperatureModel) {
			WaterHeaterTemperatureModel heaterTemperature =
											(WaterHeaterTemperatureModel)model;
			heaterTemperature.setState(WaterHeaterTemperatureModel.State.HEATING);
		}
	}
}