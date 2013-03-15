/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLDataException extends MySQLNonTransientException
/*    */ {
/*    */   public MySQLDataException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 36 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason, String SQLState) {
/* 40 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason) {
/* 44 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLDataException
 * JD-Core Version:    0.6.0
 */