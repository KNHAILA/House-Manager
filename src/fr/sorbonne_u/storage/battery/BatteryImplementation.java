package fr.sorbonne_u.storage.battery;

public interface BatteryImplementation {


    public void	activeBattery() throws Exception;
	
	public void	desactiveBattery() throws Exception;
	
	public boolean	isUsing() throws Exception;
	
	public double remainingChargePercentage() throws Exception;
	
	public void	chargeBattery() throws Exception;
	
	public void	dechargeBattery() throws Exception;

}
