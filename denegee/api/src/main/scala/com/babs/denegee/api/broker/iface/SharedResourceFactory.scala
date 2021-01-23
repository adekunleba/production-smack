package com.babs.denegee.api.broker.iface

trait SharedResourceFactory[T, K <: SharedResourceKey, S <: ScopeType[S]] {

  def getName: String

  def createResource(
      broker: SharedResourcesBroker[S],
      config: ScopedConfigView[S, K]
  ): SharedResourceFactoryResponse[T]

  def getAutoScope(broker: SharedResourcesBroker[S],
                   config: ConfigView[S, K]): S
}
