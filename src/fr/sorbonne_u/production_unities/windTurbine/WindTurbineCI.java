package fr.sorbonne_u.production_unities.windTurbine;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>WindTurbineCI</code> defines the services offered by and
 * that can be required from an electric meter component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-09-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		WindTurbineCI
extends		WindTurbineImplementation,
			RequiredCI,
			OfferedCI
{

	@Override
	default void startWindTurbine() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	default void stopWindTurbine() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	default boolean isRunning() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	default void WindIntensityControl() throws Exception {
		// TODO Auto-generated method stub
		
	}
}