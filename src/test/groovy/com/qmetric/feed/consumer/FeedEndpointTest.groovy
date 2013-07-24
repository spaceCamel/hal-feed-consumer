package com.qmetric.feed.consumer

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import spock.lang.Specification

class FeedEndpointTest extends Specification {

    final url = "http://host/feed"

    final inputStream = Mock(InputStream)

    final client = Mock(Client)

    final webResource = Mock(WebResource)

    final webResourceBuilder = Mock(WebResource.Builder)

    final response = Mock(ClientResponse)

    final feedEndpoint = new FeedEndpoint(url, client)

    def "should send request to endpoint and return response as io reader"()
    {
        given:
        client.resource(url) >> webResource
        webResource.accept("application/hal+json") >> webResourceBuilder
        webResourceBuilder.get(ClientResponse.class) >> response
        response.getEntityInputStream() >> inputStream

        when:
        final reader = feedEndpoint.get()

        then:
        reader
    }
}
