package fr.sorbonne_u.storage.battery.sil;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.storage.battery.mil.BatteryElectricityModel.State;
import fr.sorbonne_u.storage.battery.mil.events.*;
import fr.sorbonne_u.storage.battery.Battery;
import fr.sorbonne_u.storage.battery.BatteryRTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.utils.StandardComponentLogger;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

//-----------------------------------------------------------------------------
/**
* The class <code>BatteryStateModel</code> defines a SIL simulation model
* that simulate the evolution of the state of the Battery over time.
*
* <p><strong>Description</strong></p>
* 
* <p>
* In SIL simulation, the mode changes in the Battery are controlled by the
* Battery component which will issue external events into its simulation
* model. In the MIL implementation, such events are received both by the
* the {@code BatteryElectricityModel} and the {@code BatteryTemperatureModel}.
* In the SIL simulation, the {@code BatteryElectricitySILModel} will be
* executing inside the {@code ElectricMeter} component, not inside the
* {@code Battery} component. Therefore, events sent to it must
* be reemitted to the model in the {@code ElectricMeter} component.
* </p>
* <p>
* BCM4Java-CyPhy allows a component to issue an external event into its own
* simulation models, but not directly to models in other components. Hence,
* the present model is added to receive the events issued by the
* {@code Battery} component and reemit them to the
* {@code BatteryElectricitySILModel} in the {@code ElectricMeter} component.
* </p>
* <p>
* After taking care of the above, it turns out to be simpler to make
* this new {@code BatteryStateModel} responsible for also sending the
* events to the {@code BatteryTemperatureSILModel}.
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
 *  @authors	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
 *              <a href="maedeh.daemi@etu.sorbonne-universite.fr">DAEMI Maedeh</a>
*/
//-----------------------------------------------------------------------------
@ModelExternalEvents(
		imported = {UseBattery.class,DoNotUseBattery.class, ChargeBattery.class, DoNotChargeBattery.class},
		exported = {UseBattery.class,DoNotUseBattery.class, ChargeBattery.class, DoNotChargeBattery.class})
//-----------------------------------------------------------------------------
public class			BatteryStateModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long			serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String			URI = BatteryStateModel.class.
															getSimpleName();

	/** current state of the battery.									*/
	protected State						currentState;
	/** last received event or null if none.								*/
	protected BatteryEventI				lastReceived;

	/** owner component.													*/
	protected Battery		owner;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a Battery state model instance.
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
	public				BatteryStateModel(
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
						BatteryRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
		this.owner =
				(Battery) simParams.get(
						BatteryRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
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
		this.currentState = State.USE;

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
		// and for the battery model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		this.lastReceived = (BatteryEventI) currentEvents.get(0);

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
//-----------------------------------------------------------------------------
