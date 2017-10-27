package demo.back_pressure

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.ExecutionContextExecutor

object Stream extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  var i = 0
  def generateIntIterator(): Iterator[Int] = {
    i += 1
    Seq(i).toIterator
  }

  val nCores = Runtime.getRuntime.availableProcessors()

  Source.cycle(generateIntIterator)
    .mapAsync(nCores)(Processing.toOppositeAsync)
    .runForeach(println)

}
