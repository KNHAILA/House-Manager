package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class FanCVM extends AbstractCVM {

	public				FanCVM() throws Exception
	{

	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				// the first actual parameter tells the component to create
				// a SIL simulation for unit test and the second to execute
				// as a unit test.
				new Object[]{FanRTAtomicSimulatorPlugin.
											UNIT_TEST_SIM_ARCHITECTURE_URI,
							 true});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			FanCVM cvm = new FanCVM();
			cvm.startStandardLifeCycle(15000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
