package fr.sorbonne_u.components.washingMachine.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineElectricityModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

public class			Spin
extends		Event
implements	WashingMachineEventI
{
	
	private static final long serialVersionUID = 1L;

	public				Spin(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnWashingMachine || e instanceof DoNotHeatWater || e instanceof HeatWater) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void			executeOn(AtomicModel model)
	{	
		assert	model instanceof WashingMachineElectricityModel ||
		model instanceof WashingMachineElectricityModel;

		if (model instanceof WashingMachineElectricityModel) {
			WashingMachineElectricityModel heater = (WashingMachineElectricityModel)model;
			assert	heater.getState() == WashingMachineElectricityModel.State.RINSING;
			heater.setState(WashingMachineElectricityModel.State.SPINNING);
		} else if (model instanceof WashingMachineTemperatureModel) {
			WashingMachineTemperatureModel heaterTemperature =
						(WashingMachineTemperatureModel)model;
			heaterTemperature.setState(WashingMachineTemperatureModel.State.NORMAL);
		}
	}
}