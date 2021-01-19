package com.babs.denegee.api.broker.iface

trait ScopeInstance[T] extends ScopeType[T] {

  def scopeType: T // Alias for getType

  def scopeId: String
}
