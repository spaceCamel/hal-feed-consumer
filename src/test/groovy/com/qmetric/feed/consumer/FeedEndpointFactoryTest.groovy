package com.qmetric.feed.consumer

import com.sun.jersey.api.client.Client
import spock.lang.Specification

class FeedEndpointFactoryTest extends Specification {

    def "should create FeedEndpoint using factory"()
    {
        given:
        final client = Mock(Client)
        def feedEndpointFactory = new FeedEndpointFactory(client)

        when:
        FeedEndpoint feedEndpoint = feedEndpointFactory.create("any_url")

        then:
        feedEndpoint
    }
}
