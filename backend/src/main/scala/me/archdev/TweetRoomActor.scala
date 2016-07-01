package me.archdev

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import me.archdev.TweetRoomActor.{PublishTweet, Subscribe, TweetPublished, UnSubscribe, _}
import me.archdev.api.internal.{Command, Event}

class TweetRoomActor extends Actor {

  var subscribedUsers: Map[UserId, ActorRef] = Map()
  var tweets: Seq[Tweet] = Seq(
    adminUUID -> defaultTweet
  )

  override def receive: Receive = {
    case Subscribe(userId, userActor) =>
      subscribedUsers = subscribedUsers + (userId -> userActor)
      userActor ! TweetPublished(tweets.head._2)
    case PublishTweet(userId, message) =>
      tweets = (userId -> message) +: tweets
      subscribedUsers.values.foreach(_ ! TweetPublished(message))
    case UnSubscribe(userId) =>
      subscribedUsers = subscribedUsers - userId
    case unhandledMessage =>
      println(s"Command cannot be handled: $unhandledMessage")
  }

}

object TweetRoomActor {

  def apply()(implicit actorSystem: ActorSystem) =
    actorSystem.actorOf(Props[TweetRoomActor])

  type UserId = String
  type Tweet = (UserId, String)

  val adminUUID = "00000000-0000-0000-0000-000000000000"
  val defaultTweet = "Confidence ebbs and flows - just because you have it one day does not mean you will have it forever."

  sealed trait TweetRoomCommand extends Command
  case class Subscribe(userId: UserId, userActor: ActorRef) extends TweetRoomCommand
  case class UnSubscribe(userId: String) extends TweetRoomCommand
  case class PublishTweet(userId: String, message: String) extends TweetRoomCommand

  case class TweetPublished(message: String) extends Event

}