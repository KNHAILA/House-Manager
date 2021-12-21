package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.fan.FanImplementation.Mode;
import fr.sorbonne_u.components.fan.FanImplementation.State;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.ExecutionException;

@RequiredInterfaces(required = { FanCI.class })
public class FanTester extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected FanOutboundPort fanOP;
	protected String fanInboundPortURI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected FanTester() throws Exception
		{
			this(Fan.INBOUND_PORT_URI);
		}

	protected FanTester(String fanInboundPortURI)
		throws Exception
		{
			super(1, 0);

			this.initialise(fanInboundPortURI);
		}

	protected FanTester(
			String fanInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

			this.initialise(fanInboundPortURI);
		}

	protected void initialise(String fanInboundPortURI) throws Exception {
		this.fanInboundPortURI = fanInboundPortURI;
		this.fanOP = new FanOutboundPort(this);
		this.fanOP.publishPort();

		this.tracer.get().setTitle("Fan tester component");
		this.tracer.get().setRelativePosition(3, 0);
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	public void testGetState() {
		this.logMessage("testGetState()... ");
		try {
			assertEquals(State.OFF, this.fanOP.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void testGetMode() {
		this.logMessage("testGetMode()... ");
		try {
			assertEquals(Mode.LOW, this.fanOP.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void testTurnOnOff() {
		this.logMessage("testTurnOnOff()... ");
		try {
			this.fanOP.turnOn();
			assertEquals(State.ON, this.fanOP.getState());
			assertEquals(Mode.LOW, this.fanOP.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class, () -> this.fanOP.turnOn());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fanOP.turnOff();
			assertEquals(State.OFF, this.fanOP.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class, () -> this.fanOP.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void testSetLowHigh() {
		this.logMessage("testSetLowHigh()... ");
		try {
			this.fanOP.turnOn();
			this.fanOP.setHigh();
			assertEquals(State.ON, this.fanOP.getState());
			assertEquals(Mode.HIGH, this.fanOP.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class, () -> this.fanOP.setHigh());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fanOP.setLow();
			assertEquals(State.ON, this.fanOP.getState());
			assertEquals(Mode.LOW, this.fanOP.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class, () -> this.fanOP.setLow());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.fanOP.turnOff();
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	protected void runAllTests() {
		this.testGetState();
		this.testGetMode();
		this.testTurnOnOff();
		this.testSetLowHigh();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();

		try {
			this.doPortConnection(this.fanOP.getPortURI(), fanInboundPortURI,
					FanConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception {
		this.runAllTests();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.fanOP.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.fanOP.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
