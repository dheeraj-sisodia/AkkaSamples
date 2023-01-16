package com.examples.routers

import akka.actor.typed.scaladsl.{Behaviors, Routers}
import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}

import java.lang.Thread.sleep

object RouterExample1 extends App {
  val system: ActorSystem[ExampleActor1.DoLog] = ActorSystem(ExampleActor1(), "my-actor-system")
  system ! ExampleActor1.DoLog("Hello World")
  sleep(20000)
  system.terminate()
}

object ExampleActor1 {
  case class DoLog(text: String) extends Command

  sealed trait Command

  def apply(): Behavior[DoLog] =
    Behaviors.setup { context =>
      val pool = Routers.pool(poolSize = 4) {
        // make sure the workers are restarted if they fail
        Behaviors.supervise(Worker()).onFailure[Exception](SupervisorStrategy.restart)
      }
      val router = context.spawn(pool, "worker-pool")
      Behaviors.receiveMessage { message =>
        (0 to 10).foreach { n =>
          router ! DoLog(s"$n $message")
        }
        Behaviors.same
      }
    }

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
}
