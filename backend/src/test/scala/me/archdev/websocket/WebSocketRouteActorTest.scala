package me.archdev.websocket

import java.util.UUID

import akka.testkit.{TestActorRef, TestProbe}
import me.archdev.TweetRoomActor.{PublishTweet, Subscribe, TweetRoomCommand, UnSubscribe}
import me.archdev.api.internal.{ErrorMessageShowed, ShowErrorMessage}
import me.archdev.util.ActorTest
import me.archdev.websocket.WebSocketRouteActor.{Connect, Disconnect}
import org.scalatest.{Matchers, WordSpecLike}

class WebSocketRouteActorTest extends ActorTest with WordSpecLike with Matchers {

  trait Context {
    val tweetRoomActorProbe = TestProbe()
    val routeActor = TestActorRef(new WebSocketRouteActor(tweetRoomActorProbe.ref))
    val userId = UUID.randomUUID().toString

    def connect() = routeActor ! Connect(userId, testActor)
    def disconnect() = routeActor ! Disconnect(userId)

    def showError(msg: String) = routeActor ! ShowErrorMessage(userId, msg)
  }

  "Web socket route actor" should {

    "subscribe user to new tweets on Connect" in new Context {
      testScope {
        connect()
        expectCommandSignature(tweetRoomActorProbe, classOf[Subscribe])
      }
    }

    "save user reference on Connect" in new Context {
      testScope {
        connect()
        routeActor.underlyingActor.connectedUsers should be(Map(userId -> testActor))
      }
    }

    "unsubscribe user from tweets on Disconnect" in new Context {
      testScope {
        disconnect()
        expectCommandSignature(tweetRoomActorProbe, classOf[UnSubscribe])
      }
    }

    "remove user reference on Disconnect" in new Context {
      testScope {
        connect()
        disconnect()
        routeActor.underlyingActor.connectedUsers should be(Map())
      }
    }

    "convert ShowErrorMessage to ErrorMessageShowed and send it back" in new Context {
      testScope {
        connect()
        showError("test")
        expectMsg(ErrorMessageShowed("test"))
      }
    }

    "route TweetRoomCommand to TweetRoomActor" in new Context {
      testScope {
        routeActor ! PublishTweet(userId, "msg").asInstanceOf[TweetRoomCommand]
        expectCommandSignature(tweetRoomActorProbe, classOf[PublishTweet])
      }
    }

  }

}
