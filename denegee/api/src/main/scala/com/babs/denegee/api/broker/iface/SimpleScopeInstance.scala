package com.babs.denegee.api.broker.iface

import com.babs.denegee.api.broker.SimpleScopeType
import cats.syntax.all._

//trait SimpleScopeInstance[T <: EnumEntry] extends ScopeType[T] with ScopeInstance[T]
trait SimpleScopeInstance[T] extends ScopeType[T] with ScopeInstance[T] {
  def scopeTypesBase: T

  def scopeIdValue: String

  override def scopeType: T = scopeTypesBase

  override def scopeId: String = scopeIdValue

  // Maybe they should have a better name
  override def name: String = scopeIdValue

}

trait SimpleScopeImpl extends SimpleScopeInstance[SimpleScopeType] {

  override def isLocal: Boolean =
    SimpleScopeType.localScopes.toSeq.contains(scopeTypesBase)

  override def rootScope: SimpleScopeType = SimpleScopeType.Global

  override def parentScopes(): Iterable[SimpleScopeType] =
    if (scopeTypesBase.isDefinedParent) {
      scopeTypesBase.scopeParent.get
    } else {
      Seq.empty
    }

  override def defaultScopeInstance: Option[ScopeInstance[SimpleScopeType]] =
    this.some
}

object SimpleScopeImpl {

  def apply(scopeTypes: SimpleScopeType,
            parsedScopeId: String): SimpleScopeImpl =
    new SimpleScopeImpl {
      override def scopeTypesBase: SimpleScopeType = scopeTypes

      override def scopeIdValue: String = parsedScopeId
    }
}
