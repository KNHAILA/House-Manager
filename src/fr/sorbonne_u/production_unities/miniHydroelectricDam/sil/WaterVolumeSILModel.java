package fr.sorbonne_u.production_unities.miniHydroelectricDam.sil;


import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.utils.StandardComponentLogger;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.SelfControlMiniHydroelectricDam;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.WaterVolumeModel;

import java.util.Map;

//-----------------------------------------------------------------------------
/**
* The class <code>WaterVolumeSILModel</code> extends the base
* {@code HeaterTemperatureModel} to cater for its execution inside
* the {@code SelfControlMiniHydroelectricDam} component.
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
* invariant	true
* </pre>
* 
* <p>Created on : 2021-10-05</p>
* 
 *  @authors	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 *              <a href="maedeh.daemi@etu.sorbonne-universite.fr">DAEMI Maedeh</a>
*/
public class			WaterVolumeSILModel
extends		WaterVolumeModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** owner component.													*/
	protected SelfControlMiniHydroelectricDam	owner;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a heater temperature model instance.
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
	public				WaterVolumeSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return the current room temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return	the current room temperature.
	 */
	public double		getCurrentWaterVolume()
	{
		return this.waterVolume.v;
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
				(SelfControlMiniHydroelectricDam) simParams.get(
						SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin.
														OWNER_REFERENCE_NAME);
		// replace the logger set in the superclass by the one directing
		// logs to the owner component logger
		this.setLogger(new StandardComponentLogger(this.owner));
	}
}
//-----------------------------------------------------------------------------
