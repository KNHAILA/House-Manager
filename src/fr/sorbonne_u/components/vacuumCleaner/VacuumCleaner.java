package fr.sorbonne_u.components.vacuumCleaner;
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

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleanerImplementation;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleanerCI;
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleanerInboundPort;
import fr.sorbonne_u.components.vacuumCleaner.mil.VacuumCleanerCoupledModel;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.SwitchOnVacuumCleaner;
import fr.sorbonne_u.CVM_SIL;
import fr.sorbonne_u.components.vacuumCleaner.sil.VacuumCleanerStateModel;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.exceptions.PreconditionException;
import java.util.HashMap;

// -----------------------------------------------------------------------------
/**
 * The class <code>fan</code> implements the hair dryer component
 * including a SIL simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The hair dryer is an uncontrollable appliance, hence it does not connect
 * with the household energy manager. This version of the component is made to
 * act as a cyber-physical component able to use SIL simulations to test the
 * code against models of the physical world.
 * </p>
 * <p>
 * When SIL simulated, some actions made by the code must be reflected in the
 * simulation models to keep the coherence in synchronisation between the
 * code and the simulation. The first way to do so is to make the code of
 * the component emit external events towards simulation models. The method
 * {@code triggerExternalEvent} of simulator plug-ins does so to send an
 * event to a model to be executed immediately (in the simulation time).
 * It takes two parameters: the URI of the target model and a lambda
 * expression taking the current simulated time in parameter to create
 * an event instance occurring at that time. See the methods in the component
 * for example of its use.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 *  @authors	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 *              <a href="maedeh.daemi@etu.sorbonne-universite.fr">DAEMI Maedeh</a>
 */
