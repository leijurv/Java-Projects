/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLSyntaxErrorException;
/*    */ 
/*    */ public class MySQLSyntaxErrorException extends SQLSyntaxErrorException
/*    */ {
/*    */   public MySQLSyntaxErrorException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 38 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState) {
/* 42 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason) {
/* 46 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException
 * JD-Core Version:    0.6.0
 */