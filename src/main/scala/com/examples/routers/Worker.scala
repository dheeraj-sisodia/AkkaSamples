package com.examples.routers

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.examples.routers.RouterExample1.{Command, DoLog}

import java.lang.Thread.sleep
object Worker {


  def apply(): Behavior[Command] = Behaviors.setup { context =>
    context.log.info("Starting worker")

    Behaviors.receiveMessage {
      case DoLog(text) =>
        sleep(5000)
        context.log.info("Got message {}", text)
        Behaviors.same
    }
  }
}
