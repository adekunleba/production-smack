package com.babs.denegee.api.broker.iface

trait ScopeType[T] {

  def name: String

  def rootScope: T

  def isLocal: Boolean

  /**
    * Parent scope can be empty for top level scopes
    * I don't think it is needed to deploy it as a None
    * @return
    */
  def parentScopes(): Iterable[T]

  def defaultScopeInstance: Option[ScopeInstance[T]]

}
