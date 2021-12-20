package fr.sorbonne_u.components.waterHeater;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//-----------------------------------------------------------------------------
/**
* The class <code>WaterHeaterUnitTester</code> implements a component
* performing unit tests for the class <code>WaterHeater</code> as a
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
@RequiredInterfaces(required={WaterHeaterCI.class})
public class			WaterHeaterUnitTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected String waterHeaterInboundPortURI;
	protected WaterHeaterOutboundPort wop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected WaterHeaterUnitTester()  throws Exception
	{
		this(WaterHeater.INBOUND_PORT_URI);
	}

	protected WaterHeaterUnitTester(
		String waterHeaterInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(waterHeaterInboundPortURI);
	}

	protected WaterHeaterUnitTester(
		String reflectionInboundPortURI,
		String waterHeaterInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(waterHeaterInboundPortURI);
	}

	protected void initialise(String waterHeaterInboundPortURI) throws Exception
	{
		this.waterHeaterInboundPortURI = waterHeaterInboundPortURI;
		this.wop = new WaterHeaterOutboundPort(this);
		this.wop.publishPort();

		this.tracer.get().setTitle("Water Heater tester component");
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
			assertEquals(false, this.wop.isRunning());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void		testStartStopWaterHeater()
	{
		this.traceMessage("testStartStopWaterHeater()...\n");
		try {
			assertEquals(false, this.wop.isRunning());
			this.wop.startWaterHeater();
			assertEquals(true, this.wop.isRunning());
			this.wop.stopWaterHeater();
			assertEquals(false, this.wop.isRunning());
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
			assertEquals(false, this.wop.isRunning());
			this.wop.startWaterHeater();
			assertEquals(true, this.wop.isRunning());
			double target = 99.0;
			this.wop.setTargetTemperature(target);
			this.wop.stopWaterHeater();
			assertEquals(false, this.wop.isRunning());
			assertEquals(target, this.wop.getTargetTemperature());
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
									this.wop.getCurrentTemperature() + "\n");
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void		runnAllTests()
	{
		this.testIsRunning();
		this.testStartStopWaterHeater();
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
					this.wop.getPortURI(),
					this.waterHeaterInboundPortURI,
					WaterHeaterConnector.class.getCanonicalName());
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
		this.doPortDisconnection(this.wop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.wop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
//-----------------------------------------------------------------------------
