package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;


public class CVMUnitTest extends AbstractCVM {

	public				CVMUnitTest() throws Exception
	{

	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Battery.class.getCanonicalName(),
				// the first actual parameter tells the component to
				// execute as a SIL simulation and the second that is must
				// be executed as a unit test.
				new Object[]{BatteryRTAtomicSimulatorPlugin.
											UNIT_TEST_SIM_ARCHITECTURE_URI,
							 true});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(15000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


