package com.qmetric.feed.consumer;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

public class UnconsumedFeedEntriesFinder
{

    public static final String NEXT = "next";

    private final FeedEndpoint endpoint;

    private final ConsumedFeedEntryStore consumedFeedEntryStore;

    private final RepresentationFactory representationFactory;

    public UnconsumedFeedEntriesFinder(final FeedEndpoint endpoint, final ConsumedFeedEntryStore consumedFeedEntryStore)
    {

        this.endpoint = endpoint;
        this.consumedFeedEntryStore = consumedFeedEntryStore;
        this.representationFactory = new DefaultRepresentationFactory();
    }

    public List<ReadableRepresentation> findUnconsumed()
    {
        final ReadableRepresentation feedFirstPage = representationFactory.readRepresentation(endpoint.reader());

        final List<ReadableRepresentation> unconsumed = newArrayList();

        FeedDetails feedDetails = extractFeedDetailsFrom(feedFirstPage);

        unconsumed.addAll(feedDetails.unconsumed);

        while (feedDetails.next.isPresent())
        {

            final ReadableRepresentation nextPage = representationFactory.readRepresentation(endpoint.reader(feedDetails.next.get().getHref()));
            feedDetails = extractFeedDetailsFrom(nextPage);
            unconsumed.addAll(feedDetails.unconsumed);
        }

        return unconsumed;
    }

    private FeedDetails extractFeedDetailsFrom(final ReadableRepresentation readableRepresentation)
    {
        final List<? extends ReadableRepresentation> allPageEntries = readableRepresentation.getResourcesByRel("entries");

        final List<? extends ReadableRepresentation> unconsumedPageEntries = from(allPageEntries).filter(new Predicate<ReadableRepresentation>()
        {
            public boolean apply(final ReadableRepresentation input)
            {
                return consumedFeedEntryStore.notAlreadyConsumed(input);
            }
        }).toImmutableList().reverse();

        final Optional<Link> nextPageLink = allPageEntries.size() > unconsumedPageEntries.size() ? Optional.<Link>absent() : next(readableRepresentation);

        return new FeedDetails(unconsumedPageEntries, nextPageLink);
    }

    private Optional<Link> next(final ReadableRepresentation readableRepresentation)
    {
        return nextPageOfUnconsumedFeedsExists(readableRepresentation) ? Optional.of(readableRepresentation.getLinksByRel(NEXT).get(0)) : Optional.<Link>absent();
    }

    private boolean nextPageOfUnconsumedFeedsExists(final ReadableRepresentation readableRepresentation)
    {
        return !readableRepresentation.getLinksByRel(NEXT).isEmpty();
    }

    private class FeedDetails
    {
        List<? extends ReadableRepresentation> unconsumed;

        Optional<Link> next;

        FeedDetails(final List<? extends ReadableRepresentation> unconsumed, final Optional<Link> next)
        {
            this.unconsumed = unconsumed;
            this.next = next;
        }
    }
}
