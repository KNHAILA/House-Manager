package fr.sorbonne_u.production_unities.windTurbine.mil.events;

import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineElectricityModel.State;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineElectricityModel;

public class			StartWindTurbine
extends		ES_Event
implements	WindTurbineEventI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>StopwindTurbine</code> event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * post	{@code this.getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public				StartWindTurbine(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		// if many windTurbine events occur at the same time, the
		// StopWindTurbine one will be executed first.
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void			executeOn(AtomicModel model)
	{
		
		assert	model instanceof WindTurbineElectricityModel;

		WindTurbineElectricityModel windTurbine = (WindTurbineElectricityModel)model;
		assert	windTurbine.getState() == State.OFF;
		windTurbine.setState(State.ON);
	}
}
// -----------------------------------------------------------------------------
