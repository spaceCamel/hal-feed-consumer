package com.qmetric.feed.consumer;

import us.monoid.web.Resty;

import static us.monoid.web.Resty.Option.timeout;

public class FeedEndpointFactory
{
    private static final int CONNECTION_TIMEOUT = 60000;

    private final Resty resty;

    public FeedEndpointFactory()
    {
        resty = new Resty(timeout(CONNECTION_TIMEOUT));
    }

    public FeedEndpoint create(final String url)
    {
        return new FeedEndpoint(url, resty);
    }
}
