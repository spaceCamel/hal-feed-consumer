package com.qmetric.feed.consumer;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.google.common.collect.ImmutableList;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static org.joda.time.DateTime.now;

public class SimpleDBConsumedEntryStore implements ConsumedFeedEntryStore
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

    private static final String ID_PROPERTY = "id";

    private static final String CONSUMED_DATE_ATTR = "consumed";

    private final AmazonSimpleDB simpleDBClient;

    private final String domain;

    public SimpleDBConsumedEntryStore(final AmazonSimpleDB simpleDBClient, final String domain)
    {
        this.simpleDBClient = simpleDBClient;
        this.domain = domain;

        simpleDBClient.createDomain(new CreateDomainRequest(domain));
    }

    @Override public void markAsConsumed(final ReadableRepresentation feedEntry)
    {
        simpleDBClient.putAttributes(
                new PutAttributesRequest(domain, getId(feedEntry), ImmutableList.of(new ReplaceableAttribute(CONSUMED_DATE_ATTR, DATE_FORMATTER.print(now()), true))));
    }

    @Override public boolean notAlreadyConsumed(final ReadableRepresentation feedEntry)
    {
        return simpleDBClient.select(new SelectRequest(format("select itemName() from `%s` where itemName() = '%s' limit 1", domain, getId(feedEntry)), true)).getItems().isEmpty();
    }

    private String getId(final ReadableRepresentation feedEntry)
    {
        return (String) feedEntry.getValue(ID_PROPERTY);
    }
}
