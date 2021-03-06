package fr.sorbonne_u.storage.battery.mil.events;


import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;
import fr.sorbonne_u.storage.battery.mil.BatteryPercentageModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;

public class			UseBattery
extends		ES_Event
implements	BatteryEventI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>UseBattery</code> event.
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
	public				UseBattery(
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
		// if many Battery events occur at the same time, the
		// UseBattery one will be executed first.
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void			executeOn(AtomicModel model)
	{        
		assert model instanceof BatteryElectricityModel || model instanceof BatteryPercentageModel;

		if (model instanceof BatteryElectricityModel) {
			BatteryElectricityModel m = (BatteryElectricityModel)model;
	        if (m.getState() == BatteryElectricityModel.State.REST) {
	            m.setState(BatteryElectricityModel.State.USE);
	            m.toggleConsumptionHasChanged();
	        } else if (m.getState() == BatteryElectricityModel.State.CHARGE) {
	            m.setState(BatteryElectricityModel.State.USE_CHARGE);
	            m.toggleConsumptionHasChanged();
	        }
		}

		else if (model instanceof BatteryPercentageModel) {
			BatteryPercentageModel batteryPercentage = (BatteryPercentageModel) model;
			if(batteryPercentage.electricityState.v == BatteryElectricityModel.State.USE_CHARGE) {
				batteryPercentage.setState(BatteryPercentageModel.State.NOT_CHARGING);
			}else {
				batteryPercentage.setState(BatteryPercentageModel.State.DISCHARGING);
			}
		}
	}
}


