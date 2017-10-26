package demo
package graph

import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Sink}


object TripleAllAndSplitEvenNumbersFlow {

  def apply[M](evenSink: Sink[Int,M]): Flow[Int,Int,M] = {

    val tripleAndSplitEven = CustomFanOut2Graph[Int, Int](_ * 3, _ % 2)

    val graph = GraphDSL.create(evenSink) { implicit builder => evenSinkShape =>

      import GraphDSL.Implicits._

      val tripleAndSplitEvenShape = builder.add(tripleAndSplitEven)

      tripleAndSplitEvenShape.out0 ~> evenSinkShape

      FlowShape(tripleAndSplitEvenShape.in, tripleAndSplitEvenShape.out1)

    }

    Flow.fromGraph(graph)

  }

}
