/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLIntegrityConstraintViolationException;
/*    */ 
/*    */ public class MySQLIntegrityConstraintViolationException extends SQLIntegrityConstraintViolationException
/*    */ {
/*    */   public MySQLIntegrityConstraintViolationException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 39 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason, String SQLState) {
/* 43 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason) {
/* 47 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
 * JD-Core Version:    0.6.0
 */