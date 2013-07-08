package com.qmetric.feed.consumer;

import com.google.common.base.Optional;
import org.joda.time.DateTime;

public class ConsumeActionStatus
{
    private final Optional<DateTime> lastConsumed;

    public ConsumeActionStatus(final Optional<DateTime> lastConsumed)
    {
        this.lastConsumed = lastConsumed;
    }

    public Optional<DateTime> getLastConsumed()
    {
        return lastConsumed;
    }
}
