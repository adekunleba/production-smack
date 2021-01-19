package com.babs.denegee.api.broker.denegee_scope

import com.babs.denegee.api.broker.ScopeInstanceException
import com.babs.denegee.api.broker.iface.{ScopeInstance, SimpleScopeInstance}
import cats.syntax.all._

/**
  * Alias for GobblinScopeInstance
  */
trait DenegeeScopeInstance extends SimpleScopeInstance[DenegeeScopeTypesV2] {

  override def rootScope: DenegeeScopeTypesV2 = DenegeeScopeTypesV2.Global

  override def isLocal: Boolean =
    DenegeeScopeTypesV2.localScopes.toSeq.contains(scopeTypesBase)

  override def parentScopes(): Iterable[DenegeeScopeTypesV2] =
    if (scopeTypesBase.isDefinedParent) {
      scopeTypesBase.scopeParent.get
    } else {
      Seq.empty
    }

  override def defaultScopeInstance
    : Option[ScopeInstance[DenegeeScopeTypesV2]] =
    scopeTypesBase match {
      case x if x == DenegeeScopeTypesV2.Task || x == DenegeeScopeTypesV2.Job =>
        None
      case _ => this.some
    }

}

object DenegeeScopeInstance {

  /**
    * There is also a conern of throwing exception explicitly here.
    * Should we rather wrap this in an either.??
    *
    * The apply method is focused solely on ensuring that Job and Task instances
    * are not created by the Denegee scope instance rather it is delegated to using
    * the specific case classes, to ensure that the equals and hashcode is same
    * And all job types can be monitored based on their defaultID
    */
  def apply(scopeTypes: DenegeeScopeTypesV2,
            parsedScopeId: String): DenegeeScopeInstance =
    scopeTypes match {

      /** I am thinking around making use of instances derived from Denegee scope instead of the
        * scope type check that is we explicity use what was in Gobblin i.e each enumeration is
        * dependent on a baseClass and this base class is check for .isassignableFrom[DenegeeScopeInstanc]
        * dependent on a baseClass and this base class is check for .isassignableFrom[DenegeeScopeInstanc]
        */
      case x if x == DenegeeScopeTypesV2.Task || x == DenegeeScopeTypesV2.Job =>
        throw ScopeInstanceException(
          s"Cannot instantiate new cope type with this, use the default for ${x.value}"
        )
      case _ =>
        new DenegeeScopeInstance {
          override def scopeTypesBase: DenegeeScopeTypesV2 = scopeTypes

          override def scopeIdValue: String = parsedScopeId
        }
    }

}
