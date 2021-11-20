package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events;


import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamElectricityModel;

// -----------------------------------------------------------------------------

public class UseMiniHydroelectricDam extends AbstractMiniHydroelectricDamEvent
{
	// -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

   
    public	UseMiniHydroelectricDam(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
    	return true;
    }

    
    @Override
    public void				executeOn(AtomicModel model)
    {
        assert	model instanceof MiniHydroelectricDamElectricityModel;

        MiniHydroelectricDamElectricityModel m = (MiniHydroelectricDamElectricityModel)model;
        if (m.getState() == MiniHydroelectricDamElectricityModel.State.NOT_USE) {
            m.setState(MiniHydroelectricDamElectricityModel.State.USE);
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------




