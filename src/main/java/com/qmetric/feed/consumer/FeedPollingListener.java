package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

import java.util.List;

public interface FeedPollingListener
{
    void consumed(final List<ReadableRepresentation> consumedEntries);
}
