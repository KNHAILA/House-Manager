package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.components.refrigerator.RefrigeratorImplementationI;


public class BatteryInboundPort
extends		AbstractInboundPort
implements	BatteryCI {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BatteryInboundPort(ComponentI owner) throws Exception
	{
		super(BatteryCI.class, owner);
	}

	public	BatteryInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, BatteryCI.class, owner);
	}

	@Override
	public void activeBattery() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((BatteryImplementation)o).activeBattery();
						return null;
					 });
	}

	@Override
	public void desactiveBattery() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((BatteryImplementation)o).desactiveBattery();
						return null;
					 });
	}

	@Override
	public boolean isUsing() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatteryImplementation)o).isUsing());
	}

	@Override
	public double remainingChargePercentage() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatteryImplementation)o).remainingChargePercentage());
	}

	@Override
	public void chargeBattery() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((BatteryImplementation)o).chargeBattery();
						return null;
					 });
	}

	@Override
	public void dechargeBattery() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((BatteryImplementation)o).dechargeBattery();
						return null;
					 });
	}
}
