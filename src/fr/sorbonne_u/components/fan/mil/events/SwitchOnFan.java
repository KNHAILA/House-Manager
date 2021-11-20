package fr.sorbonne_u.components.fan.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.fan.mil.FanElectricityModel;

// -----------------------------------------------------------------------------

public class SwitchOnFan extends AbstractFanEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public	SwitchOnFan(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        // if many fan events occur at the same time, the
        // SwitchOnFan one will be executed first.
        return true;
    }

    @Override
    public void				executeOn(AtomicModel model)
    {
        assert	model instanceof FanElectricityModel;

        // a SwitchOn event can be executed when the state of the
        // fan model is in the state OFF
        FanElectricityModel m = (FanElectricityModel)model;
        if (m.getState() == FanElectricityModel.State.OFF) {
            // then put it in the state LOW
            m.setState(FanElectricityModel.State.LOW);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------
