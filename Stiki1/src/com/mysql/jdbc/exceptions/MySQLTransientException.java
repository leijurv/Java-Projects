/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class MySQLTransientException extends SQLException
/*    */ {
/*    */   public MySQLTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 34 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException(String reason, String SQLState) {
/* 38 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException(String reason) {
/* 42 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLTransientException
 * JD-Core Version:    0.6.0
 */