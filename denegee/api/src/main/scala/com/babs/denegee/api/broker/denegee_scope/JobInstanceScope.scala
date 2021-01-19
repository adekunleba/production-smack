package com.babs.denegee.api.broker.denegee_scope

case class JobInstanceScope(jobId: String, jobName: String)
    extends DenegeeScopeInstance {
  override def scopeTypesBase: DenegeeScopeTypesV2 = DenegeeScopeTypesV2.Job

  override def scopeIdValue: String = jobName + ", id=" + jobId
}
