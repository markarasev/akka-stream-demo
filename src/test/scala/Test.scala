import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

class Test extends TestKit(ActorSystem("Test")) with WordSpecLike with Matchers {

  "akka-stream" should {

    "process a data sequence" in {
      val data = List(1, 2, 3, 4, 5)
      Source(data)
        .map(_ * 2)
        .runForeach(println)
    }

  }

  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
