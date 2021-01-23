package com.akkaexamples.babsde.akkastream.sampleAkkaIngestion

import play.api.libs.json.{Json, OFormat}

case class Event(msg: String, clientId: String, timestamp: Long)

object Event {
  implicit val format: OFormat[Event] = Json.format[Event]
}
