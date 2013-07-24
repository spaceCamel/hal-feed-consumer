package com.qmetric.feed.consumer.metrics;

import com.codahale.metrics.health.HealthCheck;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.net.URI;
import java.net.URISyntaxException;

import static com.codahale.metrics.health.HealthCheck.Result.healthy;
import static com.codahale.metrics.health.HealthCheck.Result.unhealthy;
import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;
import static java.net.HttpURLConnection.HTTP_OK;

public class FeedConnectivityHealthCheck extends HealthCheck
{
    private final String feedPingUrl;

    private final Client client;

    public FeedConnectivityHealthCheck(final String feedUrl, final Client client)
    {
        this.feedPingUrl = pingUrlFrom(feedUrl);
        this.client = client;
    }

    @Override protected Result check() throws Exception
    {
        final ClientResponse clientResponse = client.resource(feedPingUrl).accept(HAL_JSON).get(ClientResponse.class);

        if (clientResponse.getStatus() == HTTP_OK)
        {
            return healthy("Ping was successful to %s", feedPingUrl);
        }
        else
        {
            return unhealthy("Unhealthy with status %s", clientResponse.getStatus());
        }
    }

    private String pingUrlFrom(final String feedUrl)
    {
        try
        {
            final URI uri = new URI(feedUrl);

            return String.format("%s://%s:%s/ping", uri.getScheme(), uri.getHost(), uri.getPort());
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }
}
