package com.examples.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}

object StreamsExample extends App {
  implicit val system = ActorSystem("actor-system")
  Source(List("Scala", "akka", "JVM", "StReam", "Kafka"))
    .map(element => element.toLowerCase).filter(element => element.length > 3)
    .to(Sink.foreach(element => println(element)))
    .run()
}
