package me.archdev.http

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import me.archdev.websocket.WebSocketMessageHandler

import scala.io.StdIn

class HttpServer(route: Route)(implicit actorSystem: ActorSystem, materializer: Materializer) {

  def launch(host: String, port: Int) = {
    Http().bindAndHandle(route, host, port)
    println(s"Server is now online at http://$host:$port")
  }

}

object HttpRoute {

  def apply(wsRouteActor: ActorRef) =
    path("ws-api") {
      handleWebSocketMessages(
        WebSocketMessageHandler(wsRouteActor)(UUID.randomUUID().toString)
      )
    }

}
