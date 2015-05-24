package org.vocabstar.util

import akka.http.scaladsl.marshalling.{ PredefinedToEntityMarshallers, ToEntityMarshaller }
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.{ FromEntityUnmarshaller, PredefinedFromEntityUnmarshallers }
import akka.stream.FlowMaterializer
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.vocabstar.WordCategory
import scala.concurrent.ExecutionContext

object Json4sMarshalling extends Json4sMarshalling

trait Json4sMarshalling {

  implicit val formats =
    Serialization.formats(NoTypeHints) + new EnumNameSerializer(WordCategory)


  implicit def unmarshaller(implicit ec: ExecutionContext,
      mat: FlowMaterializer): FromEntityUnmarshaller[JValue] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller
      .forContentTypes(MediaTypes.`application/json`)
      .map(parse(_))

  implicit def marshaller[A <: AnyRef]: ToEntityMarshaller[A] =
    PredefinedToEntityMarshallers
      .stringMarshaller(MediaTypes.`application/json`)
      .compose(write[A](_))
}
