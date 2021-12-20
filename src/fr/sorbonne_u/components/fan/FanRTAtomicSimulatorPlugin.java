package fr.sorbonne_u.components.fan;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.fan.mil.FanCoupledModel;
import fr.sorbonne_u.components.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.fan.sil.FanElectricitySILModel;
import fr.sorbonne_u.components.fan.sil.FanStateModel;
import fr.sorbonne_u.components.fan.sil.FanUserModel;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerRTAtomicSimulatorPlugin</code> defines the plug-in
 * that manages the SIL simulation inside the hair dryer component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-10-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			FanRTAtomicSimulatorPlugin
extends		RTAtomicSimulatorPlugin
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** simulation architectures can have URI to name them; this is the
	 *  URI used in this example for unit tests.											*/
	public static final String		UNIT_TEST_SIM_ARCHITECTURE_URI =
														"UnitTestHairDryer";
	/** name used to pass the owner component reference as simulation
	 *  parameter.															*/
	public static final String		OWNER_REFERENCE_NAME = "HDCRN";

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

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * create and set the simulation architecture internal to this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simArchURI != null && !simArchURIisEmpty()}
	 * pre	{@code accFactor > 0.0}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param simArchURI	URI of the simulation architecture to be created.
	 * @param accFactor				acceleration factor used in the real time simulation.
	 * @throws Exception			<i>to do</i>.
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
		submodels.add(FanUserModel.URI);
		submodels.add(FanStateModel.URI);

		Map<Class<? extends EventI>,ReexportedEvent> reexported = null;
		Map<EventSource, EventSink[]> connections = null;

		atomicModelDescriptors.put(
				FanUserModel.URI,
				RTAtomicModelDescriptor.create(
						FanUserModel.class,
						FanUserModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));
		atomicModelDescriptors.put(
				FanStateModel.URI,
				RTAtomicModelDescriptor.create(
						FanStateModel.class,
						FanStateModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));

		if (simArchURI.equals(UNIT_TEST_SIM_ARCHITECTURE_URI)) {
			// when executed as a unit test, the simulation architecture
			// includes the hair dryer electricity model and events exported
			// by the state model are directed to the electricity model
			submodels.add(FanElectricitySILModel.URI);

			atomicModelDescriptors.put(
					FanElectricitySILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							FanElectricitySILModel.class,
							FanElectricitySILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
							accFactor));

			connections = new HashMap<EventSource, EventSink[]>();
			EventSource source =
					new EventSource(FanStateModel.URI,
									SwitchOnFan.class);
			EventSink[] sinks =
					new EventSink[] {
							new EventSink(FanElectricitySILModel.URI,
										  SwitchOnFan.class)
					};
			connections.put(source, sinks);
			source = new EventSource(FanStateModel.URI,
									 SwitchOffFan.class);
			sinks = new EventSink[] {
							new EventSink(FanElectricitySILModel.URI,
										  SwitchOffFan.class)
					};
			connections.put(source, sinks);
			source = new EventSource(FanStateModel.URI,
									 SetHighFan.class);
			sinks = new EventSink[] {
							new EventSink(FanElectricitySILModel.URI,
										  SetHighFan.class)
					};
			connections.put(source, sinks);
			source = new EventSource(FanStateModel.URI,
									 SetLowFan.class);
			sinks = new EventSink[] {
							new EventSink(FanElectricitySILModel.URI,
										  SetLowFan.class)
					};
			connections.put(source, sinks);
		} else {
			// when *not% executed as a unit test, the simulation architecture
			// does not include the hair dryer electricity model and events
			// exported by the state model are reexported by the coupled model

			reexported =
					new HashMap<Class<? extends EventI>,ReexportedEvent>();
			reexported.put(
					SwitchOnFan.class,
					new ReexportedEvent(FanStateModel.URI,
										SwitchOnFan.class));
			reexported.put(
					SwitchOffFan.class,
					new ReexportedEvent(FanStateModel.URI,
										SwitchOffFan.class));
			reexported.put(
					SetHighFan.class,
					new ReexportedEvent(FanStateModel.URI,
										SetHighFan.class));
			reexported.put(
					SetLowFan.class,
					new ReexportedEvent(FanStateModel.URI,
										SetLowFan.class));
		}

		coupledModelDescriptors.put(
				FanCoupledModel.URI,
				new RTCoupledModelDescriptor(
						FanCoupledModel.class,
						FanCoupledModel.URI,
						submodels,
						null,
						reexported,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_RT_ENGINE,
						accFactor));

		// this sets the architecture in the plug-in for further reference
		// and use
		this.setSimulationArchitecture(
				new RTArchitecture(
						simArchURI,
						FanCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS,
						accFactor));
	}
}
// -----------------------------------------------------------------------------
