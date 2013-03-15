/*      */ package com.mysql.jdbc.jdbc2.optional;
/*      */ 
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import com.mysql.jdbc.Util;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Map;
/*      */ 
/*      */ public class CallableStatementWrapper extends PreparedStatementWrapper
/*      */   implements CallableStatement
/*      */ {
/*      */   private static final Constructor JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR;
/*      */ 
/*      */   protected static CallableStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap)
/*      */     throws SQLException
/*      */   {
/*   84 */     if (!Util.isJdbc4()) {
/*   85 */       return new CallableStatementWrapper(c, conn, toWrap);
/*      */     }
/*      */ 
/*   89 */     return (CallableStatementWrapper)Util.handleNewInstance(JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap)
/*      */   {
/*  104 */     super(c, conn, toWrap);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  115 */       if (this.wrappedStmt != null) {
/*  116 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType);
/*      */       }
/*      */       else {
/*  119 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  124 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  136 */       if (this.wrappedStmt != null) {
/*  137 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, scale);
/*      */       }
/*      */       else {
/*  140 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  145 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  156 */       if (this.wrappedStmt != null) {
/*  157 */         return ((CallableStatement)this.wrappedStmt).wasNull();
/*      */       }
/*  159 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  164 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  167 */     return false;
/*      */   }
/*      */ 
/*      */   public String getString(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  177 */       if (this.wrappedStmt != null) {
/*  178 */         return ((CallableStatement)this.wrappedStmt).getString(parameterIndex);
/*      */       }
/*      */ 
/*  181 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  186 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  188 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  198 */       if (this.wrappedStmt != null) {
/*  199 */         return ((CallableStatement)this.wrappedStmt).getBoolean(parameterIndex);
/*      */       }
/*      */ 
/*  202 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  207 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  210 */     return false;
/*      */   }
/*      */ 
/*      */   public byte getByte(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  220 */       if (this.wrappedStmt != null) {
/*  221 */         return ((CallableStatement)this.wrappedStmt).getByte(parameterIndex);
/*      */       }
/*      */ 
/*  224 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  229 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  232 */     return 0;
/*      */   }
/*      */ 
/*      */   public short getShort(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  242 */       if (this.wrappedStmt != null) {
/*  243 */         return ((CallableStatement)this.wrappedStmt).getShort(parameterIndex);
/*      */       }
/*      */ 
/*  246 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  251 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  254 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getInt(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  264 */       if (this.wrappedStmt != null) {
/*  265 */         return ((CallableStatement)this.wrappedStmt).getInt(parameterIndex);
/*      */       }
/*      */ 
/*  268 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  273 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  276 */     return 0;
/*      */   }
/*      */ 
/*      */   public long getLong(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  286 */       if (this.wrappedStmt != null) {
/*  287 */         return ((CallableStatement)this.wrappedStmt).getLong(parameterIndex);
/*      */       }
/*      */ 
/*  290 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  295 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  298 */     return 0L;
/*      */   }
/*      */ 
/*      */   public float getFloat(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  308 */       if (this.wrappedStmt != null) {
/*  309 */         return ((CallableStatement)this.wrappedStmt).getFloat(parameterIndex);
/*      */       }
/*      */ 
/*  312 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  317 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  320 */     return 0.0F;
/*      */   }
/*      */ 
/*      */   public double getDouble(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  330 */       if (this.wrappedStmt != null) {
/*  331 */         return ((CallableStatement)this.wrappedStmt).getDouble(parameterIndex);
/*      */       }
/*      */ 
/*  334 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  339 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  342 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int parameterIndex, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  353 */       if (this.wrappedStmt != null) {
/*  354 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex, scale);
/*      */       }
/*      */ 
/*  357 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  362 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  365 */     return null;
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  375 */       if (this.wrappedStmt != null) {
/*  376 */         return ((CallableStatement)this.wrappedStmt).getBytes(parameterIndex);
/*      */       }
/*      */ 
/*  379 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  384 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  387 */     return null;
/*      */   }
/*      */ 
/*      */   public Date getDate(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  397 */       if (this.wrappedStmt != null) {
/*  398 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterIndex);
/*      */       }
/*      */ 
/*  401 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  406 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  409 */     return null;
/*      */   }
/*      */ 
/*      */   public Time getTime(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  419 */       if (this.wrappedStmt != null) {
/*  420 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterIndex);
/*      */       }
/*      */ 
/*  423 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  428 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  431 */     return null;
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  441 */       if (this.wrappedStmt != null) {
/*  442 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex);
/*      */       }
/*      */ 
/*  445 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  450 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  453 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getObject(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  463 */       if (this.wrappedStmt != null) {
/*  464 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterIndex);
/*      */       }
/*      */ 
/*  467 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  472 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  475 */     return null;
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  485 */       if (this.wrappedStmt != null) {
/*  486 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex);
/*      */       }
/*      */ 
/*  489 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  494 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  497 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getObject(int parameterIndex, Map typeMap)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  508 */       if (this.wrappedStmt != null) {
/*  509 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterIndex, typeMap);
/*      */       }
/*      */ 
/*  512 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  517 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  519 */     return null;
/*      */   }
/*      */ 
/*      */   public Ref getRef(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  529 */       if (this.wrappedStmt != null) {
/*  530 */         return ((CallableStatement)this.wrappedStmt).getRef(parameterIndex);
/*      */       }
/*      */ 
/*  533 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  538 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  541 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob getBlob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  551 */       if (this.wrappedStmt != null) {
/*  552 */         return ((CallableStatement)this.wrappedStmt).getBlob(parameterIndex);
/*      */       }
/*      */ 
/*  555 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  560 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  563 */     return null;
/*      */   }
/*      */ 
/*      */   public Clob getClob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  573 */       if (this.wrappedStmt != null) {
/*  574 */         return ((CallableStatement)this.wrappedStmt).getClob(parameterIndex);
/*      */       }
/*      */ 
/*  577 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  582 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  584 */     return null;
/*      */   }
/*      */ 
/*      */   public Array getArray(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  594 */       if (this.wrappedStmt != null) {
/*  595 */         return ((CallableStatement)this.wrappedStmt).getArray(parameterIndex);
/*      */       }
/*      */ 
/*  598 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  603 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  605 */     return null;
/*      */   }
/*      */ 
/*      */   public Date getDate(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  615 */       if (this.wrappedStmt != null) {
/*  616 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterIndex, cal);
/*      */       }
/*      */ 
/*  619 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  624 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  626 */     return null;
/*      */   }
/*      */ 
/*      */   public Time getTime(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  636 */       if (this.wrappedStmt != null) {
/*  637 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterIndex, cal);
/*      */       }
/*      */ 
/*  640 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  645 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  647 */     return null;
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  658 */       if (this.wrappedStmt != null) {
/*  659 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex, cal);
/*      */       }
/*      */ 
/*  662 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  667 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  669 */     return null;
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int paramIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  681 */       if (this.wrappedStmt != null) {
/*  682 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(paramIndex, sqlType, typeName);
/*      */       }
/*      */       else {
/*  685 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  690 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  703 */       if (this.wrappedStmt != null) {
/*  704 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType);
/*      */       }
/*      */       else {
/*  707 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  712 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  725 */       if (this.wrappedStmt != null) {
/*  726 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, scale);
/*      */       }
/*      */       else {
/*  729 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  734 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  747 */       if (this.wrappedStmt != null) {
/*  748 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, typeName);
/*      */       }
/*      */       else {
/*  751 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  756 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public URL getURL(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  767 */       if (this.wrappedStmt != null) {
/*  768 */         return ((CallableStatement)this.wrappedStmt).getURL(parameterIndex);
/*      */       }
/*      */ 
/*  771 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  776 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  779 */     return null;
/*      */   }
/*      */ 
/*      */   public void setURL(String parameterName, URL val)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  789 */       if (this.wrappedStmt != null) {
/*  790 */         ((CallableStatement)this.wrappedStmt).setURL(parameterName, val);
/*      */       }
/*      */       else {
/*  793 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  798 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  809 */       if (this.wrappedStmt != null) {
/*  810 */         ((CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType);
/*      */       }
/*      */       else {
/*  813 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  818 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBoolean(String parameterName, boolean x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  829 */       if (this.wrappedStmt != null) {
/*  830 */         ((CallableStatement)this.wrappedStmt).setBoolean(parameterName, x);
/*      */       }
/*      */       else {
/*  833 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  838 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setByte(String parameterName, byte x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  849 */       if (this.wrappedStmt != null) {
/*  850 */         ((CallableStatement)this.wrappedStmt).setByte(parameterName, x);
/*      */       }
/*      */       else {
/*  853 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  858 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(String parameterName, short x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  869 */       if (this.wrappedStmt != null) {
/*  870 */         ((CallableStatement)this.wrappedStmt).setShort(parameterName, x);
/*      */       }
/*      */       else {
/*  873 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  878 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setInt(String parameterName, int x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  889 */       if (this.wrappedStmt != null)
/*  890 */         ((CallableStatement)this.wrappedStmt).setInt(parameterName, x);
/*      */       else {
/*  892 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  897 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLong(String parameterName, long x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  908 */       if (this.wrappedStmt != null) {
/*  909 */         ((CallableStatement)this.wrappedStmt).setLong(parameterName, x);
/*      */       }
/*      */       else {
/*  912 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  917 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFloat(String parameterName, float x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  928 */       if (this.wrappedStmt != null) {
/*  929 */         ((CallableStatement)this.wrappedStmt).setFloat(parameterName, x);
/*      */       }
/*      */       else {
/*  932 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  937 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDouble(String parameterName, double x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  948 */       if (this.wrappedStmt != null) {
/*  949 */         ((CallableStatement)this.wrappedStmt).setDouble(parameterName, x);
/*      */       }
/*      */       else {
/*  952 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  957 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(String parameterName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  970 */       if (this.wrappedStmt != null) {
/*  971 */         ((CallableStatement)this.wrappedStmt).setBigDecimal(parameterName, x);
/*      */       }
/*      */       else {
/*  974 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  979 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setString(String parameterName, String x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  991 */       if (this.wrappedStmt != null) {
/*  992 */         ((CallableStatement)this.wrappedStmt).setString(parameterName, x);
/*      */       }
/*      */       else {
/*  995 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1000 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBytes(String parameterName, byte[] x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1011 */       if (this.wrappedStmt != null) {
/* 1012 */         ((CallableStatement)this.wrappedStmt).setBytes(parameterName, x);
/*      */       }
/*      */       else {
/* 1015 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1020 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(String parameterName, Date x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1031 */       if (this.wrappedStmt != null) {
/* 1032 */         ((CallableStatement)this.wrappedStmt).setDate(parameterName, x);
/*      */       }
/*      */       else {
/* 1035 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1040 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(String parameterName, Time x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1051 */       if (this.wrappedStmt != null) {
/* 1052 */         ((CallableStatement)this.wrappedStmt).setTime(parameterName, x);
/*      */       }
/*      */       else {
/* 1055 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1060 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1073 */       if (this.wrappedStmt != null) {
/* 1074 */         ((CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x);
/*      */       }
/*      */       else {
/* 1077 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1082 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1095 */       if (this.wrappedStmt != null) {
/* 1096 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
/*      */       }
/*      */       else {
/* 1099 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1104 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1118 */       if (this.wrappedStmt != null) {
/* 1119 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
/*      */       }
/*      */       else {
/* 1122 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1127 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1140 */       if (this.wrappedStmt != null) {
/* 1141 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType, scale);
/*      */       }
/*      */       else {
/* 1144 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1149 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1162 */       if (this.wrappedStmt != null) {
/* 1163 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType);
/*      */       }
/*      */       else {
/* 1166 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1171 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1183 */       if (this.wrappedStmt != null) {
/* 1184 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x);
/*      */       }
/*      */       else {
/* 1187 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1192 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1205 */       if (this.wrappedStmt != null) {
/* 1206 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
/*      */       }
/*      */       else {
/* 1209 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1214 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(String parameterName, Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1227 */       if (this.wrappedStmt != null) {
/* 1228 */         ((CallableStatement)this.wrappedStmt).setDate(parameterName, x, cal);
/*      */       }
/*      */       else {
/* 1231 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1236 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(String parameterName, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1249 */       if (this.wrappedStmt != null) {
/* 1250 */         ((CallableStatement)this.wrappedStmt).setTime(parameterName, x, cal);
/*      */       }
/*      */       else {
/* 1253 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1258 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1271 */       if (this.wrappedStmt != null) {
/* 1272 */         ((CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x, cal);
/*      */       }
/*      */       else {
/* 1275 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1280 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1293 */       if (this.wrappedStmt != null) {
/* 1294 */         ((CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType, typeName);
/*      */       }
/*      */       else {
/* 1297 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1302 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getString(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1313 */       if (this.wrappedStmt != null) {
/* 1314 */         return ((CallableStatement)this.wrappedStmt).getString(parameterName);
/*      */       }
/*      */ 
/* 1317 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1322 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1324 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1334 */       if (this.wrappedStmt != null) {
/* 1335 */         return ((CallableStatement)this.wrappedStmt).getBoolean(parameterName);
/*      */       }
/*      */ 
/* 1338 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1343 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1346 */     return false;
/*      */   }
/*      */ 
/*      */   public byte getByte(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1356 */       if (this.wrappedStmt != null) {
/* 1357 */         return ((CallableStatement)this.wrappedStmt).getByte(parameterName);
/*      */       }
/*      */ 
/* 1360 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1365 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1368 */     return 0;
/*      */   }
/*      */ 
/*      */   public short getShort(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1378 */       if (this.wrappedStmt != null) {
/* 1379 */         return ((CallableStatement)this.wrappedStmt).getShort(parameterName);
/*      */       }
/*      */ 
/* 1382 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1387 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1390 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getInt(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1400 */       if (this.wrappedStmt != null) {
/* 1401 */         return ((CallableStatement)this.wrappedStmt).getInt(parameterName);
/*      */       }
/*      */ 
/* 1404 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1409 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1412 */     return 0;
/*      */   }
/*      */ 
/*      */   public long getLong(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1422 */       if (this.wrappedStmt != null) {
/* 1423 */         return ((CallableStatement)this.wrappedStmt).getLong(parameterName);
/*      */       }
/*      */ 
/* 1426 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1431 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1434 */     return 0L;
/*      */   }
/*      */ 
/*      */   public float getFloat(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1444 */       if (this.wrappedStmt != null) {
/* 1445 */         return ((CallableStatement)this.wrappedStmt).getFloat(parameterName);
/*      */       }
/*      */ 
/* 1448 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1453 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1456 */     return 0.0F;
/*      */   }
/*      */ 
/*      */   public double getDouble(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1466 */       if (this.wrappedStmt != null) {
/* 1467 */         return ((CallableStatement)this.wrappedStmt).getDouble(parameterName);
/*      */       }
/*      */ 
/* 1470 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1475 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1478 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1488 */       if (this.wrappedStmt != null) {
/* 1489 */         return ((CallableStatement)this.wrappedStmt).getBytes(parameterName);
/*      */       }
/*      */ 
/* 1492 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1497 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1500 */     return null;
/*      */   }
/*      */ 
/*      */   public Date getDate(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1510 */       if (this.wrappedStmt != null) {
/* 1511 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterName);
/*      */       }
/*      */ 
/* 1514 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1519 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1522 */     return null;
/*      */   }
/*      */ 
/*      */   public Time getTime(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1532 */       if (this.wrappedStmt != null) {
/* 1533 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterName);
/*      */       }
/*      */ 
/* 1536 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1541 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1544 */     return null;
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1554 */       if (this.wrappedStmt != null) {
/* 1555 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterName);
/*      */       }
/*      */ 
/* 1558 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1563 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1566 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getObject(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1576 */       if (this.wrappedStmt != null) {
/* 1577 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterName);
/*      */       }
/*      */ 
/* 1580 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1585 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1588 */     return null;
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1598 */       if (this.wrappedStmt != null) {
/* 1599 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterName);
/*      */       }
/*      */ 
/* 1602 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1607 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1610 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getObject(String parameterName, Map typeMap)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1621 */       if (this.wrappedStmt != null) {
/* 1622 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterName, typeMap);
/*      */       }
/*      */ 
/* 1625 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1630 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1632 */     return null;
/*      */   }
/*      */ 
/*      */   public Ref getRef(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1642 */       if (this.wrappedStmt != null) {
/* 1643 */         return ((CallableStatement)this.wrappedStmt).getRef(parameterName);
/*      */       }
/*      */ 
/* 1646 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1651 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1654 */     return null;
/*      */   }
/*      */ 
/*      */   public Blob getBlob(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1664 */       if (this.wrappedStmt != null) {
/* 1665 */         return ((CallableStatement)this.wrappedStmt).getBlob(parameterName);
/*      */       }
/*      */ 
/* 1668 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1673 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1676 */     return null;
/*      */   }
/*      */ 
/*      */   public Clob getClob(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1686 */       if (this.wrappedStmt != null) {
/* 1687 */         return ((CallableStatement)this.wrappedStmt).getClob(parameterName);
/*      */       }
/*      */ 
/* 1690 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1695 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1697 */     return null;
/*      */   }
/*      */ 
/*      */   public Array getArray(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1707 */       if (this.wrappedStmt != null) {
/* 1708 */         return ((CallableStatement)this.wrappedStmt).getArray(parameterName);
/*      */       }
/*      */ 
/* 1711 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1716 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1718 */     return null;
/*      */   }
/*      */ 
/*      */   public Date getDate(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1728 */       if (this.wrappedStmt != null) {
/* 1729 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterName, cal);
/*      */       }
/*      */ 
/* 1732 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1737 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1739 */     return null;
/*      */   }
/*      */ 
/*      */   public Time getTime(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1749 */       if (this.wrappedStmt != null) {
/* 1750 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterName, cal);
/*      */       }
/*      */ 
/* 1753 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1758 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1760 */     return null;
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1771 */       if (this.wrappedStmt != null) {
/* 1772 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterName, cal);
/*      */       }
/*      */ 
/* 1775 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1780 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1782 */     return null;
/*      */   }
/*      */ 
/*      */   public URL getURL(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1792 */       if (this.wrappedStmt != null) {
/* 1793 */         return ((CallableStatement)this.wrappedStmt).getURL(parameterName);
/*      */       }
/*      */ 
/* 1796 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1801 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1804 */     return null;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   62 */     if (Util.isJdbc4())
/*      */       try {
/*   64 */         JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4CallableStatementWrapper").getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, CallableStatement.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*   70 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   72 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   74 */         throw new RuntimeException(e);
/*      */       }
/*      */     else
/*   77 */       JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR = null;
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.CallableStatementWrapper
 * JD-Core Version:    0.6.0
 */