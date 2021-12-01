package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events;


import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamElectricityModel;


/**
 * The class <code>DoNotHeatWater</code> defines the simulation event of the
 * water heater stopping to heat water.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-11-09</p>
 * 
 * @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">Kaoutar NHAILA</a>
 */
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




