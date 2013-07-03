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
        final ReadableRepresentation feedFirstPage = representationFactory.readRepresentation(endpoint.reader());

        final List<ReadableRepresentation> unconsumed = newArrayList();

        FeedDetails feedDetails = extractFeedDetailsFrom(feedFirstPage);
        unconsumed.addAll(feedDetails.getUnconsumed());

        while (feedDetails.getNext().isPresent())
        {

            final ReadableRepresentation nextPage = representationFactory.readRepresentation(endpoint.reader(feedDetails.getNext().get()));
            feedDetails = extractFeedDetailsFrom(nextPage);
            unconsumed.addAll(feedDetails.getUnconsumed());
        }

        return unconsumed;
    }

    private FeedDetails extractFeedDetailsFrom(final ReadableRepresentation readableRepresentation)
    {
        final List<ReadableRepresentation> unconsumed = newArrayList();
        for (ReadableRepresentation entry : readableRepresentation.getResourcesByRel("entries"))
        {
            if (consumedFeedEntryStore.notAlreadyConsumed(entry))
            {
                unconsumed.add(0, entry);
            }
            else
            {
                return new FeedDetails(unconsumed, Optional.<String>absent());
            }
        }

        return new FeedDetails(unconsumed, nextLink(readableRepresentation));
    }

    private Optional<String> nextLink(final ReadableRepresentation readableRepresentation)
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

    private class FeedDetails
    {
        private List<ReadableRepresentation> unconsumed;

        private Optional<String> next;

        FeedDetails(final List<ReadableRepresentation> unconsumed, final Optional<String> next)
        {
            this.unconsumed = unconsumed;
            this.next = next;
        }

        public Optional<String> getNext()
        {
            return next;
        }

        public List<ReadableRepresentation> getUnconsumed()
        {
            return unconsumed;
        }
    }
}
