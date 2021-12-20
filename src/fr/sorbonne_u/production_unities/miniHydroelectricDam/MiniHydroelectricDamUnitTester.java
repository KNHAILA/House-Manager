package fr.sorbonne_u.production_unities.miniHydroelectricDam;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredInterfaces(required = { MiniHydroelectricDamCI.class })
public class MiniHydroelectricDamUnitTester extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected String miniHydroelectricDamInboundPortURI;
	protected MiniHydroelectricDamOutboundPort wtop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected	MiniHydroelectricDamUnitTester() throws Exception
		{
			this(MiniHydroelectricDam.Mini_Hydroelectric_Dam_INBOUND_PORT_URI);
		}

	protected	MiniHydroelectricDamUnitTester(
			String miniHydroelectricDamInboundPortURI
			) throws Exception
		{
			super(1, 0);
			this.initialise(miniHydroelectricDamInboundPortURI);
		}

	protected			MiniHydroelectricDamUnitTester(
			String reflectionInboundPortURI,
			String miniHydroelectricDamInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);
			this.initialise(miniHydroelectricDamInboundPortURI);
		}

	protected void initialise(String miniHydroelectricDamInboundPortURI) throws Exception {
		this.miniHydroelectricDamInboundPortURI = miniHydroelectricDamInboundPortURI;
		this.wtop = new MiniHydroelectricDamOutboundPort(this);
		this.wtop.publishPort();

		this.tracer.get().setTitle("MiniHydroelectricDam tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void testIsRunning() {
		this.traceMessage("testIsRunning()...\n");
		try {
			assertEquals(false, this.wtop.isRunning());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void testStartStopMiniHydroelectricDam() {
		this.traceMessage("testStartStopMiniHydroelectricDam()...\n");
		try {
			assertEquals(false, this.wtop.isRunning());
			this.wtop.startMiniHydroelectricDam();
			assertEquals(true, this.wtop.isRunning());
			this.wtop.stopMiniHydroelectricDam();
			assertEquals(false, this.wtop.isRunning());
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void runnAllTests() {
		this.testIsRunning();
		this.testStartStopMiniHydroelectricDam();
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
			this.doPortConnection(this.wtop.getPortURI(), this.miniHydroelectricDamInboundPortURI,
					MiniHydroelectricDamConnector.class.getCanonicalName());
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
		this.doPortDisconnection(this.wtop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.wtop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
