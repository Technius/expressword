package org.expressword

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.LoggingAdapter
import akka.pattern.ask
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.model.{ MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import scala.concurrent.Future
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
              (WordService ? UpdateWord(vocab))
                .mapTo[Option[Vocabulary]]
                .flatMap {
                  case Some(old) => Future.successful(ApiResponse.success(old))
                  case _ =>
                    logging.debug(s"Scraping word data for '${vocab.word}'")
                    val scrapeFuture = for {
                      updated <- SearchService.scrapeData(vocab)
                      _ <- WordService ? UpdateWord(updated)
                    } yield {
                      logging.debug(
                        s"Updated information for '${vocab.word}': $updated")
                      ApiResponse.success("Word added")
                    }
                    
                    scrapeFuture onFailure { case _ =>
                      logging.error(
                        s"Failed to retrieve word data for '${vocab.word}'")
                    }

                    scrapeFuture recover { case _ =>
                      ApiResponse.failure("Failed to retrieve word data")
                    }
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
        path("api" / Rest) { _ =>
          overrideStatusCode(StatusCodes.NotFound) {
            complete {
              ApiResponse.failure("Resource not found")
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
