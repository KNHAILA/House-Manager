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

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.fan.FanImplementation;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.fan.FanCI;
import fr.sorbonne_u.components.fan.FanInboundPort;
import fr.sorbonne_u.components.fan.mil.FanCoupledModel;
import fr.sorbonne_u.components.fan.mil.events.SetHighFan;
import fr.sorbonne_u.components.fan.mil.events.SetLowFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.CVM_SIL;
import fr.sorbonne_u.components.fan.sil.FanStateModel;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.exceptions.PreconditionException;
import java.util.HashMap;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryer</code> implements the fan component
 * including a SIL simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The fan is an uncontrollable appliance, hence it does not connect
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
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered={FanCI.class})
public class			Fan
extends		AbstractCyPhyComponent
implements	FanImplementation
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the fan reflection inbound port used.					*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
												"HAIR-DRYER-rip";
	/** URI of the fan inbound port used in tests.					*/
	public static final String			INBOUND_PORT_URI =
												"HAIR-DRYER-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean			VERBOSE = true;
	public static final State	INITIAL_STATE = State.OFF;
	public static final Mode	INITIAL_MODE = Mode.LOW;

	/** current state (on, off) of the fan.							*/
	protected State		currentState;
	/** current mode of operation (low, high) of the fan.			*/
	protected Mode			currentMode;

	/** inbound port offering the <code>HairDryerCI</code> interface.		*/
	protected FanInboundPort	hdip;

	// SIL simulation

	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected String				simArchitectureURI;
	/** URI of the executor service used to execute the real time
	 *  simulation.															*/
	protected static final String	SCHEDULED_EXECUTOR_SERVICE_URI = "ses";
	/** simulator plug-in that holds the SIL simulator for this component.	*/
	protected FanRTAtomicSimulatorPlugin	simulatorPlugin;
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
	 * create a fan component including a SIL simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	{@code getState() == HairDryerState.OFF}
	 * post	{@code getMode() == HairDryerMode.LOW}
	 * </pre>
	 * 
	 * @param simArchitectureURI	URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest	true if the component executes as a unit test, false otherwise.
	 * @throws Exception			<i>to do</i>.
	 */
	protected			Fan(
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI, 1, 0);
		this.initialise(INBOUND_PORT_URI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create a fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hairDryerInboundPortURI != null}
	 * pre	{@code !hairDryerInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	{@code getState() == HairDryerState.OFF}
	 * post	{@code getMode() == HairDryerMode.LOW}
	 * </pre>
	 * 
	 * @param hairDryerInboundPortURI	URI of the fan inbound port.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest		true if the component executes as a unit test, false otherwise.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			Fan(
		String hairDryerInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		)
	throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI, 1, 0);
		this.initialise(hairDryerInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * create a fan component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hairDryerInboundPortURI != null}
	 * pre	{@code !hairDryerInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	{@code getState() == HairDryerState.OFF}
	 * post	{@code getMode() == HairDryerMode.LOW}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @param hairDryerInboundPortURI	URI of the fan inbound port.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest		true if the component executes as a unit test, false otherwise.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			Fan(
		String reflectionInboundPortURI,
		String hairDryerInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(hairDryerInboundPortURI, simArchitectureURI,
						executesAsUnitTest);
	}

	/**
	 * initialise the fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hairDryerInboundPortURI != null}
	 * pre	{@code !hairDryerInboundPortURI.isEmpty()}
	 * pre	{@code simArchitectureURI != null}
	 * pre	{@code !simArchitectureURI.isEmpty() || !executesAsUnitTest}
	 * post	{@code getState() == HairDryerState.OFF}
	 * post	{@code getMode() == HairDryerMode.LOW}
	 * </pre>
	 * 
	 * @param hairDryerInboundPortURI	URI of the fan inbound port.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string  if the component does not execute as a SIL simulation.
	 * @param executesAsUnitTest		true if the component executes as a unit test, false otherwise.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(
		String hairDryerInboundPortURI,
		String simArchitectureURI,
		boolean executesAsUnitTest
		) throws Exception
	{
		assert	hairDryerInboundPortURI != null :
					new PreconditionException(
										"hairDryerInboundPortURI != null");
		assert	!hairDryerInboundPortURI.isEmpty() :
					new PreconditionException(
										"!hairDryerInboundPortURI.isEmpty()");
		assert	simArchitectureURI != null;
		assert	!simArchitectureURI.isEmpty() || !executesAsUnitTest;

		this.simArchitectureURI = simArchitectureURI;
		this.isSILsimulated = !simArchitectureURI.isEmpty();
		this.executesAsUnitTest = executesAsUnitTest;
		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.hdip = new FanInboundPort(hairDryerInboundPortURI, this);
		this.hdip.publishPort();

		if (Fan.VERBOSE) {
			this.tracer.get().setTitle("fan component");
			this.tracer.get().setRelativePosition(2, 0);
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

		this.traceMessage("fan starts.\n");

		if (this.isSILsimulated) {
			this.createNewExecutorService(
								SCHEDULED_EXECUTOR_SERVICE_URI, 1, true);
			this.simulatorPlugin = new FanRTAtomicSimulatorPlugin();
			this.simulatorPlugin.setPluginURI(FanCoupledModel.URI);
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
		this.traceMessage("fan stops.\n");

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
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#getState()
	 */
	@Override
	public State	getState() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("fan returns its state : " +
													this.currentState + ".\n");
		}

		return this.currentState;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#getMode()
	 */
	@Override
	public Mode	getMode() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("fan returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#turnOn()
	 */
	@Override
	public void			turnOn() throws Exception
	{
		assert	this.getState() == State.OFF :
					new PreconditionException(
										"getState() == HairDryerState.OFF");

		if (Fan.VERBOSE) {
			this.traceMessage("fan is turned on.\n");
		}

		this.currentState = State.ON;
		this.currentMode = Mode.LOW;

		if (this.isSILsimulated) {
			// trigger an immediate SwitchOnHairDryer event on the
			// HairDryerStateModel, which in turn will emit this event
			// towards the other models of the fan
			// the t parameter in the lambda expression represents the current
			// simulation time to be provided by he simulator before passing
			// the event instance to the model
			this.simulatorPlugin.triggerExternalEvent(
									FanStateModel.URI,
									t -> new SwitchOnFan(t));
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#turnOff()
	 */
	@Override
	public void			turnOff() throws Exception
	{
		assert	this.getState() == State.ON :
					new PreconditionException(
										"getState() == HairDryerState.ON");

		if (Fan.VERBOSE) {
			this.traceMessage("fan is turned off.\n");
		}

		this.currentState = State.OFF;

		if (this.isSILsimulated) {
			// trigger an immediate SwitchOffHairDryer event on the
			// HairDryerStateModel, which in turn will emit this event
			// towards the other models of the fan
			// the t parameter in the lambda expression represents the current
			// simulation time to be provided by he simulator before passing
			// the event instance to the model
			this.simulatorPlugin.triggerExternalEvent(
									FanStateModel.URI,
									t -> new SwitchOffFan(t));
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#setHigh()
	 */
	@Override
	public void			setHigh() throws Exception
	{
		assert	this.getState() == State.ON :
					new PreconditionException(
										"getState() == HairDryerState.ON");
		assert	this.getMode() == Mode.LOW :
					new PreconditionException("getMode() == HairDryerMode.LOW");

		if (Fan.VERBOSE) {
			this.traceMessage("fan is set high.\n");
		}

		this.currentMode = Mode.HIGH;

		if (this.isSILsimulated) {
			// trigger an immediate SetHighHairDryer event on the
			// HairDryerStateModel, which in turn will emit this event
			// towards the other models of the fan
			// the t parameter in the lambda expression represents the current
			// simulation time to be provided by he simulator before passing
			// the event instance to the model
			this.simulatorPlugin.triggerExternalEvent(
									FanStateModel.URI,
									t -> new SetHighFan(t));
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.hairdryer.HairDryerImplementationI#setLow()
	 */
	@Override
	public void			setLow() throws Exception
	{
		assert	this.getState() == State.ON :
					new PreconditionException(
										"getState() == HairDryerState.ON");
		assert	this.getMode() == Mode.HIGH :
					new PreconditionException(
										"getMode() == HairDryerMode.HIGH");

		if (Fan.VERBOSE) {
			this.traceMessage("fan is set low.\n");
		}

		this.currentMode = Mode.LOW;

		if (this.isSILsimulated) {
			// trigger an immediate SetLowHairDryer event on the
			// HairDryerStateModel, which in turn will emit this event
			// towards the other models of the fan
			// the t parameter in the lambda expression represents the current
			// simulation time to be provided by the simulator before passing
			// the event instance to the model
			this.simulatorPlugin.triggerExternalEvent(
									FanStateModel.URI,
									t -> new SetLowFan(t));
		}
	}
}
// -----------------------------------------------------------------------------
