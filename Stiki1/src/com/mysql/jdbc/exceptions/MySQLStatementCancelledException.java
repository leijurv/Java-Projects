/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLStatementCancelledException extends MySQLNonTransientException
/*    */ {
/*    */   public MySQLStatementCancelledException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 31 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLStatementCancelledException(String reason, String SQLState) {
/* 35 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLStatementCancelledException(String reason) {
/* 39 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLStatementCancelledException() {
/* 43 */     super("Statement cancelled due to client request");
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLStatementCancelledException
 * JD-Core Version:    0.6.0
 */