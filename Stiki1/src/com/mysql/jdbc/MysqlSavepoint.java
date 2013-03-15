/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.rmi.server.UID;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Savepoint;
/*     */ 
/*     */ public class MysqlSavepoint
/*     */   implements Savepoint
/*     */ {
/*     */   private String savepointName;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   private static String getUniqueId()
/*     */   {
/*  44 */     String uidStr = new UID().toString();
/*     */ 
/*  46 */     int uidLength = uidStr.length();
/*     */ 
/*  48 */     StringBuffer safeString = new StringBuffer(uidLength);
/*     */ 
/*  50 */     for (int i = 0; i < uidLength; i++) {
/*  51 */       char c = uidStr.charAt(i);
/*     */ 
/*  53 */       if ((Character.isLetter(c)) || (Character.isDigit(c)))
/*  54 */         safeString.append(c);
/*     */       else {
/*  56 */         safeString.append('_');
/*     */       }
/*     */     }
/*     */ 
/*  60 */     return safeString.toString();
/*     */   }
/*     */ 
/*     */   MysqlSavepoint(ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  76 */     this(getUniqueId(), exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   MysqlSavepoint(String name, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  89 */     if ((name == null) || (name.length() == 0)) {
/*  90 */       throw SQLError.createSQLException("Savepoint name can not be NULL or empty", "S1009", exceptionInterceptor);
/*     */     }
/*     */ 
/*  94 */     this.savepointName = name;
/*     */ 
/*  96 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   public int getSavepointId()
/*     */     throws SQLException
/*     */   {
/* 103 */     throw SQLError.createSQLException("Only named savepoints are supported.", "S1C00", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public String getSavepointName()
/*     */     throws SQLException
/*     */   {
/* 111 */     return this.savepointName;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.MysqlSavepoint
 * JD-Core Version:    0.6.0
 */