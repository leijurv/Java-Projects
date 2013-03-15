/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLTransientConnectionException extends MySQLTransientException
/*    */ {
/*    */   public MySQLTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 33 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException(String reason, String SQLState) {
/* 37 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException(String reason) {
/* 41 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLTransientConnectionException
 * JD-Core Version:    0.6.0
 */