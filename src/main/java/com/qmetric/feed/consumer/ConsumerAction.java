package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

public interface ConsumerAction
{
    void process(ReadableRepresentation feedEntry);
}
