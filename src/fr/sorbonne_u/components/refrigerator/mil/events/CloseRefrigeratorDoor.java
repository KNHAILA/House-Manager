package fr.sorbonne_u.components.refrigerator.mil.events;


import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

// -----------------------------------------------------------------------------

public class		CloseRefrigeratorDoor
        extends Event
        implements	RefrigeratorEventI
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public				CloseRefrigeratorDoor(
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
        // if many Refrigerator events occur at the same time, the Heat one will be
        // executed after SwitchOnRefrigerator and DoNotHeat ones but before
        // SwitchOffRefrigerator.
        if (e instanceof OnRefrigerator || e instanceof Resting) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void			executeOn(AtomicModel model)
    {
        // the Heat event can be executed either on the Refrigerator electricity
        // or temperature models

        assert	model instanceof RefrigeratorElectricityModel ||
                model instanceof RefrigeratorTemperatureModel;

        if (model instanceof RefrigeratorElectricityModel) {
            RefrigeratorElectricityModel refrigerator = (RefrigeratorElectricityModel)model;
            assert	refrigerator.getState() == RefrigeratorElectricityModel.State.ON;
            refrigerator.setState(RefrigeratorElectricityModel.State.FREEZE);
        } else if (model instanceof RefrigeratorTemperatureModel) {
            RefrigeratorTemperatureModel refrigeratorTemperature =
                    (RefrigeratorTemperatureModel)model;
            refrigeratorTemperature.setState(RefrigeratorTemperatureModel.State.FREEZE);
        }
    }
}