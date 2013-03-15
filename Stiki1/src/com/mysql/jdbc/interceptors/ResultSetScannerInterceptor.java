/*     */ package com.mysql.jdbc.interceptors;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.Statement;
/*     */ import com.mysql.jdbc.StatementInterceptor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class ResultSetScannerInterceptor
/*     */   implements StatementInterceptor
/*     */ {
/*     */   private Pattern regexP;
/*     */ 
/*     */   public void init(Connection conn, Properties props)
/*     */     throws SQLException
/*     */   {
/*  47 */     String regexFromUser = props.getProperty("resultSetScannerRegex");
/*     */ 
/*  49 */     if ((regexFromUser == null) || (regexFromUser.length() == 0)) {
/*  50 */       throw new SQLException("resultSetScannerRegex must be configured, and must be > 0 characters");
/*     */     }
/*     */     try
/*     */     {
/*  54 */       this.regexP = Pattern.compile(regexFromUser);
/*     */     } catch (Throwable t) {
/*  56 */       SQLException sqlEx = new SQLException("Can't use configured regex due to underlying exception.");
/*  57 */       sqlEx.initCause(t);
/*     */ 
/*  59 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection)
/*     */     throws SQLException
/*     */   {
/*  69 */     ResultSetInternalMethods finalResultSet = originalResultSet;
/*     */ 
/*  71 */     return (ResultSetInternalMethods)Proxy.newProxyInstance(originalResultSet.getClass().getClassLoader(), new Class[] { ResultSetInternalMethods.class }, new InvocationHandler(finalResultSet)
/*     */     {
/*     */       private final ResultSetInternalMethods val$finalResultSet;
/*     */ 
/*     */       public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */       {
/*  78 */         Object invocationResult = method.invoke(this.val$finalResultSet, args);
/*     */ 
/*  80 */         String methodName = method.getName();
/*     */ 
/*  82 */         if (((invocationResult != null) && ((invocationResult instanceof String))) || ("getString".equals(methodName)) || ("getObject".equals(methodName)) || ("getObjectStoredProc".equals(methodName)))
/*     */         {
/*  86 */           Matcher matcher = ResultSetScannerInterceptor.this.regexP.matcher(invocationResult.toString());
/*     */ 
/*  88 */           if (matcher.matches()) {
/*  89 */             throw new SQLException("value disallowed by filter");
/*     */           }
/*     */         }
/*     */ 
/*  93 */         return invocationResult;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
/*     */     throws SQLException
/*     */   {
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean executeTopLevelOnly()
/*     */   {
/* 108 */     return false;
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.interceptors.ResultSetScannerInterceptor
 * JD-Core Version:    0.6.0
 */