package com.qmetric.feed.consumer

import com.qmetric.feed.consumer.store.ConsumedStore
import com.theoryinpractise.halbuilder.api.ReadableRepresentation
import spock.lang.Specification

class UnconsumedFeedEntriesFinderTest extends Specification {

    def firstPageEndpoint = Mock(FeedEndpoint)

    def secondPageEndpoint = Mock(FeedEndpoint)

    def thirdPageEndpoint = Mock(FeedEndpoint)

    def consumedStore = Mock(ConsumedStore)

    def feedEndpointFactory = Mock(FeedEndpointFactory)

    def store = new UnconsumedFeedEntriesFinder(feedEndpointFactory, consumedStore)

    def "should return all entries provided feed contains all unconsumed entries"()
    {
        given:
        firstPageEndpoint.get() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumed.json').openStream())
        consumedStore.notAlreadyConsumed(_) >>> [true, true]

        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint)

        then:
        unprocessedList.collect { it.getValue("_id") } == ['idOfOldestUnconsumed', 'idOfNewestUnconsumed']
    }

    def "should return only unconsumed entries provided feed contains some unconsumed entries"()
    {
        given:
        firstPageEndpoint.get() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())
        consumedStore.notAlreadyConsumed(_) >>> [true, true, true, false]

        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint)

        then:
        unprocessedList.size() == 3
    }

    def "should paginate to next feed if all entries in the feed are unconsumed"()
    {
        given:
        firstPageEndpoint.get() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumedAndNextLink.json').openStream())
        feedEndpointFactory.create(_ as String) >> secondPageEndpoint
        secondPageEndpoint.get() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())
        consumedStore.notAlreadyConsumed(_) >>> [true, true, true, false]


        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint)

        then:
        unprocessedList.collect { it.getValue("_id") } == ['idOfNewUnconsumed', 'idOfNewerUnconsumed', 'idOfNewestUnconsumed']
    }

    def "should paginate to multiple pages"()
    {
        given:
        firstPageEndpoint.get() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumedAndNextLink.json').openStream())
        feedEndpointFactory.create(_ as String) >>> [secondPageEndpoint, thirdPageEndpoint]
        secondPageEndpoint.get() >> new InputStreamReader(this.getClass().getResource('/anotherFeedWithAllUnconsumedAndNextLink.json').openStream())
        thirdPageEndpoint.get() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())

        consumedStore.notAlreadyConsumed(_) >>> [true, true, true, true, true, false]

        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint)

        then:
        unprocessedList.size() == 5
    }

    def "should return none when feed has all consumed"()
    {
        given:
        firstPageEndpoint.get() >> new InputStreamReader(this.getClass().getResource('/feedWithAllConsumed.json').openStream())
        consumedStore.notAlreadyConsumed(_) >>> [false]

        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint)

        then:
        unprocessedList.isEmpty()
    }
}
