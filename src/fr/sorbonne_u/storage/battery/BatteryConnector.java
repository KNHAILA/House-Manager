package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class BatteryConnector extends AbstractConnector implements BatteryCI {

	@Override
	public void activeBattery() throws Exception {
		((BatteryCI)this.offering).activeBattery();
	}

	@Override
	public void desactiveBattery() throws Exception {
		((BatteryCI)this.offering).desactiveBattery();
	}

	@Override
	public boolean isUsing() throws Exception {
		return ((BatteryCI)this.offering).isUsing();
	}

	@Override
	public double remainingChargePercentage() throws Exception {
		return ((BatteryCI)this.offering).remainingChargePercentage();
	}

	@Override
	public void chargeBattery() throws Exception {
		((BatteryCI)this.offering).chargeBattery();
	}

	@Override
	public void dechargeBattery() throws Exception {
		((BatteryCI)this.offering).dechargeBattery();
	}
}
