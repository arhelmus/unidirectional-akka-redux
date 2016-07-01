package me.archdev.api

import cats.data.Xor
import org.scalatest.{Matchers, WordSpecLike}
import me.archdev.api.external.ExternalProtocol._
import me.archdev.api.external.{InternalServerError, PublishTweet, TweetPublished}

class ExternalProtocolTest extends WordSpecLike with Matchers {

  trait Context {}

  "External protocol" should {

    "serialize events properly" in new Context {
      val event = TweetPublished("test")
      val expected = Xor.Right("""{"TweetPublished":{"tweet":"test"}}""")
      serializeEvent(event) should be(expected)
    }

    "serialize failure messages properly" in new Context {
      val expected = """{"FailureMessage": {"message":"Internal server error"}}"""
      serializeFailureMessage(InternalServerError()) should be(expected)
    }

    "deserialize commands properly" in new Context {
      val command = """{"PublishTweet":{"tweet":"test"}}"""
      val expected = Xor.Right(PublishTweet("test"))
      deserializeCommand(command) should be(expected)
    }

    "show an error if command cannot be deserialized" in new Context {
      val command = """{"PublishTweet":{"tweety":"test"}}"""
      deserializeCommand(command).isLeft should be(true)
    }

  }

}
