package com.scala.app

import com.scala.logger.Logg
import com.scala.service.{Book, Ordering}

import java.sql.Connection
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object Dashboard extends Logg{

  def showDashboard(conn: Connection, userName: String): Unit = {

    var continue = true

    while (continue) {

      println(s"HI $userName!")

      println("1. Buy Order")
      println("2. Sell Order")
      println("3. See current Holding")
      println("4. See OrderBook")
      println("5. See Completed Trades")
      println("6. See Stocks")
      println("7. See Users(Authorized... DON\"T CLICK THIS, this is for MANAGER only!!!)")
      println("8. LogOut")

      val choiceTry = Try {
        readLine("Enter your choice: ").toInt
      }

      choiceTry match {
        case Success(1) => logger.info("Into the Buy Order...")
          Ordering.addBuyOrder(conn)

        case Success(2) => logger.info("Into the Sell Order...")
          Ordering.addSellOrder(conn)

        case Success(3) => logger.info("Info Holdings...")
          Book.showHoldings(conn)

        case Success(4) => logger.info("Showing Existing Orders...")
          Book.showOrderBook(conn)

        case Success(5) => logger.info("Showing Completed Trades...")
          Book.showTradeBook(conn)

        case Success(6) => logger.info("Showing Stocks....")
          Book.showStocks(conn)

        case Success(7) => logger.info("Showing Users...")
          Book.showUsers(conn)

        case Success(8) => logger.info("LogOut successfully.")
          continue = false

        case _ => logger.error("Invalid Option!")

      }
    }
  }
}
