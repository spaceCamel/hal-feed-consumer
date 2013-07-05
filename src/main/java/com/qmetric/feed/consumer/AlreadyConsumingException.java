package com.qmetric.feed.consumer;

public class AlreadyConsumingException extends Exception
{
    AlreadyConsumingException(final Throwable throwable)
    {
        super(throwable);
    }
}
