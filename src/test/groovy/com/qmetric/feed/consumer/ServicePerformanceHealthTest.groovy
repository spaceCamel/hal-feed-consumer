package com.qmetric.feed.consumer

import org.joda.time.DateTime
import spock.lang.Ignore
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@Ignore
class ServicePerformanceHealthTest extends Specification
{
    final scheduler = Mock(FeedConsumerScheduler)

    final now = DateTime.now();

    final fiveMinsBeforeNow = now.minusMinutes(5)

    final thirtySecondsBeforeNow = now.minusSeconds(30)

    def "Returns unhealthy result if feed was never consumed"()
    {
        given:
        final monitor = new ServicePerformanceHealthCheck(null, 1, TimeUnit.MINUTES);

        when:
        final result = monitor.check()

        then:
        result.isHealthy() == false
    }

    def "Returns unhealthy result if feed was not consumed for time more than tolerable delay"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(fiveMinsBeforeNow, 1, TimeUnit.MINUTES);

        when:
        final result = healthCheck.check()

        then:
        result.isHealthy() == false
    }

    def "Returns healthy result if feed consumed before tolerable delay has elapsed"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(thirtySecondsBeforeNow, 1, TimeUnit.MINUTES);

        when:
        final result = healthCheck.check()

        then:
        result.isHealthy() == true
    }

    def "Works with other time units"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(thirtySecondsBeforeNow, 10, TimeUnit.SECONDS);

        when:
        final result = healthCheck.check()

        then:
        result.isHealthy() == false

    }
}
