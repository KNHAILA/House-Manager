package fr.sorbonne_u.hem;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.fan.Fan;
import fr.sorbonne_u.components.fan.FanTester;
import fr.sorbonne_u.components.refrigerator.Refrigerator;
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleaner;
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleanerTester;
import fr.sorbonne_u.components.washingMachine.WashingMachine;
import fr.sorbonne_u.components.waterHeater.WaterHeater;
import fr.sorbonne_u.meter.ElectricMeter;
import fr.sorbonne_u.production_unities.windTurbine.WindTurbine;
import fr.sorbonne_u.storage.battery.Battery;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMIntegrationTest</code> defines the integration test
 * for the household energy management example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-09-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class CVMIntegrationTest
extends	AbstractCVM
{
	public	CVMIntegrationTest() throws Exception
	{

	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void	deploy() throws Exception
	{
		
		//Fan
		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				FanTester.class.getCanonicalName(),
				new Object[]{});
		
		//vacuum Cleaner
		AbstractComponent.createComponent(
				VacuumCleaner.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				VacuumCleanerTester.class.getCanonicalName(),
				new Object[]{});
		
		//ElectricMeter
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{});
		
		//Battery
		AbstractComponent.createComponent(
				Battery.class.getCanonicalName(),
				new Object[]{});
		
		//WindTurbine
		AbstractComponent.createComponent(
				WindTurbine.class.getCanonicalName(),
				new Object[]{});
		

		//HEM		
		AbstractComponent.createComponent(
				HEM.class.getCanonicalName(),
				new Object[]{});
		
		//WaterHeater
		AbstractComponent.createComponent(
				WaterHeater.class.getCanonicalName(),
				new Object[]{});
		
		//Refrigerator
				/*AbstractComponent.createComponent(
						Refrigerator.class.getCanonicalName(),
						new Object[]{}); */
		//WashingMachine
		AbstractComponent.createComponent(
				WashingMachine.class.getCanonicalName(),
				new Object[]{});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMIntegrationTest cvm = new CVMIntegrationTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
