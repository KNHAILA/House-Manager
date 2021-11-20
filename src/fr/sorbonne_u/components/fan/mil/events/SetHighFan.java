package fr.sorbonne_u.components.fan.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.fan.mil.FanElectricityModel;
// -----------------------------------------------------------------------------

public class SetHighFan extends	AbstractFanEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public	SetHighFan(Time timeOfOccurrence)
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
        // SetHighFan one will be executed after SwitchOnFan
        // and SetLowFan ones but before SwitchOffFan.
        if (e instanceof SwitchOnFan || e instanceof SetLowFan) {
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

        // a SetHigh event can only be executed when the state of the
        // fan model is in the state LOW
        if (m.getState() == FanElectricityModel.State.LOW) {
            // then put it in the state HIGH
            m.setState(FanElectricityModel.State.HIGH);
            // trigger an internal transition by toggling the electricity
            // consumption changed boolean to true
            m.toggleConsumptionHasChanged();
        }
    }
}
// -----------------------------------------------------------------------------
