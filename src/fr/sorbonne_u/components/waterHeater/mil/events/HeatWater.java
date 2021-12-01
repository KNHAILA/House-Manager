package fr.sorbonne_u.components.waterHeater.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterElectricityModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

/**
 * The class <code>HeatWater</code> defines the simulation event of the water heater
 * starting to heat water.
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
implements	WaterHeaterEventI
{
	
	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>Heat</code> event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * post	{@code this.getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code this.getEventInformation.equals(content)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public				HeatWater(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnWaterHeater || e instanceof DoNotHeatWater) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
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