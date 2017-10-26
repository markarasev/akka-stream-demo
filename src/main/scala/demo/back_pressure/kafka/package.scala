package demo.back_pressure

import org.apache.kafka.clients.producer.ProducerRecord

package object kafka {

  object ProducerRecord {

    def apply[V](topic: String, value: V): ProducerRecord[Array[Byte], V] = new ProducerRecord[Array[Byte], V](topic, value)

  }

}
