package com.qmetric.feed.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.monoid.web.Resty;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class FeedEndpoint
{
    private static final Logger LOG = LoggerFactory.getLogger(FeedEndpoint.class);

    private final String feedUrl;

    private final Resty resty;

    public FeedEndpoint(final String feedUrl)
    {
        this(feedUrl, RestyFactory.create());
    }

    public FeedEndpoint(final String feedUrl, final Resty resty)
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
        catch (final IOException e)
        {
            LOG.error("Failed to connect to feed endpoint", e);

            throw new RuntimeException("Failed to connect to feed", e);
        }
    }
}
