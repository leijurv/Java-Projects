/*    */ package com.mysql.jdbc.interceptors;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.ResultSetInternalMethods;
/*    */ import com.mysql.jdbc.Statement;
/*    */ import com.mysql.jdbc.StatementInterceptor;
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class SessionAssociationInterceptor
/*    */   implements StatementInterceptor
/*    */ {
/*    */   protected String currentSessionKey;
/* 15 */   protected static ThreadLocal sessionLocal = new ThreadLocal();
/*    */ 
/*    */   public static final void setSessionKey(String key) {
/* 18 */     sessionLocal.set(key);
/*    */   }
/*    */ 
/*    */   public static final void resetSessionKey() {
/* 22 */     sessionLocal.set(null);
/*    */   }
/*    */ 
/*    */   public static final String getSessionKey() {
/* 26 */     return (String)sessionLocal.get();
/*    */   }
/*    */ 
/*    */   public boolean executeTopLevelOnly() {
/* 30 */     return true;
/*    */   }
/*    */ 
/*    */   public void init(Connection conn, Properties props)
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection)
/*    */     throws SQLException
/*    */   {
/* 41 */     return null;
/*    */   }
/*    */ 
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
/*    */     throws SQLException
/*    */   {
/* 47 */     String key = getSessionKey();
/*    */ 
/* 49 */     if ((key != null) && (!key.equals(this.currentSessionKey))) {
/* 50 */       PreparedStatement pstmt = connection.clientPrepareStatement("SET @mysql_proxy_session=?");
/*    */       try
/*    */       {
/* 53 */         pstmt.setString(1, key);
/* 54 */         pstmt.execute();
/*    */       } finally {
/* 56 */         pstmt.close();
/*    */       }
/*    */ 
/* 59 */       this.currentSessionKey = key;
/*    */     }
/*    */ 
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   public void destroy()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.interceptors.SessionAssociationInterceptor
 * JD-Core Version:    0.6.0
 */