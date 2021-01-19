package com.babs.denegee.api.broker.denegee_scope

import java.util.UUID

sealed abstract class GlobalScopeTypes(defaultId: String,
                                       parent: Set[GlobalScopeTypes])

case class Global(private val defaultId: String = "global")
    extends GlobalScopeTypes(defaultId, Set.empty[GlobalScopeTypes])
case class Instance(private val defaultId: String = "instance")
    extends GlobalScopeTypes(defaultId, Set(Global()))

/** Generating random job Id if jobId is not provided here
  * I don't know if it is a good deal but for now let's move
  */
case class Job(private val defaultId: Option[String] = None)
    extends GlobalScopeTypes(
      defaultId.getOrElse({ UUID.randomUUID().toString }),
      Set(Instance()))

case class Container(private val defaultId: String = "container")
    extends GlobalScopeTypes(defaultId, Set(Instance()))

case class MultiTaskAttempt(private val defaultId: String = "multiTask")
    extends GlobalScopeTypes(defaultId, Set(Job(), Container()))

case class Task(private val defaultId: Option[String] = None)
    extends GlobalScopeTypes(
      defaultId.getOrElse(UUID.randomUUID().toString),
      Set(MultiTaskAttempt())
    )

object GlobalScopeTypes {

  def localScopes: Seq[GlobalScopeTypes] =
    Set(Task(), MultiTaskAttempt(), Container()).toSeq
}
