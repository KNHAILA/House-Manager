package fr.sorbonne_u.components.vacuumCleaner;

import fr.sorbonne_u.components.connectors.AbstractConnector;

//-----------------------------------------------------------------------------
/**
* The class <code>VacuumCleanerConnector</code> implements a connector for
* the <code>VacuumCleanerCI</code> component interface.
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
* @author	<a href="mailto:Jacques.MalenVacuumCleanert@lip6.fr">Jacques MalenVacuumCleanert</a>
*/
public class			VacuumCleanerConnector
extends		AbstractConnector
implements	VacuumCleanerCI
{
	/**
	 * @see fr.sorbonne_u.components.VacuumCleaner.VacuumCleanerCI#getState()
	 */
	@Override
	public VacuumCleanerState	getState() throws Exception
	{
		return ((VacuumCleanerCI)this.offering).getState();
	}

	/**
	 * @see fr.sorbonne_u.components.VacuumCleaner.VacuumCleanerCI#getMode()
	 */
	@Override
	public	VacuumCleanerMode	getMode() throws Exception
	{
		return ((VacuumCleanerCI)this.offering).getMode();
	}

	/**
	 * @see fr.sorbonne_u.components.VacuumCleaner.VacuumCleanerCI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		((VacuumCleanerCI)this.offering).turnOn();
	}

	/**
	 * @see fr.sorbonne_u.components.VacuumCleaner.VacuumCleanerCI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		((VacuumCleanerCI)this.offering).turnOff();
	}

	/**
	 * @see fr.sorbonne_u.components.VacuumCleaner.VacuumCleanerCI#setHigh()
	 */
	@Override
	public void	setHigh() throws Exception
	{
		((VacuumCleanerCI)this.offering).setHigh();
	}

	/**
	 * @see fr.sorbonne_u.components.VacuumCleaner.VacuumCleanerCI#setLow()
	 */
	@Override
	public void	setLow() throws Exception
	{
		((VacuumCleanerCI)this.offering).setLow();
	}
}
//-----------------------------------------------------------------------------
