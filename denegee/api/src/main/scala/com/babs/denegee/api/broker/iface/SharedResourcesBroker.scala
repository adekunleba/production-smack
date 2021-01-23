package com.babs.denegee.api.broker.iface

trait SharedResourcesBroker[S <: ScopeType[S]] {

  def selfScope: ScopeInstance[S]

  def getScope(scopeType: S): ScopeInstance[S]

  def getSharedResources[T, K <: SharedResourceKey](
      factory: SharedResourceFactory[T, K, S],
      key: K
  ): T

  def getSharedResourcesAsScope[T, K <: SharedResourceKey](
      factory: SharedResourceFactory[T, K, S],
      key: K,
      scopeType: S
  ): T

  def bindSharedResourceAtScope[T, K <: SharedResourceKey](
      factory: SharedResourceFactory[T, K, S],
      key: K,
      scopeType: S,
      instance: T
  ): T

  def newSubScopedBuilder[T <: SharedResourcesBroker[S]](
      subScope: ScopeInstance[S]
  ): SubScopedBrokerBuilder[S, T]
}
