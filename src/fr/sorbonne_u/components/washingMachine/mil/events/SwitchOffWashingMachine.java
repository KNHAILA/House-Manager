package fr.sorbonne_u.components.washingMachine.mil.events;

import fr.sorbonne_u.components.washingMachine.mil.WashingMachineElectricityModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineElectricityModel.State;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;


public class			SwitchOffWashingMachine
extends		ES_Event
implements	WashingMachineEventI
{
	private static final long serialVersionUID = 1L;
	
	public				SwitchOffWashingMachine(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return false;
	}

	@Override
	public void			executeOn(AtomicModel model)
	{
		assert	model instanceof WashingMachineElectricityModel;

		WashingMachineElectricityModel heater = (WashingMachineElectricityModel)model;
		assert	heater.getState() != State.OFF;
		heater.setState(State.OFF);
	}
}
