/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTransientConnectionException;
/*    */ 
/*    */ public class MySQLTransientConnectionException extends SQLTransientConnectionException
/*    */ {
/*    */   public MySQLTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 35 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException(String reason, String SQLState) {
/* 39 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException(String reason) {
/* 43 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLTransientConnectionException
 * JD-Core Version:    0.6.0
 */