package me.archdev.api.external

import cats.data.Xor
import me.archdev.TweetRoomActor

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

/**
  * External messaging protocol between client and server.
  * This layer of abstraction created to make internal messaging protocol independent from service API.
  */

sealed trait CommandProtocol
case class PublishTweet(tweet: String) extends CommandProtocol

sealed trait EventProtocol
case class TweetPublished(tweet: String) extends EventProtocol

case class FailureMessage(message: String)
object InternalServerError {
  def apply() = FailureMessage("Internal server error")
}

object ExternalProtocol {

  import me.archdev.api.internal._

  def serializeEvent(eventProtocol: EventProtocol): Xor[FailureMessage, String] =
    Xor.right(eventProtocol.asJson.noSpaces)

  def serializeFailureMessage(failureMessage: FailureMessage): String =
    s"""{"FailureMessage": ${failureMessage.asJson.noSpaces}}"""

  def deserializeCommand(text: String): Xor[FailureMessage, CommandProtocol] =
    decode[CommandProtocol](text).bimap(
      error => FailureMessage(error.getMessage),
      result => result
    )

  def convertToInternalCommand(userId: String, command: CommandProtocol): Xor[FailureMessage, Command] =
    command match {
      case PublishTweet(tweet) =>
        Xor.Right(TweetRoomActor.PublishTweet(userId, tweet))
      case unhandledCommand =>
        // TODO: log this stuff
        Xor.Left(InternalServerError())
    }

  def convertToExternalEvent(event: Event): Xor[FailureMessage, EventProtocol] =
    event match {
      case TweetRoomActor.TweetPublished(tweet) =>
        Xor.Right(TweetPublished(tweet))
      case unhandledEvent =>
        // TODO: log this stuff
        Xor.Left(InternalServerError())
    }

}