package fr.sorbonne_u.hem;


import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.refrigerator.Refrigerator;
import fr.sorbonne_u.components.refrigerator.RefrigeratorCI;
import fr.sorbonne_u.components.refrigerator.RefrigeratorConnector;
import fr.sorbonne_u.components.refrigerator.RefrigeratorOutboundPort;
import fr.sorbonne_u.components.waterHeater.ThermostatedWaterHeater;
import fr.sorbonne_u.components.waterHeater.WaterHeaterCI;
import fr.sorbonne_u.components.waterHeater.WaterHeaterConnector;
import fr.sorbonne_u.components.waterHeater.WaterHeaterOutboundPort;
import fr.sorbonne_u.interfaces.StandardEquipmentControlCI;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;
import fr.sorbonne_u.meter.ElectricMeter;
import fr.sorbonne_u.meter.ElectricMeterCI;
import fr.sorbonne_u.meter.ElectricMeterConnector;
import fr.sorbonne_u.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.production_unities.windTurbine.WindTurbineOutboundPort;
import fr.sorbonne_u.storage.battery.BatteryOutboundPort;


// -----------------------------------------------------------------------------
/**
 * The class <code>HEM</code> implements the basis for a household energy
 * management component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * As is, this component is only a very limited starting point for the actual
 * component. The given code is there only to ease the understanding of the
 * objectives, but most of it must be replaced to get the correct code.
 * Especially, no registration of the components representing the appliances
 * is given.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {StandardEquipmentControlCI.class,
								SuspensionEquipmentControlCI.class,
								ElectricMeterCI.class,
								RefrigeratorCI.class,
								WaterHeaterCI.class})
public class			HEM
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** period at which the HEM looks at the current consumption and makes
	 *  energy management decisions.									 	*/
	protected static final long		MANAGEMENT_PERIOD = 1;
	/** time unit to interpret {@code MANAGEMENT_PERIOD}.					*/
	protected static final TimeUnit	MANAGEMENT_PERIOD_TIME_UNIT =
															TimeUnit.SECONDS;

	/** true if the component executes in a unit test mode, false
	 *  otherwise.															*/
	protected boolean		executesAsUnitTest;
	/** future allowing to act upon the management task.					*/
	protected Future<?>		managementTaskFuture;

	//outbound port
	protected RefrigeratorOutboundPort	refrigeratorop;
	protected WaterHeaterOutboundPort	waterHeaterop;
	
	protected PlanningEquipmentControlOutboundPort washingMachineop;
	protected ElectricMeterOutboundPort					meterop;
	
	protected BatteryOutboundPort batteryop;
	protected WindTurbineOutboundPort windTurbineop;
	
	
	
	public static final String		INBOUND_PORT_URI = "HEM-INBOUND-PORT-URI";
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a HEM instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param executesAsUnitTest	true if the component executes in a unit test mode, false otherwise.
	 */
	protected 			HEM(
		boolean executesAsUnitTest
		)
	{
		super(1, 1);

		this.executesAsUnitTest = executesAsUnitTest;

		this.tracer.get().setTitle("Home Energy Manager component");
		this.tracer.get().setRelativePosition(1, 0);
		this.toggleTracing();
		
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * first draft of the management task for the HEM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		manage() throws Exception
	{
		this.traceMessage("Electric meter current consumption? " +
						  this.meterop.getCurrentConsumption() + "\n");
		this.traceMessage("Electric meter current production? " +
						  this.meterop.getCurrentProduction() + "\n");
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

		this.traceMessage("Home Energy Manager starts.\n");

		try {
			this.meterop = new ElectricMeterOutboundPort(this);
			this.meterop.publishPort();
			this.doPortConnection(
					this.meterop.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());

			this.waterHeaterop = new WaterHeaterOutboundPort(this);
			this.waterHeaterop.publishPort();
			this.doPortConnection(
					this.waterHeaterop.getPortURI(),
					ThermostatedWaterHeater.INBOUND_PORT_URI,
					WaterHeaterConnector.class.getCanonicalName());
			
			this.refrigeratorop = new RefrigeratorOutboundPort(this);
			this.refrigeratorop.publishPort();
			this.doPortConnection(
					this.refrigeratorop.getPortURI(),
					Refrigerator.INBOUND_PORT_URI,
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
		if (this.executesAsUnitTest) {
			// simplified integration testing.
			this.traceMessage("Electric meter current consumption? " +
				this.meterop.getCurrentConsumption() + "\n");
			this.traceMessage("Electric meter current production? " +
				this.meterop.getCurrentProduction() + "\n");

			this.waterHeaterop.startWaterHeater();
			this.traceMessage("Water Heater is on \n");
			
			this.refrigeratorop.startRefrigerator();
			this.traceMessage("Refrigerator is on \n");
			
		} else {
			final HEM h = this;
			this.managementTaskFuture =
				this.scheduleTaskAtFixedRateOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								h.manage();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					MANAGEMENT_PERIOD,
					MANAGEMENT_PERIOD,
					MANAGEMENT_PERIOD_TIME_UNIT);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		if (this.managementTaskFuture != null &&
								!this.managementTaskFuture.isCancelled()) {
			this.managementTaskFuture.cancel(true);
		}
		this.doPortDisconnection(this.meterop.getPortURI());
		this.doPortDisconnection(this.waterHeaterop.getPortURI());
		this.doPortDisconnection(this.refrigeratorop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		this.traceMessage("Home Energy Manager stops.\n");

		try {
			this.meterop.unpublishPort();
			this.waterHeaterop.unpublishPort();
			this.refrigeratorop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
