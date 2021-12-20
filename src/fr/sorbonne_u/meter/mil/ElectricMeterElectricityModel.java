package fr.sorbonne_u.meter.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.HEM_ReportI;
import fr.sorbonne_u.utils.Electricity;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------

public class			ElectricMeterElectricityModel
        extends		AtomicHIOA
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    /** URI for a model; works when only one instance is created.			*/
    public static final String		URI = ElectricMeterElectricityModel.class.
            getSimpleName();
    /** tension of electric circuit for appliances in volts.			 	*/
    public static final double		TENSION = 220.0;

    /** current intensity of the Refrigerator in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentRefrigeratorIntensity;
    /** current intensity of the fan in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentFanIntensity;
    /** current intensity of the Vacuum Cleaner in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentVacuumCleanerIntensity;
    /** current intensity of the Water Heater in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentWaterHeaterIntensity;
    /** current intensity of the Washing machine in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentWashingMachineIntensity;
    /** current intensity of the Battery in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentBatteryIntensity_consumption;
    /** current intensity of the Battery in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentBatteryIntensity_production;
    /** current intensity of the wind turbine in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentWindTurbineIntensity_production;
    /** current intensity of the MiniHydroelectricDam in amperes.							*/
    @ImportedVariable(type = Double.class)
    protected Value<Double>			currentMiniHydroelectricDamIntensity_production;
    /** evaluation step for the equation (assumed in seconds).				*/
    protected static final double	STEP = 0.1;
    /** evaluation step as a duration, including the time unit.				*/
    protected final Duration		evaluationStep;

    /** current total consumption intensity of the house in amperes.					*/
    @InternalVariable(type = Double.class)
    protected final Value<Double>	currentIntensity_consumption =
            new Value<Double>(this, 0.0, 0);
    /** current total production intensity of the house in amperes.					*/
    @InternalVariable(type = Double.class)
    protected final Value<Double>	currentIntensity_production =
            new Value<Double>(this, 0.0, 0);
    /** current total consumption of the house in kwh.						*/
    @InternalVariable(type = Double.class)
    protected final Value<Double>	currentConsumption =
            new Value<Double>(this, 0.0, 0);
    /** current total production of the house in kwh.						*/
    @InternalVariable(type = Double.class)
    protected final Value<Double>	currentProduction =
            new Value<Double>(this, 0.0, 0);

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create an <code>ElectricMeterElectricityModel</code> instance.
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
    public				ElectricMeterElectricityModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.evaluationStep = new Duration(STEP, this.getSimulatedTimeUnit());
        this.setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * update the total electricity consumption in kwh given the current
     * intensity has been constant for the duration {@code d}.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code d != null}
     * post	true		// no postcondition.
     * </pre>
     *
     * @param d	duration for which the intensity has been maintained.
     */
    protected void		updateConsumption(Duration d)
    {
        this.currentConsumption.v = this.currentConsumption.v +
                Electricity.computeConsumption(
                        d, TENSION*this.currentIntensity_consumption.v);
        this.currentConsumption.time =
                this.currentConsumption.time.add(d);
    }

    protected void		updateProduction(Duration d)
    {
        this.currentProduction.v = this.currentProduction.v +
                Electricity.computeProduction(
                        d, TENSION*this.currentIntensity_production.v);
        this.currentProduction.time =
                this.currentProduction.time.add(d);
    }

    /**
     * compute the current total intensity.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true		// no precondition.
     * post	true		// no postcondition.
     * </pre>
     *
     */
    protected void		computeTotalIntensity()
    {
        // simple sum of all incoming intensities
       this.currentIntensity_consumption.v =
                this.currentFanIntensity.v +
                        this.currentRefrigeratorIntensity.v +
                             this.currentVacuumCleanerIntensity.v +
                                  this.currentWaterHeaterIntensity.v +
                                       this.currentWashingMachineIntensity.v +
                                            this.currentBatteryIntensity_consumption.v ;

        this.currentIntensity_production.v = this.currentBatteryIntensity_production.v +
                                                  this.currentMiniHydroelectricDamIntensity_production.v +
                                                       this.currentWindTurbineIntensity_production.v;

        // Tracing
        StringBuffer message = new StringBuffer("current total intensity of consumption: ");
        message.append(this.currentIntensity_consumption.v);
        message.append(" at ");
        message.append(this.getCurrentStateTime());
        message.append('\n');
        this.logMessage(message.toString());

        StringBuffer message_ = new StringBuffer("current total intensity of production: ");
        message_.append(this.currentIntensity_production.v);
        message_.append(" at ");
        message_.append(this.getCurrentStateTime());
        message_.append('\n');
        this.logMessage(message_.toString());
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------
    @Override
    protected void		initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");

        this.computeTotalIntensity();
        this.currentConsumption.v = 0.0;
        this.currentProduction.v = 0.0;
    }

    @Override
    public ArrayList<EventI>	output()
    {
        // The model does not export any event.
        return null;
    }

    @Override
    public Duration		timeAdvance()
    {
        // trigger a new internal transition at each evaluation step duration
        return this.evaluationStep;
    }

    @Override
    public void			userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);

        // update the current consumption since the last consumption update.
        // must be done before recomputing the instantaneous intensity.
        this.updateConsumption(elapsedTime);
        this.updateProduction(elapsedTime);
        // recompute the current total intensity
        this.computeTotalIntensity();
    }

    @Override
    public void			endSimulation(Time endTime) throws Exception
    {
        this.updateConsumption(endTime.subtract(this.currentConsumption.time));
        this.updateProduction(endTime.subtract(this.currentProduction.time));

        this.logMessage("simulation ends.\n");
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------
    public static class		ElectricMeterElectricityReport
            implements	SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        protected String	modelURI;
        protected double	totalConsumption; // in kwh
        protected double	totalProduction; // in kwh

        public			ElectricMeterElectricityReport(
                String modelURI,
                double totalConsumption,
                double totalProduction
        )
        {
            super();
            this.modelURI = modelURI;
            this.totalConsumption = totalConsumption;
            this.totalProduction = totalProduction;
        }

        @Override
        public String	getModelURI()
        {
            return this.modelURI;
        }

        @Override
        public String	printout(String indent)
        {
            StringBuffer ret = new StringBuffer(indent);
            ret.append("---\n");
            ret.append(indent);
            ret.append('|');
            ret.append(this.modelURI);
            ret.append(" report\n");
            ret.append(indent);
            ret.append('|');
            ret.append("total consumption in kwh = ");
            ret.append(this.totalConsumption);
            ret.append(".\n");
            ret.append(indent);

            ret.append("\n");

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
        return new ElectricMeterElectricityReport(URI,
                this.currentConsumption.v, this.currentProduction.v);
    }
}
// -----------------------------------------------------------------------------
