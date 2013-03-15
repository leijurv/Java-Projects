/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLIntegrityConstraintViolationException extends MySQLNonTransientException
/*    */ {
/*    */   public MySQLIntegrityConstraintViolationException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException
 * JD-Core Version:    0.6.0
 */