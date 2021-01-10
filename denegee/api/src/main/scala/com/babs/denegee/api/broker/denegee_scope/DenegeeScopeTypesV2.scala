package com.babs.denegee.api.broker.denegee_scope

import enumeratum.values.{StringEnum, StringEnumEntry}
import cats.syntax.all._

/**
  * manages the various job levels under the DenegeeScope
  * @param value
  * @param parent
  */
sealed abstract class DenegeeScopeTypesV2(
    val value: String,
    parent: Option[DenegeeScopeTypesV2]*
) extends StringEnumEntry {
  def isDefinedParent: Boolean = parent.map(_.isDefined).reduce(_ && _)

  /**
    * TODO: Thinking that maybe we should get all the parents of any parent for a
    * given Global Scope
    * @return
    */
  def scopeParent: Option[Seq[DenegeeScopeTypesV2]] =
    if (isDefinedParent) parent.flatten.some else None
}

case object DenegeeScopeTypesV2 extends StringEnum[DenegeeScopeTypesV2] {

  /**
    * Caveat; Job and Task values in gobblin are null.
    * I don't know if this might have effect in future yet
    * hence may revisit
    */
  val values = findValues

  case object Global extends DenegeeScopeTypesV2("global", None)
  case object Instance extends DenegeeScopeTypesV2("instance", Global.some)
  case object Job extends DenegeeScopeTypesV2("job", Instance.some)
  case object Container extends DenegeeScopeTypesV2("container", Instance.some)

  case object MultiTaskAttempt
      extends DenegeeScopeTypesV2("multiTask", Job.some, Container.some)
  case object Task extends DenegeeScopeTypesV2("task", MultiTaskAttempt.some)

  def localScopes: Iterable[DenegeeScopeTypesV2] =
    Seq(Container, Task, MultiTaskAttempt)
}
