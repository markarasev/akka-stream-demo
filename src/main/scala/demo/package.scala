import org.apache.kafka.clients.producer.ProducerRecord

package object demo {

  type Seq[A] = collection.immutable.Seq[A]

  object ProducerRecord {

    def apply[V](topic: String, value: V): ProducerRecord[Array[Byte], V] = new ProducerRecord[Array[Byte], V](topic, value)

  }

}
