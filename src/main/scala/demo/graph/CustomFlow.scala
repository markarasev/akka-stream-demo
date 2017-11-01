package demo
package graph

import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Partition, Sink}

import scala.concurrent.Future


object CustomFlow {

  def apply(): Flow[Int,Int,Future[Int]] = {

    val evenSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

    val graph = GraphDSL.create(evenSink) { implicit builder => evenSinkShape =>

      import GraphDSL.Implicits._

      val tripleFlow = Flow.fromFunction((i: Int) => i * 3)
      val tripleShape = builder.add(tripleFlow)

      val partition = Partition(2, (i: Int) => i % 2)
      val partitionShape = builder.add(partition)

      tripleShape ~> partitionShape
                     partitionShape.out(0) ~> evenSinkShape

      FlowShape(tripleShape.in, partitionShape.out(1))

    }

    Flow.fromGraph(graph)

  }

}
