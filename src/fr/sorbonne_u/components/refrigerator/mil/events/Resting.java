package fr.sorbonne_u.components.refrigerator.mil.events;

import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>DoNotHeat</code> defines the simulation event of the
 * heater stopping to heat.
 *
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Invariant</strong></p>
 *
 * <pre>
 * invariant	true
 * </pre>
 *
 * <p>Created on : 2021-09-21</p>
 *
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
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

    /**
     * create a <code>DoNotHeat</code> event.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code timeOfOccurrence != null}
     * post	{@code this.getTimeOfOccurrence().equals(timeOfOccurrence)}
     * post	{@code this.getEventInformation.equals(content)}
     * </pre>
     *
     * @param timeOfOccurrence	time of occurrence of the event.
     */
    public				Resting(
            Time timeOfOccurrence
    )
    {
        super(timeOfOccurrence, null);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
     */
    @Override
    public boolean		hasPriorityOver(EventI e)
    {
        // if many heater events occur at the same time, the DoNotHeat one
        // will be executed first except for SwitchOnHeater ones.
        if (e instanceof OnRefrigerator) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
     */
    @Override
    public void			executeOn(AtomicModel model)
    {
        // the DoNotHeat event can be executed either on the heater electricity
        // or temperature models
        assert	model instanceof RefrigeratorElectricityModel ||
                model instanceof RefrigeratorTemperatureModel;

        if (model instanceof RefrigeratorElectricityModel) {
            RefrigeratorElectricityModel refrigerator = (RefrigeratorElectricityModel)model;
            assert	refrigerator.getState() == FREEZING;
            refrigerator.setState(State.ON);
        } else if (model instanceof RefrigeratorTemperatureModel) {
            RefrigeratorTemperatureModel refrigeratorTemperature =
                    (RefrigeratorTemperatureModel)model;
            refrigeratorTemperature.setState(RefrigeratorTemperatureModel.State.RESTING);
        }
    }
}
// -----------------------------------------------------------------------------
