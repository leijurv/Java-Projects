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
/*     */ public class JDBC4UpdatableResultSet extends UpdatableResultSet
/*     */ {
/*     */   public JDBC4UpdatableResultSet(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt)
/*     */     throws SQLException
/*     */   {
/*  50 */     super(catalog, fields, tuples, conn, creatorStmt);
/*     */   }
/*     */ 
/*     */   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
/*  54 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
/*     */   {
/*  59 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
/*     */   {
/*  64 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
/*     */   {
/*  69 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
/*     */   {
/*  74 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
/*  78 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
/*     */   {
/*  83 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateCharacterStream(int columnIndex, Reader x, long length)
/*     */     throws SQLException
/*     */   {
/*  89 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateClob(int columnIndex, Reader reader) throws SQLException
/*     */   {
/*  94 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
/*     */   {
/*  99 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
/*     */   {
/* 104 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
/*     */   {
/* 109 */     updateNCharacterStream(columnIndex, x, (int)length);
/*     */   }
/*     */ 
/*     */   public void updateNClob(int columnIndex, Reader reader)
/*     */     throws SQLException
/*     */   {
/* 115 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
/*     */   {
/* 120 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
/* 124 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateRowId(int columnIndex, RowId x) throws SQLException
/*     */   {
/* 129 */     throw new NotUpdatable();
/*     */   }
/*     */ 
/*     */   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
/* 133 */     updateAsciiStream(findColumn(columnLabel), x);
/*     */   }
/*     */ 
/*     */   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
/* 137 */     updateAsciiStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */ 
/*     */   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
/* 141 */     updateBinaryStream(findColumn(columnLabel), x);
/*     */   }
/*     */ 
/*     */   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
/* 145 */     updateBinaryStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */ 
/*     */   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
/* 149 */     updateBlob(findColumn(columnLabel), inputStream);
/*     */   }
/*     */ 
/*     */   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
/* 153 */     updateBlob(findColumn(columnLabel), inputStream, length);
/*     */   }
/*     */ 
/*     */   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
/* 157 */     updateCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/* 161 */     updateCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateClob(String columnLabel, Reader reader) throws SQLException {
/* 165 */     updateClob(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
/* 169 */     updateClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
/* 173 */     updateNCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 178 */     updateNCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateNClob(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 183 */     updateNClob(findColumn(columnLabel), reader);
/*     */   }
/*     */ 
/*     */   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 188 */     updateNClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
/* 192 */     updateSQLXML(findColumn(columnLabel), xmlObject);
/*     */   }
/*     */ 
/*     */   public synchronized void updateNCharacterStream(int columnIndex, Reader x, int length)
/*     */     throws SQLException
/*     */   {
/* 215 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/* 216 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 217 */       throw new SQLException("Can not call updateNCharacterStream() when field's character set isn't UTF-8");
/*     */     }
/*     */ 
/* 221 */     if (!this.onInsertRow) {
/* 222 */       if (!this.doingUpdates) {
/* 223 */         this.doingUpdates = true;
/* 224 */         syncUpdate();
/*     */       }
/*     */ 
/* 227 */       ((JDBC4PreparedStatement)this.updater).setNCharacterStream(columnIndex, x, length);
/*     */     } else {
/* 229 */       ((JDBC4PreparedStatement)this.inserter).setNCharacterStream(columnIndex, x, length);
/*     */ 
/* 231 */       if (x == null)
/* 232 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*     */       else
/* 234 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void updateNCharacterStream(String columnName, Reader reader, int length)
/*     */     throws SQLException
/*     */   {
/* 258 */     updateNCharacterStream(findColumn(columnName), reader, length);
/*     */   }
/*     */ 
/*     */   public void updateNClob(int columnIndex, NClob nClob)
/*     */     throws SQLException
/*     */   {
/* 266 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/* 267 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 268 */       throw new SQLException("Can not call updateNClob() when field's character set isn't UTF-8");
/*     */     }
/*     */ 
/* 271 */     if (nClob == null)
/* 272 */       updateNull(columnIndex);
/*     */     else
/* 274 */       updateNCharacterStream(columnIndex, nClob.getCharacterStream(), (int)nClob.length());
/*     */   }
/*     */ 
/*     */   public void updateNClob(String columnName, NClob nClob)
/*     */     throws SQLException
/*     */   {
/* 284 */     updateNClob(findColumn(columnName), nClob);
/*     */   }
/*     */ 
/*     */   public synchronized void updateNString(int columnIndex, String x)
/*     */     throws SQLException
/*     */   {
/* 303 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/* 304 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 305 */       throw new SQLException("Can not call updateNString() when field's character set isn't UTF-8");
/*     */     }
/*     */ 
/* 308 */     if (!this.onInsertRow) {
/* 309 */       if (!this.doingUpdates) {
/* 310 */         this.doingUpdates = true;
/* 311 */         syncUpdate();
/*     */       }
/*     */ 
/* 314 */       ((JDBC4PreparedStatement)this.updater).setNString(columnIndex, x);
/*     */     } else {
/* 316 */       ((JDBC4PreparedStatement)this.inserter).setNString(columnIndex, x);
/*     */ 
/* 318 */       if (x == null)
/* 319 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*     */       else
/* 321 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x, this.charConverter, fieldEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void updateNString(String columnName, String x)
/*     */     throws SQLException
/*     */   {
/* 345 */     updateNString(findColumn(columnName), x);
/*     */   }
/*     */ 
/*     */   public int getHoldability() throws SQLException {
/* 349 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   protected NClob getNativeNClob(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 365 */     String stringVal = getStringForNClob(columnIndex);
/*     */ 
/* 367 */     if (stringVal == null) {
/* 368 */       return null;
/*     */     }
/*     */ 
/* 371 */     return getNClobFromString(stringVal, columnIndex);
/*     */   }
/*     */ 
/*     */   public Reader getNCharacterStream(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 390 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/* 391 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 392 */       throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/* 396 */     return getCharacterStream(columnIndex);
/*     */   }
/*     */ 
/*     */   public Reader getNCharacterStream(String columnName)
/*     */     throws SQLException
/*     */   {
/* 415 */     return getNCharacterStream(findColumn(columnName));
/*     */   }
/*     */ 
/*     */   public NClob getNClob(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 430 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/*     */ 
/* 432 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 433 */       throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/* 437 */     if (!this.isBinaryEncoded) {
/* 438 */       String asString = getStringForNClob(columnIndex);
/*     */ 
/* 440 */       if (asString == null) {
/* 441 */         return null;
/*     */       }
/*     */ 
/* 444 */       return new JDBC4NClob(asString, getExceptionInterceptor());
/*     */     }
/*     */ 
/* 447 */     return getNativeNClob(columnIndex);
/*     */   }
/*     */ 
/*     */   public NClob getNClob(String columnName)
/*     */     throws SQLException
/*     */   {
/* 462 */     return getNClob(findColumn(columnName));
/*     */   }
/*     */ 
/*     */   private final NClob getNClobFromString(String stringVal, int columnIndex) throws SQLException
/*     */   {
/* 467 */     return new JDBC4NClob(stringVal, getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public String getNString(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 484 */     String fieldEncoding = this.fields[(columnIndex - 1)].getCharacterSet();
/*     */ 
/* 486 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 487 */       throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
/*     */     }
/*     */ 
/* 491 */     return getString(columnIndex);
/*     */   }
/*     */ 
/*     */   public String getNString(String columnName)
/*     */     throws SQLException
/*     */   {
/* 509 */     return getNString(findColumn(columnName));
/*     */   }
/*     */ 
/*     */   public RowId getRowId(int columnIndex) throws SQLException {
/* 513 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public RowId getRowId(String columnLabel) throws SQLException {
/* 517 */     return getRowId(findColumn(columnLabel));
/*     */   }
/*     */ 
/*     */   public SQLXML getSQLXML(int columnIndex) throws SQLException {
/* 521 */     return new JDBC4MysqlSQLXML(this, columnIndex, getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public SQLXML getSQLXML(String columnLabel) throws SQLException {
/* 525 */     return getSQLXML(findColumn(columnLabel));
/*     */   }
/*     */ 
/*     */   private String getStringForNClob(int columnIndex) throws SQLException {
/* 529 */     String asString = null;
/*     */ 
/* 531 */     String forcedEncoding = "UTF-8";
/*     */     try
/*     */     {
/* 534 */       byte[] asBytes = null;
/*     */ 
/* 536 */       if (!this.isBinaryEncoded)
/* 537 */         asBytes = getBytes(columnIndex);
/*     */       else {
/* 539 */         asBytes = getNativeBytes(columnIndex, true);
/*     */       }
/*     */ 
/* 542 */       if (asBytes != null)
/* 543 */         asString = new String(asBytes, forcedEncoding);
/*     */     }
/*     */     catch (UnsupportedEncodingException uee) {
/* 546 */       throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*     */     }
/*     */ 
/* 550 */     return asString;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isClosed() throws SQLException {
/* 554 */     return this.isClosed;
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 580 */     checkClosed();
/*     */ 
/* 584 */     return iface.isInstance(this);
/*     */   }
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 610 */       return iface.cast(this); } catch (ClassCastException cce) {
/*     */     }
/* 612 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4UpdatableResultSet
 * JD-Core Version:    0.6.0
 */