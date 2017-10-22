package demo

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object SimpleStream extends App {

  implicit val system: ActorSystem = ActorSystem.create()
  implicit val materializer: ActorMaterializer = ActorMaterializer.apply()

  val source: Source[Int, NotUsed] = Source.apply(Seq.apply(1, 2, 3, 4, 5))
  val double: Flow[Int, Int, NotUsed] = Flow.fromFunction(_ * 2)
  val sink: Sink[Int, Future[Int]] = Sink.fold(0)(_ + _)

  val graph: RunnableGraph[Future[Int]] = source.via(double).toMat(sink)(Keep.right)
  val matFuture: Future[Int] = graph.run()

  val mat = Await.result(matFuture, 1.second)
  println(s"mat = $mat")

  val doubledSource: Source[Int, NotUsed] = source.via(double)
  val mergedSource: Source[Int, NotUsed] = doubledSource.merge(source) // source with 1, 2, 3, 4, 5, 2, 4, 6, 8, 10
  val matFuture2: Future[Int] = mergedSource.runWith(sink)

  val mat2 = Await.result(matFuture2, 1.second)
  println(s"mat2 = $mat2")

  system.terminate()

}
