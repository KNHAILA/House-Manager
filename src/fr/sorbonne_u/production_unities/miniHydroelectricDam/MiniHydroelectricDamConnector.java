package fr.sorbonne_u.production_unities.miniHydroelectricDam;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.production_unities.windTurbine.WindTurbineCI;

public class MiniHydroelectricDamConnector extends AbstractConnector implements MiniHydroelectricDamCI {
	
	/**
	 * @see fr.sorbonne_u.components.miniHydroelectricDam.MiniHydroelectricDamCI#isRunning()
	 */
	
	@Override
	public boolean isRunning() throws Exception {
		return ((MiniHydroelectricDamCI)this.offering).isRunning();
	}
	
	/**
	 * @see fr.sorbonne_u.components.miniHydroelectricDam.MiniHydroelectricDamCI#startMiniHydroelectricDam()
	 */
	
	@Override
	public void startMiniHydroelectricDam() throws Exception {
		((MiniHydroelectricDamCI)this.offering).startMiniHydroelectricDam();
	}
	
	/**
	 * @see fr.sorbonne_u.components.miniHydroelectricDam.MiniHydroelectricDamCI#stopMiniHydroelectricDam()
	 */
	
	@Override
	public void stopMiniHydroelectricDam() throws Exception {
		((MiniHydroelectricDamCI)this.offering).stopMiniHydroelectricDam();
	}

	@Override
	public double getCurrentWaterVolume() throws Exception {
		return ((MiniHydroelectricDamCI)this.offering).getCurrentWaterVolume();
	}
}
