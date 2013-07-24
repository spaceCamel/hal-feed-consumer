package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

public interface ConsumeAction
{
    void consume(ReadableRepresentation feedEntry);
}
