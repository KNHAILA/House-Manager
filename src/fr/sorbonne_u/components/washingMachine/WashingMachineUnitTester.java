package fr.sorbonne_u.components.washingMachine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//-----------------------------------------------------------------------------
/**
* The class <code>WashingMachineUnitTester</code> implements a component
* performing unit tests for the class <code>WashingMachine</code> as a
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
* <p>Created on : 2021-10-17</p>
* 
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/
@RequiredInterfaces(required={WashingMachineCI.class})
public class			WashingMachineUnitTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected String washingMachineInboundPortURI;
	protected WashingMachineOutboundPort wop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected WashingMachineUnitTester()  throws Exception
	{
		this(WashingMachine.INBOUND_PORT_URI);
	}

	protected WashingMachineUnitTester(
		String washingMachineInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(washingMachineInboundPortURI);
	}

	protected WashingMachineUnitTester(
		String reflectionInboundPortURI,
		String washingMachineInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(washingMachineInboundPortURI);
	}

	protected void initialise(String washingMachineInboundPortURI) throws Exception
	{
		this.washingMachineInboundPortURI = washingMachineInboundPortURI;
		this.wop = new WashingMachineOutboundPort(this);
		this.wop.publishPort();

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
			assertEquals(false, this.wop.isRunning());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void		testStartStopHeater()
	{
		this.traceMessage("testStartStopWashingMachine()...\n");
		try {
			assertEquals(false, this.wop.isRunning());
			this.wop.startWashingMachine();
			assertEquals(true, this.wop.isRunning());
			this.wop.stopWashingMachine();
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
			this.wop.startWashingMachine();
			assertEquals(true, this.wop.isRunning());
			int target = 40;
			this.wop.setTargetTemperature(target);
			this.wop.stopWashingMachine();
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
					this.wop.getPortURI(),
					this.washingMachineInboundPortURI,
					WashingMachineConnector.class.getCanonicalName());
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
