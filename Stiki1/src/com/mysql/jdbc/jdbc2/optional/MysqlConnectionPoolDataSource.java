/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.ConnectionPoolDataSource;
/*    */ import javax.sql.PooledConnection;
/*    */ 
/*    */ public class MysqlConnectionPoolDataSource extends MysqlDataSource
/*    */   implements ConnectionPoolDataSource
/*    */ {
/*    */   public synchronized PooledConnection getPooledConnection()
/*    */     throws SQLException
/*    */   {
/* 61 */     java.sql.Connection connection = getConnection();
/* 62 */     MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((com.mysql.jdbc.Connection)connection);
/*    */ 
/* 65 */     return mysqlPooledConnection;
/*    */   }
/*    */ 
/*    */   public synchronized PooledConnection getPooledConnection(String s, String s1)
/*    */     throws SQLException
/*    */   {
/* 82 */     java.sql.Connection connection = getConnection(s, s1);
/* 83 */     MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((com.mysql.jdbc.Connection)connection);
/*    */ 
/* 86 */     return mysqlPooledConnection;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource
 * JD-Core Version:    0.6.0
 */