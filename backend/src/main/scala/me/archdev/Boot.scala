package me.archdev

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import me.archdev.http.{HttpRoute, HttpServer}

object Boot extends App {

  implicit val actorSystem = ActorSystem()
  implicit val flowMaterializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val tweetRoomActor = TweetRoomActor()
  val routeActor = RouteActor(tweetRoomActor)

  new HttpServer(HttpRoute(routeActor)).launch("localhost", config.getInt("http.port"))

}
