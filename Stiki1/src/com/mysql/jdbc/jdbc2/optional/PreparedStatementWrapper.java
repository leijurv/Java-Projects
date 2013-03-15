/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ public class PreparedStatementWrapper extends StatementWrapper
/*     */   implements PreparedStatement
/*     */ {
/*     */   private static final Constructor JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR;
/*     */ 
/*     */   protected static PreparedStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap)
/*     */     throws SQLException
/*     */   {
/*  88 */     if (!Util.isJdbc4()) {
/*  89 */       return new PreparedStatementWrapper(c, conn, toWrap);
/*     */     }
/*     */ 
/*  93 */     return (PreparedStatementWrapper)Util.handleNewInstance(JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap }, conn.getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap)
/*     */   {
/* 101 */     super(c, conn, toWrap);
/*     */   }
/*     */ 
/*     */   public void setArray(int parameterIndex, Array x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 111 */       if (this.wrappedStmt != null) {
/* 112 */         ((PreparedStatement)this.wrappedStmt).setArray(parameterIndex, x);
/*     */       }
/*     */       else {
/* 115 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 120 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 133 */       if (this.wrappedStmt != null) {
/* 134 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       }
/*     */       else {
/* 137 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 142 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 154 */       if (this.wrappedStmt != null) {
/* 155 */         ((PreparedStatement)this.wrappedStmt).setBigDecimal(parameterIndex, x);
/*     */       }
/*     */       else {
/* 158 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 163 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 176 */       if (this.wrappedStmt != null) {
/* 177 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       }
/*     */       else {
/* 180 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 185 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBlob(int parameterIndex, Blob x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 196 */       if (this.wrappedStmt != null) {
/* 197 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, x);
/*     */       }
/*     */       else {
/* 200 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 205 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBoolean(int parameterIndex, boolean x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 216 */       if (this.wrappedStmt != null) {
/* 217 */         ((PreparedStatement)this.wrappedStmt).setBoolean(parameterIndex, x);
/*     */       }
/*     */       else {
/* 220 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 225 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setByte(int parameterIndex, byte x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 236 */       if (this.wrappedStmt != null) {
/* 237 */         ((PreparedStatement)this.wrappedStmt).setByte(parameterIndex, x);
/*     */       }
/*     */       else {
/* 240 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 245 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBytes(int parameterIndex, byte[] x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 256 */       if (this.wrappedStmt != null) {
/* 257 */         ((PreparedStatement)this.wrappedStmt).setBytes(parameterIndex, x);
/*     */       }
/*     */       else {
/* 260 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 265 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 278 */       if (this.wrappedStmt != null) {
/* 279 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       }
/*     */       else {
/* 282 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 287 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setClob(int parameterIndex, Clob x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 298 */       if (this.wrappedStmt != null) {
/* 299 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, x);
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
/*     */   public void setDate(int parameterIndex, Date x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 318 */       if (this.wrappedStmt != null) {
/* 319 */         ((PreparedStatement)this.wrappedStmt).setDate(parameterIndex, x);
/*     */       }
/*     */       else {
/* 322 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 327 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDate(int parameterIndex, Date x, Calendar cal)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 340 */       if (this.wrappedStmt != null) {
/* 341 */         ((PreparedStatement)this.wrappedStmt).setDate(parameterIndex, x, cal);
/*     */       }
/*     */       else {
/* 344 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 349 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDouble(int parameterIndex, double x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 360 */       if (this.wrappedStmt != null) {
/* 361 */         ((PreparedStatement)this.wrappedStmt).setDouble(parameterIndex, x);
/*     */       }
/*     */       else {
/* 364 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 369 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFloat(int parameterIndex, float x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 380 */       if (this.wrappedStmt != null) {
/* 381 */         ((PreparedStatement)this.wrappedStmt).setFloat(parameterIndex, x);
/*     */       }
/*     */       else {
/* 384 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 389 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInt(int parameterIndex, int x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 400 */       if (this.wrappedStmt != null) {
/* 401 */         ((PreparedStatement)this.wrappedStmt).setInt(parameterIndex, x);
/*     */       }
/*     */       else {
/* 404 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 409 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setLong(int parameterIndex, long x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 420 */       if (this.wrappedStmt != null) {
/* 421 */         ((PreparedStatement)this.wrappedStmt).setLong(parameterIndex, x);
/*     */       }
/*     */       else {
/* 424 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 429 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ResultSetMetaData getMetaData()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 440 */       if (this.wrappedStmt != null) {
/* 441 */         return ((PreparedStatement)this.wrappedStmt).getMetaData();
/*     */       }
/*     */ 
/* 444 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 448 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 451 */     return null;
/*     */   }
/*     */ 
/*     */   public void setNull(int parameterIndex, int sqlType)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 461 */       if (this.wrappedStmt != null) {
/* 462 */         ((PreparedStatement)this.wrappedStmt).setNull(parameterIndex, sqlType);
/*     */       }
/*     */       else {
/* 465 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 470 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNull(int parameterIndex, int sqlType, String typeName)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 482 */       if (this.wrappedStmt != null) {
/* 483 */         ((PreparedStatement)this.wrappedStmt).setNull(parameterIndex, sqlType, typeName);
/*     */       }
/*     */       else {
/* 486 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 491 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 502 */       if (this.wrappedStmt != null) {
/* 503 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x);
/*     */       }
/*     */       else {
/* 506 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 511 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 523 */       if (this.wrappedStmt != null) {
/* 524 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
/*     */       }
/*     */       else {
/* 527 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 532 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 545 */       if (this.wrappedStmt != null) {
/* 546 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scale);
/*     */       }
/*     */       else {
/* 549 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 554 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ParameterMetaData getParameterMetaData()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 565 */       if (this.wrappedStmt != null) {
/* 566 */         return ((PreparedStatement)this.wrappedStmt).getParameterMetaData();
/*     */       }
/*     */ 
/* 570 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 574 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 577 */     return null;
/*     */   }
/*     */ 
/*     */   public void setRef(int parameterIndex, Ref x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 587 */       if (this.wrappedStmt != null) {
/* 588 */         ((PreparedStatement)this.wrappedStmt).setRef(parameterIndex, x);
/*     */       }
/*     */       else {
/* 591 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 596 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setShort(int parameterIndex, short x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 607 */       if (this.wrappedStmt != null) {
/* 608 */         ((PreparedStatement)this.wrappedStmt).setShort(parameterIndex, x);
/*     */       }
/*     */       else {
/* 611 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 616 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setString(int parameterIndex, String x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 627 */       if (this.wrappedStmt != null) {
/* 628 */         ((PreparedStatement)this.wrappedStmt).setString(parameterIndex, x);
/*     */       }
/*     */       else {
/* 631 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 636 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTime(int parameterIndex, Time x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 647 */       if (this.wrappedStmt != null) {
/* 648 */         ((PreparedStatement)this.wrappedStmt).setTime(parameterIndex, x);
/*     */       }
/*     */       else {
/* 651 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 656 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 669 */       if (this.wrappedStmt != null) {
/* 670 */         ((PreparedStatement)this.wrappedStmt).setTime(parameterIndex, x, cal);
/*     */       }
/*     */       else {
/* 673 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 678 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTimestamp(int parameterIndex, Timestamp x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 690 */       if (this.wrappedStmt != null) {
/* 691 */         ((PreparedStatement)this.wrappedStmt).setTimestamp(parameterIndex, x);
/*     */       }
/*     */       else {
/* 694 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 699 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 712 */       if (this.wrappedStmt != null) {
/* 713 */         ((PreparedStatement)this.wrappedStmt).setTimestamp(parameterIndex, x, cal);
/*     */       }
/*     */       else {
/* 716 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 721 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setURL(int parameterIndex, URL x)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 732 */       if (this.wrappedStmt != null) {
/* 733 */         ((PreparedStatement)this.wrappedStmt).setURL(parameterIndex, x);
/*     */       }
/*     */       else {
/* 736 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 741 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 765 */       if (this.wrappedStmt != null) {
/* 766 */         ((PreparedStatement)this.wrappedStmt).setUnicodeStream(parameterIndex, x, length);
/*     */       }
/*     */       else {
/* 769 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 774 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addBatch()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 785 */       if (this.wrappedStmt != null)
/* 786 */         ((PreparedStatement)this.wrappedStmt).addBatch();
/*     */       else {
/* 788 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 793 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearParameters()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 804 */       if (this.wrappedStmt != null)
/* 805 */         ((PreparedStatement)this.wrappedStmt).clearParameters();
/*     */       else {
/* 807 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 812 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean execute()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 823 */       if (this.wrappedStmt != null) {
/* 824 */         return ((PreparedStatement)this.wrappedStmt).execute();
/*     */       }
/*     */ 
/* 827 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 831 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 834 */     return false;
/*     */   }
/*     */ 
/*     */   public ResultSet executeQuery()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 847 */       if (this.wrappedStmt != null) {
/* 848 */         ResultSet rs = ((PreparedStatement)this.wrappedStmt).executeQuery();
/*     */ 
/* 851 */         ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */ 
/* 853 */         return rs;
/*     */       }
/*     */ 
/* 856 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 860 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 863 */     return null;
/*     */   }
/*     */ 
/*     */   public int executeUpdate()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 876 */       if (this.wrappedStmt != null) {
/* 877 */         return ((PreparedStatement)this.wrappedStmt).executeUpdate();
/*     */       }
/*     */ 
/* 880 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 884 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 887 */     return -1;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  66 */     if (Util.isJdbc4())
/*     */       try {
/*  68 */         JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4PreparedStatementWrapper").getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, PreparedStatement.class });
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*  74 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  76 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  78 */         throw new RuntimeException(e);
/*     */       }
/*     */     else
/*  81 */       JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR = null;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.PreparedStatementWrapper
 * JD-Core Version:    0.6.0
 */