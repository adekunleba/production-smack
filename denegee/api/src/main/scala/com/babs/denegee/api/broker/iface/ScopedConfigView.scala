package com.babs.denegee.api.broker.iface

trait ScopedConfigView[S <: ScopeType[S], K <: SharedResourceKey]
    extends ConfigView[S, K] {

  def getScope: S
}
