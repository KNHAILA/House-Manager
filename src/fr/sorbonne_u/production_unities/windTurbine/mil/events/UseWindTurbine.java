package fr.sorbonne_u.production_unities.windTurbine.mil.events;


import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineElectricityModel;


public class UseWindTurbine extends AbstractWindTurbineEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public	UseWindTurbine(Time timeOfOccurrence)
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
        assert	model instanceof WindTurbineElectricityModel;

        WindTurbineElectricityModel m = (WindTurbineElectricityModel)model;
        if (m.getState() == WindTurbineElectricityModel.State.NOT_USE) {
            m.setState(WindTurbineElectricityModel.State.USE);
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------




