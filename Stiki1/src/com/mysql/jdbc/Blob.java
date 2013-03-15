/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class Blob
/*     */   implements java.sql.Blob, OutputStreamWatcher
/*     */ {
/*  62 */   private byte[] binaryData = null;
/*  63 */   private boolean isClosed = false;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   Blob(ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  70 */     setBinaryData(Constants.EMPTY_BYTE_ARRAY);
/*  71 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   Blob(byte[] data, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  81 */     setBinaryData(data);
/*  82 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   Blob(byte[] data, ResultSetInternalMethods creatorResultSetToSet, int columnIndexToSet)
/*     */   {
/*  96 */     setBinaryData(data);
/*     */   }
/*     */ 
/*     */   private synchronized byte[] getBinaryData() {
/* 100 */     return this.binaryData;
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 112 */     checkClosed();
/*     */ 
/* 114 */     return new ByteArrayInputStream(getBinaryData());
/*     */   }
/*     */ 
/*     */   public synchronized byte[] getBytes(long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 133 */     checkClosed();
/*     */ 
/* 135 */     if (pos < 1L) {
/* 136 */       throw SQLError.createSQLException(Messages.getString("Blob.2"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 140 */     pos -= 1L;
/*     */ 
/* 142 */     if (pos > this.binaryData.length) {
/* 143 */       throw SQLError.createSQLException("\"pos\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 147 */     if (pos + length > this.binaryData.length) {
/* 148 */       throw SQLError.createSQLException("\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 152 */     byte[] newData = new byte[length];
/* 153 */     System.arraycopy(getBinaryData(), (int)pos, newData, 0, length);
/*     */ 
/* 155 */     return newData;
/*     */   }
/*     */ 
/*     */   public synchronized long length()
/*     */     throws SQLException
/*     */   {
/* 168 */     checkClosed();
/*     */ 
/* 170 */     return getBinaryData().length;
/*     */   }
/*     */ 
/*     */   public synchronized long position(byte[] pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 177 */     throw SQLError.createSQLException("Not implemented", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public synchronized long position(java.sql.Blob pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 195 */     checkClosed();
/*     */ 
/* 197 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */ 
/*     */   private synchronized void setBinaryData(byte[] newBinaryData) {
/* 201 */     this.binaryData = newBinaryData;
/*     */   }
/*     */ 
/*     */   public synchronized OutputStream setBinaryStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 209 */     checkClosed();
/*     */ 
/* 211 */     if (indexToWriteAt < 1L) {
/* 212 */       throw SQLError.createSQLException(Messages.getString("Blob.0"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 216 */     WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 217 */     bytesOut.setWatcher(this);
/*     */ 
/* 219 */     if (indexToWriteAt > 0L) {
/* 220 */       bytesOut.write(this.binaryData, 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */ 
/* 223 */     return bytesOut;
/*     */   }
/*     */ 
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes)
/*     */     throws SQLException
/*     */   {
/* 230 */     checkClosed();
/*     */ 
/* 232 */     return setBytes(writeAt, bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 240 */     checkClosed();
/*     */ 
/* 242 */     OutputStream bytesOut = setBinaryStream(writeAt);
/*     */     try
/*     */     {
/* 245 */       bytesOut.write(bytes, offset, length);
/*     */     } catch (IOException ioEx) {
/* 247 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Blob.1"), "S1000", this.exceptionInterceptor);
/*     */ 
/* 249 */       sqlEx.initCause(ioEx);
/*     */ 
/* 251 */       throw sqlEx;
/*     */     } finally {
/*     */       try {
/* 254 */         bytesOut.close();
/*     */       }
/*     */       catch (IOException doNothing)
/*     */       {
/*     */       }
/*     */     }
/* 260 */     return length;
/*     */   }
/*     */ 
/*     */   public synchronized void streamClosed(byte[] byteData)
/*     */   {
/* 267 */     this.binaryData = byteData;
/*     */   }
/*     */ 
/*     */   public synchronized void streamClosed(WatchableOutputStream out)
/*     */   {
/* 274 */     int streamSize = out.size();
/*     */ 
/* 276 */     if (streamSize < this.binaryData.length) {
/* 277 */       out.write(this.binaryData, streamSize, this.binaryData.length - streamSize);
/*     */     }
/*     */ 
/* 281 */     this.binaryData = out.toByteArray();
/*     */   }
/*     */ 
/*     */   public synchronized void truncate(long len)
/*     */     throws SQLException
/*     */   {
/* 303 */     checkClosed();
/*     */ 
/* 305 */     if (len < 0L) {
/* 306 */       throw SQLError.createSQLException("\"len\" argument can not be < 1.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 310 */     if (len > this.binaryData.length) {
/* 311 */       throw SQLError.createSQLException("\"len\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 318 */     byte[] newData = new byte[(int)len];
/* 319 */     System.arraycopy(getBinaryData(), 0, newData, 0, (int)len);
/* 320 */     this.binaryData = newData;
/*     */   }
/*     */ 
/*     */   public synchronized void free()
/*     */     throws SQLException
/*     */   {
/* 342 */     this.binaryData = null;
/* 343 */     this.isClosed = true;
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getBinaryStream(long pos, long length)
/*     */     throws SQLException
/*     */   {
/* 363 */     checkClosed();
/*     */ 
/* 365 */     if (pos < 1L) {
/* 366 */       throw SQLError.createSQLException("\"pos\" argument can not be < 1.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 370 */     pos -= 1L;
/*     */ 
/* 372 */     if (pos > this.binaryData.length) {
/* 373 */       throw SQLError.createSQLException("\"pos\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 377 */     if (pos + length > this.binaryData.length) {
/* 378 */       throw SQLError.createSQLException("\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 382 */     return new ByteArrayInputStream(getBinaryData(), (int)pos, (int)length);
/*     */   }
/*     */ 
/*     */   private synchronized void checkClosed() throws SQLException {
/* 386 */     if (this.isClosed)
/* 387 */       throw SQLError.createSQLException("Invalid operation on closed BLOB", "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Blob
 * JD-Core Version:    0.6.0
 */