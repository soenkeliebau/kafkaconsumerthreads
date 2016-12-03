# Kafka Rebalance Issue Demonstration

This is a small project to show certain issues I am investigating regarding the implementation of a threadpool for KafkaConsumer objects.

As long as the number of threads reading from these consumers is larger than the number of consumers everything works well. If there are more consumers than threads things go wrong.

## Example runs with 4 consumers and 3 threads

The below example is run with 3 threads using 4 consumers - but different settings for heartbeat.interval.ms. In both cases the first call that is made to the 4th consumer appears to hang, as it never returns (no duration is logged). In the second log, the time until the partition revokation event occurs is much longer, but this is simply triggered by the heartbeat intervall, the issue appear right at the very start of the log, when the first call to the last consumer (72ade7e3) hangs.

#### heartbeat.interval.ms = 3000
```
2016-12-02 19:24:04 WARN  Coordinator:86 - Adding consumer: org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:24:04 WARN  Coordinator:86 - Adding consumer: org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:24:04 WARN  Coordinator:86 - Adding consumer: org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:24:04 WARN  Coordinator:86 - Adding consumer: org.apache.kafka.clients.consumer.KafkaConsumer@72ade7e3
2016-12-02 19:24:04 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:24:04 WARN  ConsumerThread:38 - thread1 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:24:04 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:24:04 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: []
2016-12-02 19:24:04 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: []
2016-12-02 19:24:04 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: []
2016-12-02 19:24:05 WARN  Coordinator$RebalanceListener:134 - Partitions assigned: [testtopic-0, testtopic-2, testtopic-1]
2016-12-02 19:24:05 WARN  Coordinator$RebalanceListener:134 - Partitions assigned: [testtopic-4, testtopic-3, testtopic-5]
2016-12-02 19:24:05 WARN  Coordinator$RebalanceListener:134 - Partitions assigned: [testtopic-6, testtopic-8, testtopic-7]
2016-12-02 19:24:05 WARN  ConsumerThread:41 - thread1 poll took 985ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:24:05 WARN  ConsumerThread:38 - thread1 polling from org.apache.kafka.clients.consumer.KafkaConsumer@72ade7e3
2016-12-02 19:24:05 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:24:05 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:24:05 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:24:05 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:24:05 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: []
2016-12-02 19:24:06 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:24:06 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:24:06 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:24:06 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:24:07 WARN  ConsumerThread:41 - thread0 poll took 1002ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:24:07 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:24:07 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:24:07 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:24:08 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: [testtopic-0, testtopic-2, testtopic-1]
2016-12-02 19:24:08 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: [testtopic-6, testtopic-8, testtopic-7]
```

#### heartbeat.interval.ms = 28000
```
2016-12-02 19:34:33 WARN  Coordinator:86 - Adding consumer: org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:33 WARN  Coordinator:86 - Adding consumer: org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:33 WARN  Coordinator:86 - Adding consumer: org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:33 WARN  Coordinator:86 - Adding consumer: org.apache.kafka.clients.consumer.KafkaConsumer@72ade7e3
2016-12-02 19:34:33 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:33 WARN  ConsumerThread:38 - thread1 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:33 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:34 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: []
2016-12-02 19:34:34 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: []
2016-12-02 19:34:34 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: []
2016-12-02 19:34:34 WARN  Coordinator$RebalanceListener:134 - Partitions assigned: [testtopic-4, testtopic-3, testtopic-5]
2016-12-02 19:34:34 WARN  Coordinator$RebalanceListener:134 - Partitions assigned: [testtopic-6, testtopic-8, testtopic-7]
2016-12-02 19:34:34 WARN  Coordinator$RebalanceListener:134 - Partitions assigned: [testtopic-0, testtopic-2, testtopic-1]
2016-12-02 19:34:34 WARN  ConsumerThread:41 - thread1 poll took 943ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:34 WARN  ConsumerThread:38 - thread1 polling from org.apache.kafka.clients.consumer.KafkaConsumer@72ade7e3
2016-12-02 19:34:34 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:34 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:34 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:34 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:34 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: []
2016-12-02 19:34:35 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:35 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:35 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:35 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:36 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:36 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:36 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:36 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:37 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:37 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:37 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:37 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:38 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:38 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:38 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:38 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:39 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:39 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:39 WARN  ConsumerThread:41 - thread2 poll took 1002ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:39 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:40 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:40 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:40 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:40 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:41 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:41 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:41 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:41 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:42 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:42 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:42 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:42 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:43 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:43 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:43 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:43 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:44 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:44 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:44 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:44 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:45 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:45 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:45 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:45 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:46 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:46 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:46 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:46 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:47 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:47 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:47 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:47 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:48 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:48 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:48 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:48 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:49 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:49 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:49 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:49 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:50 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:50 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:50 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:50 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:51 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:51 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:51 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:51 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:52 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:52 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:52 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:52 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:53 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:53 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:53 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:53 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:54 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:54 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:54 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:54 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:55 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:55 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:55 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:55 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:56 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:56 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:56 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:56 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:57 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:57 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:57 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:57 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:58 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:58 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:58 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:58 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:59 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:34:59 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:34:59 WARN  ConsumerThread:41 - thread0 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:34:59 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:35:00 WARN  ConsumerThread:41 - thread2 poll took 1000ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:35:00 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:35:00 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:35:00 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:35:01 WARN  ConsumerThread:41 - thread2 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:35:01 WARN  ConsumerThread:38 - thread2 polling from org.apache.kafka.clients.consumer.KafkaConsumer@7c3fdb62
2016-12-02 19:35:01 WARN  ConsumerThread:41 - thread0 poll took 1001ms from consumer org.apache.kafka.clients.consumer.KafkaConsumer@19b89d4
2016-12-02 19:35:01 WARN  ConsumerThread:38 - thread0 polling from org.apache.kafka.clients.consumer.KafkaConsumer@2f666ebb
2016-12-02 19:35:02 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: [testtopic-0, testtopic-2, testtopic-1]
2016-12-02 19:35:02 WARN  Coordinator$RebalanceListener:129 - Partitions revoked: [testtopic-6, testtopic-8, testtopic-7]
```