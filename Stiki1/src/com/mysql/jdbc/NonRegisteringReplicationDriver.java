/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class NonRegisteringReplicationDriver extends NonRegisteringDriver
/*    */ {
/*    */   public NonRegisteringReplicationDriver()
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   public Connection connect(String url, Properties info)
/*    */     throws SQLException
/*    */   {
/* 52 */     return connectReplicationConnection(url, info);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.NonRegisteringReplicationDriver
 * JD-Core Version:    0.6.0
 */