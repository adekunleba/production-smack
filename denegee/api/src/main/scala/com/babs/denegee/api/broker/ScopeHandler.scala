package com.babs.denegee.api.broker

import cats.syntax.all._
import com.babs.denegee.api.broker.iface.ScopeType

trait ScopeHandler[T] extends ScopeType[T] {

  def value: String

  def handlerRootScope: T

  def parent: Seq[Option[T]]

  def isDefinedParent: Boolean = parent.map(_.isDefined).reduce(_ && _)

  /**
    * TODO: Thinking that maybe we should get all the parents of any parent for a
    * given Global Scope
    * @return
    */
  def scopeParent: Option[Seq[T]] =
    if (isDefinedParent) parent.flatten.some else None

  override def name: String = value

  override def rootScope: T = handlerRootScope

  override def parentScopes(): Iterable[T] =
    scopeParent.getOrElse(Seq.empty)

}
