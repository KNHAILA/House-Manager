package fr.sorbonne_u.components.vacuumCleaner.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel;

public class SwitchOffVacuumCleaner extends AbstractVacuumCleanerEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public	SwitchOffVacuumCleaner(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        // if many VacuumCleaner events occur at the same time, the
        // SwitchOffVacuumCleaner one will be executed after all others.
        return false;
    }

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
