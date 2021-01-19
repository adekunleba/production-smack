package com.babs.denegee.api.configuration

import cats.syntax.all._
import com.babs.denegee.api.configuration.PropsState.StateConfig
import com.babs.denegee.common.logging.LoggingAdapter

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
trait PropsState extends Serializable with LoggingAdapter { self =>

  val propLogger = logger

  private case class StateManager(state: Option[InnerState])

  private var stateManager = StateManager(None)

  type InnerState = PropsState.State

  implicit private class Interpreter(state: PropsState) {
    def run: PropsState =
      state match {
        case PropsState.AddAll(other) => {
          stateManager = stateManager.copy(state = other.stateManager.state)
          self
        }
        case PropsState.AddAllP(prop) => {
          logger.info("Running addition to data update")
          updateState(ConfigProperties.empty, prop)
          self
        }
        case PropsState.State(common, spec) => {
          updateState(common, spec) //Mutating State
          self
        }
      }
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
    val stateCommonProps =
      self.stateManager.state.map(_.commProps).getOrElse(ConfigProperties.empty)
    val stateSpecProps =
      self.stateManager.state.map(_.commProps).getOrElse(ConfigProperties.empty)
    (commonPropNew, specPropsNew) match {
      case (x, y) => {
        if (!x.isEmpty && !y.isEmpty) {
          val updatedState = PropsState.State(x, y)
          stateManager = self.stateManager.copy(state = updatedState.some)
        } else if (x.isEmpty && y.isEmpty) {
          () // No change is requeired
        } else if (x.isEmpty) {
          val updatedState =
            PropsState.State(stateCommonProps, y)
          stateManager = self.stateManager.copy(state = updatedState.some)
        } else if (y.isEmpty) {
          val updatedState =
            PropsState.State(x, stateSpecProps)
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
  def addAll(other: PropsState): PropsState = PropsState.AddAll(other).run

  /**
    * Add properties to this state should return new state
    * @param properties
    * @return
    */
  def addAllP(properties: PropsState.StateConfig): PropsState =
    PropsState.AddAllP(properties).run

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
