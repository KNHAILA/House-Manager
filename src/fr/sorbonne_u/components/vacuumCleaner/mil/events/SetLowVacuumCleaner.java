package fr.sorbonne_u.components.vacuumCleaner.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerElectricityModel;

public class SetLowVacuumCleaner extends AbstractVacuumCleanerEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public	SetLowVacuumCleaner(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

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
