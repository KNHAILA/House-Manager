package fr.sorbonne_u.components.fan.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.fan.mil.FanElectricityModel;

// -----------------------------------------------------------------------------
public class SetLowFan extends	AbstractFanEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public	SetLowFan(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public boolean hasPriorityOver(EventI e)
    {
        // if many fan events occur at the same time, the
        // SetLowFan one will be executed first except for
        // SwitchOnFan ones.
        if (e instanceof SwitchOnFan) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FanElectricityModel;

        FanElectricityModel m = (FanElectricityModel)model;
        // a SetLow event can only be executed when the state of the fan
        // dryer model is in the state HIGH
        if (m.getState() == FanElectricityModel.State.HIGH) {
            // then put it in the state LOW
            m.setState(FanElectricityModel.State.LOW);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------
