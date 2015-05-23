package org.vocabstar

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory

object Boot extends App {

  implicit val system = ActorSystem("vocabstar")
  implicit val materializer = ActorFlowMaterializer()

  val config = ConfigFactory.load()
  val host = config.getString("host")
  val port = config.getInt("port")

  val routes = new Routes

  val serverFuture = Http().bindAndHandle(routes.default, host, port)
}
