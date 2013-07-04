package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

import java.util.List;

public class FeedConsumer
{
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

    public void consume()
    {

        final List<ReadableRepresentation> unconsumed = finder.findUnconsumed(endpoint);

        for (final ReadableRepresentation feedEntry : unconsumed)
        {
            consumerAction.process(feedEntry);

            consumedFeedEntryStore.markAsConsumed(feedEntry);
        }
    }
}
