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
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.DoNotMiniHydroelectricDam;
import fr.sorbonne_u.production_unities.miniHydroelectricDam.mil.events.UseMiniHydroelectricDam;

// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {DoNotMiniHydroelectricDam.class,
        UseMiniHydroelectricDam.class
        })
// -----------------------------------------------------------------------------
public class MiniHydroelectricDamElectricityModel extends AtomicHIOA
{
    // -------------------------------------------------------------------------
    // Inner classes and types
    // -------------------------------------------------------------------------

    public static enum State {
        USE,
        NOT_USE
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long		serialVersionUID = 1L;

    public static final String		URI = MiniHydroelectricDamElectricityModel.class.getSimpleName();

    
    public static double			MODE_PRODUCTION = 200000.0; // Watts
    public static double			TENSION = 220.0; // Volts

    
    /** current intensity in amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	currentIntensity_production =
            new Value<Double>(this, 0.0, 0);
    
    /** current intensity in amperes; intensity is power/tension.			*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			waterSpeed;
    protected State currentState = State.NOT_USE;
    protected boolean				consumptionHasChanged = false;
    protected double				totalProduction;

    protected double				capacity = 300.0; //    ampere/h

    protected double				charge_time = 1; //    h

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

  
    public	MiniHydroelectricDamElectricityModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public void	setState(State s)
    {
        this.currentState = s;
    }

    
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

    @Override
    protected void	initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        this.currentIntensity_production.v = 0.0;
    }

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

    @Override
    public ArrayList<EventI>	output()
    {
        return null;
    }

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

    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return new MiniHydroelectricDamElectricityReport(URI, this.totalProduction);
    }
}
// -----------------------------------------------------------------------------




