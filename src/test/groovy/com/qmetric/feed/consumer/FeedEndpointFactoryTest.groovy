package com.qmetric.feed.consumer

import spock.lang.Specification

class FeedEndpointFactoryTest extends Specification {

    def "should create FeedEndpoint using factory"()
    {
        given:
        def feedEndpointFactory = new FeedEndpointFactory()

        when:
        FeedEndpoint feedEndpoint = feedEndpointFactory.create("any_url")

        then:
        feedEndpoint
    }
}
