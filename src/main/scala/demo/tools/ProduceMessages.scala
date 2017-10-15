package demo.tools

import demo.ProducerRecord
import demo.common.KafkaSettings
import org.scalacheck.Gen

object ProduceMessages extends App {

  private val producer = KafkaSettings.producerSettings.createKafkaProducer()
  private val stringGenerator = Gen.alphaNumStr.suchThat(_.nonEmpty).map(_.toLowerCase)

  private def generateString(): String = stringGenerator.sample.getOrElse(generateString())

  for (i <- 1 to 1000000) {
    val elt = generateString()
    val record = ProducerRecord.apply(KafkaSettings.topic, elt)
    producer.send(record)
    if (i % 100000 == 0) println(s"Sent $i messages.")
  }

}
