package com.qmetric.feed.consumer;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.qmetric.feed.consumer.store.ConsumedStore;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.concat;

class UnconsumedFeedEntriesFinder
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

    private final ConsumedStore consumedStore;

    private final Optional<EarliestEntryLimit> earliestEntryLimit;

    private final RepresentationFactory representationFactory;

    private final FeedEndpointFactory feedEndpointFactory;

    UnconsumedFeedEntriesFinder(final FeedEndpointFactory feedEndpointFactory, final ConsumedStore consumedStore, final Optional<EarliestEntryLimit> earliestEntryLimit)
    {
        this.consumedStore = consumedStore;
        this.earliestEntryLimit = earliestEntryLimit;
        this.representationFactory = new DefaultRepresentationFactory();
        this.feedEndpointFactory = feedEndpointFactory;
    }

    List<ReadableRepresentation> findUnconsumed(final FeedEndpoint latestPageEndpoint)
    {
        return from(concat(ImmutableList.copyOf(new UnconsumedPageIterator(latestPageEndpoint)))) //
                .toList() //
                .reverse();
    }

    private class UnconsumedPageIterator implements Iterator<List<? extends ReadableRepresentation>>
    {
        private static final String NEXT_LINK_RELATION = "next";

        private static final String PUBLISHED = "_published";

        private Optional<ReadableRepresentation> currentPage;

        UnconsumedPageIterator(final FeedEndpoint latestPageEndpoint)
        {
            currentPage = Optional.of(representationFactory.readRepresentation(latestPageEndpoint.get()));
        }

        @Override public boolean hasNext()
        {
            return currentPage.isPresent();
        }

        @Override public List<? extends ReadableRepresentation> next()
        {
            final List<? extends ReadableRepresentation> allFromPage = currentPage.get().getResourcesByRel("entries");

            final List<? extends ReadableRepresentation> unconsumedFromPage = unconsumedFrom(allFromPage);

            this.currentPage = allFromPage.size() == unconsumedFromPage.size() ? nextPage() : Optional.<ReadableRepresentation>absent();

            return unconsumedFromPage;
        }

        @Override public void remove()
        {
            throw new UnsupportedOperationException();
        }

        private Optional<ReadableRepresentation> nextPage()
        {
            return nextLink(currentPage.get()).transform(new Function<Link, ReadableRepresentation>()
            {
                @Override public ReadableRepresentation apply(final Link link)
                {
                    return representationFactory.readRepresentation(feedEndpointFactory.create(link.getHref()).get());
                }
            });
        }

        private Optional<Link> nextLink(final ReadableRepresentation readableRepresentation)
        {
            return Optional.fromNullable(readableRepresentation.getLinkByRel(NEXT_LINK_RELATION));
        }

        private boolean hasConsumablePublishedDate(final ReadableRepresentation entry)
        {
            return !earliestEntryLimit.isPresent() || earliestEntryLimit.get().date.isBefore(publishedDate(entry));
        }

        private DateTime publishedDate(final ReadableRepresentation entry)
        {
            return DATE_FORMATTER.parseDateTime((String) entry.getValue(PUBLISHED));
        }

        private List<? extends ReadableRepresentation> unconsumedFrom(final List<? extends ReadableRepresentation> entries)
        {
            return from(entries).filter(new Predicate<ReadableRepresentation>()
            {
                public boolean apply(final ReadableRepresentation input)
                {
                    return hasConsumablePublishedDate(input) && consumedStore.notAlreadyConsumed(input);
                }
            }).toList();
        }
    }
}
