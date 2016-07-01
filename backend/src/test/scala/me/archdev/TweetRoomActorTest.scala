package me.archdev

import java.util.UUID

import akka.testkit.TestActorRef
import me.archdev.TweetRoomActor.{PublishTweet, Subscribe, TweetPublished, UnSubscribe}
import me.archdev.util.ActorTest
import org.scalatest.{Matchers, WordSpecLike}

class TweetRoomActorTest extends ActorTest with WordSpecLike with Matchers {

  trait Context {
    val userId = UUID.randomUUID().toString
    val tweetRoomRef = TestActorRef[TweetRoomActor]
    val tweetRoom = tweetRoomRef.underlyingActor

    def subscribe() = tweetRoomRef ! Subscribe(userId, testActor)
    def publishTweet(message: String) = tweetRoomRef ! PublishTweet(userId, message)
    def unSubscribe() = tweetRoomRef ! UnSubscribe(userId)
    def expectDefaultTweet() = {
      // TODO: Understand whats the reason of double sending of same event from actor.
      expectMsg(TweetPublished(TweetRoomActor.defaultTweet))
      expectMsg(TweetPublished(TweetRoomActor.defaultTweet))
    }
  }

  "Tweet room actor" should {

    "have admin tweet on startup" in new Context {
      tweetRoom.tweets should be(Seq(
        TweetRoomActor.adminUUID -> TweetRoomActor.defaultTweet
      ))
    }

    "add user to subscribers on Subscribe message" in new Context {
      testScope {
        subscribe()
        tweetRoom.subscribedUsers should be(Map(userId -> testActor))
      }
    }

    "publish current message to subscribed user on Subscribe message" in new Context {
      testScope {
        subscribe()
        expectDefaultTweet()
      }
    }

    "remove user from subscribers on UnSubscribe message" in new Context{
      testScope {
        subscribe()
        unSubscribe()
        tweetRoom.subscribedUsers should be(Map.empty)
      }
    }

    "publish tweet to subscribers on PublishTweet message" in new Context {
      testScope {
        subscribe()
        expectDefaultTweet()
        publishTweet("test")
        expectMsg(TweetPublished("test"))
        unSubscribe()
        publishTweet("test2")
        expectNoMsg
      }
    }

    "save published tweet in history" in new Context {
      testScope {
        publishTweet("test")
        tweetRoom.tweets.size should be(2)
        tweetRoom.tweets.head should be(userId -> "test")
      }
    }

  }

}
