package fr.sorbonne_u;

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

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.fan.Fan;
import fr.sorbonne_u.components.fan.mil.FanCoupledModel;
import fr.sorbonne_u.components.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.refrigerator.Refrigerator;
import fr.sorbonne_u.components.refrigerator.mil.RefrigeratorCoupledModel;
import fr.sorbonne_u.components.refrigerator.mil.events.*;
import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterCoupledModel;
import fr.sorbonne_u.components.waterHeater.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.HeatWater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOffWaterHeater;
import fr.sorbonne_u.components.waterHeater.mil.events.SwitchOnWaterHeater;
import fr.sorbonne_u.components.waterHeater.ThermostatedWaterHeater;
import fr.sorbonne_u.meter.ElectricMeter;
import fr.sorbonne_u.meter.sil.ElectricMeterCoupledModel;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTCoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentModelArchitecture;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEM_SIL_Supervisor</code> implements the supervisor component
 * to perform software-in-the-loop simulations of the household energy manager
 * project.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In SIL simulations for testing or other purposes, supervisor components
 * have the responsibility: 
 * </p>
 * <ul>
 * <li>to create the overall simulation architecture over components from the
 *   local simulation architectures created in each participant component,</li>
 * <li>to perform the simulation campaign by successively starting simulation
 *   runs, often with different simulation run parameters, and</li>
 * <li>to process and analyse the results of the simulation campaign from the
 *   reports returned after each run.</li>
 * </ul>
 * <p>
 * To do so, the component uses the supervisor plug-in provided by
 * BCM4Java-CyPhy to which it passes the global simulation architecture and
 * then uses its methods to create the global simulator, run simulations and
 * get back the simulation reports.
 * </p>
 * <p>
 * In SIL simulations, the simulation architecture (composed of DEVS atomic
 * and coupled models) has a specific form. Each component defines its
 * local architecture that may be a composition of atomic and coupled models
 * with a local root coupled model. Each local architecture is then seen as an
 * atomic model, thanks to the DEVS closed over composition property (the
 * composition of models under a coupled model also defines an atomic model
 * hiding its internal structure). Hence, the global architecture defined by
 * the supervisor sees all of these local architectures to be composed under
 * a (hierarchy of) coupled models.
 * </p>
 * <p>
 * Coupled models appearing in the global simulation architecture must be run
 * by components. At this time, the simulation library and BCM4Java-CyPhy
 * impose that each of these coupled models are run by distinct components
 * that are seen as simulation coordinator components. When creating the
 * simulation architecture, BCM4Java-CyPhy interconnects the involved
 * components through component interfaces, ports and connectors (all of
 * them predefined by BCM4Java-CyPhy) that will allow for managing and running
 * the simulations.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-10-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			HEM_SIL_Supervisor
