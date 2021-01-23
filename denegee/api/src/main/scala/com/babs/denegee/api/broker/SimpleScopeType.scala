package com.babs.denegee.api.broker
import cats.syntax.all._
import com.babs.denegee.api.broker.iface.{ScopeInstance, SimpleScopeImpl}
import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.collection.immutable

sealed abstract class SimpleScopeType(val value: String,
                                      val parent: Option[SimpleScopeType]*)
    extends StringEnumEntry
    with ScopeHandler[SimpleScopeType] {
  override def handlerRootScope: SimpleScopeType = SimpleScopeType.Global

  override def isLocal: Boolean =
    SimpleScopeType.localScopes.toSeq.contains(this)

  override def defaultScopeInstance: Option[ScopeInstance[SimpleScopeType]] =
    SimpleScopeImpl(this, value).some
}

case object SimpleScopeType extends StringEnum[SimpleScopeType] {
  override def values: immutable.IndexedSeq[SimpleScopeType] = findValues

  case object Global extends SimpleScopeType("global", None)
  case object Local extends SimpleScopeType("local", Global.some)

  def localScopes: Iterable[SimpleScopeType] = Seq(Local)

}
