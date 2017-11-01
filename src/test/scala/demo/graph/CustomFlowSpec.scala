package demo
package graph

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class CustomFlowSpec
  extends TestKit(ActorSystem("TripleAllAndSplitEvenNumbersFlowSpec"))
    with WordSpecLike with Matchers {

  "TripleAllAndSplitEvenNumbersFlow" should {

    "print even numbers and accumulate others" in {
      val data = Seq(1, 2, 3, 4, 5)
      val flow: Flow[Int, Int, Future[Int]] = CustomFlow()
      val oddSink = Sink.foreach[Int](i => println(s"odd number: $i"))

      val matFuture = Source(data)
        .viaMat(flow)(Keep.right)
        .to(oddSink)
        .run()

      val mat = Await.result(matFuture, 1.second)
      mat shouldEqual 18
      println(s"even numbers sum: $mat")
    }

  }

  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
