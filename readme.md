# Kafka Rebalance Issue Demonstration

This is a small project to show certain issues I am investigating regarding the implementation of a threadpool for KafkaConsumer objects.

As long as the number of threads reading from these consumers is larger than the number of consumers everything works well. If there are more consumers than threads things go wrong.

Example output:

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

