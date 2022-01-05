package fr.sorbonne_u.production_unities.miniHydroelectricDam.sil;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.plugins.devs.utils.StandardComponentLogger;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineElectricityModel.State;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.SelfControlMiniHydroelectricDam;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.*;

//-----------------------------------------------------------------------------
/**
* The class <code>WindTurbineStateModel</code> defines a SIL simulation model
* that simulate the evolution of the state of the WindTurbine over time.
*
* <p><strong>Description</strong></p>
* 
* <p>
* In SIL simulation, the mode changes in the WindTurbine are controlled by the
* WindTurbine component which will issue external events into its simulation
* model. In the MIL implementation, such events are received both by the
* the {@code WindTurbineElectricityModel} and the {@code WindTurbineTemperatureModel}.
* In the SIL simulation, the {@code WindTurbineElectricitySILModel} will be
* executing inside the {@code ElectricMeter} component, not inside the
* {@code SelfControlWindTurbine} component. Therefore, events sent to it must
* be reemitted to the model in the {@code ElectricMeter} component.
* </p>
* <p>
* BCM4Java-CyPhy allows a component to issue an external event into its own
* simulation models, but not directly to models in other components. Hence,
* the present model is added to receive the events issued by the
* {@code SelfControlWindTurbine} component and reemit them to the
* {@code WindTurbineElectricitySILModel} in the {@code ElectricMeter} component.
* </p>
* <p>
* After taking care of the above, it turns out to be simpler to make
* this new {@code WindTurbineStateModel} responsible for also sending the
* events to the {@code WindTurbineTemperatureSILModel}.
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
//-----------------------------------------------------------------------------
@ModelExternalEvents(
		imported = {StopMiniHydroelectricDam.class, UseMiniHydroelectricDam.class,DoNotMiniHydroelectricDam.class, StartMiniHydroelectricDam.class},
		exported = {StopMiniHydroelectricDam.class, UseMiniHydroelectricDam.class,DoNotMiniHydroelectricDam.class, StartMiniHydroelectricDam.class})
//-----------------------------------------------------------------------------
public class			MiniHydroelectricDamStateModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long			serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String			URI = MiniHydroelectricDamStateModel.class.
															getSimpleName();

	/** current state of the wind turbine.									*/
	protected State						currentState;
	/** last received event or null if none.								*/
	protected AbstractMiniHydroelectricDamEvent lastReceived;

	/** owner component.													*/
	protected SelfControlMiniHydroelectricDam		owner;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a WindTurbine state model instance.
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
	public				MiniHydroelectricDamStateModel(
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
						SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
		this.owner =
				(SelfControlMiniHydroelectricDam) simParams.get(
						SelfControlMiniHydroelectricDamRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
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
		this.currentState = State.NOT_USE;

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
		// and for the wind turbine model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		this.lastReceived = (AbstractMiniHydroelectricDamEvent) currentEvents.get(0);

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
