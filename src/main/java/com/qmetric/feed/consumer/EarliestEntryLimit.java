package com.qmetric.feed.consumer;

import org.joda.time.DateTime;

public class EarliestEntryLimit
{
    public final DateTime date;

    public EarliestEntryLimit(final DateTime date)
    {
        this.date = date;
    }
}
