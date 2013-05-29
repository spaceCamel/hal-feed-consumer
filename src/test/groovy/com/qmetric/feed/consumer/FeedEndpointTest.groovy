package com.qmetric.feed.consumer

import spock.lang.Specification
import us.monoid.web.Resty
import us.monoid.web.TextResource

class FeedEndpointTest extends Specification {

    final url = "http://host/feed"

    final inputStream = Mock(InputStream)

    final textResource = Mock(TextResource)

    final resty = Mock(Resty)

    final feedEndpoint = new FeedEndpoint(url, resty)

    def "should send request to endpoint and return response as io reader"()
    {
        given:
        resty.text(url) >> textResource
        textResource.stream() >> inputStream

        when:
        final reader = feedEndpoint.reader()

        then:
        reader
    }

    def "should rethrow io exception as runtime exception"()
    {
        given:
        resty.text(url) >> {throw new IOException()}

        when:
        feedEndpoint.reader()

        then:
        thrown(RuntimeException)
    }
}
