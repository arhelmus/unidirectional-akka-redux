import java.util.UUID

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._

class TweetRoomActorTest extends TestKit(ActorSystem()) with WordSpecLike with Matchers {

  trait Context {
    val userId = UUID.randomUUID().toString
    val tweetRoomRef = TestActorRef[TweetRoomActor]
    val tweetRoom = tweetRoomRef.underlyingActor

    def subscribe() = tweetRoomRef ! Subscribe(userId, testActor)
    def publishTweet(message: String) = tweetRoomRef ! PublishTweet(userId, message)
    def unSubscribe() = tweetRoomRef ! UnSubscribe(userId)
    def expectDefaultTweet() = {
      // TODO: Understand whats the reason of double sending of same event from actor.
      expectMsg(TweetPublishedEvent(TweetRoomActor.defaultTweet))
      expectMsg(TweetPublishedEvent(TweetRoomActor.defaultTweet))
    }
  }

  "Tweet room actor" should {

    "have admin tweet on startup" in new Context {
      tweetRoom.tweets should be(Seq(
        TweetRoomActor.adminUUID -> TweetRoomActor.defaultTweet
      ))
    }

    "add user to subscribers on Subscribe message" in new Context {
      within(500 millis) {
        subscribe()
        tweetRoom.users should be(Map(userId -> testActor))
      }
    }

    "publish current message to subscribed user on Subscribe message" in new Context {
      within(500 millis) {
        subscribe()
        expectDefaultTweet()
      }
    }

    "remove user from subscribers on UnSubscribe message" in new Context{
      within(500 millis) {
        subscribe()
        unSubscribe()
        tweetRoom.users should be(Map.empty)
      }
    }

    "publish tweet to subscribers on PublishTweet message" in new Context {
      within(500 millis) {
        subscribe()
        expectDefaultTweet()
        publishTweet("test")
        expectMsg(TweetPublishedEvent("test"))
        unSubscribe()
        publishTweet("test2")
        expectNoMsg
      }
    }

    "save published tweet in history" in new Context {
      within(500 millis) {
        publishTweet("test")
        tweetRoom.tweets.size should be(2)
        tweetRoom.tweets.head should be(userId -> "test")
      }
    }

  }

}
