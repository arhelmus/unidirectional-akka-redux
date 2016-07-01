package me.archdev.websocket

import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import cats.data.Xor
import me.archdev.api.external._
import me.archdev.api.internal._
import me.archdev.websocket.WebSocketRouteActor.{Connect, Disconnect}

object WebSocketMessageHandler {

  def apply(wsRouteActor: ActorRef)(userId: String) =
    Flow[Message]
      .map(convertMessageToCommand(userId))
      .via(commandExecutionFlow(wsRouteActor, userId))
      .map(convertEventToMessage)
      .recover(handleError)

  private def convertMessageToCommand(userId: String)(message: Message): Command = {
    def collectMessage(message: Message): Xor[FailureMessage, String] =
      message match {
        case TextMessage.Strict(text) =>
          Xor.Right(text)
        case _ =>
          Xor.Left(InternalServerError())
      }

    val handleFlow = for {
      message <- collectMessage(message)
      externalCommand <- ExternalProtocol.deserializeCommand(message)
      internalCommand <- ExternalProtocol.convertToInternalCommand(userId, externalCommand)
    } yield internalCommand

    handleFlow
      .leftMap(_.message)
      .leftMap(ShowErrorMessage(userId, _))
      .merge
  }

  private def commandExecutionFlow(wsRouteActor: ActorRef, userId: String) = {
    val inputFlow =
      Flow[Command]
        .to(Sink.actorRef(wsRouteActor, Disconnect(userId)))

    val outputFlow =
      Source.actorRef[Event](10, OverflowStrategy.fail)
        .mapMaterializedValue(wsRouteActor ! Connect(userId, _))

    Flow.fromSinkAndSource(inputFlow, outputFlow)
  }

  private def convertEventToMessage(event: Event): Message = {
    def collectEvent(event: Event) =
      event match {
        case ErrorMessageShowed(failure) => Xor.Left(FailureMessage(failure))
        case e => Xor.Right(e)
      }

    val handleFlow = for {
      checkedEvent <- collectEvent(event)
      externalEvent <- ExternalProtocol.convertToExternalEvent(checkedEvent)
      serializedEvent <- ExternalProtocol.serializeEvent(externalEvent)
    } yield TextMessage.Strict(serializedEvent)

    handleFlow
      .leftMap(ExternalProtocol.serializeFailureMessage)
      .leftMap(TextMessage.Strict)
      .merge
  }

  private def handleError: PartialFunction[Throwable, Message] = {
    case ex: Throwable =>
      // TODO: log this stuff
      TextMessage.Strict(ExternalProtocol.serializeFailureMessage(InternalServerError()))
  }

}
