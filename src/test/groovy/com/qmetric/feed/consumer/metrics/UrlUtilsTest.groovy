package com.qmetric.feed.consumer.metrics

import spock.lang.Specification

class UrlUtilsTest extends Specification {

    final urlUtils = new FeedConnectivityHealthCheck.UrlUtils()

    def "should extract host:port from url"()
    {
        when:
        final hostAndPort = urlUtils.pingUrlFrom(url)

        then:
        hostAndPort == expectedPingUrl

        where:
        url                         | expectedPingUrl
        "http://localhost:8081/feed" | "http://localhost:8081/ping"
        "http://localhost/feed"      | "http://localhost/ping"
        "http://localhost"          | "http://localhost/ping"
        "http://localhost:8080"     | "http://localhost:8080/ping"
    }
}
