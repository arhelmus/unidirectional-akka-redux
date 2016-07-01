package me.archdev.util

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.testkit.{TestKit, TestProbe}

import scala.collection.immutable
import scala.concurrent.duration._

abstract class ActorTest extends TestKit(ActorSystem()) {

  implicit val flowMaterializer = ActorMaterializer()
  val defaultTimeout = 500 millis

  def testScope[T](f: => T): T =
    within(defaultTimeout)(f)

  def emptySource = sourceWithMessages(Nil)
  def sourceWithMessage(message: String) = sourceWithMessages(Seq(message))
  def sourceWithMessages(messages: Seq[String]) =
    Source[String](immutable.Iterable(messages:_*)).map(TextMessage.apply)

  def expectCommandSignature(probe: TestProbe, command: Class[_]) = expectCommandSignatures(probe, Seq(command))
  def expectCommandSignatures(probe: TestProbe, commandSequence: Seq[Class[_]]) =
    probe.expectMsgAllClassOf(defaultTimeout, commandSequence:_*)

  def expectCommand[T](probe: TestProbe, command: T) = expectCommandSequence(probe, Seq(command))
  def expectCommandSequence[T](probe: TestProbe, commandSequence: Seq[T]) =
    probe.expectMsgAllOf(defaultTimeout, commandSequence:_*)

}
