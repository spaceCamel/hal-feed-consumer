package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class FeedConsumer
{
    private final FeedEndpoint endpoint;

    private final ConsumedFeedEntryStore consumedFeedEntryStore;

    private final ConsumerAction consumerAction;

    private final RepresentationFactory representationFactory;

    public FeedConsumer(final FeedEndpoint endpoint, final ConsumedFeedEntryStore consumedFeedEntryStore, final ConsumerAction consumerAction)
    {
        this.endpoint = endpoint;
        this.consumedFeedEntryStore = consumedFeedEntryStore;
        this.consumerAction = consumerAction;
        this.representationFactory = new DefaultRepresentationFactory();
    }

    public List<ReadableRepresentation> consume()
    {
        final List<ReadableRepresentation> entries = takeWhileUnconsumed(representationFactory.readRepresentation(endpoint.reader()).getResourcesByRel("entries"));
        for (final ReadableRepresentation feedEntry : entries)
        {
            consumerAction.process(feedEntry);

            consumedFeedEntryStore.markAsConsumed(feedEntry);
        }
        return entries;
    }

    private List<ReadableRepresentation> takeWhileUnconsumed(final List<? extends ReadableRepresentation> feedEntries)
    {
        final List<ReadableRepresentation> toConsume = new ArrayList<ReadableRepresentation>();

        for (final ReadableRepresentation feedEntry : newArrayList(feedEntries))
        {
            if (consumedFeedEntryStore.notAlreadyConsumed(feedEntry))
            {
                toConsume.add(0, feedEntry);
            }
            else
            {
                break;
            }
        }

        return toConsume;
    }
}
