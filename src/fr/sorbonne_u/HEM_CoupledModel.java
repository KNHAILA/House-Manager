package fr.sorbonne_u;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import java.util.ArrayList;

public class			HEM_CoupledModel
        extends		CoupledModel
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    /** URI for an instance model; works as long as only one instance is
     *  created.															*/
    public static final String	URI = HEM_CoupledModel.class.getSimpleName();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public				HEM_CoupledModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine,
            ModelDescriptionI[] submodels,
            Map<Class<? extends EventI>, EventSink[]> imported,
            Map<Class<? extends EventI>, ReexportedEvent> reexported,
            Map<EventSource, EventSink[]> connections,
            Map<StaticVariableDescriptor, VariableSink[]> importedVars,
            Map<VariableSource, StaticVariableDescriptor> reexportedVars,
            Map<VariableSource, VariableSink[]> bindings
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine, submodels,
                imported, reexported, connections,
                importedVars, reexportedVars, bindings);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    public static class		HEM_Report
            implements	SimulationReportI, HEM_ReportI
    {
        private static final long serialVersionUID = 1L;
        ArrayList<HEM_ReportI>	subreports;
        protected String		modelURI;

        public				HEM_Report(String modelURI)
        {
            super();
            this.modelURI = modelURI;
            this.subreports = new ArrayList<>();
        }

        @Override
        public String		getModelURI()
        {
            return this.modelURI;
        }

        public void			addSubReport(HEM_ReportI r)
        {
            this.subreports.add(r);
        }

        @Override
        public String		printout(String indent)
        {
            StringBuffer ret = new StringBuffer(indent);
            ret.append("--------------------------\n");
            ret.append(indent);
            ret.append(this.modelURI);
            ret.append(" report\n");
            for (int i = 0; i < this.subreports.size() ; i++) {
                ret.append(this.subreports.get(i).printout(indent + "  "));
            }
            ret.append(indent);
            ret.append("--------------------------\n");
            return ret.toString();
        }
    }

    @Override
    public SimulationReportI	getFinalReport() throws Exception
    {
        System.out.println("HEM_CoupledModel#getFinalReport");
        HEM_Report ret = new HEM_Report(URI);
        for (int i = 0 ; i < this.submodels.length ; i++) {
            HEM_ReportI r = (HEM_ReportI)this.submodels[i].getFinalReport();
            if (r != null) {
                ret.addSubReport(r);
            }
        }
        return ret;
    }
}
// -----------------------------------------------------------------------------

