package com.opencore.misc;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sliebau on 12/2/16.
 */
public class ConsumerThread extends Thread {
  private ConcurrentLinkedQueue<KafkaConsumer<String, String>> consumers;
  private String threadId;
  private boolean running = true;
  private long runtime;
  Logger logger;

  public ConsumerThread(ThreadGroup tg, String threadID, ConcurrentLinkedQueue<KafkaConsumer<String, String>> consumers, long runtime) {
    super(tg, threadID);
    this.threadId = threadID;
    this.consumers = consumers;
    this.runtime = runtime;
    this.logger = LoggerFactory.getLogger(this.getClass());
  }

  @Override
  public void run() {
    long startTime = System.currentTimeMillis();
    KafkaConsumer<String, String> consumer;

    // run for defined amount of time in milliseconds
    while (System.currentTimeMillis() - startTime < runtime) {

      // get an available consumer from the pool
      consumer = consumers.poll();
      if (consumer != null) {
        // if consumer is null, no consumer was available at this time, skip the rest of the loop and try again
        logger.warn(threadId + " polling from " + consumer + " with subscription: " + consumer.assignment());
        long pollStartTime = System.currentTimeMillis();
        consumer.poll(100);
        logger.warn(threadId + " poll took " + (System.currentTimeMillis() - pollStartTime) + "ms from consumer " + consumer);

        // return the consumer to the pool
        consumers.add(consumer);
      }
    }

    // Time has been reached, shut down
    logger.warn(threadId + " is done!");
    running = false;
  }

  public boolean isRunning() {
    return running;
  }
}
