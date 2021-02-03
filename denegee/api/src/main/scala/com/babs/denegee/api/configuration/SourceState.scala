package com.babs.denegee.api.configuration

//import com.babs.denegee.api.broker.denegee_scope.DenegeeScopeTypesV2
//import com.babs.denegee.api.broker.iface.SharedResourcesBroker
import cats.syntax.all._
import com.babs.denegee.common.logging.LoggingAdapter

trait SourceState extends StateS with LoggingAdapter { self =>

  val sourceStateLogger = logger

  def workUnitS: Iterable[StateS]
  def datasetByUrns: Map[String, StateS]

  def properties: Option[PropsStateImm]

  /**Update the StateManager withe given properties
    * PLS NOTE: Original implementation extends the
    * State Configuration. However since we are dealing
    * as much immutability as possible, this is a better way
    * to implement things for us
    */
  def stateManager: PropsStateImm =
    if (self.properties.isDefined) {
      PropsStateImm().addAll(properties.get)
    } else {
      PropsStateImm()
    }

//  // Error here DenegeeScopeTypes should extend ScopeType hence we need to go back to the code
//  def broker: SharedResourcesBroker[DenegeeScopeTypesV2] = ???
//
//  def isWorkUnitStateMaterialized: Boolean = ???

}

/**
  *  Default implementer
  *  It also manages the Source state to ensure we always return a SourceState type
  *  This is similar to the implementation we had in the Immutable PropsState [PropsStateImm]
  */
private case class SourceStateImp[T <: StateS](
    override val workUnitS: Seq[T],
    override val datasetByUrns: Map[String, T],
    override val properties: Option[PropsStateImm]
) extends SourceState

object SourceState {

  def apply[T <: StateS](): SourceState =
    SourceStateImp[T](Seq.empty, Map.empty, none)

  /**
    *Create a SourceState with some properties
    */
  def apply[T <: StateS](state: PropsStateImm): SourceState =
    SourceStateImp[T](Seq.empty, Map.empty, state.some)

  def apply[T <: StateS](propsState: PropsStateImm,
                         previousWorkUnit: Seq[T]): SourceState =
    SourceStateImp[T](previousWorkUnit, Map.empty, propsState.some)

  def apply[T <: StateS](
      propsState: PropsStateImm,
      previousWorkUnit: Seq[T],
      previousDataUrns: Map[String, T]
  ): SourceState =
    SourceStateImp(previousWorkUnit, previousDataUrns, propsState.some)

}
