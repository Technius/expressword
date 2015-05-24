package org.vocabstar

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.marshalling.{ PredefinedToEntityMarshallers, ToEntityMarshaller }
import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, ResponseEntity }
import akka.http.scaladsl.server.Directives._
import play.twirl.api.Html

import WordCategory._

class Routes(implicit system: ActorSystem, mat: ActorFlowMaterializer)
    extends util.Json4sMarshalling {

  val htmlMarshaller: ToEntityMarshaller[Html] =
    PredefinedToEntityMarshallers
      .stringMarshaller(MediaTypes.`text/html`)
      .compose(_.body)

  val default =
    path("api" / "words")(wordsApi) ~
    get {
      // TODO: pathSingleSlash(displayPage(html.home())) ~
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

  val wordsApi =
    get {
      complete {
        Vocabulary("test", Seq(Definition(Noun, "test")), Seq(), Seq())
      }
    }
}
