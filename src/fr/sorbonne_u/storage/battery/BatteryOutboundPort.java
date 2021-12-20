package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class BatteryOutboundPort extends AbstractOutboundPort implements BatteryCI {

	private static final long serialVersionUID = 1L;

	public BatteryOutboundPort(ComponentI owner)
			throws Exception {
		super(BatteryCI.class, owner);
	}

	public BatteryOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, BatteryCI.class, owner);
	}

	@Override
	public void activeBattery() throws Exception {
		((BatteryCI)this.getConnector()).activeBattery();
	}

	@Override
	public void desactiveBattery() throws Exception {
		((BatteryCI)this.getConnector()).desactiveBattery();
	}

	@Override
	public boolean isUsing() throws Exception {
		return ((BatteryCI)this.getConnector()).isUsing();
	}

	@Override
	public double remainingChargePercentage() throws Exception {
		return ((BatteryCI)this.getConnector()).remainingChargePercentage();
	}

	@Override
	public void chargeBattery() throws Exception {
		((BatteryCI)this.getConnector()).chargeBattery();
	}

	@Override
	public void dechargeBattery() throws Exception {
		((BatteryCI)this.getConnector()).dechargeBattery();
	}
}
