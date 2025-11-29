package com.scala.service

import com.scala.SQLQueries.Queries
import com.scala.logger.Logg

import java.sql.Connection
import scala.util.Try

object Book extends Logg {

  def showOrderBook(conn: Connection): Unit = {

    println("========================ORDER BOOK=======================")

    Try {
      val orderBook = Queries.SELECT_ORDERBOOK
      val buyResultSet = conn.createStatement().executeQuery(orderBook)

      while (buyResultSet.next()) {
        println(s"${buyResultSet.getInt("OrderId")} | ${buyResultSet.getString("Company")} | ${buyResultSet.getInt("Remaining_Quantity")} | ${buyResultSet.getDouble("Price")}")

      }
    }.recover {
      case e: Exception => logger.error("Failed to Load OrderBook!")
    }
  }


  def showTradeBook(conn: Connection): Unit = {

    println("=========================TRADE BOOK========================")

    Try {
      val tradeBook = Queries.SELECT_TRADEBOOK
      val tradeResultSet = conn.createStatement.executeQuery(tradeBook)

      while (tradeResultSet.next()) {
        println(s"BuyOrderId: ${tradeResultSet.getInt("buyOrderId")} SellOrderId: ${tradeResultSet.getInt("sellOrderId")} Quantities: ${tradeResultSet.getInt("quantity")} StockPrice: ${tradeResultSet.getDouble("price")}")
      }
    }.recover {
      case e: Exception => logger.error("Failed to load TradeBook!")
    }
  }
}
