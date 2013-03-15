/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.NClob;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ 
/*     */ public class JDBC4ResultSet extends ResultSetImpl
/*     */ {
/*     */   public JDBC4ResultSet(long updateCount, long updateID, ConnectionImpl conn, StatementImpl creatorStmt)
/*     */   {
/*  50 */     super(updateCount, updateID, conn, creatorStmt);
/*     */   }
/*     */ 
/*     */   public JDBC4ResultSet(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt) throws SQLException
/*     */   {
/*  55 */     super(catalog, fields, tuples, conn, creatorStmt);
/*     */   }
/*     */ 
/*     */   public Reader getNCharacterStream(int columnIndex)
/*     */     throws SQLException
/*     */   {
/*  74 */     checkColumnBounds(columnIndex);
/*     */ 
/*  76 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/*  77 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/*  78 */       throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/*  81 */     return getCharacterStream(columnIndex);
/*     */   }
/*     */ 
/*     */   public Reader getNCharacterStream(String columnName)
/*     */     throws SQLException
/*     */   {
/* 100 */     return getNCharacterStream(findColumn(columnName));
/*     */   }
/*     */ 
/*     */   public NClob getNClob(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 115 */     checkColumnBounds(columnIndex);
/*     */ 
/* 117 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/* 118 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 119 */       throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/* 122 */     if (!this.isBinaryEncoded) {
/* 123 */       String asString = getStringForNClob(columnIndex);
/*     */ 
/* 125 */       if (asString == null) {
/* 126 */         return null;
/*     */       }
/*     */ 
/* 129 */       return new JDBC4NClob(asString, getExceptionInterceptor());
/*     */     }
/*     */ 
/* 132 */     return getNativeNClob(columnIndex);
/*     */   }
/*     */ 
/*     */   public NClob getNClob(String columnName)
/*     */     throws SQLException
/*     */   {
/* 147 */     return getNClob(findColumn(columnName));
/*     */   }
/*     */ 
/*     */   protected NClob getNativeNClob(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 163 */     String stringVal = getStringForNClob(columnIndex);
/*     */ 
/* 165 */     if (stringVal == null) {
/* 166 */       return null;
/*     */     }
/*     */ 
/* 169 */     return getNClobFromString(stringVal, columnIndex);
/*     */   }
/*     */ 
/*     */   private String getStringForNClob(int columnIndex) throws SQLException {
/* 173 */     String asString = null;
/*     */ 
/* 175 */     String forcedEncoding = "UTF-8";
/*     */     try
/*     */     {
/* 178 */       byte[] asBytes = null;
/*     */ 
/* 180 */       if (!this.isBinaryEncoded)
/* 181 */         asBytes = getBytes(columnIndex);
/*     */       else {
/* 183 */         asBytes = getNativeBytes(columnIndex, true);
/*     */       }
/*     */ 
/* 186 */       if (asBytes != null)
/* 187 */         asString = new String(asBytes, forcedEncoding);
/*     */     }
/*     */     catch (UnsupportedEncodingException uee) {
/* 190 */       throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*     */     }
/*     */ 
/* 194 */     return asString;
/*     */   }
/*     */ 
/*     */   private final NClob getNClobFromString(String stringVal, int columnIndex) throws SQLException
/*     */   {
/* 199 */     return new JDBC4NClob(stringVal, getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public String getNString(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 216 */     checkColumnBounds(columnIndex);
/*     */ 
/* 218 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/* 219 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 220 */       throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/* 223 */     return getString(columnIndex);
/*     */   }
/*     */ 
/*     */   public String getNString(String columnName)
/*     */     throws SQLException
/*     */   {
/* 241 */     return getNString(findColumn(columnName));
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, int length)
/*     */     throws SQLException
/*     */   {
/* 265 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(String columnName, Reader reader, int length)
/*     */     throws SQLException
/*     */   {
/* 287 */     updateNCharacterStream(findColumn(columnName), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateNClob(String columnName, NClob nClob)
/*     */     throws SQLException
/*     */   {
/* 294 */     updateNClob(findColumn(columnName), nClob);
/*     */   }
/*     */ 
/*     */   public void updateRowId(int columnIndex, RowId x) throws SQLException {
/* 298 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateRowId(String columnName, RowId x) throws SQLException {
/* 302 */     updateRowId(findColumn(columnName), x);
/*     */   }
/*     */ 
/*     */   public int getHoldability() throws SQLException {
/* 306 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public RowId getRowId(int columnIndex) throws SQLException {
/* 310 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public RowId getRowId(String columnLabel) throws SQLException {
/* 314 */     return getRowId(findColumn(columnLabel));
/*     */   }
/*     */ 
/*     */   public SQLXML getSQLXML(int columnIndex) throws SQLException {
/* 318 */     checkColumnBounds(columnIndex);
/*     */ 
/* 320 */     return new JDBC4MysqlSQLXML(this, columnIndex, getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public SQLXML getSQLXML(String columnLabel) throws SQLException {
/* 324 */     return getSQLXML(findColumn(columnLabel));
/*     */   }
/*     */ 
/*     */   public synchronized boolean isClosed() throws SQLException {
/* 328 */     return this.isClosed;
/*     */   }
/*     */ 
/*     */   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
/* 332 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
/*     */   {
/* 337 */     updateAsciiStream(findColumn(columnLabel), x);
/*     */   }
/*     */ 
/*     */   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
/*     */   {
/* 342 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
/*     */   {
/* 347 */     updateAsciiStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */ 
/*     */   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
/* 351 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
/*     */   {
/* 356 */     updateBinaryStream(findColumn(columnLabel), x);
/*     */   }
/*     */ 
/*     */   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
/* 360 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
/*     */   {
/* 365 */     updateBinaryStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */ 
/*     */   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
/* 369 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
/* 373 */     updateBlob(findColumn(columnLabel), inputStream);
/*     */   }
/*     */ 
/*     */   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
/* 377 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
/*     */   {
/* 382 */     updateBlob(findColumn(columnLabel), inputStream, length);
/*     */   }
/*     */ 
/*     */   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
/* 386 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 391 */     updateCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/* 395 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 400 */     updateCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateClob(int columnIndex, Reader reader) throws SQLException {
/* 404 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateClob(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 409 */     updateClob(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
/* 413 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 418 */     updateClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
/* 422 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 427 */     updateNCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
/*     */   {
/* 432 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 437 */     updateNCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
/* 441 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNClob(int columnIndex, Reader reader) throws SQLException
/*     */   {
/* 446 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNClob(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 451 */     updateNClob(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
/*     */   {
/* 456 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
/* 460 */     updateNClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateNString(int columnIndex, String nString) throws SQLException {
/* 464 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNString(String columnLabel, String nString) throws SQLException
/*     */   {
/* 469 */     updateNString(findColumn(columnLabel), nString);
/*     */   }
/*     */ 
/*     */   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
/* 473 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
/*     */   {
/* 478 */     updateSQLXML(findColumn(columnLabel), xmlObject);
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 498 */     checkClosed();
/*     */ 
/* 502 */     return iface.isInstance(this);
/*     */   }
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 523 */       return iface.cast(this); } catch (ClassCastException cce) {
/*     */     }
/* 525 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4ResultSet
 * JD-Core Version:    0.6.0
 */