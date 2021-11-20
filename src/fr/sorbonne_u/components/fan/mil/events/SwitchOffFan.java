package fr.sorbonne_u.components.fan.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.fan.mil.FanElectricityModel;

// -----------------------------------------------------------------------------
public class SwitchOffFan extends AbstractFanEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public	SwitchOffFan(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        // if many fan events occur at the same time, the
        // SwitchOffFan one will be executed after all others.
        return false;
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FanElectricityModel;

        FanElectricityModel m = ((FanElectricityModel)model);
        // a SwitchOff event can be executed when the state of the fan
        // model is *not* in the state OFF
        if (m.getState() != FanElectricityModel.State.OFF) {
            // then put it in the state OFF
            m.setState(FanElectricityModel.State.OFF);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------
