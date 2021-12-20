package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface BatteryCI extends BatteryImplementation, RequiredCI, OfferedCI {

	@Override
	default void activeBattery() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	default void desactiveBattery() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	default boolean isUsing() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	default double remainingChargePercentage() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	default void chargeBattery() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	default void dechargeBattery() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
