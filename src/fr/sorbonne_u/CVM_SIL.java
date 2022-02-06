package fr.sorbonne_u;

import fr.sorbonne_u.components.AbstractComponent;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
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

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.fan.Fan;
import fr.sorbonne_u.components.refrigerator.ThermostatedRefrigerator;
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleaner;
import fr.sorbonne_u.components.washingMachine.ThermostatedWashingMachine;
import fr.sorbonne_u.components.waterHeater.ThermostatedWaterHeater;
import fr.sorbonne_u.production_unities.windTurbine.SelfControlWindTurbine;

import fr.sorbonne_u.hem.HEM;
import fr.sorbonne_u.meter.ElectricMeter;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVM_SIL</code> execute the HEM in a software-in-the-loop
 * simulation mode.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-10-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVM_SIL
extends		AbstractCVM
{
	/** acceleration factor for the real time execution; it controls how fast
	 *  the simulation will run but keeping the same time structure as a real
	 *  time simulation running exactly at the pace of physical time; currently,
	 *  because of implementation constraints revolving around the precision
	 *  of the Java thread scheduler, this factor must be chosen in such a way
	 *  that intervals between simulation transitions or the execution of pieces
	 *  of code do not fall under 10 milliseconds approximately.			*/
	public static final double		ACC_FACTOR = 1.0;
	/** delay to start the real time simulations on every model at the
	 *  same moment (the order is delivered to the models during this
	 *  delay; this delay must be ample enough to give the time to notify
	 *  all models of their start time and to initialise them before starting,
	 *  a value that depends upon the complexity of the simulation architecture
	 *  to be traversed and the component deployment (deployments on several
	 *  JVM and even more several computers require a larger delay.			*/
	public static final long		DELAY_TO_START_SIMULATION = 1000L;
	/** duration  of the simulation.										*/
	public static final double		SIMULATION_DURATION = 10.0;

	public				CVM_SIL() throws Exception
	{

	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		//fan
		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				// the first actual parameter tells the component to create
				// a SIL simulation architecture for integration test and the
				// second 'false' that it must *not* be executed as a unit test.
				new Object[]{HEM_SIL_Supervisor.SIM_ARCHITECTURE_URI, false});
		
		// vacuum cleaner
		AbstractComponent.createComponent(
				VacuumCleaner.class.getCanonicalName(),
				new Object[]{HEM_SIL_Supervisor.SIM_ARCHITECTURE_URI, false});
		
		//water heater
		AbstractComponent.createComponent(
				ThermostatedWaterHeater.class.getCanonicalName(),
				// the first actual parameter tells the component to create
				// a SIL simulation architecture for integration test and the
				// second 'true' that it must be executed as a unit test.
				new Object[]{HEM_SIL_Supervisor.SIM_ARCHITECTURE_URI, false});
		
		//washing machine
		/*
		AbstractComponent.createComponent(
						ThermostatedWashingMachine.class.getCanonicalName(),
						new Object[]{HEM_SIL_Supervisor.SIM_ARCHITECTURE_URI, true});
						*/
		
		//refrigerator
		AbstractComponent.createComponent(
				ThermostatedRefrigerator.class.getCanonicalName(),
				new Object[]{HEM_SIL_Supervisor.SIM_ARCHITECTURE_URI, false});
				
		
		//WindTurbine
		/*
		AbstractComponent.createComponent(
				SelfControlWindTurbine.class.getCanonicalName(),
				new Object[]{HEM_SIL_Supervisor.SIM_ARCHITECTURE_URI, true});
				*/
		
		
		//meter
		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				// the first actual parameter tells the component to create
				// a SIL simulation architecture for integration test and the
				// second 'false' that it must *not* be executed as a unit test.
				new Object[]{HEM_SIL_Supervisor.SIM_ARCHITECTURE_URI, false});
		
		
		AbstractComponent.createComponent(
				HEM.class.getCanonicalName(),
				new Object[]{false});

		AbstractComponent.createComponent(
				SIL_Coordinator.class.getCanonicalName(),
				new Object[]{});
		AbstractComponent.createComponent(
				HEM_SIL_Supervisor.class.getCanonicalName(),
				new Object[]{});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVM_SIL cvm = new CVM_SIL();
			// given some margin to the component application execution
			// compared to the simulation duration
			long d = (long)(SIMULATION_DURATION*1000.0/ACC_FACTOR);
			cvm.startStandardLifeCycle(d + 5000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
