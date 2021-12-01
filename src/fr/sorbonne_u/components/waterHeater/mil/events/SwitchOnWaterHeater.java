package fr.sorbonne_u.components.waterHeater.mil.events;

import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterElectricityModel;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterElectricityModel.State;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>SwitchOnWaterHeater</code> defines the simulation event of the
 * heater being switched on.
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
public class			SwitchOnWaterHeater
extends		ES_Event
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
	public				SwitchOnWaterHeater(
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
		return true;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void			executeOn(AtomicModel model)
	{
		assert	model instanceof WaterHeaterElectricityModel;

		WaterHeaterElectricityModel heater = (WaterHeaterElectricityModel)model;
		assert	heater.getState() == State.OFF;
		heater.setState(State.ON);
	}
}