/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLNonTransientException;
/*    */ 
/*    */ public class MySQLNonTransientException extends SQLNonTransientException
/*    */ {
/*    */   public MySQLNonTransientException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 39 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason, String SQLState) {
/* 43 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason) {
/* 47 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientException
 * JD-Core Version:    0.6.0
 */