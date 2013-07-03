package com.qmetric.feed.consumer;

import com.google.common.base.Optional;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class UnconsumedFeedEntriesFinder
{
    private final FeedEndpoint endpoint;

    private final ConsumedFeedEntryStore consumedFeedEntryStore;

    private final RepresentationFactory representationFactory;

    public UnconsumedFeedEntriesFinder(final FeedEndpoint endpoint, final ConsumedFeedEntryStore consumedFeedEntryStore)
    {

        this.endpoint = endpoint;
        this.consumedFeedEntryStore = consumedFeedEntryStore;
        this.representationFactory = new DefaultRepresentationFactory();
    }

    public List<ReadableRepresentation> find()
    {

        final ReadableRepresentation readableRepresentation = representationFactory.readRepresentation(endpoint.reader());

        final List<ReadableRepresentation> unconsumed = newArrayList();

        Optional<String> nextLink = collectUnconsumedEntriesAndReturnNextLink(readableRepresentation, unconsumed);

        while (nextLink.isPresent())
        {

            final ReadableRepresentation nextPage = representationFactory.readRepresentation(endpoint.reader(nextLink.get()));
            nextLink = collectUnconsumedEntriesAndReturnNextLink(nextPage, unconsumed);
        }

        return unconsumed;
    }

    private Optional<String> collectUnconsumedEntriesAndReturnNextLink(final ReadableRepresentation readableRepresentation, final List<ReadableRepresentation> unconsumed)
    {
        for (ReadableRepresentation entry : readableRepresentation.getResourcesByRel("entries"))
        {
            if (consumedFeedEntryStore.notAlreadyConsumed(entry))
            {
                unconsumed.add(0, entry);
            }
            else
            {
                return Optional.absent();
            }
        }

        return returnNextLinkIfPresent(readableRepresentation);
    }

    private Optional<String> returnNextLinkIfPresent(final ReadableRepresentation readableRepresentation)
    {
        if (nextPageOfUnconsumedFeedsExists(readableRepresentation))
        {

            return Optional.of(readableRepresentation.getLinksByRel("next").get(0).getHref());
        }
        else
        {
            return Optional.absent();
        }
    }

    private boolean nextPageOfUnconsumedFeedsExists(final ReadableRepresentation readableRepresentation)
    {
        return !readableRepresentation.getLinksByRel("next").isEmpty();
    }
}
