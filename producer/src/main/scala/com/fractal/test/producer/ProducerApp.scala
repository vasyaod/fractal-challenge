package com.fractal.test.producer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import io.scalac.amqp.{Connection, Message, Queue}

import scala.util.{Failure, Random, Success}
import scala.concurrent.duration._

object ProducerApp extends App with LazyLogging {

  implicit val actorSystem = ActorSystem("rabbit-akka-stream")
  implicit val ec = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()

  val words = Vector("word1", "word2", "word3", "word4", "word5", "word6", "word7", "word8", "word9", "word10")

  def generatePhrase(): String = {
    val phraseLen = Random.nextInt(10) + 1
    (1 to phraseLen)
      .map ( _ => words(Random.nextInt(words.size)))
      .mkString(" ")
  }

  // All connection data is within the application.conf
  val rabbitConnection = Connection()

  val setup = for {
    _ <- rabbitConnection.queueDeclare(Queue(name = "queue1", durable = true))
  } yield ()

  setup.onComplete {
    case Success(_) =>

      // Our queue was created successfully.
      Source.tick(initialDelay = 1.second, interval = 1.second, tick = ())
        .map(_ => generatePhrase())
        .log("tick")
        .map(msg => Message(ByteString(msg)))
        .runWith(Sink.fromSubscriber(rabbitConnection.publishDirectly("queue1")))

    case Failure(t) =>
      logger.error("Something was wrong during creation of the queue", t)
  }
}
