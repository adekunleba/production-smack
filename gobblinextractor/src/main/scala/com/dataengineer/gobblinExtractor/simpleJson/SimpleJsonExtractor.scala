package com.dataengineer.gobblinExtractor.simpleJson

import java.io.{BufferedReader, InputStreamReader}

import org.apache.gobblin.configuration.{ConfigurationKeys, WorkUnitState}
import org.apache.gobblin.source.extractor.Extractor
import org.apache.commons.vfs2.{FileObject, FileSystemOptions, VFS}
import org.apache.commons.vfs2.auth.StaticUserAuthenticator
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder
import org.apache.gobblin.password.PasswordManager
import resource._

class SimpleJsonExtractor(workUnitState: WorkUnitState)
    extends Extractor[String, String] {

  private val SOURCE_FILE_KEY = "source.file"

  // Return needed file object
  private def resolveFile(): FileObject = {
    if (workUnitState.getPropAsBoolean(
          ConfigurationKeys.SOURCE_CONN_USE_AUTHENTICATION,
          false
        )) {
      val auth = new StaticUserAuthenticator(
        workUnitState.getProp(ConfigurationKeys.SOURCE_CONN_DOMAIN, ""),
        workUnitState.getProp(ConfigurationKeys.SOURCE_CONN_USERNAME),
        PasswordManager
          .getInstance(workUnitState)
          .readPassword(
            workUnitState.getProp(ConfigurationKeys.SOURCE_CONN_PASSWORD)
          )
      )
      val opts = new FileSystemOptions()
      DefaultFileSystemConfigBuilder
        .getInstance()
        .setUserAuthenticator(opts, auth)
      VFS.getManager.resolveFile(workUnitState.getProp(SOURCE_FILE_KEY), opts)
    } else
      VFS.getManager.resolveFile(workUnitState.getProp(SOURCE_FILE_KEY))
  }

  def getSchema: String =
    workUnitState.getProp(ConfigurationKeys.SOURCE_SCHEMA)

  def getExpectedRecordCount: Long = 0

  def getHighWatermark: Long = 0

  //TODO: Implement closer and ensure filestream are closed
  def close(): Unit = {}

  override def readRecord(reuse: String): String = {
    val vfs = resolveFile()
    managed(
      new BufferedReader(
        new InputStreamReader(
          vfs.getContent.getInputStream(),
          ConfigurationKeys.DEFAULT_CHARSET_ENCODING
        )
      )
    ).map(
        x => Stream.continually(x.readLine()).takeWhile(_ != null).mkString(" ")
      )
      .opt
      .getOrElse("")
  }
}

object SimpleJsonExtractor {
  def apply(workUnitState: WorkUnitState): SimpleJsonExtractor =
    new SimpleJsonExtractor(workUnitState)
}
