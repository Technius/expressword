package org.vocabstar

import akka.actor.{ ActorSystem, Props }
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory

object Boot extends App {

  implicit val system = ActorSystem("vocabstar")
  implicit val materializer = ActorFlowMaterializer()

  val config = ConfigFactory.load()
  val host = config.getString("host")
  val port = config.getInt("port")

  val wordService = system.actorOf(Props[service.InMemoryWordService]())
  val routes = new Routes(wordService)

  val serverFuture = Http().bindAndHandle(routes.default, host, port)
}
