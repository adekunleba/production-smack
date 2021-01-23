package com.babs.denegee.api.configuration

import com.babs.denegee.api.broker.denegee_scope.DenegeeScopeTypesV2
import com.babs.denegee.api.broker.iface.SharedResourcesBroker

trait SourceState[K, T] extends StateManager {

  def workUnitS: Iterable[T] = ???
  def datasetByUrns: Map[K, T] = ???

  // Error here DenegeeScopeTypes should extend ScopeType hence we need to go back to the code
  def broker: SharedResourcesBroker[DenegeeScopeTypesV2] = ???
}

/**
  *  Default implementer
  *  It also manages the Source state to ensure we always return a SourceState type
  *  This is similar to the implementation we had in the Immutable PropsState [PropsStateImm]
  */
case class SourceStateImp[K, T](
    override val workUnitS: Seq[T],
    override val datasetByUrns: Map[K, T]
) extends SourceState[K, T]

object SourceState {

  def apply[K, T](): SourceState[K, T] =
    SourceStateImp[K, T](Seq.empty, Map.empty)

}
