package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.Serializer
import org.zalando.nakadi.client.scala.model.{BusinessEvent, DataChangeEvent}
import play.api.libs.json.{Json, Writes}
import PlayJsonImplicits._

object NakadiSerializer {
  def seqDataChangeEventSerializer[T](
      implicit writes: Writes[DataChangeEvent[T]]) =
    new Serializer[Seq[DataChangeEvent[T]]] {
      override def to(from: Seq[DataChangeEvent[T]]): String = {
        Json.asciiStringify(Json.toJson(from))
      }
    }

  def seqBusinessEventSeralizer[T](
      implicit writes: Writes[BusinessEvent]
  ) = new Serializer[Seq[BusinessEvent]] {
    override def to(from: Seq[BusinessEvent]): String = {
      Json.asciiStringify(Json.toJson(from))
    }
  }

}
