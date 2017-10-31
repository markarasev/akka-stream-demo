package demo

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class MatStreamExamples extends TestKit(ActorSystem("MatStreamExamples"))
  with WordSpecLike with Matchers {

  "Materializing stream" should {

    val data = Seq(1, 2, 3, 4, 5)

    "process a (1, 2, 3, 4, 5) data sequence v1" in {

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

      val matFuture = Source(data)
        .map(_ * 2)
        .runFold(0)(_ + _)

      val mat = Await.result(matFuture, 1.second)
      mat shouldEqual 30
      println(s"mat = $mat")

    }

  }

}
