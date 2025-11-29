package com.scala.SQLQueries

object Queries {

  val SELECT_ORDERBOOK = "SELECT OrderId, UserId, StockId, OrderType, Quantity, Price, isAvailable FROM ORDERS"

  val SELECT_TRADEBOOK = "SELECT TradeId, BuyOrderId, SellOrderId, StockId, Price, Quantity FROM TRADES"

  val SELECT_STOCKBOOK = "SELECT StockId, Symbol, CompanyName, isActive FROM STOCKS"

  val SELECT_USERBOOK = "SELECT UserId, UserName, UserPassword FROM USERS"
  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val INSERT_INTO_ORDER = "INSERT INTO ORDERS(UserId, StockId, OrderType, Quantity, Price, isAvailable) VALUES (?, ?, ?, ?, ?, ?)"

  val INSERT_INTO_TRADE = "INSERT INTO TRADES(BuyOrderId, SellOrderId, StockId, Price, Quantity) VALUES (?, ?, ?, ?, ?)"

  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val SELECT_WHERE_SELL = "SELECT * FROM ORDERS WHERE StockId = ? AND OrderType = 'SELL' AND Price <= ? ORDER BY Price"

  val SELECT_WHERE_BUY = "SELECT * FROM ORDERS WHERE StockId = ? AND OrderType = 'BUY' AND Price >= ? ORDER BY Price DESC"

  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val UPDATE_ORDER_PARTY = "UPDATE ORDERS SET Quantity =  ? AND isAvailable = ? WHERE OrderId = ?"

  val UPDATE_ORDER_COUNTERPARTY = "UPDATE ORDERS SET Quantity =  ? AND isAvailable = ? WHERE OrderId = ?"

  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val INSERT_USER_PASSWORD = "INSERT INTO USERS(UserName, UserPassword) VALUES(?, ?)"

  val SELECT_USER_FROM_USERS = "SELECT UserPassword FROM USERS WHERE UserName = ?"

  //-------------------------------------------------------------------------------------------------------------------------------------------------------

  val SELECT_USER_HOLDING = "SELECT UserId, UserName, StockId, Quantity FROM HOLDINGS WHERE UserId = ?"

}
