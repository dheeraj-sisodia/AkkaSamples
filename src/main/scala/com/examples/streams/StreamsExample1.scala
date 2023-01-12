package com.examples.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}

import java.lang.Thread.sleep
import scala.concurrent.Future
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global

object StreamsExample1 extends App {

  implicit val system = ActorSystem("actor-system")
  Source(List("Scala", "akka", "JVM", "StReam", "Kafka"))
    .map(element => element.toLowerCase).filter(element => element.length > 3)
    .to(Sink.foreach(element => println(element)))
    .run()
  system.terminate()
}
