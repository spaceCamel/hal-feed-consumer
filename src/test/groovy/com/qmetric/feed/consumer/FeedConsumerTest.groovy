package com.qmetric.feed.consumer

import com.theoryinpractise.halbuilder.api.ReadableRepresentation
import spock.lang.Ignore
import spock.lang.Specification

@Ignore("Need to refactor FeedConsumer to add tests for this class")
class FeedConsumerTest extends Specification
{

    def unconsumedFeedEntriesFinder = Mock(UnconsumedFeedEntriesFinder)

    def consumerAction = Mock(ConsumerAction)

    def consumedFeedEntryStore = Mock(ConsumedFeedEntryStore)

    def endpoint = Mock(FeedEndpoint)

    def feedEntry1 = Mock(ReadableRepresentation)

    def feedEntry2 = Mock(ReadableRepresentation)

    final consumer = new FeedConsumer(endpoint, consumedFeedEntryStore, consumerAction)

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
