package com.scala.app

import com.scala.logger.Logg
import com.scala.service.{Book, Ordering}

import java.sql.Connection
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object Dashboard extends Logg{

  def showDashboard(conn: Connection): Unit = {

    var continue = true

    while (continue) {

      println("1. Buy Order")
      println("2. Sell Order")
      println("3. See OrderBook")
      println("4. See Completed Trades")
      println("5. LogOut")

      val choiceTry = Try {
        readLine("Enter your choice: ").toInt
      }

      choiceTry match {

        case Success(1) => logger.info("Into the Buy Order...")
          Ordering.addOrder(conn)

        case Success(2) => logger.info("Into the Sell Order...")
          Ordering.addOrder(conn)

        case Success(3) => logger.info("Showing Existing Orders...")
          Book.showOrderBook(conn)

        case Success(4) => logger.info("Showing Completed Trades...")
          Book.showTradeBook(conn)

        case Success(5) => logger.info("LogOut successfully.")
          continue = false

        case _ => logger.error("Invalid Option!")

      }
    }
  }
}
