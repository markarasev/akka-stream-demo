package demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.testkit.TestKit
import org.scalatest.WordSpecLike

class SimpleStreamExamples extends TestKit(ActorSystem("SimpleStreamExamples"))
  with WordSpecLike {

  "Simple stream" should {

    val data = Seq(1, 2, 3, 4, 5)

    "process a (1, 2, 3, 4, 5) data sequence v1" in {

      val source = Source(data)
      val flow = Flow.fromFunction((i: Int) => i * 2)
      val sink = Sink.foreach(println)

      source
        .via(flow)
        .to(sink)
        .run()

    }

    "process a (1, 2, 3, 4, 5) data sequence v2" in {

      Source(data)
        .map(_ * 2)
        .runForeach(println)

    }

  }

  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
