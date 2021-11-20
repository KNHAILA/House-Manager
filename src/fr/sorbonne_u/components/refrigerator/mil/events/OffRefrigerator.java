package fr.sorbonne_u.components.refrigerator.mil.events;

import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel.State;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorTemperatureModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------

public class			OffRefrigerator
        extends		ES_Event
        implements	RefrigeratorEventI
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    public	OffRefrigerator(
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
        // if many Refrigerator events occur at the same time, the
        // SwitchOffHairDryer one will be executed after all others.
        return false;
    }

    @Override
    public void			executeOn(AtomicModel model)
    {
        assert	model instanceof RefrigeratorElectricityModel ||
                model instanceof RefrigeratorTemperatureModel;

        if (model instanceof RefrigeratorElectricityModel) {
            RefrigeratorElectricityModel refrigerator = (RefrigeratorElectricityModel)model;
            assert	refrigerator.getState() != State.OFF;
            refrigerator.setState(State.OFF);
        } else if (model instanceof RefrigeratorTemperatureModel) {
            RefrigeratorTemperatureModel refrigeratorTemperature =
                    (RefrigeratorTemperatureModel)model;
            refrigeratorTemperature.setState(RefrigeratorTemperatureModel.State.OFF);
        }
    }
}
// -----------------------------------------------------------------------------
