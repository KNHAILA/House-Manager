package fr.sorbonne_u.components.vacuumCleaner.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel;

public class SwitchOnVacuumCleaner extends AbstractVacuumCleanerEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public	SwitchOnVacuumCleaner(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        // if many VacuumCleaner events occur at the same time, the
        // SwitchOnVacuumCleaner one will be executed first.
        return true;
    }

 
    @Override
    public void				executeOn(AtomicModel model)
    {
        assert	model instanceof VacuumCleanerElectricityModel;

        // a SwitchOn event can be executed when the state of the
        // vacuum cleaner model is in the state OFF
        VacuumCleanerElectricityModel m = (VacuumCleanerElectricityModel)model;
        if (m.getState() == VacuumCleanerElectricityModel.State.OFF) {
            // then put it in the state LOW
            m.setState(VacuumCleanerElectricityModel.State.LOW);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------
