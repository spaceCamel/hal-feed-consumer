package com.qmetric.feed.consumer;

import us.monoid.web.Resty;

import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;
import static us.monoid.web.Resty.Option.timeout;

public class RestyFactory
{
    private static final int CONNECTION_TIMEOUT = 60000;

    private static final String ACCEPT = "Accept";

    public static Resty create()
    {
        final Resty resty = new Resty(timeout(CONNECTION_TIMEOUT));

        resty.withHeader(ACCEPT, HAL_JSON);

        return resty;
    }
}
