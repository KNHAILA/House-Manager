package fr.sorbonne_u.interfaces;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an
// example of a cyber-physical system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

// -----------------------------------------------------------------------------
/**
 * The component interface <code>SuspensionEquipmentControlCI</code> defines
 * the standard operation that an equipment implements to let the controller
 * suspend them <i>i.e.</i>, make their energy consumption the lowest possible.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SuspensionEquipmentControlCI
extends		StandardEquipmentControlCI
{
	/**
	 * return true if the equipment has been suspended.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @return				true if the equipment has been suspended.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		emergency() throws Exception;
	public boolean		suspended() throws Exception;

	/**
	 * suspend the equipment, returning true if the suspension succeeded or
	 * false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !suspended()}
	 * post	{@code !return || suspended()}
	 * </pre>
	 *
	 * @return				true if the suspension succeeded or false otherwise.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		suspend() throws Exception;

	/**
	 * resume the previously suspended equipment, returning true if the
	 * resumption succeeded or false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code suspended()}
	 * post	{@code !return || !suspended()}
	 * </pre>
	 *
	 * @return				true if the resumption succeeded or false otherwise.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		resume() throws Exception;

	/**
	 * return the degree of emergency of a resumption for the previously
	 * suspended equipment.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code suspended()}
	 * post	{@code return >= 0.0 && return <= 1.0}
	 * </pre>
	 *
	 * @return				the degree of emergency of a resumption for the previously suspended equipment.
	 * @throws Exception	<i>to do</i>.
	 */

}
// -----------------------------------------------------------------------------
