/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTransientException;
/*    */ 
/*    */ public class MySQLTransientException extends SQLTransientException
/*    */ {
/*    */   public MySQLTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 35 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException(String reason, String SQLState) {
/* 39 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException(String reason) {
/* 43 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLTransientException
 * JD-Core Version:    0.6.0
 */