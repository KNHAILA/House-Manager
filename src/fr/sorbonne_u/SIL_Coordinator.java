package fr.sorbonne_u;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
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

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

// -----------------------------------------------------------------------------
/**
 * The class <code>SIL_Coordinator</code> defines the component used in the HEM
 * example to execute the simulation coordinator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In SIL simulations, global simulation architectures created across components
 * use coordinator components to hold and execute coupled models. Little needs
 * to be defined in coordinator components as their coupled model will be
 * created automatically by the supervisor component from the simulation
 * architecture and it will be used through the coordinator plug-in provided
 * by BCM4Java-CyPhy and also created and installed on the component
 * automatically.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2020-12-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SIL_Coordinator
extends		AbstractCyPhyComponent
{
	public static final String	REFLECTION_INBOUND_PORT_URI =
														"coordination-ribpuri";
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;

	protected			SIL_Coordinator()
	{
		super(REFLECTION_INBOUND_PORT_URI, 1, 0);

		if (VERBOSE) {
			this.tracer.get().setTitle("Coordinator component");
			this.tracer.get().setRelativePosition(0, 1);
			this.toggleTracing();
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException
	{
		super.start();

		this.traceMessage("Coordinator starts.\n");
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		this.traceMessage("Coordinator stops.\n");

		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
