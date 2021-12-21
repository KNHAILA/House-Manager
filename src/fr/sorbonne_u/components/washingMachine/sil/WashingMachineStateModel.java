package fr.sorbonne_u.components.washingMachine.sil;

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

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.washingMachine.mil.WashingMachineElectricityModel.State;
import fr.sorbonne_u.components.washingMachine.mil.events.DoNotHeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.HeatWater;
import fr.sorbonne_u.components.washingMachine.mil.events.WashingMachineEventI;
import fr.sorbonne_u.components.washingMachine.mil.events.SwitchOffWashingMachine;
import fr.sorbonne_u.components.washingMachine.mil.events.SwitchOnWashingMachine;
import fr.sorbonne_u.components.washingMachine.ThermostatedWashingMachine;
import fr.sorbonne_u.components.washingMachine.ThermostatedWashingMachineRTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.utils.StandardComponentLogger;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterStateModel</code> defines a SIL simulation model
 * that simulate the evolution of the state of the heater over time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In SIL simulation, the mode changes in the heater are controlled by the
 * heater component which will issue external events into its simulation
 * model. In the MIL implementation, such events are received both by the
 * the {@code HeaterElectricityModel} and the {@code HeaterTemperatureModel}.
 * In the SIL simulation, the {@code HeaterElectricitySILModel} will be
 * executing inside the {@code ElectricMeter} component, not inside the
 * {@code ThermostatedHeater} component. Therefore, events sent to it must
 * be reemitted to the model in the {@code ElectricMeter} component.
 * </p>
 * <p>
 * BCM4Java-CyPhy allows a component to issue an external event into its own
 * simulation models, but not directly to models in other components. Hence,
 * the present model is added to receive the events issued by the
 * {@code ThermostatedHeater} component and reemit them to the
 * {@code HeaterElectricitySILModel} in the {@code ElectricMeter} component.
 * </p>
 * <p>
 * After taking care of the above, it turns out to be simpler to make
 * this new {@code HeaterStateModel} responsible for also sending the
 * events to the {@code HeaterTemperatureSILModel}.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-10-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(
		imported = {SwitchOnWashingMachine.class,SwitchOffWashingMachine.class,
					HeatWater.class,DoNotHeatWater.class},
		exported = {SwitchOnWashingMachine.class,SwitchOffWashingMachine.class,
					HeatWater.class,DoNotHeatWater.class})
// -----------------------------------------------------------------------------
public class			WashingMachineStateModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long			serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String			URI = WashingMachineStateModel.class.
															getSimpleName();

	/** current state of the hair dryer.									*/
	protected State						currentState;
	/** last received event or null if none.								*/
	protected WashingMachineEventI				lastReceived;

	/** owner component.													*/
	protected ThermostatedWashingMachine		owner;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a heater state model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
	 * post	{@code getURI() != null}
	 * post	{@code uri != null implies this.getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code simulationEngine != null implies getSimulationEngine().equals(simulationEngine)}
	 * post	{@code !isDebugModeOn()}
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				WashingMachineStateModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		super.setSimulationRunParameters(simParams);

		// retrieve the reference to the owner component that must be passed
		// as a simulation run parameter
		assert	simParams.containsKey(
						ThermostatedWashingMachineRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
		this.owner =
				(ThermostatedWashingMachine) simParams.get(
						ThermostatedWashingMachineRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
		this.setLogger(new StandardComponentLogger(this.owner));
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.lastReceived = null;
		this.currentState = State.OFF;

		this.toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		assert	this.lastReceived != null;
		ArrayList<EventI> ret = new ArrayList<EventI>();
		ret.add(this.lastReceived);
		this.lastReceived = null;
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.lastReceived != null) {
			// trigger an immediate internal transition
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			// wait until the next external event that will trigger an internal
			// transition
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the hair dryer model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		this.lastReceived = (WashingMachineEventI) currentEvents.get(0);

		StringBuffer message = new StringBuffer(this.uri);
		message.append(" executes the external event ");
		message.append(this.lastReceived.getClass().getSimpleName());
		message.append("(");
		message.append(
			this.lastReceived.getTimeOfOccurrence().getSimulatedTime());
		message.append(")\n");
		this.logMessage(message.toString());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}
}
// -----------------------------------------------------------------------------
