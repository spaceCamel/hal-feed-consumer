package com.qmetric.feed.consumer

import com.theoryinpractise.halbuilder.api.ReadableRepresentation
import spock.lang.Specification

class UnconsumedFeedEntriesFinderTest extends Specification {

    def firstPageEndpoint = Mock(FeedEndpoint)

    def secondPageEndpoint = Mock(FeedEndpoint)

    def thirdPageEndpoint = Mock(FeedEndpoint)

    def consumedFeedEntryStore = Mock(ConsumedFeedEntryStore)

    def feedEndpointFactory = Mock(FeedEndpointFactory)

    def store = new UnconsumedFeedEntriesFinder(feedEndpointFactory, consumedFeedEntryStore)

    def "should return all entries provided feed contains all unconsumed entries"()
    {
        given:
        firstPageEndpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true]

        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint);

        then:
        unprocessedList.size() == 2
        assert unprocessedList.get(0).getValue("_id") == 'idOfOldestUnconsumed'
        assert unprocessedList.get(1).getValue("_id") == 'idOfNewestUnconsumed'
    }

    def "should return only unconsumed entries provided feed contains some unconsumed entries"()
    {
        given:
        firstPageEndpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true, true, false]

        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint);

        then:
        unprocessedList.size() == 3
    }

    def "should paginate to next feed if all entries in the feed are unconsumed"()
    {
        given:
        firstPageEndpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumedAndNextLink.json').openStream())
        feedEndpointFactory.create(_ as String) >> secondPageEndpoint
        secondPageEndpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true, true, false]


        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint);

        then:
        unprocessedList.size() == 3
        assert unprocessedList.get(0).getValue("_id") == 'idOfNewUnconsumed'
        assert unprocessedList.get(1).getValue("_id") == 'idOfNewerUnconsumed'
        assert unprocessedList.get(2).getValue("_id") == 'idOfNewestUnconsumed'
    }

    def "should paginate to multiple pages"()
    {
        given:
        firstPageEndpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumedAndNextLink.json').openStream())
        feedEndpointFactory.create(_ as String) >>> [secondPageEndpoint, thirdPageEndpoint]
        secondPageEndpoint.reader() >> new InputStreamReader(this.getClass().getResource('/anotherFeedWithAllUnconsumedAndNextLink.json').openStream())
        thirdPageEndpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())

        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true, true, true, true, false]

        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint);

        then:
        unprocessedList.size() == 5
    }

    def "should return none when feed has all consumed"()
    {
        given:
        firstPageEndpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllConsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [false]

        when:
        List<ReadableRepresentation> unprocessedList = store.findUnconsumed(firstPageEndpoint);

        then:
        unprocessedList.size() == 0
    }
}
