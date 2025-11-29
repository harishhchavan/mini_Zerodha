package com.scala.service

import com.scala.SQLQueries.Queries
import com.scala.logger.Logg
import com.scala.model.Orders

import java.sql.{Connection}
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object Ordering extends Logg {

  def addOrder(connection: Connection): Unit = {

    val order = Try {
      println("OrderId company BUY/SELL quantity price")
      val input = readLine().split(" ")

      require(input.length == 5, "Invalid order format")

      val order = Orders(
        input(0).toInt,
        input(1),
        input(2),
        input(3).toInt,
        input(3).toInt, //initially, same remaining Quantity
        input(4).toDouble
      )

      //returning order ...Try[order]
      order
    }

    order match {
      case Success(order) =>
        insertOrder(connection, order)
        tryMatch(connection, order)

      case Failure(e) => logger.error("Order input error", e)
    }
  }


//--------------------------------------------------------------------------------------------------------------------

  private def insertOrder(conn: Connection, order: Orders): Unit = {
    Try {
      val ps = conn.prepareStatement(Queries.INSERT_INTO_ORDER)
      ps.setInt(1, order.orderId)
      ps.setString(2, order.company)
      ps.setString(3, order.orderType)
      ps.setInt(4, order.quantity)
      ps.setInt(5, order.rem_Quantity) //initial quantity
      ps.setDouble(6, order.price)

      ps.executeUpdate()
      ps.close()
      logger.info("Order saved.")
    }.recover {
      case e: Exception => logger.error("Failed to Insert Order!", e)
    }

  }


  //--------------------------------------------------------------------------------------------------------------------


  private def tryMatch(conn: Connection, incoming: Orders): Unit = {

    val update = Try {
      val sql =
        if (incoming.orderType == "BUY") Queries.SELECT_WHERE_SELL
        else Queries.SELECT_WHERE_BUY

      val preparedStatement = conn.prepareStatement(sql)
      preparedStatement.setString(1, incoming.company)
      preparedStatement.setDouble(2, incoming.price)

      val rs = preparedStatement.executeQuery()

      if (rs.next()) {
        val matchedOrderId = rs.getInt("OrderId")
        val matchedPrice = rs.getDouble("Price")
        val matchedQty = rs.getInt("Remaining_Quantity")

        val used_qty = Math.min(incoming.quantity, matchedQty)

        insertTrade(conn, incoming, matchedOrderId, matchedPrice, used_qty)

        updateOrders(conn, matchedOrderId, matchedQty-used_qty)
        updateOrders(conn, incoming.orderId, incoming.quantity - used_qty)

        logger.info(s"Trade executed for quantity = $used_qty")

      } else {
         logger.error("No matching order found!!")
      }
      rs.close()
      preparedStatement.close()
    }

    update match{
      case Success(_) => logger.info("matching completed successfuly")

      case Failure(e) => logger.error("Error while matching order",e)

    }
  }


  //--------------------------------------------------------------------------------------------------------------------

  private def insertTrade(connection: Connection, incoming: Orders, matchedOrderId: Int, price: Double, used_qty: Int): Unit = {

    val (buyId, sellId) = if (incoming.orderType == "BUY") (incoming.orderId, matchedOrderId)
                          else(matchedOrderId, incoming.orderId)

    val preparedStatement = connection.prepareStatement(Queries.INSERT_INTO_TRADE)
    preparedStatement.setInt(1, buyId)
    preparedStatement.setInt(2, sellId)
    preparedStatement.setString(3, incoming.company)
    preparedStatement.setDouble(4, price)
    preparedStatement.setInt(5, used_qty)
    preparedStatement.executeUpdate()
    preparedStatement.close()

    logger.info("Trade inserted into Trade table.")

    updateOrders(connection, incoming.orderId, used_qty)
    updateOrders(connection, matchedOrderId, used_qty)
  }


  //--------------------------------------------------------------------------------------------------------------------

 private def updateOrders(conn: Connection, orderId: Int, used_qty: Int ): Unit = {

   Try {
     val preparedStatement = conn.prepareStatement(Queries.UPDATE_ORDER_REMAINING_QUANTITY)
     preparedStatement.setInt(1, used_qty)
     preparedStatement.setInt(2, orderId)
     preparedStatement.executeUpdate()

   }.recover {
     case e => logger.error("Remaining quantities update failed", e)
   }
 }
}
