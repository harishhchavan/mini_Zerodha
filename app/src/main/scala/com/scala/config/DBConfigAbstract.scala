package com.scala.config

import com.typesafe.config.ConfigFactory
import java.sql.Connection
import scala.util.Try

trait DBConfigAbstract {

  protected def getUrl : String
  protected def getUser : String
  protected def getPassword : String

  def getConnection: Try[Connection]
}
