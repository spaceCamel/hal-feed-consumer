package com.qmetric.feed.consumer
import spock.lang.Specification

class RestyFactoryTest extends Specification {

    def "should create resty with connection timeout"()
    {
        expect:
        RestyFactory.create().options[0].timeout == 60000
    }
}
