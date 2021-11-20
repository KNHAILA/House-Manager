package fr.sorbonne_u.storage.battery.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOnBattery</code> defines the simulation event of the
 * Battery being switched on.
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
 * @author	<a href="mailto:Jacques.MalenBatteryt@lip6.fr">Jacques MalenBatteryt</a>
 */
public class ChargeBattery extends AbstractBatteryEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public	ChargeBattery(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        // if many Battery events occur at the same time, the
        // ChargeBattery one will be executed first.
        return true;
    }

 
    @Override
    public void				executeOn(AtomicModel model)
    {
        assert	model instanceof BatteryElectricityModel;

        BatteryElectricityModel m = (BatteryElectricityModel)model;
        if (m.getState() == BatteryElectricityModel.State.REST || m.getState() == BatteryElectricityModel.State.DISCHARGE) {
            m.setState(BatteryElectricityModel.State.CHARGE);
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------


