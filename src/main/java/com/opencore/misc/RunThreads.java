package com.opencore.misc;

import io.confluent.kafka.schemaregistry.ClusterTestHarness;
import java.util.Properties;

/**
 * Hello world!
 */
public class RunThreads extends ClusterTestHarness{
  public static void main(String[] args) {
    // Any properties defined here will be passed to the consumers
    Properties configuration = new Properties();
    //configuration.put("heartbeat.interval.ms", 28000);
    //configuration.put("session.timeout.ms", 30000);

    //this will work
    //Coordinator testRun = new Coordinator(1, 3, 3, configuration, 30000);

    //this will also work
    //Coordinator testRun = new Coordinator(1, 3, 5, configuration, 30000);

    // this won't - depending on the settings for heartbeat and session.timeout it will fail earlier or later
    Coordinator testRun = new Coordinator(1, 4, 3, configuration, 30000);

    testRun.run();
  }
}
