package fr.sorbonne_u.components.fan.sil;

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
import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.components.fan.Fan;
import fr.sorbonne_u.components.fan.FanRTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.utils.StandardComponentLogger;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerUserModel</code> defines a very simple user
 * model for the hair dryer.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model is meant to illustrate how to program user SIL models that
 * triggers code executions in the owner component to simulate user actions.
 * Using simulation models to do so ensure the time coherence between the
 * real time simulation of SIL models and the code executions.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-09-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			FanUserModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long			serialVersionUID = 1L;
	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String			URI = FanUserModel.class.
																getSimpleName();


	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String			STEP_MEAN_DURATION_RUNPNAME =
												URI + ":STEP_MEAN_DURATION";
	/** time interval between event outputs.								*/
	protected static double				STEP_MEAN_DURATION = 2.0;
	/**	the random number generator from common math library.				*/
	protected final RandomDataGenerator	rg ;
	/** last step in the test scenario.										*/
	protected static final int			LAST_STEP = 4;
	/** current step in the test scenario.									*/
	protected int						currentStep;
	/** delay before performing the next step in the test scenario.			*/
	protected Duration					time2nextStep;

	/** owner component.													*/
	protected Fan					owner;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer user MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine != null implies simulationEngine instanceof AtomicEngine}
	 * post	{@code getURI() != null}
	 * post	{@code uri != null implies getURI().equals(uri)}
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
	public				FanUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * generate the next event in the test scenario; current implementation
	 * cycles through {@code SwitchOnHairDryer}, {@code SetHighHairDryer},
	 * {@code SetLowHairDryer} and {@code SwitchOffHairDryer} in this order
	 * at a random time interval following a gaussian distribution with
	 * mean {@code STEP_MEAN_DURATION} and standard deviation
	 * {@code STEP_MEAN_DURATION/2.0}.
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 */
	protected void		generateNextEvent()
	{
		if (this.currentStep <= LAST_STEP) {
			double delay =
					Math.max(this.rg.nextGaussian(STEP_MEAN_DURATION,
												  STEP_MEAN_DURATION/2.0),
							 0.1);
			this.time2nextStep = new Duration(delay,
											  this.getSimulatedTimeUnit());
		} else {
			this.time2nextStep = Duration.INFINITY;
		}
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

		if (simParams.containsKey(STEP_MEAN_DURATION_RUNPNAME)) {
			STEP_MEAN_DURATION =
					(double) simParams.get(STEP_MEAN_DURATION_RUNPNAME);
		}
		// retrieve the reference to the owner component that must be passed
		// as a simulation run parameter
		assert	simParams.containsKey(
					FanRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
		this.owner =
				(Fan)simParams.get(
					FanRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
		this.setLogger(new StandardComponentLogger(this.owner));
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.rg.reSeedSecure();

		// compute the delay to the first event
		this.generateNextEvent();
		this.currentStep = 1;
		// re-initialisation of the time of occurrence of the next event
		super.initialiseState(initialTime);
//		this.nextTimeAdvance = this.timeAdvance();
//		this.timeOfNextEvent =
//				this.getCurrentStateTime().add(this.getNextTimeAdvance());

		this.toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.time2nextStep;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		StringBuffer message = new StringBuffer(this.uri);

		// simple way to implement a test scenario
		// notice that the simulation model will drive code executions so
		// that they occur at coherent times compared to the real time
		// simulation; SIL simulation are executed in real time (possibly
		// accelerated) to get such coherence between code and simulation
		// executions
		switch (this.currentStep) {
		case 1:
			this.owner.runTask(o -> {try {
										((Fan)o).turnOn();
									 } catch (Exception e) {
										 e.printStackTrace();
									 }
									});
			message.append(" executes the operation turnOn.\n");
			break;
		case 2:
			this.owner.runTask(o -> {try {
										((Fan)o).setHigh();
									 } catch (Exception e) {
										 e.printStackTrace();
									 }
									});
			message.append(" executes the operation setHigh.\n");
			break;
		case 3:
			this.owner.runTask(o -> {try {
										((Fan)o).setLow();
									 } catch (Exception e) {
										 e.printStackTrace();
									 }
									});
			message.append(" executes the operation setLow.\n");
			break;
		case 4:
			this.owner.runTask(o -> {try {
										((Fan)o).turnOff();
									 } catch (Exception e) {
										 e.printStackTrace();
									 }
									});
			message.append(" executes the operation turnOff.\n");
			break;
		default:
			message.append(" ended.\n");
		}

		this.currentStep++;
		this.generateNextEvent();

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

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return null;
	}
}
// -----------------------------------------------------------------------------
