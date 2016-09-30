package org.zalando.nakadi.client.scala

import org.zalando.nakadi.client.scala.model._
import play.api.libs.functional.syntax._
import play.api.libs.json._

object PlayJsonImplicits {
  private val metadataReads: Reads[EventMetadata] = (
    (__ \ "eid").read[String] ~
      (__ \ "event_type").readNullable[String] ~
      (__ \ "occurred_at").read[String] ~
      (__ \ "received_at").readNullable[String] ~
      (__ \ "parent_eids")
        .readNullable[Seq[String]]
        .map(_.getOrElse(Seq.empty)) ~
      (__ \ "flow_id").readNullable[String] ~
      (__ \ "partition").readNullable[String]
  ).tupled.map((EventMetadata.apply _).tupled)

  private val metadataWrites: Writes[EventMetadata] =
    Writes(
      (eventMetaData: EventMetadata) =>
        Json.obj(
          "eid"         -> eventMetaData.eid,
          "event_type"  -> eventMetaData.eventTypeName,
          "occurred_at" -> eventMetaData.occurredAt,
          "received_at" -> eventMetaData.receivedAt,
          "parent_eids" -> eventMetaData.parentEids,
          "flow_id"     -> eventMetaData.flowId,
          "partition"   -> eventMetaData.partition
      ))

  implicit val nakadiKlientMetadataFormat: Format[EventMetadata] =
    Format(metadataReads, metadataWrites)

  private def dataChangeEventReads[T](
      implicit dataReads: Reads[T]
  ): Reads[DataChangeEvent[T]] =
    (
      (__ \ "data").read[T] ~
        (__ \ "data_type").read[String] ~
        (__ \ "data_op").read[String].map(DataOperation.withName) ~
        (__ \ "metadata").readNullable[EventMetadata]
    ).tupled.map(x =>
      DataChangeEvent(x._1, x._2, x._3, x._4) // Cant use (.apply _).tupled due to type inference issues
    )

  private def dataChangeEventWrites[T](
      implicit dataWrites: Writes[T]): Writes[DataChangeEvent[T]] =
    Writes(
      (dataChangeEvent: DataChangeEvent[T]) =>
        Json.obj(
          "data"      -> dataChangeEvent.data,
          "data_type" -> dataChangeEvent.dataType,
          "data_op"   -> dataChangeEvent.dataOperation.toString,
          "metadata"  -> dataChangeEvent.metadata
      ))

  implicit def nakadiKlientDataChangeFormats[T](
      implicit reads: Reads[T],
      writes: Writes[T]): Format[DataChangeEvent[T]] =
    Format(dataChangeEventReads[T], dataChangeEventWrites[T])

  private val problemReads: Reads[Problem] = (
    (__ \ "type").read[String] ~
      (__ \ "title").read[String] ~
      (__ \ "status").read[Int].map(x => new Integer(x)) ~
      (__ \ "detail").readNullable[String] ~
      (__ \ "instance").readNullable[String]
  ).tupled.map((Problem.apply _).tupled)

  private val problemWrites: Writes[Problem] =
    Writes(
      (problem: Problem) =>
        Json.obj(
          "type"     -> problem.problemType,
          "title"    -> problem.title,
          "status"   -> problem.status.toInt,
          "detail"   -> problem.detail,
          "instance" -> problem.instance
      ))

  implicit val nakadiKlientProblemFormat: Format[Problem] =
    Format(problemReads, problemWrites)

  private val partitionReads: Reads[Partition] = (
    (__ \ "partition").read[String] ~
      (__ \ "oldest_available_offset").read[String] ~
      (__ \ "newest_available_offset").read[String]
  ).tupled.map((Partition.apply _).tupled)

  private val partitionWrites: Writes[Partition] =
    Writes(
      (partition: Partition) =>
        Json.obj(
          "partition"               -> partition.partition,
          "oldest_available_offset" -> partition.oldestAvailableOffset,
          "newest_available_offset" -> partition.newestAvailableOffset
      ))

  implicit val nakadiKlientPartitionFormat: Format[Partition] =
    Format(partitionReads, partitionWrites)

  private val eventTypeSchemaReads: Reads[EventTypeSchema] = (
    (__ \ "type").read[String].map(SchemaType.withName) ~
      (__ \ "schema").read[String]
  ).tupled.map((EventTypeSchema.apply _).tupled)

  private val eventTypeSchemaWrites: Writes[EventTypeSchema] =
    Writes(
      (eventTypeSchema: EventTypeSchema) =>
        Json.obj(
          "type"   -> eventTypeSchema.schemaType.toString,
          "schema" -> eventTypeSchema.schema
      ))

  implicit val nakadiKlientEventTypeSchemaFormat: Format[EventTypeSchema] =
    Format(eventTypeSchemaReads, eventTypeSchemaWrites)

