package org.expressword

import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.Failure

import WordCategory._
import service._
import service.WordService._

class Routes(WordService: ActorRef, SearchService: SearchService)
            (implicit system: ActorSystem, mat: ActorFlowMaterializer)
    extends util.Json4sMarshalling {

  import system.dispatcher

  implicit val timeout: Timeout = 5.seconds

  val default =
    encodeResponse {
      path("api" / "words") {
        (post & entity(as[Vocabulary])) { vocab =>
          complete {
            (WordService ? UpdateWord(vocab)).mapTo[Option[Vocabulary]] map {
              case Some(old) => ApiResponse.success(old)
              case _ =>
                SearchService.scrapeData(vocab) onSuccess {
                  case updated => WordService ! UpdateWord(updated)
                }
                ApiResponse.success("Word added")
            }
          }
        }
      } ~
      path("api" / "words" / Segment) { word =>
        get {
          complete {
            (WordService ? FindWordExact(word)).mapTo[Option[Vocabulary]] map {
              ApiResponse.fromOpt(_, "Not found")
            }
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
      get {
        pathSingleSlash(getFromResource("assets/template.html")) ~
        path("assets" / Rest) { path =>
          getFromResource(s"assets/$path")
        }
      }
    }
}
