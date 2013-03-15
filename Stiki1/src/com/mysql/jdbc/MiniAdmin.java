/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class MiniAdmin
/*     */ {
/*     */   private Connection conn;
/*     */ 
/*     */   public MiniAdmin(java.sql.Connection conn)
/*     */     throws SQLException
/*     */   {
/*  57 */     if (conn == null) {
/*  58 */       throw SQLError.createSQLException(Messages.getString("MiniAdmin.0"), "S1000", ((ConnectionImpl)conn).getExceptionInterceptor());
/*     */     }
/*     */ 
/*  62 */     if (!(conn instanceof Connection)) {
/*  63 */       throw SQLError.createSQLException(Messages.getString("MiniAdmin.1"), "S1000", ((ConnectionImpl)conn).getExceptionInterceptor());
/*     */     }
/*     */ 
/*  67 */     this.conn = ((Connection)conn);
/*     */   }
/*     */ 
/*     */   public MiniAdmin(String jdbcUrl)
/*     */     throws SQLException
/*     */   {
/*  80 */     this(jdbcUrl, new Properties());
/*     */   }
/*     */ 
/*     */   public MiniAdmin(String jdbcUrl, Properties props)
/*     */     throws SQLException
/*     */   {
/*  96 */     this.conn = ((Connection)new Driver().connect(jdbcUrl, props));
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */     throws SQLException
/*     */   {
/* 110 */     this.conn.shutdownServer();
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.MiniAdmin
 * JD-Core Version:    0.6.0
 */