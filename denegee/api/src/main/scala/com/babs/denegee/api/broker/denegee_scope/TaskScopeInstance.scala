package com.babs.denegee.api.broker.denegee_scope

/**
  * The hashcode and equals should be the same  irrespective of it extending DenegeeScope
  * @param taskId
  */
case class TaskScopeInstance(taskId: String) extends DenegeeScopeInstance {
  override def scopeTypesBase: DenegeeScopeTypesV2 = DenegeeScopeTypesV2.Task

  override def scopeIdValue: String = taskId
}
