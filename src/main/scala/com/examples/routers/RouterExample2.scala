package com.examples.routers

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.{Behaviors, Routers}

import java.lang.Thread.sleep

object RouterExample2 extends App {
  val system: ActorSystem[ExampleActor2.DoLog] = ActorSystem(ExampleActor2(), "my-actor-system")
  system ! ExampleActor2.DoLog("Hello World")
  sleep(60000)
  system.terminate()
}

object ExampleActor2 {
  case class DoLog(text: String) extends Command

  sealed trait Command

  val serviceKey = ServiceKey[Command]("log-worker")


  def apply(): Behavior[DoLog] =
    Behaviors.setup { ctx =>
      // this would likely happen elsewhere - if we create it locally we
      // can just as well use a pool
      val worker = ctx.spawn(Worker(), "worker")
      ctx.system.receptionist ! Receptionist.Register(serviceKey, worker)

      val group = Routers.group(serviceKey)
      val router = ctx.spawn(group, "worker-group")
      Behaviors.receiveMessage { message =>
        // the group router will stash messages until it sees the first listing of registered
        // services from the receptionist, so it is safe to send messages right away
        (0 to 10).foreach { n =>
          router ! DoLog(s"msg $n $message")
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
