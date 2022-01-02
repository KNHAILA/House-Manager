package fr.sorbonne_u.storage.battery.mil.events;


import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;
import fr.sorbonne_u.storage.battery.mil.BatteryPercentageModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>ChargeBattery</code> defines the simulation event of the Battery
 * starting to ChargeBattery.
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
public class			ChargeBattery
extends		Event
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
	 * create a <code>ChargeBattery</code> event.
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
	public				ChargeBattery(
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
		if (e instanceof UseBattery || e instanceof DoNotChargeBattery) {
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
		assert	model instanceof BatteryElectricityModel ||	model instanceof BatteryPercentageModel;

		if (model instanceof BatteryElectricityModel) {
			
			BatteryElectricityModel m = (BatteryElectricityModel)model;
		    if (m.getState() == BatteryElectricityModel.State.REST) {
		        m.setState(BatteryElectricityModel.State.CHARGE);
		        m.toggleConsumptionHasChanged();
		    } 
		    else if (m.getState() == BatteryElectricityModel.State.USE) {
		        m.setState(BatteryElectricityModel.State.USE_CHARGE);
		        m.toggleConsumptionHasChanged();
		    }
		}

		else if (model instanceof BatteryPercentageModel) {
			BatteryPercentageModel batteryPercentage =
							(BatteryPercentageModel)model;
			batteryPercentage.setState(BatteryPercentageModel.State.CHARGING);
			
		}
	}
}
