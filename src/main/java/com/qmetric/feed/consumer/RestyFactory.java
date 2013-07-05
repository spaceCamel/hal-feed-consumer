package com.qmetric.feed.consumer;

import us.monoid.web.Resty;

import static us.monoid.web.Resty.Option.timeout;

public class RestyFactory
{
    private static final int CONNECTION_TIMEOUT = 60000;

    public static Resty create()
    {
        return new Resty(timeout(CONNECTION_TIMEOUT));
    }
}
