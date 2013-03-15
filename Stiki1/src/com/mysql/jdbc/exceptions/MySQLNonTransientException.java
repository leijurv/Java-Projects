/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class MySQLNonTransientException extends SQLException
/*    */ {
/*    */   public MySQLNonTransientException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 38 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason, String SQLState) {
/* 42 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason) {
/* 46 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLNonTransientException
 * JD-Core Version:    0.6.0
 */