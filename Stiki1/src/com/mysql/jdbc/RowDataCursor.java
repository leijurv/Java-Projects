/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RowDataCursor
/*     */   implements RowData
/*     */ {
/*     */   private static final int BEFORE_START_OF_ROWS = -1;
/*     */   private List fetchedRows;
/*  52 */   private int currentPositionInEntireResult = -1;
/*     */ 
/*  58 */   private int currentPositionInFetchedRows = -1;
/*     */   private ResultSetImpl owner;
/*  68 */   private boolean lastRowFetched = false;
/*     */   private Field[] metadata;
/*     */   private MysqlIO mysql;
/*     */   private long statementIdOnServer;
/*     */   private ServerPreparedStatement prepStmt;
/*     */   private static final int SERVER_STATUS_LAST_ROW_SENT = 128;
/* 101 */   private boolean firstFetchCompleted = false;
/*     */ 
/* 103 */   private boolean wasEmpty = false;
/*     */ 
/* 105 */   private boolean useBufferRowExplicit = false;
/*     */ 
/*     */   public RowDataCursor(MysqlIO ioChannel, ServerPreparedStatement creatingStatement, Field[] metadata)
/*     */   {
/* 119 */     this.currentPositionInEntireResult = -1;
/* 120 */     this.metadata = metadata;
/* 121 */     this.mysql = ioChannel;
/* 122 */     this.statementIdOnServer = creatingStatement.getServerStatementId();
/* 123 */     this.prepStmt = creatingStatement;
/* 124 */     this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
/*     */   }
/*     */ 
/*     */   public boolean isAfterLast()
/*     */   {
/* 134 */     return (this.lastRowFetched) && (this.currentPositionInFetchedRows > this.fetchedRows.size());
/*     */   }
/*     */ 
/*     */   public ResultSetRow getAt(int ind)
/*     */     throws SQLException
/*     */   {
/* 148 */     notSupported();
/*     */ 
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */     throws SQLException
/*     */   {
/* 161 */     return this.currentPositionInEntireResult < 0;
/*     */   }
/*     */ 
/*     */   public void setCurrentRow(int rowNumber)
/*     */     throws SQLException
/*     */   {
/* 173 */     notSupported();
/*     */   }
/*     */ 
/*     */   public int getCurrentRowNumber()
/*     */     throws SQLException
/*     */   {
/* 184 */     return this.currentPositionInEntireResult + 1;
/*     */   }
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/* 196 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */     throws SQLException
/*     */   {
/* 207 */     return (isBeforeFirst()) && (isAfterLast());
/*     */   }
/*     */ 
/*     */   public boolean isFirst()
/*     */     throws SQLException
/*     */   {
/* 218 */     return this.currentPositionInEntireResult == 0;
/*     */   }
/*     */ 
/*     */   public boolean isLast()
/*     */     throws SQLException
/*     */   {
/* 229 */     return (this.lastRowFetched) && (this.currentPositionInFetchedRows == this.fetchedRows.size() - 1);
/*     */   }
/*     */ 
/*     */   public void addRow(ResultSetRow row)
/*     */     throws SQLException
/*     */   {
/* 243 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void afterLast()
/*     */     throws SQLException
/*     */   {
/* 253 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeFirst()
/*     */     throws SQLException
/*     */   {
/* 263 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeLast()
/*     */     throws SQLException
/*     */   {
/* 273 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 284 */     this.metadata = null;
/* 285 */     this.owner = null;
/*     */   }
/*     */ 
/*     */   public boolean hasNext()
/*     */     throws SQLException
/*     */   {
/* 297 */     if ((this.fetchedRows != null) && (this.fetchedRows.size() == 0)) {
/* 298 */       return false;
/*     */     }
/*     */ 
/* 301 */     if ((this.owner != null) && (this.owner.owningStatement != null)) {
/* 302 */       int maxRows = this.owner.owningStatement.maxRows;
/*     */ 
/* 304 */       if ((maxRows != -1) && (this.currentPositionInEntireResult + 1 > maxRows)) {
/* 305 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 309 */     if (this.currentPositionInEntireResult != -1)
/*     */     {
/* 312 */       if (this.currentPositionInFetchedRows < this.fetchedRows.size() - 1)
/* 313 */         return true;
/* 314 */       if ((this.currentPositionInFetchedRows == this.fetchedRows.size()) && (this.lastRowFetched))
/*     */       {
/* 317 */         return false;
/*     */       }
/*     */ 
/* 320 */       fetchMoreRows();
/*     */ 
/* 322 */       return this.fetchedRows.size() > 0;
/*     */     }
/*     */ 
/* 328 */     fetchMoreRows();
/*     */ 
/* 330 */     return this.fetchedRows.size() > 0;
/*     */   }
/*     */ 
/*     */   public void moveRowRelative(int rows)
/*     */     throws SQLException
/*     */   {
/* 342 */     notSupported();
/*     */   }
/*     */ 
/*     */   public ResultSetRow next()
/*     */     throws SQLException
/*     */   {
/* 353 */     if ((this.fetchedRows == null) && (this.currentPositionInEntireResult != -1)) {
/* 354 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", this.mysql.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 360 */     if (!hasNext()) {
/* 361 */       return null;
/*     */     }
/*     */ 
/* 364 */     this.currentPositionInEntireResult += 1;
/* 365 */     this.currentPositionInFetchedRows += 1;
/*     */ 
/* 368 */     if ((this.fetchedRows != null) && (this.fetchedRows.size() == 0)) {
/* 369 */       return null;
/*     */     }
/*     */ 
/* 372 */     if (this.currentPositionInFetchedRows > this.fetchedRows.size() - 1) {
/* 373 */       fetchMoreRows();
/* 374 */       this.currentPositionInFetchedRows = 0;
/*     */     }
/*     */ 
/* 377 */     ResultSetRow row = (ResultSetRow)this.fetchedRows.get(this.currentPositionInFetchedRows);
/*     */ 
/* 380 */     row.setMetadata(this.metadata);
/*     */ 
/* 382 */     return row;
/*     */   }
/*     */ 
/*     */   private void fetchMoreRows()
/*     */     throws SQLException
/*     */   {
/* 389 */     if (this.lastRowFetched) {
/* 390 */       this.fetchedRows = new ArrayList(0);
/* 391 */       return;
/*     */     }
/*     */ 
/* 394 */     synchronized (this.owner.connection.getMutex()) {
/* 395 */       boolean oldFirstFetchCompleted = this.firstFetchCompleted;
/*     */ 
/* 397 */       if (!this.firstFetchCompleted) {
/* 398 */         this.firstFetchCompleted = true;
/*     */       }
/*     */ 
/* 401 */       int numRowsToFetch = this.owner.getFetchSize();
/*     */ 
/* 403 */       if (numRowsToFetch == 0) {
/* 404 */         numRowsToFetch = this.prepStmt.getFetchSize();
/*     */       }
/*     */ 
/* 407 */       if (numRowsToFetch == -2147483648)
/*     */       {
/* 411 */         numRowsToFetch = 1;
/*     */       }
/*     */ 
/* 414 */       this.fetchedRows = this.mysql.fetchRowsViaCursor(this.fetchedRows, this.statementIdOnServer, this.metadata, numRowsToFetch, this.useBufferRowExplicit);
/*     */ 
/* 417 */       this.currentPositionInFetchedRows = -1;
/*     */ 
/* 419 */       if ((this.mysql.getServerStatus() & 0x80) != 0) {
/* 420 */         this.lastRowFetched = true;
/*     */ 
/* 422 */         if ((!oldFirstFetchCompleted) && (this.fetchedRows.size() == 0))
/* 423 */           this.wasEmpty = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeRow(int ind)
/*     */     throws SQLException
/*     */   {
/* 438 */     notSupported();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 447 */     return -1;
/*     */   }
/*     */ 
/*     */   private void nextRecord() throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   private void notSupported() throws SQLException {
/* 455 */     throw new OperationNotSupportedException();
/*     */   }
/*     */ 
/*     */   public void setOwner(ResultSetImpl rs)
/*     */   {
/* 464 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods getOwner()
/*     */   {
/* 473 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public boolean wasEmpty() {
/* 477 */     return this.wasEmpty;
/*     */   }
/*     */ 
/*     */   public void setMetadata(Field[] metadata) {
/* 481 */     this.metadata = metadata;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.RowDataCursor
 * JD-Core Version:    0.6.0
 */