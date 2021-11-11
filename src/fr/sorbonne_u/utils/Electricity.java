package fr.sorbonne_u.utils;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.time.Duration;

// -----------------------------------------------------------------------------

public class Electricity
{
    /**
     * convert the duration {@code d} in hours.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true			// no precondition.
     * post	true			// no postcondition.
     * </pre>
     *
     * @param d		the duration to be converted.
     * @return		the duration equal to {@code d} in hours.
     */
    public static double	toHours(Duration d)
    {
        long factor = d.getTimeUnit().convert(1, TimeUnit.HOURS);
        double ret = d.getSimulatedDuration()/factor;
        return ret;
    }

    /**
     * compute the total consumption in kwh for the given intensity {@code i}
     * in watts consumed during the duration {@code d}.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	{@code d != null}
     * pre	{@code i >= 0.0}
     * post	{@code ret >= 0.0}
     * </pre>
     *
     * @param d		duration of the consumption to be computed.
     * @param i		constant intensity in watts during the duration {@code d}.
     * @return		the total consumption in kwh.
     */
    public static double computeConsumption(Duration d, double i)
    {
        double h = toHours(d);
        return h*i/1000.0;
    }
}
// -----------------------------------------------------------------------------