@OfferedInterfaces(offered={VacuumCleanerCI.class})
public class			VacuumCleaner
extends		AbstractCyPhyComponent
implements	VacuumCleanerImplementation
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the hair dryer reflection inbound port used.					*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
												"VACUUM-CLEANER-rip";
	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI =
												"VACUUM-CLEANER-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean			VERBOSE = true;
	public static final VacuumCleanerState	INITIAL_STATE = VacuumCleanerState.OFF;
	public static final VacuumCleanerMode	INITIAL_MODE = VacuumCleanerMode.LOW;

	/** current state (on, off) of the hair dryer.							*/
	protected VacuumCleanerState		currentState;
	/** current mode of operation (low, high) of the hair dryer.			*/
	protected VacuumCleanerMode			currentMode;

	/** inbound port offering the <code>fanCI</code> interface.		*/
	protected VacuumCleanerInboundPort	hdip;

	// SIL simulation

	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected String				simArchitectureURI;
	/** URI of the executor service used to execute the real time
	 *  simulation.															*/
	protected static final String	SCHEDULED_EXECUTOR_SERVICE_URI = "ses";
	/** simulator plug-in that holds the SIL simulator for this component.	*/
	protected VacuumCleanerRTAtomicSimulatorPlugin	simulatorPlugin;
	/** true if the component executes as a SIL simulation, false otherwise.*/
	protected boolean				isSILsimulated;
	/** true if the component executes as a unit test, false otherwise.		*/
	protected boolean				executesAsUnitTest;
	/** acceleration factor used when executing as a unit test.				*/
	protected static final double	ACC_FACTOR = 1.0;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer component including a SIL simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	{@code getState() == fanState.OFF}
	 * post	{@code getMode() == fanMode.LOW}
	 * </pre>
	 * 
	 * @param simArchitectureURI	URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest	true if the component executes as a unit test, false otherwise.
	 * @throws Exception			<i>to do</i>.
	 */
	protected			VacuumCleaner(
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI, 1, 0);
		this.initialise(INBOUND_PORT_URI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create a hair dryer component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	{@code getState() == fanState.OFF}
	 * post	{@code getMode() == fanMode.LOW}
	 * </pre>
	 * 
	 * @param fanInboundPortURI	URI of the hair dryer inbound port.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest		true if the component executes as a unit test, false otherwise.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			VacuumCleaner(
		String fanInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		)
	throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI, 1, 0);
		this.initialise(fanInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create a hair dryer component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	{@code getState() == fanState.OFF}
	 * post	{@code getMode() == fanMode.LOW}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @param fanInboundPortURI	URI of the hair dryer inbound port.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest		true if the component executes as a unit test, false otherwise.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			VacuumCleaner(
		String reflectionInboundPortURI,
		String fanInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(fanInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * initialise the hair dryer component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	{@code getState() == fanState.OFF}
	 * post	{@code getMode() == fanMode.LOW}
	 * </pre>
	 * 
	 * @param fanInboundPortURI	URI of the hair dryer inbound port.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest		true if the component executes as a unit test, false otherwise.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(
		String fanInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		assert	fanInboundPortURI != null :
					new PreconditionException(
										"vacuumCleanerInboundPortURI != null");
		assert	!fanInboundPortURI.isEmpty() :
					new PreconditionException(
										"!vacuumCleanerrInboundPortURI.isEmpty()");
		assert	simArchitectureURI != null;
		assert	!simArchitectureURI.isEmpty() || !executesAsUnitTest;

		this.simArchitectureURI = simArchitectureURI;
		this.isSILsimulated = !simArchitectureURI.isEmpty();
		this.executesAsUnitTest = executesAsUnitTest;
		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.hdip = new VacuumCleanerInboundPort(fanInboundPortURI, this);
		this.hdip.publishPort();

		if (VacuumCleaner.VERBOSE) {
			this.tracer.get().setTitle("Vacuum Cleaner component");
			this.tracer.get().setRelativePosition(2, 2);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		this.traceMessage("Vacuum Cleaner starts.\n");

		if (this.isSILsimulated) {
			this.createNewExecutorService(
								SCHEDULED_EXECUTOR_SERVICE_URI, 1, true);
			this.simulatorPlugin = new VacuumCleanerRTAtomicSimulatorPlugin();
			this.simulatorPlugin.setPluginURI(VacuumCleanerCoupledModel.URI);
			this.simulatorPlugin.setSimulationExecutorService(
											SCHEDULED_EXECUTOR_SERVICE_URI);
			try {
				this.simulatorPlugin.initialiseSimulationArchitecture(
									this.simArchitectureURI,
									this.executesAsUnitTest ?
										ACC_FACTOR
									:	CVM_SIL.ACC_FACTOR
									);
				this.installPlugin(this.simulatorPlugin);
			} catch (Exception e) {
				throw new ComponentStartException(e) ;
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		if (this.executesAsUnitTest) {
			this.simulatorPlugin.setSimulationRunParameters(
												new HashMap<String, Object>());
			long simStart = System.currentTimeMillis() + 1000L;
			double endTime = 10.0/ACC_FACTOR;
			this.simulatorPlugin.startRTSimulation(simStart, 0.0, endTime);
			this.traceMessage("real time of start = " + simStart + "\n");
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		this.traceMessage("Vacuum Cleaner stops.\n");

		try {
			this.hdip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#getState()
	 */
	@Override
	public VacuumCleanerState	getState() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum Cleaner returns its state : " +
													this.currentState + ".\n");
		}

		return this.currentState;
	}

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#getMode()
	 */
	@Override
	public VacuumCleanerMode	getMode() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum Cleaner returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#turnOn()
	 */
	@Override
	public void			turnOn() throws Exception
	{
		assert	this.getState() == VacuumCleanerState.OFF :
					new PreconditionException(
										"getState() == VacuumCleanerrState.OFF");

		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum Cleaner is turned on.\n");
		}

		this.currentState = VacuumCleanerState.ON;
		this.currentMode = VacuumCleanerMode.LOW;

		if (this.isSILsimulated) {
			// trigger an immediate SwitchOnfan event on the
			// fanStateModel, which in turn will emit this event
			// towards the other models of the hair dryer
			// the t parameter in the lambda expression represents the current
			// simulation time to be provided by he simulator before passing
			// the event instance to the model
			this.simulatorPlugin.triggerExternalEvent(
									VacuumCleanerStateModel.URI,
									t -> new SwitchOnVacuumCleaner(t));
		}
	}

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#turnOff()
	 */
	@Override
	public void			turnOff() throws Exception
	{
		assert	this.getState() == VacuumCleanerState.ON :
					new PreconditionException(
										"getState() == VacuumCleanerState.ON");

		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum Cleaner is turned off.\n");
		}

		this.currentState = VacuumCleanerState.OFF;

		if (this.isSILsimulated) {
			// trigger an immediate SwitchOfffan event on the
			// fanStateModel, which in turn will emit this event
			// towards the other models of the hair dryer
			// the t parameter in the lambda expression represents the current
			// simulation time to be provided by he simulator before passing
			// the event instance to the model
			this.simulatorPlugin.triggerExternalEvent(
					VacuumCleanerStateModel.URI,
									t -> new SwitchOffVacuumCleaner(t));
		}
	}

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#setHigh()
	 */
	@Override
	public void			setHigh() throws Exception
	{
		assert	this.getState() == VacuumCleanerState.ON :
					new PreconditionException(
										"getState() == VacuumCleanerrState.ON");
		assert	this.getMode() == VacuumCleanerMode.LOW :
					new PreconditionException("getMode() == VacuumCleanerMode.LOW");

		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum Cleaner is set high.\n");
		}

		this.currentMode = VacuumCleanerMode.HIGH;

		if (this.isSILsimulated) {
			// trigger an immediate SetHighfan event on the
			// fanStateModel, which in turn will emit this event
			// towards the other models of the hair dryer
			// the t parameter in the lambda expression represents the current
			// simulation time to be provided by he simulator before passing
			// the event instance to the model
			this.simulatorPlugin.triggerExternalEvent(
					VacuumCleanerStateModel.URI,
									t -> new SetHighVacuumCleaner(t));
		}
	}

	/**
	 * @see fr.sorbonne_u.components.fan.fanImplementationI#setLow()
	 */
	@Override
	public void			setLow() throws Exception
	{
		assert	this.getState() == VacuumCleanerState.ON :
					new PreconditionException(
										"getState() == VacuumCleanerState.ON");
		assert	this.getMode() == VacuumCleanerMode.HIGH :
					new PreconditionException(
										"getMode() == VacuumCleanerMode.HIGH");

		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum Cleaner is set low.\n");
		}

		this.currentMode = VacuumCleanerMode.LOW;

		if (this.isSILsimulated) {
			// trigger an immediate SetLowfan event on the
			// fanStateModel, which in turn will emit this event
			// towards the other models of the hair dryer
			// the t parameter in the lambda expression represents the current
			// simulation time to be provided by the simulator before passing
			// the event instance to the model
			this.simulatorPlugin.triggerExternalEvent(
					VacuumCleanerStateModel.URI,
									t -> new SetLowVacuumCleaner(t));
		}
	}
}
// -----------------------------------------------------------------------------
