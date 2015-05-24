package org.vocabstar

import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.marshalling.{ PredefinedToEntityMarshallers, ToEntityMarshaller }
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import play.twirl.api.Html
import scala.concurrent.duration._
import scala.util.Failure

import WordCategory._
import service._
import service.WordService._

class Routes(WordService: ActorRef)
            (implicit system: ActorSystem, mat: ActorFlowMaterializer)
    extends util.Json4sMarshalling {

  import system.dispatcher

  implicit val timeout: Timeout = 5.seconds

  val htmlMarshaller: ToEntityMarshaller[Html] =
    PredefinedToEntityMarshallers
      .stringMarshaller(MediaTypes.`text/html`)
      .compose(_.body)

  val default =
    encodeResponse {
      path("api" / "words") {
        (post & entity(as[Vocabulary])) { vocab =>
          complete {
            (WordService ? UpdateWord(vocab)).mapTo[Option[Vocabulary]] map {
              case Some(old) => ApiResponse.success(old)
              case _ => ApiResponse.success("Word added")
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
        path("word" / Segment) { word =>
          complete(s"wip $word")
        } ~
        path("about")(displayPage(html.about())) ~
        pathSingleSlash(displayPage(html.home())) ~
        path("assets" / Segment) { segment =>
          getFromResource(s"assets/$segment")
        }
      }
    }

  def displayPage(page: Html) =
    completeWith(htmlMarshaller) { callback =>
      callback(page)
    }
}
