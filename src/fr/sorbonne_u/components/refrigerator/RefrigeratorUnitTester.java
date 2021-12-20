package fr.sorbonne_u.components.refrigerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//-----------------------------------------------------------------------------
/**
* The class <code>RefrigeratorUnitTester</code> implements a component
* performing unit tests for the class <code>Refrigerator</code> as a
* BCM component.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
* <p>Created on : 2021-10-12</p>
* 
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/
@RequiredInterfaces(required={RefrigeratorCI.class})
public class			RefrigeratorUnitTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected String refrigeratorInboundPortURI;
	protected RefrigeratorOutboundPort rop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected RefrigeratorUnitTester()  throws Exception
	{
		this(Refrigerator.INBOUND_PORT_URI);
	}

	protected RefrigeratorUnitTester(
		String refrigeratorInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(refrigeratorInboundPortURI);
	}

	protected RefrigeratorUnitTester(
		String reflectionInboundPortURI,
		String refrigeratorInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(refrigeratorInboundPortURI);
	}

	protected void initialise(String refrigeratorInboundPortURI) throws Exception
	{
		this.refrigeratorInboundPortURI = refrigeratorInboundPortURI;
		this.rop = new RefrigeratorOutboundPort(this);
		this.rop.publishPort();

		this.tracer.get().setTitle("Refrigerator tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void		testIsRunning()
	{
		this.traceMessage("testIsRunning()...\n");
		try {
			assertEquals(false, this.rop.isRunning());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void		testStartStopHeater()
	{
		this.traceMessage("testStartStopRefrigerator()...\n");
		try {
			assertEquals(false, this.rop.isRunning());
			this.rop.startRefrigerator();
			assertEquals(true, this.rop.isRunning());
			this.rop.stopRefrigerator();
			assertEquals(false, this.rop.isRunning());
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void		testSetGetTargetTemperature()
	{
		this.traceMessage("testSetGetTargetTemperature()...\n");
		try {
			assertEquals(false, this.rop.isRunning());
			this.rop.startRefrigerator();
			assertEquals(true, this.rop.isRunning());
			double target = 22.0;
			this.rop.setTargetTemperature(target);
			this.rop.stopRefrigerator();
			assertEquals(false, this.rop.isRunning());
			assertEquals(target, this.rop.getTargetTemperature());
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void		testGetCurrentTemperature()
	{
		this.traceMessage("testGetCurrentTemperature()...\n");
		try {
			this.traceMessage("current temperature = " +
									this.rop.getCurrentTemperature() + "\n");
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void		runnAllTests()
	{
		this.testIsRunning();
		this.testStartStopHeater();
		this.testSetGetTargetTemperature();
		this.testGetCurrentTemperature();
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
					this.rop.getPortURI(),
					this.refrigeratorInboundPortURI,
					RefrigeratorConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		this.runnAllTests();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.rop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.rop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
//-----------------------------------------------------------------------------
