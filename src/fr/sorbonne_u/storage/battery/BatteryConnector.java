package fr.sorbonne_u.storage.battery;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;


public class			BatteryConnector
extends		AbstractConnector
implements	SuspensionEquipmentControlCI
{
	
	protected boolean	isSuspended;

	public				BatteryConnector()
	{
		super();
		this.isSuspended = false;
	}


	@Override
	public boolean		on() throws Exception
	{
		return this.isSuspended || ((BatteryCI)this.offering).isRunning();
	}

	
	@Override
	public boolean		switchOn() throws Exception
	{
		((BatteryCI)this.offering).startBattery();
		return true;
	}

	
	@Override
	public boolean		switchOff() throws Exception
	{
		((BatteryCI)this.offering).stopBattery();
		return true;
	}

	
	@Override
	public int			maxMode() throws Exception
	{
		// No mode in Battery, so 1 becomes the sole "mode".
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
		// No mode in Battery, so 1 becomes the sole "mode".
		return 1;
	}

	@Override
	public boolean		suspended() throws Exception
	{
		return false;
	}

	@Override
	public boolean		suspend() throws Exception
	{
		return false;
	}

	@Override
	public boolean		resume() throws Exception
	{
		return false;
	}

	@Override
	public double		emergency() throws Exception
	{
		return 0;
	}
}