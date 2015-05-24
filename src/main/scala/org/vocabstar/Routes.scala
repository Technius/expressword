package org.vocabstar

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import play.twirl.api.Html

class Routes(implicit system: ActorSystem, mat: ActorFlowMaterializer)
    extends util.Json4sMarshalling {

  val default =
    path("api" / "words")(wordsApi) ~
    get {
      // TODO: pathSingleSlash(displayPage(html.home())) ~
      encodeResponse {
        getFromResourceDirectory("static")
      }
    }

  def displayPage(page: Html) = encodeResponse { 
    complete {
      page.body
    }
  }

  val wordsApi =
    get {
      complete {
        Vocabulary("test", Seq(Definition(Noun, "test")), Seq(), Seq())
      }
    }
}
