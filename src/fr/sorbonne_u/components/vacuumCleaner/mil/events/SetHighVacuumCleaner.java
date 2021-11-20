package fr.sorbonne_u.components.vacuumCleaner.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel;

public class SetHighVacuumCleaner extends AbstractVacuumCleanerEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public	SetHighVacuumCleaner(Time timeOfOccurrence)
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
        // SetHighVacuumCleaner one will be executed after SwitchOnVacuumCleaner
        // and SetLowVacuumCleaner ones but before SwitchOffVacuumCleaner.
        if (e instanceof SwitchOnVacuumCleaner || e instanceof SetLowVacuumCleaner) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof VacuumCleanerElectricityModel;

        VacuumCleanerElectricityModel m = (VacuumCleanerElectricityModel)model;

        // a SetHigh event can only be executed when the state of the
        // VacuumCleaner model is in the state LOW
        if (m.getState() == VacuumCleanerElectricityModel.State.LOW) {
            // then put it in the state HIGH
            m.setState(VacuumCleanerElectricityModel.State.HIGH);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------
