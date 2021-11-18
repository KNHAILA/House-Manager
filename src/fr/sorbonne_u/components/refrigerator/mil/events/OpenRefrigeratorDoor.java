package fr.sorbonne_u.components.refrigerator.mil.events;

import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorElectricityModel;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>Heat</code> defines the simulation event of the Refrigerator
 * starting to heat.
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
public class		OpenRefrigeratorDoor
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

    /**
     * create a <code>Heat</code> event.
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
    public				OpenRefrigeratorDoor(
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
        // if many Refrigerator events occur at the same time, the Heat one will be
        // executed after SwitchOnRefrigerator and DoNotHeat ones but before
        // SwitchOffRefrigerator.
        if (e instanceof OnRefrigerator || e instanceof Resting) {
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
        // the Heat event can be executed either on the Refrigerator electricity
        // or temperature models

        assert	model instanceof RefrigeratorElectricityModel ||
                model instanceof RefrigeratorTemperatureModel;
        if (model instanceof RefrigeratorTemperatureModel) {
            RefrigeratorTemperatureModel refrigeratorTemperature =
                    (RefrigeratorTemperatureModel)model;
            refrigeratorTemperature.setState(RefrigeratorTemperatureModel.State.OFFSET);
        }
    }
}