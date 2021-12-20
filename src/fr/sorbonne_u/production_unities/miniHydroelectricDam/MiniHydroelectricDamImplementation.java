package fr.sorbonne_u.production_unities.miniHydroelectricDam;

public interface MiniHydroelectricDamImplementation {

	public void startMiniHydroelectricDam() throws Exception;

	public void stopMiniHydroelectricDam() throws Exception;

	public boolean isRunning() throws Exception;
}
