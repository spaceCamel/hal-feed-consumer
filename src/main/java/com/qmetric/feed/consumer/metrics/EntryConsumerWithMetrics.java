package com.qmetric.feed.consumer.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingWindowReservoir;
import com.codahale.metrics.Timer;
import com.qmetric.feed.consumer.EntryConsumer;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

public class EntryConsumerWithMetrics implements EntryConsumer
{
    private static final int MAX_SAMPLES = 100;

    private final Timer timer;

    private final Meter errorMeter;

    private final Meter successMeter;

    private final EntryConsumer next;

    public EntryConsumerWithMetrics(final MetricRegistry metricRegistry, final EntryConsumer next)
    {
        timer = metricRegistry.register("entryConsumption.timeTaken", new Timer(new SlidingWindowReservoir(MAX_SAMPLES)));
        errorMeter = metricRegistry.meter("entryConsumption.errors");
        successMeter = metricRegistry.meter("entryConsumption.success");

        this.next = next;
    }

    @Override public void consume(final ReadableRepresentation feedEntry) throws Exception
    {
        final Timer.Context context = timer.time();

        try
        {
            next.consume(feedEntry);

            successMeter.mark();
        }
        catch (Exception e)
        {
            errorMeter.mark();

            throw e;
        }
        finally
        {
            context.stop();
        }
    }
}
