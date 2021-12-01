package fr.sorbonne_u.components.washingMachine.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineElectricityModel;
import fr.sorbonne_u.components.washingMachine.mil.WashingMachineTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

/**
 * The class <code>DoNotHeatWater</code> defines the simulation event of the
 * water heater stopping to heat water.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-11-09</p>
 * 
 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">Kaoutar NHAILA</a>
 */
public class			HeatWater
extends		Event
implements	WashingMachineEventI
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
		if (e instanceof SwitchOnWashingMachine) {
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
			assert	heater.getState() == WashingMachineElectricityModel.State.ON;
			heater.setState(WashingMachineElectricityModel.State.HEATING);
		} else if (model instanceof WashingMachineTemperatureModel) {
			WashingMachineTemperatureModel heaterTemperature =
											(WashingMachineTemperatureModel)model;
			heaterTemperature.setState(WashingMachineTemperatureModel.State.HEATING);
		}
	}
}