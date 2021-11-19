package fr.sorbonne_u.components.vacuumCleaner.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOffVacuumCleaner</code> defines the simulation event of the
 * VacuumCleaner being switched off.
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
 * @author	<a href="mailto:Jacques.MalenVacuumCleanert@lip6.fr">Jacques MalenVacuumCleanert</a>
 */
public class SwitchOffVacuumCleaner extends AbstractVacuumCleanerEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a <code>SwitchOffVacuumCleaner</code> event.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code timeOfOccurrence != null}
     * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
     * </pre>
     *
     * @param timeOfOccurrence	time of occurrence of the event.
     */
    public	SwitchOffVacuumCleaner(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
     */
    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        // if many VacuumCleaner events occur at the same time, the
        // SwitchOffVacuumCleaner one will be executed after all others.
        return false;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
     */
    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof VacuumCleanerElectricityModel;

        VacuumCleanerElectricityModel m = ((VacuumCleanerElectricityModel)model);
        // a SwitchOff event can be executed when the state of the
        // dryer model is *not* in the state OFF
        if (m.getState() != VacuumCleanerElectricityModel.State.OFF) {
            // then put it in the state OFF
            m.setState(VacuumCleanerElectricityModel.State.OFF);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------
