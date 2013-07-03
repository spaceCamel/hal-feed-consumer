package com.qmetric.feed.consumer

import com.theoryinpractise.halbuilder.api.ReadableRepresentation
import spock.lang.Specification

class UnconsumedFeedEntriesFinderTest extends Specification
{

    def endpoint = Mock(FeedEndpoint)

    def consumedFeedEntryStore = Mock(ConsumedFeedEntryStore)

    def store = new UnconsumedFeedEntriesFinder(endpoint, consumedFeedEntryStore)

    def "should return all entries provided feed contains all unconsumed entries"()
    {
        given:
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true]

        when:
        List<ReadableRepresentation> unprocessedList = store.find();

        then:
        unprocessedList.size() == 2
        assert unprocessedList.get(0).getValue("_id") == 'idOfOldestUnconsumed'
        assert unprocessedList.get(1).getValue("_id") == 'idOfNewestUnconsumed'
    }

    def "should return only unconsumed entries provided feed contains some unconsumed entries"()
    {
        given:
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true, true, false]

        when:
        List<ReadableRepresentation> unprocessedList = store.find();

        then:
        unprocessedList.size() == 3
    }

    def "should paginate to next feed if all entries in the feed are unconsumed"()
    {
        given:
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllUnconsumedAndNextLink.json').openStream())
        endpoint.reader(_) >> new InputStreamReader(this.getClass().getResource('/feedWithSomeUnconsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [true, true, true, false]

        when:
        List<ReadableRepresentation> unprocessedList = store.find();

        then:
        unprocessedList.size() == 3
    }

    def "should return none when feed has all consumed"()
    {
        given:
        endpoint.reader() >> new InputStreamReader(this.getClass().getResource('/feedWithAllConsumed.json').openStream())
        consumedFeedEntryStore.notAlreadyConsumed(_) >>> [false]

        when:
        List<ReadableRepresentation> unprocessedList = store.find();

        then:
        unprocessedList.size() == 0
    }
}
