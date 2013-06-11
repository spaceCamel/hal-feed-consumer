package com.qmetric.feed.consumer
import com.amazonaws.services.simpledb.AmazonSimpleDB
import com.amazonaws.services.simpledb.model.Item
import com.amazonaws.services.simpledb.model.PutAttributesRequest
import com.amazonaws.services.simpledb.model.SelectResult
import com.theoryinpractise.halbuilder.api.ReadableRepresentation
import spock.lang.Specification

class SimpleDBConsumedEntryStoreTest extends Specification {

    final feedEntryName = "1"

    final domain = "domain"

    final feedEntry = Mock(ReadableRepresentation)

    final selectResult = Mock(SelectResult)

    final simpleDBClient = Mock(AmazonSimpleDB)

    final consumedEntryStore = new SimpleDBConsumedEntryStore(simpleDBClient, domain)

    def "should store entry as being consumed"()
    {
        given:
        feedEntry.getValue("_id") >> feedEntryName

        when:
        consumedEntryStore.markAsConsumed(feedEntry)

        then:
        simpleDBClient.putAttributes(_) >> {
            final request = (it[0] as PutAttributesRequest)

            assert request.domainName == domain
            assert request.itemName == feedEntryName
            assert request.attributes.get(0).getName() == "consumed"
        }
    }

    def "should return whether entry has already been consumed"()
    {
        given:
        selectResult.getItems() >> [new Item()]
        simpleDBClient.select(_) >> selectResult

        when:
        final notConsumedResult = consumedEntryStore.notAlreadyConsumed(feedEntry)

        then:
        !notConsumedResult
    }

    def "should return whether entry has not yet been consumed"()
    {
        given:
        selectResult.getItems() >> []
        simpleDBClient.select(_) >> selectResult

        when:
        final notConsumedResult = consumedEntryStore.notAlreadyConsumed(feedEntry)

        then:
        notConsumedResult
    }
}