/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.NClob;
/*    */ 
/*    */ public class JDBC4NClob extends Clob
/*    */   implements NClob
/*    */ {
/*    */   JDBC4NClob(ExceptionInterceptor exceptionInterceptor)
/*    */   {
/* 41 */     super(exceptionInterceptor);
/*    */   }
/*    */ 
/*    */   JDBC4NClob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
/* 45 */     super(charDataInit, exceptionInterceptor);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4NClob
 * JD-Core Version:    0.6.0
 */