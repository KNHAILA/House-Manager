package fr.sorbonne_u.hem.registration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.waterHeater.WaterHeater;
import fr.sorbonne_u.components.waterHeater.WaterHeaterOutboundPort;
import fr.sorbonne_u.hem.HEM;

//-----------------------------------------------------------------------------
/**
* The class <code>RegistrationUnitTester</code> performs unbit tests for
* the electric meter component.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
* <p>Created on : 2021-09-13</p>
* 
 *  @authors	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 *              <a href="maedeh.daemi@etu.sorbonne-universite.fr">DAEMI Maedeh</a>
*/
@RequiredInterfaces(required={RegistrationCI.class})
public class			RegistrationUnitTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected String registrationInboundPortURI;
	protected RegistrationOutboundPort rop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected RegistrationUnitTester()  throws Exception
	{
		this(HEM.INBOUND_PORT_URI);
	}

	protected RegistrationUnitTester(
		String registrationInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(registrationInboundPortURI);
	}

	protected RegistrationUnitTester(
		String reflectionInboundPortURI,
		String registrationInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(registrationInboundPortURI);
	}

	protected void initialise(String waterHeaterInboundPortURI) throws Exception
	{
		this.registrationInboundPortURI = waterHeaterInboundPortURI;
		this.rop = new RegistrationOutboundPort(this);
		this.rop.publishPort();

		this.tracer.get().setTitle("Registration tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();		
	}
	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	protected void	testRegister(String uid, String controlPortURI, String path2xmlControlAdapter)
	{
		this.traceMessage("testRegister()...\n");
		try {
			this.traceMessage("Registration? " +
			this.rop.register(uid, controlPortURI, path2xmlControlAdapter) + "\n");
			
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...done.\n");
	}

	protected void			runAllTests()
	{
		this.testRegister("", "", "staff.xml");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.rop.getPortURI(),
					HEM.INBOUND_PORT_URI,
					RegistrationConnector.class.getCanonicalName());
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
		this.runAllTests();
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
