package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EventListener;
import java.util.List;
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

    private final ConsumeActionListener[] consumerActionListeners;

    public FeedConsumerScheduler(final FeedConsumer consumer, final long interval, final TimeUnit intervalUnit, final ConsumeActionListener... listeners)
    {
        this(consumer, interval, intervalUnit, newSingleThreadScheduledExecutor(), listeners);
    }

    FeedConsumerScheduler(final FeedConsumer consumer, final long interval, final TimeUnit intervalUnit, final ScheduledExecutorService scheduledExecutorService,
                          final ConsumeActionListener... listeners)
    {
        this.consumer = consumer;
        this.interval = interval;
        this.intervalUnit = intervalUnit;
        this.consumerActionListeners = listeners;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void start()
    {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    LOG.info("attempting to consume feed");
                    final List<ReadableRepresentation> consumedEntries = consumer.consume();
                    notifyAllListeners(consumedEntries);
                    LOG.info("feed consumed successfully");
                }
                catch (Exception e)
                {
                    LOG.error("Failed to consume feed", e);
                }
            }
        }, 0, interval, intervalUnit);
    }

    private void notifyAllListeners(final List<ReadableRepresentation> consumedEntries)
    {
        for (final ConsumeActionListener listener : consumerActionListeners)
        {
            listener.consumed(consumedEntries);
        }
    }
}
