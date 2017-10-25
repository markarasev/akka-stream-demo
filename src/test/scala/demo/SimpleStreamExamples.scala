package demo

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class SimpleStreamExamples extends TestKit(ActorSystem("SimpleStreamExamples"))
  with WordSpecLike with Matchers {

  "Simple stream" should {

    "process a (1, 2, 3, 4, 5) data sequence v1" in {

      val data = Seq(1, 2, 3, 4, 5)

      val source: Source[Int, NotUsed] = Source(data)
      val double: Flow[Int, Int, NotUsed] = Flow.fromFunction(_ * 2)
      val sink: Sink[Int, Future[Int]] = Sink.fold(0)(_ + _)

      val graph: RunnableGraph[Future[Int]] =
        source
          .via(double)
          .toMat(sink)(Keep.right)

      val matFuture: Future[Int] = graph.run()

      val mat = Await.result(matFuture, 1.second)
      mat shouldEqual 30
      println(s"mat = $mat")

    }

    "process a (1, 2, 3, 4, 5) data sequence v2" in {

      val data = Seq(1, 2, 3, 4, 5)

      val source = Source(data)

      val matFuture = source
        .map(_ * 2)
        .runFold(0)(_ + _)

      val mat = Await.result(matFuture, 1.second)
      mat shouldEqual 30
      println(s"mat = $mat")

    }

  }

  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
