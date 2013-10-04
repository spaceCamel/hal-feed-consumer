package com.qmetric.feed.consumer;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.InputStreamReader;
import java.io.Reader;

import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;

public class FeedEndpoint
{

    private final WebResource resource;

    public FeedEndpoint(final WebResource resource)
    {
        this.resource = resource;
    }

    public Reader get()
    {
        final ClientResponse clientResponse = resource.accept(HAL_JSON).get(ClientResponse.class);

        return new InputStreamReader(clientResponse.getEntityInputStream());
    }
}
