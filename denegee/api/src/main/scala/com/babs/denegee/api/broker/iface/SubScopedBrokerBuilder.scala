package com.babs.denegee.api.broker.iface

import com.typesafe.config.Config

trait SubScopedBrokerBuilder[S <: ScopeType[S], B <: SharedResourcesBroker[S]] {

  def withOverridingConfig(config: Config): SubScopedBrokerBuilder[S, B]

  def build: B

  def withAdditionalParentBroker(
      broker: SharedResourcesBroker[S]): SubScopedBrokerBuilder[S, B]
}
