package fr.sorbonne_u.production_unities.miniHydroelectricDam.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;



public class WaterVolumeModel    extends		AtomicHIOA
{
    private static final long serialVersionUID = 1L;

    public static final String URI = WaterVolumeModel.class.
            getSimpleName();

    public static final double		MIN_WATER_VOLUME = 0.0;
    public static final double		MAX_WATER_VOLUME = 500.0;
    public static final double		PERIOD = 10.0;
    protected static final double	STEP = 1.0;
    protected final Duration		evaluationStep;
    @ExportedVariable(type = Double.class)
    protected final Value<Double>	waterVolume =
            new Value<Double>(this, 0.0, 0);
    protected double				cycleTime;

    public WaterVolumeModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.evaluationStep = new Duration(STEP, this.getSimulatedTimeUnit());
        this.setLogger(new StandardLogger());
    }


    @Override
    public void			initialiseState(Time initialTime)
    {
        super.initialiseState(initialTime);
        this.cycleTime = 0.0;
    }

    @Override
    protected void initialiseVariables(Time startTime)
    {
        super.initialiseVariables(startTime);

        this.waterVolume.v = MIN_WATER_VOLUME;

        this.toggleDebugMode();
        this.logMessage("simulation begins.\n");
        StringBuffer message =
                new StringBuffer("current water volume: ");
        message.append(this.waterVolume.v);
        message.append(" at ");
        message.append(this.getCurrentStateTime());
        message.append("\n");
        this.logMessage(message.toString());
    }

    @Override
    public ArrayList<EventI>	output()
    {
        return null;
    }


    @Override
    public Duration		timeAdvance()
    {
        return this.evaluationStep;
    }

    @Override
    public void			userDefinedInternalTransition(Duration elapsedTime)
    {
        super.userDefinedInternalTransition(elapsedTime);
        this.cycleTime += elapsedTime.getSimulatedDuration();
        if (this.cycleTime > PERIOD) {
            this.cycleTime -= PERIOD;
        }
        double c = Math.cos((1.0 + this.cycleTime/(PERIOD/2.0))*Math.PI);
        this.waterVolume.v =
                MIN_WATER_VOLUME +
                        (MAX_WATER_VOLUME - MIN_WATER_VOLUME)*
                                ((1.0 + c)/2.0);
        this.waterVolume.time = this.getCurrentStateTime();

        // Tracing
        StringBuffer message =
                new StringBuffer("current water volume: ");
        message.append(this.waterVolume.v);
        message.append(" at ");
        message.append(this.getCurrentStateTime());
        message.append("\n");
        this.logMessage(message.toString());
    }


    @Override
    public void			endSimulation(Time endTime) throws Exception
    {
        this.logMessage("simulation ends.\n");
        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        return null;
    }
}