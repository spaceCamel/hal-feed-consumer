package com.qmetric.feed.consumer

import spock.lang.Specification

import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON

class RestyFactoryTest extends Specification {

    def "should accept hal+json content"()
    {
        expect:
        RestyFactory.create().getAdditionalHeaders().get("Accept") == HAL_JSON
    }

    def "should create resty with connection timeout"()
    {
        expect:
        RestyFactory.create().options[0].timeout == 60000
    }
}
