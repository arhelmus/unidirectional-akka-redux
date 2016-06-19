import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{Matchers, WordSpecLike}
import pushka.json._

import scala.collection.immutable
import scala.concurrent.duration._

class TweetHandlerStreamTest extends TestKit(ActorSystem()) with WordSpecLike with Matchers {

  trait Context {
    val probe = TestProbe()
    val userId = UUID.randomUUID().toString
    val tweetHandlerStream = new TweetHandlerStream(probe.ref)
      .webSocketTweetFlow(userId).toMat(Sink.ignore)(Keep.right)
    implicit val flowMaterializer = ActorMaterializer()

    def emptySource = sourceWithMessages(Nil)
    def sourceWithMessage(message: String) = sourceWithMessages(Seq(message))
    def sourceWithMessages(messages: Seq[String]) =
      Source[String](immutable.Iterable(messages:_*)).map(TextMessage.apply)

    def commandSequence: Seq[Class[_]] = Seq(classOf[Subscribe], classOf[UnSubscribe])
    def commandSequence(commands: Class[_]*): Seq[Class[_]] = commandSequence ++ commands

  }

  "Tweet handler stream" should {

    "may send Subscribe message on connect and UnSubscribe on disconnect" in new Context {
      emptySource.runWith(tweetHandlerStream)
      probe.expectMsgAllClassOf(500 millis, commandSequence:_*)
    }

    "parse PublishTweet command and execute it" in new Context {
      sourceWithMessage(write(CommandWrapper("PublishTweet", Seq("Test tweet")))).runWith(tweetHandlerStream)
      probe.expectMsgAllClassOf(500 millis, commandSequence(classOf[PublishTweet]):_*)
    }

  }

}
