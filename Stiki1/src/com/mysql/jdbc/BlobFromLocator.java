/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class BlobFromLocator
/*     */   implements Blob
/*     */ {
/*  58 */   private List primaryKeyColumns = null;
/*     */ 
/*  60 */   private List primaryKeyValues = null;
/*     */   private ResultSetImpl creatorResultSet;
/*  65 */   private String blobColumnName = null;
/*     */ 
/*  67 */   private String tableName = null;
/*     */ 
/*  69 */   private int numColsInResultSet = 0;
/*     */ 
/*  71 */   private int numPrimaryKeys = 0;
/*     */   private String quotedId;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   BlobFromLocator(ResultSetImpl creatorResultSetToSet, int blobColumnIndex, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  82 */     this.exceptionInterceptor = exceptionInterceptor;
/*  83 */     this.creatorResultSet = creatorResultSetToSet;
/*     */ 
/*  85 */     this.numColsInResultSet = this.creatorResultSet.fields.length;
/*  86 */     this.quotedId = this.creatorResultSet.connection.getMetaData().getIdentifierQuoteString();
/*     */ 
/*  89 */     if (this.numColsInResultSet > 1) {
/*  90 */       this.primaryKeyColumns = new ArrayList();
/*  91 */       this.primaryKeyValues = new ArrayList();
/*     */ 
/*  93 */       for (int i = 0; i < this.numColsInResultSet; i++)
/*  94 */         if (this.creatorResultSet.fields[i].isPrimaryKey()) {
/*  95 */           StringBuffer keyName = new StringBuffer();
/*  96 */           keyName.append(this.quotedId);
/*     */ 
/*  98 */           String originalColumnName = this.creatorResultSet.fields[i].getOriginalName();
/*     */ 
/* 101 */           if ((originalColumnName != null) && (originalColumnName.length() > 0))
/*     */           {
/* 103 */             keyName.append(originalColumnName);
/*     */           }
/* 105 */           else keyName.append(this.creatorResultSet.fields[i].getName());
/*     */ 
/* 109 */           keyName.append(this.quotedId);
/*     */ 
/* 111 */           this.primaryKeyColumns.add(keyName.toString());
/* 112 */           this.primaryKeyValues.add(this.creatorResultSet.getString(i + 1));
/*     */         }
/*     */     }
/*     */     else
/*     */     {
/* 117 */       notEnoughInformationInQuery();
/*     */     }
/*     */ 
/* 120 */     this.numPrimaryKeys = this.primaryKeyColumns.size();
/*     */ 
/* 122 */     if (this.numPrimaryKeys == 0) {
/* 123 */       notEnoughInformationInQuery();
/*     */     }
/*     */ 
/* 126 */     if (this.creatorResultSet.fields[0].getOriginalTableName() != null) {
/* 127 */       StringBuffer tableNameBuffer = new StringBuffer();
/*     */ 
/* 129 */       String databaseName = this.creatorResultSet.fields[0].getDatabaseName();
/*     */ 
/* 132 */       if ((databaseName != null) && (databaseName.length() > 0)) {
/* 133 */         tableNameBuffer.append(this.quotedId);
/* 134 */         tableNameBuffer.append(databaseName);
/* 135 */         tableNameBuffer.append(this.quotedId);
/* 136 */         tableNameBuffer.append('.');
/*     */       }
/*     */ 
/* 139 */       tableNameBuffer.append(this.quotedId);
/* 140 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getOriginalTableName());
/*     */ 
/* 142 */       tableNameBuffer.append(this.quotedId);
/*     */ 
/* 144 */       this.tableName = tableNameBuffer.toString();
/*     */     } else {
/* 146 */       StringBuffer tableNameBuffer = new StringBuffer();
/*     */ 
/* 148 */       tableNameBuffer.append(this.quotedId);
/* 149 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getTableName());
/*     */ 
/* 151 */       tableNameBuffer.append(this.quotedId);
/*     */ 
/* 153 */       this.tableName = tableNameBuffer.toString();
/*     */     }
/*     */ 
/* 156 */     this.blobColumnName = (this.quotedId + this.creatorResultSet.getString(blobColumnIndex) + this.quotedId);
/*     */   }
/*     */ 
/*     */   private void notEnoughInformationInQuery() throws SQLException
/*     */   {
/* 161 */     throw SQLError.createSQLException("Emulated BLOB locators must come from a ResultSet with only one table selected, and all primary keys selected", "S1000", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public OutputStream setBinaryStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 171 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 184 */     return new BufferedInputStream(new LocatorInputStream(), this.creatorResultSet.connection.getLocatorFetchBufferSize());
/*     */   }
/*     */ 
/*     */   public int setBytes(long writeAt, byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 193 */     PreparedStatement pStmt = null;
/*     */ 
/* 195 */     if (offset + length > bytes.length) {
/* 196 */       length = bytes.length - offset;
/*     */     }
/*     */ 
/* 199 */     byte[] bytesToWrite = new byte[length];
/* 200 */     System.arraycopy(bytes, offset, bytesToWrite, 0, length);
/*     */ 
/* 203 */     StringBuffer query = new StringBuffer("UPDATE ");
/* 204 */     query.append(this.tableName);
/* 205 */     query.append(" SET ");
/* 206 */     query.append(this.blobColumnName);
/* 207 */     query.append(" = INSERT(");
/* 208 */     query.append(this.blobColumnName);
/* 209 */     query.append(", ");
/* 210 */     query.append(writeAt);
/* 211 */     query.append(", ");
/* 212 */     query.append(length);
/* 213 */     query.append(", ?) WHERE ");
/*     */ 
/* 215 */     query.append((String)this.primaryKeyColumns.get(0));
/* 216 */     query.append(" = ?");
/*     */ 
/* 218 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 219 */       query.append(" AND ");
/* 220 */       query.append((String)this.primaryKeyColumns.get(i));
/* 221 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 226 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 229 */       pStmt.setBytes(1, bytesToWrite);
/*     */ 
/* 231 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 232 */         pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 235 */       int rowsUpdated = pStmt.executeUpdate();
/*     */ 
/* 237 */       if (rowsUpdated != 1) {
/* 238 */         throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 243 */       if (pStmt != null) {
/*     */         try {
/* 245 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 250 */         pStmt = null;
/*     */       }
/*     */     }
/*     */ 
/* 254 */     return (int)length();
/*     */   }
/*     */ 
/*     */   public int setBytes(long writeAt, byte[] bytes)
/*     */     throws SQLException
/*     */   {
/* 261 */     return setBytes(writeAt, bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes(long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 280 */     PreparedStatement pStmt = null;
/*     */     try
/*     */     {
/* 284 */       pStmt = createGetBytesStatement();
/*     */ 
/* 286 */       arrayOfByte = getBytesInternal(pStmt, pos, length);
/*     */     }
/*     */     finally
/*     */     {
/*     */       byte[] arrayOfByte;
/* 288 */       if (pStmt != null) {
/*     */         try {
/* 290 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 295 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/* 310 */     ResultSet blobRs = null;
/* 311 */     PreparedStatement pStmt = null;
/*     */ 
/* 314 */     StringBuffer query = new StringBuffer("SELECT LENGTH(");
/* 315 */     query.append(this.blobColumnName);
/* 316 */     query.append(") FROM ");
/* 317 */     query.append(this.tableName);
/* 318 */     query.append(" WHERE ");
/*     */ 
/* 320 */     query.append((String)this.primaryKeyColumns.get(0));
/* 321 */     query.append(" = ?");
/*     */ 
/* 323 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 324 */       query.append(" AND ");
/* 325 */       query.append((String)this.primaryKeyColumns.get(i));
/* 326 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 331 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 334 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 335 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 338 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 340 */       if (blobRs.next()) {
/* 341 */         i = blobRs.getLong(1); jsr 26;
/*     */       }
/*     */ 
/* 344 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     finally
/*     */     {
/* 348 */       if (blobRs != null) {
/*     */         try {
/* 350 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 355 */         blobRs = null;
/*     */       }
/*     */ 
/* 358 */       if (pStmt != null) {
/*     */         try {
/* 360 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 365 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long position(Blob pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 385 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */ 
/*     */   public long position(byte[] pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 392 */     ResultSet blobRs = null;
/* 393 */     PreparedStatement pStmt = null;
/*     */ 
/* 396 */     StringBuffer query = new StringBuffer("SELECT LOCATE(");
/* 397 */     query.append("?, ");
/* 398 */     query.append(this.blobColumnName);
/* 399 */     query.append(", ");
/* 400 */     query.append(start);
/* 401 */     query.append(") FROM ");
/* 402 */     query.append(this.tableName);
/* 403 */     query.append(" WHERE ");
/*     */ 
/* 405 */     query.append((String)this.primaryKeyColumns.get(0));
/* 406 */     query.append(" = ?");
/*     */ 
/* 408 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 409 */       query.append(" AND ");
/* 410 */       query.append((String)this.primaryKeyColumns.get(i));
/* 411 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 416 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 418 */       pStmt.setBytes(1, pattern);
/*     */ 
/* 420 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 421 */         pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 424 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 426 */       if (blobRs.next()) {
/* 427 */         i = blobRs.getLong(1); jsr 26;
/*     */       }
/*     */ 
/* 430 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     finally
/*     */     {
/* 434 */       if (blobRs != null) {
/*     */         try {
/* 436 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 441 */         blobRs = null;
/*     */       }
/*     */ 
/* 444 */       if (pStmt != null) {
/*     */         try {
/* 446 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 451 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void truncate(long length)
/*     */     throws SQLException
/*     */   {
/* 460 */     PreparedStatement pStmt = null;
/*     */ 
/* 463 */     StringBuffer query = new StringBuffer("UPDATE ");
/* 464 */     query.append(this.tableName);
/* 465 */     query.append(" SET ");
/* 466 */     query.append(this.blobColumnName);
/* 467 */     query.append(" = LEFT(");
/* 468 */     query.append(this.blobColumnName);
/* 469 */     query.append(", ");
/* 470 */     query.append(length);
/* 471 */     query.append(") WHERE ");
/*     */ 
/* 473 */     query.append((String)this.primaryKeyColumns.get(0));
/* 474 */     query.append(" = ?");
/*     */ 
/* 476 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 477 */       query.append(" AND ");
/* 478 */       query.append((String)this.primaryKeyColumns.get(i));
/* 479 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 484 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 487 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 488 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 491 */       int rowsUpdated = pStmt.executeUpdate();
/*     */ 
/* 493 */       if (rowsUpdated != 1) {
/* 494 */         throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 499 */       if (pStmt != null) {
/*     */         try {
/* 501 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 506 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   PreparedStatement createGetBytesStatement() throws SQLException {
/* 512 */     StringBuffer query = new StringBuffer("SELECT SUBSTRING(");
/*     */ 
/* 514 */     query.append(this.blobColumnName);
/* 515 */     query.append(", ");
/* 516 */     query.append("?");
/* 517 */     query.append(", ");
/* 518 */     query.append("?");
/* 519 */     query.append(") FROM ");
/* 520 */     query.append(this.tableName);
/* 521 */     query.append(" WHERE ");
/*     */ 
/* 523 */     query.append((String)this.primaryKeyColumns.get(0));
/* 524 */     query.append(" = ?");
/*     */ 
/* 526 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 527 */       query.append(" AND ");
/* 528 */       query.append((String)this.primaryKeyColumns.get(i));
/* 529 */       query.append(" = ?");
/*     */     }
/*     */ 
/* 532 */     return this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */   }
/*     */ 
/*     */   byte[] getBytesInternal(PreparedStatement pStmt, long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 539 */     ResultSet blobRs = null;
/*     */     try
/*     */     {
/* 543 */       pStmt.setLong(1, pos);
/* 544 */       pStmt.setInt(2, length);
/*     */ 
/* 546 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 547 */         pStmt.setString(i + 3, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 550 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 552 */       if (blobRs.next()) {
/* 553 */         i = ((ResultSetImpl)blobRs).getBytes(1, true); jsr 26;
/*     */       }
/*     */ 
/* 556 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     finally
/*     */     {
/* 560 */       if (blobRs != null) {
/*     */         try {
/* 562 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 567 */         blobRs = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void free()
/*     */     throws SQLException
/*     */   {
/* 701 */     this.creatorResultSet = null;
/* 702 */     this.primaryKeyColumns = null;
/* 703 */     this.primaryKeyValues = null;
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream(long pos, long length) throws SQLException {
/* 707 */     return new LocatorInputStream(pos, length);
/*     */   }
/*     */ 
/*     */   class LocatorInputStream extends InputStream
/*     */   {
/* 573 */     long currentPositionInBlob = 0L;
/*     */ 
/* 575 */     long length = 0L;
/*     */ 
/* 577 */     PreparedStatement pStmt = null;
/*     */ 
/*     */     LocatorInputStream() throws SQLException {
/* 580 */       this.length = BlobFromLocator.this.length();
/* 581 */       this.pStmt = BlobFromLocator.this.createGetBytesStatement();
/*     */     }
/*     */ 
/*     */     LocatorInputStream(long pos, long len) throws SQLException {
/* 585 */       this.length = (pos + len);
/* 586 */       this.currentPositionInBlob = pos;
/* 587 */       long blobLength = BlobFromLocator.this.length();
/*     */ 
/* 589 */       if (pos + len > blobLength) {
/* 590 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamLength", new Object[] { new Long(blobLength), new Long(pos), new Long(len) }), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */       }
/*     */ 
/* 596 */       if (pos < 1L) {
/* 597 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */       }
/*     */ 
/* 601 */       if (pos > blobLength)
/* 602 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 608 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 609 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 613 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob++ + 1L, 1);
/*     */ 
/* 616 */         if (asBytes == null) {
/* 617 */           return -1;
/*     */         }
/*     */ 
/* 620 */         return asBytes[0]; } catch (SQLException sqlEx) {
/*     */       }
/* 622 */       throw new IOException(sqlEx.toString());
/*     */     }
/*     */ 
/*     */     public int read(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 632 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 633 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 637 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, len);
/*     */ 
/* 640 */         if (asBytes == null) {
/* 641 */           return -1;
/*     */         }
/*     */ 
/* 644 */         System.arraycopy(asBytes, 0, b, off, asBytes.length);
/*     */ 
/* 646 */         this.currentPositionInBlob += asBytes.length;
/*     */ 
/* 648 */         return asBytes.length; } catch (SQLException sqlEx) {
/*     */       }
/* 650 */       throw new IOException(sqlEx.toString());
/*     */     }
/*     */ 
/*     */     public int read(byte[] b)
/*     */       throws IOException
/*     */     {
/* 660 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 661 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 665 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, b.length);
/*     */ 
/* 668 */         if (asBytes == null) {
/* 669 */           return -1;
/*     */         }
/*     */ 
/* 672 */         System.arraycopy(asBytes, 0, b, 0, asBytes.length);
/*     */ 
/* 674 */         this.currentPositionInBlob += asBytes.length;
/*     */ 
/* 676 */         return asBytes.length; } catch (SQLException sqlEx) {
/*     */       }
/* 678 */       throw new IOException(sqlEx.toString());
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 688 */       if (this.pStmt != null) {
/*     */         try {
/* 690 */           this.pStmt.close();
/*     */         } catch (SQLException sqlEx) {
/* 692 */           throw new IOException(sqlEx.toString());
/*     */         }
/*     */       }
/*     */ 
/* 696 */       super.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.BlobFromLocator
 * JD-Core Version:    0.6.0
 */