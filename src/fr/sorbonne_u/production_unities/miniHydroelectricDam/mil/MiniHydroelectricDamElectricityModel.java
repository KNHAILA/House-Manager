package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.utils.Electricity;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.AbstractMiniHydroelectricDamEvent;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.DoNotUseMiniHydroelectricDam;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.UseMiniHydroelectricDam;

/**
 * The class <code>HeaterElectricityModel</code> defines a simulation model
 * for the electricity consumption of the heater.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model is a simple state-based one: the electricity consumption is
 * assumed to be constant in each possible state of the heater
 * ({@code State.OFF => 0.0}, {@code State.ON => NOT_HEATING_POWER} and
 * {@code State.HEATING => HEATING_POWER}). The state of the heater is
 * modified by the reception of external events ({@code SwitchOnHeater},
 * {@code SwitchOffHeater}, {@code Heat} and {@code DoNotHeat}). The
 * electricity consumption is stored in the exported variable
 * {@code currentIntensity}.
 * </p>
 * <p>
 * Initially, the mode is in state {@code State.OFF} and the electricity
 * consumption at 0.0.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code NOT_HEATING_POWER >= 0.0}
 * invariant	{@code HEATING_POWER > NOT_HEATING_POWER}
 * invariant	{@code TENSION > 0.0}
 * </pre>
 * 
 * <p>Created on : 2021-09-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(imported = {DoNotUseMiniHydroelectricDam.class,
        UseMiniHydroelectricDam.class
        })
// -----------------------------------------------------------------------------
public class MiniHydroelectricDamElectricityModel extends AtomicHIOA
{
	/**
	 * The enumeration <code>State</code> defines the state in which the
	 * heater can be.
	 *
	 * <p>Created on : 2021-09-24</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
    public static enum State {
    	/** heater is on but not heating.									*/
        USE,
        /** heater is on but not heating.									*/
        NOT_USE
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long		serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
    public static final String		URI = MiniHydroelectricDamElectricityModel.class.getSimpleName();
    /** power of the heater in watts.										*/
    public static double			MODE_PRODUCTION = 200000.0; // Watts
    /** power of the heater in watts.										*/
    public static double			TENSION = 220.0; // Volts

    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	currentIntensity_production =
            new Value<Double>(this, 0.0, 0);
    
    /** current intensity in amperes; intensity is power/tension.			*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			waterSpeed;
    /** power of the heater in watts.										*/
    protected State currentState = State.NOT_USE;
    /** power of the heater in watts.										*/
    protected boolean				consumptionHasChanged = false;
    /** power of the heater in watts.										*/
    protected double				totalProduction;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

  
    /**
	 * create a heater MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof HIOA_AtomicEngine}
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
    public	MiniHydroelectricDamElectricityModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger());
    }

	/**
	 * set the state of the heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	s != null
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 */
    public void	setState(State s)
    {
        this.currentState = s;
    }

    /**
	 * return the state of the heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true		// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current state.
	 */
    public State getState()
    {
        return this.currentState;
    }

 
    public void	toggleConsumptionHasChanged()
    {
        if (this.consumptionHasChanged) {
            this.consumptionHasChanged = false;
        } else {
            this.consumptionHasChanged = true;
        }
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    /**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
    @Override
    protected void	initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        this.currentIntensity_production.v = 0.0;
    }

    /**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
    @Override
    public void	initialiseState(Time startTime)
    {
        super.initialiseState(startTime);

        this.currentState = State.NOT_USE;
        this.consumptionHasChanged = false;
        this.totalProduction = 0.0;

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
    public Duration	timeAdvance()
    {
     
        if (this.consumptionHasChanged) {
            this.toggleConsumptionHasChanged();
            return new Duration(0.0, this.getSimulatedTimeUnit());
        } else {
            return Duration.INFINITY;
        }
    }


    /**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);

        switch (this.currentState)
        {
            case NOT_USE :
                this.currentIntensity_production.v = 0.0;
                break;
            case USE:
            	this.currentIntensity_production.v = waterSpeed.v*MODE_PRODUCTION/TENSION;
        }
        this.currentIntensity_production.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an internal transition ");

            message.append("with current production ");
            message.append(this.currentIntensity_production.v);
            message.append(" at ");
            message.append(this.currentIntensity_production.time);

        message.append(".\n");
        this.logMessage(message.toString());
    }

    /**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
    @Override
    public void	userDefinedExternalTransition(Duration elapsedTime)
    {
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        
        assert	currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);

        if(ce instanceof UseMiniHydroelectricDam) {
            this.totalProduction +=
                    Electricity.computeProduction(elapsedTime,
                            TENSION*this.currentIntensity_production.v);
        }

        // Tracing
        StringBuffer message =
                new StringBuffer("executes an external transition ");
        message.append(ce.getClass().getSimpleName());
        message.append("(");
        message.append(ce.getTimeOfOccurrence().getSimulatedTime());
        message.append(")\n");
        this.logMessage(message.toString());

        assert	ce instanceof AbstractMiniHydroelectricDamEvent;
        ce.executeOn(this);

        super.userDefinedExternalTransition(elapsedTime);
    }

    /**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
    @Override
    public void	endSimulation(Time endTime) throws Exception
    {
        Duration d = endTime.subtract(this.getCurrentStateTime());
     
        this.totalProduction +=
                Electricity.computeProduction(d,
                        TENSION*this.currentIntensity_production.v);

        
        this.logMessage(this.currentIntensity_production.v+"simulation ends.\n");
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation run parameters
    // -------------------------------------------------------------------------

   
    /** run parameter name for {@code MODE_PRODUCTION}.				*/
    public static final String		MODE_PRODUCTION_RUNPNAME =
            URI + ":MODE_PRODUCTION";
    /** run parameter name for {@code TENSION}.								*/
    public static final String		TENSION_RUNPNAME = URI + ":TENSION";


    /**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
    @Override
    public void	setSimulationRunParameters(
            Map<String, Object> simParams
    ) throws Exception
    {
        super.setSimulationRunParameters(simParams);
      
        if (simParams.containsKey(MODE_PRODUCTION_RUNPNAME)) {
            MODE_PRODUCTION =
                    (double) simParams.get(MODE_PRODUCTION_RUNPNAME);
        }
        if (simParams.containsKey(TENSION_RUNPNAME)) {
            TENSION = (double) simParams.get(TENSION_RUNPNAME);
        }
    }


    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    /**
	 * The class <code>HeaterElectricityReport</code> implements the
	 * simulation report for the <code>HeaterElectricityModel</code>.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 2021-10-01</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
    public static class		MiniHydroelectricDamElectricityReport
            implements	SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        protected String	modelURI;
        protected double	totalProduction; // in kwh

        public				MiniHydroelectricDamElectricityReport(
                String modelURI,
                double totalProduction
        )
        {
            super();
            this.modelURI = modelURI;
            this.totalProduction = totalProduction;
        }

        @Override
        public String		getModelURI()
        {
            return null;
        }

        @Override
        public String		printout(String indent)
        {
            StringBuffer ret = new StringBuffer(indent);
            ret.append("---\n");
            ret.append(indent);
            ret.append('|');
            ret.append(this.modelURI);
            ret.append(" report\n");
            ret.append(indent);
            ret.append('|');
            ret.append("total production in kwh = ");
            ret.append(this.totalProduction);
            ret.append(".\n");
            ret.append(indent);
            ret.append("---\n");
            return ret.toString();
        }
    }

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return new MiniHydroelectricDamElectricityReport(URI, this.totalProduction);
    }
}
// -----------------------------------------------------------------------------




