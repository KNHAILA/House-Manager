package fr.sorbonne_u.hem;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.hem.registration.RegistrationCI;
import fr.sorbonne_u.hem.registration.RegistrationImplementation;
import fr.sorbonne_u.hem.registration.RegistrationInboundPort;
import fr.sorbonne_u.interfaces.PlanningEquipmentControlCI;
import fr.sorbonne_u.interfaces.StandardEquipmentControlCI;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;
import fr.sorbonne_u.meter.ElectricMeter;
import fr.sorbonne_u.meter.ElectricMeterCI;
import fr.sorbonne_u.meter.ElectricMeterConnector;
import fr.sorbonne_u.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.production_unities.windTurbine.WindTurbine;
import fr.sorbonne_u.production_unities.windTurbine.WindTurbineConnector;
import fr.sorbonne_u.production_unities.windTurbine.WindTurbineOutboundPort;
import fr.sorbonne_u.storage.battery.Battery;
import fr.sorbonne_u.storage.battery.BatteryCI;
import fr.sorbonne_u.storage.battery.BatteryConnector;
import fr.sorbonne_u.storage.battery.BatteryOutboundPort;
import fr.sorbonne_u.treatements.ConnectorGenerator;
import fr.sorbonne_u.treatements.ParseXML;
import fr.sorbonne_u.treatements.XML;


@RequiredInterfaces(required =	{StandardEquipmentControlCI.class,
		 SuspensionEquipmentControlCI.class,
		 ElectricMeterCI.class,
		 BatteryCI.class})

@OfferedInterfaces(offered={RegistrationCI.class})

