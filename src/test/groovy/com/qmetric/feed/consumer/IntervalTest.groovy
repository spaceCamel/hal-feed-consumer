package com.qmetric.feed.consumer

import spock.lang.Specification

import java.util.concurrent.TimeUnit

class IntervalTest extends Specification {

    def "should construct interval with valid parameters"()
    {
        when:
        final interval = new Interval(1, TimeUnit.MINUTES)

        then:
        interval
    }

    def "should throw exception if interval parameters is invalid"()
    {
        when:
        new Interval(0, TimeUnit.MINUTES)

        then:
        thrown(RuntimeException)
    }
}
