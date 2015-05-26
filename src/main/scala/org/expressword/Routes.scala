package org.expressword

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.LoggingAdapter
import akka.pattern.ask
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

import WordCategory._
import service._
import service.WordService._

class Routes(WordService: ActorRef, SearchService: SearchService)
              (implicit system: ActorSystem, mat: ActorFlowMaterializer,
                logging: LoggingAdapter)
    extends util.Json4sMarshalling {

  import system.dispatcher

  implicit val timeout: Timeout = 5.seconds

  val default =
    logRequestResult("expressword-server") {
      encodeResponse {
        path("api" / "words") {
          (get & parameter("search")) { query =>
            complete {
              (WordService ? SearchWord(query)).mapTo[Seq[Vocabulary]].map {
                case Seq() => ApiResponse.failure("No results found.")
                case r => ApiResponse.success(r)
              }
            }
          } ~
          (post & entity(as[Vocabulary])) { vocab =>
            complete {
              (WordService ? UpdateWord(vocab)).mapTo[Option[Vocabulary]] map {
                case Some(old) => ApiResponse.success(old)
                case _ =>
                  logging.debug(s"Scraping word data for '${vocab.word}'")
                  SearchService.scrapeData(vocab) onComplete {
                    case Success(updated) =>
                      logging.debug(
                        s"Updating information for '${vocab.word}': $updated")
                      WordService ! UpdateWord(updated)
                    case Failure(err) =>
                      logging.error(
                        s"Failed to retrieve word data for '${vocab.word}'")
                  }
                  ApiResponse.success("Word added")
              }
            }
          }
        } ~
        path("api" / "words" / Segment) { word =>
          get {
            complete {
              (WordService ? FindWordExact(word))
                .mapTo[Option[Vocabulary]]
                .map(ApiResponse.fromOpt(_, "Not found"))
            }
          } ~
          delete {
            complete {
              (WordService ? RemoveWord(word)).mapTo[Option[Vocabulary]] map {
                ApiResponse.fromOpt(_, "Not found")
              }
            }
          }
        } ~
        path("assets" / Rest) { path =>
          getFromResource(s"assets/$path")
        } ~
        getFromResource("assets/template.html")
      }
    }
}
