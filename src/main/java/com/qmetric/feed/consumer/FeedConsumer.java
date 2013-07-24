package com.qmetric.feed.consumer;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

import java.util.List;

public interface FeedConsumer
{
    List<ReadableRepresentation> consume() throws Exception;
}
