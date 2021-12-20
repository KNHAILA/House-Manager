package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.connectors.AbstractConnector;

//-----------------------------------------------------------------------------
/**
* The class <code>FanConnector</code> implements a connector for
* the <code>FanCI</code> component interface.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant		true
* </pre>
* 
* <p>Created on : 2020-10-02</p>
* 
* @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
*/
public class			FanConnector
extends		AbstractConnector
implements	FanCI
{
	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanCI#getState()
	 */
	@Override
	public State	getState() throws Exception
	{
		return ((FanCI)this.offering).getState();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanCI#getMode()
	 */
	@Override
	public	Mode	getMode() throws Exception
	{
		return ((FanCI)this.offering).getMode();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanCI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		((FanCI)this.offering).turnOn();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanCI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		((FanCI)this.offering).turnOff();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanCI#setHigh()
	 */
	@Override
	public void	setHigh() throws Exception
	{
		((FanCI)this.offering).setHigh();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.fan.FanCI#setLow()
	 */
	@Override
	public void	setLow() throws Exception
	{
		((FanCI)this.offering).setLow();
	}
}
//-----------------------------------------------------------------------------
