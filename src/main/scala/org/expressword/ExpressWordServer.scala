package org.expressword

import akka.actor.{ ActorSystem, Props }
import akka.event.Logging
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import scala.util.Success

object ExpressWordServer extends App {

  implicit val system = ActorSystem("expressword")
  implicit val materializer = ActorFlowMaterializer()
  implicit val logger = Logging(system, getClass)

  val config = ConfigFactory.load()
  val host = config.getString("host")
  val port = config.getInt("port")
  val googleApiKey = config.getString("google.apiKey")
  val googleCseId = config.getString("google.searchEngineId")

  val wordService = system.actorOf(Props[service.InMemoryWordService]())
  val searchService = new service.SearchService(googleApiKey, googleCseId)
  val routes = new Routes(wordService, searchService)

  import system.dispatcher

  logger.info("Attempting to start server...")
  val serverFuture = Http().bindAndHandle(routes.default, host, port)
  serverFuture onComplete {
    case Success(_) =>
      logger.info("Server started")
    case _ =>
      logger.error("Server failed to start")
      system.shutdown()
  }
}
