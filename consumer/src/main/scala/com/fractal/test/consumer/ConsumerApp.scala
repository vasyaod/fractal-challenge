package com.fractal.test.consumer

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.rabbitmq.RabbitMQUtils
import scala.tools.nsc.io.File

object ConsumerApp extends App {

  val checkpointDir = "check-points"

//  def createStreamingContext(): StreamingContext = {
//    val conf = new SparkConf().setAppName("Just test").setMaster("local[2]")
//    val ssc = new StreamingContext(conf, Seconds(10))
//    ssc.checkpoint("check-points")
//
//    ssc
//  }
//  val ssc = StreamingContext.getOrCreate(checkpointDir, createStreamingContext)

  val conf = new SparkConf().setAppName("Just test").setMaster("local[2]")
  val ssc = new StreamingContext(conf, Seconds(10))
  ssc.checkpoint("check-points")

  val receiverStream = RabbitMQUtils.createStream[String](ssc, Map(
    "hosts" -> "localhost",
    "queueName" -> "queue1",
    "userName" -> "guest",
    "password" -> "guest"
  ))

  val initialRDD = ssc.sparkContext.parallelize(List[(String, Int)]())

  receiverStream.start()

  val words = receiverStream.flatMap(_.split(" "))
  val pairs = words.map(word => (word, 1))
  val wordCounts = pairs.reduceByKey(_ + _)

  val mappingFunc = (word: String, one: Option[Int], state: State[Int]) => {
    val sum = one.getOrElse(0) + state.getOption.getOrElse(0)
    val output = (word, sum)
    state.update(sum)
    output
  }

  val statefullWordCounts = wordCounts.mapWithState(
    StateSpec.function(mappingFunc).initialState(initialRDD))

  statefullWordCounts.foreachRDD {rdd =>
    rdd.foreach { pair =>
      // Since we don't have key-value databases like HBase or Cassandra I'm using a local file system
      // where a key is a file name and value (word counter) is the contents of the file.
      File("./words/" + pair._1).createFile().writeAll(pair._2.toString)
    }
  }

  ssc.start()
  ssc.awaitTermination()
}
