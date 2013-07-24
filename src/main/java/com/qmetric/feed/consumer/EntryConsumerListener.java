package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

public interface EntryConsumerListener
{
    void consumed(final ReadableRepresentation consumedEntry);
}
