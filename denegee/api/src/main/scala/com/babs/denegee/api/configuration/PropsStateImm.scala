package com.babs.denegee.api.configuration

import cats.syntax.all._
import com.babs.denegee.common.logging.LoggingAdapter

/**
  * Immutable format of Property State
  */
sealed trait PropsStateImm extends Serializable with LoggingAdapter { self =>

  val propLogger = logger

//    type InnerState = PropsState.State

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

  private def mixProps(
    props1: PropsStateImm.State,
    props2: PropsStateImm.State
  ): PropsStateImm.State = {
    //Compare commong props
    val specDiff = props2.specProps.getprops -- props1.specProps.getprops.keySet
    specDiff.foreach({ case (k, v) => props1.specProps.setProperty(k, v) })
    val commonDiff = props2.commProps.getprops -- props1.commProps.getprops.keySet
    commonDiff.foreach({ case (k, v) => props1.commProps.setProperty(k, v) })
    PropsStateImm.State(props1.commProps, props1.specProps)
  }

  /**
    * IMPLEMENTER
    * TODO: We can do a tail recursion on impleter thoug
    * @param state
    */
  implicit private class Interpreter(state: PropsStateImm) {
    def run: PropsStateImm =
      state match {
        case PropsStateImm.AddAll(other) =>
          val config: PropsStateImm.State = {
            (for {
              thisProps <- stateConfig
              thatProps <- other.stateConfig
              //Compare the two props
            } yield mixProps(thisProps, thatProps))
              .getOrElse(PropsStateImm.State(other.commonProperties, other.specialProperties)) //There should be something to do here
          }
          PropsStateImm(config)
        case PropsStateImm.AddAllP(prop) =>
          val property = (for {
            p <- self.stateConfig
            _ = p.specProps.putAll(prop)
          } yield p).getOrElse({
            val newConf = ConfigProperties(prop)
            // Setting up an empty configuration here
            PropsStateImm.State(ConfigProperties.empty, newConf)
          })
          PropsStateImm(property)
        case x: PropsStateImm.State =>
          PropsStateImm(x)
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
  override val stateConfig: Option[PropsStateImm.State]
) extends PropsStateImm

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

object PropsStateImm {

  def apply(): PropsStateImm = PropsStateImmImpl(None)

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
