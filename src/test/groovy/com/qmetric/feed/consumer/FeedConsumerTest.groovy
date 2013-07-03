package com.qmetric.feed.consumer

import com.theoryinpractise.halbuilder.api.ReadableRepresentation
import spock.lang.Specification

class FeedConsumerTest extends Specification
{

    final unconsumedFeedEntriesFinder = Mock(UnconsumedFeedEntriesFinder)

    final consumerAction = Mock(ConsumerAction)

    final consumedFeedEntryStore = Mock(ConsumedFeedEntryStore)

    def feedEntry1 = Mock(ReadableRepresentation)

    def feedEntry2 = Mock(ReadableRepresentation)

    final consumer = new FeedConsumer(unconsumedFeedEntriesFinder, consumedFeedEntryStore, consumerAction)

    def "should consume all entries returned by finder"()
    {
        given:
        unconsumedFeedEntriesFinder.findUnconsumed() >> Arrays.asList(feedEntry1, feedEntry2)

        when:
        consumer.consume()

        then:
        2 * consumerAction.process(_)

    }

    def "should markAsConsumed all entries returned by finder"()
    {
        given:
        unconsumedFeedEntriesFinder.findUnconsumed() >> Arrays.asList(feedEntry1, feedEntry2)

        when:
        consumer.consume()

        then:
        2 * consumedFeedEntryStore.markAsConsumed(_)
    }
}
