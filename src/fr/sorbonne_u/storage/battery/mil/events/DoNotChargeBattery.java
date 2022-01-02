package fr.sorbonne_u.storage.battery.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel;
import fr.sorbonne_u.storage.battery.mil.BatteryPercentageModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>DoNotChargeBattery</code> defines the simulation event of the
 * Battery stopping to ChargeBattery.
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
 * Created on : 2021-09-21
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class DoNotChargeBattery extends Event implements BatteryEventI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>DoNotChargeBattery</code> event.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * post	{@code this.getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code this.getEventInformation.equals(content)}
	 * </pre>
	 *
	 * @param timeOfOccurrence time of occurrence of the event.
	 */
	public DoNotChargeBattery(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		// if many Battery events occur at the same time, the DoNotChargeBattery one
		// will be executed first except for SwitchOnBattery ones.
		if (e instanceof ChargeBattery) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		// the DoNotChargeBattery event can be executed either on the Battery
		// electricity or Percentage models

		assert model instanceof BatteryElectricityModel || model instanceof BatteryPercentageModel;

		if (model instanceof BatteryElectricityModel) {
			BatteryElectricityModel m = (BatteryElectricityModel) model;
			if (m.getState() == BatteryElectricityModel.State.CHARGE) {
				m.setState(BatteryElectricityModel.State.REST);
				m.toggleConsumptionHasChanged();
			} else if (m.getState() == BatteryElectricityModel.State.USE_CHARGE) {
				m.setState(BatteryElectricityModel.State.USE);
				m.toggleConsumptionHasChanged();
			}
		}

		else if (model instanceof BatteryPercentageModel) {
			BatteryPercentageModel batteryPercentage = (BatteryPercentageModel) model;
			batteryPercentage.setState(BatteryPercentageModel.State.NOT_CHARGING);
			
			if(batteryPercentage.electricityState.v == BatteryElectricityModel.State.REST) {
				batteryPercentage.setState(BatteryPercentageModel.State.NOT_CHARGING);
			}else {
				batteryPercentage.setState(BatteryPercentageModel.State.DISCHARGING);
			}
		}
	}
}
// -----------------------------------------------------------------------------
