/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class JDBC4CommentClientInfoProvider
/*     */   implements JDBC4ClientInfoProvider
/*     */ {
/*     */   private Properties clientInfo;
/*     */ 
/*     */   public synchronized void initialize(java.sql.Connection conn, Properties configurationProps)
/*     */     throws SQLException
/*     */   {
/*  54 */     this.clientInfo = new Properties();
/*     */   }
/*     */ 
/*     */   public synchronized void destroy() throws SQLException {
/*  58 */     this.clientInfo = null;
/*     */   }
/*     */ 
/*     */   public synchronized Properties getClientInfo(java.sql.Connection conn) throws SQLException
/*     */   {
/*  63 */     return this.clientInfo;
/*     */   }
/*     */ 
/*     */   public synchronized String getClientInfo(java.sql.Connection conn, String name) throws SQLException
/*     */   {
/*  68 */     return this.clientInfo.getProperty(name);
/*     */   }
/*     */ 
/*     */   public synchronized void setClientInfo(java.sql.Connection conn, Properties properties) throws SQLClientInfoException
/*     */   {
/*  73 */     this.clientInfo = new Properties();
/*     */ 
/*  75 */     Enumeration propNames = properties.propertyNames();
/*     */ 
/*  77 */     while (propNames.hasMoreElements()) {
/*  78 */       String name = (String)propNames.nextElement();
/*     */ 
/*  80 */       this.clientInfo.put(name, properties.getProperty(name));
/*     */     }
/*     */ 
/*  83 */     setComment(conn);
/*     */   }
/*     */ 
/*     */   public synchronized void setClientInfo(java.sql.Connection conn, String name, String value) throws SQLClientInfoException
/*     */   {
/*  88 */     this.clientInfo.setProperty(name, value);
/*  89 */     setComment(conn);
/*     */   }
/*     */ 
/*     */   private synchronized void setComment(java.sql.Connection conn) {
/*  93 */     StringBuffer commentBuf = new StringBuffer();
/*  94 */     Iterator elements = this.clientInfo.entrySet().iterator();
/*     */ 
/*  96 */     while (elements.hasNext()) {
/*  97 */       if (commentBuf.length() > 0) {
/*  98 */         commentBuf.append(", ");
/*     */       }
/*     */ 
/* 101 */       Map.Entry entry = (Map.Entry)elements.next();
/* 102 */       commentBuf.append("" + entry.getKey());
/* 103 */       commentBuf.append("=");
/* 104 */       commentBuf.append("" + entry.getValue());
/*     */     }
/*     */ 
/* 107 */     ((Connection)conn).setStatementComment(commentBuf.toString());
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4CommentClientInfoProvider
 * JD-Core Version:    0.6.0
 */