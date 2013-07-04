package com.qmetric.feed.consumer;

import us.monoid.web.Resty;

public class FeedEndpointFactory
{
    private final Resty resty;

    public FeedEndpointFactory()
    {
        resty = new Resty();
    }

    public FeedEndpoint create(final String url)
    {
        return new FeedEndpoint(url, resty);
    }
}
