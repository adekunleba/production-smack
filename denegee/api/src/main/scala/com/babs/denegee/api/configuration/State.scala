package com.babs.denegee.api.configuration

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
sealed trait PropertiesAlias
case object Empty extends PropertiesAlias
trait State {

  def specProperties: PropertiesAlias
  def commonProperties: PropertiesAlias

  /**
    * This should be mixture of both the specProperties and the commonProperties
    * @return
    */
  //TODO: Refactor to get both specProperties and common properties
  def allProperties: PropertiesAlias = specProperties

  /**
    * Should populate instance of current state with another state.
    * Should return a new populated state
    *
    * @param other
    * @return
    */
  def addAll(other: State): State

  /**
    * Add properties to this state should return new state
    * @param propertiesAlias
    * @return
    */
  def addAll(propertiesAlias: PropertiesAlias): State

  /**
    * Should add properties in another state to a current state
    * Should return a new State
    * @param other
    * @return
    */
  def addAllIfNotExists(other: State): State

  /**
    * Should add properties in another Properties to a current state
    * Should return a new State
    * @param other
    * @return
    */
  def addAllIfNotExists(other: PropertiesAlias): State

  /**
    * Override properties in current state with properties in another state
    * @param other
    * @return
    */
  def overrideWith(other: State): State

  /**
    * Override properties in current property bucket with properties in another state
    * @param other
    * @return
    */
  def overrideWith(other: PropertiesAlias): State

}

//object State {
//
//  /**
//    * Should be able to create an empty State
//    * @return
//    */
//  def apply(): State = new State {
//    override def specProperties: PropertiesAlias = Empty
//
//    override def commonProperties: PropertiesAlias = Empty
//  }
//
//  /**
//    * Should be able to create a spec Properties from a parsed properties
//    * @param propertiesAlias
//    */
//  def apply(propertiesAlias: PropertiesAlias): State = new State {
//    override def specProperties: PropertiesAlias = propertiesAlias
//
//    override def commonProperties: PropertiesAlias = Empty
//  }
//
//  /**
//    * Should be able to create a state from another state
//    * @param that
//    * @return
//    */
//  def apply(that: State): State = new State {
//
//    /**
//      * If you are adding special properties from other
//      * You should remove all common properties from it
//      * @return
//      */
//    override def specProperties: PropertiesAlias = that.specProperties
//
//    override def commonProperties: PropertiesAlias = that.commonProperties
//
//  }
//}
