package com.babs.denegee.api.broker.iface

import com.typesafe.config.Config

trait ConfigView[S <: ScopeType[S], K <: SharedResourceKey] {
  def getFactoryName: String

  def getKey: K

  def getConfig: Config

  def getScopedView(scopeType: S): ScopedConfigView[S, K]
}
