package fr.sorbonne_u.meter;

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

import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.meter.mil.ElectricMeterElectricityModel;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.meter.sil.ElectricMeterCoupledModel;
import fr.sorbonne_u.meter.sil.ElectricMeterElectricitySILModel;

//fan
import fr.sorbonne_u.components.fan.mil.events.*;
import fr.sorbonne_u.components.fan.FanRTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.fan.sil.FanElectricitySILModel;

//water heater
import fr.sorbonne_u.components.waterHeater.mil.events.*;
import fr.sorbonne_u.components.waterHeater.sil.WaterHeaterElectricitySILModel;
import fr.sorbonne_u.components.waterHeater.ThermostatedWaterHeaterRTAtomicSimulatorPlugin;

//refrigerator
import fr.sorbonne_u.components.refrigerator.ThermostatedRefrigeratorRTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.refrigerator.mil.events.*;
import fr.sorbonne_u.components.refrigerator.sil.RefrigeratorElectricitySILModel;

//Wind turbine
import fr.sorbonne_u.production_unities.windTurbine.sil.WindTurbineElectricitySILModel;
import fr.sorbonne_u.production_unities.windTurbine.SelfControlWindTurbineRTAtomicSimulatorPlugin;
import fr.sorbonne_u.production_unities.windTurbine.mil.WindTurbineElectricityModel;
import fr.sorbonne_u.production_unities.windTurbine.mil.events.*;

//vacuum cleaner
import fr.sorbonne_u.components.vacuumCleaner.VacuumCleanerRTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.vacuumCleaner.mil.events.*;
import fr.sorbonne_u.components.vacuumCleaner.sil.VacuumCleanerElectricitySILModel;
// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeterRTAtomicSimulatorPlugin</code> implements
 * the simulation plug-in for the <code>ElectricMeter</code> component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2021-10-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ElectricMeterRTAtomicSimulatorPlugin
