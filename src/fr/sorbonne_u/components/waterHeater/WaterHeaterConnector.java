package fr.sorbonne_u.components.waterHeater;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;

//-----------------------------------------------------------------------------
/**
* The class <code>WaterHeaterConnector</code> implements a connector for the
* {@code WaterHeaterCI} component interface.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
* <p>Created on : 2021-10-12</p>
* 
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

/*
public class WaterHeaterConnector extends AbstractConnector implements WaterHeaterCI {
	
	@Override
	public boolean isRunning() throws Exception {
		return ((WaterHeaterCI)this.offering).isRunning();
	}
	
	
	@Override
	public void startWaterHeater() throws Exception {
		((WaterHeaterCI)this.offering).startWaterHeater();
	}
	
	
	@Override
	public void stopWaterHeater() throws Exception {
		((WaterHeaterCI)this.offering).stopWaterHeater();
	}

	
	@Override
	public void setTargetTemperature(double target) throws Exception {
		((WaterHeaterCI)this.offering).setTargetTemperature(target);
	}
	
	
	@Override
	public double getTargetTemperature() throws Exception {
		return ((WaterHeaterCI)this.offering).getTargetTemperature();
	}
	
	
	@Override
	public double getCurrentTemperature() throws Exception {
		return ((WaterHeaterCI)this.offering).getCurrentTemperature();
	}
}

*/


public class			WaterHeaterConnector
extends		AbstractConnector
implements	SuspensionEquipmentControlCI
{
	// the minimum admissible temperature from which the water heater should
	// be resumed in priority after being suspended to save energy.		
	protected static final double	MIN_ADMISSIBLE_TEMP = 12.0;
	// the maximal admissible difference between the target and the
	//  current temperature from which the water heater should be resumed in
	//  priority after being suspended to save energy.						
	protected static final double	MAX_ADMISSIBLE_DELTA = 10.0;
	// true if the water heater has been suspended, false otherwise.				
	protected boolean	isSuspended;

	public				WaterHeaterConnector()
	{
		super();
		this.isSuspended = false;
	}


	@Override
	public boolean		on() throws Exception
	{
		return this.isSuspended || ((WaterHeaterCI)this.offering).isRunning();
	}

	
	@Override
	public boolean		switchOn() throws Exception
	{
		((WaterHeaterCI)this.offering).startWaterHeater();
		return true;
	}

	
	@Override
	public boolean		switchOff() throws Exception
	{
		((WaterHeaterCI)this.offering).stopWaterHeater();
		return true;
	}

	
	@Override
	public int			maxMode() throws Exception
	{
		// No mode in waterheater, so 1 becomes the sole "mode".
		return 1;
	}

	@Override
	public boolean		upMode() throws Exception
	{
		return false;
	}

	@Override
	public boolean		downMode() throws Exception
	{
		return false;
	}


	@Override
	public boolean		setMode(int modeIndex) throws Exception
	{
		return true;
	}

	@Override
	public int			currentMode() throws Exception
	{
		// No mode in waterheater, so 1 becomes the sole "mode".
		return 1;
	}

	@Override
	public boolean		suspended() throws Exception
	{
		return this.isSuspended;
	}

	@Override
	public boolean		suspend() throws Exception
	{
		((WaterHeaterCI)this.offering).stopWaterHeater();
		this.isSuspended = true;
		return true;
	}

	@Override
	public boolean		resume() throws Exception
	{
		((WaterHeaterCI)this.offering).startWaterHeater();
		this.isSuspended = false;
		return true;
	}

	@Override
	public double		emergency() throws Exception
	{
		double currentTemperature =
					((WaterHeaterCI)this.offering).getCurrentTemperature();
		double targetTemperature =
					((WaterHeaterCI)this.offering).getTargetTemperature();
		double delta = Math.abs(targetTemperature - currentTemperature);
		if (currentTemperature < WaterHeaterConnector.MIN_ADMISSIBLE_TEMP ||
							delta >= WaterHeaterConnector.MAX_ADMISSIBLE_DELTA) {
			return 1.0;
		} else {
			return delta/WaterHeaterConnector.MAX_ADMISSIBLE_DELTA;
		}
	}
}
// -----------------------------------------------------------------------------


