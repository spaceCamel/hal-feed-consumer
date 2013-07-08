package com.qmetric.feed.consumer;

public class AlreadyConsumingException extends Exception
{
    AlreadyConsumingException()
    {
    }

    AlreadyConsumingException(final Throwable throwable)
    {
        super(throwable);
    }
}
