/*      */ package com.mysql.jdbc.jdbc2.optional;
/*      */ 
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.NClob;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Statement;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ 
/*      */ public class JDBC4CallableStatementWrapper extends CallableStatementWrapper
/*      */ {
/*      */   public JDBC4CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap)
/*      */   {
/*   65 */     super(c, conn, toWrap);
/*      */   }
/*      */ 
/*      */   public void close() throws SQLException {
/*      */     try {
/*   70 */       super.close();
/*      */     } finally {
/*   72 */       this.unwrappedInterfaces = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isClosed() throws SQLException {
/*      */     try {
/*   78 */       if (this.wrappedStmt != null) {
/*   79 */         return this.wrappedStmt.isClosed();
/*      */       }
/*   81 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*   85 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*   88 */     return false;
/*      */   }
/*      */ 
/*      */   public void setPoolable(boolean poolable) throws SQLException {
/*      */     try {
/*   93 */       if (this.wrappedStmt != null)
/*   94 */         this.wrappedStmt.setPoolable(poolable);
/*      */       else
/*   96 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  100 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isPoolable() throws SQLException {
/*      */     try {
/*  106 */       if (this.wrappedStmt != null) {
/*  107 */         return this.wrappedStmt.isPoolable();
/*      */       }
/*  109 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  113 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  116 */     return false;
/*      */   }
/*      */ 
/*      */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*      */     try {
/*  121 */       if (this.wrappedStmt != null) {
/*  122 */         ((PreparedStatement)this.wrappedStmt).setRowId(parameterIndex, x);
/*      */       }
/*      */       else {
/*  125 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  130 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/*      */     try {
/*  136 */       if (this.wrappedStmt != null) {
/*  137 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, value);
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
/*      */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
/*      */   {
/*      */     try {
/*  152 */       if (this.wrappedStmt != null) {
/*  153 */         ((PreparedStatement)this.wrappedStmt).setSQLXML(parameterIndex, xmlObject);
/*      */       }
/*      */       else {
/*  156 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  161 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNString(int parameterIndex, String value)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  170 */       if (this.wrappedStmt != null) {
/*  171 */         ((PreparedStatement)this.wrappedStmt).setNString(parameterIndex, value);
/*      */       }
/*      */       else {
/*  174 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  179 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(int parameterIndex, Reader value, long length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  188 */       if (this.wrappedStmt != null) {
/*  189 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value, length);
/*      */       }
/*      */       else {
/*  192 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  197 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  206 */       if (this.wrappedStmt != null) {
/*  207 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader, length);
/*      */       }
/*      */       else {
/*  210 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  215 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, InputStream inputStream, long length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  224 */       if (this.wrappedStmt != null) {
/*  225 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream, length);
/*      */       }
/*      */       else {
/*  228 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  233 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNClob(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  242 */       if (this.wrappedStmt != null) {
/*  243 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader, length);
/*      */       }
/*      */       else {
/*  246 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  251 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, long length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  260 */       if (this.wrappedStmt != null) {
/*  261 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*      */       }
/*      */       else {
/*  264 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  269 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, long length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  278 */       if (this.wrappedStmt != null) {
/*  279 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*      */       }
/*      */       else {
/*  282 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  287 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  296 */       if (this.wrappedStmt != null) {
/*  297 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*      */       }
/*      */       else {
/*  300 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  305 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  313 */       if (this.wrappedStmt != null) {
/*  314 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x);
/*      */       }
/*      */       else {
/*  317 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  322 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  330 */       if (this.wrappedStmt != null) {
/*  331 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x);
/*      */       }
/*      */       else {
/*  334 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  339 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  347 */       if (this.wrappedStmt != null) {
/*  348 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader);
/*      */       }
/*      */       else {
/*  351 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  356 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(int parameterIndex, Reader value)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  365 */       if (this.wrappedStmt != null) {
/*  366 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value);
/*      */       }
/*      */       else {
/*  369 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  374 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Reader reader)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  383 */       if (this.wrappedStmt != null) {
/*  384 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader);
/*      */       }
/*      */       else {
/*  387 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  392 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, InputStream inputStream)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  401 */       if (this.wrappedStmt != null) {
/*  402 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream);
/*      */       }
/*      */       else {
/*  405 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  410 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNClob(int parameterIndex, Reader reader) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  418 */       if (this.wrappedStmt != null) {
/*  419 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader);
/*      */       }
/*      */       else {
/*  422 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  427 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class<?> iface)
/*      */     throws SQLException
/*      */   {
/*  455 */     boolean isInstance = iface.isInstance(this);
/*      */ 
/*  457 */     if (isInstance) {
/*  458 */       return true;
/*      */     }
/*      */ 
/*  461 */     String interfaceClassName = iface.getName();
/*      */ 
/*  463 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement")) || (interfaceClassName.equals("java.sql.Statement")) || (interfaceClassName.equals("java.sql.PreparedStatement")) || (interfaceClassName.equals("java.sql.Wrapper"));
/*      */   }
/*      */ 
/*      */   public synchronized <T> T unwrap(Class<T> iface)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  491 */       if (("java.sql.Statement".equals(iface.getName())) || ("java.sql.PreparedStatement".equals(iface.getName())) || ("java.sql.Wrapper.class".equals(iface.getName())))
/*      */       {
/*  494 */         return iface.cast(this);
/*      */       }
/*      */ 
/*  497 */       if (this.unwrappedInterfaces == null) {
/*  498 */         this.unwrappedInterfaces = new HashMap();
/*      */       }
/*      */ 
/*  501 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*      */ 
/*  503 */       if (cachedUnwrapped == null) {
/*  504 */         if (cachedUnwrapped == null) {
/*  505 */           cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new WrapperBase.ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*      */ 
/*  509 */           this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*      */         }
/*  511 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*      */       }
/*      */ 
/*  514 */       return iface.cast(cachedUnwrapped); } catch (ClassCastException cce) {
/*      */     }
/*  516 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public void setRowId(String parameterName, RowId x) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  523 */       if (this.wrappedStmt != null)
/*  524 */         ((CallableStatement)this.wrappedStmt).setRowId(parameterName, x);
/*      */       else {
/*  526 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  531 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
/*      */     try {
/*  537 */       if (this.wrappedStmt != null)
/*  538 */         ((CallableStatement)this.wrappedStmt).setSQLXML(parameterName, xmlObject);
/*      */       else {
/*  540 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  545 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public SQLXML getSQLXML(int parameterIndex) throws SQLException {
/*      */     try {
/*  551 */       if (this.wrappedStmt != null) {
/*  552 */         return ((CallableStatement)this.wrappedStmt).getSQLXML(parameterIndex);
/*      */       }
/*  554 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  559 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  562 */     return null;
/*      */   }
/*      */ 
/*      */   public SQLXML getSQLXML(String parameterName) throws SQLException
/*      */   {
/*      */     try {
/*  568 */       if (this.wrappedStmt != null) {
/*  569 */         return ((CallableStatement)this.wrappedStmt).getSQLXML(parameterName);
/*      */       }
/*  571 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  576 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  579 */     return null;
/*      */   }
/*      */ 
/*      */   public RowId getRowId(String parameterName) throws SQLException {
/*      */     try {
/*  584 */       if (this.wrappedStmt != null) {
/*  585 */         return ((CallableStatement)this.wrappedStmt).getRowId(parameterName);
/*      */       }
/*  587 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  592 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  595 */     return null;
/*      */   }
/*      */ 
/*      */   public void setNClob(String parameterName, NClob value) throws SQLException {
/*      */     try {
/*  600 */       if (this.wrappedStmt != null)
/*  601 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, value);
/*      */       else {
/*  603 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  608 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNClob(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/*  614 */       if (this.wrappedStmt != null)
/*  615 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, reader);
/*      */       else {
/*  617 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  622 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/*  628 */       if (this.wrappedStmt != null)
/*  629 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, reader, length);
/*      */       else {
/*  631 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  636 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNString(String parameterName, String value) throws SQLException {
/*      */     try {
/*  642 */       if (this.wrappedStmt != null)
/*  643 */         ((CallableStatement)this.wrappedStmt).setNString(parameterName, value);
/*      */       else {
/*  645 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  650 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  659 */       if (this.wrappedStmt != null) {
/*  660 */         return ((CallableStatement)this.wrappedStmt).getCharacterStream(parameterIndex);
/*      */       }
/*  662 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  667 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  670 */     return null;
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  678 */       if (this.wrappedStmt != null) {
/*  679 */         return ((CallableStatement)this.wrappedStmt).getCharacterStream(parameterName);
/*      */       }
/*  681 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  686 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  689 */     return null;
/*      */   }
/*      */ 
/*      */   public Reader getNCharacterStream(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  697 */       if (this.wrappedStmt != null) {
/*  698 */         return ((CallableStatement)this.wrappedStmt).getNCharacterStream(parameterIndex);
/*      */       }
/*  700 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  705 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  708 */     return null;
/*      */   }
/*      */ 
/*      */   public Reader getNCharacterStream(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  716 */       if (this.wrappedStmt != null) {
/*  717 */         return ((CallableStatement)this.wrappedStmt).getNCharacterStream(parameterName);
/*      */       }
/*  719 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  724 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  727 */     return null;
/*      */   }
/*      */ 
/*      */   public NClob getNClob(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  735 */       if (this.wrappedStmt != null) {
/*  736 */         return ((CallableStatement)this.wrappedStmt).getNClob(parameterName);
/*      */       }
/*  738 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  743 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  746 */     return null;
/*      */   }
/*      */ 
/*      */   public String getNString(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  754 */       if (this.wrappedStmt != null) {
/*  755 */         return ((CallableStatement)this.wrappedStmt).getNString(parameterName);
/*      */       }
/*  757 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  762 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  765 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/*  770 */       if (this.wrappedStmt != null)
/*  771 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x);
/*      */       else {
/*  773 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  778 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/*  784 */       if (this.wrappedStmt != null)
/*  785 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
/*      */       else {
/*  787 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  792 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/*  798 */       if (this.wrappedStmt != null)
/*  799 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x);
/*      */       else {
/*  801 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  806 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/*  812 */       if (this.wrappedStmt != null)
/*  813 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
/*      */       else {
/*  815 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  820 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/*  826 */       if (this.wrappedStmt != null)
/*  827 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
/*      */       else {
/*  829 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  834 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/*  840 */       if (this.wrappedStmt != null)
/*  841 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x, length);
/*      */       else {
/*  843 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  848 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, Blob x) throws SQLException {
/*      */     try {
/*  854 */       if (this.wrappedStmt != null)
/*  855 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
/*      */       else {
/*  857 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  862 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/*  868 */       if (this.wrappedStmt != null)
/*  869 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader);
/*      */       else {
/*  871 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  876 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/*  882 */       if (this.wrappedStmt != null)
/*  883 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
/*      */       else {
/*  885 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  890 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Clob x) throws SQLException {
/*      */     try {
/*  896 */       if (this.wrappedStmt != null)
/*  897 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, x);
/*      */       else {
/*  899 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  904 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/*  910 */       if (this.wrappedStmt != null)
/*  911 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, reader);
/*      */       else {
/*  913 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  918 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/*  924 */       if (this.wrappedStmt != null)
/*  925 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, reader, length);
/*      */       else {
/*  927 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  932 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/*  938 */       if (this.wrappedStmt != null)
/*  939 */         ((CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader);
/*      */       else {
/*  941 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  946 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/*  952 */       if (this.wrappedStmt != null)
/*  953 */         ((CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader, length);
/*      */       else {
/*  955 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  960 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public NClob getNClob(int parameterIndex) throws SQLException {
/*      */     try {
/*  966 */       if (this.wrappedStmt != null) {
/*  967 */         return ((CallableStatement)this.wrappedStmt).getNClob(parameterIndex);
/*      */       }
/*  969 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  974 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  977 */     return null;
/*      */   }
/*      */ 
/*      */   public String getNString(int parameterIndex) throws SQLException {
/*      */     try {
/*  982 */       if (this.wrappedStmt != null) {
/*  983 */         return ((CallableStatement)this.wrappedStmt).getNString(parameterIndex);
/*      */       }
/*  985 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  990 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/*  993 */     return null;
/*      */   }
/*      */ 
/*      */   public RowId getRowId(int parameterIndex) throws SQLException {
/*      */     try {
/*  998 */       if (this.wrappedStmt != null) {
/*  999 */         return ((CallableStatement)this.wrappedStmt).getRowId(parameterIndex);
/*      */       }
/* 1001 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 1006 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */ 
/* 1009 */     return null;
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.JDBC4CallableStatementWrapper
 * JD-Core Version:    0.6.0
 */