package fr.sorbonne_u.production_unities.miniHydroelectricDam;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface MiniHydroelectricDamCI 
extends		MiniHydroelectricDamImplementation,
RequiredCI,
OfferedCI
{
	@Override
	default void startMiniHydroelectricDam() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	default void stopMiniHydroelectricDam() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	default boolean isRunning() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}