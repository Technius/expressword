package org.vocabstar

case class ApiResponse[A <: AnyRef](status: String, message: A)

object ApiResponse {

  def success[A <: AnyRef](msg: A) = ApiResponse("success", msg)

  def failure[A <: AnyRef](msg: A) = ApiResponse("failure", msg)

  def fromOpt[A <: AnyRef, B <: AnyRef](opt: Option[A], errorMsg: B) =
    opt match {
      case Some(value) => success(value)
      case _ => failure(errorMsg)
    }
}
