package com.babs.denegee.api.configuration

import cats.syntax.all._
import com.babs.denegee.api.configuration.PropsState.StateConfig

/**
  * Think about state as the holder of a State's configuration
  * We are going to be using appliction.conf to manage State here
  *
  * ON THE ONE NOTE I was thinking
  *   This is an alias for Properties until we figure out if to use Typeconfig
  *   Property needs to be a property bucket
  * BUT ON ANOTHER NOTE
  *   Use the normal properties approach
  *   Hopefully we come and reconfigure this
  *   Also start from the state test
  * TODO: Having implemented Property state as is from Jave. Consider using Interpreter to do
  *       the same as shown in the TIO project
  *
  *
  */
trait PropsState extends Serializable { self =>

  private case class StateManager(state: Option[InnerState])

  private var stateManager = StateManager(None)

  type InnerState = PropsState.State

  implicit class Interpreter(state: PropsState) {
    def run: PropsState =
      state match {
        case PropsState.AddAll(other) => {
          stateManager = stateManager.copy(state = other.stateManager.state)
          self
        }
        case PropsState.AddAllP(prop) => {
          updateState(ConfigProperties.empty, prop)
          self
        }
        case PropsState.State(common, spec) => {
          updateState(common, spec) //Mutating State
          self
        }
      }
    state.run
  }

  def createState(
      commonProp: PropsState.StateConfig,
      specProps: PropsState.StateConfig
  ): PropsState = PropsState.State(commonProp, specProps)

  /**
    * The basic unpure part of the code
    */
  private def updateState(
      commonPropNew: PropsState.StateConfig,
      specPropsNew: PropsState.StateConfig
  ): Unit = {
    val isStateDefined = self.stateManager.state.isDefined
    (commonPropNew, specPropsNew) match {
      case (x, y) => {
        if (!x.isEmpty && !y.isEmpty) {
          val updatedState = PropsState.State(x, y)
          stateManager = self.stateManager.copy(state = updatedState.some)
        } else if (x.isEmpty && y.isEmpty) {
          () // No change is requeired
        } else if (x.isEmpty && isStateDefined) {
          val updatedState =
            PropsState.State(self.stateManager.state.map(_.commProps).get, y)
          stateManager = self.stateManager.copy(state = updatedState.some)
        } else if (y.isEmpty && isStateDefined) {
          val updatedState =
            PropsState.State(x, self.stateManager.state.map(_.specProps).get)
          stateManager = self.stateManager.copy(state = updatedState.some)
        } else {
          () // This means state is not defined hence go home
        }
      }
    }
  }

  /**
    * Should populate instance of current state with another state.
    * Should return a new populated state
    *
    * @param other
    * @return
    */
  def addAll(other: PropsState): PropsState = PropsState.AddAll(other)

  /**
    * Add properties to this state should return new state
    * @param properties
    * @return
    */
//  def addAll(properties: PropsState.StateConfig): PropsState =
//    PropsState.AddAllP(properties.some).run
//
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

  def commonProperties: StateConfig = {
    val commonProps = self.stateManager.state.map(_.commProps)
    if (commonProps.isDefined) {
      commonProps.get
    } else {
      ConfigProperties.empty
    }
  }

  def specialProperties: StateConfig = {
    val specProps = self.stateManager.state.map(_.specProps)
    if (specProps.isDefined) {
      specProps.get
    } else {
      ConfigProperties.empty
    }
  }

}

object PropsState {

  def apply(): PropsState = new PropsState {}
  type StateConfig = ConfigProperties[String, String]

  case class AddAll(other: PropsState) extends PropsState
  case class AddAllP(properties: StateConfig) extends PropsState
  case class State(
      commProps: StateConfig,
      specProps: StateConfig
  ) extends PropsState
}
