package com.qmetric.feed.consumer

import spock.lang.Specification

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class FeedConsumerSchedulerTest extends Specification
{

    final timeUnitOfInterval = TimeUnit.MINUTES

    final interval = 1

    final schedulerExecutionService = Mock(ScheduledExecutorService)

    final consumer = Mock(FeedConsumer)

    final consumeActionListener = Mock(ConsumeActionListener)

    final scheduler = new FeedConsumerScheduler(consumer, interval, timeUnitOfInterval, schedulerExecutionService)


    def "should periodically consume feed"()
    {
        when:
        scheduler.start()

        then:
        1 * schedulerExecutionService.scheduleAtFixedRate(_, 0, interval, timeUnitOfInterval)
    }

    def "should notify consumer upon consume"()
    {
        given:
        final scheduler = new FeedConsumerScheduler(consumer, interval, timeUnitOfInterval, schedulerExecutionService, consumeActionListener)

        when:
        scheduler.consumeAndNotifyListeners()

        then:
        1 * consumeActionListener.consumed(_)

    }
}
