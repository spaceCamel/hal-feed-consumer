package com.qmetric.feed.consumer

import org.joda.time.DateTime
import spock.lang.Specification
import spock.lang.Unroll

import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS

class ServicePerformanceHealthCheckTest extends Specification {

    final testDates = new TestDates([])

    final dateTimeSource = Mock(ServicePerformanceHealthCheck.DateTimeSource)

    def "should be unhealthy if feed was never consumed"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(1, MINUTES)

        when:
        final result = healthCheck.check()

        then:
        !result.isHealthy()
    }

    @Unroll def "should be unhealthy if feed has not been consumed within the tolerable delay"()
    {
        given:
        final healthCheck = new ServicePerformanceHealthCheck(interval, intervalUnit, dateTimeSource)
        dateTimeSource.now() >>> testDates.lastConsumed(lastConsumedDate).currentDate(currentDate).get()
        healthCheck.consumed(_)

        when:
        final result = healthCheck.check()

        then:
        result.isHealthy() == expectedHealthyResult

        where:
        interval | intervalUnit | lastConsumedDate                      | currentDate                             | expectedHealthyResult
        1        | MINUTES      | new DateTime(2013, 7, 19, 0, 0, 0, 0) | new DateTime(2013, 7, 19, 0, 1, 0, 0)   | true
        1        | MINUTES      | new DateTime(2013, 7, 19, 0, 0, 0, 0) | new DateTime(2013, 7, 19, 0, 0, 59, 59) | true
        1        | MINUTES      | new DateTime(2013, 7, 19, 0, 0, 0, 0) | new DateTime(2013, 7, 19, 0, 1, 0, 1)   | false
        1        | SECONDS      | new DateTime(2013, 7, 19, 0, 0, 0, 0) | new DateTime(2013, 7, 19, 0, 0, 1, 0)   | true
        1        | SECONDS      | new DateTime(2013, 7, 19, 0, 0, 0, 0) | new DateTime(2013, 7, 19, 0, 0, 2, 0)   | false
    }

    private class TestDates {
        final dates = []

        private TestDates(dates)
        {
            this.dates = dates
        }

        def lastConsumed(final dateTime)
        {
            dates.add(dateTime)
            return new TestDates(dates)
        }

        def currentDate(final dateTime)
        {
            dates.add(dateTime)
            return new TestDates(dates)
        }

        def get()
        {
            dates
        }
    }
}
