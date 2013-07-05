package com.qmetric.feed.consumer;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static com.google.common.collect.FluentIterable.from;
import static java.lang.String.format;
import static org.joda.time.DateTime.now;

public class SimpleDBConsumedEntryStore implements ConsumedFeedEntryStore
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

    private static final String CONDITIONAL_CHECK_FAILED_ERROR_CODE = "ConditionalCheckFailed";

    private static final String ID_PROPERTY = "_id";

    private static final String CONSUMED_DATE_ATTR = "consumed";

    private static final String CONSUMING_DATE_ATTR = "consuming";

    private static final String SELECT_CONSUMED_ITEM = "select itemName() from `%s` where itemName() = '%s' and `%s` is not null limit 1";

    private final AmazonSimpleDB simpleDBClient;

    private final String domain;

    public SimpleDBConsumedEntryStore(final AmazonSimpleDB simpleDBClient, final String domain)
    {
        this.simpleDBClient = simpleDBClient;
        this.domain = domain;

        simpleDBClient.createDomain(new CreateDomainRequest(domain));
    }

    @Override public void markAsConsuming(final ReadableRepresentation feedEntry) throws AlreadyConsumingException
    {
        final UpdateCondition onlyIfNotAlreadyConsuming = new UpdateCondition().withName(CONSUMING_DATE_ATTR).withExists(false);

        try
        {
            simpleDBClient.putAttributes(new PutAttributesRequest(domain, getId(feedEntry), ImmutableList
                    .of(new ReplaceableAttribute().withName(CONSUMING_DATE_ATTR).withValue(DATE_FORMATTER.print(now())).withReplace(true)), onlyIfNotAlreadyConsuming));
        }
        catch (final AmazonServiceException e)
        {
            if (CONDITIONAL_CHECK_FAILED_ERROR_CODE.equalsIgnoreCase(e.getErrorCode()))
            {
                throw new AlreadyConsumingException(e);
            }
            else
            {
                throw e;
            }
        }
    }

    @Override public void revertConsuming(final ReadableRepresentation feedEntry)
    {
        simpleDBClient.deleteAttributes(new DeleteAttributesRequest(domain, getId(feedEntry), ImmutableList.of(new Attribute().withName(CONSUMING_DATE_ATTR))));
    }

    @Override public void markAsConsumed(final ReadableRepresentation feedEntry)
    {
        simpleDBClient.putAttributes(new PutAttributesRequest(domain, getId(feedEntry), ImmutableList
                .of(new ReplaceableAttribute().withName(CONSUMED_DATE_ATTR).withValue(DATE_FORMATTER.print(now())).withReplace(true))));
    }

    @Override public boolean notAlreadyConsumed(final ReadableRepresentation feedEntry)
    {
        final SelectRequest request = new SelectRequest().withSelectExpression(format(SELECT_CONSUMED_ITEM, domain, getId(feedEntry), CONSUMED_DATE_ATTR)).withConsistentRead(true);

        final Optional<Item> consumedEntry = from(simpleDBClient.select(request).getItems()).first();

        return !consumedEntry.isPresent();
    }

    private String getId(final ReadableRepresentation feedEntry)
    {
        return (String) feedEntry.getValue(ID_PROPERTY);
    }
}
