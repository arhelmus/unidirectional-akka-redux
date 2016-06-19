import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import pushka.annotation.pushka
import pushka.json._

class TweetHandlerStream(tweetRoomActor: ActorRef) {

  /**
    * Flow that converts request message into Command and execute it in tweetFlow.
    */
  def webSocketTweetFlow(userId: String) =
    Flow[Message]
      .map(parseCommandWrapper)
      .map(_.toCommand(userId))
      .via(tweetFlow(userId))
      .map(write[TweetPublishedEvent](_))
      .map(TextMessage.Strict)
      .recover {
        case ex: Throwable =>
          val errorMessage = s"An error occurred during request processing: $ex"
          println(errorMessage)
          TextMessage.Strict(errorMessage)
      }

  /**
    * Flow that sends request commands to actor and maintain a state until connection opened.
    */
  private def tweetFlow(userId: String) = {
    val inputFlow =
      Flow[TweetRoomCommand]
        .to(Sink.actorRef(tweetRoomActor, UnSubscribe(userId)))

    val outputFlow =
      Source.actorRef[TweetPublishedEvent](10, OverflowStrategy.fail)
        .mapMaterializedValue(tweetRoomActor ! Subscribe(userId, _))

    Flow.fromSinkAndSource(inputFlow, outputFlow)
  }

  private def parseCommandWrapper(message: Message): CommandWrapper =
    message match {
      case TextMessage.Strict(text) => read[CommandWrapper](text)
    }

}

/**
  * Interface for client to send commands.
  */
@pushka
case class CommandWrapper(commandName: String, parameters: Seq[String]) {

  def toCommand(userId: String): TweetRoomCommand = commandName match {
    case "PublishTweet" =>
      PublishTweet(userId, getOrThrowFromParams(0, "message"))
    case unhandledCommand =>
      throw new RuntimeException(s"Wrong command name: $unhandledCommand")
  }

  private def getOrThrowFromParams(index: Int, parameterName: String) =
    parameters.lift(index).getOrElse(throw new RuntimeException(s"Cannot find $parameterName parameter (${index+1} element)."))

}