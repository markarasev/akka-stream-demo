package demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
import akka.{Done, NotUsed}
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ReusablePartsExamples extends TestKit(ActorSystem("ReusablePartsExamples"))
  with WordSpecLike with Matchers {

  "akka-stream" should {

    "reuse and merge sources, and filter elements" in {

      val data = Seq(1, 2, 3, 4, 5)

      // source with 1 2 3 4 5
      val source: Source[Int, NotUsed] = Source(data)
      // source with 2 4 6 8 10
      val doubledSource: Source[Int, NotUsed] = source.map(_ * 2)
      // source with 1 2 3 4 5 2 4 6 8 10
      val mergedSource: Source[Int, NotUsed] = doubledSource.merge(source)
      // source with 2 4 2 4 6 8 10
      val filteredSource: Source[Int, NotUsed] = mergedSource.filter(_ % 2 == 0)

      val printlnSink: Sink[Any, Future[Done]] = Sink.foreach(println)

      val matFuture = filteredSource.runWith(printlnSink)

      val mat = Await.result(matFuture, 1.second)
      mat shouldBe Done

    }

  }

  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
