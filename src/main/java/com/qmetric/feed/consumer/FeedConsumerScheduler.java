package com.qmetric.feed.consumer;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EventListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class FeedConsumerScheduler implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger(FeedConsumerScheduler.class);

    private final FeedConsumer consumer;

    private final long interval;

    private final TimeUnit intervalUnit;

    private final ScheduledExecutorService scheduledExecutorService;

    private ConsumeActionStatus status = new ConsumeActionStatus(Optional.<DateTime>absent());

    public FeedConsumerScheduler(final FeedConsumer consumer, final long interval, final TimeUnit intervalUnit)
    {
        this(consumer, interval, intervalUnit, newSingleThreadScheduledExecutor());
    }

    FeedConsumerScheduler(final FeedConsumer consumer, final long interval, final TimeUnit intervalUnit, final ScheduledExecutorService scheduledExecutorService)
    {
        this.consumer = consumer;
        this.interval = interval;
        this.intervalUnit = intervalUnit;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void start()
    {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                consume();
            }
        }, 0, interval, intervalUnit);
    }

    void consume()
    {
        try
        {
            LOG.info("attempting to consume feed");
            consumer.consume();
            status = new ConsumeActionStatus(Optional.of(DateTime.now()));
            LOG.info("feed consumed successfully");
        }
        catch (Exception e)
        {
            LOG.error("Failed to consume feed", e);
        }
    }

    public ConsumeActionStatus getStatus()
    {
        return status;
    }
}
