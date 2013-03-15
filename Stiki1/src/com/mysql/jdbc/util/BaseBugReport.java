/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.Driver;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public abstract class BaseBugReport
/*     */ {
/*     */   private Connection conn;
/*     */   private Driver driver;
/*     */ 
/*     */   public BaseBugReport()
/*     */   {
/*     */     try
/*     */     {
/* 110 */       this.driver = new Driver();
/*     */     } catch (SQLException ex) {
/* 112 */       throw new RuntimeException(ex.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void setUp()
/*     */     throws Exception;
/*     */ 
/*     */   public abstract void tearDown()
/*     */     throws Exception;
/*     */ 
/*     */   public abstract void runTest()
/*     */     throws Exception;
/*     */ 
/*     */   public final void run()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 153 */       setUp();
/* 154 */       runTest();
/*     */     }
/*     */     finally {
/* 157 */       tearDown();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void assertTrue(String message, boolean condition)
/*     */     throws Exception
/*     */   {
/* 174 */     if (!condition)
/* 175 */       throw new Exception("Assertion failed: " + message);
/*     */   }
/*     */ 
/*     */   protected final void assertTrue(boolean condition)
/*     */     throws Exception
/*     */   {
/* 188 */     assertTrue("(no message given)", condition);
/*     */   }
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 199 */     return "jdbc:mysql:///test";
/*     */   }
/*     */ 
/*     */   public final synchronized Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 214 */     if ((this.conn == null) || (this.conn.isClosed())) {
/* 215 */       this.conn = getNewConnection();
/*     */     }
/*     */ 
/* 218 */     return this.conn;
/*     */   }
/*     */ 
/*     */   public final synchronized Connection getNewConnection()
/*     */     throws SQLException
/*     */   {
/* 231 */     return getConnection(getUrl());
/*     */   }
/*     */ 
/*     */   public final synchronized Connection getConnection(String url)
/*     */     throws SQLException
/*     */   {
/* 245 */     return getConnection(url, null);
/*     */   }
/*     */ 
/*     */   public final synchronized Connection getConnection(String url, Properties props)
/*     */     throws SQLException
/*     */   {
/* 265 */     return this.driver.connect(url, props);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.util.BaseBugReport
 * JD-Core Version:    0.6.0
 */