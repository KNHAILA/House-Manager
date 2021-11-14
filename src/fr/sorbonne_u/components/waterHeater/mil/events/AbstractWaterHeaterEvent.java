package fr.sorbonne_u.components.waterHeater.mil.events;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;


public class			AbstractWaterHeaterEvent
extends		ES_Event
{

	private static final long serialVersionUID = 1L;

	public				AbstractWaterHeaterEvent(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
		super(timeOfOccurrence, content);
	}
}