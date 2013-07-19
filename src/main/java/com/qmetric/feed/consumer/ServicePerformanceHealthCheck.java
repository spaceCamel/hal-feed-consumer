package com.qmetric.feed.consumer;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Optional;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.health.HealthCheck.Result.healthy;
import static com.codahale.metrics.health.HealthCheck.Result.unhealthy;

public class ServicePerformanceHealthCheck extends HealthCheck implements ConsumeActionListener
{
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.fullDateTime();

    private final DateTimeSource dateTimeSource;

    private final Duration tolerableDelay;

    private Optional<DateTime> lastConsumed = Optional.absent();

    public ServicePerformanceHealthCheck(final long interval, final TimeUnit intervalUnit)
    {
        this(interval, intervalUnit, new DateTimeSource());
    }

    ServicePerformanceHealthCheck(final long interval, final TimeUnit intervalUnit, DateTimeSource dateTimeSource)
    {
        this.tolerableDelay = new Duration(intervalUnit.toMillis(interval));
        this.dateTimeSource = dateTimeSource;
    }

    @Override protected synchronized HealthCheck.Result check() throws Exception
    {
        return  !lastConsumed.isPresent() || durationSinceLastConsumedIsLongerThanTolerableDelay() ? unhealthyResult() : healthyResult();
    }

    @Override public synchronized void consumed(final List<ReadableRepresentation> consumedEntries)
    {
        this.lastConsumed = Optional.of(dateTimeSource.now());
    }

    private boolean durationSinceLastConsumedIsLongerThanTolerableDelay()
    {
        return new Duration(lastConsumed.get(), dateTimeSource.now()).isLongerThan(tolerableDelay);
    }

    private Result unhealthyResult()
    {
        if (lastConsumed.isPresent())
        {
            return unhealthy(String.format("The feed has not been consumed since %s ", lastConsumed.get().toString(DATE_TIME_FORMAT)));
        }
        else
        {
            return unhealthy("The feed has never been consumed");
        }
    }

    private Result healthyResult()
    {
        return healthy(String.format("The last time consume operation was successfully completed at %s ", lastConsumed.get().toString(DATE_TIME_FORMAT)));
    }

    static class DateTimeSource
    {
        DateTime now()
        {
            return DateTime.now();
        }
    }
}
