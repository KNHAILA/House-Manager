package fr.sorbonne_u.components.waterHeater;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterCoupledModel;
import fr.sorbonne_u.components.waterHeater.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOffWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOnWaterHeater;
import fr.sorbonne_u.components.waterHeater.sil.WaterHeaterExternalTemperatureSILModel;
import fr.sorbonne_u.components.waterHeater.sil.WaterHeaterElectricitySILModel;
import fr.sorbonne_u.components.waterHeater.sil.WaterHeaterStateModel;
import fr.sorbonne_u.components.waterHeater.sil.WaterHeaterTemperatureSILModel;

// -----------------------------------------------------------------------------
/**
 * The class <code>ThermostatedHeaterRTAtomicSimulatorPlugin</code> implements
 * the simulation plug-in for the <code>ThermostatedHeater</code> component.
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
 * {@code HeaterTemperatureSILModel}.
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
public class			ThermostatedWaterHeaterRTAtomicSimulatorPlugin
extends		RTAtomicSimulatorPlugin
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** simulation architectures can have URI to name them; this is the
	 *  URI used in this example for unit tests.							*/
	public static final String	UNIT_TEST_SIM_ARCHITECTURE_URI =
															"UnitTestWaterHeater";
	/** name used to pass the owner component reference as simulation
	 *  parameter.															*/
	public static final String	OWNER_REFERENCE_NAME = "THCRN";
	/** name used to access the current room temperature in the
	 *  {@code HeaterTemperatureSILModel}.								 	*/
	public static final String	CURRENT_ROOM_TERMPERATURE = "crt";

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

		// In the heater model, the only accessible model state value is
		// the current room temperature in the HeaterTemperatureModel
		assert	modelURI.equals(WaterHeaterTemperatureSILModel.URI);
		assert	name.equals(CURRENT_ROOM_TERMPERATURE);

		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
		// The only model in this example that provides access to some value
		// is the HeaterTemperatureSILModel.
		assert	m instanceof WaterHeaterTemperatureSILModel;

		return ((WaterHeaterTemperatureSILModel)m).getCurrentRoomTemperature();
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
		submodels.add(WaterHeaterStateModel.URI);
		submodels.add(WaterHeaterExternalTemperatureSILModel.URI);
		submodels.add(WaterHeaterTemperatureSILModel.URI);

		Map<Class<? extends EventI>,ReexportedEvent> reexported = null;
		Map<EventSource, EventSink[]> connections =
									new HashMap<EventSource, EventSink[]>();

		atomicModelDescriptors.put(
				WaterHeaterStateModel.URI,
				RTAtomicModelDescriptor.create(
						WaterHeaterStateModel.class,
						WaterHeaterStateModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));
		atomicModelDescriptors.put(
				WaterHeaterExternalTemperatureSILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WaterHeaterExternalTemperatureSILModel.class,
						WaterHeaterExternalTemperatureSILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));
		atomicModelDescriptors.put(
				WaterHeaterTemperatureSILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WaterHeaterTemperatureSILModel.class,
						WaterHeaterTemperatureSILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));

		if (UNIT_TEST_SIM_ARCHITECTURE_URI.equals(simArchURI)) {
			// when executed as a unit test, the simulation architecture
			// includes the heater electricity model and events
			// exported by the state model are directed to the electricity
			// model
			submodels.add(WaterHeaterElectricitySILModel.URI);

			atomicModelDescriptors.put(
					WaterHeaterElectricitySILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							WaterHeaterElectricitySILModel.class,
							WaterHeaterElectricitySILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
							accFactor));

			connections.put(
					new EventSource(WaterHeaterStateModel.URI,
									SwitchOnWaterHeater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricitySILModel.URI,
										  SwitchOnWaterHeater.class)
					});
			connections.put(
					new EventSource(WaterHeaterStateModel.URI,
							 		SwitchOffWaterHeater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricitySILModel.URI,
										  SwitchOffWaterHeater.class)
					});
			connections.put(
					new EventSource(WaterHeaterStateModel.URI,
									HeatWater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricitySILModel.URI,
										  HeatWater.class),
							new EventSink(WaterHeaterTemperatureSILModel.URI,
									  	  HeatWater.class)
					});
			connections.put(
					new EventSource(WaterHeaterStateModel.URI,
									DoNotHeatWater.class),
					new EventSink[] {
							new EventSink(WaterHeaterElectricitySILModel.URI,
										  DoNotHeatWater.class),
							new EventSink(WaterHeaterTemperatureSILModel.URI,
										  DoNotHeatWater.class)
					});
		} else {
			// when *not* executed as a unit test, the simulation architecture
			// does not include the hair dryer electricity model and events
			// exported by the state model are reexported by the coupled model

			connections.put(
					new EventSource(WaterHeaterStateModel.URI,
									HeatWater.class),
					new EventSink[] {
							new EventSink(WaterHeaterTemperatureSILModel.URI,
									  	  HeatWater.class)
					});
			connections.put(
					new EventSource(WaterHeaterStateModel.URI,
									DoNotHeatWater.class),
					new EventSink[] {
							new EventSink(WaterHeaterTemperatureSILModel.URI,
										  DoNotHeatWater.class)
					});

			reexported =
					new HashMap<Class<? extends EventI>,ReexportedEvent>();
			reexported.put(SwitchOnWaterHeater.class,
						   new ReexportedEvent(WaterHeaterStateModel.URI,
								   			   SwitchOnWaterHeater.class));
			reexported.put(SwitchOffWaterHeater.class,
					   new ReexportedEvent(WaterHeaterStateModel.URI,
							   			   SwitchOffWaterHeater.class));
			reexported.put(HeatWater.class,
					   new ReexportedEvent(WaterHeaterStateModel.URI,
							   			   HeatWater.class));
			reexported.put(DoNotHeatWater.class,
					   new ReexportedEvent(WaterHeaterStateModel.URI,
							   			   DoNotHeatWater.class));
		}

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("externalTemperature",
										Double.class,
										WaterHeaterExternalTemperatureSILModel.URI),
					 new VariableSink[] {
							 new VariableSink("externalTemperature",
									 		  Double.class,
									 		  WaterHeaterTemperatureSILModel.URI)
					 });

		coupledModelDescriptors.put(
				WaterHeaterCoupledModel.URI,
				new RTCoupledHIOA_Descriptor(
						WaterHeaterCoupledModel.class,
						WaterHeaterCoupledModel.URI,
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
						WaterHeaterCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS,
						accFactor));
	}
}
// -----------------------------------------------------------------------------
