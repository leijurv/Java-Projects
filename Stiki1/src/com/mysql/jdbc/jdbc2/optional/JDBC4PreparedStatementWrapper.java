/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.NClob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.StatementEvent;
/*     */ 
/*     */ public class JDBC4PreparedStatementWrapper extends PreparedStatementWrapper
/*     */ {
/*     */   public JDBC4PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap)
/*     */   {
/*  65 */     super(c, conn, toWrap);
/*     */   }
/*     */ 
/*     */   public synchronized void close() throws SQLException {
/*  69 */     if (this.pooledConnection == null)
/*     */     {
/*  71 */       return;
/*     */     }
/*     */ 
/*  74 */     MysqlPooledConnection con = this.pooledConnection;
/*     */     try
/*     */     {
/*  78 */       super.close();
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/*     */         StatementEvent e;
/*  81 */         StatementEvent e = new StatementEvent(con, this);
/*     */ 
/*  83 */         if ((con instanceof JDBC4MysqlPooledConnection))
/*  84 */           ((JDBC4MysqlPooledConnection)con).fireStatementEvent(e);
/*  85 */         else if ((con instanceof JDBC4MysqlXAConnection))
/*  86 */           ((JDBC4MysqlXAConnection)con).fireStatementEvent(e);
/*  87 */         else if ((con instanceof JDBC4SuspendableXAConnection))
/*  88 */           ((JDBC4SuspendableXAConnection)con).fireStatementEvent(e);
/*     */       }
/*     */       finally {
/*  91 */         this.unwrappedInterfaces = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isClosed() throws SQLException {
/*     */     try {
/*  98 */       if (this.wrappedStmt != null) {
/*  99 */         return this.wrappedStmt.isClosed();
/*     */       }
/* 101 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 105 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 108 */     return false;
/*     */   }
/*     */ 
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     try {
/* 113 */       if (this.wrappedStmt != null)
/* 114 */         this.wrappedStmt.setPoolable(poolable);
/*     */       else
/* 116 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 120 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isPoolable() throws SQLException {
/*     */     try {
/* 126 */       if (this.wrappedStmt != null) {
/* 127 */         return this.wrappedStmt.isPoolable();
/*     */       }
/* 129 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 133 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 136 */     return false;
/*     */   }
/*     */ 
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*     */     try {
/* 141 */       if (this.wrappedStmt != null) {
/* 142 */         ((PreparedStatement)this.wrappedStmt).setRowId(parameterIndex, x);
/*     */       }
/*     */       else {
/* 145 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 150 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/*     */     try {
/* 156 */       if (this.wrappedStmt != null) {
/* 157 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, value);
/*     */       }
/*     */       else {
/* 160 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 165 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
/*     */   {
/*     */     try {
/* 172 */       if (this.wrappedStmt != null) {
/* 173 */         ((PreparedStatement)this.wrappedStmt).setSQLXML(parameterIndex, xmlObject);
/*     */       }
/*     */       else {
/* 176 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 181 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNString(int parameterIndex, String value)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 190 */       if (this.wrappedStmt != null) {
/* 191 */         ((PreparedStatement)this.wrappedStmt).setNString(parameterIndex, value);
/*     */       }
/*     */       else {
/* 194 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 199 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNCharacterStream(int parameterIndex, Reader value, long length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 208 */       if (this.wrappedStmt != null) {
/* 209 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value, length);
/*     */       }
/*     */       else {
/* 212 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 217 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setClob(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 226 */       if (this.wrappedStmt != null) {
/* 227 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader, length);
/*     */       }
/*     */       else {
/* 230 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 235 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 244 */       if (this.wrappedStmt != null) {
/* 245 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream, length);
/*     */       }
/*     */       else {
/* 248 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 253 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNClob(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 262 */       if (this.wrappedStmt != null) {
/* 263 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader, length);
/*     */       }
/*     */       else {
/* 266 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 271 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 280 */       if (this.wrappedStmt != null) {
/* 281 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       }
/*     */       else {
/* 284 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 289 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 298 */       if (this.wrappedStmt != null) {
/* 299 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       }
/*     */       else {
/* 302 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 307 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 316 */       if (this.wrappedStmt != null) {
/* 317 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       }
/*     */       else {
/* 320 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 325 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 333 */       if (this.wrappedStmt != null) {
/* 334 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x);
/*     */       }
/*     */       else {
/* 337 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 342 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 350 */       if (this.wrappedStmt != null) {
/* 351 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x);
/*     */       }
/*     */       else {
/* 354 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 359 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 367 */       if (this.wrappedStmt != null) {
/* 368 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader);
/*     */       }
/*     */       else {
/* 371 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 376 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNCharacterStream(int parameterIndex, Reader value)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 385 */       if (this.wrappedStmt != null) {
/* 386 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value);
/*     */       }
/*     */       else {
/* 389 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 394 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setClob(int parameterIndex, Reader reader)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 403 */       if (this.wrappedStmt != null) {
/* 404 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader);
/*     */       }
/*     */       else {
/* 407 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 412 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBlob(int parameterIndex, InputStream inputStream)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 421 */       if (this.wrappedStmt != null) {
/* 422 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream);
/*     */       }
/*     */       else {
/* 425 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 430 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNClob(int parameterIndex, Reader reader) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 438 */       if (this.wrappedStmt != null) {
/* 439 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader);
/*     */       }
/*     */       else {
/* 442 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 447 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 475 */     boolean isInstance = iface.isInstance(this);
/*     */ 
/* 477 */     if (isInstance) {
/* 478 */       return true;
/*     */     }
/*     */ 
/* 481 */     String interfaceClassName = iface.getName();
/*     */ 
/* 483 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement")) || (interfaceClassName.equals("java.sql.Statement")) || (interfaceClassName.equals("java.sql.PreparedStatement")) || (interfaceClassName.equals("java.sql.Wrapper"));
/*     */   }
/*     */ 
/*     */   public synchronized <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 511 */       if (("java.sql.Statement".equals(iface.getName())) || ("java.sql.PreparedStatement".equals(iface.getName())) || ("java.sql.Wrapper.class".equals(iface.getName())))
/*     */       {
/* 514 */         return iface.cast(this);
/*     */       }
/*     */ 
/* 517 */       if (this.unwrappedInterfaces == null) {
/* 518 */         this.unwrappedInterfaces = new HashMap();
/*     */       }
/*     */ 
/* 521 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */ 
/* 523 */       if (cachedUnwrapped == null) {
/* 524 */         if (cachedUnwrapped == null) {
/* 525 */           cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */ 
/* 529 */           this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */         }
/* 531 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       }
/*     */ 
/* 534 */       return iface.cast(cachedUnwrapped); } catch (ClassCastException cce) {
/*     */     }
/* 536 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.JDBC4PreparedStatementWrapper
 * JD-Core Version:    0.6.0
 */