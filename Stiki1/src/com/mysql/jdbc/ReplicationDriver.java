/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.Driver;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class ReplicationDriver extends NonRegisteringReplicationDriver
/*    */   implements Driver
/*    */ {
/*    */   public ReplicationDriver()
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 67 */       DriverManager.registerDriver(new NonRegisteringReplicationDriver());
/*    */     }
/*    */     catch (SQLException E) {
/* 70 */       throw new RuntimeException("Can't register driver!");
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ReplicationDriver
 * JD-Core Version:    0.6.0
 */