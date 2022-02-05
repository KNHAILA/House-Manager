package fr.sorbonne_u.components.refrigerator;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;
//-----------------------------------------------------------------------------
/**
* The class <code>RefrigeratorConnector</code> implements a connector for the
* {@code RefrigeratorCI} component interface.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
* <p>Created on : 2021-10-07</p>
* 
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/
/*
public class RefrigeratorConnector extends AbstractConnector implements RefrigeratorCI {

	
	@Override
	public boolean isRunning() throws Exception {
		return ((RefrigeratorCI)this.offering).isRunning();
	}
	
	
	@Override
	public void startRefrigerator() throws Exception {
		((RefrigeratorCI)this.offering).startRefrigerator();
	}
	
	
	@Override
	public void stopRefrigerator() throws Exception {
		((RefrigeratorCI)this.offering).stopRefrigerator();
	}
	
	
	@Override
	public void setTargetTemperature(double target) throws Exception {
		((RefrigeratorCI)this.offering).setTargetTemperature(target);
	}
	
	
	@Override
	public double getTargetTemperature() throws Exception {
		return ((RefrigeratorCI)this.offering).getTargetTemperature();
	}
	
	
	@Override
	public double getCurrentTemperature() throws Exception {
		return ((RefrigeratorCI)this.offering).getCurrentTemperature();
	}
}
*/

public class			RefrigeratorConnector
extends		AbstractConnector
implements	SuspensionEquipmentControlCI
{
	/** the minimum admissible temperature from which the Refrigerator should
	 *  be resumed in priority after being suspended to save energy.		*/
	protected static final double	MIN_ADMISSIBLE_TEMP = 12.0;
	/** the maximal admissible difference between the target and the
	 *  current temperature from which the Refrigerator should be resumed in
	 *  priority after being suspended to save energy.						*/
	protected static final double	MAX_ADMISSIBLE_DELTA = 10.0;
	/** true if the Refrigerator has been suspended, false otherwise.				*/
	protected boolean	isSuspended;

	public				RefrigeratorConnector()
	{
		super();
		this.isSuspended = false;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.StandardEquipmentControlCI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		System.out.println("connector on ******");
		return this.isSuspended || ((RefrigeratorCI)this.offering).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.StandardEquipmentControlCI#switchOn()
	 */
	@Override
	public boolean		switchOn() throws Exception
	{
		System.out.println("connector switchOn ******");
		((RefrigeratorCI)this.offering).startRefrigerator();
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.StandardEquipmentControlCI#switchOff()
	 */
	@Override
	public boolean		switchOff() throws Exception
	{
		System.out.println("connector switchOff ******");
		((RefrigeratorCI)this.offering).stopRefrigerator();
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.StandardEquipmentControlCI#maxMode()
	 */
	@Override
	public int			maxMode() throws Exception
	{
		// No mode in Refrigerator, so 1 becomes the sole "mode".
		return 1;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.StandardEquipmentControlCI#upMode()
	 */
	@Override
	public boolean		upMode() throws Exception
	{
		return false;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.StandardEquipmentControlCI#downMode()
	 */
	@Override
	public boolean		downMode() throws Exception
	{
		return false;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.StandardEquipmentControlCI#setMode(int)
	 */
	@Override
	public boolean		setMode(int modeIndex) throws Exception
	{
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.StandardEquipmentControlCI#currentMode()
	 */
	@Override
	public int			currentMode() throws Exception
	{
		// No mode in Refrigerator, so 1 becomes the sole "mode".
		return 1;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.SuspensionEquipmentControlCI#suspended()
	 */
	@Override
	public boolean		suspended() throws Exception
	{
		return this.isSuspended;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.SuspensionEquipmentControlCI#suspend()
	 */
	@Override
	public boolean		suspend() throws Exception
	{
		System.out.println("connector suspend ******");
		((RefrigeratorCI)this.offering).stopRefrigerator();
		this.isSuspended = true;
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.SuspensionEquipmentControlCI#resume()
	 */
	@Override
	public boolean		resume() throws Exception
	{
		System.out.println("connector resume ******");
		((RefrigeratorCI)this.offering).startRefrigerator();
		this.isSuspended = false;
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.SuspensionEquipmentControlCI#emergency()
	 */
	@Override
	public double		emergency() throws Exception
	{
		System.out.println("connector emergency ******");
		double currentTemperature =
					((RefrigeratorCI)this.offering).getCurrentTemperature();
		double targetTemperature =
					((RefrigeratorCI)this.offering).getTargetTemperature();
		double delta = Math.abs(targetTemperature - currentTemperature);
		if (currentTemperature < RefrigeratorConnector.MIN_ADMISSIBLE_TEMP ||
							delta >= RefrigeratorConnector.MAX_ADMISSIBLE_DELTA) {
			return 1.0;
		} else {
			return delta/RefrigeratorConnector.MAX_ADMISSIBLE_DELTA;
		}
	}
}
// -----------------------------------------------------------------------------

