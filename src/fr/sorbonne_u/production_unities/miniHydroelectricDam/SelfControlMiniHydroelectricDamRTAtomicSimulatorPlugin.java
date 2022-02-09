package fr.sorbonne_u.production_unities.miniHydroelectricDam;


import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.MiniHydroelectricDamCoupledModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.*;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.sil.WaterVolumeSILModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.sil.MiniHydroelectricDamElectricitySILModel;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.sil.MiniHydroelectricDamStateModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


//-----------------------------------------------------------------------------
/**
* The class <code>ThermostatedWindTurbineRTAtomicSimulatorPlugin</code> implements
* the simulation plug-in for the <code>ThermostatedWindTurbine</code> component.
*
* <p><strong>Description</strong></p>
* 
* <p>
* This plug-in implementation illustrates the use of the method
* {@code setSimulationRunParameters} to set the reference to the object
* representing the owner component so that simulations models can refer
* to this component to perform some operations (tracing, calling services,
* etc.). It also illustrates the use of the method {@code getModelStateValue}
* by the component code to access values in the state of simulation models
* at run time. Here, this is used to simulate a room temperature sensor by
* getting the simulated value for this in the
* {@code WindSpeedSILModel}.
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
public class			SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin
extends		RTAtomicSimulatorPlugin
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** simulation architectures can have URI to name them; this is the
	 *  URI used in this example for unit tests.							*/
	public static final String	UNIT_TEST_SIM_ARCHITECTURE_URI =
															"UnitTestMiniHydroelectricDam";
	/** name used to pass the owner component reference as simulation
	 *  parameter.															*/
	public static final String	OWNER_REFERENCE_NAME = "MHDRN";
	/** name used to access the current room temperature in the
	 *  {@code WindSpeedSILModel}.								 	*/
	public static final String	CURRENT_WATER_VOLUME = "cwv";

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// initialise the simulation parameter giving the reference to the
		// owner component before passing the parameters to the simulation
		// models
		simParams.put(OWNER_REFERENCE_NAME, this.getOwner());

		// this will pass the parameters to the simulation models that will
		// then be able to get their own parameters.
		super.setSimulationRunParameters(simParams);

		// remove the value so that the reference may not exit the context of
		// the component
		simParams.remove(OWNER_REFERENCE_NAME);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String, java.lang.String)
	 */
	@Override
	public Object		getModelStateValue(
		String modelURI,
		String name
		) throws Exception
	{
		assert	modelURI != null && name != null;

		// In the Dam model, the only accessible model state value is
		// the current room temperature in the WindSpeedModel
		assert	modelURI.equals(WaterVolumeSILModel.URI);
		assert	name.equals(CURRENT_WATER_VOLUME);

		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
		// The only model in this example that provides access to some value
		// is the WindSpeedSILModel.
		assert	m instanceof WaterVolumeSILModel;

		return ((WaterVolumeSILModel)m).getCurrentWaterVolume();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * create and set the simulation architecture internal to this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simArchURI != null}
	 * pre	{@code accFactor > 0.0}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param simArchURI	URI of the simulation architecture to be created.
	 * @param accFactor		acceleration factor used in the real time simulation.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			initialiseSimulationArchitecture(
		String simArchURI,
		double accFactor
		) throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor>
									atomicModelDescriptors = new HashMap<>();
		Map<String,CoupledModelDescriptor>
									coupledModelDescriptors = new HashMap<>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(MiniHydroelectricDamStateModel.URI);
		submodels.add(WaterVolumeSILModel.URI);

		Map<Class<? extends EventI>,ReexportedEvent> reexported = null;
		Map<EventSource, EventSink[]> connections =
									new HashMap<EventSource, EventSink[]>();

		atomicModelDescriptors.put(
				MiniHydroelectricDamStateModel.URI,
				RTAtomicModelDescriptor.create(
						MiniHydroelectricDamStateModel.class,
						MiniHydroelectricDamStateModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));
		atomicModelDescriptors.put(
				WaterVolumeSILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WaterVolumeSILModel.class,
						WaterVolumeSILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));

		if (UNIT_TEST_SIM_ARCHITECTURE_URI.equals(simArchURI)) {
			// when executed as a unit test, the simulation architecture
			// includes the WindTurbine electricity model and events
			// exported by the state model are directed to the electricity
			// model
			submodels.add(MiniHydroelectricDamElectricitySILModel.URI);

			atomicModelDescriptors.put(
					MiniHydroelectricDamElectricitySILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							MiniHydroelectricDamElectricitySILModel.class,
							MiniHydroelectricDamElectricitySILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
							accFactor));

			connections.put(
					new EventSource(MiniHydroelectricDamStateModel.URI,
									StartMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricitySILModel.URI,
										  StartMiniHydroelectricDam.class)
					});
			connections.put(
					new EventSource(MiniHydroelectricDamStateModel.URI,
							 		StopMiniHydroelectricDam.class),
					new EventSink[] {
							new EventSink(MiniHydroelectricDamElectricitySILModel.URI,
										  StopMiniHydroelectricDam.class)
					});
		} else {
			// when *not* executed as a unit test, the simulation architecture
			// does not include the Dam electricity model and events
			// exported by the state model are reexported by the coupled model

			reexported =
					new HashMap<Class<? extends EventI>,ReexportedEvent>();
			reexported.put(StartMiniHydroelectricDam.class,
						   new ReexportedEvent(MiniHydroelectricDamStateModel.URI,
								   			   StartMiniHydroelectricDam.class));
			reexported.put(StopMiniHydroelectricDam.class,
					   new ReexportedEvent(MiniHydroelectricDamStateModel.URI,
							   			   StopMiniHydroelectricDam.class));
		}

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("waterVolume",
										Double.class,
										WaterVolumeSILModel.URI),
					 new VariableSink[] {
							 new VariableSink("waterVolume",
									 		  Double.class,
									 		 MiniHydroelectricDamElectricitySILModel.URI)
					 });

		coupledModelDescriptors.put(
				MiniHydroelectricDamCoupledModel.URI,
				new RTCoupledHIOA_Descriptor(
						MiniHydroelectricDamCoupledModel.class,
						MiniHydroelectricDamCoupledModel.URI,
						submodels,
						null,
						reexported,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_RT_ENGINE,
						null,
						null,
						bindings,
						accFactor));

		// this sets the architecture in the plug-in for further reference
		// and use
		this.setSimulationArchitecture(
				new RTArchitecture(
						simArchURI,
						MiniHydroelectricDamCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS,
						accFactor));
	}
}
//-----------------------------------------------------------------------------
