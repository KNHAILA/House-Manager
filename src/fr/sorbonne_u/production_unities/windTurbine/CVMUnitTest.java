package fr.sorbonne_u.production_unities.windTurbine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMUnitTest extends AbstractCVM {

	public CVMUnitTest() throws Exception {
	}
	
	@Override
	public void deploy() throws Exception {
		AbstractComponent.createComponent(
				WindTurbine.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				WindTurbineUnitTester.class.getCanonicalName(),
				new Object[]{});

		super.deploy();
	}
	
	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(10000000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}