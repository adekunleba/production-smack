package com.babs.denegee.api.configuration

import cats.syntax.all._

import com.babs.denegee.common.logging.LoggingAdapter

/**
  * Immutable format of Property State
  */
sealed trait PropsStateImm extends Serializable with LoggingAdapter { self =>
  import PropsStateImm.StateConfigImm
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
  def addAllP(properties: StateConfigImm): PropsStateImm =
    PropsStateImm.AddAllP(properties).run

  def addAllP(
      commonProps: StateConfigImm,
      specProps: StateConfigImm
  ): PropsStateImm =
    PropsStateImm.State(commonProps, specProps).run

  def commonProperties: StateConfigImm = {
    val commonProps = self.stateConfig.map(_.commProps)
    if (commonProps.isDefined) {
      commonProps.get
    } else {
      ConfigProperties.empty
    }
  }

  def specialProperties: StateConfigImm = {
    val specProps = self.stateConfig.map(_.specProps)
    if (specProps.isDefined) {
      specProps.get
    } else {
      ConfigProperties.empty
    }
  }

  private def mixProps(
      thisState: PropsStateImm.State,
      thatState: PropsStateImm.State
  ): PropsStateImm.State = {
    //Compare commong props
    val specDiff = thatState.specProps.getprops -- thisState.specProps.getprops.keySet
    specDiff.foreach({ case (k, v) => thisState.specProps.setProperty(k, v) })
    val commonDiff = thatState.commProps.getprops -- thisState.commProps.getprops.keySet
    commonDiff.foreach({ case (k, v) => thisState.commProps.setProperty(k, v) })
    PropsStateImm.State(thisState.commProps, thisState.specProps)
  }

  /**
    * Return underlying ConfigProperties
    */
  def getProperties: StateConfigImm =
    ConfigProperties(
      self.specialProperties.getprops ++ self.commonProperties.getprops)

  /**
    * Should add properties in another Properties to a current state
    * Should return a new State
    * @param other
    * @return
    */
  def addAllIfNotExists(other: StateConfigImm): PropsStateImm = {
    val diffProps: StateConfigImm = (for {
      props <- stateConfig
      specDiff = other.getprops -- props.specProps.getprops.keySet
      commDiff = other.getprops -- props.commProps.getprops.keySet
    } yield ConfigProperties(specDiff ++ commDiff)).getOrElse(other)
    PropsStateImm.AddAllP(diffProps).run
  }

  /**
    * Should add properties in another state to a current state
    * Should return a new State
    * @param other
    * @return
    */
  def addAllIfNotExists(other: PropsStateImm): PropsStateImm = {
    addAllIfNotExists(other.specialProperties)
    addAllIfNotExists(other.commonProperties)
  }

  /**
    * Override properties in current property bucket with properties in another state
    * @param other
    * @return
    */
  def overrideWith(other: StateConfigImm): PropsStateImm =
    PropsStateImm.Override(other).run

  /**
    * Override properties in current state with properties in another state
    * @param other
    * @return
    */
  def overrideWith(other: PropsStateImm): PropsStateImm = {
    overrideWith(other.specialProperties)
    overrideWith(other.commonProperties)
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
              .getOrElse(PropsStateImm.State(
                other.commonProperties,
                other.specialProperties)) //There should be something to do here
          }
          PropsStateImm(config)
        case PropsStateImm.AddAllP(prop) =>
          val property = (for {
            p <- self.stateConfig
            _ = p.specProps.putAll(prop)
          } yield p).getOrElse({
            PropsStateImm.State(ConfigProperties.empty, prop)
          })
          PropsStateImm(property)
        case PropsStateImm.Override(other) =>
          val updatedConfig: PropsStateImm.State = (for {
            props <- self.stateConfig
            commSpec = other.getprops.keySet.intersect(
              props.specProps.getprops.keySet)
            commComm = other.getprops.keySet.intersect(
              props.commProps.getprops.keySet)
            commons = commSpec ++ commComm
            //TODO: BUG - IF property in common property and you try to add
            // It discards the property -- is this the intended way??
            // It has to be explicit at least
            _ = commons.foreach({ x =>
              props.specProps.setProperty(x, other.getProperty(x).get)
            })
          } yield PropsStateImm.State(props.commProps, props.specProps))
            .getOrElse(PropsStateImm.State(ConfigProperties.empty, other))
          PropsStateImm(updatedConfig)
        case x: PropsStateImm.State => {
          // This should mix configuration from other state with this
          val state = (for {
            thisState <- stateConfig
          } yield mixProps(thisState, x)).getOrElse(x)
          PropsStateImm(state)
        }
        case _ => self
      }
  }
}

object PropsStateImm {

  def apply(): PropsStateImm = PropsStateImmImpl(None)

  private def apply(someConfig: State): PropsStateImm =
    PropsStateImmImpl(someConfig.some)

  def apply(commonProps: StateConfigImm,
            specialProps: StateConfigImm): PropsStateImm =
    PropsStateImm(State(commonProps, specialProps))

  type StateConfigImm = ConfigProperties[String, String]

  case class AddAll(other: PropsStateImm) extends PropsStateImm
  case class AddAllP(properties: StateConfigImm) extends PropsStateImm
  case class State(
      commProps: StateConfigImm,
      specProps: StateConfigImm
  ) extends PropsStateImm

  case class Override(prop: StateConfigImm) extends PropsStateImm

  /**
    * This is an immutable formation of the propstate
    * The run function always return a PropsStateImmImpl
    * This ensures that whatever new state you have is an implementation of
    * the PropStateImmImple hence guarantee to have a state of its own.
    */
  final case class PropsStateImmImpl(
      override val stateConfig: Option[PropsStateImm.State]
  ) extends PropsStateImm
}

/**
  * This is the manager for any state outside here
  */
trait StateManager extends PropsStateImm
