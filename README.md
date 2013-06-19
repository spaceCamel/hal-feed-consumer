HAL+JSON feed consumer
======================

Java library used to consume [HAL+JSON](http://stateless.co/hal_specification.html) feeds produced by [hal-feed-server](https://github.com/qmetric/hal-feed-server).

Usage
-----

First, configure a data store used by the consumer to track which feed entries need to be consumed, and which have already been consumed.
An [Amazon SimpleDB](http://aws.amazon.com/simpledb/) based implementation is supplied as part of the library.

```java
final AmazonSimpleDB simpleDBClient = new AmazonSimpleDBClient(new BasicAWSCredentials("access key", "secret key"));
simpleDBClient.setRegion(getRegion(EU_WEST_1));

final ConsumedFeedEntryStore consumedFeedEntryStore = new SimpleDBConsumedEntryStore(simpleDBClient, "your-sdb-domain");
```

Next, configure a feed consumer.

```java
final FeedConsumer consumer = new FeedConsumer(new FeedEndpoint("http://your-feed-endpoint"), consumedFeedEntryStore, new ConsumerAction() {
    @Override public void process(final ReadableRepresentation feedEntry) {
        System.out.println("write your code here to consume the next feed entry...");
}});
```

Finally, configure a scheduler for determining how frequently your feed should be checked for new entries.

```java
new FeedConsumerScheduler(consumer, 1, MINUTES).start();
```


Library available from [Maven central repository](http://search.maven.org/)

```
<dependency>
    <groupId>com.qmetric</groupId>
    <artifactId>hal-feed-consumer</artifactId>
    <version>${VERSION}</version>
</dependency>
```


Implementation limitations
--------------------------

There are currently some limitations with the current feed consumer implementation, described below:

* Does not support the competing consumer pattern (yet). One consumer only per Amazon SimpleDB store.

* On any error, the current feed consumption is aborted. Retry will occur on next scheduled feed check, starting from the feed entry where error occurred.
  If error does not resolve over time, this will result in continuous error/ retry behaviour, with no other feed entries being consumed.
