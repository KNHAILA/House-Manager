package fr.sorbonne_u.production_unities.miniHydroelectricDam.sil;


import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.utils.StandardComponentLogger;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamElectricityModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamElectricityModel;

import java.util.Map;

//-----------------------------------------------------------------------------
/**
* The class <code>DameElectricitySILModel</code> extends the base
* {@code DameElectricityModel} to cater for its execution inside
* the {@code ElectricMeter} component.
*
* <p><strong>Description</strong></p>
* 
* <p>
* When executing MIL simulations, simulation models trace their actions by
* printing messages using their own tracing service. When executed inside
* components, it is better that the messages are printed using the component
* trace service. To enable that, we use the {@code setSimulationRunParameters}
* method to retrieve the reference on the component that is holding the
* simulation model. Then, this reference can be used by a specific logger for
* simulation models, {@code StandardComponentLogger}, that use the component
* trace service rather than the standard simulation models tracing service.
* </p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant		true
* </pre>
* 
* <p>Created on : 2021-10-05</p>
* 
* @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
*/
public class			MiniHydroelectricDamElectricitySILModel
extends		MiniHydroelectricDamElectricityModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** owner component.													*/
	protected AbstractComponent		owner;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a Dame electricity model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof HIOA_AtomicEngine}
	 * post	{@code getURI() != null}
	 * post	{@code uri != null implies this.getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code simulationEngine != null implies getSimulationEngine().equals(simulationEngine)}
	 * post	{@code !isDebugModeOn()}
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				MiniHydroelectricDamElectricitySILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		super.setSimulationRunParameters(simParams);

		// retrieve the reference to the owner component that must be passed
		// as a simulation run parameter
		assert	simParams.containsKey(
						SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin.
														OWNER_REFERENCE_NAME);
		this.owner =
				(AbstractComponent) simParams.get(
						SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin.
														OWNER_REFERENCE_NAME);
		// replace the logger set in the superclass by the one directing
		// logs to the owner component logger
		this.setLogger(new StandardComponentLogger(this.owner));
	}
}
//-----------------------------------------------------------------------------
