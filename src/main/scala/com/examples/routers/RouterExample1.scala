package com.examples.routers

import akka.actor.typed.scaladsl.{Behaviors, Routers}
import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}

import java.lang.Thread.sleep

object RouterExample1 extends App {
  val system: ActorSystem[MasterActor.DoLog] = ActorSystem(MasterActor(), "my-actor-system")
  system ! MasterActor.DoLog("World")
  sleep(20000)
  system.terminate()
}

object MasterActor {
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
}
