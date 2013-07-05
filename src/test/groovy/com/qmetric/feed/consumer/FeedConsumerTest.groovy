package com.qmetric.feed.consumer

import spock.lang.Specification

class FeedConsumerTest extends Specification {

    def consumerAction = Mock(ConsumerAction)

    def consumedFeedEntryStore = Mock(ConsumedFeedEntryStore)

    def endpoint = Mock(FeedEndpoint)

    final consumer = new FeedConsumer(endpoint, consumedFeedEntryStore, consumerAction)

    def "should consume all entries returned by finder"()
    {
        given:
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true]

        when:
        consumer.consume()

        then:
        2 * consumerAction.process(_)
    }

    def "should markAsConsumed all entries returned by finder"()
    {
        given:
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true]

        when:
        consumer.consume()

        then:
        2 * consumedFeedEntryStore.markAsConsumed(_)
    }
}
