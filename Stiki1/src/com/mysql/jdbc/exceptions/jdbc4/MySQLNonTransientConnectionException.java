/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLNonTransientConnectionException;
/*    */ 
/*    */ public class MySQLNonTransientConnectionException extends SQLNonTransientConnectionException
/*    */ {
/*    */   public MySQLNonTransientConnectionException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 39 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason, String SQLState) {
/* 43 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason) {
/* 47 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException
 * JD-Core Version:    0.6.0
 */