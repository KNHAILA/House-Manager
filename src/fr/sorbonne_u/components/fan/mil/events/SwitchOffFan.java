package fr.sorbonne_u.components.fan.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.fan.mil.FanElectricityModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOffFan</code> defines the simulation event of the
 * fan being switched off.
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
public class SwitchOffFan extends AbstractFanEvent
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create a <code>SwitchOffFan</code> event.
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
    public	SwitchOffFan(Time timeOfOccurrence)
    {
        super(timeOfOccurrence, null);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
     */
    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        // if many fan events occur at the same time, the
        // SwitchOffFan one will be executed after all others.
        return false;
    }

    /**
     * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
     */
    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FanElectricityModel;

        FanElectricityModel m = ((FanElectricityModel)model);
        // a SwitchOff event can be executed when the state of the hair
        // dryer model is *not* in the state OFF
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
