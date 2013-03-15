/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class JDBC4ConnectionWrapper extends ConnectionWrapper
/*     */ {
/*     */   public JDBC4ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, com.mysql.jdbc.Connection mysqlConnection, boolean forXa)
/*     */     throws SQLException
/*     */   {
/*  72 */     super(mysqlPooledConnection, mysqlConnection, forXa);
/*     */   }
/*     */ 
/*     */   public void close() throws SQLException {
/*     */     try {
/*  77 */       super.close();
/*     */     } finally {
/*  79 */       this.unwrappedInterfaces = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SQLXML createSQLXML() throws SQLException {
/*  84 */     checkClosed();
/*     */     try
/*     */     {
/*  87 */       return this.mc.createSQLXML();
/*     */     } catch (SQLException sqlException) {
/*  89 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException
/*     */   {
/*  97 */     checkClosed();
/*     */     try
/*     */     {
/* 100 */       return this.mc.createArrayOf(typeName, elements);
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 103 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 106 */     return null;
/*     */   }
/*     */ 
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException
/*     */   {
/* 111 */     checkClosed();
/*     */     try
/*     */     {
/* 114 */       return this.mc.createStruct(typeName, attributes);
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 117 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */   public Properties getClientInfo() throws SQLException {
/* 124 */     checkClosed();
/*     */     try
/*     */     {
/* 127 */       return this.mc.getClientInfo();
/*     */     } catch (SQLException sqlException) {
/* 129 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   public String getClientInfo(String name) throws SQLException {
/* 136 */     checkClosed();
/*     */     try
/*     */     {
/* 139 */       return this.mc.getClientInfo(name);
/*     */     } catch (SQLException sqlException) {
/* 141 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isValid(int timeout)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 171 */       return this.mc.isValid(timeout);
/*     */     } catch (SQLException sqlException) {
/* 173 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 176 */     return false;
/*     */   }
/*     */ 
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException
/*     */   {
/*     */     try {
/* 182 */       checkClosed();
/*     */ 
/* 184 */       this.mc.setClientInfo(properties);
/*     */     } catch (SQLException sqlException) {
/*     */       try {
/* 187 */         checkAndFireConnectionError(sqlException);
/*     */       } catch (SQLException sqlEx2) {
/* 189 */         SQLClientInfoException clientEx = new SQLClientInfoException();
/* 190 */         clientEx.initCause(sqlEx2);
/*     */ 
/* 192 */         throw clientEx;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException
/*     */   {
/*     */     try {
/* 200 */       checkClosed();
/*     */ 
/* 202 */       this.mc.setClientInfo(name, value);
/*     */     } catch (SQLException sqlException) {
/*     */       try {
/* 205 */         checkAndFireConnectionError(sqlException);
/*     */       } catch (SQLException sqlEx2) {
/* 207 */         SQLClientInfoException clientEx = new SQLClientInfoException();
/* 208 */         clientEx.initCause(sqlEx2);
/*     */ 
/* 210 */         throw clientEx;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 238 */     checkClosed();
/*     */ 
/* 240 */     boolean isInstance = iface.isInstance(this);
/*     */ 
/* 242 */     if (isInstance) {
/* 243 */       return true;
/*     */     }
/*     */ 
/* 246 */     return (iface.getName().equals("com.mysql.jdbc.Connection")) || (iface.getName().equals("com.mysql.jdbc.ConnectionProperties"));
/*     */   }
/*     */ 
/*     */   public synchronized <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 272 */       if (("java.sql.Connection".equals(iface.getName())) || ("java.sql.Wrapper.class".equals(iface.getName())))
/*     */       {
/* 274 */         return iface.cast(this);
/*     */       }
/*     */ 
/* 277 */       if (this.unwrappedInterfaces == null) {
/* 278 */         this.unwrappedInterfaces = new HashMap();
/*     */       }
/*     */ 
/* 281 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */ 
/* 283 */       if (cachedUnwrapped == null) {
/* 284 */         cachedUnwrapped = Proxy.newProxyInstance(this.mc.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.mc));
/*     */ 
/* 287 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       }
/*     */ 
/* 290 */       return iface.cast(cachedUnwrapped); } catch (ClassCastException cce) {
/*     */     }
/* 292 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public Blob createBlob()
/*     */     throws SQLException
/*     */   {
/* 301 */     checkClosed();
/*     */     try
/*     */     {
/* 304 */       return this.mc.createBlob();
/*     */     } catch (SQLException sqlException) {
/* 306 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 309 */     return null;
/*     */   }
/*     */ 
/*     */   public Clob createClob()
/*     */     throws SQLException
/*     */   {
/* 316 */     checkClosed();
/*     */     try
/*     */     {
/* 319 */       return this.mc.createClob();
/*     */     } catch (SQLException sqlException) {
/* 321 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 324 */     return null;
/*     */   }
/*     */ 
/*     */   public NClob createNClob()
/*     */     throws SQLException
/*     */   {
/* 331 */     checkClosed();
/*     */     try
/*     */     {
/* 334 */       return this.mc.createNClob();
/*     */     } catch (SQLException sqlException) {
/* 336 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 339 */     return null;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.JDBC4ConnectionWrapper
 * JD-Core Version:    0.6.0
 */