extends		RTAtomicSimulatorPlugin
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** simulation architectures can have URI to name them; this is the
	 *  URI used in this example for unit tests.							*/
	public static final String		UNIT_TEST_SIM_ARCHITECTURE_URI =
															"UnitTestMeter";
	/** name used to pass the owner component reference as simulation
	 *  parameter.															*/
	public static final String		METER_REFERENCE_NAME = "MCRN";

	// Hair dryer and heater electricity models to be co-instantiated with the
	// electric meter electricity model to enable the sharing of continuous
	// variables between them, the electricity consumption of the two
	// appliances.
	// Nota: these static variables try to abstract the electric meter
	// implementation from the implementation of the appliances simulators.
	// Indeed, this solution still names explicitly the classes that implements
	// the models of the appliances, introducing a coupling between the present
	// class and the two model classes. A better solution should be implemented,
	// but for ALASCA, this way of doing things will be fine (and simple).

	/** URI of the fan electricity model.								*/
	protected static final String	FAN_ELECTRICITY_MODEL_URI =
											FanElectricitySILModel.URI;
	protected static final String	VACUUMCLEANER_ELECTRICITY_MODEL_URI =
			VacuumCleanerElectricitySILModel.URI;
	/** class implementing the FAN electricity model.					*/
	protected static final Class<FanElectricitySILModel>
									FAN_ELECTRICITY_MODEL_CLASS =
											FanElectricitySILModel.class;
	
	protected static final Class<VacuumCleanerElectricitySILModel>
	                               VACUUMCLEANER_ELECTRICITY_MODEL_CLASS =
	                            		   VacuumCleanerElectricitySILModel.class;
	/** URI of the heater electricity model.								*/
	protected static final String	WATER_HEATER_ELECTRICITY_MODEL_URI =
											WaterHeaterElectricitySILModel.URI;
	/** class implementing the heater electricity model.					*/
	protected static final Class<WaterHeaterElectricitySILModel>
	                                WATER_HEATER_ELECTRICITY_MODEL_CLASS =
											WaterHeaterElectricitySILModel.class;
	
	/** URI of the wind turbine electricity model.								*/
	protected static final String	WIND_TURBINE_ELECTRICITY_MODEL_URI =
											WindTurbineElectricitySILModel.URI;
	/** class implementing the wind turbine electricity model.					*/
	protected static final Class<WindTurbineElectricitySILModel>
	                                WIND_TURBINE_ELECTRICITY_MODEL_CLASS =
	                                		WindTurbineElectricitySILModel.class;
	
	/** URI of the refrigerator electricity model.								*/
	protected static final String	REFRIGERATOR_ELECTRICITY_MODEL_URI =
											RefrigeratorElectricitySILModel.URI;
	/** class implementing the refrigerator electricity model.					*/
	protected static final Class<RefrigeratorElectricitySILModel>
	                                REFRIGERATOR_ELECTRICITY_MODEL_CLASS =
	                                		RefrigeratorElectricitySILModel.class;
	

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// initialise the simulation parameter giving the reference to the
		// owner component before passing the parameters to the simulation
		// models; because each model has been defined to retrieve the
		// reference to its owner component using its own parameter name,
		// we must pass the reference under each different name
		
		//meter
		simParams.put(METER_REFERENCE_NAME, this.getOwner());
		
		//water heater
		simParams.put(ThermostatedWaterHeaterRTAtomicSimulatorPlugin.
														OWNER_REFERENCE_NAME,
					  this.getOwner());
		
		//refrigerator
		System.out.println("meter plugin 1 ******");
		simParams.put(ThermostatedRefrigeratorRTAtomicSimulatorPlugin.
				OWNER_REFERENCE_NAME,
                  this.getOwner());
                  
		
		//wind turbine
	/*	simParams.put(SelfControlWindTurbineRTAtomicSimulatorPlugin.
																OWNER_REFERENCE_NAME,
							  this.getOwner());
							  */
		
		//fan
		simParams.put(FanRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME,
					  this.getOwner());
		
		//VacuumCleaner
		simParams.put(VacuumCleanerRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME,
					this.getOwner());

		// this will pass the parameters to the simulation models that will
		// then be able to get their own parameters
		super.setSimulationRunParameters(simParams);

		// remove the value so that the reference may not exit the context of
		// the component
		simParams.remove(METER_REFERENCE_NAME);
		simParams.remove(ThermostatedWaterHeaterRTAtomicSimulatorPlugin.
														OWNER_REFERENCE_NAME);
		simParams.remove(ThermostatedRefrigeratorRTAtomicSimulatorPlugin.
				OWNER_REFERENCE_NAME);
				
	/*	simParams.remove(SelfControlWindTurbineRTAtomicSimulatorPlugin.
				OWNER_REFERENCE_NAME);
				*/
		simParams.remove(FanRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
		simParams.remove(VacuumCleanerRTAtomicSimulatorPlugin.OWNER_REFERENCE_NAME);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * create and set the simulation architecture internal to this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simArchURI != null && !simArchURIisEmpty()}
	 * pre	{@code accFactor > 0.0}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param simArchURI	URI of the simulation architecture to be created.
	 * @param accFactor		acceleration factor used in the real time simulation.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			initialiseSimulationArchitecture(
		String simArchURI,
		double accFactor
		) throws Exception
	{
		// For the project, the coupled model created for the electric meter
		// will include all of the models simulating the electricity consumption
		// and production for appliances and production units.
		// At this point, this only includes the electricity consumption models
		// of the hair dryer and the heater.

		Map<String,AbstractAtomicModelDescriptor>
									atomicModelDescriptors = new HashMap<>();
		Map<String,CoupledModelDescriptor>
									coupledModelDescriptors = new HashMap<>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(FAN_ELECTRICITY_MODEL_URI);
		submodels.add(VACUUMCLEANER_ELECTRICITY_MODEL_URI);
		submodels.add(WATER_HEATER_ELECTRICITY_MODEL_URI);
		submodels.add(REFRIGERATOR_ELECTRICITY_MODEL_URI);
		//submodels.add(WIND_TURBINE_ELECTRICITY_MODEL_URI);
		submodels.add(ElectricMeterElectricitySILModel.URI);

		//fan
		atomicModelDescriptors.put(
				FAN_ELECTRICITY_MODEL_URI,
				RTAtomicHIOA_Descriptor.create(
						FAN_ELECTRICITY_MODEL_CLASS,
						FAN_ELECTRICITY_MODEL_URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));
		
		//vacuumCleaner
		atomicModelDescriptors.put(
						VACUUMCLEANER_ELECTRICITY_MODEL_URI,
						RTAtomicHIOA_Descriptor.create(
								VACUUMCLEANER_ELECTRICITY_MODEL_CLASS,
								VACUUMCLEANER_ELECTRICITY_MODEL_URI,
								TimeUnit.SECONDS,
								null,
								SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
								accFactor));
		
		//water heater
		atomicModelDescriptors.put(
				WATER_HEATER_ELECTRICITY_MODEL_URI,
				RTAtomicHIOA_Descriptor.create(
						WATER_HEATER_ELECTRICITY_MODEL_CLASS,
						WATER_HEATER_ELECTRICITY_MODEL_URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));
		
		//Wind turbine
		/*
		atomicModelDescriptors.put(
						WIND_TURBINE_ELECTRICITY_MODEL_URI,
						RTAtomicHIOA_Descriptor.create(
						WIND_TURBINE_ELECTRICITY_MODEL_CLASS,
						WIND_TURBINE_ELECTRICITY_MODEL_URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));
		*/
		//refrigerator
		System.out.println("meter plugin 2 ******");
		atomicModelDescriptors.put(
						REFRIGERATOR_ELECTRICITY_MODEL_URI,
						RTAtomicHIOA_Descriptor.create(
								REFRIGERATOR_ELECTRICITY_MODEL_CLASS,
								REFRIGERATOR_ELECTRICITY_MODEL_URI,
								TimeUnit.SECONDS,
								null,
								SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
								accFactor));
								
		
		//meter
		atomicModelDescriptors.put(
				ElectricMeterElectricitySILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						ElectricMeterElectricitySILModel.class,
						ElectricMeterElectricitySILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_RT_ENGINE,
						accFactor));

		Map<Class<? extends EventI>, EventSink[]> imported = null;

		if (!simArchURI.equals(UNIT_TEST_SIM_ARCHITECTURE_URI)) {
			// if not executed as a unit test, the events concerning the
			// heater and the hair dryer electricity consumption models will
			// be imported from the corresponding components simulation models

			imported = new HashMap<>();
			
			//water heater
			imported.put(
					SwitchOnWaterHeater.class,
					new EventSink[] {
							new EventSink(WATER_HEATER_ELECTRICITY_MODEL_URI,
										  SwitchOnWaterHeater.class)
					});
			imported.put(
					SwitchOffWaterHeater.class,
					new EventSink[] {
							new EventSink(WATER_HEATER_ELECTRICITY_MODEL_URI,
										  SwitchOffWaterHeater.class)
					});
			imported.put(
					HeatWater.class,
					new EventSink[] {
							new EventSink(WATER_HEATER_ELECTRICITY_MODEL_URI,
										  HeatWater.class)
					});
			imported.put(
					DoNotHeatWater.class,
					new EventSink[] {
							new EventSink(WATER_HEATER_ELECTRICITY_MODEL_URI,
										  DoNotHeatWater.class)
					});
			
			//refrigerator
			System.out.println("meter plugin 3 ******");
			imported.put(
					CloseRefrigeratorDoor.class,
					new EventSink[] {
							new EventSink(REFRIGERATOR_ELECTRICITY_MODEL_URI,
									CloseRefrigeratorDoor.class)
					});
			imported.put(
					OpenRefrigeratorDoor.class,
					new EventSink[] {
							new EventSink(REFRIGERATOR_ELECTRICITY_MODEL_URI,
									OpenRefrigeratorDoor.class)
					});
			imported.put(
					Freezing.class,
					new EventSink[] {
							new EventSink(REFRIGERATOR_ELECTRICITY_MODEL_URI,
									Freezing.class)
					});
			imported.put(
					OffRefrigerator.class,
					new EventSink[] {
							new EventSink(REFRIGERATOR_ELECTRICITY_MODEL_URI,
									OffRefrigerator.class)
					});
			imported.put(
					OnRefrigerator.class,
					new EventSink[] {
							new EventSink(REFRIGERATOR_ELECTRICITY_MODEL_URI,
									OnRefrigerator.class)
					});
			imported.put(
					Resting.class,
					new EventSink[] {
							new EventSink(REFRIGERATOR_ELECTRICITY_MODEL_URI,
									Resting.class)
					});
			

			//wind turbine
			/*
			imported.put(
					StartWindTurbine.class,
					new EventSink[] {
							new EventSink(WIND_TURBINE_ELECTRICITY_MODEL_URI,
									StartWindTurbine.class)
					});
			imported.put(
					StopWindTurbine.class,
					new EventSink[] {
							new EventSink(WIND_TURBINE_ELECTRICITY_MODEL_URI,
									StopWindTurbine.class)
					});
					*/
			
			//fan
			imported.put(
					SwitchOnFan.class,
					new EventSink[] {
							new EventSink(FAN_ELECTRICITY_MODEL_URI,
										  SwitchOnFan.class)
					});
			imported.put(
					SwitchOffFan.class,
					new EventSink[] {
							new EventSink(FAN_ELECTRICITY_MODEL_URI,
										  SwitchOffFan.class)
					});
			imported.put(
					SetHighFan.class,
					new EventSink[] {
							new EventSink(FAN_ELECTRICITY_MODEL_URI,
										  SetHighFan.class)
					});
			imported.put(
					SetLowFan.class,
					new EventSink[] {
							new EventSink(FAN_ELECTRICITY_MODEL_URI,
										  SetLowFan.class)
					});
			
			//vacuum cleaner
			imported.put(
					SwitchOnVacuumCleaner.class,
					new EventSink[] {
							new EventSink(VACUUMCLEANER_ELECTRICITY_MODEL_URI,
										  SwitchOnVacuumCleaner.class)
					});
			imported.put(
					SwitchOffVacuumCleaner.class,
					new EventSink[] {
							new EventSink(VACUUMCLEANER_ELECTRICITY_MODEL_URI,
										  SwitchOffVacuumCleaner.class)
					});
			imported.put(
					SetHighVacuumCleaner.class,
					new EventSink[] {
							new EventSink(VACUUMCLEANER_ELECTRICITY_MODEL_URI,
										  SetHighVacuumCleaner.class)
					});
			imported.put(
					SetLowVacuumCleaner.class,
					new EventSink[] {
							new EventSink(VACUUMCLEANER_ELECTRICITY_MODEL_URI,
										  SetLowVacuumCleaner.class)
					});
		}

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

		// bindings between hair fan and heater models to the electric
		// meter model
								
		//fan						
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   FAN_ELECTRICITY_MODEL_URI),
				new VariableSink[] {
						new VariableSink("currentFanIntensity",
										 Double.class,
										 ElectricMeterElectricityModel.URI)
				});
		
		//vacuum cleaner						
		bindings.put(
				new VariableSource("currentIntensity",
										   Double.class,
										   VACUUMCLEANER_ELECTRICITY_MODEL_URI),
				new VariableSink[] {
						new VariableSink("currentVacuumCleanerIntensity",
												 Double.class,
												 ElectricMeterElectricityModel.URI)
						});
		
		//water heater
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   WATER_HEATER_ELECTRICITY_MODEL_URI),
				new VariableSink[] {
						new VariableSink("currentWaterHeaterIntensity",
										 Double.class,
										 ElectricMeterElectricityModel.URI)
				});
		
		//refrigerator
		System.out.println("meter plugin 4 ******");
		bindings.put(
				new VariableSource("currentIntensity",
								   Double.class,
								   REFRIGERATOR_ELECTRICITY_MODEL_URI),
				new VariableSink[] {
						new VariableSink("currentRefrigeratorIntensity",
										 Double.class,
										 ElectricMeterElectricityModel.URI)
				});
				
		
		//Wind turbine
		/*
		bindings.put(
					new VariableSource("currentIntensity_production",
										   Double.class,
										   WIND_TURBINE_ELECTRICITY_MODEL_URI),
					new VariableSink[] {
							new VariableSink("currentWindTurbineIntensity_production",
												 Double.class,
												 ElectricMeterElectricityModel.URI)
						});
						*/


		// coupled model descriptor: an HIOA requires a
		// RTCoupledHIOA_Descriptor
		coupledModelDescriptors.put(
				ElectricMeterCoupledModel.URI,
				new RTCoupledHIOA_Descriptor(
						ElectricMeterCoupledModel.class,
						ElectricMeterCoupledModel.URI,
						submodels,
						imported,
						null,
						null,
						null,
						SimulationEngineCreationMode.COORDINATION_RT_ENGINE,
						null,
						null,
						bindings,
						accFactor));

		// this sets the architecture in the plug-in for further reference
		// and use
		this.setSimulationArchitecture(
				new RTArchitecture(
						simArchURI,
						ElectricMeterCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS,
						accFactor));
	}
}
// -----------------------------------------------------------------------------
