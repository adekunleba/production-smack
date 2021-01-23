package com.babs.denegee.api.broker.denegee_scope

import enumeratum.values.{StringEnum, StringEnumEntry}
import cats.syntax.all._
import com.babs.denegee.api.broker.ScopeHandler
import com.babs.denegee.api.broker.iface.ScopeInstance

import scala.collection.immutable

/**
  * manages the various job levels under the DenegeeScope
  * @param value
  * @param parent
  */
sealed abstract class DenegeeScopeTypesV2(
  val value: String,
  val parent: Option[DenegeeScopeTypesV2]*
) extends StringEnumEntry
    with ScopeHandler[DenegeeScopeTypesV2] {
  override def handlerRootScope: DenegeeScopeTypesV2 =
    DenegeeScopeTypesV2.Global

  override def isLocal: Boolean =
    DenegeeScopeTypesV2.localScopes.toSeq.contains(this)

  override def defaultScopeInstance: Option[ScopeInstance[DenegeeScopeTypesV2]] =
    if (this != DenegeeScopeTypesV2.Task) DenegeeScopeInstance(this, value).some else None
}

case object DenegeeScopeTypesV2 extends StringEnum[DenegeeScopeTypesV2] {

  /**
    * Caveat; Job and Task values in gobblin are null.
    * I don't know if this might have effect in future yet
    * hence may revisit
    */
  override def values: immutable.IndexedSeq[DenegeeScopeTypesV2] = findValues

  case object Global extends DenegeeScopeTypesV2("global", None)
  case object Instance extends DenegeeScopeTypesV2("instance", Global.some)
  case object Job extends DenegeeScopeTypesV2("job", Instance.some)
  case object Container extends DenegeeScopeTypesV2("container", Instance.some)

  case object MultiTaskAttempt extends DenegeeScopeTypesV2("multiTask", Job.some, Container.some)
  case object Task extends DenegeeScopeTypesV2("task", MultiTaskAttempt.some)

  def localScopes: Iterable[DenegeeScopeTypesV2] =
    Seq(Container, Task, MultiTaskAttempt)
}
