/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class NoSubInterceptorWrapper
/*    */   implements StatementInterceptorV2
/*    */ {
/*    */   private final StatementInterceptorV2 underlyingInterceptor;
/*    */ 
/*    */   public NoSubInterceptorWrapper(StatementInterceptorV2 underlyingInterceptor)
/*    */   {
/* 34 */     if (underlyingInterceptor == null) {
/* 35 */       throw new RuntimeException("Interceptor to be wrapped can not be NULL");
/*    */     }
/*    */ 
/* 38 */     this.underlyingInterceptor = underlyingInterceptor;
/*    */   }
/*    */ 
/*    */   public void destroy() {
/* 42 */     this.underlyingInterceptor.destroy();
/*    */   }
/*    */ 
/*    */   public boolean executeTopLevelOnly() {
/* 46 */     return this.underlyingInterceptor.executeTopLevelOnly();
/*    */   }
/*    */ 
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 50 */     this.underlyingInterceptor.init(conn, props);
/*    */   }
/*    */ 
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException)
/*    */     throws SQLException
/*    */   {
/* 58 */     this.underlyingInterceptor.postProcess(sql, interceptedStatement, originalResultSet, connection, warningCount, noIndexUsed, noGoodIndexUsed, statementException);
/*    */ 
/* 61 */     return null;
/*    */   }
/*    */ 
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
/*    */     throws SQLException
/*    */   {
/* 67 */     this.underlyingInterceptor.preProcess(sql, interceptedStatement, connection);
/*    */ 
/* 69 */     return null;
/*    */   }
/*    */ 
/*    */   public StatementInterceptorV2 getUnderlyingInterceptor() {
/* 73 */     return this.underlyingInterceptor;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.NoSubInterceptorWrapper
 * JD-Core Version:    0.6.0
 */