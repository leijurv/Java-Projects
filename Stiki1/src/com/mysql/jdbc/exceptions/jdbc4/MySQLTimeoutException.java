/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTimeoutException;
/*    */ 
/*    */ public class MySQLTimeoutException extends SQLTimeoutException
/*    */ {
/*    */   public MySQLTimeoutException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 34 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTimeoutException(String reason, String SQLState) {
/* 38 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTimeoutException(String reason) {
/* 42 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTimeoutException() {
/* 46 */     super("Statement cancelled due to timeout or client request");
/*    */   }
/*    */ 
/*    */   public int getErrorCode()
/*    */   {
/* 51 */     return super.getErrorCode();
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLTimeoutException
 * JD-Core Version:    0.6.0
 */