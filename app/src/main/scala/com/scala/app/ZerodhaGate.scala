package com.scala.app

import com.scala.config.DBConfig
import com.scala.logger.Logg
import com.scala.login.AuthService

import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object ZerodhaGate extends App with Logg{

  val connTry = DBConfig.getConnection

  connTry match {
    case Success(conn) => {
      logger.info("Successfully Connected to Database.")

      var continue = true
      while (continue) {
        println("WELCOME TO ZERODHA!")
        println("1. SignUp")
        println("2. Login")
        println("3. Exit")

        val choice = Try {readLine("Choice: ").toInt}

        choice match {
          case Success(1) => AuthService.signup(conn)
          case Success(2) => AuthService.login(conn)
          case Success(3) => logger.info("Thank you for NOT using Zerodha.")
                              conn.close()
                              continue = false

          case _ => logger.error("Invalid input, try again!")
        }
      }
    }

    case Failure(e) =>
        logger.error("Failed to Connect to Database!")
  }

}
