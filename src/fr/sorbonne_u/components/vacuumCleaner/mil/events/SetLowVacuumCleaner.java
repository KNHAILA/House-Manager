package fr.sorbonne_u.components.vacuumCleaner.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>SetLowVacuumCleanerDryer</code> defines the simulation event of the
 * VacuumCleaner being set to low temperature mode.
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
public class SetLowVacuumCleaner extends AbstractVacuumCleanerEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a <code>SetLowVacuumCleaner</code> event.
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
    public	SetLowVacuumCleaner(Time timeOfOccurrence)
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
    public boolean hasPriorityOver(EventI e)
    {
        // if many VacuumCleaner events occur at the same time, the
        // SetLowVacuumCleaner one will be executed first except for
        // SwitchOnVacuumCleaner ones.
        if (e instanceof SwitchOnVacuumCleaner) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
     */
    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof VacuumCleanerElectricityModel;

        VacuumCleanerElectricityModel m = (VacuumCleanerElectricityModel)model;
        // a SetLow event can only be executed when the state of the VacuumCleaner
        // dryer model is in the state HIGH
        if (m.getState() == VacuumCleanerElectricityModel.State.HIGH) {
            // then put it in the state LOW
            m.setState(VacuumCleanerElectricityModel.State.LOW);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------
