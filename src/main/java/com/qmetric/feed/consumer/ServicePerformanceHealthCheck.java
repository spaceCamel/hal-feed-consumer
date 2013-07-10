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

    public static final int MULTIPLIER = 2;

    private final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.fullDateTime();

    private final Duration tolerableDelay;

    private Optional<DateTime> lastConsumed;

    public ServicePerformanceHealthCheck(final long interval, final TimeUnit intervalUnit)
    {
        this.tolerableDelay = new Duration(intervalUnit.toMillis(interval) * MULTIPLIER);
    }

    @Override protected HealthCheck.Result check() throws Exception
    {
        if (!lastConsumed.isPresent())
        {
            return unhealthy(new ConsumerTimeoutException("The consumer has never finished successfully"));
        }
        else
        {
            final Duration durationBetweenLastConsumed = new Duration(lastConsumed.get(), DateTime.now());

            return durationBetweenLastConsumed.isShorterThan(tolerableDelay) ? healthyResult() : unhealthyResult();
        }
    }

    @Override public void consumed(final List<ReadableRepresentation> consumedEntries)
    {
        this.lastConsumed = Optional.of(DateTime.now());
    }

    private Result unhealthyResult()
    {
        return unhealthy(String.format("The feed is not responding since %s ", lastConsumed.get().toString(DATE_TIME_FORMAT)));
    }

    private Result healthyResult()
    {
        return healthy(String.format("The last time consume operation was successfully completed at %s ", lastConsumed.get().toString(DATE_TIME_FORMAT)));
    }

    private class ConsumerTimeoutException extends Exception
    {
        public ConsumerTimeoutException(String message)
        {
            super(message);
        }
    }
}