extends		AbstractCyPhyComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** simulation architectures can have URI to name them; this is the
	 *  URI used in this example.											*/
	public static final String		SIM_ARCHITECTURE_URI = "global";
	/** URI of the supervisor plug-in.										*/
	protected static final String	SUPERVISOR_PLUGIN_URI = "supervisor-uri";
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;

	/** the supervisor plug-in attached to this component.					*/
	protected SupervisorPlugin		sp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the supervisor component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	{@code isInstalled(SUPERVISOR_PLUGIN_URI)}
	 * </pre>
	 *
	 */
	protected			HEM_SIL_Supervisor()
	{
		super(1, 0);
		this.initialise();
	}

	/**
	 * create the supervisor component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * post	{@code isInstalled(SUPERVISOR_PLUGIN_URI)}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port.
	 */
	protected			HEM_SIL_Supervisor(
		String reflectionInboundPortURI
		)
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise();
	}

	/**
	 * initialise the supervisor plug-in of this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	{@code isInstalled(SUPERVISOR_PLUGIN_URI)}
	 * </pre>
	 *
	 */
	protected void		initialise()
	{
		try {
			this.sp = new SupervisorPlugin(this.createArchitecture());
			this.sp.setPluginURI(SUPERVISOR_PLUGIN_URI);
			this.installPlugin(this.sp);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		if (VERBOSE) {
			this.tracer.get().setTitle("Supervisor component");
			this.tracer.get().setRelativePosition(0, 0);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * create the global simulation architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the global simulation architecture.
	 * @throws Exception	<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	protected ComponentModelArchitecture	createArchitecture()
	throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// Descriptors for atomic models included in components must define
		// simulation models specific information plus the reflection inbound
		// port URI of the component holding them in order to connect this
		// component inside the component-based simulation architecture.

		// The fan simulation model held by the Fan component.
		atomicModelDescriptors.put(
				FanCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						FanCoupledModel.URI,
						new Class[]{},
						new Class[]{
								SwitchOnFan.class,
								SwitchOffFan.class,
								SetHighFan.class, SetLowFan.class},
						TimeUnit.SECONDS,
						Fan.REFLECTION_INBOUND_PORT_URI));

		// ThermostatedHeater
		atomicModelDescriptors.put(
				WaterHeaterCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						WaterHeaterCoupledModel.URI,
						new Class[]{},
						new Class[]{
								SwitchOnWaterHeater.class, SwitchOffWaterHeater.class,
								HeatWater.class, DoNotHeatWater.class},
						TimeUnit.SECONDS,
						ThermostatedWaterHeater.REFLECTION_INBOUND_PORT_URI));
		
		// Refrigerator
		atomicModelDescriptors.put(
				RefrigeratorCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						RefrigeratorCoupledModel.URI,
						new Class[]{},
						new Class[]{
								CloseRefrigeratorDoor.class, Freezing.class,
								OffRefrigerator.class, OnRefrigerator.class,
								OpenRefrigeratorDoor.class, Resting.class},
						TimeUnit.SECONDS,
						Refrigerator.REFLECTION_INBOUND_PORT_URI));

		// The electric meter simulation model held by the ElectricMeter
		// component.
		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.URI,
						new Class[]{
								SwitchOnWaterHeater.class, SwitchOffWaterHeater.class,
								HeatWater.class, DoNotHeatWater.class,
								SwitchOnFan.class,
								SwitchOffFan.class,
								SetHighFan.class, SetLowFan.class, CloseRefrigeratorDoor.class, Freezing.class,
								OffRefrigerator.class, OnRefrigerator.class,
								OpenRefrigeratorDoor.class, Resting.class},
						new Class[]{},
						TimeUnit.SECONDS,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		Set<String> submodels = new HashSet<String>();
		submodels.add(FanCoupledModel.URI);
		submodels.add(WaterHeaterCoupledModel.URI);
		submodels.add(RefrigeratorCoupledModel.URI);
		submodels.add(ElectricMeterCoupledModel.URI);

		Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

		// In the component-based simulation architecture, electric consumption
		// simulation models of appliances must be co-located with the electric
		// meter simulation model to share continuous variables. Hence, events
		// coming from the appliances towards their electric consumption models
		// must pass from their components to the ElectricMeter component as
		// shown in the next connections.
										
        //Fan										
		connections.put(
				new EventSource(FanCoupledModel.URI,
								SwitchOnFan.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
									  SwitchOnFan.class)
				});
		connections.put(
				new EventSource(FanCoupledModel.URI,
								SwitchOffFan.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
									  SwitchOffFan.class)
				});
		connections.put(
				new EventSource(FanCoupledModel.URI,
								SetHighFan.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
									  SetHighFan.class)
				});
		connections.put(
				new EventSource(FanCoupledModel.URI,
								SetLowFan.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
									  SetLowFan.class)
				});
		
		
		//Water heater
		connections.put(
				new EventSource(WaterHeaterCoupledModel.URI,
								SwitchOnWaterHeater.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
									  SwitchOnWaterHeater.class)
				});
		connections.put(
				new EventSource(WaterHeaterCoupledModel.URI,
								SwitchOffWaterHeater.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
									  SwitchOffWaterHeater.class)
				});
		connections.put(
				new EventSource(WaterHeaterCoupledModel.URI,
								HeatWater.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
									  HeatWater.class)
				});
		connections.put(
				new EventSource(WaterHeaterCoupledModel.URI,
								DoNotHeatWater.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
									  DoNotHeatWater.class)
				});
		
		//Refrigerator
		connections.put(
				new EventSource(RefrigeratorCoupledModel.URI,
						CloseRefrigeratorDoor.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								CloseRefrigeratorDoor.class)
				});
		connections.put(
				new EventSource(RefrigeratorCoupledModel.URI,
						OpenRefrigeratorDoor.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								OpenRefrigeratorDoor.class)
				});
		connections.put(
				new EventSource(RefrigeratorCoupledModel.URI,
								Freezing.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								Freezing.class)
				});
		connections.put(
				new EventSource(RefrigeratorCoupledModel.URI,
								Resting.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								Resting.class)
				});
		connections.put(
				new EventSource(RefrigeratorCoupledModel.URI,
								OffRefrigerator.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								OffRefrigerator.class)
				});
		connections.put(
				new EventSource(RefrigeratorCoupledModel.URI,
						OnRefrigerator.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								OnRefrigerator.class)
				});

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();
		coupledModelDescriptors.put(
				HEM_CoupledModel.URI,
				RTComponentCoupledModelDescriptor.create(
						HEM_CoupledModel.class,
						HEM_CoupledModel.URI,
						submodels,
						null,
						null,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_RT_ENGINE,
						null,
						SIL_Coordinator.REFLECTION_INBOUND_PORT_URI,
						RTCoordinatorPlugin.class,
						null,
						CVM_SIL.ACC_FACTOR));

		// SIL simulations must be executed under the real time modality to
		// be able to intertwine simulation events processing and code
		// execution under the same time line.
		RTComponentModelArchitecture arch =			// RT = RealTime
				new RTComponentModelArchitecture(
						SIM_ARCHITECTURE_URI,
						HEM_CoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS);

		return arch;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle.
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		this.traceMessage("Supervisor starts.\n");
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		this.sp.createSimulator();
		this.sp.setSimulationRunParameters(new HashMap<String, Object>());
		long realTimeOfStart =
				System.currentTimeMillis() + CVM_SIL.DELAY_TO_START_SIMULATION;
		this.sp.startRTSimulation(realTimeOfStart, 0.0,
								  CVM_SIL.SIMULATION_DURATION);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		this.traceMessage("Supervisor stops.\n");

		super.shutdown();
	}	
}
// -----------------------------------------------------------------------------
