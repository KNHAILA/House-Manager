package fr.sorbonne_u.components.fan.mil.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.fan.mil.FanElectricityModel;
// -----------------------------------------------------------------------------
/**
 * The class <code>SetHighFan</code> defines the simulation event of the
 * fan being set to high temperature mode.
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
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class SetHighFan extends	AbstractFanEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a <code>SetHighFan</code> event.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code timeOfOccurrence != null}
     * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
     * </pre>
     *
     * @param timeOfOccurrence	time of occurrence of the event.
     */
    public	SetHighFan(Time timeOfOccurrence)
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
        // if many fan events occur at the same time, the
        // SetHighFan one will be executed after SwitchOnFan
        // and SetLowFan ones but before SwitchOffFan.
        if (e instanceof SwitchOnFan || e instanceof SetLowFan) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
     */
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
