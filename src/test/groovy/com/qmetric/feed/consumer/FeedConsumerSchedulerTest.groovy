package com.qmetric.feed.consumer

import com.google.common.base.Optional
import spock.lang.Specification

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class FeedConsumerSchedulerTest extends Specification {

    final timeUnitOfInterval = TimeUnit.MINUTES

    final interval = 1

    final schedulerExecutionService = Mock(ScheduledExecutorService)

    final consumer = Mock(FeedConsumer)

    final scheduler = new FeedConsumerScheduler(consumer, interval, timeUnitOfInterval, schedulerExecutionService)


    def "should periodically consume feed"()
    {
        when:
        scheduler.start()

        then:
        1 * schedulerExecutionService.scheduleAtFixedRate(_, 0, interval, timeUnitOfInterval)
    }

    def "should update lastConsumed when consume operation was successful"()
    {
        when:
        scheduler.consume()

        then:
        1 * consumer.consume()
        assert scheduler.getStatus().getLastConsumed() != Optional.absent()
    }

    def "should return lastConsumed as null if consume action failed"()
    {
        given:
        consumer.consume() >> { throw new Exception("Mock exception")}

        when:
        scheduler.consume()

        then:
        assert scheduler.getStatus().getLastConsumed() == Optional.absent()
    }
}
