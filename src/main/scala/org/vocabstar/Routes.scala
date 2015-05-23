package org.vocabstar

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._

class Routes(implicit system: ActorSystem, mat: ActorFlowMaterializer) {
  val default =
    get {
      complete {
        "test"
      }
      // todo
    }
}
