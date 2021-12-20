package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;


@OfferedInterfaces(offered={FanCI.class})
public class Fan extends AbstractComponent implements FanImplementation {
	

	/** URI of the  fan in bound port used in tests.	**/				
	public static final String fanIP_URL = "fanIP_url";
	
	/** when true, methods trace their actions.								*/
	public static final boolean	VERBOSE = true;
	public static final State	INITIAL_STATE = State.OFF;
	public static final Mode	INITIAL_MODE = Mode.LOW;
	
	/** current state (on, off) of the  fan.							*/
	protected State	currentState;
	
	/** current mode of operation (low, high) of the  fan.			*/
	protected Mode currentMode;
	
	/** in bound port offering the <code>FanCI</code> interface.		*/
	protected FanInboundPort fanIP;
	

	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	{@code getState() == State.OFF}
	 * post	{@code getMode() == Mode.LOW}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected Fan()
	throws Exception
	{
		super(1, 0);
		this.initialise(fanIP_URL);
	}

	/**
	 * create a fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * post	{@code getState() == State.OFF}
	 * post	{@code getMode() == Mode.LOW}
	 * </pre>
	 * 
	 * @param fanInboundPortURI	URI of the  fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected Fan(String fanInboundPortURI)
	throws Exception
	{
		super(1, 0);
		this.initialise(fanInboundPortURI);
	}

	/**
	 * create a  fan component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * post	{@code getState() == State.OFF}
	 * post	{@code getMode() == Mode.LOW}
	 * </pre>
	 *
	 * @param fanInboundPortURI	URI of the  fan inbound port.
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected Fan(
		String fanInboundPortURI,
		String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(fanInboundPortURI);
	}

	/**
	 * initialise the  fan component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanInboundPortURI != null}
	 * pre	{@code !fanInboundPortURI.isEmpty()}
	 * post	{@code getState() == State.OFF}
	 * post	{@code getMode() == Mode.LOW}
	 * </pre>
	 * 
	 * @param fanInboundPortURI	URI of the  fan inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void	initialise(String fanInboundPortURI)
	throws Exception
	{
		assert	fanInboundPortURI != null :
					new PreconditionException(
										"fanInboundPortURI != null");
		assert	!fanInboundPortURI.isEmpty() :
					new PreconditionException(
										"!fanInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.fanIP = new FanInboundPort(fanInboundPortURI, this);
		this.fanIP.publishPort();

		if (Fan.VERBOSE) {
			this.tracer.get().setTitle("fan component");
			this.tracer.get().setRelativePosition(3, 1);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			this.fanIP.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanImplementationI#getState()
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
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanImplementationI#getMode()
	 */
	@Override
	public Mode getMode() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("fan returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanImplementationI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("fan is turned on.\n");
		}

		assert	this.getState() == State.OFF :
					new PreconditionException(
										"getState() == State.OFF");

		this.currentState = State.ON;
		this.currentMode = Mode.LOW;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanImplementationI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is turned off.\n");
		}

		assert	this.getState() == State.ON :
					new PreconditionException(
										"getState() == State.ON");

		this.currentState = State.OFF;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanImplementationI#setHigh()
	 */
	@Override
	public void	setHigh() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is set high.\n");
		}

		assert	this.getState() == State.ON :
					new PreconditionException(
										"getState() == State.ON");
		assert	this.getMode() == Mode.LOW :
					new PreconditionException("getMode() == Mode.LOW");

		this.currentMode = Mode.HIGH;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanImplementationI#setLow()
	 */
	@Override
	public void	setLow() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan is set low.\n");
		}

		assert	this.getState() == State.ON :
					new PreconditionException(
										"getState() == State.ON");
		assert	this.getMode() == Mode.HIGH :
					new PreconditionException(
										"getMode() == Mode.HIGH");

		this.currentMode = Mode.LOW;
	}
}
