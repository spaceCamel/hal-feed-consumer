package com.qmetric.feed.consumer

import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import spock.lang.Specification

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyNonEmptyString
import static org.apache.commons.io.IOUtils.toString

class FeedEndpointTest extends Specification
{
    private final expectedString = anyNonEmptyString()
    private final inputStream = new ByteArrayInputStream(expectedString.getBytes("UTF-8"))
    private final webResource = Mock(WebResource)
    private final webResourceBuilder = Mock(WebResource.Builder)
    private final response = Mock(ClientResponse)
    private final feedEndpoint = new FeedEndpoint(webResource)

    def "should send request to endpoint and return response as io reader"()
    {
        when:
        final reader = feedEndpoint.get()

        then:
        1 * webResource.accept("application/hal+json") >> webResourceBuilder
        1 * webResourceBuilder.get(ClientResponse.class) >> response
        1 * response.getEntityInputStream() >> inputStream
        expectedString == toString(reader)
    }
}
