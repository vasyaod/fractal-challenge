package com.fractal.test.producer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import io.scalac.amqp.{Connection, Message, Queue}

import scala.util.{Failure, Success}

object ProducerApp extends App with LazyLogging {

  implicit val actorSystem = ActorSystem("rabbit-akka-stream")
  implicit val ec = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()

  val words = Vector("word1", "word2", "word3", "word4", "word5", "word6", "word7", "word8", "word9", "word10")

//  val frases = (0 to 1000).map { _ =>
//    val wordNum = Random.nextInt(10)
//    (0 to wordNum - 1).
//  }

  // All connection data is within the application.conf
  val rabbitConnection = Connection()

  val setup = for {
    _ <- rabbitConnection.queueDeclare(Queue(name = "queue1", durable = true))
  } yield ()

  setup.onComplete {
    case Success(_) =>

      // Our queue was created successfully.
      Source (words)
        .map(msg => Message(ByteString(msg)))
        .runWith(Sink.fromSubscriber(rabbitConnection.publishDirectly("queue1")))

    case Failure(t) =>
      logger.error("Something was wrong during creation of the queue", t)
  }
}
