package me.archdev

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.archdev.http.{HttpRoute, HttpServer}
import me.archdev.websocket.WebSocketRouteActor

object Boot extends App {

  implicit val actorSystem = ActorSystem()
  implicit val flowMaterializer = ActorMaterializer()

  val tweetRoomActor = TweetRoomActor()
  val routeActor = WebSocketRouteActor(tweetRoomActor)

  new HttpServer(HttpRoute(routeActor)).launch("localhost", 8080)

}
