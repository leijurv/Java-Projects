/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import java.io.InputStream;
/*      */ import java.math.BigInteger;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ 
/*      */ public class StatementImpl
/*      */   implements Statement
/*      */ {
/*      */   protected static final String PING_MARKER = "/* ping */";
/*  153 */   protected Object cancelTimeoutMutex = new Object();
/*      */ 
/*  156 */   protected static int statementCounter = 1;
/*      */   public static final byte USES_VARIABLES_FALSE = 0;
/*      */   public static final byte USES_VARIABLES_TRUE = 1;
/*      */   public static final byte USES_VARIABLES_UNKNOWN = -1;
/*  164 */   protected boolean wasCancelled = false;
/*  165 */   protected boolean wasCancelledByTimeout = false;
/*      */   protected List batchedArgs;
/*  171 */   protected SingleByteCharsetConverter charConverter = null;
/*      */ 
/*  174 */   protected String charEncoding = null;
/*      */ 
/*  177 */   protected ConnectionImpl connection = null;
/*      */ 
/*  179 */   protected long connectionId = 0L;
/*      */ 
/*  182 */   protected String currentCatalog = null;
/*      */ 
/*  185 */   protected boolean doEscapeProcessing = true;
/*      */ 
/*  188 */   protected ProfilerEventHandler eventSink = null;
/*      */ 
/*  191 */   private int fetchSize = 0;
/*      */ 
/*  194 */   protected boolean isClosed = false;
/*      */ 
/*  197 */   protected long lastInsertId = -1L;
/*      */ 
/*  200 */   protected int maxFieldSize = MysqlIO.getMaxBuf();
/*      */ 
/*  206 */   protected int maxRows = -1;
/*      */ 
/*  209 */   protected boolean maxRowsChanged = false;
/*      */ 
/*  212 */   protected Set openResults = new HashSet();
/*      */ 
/*  215 */   protected boolean pedantic = false;
/*      */   protected Throwable pointOfOrigin;
/*  224 */   protected boolean profileSQL = false;
/*      */ 
/*  227 */   protected ResultSetInternalMethods results = null;
/*      */ 
/*  230 */   protected int resultSetConcurrency = 0;
/*      */ 
/*  233 */   protected int resultSetType = 0;
/*      */   protected int statementId;
/*  239 */   protected int timeoutInMillis = 0;
/*      */ 
/*  242 */   protected long updateCount = -1L;
/*      */ 
/*  245 */   protected boolean useUsageAdvisor = false;
/*      */ 
/*  248 */   protected SQLWarning warningChain = null;
/*      */ 
/*  254 */   protected boolean holdResultsOpenOverClose = false;
/*      */ 
/*  256 */   protected ArrayList batchedGeneratedKeys = null;
/*      */ 
/*  258 */   protected boolean retrieveGeneratedKeys = false;
/*      */ 
/*  260 */   protected boolean continueBatchOnError = false;
/*      */ 
/*  262 */   protected PingTarget pingTarget = null;
/*      */   protected boolean useLegacyDatetimeCode;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*  269 */   protected boolean lastQueryIsOnDupKeyUpdate = false;
/*      */ 
/*  589 */   private int originalResultSetType = 0;
/*  590 */   private int originalFetchSize = 0;
/*      */ 
/* 2650 */   private boolean isPoolable = true;
/*      */   private InputStream localInfileInputStream;
/*      */ 
/*      */   public StatementImpl(ConnectionImpl c, String catalog)
/*      */     throws SQLException
/*      */   {
/*  283 */     if ((c == null) || (c.isClosed())) {
/*  284 */       throw SQLError.createSQLException(Messages.getString("Statement.0"), "08003", null);
/*      */     }
/*      */ 
/*  289 */     this.connection = c;
/*  290 */     this.connectionId = this.connection.getId();
/*  291 */     this.exceptionInterceptor = c.getExceptionInterceptor();
/*      */ 
/*  293 */     this.currentCatalog = catalog;
/*  294 */     this.pedantic = this.connection.getPedantic();
/*  295 */     this.continueBatchOnError = this.connection.getContinueBatchOnError();
/*  296 */     this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
/*      */ 
/*  298 */     if (!this.connection.getDontTrackOpenResources()) {
/*  299 */       this.connection.registerStatement(this);
/*      */     }
/*      */ 
/*  306 */     if (this.connection != null) {
/*  307 */       this.maxFieldSize = this.connection.getMaxAllowedPacket();
/*      */ 
/*  309 */       int defaultFetchSize = this.connection.getDefaultFetchSize();
/*      */ 
/*  311 */       if (defaultFetchSize != 0) {
/*  312 */         setFetchSize(defaultFetchSize);
/*      */       }
/*      */     }
/*      */ 
/*  316 */     if (this.connection.getUseUnicode()) {
/*  317 */       this.charEncoding = this.connection.getEncoding();
/*      */ 
/*  319 */       this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */     }
/*      */ 
/*  323 */     boolean profiling = (this.connection.getProfileSql()) || (this.connection.getUseUsageAdvisor()) || (this.connection.getLogSlowQueries());
/*      */ 
/*  326 */     if ((this.connection.getAutoGenerateTestcaseScript()) || (profiling)) {
/*  327 */       this.statementId = (statementCounter++);
/*      */     }
/*      */ 
/*  330 */     if (profiling) {
/*  331 */       this.pointOfOrigin = new Throwable();
/*  332 */       this.profileSQL = this.connection.getProfileSql();
/*  333 */       this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  334 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */     }
/*      */ 
/*  337 */     int maxRowsConn = this.connection.getMaxRows();
/*      */ 
/*  339 */     if (maxRowsConn != -1) {
/*  340 */       setMaxRows(maxRowsConn);
/*      */     }
/*      */ 
/*  343 */     this.holdResultsOpenOverClose = this.connection.getHoldResultsOpenOverStatementClose();
/*      */   }
/*      */ 
/*      */   public synchronized void addBatch(String sql)
/*      */     throws SQLException
/*      */   {
/*  356 */     if (this.batchedArgs == null) {
/*  357 */       this.batchedArgs = new ArrayList();
/*      */     }
/*      */ 
/*  360 */     if (sql != null)
/*  361 */       this.batchedArgs.add(sql);
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/*  371 */     if ((!this.isClosed) && (this.connection != null) && (this.connection.versionMeetsMinimum(5, 0, 0)))
/*      */     {
/*  374 */       Connection cancelConn = null;
/*  375 */       java.sql.Statement cancelStmt = null;
/*      */       try
/*      */       {
/*  378 */         cancelConn = this.connection.duplicate();
/*  379 */         cancelStmt = cancelConn.createStatement();
/*  380 */         cancelStmt.execute("KILL QUERY " + this.connection.getIO().getThreadId());
/*      */ 
/*  382 */         this.wasCancelled = true;
/*      */       } finally {
/*  384 */         if (cancelStmt != null) {
/*  385 */           cancelStmt.close();
/*      */         }
/*      */ 
/*  388 */         if (cancelConn != null)
/*  389 */           cancelConn.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkClosed()
/*      */     throws SQLException
/*      */   {
/*  405 */     if (this.isClosed)
/*  406 */       throw SQLError.createSQLException(Messages.getString("Statement.49"), "08003", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected void checkForDml(String sql, char firstStatementChar)
/*      */     throws SQLException
/*      */   {
/*  426 */     if ((firstStatementChar == 'I') || (firstStatementChar == 'U') || (firstStatementChar == 'D') || (firstStatementChar == 'A') || (firstStatementChar == 'C'))
/*      */     {
/*  429 */       String noCommentSql = StringUtils.stripComments(sql, "'\"", "'\"", true, false, true, true);
/*      */ 
/*  432 */       if ((StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DROP")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "CREATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "ALTER")))
/*      */       {
/*  438 */         throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkNullOrEmptyQuery(String sql)
/*      */     throws SQLException
/*      */   {
/*  455 */     if (sql == null) {
/*  456 */       throw SQLError.createSQLException(Messages.getString("Statement.59"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  461 */     if (sql.length() == 0)
/*  462 */       throw SQLError.createSQLException(Messages.getString("Statement.61"), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public synchronized void clearBatch()
/*      */     throws SQLException
/*      */   {
/*  477 */     if (this.batchedArgs != null)
/*  478 */       this.batchedArgs.clear();
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  490 */     this.warningChain = null;
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/*  509 */     realClose(true, true);
/*      */   }
/*      */ 
/*      */   protected synchronized void closeAllOpenResults()
/*      */   {
/*  516 */     if (this.openResults != null) {
/*  517 */       for (Iterator iter = this.openResults.iterator(); iter.hasNext(); ) {
/*  518 */         ResultSetInternalMethods element = (ResultSetInternalMethods)iter.next();
/*      */         try
/*      */         {
/*  521 */           element.realClose(false);
/*      */         } catch (SQLException sqlEx) {
/*  523 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */       }
/*      */ 
/*  527 */       this.openResults.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void removeOpenResultSet(ResultSet rs) {
/*  532 */     if (this.openResults != null)
/*  533 */       this.openResults.remove(rs);
/*      */   }
/*      */ 
/*      */   public synchronized int getOpenResultSetCount()
/*      */   {
/*  538 */     if (this.openResults != null) {
/*  539 */       return this.openResults.size();
/*      */     }
/*      */ 
/*  542 */     return 0;
/*      */   }
/*      */ 
/*      */   private ResultSetInternalMethods createResultSetUsingServerFetch(String sql)
/*      */     throws SQLException
/*      */   {
/*  551 */     java.sql.PreparedStatement pStmt = this.connection.prepareStatement(sql, this.resultSetType, this.resultSetConcurrency);
/*      */ 
/*  554 */     pStmt.setFetchSize(this.fetchSize);
/*      */ 
/*  556 */     if (this.maxRows > -1) {
/*  557 */       pStmt.setMaxRows(this.maxRows);
/*      */     }
/*      */ 
/*  560 */     pStmt.execute();
/*      */ 
/*  566 */     ResultSetInternalMethods rs = ((StatementImpl)pStmt).getResultSetInternal();
/*      */ 
/*  569 */     rs.setStatementUsedForFetchingRows((PreparedStatement)pStmt);
/*      */ 
/*  572 */     this.results = rs;
/*      */ 
/*  574 */     return rs;
/*      */   }
/*      */ 
/*      */   protected boolean createStreamingResultSet()
/*      */   {
/*  585 */     return (this.resultSetType == 1003) && (this.resultSetConcurrency == 1007) && (this.fetchSize == -2147483648);
/*      */   }
/*      */ 
/*      */   public void enableStreamingResults()
/*      */     throws SQLException
/*      */   {
/*  596 */     this.originalResultSetType = this.resultSetType;
/*  597 */     this.originalFetchSize = this.fetchSize;
/*      */ 
/*  599 */     setFetchSize(-2147483648);
/*  600 */     setResultSetType(1003);
/*      */   }
/*      */ 
/*      */   public void disableStreamingResults() throws SQLException {
/*  604 */     if ((this.fetchSize == -2147483648) && (this.resultSetType == 1003))
/*      */     {
/*  606 */       setFetchSize(this.originalFetchSize);
/*  607 */       setResultSetType(this.originalResultSetType);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql)
/*      */     throws SQLException
/*      */   {
/*  626 */     return execute(sql, false);
/*      */   }
/*      */ 
/*      */   private boolean execute(String sql, boolean returnGeneratedKeys) throws SQLException {
/*  630 */     checkClosed();
/*      */ 
/*  632 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/*  634 */     synchronized (locallyScopedConn.getMutex()) {
/*  635 */       this.retrieveGeneratedKeys = returnGeneratedKeys;
/*  636 */       this.lastQueryIsOnDupKeyUpdate = false;
/*  637 */       if (returnGeneratedKeys) {
/*  638 */         this.lastQueryIsOnDupKeyUpdate = containsOnDuplicateKeyInString(sql);
/*      */       }
/*  640 */       resetCancelledState();
/*      */ 
/*  642 */       checkNullOrEmptyQuery(sql);
/*      */ 
/*  644 */       checkClosed();
/*      */ 
/*  646 */       char firstNonWsChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
/*      */ 
/*  648 */       boolean isSelect = true;
/*      */ 
/*  650 */       if (firstNonWsChar != 'S') {
/*  651 */         isSelect = false;
/*      */ 
/*  653 */         if (locallyScopedConn.isReadOnly()) {
/*  654 */           throw SQLError.createSQLException(Messages.getString("Statement.27") + Messages.getString("Statement.28"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  661 */       boolean doStreaming = createStreamingResultSet();
/*      */ 
/*  671 */       if ((doStreaming) && (this.connection.getNetTimeoutForStreamingResults() > 0))
/*      */       {
/*  673 */         executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
/*      */       }
/*      */ 
/*  677 */       if (this.doEscapeProcessing) {
/*  678 */         Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), locallyScopedConn);
/*      */ 
/*  681 */         if ((escapedSqlResult instanceof String))
/*  682 */           sql = (String)escapedSqlResult;
/*      */         else {
/*  684 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */ 
/*  688 */       if ((this.results != null) && 
/*  689 */         (!locallyScopedConn.getHoldResultsOpenOverStatementClose())) {
/*  690 */         this.results.realClose(false);
/*      */       }
/*      */ 
/*  694 */       if ((sql.charAt(0) == '/') && 
/*  695 */         (sql.startsWith("/* ping */"))) {
/*  696 */         doPingInstead();
/*      */ 
/*  698 */         return true;
/*      */       }
/*      */ 
/*  702 */       CachedResultSetMetaData cachedMetaData = null;
/*      */ 
/*  704 */       ResultSetInternalMethods rs = null;
/*      */ 
/*  713 */       this.batchedGeneratedKeys = null;
/*      */ 
/*  715 */       if (useServerFetch()) {
/*  716 */         rs = createResultSetUsingServerFetch(sql);
/*      */       } else {
/*  718 */         CancelTask timeoutTask = null;
/*      */ 
/*  720 */         String oldCatalog = null;
/*      */         try
/*      */         {
/*  723 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/*  726 */             timeoutTask = new CancelTask(this);
/*  727 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */           }
/*      */ 
/*  733 */           if (!locallyScopedConn.getCatalog().equals(this.currentCatalog))
/*      */           {
/*  735 */             oldCatalog = locallyScopedConn.getCatalog();
/*  736 */             locallyScopedConn.setCatalog(this.currentCatalog);
/*      */           }
/*      */ 
/*  743 */           Field[] cachedFields = null;
/*      */ 
/*  745 */           if (locallyScopedConn.getCacheResultSetMetadata()) {
/*  746 */             cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
/*      */ 
/*  748 */             if (cachedMetaData != null) {
/*  749 */               cachedFields = cachedMetaData.fields;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  756 */           if (locallyScopedConn.useMaxRows()) {
/*  757 */             int rowLimit = -1;
/*      */ 
/*  759 */             if (isSelect) {
/*  760 */               if (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1) {
/*  761 */                 rowLimit = this.maxRows;
/*      */               }
/*  763 */               else if (this.maxRows <= 0) {
/*  764 */                 executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */               }
/*      */               else {
/*  767 */                 executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows);
/*      */               }
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*  773 */               executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */             }
/*      */ 
/*  778 */             rs = locallyScopedConn.execSQL(this, sql, rowLimit, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */           }
/*      */           else
/*      */           {
/*  783 */             rs = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */           }
/*      */ 
/*  789 */           if (timeoutTask != null) {
/*  790 */             if (timeoutTask.caughtWhileCancelling != null) {
/*  791 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */ 
/*  794 */             timeoutTask.cancel();
/*  795 */             timeoutTask = null;
/*      */           }
/*      */ 
/*  798 */           synchronized (this.cancelTimeoutMutex) {
/*  799 */             if (this.wasCancelled) {
/*  800 */               SQLException cause = null;
/*      */ 
/*  802 */               if (this.wasCancelledByTimeout)
/*  803 */                 cause = new MySQLTimeoutException();
/*      */               else {
/*  805 */                 cause = new MySQLStatementCancelledException();
/*      */               }
/*      */ 
/*  808 */               resetCancelledState();
/*      */ 
/*  810 */               throw cause;
/*      */             }
/*      */           }
/*      */         } finally {
/*  814 */           if (timeoutTask != null) {
/*  815 */             timeoutTask.cancel();
/*      */           }
/*      */ 
/*  818 */           if (oldCatalog != null) {
/*  819 */             locallyScopedConn.setCatalog(oldCatalog);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  824 */       if (rs != null) {
/*  825 */         this.lastInsertId = rs.getUpdateID();
/*      */ 
/*  827 */         this.results = rs;
/*      */ 
/*  829 */         rs.setFirstCharOfQuery(firstNonWsChar);
/*      */ 
/*  831 */         if (rs.reallyResult()) {
/*  832 */           if (cachedMetaData != null) {
/*  833 */             locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
/*      */           }
/*  836 */           else if (this.connection.getCacheResultSetMetadata()) {
/*  837 */             locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  844 */       return (rs != null) && (rs.reallyResult());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void resetCancelledState() {
/*  849 */     if (this.cancelTimeoutMutex == null) {
/*  850 */       return;
/*      */     }
/*      */ 
/*  853 */     synchronized (this.cancelTimeoutMutex) {
/*  854 */       this.wasCancelled = false;
/*  855 */       this.wasCancelledByTimeout = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, int returnGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/*  866 */     if (returnGeneratedKeys == 1) {
/*  867 */       checkClosed();
/*      */ 
/*  869 */       ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/*  871 */       synchronized (locallyScopedConn.getMutex())
/*      */       {
/*  875 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/*  877 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/*  880 */           boolean bool1 = execute(sql, true);
/*      */ 
/*  882 */           locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); return bool1; } finally { locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  887 */     return execute(sql);
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, int[] generatedKeyIndices)
/*      */     throws SQLException
/*      */   {
/*  895 */     if ((generatedKeyIndices != null) && (generatedKeyIndices.length > 0)) {
/*  896 */       checkClosed();
/*      */ 
/*  898 */       ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/*  900 */       synchronized (locallyScopedConn.getMutex()) {
/*  901 */         this.retrieveGeneratedKeys = true;
/*      */ 
/*  906 */         boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/*      */ 
/*  908 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/*  911 */           boolean bool1 = execute(sql, true);
/*      */ 
/*  913 */           locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); return bool1; } finally { locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  918 */     return execute(sql);
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, String[] generatedKeyNames)
/*      */     throws SQLException
/*      */   {
/*  926 */     if ((generatedKeyNames != null) && (generatedKeyNames.length > 0)) {
/*  927 */       checkClosed();
/*      */ 
/*  929 */       ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/*  931 */       synchronized (locallyScopedConn.getMutex()) {
/*  932 */         this.retrieveGeneratedKeys = true;
/*      */ 
/*  936 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/*  938 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/*  941 */           boolean bool1 = execute(sql, true);
/*      */ 
/*  943 */           locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); return bool1; } finally { locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  948 */     return execute(sql);
/*      */   }
/*      */ 
/*      */   public synchronized int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/*  966 */     checkClosed();
/*      */ 
/*  968 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/*  970 */     if (locallyScopedConn.isReadOnly()) {
/*  971 */       throw SQLError.createSQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  977 */     if ((this.results != null) && 
/*  978 */       (!locallyScopedConn.getHoldResultsOpenOverStatementClose())) {
/*  979 */       this.results.realClose(false);
/*      */     }
/*      */ 
/*  983 */     synchronized (locallyScopedConn.getMutex()) {
/*  984 */       if ((this.batchedArgs == null) || (this.batchedArgs.size() == 0)) {
/*  985 */         return new int[0];
/*      */       }
/*      */ 
/*  989 */       int individualStatementTimeout = this.timeoutInMillis;
/*  990 */       this.timeoutInMillis = 0;
/*      */ 
/*  992 */       CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/*  995 */         resetCancelledState();
/*      */ 
/*  997 */         this.retrieveGeneratedKeys = true;
/*      */ 
/*  999 */         int[] updateCounts = null;
/*      */ 
/* 1002 */         if (this.batchedArgs != null) {
/* 1003 */           nbrCommands = this.batchedArgs.size();
/*      */ 
/* 1005 */           this.batchedGeneratedKeys = new ArrayList(this.batchedArgs.size());
/*      */ 
/* 1007 */           boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
/*      */ 
/* 1009 */           if ((locallyScopedConn.versionMeetsMinimum(4, 1, 1)) && ((multiQueriesEnabled) || ((locallyScopedConn.getRewriteBatchedStatements()) && (nbrCommands > 4))))
/*      */           {
/* 1013 */             int[] arrayOfInt1 = executeBatchUsingMultiQueries(multiQueriesEnabled, nbrCommands, individualStatementTimeout); jsr 367; return arrayOfInt1;
/*      */           }
/*      */ 
/* 1016 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (individualStatementTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/* 1019 */             timeoutTask = new CancelTask(this);
/* 1020 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
/*      */           }
/*      */ 
/* 1024 */           updateCounts = new int[nbrCommands];
/*      */ 
/* 1026 */           for (int i = 0; i < nbrCommands; i++) {
/* 1027 */             updateCounts[i] = -3;
/*      */           }
/*      */ 
/* 1030 */           SQLException sqlEx = null;
/*      */ 
/* 1032 */           int commandIndex = 0;
/*      */ 
/* 1034 */           for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*      */             try {
/* 1036 */               String sql = (String)this.batchedArgs.get(commandIndex);
/* 1037 */               updateCounts[commandIndex] = executeUpdate(sql, true, true);
/*      */ 
/* 1039 */               getBatchedGeneratedKeys(containsOnDuplicateKeyInString(sql) ? 1 : 0);
/*      */             } catch (SQLException ex) {
/* 1041 */               updateCounts[commandIndex] = -3;
/*      */ 
/* 1043 */               if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */               {
/* 1047 */                 sqlEx = ex;
/*      */               } else {
/* 1049 */                 int[] newUpdateCounts = new int[commandIndex];
/*      */ 
/* 1051 */                 if (hasDeadlockOrTimeoutRolledBackTx(ex)) {
/* 1052 */                   for (int i = 0; i < newUpdateCounts.length; i++)
/* 1053 */                     newUpdateCounts[i] = -3;
/*      */                 }
/*      */                 else {
/* 1056 */                   System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */                 }
/*      */ 
/* 1060 */                 throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1067 */           if (sqlEx != null) {
/* 1068 */             throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1074 */         if (timeoutTask != null) {
/* 1075 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1076 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 1079 */           timeoutTask.cancel();
/* 1080 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 1083 */         int nbrCommands = updateCounts != null ? updateCounts : new int[0]; jsr 16; return nbrCommands;
/*      */       }
/*      */       finally {
/* 1086 */         jsr 6; } localObject2 = returnAddress; if (timeoutTask != null) {
/* 1087 */         timeoutTask.cancel();
/*      */       }
/*      */ 
/* 1090 */       resetCancelledState();
/*      */ 
/* 1092 */       this.timeoutInMillis = individualStatementTimeout;
/*      */ 
/* 1094 */       clearBatch(); ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final boolean hasDeadlockOrTimeoutRolledBackTx(SQLException ex)
/*      */   {
/* 1100 */     int vendorCode = ex.getErrorCode();
/*      */ 
/* 1102 */     switch (vendorCode) {
/*      */     case 1206:
/*      */     case 1213:
/* 1105 */       return true;
/*      */     case 1205:
/*      */       try {
/* 1108 */         return !this.connection.versionMeetsMinimum(5, 0, 13);
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 1111 */         return false;
/*      */       }
/*      */     }
/* 1114 */     return false;
/*      */   }
/*      */ 
/*      */   private int[] executeBatchUsingMultiQueries(boolean multiQueriesEnabled, int nbrCommands, int individualStatementTimeout)
/*      */     throws SQLException
/*      */   {
/* 1129 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1131 */     if (!multiQueriesEnabled) {
/* 1132 */       locallyScopedConn.getIO().enableMultiQueries();
/*      */     }
/*      */ 
/* 1135 */     java.sql.Statement batchStmt = null;
/*      */ 
/* 1137 */     CancelTask timeoutTask = null;
/*      */     try
/*      */     {
/* 1140 */       int[] updateCounts = new int[nbrCommands];
/*      */ 
/* 1142 */       for (int i = 0; i < nbrCommands; i++) {
/* 1143 */         updateCounts[i] = -3;
/*      */       }
/*      */ 
/* 1146 */       int commandIndex = 0;
/*      */ 
/* 1148 */       StringBuffer queryBuf = new StringBuffer();
/*      */ 
/* 1150 */       batchStmt = locallyScopedConn.createStatement();
/*      */ 
/* 1152 */       if ((locallyScopedConn.getEnableQueryTimeouts()) && (individualStatementTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */       {
/* 1155 */         timeoutTask = new CancelTask((StatementImpl)batchStmt);
/* 1156 */         locallyScopedConn.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
/*      */       }
/*      */ 
/* 1160 */       int counter = 0;
/*      */ 
/* 1162 */       int numberOfBytesPerChar = 1;
/*      */ 
/* 1164 */       String connectionEncoding = locallyScopedConn.getEncoding();
/*      */ 
/* 1166 */       if (StringUtils.startsWithIgnoreCase(connectionEncoding, "utf"))
/* 1167 */         numberOfBytesPerChar = 3;
/* 1168 */       else if (CharsetMapping.isMultibyteCharset(connectionEncoding)) {
/* 1169 */         numberOfBytesPerChar = 2;
/*      */       }
/*      */ 
/* 1172 */       int escapeAdjust = 1;
/*      */ 
/* 1174 */       if (this.doEscapeProcessing) {
/* 1175 */         escapeAdjust = 2;
/*      */       }
/*      */ 
/* 1180 */       SQLException sqlEx = null;
/*      */ 
/* 1182 */       int argumentSetsInBatchSoFar = 0;
/*      */ 
/* 1184 */       for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 1185 */         String nextQuery = (String)this.batchedArgs.get(commandIndex);
/*      */ 
/* 1187 */         if (((queryBuf.length() + nextQuery.length()) * numberOfBytesPerChar + 1 + 4) * escapeAdjust + 32 > this.connection.getMaxAllowedPacket())
/*      */         {
/*      */           try
/*      */           {
/* 1192 */             batchStmt.execute(queryBuf.toString(), 1);
/*      */           } catch (SQLException ex) {
/* 1194 */             sqlEx = handleExceptionForBatch(commandIndex, argumentSetsInBatchSoFar, updateCounts, ex);
/*      */           }
/*      */ 
/* 1198 */           counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
/*      */ 
/* 1201 */           queryBuf = new StringBuffer();
/* 1202 */           argumentSetsInBatchSoFar = 0;
/*      */         }
/*      */ 
/* 1205 */         queryBuf.append(nextQuery);
/* 1206 */         queryBuf.append(";");
/* 1207 */         argumentSetsInBatchSoFar++;
/*      */       }
/*      */ 
/* 1210 */       if (queryBuf.length() > 0) {
/*      */         try {
/* 1212 */           batchStmt.execute(queryBuf.toString(), 1);
/*      */         } catch (SQLException ex) {
/* 1214 */           sqlEx = handleExceptionForBatch(commandIndex - 1, argumentSetsInBatchSoFar, updateCounts, ex);
/*      */         }
/*      */ 
/* 1218 */         counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
/*      */       }
/*      */ 
/* 1222 */       if (timeoutTask != null) {
/* 1223 */         if (timeoutTask.caughtWhileCancelling != null) {
/* 1224 */           throw timeoutTask.caughtWhileCancelling;
/*      */         }
/*      */ 
/* 1227 */         timeoutTask.cancel();
/* 1228 */         timeoutTask = null;
/*      */       }
/*      */ 
/* 1231 */       if (sqlEx != null) {
/* 1232 */         throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */       }
/*      */ 
/* 1237 */       ex = updateCounts != null ? updateCounts : new int[0];
/*      */     } finally {
/* 1239 */       if (timeoutTask != null) {
/* 1240 */         timeoutTask.cancel();
/*      */       }
/*      */ 
/* 1243 */       resetCancelledState();
/*      */       try
/*      */       {
/* 1246 */         if (batchStmt != null)
/* 1247 */           batchStmt.close();
/*      */       }
/*      */       finally {
/* 1250 */         if (!multiQueriesEnabled)
/* 1251 */           locallyScopedConn.getIO().disableMultiQueries();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int processMultiCountsAndKeys(StatementImpl batchedStatement, int updateCountCounter, int[] updateCounts)
/*      */     throws SQLException
/*      */   {
/* 1260 */     updateCounts[(updateCountCounter++)] = batchedStatement.getUpdateCount();
/*      */ 
/* 1262 */     boolean doGenKeys = this.batchedGeneratedKeys != null;
/*      */ 
/* 1264 */     byte[][] row = (byte[][])null;
/*      */ 
/* 1266 */     if (doGenKeys) {
/* 1267 */       long generatedKey = batchedStatement.getLastInsertID();
/*      */ 
/* 1269 */       row = new byte[1][];
/* 1270 */       row[0] = Long.toString(generatedKey).getBytes();
/* 1271 */       this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */     }
/*      */ 
/* 1275 */     while ((batchedStatement.getMoreResults()) || (batchedStatement.getUpdateCount() != -1)) {
/* 1276 */       updateCounts[(updateCountCounter++)] = batchedStatement.getUpdateCount();
/*      */ 
/* 1278 */       if (doGenKeys) {
/* 1279 */         long generatedKey = batchedStatement.getLastInsertID();
/*      */ 
/* 1281 */         row = new byte[1][];
/* 1282 */         row[0] = Long.toString(generatedKey).getBytes();
/* 1283 */         this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */       }
/*      */     }
/*      */ 
/* 1287 */     return updateCountCounter;
/*      */   }
/*      */ 
/*      */   protected SQLException handleExceptionForBatch(int endOfBatchIndex, int numValuesPerBatch, int[] updateCounts, SQLException ex)
/*      */     throws BatchUpdateException
/*      */   {
/* 1295 */     for (int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; j--)
/* 1296 */       updateCounts[j] = -3;
/*      */     SQLException sqlEx;
/* 1299 */     if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */     {
/* 1303 */       sqlEx = ex;
/*      */     } else {
/* 1305 */       int[] newUpdateCounts = new int[endOfBatchIndex];
/* 1306 */       System.arraycopy(updateCounts, 0, newUpdateCounts, 0, endOfBatchIndex);
/*      */ 
/* 1309 */       throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */     }
/*      */     SQLException sqlEx;
/* 1314 */     return sqlEx;
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery(String sql)
/*      */     throws SQLException
/*      */   {
/* 1330 */     checkClosed();
/*      */ 
/* 1332 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1334 */     synchronized (locallyScopedConn.getMutex()) {
/* 1335 */       this.retrieveGeneratedKeys = false;
/*      */ 
/* 1337 */       resetCancelledState();
/*      */ 
/* 1339 */       checkNullOrEmptyQuery(sql);
/*      */ 
/* 1341 */       boolean doStreaming = createStreamingResultSet();
/*      */ 
/* 1351 */       if ((doStreaming) && (this.connection.getNetTimeoutForStreamingResults() > 0))
/*      */       {
/* 1353 */         executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
/*      */       }
/*      */ 
/* 1357 */       if (this.doEscapeProcessing) {
/* 1358 */         Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), this.connection);
/*      */ 
/* 1361 */         if ((escapedSqlResult instanceof String))
/* 1362 */           sql = (String)escapedSqlResult;
/*      */         else {
/* 1364 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */ 
/* 1368 */       char firstStatementChar = StringUtils.firstNonWsCharUc(sql, findStartOfStatement(sql));
/*      */ 
/* 1371 */       if ((sql.charAt(0) == '/') && 
/* 1372 */         (sql.startsWith("/* ping */"))) {
/* 1373 */         doPingInstead();
/*      */ 
/* 1375 */         return this.results;
/*      */       }
/*      */ 
/* 1379 */       checkForDml(sql, firstStatementChar);
/*      */ 
/* 1381 */       if ((this.results != null) && 
/* 1382 */         (!locallyScopedConn.getHoldResultsOpenOverStatementClose())) {
/* 1383 */         this.results.realClose(false);
/*      */       }
/*      */ 
/* 1387 */       CachedResultSetMetaData cachedMetaData = null;
/*      */ 
/* 1396 */       if (useServerFetch()) {
/* 1397 */         this.results = createResultSetUsingServerFetch(sql);
/*      */ 
/* 1399 */         return this.results;
/*      */       }
/*      */ 
/* 1402 */       CancelTask timeoutTask = null;
/*      */ 
/* 1404 */       String oldCatalog = null;
/*      */       try
/*      */       {
/* 1407 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 1410 */           timeoutTask = new CancelTask(this);
/* 1411 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */ 
/* 1415 */         if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1416 */           oldCatalog = locallyScopedConn.getCatalog();
/* 1417 */           locallyScopedConn.setCatalog(this.currentCatalog);
/*      */         }
/*      */ 
/* 1424 */         Field[] cachedFields = null;
/*      */ 
/* 1426 */         if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1427 */           cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
/*      */ 
/* 1429 */           if (cachedMetaData != null) {
/* 1430 */             cachedFields = cachedMetaData.fields;
/*      */           }
/*      */         }
/*      */ 
/* 1434 */         if (locallyScopedConn.useMaxRows())
/*      */         {
/* 1439 */           if (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1) {
/* 1440 */             this.results = locallyScopedConn.execSQL(this, sql, this.maxRows, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */           }
/*      */           else
/*      */           {
/* 1446 */             if (this.maxRows <= 0) {
/* 1447 */               executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */             }
/*      */             else {
/* 1450 */               executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows);
/*      */             }
/*      */ 
/* 1454 */             this.results = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */ 
/* 1460 */             if (oldCatalog != null)
/* 1461 */               locallyScopedConn.setCatalog(oldCatalog);
/*      */           }
/*      */         }
/*      */         else {
/* 1465 */           this.results = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */         }
/*      */ 
/* 1471 */         if (timeoutTask != null) {
/* 1472 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1473 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 1476 */           timeoutTask.cancel();
/* 1477 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 1480 */         synchronized (this.cancelTimeoutMutex) {
/* 1481 */           if (this.wasCancelled) {
/* 1482 */             SQLException cause = null;
/*      */ 
/* 1484 */             if (this.wasCancelledByTimeout)
/* 1485 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 1487 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 1490 */             resetCancelledState();
/*      */ 
/* 1492 */             throw cause;
/*      */           }
/*      */         }
/*      */       } finally {
/* 1496 */         if (timeoutTask != null) {
/* 1497 */           timeoutTask.cancel();
/*      */         }
/*      */ 
/* 1500 */         if (oldCatalog != null) {
/* 1501 */           locallyScopedConn.setCatalog(oldCatalog);
/*      */         }
/*      */       }
/*      */ 
/* 1505 */       this.lastInsertId = this.results.getUpdateID();
/*      */ 
/* 1507 */       if (cachedMetaData != null) {
/* 1508 */         locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
/*      */       }
/* 1511 */       else if (this.connection.getCacheResultSetMetadata()) {
/* 1512 */         locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
/*      */       }
/*      */ 
/* 1517 */       return this.results;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void doPingInstead() throws SQLException {
/* 1522 */     if (this.pingTarget != null)
/* 1523 */       this.pingTarget.doPing();
/*      */     else {
/* 1525 */       this.connection.ping();
/*      */     }
/*      */ 
/* 1528 */     ResultSetInternalMethods fakeSelectOneResultSet = generatePingResultSet();
/* 1529 */     this.results = fakeSelectOneResultSet;
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods generatePingResultSet() throws SQLException {
/* 1533 */     Field[] fields = { new Field(null, "1", -5, 1) };
/* 1534 */     ArrayList rows = new ArrayList();
/* 1535 */     byte[] colVal = { 49 };
/*      */ 
/* 1537 */     rows.add(new ByteArrayRow(new byte[][] { colVal }, getExceptionInterceptor()));
/*      */ 
/* 1539 */     return (ResultSetInternalMethods)DatabaseMetaData.buildResultSet(fields, rows, this.connection);
/*      */   }
/*      */ 
/*      */   protected void executeSimpleNonQuery(ConnectionImpl c, String nonQuery)
/*      */     throws SQLException
/*      */   {
/* 1545 */     c.execSQL(this, nonQuery, -1, null, 1003, 1007, false, this.currentCatalog, null, false).close();
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql)
/*      */     throws SQLException
/*      */   {
/* 1567 */     return executeUpdate(sql, false, false);
/*      */   }
/*      */ 
/*      */   protected int executeUpdate(String sql, boolean isBatch, boolean returnGeneratedKeys) throws SQLException
/*      */   {
/* 1572 */     checkClosed();
/*      */ 
/* 1574 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1576 */     char firstStatementChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
/*      */ 
/* 1579 */     ResultSetInternalMethods rs = null;
/*      */ 
/* 1581 */     synchronized (locallyScopedConn.getMutex()) {
/* 1582 */       this.retrieveGeneratedKeys = returnGeneratedKeys;
/*      */ 
/* 1584 */       resetCancelledState();
/*      */ 
/* 1586 */       checkNullOrEmptyQuery(sql);
/*      */ 
/* 1588 */       if (this.doEscapeProcessing) {
/* 1589 */         Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.connection.serverSupportsConvertFn(), this.connection);
/*      */ 
/* 1592 */         if ((escapedSqlResult instanceof String))
/* 1593 */           sql = (String)escapedSqlResult;
/*      */         else {
/* 1595 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */ 
/* 1599 */       if (locallyScopedConn.isReadOnly()) {
/* 1600 */         throw SQLError.createSQLException(Messages.getString("Statement.42") + Messages.getString("Statement.43"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1606 */       if (StringUtils.startsWithIgnoreCaseAndWs(sql, "select")) {
/* 1607 */         throw SQLError.createSQLException(Messages.getString("Statement.46"), "01S03", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1612 */       if ((this.results != null) && 
/* 1613 */         (!locallyScopedConn.getHoldResultsOpenOverStatementClose())) {
/* 1614 */         this.results.realClose(false);
/*      */       }
/*      */ 
/* 1622 */       CancelTask timeoutTask = null;
/*      */ 
/* 1624 */       String oldCatalog = null;
/*      */       try
/*      */       {
/* 1627 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 1630 */           timeoutTask = new CancelTask(this);
/* 1631 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */ 
/* 1635 */         if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1636 */           oldCatalog = locallyScopedConn.getCatalog();
/* 1637 */           locallyScopedConn.setCatalog(this.currentCatalog);
/*      */         }
/*      */ 
/* 1643 */         if (locallyScopedConn.useMaxRows()) {
/* 1644 */           executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */         }
/*      */ 
/* 1648 */         rs = locallyScopedConn.execSQL(this, sql, -1, null, 1003, 1007, false, this.currentCatalog, null, isBatch);
/*      */ 
/* 1655 */         if (timeoutTask != null) {
/* 1656 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1657 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 1660 */           timeoutTask.cancel();
/* 1661 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 1664 */         synchronized (this.cancelTimeoutMutex) {
/* 1665 */           if (this.wasCancelled) {
/* 1666 */             SQLException cause = null;
/*      */ 
/* 1668 */             if (this.wasCancelledByTimeout)
/* 1669 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 1671 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 1674 */             resetCancelledState();
/*      */ 
/* 1676 */             throw cause;
/*      */           }
/*      */         }
/*      */       } finally {
/* 1680 */         if (timeoutTask != null) {
/* 1681 */           timeoutTask.cancel();
/*      */         }
/*      */ 
/* 1684 */         if (oldCatalog != null) {
/* 1685 */           locallyScopedConn.setCatalog(oldCatalog);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1690 */     this.results = rs;
/*      */ 
/* 1692 */     rs.setFirstCharOfQuery(firstStatementChar);
/*      */ 
/* 1694 */     this.updateCount = rs.getUpdateCount();
/*      */ 
/* 1696 */     int truncatedUpdateCount = 0;
/*      */ 
/* 1698 */     if (this.updateCount > 2147483647L)
/* 1699 */       truncatedUpdateCount = 2147483647;
/*      */     else {
/* 1701 */       truncatedUpdateCount = (int)this.updateCount;
/*      */     }
/*      */ 
/* 1704 */     this.lastInsertId = rs.getUpdateID();
/*      */ 
/* 1706 */     return truncatedUpdateCount;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, int returnGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/* 1715 */     if (returnGeneratedKeys == 1) {
/* 1716 */       checkClosed();
/*      */ 
/* 1718 */       ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1720 */       synchronized (locallyScopedConn.getMutex())
/*      */       {
/* 1724 */         boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/*      */ 
/* 1726 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1729 */           int i = executeUpdate(sql, false, true);
/*      */ 
/* 1731 */           locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); return i; } finally { locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1736 */     return executeUpdate(sql);
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, int[] generatedKeyIndices)
/*      */     throws SQLException
/*      */   {
/* 1744 */     if ((generatedKeyIndices != null) && (generatedKeyIndices.length > 0)) {
/* 1745 */       checkClosed();
/*      */ 
/* 1747 */       ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1749 */       synchronized (locallyScopedConn.getMutex())
/*      */       {
/* 1753 */         boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/*      */ 
/* 1755 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1758 */           int i = executeUpdate(sql, false, true);
/*      */ 
/* 1760 */           locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); return i; } finally { locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1765 */     return executeUpdate(sql);
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, String[] generatedKeyNames)
/*      */     throws SQLException
/*      */   {
/* 1773 */     if ((generatedKeyNames != null) && (generatedKeyNames.length > 0)) {
/* 1774 */       checkClosed();
/*      */ 
/* 1776 */       ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1778 */       synchronized (locallyScopedConn.getMutex())
/*      */       {
/* 1782 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/* 1784 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1787 */           int i = executeUpdate(sql, false, true);
/*      */ 
/* 1789 */           locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); return i; } finally { locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1794 */     return executeUpdate(sql);
/*      */   }
/*      */ 
/*      */   protected Calendar getCalendarInstanceForSessionOrNew()
/*      */   {
/* 1802 */     if (this.connection != null) {
/* 1803 */       return this.connection.getCalendarInstanceForSessionOrNew();
/*      */     }
/*      */ 
/* 1806 */     return new GregorianCalendar();
/*      */   }
/*      */ 
/*      */   public java.sql.Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 1819 */     return this.connection;
/*      */   }
/*      */ 
/*      */   public int getFetchDirection()
/*      */     throws SQLException
/*      */   {
/* 1831 */     return 1000;
/*      */   }
/*      */ 
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/* 1843 */     return this.fetchSize;
/*      */   }
/*      */ 
/*      */   public synchronized ResultSet getGeneratedKeys()
/*      */     throws SQLException
/*      */   {
/* 1856 */     if (!this.retrieveGeneratedKeys) {
/* 1857 */       throw SQLError.createSQLException(Messages.getString("Statement.GeneratedKeysNotRequested"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1860 */     if (this.batchedGeneratedKeys == null) {
/* 1861 */       if (this.lastQueryIsOnDupKeyUpdate) {
/* 1862 */         return getGeneratedKeysInternal(1);
/*      */       }
/* 1864 */       return getGeneratedKeysInternal();
/*      */     }
/*      */ 
/* 1867 */     Field[] fields = new Field[1];
/* 1868 */     fields[0] = new Field("", "GENERATED_KEY", -5, 17);
/* 1869 */     fields[0].setConnection(this.connection);
/*      */ 
/* 1871 */     return ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(this.batchedGeneratedKeys), this.connection, this, false);
/*      */   }
/*      */ 
/*      */   protected ResultSet getGeneratedKeysInternal()
/*      */     throws SQLException
/*      */   {
/* 1883 */     int numKeys = getUpdateCount();
/* 1884 */     return getGeneratedKeysInternal(numKeys);
/*      */   }
/*      */ 
/*      */   protected synchronized ResultSet getGeneratedKeysInternal(int numKeys) throws SQLException
/*      */   {
/* 1889 */     Field[] fields = new Field[1];
/* 1890 */     fields[0] = new Field("", "GENERATED_KEY", -5, 17);
/* 1891 */     fields[0].setConnection(this.connection);
/* 1892 */     fields[0].setUseOldNameMetadata(true);
/*      */ 
/* 1894 */     ArrayList rowSet = new ArrayList();
/*      */ 
/* 1896 */     long beginAt = getLastInsertID();
/*      */ 
/* 1898 */     if (beginAt < 0L) {
/* 1899 */       fields[0].setUnsigned();
/*      */     }
/*      */ 
/* 1902 */     if (this.results != null) {
/* 1903 */       String serverInfo = this.results.getServerInfo();
/*      */ 
/* 1909 */       if ((numKeys > 0) && (this.results.getFirstCharOfQuery() == 'R') && (serverInfo != null) && (serverInfo.length() > 0))
/*      */       {
/* 1911 */         numKeys = getRecordCountFromInfo(serverInfo);
/*      */       }
/*      */ 
/* 1914 */       if ((beginAt != 0L) && (numKeys > 0)) {
/* 1915 */         for (int i = 0; i < numKeys; i++) {
/* 1916 */           byte[][] row = new byte[1][];
/* 1917 */           if (beginAt > 0L) {
/* 1918 */             row[0] = Long.toString(beginAt).getBytes();
/*      */           } else {
/* 1920 */             byte[] asBytes = new byte[8];
/* 1921 */             asBytes[7] = (byte)(int)(beginAt & 0xFF);
/* 1922 */             asBytes[6] = (byte)(int)(beginAt >>> 8);
/* 1923 */             asBytes[5] = (byte)(int)(beginAt >>> 16);
/* 1924 */             asBytes[4] = (byte)(int)(beginAt >>> 24);
/* 1925 */             asBytes[3] = (byte)(int)(beginAt >>> 32);
/* 1926 */             asBytes[2] = (byte)(int)(beginAt >>> 40);
/* 1927 */             asBytes[1] = (byte)(int)(beginAt >>> 48);
/* 1928 */             asBytes[0] = (byte)(int)(beginAt >>> 56);
/*      */ 
/* 1930 */             BigInteger val = new BigInteger(1, asBytes);
/*      */ 
/* 1932 */             row[0] = val.toString().getBytes();
/*      */           }
/* 1934 */           rowSet.add(new ByteArrayRow(row, getExceptionInterceptor()));
/* 1935 */           beginAt += this.connection.getAutoIncrementIncrement();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1940 */     ResultSetImpl gkRs = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(rowSet), this.connection, this, false);
/*      */ 
/* 1943 */     this.openResults.add(gkRs);
/*      */ 
/* 1945 */     return gkRs;
/*      */   }
/*      */ 
/*      */   protected int getId()
/*      */   {
/* 1954 */     return this.statementId;
/*      */   }
/*      */ 
/*      */   public long getLastInsertID()
/*      */   {
/* 1971 */     return this.lastInsertId;
/*      */   }
/*      */ 
/*      */   public long getLongUpdateCount()
/*      */   {
/* 1987 */     if (this.results == null) {
/* 1988 */       return -1L;
/*      */     }
/*      */ 
/* 1991 */     if (this.results.reallyResult()) {
/* 1992 */       return -1L;
/*      */     }
/*      */ 
/* 1995 */     return this.updateCount;
/*      */   }
/*      */ 
/*      */   public int getMaxFieldSize()
/*      */     throws SQLException
/*      */   {
/* 2010 */     return this.maxFieldSize;
/*      */   }
/*      */ 
/*      */   public int getMaxRows()
/*      */     throws SQLException
/*      */   {
/* 2024 */     if (this.maxRows <= 0) {
/* 2025 */       return 0;
/*      */     }
/*      */ 
/* 2028 */     return this.maxRows;
/*      */   }
/*      */ 
/*      */   public boolean getMoreResults()
/*      */     throws SQLException
/*      */   {
/* 2041 */     return getMoreResults(1);
/*      */   }
/*      */ 
/*      */   public synchronized boolean getMoreResults(int current)
/*      */     throws SQLException
/*      */   {
/* 2049 */     if (this.results == null) {
/* 2050 */       return false;
/*      */     }
/*      */ 
/* 2053 */     boolean streamingMode = createStreamingResultSet();
/*      */ 
/* 2055 */     while ((streamingMode) && 
/* 2056 */       (this.results.reallyResult()) && 
/* 2057 */       (this.results.next()));
/* 2062 */     ResultSetInternalMethods nextResultSet = this.results.getNextResultSet();
/*      */ 
/* 2064 */     switch (current)
/*      */     {
/*      */     case 1:
/* 2067 */       if (this.results == null) break;
/* 2068 */       if (!streamingMode) {
/* 2069 */         this.results.close();
/*      */       }
/*      */ 
/* 2072 */       this.results.clearNextResult(); break;
/*      */     case 3:
/* 2079 */       if (this.results != null) {
/* 2080 */         if (!streamingMode) {
/* 2081 */           this.results.close();
/*      */         }
/*      */ 
/* 2084 */         this.results.clearNextResult();
/*      */       }
/*      */ 
/* 2087 */       closeAllOpenResults();
/*      */ 
/* 2089 */       break;
/*      */     case 2:
/* 2092 */       if (!this.connection.getDontTrackOpenResources()) {
/* 2093 */         this.openResults.add(this.results);
/*      */       }
/*      */ 
/* 2096 */       this.results.clearNextResult();
/*      */ 
/* 2098 */       break;
/*      */     default:
/* 2101 */       throw SQLError.createSQLException(Messages.getString("Statement.19"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2106 */     this.results = nextResultSet;
/*      */ 
/* 2108 */     if (this.results == null) {
/* 2109 */       this.updateCount = -1L;
/* 2110 */       this.lastInsertId = -1L;
/* 2111 */     } else if (this.results.reallyResult()) {
/* 2112 */       this.updateCount = -1L;
/* 2113 */       this.lastInsertId = -1L;
/*      */     } else {
/* 2115 */       this.updateCount = this.results.getUpdateCount();
/* 2116 */       this.lastInsertId = this.results.getUpdateID();
/*      */     }
/*      */ 
/* 2119 */     return (this.results != null) && (this.results.reallyResult());
/*      */   }
/*      */ 
/*      */   public int getQueryTimeout()
/*      */     throws SQLException
/*      */   {
/* 2134 */     return this.timeoutInMillis / 1000;
/*      */   }
/*      */ 
/*      */   private int getRecordCountFromInfo(String serverInfo)
/*      */   {
/* 2146 */     StringBuffer recordsBuf = new StringBuffer();
/* 2147 */     int recordsCount = 0;
/* 2148 */     int duplicatesCount = 0;
/*      */ 
/* 2150 */     char c = '\000';
/*      */ 
/* 2152 */     int length = serverInfo.length();
/* 2153 */     int i = 0;
/*      */ 
/* 2155 */     for (; i < length; i++) {
/* 2156 */       c = serverInfo.charAt(i);
/*      */ 
/* 2158 */       if (Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 2163 */     recordsBuf.append(c);
/* 2164 */     i++;
/*      */ 
/* 2166 */     for (; i < length; i++) {
/* 2167 */       c = serverInfo.charAt(i);
/*      */ 
/* 2169 */       if (!Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/* 2173 */       recordsBuf.append(c);
/*      */     }
/*      */ 
/* 2176 */     recordsCount = Integer.parseInt(recordsBuf.toString());
/*      */ 
/* 2178 */     StringBuffer duplicatesBuf = new StringBuffer();
/*      */ 
/* 2180 */     for (; i < length; i++) {
/* 2181 */       c = serverInfo.charAt(i);
/*      */ 
/* 2183 */       if (Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 2188 */     duplicatesBuf.append(c);
/* 2189 */     i++;
/*      */ 
/* 2191 */     for (; i < length; i++) {
/* 2192 */       c = serverInfo.charAt(i);
/*      */ 
/* 2194 */       if (!Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/* 2198 */       duplicatesBuf.append(c);
/*      */     }
/*      */ 
/* 2201 */     duplicatesCount = Integer.parseInt(duplicatesBuf.toString());
/*      */ 
/* 2203 */     return recordsCount - duplicatesCount;
/*      */   }
/*      */ 
/*      */   public ResultSet getResultSet()
/*      */     throws SQLException
/*      */   {
/* 2216 */     return (this.results != null) && (this.results.reallyResult()) ? this.results : null;
/*      */   }
/*      */ 
/*      */   public int getResultSetConcurrency()
/*      */     throws SQLException
/*      */   {
/* 2229 */     return this.resultSetConcurrency;
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 2236 */     return 1;
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods getResultSetInternal() {
/* 2240 */     return this.results;
/*      */   }
/*      */ 
/*      */   public int getResultSetType()
/*      */     throws SQLException
/*      */   {
/* 2252 */     return this.resultSetType;
/*      */   }
/*      */ 
/*      */   public int getUpdateCount()
/*      */     throws SQLException
/*      */   {
/* 2266 */     if (this.results == null) {
/* 2267 */       return -1;
/*      */     }
/*      */ 
/* 2270 */     if (this.results.reallyResult()) {
/* 2271 */       return -1;
/*      */     }
/*      */ 
/* 2274 */     int truncatedUpdateCount = 0;
/*      */ 
/* 2276 */     if (this.results.getUpdateCount() > 2147483647L)
/* 2277 */       truncatedUpdateCount = 2147483647;
/*      */     else {
/* 2279 */       truncatedUpdateCount = (int)this.results.getUpdateCount();
/*      */     }
/*      */ 
/* 2282 */     return truncatedUpdateCount;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 2307 */     checkClosed();
/*      */ 
/* 2309 */     if ((this.connection != null) && (!this.connection.isClosed()) && (this.connection.versionMeetsMinimum(4, 1, 0)))
/*      */     {
/* 2311 */       SQLWarning pendingWarningsFromServer = SQLError.convertShowWarningsToSQLWarnings(this.connection);
/*      */ 
/* 2314 */       if (this.warningChain != null)
/* 2315 */         this.warningChain.setNextWarning(pendingWarningsFromServer);
/*      */       else {
/* 2317 */         this.warningChain = pendingWarningsFromServer;
/*      */       }
/*      */ 
/* 2320 */       return this.warningChain;
/*      */     }
/*      */ 
/* 2323 */     return this.warningChain;
/*      */   }
/*      */ 
/*      */   protected synchronized void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/* 2337 */     if (this.isClosed) {
/* 2338 */       return;
/*      */     }
/*      */ 
/* 2341 */     if ((this.useUsageAdvisor) && 
/* 2342 */       (!calledExplicitly)) {
/* 2343 */       String message = Messages.getString("Statement.63") + Messages.getString("Statement.64");
/*      */ 
/* 2346 */       this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.currentCatalog, this.connectionId, getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */     }
/*      */ 
/* 2356 */     if (closeOpenResults) {
/* 2357 */       closeOpenResults = !this.holdResultsOpenOverClose;
/*      */     }
/*      */ 
/* 2360 */     if (closeOpenResults) {
/* 2361 */       if (this.results != null) {
/*      */         try
/*      */         {
/* 2364 */           this.results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/*      */       }
/* 2370 */       closeAllOpenResults();
/*      */     }
/*      */ 
/* 2373 */     if (this.connection != null) {
/* 2374 */       if (this.maxRowsChanged) {
/* 2375 */         this.connection.unsetMaxRows(this);
/*      */       }
/*      */ 
/* 2378 */       if (!this.connection.getDontTrackOpenResources()) {
/* 2379 */         this.connection.unregisterStatement(this);
/*      */       }
/*      */     }
/*      */ 
/* 2383 */     this.isClosed = true;
/*      */ 
/* 2385 */     this.results = null;
/* 2386 */     this.connection = null;
/* 2387 */     this.warningChain = null;
/* 2388 */     this.openResults = null;
/* 2389 */     this.batchedGeneratedKeys = null;
/* 2390 */     this.localInfileInputStream = null;
/* 2391 */     this.pingTarget = null;
/*      */   }
/*      */ 
/*      */   public void setCursorName(String name)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setEscapeProcessing(boolean enable)
/*      */     throws SQLException
/*      */   {
/* 2427 */     this.doEscapeProcessing = enable;
/*      */   }
/*      */ 
/*      */   public void setFetchDirection(int direction)
/*      */     throws SQLException
/*      */   {
/* 2444 */     switch (direction) {
/*      */     case 1000:
/*      */     case 1001:
/*      */     case 1002:
/* 2448 */       break;
/*      */     default:
/* 2451 */       throw SQLError.createSQLException(Messages.getString("Statement.5"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFetchSize(int rows)
/*      */     throws SQLException
/*      */   {
/* 2472 */     if (((rows < 0) && (rows != -2147483648)) || ((this.maxRows != 0) && (this.maxRows != -1) && (rows > getMaxRows())))
/*      */     {
/* 2475 */       throw SQLError.createSQLException(Messages.getString("Statement.7"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2480 */     this.fetchSize = rows;
/*      */   }
/*      */ 
/*      */   protected void setHoldResultsOpenOverClose(boolean holdResultsOpenOverClose) {
/* 2484 */     this.holdResultsOpenOverClose = holdResultsOpenOverClose;
/*      */   }
/*      */ 
/*      */   public void setMaxFieldSize(int max)
/*      */     throws SQLException
/*      */   {
/* 2497 */     if (max < 0) {
/* 2498 */       throw SQLError.createSQLException(Messages.getString("Statement.11"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2503 */     int maxBuf = this.connection != null ? this.connection.getMaxAllowedPacket() : MysqlIO.getMaxBuf();
/*      */ 
/* 2506 */     if (max > maxBuf) {
/* 2507 */       throw SQLError.createSQLException(Messages.getString("Statement.13", new Object[] { Constants.longValueOf(maxBuf) }), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2513 */     this.maxFieldSize = max;
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int max)
/*      */     throws SQLException
/*      */   {
/* 2528 */     if ((max > 50000000) || (max < 0)) {
/* 2529 */       throw SQLError.createSQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2536 */     if (max == 0) {
/* 2537 */       max = -1;
/*      */     }
/*      */ 
/* 2540 */     this.maxRows = max;
/* 2541 */     this.maxRowsChanged = true;
/*      */ 
/* 2543 */     if (this.maxRows == -1) {
/* 2544 */       this.connection.unsetMaxRows(this);
/* 2545 */       this.maxRowsChanged = false;
/*      */     }
/*      */     else
/*      */     {
/* 2552 */       this.connection.maxRowsChanged(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setQueryTimeout(int seconds)
/*      */     throws SQLException
/*      */   {
/* 2566 */     if (seconds < 0) {
/* 2567 */       throw SQLError.createSQLException(Messages.getString("Statement.21"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2572 */     this.timeoutInMillis = (seconds * 1000);
/*      */   }
/*      */ 
/*      */   void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 2582 */     this.resultSetConcurrency = concurrencyFlag;
/*      */   }
/*      */ 
/*      */   void setResultSetType(int typeFlag)
/*      */   {
/* 2592 */     this.resultSetType = typeFlag;
/*      */   }
/*      */ 
/*      */   protected void getBatchedGeneratedKeys(java.sql.Statement batchedStatement) throws SQLException {
/* 2596 */     if (this.retrieveGeneratedKeys) {
/* 2597 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 2600 */         rs = batchedStatement.getGeneratedKeys();
/*      */ 
/* 2602 */         while (rs.next())
/* 2603 */           this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */       }
/*      */       finally
/*      */       {
/* 2607 */         if (rs != null)
/* 2608 */           rs.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void getBatchedGeneratedKeys(int maxKeys) throws SQLException
/*      */   {
/* 2615 */     if (this.retrieveGeneratedKeys) {
/* 2616 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 2619 */         if (maxKeys == 0)
/* 2620 */           rs = getGeneratedKeysInternal();
/*      */         else {
/* 2622 */           rs = getGeneratedKeysInternal(maxKeys);
/*      */         }
/* 2624 */         while (rs.next())
/* 2625 */           this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */       }
/*      */       finally
/*      */       {
/* 2629 */         if (rs != null)
/* 2630 */           rs.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean useServerFetch()
/*      */     throws SQLException
/*      */   {
/* 2641 */     return (this.connection.isCursorFetchEnabled()) && (this.fetchSize > 0) && (this.resultSetConcurrency == 1007) && (this.resultSetType == 1003);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isClosed()
/*      */     throws SQLException
/*      */   {
/* 2647 */     return this.isClosed;
/*      */   }
/*      */ 
/*      */   public boolean isPoolable()
/*      */     throws SQLException
/*      */   {
/* 2653 */     return this.isPoolable;
/*      */   }
/*      */ 
/*      */   public void setPoolable(boolean poolable) throws SQLException {
/* 2657 */     this.isPoolable = poolable;
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class iface)
/*      */     throws SQLException
/*      */   {
/* 2676 */     checkClosed();
/*      */ 
/* 2680 */     return iface.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class iface)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2701 */       return Util.cast(iface, this); } catch (ClassCastException cce) {
/*      */     }
/* 2703 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected int findStartOfStatement(String sql)
/*      */   {
/* 2709 */     int statementStartPos = 0;
/*      */ 
/* 2711 */     if (StringUtils.startsWithIgnoreCaseAndWs(sql, "/*")) {
/* 2712 */       statementStartPos = sql.indexOf("*/");
/*      */ 
/* 2714 */       if (statementStartPos == -1)
/* 2715 */         statementStartPos = 0;
/*      */       else
/* 2717 */         statementStartPos += 2;
/*      */     }
/* 2719 */     else if ((StringUtils.startsWithIgnoreCaseAndWs(sql, "--")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "#")))
/*      */     {
/* 2721 */       statementStartPos = sql.indexOf('\n');
/*      */ 
/* 2723 */       if (statementStartPos == -1) {
/* 2724 */         statementStartPos = sql.indexOf('\r');
/*      */ 
/* 2726 */         if (statementStartPos == -1) {
/* 2727 */           statementStartPos = 0;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2732 */     return statementStartPos;
/*      */   }
/*      */ 
/*      */   public synchronized InputStream getLocalInfileInputStream()
/*      */   {
/* 2738 */     return this.localInfileInputStream;
/*      */   }
/*      */ 
/*      */   public synchronized void setLocalInfileInputStream(InputStream stream) {
/* 2742 */     this.localInfileInputStream = stream;
/*      */   }
/*      */ 
/*      */   public synchronized void setPingTarget(PingTarget pingTarget) {
/* 2746 */     this.pingTarget = pingTarget;
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/* 2750 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   protected boolean containsOnDuplicateKeyInString(String sql) {
/* 2754 */     return getOnDuplicateKeyLocation(sql) != -1;
/*      */   }
/*      */ 
/*      */   protected int getOnDuplicateKeyLocation(String sql) {
/* 2758 */     return StringUtils.indexOfIgnoreCaseRespectMarker(0, sql, " ON DUPLICATE KEY UPDATE ", "\"'`", "\"'`", !this.connection.isNoBackslashEscapesSet());
/*      */   }
/*      */ 
/*      */   class CancelTask extends TimerTask
/*      */   {
/*   79 */     long connectionId = 0L;
/*   80 */     SQLException caughtWhileCancelling = null;
/*      */     StatementImpl toCancel;
/*      */ 
/*      */     CancelTask(StatementImpl cancellee)
/*      */       throws SQLException
/*      */     {
/*   84 */       this.connectionId = StatementImpl.this.connection.getIO().getThreadId();
/*   85 */       this.toCancel = cancellee;
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*   90 */       Thread cancelThread = new StatementImpl.1(this);
/*      */ 
/*  146 */       cancelThread.start();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.StatementImpl
 * JD-Core Version:    0.6.0
 */