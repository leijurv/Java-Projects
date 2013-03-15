/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLSyntaxErrorException extends MySQLNonTransientException
/*    */ {
/*    */   public MySQLSyntaxErrorException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 36 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState) {
/* 40 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason) {
/* 44 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLSyntaxErrorException
 * JD-Core Version:    0.6.0
 */