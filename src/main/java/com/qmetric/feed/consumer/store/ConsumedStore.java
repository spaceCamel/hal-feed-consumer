package com.qmetric.feed.consumer.store;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

public interface ConsumedStore
{
    void checkConnectivity() throws ConnectivityException;

    void markAsConsuming(final ReadableRepresentation feedEntry) throws AlreadyConsumingException;

    void revertConsuming(final ReadableRepresentation feedEntry);

    void markAsConsumed(ReadableRepresentation feedEntry);

    boolean notAlreadyConsumed(ReadableRepresentation feedEntry);
}
