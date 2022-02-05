package fr.sorbonne_u.production_unities.windTurbine;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;


/*
public class WindTurbineConnector extends AbstractConnector implements WindTurbineCI {

	
	@Override
	public boolean isRunning() throws Exception {
		return ((WindTurbineCI)this.offering).isRunning();
	}
	
	
	@Override
	public void startWindTurbine() throws Exception {
		((WindTurbineCI)this.offering).startWindTurbine();
	}
	
	
	@Override
	public void stopWindTurbine() throws Exception {
		((WindTurbineCI)this.offering).stopWindTurbine();
	}

	@Override
	public double getCurrentWindSpeed() throws Exception {
		return ((WindTurbineCI)this.offering).getCurrentWindSpeed();
	}
}
*/


public class			WindTurbineConnector
extends		AbstractConnector
implements	SuspensionEquipmentControlCI
{
				
	protected boolean	isSuspended;

	public				WindTurbineConnector()
	{
		super();
		this.isSuspended = false;
	}


	@Override
	public boolean		on() throws Exception
	{
		return this.isSuspended || ((WindTurbineCI)this.offering).isRunning();
	}

	
	@Override
	public boolean		switchOn() throws Exception
	{
		((WindTurbineCI)this.offering).startWindTurbine();
		return true;
	}

	
	@Override
	public boolean		switchOff() throws Exception
	{
		((WindTurbineCI)this.offering).stopWindTurbine();
		return true;
	}

	
	@Override
	public int			maxMode() throws Exception
	{
		// No mode in WindTurbine, so 1 becomes the sole "mode".
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
		// No mode in WindTurbine, so 1 becomes the sole "mode".
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
		((WindTurbineCI)this.offering).stopWindTurbine();
		this.isSuspended = true;
		return true;
	}

	@Override
	public boolean		resume() throws Exception
	{
		((WindTurbineCI)this.offering).startWindTurbine();
		this.isSuspended = false;
		return true;
	}

	@Override
	public double		emergency() throws Exception
	{
		return 0;
	}
}
// -----------------------------------------------------------------------------


