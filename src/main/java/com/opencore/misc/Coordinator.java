package com.opencore.misc;

import io.confluent.kafka.schemaregistry.ClusterTestHarness;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sliebau on 12/2/16.
 */


public class Coordinator extends ClusterTestHarness {
  private int numConsumers;
  private int numThreads;
  private long runtime;
  private Logger logger;
  private Properties initialProperties;

  public Coordinator(int numBrokers, int numConsumers, int numThreads, Properties consumerProperties, long runtime) {
    super(numBrokers);
    try {
      super.setUp();
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.numConsumers = numConsumers;
    this.numThreads = numThreads;
    this.runtime = runtime;
    this.logger = LoggerFactory.getLogger(this.getClass());
    this.initialProperties = consumerProperties;
  }

  public void run() {
    // Create topic
    AdminUtils.createTopic(this.zkUtils, "testtopic", 9, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);

    // Send a message just so that something is in the topic
    Properties producerProps = new Properties();
    producerProps.put("bootstrap.servers", brokerList);
    producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    KafkaProducer<String, String> producer = new KafkaProducer<String, String>(producerProps);

    Future produceResult = producer.send(new ProducerRecord<String, String>("testtopic","key", "testvalue"));

    // Wait till the request is done and check that is was successful
    producer.flush();
    try {
      produceResult.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return;
    }
    producer.close();

    // Set consumer properties
    Properties consumerProps = new Properties();
    consumerProps.put("bootstrap.servers", brokerList);
    consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    consumerProps.put("auto.offset.reset", "earliest");
    consumerProps.put("group.id", "test");

    // overwrite defaults with Properties that were passed at creation
    consumerProps.putAll(initialProperties);

    // Create as many consumers as needed
    ConcurrentLinkedQueue<KafkaConsumer<String, String>> consumerPool = new ConcurrentLinkedQueue<>();
    for (int i = 0; i < numConsumers; i++) {
      KafkaConsumer<String, String> tmp = new KafkaConsumer<String, String>(consumerProps);
      tmp.subscribe(Collections.singletonList("testtopic"), new RebalanceListener());
      logger.warn("Adding consumer: " + tmp);
      consumerPool.add(tmp);
    }

    // Start a couple of threads to read from the consumers
    ThreadGroup tg = new ThreadGroup("Consumer Threadgroup");
    ArrayList<ConsumerThread> consumerThreads = new ArrayList<>();
    for (int i = 0; i < numThreads; i++) {
      ConsumerThread newThread = new ConsumerThread(tg, "thread" + i, consumerPool, runtime);
      consumerThreads.add(newThread);
      newThread.start();
    }

    // Wait until all threads are done running
    boolean running = true;
    while (running) {
      running = false;
      for (ConsumerThread thread : consumerThreads) {
        running = running || thread.isRunning();
      }
    }

    // Cleanup
    logger.warn("All threads done, shutting down operations..");
    for (KafkaConsumer consumer : consumerPool){
      consumer.close();
    }
    try {
      super.tearDown();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private class RebalanceListener implements ConsumerRebalanceListener {
    Logger logger;

    public RebalanceListener() {
      this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {
      logger.warn("Partitions revoked: " + collection.toString());
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> collection) {
      logger.warn("Partitions assigned: " + collection.toString());
    }
  }
}
