package fr.sorbonne_u.components.refrigerator.mil.events;

import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel.State;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

// -----------------------------------------------------------------------------
public class			Resting
        extends		Event
        implements	RefrigeratorEventI
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public				Resting(
            Time timeOfOccurrence
    )
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public boolean		hasPriorityOver(EventI e)
    {
        // if many Refrigerator events occur at the same time, the DoNotHeat one
        // will be executed first except for SwitchOnRefrigerator ones.
        if (e instanceof OnRefrigerator) {
            return false;
        } else {
            return true;
        }
    }

 
    @Override
    public void			executeOn(AtomicModel model)
    {

        // the DoNotHeat event can be executed either on the Refrigerator electricity
        // or temperature models
        assert	model instanceof RefrigeratorElectricityModel ||
                model instanceof RefrigeratorTemperatureModel;

        if (model instanceof RefrigeratorElectricityModel) {
            RefrigeratorElectricityModel refrigerator = (RefrigeratorElectricityModel)model;
            assert	refrigerator.getState() == State.FREEZE;
            refrigerator.setState(State.ON);
        } else if (model instanceof RefrigeratorTemperatureModel) {
            RefrigeratorTemperatureModel refrigeratorTemperature =
                    (RefrigeratorTemperatureModel)model;
            refrigeratorTemperature.setState(RefrigeratorTemperatureModel.State.REST);
        }
    }
}
// -----------------------------------------------------------------------------
