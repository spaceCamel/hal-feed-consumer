package com.qmetric.feed.consumer

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientHandlerException
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Timeout
import sun.net.httpserver.DefaultHttpServerProvider

import static java.util.concurrent.TimeUnit.SECONDS

class FeedEndpointFactoryTest extends Specification
{

    static timeout = new FeedEndpointFactory.ConnectioTimeout(SECONDS, 1)

    static server = new DefaultHttpServerProvider().createHttpServer(new InetSocketAddress(15001), 0)

    def setupSpec()
    {
        server.createContext("/service-path", new HttpHandler() {
            @Override void handle(final HttpExchange httpExchange) throws IOException
            {
                println "Making the client wait 3 SECONDS"
                Thread.sleep(3000)
                println "Returning"
            }
        })
        server.start()
    }

    def cleanupSpec()
    {
        server.stop(3)
    }

    def "should create FeedEndpoint using factory"()
    {
        given:
        final client = Mock(Client)

        when:
        def feedEndpoint = new FeedEndpointFactory(client, timeout).create("any_url")

        then:
        1 * client.setConnectTimeout(_ as Integer)
        1 * client.setReadTimeout(_ as Integer)
        null != feedEndpoint
    }

    @Timeout(value = 10, unit = SECONDS) def 'throws SocketTimeoutException (read-timeout)'()
    {
        when:
        new FeedEndpointFactory(new Client(), timeout).create("http://localhost:15001/service-path").get()

        then:
        def exception = thrown(ClientHandlerException)
        SocketTimeoutException.isAssignableFrom(exception.getCause().class)
    }

    @Ignore("How to trigger a connection-timeout exception?") @Timeout(value = 10, unit = SECONDS) def 'throws SocketTimeoutException (connection-timeout)'()
    {
        when:
        new FeedEndpointFactory(new Client(), timeout).create("????").get()

        then:
        def exception = thrown(ClientHandlerException)
        SocketTimeoutException.isAssignableFrom(exception.getCause().class)
    }

    @Timeout(value = 10, unit = SECONDS) def 'throws ConnectException'()
    {
        when:
        new FeedEndpointFactory(new Client(), timeout).create("http://localhost:15000").get()

        then:
        def exception = thrown(ClientHandlerException)
        ConnectException.isAssignableFrom(exception.getCause().class)
    }
}
