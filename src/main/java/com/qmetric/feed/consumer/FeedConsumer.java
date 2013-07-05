package com.qmetric.feed.consumer;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryerBuilder;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.github.rholder.retry.StopStrategies.stopAfterAttempt;
import static com.github.rholder.retry.WaitStrategies.fixedWait;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FeedConsumer
{
    private static final RetryerBuilder<Void> RETRY_BUILDER = RetryerBuilder.<Void>newBuilder() //
            .retryIfException() //
            .withWaitStrategy(fixedWait(1, SECONDS)) //
            .withStopStrategy(stopAfterAttempt(60));

    private final FeedEndpoint endpoint;

    private final ConsumedFeedEntryStore consumedFeedEntryStore;

    private final ConsumerAction consumerAction;

    private final UnconsumedFeedEntriesFinder finder;

    public FeedConsumer(final FeedEndpoint endpoint, final ConsumedFeedEntryStore consumedFeedEntryStore, final ConsumerAction consumerAction)
    {
        this.endpoint = endpoint;
        this.consumedFeedEntryStore = consumedFeedEntryStore;
        this.consumerAction = consumerAction;
        this.finder = new UnconsumedFeedEntriesFinder(new FeedEndpointFactory(), consumedFeedEntryStore);
    }

    public void consume() throws Exception
    {
        for (final ReadableRepresentation feedEntry : finder.findUnconsumed(endpoint))
        {
            markAsConsuming(feedEntry);

            process(feedEntry);

            markAsConsumed(feedEntry);
        }
    }

    private void markAsConsuming(final ReadableRepresentation feedEntry)
    {
        consumedFeedEntryStore.markAsConsuming(feedEntry);
    }

    private void process(final ReadableRepresentation feedEntry) throws Exception
    {
        try
        {
            consumerAction.process(feedEntry);
        }
        catch (final Exception e)
        {
            consumedFeedEntryStore.revertConsuming(feedEntry);

            throw e;
        }
    }

    private void markAsConsumed(final ReadableRepresentation feedEntry) throws ExecutionException, RetryException
    {
        RETRY_BUILDER.build().call(new Callable<Void>()
        {
            @Override public Void call() throws Exception
            {
                consumedFeedEntryStore.markAsConsumed(feedEntry);
                return null;
            }
        });
    }
}
