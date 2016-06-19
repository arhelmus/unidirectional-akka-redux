import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}

import scala.io.StdIn

object Boot extends App {

  implicit val actorSystem = ActorSystem("akka-system")

  val decider: Supervision.Decider = {
    case _: Throwable => Supervision.Restart
  }

  implicit val flowMaterializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider)
  )

  val tweetRoomActor = actorSystem.actorOf(Props[TweetRoomActor])
  val tweetHandlerStream = new TweetHandlerStream(tweetRoomActor)
  val router = new Router(tweetHandlerStream)

  startServer(router)

  def startServer(router: Router)(implicit actorSystem: ActorSystem) = {
    val interface = "localhost"
    val port = 8080

    val binding = Http().bindAndHandle(router.wsRoute, interface, port)
    println(s"Server is now online at http://$interface:$port\nPress RETURN to stop...")
    StdIn.readLine()

    import actorSystem.dispatcher

    binding.flatMap(_.unbind()).onComplete(_ => actorSystem.shutdown())
    println("Server is down...")
  }

}
