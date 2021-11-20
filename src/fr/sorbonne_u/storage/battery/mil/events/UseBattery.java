package fr.sorbonne_u.storage.battery.mil.events;


import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;

public class UseBattery extends AbstractBatteryEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public	UseBattery(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        if (e instanceof ChargeBattery) {
            return false;
        } else {
            return true;
        }
    }

 
    @Override
    public void				executeOn(AtomicModel model)
    {
        assert	model instanceof BatteryElectricityModel;

        BatteryElectricityModel m = (BatteryElectricityModel)model;
        if (m.getState() == BatteryElectricityModel.State.REST || m.getState() == BatteryElectricityModel.State.CHARGE) {
            m.setState(BatteryElectricityModel.State.DISCHARGE);
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------




