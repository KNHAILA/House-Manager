package fr.sorbonne_u.production_unities.windTurbine;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class WindTurbineConnector extends AbstractConnector implements WindTurbineCI {

	/**
	 * @see fr.sorbonne_u.components.windTurbine.WindTurbineCI#isRunning()
	 */
	
	@Override
	public boolean isRunning() throws Exception {
		return ((WindTurbineCI)this.offering).isRunning();
	}
	
	/**
	 * @see fr.sorbonne_u.components.windTurbine.WindTurbineCI#startWindTurbine()
	 */
	
	@Override
	public void startWindTurbine() throws Exception {
		((WindTurbineCI)this.offering).startWindTurbine();
	}
	
	/**
	 * @see fr.sorbonne_u.components.windTurbine.WindTurbineCI#stopWindTurbine()
	 */
	
	@Override
	public void stopWindTurbine() throws Exception {
		((WindTurbineCI)this.offering).stopWindTurbine();
	}
	
	@Override
	public void WindIntensityControl() throws Exception {
		((WindTurbineCI)this.offering).WindIntensityControl();
	}
}
