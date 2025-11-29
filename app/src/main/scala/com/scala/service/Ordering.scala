package com.scala.service

import com.scala.SQLQueries.Queries
import com.scala.logger.Logg
import com.scala.model.Orders

import java.sql.{Connection}
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object Ordering extends Logg {

  def addBuyOrder(connection: Connection): Unit = {

    val buyOrder = Try {
      println("OrderId: ")
      val orderId = readLine().toInt

      println("UserId: ")
      val userId = readLine().toInt

      println("StockId: ")
      val stockId = readLine().toInt

      //BUY

      println("Quantity: ")
      val quantity = readLine().toInt

      println("Price: ")
      val price = readLine().toDouble

      val buyOrder = Orders(
        orderId,
        userId,
        stockId,
        "BUY",
        quantity,
        price
      )

      //returning order ...Try[order]
      buyOrder
    }

    buyOrder match {
      case Success(order) =>
        insertOrder(connection, order)
        tryMatch(connection, order)

      case Failure(e) => logger.error("Order input error", e)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------


  def addSellOrder(connection: Connection): Unit = {

    val sellOrder = Try {
      println("OrderId: ")
      val orderId = readLine().toInt

      println("UserId: ")
      val userId = readLine().toInt

      println("StockId: ")
      val stockId = readLine().toInt

      //SELL

      println("Quantity: ")
      val quantity = readLine().toInt

      println("Price: ")
      val price = readLine().toDouble

      val buyOrder = Orders(
        orderId,
        userId,
        stockId,
        "SELL",
        quantity,
        price
      )

      //returning order ...Try[order]
      buyOrder
    }

    sellOrder match {
      case Success(order) =>
        insertOrder(connection, order)
        tryMatch(connection, order)

      case Failure(e) => logger.error("Order input error", e)
    }
  }

  //--------------------------------------------------------------------------------------------------------------------


  private def insertOrder(conn: Connection, party: Orders): Unit = {

    Try {
      val ps = conn.prepareStatement(Queries.INSERT_INTO_ORDER)
      ps.setInt(1, party.userId)
      ps.setInt(2, party.stockId)
      ps.setString(3, party.buyOrSell)
      ps.setInt(4, party.quantity)
      ps.setDouble(5, party.price)
      ps.setString(6, "Available")

      ps.executeUpdate()
      ps.close()
      logger.info("Order saved.")
    }.recover {
      case e: Exception => logger.error("Failed to Insert Order!", e)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------


  private def tryMatch(conn: Connection, party: Orders): Unit = {

    val update = Try {
      val sql =
        if (party.buyOrSell == "BUY") Queries.SELECT_WHERE_SELL
        else Queries.SELECT_WHERE_BUY

      val preparedStatement = conn.prepareStatement(sql)
      preparedStatement.setInt(1, party.stockId)
      preparedStatement.setDouble(2, party.price)

      val rs = preparedStatement.executeQuery()

      if (rs.next()) {
        val counterPartyOrderId = rs.getInt("OrderId")
        val counterPartyOrderQuantity = rs.getInt("Quantity")
        val tradedQty = Math.min(party.quantity, counterPartyOrderQuantity)


        Try {
          updateOrders(conn, party, counterPartyOrderId, tradedQty)
        } match {
          case Success(_) =>
            insertTrade(conn, party, counterPartyOrderId, tradedQty)
        }

      } else {
        logger.error("No matching order found!!")
      }
      rs.close()
      preparedStatement.close()
    }

    update match {
      case Success(_) => logger.info("matching completed successfully")
      case Failure(e) => logger.error("Error while matching order", e)
    }
  }

  //--------------------------------------------------------------------------------------------------------------------

  private def updateOrders(conn: Connection, party: Orders, counterPartyOrderId: Int, tradedQty: Int): Unit = {

    var count = 0

    val tCount = Try {
      if (party.buyOrSell == "BUY") {
        val preparedStatement1 = conn.prepareStatement(Queries.UPDATE_ORDER_PARTY)
        preparedStatement1.setInt(1, party.quantity + tradedQty)
        preparedStatement1.setString(2, "Completed")
        preparedStatement1.setInt(3, party.orderId)
        preparedStatement1.executeUpdate()
        count += 1

        val preparedStatement2 = conn.prepareStatement(Queries.UPDATE_ORDER_COUNTERPARTY)
        preparedStatement2.setInt(1, party.quantity - tradedQty)
        preparedStatement2.setString(2, "Full/Part")
        preparedStatement2.setInt(3, counterPartyOrderId)
        preparedStatement2.executeUpdate()
        count += 1

      }
      else if (party.buyOrSell == "SELL") {
        val preparedStatement1 = conn.prepareStatement(Queries.UPDATE_ORDER_PARTY)
        preparedStatement1.setInt(1, party.quantity - tradedQty)
        preparedStatement1.setString(2, "Completed")
        preparedStatement1.setInt(3, party.orderId)
        preparedStatement1.executeUpdate()
        count += 1

        val preparedStatement2 = conn.prepareStatement(Queries.UPDATE_ORDER_COUNTERPARTY)
        preparedStatement2.setInt(1, party.quantity + tradedQty)
        preparedStatement2.setString(2, "Full/Part")
        preparedStatement2.setInt(3, counterPartyOrderId)
        preparedStatement2.executeUpdate()
        count += 1


      } else {
        logger.info("Something went wrong!!!!!!")
      }

      count
    }

    tCount match {

      case Failure(e) => logger.error("Failed to update rows in Order table!")
    }
  }


  //--------------------------------------------------------------------------------------------------------------------

  private def insertTrade(connection: Connection, party: Orders, counterPartyOrderId: Int, tradedQty: Int): Unit = {


    val preparedStatement = connection.prepareStatement(Queries.INSERT_INTO_TRADE)
    //tradeId
    preparedStatement.setInt(1, party.orderId)
    preparedStatement.setInt(2, counterPartyOrderId)
    preparedStatement.setInt(3, party.stockId)
    preparedStatement.setDouble(4, party.price)
    preparedStatement.setInt(5, tradedQty)
    preparedStatement.executeUpdate()
    preparedStatement.close()

    logger.info("Trade inserted into Trade table.")

  }

}
