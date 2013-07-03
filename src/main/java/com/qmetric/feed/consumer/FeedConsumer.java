package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

import java.util.List;

public class FeedConsumer
{

    private final ConsumedFeedEntryStore consumedFeedEntryStore;

    private final ConsumerAction consumerAction;

    private final UnconsumedFeedEntriesFinder finder;

    public FeedConsumer(final UnconsumedFeedEntriesFinder finder, final ConsumedFeedEntryStore consumedFeedEntryStore, final ConsumerAction consumerAction)
    {
        this.finder = finder;
        this.consumedFeedEntryStore = consumedFeedEntryStore;
        this.consumerAction = consumerAction;
    }

    public void consume()
    {

        final List<ReadableRepresentation> unconsumed = finder.findUnconsumed();

        for (final ReadableRepresentation feedEntry : unconsumed)
        {
            consumerAction.process(feedEntry);

            consumedFeedEntryStore.markAsConsumed(feedEntry);
        }
    }
}
