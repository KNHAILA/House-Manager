package fr.sorbonne_u.storageUnit.battery.mil.events;

import fr.sorbonne_u.storageUnit.battery.mil.events.AbstractBatteryEvent;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.storageUnit.battery.mil.BatteryElectricityModel;

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

    /**
     * create a <code>SwitchOnBattery</code> event.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code timeOfOccurrence != null}
     * post	{@code this.getTimeOfOccurrence().equals(timeOfOccurrence)}
     * </pre>
     *
     * @param timeOfOccurrence	time of occurrence of the event.
     */
    public	ChargeBattery(Time timeOfOccurrence)
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
    public boolean	hasPriorityOver(EventI e)
    {
        // if many Battery events occur at the same time, the
        // ChargeBattery one will be executed first.
        return true;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
     */
    @Override
    public void				executeOn(AtomicModel model)
    {
        assert	model instanceof BatteryElectricityModel;

        // a SwitchOn event can be executed when the state of the
        // Battery model is in the state OFF
        BatteryElectricityModel m = (BatteryElectricityModel)model;
        if (m.getState() == BatteryElectricityModel.State.OFF) {
            // then put it in the state LOW
            m.setState(BatteryElectricityModel.State.LOW);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------


