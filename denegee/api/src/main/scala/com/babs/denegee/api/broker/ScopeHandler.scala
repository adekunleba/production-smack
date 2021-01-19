package com.babs.denegee.api.broker

import cats.syntax.all._

trait ScopeHandler[T] {

  def value: String

  def parent: Seq[Option[T]]

  def isDefinedParent: Boolean = parent.map(_.isDefined).reduce(_ && _)

  /**
    * TODO: Thinking that maybe we should get all the parents of any parent for a
    * given Global Scope
    * @return
    */
  def scopeParent: Option[Seq[T]] =
    if (isDefinedParent) parent.flatten.some else None
}
