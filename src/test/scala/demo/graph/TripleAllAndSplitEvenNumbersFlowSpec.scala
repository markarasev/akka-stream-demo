package demo
package graph

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class TripleAllAndSplitEvenNumbersFlowSpec
  extends TestKit(ActorSystem("TripleAllAndSplitEvenNumbersFlowSpec"))
    with WordSpecLike with Matchers {

  "TripleAllAndSplitEvenNumbersFlow" should {

    "print even numbers and accumulate others" in {
      val data = Seq(1, 2, 3, 4, 5)
      val evenSink = Sink.fold[Int, Int](0)(_ + _)
      val flow = TripleAllAndSplitEvenNumbersFlow(evenSink)
      val oddSink = Sink.foreach[Int](i => println(s"odd number: $i"))

      val matFuture = Source(data)
        .viaMat(flow)(Keep.right)
        .to(oddSink)
        .run()

      val mat = Await.result(matFuture, 1.second)
      mat shouldEqual 18
      println(s"mat: $mat")
    }

  }

  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
