/*     */ package com.mysql.jdbc.integration.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*     */ import com.mchange.v2.c3p0.QueryConnectionTester;
/*     */ import com.mysql.jdbc.CommunicationsException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ public final class MysqlConnectionTester
/*     */   implements QueryConnectionTester
/*     */ {
/*     */   private static final long serialVersionUID = 3256444690067896368L;
/*  50 */   private static final Object[] NO_ARGS_ARRAY = new Object[0];
/*     */   private Method pingMethod;
/*     */ 
/*     */   public MysqlConnectionTester()
/*     */   {
/*     */     try
/*     */     {
/*  56 */       this.pingMethod = com.mysql.jdbc.Connection.class.getMethod("ping", null);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public int activeCheckConnection(java.sql.Connection con)
/*     */   {
/*     */     try
/*     */     {
/*  72 */       if (this.pingMethod != null) {
/*  73 */         if ((con instanceof com.mysql.jdbc.Connection))
/*     */         {
/*  76 */           ((com.mysql.jdbc.Connection)con).ping();
/*     */         }
/*     */         else {
/*  79 */           C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
/*  80 */           castCon.rawConnectionOperation(this.pingMethod, C3P0ProxyConnection.RAW_CONNECTION, NO_ARGS_ARRAY);
/*     */         }
/*     */       }
/*     */       else {
/*  84 */         Statement pingStatement = null;
/*     */         try
/*     */         {
/*  87 */           pingStatement = con.createStatement();
/*  88 */           pingStatement.executeQuery("SELECT 1").close();
/*     */         } finally {
/*  90 */           if (pingStatement != null) {
/*  91 */             pingStatement.close();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  96 */       return 0; } catch (Exception ex) {
/*     */     }
/*  98 */     return -1;
/*     */   }
/*     */ 
/*     */   public int statusOnException(java.sql.Connection arg0, Throwable throwable)
/*     */   {
/* 109 */     if (((throwable instanceof CommunicationsException)) || ("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException".equals(throwable.getClass().getName())))
/*     */     {
/* 112 */       return -1;
/*     */     }
/*     */ 
/* 115 */     if ((throwable instanceof SQLException)) {
/* 116 */       String sqlState = ((SQLException)throwable).getSQLState();
/*     */ 
/* 118 */       if ((sqlState != null) && (sqlState.startsWith("08"))) {
/* 119 */         return -1;
/*     */       }
/*     */ 
/* 122 */       return 0;
/*     */     }
/*     */ 
/* 127 */     return -1;
/*     */   }
/*     */ 
/*     */   public int activeCheckConnection(java.sql.Connection arg0, String arg1)
/*     */   {
/* 137 */     return 0;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.integration.c3p0.MysqlConnectionTester
 * JD-Core Version:    0.6.0
 */