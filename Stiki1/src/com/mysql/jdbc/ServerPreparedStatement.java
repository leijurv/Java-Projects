/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.List;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ 
/*      */ public class ServerPreparedStatement extends PreparedStatement
/*      */ {
/*      */   private static final Constructor JDBC_4_SPS_CTOR;
/*      */   protected static final int BLOB_STREAM_READ_BUF_SIZE = 8192;
/*      */   private static final byte MAX_DATE_REP_LENGTH = 5;
/*      */   private static final byte MAX_DATETIME_REP_LENGTH = 12;
/*      */   private static final byte MAX_TIME_REP_LENGTH = 13;
/*  272 */   private boolean hasOnDuplicateKeyUpdate = false;
/*      */ 
/*  303 */   private boolean detectedLongParameterSwitch = false;
/*      */   private int fieldCount;
/*  312 */   private boolean invalid = false;
/*      */   private SQLException invalidationException;
/*      */   private boolean isSelectQuery;
/*      */   private Buffer outByteBuffer;
/*      */   private BindValue[] parameterBindings;
/*      */   private Field[] parameterFields;
/*      */   private Field[] resultFields;
/*  332 */   private boolean sendTypesToServer = false;
/*      */   private long serverStatementId;
/*  338 */   private int stringTypeCode = 254;
/*      */   private boolean serverNeedsResetBeforeEachExecution;
/*  579 */   protected boolean isCached = false;
/*      */   private boolean useAutoSlowLog;
/*      */   private Calendar serverTzCalendar;
/*      */   private Calendar defaultTzCalendar;
/* 2748 */   private boolean hasCheckedRewrite = false;
/* 2749 */   private boolean canRewrite = false;
/*      */ 
/* 2801 */   private int locationOfOnDuplicateKeyUpdate = -2;
/*      */ 
/*      */   private void storeTime(Buffer intoBuf, Time tm)
/*      */     throws SQLException
/*      */   {
/*  276 */     intoBuf.ensureCapacity(9);
/*  277 */     intoBuf.writeByte(8);
/*  278 */     intoBuf.writeByte(0);
/*  279 */     intoBuf.writeLong(0L);
/*      */ 
/*  281 */     Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */ 
/*  283 */     synchronized (sessionCalendar) {
/*  284 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try {
/*  286 */         sessionCalendar.setTime(tm);
/*  287 */         intoBuf.writeByte((byte)sessionCalendar.get(11));
/*  288 */         intoBuf.writeByte((byte)sessionCalendar.get(12));
/*  289 */         intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */       }
/*      */       finally
/*      */       {
/*  293 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static ServerPreparedStatement getInstance(ConnectionImpl conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  352 */     if (!Util.isJdbc4())
/*  353 */       return new ServerPreparedStatement(conn, sql, catalog, resultSetType, resultSetConcurrency);
/*      */     Throwable target;
/*      */     try
/*      */     {
/*  358 */       return (ServerPreparedStatement)JDBC_4_SPS_CTOR.newInstance(new Object[] { conn, sql, catalog, Constants.integerValueOf(resultSetType), Constants.integerValueOf(resultSetConcurrency) });
/*      */     }
/*      */     catch (IllegalArgumentException e)
/*      */     {
/*  362 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (InstantiationException e) {
/*  364 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (IllegalAccessException e) {
/*  366 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (InvocationTargetException e) {
/*  368 */       target = e.getTargetException();
/*      */ 
/*  370 */       if ((target instanceof SQLException)) {
/*  371 */         throw ((SQLException)target);
/*      */       }
/*      */     }
/*  374 */     throw new SQLException(target.toString(), "S1000");
/*      */   }
/*      */ 
/*      */   protected ServerPreparedStatement(ConnectionImpl conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  394 */     super(conn, catalog);
/*      */ 
/*  396 */     checkNullOrEmptyQuery(sql);
/*      */ 
/*  398 */     this.hasOnDuplicateKeyUpdate = containsOnDuplicateKeyInString(sql);
/*      */ 
/*  400 */     int startOfStatement = findStartOfStatement(sql);
/*      */ 
/*  402 */     this.firstCharOfStmt = StringUtils.firstAlphaCharUc(sql, startOfStatement);
/*      */ 
/*  404 */     this.isSelectQuery = ('S' == this.firstCharOfStmt);
/*      */ 
/*  406 */     if (this.connection.versionMeetsMinimum(5, 0, 0)) {
/*  407 */       this.serverNeedsResetBeforeEachExecution = (!this.connection.versionMeetsMinimum(5, 0, 3));
/*      */     }
/*      */     else {
/*  410 */       this.serverNeedsResetBeforeEachExecution = (!this.connection.versionMeetsMinimum(4, 1, 10));
/*      */     }
/*      */ 
/*  414 */     this.useAutoSlowLog = this.connection.getAutoSlowLog();
/*  415 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*  416 */     this.hasLimitClause = (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1);
/*      */ 
/*  418 */     String statementComment = this.connection.getStatementComment();
/*      */ 
/*  420 */     this.originalSql = ("/* " + statementComment + " */ " + sql);
/*      */ 
/*  423 */     if (this.connection.versionMeetsMinimum(4, 1, 2))
/*  424 */       this.stringTypeCode = 253;
/*      */     else {
/*  426 */       this.stringTypeCode = 254;
/*      */     }
/*      */     try
/*      */     {
/*  430 */       serverPrepare(sql);
/*      */     } catch (SQLException sqlEx) {
/*  432 */       realClose(false, true);
/*      */ 
/*  434 */       throw sqlEx;
/*      */     } catch (Exception ex) {
/*  436 */       realClose(false, true);
/*      */ 
/*  438 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/*  440 */       sqlEx.initCause(ex);
/*      */ 
/*  442 */       throw sqlEx;
/*      */     }
/*      */ 
/*  445 */     setResultSetType(resultSetType);
/*  446 */     setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/*  448 */     this.parameterTypes = new int[this.parameterCount];
/*      */   }
/*      */ 
/*      */   public synchronized void addBatch()
/*      */     throws SQLException
/*      */   {
/*  460 */     checkClosed();
/*      */ 
/*  462 */     if (this.batchedArgs == null) {
/*  463 */       this.batchedArgs = new ArrayList();
/*      */     }
/*      */ 
/*  466 */     this.batchedArgs.add(new BatchedBindValues(this.parameterBindings));
/*      */   }
/*      */ 
/*      */   protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException
/*      */   {
/*  471 */     if (this.isClosed) {
/*  472 */       return "statement has been closed, no further internal information available";
/*      */     }
/*      */ 
/*  475 */     PreparedStatement pStmtForSub = null;
/*      */     try
/*      */     {
/*  478 */       pStmtForSub = PreparedStatement.getInstance(this.connection, this.originalSql, this.currentCatalog);
/*      */ 
/*  481 */       int numParameters = pStmtForSub.parameterCount;
/*  482 */       int ourNumParameters = this.parameterCount;
/*      */ 
/*  484 */       for (i = 0; (i < numParameters) && (i < ourNumParameters); i++) {
/*  485 */         if (this.parameterBindings[i] != null) {
/*  486 */           if (this.parameterBindings[i].isNull) {
/*  487 */             pStmtForSub.setNull(i + 1, 0);
/*      */           } else {
/*  489 */             BindValue bindValue = this.parameterBindings[i];
/*      */ 
/*  494 */             switch (bindValue.bufferType)
/*      */             {
/*      */             case 1:
/*  497 */               pStmtForSub.setByte(i + 1, bindValue.byteBinding);
/*  498 */               break;
/*      */             case 2:
/*  500 */               pStmtForSub.setShort(i + 1, bindValue.shortBinding);
/*  501 */               break;
/*      */             case 3:
/*  503 */               pStmtForSub.setInt(i + 1, bindValue.intBinding);
/*  504 */               break;
/*      */             case 8:
/*  506 */               pStmtForSub.setLong(i + 1, bindValue.longBinding);
/*  507 */               break;
/*      */             case 4:
/*  509 */               pStmtForSub.setFloat(i + 1, bindValue.floatBinding);
/*  510 */               break;
/*      */             case 5:
/*  512 */               pStmtForSub.setDouble(i + 1, bindValue.doubleBinding);
/*      */ 
/*  514 */               break;
/*      */             case 6:
/*      */             case 7:
/*      */             default:
/*  516 */               pStmtForSub.setObject(i + 1, this.parameterBindings[i].value);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  524 */       i = pStmtForSub.asSql(quoteStreamsAndUnknowns);
/*      */     }
/*      */     finally
/*      */     {
/*      */       int i;
/*  526 */       if (pStmtForSub != null)
/*      */         try {
/*  528 */           pStmtForSub.close();
/*      */         }
/*      */         catch (SQLException sqlEx)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkClosed()
/*      */     throws SQLException
/*      */   {
/*  542 */     if (this.invalid) {
/*  543 */       throw this.invalidationException;
/*      */     }
/*      */ 
/*  546 */     super.checkClosed();
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  553 */     checkClosed();
/*  554 */     clearParametersInternal(true);
/*      */   }
/*      */ 
/*      */   private void clearParametersInternal(boolean clearServerParameters) throws SQLException
/*      */   {
/*  559 */     boolean hadLongData = false;
/*      */ 
/*  561 */     if (this.parameterBindings != null) {
/*  562 */       for (int i = 0; i < this.parameterCount; i++) {
/*  563 */         if ((this.parameterBindings[i] != null) && (this.parameterBindings[i].isLongData))
/*      */         {
/*  565 */           hadLongData = true;
/*      */         }
/*      */ 
/*  568 */         this.parameterBindings[i].reset();
/*      */       }
/*      */     }
/*      */ 
/*  572 */     if ((clearServerParameters) && (hadLongData)) {
/*  573 */       serverResetStatement();
/*      */ 
/*  575 */       this.detectedLongParameterSwitch = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setClosed(boolean flag)
/*      */   {
/*  588 */     this.isClosed = flag;
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/*  595 */     if ((this.isCached) && (!this.isClosed)) {
/*  596 */       clearParameters();
/*      */ 
/*  598 */       this.isClosed = true;
/*      */ 
/*  600 */       this.connection.recachePreparedStatement(this);
/*  601 */       return;
/*      */     }
/*      */ 
/*  604 */     realClose(true, true);
/*      */   }
/*      */ 
/*      */   private void dumpCloseForTestcase() {
/*  608 */     StringBuffer buf = new StringBuffer();
/*  609 */     this.connection.generateConnectionCommentBlock(buf);
/*  610 */     buf.append("DEALLOCATE PREPARE debug_stmt_");
/*  611 */     buf.append(this.statementId);
/*  612 */     buf.append(";\n");
/*      */ 
/*  614 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */ 
/*      */   private void dumpExecuteForTestcase() throws SQLException {
/*  618 */     StringBuffer buf = new StringBuffer();
/*      */ 
/*  620 */     for (int i = 0; i < this.parameterCount; i++) {
/*  621 */       this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  623 */       buf.append("SET @debug_stmt_param");
/*  624 */       buf.append(this.statementId);
/*  625 */       buf.append("_");
/*  626 */       buf.append(i);
/*  627 */       buf.append("=");
/*      */ 
/*  629 */       if (this.parameterBindings[i].isNull)
/*  630 */         buf.append("NULL");
/*      */       else {
/*  632 */         buf.append(this.parameterBindings[i].toString(true));
/*      */       }
/*      */ 
/*  635 */       buf.append(";\n");
/*      */     }
/*      */ 
/*  638 */     this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  640 */     buf.append("EXECUTE debug_stmt_");
/*  641 */     buf.append(this.statementId);
/*      */ 
/*  643 */     if (this.parameterCount > 0) {
/*  644 */       buf.append(" USING ");
/*  645 */       for (int i = 0; i < this.parameterCount; i++) {
/*  646 */         if (i > 0) {
/*  647 */           buf.append(", ");
/*      */         }
/*      */ 
/*  650 */         buf.append("@debug_stmt_param");
/*  651 */         buf.append(this.statementId);
/*  652 */         buf.append("_");
/*  653 */         buf.append(i);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  658 */     buf.append(";\n");
/*      */ 
/*  660 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */ 
/*      */   private void dumpPrepareForTestcase() throws SQLException
/*      */   {
/*  665 */     StringBuffer buf = new StringBuffer(this.originalSql.length() + 64);
/*      */ 
/*  667 */     this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  669 */     buf.append("PREPARE debug_stmt_");
/*  670 */     buf.append(this.statementId);
/*  671 */     buf.append(" FROM \"");
/*  672 */     buf.append(this.originalSql);
/*  673 */     buf.append("\";\n");
/*      */ 
/*  675 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */ 
/*      */   protected int[] executeBatchSerially(int batchTimeout) throws SQLException {
/*  679 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/*  681 */     if (locallyScopedConn == null) {
/*  682 */       checkClosed();
/*      */     }
/*      */ 
/*  685 */     if (locallyScopedConn.isReadOnly()) {
/*  686 */       throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.2") + Messages.getString("ServerPreparedStatement.3"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  692 */     checkClosed();
/*      */     BindValue[] oldBindValues;
/*  694 */     synchronized (locallyScopedConn.getMutex()) {
/*  695 */       clearWarnings();
/*      */ 
/*  699 */       oldBindValues = this.parameterBindings;
/*      */     }
/*      */     try {
/*  702 */       int[] updateCounts = null;
/*      */ 
/*  704 */       if (this.batchedArgs != null) {
/*  705 */         nbrCommands = this.batchedArgs.size();
/*  706 */         updateCounts = new int[nbrCommands];
/*      */ 
/*  708 */         if (this.retrieveGeneratedKeys) {
/*  709 */           this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */         }
/*      */ 
/*  712 */         for (int i = 0; i < nbrCommands; i++) {
/*  713 */           updateCounts[i] = -3;
/*      */         }
/*      */ 
/*  716 */         SQLException sqlEx = null;
/*      */ 
/*  718 */         int commandIndex = 0;
/*      */ 
/*  720 */         BindValue[] previousBindValuesForBatch = null;
/*      */ 
/*  722 */         StatementImpl.CancelTask timeoutTask = null;
/*      */         try
/*      */         {
/*  725 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/*  728 */             timeoutTask = new StatementImpl.CancelTask(this, this);
/*  729 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           }
/*      */ 
/*  733 */           for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*  734 */             Object arg = this.batchedArgs.get(commandIndex);
/*      */ 
/*  736 */             if ((arg instanceof String)) {
/*  737 */               updateCounts[commandIndex] = executeUpdate((String)arg);
/*      */             } else {
/*  739 */               this.parameterBindings = ((BatchedBindValues)arg).batchedParameterValues;
/*      */               try
/*      */               {
/*  746 */                 if (previousBindValuesForBatch != null) {
/*  747 */                   for (int j = 0; j < this.parameterBindings.length; j++) {
/*  748 */                     if (this.parameterBindings[j].bufferType != previousBindValuesForBatch[j].bufferType) {
/*  749 */                       this.sendTypesToServer = true;
/*      */ 
/*  751 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 try
/*      */                 {
/*  757 */                   updateCounts[commandIndex] = executeUpdate(false, true);
/*      */                 } finally {
/*  759 */                   previousBindValuesForBatch = this.parameterBindings;
/*      */                 }
/*      */ 
/*  762 */                 if (this.retrieveGeneratedKeys) {
/*  763 */                   ResultSet rs = null;
/*      */                   try
/*      */                   {
/*  775 */                     rs = getGeneratedKeysInternal();
/*      */ 
/*  777 */                     while (rs.next()) {
/*  778 */                       this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */                     }
/*      */                   }
/*      */                   finally
/*      */                   {
/*  783 */                     if (rs != null)
/*  784 */                       rs.close();
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (SQLException ex) {
/*  789 */                 updateCounts[commandIndex] = -3;
/*      */ 
/*  791 */                 if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */                 {
/*  795 */                   sqlEx = ex;
/*      */                 } else {
/*  797 */                   int[] newUpdateCounts = new int[commandIndex];
/*  798 */                   System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */ 
/*  801 */                   throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  809 */           if (timeoutTask != null) {
/*  810 */             timeoutTask.cancel();
/*      */           }
/*      */ 
/*  813 */           resetCancelledState();
/*      */         }
/*      */ 
/*  816 */         if (sqlEx != null) {
/*  817 */           throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  823 */       int nbrCommands = updateCounts != null ? updateCounts : new int[0]; jsr 16; monitorexit; return nbrCommands;
/*      */     } finally {
/*  825 */       jsr 6; } localObject6 = returnAddress; this.parameterBindings = oldBindValues;
/*  826 */     this.sendTypesToServer = true;
/*      */ 
/*  828 */     clearBatch(); ret;
/*      */ 
/*  830 */     localObject7 = finally;
/*      */ 
/*  830 */     monitorexit; throw localObject7;
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch) throws SQLException
/*      */   {
/*  842 */     this.numberOfExecutions += 1;
/*      */     SQLException sqlEx;
/*      */     try {
/*  846 */       return serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadataFromCache);
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  850 */       if (this.connection.getEnablePacketDebug()) {
/*  851 */         this.connection.getIO().dumpPacketRingBuffer();
/*      */       }
/*      */ 
/*  854 */       if (this.connection.getDumpQueriesOnException()) {
/*  855 */         String extractedSql = toString();
/*  856 */         StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/*  858 */         messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */ 
/*  860 */         messageBuf.append(extractedSql);
/*      */ 
/*  862 */         sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */       }
/*      */ 
/*  866 */       throw sqlEx;
/*      */     } catch (Exception ex) {
/*  868 */       if (this.connection.getEnablePacketDebug()) {
/*  869 */         this.connection.getIO().dumpPacketRingBuffer();
/*      */       }
/*      */ 
/*  872 */       sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/*  875 */       if (this.connection.getDumpQueriesOnException()) {
/*  876 */         String extractedSql = toString();
/*  877 */         StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/*  879 */         messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */ 
/*  881 */         messageBuf.append(extractedSql);
/*      */ 
/*  883 */         sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */       }
/*      */ 
/*  887 */       sqlEx.initCause(ex);
/*      */     }
/*  889 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket()
/*      */     throws SQLException
/*      */   {
/*  897 */     return null;
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths)
/*      */     throws SQLException
/*      */   {
/*  907 */     return null;
/*      */   }
/*      */ 
/*      */   protected BindValue getBinding(int parameterIndex, boolean forLongData)
/*      */     throws SQLException
/*      */   {
/*  921 */     checkClosed();
/*      */ 
/*  923 */     if (this.parameterBindings.length == 0) {
/*  924 */       throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.8"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  929 */     parameterIndex--;
/*      */ 
/*  931 */     if ((parameterIndex < 0) || (parameterIndex >= this.parameterBindings.length))
/*      */     {
/*  933 */       throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString("ServerPreparedStatement.10") + this.parameterBindings.length, "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  941 */     if (this.parameterBindings[parameterIndex] == null) {
/*  942 */       this.parameterBindings[parameterIndex] = new BindValue();
/*      */     }
/*  944 */     else if ((this.parameterBindings[parameterIndex].isLongData) && (!forLongData))
/*      */     {
/*  946 */       this.detectedLongParameterSwitch = true;
/*      */     }
/*      */ 
/*  950 */     this.parameterBindings[parameterIndex].isSet = true;
/*  951 */     this.parameterBindings[parameterIndex].boundBeforeExecutionNum = this.numberOfExecutions;
/*      */ 
/*  953 */     return this.parameterBindings[parameterIndex];
/*      */   }
/*      */ 
/*      */   byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  960 */     BindValue bindValue = getBinding(parameterIndex, false);
/*      */ 
/*  962 */     if (bindValue.isNull)
/*  963 */       return null;
/*  964 */     if (bindValue.isLongData) {
/*  965 */       throw SQLError.notImplemented();
/*      */     }
/*  967 */     if (this.outByteBuffer == null) {
/*  968 */       this.outByteBuffer = new Buffer(this.connection.getNetBufferLength());
/*      */     }
/*      */ 
/*  972 */     this.outByteBuffer.clear();
/*      */ 
/*  974 */     int originalPosition = this.outByteBuffer.getPosition();
/*      */ 
/*  976 */     storeBinding(this.outByteBuffer, bindValue, this.connection.getIO());
/*      */ 
/*  978 */     int newPosition = this.outByteBuffer.getPosition();
/*      */ 
/*  980 */     int length = newPosition - originalPosition;
/*      */ 
/*  982 */     byte[] valueAsBytes = new byte[length];
/*      */ 
/*  984 */     System.arraycopy(this.outByteBuffer.getByteBuffer(), originalPosition, valueAsBytes, 0, length);
/*      */ 
/*  987 */     return valueAsBytes;
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/*  995 */     checkClosed();
/*      */ 
/*  997 */     if (this.resultFields == null) {
/*  998 */       return null;
/*      */     }
/*      */ 
/* 1001 */     return new ResultSetMetaData(this.resultFields, this.connection.getUseOldAliasMetadataBehavior(), getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 1009 */     checkClosed();
/*      */ 
/* 1011 */     if (this.parameterMetaData == null) {
/* 1012 */       this.parameterMetaData = new MysqlParameterMetadata(this.parameterFields, this.parameterCount, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1016 */     return this.parameterMetaData;
/*      */   }
/*      */ 
/*      */   boolean isNull(int paramIndex)
/*      */   {
/* 1023 */     throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.7"));
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/* 1038 */     if (this.isClosed) {
/* 1039 */       return;
/*      */     }
/*      */ 
/* 1042 */     if (this.connection != null) {
/* 1043 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1044 */         dumpCloseForTestcase();
/*      */       }
/*      */ 
/* 1058 */       SQLException exceptionDuringClose = null;
/*      */ 
/* 1060 */       if ((calledExplicitly) && (!this.connection.isClosed())) {
/* 1061 */         synchronized (this.connection.getMutex())
/*      */         {
/*      */           try {
/* 1064 */             MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1066 */             Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1068 */             packet.writeByte(25);
/* 1069 */             packet.writeLong(this.serverStatementId);
/*      */ 
/* 1071 */             mysql.sendCommand(25, null, packet, true, null, 0);
/*      */           }
/*      */           catch (SQLException sqlEx) {
/* 1074 */             exceptionDuringClose = sqlEx;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1079 */       super.realClose(calledExplicitly, closeOpenResults);
/*      */ 
/* 1081 */       clearParametersInternal(false);
/* 1082 */       this.parameterBindings = null;
/*      */ 
/* 1084 */       this.parameterFields = null;
/* 1085 */       this.resultFields = null;
/*      */ 
/* 1087 */       if (exceptionDuringClose != null)
/* 1088 */         throw exceptionDuringClose;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void rePrepare()
/*      */     throws SQLException
/*      */   {
/* 1101 */     this.invalidationException = null;
/*      */     try
/*      */     {
/* 1104 */       serverPrepare(this.originalSql);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1107 */       this.invalidationException = sqlEx;
/*      */     } catch (Exception ex) {
/* 1109 */       this.invalidationException = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/* 1111 */       this.invalidationException.initCause(ex);
/*      */     }
/*      */ 
/* 1114 */     if (this.invalidationException != null) {
/* 1115 */       this.invalid = true;
/*      */ 
/* 1117 */       this.parameterBindings = null;
/*      */ 
/* 1119 */       this.parameterFields = null;
/* 1120 */       this.resultFields = null;
/*      */ 
/* 1122 */       if (this.results != null) {
/*      */         try {
/* 1124 */           this.results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/*      */       }
/* 1130 */       if (this.connection != null) {
/* 1131 */         if (this.maxRowsChanged) {
/* 1132 */           this.connection.unsetMaxRows(this);
/*      */         }
/*      */ 
/* 1135 */         if (!this.connection.getDontTrackOpenResources())
/* 1136 */           this.connection.unregisterStatement(this);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private ResultSetInternalMethods serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/* 1178 */     synchronized (this.connection.getMutex()) {
/* 1179 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1181 */       if (mysql.shouldIntercept()) {
/* 1182 */         ResultSetInternalMethods interceptedResults = mysql.invokeStatementInterceptorsPre(this.originalSql, this, true);
/*      */ 
/* 1185 */         if (interceptedResults != null) {
/* 1186 */           return interceptedResults;
/*      */         }
/*      */       }
/*      */ 
/* 1190 */       if (this.detectedLongParameterSwitch)
/*      */       {
/* 1192 */         boolean firstFound = false;
/* 1193 */         long boundTimeToCheck = 0L;
/*      */ 
/* 1195 */         for (int i = 0; i < this.parameterCount - 1; i++) {
/* 1196 */           if (this.parameterBindings[i].isLongData) {
/* 1197 */             if ((firstFound) && (boundTimeToCheck != this.parameterBindings[i].boundBeforeExecutionNum))
/*      */             {
/* 1199 */               throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.11") + Messages.getString("ServerPreparedStatement.12"), "S1C00", getExceptionInterceptor());
/*      */             }
/*      */ 
/* 1204 */             firstFound = true;
/* 1205 */             boundTimeToCheck = this.parameterBindings[i].boundBeforeExecutionNum;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1213 */         serverResetStatement();
/*      */       }
/*      */ 
/* 1218 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1219 */         if (!this.parameterBindings[i].isSet) {
/* 1220 */           throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.13") + (i + 1) + Messages.getString("ServerPreparedStatement.14"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1230 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1231 */         if (this.parameterBindings[i].isLongData) {
/* 1232 */           serverLongData(i, this.parameterBindings[i]);
/*      */         }
/*      */       }
/*      */ 
/* 1236 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1237 */         dumpExecuteForTestcase();
/*      */       }
/*      */ 
/* 1244 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1246 */       packet.clear();
/* 1247 */       packet.writeByte(23);
/* 1248 */       packet.writeLong(this.serverStatementId);
/*      */ 
/* 1250 */       boolean usingCursor = false;
/*      */ 
/* 1252 */       if (this.connection.versionMeetsMinimum(4, 1, 2))
/*      */       {
/* 1259 */         if ((this.resultFields != null) && (this.connection.isCursorFetchEnabled()) && (getResultSetType() == 1003) && (getResultSetConcurrency() == 1007) && (getFetchSize() > 0))
/*      */         {
/* 1264 */           packet.writeByte(1);
/* 1265 */           usingCursor = true;
/*      */         } else {
/* 1267 */           packet.writeByte(0);
/*      */         }
/*      */ 
/* 1270 */         packet.writeLong(1L);
/*      */       }
/*      */ 
/* 1275 */       int nullCount = (this.parameterCount + 7) / 8;
/*      */ 
/* 1280 */       int nullBitsPosition = packet.getPosition();
/*      */ 
/* 1282 */       for (int i = 0; i < nullCount; i++) {
/* 1283 */         packet.writeByte(0);
/*      */       }
/*      */ 
/* 1286 */       byte[] nullBitsBuffer = new byte[nullCount];
/*      */ 
/* 1289 */       packet.writeByte(this.sendTypesToServer ? 1 : 0);
/*      */ 
/* 1291 */       if (this.sendTypesToServer)
/*      */       {
/* 1296 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1297 */           packet.writeInt(this.parameterBindings[i].bufferType);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1304 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1305 */         if (!this.parameterBindings[i].isLongData) {
/* 1306 */           if (!this.parameterBindings[i].isNull) {
/* 1307 */             storeBinding(packet, this.parameterBindings[i], mysql);
/*      */           }
/*      */           else
/*      */           {
/*      */             int tmp585_584 = (i / 8);
/*      */             byte[] tmp585_578 = nullBitsBuffer; tmp585_578[tmp585_584] = (byte)(tmp585_578[tmp585_584] | 1 << (i & 0x7));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1318 */       int endPosition = packet.getPosition();
/* 1319 */       packet.setPosition(nullBitsPosition);
/* 1320 */       packet.writeBytesNoNull(nullBitsBuffer);
/* 1321 */       packet.setPosition(endPosition);
/*      */ 
/* 1323 */       long begin = 0L;
/*      */ 
/* 1325 */       boolean logSlowQueries = this.connection.getLogSlowQueries();
/* 1326 */       boolean gatherPerformanceMetrics = this.connection.getGatherPerformanceMetrics();
/*      */ 
/* 1329 */       if ((this.profileSQL) || (logSlowQueries) || (gatherPerformanceMetrics)) {
/* 1330 */         begin = mysql.getCurrentTimeNanosOrMillis();
/*      */       }
/*      */ 
/* 1333 */       resetCancelledState();
/*      */ 
/* 1335 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/* 1338 */         if ((this.connection.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (this.connection.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 1341 */           timeoutTask = new StatementImpl.CancelTask(this, this);
/* 1342 */           this.connection.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */ 
/* 1346 */         Buffer resultPacket = mysql.sendCommand(23, null, packet, false, null, 0);
/*      */ 
/* 1349 */         long queryEndTime = 0L;
/*      */ 
/* 1351 */         if ((logSlowQueries) || (gatherPerformanceMetrics) || (this.profileSQL)) {
/* 1352 */           queryEndTime = mysql.getCurrentTimeNanosOrMillis();
/*      */         }
/*      */ 
/* 1355 */         if (timeoutTask != null) {
/* 1356 */           timeoutTask.cancel();
/*      */ 
/* 1358 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1359 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 1362 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 1365 */         synchronized (this.cancelTimeoutMutex) {
/* 1366 */           if (this.wasCancelled) {
/* 1367 */             SQLException cause = null;
/*      */ 
/* 1369 */             if (this.wasCancelledByTimeout)
/* 1370 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 1372 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 1375 */             resetCancelledState();
/*      */ 
/* 1377 */             throw cause;
/*      */           }
/*      */         }
/*      */ 
/* 1381 */         boolean queryWasSlow = false;
/*      */ 
/* 1383 */         if ((logSlowQueries) || (gatherPerformanceMetrics)) {
/* 1384 */           long elapsedTime = queryEndTime - begin;
/*      */ 
/* 1386 */           if (logSlowQueries) {
/* 1387 */             if (this.useAutoSlowLog) {
/* 1388 */               queryWasSlow = elapsedTime > this.connection.getSlowQueryThresholdMillis();
/*      */             } else {
/* 1390 */               queryWasSlow = this.connection.isAbonormallyLongQuery(elapsedTime);
/*      */ 
/* 1392 */               this.connection.reportQueryTime(elapsedTime);
/*      */             }
/*      */           }
/*      */ 
/* 1396 */           if (queryWasSlow)
/*      */           {
/* 1398 */             StringBuffer mesgBuf = new StringBuffer(48 + this.originalSql.length());
/*      */ 
/* 1400 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.15"));
/*      */ 
/* 1402 */             mesgBuf.append(mysql.getSlowQueryThreshold());
/* 1403 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.15a"));
/*      */ 
/* 1405 */             mesgBuf.append(elapsedTime);
/* 1406 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.16"));
/*      */ 
/* 1409 */             mesgBuf.append("as prepared: ");
/* 1410 */             mesgBuf.append(this.originalSql);
/* 1411 */             mesgBuf.append("\n\n with parameters bound:\n\n");
/* 1412 */             mesgBuf.append(asSql(true));
/*      */ 
/* 1414 */             this.eventSink.consumeEvent(new ProfilerEvent(6, "", this.currentCatalog, this.connection.getId(), getId(), 0, System.currentTimeMillis(), elapsedTime, mysql.getQueryTimingUnits(), null, new Throwable(), mesgBuf.toString()));
/*      */           }
/*      */ 
/* 1424 */           if (gatherPerformanceMetrics) {
/* 1425 */             this.connection.registerQueryExecutionTime(elapsedTime);
/*      */           }
/*      */         }
/*      */ 
/* 1429 */         this.connection.incrementNumberOfPreparedExecutes();
/*      */ 
/* 1431 */         if (this.profileSQL) {
/* 1432 */           this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 1435 */           this.eventSink.consumeEvent(new ProfilerEvent(4, "", this.currentCatalog, this.connectionId, this.statementId, -1, System.currentTimeMillis(), (int)(mysql.getCurrentTimeNanosOrMillis() - begin), mysql.getQueryTimingUnits(), null, new Throwable(), truncateQueryToLog(asSql(true))));
/*      */         }
/*      */ 
/* 1445 */         ResultSetInternalMethods rs = mysql.readAllResults(this, maxRowsToRetrieve, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, resultPacket, true, this.fieldCount, metadataFromCache);
/*      */ 
/* 1451 */         if (mysql.shouldIntercept()) {
/* 1452 */           ResultSetInternalMethods interceptedResults = mysql.invokeStatementInterceptorsPost(this.originalSql, this, rs, true, null);
/*      */ 
/* 1455 */           if (interceptedResults != null) {
/* 1456 */             rs = interceptedResults;
/*      */           }
/*      */         }
/*      */ 
/* 1460 */         if (this.profileSQL) {
/* 1461 */           long fetchEndTime = mysql.getCurrentTimeNanosOrMillis();
/*      */ 
/* 1463 */           this.eventSink.consumeEvent(new ProfilerEvent(5, "", this.currentCatalog, this.connection.getId(), getId(), 0, System.currentTimeMillis(), fetchEndTime - queryEndTime, mysql.getQueryTimingUnits(), null, new Throwable(), null));
/*      */         }
/*      */ 
/* 1472 */         if ((queryWasSlow) && (this.connection.getExplainSlowQueries())) {
/* 1473 */           queryAsString = asSql(true);
/*      */ 
/* 1475 */           mysql.explainSlowQuery(((String)queryAsString).getBytes(), (String)queryAsString);
/*      */         }
/*      */ 
/* 1479 */         if ((!createStreamingResultSet) && (this.serverNeedsResetBeforeEachExecution))
/*      */         {
/* 1481 */           serverResetStatement();
/*      */         }
/*      */ 
/* 1485 */         this.sendTypesToServer = false;
/* 1486 */         this.results = rs;
/*      */ 
/* 1488 */         if (mysql.hadWarnings()) {
/* 1489 */           mysql.scanForAndThrowDataTruncation();
/*      */         }
/*      */ 
/* 1492 */         Object queryAsString = rs;
/*      */ 
/* 1500 */         if (timeoutTask != null)
/* 1501 */           timeoutTask.cancel(); return queryAsString;
/*      */       }
/*      */       catch (SQLException sqlEx)
/*      */       {
/* 1494 */         if (mysql.shouldIntercept()) {
/* 1495 */           mysql.invokeStatementInterceptorsPost(this.originalSql, this, null, true, sqlEx);
/*      */         }
/*      */ 
/* 1498 */         throw sqlEx;
/*      */       } finally {
/* 1500 */         if (timeoutTask != null)
/* 1501 */           timeoutTask.cancel();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverLongData(int parameterIndex, BindValue longData)
/*      */     throws SQLException
/*      */   {
/* 1536 */     synchronized (this.connection.getMutex()) {
/* 1537 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1539 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1541 */       Object value = longData.value;
/*      */ 
/* 1543 */       if ((value instanceof byte[])) {
/* 1544 */         packet.clear();
/* 1545 */         packet.writeByte(24);
/* 1546 */         packet.writeLong(this.serverStatementId);
/* 1547 */         packet.writeInt(parameterIndex);
/*      */ 
/* 1549 */         packet.writeBytesNoNull((byte[])longData.value);
/*      */ 
/* 1551 */         mysql.sendCommand(24, null, packet, true, null, 0);
/*      */       }
/* 1553 */       else if ((value instanceof InputStream)) {
/* 1554 */         storeStream(mysql, parameterIndex, packet, (InputStream)value);
/* 1555 */       } else if ((value instanceof Blob)) {
/* 1556 */         storeStream(mysql, parameterIndex, packet, ((Blob)value).getBinaryStream());
/*      */       }
/* 1558 */       else if ((value instanceof Reader)) {
/* 1559 */         storeReader(mysql, parameterIndex, packet, (Reader)value);
/*      */       } else {
/* 1561 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverPrepare(String sql)
/*      */     throws SQLException
/*      */   {
/* 1570 */     synchronized (this.connection.getMutex()) {
/* 1571 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1573 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1574 */         dumpPrepareForTestcase();
/*      */       }
/*      */       try
/*      */       {
/* 1578 */         long begin = 0L;
/*      */ 
/* 1580 */         if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA"))
/* 1581 */           this.isLoadDataQuery = true;
/*      */         else {
/* 1583 */           this.isLoadDataQuery = false;
/*      */         }
/*      */ 
/* 1586 */         if (this.connection.getProfileSql()) {
/* 1587 */           begin = System.currentTimeMillis();
/*      */         }
/*      */ 
/* 1590 */         String characterEncoding = null;
/* 1591 */         String connectionEncoding = this.connection.getEncoding();
/*      */ 
/* 1593 */         if ((!this.isLoadDataQuery) && (this.connection.getUseUnicode()) && (connectionEncoding != null))
/*      */         {
/* 1595 */           characterEncoding = connectionEncoding;
/*      */         }
/*      */ 
/* 1598 */         Buffer prepareResultPacket = mysql.sendCommand(22, sql, null, false, characterEncoding, 0);
/*      */ 
/* 1602 */         if (this.connection.versionMeetsMinimum(4, 1, 1))
/*      */         {
/* 1607 */           prepareResultPacket.setPosition(1);
/*      */         }
/*      */         else
/*      */         {
/* 1611 */           prepareResultPacket.setPosition(0);
/*      */         }
/*      */ 
/* 1614 */         this.serverStatementId = prepareResultPacket.readLong();
/* 1615 */         this.fieldCount = prepareResultPacket.readInt();
/* 1616 */         this.parameterCount = prepareResultPacket.readInt();
/* 1617 */         this.parameterBindings = new BindValue[this.parameterCount];
/*      */ 
/* 1619 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1620 */           this.parameterBindings[i] = new BindValue();
/*      */         }
/*      */ 
/* 1623 */         this.connection.incrementNumberOfPrepares();
/*      */ 
/* 1625 */         if (this.profileSQL) {
/* 1626 */           this.eventSink.consumeEvent(new ProfilerEvent(2, "", this.currentCatalog, this.connectionId, this.statementId, -1, System.currentTimeMillis(), mysql.getCurrentTimeNanosOrMillis() - begin, mysql.getQueryTimingUnits(), null, new Throwable(), truncateQueryToLog(sql)));
/*      */         }
/*      */ 
/* 1636 */         if ((this.parameterCount > 0) && 
/* 1637 */           (this.connection.versionMeetsMinimum(4, 1, 2)) && (!mysql.isVersion(5, 0, 0)))
/*      */         {
/* 1639 */           this.parameterFields = new Field[this.parameterCount];
/*      */ 
/* 1641 */           Buffer metaDataPacket = mysql.readPacket();
/*      */ 
/* 1643 */           int i = 0;
/*      */ 
/* 1646 */           while ((!metaDataPacket.isLastDataPacket()) && (i < this.parameterCount)) {
/* 1647 */             this.parameterFields[(i++)] = mysql.unpackField(metaDataPacket, false);
/*      */ 
/* 1649 */             metaDataPacket = mysql.readPacket();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1654 */         if (this.fieldCount > 0) {
/* 1655 */           this.resultFields = new Field[this.fieldCount];
/*      */ 
/* 1657 */           Buffer fieldPacket = mysql.readPacket();
/*      */ 
/* 1659 */           int i = 0;
/*      */ 
/* 1663 */           while ((!fieldPacket.isLastDataPacket()) && (i < this.fieldCount)) {
/* 1664 */             this.resultFields[(i++)] = mysql.unpackField(fieldPacket, false);
/*      */ 
/* 1666 */             fieldPacket = mysql.readPacket();
/*      */           }
/*      */         }
/*      */       } catch (SQLException sqlEx) {
/* 1670 */         if (this.connection.getDumpQueriesOnException()) {
/* 1671 */           StringBuffer messageBuf = new StringBuffer(this.originalSql.length() + 32);
/*      */ 
/* 1673 */           messageBuf.append("\n\nQuery being prepared when exception was thrown:\n\n");
/*      */ 
/* 1675 */           messageBuf.append(this.originalSql);
/*      */ 
/* 1677 */           sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */ 
/* 1681 */         throw sqlEx;
/*      */       }
/*      */       finally
/*      */       {
/* 1686 */         this.connection.getIO().clearInputStream();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private String truncateQueryToLog(String sql) {
/* 1692 */     String query = null;
/*      */ 
/* 1694 */     if (sql.length() > this.connection.getMaxQuerySizeToLog()) {
/* 1695 */       StringBuffer queryBuf = new StringBuffer(this.connection.getMaxQuerySizeToLog() + 12);
/*      */ 
/* 1697 */       queryBuf.append(sql.substring(0, this.connection.getMaxQuerySizeToLog()));
/* 1698 */       queryBuf.append(Messages.getString("MysqlIO.25"));
/*      */ 
/* 1700 */       query = queryBuf.toString();
/*      */     } else {
/* 1702 */       query = sql;
/*      */     }
/*      */ 
/* 1705 */     return query;
/*      */   }
/*      */ 
/*      */   private void serverResetStatement() throws SQLException {
/* 1709 */     synchronized (this.connection.getMutex())
/*      */     {
/* 1711 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1713 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1715 */       packet.clear();
/* 1716 */       packet.writeByte(26);
/* 1717 */       packet.writeLong(this.serverStatementId);
/*      */       try
/*      */       {
/* 1720 */         mysql.sendCommand(26, null, packet, !this.connection.versionMeetsMinimum(4, 1, 2), null, 0);
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 1723 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/* 1725 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/* 1727 */         sqlEx.initCause(ex);
/*      */ 
/* 1729 */         throw sqlEx;
/*      */       } finally {
/* 1731 */         mysql.clearInputStream();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setArray(int i, Array x)
/*      */     throws SQLException
/*      */   {
/* 1740 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1749 */     checkClosed();
/*      */ 
/* 1751 */     if (x == null) {
/* 1752 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1754 */       BindValue binding = getBinding(parameterIndex, true);
/* 1755 */       setType(binding, 252);
/*      */ 
/* 1757 */       binding.value = x;
/* 1758 */       binding.isNull = false;
/* 1759 */       binding.isLongData = true;
/*      */ 
/* 1761 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1762 */         binding.bindLength = length;
/*      */       else
/* 1764 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1774 */     checkClosed();
/*      */ 
/* 1776 */     if (x == null) {
/* 1777 */       setNull(parameterIndex, 3);
/*      */     }
/*      */     else {
/* 1780 */       BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 1782 */       if (this.connection.versionMeetsMinimum(5, 0, 3))
/* 1783 */         setType(binding, 246);
/*      */       else {
/* 1785 */         setType(binding, this.stringTypeCode);
/*      */       }
/*      */ 
/* 1788 */       binding.value = StringUtils.fixDecimalExponent(StringUtils.consistentToString(x));
/*      */ 
/* 1790 */       binding.isNull = false;
/* 1791 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1801 */     checkClosed();
/*      */ 
/* 1803 */     if (x == null) {
/* 1804 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1806 */       BindValue binding = getBinding(parameterIndex, true);
/* 1807 */       setType(binding, 252);
/*      */ 
/* 1809 */       binding.value = x;
/* 1810 */       binding.isNull = false;
/* 1811 */       binding.isLongData = true;
/*      */ 
/* 1813 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1814 */         binding.bindLength = length;
/*      */       else
/* 1816 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, Blob x)
/*      */     throws SQLException
/*      */   {
/* 1825 */     checkClosed();
/*      */ 
/* 1827 */     if (x == null) {
/* 1828 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1830 */       BindValue binding = getBinding(parameterIndex, true);
/* 1831 */       setType(binding, 252);
/*      */ 
/* 1833 */       binding.value = x;
/* 1834 */       binding.isNull = false;
/* 1835 */       binding.isLongData = true;
/*      */ 
/* 1837 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1838 */         binding.bindLength = x.length();
/*      */       else
/* 1840 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBoolean(int parameterIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1849 */     setByte(parameterIndex, x ? 1 : 0);
/*      */   }
/*      */ 
/*      */   public void setByte(int parameterIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1856 */     checkClosed();
/*      */ 
/* 1858 */     BindValue binding = getBinding(parameterIndex, false);
/* 1859 */     setType(binding, 1);
/*      */ 
/* 1861 */     binding.value = null;
/* 1862 */     binding.byteBinding = x;
/* 1863 */     binding.isNull = false;
/* 1864 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setBytes(int parameterIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1871 */     checkClosed();
/*      */ 
/* 1873 */     if (x == null) {
/* 1874 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1876 */       BindValue binding = getBinding(parameterIndex, false);
/* 1877 */       setType(binding, 253);
/*      */ 
/* 1879 */       binding.value = x;
/* 1880 */       binding.isNull = false;
/* 1881 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1891 */     checkClosed();
/*      */ 
/* 1893 */     if (reader == null) {
/* 1894 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1896 */       BindValue binding = getBinding(parameterIndex, true);
/* 1897 */       setType(binding, 252);
/*      */ 
/* 1899 */       binding.value = reader;
/* 1900 */       binding.isNull = false;
/* 1901 */       binding.isLongData = true;
/*      */ 
/* 1903 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1904 */         binding.bindLength = length;
/*      */       else
/* 1906 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Clob x)
/*      */     throws SQLException
/*      */   {
/* 1915 */     checkClosed();
/*      */ 
/* 1917 */     if (x == null) {
/* 1918 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1920 */       BindValue binding = getBinding(parameterIndex, true);
/* 1921 */       setType(binding, 252);
/*      */ 
/* 1923 */       binding.value = x.getCharacterStream();
/* 1924 */       binding.isNull = false;
/* 1925 */       binding.isLongData = true;
/*      */ 
/* 1927 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1928 */         binding.bindLength = x.length();
/*      */       else
/* 1930 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x)
/*      */     throws SQLException
/*      */   {
/* 1948 */     setDate(parameterIndex, x, null);
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1967 */     if (x == null) {
/* 1968 */       setNull(parameterIndex, 91);
/*      */     } else {
/* 1970 */       BindValue binding = getBinding(parameterIndex, false);
/* 1971 */       setType(binding, 10);
/*      */ 
/* 1973 */       binding.value = x;
/* 1974 */       binding.isNull = false;
/* 1975 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDouble(int parameterIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 1983 */     checkClosed();
/*      */ 
/* 1985 */     if ((!this.connection.getAllowNanAndInf()) && ((x == (1.0D / 0.0D)) || (x == (-1.0D / 0.0D)) || (Double.isNaN(x))))
/*      */     {
/* 1988 */       throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1994 */     BindValue binding = getBinding(parameterIndex, false);
/* 1995 */     setType(binding, 5);
/*      */ 
/* 1997 */     binding.value = null;
/* 1998 */     binding.doubleBinding = x;
/* 1999 */     binding.isNull = false;
/* 2000 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setFloat(int parameterIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 2007 */     checkClosed();
/*      */ 
/* 2009 */     BindValue binding = getBinding(parameterIndex, false);
/* 2010 */     setType(binding, 4);
/*      */ 
/* 2012 */     binding.value = null;
/* 2013 */     binding.floatBinding = x;
/* 2014 */     binding.isNull = false;
/* 2015 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setInt(int parameterIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 2022 */     checkClosed();
/*      */ 
/* 2024 */     BindValue binding = getBinding(parameterIndex, false);
/* 2025 */     setType(binding, 3);
/*      */ 
/* 2027 */     binding.value = null;
/* 2028 */     binding.intBinding = x;
/* 2029 */     binding.isNull = false;
/* 2030 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setLong(int parameterIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 2037 */     checkClosed();
/*      */ 
/* 2039 */     BindValue binding = getBinding(parameterIndex, false);
/* 2040 */     setType(binding, 8);
/*      */ 
/* 2042 */     binding.value = null;
/* 2043 */     binding.longBinding = x;
/* 2044 */     binding.isNull = false;
/* 2045 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 2052 */     checkClosed();
/*      */ 
/* 2054 */     BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 2060 */     if (binding.bufferType == 0) {
/* 2061 */       setType(binding, 6);
/*      */     }
/*      */ 
/* 2064 */     binding.value = null;
/* 2065 */     binding.isNull = true;
/* 2066 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 2074 */     checkClosed();
/*      */ 
/* 2076 */     BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 2082 */     if (binding.bufferType == 0) {
/* 2083 */       setType(binding, 6);
/*      */     }
/*      */ 
/* 2086 */     binding.value = null;
/* 2087 */     binding.isNull = true;
/* 2088 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setRef(int i, Ref x)
/*      */     throws SQLException
/*      */   {
/* 2095 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void setShort(int parameterIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 2102 */     checkClosed();
/*      */ 
/* 2104 */     BindValue binding = getBinding(parameterIndex, false);
/* 2105 */     setType(binding, 2);
/*      */ 
/* 2107 */     binding.value = null;
/* 2108 */     binding.shortBinding = x;
/* 2109 */     binding.isNull = false;
/* 2110 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 2117 */     checkClosed();
/*      */ 
/* 2119 */     if (x == null) {
/* 2120 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 2122 */       BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 2124 */       setType(binding, this.stringTypeCode);
/*      */ 
/* 2126 */       binding.value = x;
/* 2127 */       binding.isNull = false;
/* 2128 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 2145 */     setTimeInternal(parameterIndex, x, null, this.connection.getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2165 */     setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 2186 */     if (x == null) {
/* 2187 */       setNull(parameterIndex, 92);
/*      */     } else {
/* 2189 */       BindValue binding = getBinding(parameterIndex, false);
/* 2190 */       setType(binding, 11);
/*      */ 
/* 2192 */       if (!this.useLegacyDatetimeCode) {
/* 2193 */         binding.value = x;
/*      */       } else {
/* 2195 */         Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */ 
/* 2197 */         synchronized (sessionCalendar) {
/* 2198 */           binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2207 */       binding.isNull = false;
/* 2208 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2226 */     setTimestampInternal(parameterIndex, x, null, this.connection.getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2245 */     setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   protected void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 2252 */     if (x == null) {
/* 2253 */       setNull(parameterIndex, 93);
/*      */     } else {
/* 2255 */       BindValue binding = getBinding(parameterIndex, false);
/* 2256 */       setType(binding, 12);
/*      */ 
/* 2258 */       if (!this.useLegacyDatetimeCode) {
/* 2259 */         binding.value = x;
/*      */       } else {
/* 2261 */         Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 2265 */         synchronized (sessionCalendar) {
/* 2266 */           binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         }
/*      */ 
/* 2274 */         binding.isNull = false;
/* 2275 */         binding.isLongData = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setType(BindValue oldValue, int bufferType) {
/* 2281 */     if (oldValue.bufferType != bufferType) {
/* 2282 */       this.sendTypesToServer = true;
/*      */     }
/*      */ 
/* 2285 */     oldValue.bufferType = bufferType;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 2309 */     checkClosed();
/*      */ 
/* 2311 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void setURL(int parameterIndex, URL x)
/*      */     throws SQLException
/*      */   {
/* 2318 */     checkClosed();
/*      */ 
/* 2320 */     setString(parameterIndex, x.toString());
/*      */   }
/*      */ 
/*      */   private void storeBinding(Buffer packet, BindValue bindValue, MysqlIO mysql)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2337 */       Object value = bindValue.value;
/*      */ 
/* 2342 */       switch (bindValue.bufferType)
/*      */       {
/*      */       case 1:
/* 2345 */         packet.writeByte(bindValue.byteBinding);
/* 2346 */         return;
/*      */       case 2:
/* 2348 */         packet.ensureCapacity(2);
/* 2349 */         packet.writeInt(bindValue.shortBinding);
/* 2350 */         return;
/*      */       case 3:
/* 2352 */         packet.ensureCapacity(4);
/* 2353 */         packet.writeLong(bindValue.intBinding);
/* 2354 */         return;
/*      */       case 8:
/* 2356 */         packet.ensureCapacity(8);
/* 2357 */         packet.writeLongLong(bindValue.longBinding);
/* 2358 */         return;
/*      */       case 4:
/* 2360 */         packet.ensureCapacity(4);
/* 2361 */         packet.writeFloat(bindValue.floatBinding);
/* 2362 */         return;
/*      */       case 5:
/* 2364 */         packet.ensureCapacity(8);
/* 2365 */         packet.writeDouble(bindValue.doubleBinding);
/* 2366 */         return;
/*      */       case 11:
/* 2368 */         storeTime(packet, (Time)value);
/* 2369 */         return;
/*      */       case 7:
/*      */       case 10:
/*      */       case 12:
/* 2373 */         storeDateTime(packet, (java.util.Date)value, mysql, bindValue.bufferType);
/* 2374 */         return;
/*      */       case 0:
/*      */       case 15:
/*      */       case 246:
/*      */       case 253:
/*      */       case 254:
/* 2380 */         if ((value instanceof byte[]))
/* 2381 */           packet.writeLenBytes((byte[])value);
/* 2382 */         else if (!this.isLoadDataQuery) {
/* 2383 */           packet.writeLenString((String)value, this.charEncoding, this.connection.getServerCharacterEncoding(), this.charConverter, this.connection.parserKnowsUnicode(), this.connection);
/*      */         }
/*      */         else
/*      */         {
/* 2389 */           packet.writeLenBytes(((String)value).getBytes());
/*      */         }
/*      */ 
/* 2392 */         return;
/*      */       }
/*      */     }
/*      */     catch (UnsupportedEncodingException uEE)
/*      */     {
/* 2397 */       throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.22") + this.connection.getEncoding() + "'", "S1000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeDateTime412AndOlder(Buffer intoBuf, java.util.Date dt, int bufferType)
/*      */     throws SQLException
/*      */   {
/* 2407 */     Calendar sessionCalendar = null;
/*      */ 
/* 2409 */     if (!this.useLegacyDatetimeCode) {
/* 2410 */       if (bufferType == 10)
/* 2411 */         sessionCalendar = getDefaultTzCalendar();
/*      */       else
/* 2413 */         sessionCalendar = getServerTzCalendar();
/*      */     }
/*      */     else {
/* 2416 */       sessionCalendar = ((dt instanceof Timestamp)) && (this.connection.getUseJDBCCompliantTimezoneShift()) ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */     }
/*      */ 
/* 2421 */     synchronized (sessionCalendar) {
/* 2422 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try
/*      */       {
/* 2425 */         intoBuf.ensureCapacity(8);
/* 2426 */         intoBuf.writeByte(7);
/*      */ 
/* 2428 */         sessionCalendar.setTime(dt);
/*      */ 
/* 2430 */         int year = sessionCalendar.get(1);
/* 2431 */         int month = sessionCalendar.get(2) + 1;
/* 2432 */         int date = sessionCalendar.get(5);
/*      */ 
/* 2434 */         intoBuf.writeInt(year);
/* 2435 */         intoBuf.writeByte((byte)month);
/* 2436 */         intoBuf.writeByte((byte)date);
/*      */ 
/* 2438 */         if ((dt instanceof java.sql.Date)) {
/* 2439 */           intoBuf.writeByte(0);
/* 2440 */           intoBuf.writeByte(0);
/* 2441 */           intoBuf.writeByte(0);
/*      */         } else {
/* 2443 */           intoBuf.writeByte((byte)sessionCalendar.get(11));
/*      */ 
/* 2445 */           intoBuf.writeByte((byte)sessionCalendar.get(12));
/*      */ 
/* 2447 */           intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */         }
/*      */       }
/*      */       finally {
/* 2451 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeDateTime(Buffer intoBuf, java.util.Date dt, MysqlIO mysql, int bufferType) throws SQLException
/*      */   {
/* 2458 */     if (this.connection.versionMeetsMinimum(4, 1, 3))
/* 2459 */       storeDateTime413AndNewer(intoBuf, dt, bufferType);
/*      */     else
/* 2461 */       storeDateTime412AndOlder(intoBuf, dt, bufferType);
/*      */   }
/*      */ 
/*      */   private void storeDateTime413AndNewer(Buffer intoBuf, java.util.Date dt, int bufferType)
/*      */     throws SQLException
/*      */   {
/* 2467 */     Calendar sessionCalendar = null;
/*      */ 
/* 2469 */     if (!this.useLegacyDatetimeCode) {
/* 2470 */       if (bufferType == 10)
/* 2471 */         sessionCalendar = getDefaultTzCalendar();
/*      */       else
/* 2473 */         sessionCalendar = getServerTzCalendar();
/*      */     }
/*      */     else {
/* 2476 */       sessionCalendar = ((dt instanceof Timestamp)) && (this.connection.getUseJDBCCompliantTimezoneShift()) ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */     }
/*      */ 
/* 2481 */     synchronized (sessionCalendar) {
/* 2482 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try
/*      */       {
/* 2485 */         sessionCalendar.setTime(dt);
/*      */ 
/* 2487 */         if ((dt instanceof java.sql.Date)) {
/* 2488 */           sessionCalendar.set(11, 0);
/* 2489 */           sessionCalendar.set(12, 0);
/* 2490 */           sessionCalendar.set(13, 0);
/*      */         }
/*      */ 
/* 2493 */         byte length = 7;
/*      */ 
/* 2495 */         if ((dt instanceof Timestamp)) {
/* 2496 */           length = 11;
/*      */         }
/*      */ 
/* 2499 */         intoBuf.ensureCapacity(length);
/*      */ 
/* 2501 */         intoBuf.writeByte(length);
/*      */ 
/* 2503 */         int year = sessionCalendar.get(1);
/* 2504 */         int month = sessionCalendar.get(2) + 1;
/* 2505 */         int date = sessionCalendar.get(5);
/*      */ 
/* 2507 */         intoBuf.writeInt(year);
/* 2508 */         intoBuf.writeByte((byte)month);
/* 2509 */         intoBuf.writeByte((byte)date);
/*      */ 
/* 2511 */         if ((dt instanceof java.sql.Date)) {
/* 2512 */           intoBuf.writeByte(0);
/* 2513 */           intoBuf.writeByte(0);
/* 2514 */           intoBuf.writeByte(0);
/*      */         } else {
/* 2516 */           intoBuf.writeByte((byte)sessionCalendar.get(11));
/*      */ 
/* 2518 */           intoBuf.writeByte((byte)sessionCalendar.get(12));
/*      */ 
/* 2520 */           intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */         }
/*      */ 
/* 2524 */         if (length == 11)
/*      */         {
/* 2526 */           intoBuf.writeLong(((Timestamp)dt).getNanos() / 1000);
/*      */         }
/*      */       }
/*      */       finally {
/* 2530 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private Calendar getServerTzCalendar() {
/* 2536 */     synchronized (this) {
/* 2537 */       if (this.serverTzCalendar == null) {
/* 2538 */         this.serverTzCalendar = new GregorianCalendar(this.connection.getServerTimezoneTZ());
/*      */       }
/*      */ 
/* 2541 */       return this.serverTzCalendar;
/*      */     }
/*      */   }
/*      */ 
/*      */   private Calendar getDefaultTzCalendar() {
/* 2546 */     synchronized (this) {
/* 2547 */       if (this.defaultTzCalendar == null) {
/* 2548 */         this.defaultTzCalendar = new GregorianCalendar(TimeZone.getDefault());
/*      */       }
/*      */ 
/* 2551 */       return this.defaultTzCalendar;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeReader(MysqlIO mysql, int parameterIndex, Buffer packet, Reader inStream)
/*      */     throws SQLException
/*      */   {
/* 2560 */     String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/* 2562 */     String clobEncoding = forcedEncoding == null ? this.connection.getEncoding() : forcedEncoding;
/*      */ 
/* 2565 */     int maxBytesChar = 2;
/*      */ 
/* 2567 */     if (clobEncoding != null) {
/* 2568 */       if (!clobEncoding.equals("UTF-16")) {
/* 2569 */         maxBytesChar = this.connection.getMaxBytesPerChar(clobEncoding);
/*      */ 
/* 2571 */         if (maxBytesChar == 1)
/* 2572 */           maxBytesChar = 2;
/*      */       }
/*      */       else {
/* 2575 */         maxBytesChar = 4;
/*      */       }
/*      */     }
/*      */ 
/* 2579 */     char[] buf = new char[8192 / maxBytesChar];
/*      */ 
/* 2581 */     int numRead = 0;
/*      */ 
/* 2583 */     int bytesInPacket = 0;
/* 2584 */     int totalBytesRead = 0;
/* 2585 */     int bytesReadAtLastSend = 0;
/* 2586 */     int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */     try
/*      */     {
/* 2591 */       packet.clear();
/* 2592 */       packet.writeByte(24);
/* 2593 */       packet.writeLong(this.serverStatementId);
/* 2594 */       packet.writeInt(parameterIndex);
/*      */ 
/* 2596 */       boolean readAny = false;
/*      */ 
/* 2598 */       while ((numRead = inStream.read(buf)) != -1) {
/* 2599 */         readAny = true;
/*      */ 
/* 2601 */         byte[] valueAsBytes = StringUtils.getBytes(buf, null, clobEncoding, this.connection.getServerCharacterEncoding(), 0, numRead, this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */ 
/* 2606 */         packet.writeBytesNoNull(valueAsBytes, 0, valueAsBytes.length);
/*      */ 
/* 2608 */         bytesInPacket += valueAsBytes.length;
/* 2609 */         totalBytesRead += valueAsBytes.length;
/*      */ 
/* 2611 */         if (bytesInPacket >= packetIsFullAt) {
/* 2612 */           bytesReadAtLastSend = totalBytesRead;
/*      */ 
/* 2614 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */ 
/* 2617 */           bytesInPacket = 0;
/* 2618 */           packet.clear();
/* 2619 */           packet.writeByte(24);
/* 2620 */           packet.writeLong(this.serverStatementId);
/* 2621 */           packet.writeInt(parameterIndex);
/*      */         }
/*      */       }
/*      */ 
/* 2625 */       if (totalBytesRead != bytesReadAtLastSend) {
/* 2626 */         mysql.sendCommand(24, null, packet, true, null, 0);
/*      */       }
/*      */ 
/* 2630 */       if (!readAny)
/* 2631 */         mysql.sendCommand(24, null, packet, true, null, 0);
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/* 2635 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.24") + ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/* 2638 */       sqlEx.initCause(ioEx);
/*      */ 
/* 2640 */       throw sqlEx;
/*      */     } finally {
/* 2642 */       if ((this.connection.getAutoClosePStmtStreams()) && 
/* 2643 */         (inStream != null))
/*      */         try {
/* 2645 */           inStream.close();
/*      */         }
/*      */         catch (IOException ioEx)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeStream(MysqlIO mysql, int parameterIndex, Buffer packet, InputStream inStream)
/*      */     throws SQLException
/*      */   {
/* 2656 */     byte[] buf = new byte[8192];
/*      */ 
/* 2658 */     int numRead = 0;
/*      */     try
/*      */     {
/* 2661 */       int bytesInPacket = 0;
/* 2662 */       int totalBytesRead = 0;
/* 2663 */       int bytesReadAtLastSend = 0;
/* 2664 */       int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */ 
/* 2666 */       packet.clear();
/* 2667 */       packet.writeByte(24);
/* 2668 */       packet.writeLong(this.serverStatementId);
/* 2669 */       packet.writeInt(parameterIndex);
/*      */ 
/* 2671 */       boolean readAny = false;
/*      */ 
/* 2673 */       while ((numRead = inStream.read(buf)) != -1)
/*      */       {
/* 2675 */         readAny = true;
/*      */ 
/* 2677 */         packet.writeBytesNoNull(buf, 0, numRead);
/* 2678 */         bytesInPacket += numRead;
/* 2679 */         totalBytesRead += numRead;
/*      */ 
/* 2681 */         if (bytesInPacket >= packetIsFullAt) {
/* 2682 */           bytesReadAtLastSend = totalBytesRead;
/*      */ 
/* 2684 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */ 
/* 2687 */           bytesInPacket = 0;
/* 2688 */           packet.clear();
/* 2689 */           packet.writeByte(24);
/* 2690 */           packet.writeLong(this.serverStatementId);
/* 2691 */           packet.writeInt(parameterIndex);
/*      */         }
/*      */       }
/*      */ 
/* 2695 */       if (totalBytesRead != bytesReadAtLastSend) {
/* 2696 */         mysql.sendCommand(24, null, packet, true, null, 0);
/*      */       }
/*      */ 
/* 2700 */       if (!readAny)
/* 2701 */         mysql.sendCommand(24, null, packet, true, null, 0);
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/* 2705 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.25") + ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/* 2708 */       sqlEx.initCause(ioEx);
/*      */ 
/* 2710 */       throw sqlEx;
/*      */     } finally {
/* 2712 */       if ((this.connection.getAutoClosePStmtStreams()) && 
/* 2713 */         (inStream != null))
/*      */         try {
/* 2715 */           inStream.close();
/*      */         }
/*      */         catch (IOException ioEx)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 2728 */     StringBuffer toStringBuf = new StringBuffer();
/*      */ 
/* 2730 */     toStringBuf.append("com.mysql.jdbc.ServerPreparedStatement[");
/* 2731 */     toStringBuf.append(this.serverStatementId);
/* 2732 */     toStringBuf.append("] - ");
/*      */     try
/*      */     {
/* 2735 */       toStringBuf.append(asSql());
/*      */     } catch (SQLException sqlEx) {
/* 2737 */       toStringBuf.append(Messages.getString("ServerPreparedStatement.6"));
/* 2738 */       toStringBuf.append(sqlEx);
/*      */     }
/*      */ 
/* 2741 */     return toStringBuf.toString();
/*      */   }
/*      */ 
/*      */   protected long getServerStatementId() {
/* 2745 */     return this.serverStatementId;
/*      */   }
/*      */ 
/*      */   public synchronized boolean canRewriteAsMultiValueInsertAtSqlLevel()
/*      */     throws SQLException
/*      */   {
/* 2752 */     if (!this.hasCheckedRewrite) {
/* 2753 */       this.hasCheckedRewrite = true;
/* 2754 */       this.canRewrite = canRewrite(this.originalSql, isOnDuplicateKeyUpdate(), getLocationOfOnDuplicateKeyUpdate(), 0);
/*      */ 
/* 2756 */       this.parseInfo = new PreparedStatement.ParseInfo(this, this.originalSql, this.connection, this.connection.getMetaData(), this.charEncoding, this.charConverter);
/*      */     }
/*      */ 
/* 2759 */     return this.canRewrite;
/*      */   }
/*      */ 
/*      */   public synchronized boolean canRewriteAsMultivalueInsertStatement() throws SQLException
/*      */   {
/* 2764 */     if (!canRewriteAsMultiValueInsertAtSqlLevel()) {
/* 2765 */       return false;
/*      */     }
/*      */ 
/* 2768 */     BindValue[] currentBindValues = null;
/* 2769 */     BindValue[] previousBindValues = null;
/*      */ 
/* 2771 */     int nbrCommands = this.batchedArgs.size();
/*      */ 
/* 2775 */     for (int commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 2776 */       Object arg = this.batchedArgs.get(commandIndex);
/*      */ 
/* 2778 */       if ((arg instanceof String))
/*      */         continue;
/* 2780 */       currentBindValues = ((BatchedBindValues)arg).batchedParameterValues;
/*      */ 
/* 2786 */       if (previousBindValues != null) {
/* 2787 */         for (int j = 0; j < this.parameterBindings.length; j++) {
/* 2788 */           if (currentBindValues[j].bufferType != previousBindValues[j].bufferType) {
/* 2789 */             return false;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2798 */     return true;
/*      */   }
/*      */ 
/*      */   protected synchronized int getLocationOfOnDuplicateKeyUpdate()
/*      */   {
/* 2804 */     if (this.locationOfOnDuplicateKeyUpdate == -2) {
/* 2805 */       this.locationOfOnDuplicateKeyUpdate = getOnDuplicateKeyLocation(this.originalSql);
/*      */     }
/*      */ 
/* 2808 */     return this.locationOfOnDuplicateKeyUpdate;
/*      */   }
/*      */ 
/*      */   protected synchronized boolean isOnDuplicateKeyUpdate() {
/* 2812 */     return getLocationOfOnDuplicateKeyUpdate() != -1;
/*      */   }
/*      */ 
/*      */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs)
/*      */   {
/* 2823 */     long sizeOfEntireBatch = 10L;
/* 2824 */     long maxSizeOfParameterSet = 0L;
/*      */ 
/* 2826 */     for (int i = 0; i < numBatchedArgs; i++) {
/* 2827 */       BindValue[] paramArg = ((BatchedBindValues)this.batchedArgs.get(i)).batchedParameterValues;
/*      */ 
/* 2829 */       long sizeOfParameterSet = 0L;
/*      */ 
/* 2831 */       sizeOfParameterSet += (this.parameterCount + 7) / 8;
/*      */ 
/* 2833 */       sizeOfParameterSet += this.parameterCount * 2;
/*      */ 
/* 2835 */       for (int j = 0; j < this.parameterBindings.length; j++) {
/* 2836 */         if (paramArg[j].isNull)
/*      */           continue;
/* 2838 */         long size = paramArg[j].getBoundLength();
/*      */ 
/* 2840 */         if (paramArg[j].isLongData) {
/* 2841 */           if (size != -1L)
/* 2842 */             sizeOfParameterSet += size;
/*      */         }
/*      */         else {
/* 2845 */           sizeOfParameterSet += size;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2850 */       sizeOfEntireBatch += sizeOfParameterSet;
/*      */ 
/* 2852 */       if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 2853 */         maxSizeOfParameterSet = sizeOfParameterSet;
/*      */       }
/*      */     }
/*      */ 
/* 2857 */     return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*      */   }
/*      */ 
/*      */   protected int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet)
/*      */     throws SQLException
/*      */   {
/* 2863 */     BindValue[] paramArg = ((BatchedBindValues)paramSet).batchedParameterValues;
/*      */ 
/* 2865 */     for (int j = 0; j < paramArg.length; j++) {
/* 2866 */       if (paramArg[j].isNull) {
/* 2867 */         batchedStatement.setNull(batchedParamIndex++, 0);
/*      */       }
/* 2869 */       else if (paramArg[j].isLongData) {
/* 2870 */         Object value = paramArg[j].value;
/*      */ 
/* 2872 */         if ((value instanceof InputStream)) {
/* 2873 */           batchedStatement.setBinaryStream(batchedParamIndex++, (InputStream)value, (int)paramArg[j].bindLength);
/*      */         }
/*      */         else
/*      */         {
/* 2877 */           batchedStatement.setCharacterStream(batchedParamIndex++, (Reader)value, (int)paramArg[j].bindLength);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2883 */         switch (paramArg[j].bufferType)
/*      */         {
/*      */         case 1:
/* 2886 */           batchedStatement.setByte(batchedParamIndex++, paramArg[j].byteBinding);
/*      */ 
/* 2888 */           break;
/*      */         case 2:
/* 2890 */           batchedStatement.setShort(batchedParamIndex++, paramArg[j].shortBinding);
/*      */ 
/* 2892 */           break;
/*      */         case 3:
/* 2894 */           batchedStatement.setInt(batchedParamIndex++, paramArg[j].intBinding);
/*      */ 
/* 2896 */           break;
/*      */         case 8:
/* 2898 */           batchedStatement.setLong(batchedParamIndex++, paramArg[j].longBinding);
/*      */ 
/* 2900 */           break;
/*      */         case 4:
/* 2902 */           batchedStatement.setFloat(batchedParamIndex++, paramArg[j].floatBinding);
/*      */ 
/* 2904 */           break;
/*      */         case 5:
/* 2906 */           batchedStatement.setDouble(batchedParamIndex++, paramArg[j].doubleBinding);
/*      */ 
/* 2908 */           break;
/*      */         case 11:
/* 2910 */           batchedStatement.setTime(batchedParamIndex++, (Time)paramArg[j].value);
/*      */ 
/* 2912 */           break;
/*      */         case 10:
/* 2914 */           batchedStatement.setDate(batchedParamIndex++, (java.sql.Date)paramArg[j].value);
/*      */ 
/* 2916 */           break;
/*      */         case 7:
/*      */         case 12:
/* 2919 */           batchedStatement.setTimestamp(batchedParamIndex++, (Timestamp)paramArg[j].value);
/*      */ 
/* 2921 */           break;
/*      */         case 0:
/*      */         case 15:
/*      */         case 246:
/*      */         case 253:
/*      */         case 254:
/* 2927 */           Object value = paramArg[j].value;
/*      */ 
/* 2929 */           if ((value instanceof byte[])) {
/* 2930 */             batchedStatement.setBytes(batchedParamIndex, (byte[])value);
/*      */           }
/*      */           else {
/* 2933 */             batchedStatement.setString(batchedParamIndex, (String)value);
/*      */           }
/*      */ 
/* 2939 */           if ((batchedStatement instanceof ServerPreparedStatement)) {
/* 2940 */             BindValue asBound = ((ServerPreparedStatement)batchedStatement).getBinding(batchedParamIndex, false);
/*      */ 
/* 2944 */             asBound.bufferType = paramArg[j].bufferType;
/*      */           }
/*      */ 
/* 2947 */           batchedParamIndex++;
/*      */ 
/* 2949 */           break;
/*      */         default:
/* 2951 */           throw new IllegalArgumentException("Unknown type when re-binding parameter into batched statement for parameter index " + batchedParamIndex);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2959 */     return batchedParamIndex;
/*      */   }
/*      */ 
/*      */   protected boolean containsOnDuplicateKeyUpdateInSQL() {
/* 2963 */     return this.hasOnDuplicateKeyUpdate;
/*      */   }
/*      */   protected PreparedStatement prepareBatchedInsertSQL(ConnectionImpl localConn, int numBatches) throws SQLException {
/*      */     SQLException sqlEx;
/*      */     try { PreparedStatement pstmt = new ServerPreparedStatement(localConn, this.parseInfo.getSqlForBatch(numBatches), this.currentCatalog, this.resultSetConcurrency, this.resultSetType);
/* 2969 */       pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
/*      */ 
/* 2971 */       return pstmt;
/*      */     } catch (UnsupportedEncodingException e) {
/* 2973 */       sqlEx = SQLError.createSQLException("Unable to prepare batch statement", "S1000", getExceptionInterceptor());
/* 2974 */       sqlEx.initCause(e);
/*      */     }
/* 2976 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   70 */     if (Util.isJdbc4())
/*      */       try {
/*   72 */         JDBC_4_SPS_CTOR = Class.forName("com.mysql.jdbc.JDBC4ServerPreparedStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class, String.class, Integer.TYPE, Integer.TYPE });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*   77 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   79 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   81 */         throw new RuntimeException(e);
/*      */       }
/*      */     else
/*   84 */       JDBC_4_SPS_CTOR = null;
/*      */   }
/*      */ 
/*      */   public static class BindValue {
/*  106 */     long boundBeforeExecutionNum = 0L;
/*      */     public long bindLength;
/*      */     int bufferType;
/*      */     byte byteBinding;
/*      */     double doubleBinding;
/*      */     float floatBinding;
/*      */     int intBinding;
/*      */     public boolean isLongData;
/*      */     public boolean isNull;
/*  124 */     boolean isSet = false;
/*      */     long longBinding;
/*      */     short shortBinding;
/*      */     public Object value;
/*      */ 
/*      */     BindValue() {
/*      */     }
/*      */ 
/*      */     BindValue(BindValue copyMe) {
/*  136 */       this.value = copyMe.value;
/*  137 */       this.isSet = copyMe.isSet;
/*  138 */       this.isLongData = copyMe.isLongData;
/*  139 */       this.isNull = copyMe.isNull;
/*  140 */       this.bufferType = copyMe.bufferType;
/*  141 */       this.bindLength = copyMe.bindLength;
/*  142 */       this.byteBinding = copyMe.byteBinding;
/*  143 */       this.shortBinding = copyMe.shortBinding;
/*  144 */       this.intBinding = copyMe.intBinding;
/*  145 */       this.longBinding = copyMe.longBinding;
/*  146 */       this.floatBinding = copyMe.floatBinding;
/*  147 */       this.doubleBinding = copyMe.doubleBinding;
/*      */     }
/*      */ 
/*      */     void reset() {
/*  151 */       this.isSet = false;
/*  152 */       this.value = null;
/*  153 */       this.isLongData = false;
/*      */ 
/*  155 */       this.byteBinding = 0;
/*  156 */       this.shortBinding = 0;
/*  157 */       this.intBinding = 0;
/*  158 */       this.longBinding = 0L;
/*  159 */       this.floatBinding = 0.0F;
/*  160 */       this.doubleBinding = 0.0D;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  164 */       return toString(false);
/*      */     }
/*      */ 
/*      */     public String toString(boolean quoteIfNeeded) {
/*  168 */       if (this.isLongData) {
/*  169 */         return "' STREAM DATA '";
/*      */       }
/*      */ 
/*  172 */       switch (this.bufferType) {
/*      */       case 1:
/*  174 */         return String.valueOf(this.byteBinding);
/*      */       case 2:
/*  176 */         return String.valueOf(this.shortBinding);
/*      */       case 3:
/*  178 */         return String.valueOf(this.intBinding);
/*      */       case 8:
/*  180 */         return String.valueOf(this.longBinding);
/*      */       case 4:
/*  182 */         return String.valueOf(this.floatBinding);
/*      */       case 5:
/*  184 */         return String.valueOf(this.doubleBinding);
/*      */       case 7:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 15:
/*      */       case 253:
/*      */       case 254:
/*  192 */         if (quoteIfNeeded) {
/*  193 */           return "'" + String.valueOf(this.value) + "'";
/*      */         }
/*  195 */         return String.valueOf(this.value);
/*      */       }
/*      */ 
/*  198 */       if ((this.value instanceof byte[])) {
/*  199 */         return "byte data";
/*      */       }
/*      */ 
/*  202 */       if (quoteIfNeeded) {
/*  203 */         return "'" + String.valueOf(this.value) + "'";
/*      */       }
/*  205 */       return String.valueOf(this.value);
/*      */     }
/*      */ 
/*      */     long getBoundLength()
/*      */     {
/*  212 */       if (this.isNull) {
/*  213 */         return 0L;
/*      */       }
/*      */ 
/*  216 */       if (this.isLongData) {
/*  217 */         return this.bindLength;
/*      */       }
/*      */ 
/*  220 */       switch (this.bufferType)
/*      */       {
/*      */       case 1:
/*  223 */         return 1L;
/*      */       case 2:
/*  225 */         return 2L;
/*      */       case 3:
/*  227 */         return 4L;
/*      */       case 8:
/*  229 */         return 8L;
/*      */       case 4:
/*  231 */         return 4L;
/*      */       case 5:
/*  233 */         return 8L;
/*      */       case 11:
/*  235 */         return 9L;
/*      */       case 10:
/*  237 */         return 7L;
/*      */       case 7:
/*      */       case 12:
/*  240 */         return 11L;
/*      */       case 0:
/*      */       case 15:
/*      */       case 246:
/*      */       case 253:
/*      */       case 254:
/*  246 */         if ((this.value instanceof byte[])) {
/*  247 */           return ((byte[])this.value).length;
/*      */         }
/*  249 */         return ((String)this.value).length();
/*      */       }
/*      */ 
/*  252 */       return 0L;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class BatchedBindValues
/*      */   {
/*      */     ServerPreparedStatement.BindValue[] batchedParameterValues;
/*      */ 
/*      */     BatchedBindValues(ServerPreparedStatement.BindValue[] paramVals)
/*      */     {
/*   94 */       int numParams = paramVals.length;
/*      */ 
/*   96 */       this.batchedParameterValues = new ServerPreparedStatement.BindValue[numParams];
/*      */ 
/*   98 */       for (int i = 0; i < numParams; i++)
/*   99 */         this.batchedParameterValues[i] = new ServerPreparedStatement.BindValue(paramVals[i]);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ServerPreparedStatement
 * JD-Core Version:    0.6.0
 */