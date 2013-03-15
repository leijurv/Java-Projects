/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class ResultSetImpl
/*      */   implements ResultSetInternalMethods
/*      */ {
/*      */   private static final Constructor JDBC_4_RS_4_ARG_CTOR;
/*      */   private static final Constructor JDBC_4_RS_6_ARG_CTOR;
/*      */   private static final Constructor JDBC_4_UPD_RS_6_ARG_CTOR;
/*      */   protected static final double MIN_DIFF_PREC;
/*      */   protected static final double MAX_DIFF_PREC;
/*      */   protected static int resultCounter;
/*  192 */   protected String catalog = null;
/*      */ 
/*  195 */   protected Map columnLabelToIndex = null;
/*      */ 
/*  201 */   protected Map columnToIndexCache = null;
/*      */ 
/*  204 */   protected boolean[] columnUsed = null;
/*      */   protected ConnectionImpl connection;
/*  210 */   protected long connectionId = 0L;
/*      */ 
/*  213 */   protected int currentRow = -1;
/*      */   TimeZone defaultTimeZone;
/*  218 */   protected boolean doingUpdates = false;
/*      */ 
/*  220 */   protected ProfilerEventHandler eventSink = null;
/*      */ 
/*  222 */   Calendar fastDateCal = null;
/*      */ 
/*  225 */   protected int fetchDirection = 1000;
/*      */ 
/*  228 */   protected int fetchSize = 0;
/*      */   protected Field[] fields;
/*      */   protected char firstCharOfQuery;
/*  241 */   protected Map fullColumnNameToIndex = null;
/*      */ 
/*  243 */   protected Map columnNameToIndex = null;
/*      */ 
/*  245 */   protected boolean hasBuiltIndexMapping = false;
/*      */ 
/*  251 */   protected boolean isBinaryEncoded = false;
/*      */ 
/*  254 */   protected boolean isClosed = false;
/*      */ 
/*  256 */   protected ResultSetInternalMethods nextResultSet = null;
/*      */ 
/*  259 */   protected boolean onInsertRow = false;
/*      */   protected StatementImpl owningStatement;
/*      */   protected Throwable pointOfOrigin;
/*  270 */   protected boolean profileSql = false;
/*      */ 
/*  276 */   protected boolean reallyResult = false;
/*      */   protected int resultId;
/*  282 */   protected int resultSetConcurrency = 0;
/*      */ 
/*  285 */   protected int resultSetType = 0;
/*      */   protected RowData rowData;
/*  294 */   protected String serverInfo = null;
/*      */   PreparedStatement statementUsedForFetchingRows;
/*  299 */   protected ResultSetRow thisRow = null;
/*      */   protected long updateCount;
/*  313 */   protected long updateId = -1L;
/*      */ 
/*  315 */   private boolean useStrictFloatingPoint = false;
/*      */ 
/*  317 */   protected boolean useUsageAdvisor = false;
/*      */ 
/*  320 */   protected SQLWarning warningChain = null;
/*      */ 
/*  323 */   protected boolean wasNullFlag = false;
/*      */   protected Statement wrapperStatement;
/*      */   protected boolean retainOwningStatement;
/*  329 */   protected Calendar gmtCalendar = null;
/*      */ 
/*  331 */   protected boolean useFastDateParsing = false;
/*      */ 
/*  333 */   private boolean padCharsWithSpace = false;
/*      */   private boolean jdbcCompliantTruncationForReads;
/*  337 */   private boolean useFastIntParsing = true;
/*      */   private boolean useColumnNamesInFindColumn;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */   protected static final char[] EMPTY_SPACE;
/*  847 */   private boolean onValidRow = false;
/*  848 */   private String invalidRowReason = null;
/*      */   protected boolean useLegacyDatetimeCode;
/*      */   private TimeZone serverTimeZoneTz;
/*      */ 
/*      */   protected static BigInteger convertLongToUlong(long longVal)
/*      */   {
/*  178 */     byte[] asBytes = new byte[8];
/*  179 */     asBytes[7] = (byte)(int)(longVal & 0xFF);
/*  180 */     asBytes[6] = (byte)(int)(longVal >>> 8);
/*  181 */     asBytes[5] = (byte)(int)(longVal >>> 16);
/*  182 */     asBytes[4] = (byte)(int)(longVal >>> 24);
/*  183 */     asBytes[3] = (byte)(int)(longVal >>> 32);
/*  184 */     asBytes[2] = (byte)(int)(longVal >>> 40);
/*  185 */     asBytes[1] = (byte)(int)(longVal >>> 48);
/*  186 */     asBytes[0] = (byte)(int)(longVal >>> 56);
/*      */ 
/*  188 */     return new BigInteger(1, asBytes);
/*      */   }
/*      */ 
/*      */   protected static ResultSetImpl getInstance(long updateCount, long updateID, ConnectionImpl conn, StatementImpl creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  352 */     if (!Util.isJdbc4()) {
/*  353 */       return new ResultSetImpl(updateCount, updateID, conn, creatorStmt);
/*      */     }
/*      */ 
/*  356 */     return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_4_ARG_CTOR, new Object[] { Constants.longValueOf(updateCount), Constants.longValueOf(updateID), conn, creatorStmt }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected static ResultSetImpl getInstance(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt, boolean isUpdatable)
/*      */     throws SQLException
/*      */   {
/*  372 */     if (!Util.isJdbc4()) {
/*  373 */       if (!isUpdatable) {
/*  374 */         return new ResultSetImpl(catalog, fields, tuples, conn, creatorStmt);
/*      */       }
/*      */ 
/*  377 */       return new UpdatableResultSet(catalog, fields, tuples, conn, creatorStmt);
/*      */     }
/*      */ 
/*  381 */     if (!isUpdatable) {
/*  382 */       return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_6_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt }, conn.getExceptionInterceptor());
/*      */     }
/*      */ 
/*  387 */     return (ResultSetImpl)Util.handleNewInstance(JDBC_4_UPD_RS_6_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public ResultSetImpl(long updateCount, long updateID, ConnectionImpl conn, StatementImpl creatorStmt)
/*      */   {
/*  405 */     this.updateCount = updateCount;
/*  406 */     this.updateId = updateID;
/*  407 */     this.reallyResult = false;
/*  408 */     this.fields = new Field[0];
/*      */ 
/*  410 */     this.connection = conn;
/*  411 */     this.owningStatement = creatorStmt;
/*      */ 
/*  413 */     this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*      */ 
/*  415 */     this.retainOwningStatement = false;
/*      */ 
/*  417 */     if (this.connection != null) {
/*  418 */       this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
/*      */ 
/*  421 */       this.connectionId = this.connection.getId();
/*  422 */       this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
/*  423 */       this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
/*      */     }
/*      */ 
/*  426 */     this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
/*      */   }
/*      */ 
/*      */   public ResultSetImpl(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  448 */     this.connection = conn;
/*      */ 
/*  450 */     this.retainOwningStatement = false;
/*      */ 
/*  452 */     if (this.connection != null) {
/*  453 */       this.useStrictFloatingPoint = this.connection.getStrictFloatingPoint();
/*      */ 
/*  455 */       setDefaultTimeZone(this.connection.getDefaultTimeZone());
/*  456 */       this.connectionId = this.connection.getId();
/*  457 */       this.useFastDateParsing = this.connection.getUseFastDateParsing();
/*  458 */       this.profileSql = this.connection.getProfileSql();
/*  459 */       this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
/*      */ 
/*  461 */       this.jdbcCompliantTruncationForReads = this.connection.getJdbcCompliantTruncationForReads();
/*  462 */       this.useFastIntParsing = this.connection.getUseFastIntParsing();
/*  463 */       this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
/*  464 */       this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
/*      */     }
/*      */ 
/*  467 */     this.owningStatement = creatorStmt;
/*      */ 
/*  469 */     this.catalog = catalog;
/*      */ 
/*  471 */     this.fields = fields;
/*  472 */     this.rowData = tuples;
/*  473 */     this.updateCount = this.rowData.size();
/*      */ 
/*  480 */     this.reallyResult = true;
/*      */ 
/*  483 */     if (this.rowData.size() > 0) {
/*  484 */       if ((this.updateCount == 1L) && 
/*  485 */         (this.thisRow == null)) {
/*  486 */         this.rowData.close();
/*  487 */         this.updateCount = -1L;
/*      */       }
/*      */     }
/*      */     else {
/*  491 */       this.thisRow = null;
/*      */     }
/*      */ 
/*  494 */     this.rowData.setOwner(this);
/*      */ 
/*  496 */     if (this.fields != null) {
/*  497 */       initializeWithMetadata();
/*      */     }
/*  499 */     this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
/*      */ 
/*  501 */     this.useColumnNamesInFindColumn = this.connection.getUseColumnNamesInFindColumn();
/*      */ 
/*  503 */     setRowPositionValidity();
/*      */   }
/*      */ 
/*      */   public void initializeWithMetadata() throws SQLException {
/*  507 */     this.rowData.setMetadata(this.fields);
/*      */ 
/*  509 */     this.columnToIndexCache = new HashMap();
/*      */ 
/*  511 */     if ((this.profileSql) || (this.connection.getUseUsageAdvisor())) {
/*  512 */       this.columnUsed = new boolean[this.fields.length];
/*  513 */       this.pointOfOrigin = new Throwable();
/*  514 */       this.resultId = (resultCounter++);
/*  515 */       this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  516 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */     }
/*      */ 
/*  519 */     if (this.connection.getGatherPerformanceMetrics()) {
/*  520 */       this.connection.incrementNumberOfResultSetsCreated();
/*      */ 
/*  522 */       Map tableNamesMap = new HashMap();
/*      */ 
/*  524 */       for (int i = 0; i < this.fields.length; i++) {
/*  525 */         Field f = this.fields[i];
/*      */ 
/*  527 */         String tableName = f.getOriginalTableName();
/*      */ 
/*  529 */         if (tableName == null) {
/*  530 */           tableName = f.getTableName();
/*      */         }
/*      */ 
/*  533 */         if (tableName != null) {
/*  534 */           if (this.connection.lowerCaseTableNames()) {
/*  535 */             tableName = tableName.toLowerCase();
/*      */           }
/*      */ 
/*  539 */           tableNamesMap.put(tableName, null);
/*      */         }
/*      */       }
/*      */ 
/*  543 */       this.connection.reportNumberOfTablesAccessed(tableNamesMap.size());
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void createCalendarIfNeeded() {
/*  548 */     if (this.fastDateCal == null) {
/*  549 */       this.fastDateCal = new GregorianCalendar(Locale.US);
/*  550 */       this.fastDateCal.setTimeZone(getDefaultTimeZone());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean absolute(int row)
/*      */     throws SQLException
/*      */   {
/*  593 */     checkClosed();
/*      */     boolean b;
/*      */     boolean b;
/*  597 */     if (this.rowData.size() == 0) {
/*  598 */       b = false;
/*      */     } else {
/*  600 */       if (row == 0) {
/*  601 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Cannot_absolute_position_to_row_0_110"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*  607 */       if (this.onInsertRow) {
/*  608 */         this.onInsertRow = false;
/*      */       }
/*      */ 
/*  611 */       if (this.doingUpdates) {
/*  612 */         this.doingUpdates = false;
/*      */       }
/*      */ 
/*  615 */       if (this.thisRow != null)
/*  616 */         this.thisRow.closeOpenStreams();
/*      */       boolean b;
/*  619 */       if (row == 1) {
/*  620 */         b = first();
/*      */       }
/*      */       else
/*      */       {
/*      */         boolean b;
/*  621 */         if (row == -1) {
/*  622 */           b = last();
/*      */         }
/*      */         else
/*      */         {
/*      */           boolean b;
/*  623 */           if (row > this.rowData.size()) {
/*  624 */             afterLast();
/*  625 */             b = false;
/*      */           }
/*      */           else
/*      */           {
/*      */             boolean b;
/*  627 */             if (row < 0)
/*      */             {
/*  629 */               int newRowPosition = this.rowData.size() + row + 1;
/*      */               boolean b;
/*  631 */               if (newRowPosition <= 0) {
/*  632 */                 beforeFirst();
/*  633 */                 b = false;
/*      */               } else {
/*  635 */                 b = absolute(newRowPosition);
/*      */               }
/*      */             } else {
/*  638 */               row--;
/*  639 */               this.rowData.setCurrentRow(row);
/*  640 */               this.thisRow = this.rowData.getAt(row);
/*  641 */               b = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  646 */     setRowPositionValidity();
/*      */ 
/*  648 */     return b;
/*      */   }
/*      */ 
/*      */   public void afterLast()
/*      */     throws SQLException
/*      */   {
/*  664 */     checkClosed();
/*      */ 
/*  666 */     if (this.onInsertRow) {
/*  667 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/*  670 */     if (this.doingUpdates) {
/*  671 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/*  674 */     if (this.thisRow != null) {
/*  675 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */ 
/*  678 */     if (this.rowData.size() != 0) {
/*  679 */       this.rowData.afterLast();
/*  680 */       this.thisRow = null;
/*      */     }
/*      */ 
/*  683 */     setRowPositionValidity();
/*      */   }
/*      */ 
/*      */   public void beforeFirst()
/*      */     throws SQLException
/*      */   {
/*  699 */     checkClosed();
/*      */ 
/*  701 */     if (this.onInsertRow) {
/*  702 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/*  705 */     if (this.doingUpdates) {
/*  706 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/*  709 */     if (this.rowData.size() == 0) {
/*  710 */       return;
/*      */     }
/*      */ 
/*  713 */     if (this.thisRow != null) {
/*  714 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */ 
/*  717 */     this.rowData.beforeFirst();
/*  718 */     this.thisRow = null;
/*      */ 
/*  720 */     setRowPositionValidity();
/*      */   }
/*      */ 
/*      */   public void buildIndexMapping()
/*      */     throws SQLException
/*      */   {
/*  731 */     int numFields = this.fields.length;
/*  732 */     this.columnLabelToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  733 */     this.fullColumnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  734 */     this.columnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */ 
/*  748 */     for (int i = numFields - 1; i >= 0; i--) {
/*  749 */       Integer index = Constants.integerValueOf(i);
/*  750 */       String columnName = this.fields[i].getOriginalName();
/*  751 */       String columnLabel = this.fields[i].getName();
/*  752 */       String fullColumnName = this.fields[i].getFullName();
/*      */ 
/*  754 */       if (columnLabel != null) {
/*  755 */         this.columnLabelToIndex.put(columnLabel, index);
/*      */       }
/*      */ 
/*  758 */       if (fullColumnName != null) {
/*  759 */         this.fullColumnNameToIndex.put(fullColumnName, index);
/*      */       }
/*      */ 
/*  762 */       if (columnName != null) {
/*  763 */         this.columnNameToIndex.put(columnName, index);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  768 */     this.hasBuiltIndexMapping = true;
/*      */   }
/*      */ 
/*      */   public void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/*  784 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   protected final void checkClosed()
/*      */     throws SQLException
/*      */   {
/*  794 */     if (this.isClosed)
/*  795 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected final void checkColumnBounds(int columnIndex)
/*      */     throws SQLException
/*      */   {
/*  812 */     if (columnIndex < 1) {
/*  813 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[] { Constants.integerValueOf(columnIndex), Constants.integerValueOf(this.fields.length) }), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  818 */     if (columnIndex > this.fields.length) {
/*  819 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[] { Constants.integerValueOf(columnIndex), Constants.integerValueOf(this.fields.length) }), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  826 */     if ((this.profileSql) || (this.useUsageAdvisor))
/*  827 */       this.columnUsed[(columnIndex - 1)] = true;
/*      */   }
/*      */ 
/*      */   protected void checkRowPos()
/*      */     throws SQLException
/*      */   {
/*  839 */     checkClosed();
/*      */ 
/*  841 */     if (!this.onValidRow)
/*  842 */       throw SQLError.createSQLException(this.invalidRowReason, "S1000", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private void setRowPositionValidity()
/*      */     throws SQLException
/*      */   {
/*  853 */     if ((!this.rowData.isDynamic()) && (this.rowData.size() == 0)) {
/*  854 */       this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
/*      */ 
/*  856 */       this.onValidRow = false;
/*  857 */     } else if (this.rowData.isBeforeFirst()) {
/*  858 */       this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
/*      */ 
/*  860 */       this.onValidRow = false;
/*  861 */     } else if (this.rowData.isAfterLast()) {
/*  862 */       this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
/*      */ 
/*  864 */       this.onValidRow = false;
/*      */     } else {
/*  866 */       this.onValidRow = true;
/*  867 */       this.invalidRowReason = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearNextResult()
/*      */   {
/*  876 */     this.nextResultSet = null;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  887 */     this.warningChain = null;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  908 */     realClose(true);
/*      */   }
/*      */ 
/*      */   private int convertToZeroWithEmptyCheck()
/*      */     throws SQLException
/*      */   {
/*  915 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  916 */       return 0;
/*      */     }
/*      */ 
/*  919 */     throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private String convertToZeroLiteralStringWithEmptyCheck()
/*      */     throws SQLException
/*      */   {
/*  926 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  927 */       return "0";
/*      */     }
/*      */ 
/*  930 */     throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public ResultSetInternalMethods copy()
/*      */     throws SQLException
/*      */   {
/*  938 */     ResultSetInternalMethods rs = getInstance(this.catalog, this.fields, this.rowData, this.connection, this.owningStatement, false);
/*      */ 
/*  941 */     return rs;
/*      */   }
/*      */ 
/*      */   public void redefineFieldsForDBMD(Field[] f) {
/*  945 */     this.fields = f;
/*      */ 
/*  947 */     for (int i = 0; i < this.fields.length; i++) {
/*  948 */       this.fields[i].setUseOldNameMetadata(true);
/*  949 */       this.fields[i].setConnection(this.connection);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException
/*      */   {
/*  955 */     cachedMetaData.fields = this.fields;
/*  956 */     cachedMetaData.columnNameToIndex = this.columnLabelToIndex;
/*  957 */     cachedMetaData.fullColumnNameToIndex = this.fullColumnNameToIndex;
/*  958 */     cachedMetaData.metadata = getMetaData();
/*      */   }
/*      */ 
/*      */   public void initializeFromCachedMetaData(CachedResultSetMetaData cachedMetaData) {
/*  962 */     this.fields = cachedMetaData.fields;
/*  963 */     this.columnLabelToIndex = cachedMetaData.columnNameToIndex;
/*  964 */     this.fullColumnNameToIndex = cachedMetaData.fullColumnNameToIndex;
/*  965 */     this.hasBuiltIndexMapping = true;
/*      */   }
/*      */ 
/*      */   public void deleteRow()
/*      */     throws SQLException
/*      */   {
/*  980 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   private String extractStringFromNativeColumn(int columnIndex, int mysqlType)
/*      */     throws SQLException
/*      */   {
/*  992 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/*  994 */     this.wasNullFlag = false;
/*      */ 
/*  996 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/*  997 */       this.wasNullFlag = true;
/*      */ 
/*  999 */       return null;
/*      */     }
/*      */ 
/* 1002 */     this.wasNullFlag = false;
/*      */ 
/* 1004 */     String encoding = this.fields[columnIndexMinusOne].getCharacterSet();
/*      */ 
/* 1007 */     return this.thisRow.getString(columnIndex - 1, encoding, this.connection);
/*      */   }
/*      */ 
/*      */   protected synchronized Date fastDateCreate(Calendar cal, int year, int month, int day)
/*      */   {
/* 1012 */     if (this.useLegacyDatetimeCode) {
/* 1013 */       return TimeUtil.fastDateCreate(year, month, day, cal);
/*      */     }
/*      */ 
/* 1016 */     if (cal == null) {
/* 1017 */       createCalendarIfNeeded();
/* 1018 */       cal = this.fastDateCal;
/*      */     }
/*      */ 
/* 1021 */     boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
/*      */ 
/* 1023 */     return TimeUtil.fastDateCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : cal, cal, year, month, day);
/*      */   }
/*      */ 
/*      */   protected synchronized Time fastTimeCreate(Calendar cal, int hour, int minute, int second)
/*      */     throws SQLException
/*      */   {
/* 1030 */     if (!this.useLegacyDatetimeCode) {
/* 1031 */       return TimeUtil.fastTimeCreate(hour, minute, second, cal, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1034 */     if (cal == null) {
/* 1035 */       createCalendarIfNeeded();
/* 1036 */       cal = this.fastDateCal;
/*      */     }
/*      */ 
/* 1039 */     return TimeUtil.fastTimeCreate(cal, hour, minute, second, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected synchronized Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*      */   {
/* 1045 */     if (!this.useLegacyDatetimeCode) {
/* 1046 */       return TimeUtil.fastTimestampCreate(cal.getTimeZone(), year, month, day, hour, minute, seconds, secondsPart);
/*      */     }
/*      */ 
/* 1050 */     if (cal == null) {
/* 1051 */       createCalendarIfNeeded();
/* 1052 */       cal = this.fastDateCal;
/*      */     }
/*      */ 
/* 1055 */     boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
/*      */ 
/* 1057 */     return TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : null, cal, year, month, day, hour, minute, seconds, secondsPart);
/*      */   }
/*      */ 
/*      */   public synchronized int findColumn(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1106 */     checkClosed();
/*      */ 
/* 1108 */     if (!this.hasBuiltIndexMapping) {
/* 1109 */       buildIndexMapping();
/*      */     }
/*      */ 
/* 1112 */     Integer index = (Integer)this.columnToIndexCache.get(columnName);
/*      */ 
/* 1114 */     if (index != null) {
/* 1115 */       return index.intValue() + 1;
/*      */     }
/*      */ 
/* 1118 */     index = (Integer)this.columnLabelToIndex.get(columnName);
/*      */ 
/* 1120 */     if ((index == null) && (this.useColumnNamesInFindColumn)) {
/* 1121 */       index = (Integer)this.columnNameToIndex.get(columnName);
/*      */     }
/*      */ 
/* 1124 */     if (index == null) {
/* 1125 */       index = (Integer)this.fullColumnNameToIndex.get(columnName);
/*      */     }
/*      */ 
/* 1128 */     if (index != null) {
/* 1129 */       this.columnToIndexCache.put(columnName, index);
/*      */ 
/* 1131 */       return index.intValue() + 1;
/*      */     }
/*      */ 
/* 1136 */     for (int i = 0; i < this.fields.length; i++) {
/* 1137 */       if (this.fields[i].getName().equalsIgnoreCase(columnName))
/* 1138 */         return i + 1;
/* 1139 */       if (this.fields[i].getFullName().equalsIgnoreCase(columnName))
/*      */       {
/* 1141 */         return i + 1;
/*      */       }
/*      */     }
/*      */ 
/* 1145 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public boolean first()
/*      */     throws SQLException
/*      */   {
/* 1165 */     checkClosed();
/*      */ 
/* 1167 */     boolean b = true;
/*      */ 
/* 1169 */     if (this.rowData.isEmpty()) {
/* 1170 */       b = false;
/*      */     }
/*      */     else {
/* 1173 */       if (this.onInsertRow) {
/* 1174 */         this.onInsertRow = false;
/*      */       }
/*      */ 
/* 1177 */       if (this.doingUpdates) {
/* 1178 */         this.doingUpdates = false;
/*      */       }
/*      */ 
/* 1181 */       this.rowData.beforeFirst();
/* 1182 */       this.thisRow = this.rowData.next();
/*      */     }
/*      */ 
/* 1185 */     setRowPositionValidity();
/*      */ 
/* 1187 */     return b;
/*      */   }
/*      */ 
/*      */   public Array getArray(int i)
/*      */     throws SQLException
/*      */   {
/* 1204 */     checkColumnBounds(i);
/*      */ 
/* 1206 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public Array getArray(String colName)
/*      */     throws SQLException
/*      */   {
/* 1223 */     return getArray(findColumn(colName));
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1252 */     checkRowPos();
/*      */ 
/* 1254 */     if (!this.isBinaryEncoded) {
/* 1255 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */ 
/* 1258 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1273 */     return getAsciiStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1290 */     if (!this.isBinaryEncoded) {
/* 1291 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1294 */       if (stringVal != null) {
/* 1295 */         if (stringVal.length() == 0)
/*      */         {
/* 1297 */           BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */ 
/* 1300 */           return val;
/*      */         }
/*      */         try
/*      */         {
/* 1304 */           BigDecimal val = new BigDecimal(stringVal);
/*      */ 
/* 1306 */           return val;
/*      */         } catch (NumberFormatException ex) {
/* 1308 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1316 */       return null;
/*      */     }
/*      */ 
/* 1319 */     return getNativeBigDecimal(columnIndex);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1340 */     if (!this.isBinaryEncoded) {
/* 1341 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1344 */       if (stringVal != null) {
/* 1345 */         if (stringVal.length() == 0) {
/* 1346 */           BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */           try
/*      */           {
/* 1350 */             return val.setScale(scale);
/*      */           } catch (ArithmeticException ex) {
/*      */             try {
/* 1353 */               return val.setScale(scale, 4);
/*      */             }
/*      */             catch (ArithmeticException arEx) {
/* 1356 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, new Integer(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1366 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/*      */           BigDecimal val;
/* 1368 */           if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 1369 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 1371 */             val = new BigDecimal(valueAsLong);
/*      */           } else {
/* 1373 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Constants.integerValueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1382 */           return val.setScale(scale);
/*      */         }
/*      */         catch (ArithmeticException ex)
/*      */         {
/*      */           try
/*      */           {
/*      */             BigDecimal val;
/* 1385 */             return val.setScale(scale, 4);
/*      */           } catch (ArithmeticException arithEx) {
/* 1387 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Constants.integerValueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1396 */       return null;
/*      */     }
/*      */ 
/* 1399 */     return getNativeBigDecimal(columnIndex, scale);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1415 */     return getBigDecimal(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(String columnName, int scale)
/*      */     throws SQLException
/*      */   {
/* 1435 */     return getBigDecimal(findColumn(columnName), scale);
/*      */   }
/*      */ 
/*      */   private final BigDecimal getBigDecimalFromString(String stringVal, int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1442 */     if (stringVal != null) {
/* 1443 */       if (stringVal.length() == 0) {
/* 1444 */         BigDecimal bdVal = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */         try
/*      */         {
/* 1447 */           return bdVal.setScale(scale);
/*      */         } catch (ArithmeticException ex) {
/*      */           try {
/* 1450 */             return bdVal.setScale(scale, 4);
/*      */           } catch (ArithmeticException arEx) {
/* 1452 */             throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1463 */         return new BigDecimal(stringVal).setScale(scale);
/*      */       } catch (ArithmeticException ex) {
/*      */         try {
/* 1466 */           return new BigDecimal(stringVal).setScale(scale, 4);
/*      */         }
/*      */         catch (ArithmeticException arEx) {
/* 1469 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/* 1477 */         if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 1478 */           long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */           try
/*      */           {
/* 1481 */             return new BigDecimal(valueAsLong).setScale(scale);
/*      */           } catch (ArithmeticException arEx1) {
/*      */             try {
/* 1484 */               return new BigDecimal(valueAsLong).setScale(scale, 4);
/*      */             }
/*      */             catch (ArithmeticException arEx2) {
/* 1487 */               throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1496 */         if ((this.fields[(columnIndex - 1)].getMysqlType() == 1) && (this.connection.getTinyInt1isBit()) && (this.fields[(columnIndex - 1)].getLength() == 1L))
/*      */         {
/* 1498 */           return new BigDecimal(stringVal.equalsIgnoreCase("true") ? 1.0D : 0.0D).setScale(scale);
/*      */         }
/*      */ 
/* 1501 */         throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1509 */     return null;
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1530 */     checkRowPos();
/*      */ 
/* 1532 */     if (!this.isBinaryEncoded) {
/* 1533 */       checkColumnBounds(columnIndex);
/*      */ 
/* 1535 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1537 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1538 */         this.wasNullFlag = true;
/*      */ 
/* 1540 */         return null;
/*      */       }
/*      */ 
/* 1543 */       this.wasNullFlag = false;
/*      */ 
/* 1545 */       return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 1548 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1563 */     return getBinaryStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public java.sql.Blob getBlob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1578 */     if (!this.isBinaryEncoded) {
/* 1579 */       checkRowPos();
/*      */ 
/* 1581 */       checkColumnBounds(columnIndex);
/*      */ 
/* 1583 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1585 */       if (this.thisRow.isNull(columnIndexMinusOne))
/* 1586 */         this.wasNullFlag = true;
/*      */       else {
/* 1588 */         this.wasNullFlag = false;
/*      */       }
/*      */ 
/* 1591 */       if (this.wasNullFlag) {
/* 1592 */         return null;
/*      */       }
/*      */ 
/* 1595 */       if (!this.connection.getEmulateLocators()) {
/* 1596 */         return new Blob(this.thisRow.getColumnValue(columnIndexMinusOne), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1599 */       return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1602 */     return getNativeBlob(columnIndex);
/*      */   }
/*      */ 
/*      */   public java.sql.Blob getBlob(String colName)
/*      */     throws SQLException
/*      */   {
/* 1617 */     return getBlob(findColumn(colName));
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1633 */     checkColumnBounds(columnIndex);
/*      */ 
/* 1640 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1642 */     Field field = this.fields[columnIndexMinusOne];
/*      */ 
/* 1644 */     if (field.getMysqlType() == 16) {
/* 1645 */       return byteArrayToBoolean(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 1648 */     this.wasNullFlag = false;
/*      */ 
/* 1650 */     int sqlType = field.getSQLType();
/*      */ 
/* 1652 */     switch (sqlType) {
/*      */     case 16:
/* 1654 */       if (field.getMysqlType() == -1) {
/* 1655 */         String stringVal = getString(columnIndex);
/*      */ 
/* 1657 */         return getBooleanFromString(stringVal, columnIndex);
/*      */       }
/*      */ 
/* 1660 */       long boolVal = getLong(columnIndex, false);
/*      */ 
/* 1662 */       return (boolVal == -1L) || (boolVal > 0L);
/*      */     case -7:
/*      */     case -6:
/*      */     case -5:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/* 1673 */       long boolVal = getLong(columnIndex, false);
/*      */ 
/* 1675 */       return (boolVal == -1L) || (boolVal > 0L);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/* 1677 */     case 15: } if (this.connection.getPedantic())
/*      */     {
/* 1679 */       switch (sqlType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case 70:
/*      */       case 91:
/*      */       case 92:
/*      */       case 93:
/*      */       case 2000:
/*      */       case 2002:
/*      */       case 2003:
/*      */       case 2004:
/*      */       case 2005:
/*      */       case 2006:
/* 1693 */         throw SQLError.createSQLException("Required type conversion not allowed", "22018", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1698 */     if ((sqlType == -2) || (sqlType == -3) || (sqlType == -4) || (sqlType == 2004))
/*      */     {
/* 1702 */       return byteArrayToBoolean(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 1705 */     if (this.useUsageAdvisor) {
/* 1706 */       issueConversionViaParsingWarning("getBoolean()", columnIndex, this.thisRow.getColumnValue(columnIndexMinusOne), this.fields[columnIndex], new int[] { 16, 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 1718 */     String stringVal = getString(columnIndex);
/*      */ 
/* 1720 */     return getBooleanFromString(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   private boolean byteArrayToBoolean(int columnIndexMinusOne) throws SQLException
/*      */   {
/* 1725 */     Object value = this.thisRow.getColumnValue(columnIndexMinusOne);
/*      */ 
/* 1727 */     if (value == null) {
/* 1728 */       this.wasNullFlag = true;
/*      */ 
/* 1730 */       return false;
/*      */     }
/*      */ 
/* 1733 */     this.wasNullFlag = false;
/*      */ 
/* 1735 */     if (((byte[])value).length == 0) {
/* 1736 */       return false;
/*      */     }
/*      */ 
/* 1739 */     byte boolVal = ((byte[])value)[0];
/*      */ 
/* 1741 */     if (boolVal == 49)
/* 1742 */       return true;
/* 1743 */     if (boolVal == 48) {
/* 1744 */       return false;
/*      */     }
/*      */ 
/* 1747 */     return (boolVal == -1) || (boolVal > 0);
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1762 */     return getBoolean(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final boolean getBooleanFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 1767 */     if ((stringVal != null) && (stringVal.length() > 0)) {
/* 1768 */       int c = Character.toLowerCase(stringVal.charAt(0));
/*      */ 
/* 1770 */       return (c == 116) || (c == 121) || (c == 49) || (stringVal.equals("-1"));
/*      */     }
/*      */ 
/* 1774 */     return false;
/*      */   }
/*      */ 
/*      */   public byte getByte(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1789 */     if (!this.isBinaryEncoded) {
/* 1790 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1792 */       if ((this.wasNullFlag) || (stringVal == null)) {
/* 1793 */         return 0;
/*      */       }
/*      */ 
/* 1796 */       return getByteFromString(stringVal, columnIndex);
/*      */     }
/*      */ 
/* 1799 */     return getNativeByte(columnIndex);
/*      */   }
/*      */ 
/*      */   public byte getByte(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1814 */     return getByte(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final byte getByteFromString(String stringVal, int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1820 */     if ((stringVal != null) && (stringVal.length() == 0)) {
/* 1821 */       return (byte)convertToZeroWithEmptyCheck();
/*      */     }
/*      */ 
/* 1832 */     if (stringVal == null) {
/* 1833 */       return 0;
/*      */     }
/*      */ 
/* 1836 */     stringVal = stringVal.trim();
/*      */     try
/*      */     {
/* 1839 */       int decimalIndex = stringVal.indexOf(".");
/*      */ 
/* 1842 */       if (decimalIndex != -1) {
/* 1843 */         double valueAsDouble = Double.parseDouble(stringVal);
/*      */ 
/* 1845 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 1846 */           (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D)))
/*      */         {
/* 1848 */           throwRangeException(stringVal, columnIndex, -6);
/*      */         }
/*      */ 
/* 1853 */         return (byte)(int)valueAsDouble;
/*      */       }
/*      */ 
/* 1856 */       long valueAsLong = Long.parseLong(stringVal);
/*      */ 
/* 1858 */       if ((this.jdbcCompliantTruncationForReads) && (
/* 1859 */         (valueAsLong < -128L) || (valueAsLong > 127L)))
/*      */       {
/* 1861 */         throwRangeException(String.valueOf(valueAsLong), columnIndex, -6);
/*      */       }
/*      */ 
/* 1866 */       return (byte)(int)valueAsLong; } catch (NumberFormatException NFE) {
/*      */     }
/* 1868 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Value____173") + stringVal + Messages.getString("ResultSet.___is_out_of_range_[-127,127]_174"), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1893 */     return getBytes(columnIndex, false);
/*      */   }
/*      */ 
/*      */   protected byte[] getBytes(int columnIndex, boolean noConversion) throws SQLException
/*      */   {
/* 1898 */     if (!this.isBinaryEncoded) {
/* 1899 */       checkRowPos();
/*      */ 
/* 1901 */       checkColumnBounds(columnIndex);
/*      */ 
/* 1903 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1905 */       if (this.thisRow.isNull(columnIndexMinusOne))
/* 1906 */         this.wasNullFlag = true;
/*      */       else {
/* 1908 */         this.wasNullFlag = false;
/*      */       }
/*      */ 
/* 1911 */       if (this.wasNullFlag) {
/* 1912 */         return null;
/*      */       }
/*      */ 
/* 1915 */       return this.thisRow.getColumnValue(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 1918 */     return getNativeBytes(columnIndex, noConversion);
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1933 */     return getBytes(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final byte[] getBytesFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 1938 */     if (stringVal != null) {
/* 1939 */       return StringUtils.getBytes(stringVal, this.connection.getEncoding(), this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1946 */     return null;
/*      */   }
/*      */ 
/*      */   public int getBytesSize() throws SQLException {
/* 1950 */     RowData localRowData = this.rowData;
/*      */ 
/* 1952 */     checkClosed();
/*      */ 
/* 1954 */     if ((localRowData instanceof RowDataStatic)) {
/* 1955 */       int bytesSize = 0;
/*      */ 
/* 1957 */       int numRows = localRowData.size();
/*      */ 
/* 1959 */       for (int i = 0; i < numRows; i++) {
/* 1960 */         bytesSize += localRowData.getAt(i).getBytesSize();
/*      */       }
/*      */ 
/* 1963 */       return bytesSize;
/*      */     }
/*      */ 
/* 1966 */     return -1;
/*      */   }
/*      */ 
/*      */   protected Calendar getCalendarInstanceForSessionOrNew()
/*      */   {
/* 1974 */     if (this.connection != null) {
/* 1975 */       return this.connection.getCalendarInstanceForSessionOrNew();
/*      */     }
/*      */ 
/* 1978 */     return new GregorianCalendar();
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1999 */     if (!this.isBinaryEncoded) {
/* 2000 */       checkColumnBounds(columnIndex);
/*      */ 
/* 2002 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 2004 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2005 */         this.wasNullFlag = true;
/*      */ 
/* 2007 */         return null;
/*      */       }
/*      */ 
/* 2010 */       this.wasNullFlag = false;
/*      */ 
/* 2012 */       return this.thisRow.getReader(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 2015 */     return getNativeCharacterStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2035 */     return getCharacterStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final Reader getCharacterStreamFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 2040 */     if (stringVal != null) {
/* 2041 */       return new StringReader(stringVal);
/*      */     }
/*      */ 
/* 2044 */     return null;
/*      */   }
/*      */ 
/*      */   public java.sql.Clob getClob(int i)
/*      */     throws SQLException
/*      */   {
/* 2059 */     if (!this.isBinaryEncoded) {
/* 2060 */       String asString = getStringForClob(i);
/*      */ 
/* 2062 */       if (asString == null) {
/* 2063 */         return null;
/*      */       }
/*      */ 
/* 2066 */       return new Clob(asString, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2069 */     return getNativeClob(i);
/*      */   }
/*      */ 
/*      */   public java.sql.Clob getClob(String colName)
/*      */     throws SQLException
/*      */   {
/* 2084 */     return getClob(findColumn(colName));
/*      */   }
/*      */ 
/*      */   private final java.sql.Clob getClobFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 2089 */     return new Clob(stringVal, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/* 2102 */     return 1007;
/*      */   }
/*      */ 
/*      */   public String getCursorName()
/*      */     throws SQLException
/*      */   {
/* 2131 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public Date getDate(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2148 */     return getDate(columnIndex, null);
/*      */   }
/*      */ 
/*      */   public Date getDate(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2169 */     if (this.isBinaryEncoded) {
/* 2170 */       return getNativeDate(columnIndex, cal);
/*      */     }
/*      */ 
/* 2173 */     if (!this.useFastDateParsing) {
/* 2174 */       String stringVal = getStringInternal(columnIndex, false);
/*      */ 
/* 2176 */       if (stringVal == null) {
/* 2177 */         return null;
/*      */       }
/*      */ 
/* 2180 */       return getDateFromString(stringVal, columnIndex, cal);
/*      */     }
/*      */ 
/* 2183 */     checkColumnBounds(columnIndex);
/*      */ 
/* 2185 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 2187 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2188 */       this.wasNullFlag = true;
/*      */ 
/* 2190 */       return null;
/*      */     }
/*      */ 
/* 2193 */     this.wasNullFlag = false;
/*      */ 
/* 2195 */     return this.thisRow.getDateFast(columnIndexMinusOne, this.connection, this, cal);
/*      */   }
/*      */ 
/*      */   public Date getDate(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2211 */     return getDate(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Date getDate(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2231 */     return getDate(findColumn(columnName), cal);
/*      */   }
/* 2236 */   private final Date getDateFromString(String stringVal, int columnIndex, Calendar targetCalendar) throws SQLException { int year = 0;
/* 2237 */     int month = 0;
/* 2238 */     int day = 0;
/*      */     SQLException sqlEx;
/*      */     try { this.wasNullFlag = false;
/*      */ 
/* 2243 */       if (stringVal == null) {
/* 2244 */         this.wasNullFlag = true;
/*      */ 
/* 2246 */         return null;
/*      */       }
/*      */ 
/* 2257 */       stringVal = stringVal.trim();
/*      */ 
/* 2259 */       if ((stringVal.equals("0")) || (stringVal.equals("0000-00-00")) || (stringVal.equals("0000-00-00 00:00:00")) || (stringVal.equals("00000000000000")) || (stringVal.equals("0")))
/*      */       {
/* 2264 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 2266 */           this.wasNullFlag = true;
/*      */ 
/* 2268 */           return null;
/* 2269 */         }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 2271 */           throw SQLError.createSQLException("Value '" + stringVal + "' can not be represented as java.sql.Date", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 2278 */         return fastDateCreate(targetCalendar, 1, 1, 1);
/*      */       }
/* 2280 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 7)
/*      */       {
/* 2282 */         switch (stringVal.length()) {
/*      */         case 19:
/*      */         case 21:
/* 2285 */           year = Integer.parseInt(stringVal.substring(0, 4));
/* 2286 */           month = Integer.parseInt(stringVal.substring(5, 7));
/* 2287 */           day = Integer.parseInt(stringVal.substring(8, 10));
/*      */ 
/* 2289 */           return fastDateCreate(targetCalendar, year, month, day);
/*      */         case 8:
/*      */         case 14:
/* 2294 */           year = Integer.parseInt(stringVal.substring(0, 4));
/* 2295 */           month = Integer.parseInt(stringVal.substring(4, 6));
/* 2296 */           day = Integer.parseInt(stringVal.substring(6, 8));
/*      */ 
/* 2298 */           return fastDateCreate(targetCalendar, year, month, day);
/*      */         case 6:
/*      */         case 10:
/*      */         case 12:
/* 2304 */           year = Integer.parseInt(stringVal.substring(0, 2));
/*      */ 
/* 2306 */           if (year <= 69) {
/* 2307 */             year += 100;
/*      */           }
/*      */ 
/* 2310 */           month = Integer.parseInt(stringVal.substring(2, 4));
/* 2311 */           day = Integer.parseInt(stringVal.substring(4, 6));
/*      */ 
/* 2313 */           return fastDateCreate(targetCalendar, year + 1900, month, day);
/*      */         case 4:
/* 2317 */           year = Integer.parseInt(stringVal.substring(0, 4));
/*      */ 
/* 2319 */           if (year <= 69) {
/* 2320 */             year += 100;
/*      */           }
/*      */ 
/* 2323 */           month = Integer.parseInt(stringVal.substring(2, 4));
/*      */ 
/* 2325 */           return fastDateCreate(targetCalendar, year + 1900, month, 1);
/*      */         case 2:
/* 2329 */           year = Integer.parseInt(stringVal.substring(0, 2));
/*      */ 
/* 2331 */           if (year <= 69) {
/* 2332 */             year += 100;
/*      */           }
/*      */ 
/* 2335 */           return fastDateCreate(targetCalendar, year + 1900, 1, 1);
/*      */         case 3:
/*      */         case 5:
/*      */         case 7:
/*      */         case 9:
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/* 2339 */         case 20: } throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2344 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
/*      */       {
/* 2346 */         if ((stringVal.length() == 2) || (stringVal.length() == 1)) {
/* 2347 */           year = Integer.parseInt(stringVal);
/*      */ 
/* 2349 */           if (year <= 69) {
/* 2350 */             year += 100;
/*      */           }
/*      */ 
/* 2353 */           year += 1900;
/*      */         } else {
/* 2355 */           year = Integer.parseInt(stringVal.substring(0, 4));
/*      */         }
/*      */ 
/* 2358 */         return fastDateCreate(targetCalendar, year, 1, 1);
/* 2359 */       }if (this.fields[(columnIndex - 1)].getMysqlType() == 11) {
/* 2360 */         return fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */       }
/* 2362 */       if (stringVal.length() < 10) {
/* 2363 */         if (stringVal.length() == 8) {
/* 2364 */           return fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */         }
/*      */ 
/* 2367 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2373 */       if (stringVal.length() != 18) {
/* 2374 */         year = Integer.parseInt(stringVal.substring(0, 4));
/* 2375 */         month = Integer.parseInt(stringVal.substring(5, 7));
/* 2376 */         day = Integer.parseInt(stringVal.substring(8, 10));
/*      */       }
/*      */       else {
/* 2379 */         StringTokenizer st = new StringTokenizer(stringVal, "- ");
/*      */ 
/* 2381 */         year = Integer.parseInt(st.nextToken());
/* 2382 */         month = Integer.parseInt(st.nextToken());
/* 2383 */         day = Integer.parseInt(st.nextToken());
/*      */       }
/*      */ 
/* 2387 */       return fastDateCreate(targetCalendar, year, month, day);
/*      */     } catch (SQLException sqlEx) {
/* 2389 */       throw sqlEx;
/*      */     } catch (Exception e) {
/* 2391 */       sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */ 
/* 2396 */       sqlEx.initCause(e);
/*      */     }
/* 2398 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private TimeZone getDefaultTimeZone()
/*      */   {
/* 2403 */     if ((!this.useLegacyDatetimeCode) && (this.connection != null)) {
/* 2404 */       return this.serverTimeZoneTz;
/*      */     }
/*      */ 
/* 2407 */     return this.connection.getDefaultTimeZone();
/*      */   }
/*      */ 
/*      */   public double getDouble(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2422 */     if (!this.isBinaryEncoded) {
/* 2423 */       return getDoubleInternal(columnIndex);
/*      */     }
/*      */ 
/* 2426 */     return getNativeDouble(columnIndex);
/*      */   }
/*      */ 
/*      */   public double getDouble(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2441 */     return getDouble(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final double getDoubleFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 2446 */     return getDoubleInternal(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   protected double getDoubleInternal(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 2462 */     return getDoubleInternal(getString(colIndex), colIndex); } 
/*      */   protected double getDoubleInternal(String stringVal, int colIndex) throws SQLException { // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: ifnonnull +5 -> 6
/*      */     //   4: dconst_0
/*      */     //   5: dreturn
/*      */     //   6: aload_1
/*      */     //   7: invokevirtual 200	java/lang/String:length	()I
/*      */     //   10: ifne +9 -> 19
/*      */     //   13: aload_0
/*      */     //   14: invokespecial 248	com/mysql/jdbc/ResultSetImpl:convertToZeroWithEmptyCheck	()I
/*      */     //   17: i2d
/*      */     //   18: dreturn
/*      */     //   19: aload_1
/*      */     //   20: invokestatic 252	java/lang/Double:parseDouble	(Ljava/lang/String;)D
/*      */     //   23: dstore_3
/*      */     //   24: aload_0
/*      */     //   25: getfield 51	com/mysql/jdbc/ResultSetImpl:useStrictFloatingPoint	Z
/*      */     //   28: ifeq +120 -> 148
/*      */     //   31: dload_3
/*      */     //   32: ldc2_w 317
/*      */     //   35: dcmpl
/*      */     //   36: ifne +10 -> 46
/*      */     //   39: ldc2_w 319
/*      */     //   42: dstore_3
/*      */     //   43: goto +105 -> 148
/*      */     //   46: dload_3
/*      */     //   47: ldc2_w 321
/*      */     //   50: dcmpl
/*      */     //   51: ifne +10 -> 61
/*      */     //   54: ldc2_w 323
/*      */     //   57: dstore_3
/*      */     //   58: goto +90 -> 148
/*      */     //   61: dload_3
/*      */     //   62: ldc2_w 325
/*      */     //   65: dcmpl
/*      */     //   66: ifne +10 -> 76
/*      */     //   69: ldc2_w 327
/*      */     //   72: dstore_3
/*      */     //   73: goto +75 -> 148
/*      */     //   76: dload_3
/*      */     //   77: ldc2_w 329
/*      */     //   80: dcmpl
/*      */     //   81: ifne +10 -> 91
/*      */     //   84: ldc2_w 331
/*      */     //   87: dstore_3
/*      */     //   88: goto +60 -> 148
/*      */     //   91: dload_3
/*      */     //   92: ldc2_w 333
/*      */     //   95: dcmpl
/*      */     //   96: ifne +10 -> 106
/*      */     //   99: ldc2_w 331
/*      */     //   102: dstore_3
/*      */     //   103: goto +45 -> 148
/*      */     //   106: dload_3
/*      */     //   107: ldc2_w 335
/*      */     //   110: dcmpl
/*      */     //   111: ifne +10 -> 121
/*      */     //   114: ldc2_w 337
/*      */     //   117: dstore_3
/*      */     //   118: goto +30 -> 148
/*      */     //   121: dload_3
/*      */     //   122: ldc2_w 339
/*      */     //   125: dcmpl
/*      */     //   126: ifne +10 -> 136
/*      */     //   129: ldc2_w 341
/*      */     //   132: dstore_3
/*      */     //   133: goto +15 -> 148
/*      */     //   136: dload_3
/*      */     //   137: ldc2_w 343
/*      */     //   140: dcmpl
/*      */     //   141: ifne +7 -> 148
/*      */     //   144: ldc2_w 337
/*      */     //   147: dstore_3
/*      */     //   148: dload_3
/*      */     //   149: dreturn
/*      */     //   150: astore_3
/*      */     //   151: aload_0
/*      */     //   152: getfield 63	com/mysql/jdbc/ResultSetImpl:fields	[Lcom/mysql/jdbc/Field;
/*      */     //   155: iload_2
/*      */     //   156: iconst_1
/*      */     //   157: isub
/*      */     //   158: aaload
/*      */     //   159: invokevirtual 211	com/mysql/jdbc/Field:getMysqlType	()I
/*      */     //   162: bipush 16
/*      */     //   164: if_icmpne +14 -> 178
/*      */     //   167: aload_0
/*      */     //   168: iload_2
/*      */     //   169: invokespecial 212	com/mysql/jdbc/ResultSetImpl:getNumericRepresentationOfSQLBitType	(I)J
/*      */     //   172: lstore 4
/*      */     //   174: lload 4
/*      */     //   176: l2d
/*      */     //   177: dreturn
/*      */     //   178: ldc_w 345
/*      */     //   181: iconst_2
/*      */     //   182: anewarray 14	java/lang/Object
/*      */     //   185: dup
/*      */     //   186: iconst_0
/*      */     //   187: aload_1
/*      */     //   188: aastore
/*      */     //   189: dup
/*      */     //   190: iconst_1
/*      */     //   191: iload_2
/*      */     //   192: invokestatic 134	com/mysql/jdbc/Constants:integerValueOf	(I)Ljava/lang/Integer;
/*      */     //   195: aastore
/*      */     //   196: invokestatic 143	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   199: ldc 118
/*      */     //   201: aload_0
/*      */     //   202: invokevirtual 119	com/mysql/jdbc/ResultSetImpl:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
/*      */     //   205: invokestatic 120	com/mysql/jdbc/SQLError:createSQLException	(Ljava/lang/String;Ljava/lang/String;Lcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
/*      */     //   208: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	5	150	java/lang/NumberFormatException
/*      */     //   6	18	150	java/lang/NumberFormatException
/*      */     //   19	149	150	java/lang/NumberFormatException } 
/* 2539 */   public int getFetchDirection() throws SQLException { return this.fetchDirection;
/*      */   }
/*      */ 
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/* 2551 */     return this.fetchSize;
/*      */   }
/*      */ 
/*      */   public char getFirstCharOfQuery()
/*      */   {
/* 2561 */     return this.firstCharOfQuery;
/*      */   }
/*      */ 
/*      */   public float getFloat(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2576 */     if (!this.isBinaryEncoded) {
/* 2577 */       String val = null;
/*      */ 
/* 2579 */       val = getString(columnIndex);
/*      */ 
/* 2581 */       return getFloatFromString(val, columnIndex);
/*      */     }
/*      */ 
/* 2584 */     return getNativeFloat(columnIndex);
/*      */   }
/*      */ 
/*      */   public float getFloat(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2599 */     return getFloat(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final float getFloatFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 2605 */       if (val != null) {
/* 2606 */         if (val.length() == 0) {
/* 2607 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2610 */         float f = Float.parseFloat(val);
/*      */ 
/* 2612 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2613 */           (f == 1.4E-45F) || (f == 3.4028235E+38F))) {
/* 2614 */           double valAsDouble = Double.parseDouble(val);
/*      */ 
/* 2620 */           if ((valAsDouble < 1.401298464324817E-45D - MIN_DIFF_PREC) || (valAsDouble > 3.402823466385289E+38D - MAX_DIFF_PREC))
/*      */           {
/* 2622 */             throwRangeException(String.valueOf(valAsDouble), columnIndex, 6);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2628 */         return f;
/*      */       }
/*      */ 
/* 2631 */       return 0.0F;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2634 */         Double valueAsDouble = new Double(val);
/* 2635 */         float valueAsFloat = valueAsDouble.floatValue();
/*      */ 
/* 2637 */         if (this.jdbcCompliantTruncationForReads)
/*      */         {
/* 2639 */           if (((this.jdbcCompliantTruncationForReads) && (valueAsFloat == (1.0F / -1.0F))) || (valueAsFloat == (1.0F / 1.0F)))
/*      */           {
/* 2642 */             throwRangeException(valueAsDouble.toString(), columnIndex, 6);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2647 */         return valueAsFloat;
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 2652 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getFloat()_-____200") + val + Messages.getString("ResultSet.___in_column__201") + columnIndex, "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public int getInt(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2673 */     checkRowPos();
/*      */ 
/* 2675 */     if (!this.isBinaryEncoded) {
/* 2676 */       int columnIndexMinusOne = columnIndex - 1;
/* 2677 */       if (this.useFastIntParsing) {
/* 2678 */         checkColumnBounds(columnIndex);
/*      */ 
/* 2680 */         if (this.thisRow.isNull(columnIndexMinusOne))
/* 2681 */           this.wasNullFlag = true;
/*      */         else {
/* 2683 */           this.wasNullFlag = false;
/*      */         }
/*      */ 
/* 2686 */         if (this.wasNullFlag) {
/* 2687 */           return 0;
/*      */         }
/*      */ 
/* 2690 */         if (this.thisRow.length(columnIndexMinusOne) == 0L) {
/* 2691 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2694 */         boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
/*      */ 
/* 2697 */         if (!needsFullParse) {
/*      */           try {
/* 2699 */             return getIntWithOverflowCheck(columnIndexMinusOne);
/*      */           }
/*      */           catch (NumberFormatException nfe) {
/*      */             try {
/* 2703 */               return parseIntAsDouble(columnIndex, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection));
/*      */             }
/*      */             catch (NumberFormatException valueAsLong)
/*      */             {
/* 2712 */               if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2713 */                 long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 2715 */                 if ((this.connection.getJdbcCompliantTruncationForReads()) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */                 {
/* 2718 */                   throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */                 }
/*      */ 
/* 2723 */                 return (int)valueAsLong;
/*      */               }
/*      */ 
/* 2726 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2742 */       String val = null;
/*      */       try
/*      */       {
/* 2745 */         val = getString(columnIndex);
/*      */ 
/* 2747 */         if (val != null) {
/* 2748 */           if (val.length() == 0) {
/* 2749 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 2752 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */           {
/* 2754 */             int intVal = Integer.parseInt(val);
/*      */ 
/* 2756 */             checkForIntegerTruncation(columnIndexMinusOne, null, intVal);
/*      */ 
/* 2758 */             return intVal;
/*      */           }
/*      */ 
/* 2762 */           int intVal = parseIntAsDouble(columnIndex, val);
/*      */ 
/* 2764 */           checkForIntegerTruncation(columnIndex, null, intVal);
/*      */ 
/* 2766 */           return intVal;
/*      */         }
/*      */ 
/* 2769 */         return 0;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2772 */           return parseIntAsDouble(columnIndex, val);
/*      */         }
/*      */         catch (NumberFormatException valueAsLong)
/*      */         {
/* 2777 */           if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2778 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 2780 */             if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */             {
/* 2782 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */ 
/* 2786 */             return (int)valueAsLong;
/*      */           }
/*      */ 
/* 2789 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2799 */     return getNativeInt(columnIndex);
/*      */   }
/*      */ 
/*      */   public int getInt(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2814 */     return getInt(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final int getIntFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 2820 */       if (val != null)
/*      */       {
/* 2822 */         if (val.length() == 0) {
/* 2823 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2826 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */         {
/* 2836 */           val = val.trim();
/*      */ 
/* 2838 */           int valueAsInt = Integer.parseInt(val);
/*      */ 
/* 2840 */           if ((this.jdbcCompliantTruncationForReads) && (
/* 2841 */             (valueAsInt == -2147483648) || (valueAsInt == 2147483647)))
/*      */           {
/* 2843 */             long valueAsLong = Long.parseLong(val);
/*      */ 
/* 2845 */             if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))
/*      */             {
/* 2847 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2854 */           return valueAsInt;
/*      */         }
/*      */ 
/* 2859 */         double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 2861 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2862 */           (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */         {
/* 2864 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */ 
/* 2869 */         return (int)valueAsDouble;
/*      */       }
/*      */ 
/* 2872 */       return 0;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2875 */         double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 2877 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2878 */           (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */         {
/* 2880 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */ 
/* 2885 */         return (int)valueAsDouble;
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 2890 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____206") + val + Messages.getString("ResultSet.___in_column__207") + columnIndex, "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public long getLong(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2910 */     return getLong(columnIndex, true);
/*      */   }
/*      */ 
/*      */   private long getLong(int columnIndex, boolean overflowCheck) throws SQLException {
/* 2914 */     if (!this.isBinaryEncoded) {
/* 2915 */       checkRowPos();
/*      */ 
/* 2917 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 2919 */       if (this.useFastIntParsing)
/*      */       {
/* 2921 */         checkColumnBounds(columnIndex);
/*      */ 
/* 2923 */         if (this.thisRow.isNull(columnIndexMinusOne))
/* 2924 */           this.wasNullFlag = true;
/*      */         else {
/* 2926 */           this.wasNullFlag = false;
/*      */         }
/*      */ 
/* 2929 */         if (this.wasNullFlag) {
/* 2930 */           return 0L;
/*      */         }
/*      */ 
/* 2933 */         if (this.thisRow.length(columnIndexMinusOne) == 0L) {
/* 2934 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2937 */         boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
/*      */ 
/* 2939 */         if (!needsFullParse) {
/*      */           try {
/* 2941 */             return getLongWithOverflowCheck(columnIndexMinusOne, overflowCheck);
/*      */           }
/*      */           catch (NumberFormatException nfe) {
/*      */             try {
/* 2945 */               return parseLongAsDouble(columnIndexMinusOne, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection));
/*      */             }
/*      */             catch (NumberFormatException newNfe)
/*      */             {
/* 2954 */               if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2955 */                 return getNumericRepresentationOfSQLBitType(columnIndex);
/*      */               }
/*      */ 
/* 2958 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2972 */       String val = null;
/*      */       try
/*      */       {
/* 2975 */         val = getString(columnIndex);
/*      */ 
/* 2977 */         if (val != null) {
/* 2978 */           if (val.length() == 0) {
/* 2979 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 2982 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
/* 2983 */             return parseLongWithOverflowCheck(columnIndexMinusOne, null, val, overflowCheck);
/*      */           }
/*      */ 
/* 2988 */           return parseLongAsDouble(columnIndexMinusOne, val);
/*      */         }
/*      */ 
/* 2991 */         return 0L;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2994 */           return parseLongAsDouble(columnIndexMinusOne, val);
/*      */         }
/*      */         catch (NumberFormatException newNfe)
/*      */         {
/* 2999 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3007 */     return getNativeLong(columnIndex, overflowCheck, true);
/*      */   }
/*      */ 
/*      */   public long getLong(String columnName)
/*      */     throws SQLException
/*      */   {
/* 3022 */     return getLong(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final long getLongFromString(String val, int columnIndexZeroBased) throws SQLException
/*      */   {
/*      */     try {
/* 3028 */       if (val != null)
/*      */       {
/* 3030 */         if (val.length() == 0) {
/* 3031 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 3034 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
/* 3035 */           return parseLongWithOverflowCheck(columnIndexZeroBased, null, val, true);
/*      */         }
/*      */ 
/* 3039 */         return parseLongAsDouble(columnIndexZeroBased, val);
/*      */       }
/*      */ 
/* 3042 */       return 0L;
/*      */     }
/*      */     catch (NumberFormatException nfe) {
/*      */       try {
/* 3046 */         return parseLongAsDouble(columnIndexZeroBased, val);
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 3051 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____211") + val + Messages.getString("ResultSet.___in_column__212") + (columnIndexZeroBased + 1), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 3070 */     checkClosed();
/*      */ 
/* 3072 */     return new ResultSetMetaData(this.fields, this.connection.getUseOldAliasMetadataBehavior(), getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected Array getNativeArray(int i)
/*      */     throws SQLException
/*      */   {
/* 3090 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeAsciiStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3120 */     checkRowPos();
/*      */ 
/* 3122 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3141 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3143 */     int scale = this.fields[(columnIndex - 1)].getDecimals();
/*      */ 
/* 3145 */     return getNativeBigDecimal(columnIndex, scale);
/*      */   }
/*      */ 
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 3164 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3166 */     String stringVal = null;
/*      */ 
/* 3168 */     Field f = this.fields[(columnIndex - 1)];
/*      */ 
/* 3170 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 3172 */     if (value == null) {
/* 3173 */       this.wasNullFlag = true;
/*      */ 
/* 3175 */       return null;
/*      */     }
/*      */ 
/* 3178 */     this.wasNullFlag = false;
/*      */ 
/* 3180 */     switch (f.getSQLType()) {
/*      */     case 2:
/*      */     case 3:
/* 3183 */       stringVal = StringUtils.toAsciiString((byte[])value);
/*      */ 
/* 3185 */       break;
/*      */     default:
/* 3187 */       stringVal = getNativeString(columnIndex);
/*      */     }
/*      */ 
/* 3190 */     return getBigDecimalFromString(stringVal, columnIndex, scale);
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeBinaryStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3212 */     checkRowPos();
/*      */ 
/* 3214 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 3216 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 3217 */       this.wasNullFlag = true;
/*      */ 
/* 3219 */       return null;
/*      */     }
/*      */ 
/* 3222 */     this.wasNullFlag = false;
/*      */ 
/* 3224 */     switch (this.fields[columnIndexMinusOne].getSQLType()) {
/*      */     case -7:
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case 2004:
/* 3230 */       return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 3233 */     byte[] b = getNativeBytes(columnIndex, false);
/*      */ 
/* 3235 */     if (b != null) {
/* 3236 */       return new ByteArrayInputStream(b);
/*      */     }
/*      */ 
/* 3239 */     return null;
/*      */   }
/*      */ 
/*      */   protected java.sql.Blob getNativeBlob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3254 */     checkRowPos();
/*      */ 
/* 3256 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3258 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 3260 */     if (value == null)
/* 3261 */       this.wasNullFlag = true;
/*      */     else {
/* 3263 */       this.wasNullFlag = false;
/*      */     }
/*      */ 
/* 3266 */     if (this.wasNullFlag) {
/* 3267 */       return null;
/*      */     }
/*      */ 
/* 3270 */     int mysqlType = this.fields[(columnIndex - 1)].getMysqlType();
/*      */ 
/* 3272 */     byte[] dataAsBytes = null;
/*      */ 
/* 3274 */     switch (mysqlType) {
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 3279 */       dataAsBytes = (byte[])value;
/* 3280 */       break;
/*      */     default:
/* 3283 */       dataAsBytes = getNativeBytes(columnIndex, false);
/*      */     }
/*      */ 
/* 3286 */     if (!this.connection.getEmulateLocators()) {
/* 3287 */       return new Blob(dataAsBytes, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3290 */     return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public static boolean arraysEqual(byte[] left, byte[] right) {
/* 3294 */     if (left == null) {
/* 3295 */       return right == null;
/*      */     }
/* 3297 */     if (right == null) {
/* 3298 */       return false;
/*      */     }
/* 3300 */     if (left.length != right.length) {
/* 3301 */       return false;
/*      */     }
/* 3303 */     for (int i = 0; i < left.length; i++) {
/* 3304 */       if (left[i] != right[i]) {
/* 3305 */         return false;
/*      */       }
/*      */     }
/* 3308 */     return true;
/*      */   }
/*      */ 
/*      */   protected byte getNativeByte(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3323 */     return getNativeByte(columnIndex, true);
/*      */   }
/*      */ 
/*      */   protected byte getNativeByte(int columnIndex, boolean overflowCheck) throws SQLException {
/* 3327 */     checkRowPos();
/*      */ 
/* 3329 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3331 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 3333 */     if (value == null) {
/* 3334 */       this.wasNullFlag = true;
/*      */ 
/* 3336 */       return 0;
/*      */     }
/*      */ 
/* 3339 */     if (value == null)
/* 3340 */       this.wasNullFlag = true;
/*      */     else {
/* 3342 */       this.wasNullFlag = false;
/*      */     }
/*      */ 
/* 3345 */     if (this.wasNullFlag) {
/* 3346 */       return 0;
/*      */     }
/*      */ 
/* 3349 */     columnIndex--;
/*      */ 
/* 3351 */     Field field = this.fields[columnIndex];
/*      */ 
/* 3353 */     switch (field.getMysqlType()) {
/*      */     case 16:
/* 3355 */       long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */ 
/* 3357 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((valueAsLong < -128L) || (valueAsLong > 127L)))
/*      */       {
/* 3360 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3364 */       return (byte)(int)valueAsLong;
/*      */     case 1:
/* 3366 */       byte valueAsByte = ((byte[])value)[0];
/*      */ 
/* 3368 */       if (!field.isUnsigned()) {
/* 3369 */         return valueAsByte;
/*      */       }
/*      */ 
/* 3372 */       short valueAsShort = valueAsByte >= 0 ? (short)valueAsByte : (short)(valueAsByte + 256);
/*      */ 
/* 3375 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && 
/* 3376 */         (valueAsShort > 127)) {
/* 3377 */         throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3382 */       return (byte)valueAsShort;
/*      */     case 2:
/*      */     case 13:
/* 3386 */       short valueAsShort = getNativeShort(columnIndex + 1);
/*      */ 
/* 3388 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3389 */         (valueAsShort < -128) || (valueAsShort > 127)))
/*      */       {
/* 3391 */         throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3396 */       return (byte)valueAsShort;
/*      */     case 3:
/*      */     case 9:
/* 3399 */       int valueAsInt = getNativeInt(columnIndex + 1, false);
/*      */ 
/* 3401 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3402 */         (valueAsInt < -128) || (valueAsInt > 127))) {
/* 3403 */         throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3408 */       return (byte)valueAsInt;
/*      */     case 4:
/* 3411 */       float valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */ 
/* 3413 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3414 */         (valueAsFloat < -128.0F) || (valueAsFloat > 127.0F)))
/*      */       {
/* 3417 */         throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3422 */       return (byte)(int)valueAsFloat;
/*      */     case 5:
/* 3425 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 3427 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3428 */         (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D)))
/*      */       {
/* 3430 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3435 */       return (byte)(int)valueAsDouble;
/*      */     case 8:
/* 3438 */       long valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */ 
/* 3440 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3441 */         (valueAsLong < -128L) || (valueAsLong > 127L)))
/*      */       {
/* 3443 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3448 */       return (byte)(int)valueAsLong;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 3451 */     case 15: } if (this.useUsageAdvisor) {
/* 3452 */       issueConversionViaParsingWarning("getByte()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 3462 */     return getByteFromString(getNativeString(columnIndex + 1), columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected byte[] getNativeBytes(int columnIndex, boolean noConversion)
/*      */     throws SQLException
/*      */   {
/* 3484 */     checkRowPos();
/*      */ 
/* 3486 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3488 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 3490 */     if (value == null)
/* 3491 */       this.wasNullFlag = true;
/*      */     else {
/* 3493 */       this.wasNullFlag = false;
/*      */     }
/*      */ 
/* 3496 */     if (this.wasNullFlag) {
/* 3497 */       return null;
/*      */     }
/*      */ 
/* 3500 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 3502 */     int mysqlType = field.getMysqlType();
/*      */ 
/* 3506 */     if (noConversion) {
/* 3507 */       mysqlType = 252;
/*      */     }
/*      */ 
/* 3510 */     switch (mysqlType) {
/*      */     case 16:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 3516 */       return (byte[])value;
/*      */     case 15:
/*      */     case 253:
/*      */     case 254:
/* 3521 */       if (!(value instanceof byte[])) break;
/* 3522 */       return (byte[])value;
/*      */     }
/*      */ 
/* 3526 */     int sqlType = field.getSQLType();
/*      */ 
/* 3528 */     if ((sqlType == -3) || (sqlType == -2)) {
/* 3529 */       return (byte[])value;
/*      */     }
/*      */ 
/* 3532 */     return getBytesFromString(getNativeString(columnIndex), columnIndex);
/*      */   }
/*      */ 
/*      */   protected Reader getNativeCharacterStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3553 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 3555 */     switch (this.fields[columnIndexMinusOne].getSQLType()) {
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/*      */     case 2005:
/* 3560 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 3561 */         this.wasNullFlag = true;
/*      */ 
/* 3563 */         return null;
/*      */       }
/*      */ 
/* 3566 */       this.wasNullFlag = false;
/*      */ 
/* 3568 */       return this.thisRow.getReader(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 3571 */     String asString = null;
/*      */ 
/* 3573 */     asString = getStringForClob(columnIndex);
/*      */ 
/* 3575 */     if (asString == null) {
/* 3576 */       return null;
/*      */     }
/*      */ 
/* 3579 */     return getCharacterStreamFromString(asString, columnIndex);
/*      */   }
/*      */ 
/*      */   protected java.sql.Clob getNativeClob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3594 */     String stringVal = getStringForClob(columnIndex);
/*      */ 
/* 3596 */     if (stringVal == null) {
/* 3597 */       return null;
/*      */     }
/*      */ 
/* 3600 */     return getClobFromString(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   private String getNativeConvertToString(int columnIndex, Field field)
/*      */     throws SQLException
/*      */   {
/* 3608 */     int sqlType = field.getSQLType();
/* 3609 */     int mysqlType = field.getMysqlType();
/*      */ 
/* 3611 */     switch (sqlType) {
/*      */     case -7:
/* 3613 */       return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
/*      */     case 16:
/* 3615 */       boolean booleanVal = getBoolean(columnIndex);
/*      */ 
/* 3617 */       if (this.wasNullFlag) {
/* 3618 */         return null;
/*      */       }
/*      */ 
/* 3621 */       return String.valueOf(booleanVal);
/*      */     case -6:
/* 3624 */       byte tinyintVal = getNativeByte(columnIndex, false);
/*      */ 
/* 3626 */       if (this.wasNullFlag) {
/* 3627 */         return null;
/*      */       }
/*      */ 
/* 3630 */       if ((!field.isUnsigned()) || (tinyintVal >= 0)) {
/* 3631 */         return String.valueOf(tinyintVal);
/*      */       }
/*      */ 
/* 3634 */       short unsignedTinyVal = (short)(tinyintVal & 0xFF);
/*      */ 
/* 3636 */       return String.valueOf(unsignedTinyVal);
/*      */     case 5:
/* 3640 */       int intVal = getNativeInt(columnIndex, false);
/*      */ 
/* 3642 */       if (this.wasNullFlag) {
/* 3643 */         return null;
/*      */       }
/*      */ 
/* 3646 */       if ((!field.isUnsigned()) || (intVal >= 0)) {
/* 3647 */         return String.valueOf(intVal);
/*      */       }
/*      */ 
/* 3650 */       intVal &= 65535;
/*      */ 
/* 3652 */       return String.valueOf(intVal);
/*      */     case 4:
/* 3655 */       int intVal = getNativeInt(columnIndex, false);
/*      */ 
/* 3657 */       if (this.wasNullFlag) {
/* 3658 */         return null;
/*      */       }
/*      */ 
/* 3661 */       if ((!field.isUnsigned()) || (intVal >= 0) || (field.getMysqlType() == 9))
/*      */       {
/* 3664 */         return String.valueOf(intVal);
/*      */       }
/*      */ 
/* 3667 */       long longVal = intVal & 0xFFFFFFFF;
/*      */ 
/* 3669 */       return String.valueOf(longVal);
/*      */     case -5:
/* 3673 */       if (!field.isUnsigned()) {
/* 3674 */         long longVal = getNativeLong(columnIndex, false, true);
/*      */ 
/* 3676 */         if (this.wasNullFlag) {
/* 3677 */           return null;
/*      */         }
/*      */ 
/* 3680 */         return String.valueOf(longVal);
/*      */       }
/*      */ 
/* 3683 */       long longVal = getNativeLong(columnIndex, false, false);
/*      */ 
/* 3685 */       if (this.wasNullFlag) {
/* 3686 */         return null;
/*      */       }
/*      */ 
/* 3689 */       return String.valueOf(convertLongToUlong(longVal));
/*      */     case 7:
/* 3691 */       float floatVal = getNativeFloat(columnIndex);
/*      */ 
/* 3693 */       if (this.wasNullFlag) {
/* 3694 */         return null;
/*      */       }
/*      */ 
/* 3697 */       return String.valueOf(floatVal);
/*      */     case 6:
/*      */     case 8:
/* 3701 */       double doubleVal = getNativeDouble(columnIndex);
/*      */ 
/* 3703 */       if (this.wasNullFlag) {
/* 3704 */         return null;
/*      */       }
/*      */ 
/* 3707 */       return String.valueOf(doubleVal);
/*      */     case 2:
/*      */     case 3:
/* 3711 */       String stringVal = StringUtils.toAsciiString((byte[])this.thisRow.getColumnValue(columnIndex - 1));
/*      */ 
/* 3716 */       if (stringVal != null) {
/* 3717 */         this.wasNullFlag = false;
/*      */ 
/* 3719 */         if (stringVal.length() == 0) {
/* 3720 */           BigDecimal val = new BigDecimal(0.0D);
/*      */ 
/* 3722 */           return val.toString();
/*      */         }
/*      */         try
/*      */         {
/* 3726 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/* 3728 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Constants.integerValueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */         BigDecimal val;
/* 3735 */         return val.toString();
/*      */       }
/*      */ 
/* 3738 */       this.wasNullFlag = true;
/*      */ 
/* 3740 */       return null;
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/* 3746 */       return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 3751 */       if (!field.isBlob())
/* 3752 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/* 3753 */       if (!field.isBinary()) {
/* 3754 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */       }
/* 3756 */       byte[] data = getBytes(columnIndex);
/* 3757 */       Object obj = data;
/*      */ 
/* 3759 */       if ((data != null) && (data.length >= 2)) {
/* 3760 */         if ((data[0] == -84) && (data[1] == -19)) {
/*      */           try
/*      */           {
/* 3763 */             ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/*      */ 
/* 3765 */             ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/*      */ 
/* 3767 */             obj = objIn.readObject();
/* 3768 */             objIn.close();
/* 3769 */             bytesIn.close();
/*      */           } catch (ClassNotFoundException cnfe) {
/* 3771 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
/*      */           }
/*      */           catch (IOException ex)
/*      */           {
/* 3778 */             obj = data;
/*      */           }
/*      */         }
/*      */ 
/* 3782 */         return obj.toString();
/*      */       }
/*      */ 
/* 3785 */       return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */     case 91:
/* 3791 */       if (mysqlType == 13) {
/* 3792 */         short shortVal = getNativeShort(columnIndex);
/*      */ 
/* 3794 */         if (!this.connection.getYearIsDateType())
/*      */         {
/* 3796 */           if (this.wasNullFlag) {
/* 3797 */             return null;
/*      */           }
/*      */ 
/* 3800 */           return String.valueOf(shortVal);
/*      */         }
/*      */ 
/* 3803 */         if (field.getLength() == 2L)
/*      */         {
/* 3805 */           if (shortVal <= 69) {
/* 3806 */             shortVal = (short)(shortVal + 100);
/*      */           }
/*      */ 
/* 3809 */           shortVal = (short)(shortVal + 1900);
/*      */         }
/*      */ 
/* 3812 */         return fastDateCreate(null, shortVal, 1, 1).toString();
/*      */       }
/*      */ 
/* 3816 */       if (this.connection.getNoDatetimeStringSync()) {
/* 3817 */         byte[] asBytes = getNativeBytes(columnIndex, true);
/*      */ 
/* 3819 */         if (asBytes == null) {
/* 3820 */           return null;
/*      */         }
/*      */ 
/* 3823 */         if (asBytes.length == 0)
/*      */         {
/* 3825 */           return "0000-00-00";
/*      */         }
/*      */ 
/* 3828 */         int year = asBytes[0] & 0xFF | (asBytes[1] & 0xFF) << 8;
/*      */ 
/* 3830 */         int month = asBytes[2];
/* 3831 */         int day = asBytes[3];
/*      */ 
/* 3833 */         if ((year == 0) && (month == 0) && (day == 0)) {
/* 3834 */           return "0000-00-00";
/*      */         }
/*      */       }
/*      */ 
/* 3838 */       Date dt = getNativeDate(columnIndex);
/*      */ 
/* 3840 */       if (dt == null) {
/* 3841 */         return null;
/*      */       }
/*      */ 
/* 3844 */       return String.valueOf(dt);
/*      */     case 92:
/* 3847 */       Time tm = getNativeTime(columnIndex, null, this.defaultTimeZone, false);
/*      */ 
/* 3849 */       if (tm == null) {
/* 3850 */         return null;
/*      */       }
/*      */ 
/* 3853 */       return String.valueOf(tm);
/*      */     case 93:
/* 3856 */       if (this.connection.getNoDatetimeStringSync()) {
/* 3857 */         byte[] asBytes = getNativeBytes(columnIndex, true);
/*      */ 
/* 3859 */         if (asBytes == null) {
/* 3860 */           return null;
/*      */         }
/*      */ 
/* 3863 */         if (asBytes.length == 0)
/*      */         {
/* 3865 */           return "0000-00-00 00:00:00";
/*      */         }
/*      */ 
/* 3868 */         int year = asBytes[0] & 0xFF | (asBytes[1] & 0xFF) << 8;
/*      */ 
/* 3870 */         int month = asBytes[2];
/* 3871 */         int day = asBytes[3];
/*      */ 
/* 3873 */         if ((year == 0) && (month == 0) && (day == 0)) {
/* 3874 */           return "0000-00-00 00:00:00";
/*      */         }
/*      */       }
/*      */ 
/* 3878 */       Timestamp tstamp = getNativeTimestamp(columnIndex, null, this.defaultTimeZone, false);
/*      */ 
/* 3881 */       if (tstamp == null) {
/* 3882 */         return null;
/*      */       }
/*      */ 
/* 3885 */       String result = String.valueOf(tstamp);
/*      */ 
/* 3887 */       if (!this.connection.getNoDatetimeStringSync()) {
/* 3888 */         return result;
/*      */       }
/*      */ 
/* 3891 */       if (!result.endsWith(".0")) break;
/* 3892 */       return result.substring(0, result.length() - 2);
/*      */     }
/*      */ 
/* 3896 */     return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */   }
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3912 */     return getNativeDate(columnIndex, null);
/*      */   }
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 3933 */     checkRowPos();
/* 3934 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3936 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 3938 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */ 
/* 3940 */     Date dateToReturn = null;
/*      */ 
/* 3942 */     if (mysqlType == 10)
/*      */     {
/* 3944 */       dateToReturn = this.thisRow.getNativeDate(columnIndexMinusOne, this.connection, this, cal);
/*      */     }
/*      */     else {
/* 3947 */       TimeZone tz = cal != null ? cal.getTimeZone() : getDefaultTimeZone();
/*      */ 
/* 3950 */       boolean rollForward = (tz != null) && (!tz.equals(getDefaultTimeZone()));
/*      */ 
/* 3952 */       dateToReturn = (Date)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 91, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */ 
/* 3964 */     if (dateToReturn == null)
/*      */     {
/* 3966 */       this.wasNullFlag = true;
/*      */ 
/* 3968 */       return null;
/*      */     }
/*      */ 
/* 3971 */     this.wasNullFlag = false;
/*      */ 
/* 3973 */     return dateToReturn;
/*      */   }
/*      */ 
/*      */   Date getNativeDateViaParseConversion(int columnIndex) throws SQLException {
/* 3977 */     if (this.useUsageAdvisor) {
/* 3978 */       issueConversionViaParsingWarning("getDate()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 10 });
/*      */     }
/*      */ 
/* 3983 */     String stringVal = getNativeString(columnIndex);
/*      */ 
/* 3985 */     return getDateFromString(stringVal, columnIndex, null);
/*      */   }
/*      */ 
/*      */   protected double getNativeDouble(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4000 */     checkRowPos();
/* 4001 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4003 */     columnIndex--;
/*      */ 
/* 4005 */     if (this.thisRow.isNull(columnIndex)) {
/* 4006 */       this.wasNullFlag = true;
/*      */ 
/* 4008 */       return 0.0D;
/*      */     }
/*      */ 
/* 4011 */     this.wasNullFlag = false;
/*      */ 
/* 4013 */     Field f = this.fields[columnIndex];
/*      */ 
/* 4015 */     switch (f.getMysqlType()) {
/*      */     case 5:
/* 4017 */       return this.thisRow.getNativeDouble(columnIndex);
/*      */     case 1:
/* 4019 */       if (!f.isUnsigned()) {
/* 4020 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */ 
/* 4023 */       return getNativeShort(columnIndex + 1);
/*      */     case 2:
/*      */     case 13:
/* 4026 */       if (!f.isUnsigned()) {
/* 4027 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */ 
/* 4030 */       return getNativeInt(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 4033 */       if (!f.isUnsigned()) {
/* 4034 */         return getNativeInt(columnIndex + 1);
/*      */       }
/*      */ 
/* 4037 */       return getNativeLong(columnIndex + 1);
/*      */     case 8:
/* 4039 */       long valueAsLong = getNativeLong(columnIndex + 1);
/*      */ 
/* 4041 */       if (!f.isUnsigned()) {
/* 4042 */         return valueAsLong;
/*      */       }
/*      */ 
/* 4045 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/* 4049 */       return asBigInt.doubleValue();
/*      */     case 4:
/* 4051 */       return getNativeFloat(columnIndex + 1);
/*      */     case 16:
/* 4053 */       return getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 4055 */     case 15: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4057 */     if (this.useUsageAdvisor) {
/* 4058 */       issueConversionViaParsingWarning("getDouble()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4068 */     return getDoubleFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected float getNativeFloat(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4084 */     checkRowPos();
/* 4085 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4087 */     columnIndex--;
/*      */ 
/* 4089 */     if (this.thisRow.isNull(columnIndex)) {
/* 4090 */       this.wasNullFlag = true;
/*      */ 
/* 4092 */       return 0.0F;
/*      */     }
/*      */ 
/* 4095 */     this.wasNullFlag = false;
/*      */ 
/* 4097 */     Field f = this.fields[columnIndex];
/*      */ 
/* 4099 */     switch (f.getMysqlType()) {
/*      */     case 16:
/* 4101 */       long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */ 
/* 4103 */       return (float)valueAsLong;
/*      */     case 5:
/* 4110 */       Double valueAsDouble = new Double(getNativeDouble(columnIndex + 1));
/*      */ 
/* 4112 */       float valueAsFloat = valueAsDouble.floatValue();
/*      */ 
/* 4114 */       if (((this.jdbcCompliantTruncationForReads) && (valueAsFloat == (1.0F / -1.0F))) || (valueAsFloat == (1.0F / 1.0F)))
/*      */       {
/* 4117 */         throwRangeException(valueAsDouble.toString(), columnIndex + 1, 6);
/*      */       }
/*      */ 
/* 4121 */       return (float)getNativeDouble(columnIndex + 1);
/*      */     case 1:
/* 4123 */       if (!f.isUnsigned()) {
/* 4124 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */ 
/* 4127 */       return getNativeShort(columnIndex + 1);
/*      */     case 2:
/*      */     case 13:
/* 4130 */       if (!f.isUnsigned()) {
/* 4131 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */ 
/* 4134 */       return getNativeInt(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 4137 */       if (!f.isUnsigned()) {
/* 4138 */         return getNativeInt(columnIndex + 1);
/*      */       }
/*      */ 
/* 4141 */       return (float)getNativeLong(columnIndex + 1);
/*      */     case 8:
/* 4143 */       long valueAsLong = getNativeLong(columnIndex + 1);
/*      */ 
/* 4145 */       if (!f.isUnsigned()) {
/* 4146 */         return (float)valueAsLong;
/*      */       }
/*      */ 
/* 4149 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/* 4153 */       return asBigInt.floatValue();
/*      */     case 4:
/* 4156 */       return this.thisRow.getNativeFloat(columnIndex);
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 4159 */     case 15: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4161 */     if (this.useUsageAdvisor) {
/* 4162 */       issueConversionViaParsingWarning("getFloat()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4172 */     return getFloatFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected int getNativeInt(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4188 */     return getNativeInt(columnIndex, true);
/*      */   }
/*      */ 
/*      */   protected int getNativeInt(int columnIndex, boolean overflowCheck) throws SQLException {
/* 4192 */     checkRowPos();
/* 4193 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4195 */     columnIndex--;
/*      */ 
/* 4197 */     if (this.thisRow.isNull(columnIndex)) {
/* 4198 */       this.wasNullFlag = true;
/*      */ 
/* 4200 */       return 0;
/*      */     }
/*      */ 
/* 4203 */     this.wasNullFlag = false;
/*      */ 
/* 4205 */     Field f = this.fields[columnIndex];
/*      */ 
/* 4207 */     switch (f.getMysqlType()) {
/*      */     case 16:
/* 4209 */       long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */ 
/* 4211 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */       {
/* 4214 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4218 */       return (short)(int)valueAsLong;
/*      */     case 1:
/* 4220 */       byte tinyintVal = getNativeByte(columnIndex + 1, false);
/*      */ 
/* 4222 */       if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
/* 4223 */         return tinyintVal;
/*      */       }
/*      */ 
/* 4226 */       return tinyintVal + 256;
/*      */     case 2:
/*      */     case 13:
/* 4229 */       short asShort = getNativeShort(columnIndex + 1, false);
/*      */ 
/* 4231 */       if ((!f.isUnsigned()) || (asShort >= 0)) {
/* 4232 */         return asShort;
/*      */       }
/*      */ 
/* 4235 */       return asShort + 65536;
/*      */     case 3:
/*      */     case 9:
/* 4239 */       int valueAsInt = this.thisRow.getNativeInt(columnIndex);
/*      */ 
/* 4241 */       if (!f.isUnsigned()) {
/* 4242 */         return valueAsInt;
/*      */       }
/*      */ 
/* 4245 */       long valueAsLong = valueAsInt >= 0 ? valueAsInt : valueAsInt + 4294967296L;
/*      */ 
/* 4248 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsLong > 2147483647L))
/*      */       {
/* 4250 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4254 */       return (int)valueAsLong;
/*      */     case 8:
/* 4256 */       long valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */ 
/* 4258 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4259 */         (valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */       {
/* 4261 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4266 */       return (int)valueAsLong;
/*      */     case 5:
/* 4268 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 4270 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4271 */         (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */       {
/* 4273 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4278 */       return (int)valueAsDouble;
/*      */     case 4:
/* 4280 */       double valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */ 
/* 4282 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4283 */         (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */       {
/* 4285 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4290 */       return (int)valueAsDouble;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 4293 */     case 15: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4295 */     if (this.useUsageAdvisor) {
/* 4296 */       issueConversionViaParsingWarning("getInt()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4306 */     return getIntFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected long getNativeLong(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4322 */     return getNativeLong(columnIndex, true, true);
/*      */   }
/*      */ 
/*      */   protected long getNativeLong(int columnIndex, boolean overflowCheck, boolean expandUnsignedLong) throws SQLException
/*      */   {
/* 4327 */     checkRowPos();
/* 4328 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4330 */     columnIndex--;
/*      */ 
/* 4332 */     if (this.thisRow.isNull(columnIndex)) {
/* 4333 */       this.wasNullFlag = true;
/*      */ 
/* 4335 */       return 0L;
/*      */     }
/*      */ 
/* 4338 */     this.wasNullFlag = false;
/*      */ 
/* 4340 */     Field f = this.fields[columnIndex];
/*      */ 
/* 4342 */     switch (f.getMysqlType()) {
/*      */     case 16:
/* 4344 */       return getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */     case 1:
/* 4346 */       if (!f.isUnsigned()) {
/* 4347 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */ 
/* 4350 */       return getNativeInt(columnIndex + 1);
/*      */     case 2:
/* 4352 */       if (!f.isUnsigned()) {
/* 4353 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */ 
/* 4356 */       return getNativeInt(columnIndex + 1, false);
/*      */     case 13:
/* 4359 */       return getNativeShort(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 4362 */       int asInt = getNativeInt(columnIndex + 1, false);
/*      */ 
/* 4364 */       if ((!f.isUnsigned()) || (asInt >= 0)) {
/* 4365 */         return asInt;
/*      */       }
/*      */ 
/* 4368 */       return asInt + 4294967296L;
/*      */     case 8:
/* 4370 */       long valueAsLong = this.thisRow.getNativeLong(columnIndex);
/*      */ 
/* 4372 */       if ((!f.isUnsigned()) || (!expandUnsignedLong)) {
/* 4373 */         return valueAsLong;
/*      */       }
/*      */ 
/* 4376 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/* 4378 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((asBigInt.compareTo(new BigInteger(String.valueOf(9223372036854775807L))) > 0) || (asBigInt.compareTo(new BigInteger(String.valueOf(-9223372036854775808L))) < 0)))
/*      */       {
/* 4381 */         throwRangeException(asBigInt.toString(), columnIndex + 1, -5);
/*      */       }
/*      */ 
/* 4385 */       return getLongFromString(asBigInt.toString(), columnIndex);
/*      */     case 5:
/* 4388 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 4390 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4391 */         (valueAsDouble < -9.223372036854776E+18D) || (valueAsDouble > 9.223372036854776E+18D)))
/*      */       {
/* 4393 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */       }
/*      */ 
/* 4398 */       return ()valueAsDouble;
/*      */     case 4:
/* 4400 */       double valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */ 
/* 4402 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4403 */         (valueAsDouble < -9.223372036854776E+18D) || (valueAsDouble > 9.223372036854776E+18D)))
/*      */       {
/* 4405 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */       }
/*      */ 
/* 4410 */       return ()valueAsDouble;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 4412 */     case 15: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4414 */     if (this.useUsageAdvisor) {
/* 4415 */       issueConversionViaParsingWarning("getLong()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4425 */     return getLongFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected Ref getNativeRef(int i)
/*      */     throws SQLException
/*      */   {
/* 4443 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   protected short getNativeShort(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4458 */     return getNativeShort(columnIndex, true);
/*      */   }
/*      */ 
/*      */   protected short getNativeShort(int columnIndex, boolean overflowCheck) throws SQLException {
/* 4462 */     checkRowPos();
/* 4463 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4465 */     columnIndex--;
/*      */ 
/* 4468 */     if (this.thisRow.isNull(columnIndex)) {
/* 4469 */       this.wasNullFlag = true;
/*      */ 
/* 4471 */       return 0;
/*      */     }
/*      */ 
/* 4474 */     this.wasNullFlag = false;
/*      */ 
/* 4476 */     Field f = this.fields[columnIndex];
/*      */ 
/* 4478 */     switch (f.getMysqlType())
/*      */     {
/*      */     case 1:
/* 4481 */       byte tinyintVal = getNativeByte(columnIndex + 1, false);
/*      */ 
/* 4483 */       if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
/* 4484 */         return (short)tinyintVal;
/*      */       }
/*      */ 
/* 4487 */       return (short)(tinyintVal + 256);
/*      */     case 2:
/*      */     case 13:
/* 4491 */       short asShort = this.thisRow.getNativeShort(columnIndex);
/*      */ 
/* 4493 */       if (!f.isUnsigned()) {
/* 4494 */         return asShort;
/*      */       }
/*      */ 
/* 4497 */       int valueAsInt = asShort & 0xFFFF;
/*      */ 
/* 4499 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsInt > 32767))
/*      */       {
/* 4501 */         throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4505 */       return (short)valueAsInt;
/*      */     case 3:
/*      */     case 9:
/* 4508 */       if (!f.isUnsigned()) {
/* 4509 */         int valueAsInt = getNativeInt(columnIndex + 1, false);
/*      */ 
/* 4511 */         if (((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsInt > 32767)) || (valueAsInt < -32768))
/*      */         {
/* 4514 */           throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
/*      */         }
/*      */ 
/* 4518 */         return (short)valueAsInt;
/*      */       }
/*      */ 
/* 4521 */       long valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */ 
/* 4523 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsLong > 32767L))
/*      */       {
/* 4525 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4529 */       return (short)(int)valueAsLong;
/*      */     case 8:
/* 4532 */       long valueAsLong = getNativeLong(columnIndex + 1, false, false);
/*      */ 
/* 4534 */       if (!f.isUnsigned()) {
/* 4535 */         if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4536 */           (valueAsLong < -32768L) || (valueAsLong > 32767L)))
/*      */         {
/* 4538 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
/*      */         }
/*      */ 
/* 4543 */         return (short)(int)valueAsLong;
/*      */       }
/*      */ 
/* 4546 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/* 4548 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((asBigInt.compareTo(new BigInteger(String.valueOf(32767))) > 0) || (asBigInt.compareTo(new BigInteger(String.valueOf(-32768))) < 0)))
/*      */       {
/* 4551 */         throwRangeException(asBigInt.toString(), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4555 */       return (short)getIntFromString(asBigInt.toString(), columnIndex + 1);
/*      */     case 5:
/* 4558 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 4560 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4561 */         (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D)))
/*      */       {
/* 4563 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4568 */       return (short)(int)valueAsDouble;
/*      */     case 4:
/* 4570 */       float valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */ 
/* 4572 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4573 */         (valueAsFloat < -32768.0F) || (valueAsFloat > 32767.0F)))
/*      */       {
/* 4575 */         throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4580 */       return (short)(int)valueAsFloat;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/* 4582 */     case 12: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4584 */     if (this.useUsageAdvisor) {
/* 4585 */       issueConversionViaParsingWarning("getShort()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4595 */     return getShortFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected String getNativeString(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4611 */     checkRowPos();
/* 4612 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4614 */     if (this.fields == null) {
/* 4615 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_133"), "S1002", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4621 */     if (this.thisRow.isNull(columnIndex - 1)) {
/* 4622 */       this.wasNullFlag = true;
/*      */ 
/* 4624 */       return null;
/*      */     }
/*      */ 
/* 4627 */     this.wasNullFlag = false;
/*      */ 
/* 4629 */     String stringVal = null;
/*      */ 
/* 4631 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 4634 */     stringVal = getNativeConvertToString(columnIndex, field);
/* 4635 */     int mysqlType = field.getMysqlType();
/*      */ 
/* 4637 */     if ((mysqlType != 7) && (mysqlType != 10) && (field.isZeroFill()) && (stringVal != null))
/*      */     {
/* 4640 */       int origLength = stringVal.length();
/*      */ 
/* 4642 */       StringBuffer zeroFillBuf = new StringBuffer(origLength);
/*      */ 
/* 4644 */       long numZeros = field.getLength() - origLength;
/*      */ 
/* 4646 */       for (long i = 0L; i < numZeros; i += 1L) {
/* 4647 */         zeroFillBuf.append('0');
/*      */       }
/*      */ 
/* 4650 */       zeroFillBuf.append(stringVal);
/*      */ 
/* 4652 */       stringVal = zeroFillBuf.toString();
/*      */     }
/*      */ 
/* 4655 */     return stringVal;
/*      */   }
/*      */ 
/*      */   private Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4661 */     checkRowPos();
/* 4662 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4664 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 4666 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */ 
/* 4668 */     Time timeVal = null;
/*      */ 
/* 4670 */     if (mysqlType == 11) {
/* 4671 */       timeVal = this.thisRow.getNativeTime(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */     }
/*      */     else
/*      */     {
/* 4675 */       timeVal = (Time)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 92, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */ 
/* 4687 */     if (timeVal == null)
/*      */     {
/* 4689 */       this.wasNullFlag = true;
/*      */ 
/* 4691 */       return null;
/*      */     }
/*      */ 
/* 4694 */     this.wasNullFlag = false;
/*      */ 
/* 4696 */     return timeVal;
/*      */   }
/*      */ 
/*      */   Time getNativeTimeViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/* 4701 */     if (this.useUsageAdvisor) {
/* 4702 */       issueConversionViaParsingWarning("getTime()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 11 });
/*      */     }
/*      */ 
/* 4707 */     String strTime = getNativeString(columnIndex);
/*      */ 
/* 4709 */     return getTimeFromString(strTime, targetCalendar, columnIndex, tz, rollForward);
/*      */   }
/*      */ 
/*      */   private Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4716 */     checkRowPos();
/* 4717 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4719 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 4721 */     Timestamp tsVal = null;
/*      */ 
/* 4723 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */ 
/* 4725 */     switch (mysqlType) {
/*      */     case 7:
/*      */     case 12:
/* 4728 */       tsVal = this.thisRow.getNativeTimestamp(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */ 
/* 4730 */       break;
/*      */     default:
/* 4735 */       tsVal = (Timestamp)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 93, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */ 
/* 4747 */     if (tsVal == null)
/*      */     {
/* 4749 */       this.wasNullFlag = true;
/*      */ 
/* 4751 */       return null;
/*      */     }
/*      */ 
/* 4754 */     this.wasNullFlag = false;
/*      */ 
/* 4756 */     return tsVal;
/*      */   }
/*      */ 
/*      */   Timestamp getNativeTimestampViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/* 4761 */     if (this.useUsageAdvisor) {
/* 4762 */       issueConversionViaParsingWarning("getTimestamp()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 7, 12 });
/*      */     }
/*      */ 
/* 4768 */     String strTimestamp = getNativeString(columnIndex);
/*      */ 
/* 4770 */     return getTimestampFromString(columnIndex, targetCalendar, strTimestamp, tz, rollForward);
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeUnicodeStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4797 */     checkRowPos();
/*      */ 
/* 4799 */     return getBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   protected URL getNativeURL(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 4806 */     String val = getString(colIndex);
/*      */ 
/* 4808 */     if (val == null) {
/* 4809 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 4813 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 4815 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____141") + val + "'", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public ResultSetInternalMethods getNextResultSet()
/*      */   {
/* 4827 */     return this.nextResultSet;
/*      */   }
/*      */ 
/*      */   public Object getObject(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4854 */     checkRowPos();
/* 4855 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4857 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 4859 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 4860 */       this.wasNullFlag = true;
/*      */ 
/* 4862 */       return null;
/*      */     }
/*      */ 
/* 4865 */     this.wasNullFlag = false;
/*      */ 
/* 4868 */     Field field = this.fields[columnIndexMinusOne];
/*      */ 
/* 4870 */     switch (field.getSQLType()) {
/*      */     case -7:
/*      */     case 16:
/* 4873 */       if ((field.getMysqlType() == 16) && (!field.isSingleBit()))
/*      */       {
/* 4875 */         return getBytes(columnIndex);
/*      */       }
/*      */ 
/* 4881 */       return Boolean.valueOf(getBoolean(columnIndex));
/*      */     case -6:
/* 4884 */       if (!field.isUnsigned()) {
/* 4885 */         return Constants.integerValueOf(getByte(columnIndex));
/*      */       }
/*      */ 
/* 4888 */       return Constants.integerValueOf(getInt(columnIndex));
/*      */     case 5:
/* 4892 */       return Constants.integerValueOf(getInt(columnIndex));
/*      */     case 4:
/* 4896 */       if ((!field.isUnsigned()) || (field.getMysqlType() == 9))
/*      */       {
/* 4898 */         return Constants.integerValueOf(getInt(columnIndex));
/*      */       }
/*      */ 
/* 4901 */       return Constants.longValueOf(getLong(columnIndex));
/*      */     case -5:
/* 4905 */       if (!field.isUnsigned()) {
/* 4906 */         return Constants.longValueOf(getLong(columnIndex));
/*      */       }
/*      */ 
/* 4909 */       String stringVal = getString(columnIndex);
/*      */ 
/* 4911 */       if (stringVal == null) {
/* 4912 */         return null;
/*      */       }
/*      */       try
/*      */       {
/* 4916 */         return new BigInteger(stringVal);
/*      */       } catch (NumberFormatException nfe) {
/* 4918 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[] { Constants.integerValueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     case 2:
/*      */     case 3:
/* 4926 */       String stringVal = getString(columnIndex);
/*      */ 
/* 4930 */       if (stringVal != null) {
/* 4931 */         if (stringVal.length() == 0) {
/* 4932 */           BigDecimal val = new BigDecimal(0.0D);
/*      */ 
/* 4934 */           return val;
/*      */         }
/*      */         try
/*      */         {
/* 4938 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/* 4940 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, new Integer(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */         BigDecimal val;
/* 4947 */         return val;
/*      */       }
/*      */ 
/* 4950 */       return null;
/*      */     case 7:
/* 4953 */       return new Float(getFloat(columnIndex));
/*      */     case 6:
/*      */     case 8:
/* 4957 */       return new Double(getDouble(columnIndex));
/*      */     case 1:
/*      */     case 12:
/* 4961 */       if (!field.isOpaqueBinary()) {
/* 4962 */         return getString(columnIndex);
/*      */       }
/*      */ 
/* 4965 */       return getBytes(columnIndex);
/*      */     case -1:
/* 4967 */       if (!field.isOpaqueBinary()) {
/* 4968 */         return getStringForClob(columnIndex);
/*      */       }
/*      */ 
/* 4971 */       return getBytes(columnIndex);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 4976 */       if (field.getMysqlType() == 255)
/* 4977 */         return getBytes(columnIndex);
/* 4978 */       if ((field.isBinary()) || (field.isBlob())) {
/* 4979 */         byte[] data = getBytes(columnIndex);
/*      */ 
/* 4981 */         if (this.connection.getAutoDeserialize()) {
/* 4982 */           Object obj = data;
/*      */ 
/* 4984 */           if ((data != null) && (data.length >= 2)) {
/* 4985 */             if ((data[0] == -84) && (data[1] == -19))
/*      */               try
/*      */               {
/* 4988 */                 ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/*      */ 
/* 4990 */                 ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/*      */ 
/* 4992 */                 obj = objIn.readObject();
/* 4993 */                 objIn.close();
/* 4994 */                 bytesIn.close();
/*      */               } catch (ClassNotFoundException cnfe) {
/* 4996 */                 throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
/*      */               }
/*      */               catch (IOException ex)
/*      */               {
/* 5003 */                 obj = data;
/*      */               }
/*      */             else {
/* 5006 */               return getString(columnIndex);
/*      */             }
/*      */           }
/*      */ 
/* 5010 */           return obj;
/*      */         }
/*      */ 
/* 5013 */         return data;
/*      */       }
/*      */ 
/* 5016 */       return getBytes(columnIndex);
/*      */     case 91:
/* 5019 */       if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType()))
/*      */       {
/* 5021 */         return Constants.shortValueOf(getShort(columnIndex));
/*      */       }
/*      */ 
/* 5024 */       return getDate(columnIndex);
/*      */     case 92:
/* 5027 */       return getTime(columnIndex);
/*      */     case 93:
/* 5030 */       return getTimestamp(columnIndex);
/*      */     }
/*      */ 
/* 5033 */     return getString(columnIndex);
/*      */   }
/*      */ 
/*      */   public Object getObject(int i, Map map)
/*      */     throws SQLException
/*      */   {
/* 5053 */     return getObject(i);
/*      */   }
/*      */ 
/*      */   public Object getObject(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5080 */     return getObject(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Object getObject(String colName, Map map)
/*      */     throws SQLException
/*      */   {
/* 5100 */     return getObject(findColumn(colName), map);
/*      */   }
/*      */ 
/*      */   public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException
/*      */   {
/* 5105 */     checkRowPos();
/* 5106 */     checkColumnBounds(columnIndex);
/*      */ 
/* 5108 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 5110 */     if (value == null) {
/* 5111 */       this.wasNullFlag = true;
/*      */ 
/* 5113 */       return null;
/*      */     }
/*      */ 
/* 5116 */     this.wasNullFlag = false;
/*      */ 
/* 5119 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 5121 */     switch (desiredSqlType)
/*      */     {
/*      */     case -7:
/*      */     case 16:
/* 5127 */       return Boolean.valueOf(getBoolean(columnIndex));
/*      */     case -6:
/* 5130 */       return Constants.integerValueOf(getInt(columnIndex));
/*      */     case 5:
/* 5133 */       return Constants.integerValueOf(getInt(columnIndex));
/*      */     case 4:
/* 5137 */       if ((!field.isUnsigned()) || (field.getMysqlType() == 9))
/*      */       {
/* 5139 */         return Constants.integerValueOf(getInt(columnIndex));
/*      */       }
/*      */ 
/* 5142 */       return Constants.longValueOf(getLong(columnIndex));
/*      */     case -5:
/* 5146 */       if (field.isUnsigned()) {
/* 5147 */         return getBigDecimal(columnIndex);
/*      */       }
/*      */ 
/* 5150 */       return Constants.longValueOf(getLong(columnIndex));
/*      */     case 2:
/*      */     case 3:
/* 5155 */       String stringVal = getString(columnIndex);
/*      */ 
/* 5158 */       if (stringVal != null) {
/* 5159 */         if (stringVal.length() == 0) {
/* 5160 */           BigDecimal val = new BigDecimal(0.0D);
/*      */ 
/* 5162 */           return val;
/*      */         }
/*      */         try
/*      */         {
/* 5166 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/* 5168 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, new Integer(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */         BigDecimal val;
/* 5175 */         return val;
/*      */       }
/*      */ 
/* 5178 */       return null;
/*      */     case 7:
/* 5181 */       return new Float(getFloat(columnIndex));
/*      */     case 6:
/* 5185 */       if (!this.connection.getRunningCTS13()) {
/* 5186 */         return new Double(getFloat(columnIndex));
/*      */       }
/* 5188 */       return new Float(getFloat(columnIndex));
/*      */     case 8:
/* 5195 */       return new Double(getDouble(columnIndex));
/*      */     case 1:
/*      */     case 12:
/* 5199 */       return getString(columnIndex);
/*      */     case -1:
/* 5201 */       return getStringForClob(columnIndex);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 5205 */       return getBytes(columnIndex);
/*      */     case 91:
/* 5208 */       if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType()))
/*      */       {
/* 5210 */         return Constants.shortValueOf(getShort(columnIndex));
/*      */       }
/*      */ 
/* 5213 */       return getDate(columnIndex);
/*      */     case 92:
/* 5216 */       return getTime(columnIndex);
/*      */     case 93:
/* 5219 */       return getTimestamp(columnIndex);
/*      */     }
/*      */ 
/* 5222 */     return getString(columnIndex);
/*      */   }
/*      */ 
/*      */   public Object getObjectStoredProc(int i, Map map, int desiredSqlType)
/*      */     throws SQLException
/*      */   {
/* 5228 */     return getObjectStoredProc(i, desiredSqlType);
/*      */   }
/*      */ 
/*      */   public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException
/*      */   {
/* 5233 */     return getObjectStoredProc(findColumn(columnName), desiredSqlType);
/*      */   }
/*      */ 
/*      */   public Object getObjectStoredProc(String colName, Map map, int desiredSqlType) throws SQLException
/*      */   {
/* 5238 */     return getObjectStoredProc(findColumn(colName), map, desiredSqlType);
/*      */   }
/*      */ 
/*      */   public Ref getRef(int i)
/*      */     throws SQLException
/*      */   {
/* 5255 */     checkColumnBounds(i);
/* 5256 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public Ref getRef(String colName)
/*      */     throws SQLException
/*      */   {
/* 5273 */     return getRef(findColumn(colName));
/*      */   }
/*      */ 
/*      */   public int getRow()
/*      */     throws SQLException
/*      */   {
/* 5290 */     checkClosed();
/*      */ 
/* 5292 */     int currentRowNumber = this.rowData.getCurrentRowNumber();
/* 5293 */     int row = 0;
/*      */ 
/* 5297 */     if (!this.rowData.isDynamic()) {
/* 5298 */       if ((currentRowNumber < 0) || (this.rowData.isAfterLast()) || (this.rowData.isEmpty()))
/*      */       {
/* 5300 */         row = 0;
/*      */       }
/* 5302 */       else row = currentRowNumber + 1;
/*      */     }
/*      */     else
/*      */     {
/* 5306 */       row = currentRowNumber + 1;
/*      */     }
/*      */ 
/* 5309 */     return row;
/*      */   }
/*      */ 
/*      */   public String getServerInfo()
/*      */   {
/* 5318 */     return this.serverInfo;
/*      */   }
/*      */ 
/*      */   private long getNumericRepresentationOfSQLBitType(int columnIndex) throws SQLException
/*      */   {
/* 5323 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 5325 */     if ((this.fields[(columnIndex - 1)].isSingleBit()) || (((byte[])value).length == 1))
/*      */     {
/* 5327 */       return ((byte[])value)[0];
/*      */     }
/*      */ 
/* 5331 */     byte[] asBytes = (byte[])value;
/*      */ 
/* 5334 */     int shift = 0;
/*      */ 
/* 5336 */     long[] steps = new long[asBytes.length];
/*      */ 
/* 5338 */     for (int i = asBytes.length - 1; i >= 0; i--) {
/* 5339 */       steps[i] = ((asBytes[i] & 0xFF) << shift);
/* 5340 */       shift += 8;
/*      */     }
/*      */ 
/* 5343 */     long valueAsLong = 0L;
/*      */ 
/* 5345 */     for (int i = 0; i < asBytes.length; i++) {
/* 5346 */       valueAsLong |= steps[i];
/*      */     }
/*      */ 
/* 5349 */     return valueAsLong;
/*      */   }
/*      */ 
/*      */   public short getShort(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5364 */     if (!this.isBinaryEncoded) {
/* 5365 */       checkRowPos();
/*      */ 
/* 5367 */       if (this.useFastIntParsing)
/*      */       {
/* 5369 */         checkColumnBounds(columnIndex);
/*      */ 
/* 5371 */         Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 5373 */         if (value == null)
/* 5374 */           this.wasNullFlag = true;
/*      */         else {
/* 5376 */           this.wasNullFlag = false;
/*      */         }
/*      */ 
/* 5379 */         if (this.wasNullFlag) {
/* 5380 */           return 0;
/*      */         }
/*      */ 
/* 5383 */         byte[] shortAsBytes = (byte[])value;
/*      */ 
/* 5385 */         if (shortAsBytes.length == 0) {
/* 5386 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 5389 */         boolean needsFullParse = false;
/*      */ 
/* 5391 */         for (int i = 0; i < shortAsBytes.length; i++) {
/* 5392 */           if (((char)shortAsBytes[i] != 'e') && ((char)shortAsBytes[i] != 'E'))
/*      */             continue;
/* 5394 */           needsFullParse = true;
/*      */ 
/* 5396 */           break;
/*      */         }
/*      */ 
/* 5400 */         if (!needsFullParse) {
/*      */           try {
/* 5402 */             return parseShortWithOverflowCheck(columnIndex, shortAsBytes, null);
/*      */           }
/*      */           catch (NumberFormatException nfe)
/*      */           {
/*      */             try {
/* 5407 */               return parseShortAsDouble(columnIndex, new String(shortAsBytes));
/*      */             }
/*      */             catch (NumberFormatException valueAsLong)
/*      */             {
/* 5413 */               if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 5414 */                 long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 5416 */                 if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -32768L) || (valueAsLong > 32767L)))
/*      */                 {
/* 5419 */                   throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
/*      */                 }
/*      */ 
/* 5423 */                 return (short)(int)valueAsLong;
/*      */               }
/*      */ 
/* 5426 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + new String(shortAsBytes) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5436 */       String val = null;
/*      */       try
/*      */       {
/* 5439 */         val = getString(columnIndex);
/*      */ 
/* 5441 */         if (val != null)
/*      */         {
/* 5443 */           if (val.length() == 0) {
/* 5444 */             return (short)convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 5447 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */           {
/* 5449 */             return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */           }
/*      */ 
/* 5454 */           return parseShortAsDouble(columnIndex, val);
/*      */         }
/*      */ 
/* 5457 */         return 0;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 5460 */           return parseShortAsDouble(columnIndex, val);
/*      */         }
/*      */         catch (NumberFormatException valueAsLong)
/*      */         {
/* 5465 */           if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 5466 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 5468 */             if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -32768L) || (valueAsLong > 32767L)))
/*      */             {
/* 5471 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
/*      */             }
/*      */ 
/* 5475 */             return (short)(int)valueAsLong;
/*      */           }
/*      */ 
/* 5478 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5486 */     return getNativeShort(columnIndex);
/*      */   }
/*      */ 
/*      */   public short getShort(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5501 */     return getShort(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final short getShortFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 5507 */       if (val != null)
/*      */       {
/* 5509 */         if (val.length() == 0) {
/* 5510 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 5513 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */         {
/* 5515 */           return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */         }
/*      */ 
/* 5519 */         return parseShortAsDouble(columnIndex, val);
/*      */       }
/*      */ 
/* 5522 */       return 0;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 5525 */         return parseShortAsDouble(columnIndex, val);
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 5530 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____217") + val + Messages.getString("ResultSet.___in_column__218") + columnIndex, "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public Statement getStatement()
/*      */     throws SQLException
/*      */   {
/* 5549 */     if ((this.isClosed) && (!this.retainOwningStatement)) {
/* 5550 */       throw SQLError.createSQLException("Operation not allowed on closed ResultSet. Statements can be retained over result set closure by setting the connection property \"retainStatementAfterResultSetClose\" to \"true\".", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 5558 */     if (this.wrapperStatement != null) {
/* 5559 */       return this.wrapperStatement;
/*      */     }
/*      */ 
/* 5562 */     return this.owningStatement;
/*      */   }
/*      */ 
/*      */   public String getString(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5577 */     String stringVal = getStringInternal(columnIndex, true);
/*      */ 
/* 5579 */     if ((this.padCharsWithSpace) && (stringVal != null)) {
/* 5580 */       Field f = this.fields[(columnIndex - 1)];
/*      */ 
/* 5582 */       if (f.getMysqlType() == 254) {
/* 5583 */         int fieldLength = (int)f.getLength() / f.getMaxBytesPerCharacter();
/*      */ 
/* 5586 */         int currentLength = stringVal.length();
/*      */ 
/* 5588 */         if (currentLength < fieldLength) {
/* 5589 */           StringBuffer paddedBuf = new StringBuffer(fieldLength);
/* 5590 */           paddedBuf.append(stringVal);
/*      */ 
/* 5592 */           int difference = fieldLength - currentLength;
/*      */ 
/* 5594 */           paddedBuf.append(EMPTY_SPACE, 0, difference);
/*      */ 
/* 5596 */           stringVal = paddedBuf.toString();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 5601 */     return stringVal;
/*      */   }
/*      */ 
/*      */   public String getString(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5617 */     return getString(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private String getStringForClob(int columnIndex) throws SQLException {
/* 5621 */     String asString = null;
/*      */ 
/* 5623 */     String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/* 5626 */     if (forcedEncoding == null) {
/* 5627 */       if (!this.isBinaryEncoded)
/* 5628 */         asString = getString(columnIndex);
/*      */       else
/* 5630 */         asString = getNativeString(columnIndex);
/*      */     }
/*      */     else {
/*      */       try {
/* 5634 */         byte[] asBytes = null;
/*      */ 
/* 5636 */         if (!this.isBinaryEncoded)
/* 5637 */           asBytes = getBytes(columnIndex);
/*      */         else {
/* 5639 */           asBytes = getNativeBytes(columnIndex, true);
/*      */         }
/*      */ 
/* 5642 */         if (asBytes != null)
/* 5643 */           asString = new String(asBytes, forcedEncoding);
/*      */       }
/*      */       catch (UnsupportedEncodingException uee) {
/* 5646 */         throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5651 */     return asString;
/*      */   }
/*      */ 
/*      */   protected String getStringInternal(int columnIndex, boolean checkDateTypes) throws SQLException
/*      */   {
/* 5656 */     if (!this.isBinaryEncoded) {
/* 5657 */       checkRowPos();
/* 5658 */       checkColumnBounds(columnIndex);
/*      */ 
/* 5660 */       if (this.fields == null) {
/* 5661 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_99"), "S1002", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 5669 */       int internalColumnIndex = columnIndex - 1;
/*      */ 
/* 5671 */       if (this.thisRow.isNull(internalColumnIndex)) {
/* 5672 */         this.wasNullFlag = true;
/*      */ 
/* 5674 */         return null;
/*      */       }
/*      */ 
/* 5677 */       this.wasNullFlag = false;
/*      */ 
/* 5680 */       Field metadata = this.fields[internalColumnIndex];
/*      */ 
/* 5682 */       String stringVal = null;
/*      */ 
/* 5684 */       if (metadata.getMysqlType() == 16) {
/* 5685 */         if (metadata.isSingleBit()) {
/* 5686 */           byte[] value = this.thisRow.getColumnValue(internalColumnIndex);
/*      */ 
/* 5688 */           if (value.length == 0) {
/* 5689 */             return String.valueOf(convertToZeroWithEmptyCheck());
/*      */           }
/*      */ 
/* 5692 */           return String.valueOf(value[0]);
/*      */         }
/*      */ 
/* 5695 */         return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
/*      */       }
/*      */ 
/* 5698 */       String encoding = metadata.getCharacterSet();
/*      */ 
/* 5700 */       stringVal = this.thisRow.getString(internalColumnIndex, encoding, this.connection);
/*      */ 
/* 5707 */       if (metadata.getMysqlType() == 13) {
/* 5708 */         if (!this.connection.getYearIsDateType()) {
/* 5709 */           return stringVal;
/*      */         }
/*      */ 
/* 5712 */         Date dt = getDateFromString(stringVal, columnIndex, null);
/*      */ 
/* 5714 */         if (dt == null) {
/* 5715 */           this.wasNullFlag = true;
/*      */ 
/* 5717 */           return null;
/*      */         }
/*      */ 
/* 5720 */         this.wasNullFlag = false;
/*      */ 
/* 5722 */         return dt.toString();
/*      */       }
/*      */ 
/* 5727 */       if ((checkDateTypes) && (!this.connection.getNoDatetimeStringSync())) {
/* 5728 */         switch (metadata.getSQLType()) {
/*      */         case 92:
/* 5730 */           Time tm = getTimeFromString(stringVal, null, columnIndex, getDefaultTimeZone(), false);
/*      */ 
/* 5733 */           if (tm == null) {
/* 5734 */             this.wasNullFlag = true;
/*      */ 
/* 5736 */             return null;
/*      */           }
/*      */ 
/* 5739 */           this.wasNullFlag = false;
/*      */ 
/* 5741 */           return tm.toString();
/*      */         case 91:
/* 5744 */           Date dt = getDateFromString(stringVal, columnIndex, null);
/*      */ 
/* 5746 */           if (dt == null) {
/* 5747 */             this.wasNullFlag = true;
/*      */ 
/* 5749 */             return null;
/*      */           }
/*      */ 
/* 5752 */           this.wasNullFlag = false;
/*      */ 
/* 5754 */           return dt.toString();
/*      */         case 93:
/* 5756 */           Timestamp ts = getTimestampFromString(columnIndex, null, stringVal, getDefaultTimeZone(), false);
/*      */ 
/* 5759 */           if (ts == null) {
/* 5760 */             this.wasNullFlag = true;
/*      */ 
/* 5762 */             return null;
/*      */           }
/*      */ 
/* 5765 */           this.wasNullFlag = false;
/*      */ 
/* 5767 */           return ts.toString();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5773 */       return stringVal;
/*      */     }
/*      */ 
/* 5776 */     return getNativeString(columnIndex);
/*      */   }
/*      */ 
/*      */   public Time getTime(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5791 */     return getTimeInternal(columnIndex, null, getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   public Time getTime(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5811 */     return getTimeInternal(columnIndex, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public Time getTime(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5826 */     return getTime(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Time getTime(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5846 */     return getTime(findColumn(columnName), cal);
/*      */   }
/*      */   private Time getTimeFromString(String timeAsString, Calendar targetCalendar, int columnIndex, TimeZone tz, boolean rollForward) throws SQLException {
/* 5853 */     int hr = 0;
/* 5854 */     int min = 0;
/* 5855 */     int sec = 0;
/*      */     SQLException sqlEx;
/*      */     try {
/* 5859 */       if (timeAsString == null) {
/* 5860 */         this.wasNullFlag = true;
/*      */ 
/* 5862 */         return null;
/*      */       }
/*      */ 
/* 5873 */       timeAsString = timeAsString.trim();
/*      */ 
/* 5875 */       if ((timeAsString.equals("0")) || (timeAsString.equals("0000-00-00")) || (timeAsString.equals("0000-00-00 00:00:00")) || (timeAsString.equals("00000000000000")))
/*      */       {
/* 5879 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 5881 */           this.wasNullFlag = true;
/*      */ 
/* 5883 */           return null;
/* 5884 */         }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 5886 */           throw SQLError.createSQLException("Value '" + timeAsString + "' can not be represented as java.sql.Time", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 5893 */         return fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */       }
/*      */ 
/* 5896 */       this.wasNullFlag = false;
/*      */ 
/* 5898 */       Field timeColField = this.fields[(columnIndex - 1)];
/*      */ 
/* 5900 */       if (timeColField.getMysqlType() == 7)
/*      */       {
/* 5902 */         int length = timeAsString.length();
/*      */ 
/* 5904 */         switch (length)
/*      */         {
/*      */         case 19:
/* 5907 */           hr = Integer.parseInt(timeAsString.substring(length - 8, length - 6));
/*      */ 
/* 5909 */           min = Integer.parseInt(timeAsString.substring(length - 5, length - 3));
/*      */ 
/* 5911 */           sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */ 
/* 5915 */           break;
/*      */         case 12:
/*      */         case 14:
/* 5918 */           hr = Integer.parseInt(timeAsString.substring(length - 6, length - 4));
/*      */ 
/* 5920 */           min = Integer.parseInt(timeAsString.substring(length - 4, length - 2));
/*      */ 
/* 5922 */           sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */ 
/* 5926 */           break;
/*      */         case 10:
/* 5929 */           hr = Integer.parseInt(timeAsString.substring(6, 8));
/* 5930 */           min = Integer.parseInt(timeAsString.substring(8, 10));
/* 5931 */           sec = 0;
/*      */ 
/* 5934 */           break;
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         default:
/* 5937 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 5946 */         SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
/*      */ 
/* 5953 */         if (this.warningChain == null)
/* 5954 */           this.warningChain = precisionLost;
/*      */         else
/* 5956 */           this.warningChain.setNextWarning(precisionLost);
/*      */       }
/* 5958 */       else if (timeColField.getMysqlType() == 12) {
/* 5959 */         hr = Integer.parseInt(timeAsString.substring(11, 13));
/* 5960 */         min = Integer.parseInt(timeAsString.substring(14, 16));
/* 5961 */         sec = Integer.parseInt(timeAsString.substring(17, 19));
/*      */ 
/* 5963 */         SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
/*      */ 
/* 5970 */         if (this.warningChain == null)
/* 5971 */           this.warningChain = precisionLost;
/*      */         else
/* 5973 */           this.warningChain.setNextWarning(precisionLost);
/*      */       } else {
/* 5975 */         if (timeColField.getMysqlType() == 10) {
/* 5976 */           return fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */         }
/*      */ 
/* 5980 */         if ((timeAsString.length() != 5) && (timeAsString.length() != 8))
/*      */         {
/* 5982 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + timeAsString + Messages.getString("ResultSet.___in_column__268") + columnIndex, "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 5989 */         hr = Integer.parseInt(timeAsString.substring(0, 2));
/* 5990 */         min = Integer.parseInt(timeAsString.substring(3, 5));
/* 5991 */         sec = timeAsString.length() == 5 ? 0 : Integer.parseInt(timeAsString.substring(6));
/*      */       }
/*      */ 
/* 5995 */       Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */ 
/* 5997 */       synchronized (sessionCalendar) {
/* 5998 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimeCreate(sessionCalendar, hr, min, sec), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/* 6007 */       sqlEx = SQLError.createSQLException(ex.toString(), "S1009", getExceptionInterceptor());
/*      */ 
/* 6009 */       sqlEx.initCause(ex);
/*      */     }
/* 6011 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private Time getTimeInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 6032 */     checkRowPos();
/*      */ 
/* 6034 */     if (this.isBinaryEncoded) {
/* 6035 */       return getNativeTime(columnIndex, targetCalendar, tz, rollForward);
/*      */     }
/*      */ 
/* 6038 */     if (!this.useFastDateParsing) {
/* 6039 */       String timeAsString = getStringInternal(columnIndex, false);
/*      */ 
/* 6041 */       return getTimeFromString(timeAsString, targetCalendar, columnIndex, tz, rollForward);
/*      */     }
/*      */ 
/* 6045 */     checkColumnBounds(columnIndex);
/*      */ 
/* 6047 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 6049 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 6050 */       this.wasNullFlag = true;
/*      */ 
/* 6052 */       return null;
/*      */     }
/*      */ 
/* 6055 */     this.wasNullFlag = false;
/*      */ 
/* 6057 */     return this.thisRow.getTimeFast(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 6074 */     return getTimestampInternal(columnIndex, null, getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 6096 */     return getTimestampInternal(columnIndex, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String columnName)
/*      */     throws SQLException
/*      */   {
/* 6112 */     return getTimestamp(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 6133 */     return getTimestamp(findColumn(columnName), cal);
/*      */   }
/*      */ 
/*      */   private Timestamp getTimestampFromString(int columnIndex, Calendar targetCalendar, String timestampValue, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/*      */     SQLException sqlEx;
/*      */     try {
/* 6141 */       this.wasNullFlag = false;
/*      */ 
/* 6143 */       if (timestampValue == null) {
/* 6144 */         this.wasNullFlag = true;
/*      */ 
/* 6146 */         return null;
/*      */       }
/*      */ 
/* 6157 */       timestampValue = timestampValue.trim();
/*      */ 
/* 6159 */       int length = timestampValue.length();
/*      */ 
/* 6161 */       Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 6165 */       synchronized (sessionCalendar) {
/* 6166 */         if ((length > 0) && (timestampValue.charAt(0) == '0') && ((timestampValue.equals("0000-00-00")) || (timestampValue.equals("0000-00-00 00:00:00")) || (timestampValue.equals("00000000000000")) || (timestampValue.equals("0"))))
/*      */         {
/* 6173 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6175 */             this.wasNullFlag = true;
/*      */ 
/* 6177 */             return null;
/* 6178 */           }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6180 */             throw SQLError.createSQLException("Value '" + timestampValue + "' can not be represented as java.sql.Timestamp", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 6187 */           return fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */         }
/* 6189 */         if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
/*      */         {
/* 6191 */           if (!this.useLegacyDatetimeCode) {
/* 6192 */             return TimeUtil.fastTimestampCreate(tz, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/* 6196 */           return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */         }
/*      */ 
/* 6206 */         if (timestampValue.endsWith(".")) {
/* 6207 */           timestampValue = timestampValue.substring(0, timestampValue.length() - 1);
/*      */         }
/*      */ 
/* 6213 */         int year = 0;
/* 6214 */         int month = 0;
/* 6215 */         int day = 0;
/* 6216 */         int hour = 0;
/* 6217 */         int minutes = 0;
/* 6218 */         int seconds = 0;
/* 6219 */         int nanos = 0;
/*      */ 
/* 6221 */         switch (length) {
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/* 6230 */           year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6231 */           month = Integer.parseInt(timestampValue.substring(5, 7));
/*      */ 
/* 6233 */           day = Integer.parseInt(timestampValue.substring(8, 10));
/* 6234 */           hour = Integer.parseInt(timestampValue.substring(11, 13));
/*      */ 
/* 6236 */           minutes = Integer.parseInt(timestampValue.substring(14, 16));
/*      */ 
/* 6238 */           seconds = Integer.parseInt(timestampValue.substring(17, 19));
/*      */ 
/* 6241 */           nanos = 0;
/*      */ 
/* 6243 */           if (length <= 19) break;
/* 6244 */           int decimalIndex = timestampValue.lastIndexOf('.');
/*      */ 
/* 6246 */           if (decimalIndex != -1)
/* 6247 */             if (decimalIndex + 2 <= length) {
/* 6248 */               nanos = Integer.parseInt(timestampValue.substring(decimalIndex + 1));
/*      */ 
/* 6251 */               int numDigits = length - (decimalIndex + 1);
/*      */ 
/* 6253 */               if (numDigits < 9) {
/* 6254 */                 int factor = (int)Math.pow(10.0D, 9 - numDigits);
/* 6255 */                 nanos *= factor;
/*      */               }
/*      */             } else {
/* 6258 */               throw new IllegalArgumentException();
/*      */             } break;
/*      */         case 14:
/* 6272 */           year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6273 */           month = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 6275 */           day = Integer.parseInt(timestampValue.substring(6, 8));
/* 6276 */           hour = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 6278 */           minutes = Integer.parseInt(timestampValue.substring(10, 12));
/*      */ 
/* 6280 */           seconds = Integer.parseInt(timestampValue.substring(12, 14));
/*      */ 
/* 6283 */           break;
/*      */         case 12:
/* 6287 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6289 */           if (year <= 69) {
/* 6290 */             year += 100;
/*      */           }
/*      */ 
/* 6293 */           year += 1900;
/*      */ 
/* 6295 */           month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 6297 */           day = Integer.parseInt(timestampValue.substring(4, 6));
/* 6298 */           hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 6299 */           minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 6301 */           seconds = Integer.parseInt(timestampValue.substring(10, 12));
/*      */ 
/* 6304 */           break;
/*      */         case 10:
/* 6308 */           if ((this.fields[(columnIndex - 1)].getMysqlType() == 10) || (timestampValue.indexOf("-") != -1))
/*      */           {
/* 6310 */             year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6311 */             month = Integer.parseInt(timestampValue.substring(5, 7));
/*      */ 
/* 6313 */             day = Integer.parseInt(timestampValue.substring(8, 10));
/* 6314 */             hour = 0;
/* 6315 */             minutes = 0;
/*      */           } else {
/* 6317 */             year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6319 */             if (year <= 69) {
/* 6320 */               year += 100;
/*      */             }
/*      */ 
/* 6323 */             month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 6325 */             day = Integer.parseInt(timestampValue.substring(4, 6));
/* 6326 */             hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 6327 */             minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 6330 */             year += 1900;
/*      */           }
/*      */ 
/* 6333 */           break;
/*      */         case 8:
/* 6337 */           if (timestampValue.indexOf(":") != -1) {
/* 6338 */             hour = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6340 */             minutes = Integer.parseInt(timestampValue.substring(3, 5));
/*      */ 
/* 6342 */             seconds = Integer.parseInt(timestampValue.substring(6, 8));
/*      */ 
/* 6344 */             year = 1970;
/* 6345 */             month = 1;
/* 6346 */             day = 1;
/*      */           }
/*      */           else
/*      */           {
/* 6350 */             year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6351 */             month = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 6353 */             day = Integer.parseInt(timestampValue.substring(6, 8));
/*      */ 
/* 6355 */             year -= 1900;
/* 6356 */             month--;
/*      */           }
/* 6358 */           break;
/*      */         case 6:
/* 6362 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6364 */           if (year <= 69) {
/* 6365 */             year += 100;
/*      */           }
/*      */ 
/* 6368 */           year += 1900;
/*      */ 
/* 6370 */           month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 6372 */           day = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 6374 */           break;
/*      */         case 4:
/* 6378 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6380 */           if (year <= 69) {
/* 6381 */             year += 100;
/*      */           }
/*      */ 
/* 6384 */           year += 1900;
/*      */ 
/* 6386 */           month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 6389 */           day = 1;
/*      */ 
/* 6391 */           break;
/*      */         case 2:
/* 6395 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6397 */           if (year <= 69) {
/* 6398 */             year += 100;
/*      */           }
/*      */ 
/* 6401 */           year += 1900;
/* 6402 */           month = 1;
/* 6403 */           day = 1;
/*      */ 
/* 6405 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 7:
/*      */         case 9:
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         default:
/* 6409 */           throw new SQLException("Bad format for Timestamp '" + timestampValue + "' in column " + columnIndex + ".", "S1009");
/*      */         }
/*      */ 
/* 6415 */         if (!this.useLegacyDatetimeCode) {
/* 6416 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */         }
/*      */ 
/* 6420 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 6429 */       sqlEx = SQLError.createSQLException("Cannot convert value '" + timestampValue + "' from column " + columnIndex + " to TIMESTAMP.", "S1009", getExceptionInterceptor());
/*      */ 
/* 6432 */       sqlEx.initCause(e);
/*      */     }
/* 6434 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private Timestamp getTimestampFromBytes(int columnIndex, Calendar targetCalendar, byte[] timestampAsBytes, TimeZone tz, boolean rollForward) throws SQLException {
/* 6443 */     checkColumnBounds(columnIndex);
/*      */     SQLException sqlEx;
/*      */     try {
/* 6446 */       this.wasNullFlag = false;
/*      */ 
/* 6448 */       if (timestampAsBytes == null) {
/* 6449 */         this.wasNullFlag = true;
/*      */ 
/* 6451 */         return null;
/*      */       }
/*      */ 
/* 6454 */       int length = timestampAsBytes.length;
/*      */ 
/* 6456 */       Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 6460 */       synchronized (sessionCalendar) {
/* 6461 */         boolean allZeroTimestamp = true;
/*      */ 
/* 6463 */         boolean onlyTimePresent = StringUtils.indexOf(timestampAsBytes, ':') != -1;
/*      */ 
/* 6465 */         for (int i = 0; i < length; i++) {
/* 6466 */           byte b = timestampAsBytes[i];
/*      */ 
/* 6468 */           if ((b == 32) || (b == 45) || (b == 47)) {
/* 6469 */             onlyTimePresent = false;
/*      */           }
/*      */ 
/* 6472 */           if ((b == 48) || (b == 32) || (b == 58) || (b == 45) || (b == 47) || (b == 46))
/*      */             continue;
/* 6474 */           allZeroTimestamp = false;
/*      */ 
/* 6476 */           break;
/*      */         }
/*      */ 
/* 6480 */         if ((!onlyTimePresent) && (allZeroTimestamp))
/*      */         {
/* 6482 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6484 */             this.wasNullFlag = true;
/*      */ 
/* 6486 */             return null;
/* 6487 */           }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6489 */             throw SQLError.createSQLException("Value '" + timestampAsBytes + "' can not be represented as java.sql.Timestamp", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 6496 */           if (!this.useLegacyDatetimeCode) {
/* 6497 */             return TimeUtil.fastTimestampCreate(tz, 1, 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/* 6501 */           return fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/* 6502 */         }if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
/*      */         {
/* 6504 */           if (!this.useLegacyDatetimeCode) {
/* 6505 */             return TimeUtil.fastTimestampCreate(tz, StringUtils.getInt(timestampAsBytes, 0, 4), 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/* 6509 */           return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, StringUtils.getInt(timestampAsBytes, 0, 4), 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */         }
/*      */ 
/* 6517 */         if (timestampAsBytes[(length - 1)] == 46) {
/* 6518 */           length--;
/*      */         }
/*      */ 
/* 6523 */         int year = 0;
/* 6524 */         int month = 0;
/* 6525 */         int day = 0;
/* 6526 */         int hour = 0;
/* 6527 */         int minutes = 0;
/* 6528 */         int seconds = 0;
/* 6529 */         int nanos = 0;
/*      */ 
/* 6531 */         switch (length) {
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/* 6540 */           year = StringUtils.getInt(timestampAsBytes, 0, 4);
/* 6541 */           month = StringUtils.getInt(timestampAsBytes, 5, 7);
/* 6542 */           day = StringUtils.getInt(timestampAsBytes, 8, 10);
/* 6543 */           hour = StringUtils.getInt(timestampAsBytes, 11, 13);
/* 6544 */           minutes = StringUtils.getInt(timestampAsBytes, 14, 16);
/* 6545 */           seconds = StringUtils.getInt(timestampAsBytes, 17, 19);
/*      */ 
/* 6547 */           nanos = 0;
/*      */ 
/* 6549 */           if (length <= 19) break;
/* 6550 */           int decimalIndex = StringUtils.lastIndexOf(timestampAsBytes, '.');
/*      */ 
/* 6552 */           if (decimalIndex != -1)
/* 6553 */             if (decimalIndex + 2 <= length)
/* 6554 */               nanos = StringUtils.getInt(timestampAsBytes, decimalIndex + 1, length);
/*      */             else
/* 6556 */               throw new IllegalArgumentException(); 
/* 6556 */           break;
/*      */         case 14:
/* 6570 */           year = StringUtils.getInt(timestampAsBytes, 0, 4);
/* 6571 */           month = StringUtils.getInt(timestampAsBytes, 4, 6);
/* 6572 */           day = StringUtils.getInt(timestampAsBytes, 6, 8);
/* 6573 */           hour = StringUtils.getInt(timestampAsBytes, 8, 10);
/* 6574 */           minutes = StringUtils.getInt(timestampAsBytes, 10, 12);
/* 6575 */           seconds = StringUtils.getInt(timestampAsBytes, 12, 14);
/*      */ 
/* 6577 */           break;
/*      */         case 12:
/* 6581 */           year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */ 
/* 6583 */           if (year <= 69) {
/* 6584 */             year += 100;
/*      */           }
/*      */ 
/* 6587 */           year += 1900;
/*      */ 
/* 6589 */           month = StringUtils.getInt(timestampAsBytes, 2, 4);
/* 6590 */           day = StringUtils.getInt(timestampAsBytes, 4, 6);
/* 6591 */           hour = StringUtils.getInt(timestampAsBytes, 6, 8);
/* 6592 */           minutes = StringUtils.getInt(timestampAsBytes, 8, 10);
/* 6593 */           seconds = StringUtils.getInt(timestampAsBytes, 10, 12);
/*      */ 
/* 6595 */           break;
/*      */         case 10:
/* 6599 */           if ((this.fields[(columnIndex - 1)].getMysqlType() == 10) || (StringUtils.indexOf(timestampAsBytes, '-') != -1))
/*      */           {
/* 6601 */             year = StringUtils.getInt(timestampAsBytes, 0, 4);
/* 6602 */             month = StringUtils.getInt(timestampAsBytes, 5, 7);
/* 6603 */             day = StringUtils.getInt(timestampAsBytes, 8, 10);
/* 6604 */             hour = 0;
/* 6605 */             minutes = 0;
/*      */           } else {
/* 6607 */             year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */ 
/* 6609 */             if (year <= 69) {
/* 6610 */               year += 100;
/*      */             }
/*      */ 
/* 6613 */             month = StringUtils.getInt(timestampAsBytes, 2, 4);
/* 6614 */             day = StringUtils.getInt(timestampAsBytes, 4, 6);
/* 6615 */             hour = StringUtils.getInt(timestampAsBytes, 6, 8);
/* 6616 */             minutes = StringUtils.getInt(timestampAsBytes, 8, 10);
/*      */ 
/* 6618 */             year += 1900;
/*      */           }
/*      */ 
/* 6621 */           break;
/*      */         case 8:
/* 6625 */           if (StringUtils.indexOf(timestampAsBytes, ':') != -1) {
/* 6626 */             hour = StringUtils.getInt(timestampAsBytes, 0, 2);
/* 6627 */             minutes = StringUtils.getInt(timestampAsBytes, 3, 5);
/* 6628 */             seconds = StringUtils.getInt(timestampAsBytes, 6, 8);
/*      */ 
/* 6630 */             year = 1970;
/* 6631 */             month = 1;
/* 6632 */             day = 1;
/*      */           }
/*      */           else
/*      */           {
/* 6637 */             year = StringUtils.getInt(timestampAsBytes, 0, 4);
/* 6638 */             month = StringUtils.getInt(timestampAsBytes, 4, 6);
/* 6639 */             day = StringUtils.getInt(timestampAsBytes, 6, 8);
/*      */ 
/* 6641 */             year -= 1900;
/* 6642 */             month--;
/*      */           }
/* 6644 */           break;
/*      */         case 6:
/* 6648 */           year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */ 
/* 6650 */           if (year <= 69) {
/* 6651 */             year += 100;
/*      */           }
/*      */ 
/* 6654 */           year += 1900;
/*      */ 
/* 6656 */           month = StringUtils.getInt(timestampAsBytes, 2, 4);
/* 6657 */           day = StringUtils.getInt(timestampAsBytes, 4, 6);
/*      */ 
/* 6659 */           break;
/*      */         case 4:
/* 6663 */           year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */ 
/* 6665 */           if (year <= 69) {
/* 6666 */             year += 100;
/*      */           }
/*      */ 
/* 6669 */           year += 1900;
/*      */ 
/* 6671 */           month = StringUtils.getInt(timestampAsBytes, 2, 4);
/* 6672 */           day = 1;
/*      */ 
/* 6674 */           break;
/*      */         case 2:
/* 6678 */           year = StringUtils.getInt(timestampAsBytes, 0, 2);
/*      */ 
/* 6680 */           if (year <= 69) {
/* 6681 */             year += 100;
/*      */           }
/*      */ 
/* 6684 */           year += 1900;
/* 6685 */           month = 1;
/* 6686 */           day = 1;
/*      */ 
/* 6688 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 7:
/*      */         case 9:
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         default:
/* 6692 */           throw new SQLException("Bad format for Timestamp '" + new String(timestampAsBytes) + "' in column " + columnIndex + ".", "S1009");
/*      */         }
/*      */ 
/* 6698 */         if (!this.useLegacyDatetimeCode) {
/* 6699 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */         }
/*      */ 
/* 6703 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 6712 */       sqlEx = SQLError.createSQLException("Cannot convert value '" + new String(timestampAsBytes) + "' from column " + columnIndex + " to TIMESTAMP.", "S1009", getExceptionInterceptor());
/*      */ 
/* 6715 */       sqlEx.initCause(e);
/*      */     }
/* 6717 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private Timestamp getTimestampInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 6738 */     if (this.isBinaryEncoded) {
/* 6739 */       return getNativeTimestamp(columnIndex, targetCalendar, tz, rollForward);
/*      */     }
/*      */ 
/* 6742 */     Timestamp tsVal = null;
/*      */ 
/* 6744 */     if (!this.useFastDateParsing) {
/* 6745 */       String timestampValue = getStringInternal(columnIndex, false);
/*      */ 
/* 6747 */       tsVal = getTimestampFromString(columnIndex, targetCalendar, timestampValue, tz, rollForward);
/*      */     }
/*      */     else
/*      */     {
/* 6751 */       checkClosed();
/* 6752 */       checkRowPos();
/* 6753 */       checkColumnBounds(columnIndex);
/*      */ 
/* 6755 */       tsVal = this.thisRow.getTimestampFast(columnIndex - 1, targetCalendar, tz, rollForward, this.connection, this);
/*      */     }
/*      */ 
/* 6759 */     if (tsVal == null)
/* 6760 */       this.wasNullFlag = true;
/*      */     else {
/* 6762 */       this.wasNullFlag = false;
/*      */     }
/*      */ 
/* 6765 */     return tsVal;
/*      */   }
/*      */ 
/*      */   public int getType()
/*      */     throws SQLException
/*      */   {
/* 6779 */     return this.resultSetType;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public InputStream getUnicodeStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 6801 */     if (!this.isBinaryEncoded) {
/* 6802 */       checkRowPos();
/*      */ 
/* 6804 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */ 
/* 6807 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public InputStream getUnicodeStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 6824 */     return getUnicodeStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public long getUpdateCount() {
/* 6828 */     return this.updateCount;
/*      */   }
/*      */ 
/*      */   public long getUpdateID() {
/* 6832 */     return this.updateId;
/*      */   }
/*      */ 
/*      */   public URL getURL(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 6839 */     String val = getString(colIndex);
/*      */ 
/* 6841 */     if (val == null) {
/* 6842 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 6846 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 6848 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public URL getURL(String colName)
/*      */     throws SQLException
/*      */   {
/* 6858 */     String val = getString(colName);
/*      */ 
/* 6860 */     if (val == null) {
/* 6861 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 6865 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 6867 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 6894 */     return this.warningChain;
/*      */   }
/*      */ 
/*      */   public void insertRow()
/*      */     throws SQLException
/*      */   {
/* 6909 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/* 6926 */     checkClosed();
/*      */ 
/* 6928 */     boolean b = this.rowData.isAfterLast();
/*      */ 
/* 6930 */     return b;
/*      */   }
/*      */ 
/*      */   public boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/* 6947 */     checkClosed();
/*      */ 
/* 6949 */     return this.rowData.isBeforeFirst();
/*      */   }
/*      */ 
/*      */   public boolean isFirst()
/*      */     throws SQLException
/*      */   {
/* 6965 */     checkClosed();
/*      */ 
/* 6967 */     return this.rowData.isFirst();
/*      */   }
/*      */ 
/*      */   public boolean isLast()
/*      */     throws SQLException
/*      */   {
/* 6986 */     checkClosed();
/*      */ 
/* 6988 */     return this.rowData.isLast();
/*      */   }
/*      */ 
/*      */   private void issueConversionViaParsingWarning(String methodName, int columnIndex, Object value, Field fieldInfo, int[] typesWithNoParseConversion)
/*      */     throws SQLException
/*      */   {
/* 7000 */     StringBuffer originalQueryBuf = new StringBuffer();
/*      */ 
/* 7002 */     if ((this.owningStatement != null) && ((this.owningStatement instanceof PreparedStatement)))
/*      */     {
/* 7004 */       originalQueryBuf.append(Messages.getString("ResultSet.CostlyConversionCreatedFromQuery"));
/* 7005 */       originalQueryBuf.append(((PreparedStatement)this.owningStatement).originalSql);
/*      */ 
/* 7007 */       originalQueryBuf.append("\n\n");
/*      */     } else {
/* 7009 */       originalQueryBuf.append(".");
/*      */     }
/*      */ 
/* 7012 */     StringBuffer convertibleTypesBuf = new StringBuffer();
/*      */ 
/* 7014 */     for (int i = 0; i < typesWithNoParseConversion.length; i++) {
/* 7015 */       convertibleTypesBuf.append(MysqlDefs.typeToName(typesWithNoParseConversion[i]));
/* 7016 */       convertibleTypesBuf.append("\n");
/*      */     }
/*      */ 
/* 7019 */     String message = Messages.getString("ResultSet.CostlyConversion", new Object[] { methodName, new Integer(columnIndex + 1), fieldInfo.getOriginalName(), fieldInfo.getOriginalTableName(), originalQueryBuf.toString(), value != null ? value.getClass().getName() : ResultSetMetaData.getClassNameForJavaType(fieldInfo.getSQLType(), fieldInfo.isUnsigned(), fieldInfo.getMysqlType(), (fieldInfo.isBinary()) || (fieldInfo.isBlob()) ? 1 : false, fieldInfo.isOpaqueBinary()), MysqlDefs.typeToName(fieldInfo.getMysqlType()), convertibleTypesBuf.toString() });
/*      */ 
/* 7034 */     this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */   }
/*      */ 
/*      */   public boolean last()
/*      */     throws SQLException
/*      */   {
/* 7058 */     checkClosed();
/*      */ 
/* 7060 */     boolean b = true;
/*      */ 
/* 7062 */     if (this.rowData.size() == 0) {
/* 7063 */       b = false;
/*      */     }
/*      */     else {
/* 7066 */       if (this.onInsertRow) {
/* 7067 */         this.onInsertRow = false;
/*      */       }
/*      */ 
/* 7070 */       if (this.doingUpdates) {
/* 7071 */         this.doingUpdates = false;
/*      */       }
/*      */ 
/* 7074 */       if (this.thisRow != null) {
/* 7075 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */ 
/* 7078 */       this.rowData.beforeLast();
/* 7079 */       this.thisRow = this.rowData.next();
/*      */     }
/*      */ 
/* 7082 */     setRowPositionValidity();
/*      */ 
/* 7084 */     return b;
/*      */   }
/*      */ 
/*      */   public void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/* 7106 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/* 7127 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean next()
/*      */     throws SQLException
/*      */   {
/* 7146 */     checkClosed();
/*      */ 
/* 7148 */     if (this.onInsertRow) {
/* 7149 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/* 7152 */     if (this.doingUpdates) {
/* 7153 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/* 7158 */     if (!reallyResult()) {
/* 7159 */       throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7165 */     if (this.thisRow != null)
/* 7166 */       this.thisRow.closeOpenStreams();
/*      */     boolean b;
/*      */     boolean b;
/* 7169 */     if (this.rowData.size() == 0) {
/* 7170 */       b = false;
/*      */     } else {
/* 7172 */       this.thisRow = this.rowData.next();
/*      */       boolean b;
/* 7174 */       if (this.thisRow == null) {
/* 7175 */         b = false;
/*      */       } else {
/* 7177 */         clearWarnings();
/*      */ 
/* 7179 */         b = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 7184 */     setRowPositionValidity();
/*      */ 
/* 7186 */     return b;
/*      */   }
/*      */ 
/*      */   private int parseIntAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException
/*      */   {
/* 7191 */     if (val == null) {
/* 7192 */       return 0;
/*      */     }
/*      */ 
/* 7195 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 7197 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7198 */       (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */     {
/* 7200 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */     }
/*      */ 
/* 7205 */     return (int)valueAsDouble;
/*      */   }
/*      */ 
/*      */   private int getIntWithOverflowCheck(int columnIndex) throws SQLException {
/* 7209 */     int intValue = this.thisRow.getInt(columnIndex);
/*      */ 
/* 7211 */     checkForIntegerTruncation(columnIndex, null, intValue);
/*      */ 
/* 7214 */     return intValue;
/*      */   }
/*      */ 
/*      */   private void checkForIntegerTruncation(int columnIndex, byte[] valueAsBytes, int intValue)
/*      */     throws SQLException
/*      */   {
/* 7220 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7221 */       (intValue == -2147483648) || (intValue == 2147483647))) {
/* 7222 */       String valueAsString = null;
/*      */ 
/* 7224 */       if (valueAsBytes == null) {
/* 7225 */         valueAsString = this.thisRow.getString(columnIndex, this.fields[columnIndex].getCharacterSet(), this.connection);
/*      */       }
/*      */ 
/* 7230 */       long valueAsLong = Long.parseLong(valueAsString == null ? new String(valueAsBytes) : valueAsString);
/*      */ 
/* 7234 */       if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))
/*      */       {
/* 7236 */         throwRangeException(valueAsString == null ? new String(valueAsBytes) : valueAsString, columnIndex + 1, 4);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private long parseLongAsDouble(int columnIndexZeroBased, String val)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 7246 */     if (val == null) {
/* 7247 */       return 0L;
/*      */     }
/*      */ 
/* 7250 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 7252 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7253 */       (valueAsDouble < -9.223372036854776E+18D) || (valueAsDouble > 9.223372036854776E+18D)))
/*      */     {
/* 7255 */       throwRangeException(val, columnIndexZeroBased + 1, -5);
/*      */     }
/*      */ 
/* 7259 */     return ()valueAsDouble;
/*      */   }
/*      */ 
/*      */   private long getLongWithOverflowCheck(int columnIndexZeroBased, boolean doOverflowCheck) throws SQLException {
/* 7263 */     long longValue = this.thisRow.getLong(columnIndexZeroBased);
/*      */ 
/* 7265 */     if (doOverflowCheck) {
/* 7266 */       checkForLongTruncation(columnIndexZeroBased, null, longValue);
/*      */     }
/*      */ 
/* 7269 */     return longValue;
/*      */   }
/*      */ 
/*      */   private long parseLongWithOverflowCheck(int columnIndexZeroBased, byte[] valueAsBytes, String valueAsString, boolean doCheck)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 7276 */     long longValue = 0L;
/*      */ 
/* 7278 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 7279 */       return 0L;
/*      */     }
/*      */ 
/* 7282 */     if (valueAsBytes != null) {
/* 7283 */       longValue = StringUtils.getLong(valueAsBytes);
/*      */     }
/*      */     else
/*      */     {
/* 7293 */       valueAsString = valueAsString.trim();
/*      */ 
/* 7295 */       longValue = Long.parseLong(valueAsString);
/*      */     }
/*      */ 
/* 7298 */     if ((doCheck) && (this.jdbcCompliantTruncationForReads)) {
/* 7299 */       checkForLongTruncation(columnIndexZeroBased, valueAsBytes, longValue);
/*      */     }
/*      */ 
/* 7302 */     return longValue;
/*      */   }
/*      */ 
/*      */   private void checkForLongTruncation(int columnIndexZeroBased, byte[] valueAsBytes, long longValue) throws SQLException {
/* 7306 */     if ((longValue == -9223372036854775808L) || (longValue == 9223372036854775807L))
/*      */     {
/* 7308 */       String valueAsString = null;
/*      */ 
/* 7310 */       if (valueAsBytes == null) {
/* 7311 */         valueAsString = this.thisRow.getString(columnIndexZeroBased, this.fields[columnIndexZeroBased].getCharacterSet(), this.connection);
/*      */       }
/*      */ 
/* 7316 */       double valueAsDouble = Double.parseDouble(valueAsString == null ? new String(valueAsBytes) : valueAsString);
/*      */ 
/* 7320 */       if ((valueAsDouble < -9.223372036854776E+18D) || (valueAsDouble > 9.223372036854776E+18D))
/*      */       {
/* 7322 */         throwRangeException(valueAsString == null ? new String(valueAsBytes) : valueAsString, columnIndexZeroBased + 1, -5);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private short parseShortAsDouble(int columnIndex, String val)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 7331 */     if (val == null) {
/* 7332 */       return 0;
/*      */     }
/*      */ 
/* 7335 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 7337 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7338 */       (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D)))
/*      */     {
/* 7340 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 5);
/*      */     }
/*      */ 
/* 7345 */     return (short)(int)valueAsDouble;
/*      */   }
/*      */ 
/*      */   private short parseShortWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 7352 */     short shortValue = 0;
/*      */ 
/* 7354 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 7355 */       return 0;
/*      */     }
/*      */ 
/* 7358 */     if (valueAsBytes != null) {
/* 7359 */       shortValue = StringUtils.getShort(valueAsBytes);
/*      */     }
/*      */     else
/*      */     {
/* 7369 */       valueAsString = valueAsString.trim();
/*      */ 
/* 7371 */       shortValue = Short.parseShort(valueAsString);
/*      */     }
/*      */ 
/* 7374 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7375 */       (shortValue == -32768) || (shortValue == 32767))) {
/* 7376 */       long valueAsLong = Long.parseLong(valueAsString == null ? new String(valueAsBytes) : valueAsString);
/*      */ 
/* 7380 */       if ((valueAsLong < -32768L) || (valueAsLong > 32767L))
/*      */       {
/* 7382 */         throwRangeException(valueAsString == null ? new String(valueAsBytes) : valueAsString, columnIndex, 5);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 7389 */     return shortValue;
/*      */   }
/*      */ 
/*      */   public boolean prev()
/*      */     throws SQLException
/*      */   {
/* 7413 */     checkClosed();
/*      */ 
/* 7415 */     int rowIndex = this.rowData.getCurrentRowNumber();
/*      */ 
/* 7417 */     if (this.thisRow != null) {
/* 7418 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */ 
/* 7421 */     boolean b = true;
/*      */ 
/* 7423 */     if (rowIndex - 1 >= 0) {
/* 7424 */       rowIndex--;
/* 7425 */       this.rowData.setCurrentRow(rowIndex);
/* 7426 */       this.thisRow = this.rowData.getAt(rowIndex);
/*      */ 
/* 7428 */       b = true;
/* 7429 */     } else if (rowIndex - 1 == -1) {
/* 7430 */       rowIndex--;
/* 7431 */       this.rowData.setCurrentRow(rowIndex);
/* 7432 */       this.thisRow = null;
/*      */ 
/* 7434 */       b = false;
/*      */     } else {
/* 7436 */       b = false;
/*      */     }
/*      */ 
/* 7439 */     setRowPositionValidity();
/*      */ 
/* 7441 */     return b;
/*      */   }
/*      */ 
/*      */   public boolean previous()
/*      */     throws SQLException
/*      */   {
/* 7463 */     if (this.onInsertRow) {
/* 7464 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/* 7467 */     if (this.doingUpdates) {
/* 7468 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/* 7471 */     return prev();
/*      */   }
/*      */ 
/*      */   public void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 7484 */     if (this.isClosed) {
/* 7485 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 7489 */       if (this.useUsageAdvisor)
/*      */       {
/* 7493 */         if (!calledExplicitly) {
/* 7494 */           this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver")));
/*      */         }
/*      */ 
/* 7513 */         if ((this.rowData instanceof RowDataStatic))
/*      */         {
/* 7517 */           if (this.rowData.size() > this.connection.getResultSetSizeThreshold())
/*      */           {
/* 7519 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Too_Large_Result_Set", new Object[] { new Integer(this.rowData.size()), new Integer(this.connection.getResultSetSizeThreshold()) })));
/*      */           }
/*      */ 
/* 7547 */           if ((!isLast()) && (!isAfterLast()) && (this.rowData.size() != 0))
/*      */           {
/* 7549 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set", new Object[] { new Integer(getRow()), new Integer(this.rowData.size()) })));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 7582 */         if ((this.columnUsed.length > 0) && (!this.rowData.wasEmpty())) {
/* 7583 */           StringBuffer buf = new StringBuffer(Messages.getString("ResultSet.The_following_columns_were_never_referenced"));
/*      */ 
/* 7587 */           boolean issueWarn = false;
/*      */ 
/* 7589 */           for (int i = 0; i < this.columnUsed.length; i++) {
/* 7590 */             if (this.columnUsed[i] == 0) {
/* 7591 */               if (!issueWarn)
/* 7592 */                 issueWarn = true;
/*      */               else {
/* 7594 */                 buf.append(", ");
/*      */               }
/*      */ 
/* 7597 */               buf.append(this.fields[i].getFullName());
/*      */             }
/*      */           }
/*      */ 
/* 7601 */           if (issueWarn) {
/* 7602 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), 0, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, buf.toString()));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 7616 */       if ((this.owningStatement != null) && (calledExplicitly)) {
/* 7617 */         this.owningStatement.removeOpenResultSet(this);
/*      */       }
/*      */ 
/* 7620 */       SQLException exceptionDuringClose = null;
/*      */ 
/* 7622 */       if (this.rowData != null) {
/*      */         try {
/* 7624 */           this.rowData.close();
/*      */         } catch (SQLException sqlEx) {
/* 7626 */           exceptionDuringClose = sqlEx;
/*      */         }
/*      */       }
/*      */ 
/* 7630 */       if (this.statementUsedForFetchingRows != null) {
/*      */         try {
/* 7632 */           this.statementUsedForFetchingRows.realClose(true, false);
/*      */         } catch (SQLException sqlEx) {
/* 7634 */           if (exceptionDuringClose != null)
/* 7635 */             exceptionDuringClose.setNextException(sqlEx);
/*      */           else {
/* 7637 */             exceptionDuringClose = sqlEx;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 7642 */       this.rowData = null;
/* 7643 */       this.defaultTimeZone = null;
/* 7644 */       this.fields = null;
/* 7645 */       this.columnLabelToIndex = null;
/* 7646 */       this.fullColumnNameToIndex = null;
/* 7647 */       this.columnToIndexCache = null;
/* 7648 */       this.eventSink = null;
/* 7649 */       this.warningChain = null;
/*      */ 
/* 7651 */       if (!this.retainOwningStatement) {
/* 7652 */         this.owningStatement = null;
/*      */       }
/*      */ 
/* 7655 */       this.catalog = null;
/* 7656 */       this.serverInfo = null;
/* 7657 */       this.thisRow = null;
/* 7658 */       this.fastDateCal = null;
/* 7659 */       this.connection = null;
/*      */ 
/* 7661 */       this.isClosed = true;
/*      */ 
/* 7663 */       if (exceptionDuringClose != null)
/* 7664 */         throw exceptionDuringClose;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean reallyResult()
/*      */   {
/* 7670 */     if (this.rowData != null) {
/* 7671 */       return true;
/*      */     }
/*      */ 
/* 7674 */     return this.reallyResult;
/*      */   }
/*      */ 
/*      */   public void refreshRow()
/*      */     throws SQLException
/*      */   {
/* 7698 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean relative(int rows)
/*      */     throws SQLException
/*      */   {
/* 7728 */     checkClosed();
/*      */ 
/* 7730 */     if (this.rowData.size() == 0) {
/* 7731 */       setRowPositionValidity();
/*      */ 
/* 7733 */       return false;
/*      */     }
/*      */ 
/* 7736 */     if (this.thisRow != null) {
/* 7737 */       this.thisRow.closeOpenStreams();
/*      */     }
/*      */ 
/* 7740 */     this.rowData.moveRowRelative(rows);
/* 7741 */     this.thisRow = this.rowData.getAt(this.rowData.getCurrentRowNumber());
/*      */ 
/* 7743 */     setRowPositionValidity();
/*      */ 
/* 7745 */     return (!this.rowData.isAfterLast()) && (!this.rowData.isBeforeFirst());
/*      */   }
/*      */ 
/*      */   public boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 7764 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 7782 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 7800 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   protected void setBinaryEncoded()
/*      */   {
/* 7808 */     this.isBinaryEncoded = true;
/*      */   }
/*      */ 
/*      */   private void setDefaultTimeZone(TimeZone defaultTimeZone) {
/* 7812 */     this.defaultTimeZone = defaultTimeZone;
/*      */   }
/*      */ 
/*      */   public void setFetchDirection(int direction)
/*      */     throws SQLException
/*      */   {
/* 7831 */     if ((direction != 1000) && (direction != 1001) && (direction != 1002))
/*      */     {
/* 7833 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7839 */     this.fetchDirection = direction;
/*      */   }
/*      */ 
/*      */   public void setFetchSize(int rows)
/*      */     throws SQLException
/*      */   {
/* 7859 */     if (rows < 0) {
/* 7860 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7866 */     this.fetchSize = rows;
/*      */   }
/*      */ 
/*      */   public void setFirstCharOfQuery(char c)
/*      */   {
/* 7877 */     this.firstCharOfQuery = c;
/*      */   }
/*      */ 
/*      */   protected void setNextResultSet(ResultSetInternalMethods nextResultSet)
/*      */   {
/* 7888 */     this.nextResultSet = nextResultSet;
/*      */   }
/*      */ 
/*      */   public void setOwningStatement(StatementImpl owningStatement) {
/* 7892 */     this.owningStatement = owningStatement;
/*      */   }
/*      */ 
/*      */   protected void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 7902 */     this.resultSetConcurrency = concurrencyFlag;
/*      */   }
/*      */ 
/*      */   protected void setResultSetType(int typeFlag)
/*      */   {
/* 7913 */     this.resultSetType = typeFlag;
/*      */   }
/*      */ 
/*      */   protected void setServerInfo(String info)
/*      */   {
/* 7923 */     this.serverInfo = info;
/*      */   }
/*      */ 
/*      */   public void setStatementUsedForFetchingRows(PreparedStatement stmt) {
/* 7927 */     this.statementUsedForFetchingRows = stmt;
/*      */   }
/*      */ 
/*      */   public void setWrapperStatement(Statement wrapperStatement)
/*      */   {
/* 7935 */     this.wrapperStatement = wrapperStatement;
/*      */   }
/*      */ 
/*      */   private void throwRangeException(String valueAsString, int columnIndex, int jdbcType) throws SQLException
/*      */   {
/* 7940 */     String datatype = null;
/*      */ 
/* 7942 */     switch (jdbcType) {
/*      */     case -6:
/* 7944 */       datatype = "TINYINT";
/* 7945 */       break;
/*      */     case 5:
/* 7947 */       datatype = "SMALLINT";
/* 7948 */       break;
/*      */     case 4:
/* 7950 */       datatype = "INTEGER";
/* 7951 */       break;
/*      */     case -5:
/* 7953 */       datatype = "BIGINT";
/* 7954 */       break;
/*      */     case 7:
/* 7956 */       datatype = "REAL";
/* 7957 */       break;
/*      */     case 6:
/* 7959 */       datatype = "FLOAT";
/* 7960 */       break;
/*      */     case 8:
/* 7962 */       datatype = "DOUBLE";
/* 7963 */       break;
/*      */     case 3:
/* 7965 */       datatype = "DECIMAL";
/* 7966 */       break;
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     default:
/* 7968 */       datatype = " (JDBC type '" + jdbcType + "')";
/*      */     }
/*      */ 
/* 7971 */     throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 7982 */     if (this.reallyResult) {
/* 7983 */       return super.toString();
/*      */     }
/*      */ 
/* 7986 */     return "Result set representing update count of " + this.updateCount;
/*      */   }
/*      */ 
/*      */   public void updateArray(int arg0, Array arg1)
/*      */     throws SQLException
/*      */   {
/* 7993 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateArray(String arg0, Array arg1)
/*      */     throws SQLException
/*      */   {
/* 8000 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 8024 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 8046 */     updateAsciiStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(int columnIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 8067 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(String columnName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 8086 */     updateBigDecimal(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 8110 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 8132 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public void updateBlob(int arg0, java.sql.Blob arg1)
/*      */     throws SQLException
/*      */   {
/* 8139 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBlob(String arg0, java.sql.Blob arg1)
/*      */     throws SQLException
/*      */   {
/* 8146 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBoolean(int columnIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 8166 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBoolean(String columnName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 8184 */     updateBoolean(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateByte(int columnIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 8204 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateByte(String columnName, byte x)
/*      */     throws SQLException
/*      */   {
/* 8222 */     updateByte(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateBytes(int columnIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 8242 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBytes(String columnName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 8260 */     updateBytes(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int columnIndex, Reader x, int length)
/*      */     throws SQLException
/*      */   {
/* 8284 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String columnName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 8306 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */ 
/*      */   public void updateClob(int arg0, java.sql.Clob arg1)
/*      */     throws SQLException
/*      */   {
/* 8313 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateClob(String columnName, java.sql.Clob clob)
/*      */     throws SQLException
/*      */   {
/* 8321 */     updateClob(findColumn(columnName), clob);
/*      */   }
/*      */ 
/*      */   public void updateDate(int columnIndex, Date x)
/*      */     throws SQLException
/*      */   {
/* 8342 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateDate(String columnName, Date x)
/*      */     throws SQLException
/*      */   {
/* 8361 */     updateDate(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateDouble(int columnIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 8381 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateDouble(String columnName, double x)
/*      */     throws SQLException
/*      */   {
/* 8399 */     updateDouble(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateFloat(int columnIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 8419 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateFloat(String columnName, float x)
/*      */     throws SQLException
/*      */   {
/* 8437 */     updateFloat(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateInt(int columnIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 8457 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateInt(String columnName, int x)
/*      */     throws SQLException
/*      */   {
/* 8475 */     updateInt(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateLong(int columnIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 8495 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateLong(String columnName, long x)
/*      */     throws SQLException
/*      */   {
/* 8513 */     updateLong(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateNull(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 8531 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateNull(String columnName)
/*      */     throws SQLException
/*      */   {
/* 8547 */     updateNull(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public void updateObject(int columnIndex, Object x)
/*      */     throws SQLException
/*      */   {
/* 8567 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateObject(int columnIndex, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 8592 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateObject(String columnName, Object x)
/*      */     throws SQLException
/*      */   {
/* 8610 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateObject(String columnName, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 8633 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateRef(int arg0, Ref arg1)
/*      */     throws SQLException
/*      */   {
/* 8640 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateRef(String arg0, Ref arg1)
/*      */     throws SQLException
/*      */   {
/* 8647 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateRow()
/*      */     throws SQLException
/*      */   {
/* 8661 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateShort(int columnIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 8681 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateShort(String columnName, short x)
/*      */     throws SQLException
/*      */   {
/* 8699 */     updateShort(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateString(int columnIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 8719 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateString(String columnName, String x)
/*      */     throws SQLException
/*      */   {
/* 8737 */     updateString(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateTime(int columnIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 8758 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateTime(String columnName, Time x)
/*      */     throws SQLException
/*      */   {
/* 8777 */     updateTime(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(int columnIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 8799 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(String columnName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 8818 */     updateTimestamp(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/* 8833 */     return this.wasNullFlag;
/*      */   }
/*      */ 
/*      */   protected Calendar getGmtCalendar()
/*      */   {
/* 8840 */     if (this.gmtCalendar == null) {
/* 8841 */       this.gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */     }
/*      */ 
/* 8844 */     return this.gmtCalendar;
/*      */   }
/*      */ 
/*      */   protected ExceptionInterceptor getExceptionInterceptor() {
/* 8848 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  124 */     if (Util.isJdbc4()) {
/*      */       try {
/*  126 */         JDBC_4_RS_4_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4ResultSet").getConstructor(new Class[] { Long.TYPE, Long.TYPE, ConnectionImpl.class, StatementImpl.class });
/*      */ 
/*  131 */         JDBC_4_RS_6_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4ResultSet").getConstructor(new Class[] { String.class, new Field[0].getClass(), RowData.class, ConnectionImpl.class, StatementImpl.class });
/*      */ 
/*  137 */         JDBC_4_UPD_RS_6_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4UpdatableResultSet").getConstructor(new Class[] { String.class, new Field[0].getClass(), RowData.class, ConnectionImpl.class, StatementImpl.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  145 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  147 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  149 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  152 */       JDBC_4_RS_4_ARG_CTOR = null;
/*  153 */       JDBC_4_RS_6_ARG_CTOR = null;
/*  154 */       JDBC_4_UPD_RS_6_ARG_CTOR = null;
/*      */     }
/*      */ 
/*  161 */     MIN_DIFF_PREC = Float.parseFloat(Float.toString(1.4E-45F)) - Double.parseDouble(Float.toString(1.4E-45F));
/*      */ 
/*  167 */     MAX_DIFF_PREC = Float.parseFloat(Float.toString(3.4028235E+38F)) - Double.parseDouble(Float.toString(3.4028235E+38F));
/*      */ 
/*  171 */     resultCounter = 1;
/*      */ 
/*  342 */     EMPTY_SPACE = new char['ÿ'];
/*      */ 
/*  345 */     for (int i = 0; i < EMPTY_SPACE.length; i++)
/*  346 */       EMPTY_SPACE[i] = ' ';
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ResultSetImpl
 * JD-Core Version:    0.6.0
 */