  private val eventTypeStatisticsReads: Reads[EventTypeStatistics] = (
    (__ \ "messages_per_minute").read[Int].map(x => new Integer(x)) ~
      (__ \ "message_size").read[Int].map(x => new Integer(x)) ~
      (__ \ "read_parallelism").read[Int].map(x => new Integer(x)) ~
      (__ \ "write_parallelism").read[Int].map(x => new Integer(x))
  ).tupled.map((EventTypeStatistics.apply _).tupled)

  private val eventTypeStatisticsWrites: Writes[EventTypeStatistics] =
    Writes(
      (eventTypeStatistics: EventTypeStatistics) =>
        Json.obj(
          "messages_per_minute" -> eventTypeStatistics.messagesPerMinute.toInt,
          "message_size"        -> eventTypeStatistics.messageSize.toInt,
          "read_parallelism"    -> eventTypeStatistics.readParallelism.toInt,
          "write_parallelism"   -> eventTypeStatistics.writeParallelism.toInt
      ))

  implicit val nakadiKlientEventTypeStatisticsFormat: Format[
    EventTypeStatistics] =
    Format(eventTypeStatisticsReads, eventTypeStatisticsWrites)

  private val eventTypeReads: Reads[EventType] = (
    (__ \ "name").read[String] ~
      (__ \ "owning_application").read[String] ~
      (__ \ "category").read[String].map(EventTypeCategory.withName) ~
      (__ \ "enrichment_strategies")
        .read[Seq[String]]
        .map(_.map(EventEnrichmentStrategy.withName)) ~
      (__ \ "partition_strategy")
        .readNullable[String]
        .map(_.map(PartitionStrategy.withName)) ~
      (__ \ "schema").read[EventTypeSchema] ~
      (__ \ "data_key_fields").read[Seq[String]] ~
      (__ \ "partition_key_fields").read[Seq[String]] ~
      (__ \ "default_statistics").readNullable[EventTypeStatistics]
  ).tupled.map((EventType.apply _).tupled)

  private val eventTypeWrites: Writes[EventType] =
    Writes(
      (eventType: EventType) =>
        Json.obj(
          "name"               -> eventType.name,
          "owning_application" -> eventType.owningApplication,
          "category"           -> eventType.category.toString,
          "enrichment_strategies" -> eventType.enrichmentStrategies.map(
            _.toString),
          "partition_strategy"   -> eventType.partitionStrategy.map(_.toString),
          "schema"               -> eventType.schema,
          "data_key_fields"      -> eventType.dataKeyFields,
          "partition_key_fields" -> eventType.partitionKeyFields,
          "default_statistics"   -> eventType.statistics
      ))

  implicit val nakadiKlientEventTypeFormat: Format[EventType] =
    Format(eventTypeReads, eventTypeWrites)

  private val cursorReads: Reads[Cursor] = (
    (__ \ "partition").read[String] ~
      (__ \ "offset").read[String]
  ).tupled.map((Cursor.apply _).tupled)

  private val cursorWrites: Writes[Cursor] =
    Writes(
      (cursor: Cursor) =>
        Json.obj(
          "partition" -> cursor.partition,
          "offset"    -> cursor.offset
      ))

  implicit val nakadiKlientCursorFormat: Format[Cursor] =
    Format(cursorReads, cursorWrites)

  private def eventStreamBatchReads[T <: Event](
      implicit eventReads: Reads[T]): Reads[EventStreamBatch[T]] =
    (
      (__ \ "cursor").read[Cursor] ~
        (__ \ "events").readNullable[Seq[T]]
    ).tupled.map(x => EventStreamBatch(x._1, x._2))

  private def eventStreamBatchWrites[T <: Event](
      implicit eventWrites: Writes[T]): Writes[EventStreamBatch[T]] =
    Writes(
      (eventStreamBatch: EventStreamBatch[T]) =>
        Json.obj(
          "cursor" -> eventStreamBatch.cursor,
          "events" -> eventStreamBatch.events
      ))

  implicit def nakadiKlientEventStreamBatchFormat[T <: Event](
      implicit eventReads: Reads[T],
      eventWrites: Writes[T]): Format[EventStreamBatch[T]] =
    Format(eventStreamBatchReads[T], eventStreamBatchWrites[T])

  private val batchItemResponseReads: Reads[BatchItemResponse] = (
    (__ \ "eid").readNullable[String] ~
      (__ \ "publishing_status")
        .read[String]
        .map(BatchItemPublishingStatus.withName) ~
      (__ \ "step").readNullable[String].map(_.map(BatchItemStep.withName)) ~
      (__ \ "detail").readNullable[String]
  ).tupled.map((BatchItemResponse.apply _).tupled)

  private val batchItemResponseWrites: Writes[BatchItemResponse] =
    Writes(
      (batchItemResponse: BatchItemResponse) =>
        Json.obj(
          "eid"               -> batchItemResponse.eid,
          "publishing_status" -> batchItemResponse.publishingStatus.toString,
          "step"              -> batchItemResponse.step.map(_.toString),
          "detail"            -> batchItemResponse.detail
      ))

  implicit val nakadiKlientBatchItemResponseFormat: Format[BatchItemResponse] =
    Format(batchItemResponseReads, batchItemResponseWrites)
}
