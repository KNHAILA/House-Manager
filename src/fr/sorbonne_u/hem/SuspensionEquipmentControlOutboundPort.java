package fr.sorbonne_u.hem;


import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;


// -----------------------------------------------------------------------------
/**
 * The class <code>SuspensionEquipmentControlOutboundPort</code> implements an
 * outbound port for the {@code SuspensionEquipmentControlCI} component
 * interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-09-13</p>
 * 
 *  @authors	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 *              <a href="maedeh.daemi@etu.sorbonne-universite.fr">DAEMI Maedeh</a>
 */
public class SuspensionEquipmentControlOutboundPort
extends		StandardEquipmentControlOutboundPort
implements	SuspensionEquipmentControlCI
{
	private static final long serialVersionUID = 1L;

	public				SuspensionEquipmentControlOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(SuspensionEquipmentControlCI.class, owner);
	}

	public				SuspensionEquipmentControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, SuspensionEquipmentControlCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.SuspensionEquipmentControlCI#suspended()
	 */
	@Override
	public boolean		suspended() throws Exception
	{
		assert	this.on();
		return ((SuspensionEquipmentControlCI)this.getConnector()).suspended();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.SuspensionEquipmentControlCI#suspend()
	 */
	@Override
	public boolean		suspend() throws Exception
	{
		assert	!this.suspended();
		boolean ret = ((SuspensionEquipmentControlCI)this.getConnector()).
																	suspend();
		assert	!ret || this.suspended();
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.SuspensionEquipmentControlCI#resume()
	 */
	@Override
	public boolean		resume() throws Exception
	{
		assert	this.suspended();
		boolean ret = ((SuspensionEquipmentControlCI)this.getConnector()).
																	resume();
		assert	!ret || !this.suspended();
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021.interfaces.SuspensionEquipmentControlCI#emergency()
	 */
	@Override
	public double		emergency() throws Exception
	{
		assert	this.suspended();
		double ret = ((SuspensionEquipmentControlCI)this.getConnector()).
																emergency();
		assert	ret >= 0.0 && ret <= 1.0;
		return ret;
	}
}
// -----------------------------------------------------------------------------
