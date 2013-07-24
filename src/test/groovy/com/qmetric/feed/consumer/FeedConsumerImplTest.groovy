package com.qmetric.feed.consumer

import com.qmetric.feed.consumer.store.ConsumedStore
import spock.lang.Specification

class FeedConsumerImplTest extends Specification {

    final url = "http://host/feed"

    final endpoint = Mock(FeedEndpoint)

    final entryConsumer = Mock(EntryConsumer)

    final consumedStore = Mock(ConsumedStore)

    final listener = Mock(FeedPollingListener)

    final feedEndpointFactory = Mock(FeedEndpointFactory)

    def consumer

    def setup()
    {
        feedEndpointFactory.create(url) >> endpoint
        endpoint.get() >> new InputStreamReader(this.getClass().getResource('/feedWithEntry.json').openStream())
        consumer = new FeedConsumerImpl(url, feedEndpointFactory, entryConsumer, consumedStore, [listener])
    }

    def "should consume all unconsumed entries"()
    {
        given:
        consumedStore.notAlreadyConsumed(_) >> true

        when:
        consumer.consume()

        then:
        1 * entryConsumer.consume(_)
    }

    def "should ignore already consumed entries"()
    {
        given:
        consumedStore.notAlreadyConsumed(_) >> false

        when:
        consumer.consume()

        then:
        0 * entryConsumer.consume(_)
    }

    def "should notify listeners after polling feed for new entries"()
    {
        given:
        consumedStore.notAlreadyConsumed(_) >> false

        when:
        consumer.consume()

        then:
        1 * listener.consumed([])
    }
}
