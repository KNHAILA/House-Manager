package fr.sorbonne_u.components.refrigerator.mil.events;

import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOnHeater</code> defines the simulation event of the
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
 * <p>Created on : 2021-09-21</p>
 *
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			OnRefrigerator
        extends		ES_Event
        implements	RefrigeratorEventI
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a <code>SwitchOnRefrigerator</code> event.
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
    public	OnRefrigerator(
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
        // if many Refrigerator events occur at the same time, the
        // SwitchOnRefrigerator one will be executed first.
        return true;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
     */
    @Override
    public void			executeOn(AtomicModel model)
    {
        assert	model instanceof RefrigeratorElectricityModel;

        RefrigeratorElectricityModel refrigerator = (RefrigeratorElectricityModel)model;
        assert	refrigerator.getState() == State.OFF;
        refrigerator.setState(State.ON);
    }
}
// -----------------------------------------------------------------------------
