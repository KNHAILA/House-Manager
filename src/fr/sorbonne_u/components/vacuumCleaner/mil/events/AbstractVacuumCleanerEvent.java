package fr.sorbonne_u.components.vacuumCleaner.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractVacuumCleanerEventEvent</code> enforces a common
 * type for all vacuumCleanerEvent simulation events.
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
 * @author	<a href="mailto:Jacques.MalenvacuumCleanerEventt@lip6.fr">Jacques MalenvacuumCleanerEventt</a>
 */
public class AbstractVacuumCleanerEvent extends ES_Event
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * used to create an event used by the vacuumCleanerEvent simulation model.
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
    public	AbstractVacuumCleanerEvent(
            Time timeOfOccurrence,
            EventInformationI content
    )
    {
        super(timeOfOccurrence, content);
    }
}
// -----------------------------------------------------------------------------
