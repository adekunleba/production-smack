package com.babs.denegee.api.broker

case class ScopeInstanceException(message: String)
    extends IllegalArgumentException(message)
