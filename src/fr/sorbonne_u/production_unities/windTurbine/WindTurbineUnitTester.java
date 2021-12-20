package fr.sorbonne_u.production_unities.windTurbine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredInterfaces(required={WindTurbineCI.class})
public class WindTurbineUnitTester 
extends	AbstractComponent
{

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected String windTurbineInboundPortURI;
	protected WindTurbineOutboundPort wtop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected	WindTurbineUnitTester() throws Exception
	{
		this(WindTurbine.Wind_Turbine_INBOUND_PORT_URI);
	}

	protected	WindTurbineUnitTester(
		String windTurbineInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(windTurbineInboundPortURI);
	}

	protected			WindTurbineUnitTester(
		String reflectionInboundPortURI,
		String windTurbineInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(windTurbineInboundPortURI);
	}

	protected void		initialise(String windTurbineInboundPortURI) throws Exception
	{
		this.windTurbineInboundPortURI = windTurbineInboundPortURI;
		this.wtop = new WindTurbineOutboundPort(this);
		this.wtop.publishPort();

		this.tracer.get().setTitle("WindTurbine tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void	testIsRunning()
	{
		this.traceMessage("testIsRunning()...\n");
		try {
			assertEquals(false, this.wtop.isRunning());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void	testStartStopWindTurbine()
	{
		this.traceMessage("testStartStopWindTurbine()...\n");
		try {
			assertEquals(false, this.wtop.isRunning());
			this.wtop.startWindTurbine();
			assertEquals(true, this.wtop.isRunning());
			this.wtop.stopWindTurbine();
			assertEquals(false, this.wtop.isRunning());
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void	runnAllTests()
	{
		this.testIsRunning();
		this.testStartStopWindTurbine();
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

		try {
			this.doPortConnection(
					this.wtop.getPortURI(),
					this.windTurbineInboundPortURI,
					WindTurbineConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception
	{
		this.runnAllTests();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.wtop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.wtop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