public class HEM
extends	AbstractComponent implements RegistrationImplementation
{

	//Componants
	protected RegistrationInboundPort rip;
    protected SuspensionEquipmentControlOutboundPort	waterHeaterop;
	//protected SuspensionEquipmentControlOutboundPort	refrigeratorop;
    protected PlanningEquipmentControlOutboundPort washingMachineop;
    
    //Electric Meter 
	protected ElectricMeterOutboundPort	meterop;
	
	//Storage
	protected BatteryOutboundPort batteryop;
	
	//Production Unit
	protected WindTurbineOutboundPort windTurbineop;
	
	
	public static final String		INBOUND_PORT_URI =
			"HEM-INBOUND-PORT-URI";
	
	
	protected HEM() throws Exception{
		super(1, 0);
		this.rip = new RegistrationInboundPort(INBOUND_PORT_URI, this);
		this.rip.publishPort();
		
		this.meterop = new ElectricMeterOutboundPort(this);
		this.meterop.publishPort();
		
		this.batteryop = new BatteryOutboundPort(this);
		this.batteryop.publishPort();
		
		this.windTurbineop = new WindTurbineOutboundPort(this);
		this.windTurbineop.publishPort();
		
	}
	protected HEM(
			String registrationInboundPortURI
			) throws Exception
		{
			super(1, 0);
			this.initialise(registrationInboundPortURI);
		}
	
	protected	HEM(
			String reflectionInboundPortURI,
			String registrationInboundPortURI
			) throws Exception
		{
			super(reflectionInboundPortURI, 1, 0);
			this.initialise(registrationInboundPortURI);
		}
	
	
	protected void	initialise(String registrationInboundPortURI)
	throws Exception
	{
		assert registrationInboundPortURI != null;
		assert !registrationInboundPortURI.isEmpty();

		this.tracer.get().setTitle("Refrigerator component");
		this.tracer.get().setRelativePosition(1, 1);
		this.toggleTracing();		
	}
	
	@Override
	public synchronized void start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.meterop.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());
			
			this.doPortConnection(
					this.batteryop.getPortURI(),
					Battery.Battery_INBOUND_PORT_URI,
					BatteryConnector.class.getCanonicalName());
			
			this.doPortConnection(
					this.windTurbineop.getPortURI(),
					WindTurbine.Wind_Turbine_INBOUND_PORT_URI,
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
		// simplified integration testing.
		
		//Electric meter
		this.traceMessage("Electric meter current consumption? " +
				this.meterop.getCurrentConsumption() + "\n");
		this.traceMessage("Electric meter current production? " +
				this.meterop.getCurrentProduction() + "\n");
		
		
		//Battery
		this.traceMessage("Battery is unisg? " +
				this.batteryop.isUsing() + "\n");
		this.traceMessage("Active Battery");
		this.batteryop.activeBattery();
		this.traceMessage("Battery is unisg? " +
				this.batteryop.isUsing() + "\n");
		this.traceMessage("Battry remain charge percentage " +
				this.batteryop.remainingChargePercentage() + "\n");
		this.traceMessage("Charge Battery");
		this.batteryop.chargeBattery();
		this.traceMessage("Battry remain charge percentage " +
				this.batteryop.remainingChargePercentage() + "\n");
		this.traceMessage("Decharge Battery");
		this.batteryop.dechargeBattery();
		this.traceMessage("Battry remain charge percentage " +
				this.batteryop.remainingChargePercentage() + "\n");
		
		//WindTurbine
		this.traceMessage("Start Wind Turbine");
		this.windTurbineop.startWindTurbine();
		this.traceMessage("Wind Turbine is running? " +
				this.windTurbineop.isRunning() + "\n");
		this.traceMessage("Stop Wind Turbine");
		this.windTurbineop.stopWindTurbine();
		
		
		//Washing Machine
		this.traceMessage("Water heater max mode index is? " +
				this.washingMachineop.maxMode() + "\n");
		this.traceMessage("Water heater is switched on? " +
				this.washingMachineop.switchOn() + "\n");
		this.traceMessage("Washing Machine has plan? " +
				this.washingMachineop.hasPlan() + "\n");
		this.traceMessage("Washing Machine startTime is"+
				this.washingMachineop.startTime());
		this.traceMessage("Water heater is switched off? " +
				this.washingMachineop.switchOff() + "\n");
		
	
		
		//Water Heater
		this.traceMessage("Water heater is on? " + this.waterHeaterop.on() + "\n");
		this.traceMessage("Water heater max mode index is? " +
				this.waterHeaterop.maxMode() + "\n");
		this.traceMessage("Water heater is switched on? " +
				this.waterHeaterop.switchOn() + "\n");
		this.traceMessage("Water heater current mode is? " +
				this.waterHeaterop.currentMode() + "\n");
		this.traceMessage("Water heater is suspended? " +
				this.waterHeaterop.suspended() + "\n");
		this.traceMessage("Water heater suspends? " +
				this.waterHeaterop.suspend() + "\n");
		this.traceMessage("Water heater emergency? " +
				this.waterHeaterop.emergency() + "\n");
		this.traceMessage("Water heater resumes? " +
				this.waterHeaterop.resume() + "\n");
		this.traceMessage("Water heater is suspended? " +
				this.waterHeaterop.suspended() + "\n");
		this.traceMessage("Water heater is switched off? " +
				this.waterHeaterop.switchOff() + "\n");
		this.traceMessage("Water heater is on? " + this.waterHeaterop.on() + "\n");
		
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
	    this.doPortDisconnection(this.meterop.getPortURI());
	    this.doPortDisconnection(this.batteryop.getPortURI());
	    this.doPortDisconnection(this.windTurbineop.getPortURI());
		this.doPortDisconnection(this.waterHeaterop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
            this.meterop.unpublishPort();
            this.batteryop.unpublishPort();
            this.windTurbineop.unpublishPort();
			this.waterHeaterop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	@Override
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception {
		XML xmlElements = ParseXML.getXmlElements(path2xmlControlAdapter);
		if(xmlElements.getType().equals("suspension")) {
			Class clazz = ConnectorGenerator.makeConnectorClassJavassist("fr.sorbonne_u.components.hem" + xmlElements.getRef() + "Connector",
					AbstractConnector.class, SuspensionEquipmentControlCI.class, Class.forName(xmlElements.getOffered()), xmlElements.getMethods(), xmlElements.getParametersOfOperations(), xmlElements.getAttributes(), xmlElements.getPackages());
			this.waterHeaterop = new SuspensionEquipmentControlOutboundPort(this);
			this.waterHeaterop.publishPort();
			this.doPortConnection(
					this.waterHeaterop.getPortURI(),
					controlPortURI,
					clazz.getCanonicalName());
		}else{
		Class clazz = ConnectorGenerator.makeConnectorClassJavassist("fr.sorbonne_u.components.hem" + xmlElements.getRef() + "Connector",
					AbstractConnector.class, PlanningEquipmentControlCI.class, Class.forName(xmlElements.getOffered()), xmlElements.getMethods(), xmlElements.getParametersOfOperations(), xmlElements.getAttributes(), xmlElements.getPackages());
			this.washingMachineop = new PlanningEquipmentControlOutboundPort(this);
			this.washingMachineop.publishPort();
			this.doPortConnection(
					this.washingMachineop.getPortURI(),
					controlPortURI,
					clazz.getCanonicalName());
		} 
		return true;
	}
}

