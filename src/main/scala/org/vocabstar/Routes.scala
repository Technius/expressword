package org.vocabstar

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._

class Routes(implicit system: ActorSystem, mat: ActorFlowMaterializer) {
  val default =
    get {
      pathSingleSlash {
        encodeResponse {
          getFromResource("static/index.html")
        }
      } ~
      encodeResponse {
        getFromResourceDirectory("static")
      }
    }
}
