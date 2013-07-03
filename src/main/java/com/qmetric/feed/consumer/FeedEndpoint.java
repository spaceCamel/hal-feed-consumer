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
        this(feedUrl, new Resty());
    }

    FeedEndpoint(final String feedUrl, final Resty resty)
    {
        this.feedUrl = feedUrl;
        this.resty = resty;
    }

    public Reader reader()
    {

        return reader(feedUrl);
    }

    public Reader reader(final String url)
    {
        try
        {
            return new InputStreamReader(resty.text(url).stream());
        }
        catch (IOException e)
        {
            LOG.error("Failed to connect to feed endpoint", e);

            throw new RuntimeException("Failed to connect to feed", e);
        }
    }
}
