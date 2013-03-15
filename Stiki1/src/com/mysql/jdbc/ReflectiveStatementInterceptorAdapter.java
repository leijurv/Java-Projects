/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class ReflectiveStatementInterceptorAdapter
/*    */   implements StatementInterceptorV2
/*    */ {
/*    */   private final StatementInterceptor toProxy;
/*    */   final Method v2PostProcessMethod;
/*    */ 
/*    */   public ReflectiveStatementInterceptorAdapter(StatementInterceptor toProxy)
/*    */   {
/* 35 */     this.toProxy = toProxy;
/* 36 */     this.v2PostProcessMethod = getV2PostProcessMethod(toProxy.getClass());
/*    */   }
/*    */ 
/*    */   public void destroy() {
/* 40 */     this.toProxy.destroy();
/*    */   }
/*    */ 
/*    */   public boolean executeTopLevelOnly() {
/* 44 */     return this.toProxy.executeTopLevelOnly();
/*    */   }
/*    */ 
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 48 */     this.toProxy.init(conn, props);
/*    */   }
/*    */ 
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException)
/*    */     throws SQLException
/*    */   {
/*    */     SQLException sqlEx;
/*    */     try
/*    */     {
/* 58 */       return (ResultSetInternalMethods)this.v2PostProcessMethod.invoke(this.toProxy, new Object[] { sql, interceptedStatement, originalResultSet, connection, new Integer(warningCount), noIndexUsed ? Boolean.TRUE : Boolean.FALSE, noGoodIndexUsed ? Boolean.TRUE : Boolean.FALSE, statementException });
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 64 */       SQLException sqlEx = new SQLException("Unable to reflectively invoke interceptor");
/* 65 */       sqlEx.initCause(e);
/*    */ 
/* 67 */       throw sqlEx;
/*    */     } catch (IllegalAccessException e) {
/* 69 */       SQLException sqlEx = new SQLException("Unable to reflectively invoke interceptor");
/* 70 */       sqlEx.initCause(e);
/*    */ 
/* 72 */       throw sqlEx;
/*    */     } catch (InvocationTargetException e) {
/* 74 */       sqlEx = new SQLException("Unable to reflectively invoke interceptor");
/* 75 */       sqlEx.initCause(e);
/*    */     }
/* 77 */     throw sqlEx;
/*    */   }
/*    */ 
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
/*    */     throws SQLException
/*    */   {
/* 84 */     return this.toProxy.preProcess(sql, interceptedStatement, connection);
/*    */   }
/*    */ 
/*    */   public static final Method getV2PostProcessMethod(Class toProxyClass) {
/*    */     try {
/* 89 */       Method postProcessMethod = toProxyClass.getMethod("postProcess", new Class[] { String.class, Statement.class, ResultSetInternalMethods.class, Connection.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE, SQLException.class });
/*    */ 
/* 93 */       return postProcessMethod;
/*    */     } catch (SecurityException e) {
/* 95 */       return null; } catch (NoSuchMethodException e) {
/*    */     }
/* 97 */     return null;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ReflectiveStatementInterceptorAdapter
 * JD-Core Version:    0.6.0
 */