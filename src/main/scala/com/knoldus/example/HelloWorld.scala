package com.knoldus.example

import java.time.Duration

import com.google.common.collect.{ImmutableList, ImmutableMap}
import com.typesafe.config.ConfigFactory
import org.apache.samza.application.StreamApplication
import org.apache.samza.application.descriptors.StreamApplicationDescriptor
import org.apache.samza.serializers.{KVSerde, StringSerde}
import org.apache.samza.system.kafka.descriptors.KafkaSystemDescriptor
import org.apache.samza.test.framework.{StreamAssert, TestRunner}
import org.apache.samza.test.framework.system.descriptors.InMemorySystemDescriptor
import org.junit.Test

/**
 * To use high level api, we need to implement StreamApplication trait
 * which comes with an abstract method "describe".
 */
class HelloWorld extends StreamApplication {

  /**
   * This method describes the processing logic using Samza’s High Level
   * Streams API in terms of MessageStream operators.
   *
   * @param appDescriptor holds description of input, output, state and processing logic.
   */
  override def describe(appDescriptor: StreamApplicationDescriptor): Unit = {

    val config = ConfigFactory.load()
    val systemName = config.getString("system.name")
    val zookeeper = ImmutableList.of(config.getString("zk.host"))
    val server = ImmutableList.of(config.getString("server.host"))
    val streamConfig = ImmutableMap.of("replication.factor", config.getString("replication.factor"))
    val inputTopicName = config.getString("input.topic")
    val outputTopicName = config.getString("output.topic")
    val serdeForKV = KVSerde.of(new StringSerde(), new StringSerde())

    val ksd = new KafkaSystemDescriptor(systemName)
      .withConsumerZkConnect(zookeeper)
      .withProducerBootstrapServers(server)
      .withDefaultStreamConfigs(streamConfig)

    val kid = ksd.getInputDescriptor(inputTopicName, serdeForKV)

    val kod = ksd.getOutputDescriptor(outputTopicName, serdeForKV)

    appDescriptor.withDefaultSystem(ksd)

    val inputStream = appDescriptor.getInputStream(kid)

    val outputStream = appDescriptor.getOutputStream(kod)

    inputStream.filter(kv => kv.value.contains("hi")).sendTo(outputStream)

  }

  /**
   * This is to use Test API provided by Samza to test the task.
   */
  @Test
  def testTask(): Unit = {
    val partitionCount = 1
    val duration = 5
    val inSD = new InMemorySystemDescriptor("kafka")
    val inID = inSD.getInputDescriptor("test", new StringSerde())
    val inOD = inSD.getOutputDescriptor("testOut", new StringSerde())

    TestRunner
      .of(new HelloWorld)
      .addInputStream(inID, ImmutableList.of("Hi", "hello", "yb","hi"))
      .addOutputStream(inOD, partitionCount)
      .run(Duration.ofSeconds(duration))

    val assertTime = 2000

    StreamAssert.containsInOrder(ImmutableList.of("hi"), inOD, Duration.ofMillis(assertTime))

  }

}
