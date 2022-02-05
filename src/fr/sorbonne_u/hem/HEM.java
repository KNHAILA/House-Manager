

package fr.sorbonne_u.hem;


import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

//refrigerator
import fr.sorbonne_u.components.refrigerator.RefrigeratorConnector;
import fr.sorbonne_u.components.refrigerator.ThermostatedRefrigerator;

//water heater
import fr.sorbonne_u.components.waterHeater.ThermostatedWaterHeater;
import fr.sorbonne_u.components.waterHeater.WaterHeaterConnector;

import fr.sorbonne_u.interfaces.StandardEquipmentControlCI;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;
import fr.sorbonne_u.meter.ElectricMeter;
import fr.sorbonne_u.meter.ElectricMeterCI;
import fr.sorbonne_u.meter.ElectricMeterConnector;
import fr.sorbonne_u.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.production_unities.windTurbine.SelfControlWindTurbine;
import fr.sorbonne_u.production_unities.windTurbine.WindTurbineConnector;



@RequiredInterfaces(required = {StandardEquipmentControlCI.class,
		SuspensionEquipmentControlCI.class,
		ElectricMeterCI.class})

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

	protected PlanningEquipmentControlOutboundPort washingMachineop;
	protected ElectricMeterOutboundPort					meterop;
	protected SuspensionEquipmentControlOutboundPort	waterHeaterop;
	protected SuspensionEquipmentControlOutboundPort	windTurbineop;
	protected SuspensionEquipmentControlOutboundPort	refrigeratorop;

//	protected BatteryOutboundPort batteryop;
//	protected WindTurbineOutboundPort windTurbineop;



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


			this.waterHeaterop = new SuspensionEquipmentControlOutboundPort(this);
			this.waterHeaterop.publishPort();
			this.doPortConnection(
					this.waterHeaterop.getPortURI(),
					ThermostatedWaterHeater.INBOUND_PORT_URI,
					WaterHeaterConnector.class.getCanonicalName());
			
			System.out.println("hem ******");
			this.refrigeratorop = new SuspensionEquipmentControlOutboundPort(this);
			this.refrigeratorop.publishPort();
			
			System.out.println("hem 2******");
			this.doPortConnection(
					this.refrigeratorop.getPortURI(),
					ThermostatedRefrigerator.INBOUND_PORT_URI,
					RefrigeratorConnector.class.getCanonicalName());
					
			
			//SelfControlWindTurbine
			/*
			this.windTurbineop = new SuspensionEquipmentControlOutboundPort(this);
			this.windTurbineop.publishPort();
			this.doPortConnection(
					this.windTurbineop.getPortURI(),
					SelfControlWindTurbine.INBOUND_PORT_URI,
					WindTurbineConnector.class.getCanonicalName());
					*/

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


			//water heater
			this.traceMessage("WaterHeater is on? " + this.waterHeaterop.on() + "\n");
			this.traceMessage("WaterHeater max mode index is? " +
					this.waterHeaterop.maxMode() + "\n");
			this.traceMessage("WaterHeater is switched on? " +
					this.waterHeaterop.switchOn() + "\n");
			this.traceMessage("WaterHeater current mode is? " +
					this.waterHeaterop.currentMode() + "\n");
			this.traceMessage("WaterHeater is suspended? " +
					this.waterHeaterop.suspended() + "\n");
			this.traceMessage("WaterHeater suspends? " +
					this.waterHeaterop.suspend() + "\n");
			this.traceMessage("WaterHeater emergency? " +
					this.waterHeaterop.emergency() + "\n");
			this.traceMessage("WaterHeater resumes? " +
					this.waterHeaterop.resume() + "\n");
			this.traceMessage("WaterHeater is suspended? " +
					this.waterHeaterop.suspended() + "\n");
			this.traceMessage("WaterHeater is switched off? " +
					this.waterHeaterop.switchOff() + "\n");
			this.traceMessage("WaterHeater is on? " + this.waterHeaterop.on() + "\n");
			
			
			//refrigerator
			/*
			this.traceMessage("Refrigerator is on? " + this.refrigeratorop.on() + "\n");
			this.traceMessage("Refrigerator max mode index is? " +
					this.refrigeratorop.maxMode() + "\n");
			this.traceMessage("Refrigerator is switched on? " +
					this.refrigeratorop.switchOn() + "\n");
			this.traceMessage("Refrigerator current mode is? " +
					this.refrigeratorop.currentMode() + "\n");
			this.traceMessage("Refrigerator is suspended? " +
					this.refrigeratorop.suspended() + "\n");
			this.traceMessage("Refrigerator suspends? " +
					this.refrigeratorop.suspend() + "\n");
			this.traceMessage("Refrigerator emergency? " +
					this.refrigeratorop.emergency() + "\n");
			this.traceMessage("Refrigerator resumes? " +
					this.refrigeratorop.resume() + "\n");
			this.traceMessage("Refrigerator is suspended? " +
					this.refrigeratorop.suspended() + "\n");
			this.traceMessage("Refrigerator is switched off? " +
					this.refrigeratorop.switchOff() + "\n");
			this.traceMessage("Refrigerator is on? " + this.refrigeratorop.on() + "\n");
			*/

			//wind turbine
			/*
			this.traceMessage("WindTurbine is on? " + this.windTurbineop.on() + "\n");
			this.traceMessage("wind turbine max mode index is? " +
					this.windTurbineop.maxMode() + "\n");
			this.traceMessage("wind turbine is switched on? " +
					this.windTurbineop.switchOn() + "\n");
			this.traceMessage("Wind turbine current mode is? " +
					this.windTurbineop.currentMode() + "\n");
			this.traceMessage("Wind turbine is suspended? " +
					this.windTurbineop.suspended() + "\n");
			this.traceMessage("Wind turbine suspends? " +
					this.windTurbineop.suspend() + "\n");
			this.traceMessage("Wind turbine emergency? " +
					this.windTurbineop.emergency() + "\n");
			this.traceMessage("Wind turbine resumes? " +
					this.windTurbineop.resume() + "\n");
			this.traceMessage("Wind turbine is suspended? " +
					this.windTurbineop.suspended() + "\n");
			this.traceMessage("Wind turbine is switched off? " +
					this.windTurbineop.switchOff() + "\n");
			this.traceMessage("Wind turbine is on? " + this.windTurbineop.on() + "\n");
			*/


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
		//this.doPortDisconnection(this.windTurbineop.getPortURI());
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
			//this.windTurbineop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}