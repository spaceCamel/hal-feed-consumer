package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

public interface ConsumedFeedEntryStore
{
    void markAsConsuming(final ReadableRepresentation feedEntry);

    void revertConsuming(final ReadableRepresentation feedEntry);

    void markAsConsumed(ReadableRepresentation feedEntry);

    boolean notAlreadyConsumed(ReadableRepresentation feedEntry);
}
