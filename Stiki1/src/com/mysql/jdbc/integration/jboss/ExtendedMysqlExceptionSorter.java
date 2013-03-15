/*    */ package com.mysql.jdbc.integration.jboss;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter;
/*    */ 
/*    */ public final class ExtendedMysqlExceptionSorter extends MySQLExceptionSorter
/*    */ {
/*    */   public boolean isExceptionFatal(SQLException ex)
/*    */   {
/* 47 */     String sqlState = ex.getSQLState();
/*    */ 
/* 49 */     if ((sqlState != null) && (sqlState.startsWith("08"))) {
/* 50 */       return true;
/*    */     }
/*    */ 
/* 53 */     return super.isExceptionFatal(ex);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.integration.jboss.ExtendedMysqlExceptionSorter
 * JD-Core Version:    0.6.0
 */