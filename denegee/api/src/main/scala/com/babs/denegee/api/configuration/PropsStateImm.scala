package com.babs.denegee.api.configuration

import cats.syntax.all._
import com.babs.denegee.common.logging.LoggingAdapter

sealed trait PropsStateImm extends Serializable with LoggingAdapter { self =>

  val propLogger = logger

  def stateConfig: Option[PropsStateImm.State] = None

  /**
    * Should populate instance of current state with another state.
    * Should return a new populated state
    *
    * @param other
    * @return
    */
  def addAll(other: PropsStateImm): PropsStateImm =
    PropsStateImm.AddAll(other).run

  /**
    * Add properties to this state should return new state
    * @param properties
    * @return
    */
  def addAllP(properties: PropsStateImm.StateConfigImmi): PropsStateImm =
    PropsStateImm.AddAllP(properties).run

  def commonProperties: PropsStateImm.StateConfigImmi = {
    val commonProps = self.stateConfig.map(_.commProps)
    if (commonProps.isDefined) {
      commonProps.get
    } else {
      ConfigProperties.empty
    }
  }

  def specialProperties: PropsStateImm.StateConfigImmi = {
    val specProps = self.stateConfig.map(_.specProps)
    if (specProps.isDefined) {
      specProps.get
    } else {
      ConfigProperties.empty
    }
  }

  /**
    * IMPLEMENTER
    * @param state
    */
  implicit private class Interpreter(state: PropsStateImm) {
    def run: PropsStateImm =
      state match {
        case PropsStateImm.AddAll(other) => {
          val config: PropsStateImm.State = {
            (for {
              props <- stateConfig
            } yield props)
              .getOrElse(PropsStateImm.State(
                ConfigProperties.empty,
                ConfigProperties.empty)) //There should be something to do here
          }
          PropsStateImm(config)
        }

        case PropsStateImm.AddAllP(prop) => {
          val property = (for {
            p <- self.stateConfig
            _ = p.specProps.putAll(prop)
          } yield p).getOrElse({
            val newConf = ConfigProperties(prop)
            propLogger.warn("Since State is none , we should add new config")
            // Setting up an empty configuration here
            PropsStateImm.State(ConfigProperties.empty, newConf)
          })
          PropsStateImm(property)
        }

        case PropsStateImm.State(common, spec) => {
          PropsStateImm(
            PropsStateImm.State(ConfigProperties.empty, ConfigProperties.empty))
        }
        case _ => self
      }

  }
}

/**
  * This is an immutable formation of the propstate
  * The run function always return a PropsStateImmImpl
  * This ensures that whatever new state you have is an implementation of
  * the PropStateImmImple hence guarantee to have a state of its own.
  */
final case class PropsStateImmImpl(
    override val stateConfig: Option[PropsStateImm.State] = None
) extends PropsStateImm
    with LoggingAdapter { self =>

  type InnerState = PropsState.State

  def createState(
      commonProp: PropsStateImm.StateConfigImmi,
      specProps: PropsStateImm.StateConfigImmi
  ): PropsStateImm = PropsStateImm.State(commonProp, specProps)

  /**
    * The basic unpure part of the code
    */
//  private def updateState(
//    commonPropNew: PropsState.StateConfig,
//    specPropsNew: PropsState.StateConfig
//  ): Unit = {
//    val stateCommonProps =
//      self.stateManager.state.map(_.commProps).getOrElse(ConfigProperties.empty)
//    val stateSpecProps =
//      self.stateManager.state.map(_.commProps).getOrElse(ConfigProperties.empty)
//    (commonPropNew, specPropsNew) match {
//      case (x, y) => {
//        if (!x.isEmpty && !y.isEmpty) {
//          val updatedState = PropsState.State(x, y)
//          stateManager = self.stateManager.copy(state = updatedState.some)
//        } else if (x.isEmpty && y.isEmpty) {
//          () // No change is requeired
//        } else if (x.isEmpty) {
//          val updatedState =
//            PropsState.State(stateCommonProps, y)
//          stateManager = self.stateManager.copy(state = updatedState.some)
//        } else if (y.isEmpty) {
//          val updatedState =
//            PropsState.State(x, stateSpecProps)
//          stateManager = self.stateManager.copy(state = updatedState.some)
//        } else {
//          () // This means state is not defined hence go home
//        }
//      }
//    }
//  }
//  /**
//    * Should add properties in another state to a current state
//    * Should return a new State
//    * @param other
//    * @return
//    */
//  def addAllIfNotExists(other: StateWithInterpreter): StateWithInterpreter
//
//  /**
//    * Should add properties in another Properties to a current state
//    * Should return a new State
//    * @param other
//    * @return
//    */
//  def addAllIfNotExists(other: PropertyIntepreter): StateWithInterpreter
//
//  /**
//    * Override properties in current state with properties in another state
//    * @param other
//    * @return
//    */
//  def overrideWith(other: StateWithInterpreter): StateWithInterpreter
//
//  /**
//    * Override properties in current property bucket with properties in another state
//    * @param other
//    * @return
//    */
//  def overrideWith(other: PropertyIntepreter): StateWithInterpreter

}

object PropsStateImm {

  def apply(): PropsStateImm = PropsStateImmImpl()

  def apply(someConfig: State): PropsStateImm =
    PropsStateImmImpl(someConfig.some)

  type StateConfigImmi = ConfigProperties[String, String]

  case class AddAll(other: PropsStateImm) extends PropsStateImm
  case class AddAllP(properties: StateConfigImmi) extends PropsStateImm
  case class State(
      commProps: StateConfigImmi,
      specProps: StateConfigImmi
  ) extends PropsStateImm
}
