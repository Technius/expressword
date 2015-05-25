package org.expressword

import akka.actor.{ ActorSystem, Props }
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory

object ExpressWordServer extends App {

  implicit val system = ActorSystem("expressword")
  implicit val materializer = ActorFlowMaterializer()

  val config = ConfigFactory.load()
  val host = config.getString("host")
  val port = config.getInt("port")
  val googleApiKey = config.getString("google.apiKey")
  val googleCseId = config.getString("google.searchEngineId")

  val wordService = system.actorOf(Props[service.InMemoryWordService]())
  val searchService = new service.SearchService(googleApiKey, googleCseId)
  val routes = new Routes(wordService, searchService)

  val serverFuture = Http().bindAndHandle(routes.default, host, port)
}
