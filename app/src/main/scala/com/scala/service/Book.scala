package com.scala.service

import com.scala.SQLQueries.Queries
import com.scala.logger.Logg

import java.sql.Connection
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object Book extends Logg {

  def showOrderBook(conn: Connection): Unit = {

    println("========================ORDER BOOK=======================")

    Try {
      val orderBook = Queries.SELECT_ORDERBOOK
      val orderResultSet = conn.createStatement().executeQuery(orderBook)

      while (orderResultSet.next()) {
        println(s"OrderId: ${orderResultSet.getInt("OrderId")} UserId: ${orderResultSet.getInt("UserId")} StockId: ${orderResultSet.getInt("StockId")} OrderType: ${orderResultSet.getString("OrderType")} Quantity: ${orderResultSet.getInt("Quantity")} Price: ${orderResultSet.getDouble("Price")}  IsAvailable: ${orderResultSet.getString("IsAvailable")}")

      }
    }.recover {
      case e: Exception => logger.error("Failed to Load OrderBook!", e)
    }
  }


  def showTradeBook(conn: Connection): Unit = {

    println("=========================TRADE BOOK========================")

    Try {
      val tradeBook = Queries.SELECT_TRADEBOOK
      val tradeResultSet = conn.createStatement.executeQuery(tradeBook)

      while (tradeResultSet.next()) {
        println(s"TradeId: ${tradeResultSet.getInt("TradeId")} BuyOrderId: ${tradeResultSet.getInt("BuyOrderId")} SellOrderId: ${tradeResultSet.getInt("SellOrderId")} StockId: ${tradeResultSet.getInt("StockId")} Price: ${tradeResultSet.getDouble("Price")} Quantity: ${tradeResultSet.getInt("Quantity")}")
      }

    }.recover {
      case _: Exception => logger.error("Failed to load TradeBook!", _)
    }
  }


  def showHoldings(conn: Connection): Unit = {

    println("=========================HOLDING BOOK==========================")

    println("Enter UserId you want to check")
    val userId = readLine().toInt

    val result = Try {
      val holdingPs = conn.prepareStatement(Queries.SELECT_USER_HOLDING)
      holdingPs.setInt(1, userId)

      val holdingResult = holdingPs.executeQuery()

      var hasData = false

      while (holdingResult.next()) {
        hasData = true
        println(s"UserId: ${holdingResult.getInt(1)}, Username: ${holdingResult.getString(2)}, StockId: ${holdingResult.getInt(3)}, Quantity: ${holdingResult.getInt(4)}")
      }

      if (!hasData) {
        println("No holding found.")
      }

      holdingResult.close()
      holdingPs.close()
    }

    result match {
      case Success(_) => logger.info("Holding displayed Successfully.")
      case Failure(e) => logger.error("Failed to fetch holdings", e)
    }
  }


  def showStocks(conn: Connection): Unit = {

    println("=============================STOCK BOOK================================")

    
    val statement = conn.createStatement()
    val stockResultSet = statement.executeQuery(Queries.SELECT_STOCKBOOK)

    while(stockResultSet.next()){
      println(s"StockId: ${stockResultSet.getInt("StockId")} Symbol: ${stockResultSet.getString("Symbol")} ${stockResultSet.getString("CompanyName")} ${stockResultSet.getString("isActive")}")
    }

    stockResultSet.close()
    statement.close()
  }

  def showUsers(conn: Connection): Unit = {

    println("=============================USERS=================================")

    val userResultSet =  conn.createStatement().executeQuery(Queries.SELECT_USERBOOK)

    while(userResultSet.next()){
      println(s"UserId: ${userResultSet.getInt("UserId")} UserName: ${userResultSet.getString("UserPassword")}")
    }

    userResultSet.close()
  }
}

