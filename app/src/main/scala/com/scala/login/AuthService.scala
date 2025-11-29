package com.scala.login

import com.scala.SQLQueries.Queries
import com.scala.app.Dashboard
import com.scala.logger.Logg

import java.sql.Connection
import scala.io.StdIn.readLine

object AuthService extends Logg {

  def signup(conn: Connection): Unit = {
    try {
      println("Do SIGNUP")
      println("Enter your Username")
      val username = readLine().trim()

      println("Enter your Password")
      val password = readLine().trim()

      val ps = conn.prepareStatement(Queries.INSERT_USER_PASSWORD)
      ps.setString(1, username)
      ps.setString(2, password)

      ps.executeUpdate()
      println("SignUp Successful.")

    } catch {
      case e: Exception => logger.error("SignUp Failed!", e)
    }
  }

  def login(conn: Connection): Unit = {

    try {
      println("DO LOGIN")
      println("Enter the Username: ")
      val username = readLine().toLowerCase().trim()

      val ps = conn.prepareStatement(Queries.SELECT_USER_FROM_USERS)
      ps.setString(1, username)
      val rs = ps.executeQuery()

      if (rs.next()) {
        val dbPassword = rs.getString("UserPassword")
        val dbUserName = rs.getString("UserName")

        println("Enter the Password: ")
        val password = readLine().trim()

        if (password == dbPassword) {
          logger.info("LOGIN SUCCESSFUL!")
          Dashboard.showDashboard(conn, dbUserName)
        }
        else {
          logger.error("Invalid Password!")
        }
      } else {
        println("You need to SIGNUP first!")
        signup(conn: Connection)
      }
    } catch {
      case e: Exception => logger.error("Login failed!")
    }
  }
}
