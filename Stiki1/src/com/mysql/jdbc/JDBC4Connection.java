/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.Array;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class JDBC4Connection extends ConnectionImpl
/*     */ {
/*     */   private JDBC4ClientInfoProvider infoProvider;
/*     */ 
/*     */   public JDBC4Connection(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url)
/*     */     throws SQLException
/*     */   {
/*  49 */     super(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
/*     */   }
/*     */ 
/*     */   public SQLXML createSQLXML() throws SQLException
/*     */   {
/*  54 */     return new JDBC4MysqlSQLXML(getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*  58 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/*  62 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public Properties getClientInfo() throws SQLException {
/*  66 */     return getClientInfoProviderImpl().getClientInfo(this);
/*     */   }
/*     */ 
/*     */   public String getClientInfo(String name) throws SQLException {
/*  70 */     return getClientInfoProviderImpl().getClientInfo(this, name);
/*     */   }
/*     */ 
/*     */   public synchronized boolean isValid(int timeout)
/*     */     throws SQLException
/*     */   {
/*  95 */     if (isClosed()) {
/*  96 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 100 */       synchronized (getMutex()) {
/*     */         try {
/* 102 */           pingInternal(false, timeout * 1000);
/*     */         } catch (Throwable t) {
/*     */           try {
/* 105 */             abortInternal();
/*     */           }
/*     */           catch (Throwable ignoreThrown)
/*     */           {
/*     */           }
/* 110 */           return false;
/*     */         }
/*     */       }
/*     */     } catch (Throwable t) {
/* 114 */       return false;
/*     */     }
/*     */ 
/* 117 */     return true;
/*     */   }
/*     */ 
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/*     */     try {
/* 122 */       getClientInfoProviderImpl().setClientInfo(this, properties);
/*     */     } catch (SQLClientInfoException ciEx) {
/* 124 */       throw ciEx;
/*     */     } catch (SQLException sqlEx) {
/* 126 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 127 */       clientInfoEx.initCause(sqlEx);
/*     */ 
/* 129 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*     */     try {
/* 135 */       getClientInfoProviderImpl().setClientInfo(this, name, value);
/*     */     } catch (SQLClientInfoException ciEx) {
/* 137 */       throw ciEx;
/*     */     } catch (SQLException sqlEx) {
/* 139 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 140 */       clientInfoEx.initCause(sqlEx);
/*     */ 
/* 142 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 162 */     checkClosed();
/*     */ 
/* 166 */     return iface.isInstance(this);
/*     */   }
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 187 */       return iface.cast(this); } catch (ClassCastException cce) {
/*     */     }
/* 189 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public java.sql.Blob createBlob()
/*     */   {
/* 198 */     return new Blob(getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public java.sql.Clob createClob()
/*     */   {
/* 205 */     return new Clob(getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public NClob createNClob()
/*     */   {
/* 212 */     return new JDBC4NClob(getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   protected synchronized JDBC4ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
/* 216 */     if (this.infoProvider == null) {
/*     */       try {
/*     */         try {
/* 219 */           this.infoProvider = ((JDBC4ClientInfoProvider)Util.getInstance(getClientInfoProvider(), new Class[0], new Object[0], getExceptionInterceptor()));
/*     */         }
/*     */         catch (SQLException sqlEx) {
/* 222 */           if ((sqlEx.getCause() instanceof ClassCastException))
/*     */           {
/* 224 */             this.infoProvider = ((JDBC4ClientInfoProvider)Util.getInstance("com.mysql.jdbc." + getClientInfoProvider(), new Class[0], new Object[0], getExceptionInterceptor()));
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (ClassCastException cce)
/*     */       {
/* 230 */         throw SQLError.createSQLException(Messages.getString("JDBC4Connection.ClientInfoNotImplemented", new Object[] { getClientInfoProvider() }), "S1009", getExceptionInterceptor());
/*     */       }
/*     */ 
/* 235 */       this.infoProvider.initialize(this, this.props);
/*     */     }
/*     */ 
/* 238 */     return this.infoProvider;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4Connection
 * JD-Core Version:    0.6.0
 */