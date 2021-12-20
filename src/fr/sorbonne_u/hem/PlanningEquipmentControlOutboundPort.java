package fr.sorbonne_u.hem;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.interfaces.PlanningEquipmentControlCI;
import fr.sorbonne_u.interfaces.SuspensionEquipmentControlCI;

import java.time.Duration;
import java.time.LocalTime;

public class PlanningEquipmentControlOutboundPort
extends StandardEquipmentControlOutboundPort
implements PlanningEquipmentControlCI {

	private static final long serialVersionUID = 1L;
    public PlanningEquipmentControlOutboundPort(
            ComponentI owner
    ) throws Exception
    {
        super(SuspensionEquipmentControlCI.class, owner);
    }

    public PlanningEquipmentControlOutboundPort(
            String uri,
            ComponentI owner
    ) throws Exception
    {
        super(uri, SuspensionEquipmentControlCI.class, owner);
    }

    @Override
    public boolean hasPlan() throws Exception {
        return ((PlanningEquipmentControlCI)this.getConnector()).hasPlan();
    }

    @Override
    public LocalTime startTime() throws Exception {
        return ((PlanningEquipmentControlCI)this.getConnector()).startTime();
    }

    @Override
    public Duration duration() throws Exception {
        return ((PlanningEquipmentControlCI)this.getConnector()).duration();
    }

    @Override
    public LocalTime deadline() throws Exception {
        return ((PlanningEquipmentControlCI)this.getConnector()).deadline();
    }

    @Override
    public boolean postpone(Duration d) throws Exception {
        return ((PlanningEquipmentControlCI)this.getConnector()).postpone(d);
    }

    @Override
    public boolean cancel() throws Exception {
        return ((PlanningEquipmentControlCI)this.getConnector()).cancel();
    }
}