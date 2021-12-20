package fr.sorbonne_u.components.vacuumCleaner;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class VacuumCleanerCVMUnitTest extends AbstractCVM {

	public VacuumCleanerCVMUnitTest() throws Exception {
		
	}

	public static void main(String[] args) {
		try {
			VacuumCleanerCVMUnitTest cvm = new VacuumCleanerCVMUnitTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deploy() throws Exception {
		AbstractComponent.createComponent(VacuumCleaner.class.getCanonicalName(), new Object[] {});
		AbstractComponent.createComponent(VacuumCleanerTester.class.getCanonicalName(), new Object[] {});
		
		super.deploy();
	}

}
