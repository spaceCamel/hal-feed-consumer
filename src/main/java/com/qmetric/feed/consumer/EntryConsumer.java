package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

public interface EntryConsumer
{
    void consume(ReadableRepresentation feedEntry) throws Exception;
}
