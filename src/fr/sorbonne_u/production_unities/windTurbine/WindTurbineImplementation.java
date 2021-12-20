package fr.sorbonne_u.production_unities.windTurbine;

public interface WindTurbineImplementation {
	
	public void	startWindTurbine() throws Exception;
	
	public void	stopWindTurbine() throws Exception;
	
	public boolean	isRunning() throws Exception;
	
	public void	WindIntensityControl() throws Exception;
}
