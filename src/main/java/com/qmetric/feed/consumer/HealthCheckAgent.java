package com.qmetric.feed.consumer;

import com.codahale.metrics.health.HealthCheck;

public interface HealthCheckAgent
{
    HealthCheck healthCheck();
}
