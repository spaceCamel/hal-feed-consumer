package com.qmetric.feed.consumer;

import com.sun.jersey.api.client.Client;

import java.util.concurrent.TimeUnit;

public class FeedEndpointFactory
{
    private final Client client;

    public FeedEndpointFactory(final Client client, final ConnectioTimeout timeout)
    {
        this.client = client;
        initClient(timeout);
    }

    private void initClient(final ConnectioTimeout timeout)
    {
        client.setConnectTimeout(timeout.asMillis());
        client.setReadTimeout(timeout.asMillis());
    }

    public FeedEndpoint create(final String url)
    {
        return new FeedEndpoint(client.resource(url));
    }

    public static class ConnectioTimeout
    {
        final TimeUnit unit;

        final int value;

        public ConnectioTimeout(final TimeUnit unit, final int value)
        {
            this.unit = unit;
            this.value = value;
        }

        public int asMillis()
        {
            return (int) unit.toMillis(value);
        }
    }
}
