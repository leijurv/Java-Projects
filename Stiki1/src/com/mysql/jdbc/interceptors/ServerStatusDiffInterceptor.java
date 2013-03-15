/*     */ package com.mysql.jdbc.interceptors;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.StatementInterceptor;
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class ServerStatusDiffInterceptor
/*     */   implements StatementInterceptor
/*     */ {
/*  42 */   private Map preExecuteValues = new HashMap();
/*     */ 
/*  44 */   private Map postExecuteValues = new HashMap();
/*     */ 
/*     */   public void init(Connection conn, Properties props)
/*     */     throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods postProcess(String sql, com.mysql.jdbc.Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection)
/*     */     throws SQLException
/*     */   {
/*  55 */     if (connection.versionMeetsMinimum(5, 0, 2)) {
/*  56 */       populateMapWithSessionStatusValues(connection, this.postExecuteValues);
/*     */ 
/*  58 */       connection.getLog().logInfo("Server status change for statement:\n" + Util.calculateDifferences(this.preExecuteValues, this.postExecuteValues));
/*     */     }
/*     */ 
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   private void populateMapWithSessionStatusValues(Connection connection, Map toPopulate)
/*     */     throws SQLException
/*     */   {
/*  70 */     java.sql.Statement stmt = null;
/*  71 */     ResultSet rs = null;
/*     */     try
/*     */     {
/*  74 */       toPopulate.clear();
/*     */ 
/*  76 */       stmt = connection.createStatement();
/*  77 */       rs = stmt.executeQuery("SHOW SESSION STATUS");
/*  78 */       Util.resultSetToMap(toPopulate, rs);
/*     */     } finally {
/*  80 */       if (rs != null) {
/*  81 */         rs.close();
/*     */       }
/*     */ 
/*  84 */       if (stmt != null)
/*  85 */         stmt.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods preProcess(String sql, com.mysql.jdbc.Statement interceptedStatement, Connection connection)
/*     */     throws SQLException
/*     */   {
/*  94 */     if (connection.versionMeetsMinimum(5, 0, 2)) {
/*  95 */       populateMapWithSessionStatusValues(connection, this.preExecuteValues);
/*     */     }
/*     */ 
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean executeTopLevelOnly() {
/* 103 */     return true;
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor
 * JD-Core Version:    0.6.0
 */