package demo

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class MatStreamExamples extends TestKit(ActorSystem("MatStreamExamples"))
  with WordSpecLike with Matchers {

  "Materializing stream" should {

    val data = Seq(1, 2, 3, 4, 5)

    "materialize the sum of the processing results v1" in {

      val source: Source[Int, NotUsed] = Source(data)
      val double: Flow[Int, Int, NotUsed] = Flow.fromFunction(_ * 2)
      val sink: Sink[Int, Future[Int]] = Sink.fold(0) { (sum, i) =>
        println(i)
        sum + i
      }

      val graph: RunnableGraph[Future[Int]] =
        source
          .via(double)
          .toMat(sink)(Keep.right)

      val mat: Future[Int] = graph.run()

      val sum = Await.result(mat, 1.second)
      sum shouldEqual 30
      println(s"sum = $sum")

    }

    "materialize the sum of the processing results v2" in {

      val mat = Source(data)
        .map(_ * 2)
        .runFold(0) { (sum, i) =>
          println(i)
          sum + i
        }

      val sum = Await.result(mat, 1.second)
      sum shouldEqual 30
      println(s"sum = $sum")

    }

  }

  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
