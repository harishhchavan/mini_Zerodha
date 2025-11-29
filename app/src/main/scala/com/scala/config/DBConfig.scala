package com.scala.config

import com.typesafe.config.ConfigFactory

import java.sql.{Connection, DriverManager, SQLException}
import scala.util.Try

object DBConfig extends DBConfigAbstract{

  private val config =
    Try(ConfigFactory.load().getConfig("database"))
      .getOrElse(throw new RuntimeException("Database config not found"))

  def getUrl: String = config.getString("url")
  def getUser: String = config.getString("user")
  def getPassword: String = config.getString("password")


  def getConnection: Try[Connection] = {
    val connTry = Try(DriverManager.getConnection(getUrl, getUser, getPassword))
    connTry
  }
}
