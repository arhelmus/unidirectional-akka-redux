package me.archdev

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import me.archdev.RouteActor.{Connect, Disconnect}
import me.archdev.TweetRoomActor.{Subscribe, TweetRoomCommand, UnSubscribe}
import me.archdev.api.internal.{Command, ErrorMessageShowed, ShowErrorMessage}

class RouteActor(tweetRoomActor: ActorRef) extends Actor {

  var connectedUsers: Map[String, ActorRef] = Map()

  override def receive: Receive = {
    case command: TweetRoomCommand =>
      tweetRoomActor ! command

    // Smth like constructor and destructor for our system
    case Connect(userId, userActor) =>
      connectedUsers = connectedUsers + (userId -> userActor)
      tweetRoomActor ! Subscribe(userId, userActor)
    case Disconnect(userId) =>
      connectedUsers = connectedUsers - userId
      tweetRoomActor ! UnSubscribe(userId)

    case ShowErrorMessage(userId, message) =>
      connectedUsers(userId) ! ErrorMessageShowed(message)
  }

}

object RouteActor {

  def apply(tweetRoomActor: ActorRef)(implicit actorSystem: ActorSystem) =
    actorSystem.actorOf(Props(classOf[RouteActor], tweetRoomActor))

  sealed trait RouterCommand extends Command
  case class Connect(userId: String, userActor: ActorRef) extends RouterCommand
  case class Disconnect(userId: String) extends RouterCommand

}