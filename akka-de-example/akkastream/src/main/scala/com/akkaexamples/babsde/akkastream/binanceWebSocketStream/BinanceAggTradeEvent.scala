package com.akkaexamples.babsde.akkastream.binanceWebSocketStream

import play.api.libs.json.{Json, OFormat}

//{"e":"aggTrade","E":1608839581249,"s":"BTCUSDT","a":471718643,"p":"23314.87000000","q":"0.00297600","f":524221954,"l":524221954,"T":1608839581247,"m":false,"M":true}
case class BinanceAggTradeEvent(
  e: String,
  E: Long,
  s: String,
  a: Long,
  p: String,
  q: String,
  f: Long,
  l: Long,
  T: Long,
  m: Boolean,
  M: Boolean
)

object BinanceAggTradeEvent {
  implicit val format: OFormat[BinanceAggTradeEvent] = Json.format[BinanceAggTradeEvent]
}
