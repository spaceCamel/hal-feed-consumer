package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

public interface ConsumedFeedEntryStore
{
    void markAsConsumed(ReadableRepresentation feedEntry);

    boolean notAlreadyConsumed(ReadableRepresentation feedEntry);
}
