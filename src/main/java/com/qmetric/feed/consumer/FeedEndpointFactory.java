package com.qmetric.feed.consumer;

import com.sun.jersey.api.client.Client;

public class FeedEndpointFactory
{
    private final Client client;

    public FeedEndpointFactory(final Client client)
    {
        this.client = client;
    }

    public FeedEndpoint create(final String url)
    {
        return new FeedEndpoint(url, client);
    }
}
