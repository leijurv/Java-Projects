/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*     */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ public class RowDataDynamic
/*     */   implements RowData
/*     */ {
/*     */   private int columnCount;
/*     */   private Field[] metadata;
/*  57 */   private int index = -1;
/*     */   private MysqlIO io;
/*  61 */   private boolean isAfterEnd = false;
/*     */ 
/*  63 */   private boolean noMoreRows = false;
/*     */ 
/*  65 */   private boolean isBinaryEncoded = false;
/*     */   private ResultSetRow nextRow;
/*     */   private ResultSetImpl owner;
/*  71 */   private boolean streamerClosed = false;
/*     */ 
/*  73 */   private boolean wasEmpty = false;
/*     */   private boolean useBufferRowExplicit;
/*     */   private boolean moreResultsExisted;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   public RowDataDynamic(MysqlIO io, int colCount, Field[] fields, boolean isBinaryEncoded)
/*     */     throws SQLException
/*     */   {
/*  97 */     this.io = io;
/*  98 */     this.columnCount = colCount;
/*  99 */     this.isBinaryEncoded = isBinaryEncoded;
/* 100 */     this.metadata = fields;
/* 101 */     this.exceptionInterceptor = this.io.getExceptionInterceptor();
/* 102 */     this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
/*     */   }
/*     */ 
/*     */   public void addRow(ResultSetRow row)
/*     */     throws SQLException
/*     */   {
/* 114 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void afterLast()
/*     */     throws SQLException
/*     */   {
/* 124 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeFirst()
/*     */     throws SQLException
/*     */   {
/* 134 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeLast()
/*     */     throws SQLException
/*     */   {
/* 144 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 159 */     Object mutex = this;
/*     */ 
/* 161 */     ConnectionImpl conn = null;
/*     */ 
/* 163 */     if (this.owner != null) {
/* 164 */       conn = this.owner.connection;
/*     */ 
/* 166 */       if (conn != null) {
/* 167 */         mutex = conn.getMutex();
/*     */       }
/*     */     }
/*     */ 
/* 171 */     boolean hadMore = false;
/* 172 */     int howMuchMore = 0;
/*     */ 
/* 174 */     synchronized (mutex)
/*     */     {
/* 176 */       while (next() != null) {
/* 177 */         hadMore = true;
/* 178 */         howMuchMore++;
/*     */ 
/* 180 */         if (howMuchMore % 100 == 0) {
/* 181 */           Thread.yield();
/*     */         }
/*     */       }
/*     */ 
/* 185 */       if (conn != null) {
/* 186 */         if ((!conn.getClobberStreamingResults()) && (conn.getNetTimeoutForStreamingResults() > 0))
/*     */         {
/* 188 */           String oldValue = conn.getServerVariable("net_write_timeout");
/*     */ 
/* 191 */           if ((oldValue == null) || (oldValue.length() == 0)) {
/* 192 */             oldValue = "60";
/*     */           }
/*     */ 
/* 195 */           this.io.clearInputStream();
/*     */ 
/* 197 */           Statement stmt = null;
/*     */           try
/*     */           {
/* 200 */             stmt = conn.createStatement();
/* 201 */             ((StatementImpl)stmt).executeSimpleNonQuery(conn, "SET net_write_timeout=" + oldValue);
/*     */           } finally {
/* 203 */             if (stmt != null) {
/* 204 */               stmt.close();
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 209 */         if ((conn.getUseUsageAdvisor()) && 
/* 210 */           (hadMore))
/*     */         {
/* 212 */           ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(conn);
/*     */ 
/* 215 */           eventSink.consumeEvent(new ProfilerEvent(0, "", this.owner.owningStatement == null ? "N/A" : this.owner.owningStatement.currentCatalog, this.owner.connectionId, this.owner.owningStatement == null ? -1 : this.owner.owningStatement.getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, null, Messages.getString("RowDataDynamic.2") + howMuchMore + Messages.getString("RowDataDynamic.3") + Messages.getString("RowDataDynamic.4") + Messages.getString("RowDataDynamic.5") + Messages.getString("RowDataDynamic.6") + this.owner.pointOfOrigin));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 246 */     this.metadata = null;
/* 247 */     this.owner = null;
/*     */   }
/*     */ 
/*     */   public ResultSetRow getAt(int ind)
/*     */     throws SQLException
/*     */   {
/* 260 */     notSupported();
/*     */ 
/* 262 */     return null;
/*     */   }
/*     */ 
/*     */   public int getCurrentRowNumber()
/*     */     throws SQLException
/*     */   {
/* 273 */     notSupported();
/*     */ 
/* 275 */     return -1;
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods getOwner()
/*     */   {
/* 282 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public boolean hasNext()
/*     */     throws SQLException
/*     */   {
/* 293 */     boolean hasNext = this.nextRow != null;
/*     */ 
/* 295 */     if ((!hasNext) && (!this.streamerClosed)) {
/* 296 */       this.io.closeStreamer(this);
/* 297 */       this.streamerClosed = true;
/*     */     }
/*     */ 
/* 300 */     return hasNext;
/*     */   }
/*     */ 
/*     */   public boolean isAfterLast()
/*     */     throws SQLException
/*     */   {
/* 311 */     return this.isAfterEnd;
/*     */   }
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */     throws SQLException
/*     */   {
/* 322 */     return this.index < 0;
/*     */   }
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/* 334 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */     throws SQLException
/*     */   {
/* 345 */     notSupported();
/*     */ 
/* 347 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isFirst()
/*     */     throws SQLException
/*     */   {
/* 358 */     notSupported();
/*     */ 
/* 360 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isLast()
/*     */     throws SQLException
/*     */   {
/* 371 */     notSupported();
/*     */ 
/* 373 */     return false;
/*     */   }
/*     */ 
/*     */   public void moveRowRelative(int rows)
/*     */     throws SQLException
/*     */   {
/* 385 */     notSupported();
/*     */   }
/*     */ 
/*     */   public ResultSetRow next()
/*     */     throws SQLException
/*     */   {
/* 398 */     nextRecord();
/*     */ 
/* 400 */     if ((this.nextRow == null) && (!this.streamerClosed) && (!this.moreResultsExisted)) {
/* 401 */       this.io.closeStreamer(this);
/* 402 */       this.streamerClosed = true;
/*     */     }
/*     */ 
/* 405 */     if ((this.nextRow != null) && 
/* 406 */       (this.index != 2147483647)) {
/* 407 */       this.index += 1;
/*     */     }
/*     */ 
/* 411 */     return this.nextRow;
/*     */   }
/*     */ 
/*     */   private void nextRecord() throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 418 */       if (!this.noMoreRows) {
/* 419 */         this.nextRow = this.io.nextRow(this.metadata, this.columnCount, this.isBinaryEncoded, 1007, true, this.useBufferRowExplicit, true, null);
/*     */ 
/* 424 */         if (this.nextRow == null) {
/* 425 */           this.noMoreRows = true;
/* 426 */           this.isAfterEnd = true;
/* 427 */           this.moreResultsExisted = this.io.tackOnMoreStreamingResults(this.owner);
/*     */ 
/* 429 */           if (this.index == -1)
/* 430 */             this.wasEmpty = true;
/*     */         }
/*     */       }
/*     */       else {
/* 434 */         this.isAfterEnd = true;
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 437 */       if ((sqlEx instanceof StreamingNotifiable)) {
/* 438 */         ((StreamingNotifiable)sqlEx).setWasStreamingResults();
/*     */       }
/*     */ 
/* 442 */       throw sqlEx;
/*     */     } catch (Exception ex) {
/* 444 */       String exceptionType = ex.getClass().getName();
/* 445 */       String exceptionMessage = ex.getMessage();
/*     */ 
/* 447 */       exceptionMessage = exceptionMessage + Messages.getString("RowDataDynamic.7");
/* 448 */       exceptionMessage = exceptionMessage + Util.stackTraceToString(ex);
/*     */ 
/* 450 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("RowDataDynamic.8") + exceptionType + Messages.getString("RowDataDynamic.9") + exceptionMessage, "S1000", this.exceptionInterceptor);
/*     */ 
/* 454 */       sqlEx.initCause(ex);
/*     */ 
/* 456 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notSupported() throws SQLException {
/* 461 */     throw new OperationNotSupportedException();
/*     */   }
/*     */ 
/*     */   public void removeRow(int ind)
/*     */     throws SQLException
/*     */   {
/* 473 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void setCurrentRow(int rowNumber)
/*     */     throws SQLException
/*     */   {
/* 485 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void setOwner(ResultSetImpl rs)
/*     */   {
/* 492 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 501 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean wasEmpty() {
/* 505 */     return this.wasEmpty;
/*     */   }
/*     */ 
/*     */   public void setMetadata(Field[] metadata) {
/* 509 */     this.metadata = metadata;
/*     */   }
/*     */ 
/*     */   class OperationNotSupportedException extends SQLException
/*     */   {
/*     */     OperationNotSupportedException()
/*     */     {
/*  48 */       super("S1009");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.RowDataDynamic
 * JD-Core Version:    0.6.0
 */