import java.util.UUID

import akka.http.scaladsl.server.Directives._

class Router(tweetHandlerStream: TweetHandlerStream) {

  val wsRoute =
    path("ws-api") {
      handleWebSocketMessages(
        tweetHandlerStream.webSocketTweetFlow(UUID.randomUUID().toString)
      )
    }

}
