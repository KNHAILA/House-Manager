package fr.sorbonne_u.components.fan.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractFanEvent</code> enforces a common
 * type for all fan simulation events.
 *
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Invariant</strong></p>
 *
 * <pre>
 * invariant	true
 * </pre>
 *
 * <p>Created on : 2021-09-20</p>
 *
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class AbstractFanEvent extends ES_Event
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * used to create an event used by the fan simulation model.
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
     * @param content			content (data) associated with the event.
     */
    public	AbstractFanEvent(
            Time timeOfOccurrence,
            EventInformationI content
    )
    {
        super(timeOfOccurrence, content);
    }
}
// -----------------------------------------------------------------------------
