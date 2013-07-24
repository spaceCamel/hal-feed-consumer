package com.qmetric.feed.consumer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.io.InputStreamReader;
import java.io.Reader;

import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;

public class FeedEndpoint
{
    private final String feedUrl;

    private final Client client;

    public FeedEndpoint(final String feedUrl, final Client client)
    {
        this.feedUrl = feedUrl;

        this.client = client;
    }

    public Reader get()
    {
        final ClientResponse clientResponse = client.resource(feedUrl).accept(HAL_JSON).get(ClientResponse.class);

        return new InputStreamReader(clientResponse.getEntityInputStream());
    }
}
