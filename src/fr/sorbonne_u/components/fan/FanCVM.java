package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class FanCVM extends AbstractCVM {

	public FanCVM() throws Exception {
	
	}

	public static void main(String[] args) {
		try {
			FanCVM cvm = new FanCVM();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deploy() throws Exception {
		AbstractComponent.createComponent(Fan.class.getCanonicalName(), new Object[] {});
		AbstractComponent.createComponent(FanTester.class.getCanonicalName(), new Object[] {});
		
		super.deploy();
	}
}
