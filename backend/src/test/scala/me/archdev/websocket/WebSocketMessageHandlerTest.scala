package me.archdev.websocket

import java.util.UUID

import akka.stream.scaladsl.{Keep, Sink}
import akka.testkit.TestProbe
import me.archdev.RouteActor
import me.archdev.TweetRoomActor.PublishTweet
import me.archdev.util.ActorTest
import RouteActor.{Connect, Disconnect}
import org.scalatest.{Matchers, WordSpecLike}

class WebSocketMessageHandlerTest extends ActorTest with WordSpecLike with Matchers {

  trait Context {
    val probe = TestProbe()
    val userId = UUID.randomUUID().toString
    val tweetHandlerStream = WebSocketMessageHandler(probe.ref)(userId).toMat(Sink.ignore)(Keep.right)

    def testCommandSequence: Seq[Class[_]] = Seq(classOf[Connect], classOf[Disconnect])
    def testCommandSequence(commands: Class[_]*): Seq[Class[_]] = testCommandSequence ++ commands
  }

  "Tweet handler stream" should {

    "send Connect and Disconnect messages to Router" in new Context {
      emptySource.runWith(tweetHandlerStream)
      expectCommandSignatures(probe, testCommandSequence)
    }

    "deserialize command and convert it to inner representation" in new Context {
      sourceWithMessage("""{"PublishTweet": {"tweet": "smth"}}""").runWith(tweetHandlerStream)
      expectCommandSignatures(probe, testCommandSequence(classOf[PublishTweet]))
    }

  }

}
