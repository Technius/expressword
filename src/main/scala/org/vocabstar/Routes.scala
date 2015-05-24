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
    path("api" / "words") {
      (post & entity(as[Vocabulary])) { vocab =>
        complete {
          WordService ? UpdateWord(vocab)
        }
      }
    } ~
    path("api" / "words" / Segment) { word =>
      get {
        complete {
          (WordService ? FindWordExact(word)).mapTo[Option[Vocabulary]] map {
            case Some(vocab) => vocab
            case _ => "not found"
          }
        }
      } ~
      delete {
        complete {
          (WordService ? RemoveWord(word)).mapTo[Option[Vocabulary]] map {
            case Some(vocab) => vocab
            case _ => "not found"
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
      encodeResponse {
        getFromResourceDirectory("static")
      }
    }

  def displayPage(page: Html) =
    encodeResponse {
      completeWith(htmlMarshaller) { callback =>
        callback(page)
      }
    }
}
