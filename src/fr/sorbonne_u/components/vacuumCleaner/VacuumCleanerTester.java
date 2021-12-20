package fr.sorbonne_u.components.vacuumCleaner;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleanerImplementation.VacuumCleanerMode;
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleanerImplementation.VacuumCleanerState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.ExecutionException;

@RequiredInterfaces(required = { VacuumCleanerCI.class })
public class VacuumCleanerTester extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected VacuumCleanerOutboundPort vacuumCleanerOP;
	protected String vacuumCleanerInboundPortURI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected VacuumCleanerTester() throws Exception
		{
			this(VacuumCleaner.vacuumCleanerIP_URL);
		}

	protected VacuumCleanerTester(String vacuumCleanerInboundPortURI)
		throws Exception
		{
			super(1, 0);

			this.initialise(vacuumCleanerInboundPortURI);
		}

	protected VacuumCleanerTester(
			String FacuumCleanerInboundPortURI,
			String reflectionInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);

			this.initialise(vacuumCleanerInboundPortURI);
		}

	protected void initialise(String vacuumCleanerInboundPortURI) throws Exception {
		this.vacuumCleanerInboundPortURI = vacuumCleanerInboundPortURI;
		this.vacuumCleanerOP = new VacuumCleanerOutboundPort(this);
		this.vacuumCleanerOP.publishPort();

		this.tracer.get().setTitle("Vacuum Cleaner tester component");
		this.tracer.get().setRelativePosition(2, 0);
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	public void testGetState() {
		this.logMessage("testGetState()... ");
		try {
			assertEquals(VacuumCleanerState.OFF, this.vacuumCleanerOP.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void testGetMode() {
		this.logMessage("testGetMode()... ");
		try {
			assertEquals(VacuumCleanerMode.LOW, this.vacuumCleanerOP.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void testTurnOnOff() {
		this.logMessage("testTurnOnOff()... ");
		try {
			this.vacuumCleanerOP.turnOn();
			assertEquals(VacuumCleanerState.ON, this.vacuumCleanerOP.getState());
			assertEquals(VacuumCleanerMode.LOW, this.vacuumCleanerOP.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class, () -> this.vacuumCleanerOP.turnOn());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.vacuumCleanerOP.turnOff();
			assertEquals(VacuumCleanerState.OFF, this.vacuumCleanerOP.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class, () -> this.vacuumCleanerOP.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void testSetLowHigh() {
		this.logMessage("testSetLowHigh()... ");
		try {
			this.vacuumCleanerOP.turnOn();
			this.vacuumCleanerOP.setHigh();
			assertEquals(VacuumCleanerState.ON, this.vacuumCleanerOP.getState());
			assertEquals(VacuumCleanerMode.HIGH, this.vacuumCleanerOP.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class, () -> this.vacuumCleanerOP.setHigh());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.vacuumCleanerOP.setLow();
			assertEquals(VacuumCleanerState.ON, this.vacuumCleanerOP.getState());
			assertEquals(VacuumCleanerMode.LOW, this.vacuumCleanerOP.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class, () -> this.vacuumCleanerOP.setLow());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.vacuumCleanerOP.turnOff();
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
			this.doPortConnection(this.vacuumCleanerOP.getPortURI(), vacuumCleanerInboundPortURI,
					VacuumCleanerConnector.class.getCanonicalName());
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
		this.doPortDisconnection(this.vacuumCleanerOP.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.vacuumCleanerOP.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
