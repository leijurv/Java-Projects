/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLNonTransientConnectionException extends MySQLNonTransientException
/*    */ {
/*    */   public MySQLNonTransientConnectionException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException
 * JD-Core Version:    0.6.0
 */