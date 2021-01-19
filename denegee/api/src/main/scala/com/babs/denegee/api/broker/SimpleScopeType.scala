package com.babs.denegee.api.broker
import cats.syntax.all._
import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.collection.immutable

sealed abstract class SimpleScopeType(val value: String,
                                      val parent: Option[SimpleScopeType]*)
    extends StringEnumEntry
    with ScopeHandler[SimpleScopeType]

case object SimpleScopeType extends StringEnum[SimpleScopeType] {
  override def values: immutable.IndexedSeq[SimpleScopeType] = findValues

  case object Global extends SimpleScopeType("global", None)
  case object Local extends SimpleScopeType("local", Global.some)

  def localScopes: Iterable[SimpleScopeType] = Seq(Local)

}
