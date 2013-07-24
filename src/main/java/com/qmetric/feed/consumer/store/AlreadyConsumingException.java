package com.qmetric.feed.consumer.store;

public class AlreadyConsumingException extends Exception
{
    public AlreadyConsumingException()
    {
    }

    public AlreadyConsumingException(final Throwable throwable)
    {
        super(throwable);
    }
}
