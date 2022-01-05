package fr.sorbonne_u.hem;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.fan.Fan;
import fr.sorbonne_u.components.fan.FanTester;
import fr.sorbonne_u.components.refrigerator.Refrigerator;
import fr.sorbonne_u.components.waterHeater.ThermostatedWaterHeater;
import fr.sorbonne_u.meter.ElectricMeter;
import fr.sorbonne_u.components.AbstractComponent;


//-----------------------------------------------------------------------------
/**
* The class <code>CVMIntegrationTest</code> defines the integration test
* for the household energy management example without SIL simulation.
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
public class			CVMIntegrationTest
extends		AbstractCVM
{
	public				CVMIntegrationTest() throws Exception
	{

	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		//Meter
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{"",false});

		
		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				new Object[]{"",false});

		AbstractComponent.createComponent(
				FanTester.class.getCanonicalName(),
				new Object[]{});

		//ThermostatedHeater
		AbstractComponent.createComponent(
				ThermostatedWaterHeater.class.getCanonicalName(),
				new Object[]{"","",false});

	// Refrigerator
	/*	AbstractComponent.createComponent(
				Refrigerator.class.getCanonicalName(),
				new Object[] { "", "", "" });

		// HEM
		AbstractComponent.createComponent(
				HEM.class.getCanonicalName(),
				new Object[]{true});
*/
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
//-----------------------------------------------------------------------------
