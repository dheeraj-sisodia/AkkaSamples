package com.examples.dispatchers

import akka.actor.ActorSystem

import scala.concurrent.Future
import scala.util.Success

object DispatchersExample extends App {
  implicit val system = ActorSystem("actor-system")
  implicit val defaultDispatcher = system.dispatcher
  //--------------------------------Default Dispatcher-------------------------------------
  val defaultDispatcherFuture = Future { // uses defaultDispatcher
    Thread.sleep(1000) // will block on default dispatcher,
    System.currentTimeMillis().toString // Starving the routing infrastructure
    "------------------------Default Dispatcher Completed------------------------"
  }
  defaultDispatcherFuture.onComplete {
    case Success(value) => println(value)
  }
  //--------------------------------Custom Dispatcher-----------------------------------------
  //------------------'my-blocking-dispatcher' is defined in application.conf file--------------
  implicit val blockingDispatcher = system.dispatchers.lookup("my-blocking-dispatcher")

  val customDispatcherFuture = Future { // uses the good "blocking dispatcher" that we configured,
    // instead of the default dispatcher to isolate the blocking.
    Thread.sleep(1000)
    System.currentTimeMillis().toString
    "------------------------Custom Dispatcher Completed------------------------"
  }
  customDispatcherFuture.onComplete {
    case Success(value) => println(value)
  }
  system.terminate()
}
