package fr.sorbonne_u.storage.battery.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;
import fr.sorbonne_u.storage.battery.mil.BatteryPercentageModel;


// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOnBattery</code> defines the simulation event of the
 * Battery being switched on.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 *
 * <p>
 * <strong>Invariant</strong>
 * </p>
 *
 * <pre>
 * invariant	true
 * </pre>
 *
 * <p>
 * Created on : 2021-09-20
 * </p>
 *
 * @author <a href="mailto:Jacques.MalenBatteryt@lip6.fr">Jacques
 *         MalenBatteryt</a>
 */
public class			DoNotUseBattery
extends		ES_Event
implements	BatteryEventI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>DoNotUseBattery</code> event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * post	{@code this.getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public				DoNotUseBattery(
		Time timeOfOccurrence
		)
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
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof ChargeBattery ) {
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
		
		assert model instanceof BatteryElectricityModel || model instanceof BatteryPercentageModel;

		if (model instanceof BatteryElectricityModel) {
			BatteryElectricityModel m = (BatteryElectricityModel) model;
			if (m.getState() == BatteryElectricityModel.State.USE) {
				m.setState(BatteryElectricityModel.State.REST);
				m.toggleConsumptionHasChanged();
			}
			else if (m.getState() == BatteryElectricityModel.State.USE_CHARGE) {
				m.setState(BatteryElectricityModel.State.CHARGE);
				m.toggleConsumptionHasChanged();
			}
		}

		else if (model instanceof BatteryPercentageModel) {
			BatteryPercentageModel batteryPercentage = (BatteryPercentageModel) model;
			
			if(batteryPercentage.electricityState.v == BatteryElectricityModel.State.CHARGE) {
				batteryPercentage.setState(BatteryPercentageModel.State.CHARGING);
			}else {
				batteryPercentage.setState(BatteryPercentageModel.State.NOT_CHARGING);
			}
		}
	}
}
