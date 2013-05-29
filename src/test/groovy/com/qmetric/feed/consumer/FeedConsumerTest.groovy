package com.qmetric.feed.consumer

import com.theoryinpractise.halbuilder.api.ReadableRepresentation
import spock.lang.Specification

class FeedConsumerTest extends Specification {

    final endpoint = Mock(FeedEndpoint)

    final consumerAction = Mock(ConsumerAction)

    final consumedFeedEntryStore = Mock(ConsumedFeedEntryStore)

    final consumer = new FeedConsumer(endpoint, consumedFeedEntryStore, consumerAction)

    def "should consume unconsumed in descending order of feed publish date"()
    {
        given:
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true, false]
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())

        when:
        consumer.consume()

        then:
        1 * consumerAction.process(_) >> {
            assert (it[0] as ReadableRepresentation).getValue('id') == 'idOfOldestUnconsumed'
        }

        then:
        1 * consumerAction.process(_) >> {
            assert (it[0] as ReadableRepresentation).getValue('id') == 'idOfNewestUnconsumed'
        }
    }

    def "should mark entry on consumption to prevent duplicate consumes"()
    {
        given:
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true, false]
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())

        when:
        consumer.consume()

        then:
        2 * consumedFeedEntryStore.markAsConsumed(_)
        2 * consumerAction.process(_)
    }

    def "should not check for further unconsumed entries once a consumed entry is found"()
    {
        given:
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [false]
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllConsumed.json').openStream())

        when:
        consumer.consume()

        then:
        1 * consumedFeedEntryStore.notAlreadyConsumed(_)
        0 * consumerAction.process(_)
        0 * consumedFeedEntryStore.markAsConsumed(_)
    }
}
