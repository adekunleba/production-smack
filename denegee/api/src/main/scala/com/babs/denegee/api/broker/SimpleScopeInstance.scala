package com.babs.denegee.api.broker

import com.babs.denegee.api.broker.iface.{ScopeInstance, ScopeType}

//trait SimpleScopeInstance[T <: EnumEntry] extends ScopeType[T] with ScopeInstance[T]
trait SimpleScopeInstance[T] extends ScopeType[T] with ScopeInstance[T]
