package com.qmetric.feed.consumer

import spock.lang.Specification

class FeedConsumerTest extends Specification {

    def consumerAction = Mock(ConsumerAction)

    def consumedFeedEntryStore = Mock(ConsumedFeedEntryStore)

    def endpoint = Mock(FeedEndpoint)

    final consumer = new FeedConsumer(endpoint, consumedFeedEntryStore, consumerAction)

    def setup()
    {
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithEntry.json').openStream())
    }

    def "should consume all unconsumed entries"()
    {
        given:
        consumedFeedEntryStore.notAlreadyConsumed(_) >> true

        when:
        consumer.consume()

        then:
        1 * consumedFeedEntryStore.markAsConsuming(_)

        then:
        1 * consumerAction.process(_)

        then:
        1 * consumedFeedEntryStore.markAsConsumed(_)
    }

    def "should ignore already consumed entries"()
    {
        given:
        consumedFeedEntryStore.notAlreadyConsumed(_) >> false

        when:
        consumer.consume()

        then:
        0 * consumedFeedEntryStore.markAsConsuming(_)
        0 * consumerAction.process(_)
        0 * consumedFeedEntryStore.markAsConsumed(_)
        0 * consumedFeedEntryStore.revertConsuming(_)
    }

    def "should not consume entry if already being consumed by another consumer"()
    {
        given:
        consumedFeedEntryStore.notAlreadyConsumed(_) >> true
        consumedFeedEntryStore.markAsConsuming(_) >> { throw new Exception() }

        when:
        consumer.consume()

        then:
        0 * consumedFeedEntryStore.markAsConsumed(_) >> { throw new Exception() }
        0 * consumedFeedEntryStore.markAsConsumed(_)
        thrown(Exception)

    }

    def "should revert consuming state if error occurs whilst consuming entry"()
    {
        given:
        consumedFeedEntryStore.notAlreadyConsumed(_) >> true
        consumerAction.process(_) >> { throw new Exception() }

        when:
        consumer.consume()

        then:
        0 * consumedFeedEntryStore.markAsConsumed(_)
        1 * consumedFeedEntryStore.revertConsuming(_)
        thrown(Exception)
    }

    def "should retry to set consumed state on error"()
    {
        given:
        consumedFeedEntryStore.notAlreadyConsumed(_) >> true

        when:
        consumer.consume()

        then:
        1 * consumedFeedEntryStore.markAsConsumed(_) >> { throw new Exception() }

        then:
        1 * consumedFeedEntryStore.markAsConsumed(_)
    }
}
