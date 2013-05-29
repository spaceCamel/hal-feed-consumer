package com.qmetric.feed.consumer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class FeedConsumerScheduler
{
    private final FeedConsumer consumer;

    private final long interval;

    private final TimeUnit intervalUnit;

    private final ScheduledExecutorService scheduledExecutorService;

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
                try
                {
                    consumer.consume();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 0, interval, intervalUnit);
    }
}
