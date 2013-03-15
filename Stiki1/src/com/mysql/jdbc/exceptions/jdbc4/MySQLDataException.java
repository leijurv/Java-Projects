/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLDataException;
/*    */ 
/*    */ public class MySQLDataException extends SQLDataException
/*    */ {
/*    */   public MySQLDataException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 38 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason, String SQLState) {
/* 42 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason) {
/* 46 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLDataException
 * JD-Core Version:    0.6.0
 */