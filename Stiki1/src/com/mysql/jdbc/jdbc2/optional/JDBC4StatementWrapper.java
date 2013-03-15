/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class JDBC4StatementWrapper extends StatementWrapper
/*     */ {
/*     */   public JDBC4StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap)
/*     */   {
/*  63 */     super(c, conn, toWrap);
/*     */   }
/*     */ 
/*     */   public void close() throws SQLException {
/*     */     try {
/*  68 */       super.close();
/*     */     } finally {
/*  70 */       this.unwrappedInterfaces = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isClosed() throws SQLException {
/*     */     try {
/*  76 */       if (this.wrappedStmt != null) {
/*  77 */         return this.wrappedStmt.isClosed();
/*     */       }
/*  79 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/*  83 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/*  86 */     return false;
/*     */   }
/*     */ 
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     try {
/*  91 */       if (this.wrappedStmt != null)
/*  92 */         this.wrappedStmt.setPoolable(poolable);
/*     */       else
/*  94 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/*  98 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isPoolable() throws SQLException {
/*     */     try {
/* 104 */       if (this.wrappedStmt != null) {
/* 105 */         return this.wrappedStmt.isPoolable();
/*     */       }
/* 107 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 111 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 114 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 141 */     boolean isInstance = iface.isInstance(this);
/*     */ 
/* 143 */     if (isInstance) {
/* 144 */       return true;
/*     */     }
/*     */ 
/* 147 */     String interfaceClassName = iface.getName();
/*     */ 
/* 149 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement")) || (interfaceClassName.equals("java.sql.Statement")) || (interfaceClassName.equals("java.sql.Wrapper"));
/*     */   }
/*     */ 
/*     */   public synchronized <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 176 */       if (("java.sql.Statement".equals(iface.getName())) || ("java.sql.Wrapper.class".equals(iface.getName())))
/*     */       {
/* 178 */         return iface.cast(this);
/*     */       }
/*     */ 
/* 181 */       if (this.unwrappedInterfaces == null) {
/* 182 */         this.unwrappedInterfaces = new HashMap();
/*     */       }
/*     */ 
/* 185 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */ 
/* 187 */       if (cachedUnwrapped == null) {
/* 188 */         cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */ 
/* 192 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       }
/*     */ 
/* 195 */       return iface.cast(cachedUnwrapped); } catch (ClassCastException cce) {
/*     */     }
/* 197 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.JDBC4StatementWrapper
 * JD-Core Version:    0.6.0
 */