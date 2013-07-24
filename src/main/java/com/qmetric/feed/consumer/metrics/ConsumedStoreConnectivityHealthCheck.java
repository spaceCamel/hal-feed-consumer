package com.qmetric.feed.consumer.metrics;

import com.codahale.metrics.health.HealthCheck;
import com.qmetric.feed.consumer.store.ConsumedStore;

import static com.codahale.metrics.health.HealthCheck.Result.healthy;
import static com.codahale.metrics.health.HealthCheck.Result.unhealthy;

public class ConsumedStoreConnectivityHealthCheck extends HealthCheck
{
    private final ConsumedStore consumedStore;

    public ConsumedStoreConnectivityHealthCheck(final ConsumedStore consumedStore)
    {
        this.consumedStore = consumedStore;
    }

    @Override protected Result check() throws Exception
    {
        try
        {
            consumedStore.checkConnectivity();

            return healthy("Consumed store connectivity is healthy");
        }
        catch (final Exception exception)
        {
            return unhealthy(exception);
        }
    }
}

