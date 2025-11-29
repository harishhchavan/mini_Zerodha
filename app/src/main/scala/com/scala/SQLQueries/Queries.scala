package com.scala.SQLQueries

object Queries {

  val SELECT_ORDERBOOK = "SELECT OrderId, Company, OrderType, Remaining_Quantity, Price FROM ORDERS WHERE Remaining_Quantity > 0 ORDER BY PRICE DESC"

  val SELECT_TRADEBOOK = "SELECT buyOrderId, sellOrderId, Company, price, Quantity FROM TRADES"

  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val INSERT_INTO_ORDER = "INSERT INTO ORDERS VALUES (?, ?, ?, ?, ?, ?)"

  val INSERT_INTO_TRADE = "INSERT INTO TRADES VALUES (?, ?, ?, ?, ?)"

  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val SELECT_WHERE_SELL = "SELECT * FROM ORDERS WHERE Company = ? AND OrderType = 'SELL' AND Price <= ? AND Remaining_Quantity > 0 ORDER BY Price"

  val SELECT_WHERE_BUY = "SELECT * FROM ORDERS WHERE Company = ? AND OrderType = 'BUY' AND Price >= ? AND Remaining_Quantity > 0 ORDER BY Price DESC"

  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val UPDATE_ORDER_REMAINING_QUANTITY = "UPDATE ORDERS SET Remaining_Quantity = Remaining_Quantity - ? WHERE OrderId = ?"


  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val INSERT_USER_PASSWORD = "INSERT INTO USERS(UserName, UserPassword) VALUES(?, ?)"

  val SELECT_USER_FROM_USERS = "SELECT UserPassword FROM USERS WHERE UserName = ?"

  //with USER RESULTSET, we can use getString()... for password aslso
  val SELECT_PASSWORD_FROM_USER = "SELECT UserPassword FROM USERS WHERE UserName = ?"

}
