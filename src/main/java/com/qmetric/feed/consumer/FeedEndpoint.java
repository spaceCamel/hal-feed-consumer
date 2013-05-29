package com.qmetric.feed.consumer;

import us.monoid.web.Resty;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class FeedEndpoint
{
    private final String feedUrl;

    private final Resty resty;

    public FeedEndpoint(final String feedUrl)
    {
        this(feedUrl, new Resty());
    }

    FeedEndpoint(final String feedUrl, final Resty resty)
    {
        this.feedUrl = feedUrl;
        this.resty = resty;
    }

    public Reader reader()
    {
        try
        {
            return new InputStreamReader(resty.text(feedUrl).stream());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to connect to feed", e);
        }
    }
}
