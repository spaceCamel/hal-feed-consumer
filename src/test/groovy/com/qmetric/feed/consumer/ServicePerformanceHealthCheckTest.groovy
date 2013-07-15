package com.qmetric.feed.consumer

import com.google.common.base.Optional
import org.joda.time.DateTime
import org.joda.time.Duration
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class ServicePerformanceHealthCheckTest extends Specification
{
    final scheduler = Mock(FeedConsumerScheduler)

    final now = DateTime.now();

    final threeMinsBeforeNow = now.minusMinutes(3)

    final thirtySecondsBeforeNow = now.minusSeconds(30)

    def "tolerable delay is set to double the interval passed"()
    {
        when:
        final healthCheck = new ServicePerformanceHealthCheck(1, TimeUnit.MINUTES)

        then:
        assert healthCheck.tolerableDelay == Duration.standardMinutes(2)
    }

    def "Returns unhealthy result if feed was never consumed"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(1, TimeUnit.MINUTES)
        healthCheck.lastConsumed = Optional.absent()

        when:
        final result = healthCheck.check()

        then:
        result.isHealthy() == false
    }

    def "Returns unhealthy result if feed was not consumed for time more than tolerable delay"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(1, TimeUnit.MINUTES)
        healthCheck.lastConsumed = Optional.of(threeMinsBeforeNow)

        when:
        final result = healthCheck.check()

        then:
        result.isHealthy() == false
    }

    def "Returns healthy result if feed consumed before tolerable delay has elapsed"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(1, TimeUnit.MINUTES);
        healthCheck.lastConsumed = Optional.of(thirtySecondsBeforeNow);

        when:
        final result = healthCheck.check()

        then:
        result.isHealthy() == true
    }

    def "Works with other time units"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(60, TimeUnit.SECONDS);
        healthCheck.lastConsumed = Optional.of(thirtySecondsBeforeNow)

        when:
        final result = healthCheck.check()

        then:
        result.isHealthy() == true

    }
}
