/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ class OperationNotSupportedException extends SQLException
/*    */ {
/*    */   OperationNotSupportedException()
/*    */   {
/* 31 */     super(Messages.getString("RowDataDynamic.10"), "S1009");
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.OperationNotSupportedException
 * JD-Core Version:    0.6.0
 */