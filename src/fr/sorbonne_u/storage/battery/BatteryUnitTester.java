package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredInterfaces(required = { BatteryCI.class })
public class BatteryUnitTester extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected String batteryInboundPortURI;
	protected BatteryOutboundPort bop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected BatteryUnitTester() throws Exception {
		this(Battery.INBOUND_PORT_URI);
	}

	protected BatteryUnitTester(String batteryInboundPortURI) throws Exception {
		super(1, 0);
		this.initialise(batteryInboundPortURI);
	}

	protected BatteryUnitTester(String reflectionInboundPortURI, String batteryInboundPortURI) throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(batteryInboundPortURI);
	}

	protected void initialise(String batteryInboundPortURI) throws Exception {
		this.batteryInboundPortURI = batteryInboundPortURI;
		this.bop = new BatteryOutboundPort(this);
		this.bop.publishPort();

		this.tracer.get().setTitle("Battery tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void testRemainingChargePercentage() {
		this.traceMessage("testRemainingChargePercentage()...\n");
		try {
			this.traceMessage("current PERCENTAGE = " + this.bop.getCurrentPercentage() + "\n");
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void runnAllTests() {
		this.testRemainingChargePercentage();
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
			this.doPortConnection(this.bop.getPortURI(), this.batteryInboundPortURI,
					BatteryConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception {
		this.runnAllTests();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.bop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.bop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}