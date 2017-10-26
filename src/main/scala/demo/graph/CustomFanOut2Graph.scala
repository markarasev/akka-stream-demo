package demo
package graph

import akka.NotUsed
import akka.stream.scaladsl.{Flow, GraphDSL, Partition}
import akka.stream.{FanOutShape2, Graph}

object CustomFanOut2Graph {

  def apply[I,O](f: I => O, p: O => Int): Graph[FanOutShape2[I,O,O], NotUsed] =

    GraphDSL.create() { implicit builder =>

      import GraphDSL.Implicits._

      val applyFlow = Flow.fromFunction(f)
      val applyShape = builder.add(applyFlow)

      val partition = Partition(2, p)
      val partitionShape = builder.add(partition)

      applyShape ~> partitionShape

      new FanOutShape2(applyShape.in, partitionShape.out(0), partitionShape.out(1))

    }

}
