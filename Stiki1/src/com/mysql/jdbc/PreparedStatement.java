/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URL;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.sql.Array;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParsePosition;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ 
/*      */ public class PreparedStatement extends StatementImpl
/*      */   implements java.sql.PreparedStatement
/*      */ {
/*      */   private static final Constructor JDBC_4_PSTMT_2_ARG_CTOR;
/*      */   private static final Constructor JDBC_4_PSTMT_3_ARG_CTOR;
/*      */   private static final Constructor JDBC_4_PSTMT_4_ARG_CTOR;
/*      */   private static final byte[] HEX_DIGITS;
/*  774 */   protected boolean batchHasPlainStatements = false;
/*      */ 
/*  776 */   private DatabaseMetaData dbmd = null;
/*      */ 
/*  782 */   protected char firstCharOfStmt = '\000';
/*      */ 
/*  785 */   protected boolean hasLimitClause = false;
/*      */ 
/*  788 */   protected boolean isLoadDataQuery = false;
/*      */ 
/*  790 */   private boolean[] isNull = null;
/*      */ 
/*  792 */   private boolean[] isStream = null;
/*      */ 
/*  794 */   protected int numberOfExecutions = 0;
/*      */ 
/*  797 */   protected String originalSql = null;
/*      */   protected int parameterCount;
/*      */   protected MysqlParameterMetadata parameterMetaData;
/*  804 */   private InputStream[] parameterStreams = null;
/*      */ 
/*  806 */   private byte[][] parameterValues = (byte[][])null;
/*      */ 
/*  812 */   protected int[] parameterTypes = null;
/*      */   protected ParseInfo parseInfo;
/*      */   private java.sql.ResultSetMetaData pstmtResultMetaData;
/*  818 */   private byte[][] staticSqlStrings = (byte[][])null;
/*      */ 
/*  820 */   private byte[] streamConvertBuf = new byte[4096];
/*      */ 
/*  822 */   private int[] streamLengths = null;
/*      */ 
/*  824 */   private SimpleDateFormat tsdf = null;
/*      */ 
/*  829 */   protected boolean useTrueBoolean = false;
/*      */   protected boolean usingAnsiMode;
/*      */   protected String batchedValuesClause;
/*      */   private boolean doPingInstead;
/*      */   private SimpleDateFormat ddf;
/*      */   private SimpleDateFormat tdf;
/*  839 */   private boolean compensateForOnDuplicateKeyUpdate = false;
/*      */   private CharsetEncoder charsetEncoder;
/*  845 */   private int batchCommandIndex = -1;
/*      */ 
/* 2566 */   protected int rewrittenBatchSize = 0;
/*      */ 
/*      */   protected static int readFully(Reader reader, char[] buf, int length)
/*      */     throws IOException
/*      */   {
/*  753 */     int numCharsRead = 0;
/*      */ 
/*  755 */     while (numCharsRead < length) {
/*  756 */       int count = reader.read(buf, numCharsRead, length - numCharsRead);
/*      */ 
/*  758 */       if (count < 0)
/*      */       {
/*      */         break;
/*      */       }
/*  762 */       numCharsRead += count;
/*      */     }
/*      */ 
/*  765 */     return numCharsRead;
/*      */   }
/*      */ 
/*      */   protected static PreparedStatement getInstance(ConnectionImpl conn, String catalog)
/*      */     throws SQLException
/*      */   {
/*  856 */     if (!Util.isJdbc4()) {
/*  857 */       return new PreparedStatement(conn, catalog);
/*      */     }
/*      */ 
/*  860 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_2_ARG_CTOR, new Object[] { conn, catalog }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected static PreparedStatement getInstance(ConnectionImpl conn, String sql, String catalog)
/*      */     throws SQLException
/*      */   {
/*  873 */     if (!Util.isJdbc4()) {
/*  874 */       return new PreparedStatement(conn, sql, catalog);
/*      */     }
/*      */ 
/*  877 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_3_ARG_CTOR, new Object[] { conn, sql, catalog }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected static PreparedStatement getInstance(ConnectionImpl conn, String sql, String catalog, ParseInfo cachedParseInfo)
/*      */     throws SQLException
/*      */   {
/*  890 */     if (!Util.isJdbc4()) {
/*  891 */       return new PreparedStatement(conn, sql, catalog, cachedParseInfo);
/*      */     }
/*      */ 
/*  894 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_4_ARG_CTOR, new Object[] { conn, sql, catalog, cachedParseInfo }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public PreparedStatement(ConnectionImpl conn, String catalog)
/*      */     throws SQLException
/*      */   {
/*  912 */     super(conn, catalog);
/*      */ 
/*  914 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */   }
/*      */ 
/*      */   public PreparedStatement(ConnectionImpl conn, String sql, String catalog)
/*      */     throws SQLException
/*      */   {
/*  932 */     super(conn, catalog);
/*      */ 
/*  934 */     if (sql == null) {
/*  935 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.0"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  939 */     this.originalSql = sql;
/*      */ 
/*  941 */     if (this.originalSql.startsWith("/* ping */"))
/*  942 */       this.doPingInstead = true;
/*      */     else {
/*  944 */       this.doPingInstead = false;
/*      */     }
/*      */ 
/*  947 */     this.dbmd = this.connection.getMetaData();
/*      */ 
/*  949 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */ 
/*  951 */     this.parseInfo = new ParseInfo(sql, this.connection, this.dbmd, this.charEncoding, this.charConverter);
/*      */ 
/*  954 */     initializeFromParseInfo();
/*      */ 
/*  956 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */ 
/*  958 */     if (conn.getRequiresEscapingEncoder())
/*  959 */       this.charsetEncoder = Charset.forName(conn.getEncoding()).newEncoder();
/*      */   }
/*      */ 
/*      */   public PreparedStatement(ConnectionImpl conn, String sql, String catalog, ParseInfo cachedParseInfo)
/*      */     throws SQLException
/*      */   {
/*  979 */     super(conn, catalog);
/*      */ 
/*  981 */     if (sql == null) {
/*  982 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.1"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  986 */     this.originalSql = sql;
/*      */ 
/*  988 */     this.dbmd = this.connection.getMetaData();
/*      */ 
/*  990 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */ 
/*  992 */     this.parseInfo = cachedParseInfo;
/*      */ 
/*  994 */     this.usingAnsiMode = (!this.connection.useAnsiQuotedIdentifiers());
/*      */ 
/*  996 */     initializeFromParseInfo();
/*      */ 
/*  998 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */ 
/* 1000 */     if (conn.getRequiresEscapingEncoder())
/* 1001 */       this.charsetEncoder = Charset.forName(conn.getEncoding()).newEncoder();
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/* 1013 */     if (this.batchedArgs == null) {
/* 1014 */       this.batchedArgs = new ArrayList();
/*      */     }
/*      */ 
/* 1017 */     for (int i = 0; i < this.parameterValues.length; i++) {
/* 1018 */       checkAllParametersSet(this.parameterValues[i], this.parameterStreams[i], i);
/*      */     }
/*      */ 
/* 1022 */     this.batchedArgs.add(new BatchParams(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull));
/*      */   }
/*      */ 
/*      */   public synchronized void addBatch(String sql)
/*      */     throws SQLException
/*      */   {
/* 1028 */     this.batchHasPlainStatements = true;
/*      */ 
/* 1030 */     super.addBatch(sql);
/*      */   }
/*      */ 
/*      */   protected String asSql() throws SQLException {
/* 1034 */     return asSql(false);
/*      */   }
/*      */ 
/*      */   protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
/* 1038 */     if (this.isClosed) {
/* 1039 */       return "statement has been closed, no further internal information available";
/*      */     }
/*      */ 
/* 1042 */     StringBuffer buf = new StringBuffer();
/*      */     try
/*      */     {
/* 1045 */       int realParameterCount = this.parameterCount + getParameterIndexOffset();
/* 1046 */       Object batchArg = null;
/* 1047 */       if (this.batchCommandIndex != -1) {
/* 1048 */         batchArg = this.batchedArgs.get(this.batchCommandIndex);
/*      */       }
/* 1050 */       for (int i = 0; i < realParameterCount; i++) {
/* 1051 */         if (this.charEncoding != null) {
/* 1052 */           buf.append(new String(this.staticSqlStrings[i], this.charEncoding));
/*      */         }
/*      */         else {
/* 1055 */           buf.append(new String(this.staticSqlStrings[i]));
/*      */         }
/*      */ 
/* 1058 */         byte[] val = null;
/* 1059 */         if ((batchArg != null) && ((batchArg instanceof String))) {
/* 1060 */           buf.append((String)batchArg);
/*      */         }
/*      */         else {
/* 1063 */           if (this.batchCommandIndex == -1)
/* 1064 */             val = this.parameterValues[i];
/*      */           else {
/* 1066 */             val = ((BatchParams)batchArg).parameterStrings[i];
/*      */           }
/* 1068 */           boolean isStreamParam = false;
/* 1069 */           if (this.batchCommandIndex == -1)
/* 1070 */             isStreamParam = this.isStream[i];
/*      */           else {
/* 1072 */             isStreamParam = ((BatchParams)batchArg).isStream[i];
/*      */           }
/* 1074 */           if ((val == null) && (!isStreamParam)) {
/* 1075 */             if (quoteStreamsAndUnknowns) {
/* 1076 */               buf.append("'");
/*      */             }
/*      */ 
/* 1079 */             buf.append("** NOT SPECIFIED **");
/*      */ 
/* 1081 */             if (quoteStreamsAndUnknowns)
/* 1082 */               buf.append("'");
/*      */           }
/* 1084 */           else if (isStreamParam) {
/* 1085 */             if (quoteStreamsAndUnknowns) {
/* 1086 */               buf.append("'");
/*      */             }
/*      */ 
/* 1089 */             buf.append("** STREAM DATA **");
/*      */ 
/* 1091 */             if (quoteStreamsAndUnknowns) {
/* 1092 */               buf.append("'");
/*      */             }
/*      */           }
/* 1095 */           else if (this.charConverter != null) {
/* 1096 */             buf.append(this.charConverter.toString(val));
/*      */           }
/* 1098 */           else if (this.charEncoding != null) {
/* 1099 */             buf.append(new String(val, this.charEncoding));
/*      */           } else {
/* 1101 */             buf.append(StringUtils.toAsciiString(val));
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1107 */       if (this.charEncoding != null) {
/* 1108 */         buf.append(new String(this.staticSqlStrings[(this.parameterCount + getParameterIndexOffset())], this.charEncoding));
/*      */       }
/*      */       else
/*      */       {
/* 1112 */         buf.append(StringUtils.toAsciiString(this.staticSqlStrings[(this.parameterCount + getParameterIndexOffset())]));
/*      */       }
/*      */     }
/*      */     catch (UnsupportedEncodingException uue)
/*      */     {
/* 1117 */       throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
/*      */     }
/*      */ 
/* 1123 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   public synchronized void clearBatch() throws SQLException {
/* 1127 */     this.batchHasPlainStatements = false;
/*      */ 
/* 1129 */     super.clearBatch();
/*      */   }
/*      */ 
/*      */   public synchronized void clearParameters()
/*      */     throws SQLException
/*      */   {
/* 1143 */     checkClosed();
/*      */ 
/* 1145 */     for (int i = 0; i < this.parameterValues.length; i++) {
/* 1146 */       this.parameterValues[i] = null;
/* 1147 */       this.parameterStreams[i] = null;
/* 1148 */       this.isStream[i] = false;
/* 1149 */       this.isNull[i] = false;
/* 1150 */       this.parameterTypes[i] = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/* 1161 */     realClose(true, true);
/*      */   }
/*      */ 
/*      */   private final void escapeblockFast(byte[] buf, Buffer packet, int size) throws SQLException
/*      */   {
/* 1166 */     int lastwritten = 0;
/*      */ 
/* 1168 */     for (int i = 0; i < size; i++) {
/* 1169 */       byte b = buf[i];
/*      */ 
/* 1171 */       if (b == 0)
/*      */       {
/* 1173 */         if (i > lastwritten) {
/* 1174 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/* 1178 */         packet.writeByte(92);
/* 1179 */         packet.writeByte(48);
/* 1180 */         lastwritten = i + 1;
/*      */       } else {
/* 1182 */         if ((b != 92) && (b != 39) && ((this.usingAnsiMode) || (b != 34))) {
/*      */           continue;
/*      */         }
/* 1185 */         if (i > lastwritten) {
/* 1186 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/* 1191 */         packet.writeByte(92);
/* 1192 */         lastwritten = i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1198 */     if (lastwritten < size)
/* 1199 */       packet.writeBytesNoNull(buf, lastwritten, size - lastwritten);
/*      */   }
/*      */ 
/*      */   private final void escapeblockFast(byte[] buf, ByteArrayOutputStream bytesOut, int size)
/*      */   {
/* 1205 */     int lastwritten = 0;
/*      */ 
/* 1207 */     for (int i = 0; i < size; i++) {
/* 1208 */       byte b = buf[i];
/*      */ 
/* 1210 */       if (b == 0)
/*      */       {
/* 1212 */         if (i > lastwritten) {
/* 1213 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/* 1217 */         bytesOut.write(92);
/* 1218 */         bytesOut.write(48);
/* 1219 */         lastwritten = i + 1;
/*      */       } else {
/* 1221 */         if ((b != 92) && (b != 39) && ((this.usingAnsiMode) || (b != 34))) {
/*      */           continue;
/*      */         }
/* 1224 */         if (i > lastwritten) {
/* 1225 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/* 1229 */         bytesOut.write(92);
/* 1230 */         lastwritten = i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1236 */     if (lastwritten < size)
/* 1237 */       bytesOut.write(buf, lastwritten, size - lastwritten);
/*      */   }
/*      */ 
/*      */   protected boolean checkReadOnlySafeStatement()
/*      */     throws SQLException
/*      */   {
/* 1248 */     return (!this.connection.isReadOnly()) || (this.firstCharOfStmt == 'S');
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/* 1263 */     checkClosed();
/*      */ 
/* 1265 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1267 */     if (!checkReadOnlySafeStatement()) {
/* 1268 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.20") + Messages.getString("PreparedStatement.21"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1273 */     ResultSetInternalMethods rs = null;
/*      */ 
/* 1275 */     CachedResultSetMetaData cachedMetadata = null;
/*      */ 
/* 1277 */     synchronized (locallyScopedConn.getMutex()) {
/* 1278 */       this.lastQueryIsOnDupKeyUpdate = false;
/* 1279 */       if (this.retrieveGeneratedKeys)
/* 1280 */         this.lastQueryIsOnDupKeyUpdate = containsOnDuplicateKeyUpdateInSQL();
/* 1281 */       boolean doStreaming = createStreamingResultSet();
/*      */ 
/* 1283 */       clearWarnings();
/*      */ 
/* 1293 */       if ((doStreaming) && (this.connection.getNetTimeoutForStreamingResults() > 0))
/*      */       {
/* 1295 */         executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
/*      */       }
/*      */ 
/* 1301 */       this.batchedGeneratedKeys = null;
/*      */ 
/* 1303 */       Buffer sendPacket = fillSendPacket();
/*      */ 
/* 1305 */       String oldCatalog = null;
/*      */ 
/* 1307 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1308 */         oldCatalog = locallyScopedConn.getCatalog();
/* 1309 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/* 1315 */       if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1316 */         cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
/*      */       }
/*      */ 
/* 1319 */       Field[] metadataFromCache = null;
/*      */ 
/* 1321 */       if (cachedMetadata != null) {
/* 1322 */         metadataFromCache = cachedMetadata.fields;
/*      */       }
/*      */ 
/* 1325 */       boolean oldInfoMsgState = false;
/*      */ 
/* 1327 */       if (this.retrieveGeneratedKeys) {
/* 1328 */         oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/* 1329 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       }
/*      */ 
/* 1341 */       if (locallyScopedConn.useMaxRows()) {
/* 1342 */         int rowLimit = -1;
/*      */ 
/* 1344 */         if (this.firstCharOfStmt == 'S') {
/* 1345 */           if (this.hasLimitClause) {
/* 1346 */             rowLimit = this.maxRows;
/*      */           }
/* 1348 */           else if (this.maxRows <= 0) {
/* 1349 */             executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */           }
/*      */           else {
/* 1352 */             executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1358 */           executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */         }
/*      */ 
/* 1363 */         rs = executeInternal(rowLimit, sendPacket, doStreaming, this.firstCharOfStmt == 'S', metadataFromCache, false);
/*      */       }
/*      */       else
/*      */       {
/* 1367 */         rs = executeInternal(-1, sendPacket, doStreaming, this.firstCharOfStmt == 'S', metadataFromCache, false);
/*      */       }
/*      */ 
/* 1372 */       if (cachedMetadata != null) {
/* 1373 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
/*      */       }
/* 1376 */       else if ((rs.reallyResult()) && (locallyScopedConn.getCacheResultSetMetadata())) {
/* 1377 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, null, rs);
/*      */       }
/*      */ 
/* 1382 */       if (this.retrieveGeneratedKeys) {
/* 1383 */         locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
/* 1384 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       }
/*      */ 
/* 1387 */       if (oldCatalog != null) {
/* 1388 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */ 
/* 1391 */       if (rs != null) {
/* 1392 */         this.lastInsertId = rs.getUpdateID();
/*      */ 
/* 1394 */         this.results = rs;
/*      */       }
/*      */     }
/*      */ 
/* 1398 */     return (rs != null) && (rs.reallyResult());
/*      */   }
/*      */ 
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/* 1416 */     checkClosed();
/*      */ 
/* 1418 */     if (this.connection.isReadOnly()) {
/* 1419 */       throw new SQLException(Messages.getString("PreparedStatement.25") + Messages.getString("PreparedStatement.26"), "S1009");
/*      */     }
/*      */ 
/* 1424 */     synchronized (this.connection.getMutex()) {
/* 1425 */       if ((this.batchedArgs == null) || (this.batchedArgs.size() == 0)) {
/* 1426 */         return new int[0];
/*      */       }
/*      */ 
/* 1430 */       int batchTimeout = this.timeoutInMillis;
/* 1431 */       this.timeoutInMillis = 0;
/*      */ 
/* 1433 */       resetCancelledState();
/*      */       try
/*      */       {
/* 1436 */         clearWarnings();
/*      */ 
/* 1438 */         if ((!this.batchHasPlainStatements) && (this.connection.getRewriteBatchedStatements()))
/*      */         {
/* 1442 */           if (canRewriteAsMultiValueInsertAtSqlLevel()) {
/* 1443 */             arrayOfInt = executeBatchedInserts(batchTimeout);
/*      */ 
/* 1456 */             clearBatch(); return arrayOfInt;
/*      */           }
/* 1446 */           if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (!this.batchHasPlainStatements) && (this.batchedArgs != null) && (this.batchedArgs.size() > 3))
/*      */           {
/* 1450 */             arrayOfInt = executePreparedBatchAsMultiStatement(batchTimeout);
/*      */ 
/* 1456 */             clearBatch(); return arrayOfInt;
/*      */           }
/*      */         }
/* 1454 */         int[] arrayOfInt = executeBatchSerially(batchTimeout);
/*      */ 
/* 1456 */         clearBatch(); return arrayOfInt; } finally { clearBatch(); }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean canRewriteAsMultiValueInsertAtSqlLevel() throws SQLException
/*      */   {
/* 1462 */     return this.parseInfo.canRewriteAsMultiValueInsert;
/*      */   }
/*      */ 
/*      */   protected int getLocationOfOnDuplicateKeyUpdate() {
/* 1466 */     return this.parseInfo.locationOfOnDuplicateKeyUpdate;
/*      */   }
/*      */ 
/*      */   protected int[] executePreparedBatchAsMultiStatement(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/* 1480 */     synchronized (this.connection.getMutex())
/*      */     {
/* 1482 */       if (this.batchedValuesClause == null) {
/* 1483 */         this.batchedValuesClause = (this.originalSql + ";");
/*      */       }
/*      */ 
/* 1486 */       ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1488 */       boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
/* 1489 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/* 1492 */         clearWarnings();
/*      */ 
/* 1494 */         int numBatchedArgs = this.batchedArgs.size();
/*      */ 
/* 1496 */         if (this.retrieveGeneratedKeys) {
/* 1497 */           this.batchedGeneratedKeys = new ArrayList(numBatchedArgs);
/*      */         }
/*      */ 
/* 1500 */         int numValuesPerBatch = computeBatchSize(numBatchedArgs);
/*      */ 
/* 1502 */         if (numBatchedArgs < numValuesPerBatch) {
/* 1503 */           numValuesPerBatch = numBatchedArgs;
/*      */         }
/*      */ 
/* 1506 */         java.sql.PreparedStatement batchedStatement = null;
/*      */ 
/* 1508 */         int batchedParamIndex = 1;
/* 1509 */         int numberToExecuteAsMultiValue = 0;
/* 1510 */         int batchCounter = 0;
/* 1511 */         int updateCountCounter = 0;
/* 1512 */         int[] updateCounts = new int[numBatchedArgs];
/* 1513 */         SQLException sqlEx = null;
/*      */         try
/*      */         {
/* 1516 */           if (!multiQueriesEnabled) {
/* 1517 */             locallyScopedConn.getIO().enableMultiQueries();
/*      */           }
/*      */ 
/* 1520 */           if (this.retrieveGeneratedKeys) {
/* 1521 */             batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch), 1);
/*      */           }
/*      */           else
/*      */           {
/* 1525 */             batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch));
/*      */           }
/*      */ 
/* 1529 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/* 1532 */             timeoutTask = new StatementImpl.CancelTask(this, (StatementImpl)batchedStatement);
/* 1533 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           }
/*      */ 
/* 1537 */           if (numBatchedArgs < numValuesPerBatch)
/* 1538 */             numberToExecuteAsMultiValue = numBatchedArgs;
/*      */           else {
/* 1540 */             numberToExecuteAsMultiValue = numBatchedArgs / numValuesPerBatch;
/*      */           }
/*      */ 
/* 1543 */           int numberArgsToExecute = numberToExecuteAsMultiValue * numValuesPerBatch;
/*      */ 
/* 1545 */           for (int i = 0; i < numberArgsToExecute; i++) {
/* 1546 */             if ((i != 0) && (i % numValuesPerBatch == 0)) {
/*      */               try {
/* 1548 */                 batchedStatement.execute();
/*      */               } catch (SQLException ex) {
/* 1550 */                 sqlEx = handleExceptionForBatch(batchCounter, numValuesPerBatch, updateCounts, ex);
/*      */               }
/*      */ 
/* 1554 */               updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/* 1558 */               batchedStatement.clearParameters();
/* 1559 */               batchedParamIndex = 1;
/*      */             }
/*      */ 
/* 1562 */             batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1568 */             batchedStatement.execute();
/*      */           } catch (SQLException ex) {
/* 1570 */             sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */           }
/*      */ 
/* 1574 */           updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/* 1578 */           batchedStatement.clearParameters();
/*      */ 
/* 1580 */           numValuesPerBatch = numBatchedArgs - batchCounter;
/*      */         } finally {
/* 1582 */           if (batchedStatement != null) {
/* 1583 */             batchedStatement.close();
/*      */           }
/*      */         }
/*      */         try
/*      */         {
/* 1588 */           if (numValuesPerBatch > 0)
/*      */           {
/* 1590 */             if (this.retrieveGeneratedKeys) {
/* 1591 */               batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch), 1);
/*      */             }
/*      */             else
/*      */             {
/* 1595 */               batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch));
/*      */             }
/*      */ 
/* 1599 */             if (timeoutTask != null) {
/* 1600 */               timeoutTask.toCancel = ((StatementImpl)batchedStatement);
/*      */             }
/*      */ 
/* 1603 */             batchedParamIndex = 1;
/*      */ 
/* 1605 */             while (batchCounter < numBatchedArgs) {
/* 1606 */               batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/* 1612 */               batchedStatement.execute();
/*      */             } catch (SQLException ex) {
/* 1614 */               sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */             }
/*      */ 
/* 1618 */             updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/* 1622 */             batchedStatement.clearParameters();
/*      */           }
/*      */ 
/* 1625 */           if (timeoutTask != null) {
/* 1626 */             if (timeoutTask.caughtWhileCancelling != null) {
/* 1627 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */ 
/* 1630 */             timeoutTask.cancel();
/* 1631 */             timeoutTask = null;
/*      */           }
/*      */ 
/* 1634 */           if (sqlEx != null) {
/* 1635 */             throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */           }
/*      */ 
/* 1640 */           ex = updateCounts;
/*      */ 
/* 1642 */           if (batchedStatement != null)
/* 1643 */             batchedStatement.close(); jsr 33; return ex;
/*      */         }
/*      */         finally
/*      */         {
/* 1642 */           if (batchedStatement != null)
/* 1643 */             batchedStatement.close();
/*      */         }
/*      */       }
/*      */       finally {
/* 1647 */         jsr 6; } localObject4 = returnAddress; if (timeoutTask != null) {
/* 1648 */         timeoutTask.cancel();
/*      */       }
/*      */ 
/* 1651 */       resetCancelledState();
/*      */ 
/* 1653 */       if (!multiQueriesEnabled) {
/* 1654 */         locallyScopedConn.getIO().disableMultiQueries();
/*      */       }
/*      */ 
/* 1657 */       clearBatch(); ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   private String generateMultiStatementForBatch(int numBatches)
/*      */   {
/* 1663 */     StringBuffer newStatementSql = new StringBuffer((this.originalSql.length() + 1) * numBatches);
/*      */ 
/* 1666 */     newStatementSql.append(this.originalSql);
/*      */ 
/* 1668 */     for (int i = 0; i < numBatches - 1; i++) {
/* 1669 */       newStatementSql.append(';');
/* 1670 */       newStatementSql.append(this.originalSql);
/*      */     }
/*      */ 
/* 1673 */     return newStatementSql.toString(); } 
/*      */   // ERROR //
/*      */   protected int[] executeBatchedInserts(int batchTimeout) throws SQLException { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 192	com/mysql/jdbc/PreparedStatement:getValuesClause	()Ljava/lang/String;
/*      */     //   4: astore_2
/*      */     //   5: aload_0
/*      */     //   6: getfield 41	com/mysql/jdbc/PreparedStatement:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   9: astore_3
/*      */     //   10: aload_2
/*      */     //   11: ifnonnull +9 -> 20
/*      */     //   14: aload_0
/*      */     //   15: iload_1
/*      */     //   16: invokevirtual 155	com/mysql/jdbc/PreparedStatement:executeBatchSerially	(I)[I
/*      */     //   19: areturn
/*      */     //   20: aload_0
/*      */     //   21: getfield 67	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   24: invokeinterface 147 1 0
/*      */     //   29: istore 4
/*      */     //   31: aload_0
/*      */     //   32: getfield 113	com/mysql/jdbc/PreparedStatement:retrieveGeneratedKeys	Z
/*      */     //   35: ifeq +16 -> 51
/*      */     //   38: aload_0
/*      */     //   39: new 68	java/util/ArrayList
/*      */     //   42: dup
/*      */     //   43: iload 4
/*      */     //   45: invokespecial 161	java/util/ArrayList:<init>	(I)V
/*      */     //   48: putfield 121	com/mysql/jdbc/PreparedStatement:batchedGeneratedKeys	Ljava/util/ArrayList;
/*      */     //   51: aload_0
/*      */     //   52: iload 4
/*      */     //   54: invokevirtual 162	com/mysql/jdbc/PreparedStatement:computeBatchSize	(I)I
/*      */     //   57: istore 5
/*      */     //   59: iload 4
/*      */     //   61: iload 5
/*      */     //   63: if_icmpge +7 -> 70
/*      */     //   66: iload 4
/*      */     //   68: istore 5
/*      */     //   70: aconst_null
/*      */     //   71: astore 6
/*      */     //   73: iconst_1
/*      */     //   74: istore 7
/*      */     //   76: iconst_0
/*      */     //   77: istore 8
/*      */     //   79: iconst_0
/*      */     //   80: istore 9
/*      */     //   82: iconst_0
/*      */     //   83: istore 10
/*      */     //   85: aconst_null
/*      */     //   86: astore 11
/*      */     //   88: aconst_null
/*      */     //   89: astore 12
/*      */     //   91: iload 4
/*      */     //   93: newarray int
/*      */     //   95: astore 13
/*      */     //   97: iconst_0
/*      */     //   98: istore 14
/*      */     //   100: iload 14
/*      */     //   102: aload_0
/*      */     //   103: getfield 67	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   106: invokeinterface 147 1 0
/*      */     //   111: if_icmpge +15 -> 126
/*      */     //   114: aload 13
/*      */     //   116: iload 14
/*      */     //   118: iconst_1
/*      */     //   119: iastore
/*      */     //   120: iinc 14 1
/*      */     //   123: goto -23 -> 100
/*      */     //   126: aload_0
/*      */     //   127: aload_3
/*      */     //   128: iload 5
/*      */     //   130: invokevirtual 193	com/mysql/jdbc/PreparedStatement:prepareBatchedInsertSQL	(Lcom/mysql/jdbc/ConnectionImpl;I)Lcom/mysql/jdbc/PreparedStatement;
/*      */     //   133: astore 6
/*      */     //   135: aload_3
/*      */     //   136: invokevirtual 168	com/mysql/jdbc/ConnectionImpl:getEnableQueryTimeouts	()Z
/*      */     //   139: ifeq +43 -> 182
/*      */     //   142: iload_1
/*      */     //   143: ifeq +39 -> 182
/*      */     //   146: aload_3
/*      */     //   147: iconst_5
/*      */     //   148: iconst_0
/*      */     //   149: iconst_0
/*      */     //   150: invokevirtual 52	com/mysql/jdbc/ConnectionImpl:versionMeetsMinimum	(III)Z
/*      */     //   153: ifeq +29 -> 182
/*      */     //   156: new 169	com/mysql/jdbc/StatementImpl$CancelTask
/*      */     //   159: dup
/*      */     //   160: aload_0
/*      */     //   161: aload 6
/*      */     //   163: checkcast 170	com/mysql/jdbc/StatementImpl
/*      */     //   166: invokespecial 171	com/mysql/jdbc/StatementImpl$CancelTask:<init>	(Lcom/mysql/jdbc/StatementImpl;Lcom/mysql/jdbc/StatementImpl;)V
/*      */     //   169: astore 11
/*      */     //   171: aload_3
/*      */     //   172: invokevirtual 172	com/mysql/jdbc/ConnectionImpl:getCancelTimer	()Ljava/util/Timer;
/*      */     //   175: aload 11
/*      */     //   177: iload_1
/*      */     //   178: i2l
/*      */     //   179: invokevirtual 173	java/util/Timer:schedule	(Ljava/util/TimerTask;J)V
/*      */     //   182: iload 4
/*      */     //   184: iload 5
/*      */     //   186: if_icmpge +10 -> 196
/*      */     //   189: iload 4
/*      */     //   191: istore 9
/*      */     //   193: goto +10 -> 203
/*      */     //   196: iload 4
/*      */     //   198: iload 5
/*      */     //   200: idiv
/*      */     //   201: istore 9
/*      */     //   203: iload 9
/*      */     //   205: iload 5
/*      */     //   207: imul
/*      */     //   208: istore 14
/*      */     //   210: iconst_0
/*      */     //   211: istore 15
/*      */     //   213: iload 15
/*      */     //   215: iload 14
/*      */     //   217: if_icmpge +95 -> 312
/*      */     //   220: iload 15
/*      */     //   222: ifeq +60 -> 282
/*      */     //   225: iload 15
/*      */     //   227: iload 5
/*      */     //   229: irem
/*      */     //   230: ifne +52 -> 282
/*      */     //   233: iload 8
/*      */     //   235: aload 6
/*      */     //   237: invokeinterface 194 1 0
/*      */     //   242: iadd
/*      */     //   243: istore 8
/*      */     //   245: goto +21 -> 266
/*      */     //   248: astore 16
/*      */     //   250: aload_0
/*      */     //   251: iload 10
/*      */     //   253: iconst_1
/*      */     //   254: isub
/*      */     //   255: iload 5
/*      */     //   257: aload 13
/*      */     //   259: aload 16
/*      */     //   261: invokevirtual 175	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[ILjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   264: astore 12
/*      */     //   266: aload_0
/*      */     //   267: aload 6
/*      */     //   269: invokevirtual 195	com/mysql/jdbc/PreparedStatement:getBatchedGeneratedKeys	(Ljava/sql/Statement;)V
/*      */     //   272: aload 6
/*      */     //   274: invokeinterface 177 1 0
/*      */     //   279: iconst_1
/*      */     //   280: istore 7
/*      */     //   282: aload_0
/*      */     //   283: aload 6
/*      */     //   285: iload 7
/*      */     //   287: aload_0
/*      */     //   288: getfield 67	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   291: iload 10
/*      */     //   293: iinc 10 1
/*      */     //   296: invokeinterface 82 2 0
/*      */     //   301: invokevirtual 178	com/mysql/jdbc/PreparedStatement:setOneBatchedParameterSet	(Ljava/sql/PreparedStatement;ILjava/lang/Object;)I
/*      */     //   304: istore 7
/*      */     //   306: iinc 15 1
/*      */     //   309: goto -96 -> 213
/*      */     //   312: iload 8
/*      */     //   314: aload 6
/*      */     //   316: invokeinterface 194 1 0
/*      */     //   321: iadd
/*      */     //   322: istore 8
/*      */     //   324: goto +21 -> 345
/*      */     //   327: astore 15
/*      */     //   329: aload_0
/*      */     //   330: iload 10
/*      */     //   332: iconst_1
/*      */     //   333: isub
/*      */     //   334: iload 5
/*      */     //   336: aload 13
/*      */     //   338: aload 15
/*      */     //   340: invokevirtual 175	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[ILjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   343: astore 12
/*      */     //   345: aload_0
/*      */     //   346: aload 6
/*      */     //   348: invokevirtual 195	com/mysql/jdbc/PreparedStatement:getBatchedGeneratedKeys	(Ljava/sql/Statement;)V
/*      */     //   351: iload 4
/*      */     //   353: iload 10
/*      */     //   355: isub
/*      */     //   356: istore 5
/*      */     //   358: aload 6
/*      */     //   360: ifnull +30 -> 390
/*      */     //   363: aload 6
/*      */     //   365: invokeinterface 179 1 0
/*      */     //   370: goto +20 -> 390
/*      */     //   373: astore 17
/*      */     //   375: aload 6
/*      */     //   377: ifnull +10 -> 387
/*      */     //   380: aload 6
/*      */     //   382: invokeinterface 179 1 0
/*      */     //   387: aload 17
/*      */     //   389: athrow
/*      */     //   390: iload 5
/*      */     //   392: ifle +103 -> 495
/*      */     //   395: aload_0
/*      */     //   396: aload_3
/*      */     //   397: iload 5
/*      */     //   399: invokevirtual 193	com/mysql/jdbc/PreparedStatement:prepareBatchedInsertSQL	(Lcom/mysql/jdbc/ConnectionImpl;I)Lcom/mysql/jdbc/PreparedStatement;
/*      */     //   402: astore 6
/*      */     //   404: aload 11
/*      */     //   406: ifnull +13 -> 419
/*      */     //   409: aload 11
/*      */     //   411: aload 6
/*      */     //   413: checkcast 170	com/mysql/jdbc/StatementImpl
/*      */     //   416: putfield 180	com/mysql/jdbc/StatementImpl$CancelTask:toCancel	Lcom/mysql/jdbc/StatementImpl;
/*      */     //   419: iconst_1
/*      */     //   420: istore 7
/*      */     //   422: iload 10
/*      */     //   424: iload 4
/*      */     //   426: if_icmpge +30 -> 456
/*      */     //   429: aload_0
/*      */     //   430: aload 6
/*      */     //   432: iload 7
/*      */     //   434: aload_0
/*      */     //   435: getfield 67	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   438: iload 10
/*      */     //   440: iinc 10 1
/*      */     //   443: invokeinterface 82 2 0
/*      */     //   448: invokevirtual 178	com/mysql/jdbc/PreparedStatement:setOneBatchedParameterSet	(Ljava/sql/PreparedStatement;ILjava/lang/Object;)I
/*      */     //   451: istore 7
/*      */     //   453: goto -31 -> 422
/*      */     //   456: iload 8
/*      */     //   458: aload 6
/*      */     //   460: invokeinterface 194 1 0
/*      */     //   465: iadd
/*      */     //   466: istore 8
/*      */     //   468: goto +21 -> 489
/*      */     //   471: astore 14
/*      */     //   473: aload_0
/*      */     //   474: iload 10
/*      */     //   476: iconst_1
/*      */     //   477: isub
/*      */     //   478: iload 5
/*      */     //   480: aload 13
/*      */     //   482: aload 14
/*      */     //   484: invokevirtual 175	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[ILjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   487: astore 12
/*      */     //   489: aload_0
/*      */     //   490: aload 6
/*      */     //   492: invokevirtual 195	com/mysql/jdbc/PreparedStatement:getBatchedGeneratedKeys	(Ljava/sql/Statement;)V
/*      */     //   495: aload 12
/*      */     //   497: ifnull +28 -> 525
/*      */     //   500: new 183	java/sql/BatchUpdateException
/*      */     //   503: dup
/*      */     //   504: aload 12
/*      */     //   506: invokevirtual 184	java/sql/SQLException:getMessage	()Ljava/lang/String;
/*      */     //   509: aload 12
/*      */     //   511: invokevirtual 185	java/sql/SQLException:getSQLState	()Ljava/lang/String;
/*      */     //   514: aload 12
/*      */     //   516: invokevirtual 186	java/sql/SQLException:getErrorCode	()I
/*      */     //   519: aload 13
/*      */     //   521: invokespecial 187	java/sql/BatchUpdateException:<init>	(Ljava/lang/String;Ljava/lang/String;I[I)V
/*      */     //   524: athrow
/*      */     //   525: aload 13
/*      */     //   527: astore 14
/*      */     //   529: aload 6
/*      */     //   531: ifnull +10 -> 541
/*      */     //   534: aload 6
/*      */     //   536: invokeinterface 179 1 0
/*      */     //   541: jsr +31 -> 572
/*      */     //   544: aload 14
/*      */     //   546: areturn
/*      */     //   547: astore 18
/*      */     //   549: aload 6
/*      */     //   551: ifnull +10 -> 561
/*      */     //   554: aload 6
/*      */     //   556: invokeinterface 179 1 0
/*      */     //   561: aload 18
/*      */     //   563: athrow
/*      */     //   564: astore 19
/*      */     //   566: jsr +6 -> 572
/*      */     //   569: aload 19
/*      */     //   571: athrow
/*      */     //   572: astore 20
/*      */     //   574: aload 11
/*      */     //   576: ifnull +9 -> 585
/*      */     //   579: aload 11
/*      */     //   581: invokevirtual 182	com/mysql/jdbc/StatementImpl$CancelTask:cancel	()Z
/*      */     //   584: pop
/*      */     //   585: aload_0
/*      */     //   586: invokevirtual 149	com/mysql/jdbc/PreparedStatement:resetCancelledState	()V
/*      */     //   589: ret 20
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   233	245	248	java/sql/SQLException
/*      */     //   312	324	327	java/sql/SQLException
/*      */     //   126	358	373	finally
/*      */     //   373	375	373	finally
/*      */     //   456	468	471	java/sql/SQLException
/*      */     //   390	529	547	finally
/*      */     //   547	549	547	finally
/*      */     //   126	544	564	finally
/*      */     //   547	569	564	finally } 
/* 1832 */   protected String getValuesClause() throws SQLException { return this.parseInfo.valuesClause;
/*      */   }
/*      */ 
/*      */   protected int computeBatchSize(int numBatchedArgs)
/*      */     throws SQLException
/*      */   {
/* 1844 */     long[] combinedValues = computeMaxParameterSetSizeAndBatchSize(numBatchedArgs);
/*      */ 
/* 1846 */     long maxSizeOfParameterSet = combinedValues[0];
/* 1847 */     long sizeOfEntireBatch = combinedValues[1];
/*      */ 
/* 1849 */     int maxAllowedPacket = this.connection.getMaxAllowedPacket();
/*      */ 
/* 1851 */     if (sizeOfEntireBatch < maxAllowedPacket - this.originalSql.length()) {
/* 1852 */       return numBatchedArgs;
/*      */     }
/*      */ 
/* 1855 */     return (int)Math.max(1L, (maxAllowedPacket - this.originalSql.length()) / maxSizeOfParameterSet);
/*      */   }
/*      */ 
/*      */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs)
/*      */     throws SQLException
/*      */   {
/* 1864 */     long sizeOfEntireBatch = 0L;
/* 1865 */     long maxSizeOfParameterSet = 0L;
/*      */ 
/* 1867 */     for (int i = 0; i < numBatchedArgs; i++) {
/* 1868 */       BatchParams paramArg = (BatchParams)this.batchedArgs.get(i);
/*      */ 
/* 1871 */       boolean[] isNullBatch = paramArg.isNull;
/* 1872 */       boolean[] isStreamBatch = paramArg.isStream;
/*      */ 
/* 1874 */       long sizeOfParameterSet = 0L;
/*      */ 
/* 1876 */       for (int j = 0; j < isNullBatch.length; j++) {
/* 1877 */         if (isNullBatch[j] == 0)
/*      */         {
/* 1879 */           if (isStreamBatch[j] != 0) {
/* 1880 */             int streamLength = paramArg.streamLengths[j];
/*      */ 
/* 1882 */             if (streamLength != -1) {
/* 1883 */               sizeOfParameterSet += streamLength * 2;
/*      */             } else {
/* 1885 */               int paramLength = paramArg.parameterStrings[j].length;
/* 1886 */               sizeOfParameterSet += paramLength;
/*      */             }
/*      */           } else {
/* 1889 */             sizeOfParameterSet += paramArg.parameterStrings[j].length;
/*      */           }
/*      */         }
/* 1892 */         else sizeOfParameterSet += 4L;
/*      */ 
/*      */       }
/*      */ 
/* 1904 */       if (getValuesClause() != null)
/* 1905 */         sizeOfParameterSet += getValuesClause().length() + 1;
/*      */       else {
/* 1907 */         sizeOfParameterSet += this.originalSql.length() + 1;
/*      */       }
/*      */ 
/* 1910 */       sizeOfEntireBatch += sizeOfParameterSet;
/*      */ 
/* 1912 */       if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 1913 */         maxSizeOfParameterSet = sizeOfParameterSet;
/*      */       }
/*      */     }
/*      */ 
/* 1917 */     return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*      */   }
/*      */ 
/*      */   protected int[] executeBatchSerially(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/* 1930 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 1932 */     if (locallyScopedConn == null) {
/* 1933 */       checkClosed();
/*      */     }
/*      */ 
/* 1936 */     int[] updateCounts = null;
/*      */ 
/* 1938 */     if (this.batchedArgs != null) {
/* 1939 */       int nbrCommands = this.batchedArgs.size();
/* 1940 */       updateCounts = new int[nbrCommands];
/*      */ 
/* 1942 */       for (int i = 0; i < nbrCommands; i++) {
/* 1943 */         updateCounts[i] = -3;
/*      */       }
/*      */ 
/* 1946 */       SQLException sqlEx = null;
/*      */ 
/* 1948 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/* 1951 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 1954 */           timeoutTask = new StatementImpl.CancelTask(this, this);
/* 1955 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */         }
/*      */ 
/* 1959 */         if (this.retrieveGeneratedKeys) {
/* 1960 */           this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */         }
/*      */ 
/* 1963 */         for (this.batchCommandIndex = 0; this.batchCommandIndex < nbrCommands; this.batchCommandIndex += 1) {
/* 1964 */           Object arg = this.batchedArgs.get(this.batchCommandIndex);
/*      */ 
/* 1966 */           if ((arg instanceof String)) {
/* 1967 */             updateCounts[this.batchCommandIndex] = executeUpdate((String)arg);
/*      */           } else {
/* 1969 */             BatchParams paramArg = (BatchParams)arg;
/*      */             try
/*      */             {
/* 1972 */               updateCounts[this.batchCommandIndex] = executeUpdate(paramArg.parameterStrings, paramArg.parameterStreams, paramArg.isStream, paramArg.streamLengths, paramArg.isNull, true);
/*      */ 
/* 1977 */               if (this.retrieveGeneratedKeys) {
/* 1978 */                 ResultSet rs = null;
/*      */                 try
/*      */                 {
/* 1981 */                   if (containsOnDuplicateKeyUpdateInSQL())
/* 1982 */                     rs = getGeneratedKeysInternal(1);
/*      */                   else {
/* 1984 */                     rs = getGeneratedKeysInternal();
/*      */                   }
/* 1986 */                   while (rs.next())
/* 1987 */                     this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */                 }
/*      */                 finally
/*      */                 {
/* 1991 */                   if (rs != null)
/* 1992 */                     rs.close();
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (SQLException ex) {
/* 1997 */               updateCounts[this.batchCommandIndex] = -3;
/*      */ 
/* 1999 */               if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */               {
/* 2003 */                 sqlEx = ex;
/*      */               } else {
/* 2005 */                 int[] newUpdateCounts = new int[this.batchCommandIndex];
/* 2006 */                 System.arraycopy(updateCounts, 0, newUpdateCounts, 0, this.batchCommandIndex);
/*      */ 
/* 2009 */                 throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2017 */         if (sqlEx != null)
/* 2018 */           throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */       }
/*      */       catch (NullPointerException npe)
/*      */       {
/*      */         try {
/* 2023 */           checkClosed();
/*      */         } catch (SQLException connectionClosedEx) {
/* 2025 */           updateCounts[this.batchCommandIndex] = -3;
/*      */ 
/* 2027 */           int[] newUpdateCounts = new int[this.batchCommandIndex];
/*      */ 
/* 2029 */           System.arraycopy(updateCounts, 0, newUpdateCounts, 0, this.batchCommandIndex);
/*      */ 
/* 2032 */           throw new BatchUpdateException(connectionClosedEx.getMessage(), connectionClosedEx.getSQLState(), connectionClosedEx.getErrorCode(), newUpdateCounts);
/*      */         }
/*      */ 
/* 2037 */         throw npe;
/*      */       } finally {
/* 2039 */         this.batchCommandIndex = -1;
/*      */ 
/* 2041 */         if (timeoutTask != null) {
/* 2042 */           timeoutTask.cancel();
/*      */         }
/*      */ 
/* 2045 */         resetCancelledState();
/*      */       }
/*      */     }
/*      */ 
/* 2049 */     return updateCounts != null ? updateCounts : new int[0];
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2080 */       resetCancelledState();
/*      */ 
/* 2082 */       ConnectionImpl locallyScopedConnection = this.connection;
/*      */ 
/* 2084 */       this.numberOfExecutions += 1;
/*      */ 
/* 2086 */       if (this.doPingInstead) {
/* 2087 */         doPingInstead();
/*      */ 
/* 2089 */         return this.results;
/*      */       }
/*      */ 
/* 2094 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       ResultSetInternalMethods rs;
/*      */       try {
/* 2097 */         if ((locallyScopedConnection.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConnection.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 2100 */           timeoutTask = new StatementImpl.CancelTask(this, this);
/* 2101 */           locallyScopedConnection.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */ 
/* 2105 */         rs = locallyScopedConnection.execSQL(this, null, maxRowsToRetrieve, sendPacket, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, metadataFromCache, isBatch);
/*      */ 
/* 2110 */         if (timeoutTask != null) {
/* 2111 */           timeoutTask.cancel();
/*      */ 
/* 2113 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 2114 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 2117 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 2120 */         synchronized (this.cancelTimeoutMutex) {
/* 2121 */           if (this.wasCancelled) {
/* 2122 */             SQLException cause = null;
/*      */ 
/* 2124 */             if (this.wasCancelledByTimeout)
/* 2125 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 2127 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 2130 */             resetCancelledState();
/*      */ 
/* 2132 */             throw cause;
/*      */           }
/*      */         }
/*      */       } finally {
/* 2136 */         if (timeoutTask != null) {
/* 2137 */           timeoutTask.cancel();
/*      */         }
/*      */       }
/*      */ 
/* 2141 */       return rs;
/*      */     } catch (NullPointerException npe) {
/* 2143 */       checkClosed();
/*      */     }
/*      */ 
/* 2147 */     throw npe;
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/* 2161 */     checkClosed();
/*      */ 
/* 2163 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 2165 */     checkForDml(this.originalSql, this.firstCharOfStmt);
/*      */ 
/* 2167 */     CachedResultSetMetaData cachedMetadata = null;
/*      */ 
/* 2173 */     synchronized (locallyScopedConn.getMutex()) {
/* 2174 */       clearWarnings();
/*      */ 
/* 2176 */       boolean doStreaming = createStreamingResultSet();
/*      */ 
/* 2178 */       this.batchedGeneratedKeys = null;
/*      */ 
/* 2188 */       if ((doStreaming) && (this.connection.getNetTimeoutForStreamingResults() > 0))
/*      */       {
/* 2191 */         Statement stmt = null;
/*      */         try
/*      */         {
/* 2194 */           stmt = this.connection.createStatement();
/*      */ 
/* 2196 */           ((StatementImpl)stmt).executeSimpleNonQuery(this.connection, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
/*      */         }
/*      */         finally {
/* 2199 */           if (stmt != null) {
/* 2200 */             stmt.close();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2205 */       Buffer sendPacket = fillSendPacket();
/*      */ 
/* 2207 */       if ((this.results != null) && 
/* 2208 */         (!this.connection.getHoldResultsOpenOverStatementClose()) && 
/* 2209 */         (!this.holdResultsOpenOverClose)) {
/* 2210 */         this.results.realClose(false);
/*      */       }
/*      */ 
/* 2215 */       String oldCatalog = null;
/*      */ 
/* 2217 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 2218 */         oldCatalog = locallyScopedConn.getCatalog();
/* 2219 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/* 2225 */       if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 2226 */         cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
/*      */       }
/*      */ 
/* 2229 */       Field[] metadataFromCache = null;
/*      */ 
/* 2231 */       if (cachedMetadata != null) {
/* 2232 */         metadataFromCache = cachedMetadata.fields;
/*      */       }
/*      */ 
/* 2235 */       if (locallyScopedConn.useMaxRows())
/*      */       {
/* 2242 */         if (this.hasLimitClause) {
/* 2243 */           this.results = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), true, metadataFromCache, false);
/*      */         }
/*      */         else
/*      */         {
/* 2247 */           if (this.maxRows <= 0) {
/* 2248 */             executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */           }
/*      */           else {
/* 2251 */             executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows);
/*      */           }
/*      */ 
/* 2255 */           this.results = executeInternal(-1, sendPacket, doStreaming, true, metadataFromCache, false);
/*      */ 
/* 2259 */           if (oldCatalog != null)
/* 2260 */             this.connection.setCatalog(oldCatalog);
/*      */         }
/*      */       }
/*      */       else {
/* 2264 */         this.results = executeInternal(-1, sendPacket, doStreaming, true, metadataFromCache, false);
/*      */       }
/*      */ 
/* 2269 */       if (oldCatalog != null) {
/* 2270 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */ 
/* 2273 */       if (cachedMetadata != null) {
/* 2274 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
/*      */       }
/* 2277 */       else if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 2278 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, null, this.results);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2284 */     this.lastInsertId = this.results.getUpdateID();
/*      */ 
/* 2286 */     return this.results;
/*      */   }
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/* 2301 */     return executeUpdate(true, false);
/*      */   }
/*      */ 
/*      */   protected int executeUpdate(boolean clearBatchedGeneratedKeysAndWarnings, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/* 2311 */     if (clearBatchedGeneratedKeysAndWarnings) {
/* 2312 */       clearWarnings();
/* 2313 */       this.batchedGeneratedKeys = null;
/*      */     }
/*      */ 
/* 2316 */     return executeUpdate(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull, isBatch);
/*      */   }
/*      */ 
/*      */   protected int executeUpdate(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths, boolean[] batchedIsNull, boolean isReallyBatch)
/*      */     throws SQLException
/*      */   {
/* 2344 */     checkClosed();
/*      */ 
/* 2346 */     ConnectionImpl locallyScopedConn = this.connection;
/*      */ 
/* 2348 */     if (locallyScopedConn.isReadOnly()) {
/* 2349 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.34") + Messages.getString("PreparedStatement.35"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2354 */     if ((this.firstCharOfStmt == 'S') && (isSelectQuery()))
/*      */     {
/* 2356 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.37"), "01S03", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2360 */     if ((this.results != null) && 
/* 2361 */       (!locallyScopedConn.getHoldResultsOpenOverStatementClose())) {
/* 2362 */       this.results.realClose(false);
/*      */     }
/*      */ 
/* 2366 */     ResultSetInternalMethods rs = null;
/*      */ 
/* 2371 */     synchronized (locallyScopedConn.getMutex()) {
/* 2372 */       Buffer sendPacket = fillSendPacket(batchedParameterStrings, batchedParameterStreams, batchedIsStream, batchedStreamLengths);
/*      */ 
/* 2376 */       String oldCatalog = null;
/*      */ 
/* 2378 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 2379 */         oldCatalog = locallyScopedConn.getCatalog();
/* 2380 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/* 2386 */       if (locallyScopedConn.useMaxRows()) {
/* 2387 */         executeSimpleNonQuery(locallyScopedConn, "SET OPTION SQL_SELECT_LIMIT=DEFAULT");
/*      */       }
/*      */ 
/* 2391 */       boolean oldInfoMsgState = false;
/*      */ 
/* 2393 */       if (this.retrieveGeneratedKeys) {
/* 2394 */         oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/* 2395 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       }
/*      */ 
/* 2398 */       rs = executeInternal(-1, sendPacket, false, false, null, isReallyBatch);
/*      */ 
/* 2401 */       if (this.retrieveGeneratedKeys) {
/* 2402 */         locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
/* 2403 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       }
/*      */ 
/* 2406 */       if (oldCatalog != null) {
/* 2407 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */     }
/*      */ 
/* 2411 */     this.results = rs;
/*      */ 
/* 2413 */     this.updateCount = rs.getUpdateCount();
/*      */ 
/* 2415 */     if ((containsOnDuplicateKeyUpdateInSQL()) && (this.compensateForOnDuplicateKeyUpdate))
/*      */     {
/* 2417 */       if ((this.updateCount == 2L) || (this.updateCount == 0L)) {
/* 2418 */         this.updateCount = 1L;
/*      */       }
/*      */     }
/*      */ 
/* 2422 */     int truncatedUpdateCount = 0;
/*      */ 
/* 2424 */     if (this.updateCount > 2147483647L)
/* 2425 */       truncatedUpdateCount = 2147483647;
/*      */     else {
/* 2427 */       truncatedUpdateCount = (int)this.updateCount;
/*      */     }
/*      */ 
/* 2430 */     this.lastInsertId = rs.getUpdateID();
/*      */ 
/* 2432 */     return truncatedUpdateCount;
/*      */   }
/*      */ 
/*      */   protected boolean containsOnDuplicateKeyUpdateInSQL() {
/* 2436 */     return this.parseInfo.isOnDuplicateKeyUpdate;
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket()
/*      */     throws SQLException
/*      */   {
/* 2451 */     return fillSendPacket(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths);
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths)
/*      */     throws SQLException
/*      */   {
/* 2475 */     Buffer sendPacket = this.connection.getIO().getSharedSendPacket();
/*      */ 
/* 2477 */     sendPacket.clear();
/*      */ 
/* 2479 */     sendPacket.writeByte(3);
/*      */ 
/* 2481 */     boolean useStreamLengths = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/* 2488 */     int ensurePacketSize = 0;
/*      */ 
/* 2490 */     String statementComment = this.connection.getStatementComment();
/*      */ 
/* 2492 */     byte[] commentAsBytes = null;
/*      */ 
/* 2494 */     if (statementComment != null) {
/* 2495 */       if (this.charConverter != null)
/* 2496 */         commentAsBytes = this.charConverter.toBytes(statementComment);
/*      */       else {
/* 2498 */         commentAsBytes = StringUtils.getBytes(statementComment, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2504 */       ensurePacketSize += commentAsBytes.length;
/* 2505 */       ensurePacketSize += 6;
/*      */     }
/*      */ 
/* 2508 */     for (int i = 0; i < batchedParameterStrings.length; i++) {
/* 2509 */       if ((batchedIsStream[i] != 0) && (useStreamLengths)) {
/* 2510 */         ensurePacketSize += batchedStreamLengths[i];
/*      */       }
/*      */     }
/*      */ 
/* 2514 */     if (ensurePacketSize != 0) {
/* 2515 */       sendPacket.ensureCapacity(ensurePacketSize);
/*      */     }
/*      */ 
/* 2518 */     if (commentAsBytes != null) {
/* 2519 */       sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 2520 */       sendPacket.writeBytesNoNull(commentAsBytes);
/* 2521 */       sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */     }
/*      */ 
/* 2524 */     for (int i = 0; i < batchedParameterStrings.length; i++) {
/* 2525 */       checkAllParametersSet(batchedParameterStrings[i], batchedParameterStreams[i], i);
/*      */ 
/* 2528 */       sendPacket.writeBytesNoNull(this.staticSqlStrings[i]);
/*      */ 
/* 2530 */       if (batchedIsStream[i] != 0) {
/* 2531 */         streamToBytes(sendPacket, batchedParameterStreams[i], true, batchedStreamLengths[i], useStreamLengths);
/*      */       }
/*      */       else {
/* 2534 */         sendPacket.writeBytesNoNull(batchedParameterStrings[i]);
/*      */       }
/*      */     }
/*      */ 
/* 2538 */     sendPacket.writeBytesNoNull(this.staticSqlStrings[batchedParameterStrings.length]);
/*      */ 
/* 2541 */     return sendPacket;
/*      */   }
/*      */ 
/*      */   private void checkAllParametersSet(byte[] parameterString, InputStream parameterStream, int columnIndex) throws SQLException
/*      */   {
/* 2546 */     if ((parameterString == null) && (parameterStream == null))
/*      */     {
/* 2548 */       System.out.println(toString());
/* 2549 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.40") + (columnIndex + 1), "07001", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected PreparedStatement prepareBatchedInsertSQL(ConnectionImpl localConn, int numBatches)
/*      */     throws SQLException
/*      */   {
/* 2559 */     PreparedStatement pstmt = new PreparedStatement(localConn, "Rewritten batch of: " + this.originalSql, this.currentCatalog, this.parseInfo.getParseInfoForBatch(numBatches));
/* 2560 */     pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
/* 2561 */     pstmt.rewrittenBatchSize = numBatches;
/*      */ 
/* 2563 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public int getRewrittenBatchSize()
/*      */   {
/* 2569 */     return this.rewrittenBatchSize;
/*      */   }
/*      */ 
/*      */   public String getNonRewrittenSql() {
/* 2573 */     int indexOfBatch = this.originalSql.indexOf(" of: ");
/*      */ 
/* 2575 */     if (indexOfBatch != -1) {
/* 2576 */       return this.originalSql.substring(indexOfBatch + 5);
/*      */     }
/*      */ 
/* 2579 */     return this.originalSql;
/*      */   }
/*      */ 
/*      */   public byte[] getBytesRepresentation(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 2596 */     if (this.isStream[parameterIndex] != 0) {
/* 2597 */       return streamToBytes(this.parameterStreams[parameterIndex], false, this.streamLengths[parameterIndex], this.connection.getUseStreamLengthsInPrepStmts());
/*      */     }
/*      */ 
/* 2602 */     byte[] parameterVal = this.parameterValues[parameterIndex];
/*      */ 
/* 2604 */     if (parameterVal == null) {
/* 2605 */       return null;
/*      */     }
/*      */ 
/* 2608 */     if ((parameterVal[0] == 39) && (parameterVal[(parameterVal.length - 1)] == 39))
/*      */     {
/* 2610 */       byte[] valNoQuotes = new byte[parameterVal.length - 2];
/* 2611 */       System.arraycopy(parameterVal, 1, valNoQuotes, 0, parameterVal.length - 2);
/*      */ 
/* 2614 */       return valNoQuotes;
/*      */     }
/*      */ 
/* 2617 */     return parameterVal;
/*      */   }
/*      */ 
/*      */   protected byte[] getBytesRepresentationForBatch(int parameterIndex, int commandIndex)
/*      */     throws SQLException
/*      */   {
/* 2629 */     Object batchedArg = this.batchedArgs.get(commandIndex);
/* 2630 */     if ((batchedArg instanceof String)) {
/*      */       try {
/* 2632 */         return ((String)batchedArg).getBytes(this.charEncoding);
/*      */       }
/*      */       catch (UnsupportedEncodingException uue) {
/* 2635 */         throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2642 */     BatchParams params = (BatchParams)batchedArg;
/* 2643 */     if (params.isStream[parameterIndex] != 0) {
/* 2644 */       return streamToBytes(params.parameterStreams[parameterIndex], false, params.streamLengths[parameterIndex], this.connection.getUseStreamLengthsInPrepStmts());
/*      */     }
/*      */ 
/* 2647 */     byte[] parameterVal = params.parameterStrings[parameterIndex];
/* 2648 */     if (parameterVal == null) {
/* 2649 */       return null;
/*      */     }
/* 2651 */     if ((parameterVal[0] == 39) && (parameterVal[(parameterVal.length - 1)] == 39))
/*      */     {
/* 2653 */       byte[] valNoQuotes = new byte[parameterVal.length - 2];
/* 2654 */       System.arraycopy(parameterVal, 1, valNoQuotes, 0, parameterVal.length - 2);
/*      */ 
/* 2657 */       return valNoQuotes;
/*      */     }
/*      */ 
/* 2660 */     return parameterVal;
/*      */   }
/*      */ 
/*      */   private final String getDateTimePattern(String dt, boolean toTime)
/*      */     throws Exception
/*      */   {
/* 2670 */     int dtLength = dt != null ? dt.length() : 0;
/*      */ 
/* 2672 */     if ((dtLength >= 8) && (dtLength <= 10)) {
/* 2673 */       int dashCount = 0;
/* 2674 */       boolean isDateOnly = true;
/*      */ 
/* 2676 */       for (int i = 0; i < dtLength; i++) {
/* 2677 */         char c = dt.charAt(i);
/*      */ 
/* 2679 */         if ((!Character.isDigit(c)) && (c != '-')) {
/* 2680 */           isDateOnly = false;
/*      */ 
/* 2682 */           break;
/*      */         }
/*      */ 
/* 2685 */         if (c == '-') {
/* 2686 */           dashCount++;
/*      */         }
/*      */       }
/*      */ 
/* 2690 */       if ((isDateOnly) && (dashCount == 2)) {
/* 2691 */         return "yyyy-MM-dd";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2698 */     boolean colonsOnly = true;
/*      */ 
/* 2700 */     for (int i = 0; i < dtLength; i++) {
/* 2701 */       char c = dt.charAt(i);
/*      */ 
/* 2703 */       if ((!Character.isDigit(c)) && (c != ':')) {
/* 2704 */         colonsOnly = false;
/*      */ 
/* 2706 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 2710 */     if (colonsOnly) {
/* 2711 */       return "HH:mm:ss";
/*      */     }
/*      */ 
/* 2720 */     StringReader reader = new StringReader(dt + " ");
/* 2721 */     ArrayList vec = new ArrayList();
/* 2722 */     ArrayList vecRemovelist = new ArrayList();
/* 2723 */     Object[] nv = new Object[3];
/*      */ 
/* 2725 */     nv[0] = Constants.characterValueOf('y');
/* 2726 */     nv[1] = new StringBuffer();
/* 2727 */     nv[2] = Constants.integerValueOf(0);
/* 2728 */     vec.add(nv);
/*      */ 
/* 2730 */     if (toTime) {
/* 2731 */       nv = new Object[3];
/* 2732 */       nv[0] = Constants.characterValueOf('h');
/* 2733 */       nv[1] = new StringBuffer();
/* 2734 */       nv[2] = Constants.integerValueOf(0);
/* 2735 */       vec.add(nv);
/*      */     }
/*      */     int z;
/* 2738 */     while ((z = reader.read()) != -1) {
/* 2739 */       char separator = (char)z;
/* 2740 */       int maxvecs = vec.size();
/*      */ 
/* 2742 */       for (int count = 0; count < maxvecs; count++) {
/* 2743 */         Object[] v = (Object[])vec.get(count);
/* 2744 */         int n = ((Integer)v[2]).intValue();
/* 2745 */         char c = getSuccessor(((Character)v[0]).charValue(), n);
/*      */ 
/* 2747 */         if (!Character.isLetterOrDigit(separator)) {
/* 2748 */           if ((c == ((Character)v[0]).charValue()) && (c != 'S')) {
/* 2749 */             vecRemovelist.add(v);
/*      */           } else {
/* 2751 */             ((StringBuffer)v[1]).append(separator);
/*      */ 
/* 2753 */             if ((c == 'X') || (c == 'Y'))
/* 2754 */               v[2] = Constants.integerValueOf(4);
/*      */           }
/*      */         }
/*      */         else {
/* 2758 */           if (c == 'X') {
/* 2759 */             c = 'y';
/* 2760 */             nv = new Object[3];
/* 2761 */             nv[1] = new StringBuffer(((StringBuffer)v[1]).toString()).append('M');
/*      */ 
/* 2763 */             nv[0] = Constants.characterValueOf('M');
/* 2764 */             nv[2] = Constants.integerValueOf(1);
/* 2765 */             vec.add(nv);
/* 2766 */           } else if (c == 'Y') {
/* 2767 */             c = 'M';
/* 2768 */             nv = new Object[3];
/* 2769 */             nv[1] = new StringBuffer(((StringBuffer)v[1]).toString()).append('d');
/*      */ 
/* 2771 */             nv[0] = Constants.characterValueOf('d');
/* 2772 */             nv[2] = Constants.integerValueOf(1);
/* 2773 */             vec.add(nv);
/*      */           }
/*      */ 
/* 2776 */           ((StringBuffer)v[1]).append(c);
/*      */ 
/* 2778 */           if (c == ((Character)v[0]).charValue()) {
/* 2779 */             v[2] = Constants.integerValueOf(n + 1);
/*      */           } else {
/* 2781 */             v[0] = Constants.characterValueOf(c);
/* 2782 */             v[2] = Constants.integerValueOf(1);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2787 */       int size = vecRemovelist.size();
/*      */ 
/* 2789 */       for (int i = 0; i < size; i++) {
/* 2790 */         Object[] v = (Object[])vecRemovelist.get(i);
/* 2791 */         vec.remove(v);
/*      */       }
/*      */ 
/* 2794 */       vecRemovelist.clear();
/*      */     }
/*      */ 
/* 2797 */     int size = vec.size();
/*      */ 
/* 2799 */     for (int i = 0; i < size; i++) {
/* 2800 */       Object[] v = (Object[])vec.get(i);
/* 2801 */       char c = ((Character)v[0]).charValue();
/* 2802 */       int n = ((Integer)v[2]).intValue();
/*      */ 
/* 2804 */       boolean bk = getSuccessor(c, n) != c;
/* 2805 */       boolean atEnd = ((c == 's') || (c == 'm') || ((c == 'h') && (toTime))) && (bk);
/* 2806 */       boolean finishesAtDate = (bk) && (c == 'd') && (!toTime);
/* 2807 */       boolean containsEnd = ((StringBuffer)v[1]).toString().indexOf('W') != -1;
/*      */ 
/* 2810 */       if (((!atEnd) && (!finishesAtDate)) || (containsEnd)) {
/* 2811 */         vecRemovelist.add(v);
/*      */       }
/*      */     }
/*      */ 
/* 2815 */     size = vecRemovelist.size();
/*      */ 
/* 2817 */     for (int i = 0; i < size; i++) {
/* 2818 */       vec.remove(vecRemovelist.get(i));
/*      */     }
/*      */ 
/* 2821 */     vecRemovelist.clear();
/* 2822 */     Object[] v = (Object[])vec.get(0);
/*      */ 
/* 2824 */     StringBuffer format = (StringBuffer)v[1];
/* 2825 */     format.setLength(format.length() - 1);
/*      */ 
/* 2827 */     return format.toString();
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 2853 */     if (!isSelectQuery()) {
/* 2854 */       return null;
/*      */     }
/*      */ 
/* 2857 */     PreparedStatement mdStmt = null;
/* 2858 */     ResultSet mdRs = null;
/*      */ 
/* 2860 */     if (this.pstmtResultMetaData == null) {
/*      */       try {
/* 2862 */         mdStmt = new PreparedStatement(this.connection, this.originalSql, this.currentCatalog, this.parseInfo);
/*      */ 
/* 2865 */         mdStmt.setMaxRows(0);
/*      */ 
/* 2867 */         int paramCount = this.parameterValues.length;
/*      */ 
/* 2869 */         for (int i = 1; i <= paramCount; i++) {
/* 2870 */           mdStmt.setString(i, "");
/*      */         }
/*      */ 
/* 2873 */         boolean hadResults = mdStmt.execute();
/*      */ 
/* 2875 */         if (hadResults) {
/* 2876 */           mdRs = mdStmt.getResultSet();
/*      */ 
/* 2878 */           this.pstmtResultMetaData = mdRs.getMetaData();
/*      */         } else {
/* 2880 */           this.pstmtResultMetaData = new ResultSetMetaData(new Field[0], this.connection.getUseOldAliasMetadataBehavior(), getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 2885 */         SQLException sqlExRethrow = null;
/*      */ 
/* 2887 */         if (mdRs != null) {
/*      */           try {
/* 2889 */             mdRs.close();
/*      */           } catch (SQLException sqlEx) {
/* 2891 */             sqlExRethrow = sqlEx;
/*      */           }
/*      */ 
/* 2894 */           mdRs = null;
/*      */         }
/*      */ 
/* 2897 */         if (mdStmt != null) {
/*      */           try {
/* 2899 */             mdStmt.close();
/*      */           } catch (SQLException sqlEx) {
/* 2901 */             sqlExRethrow = sqlEx;
/*      */           }
/*      */ 
/* 2904 */           mdStmt = null;
/*      */         }
/*      */ 
/* 2907 */         if (sqlExRethrow != null) {
/* 2908 */           throw sqlExRethrow;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2913 */     return this.pstmtResultMetaData;
/*      */   }
/*      */ 
/*      */   protected boolean isSelectQuery() {
/* 2917 */     return StringUtils.startsWithIgnoreCaseAndWs(StringUtils.stripComments(this.originalSql, "'\"", "'\"", true, false, true, true), "SELECT");
/*      */   }
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 2928 */     if (this.parameterMetaData == null) {
/* 2929 */       if (this.connection.getGenerateSimpleParameterMetadata())
/* 2930 */         this.parameterMetaData = new MysqlParameterMetadata(this.parameterCount);
/*      */       else {
/* 2932 */         this.parameterMetaData = new MysqlParameterMetadata(null, this.parameterCount, getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2937 */     return this.parameterMetaData;
/*      */   }
/*      */ 
/*      */   ParseInfo getParseInfo() {
/* 2941 */     return this.parseInfo;
/*      */   }
/*      */ 
/*      */   private final char getSuccessor(char c, int n) {
/* 2945 */     return (c == 's') && (n < 2) ? 's' : c == 'm' ? 's' : (c == 'm') && (n < 2) ? 'm' : c == 'H' ? 'm' : (c == 'H') && (n < 2) ? 'H' : c == 'd' ? 'H' : (c == 'd') && (n < 2) ? 'd' : c == 'M' ? 'd' : (c == 'M') && (n < 3) ? 'M' : (c == 'M') && (n == 2) ? 'Y' : c == 'y' ? 'M' : (c == 'y') && (n < 4) ? 'y' : (c == 'y') && (n == 2) ? 'X' : 'W';
/*      */   }
/*      */ 
/*      */   private final void hexEscapeBlock(byte[] buf, Buffer packet, int size)
/*      */     throws SQLException
/*      */   {
/* 2971 */     for (int i = 0; i < size; i++) {
/* 2972 */       byte b = buf[i];
/* 2973 */       int lowBits = (b & 0xFF) / 16;
/* 2974 */       int highBits = (b & 0xFF) % 16;
/*      */ 
/* 2976 */       packet.writeByte(HEX_DIGITS[lowBits]);
/* 2977 */       packet.writeByte(HEX_DIGITS[highBits]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initializeFromParseInfo() throws SQLException {
/* 2982 */     this.staticSqlStrings = this.parseInfo.staticSql;
/* 2983 */     this.hasLimitClause = this.parseInfo.foundLimitClause;
/* 2984 */     this.isLoadDataQuery = this.parseInfo.foundLoadData;
/* 2985 */     this.firstCharOfStmt = this.parseInfo.firstStmtChar;
/*      */ 
/* 2987 */     this.parameterCount = (this.staticSqlStrings.length - 1);
/*      */ 
/* 2989 */     this.parameterValues = new byte[this.parameterCount][];
/* 2990 */     this.parameterStreams = new InputStream[this.parameterCount];
/* 2991 */     this.isStream = new boolean[this.parameterCount];
/* 2992 */     this.streamLengths = new int[this.parameterCount];
/* 2993 */     this.isNull = new boolean[this.parameterCount];
/* 2994 */     this.parameterTypes = new int[this.parameterCount];
/*      */ 
/* 2996 */     clearParameters();
/*      */ 
/* 2998 */     for (int j = 0; j < this.parameterCount; j++)
/* 2999 */       this.isStream[j] = false;
/*      */   }
/*      */ 
/*      */   boolean isNull(int paramIndex)
/*      */   {
/* 3004 */     return this.isNull[paramIndex];
/*      */   }
/*      */   private final int readblock(InputStream i, byte[] b) throws SQLException {
/*      */     SQLException sqlEx;
/*      */     try { return i.read(b);
/*      */     } catch (Throwable ex) {
/* 3011 */       sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), "S1000", getExceptionInterceptor());
/*      */ 
/* 3013 */       sqlEx.initCause(ex);
/*      */     }
/* 3015 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private final int readblock(InputStream i, byte[] b, int length) throws SQLException {
/*      */     SQLException sqlEx;
/*      */     try {
/* 3022 */       int lengthToRead = length;
/*      */ 
/* 3024 */       if (lengthToRead > b.length) {
/* 3025 */         lengthToRead = b.length;
/*      */       }
/*      */ 
/* 3028 */       return i.read(b, 0, lengthToRead);
/*      */     } catch (Throwable ex) {
/* 3030 */       sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), "S1000", getExceptionInterceptor());
/*      */ 
/* 3032 */       sqlEx.initCause(ex);
/*      */     }
/* 3034 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/* 3049 */     if ((this.useUsageAdvisor) && 
/* 3050 */       (this.numberOfExecutions <= 1)) {
/* 3051 */       String message = Messages.getString("PreparedStatement.43");
/*      */ 
/* 3053 */       this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.currentCatalog, this.connectionId, getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */     }
/*      */ 
/* 3062 */     super.realClose(calledExplicitly, closeOpenResults);
/*      */ 
/* 3064 */     this.dbmd = null;
/* 3065 */     this.originalSql = null;
/* 3066 */     this.staticSqlStrings = ((byte[][])null);
/* 3067 */     this.parameterValues = ((byte[][])null);
/* 3068 */     this.parameterStreams = null;
/* 3069 */     this.isStream = null;
/* 3070 */     this.streamLengths = null;
/* 3071 */     this.isNull = null;
/* 3072 */     this.streamConvertBuf = null;
/* 3073 */     this.parameterTypes = null;
/*      */   }
/*      */ 
/*      */   public void setArray(int i, Array x)
/*      */     throws SQLException
/*      */   {
/* 3090 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 3117 */     if (x == null)
/* 3118 */       setNull(parameterIndex, 12);
/*      */     else
/* 3120 */       setBinaryStream(parameterIndex, x, length);
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 3138 */     if (x == null) {
/* 3139 */       setNull(parameterIndex, 3);
/*      */     } else {
/* 3141 */       setInternal(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString(x)));
/*      */ 
/* 3144 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 3;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 3170 */     if (x == null) {
/* 3171 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 3173 */       int parameterIndexOffset = getParameterIndexOffset();
/*      */ 
/* 3175 */       if ((parameterIndex < 1) || (parameterIndex > this.staticSqlStrings.length))
/*      */       {
/* 3177 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.2") + parameterIndex + Messages.getString("PreparedStatement.3") + this.staticSqlStrings.length + Messages.getString("PreparedStatement.4"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3182 */       if ((parameterIndexOffset == -1) && (parameterIndex == 1)) {
/* 3183 */         throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3188 */       this.parameterStreams[(parameterIndex - 1 + parameterIndexOffset)] = x;
/* 3189 */       this.isStream[(parameterIndex - 1 + parameterIndexOffset)] = true;
/* 3190 */       this.streamLengths[(parameterIndex - 1 + parameterIndexOffset)] = length;
/* 3191 */       this.isNull[(parameterIndex - 1 + parameterIndexOffset)] = false;
/* 3192 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2004;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException
/*      */   {
/* 3198 */     setBinaryStream(parameterIndex, inputStream, (int)length);
/*      */   }
/*      */ 
/*      */   public void setBlob(int i, Blob x)
/*      */     throws SQLException
/*      */   {
/* 3213 */     if (x == null) {
/* 3214 */       setNull(i, 2004);
/*      */     } else {
/* 3216 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */ 
/* 3218 */       bytesOut.write(39);
/* 3219 */       escapeblockFast(x.getBytes(1L, (int)x.length()), bytesOut, (int)x.length());
/*      */ 
/* 3221 */       bytesOut.write(39);
/*      */ 
/* 3223 */       setInternal(i, bytesOut.toByteArray());
/*      */ 
/* 3225 */       this.parameterTypes[(i - 1 + getParameterIndexOffset())] = 2004;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBoolean(int parameterIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 3242 */     if (this.useTrueBoolean) {
/* 3243 */       setInternal(parameterIndex, x ? "1" : "0");
/*      */     } else {
/* 3245 */       setInternal(parameterIndex, x ? "'t'" : "'f'");
/*      */ 
/* 3247 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 16;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setByte(int parameterIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 3264 */     setInternal(parameterIndex, String.valueOf(x));
/*      */ 
/* 3266 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -6;
/*      */   }
/*      */ 
/*      */   public void setBytes(int parameterIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 3283 */     setBytes(parameterIndex, x, true, true);
/*      */ 
/* 3285 */     if (x != null)
/* 3286 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -2;
/*      */   }
/*      */ 
/*      */   protected void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars)
/*      */     throws SQLException
/*      */   {
/* 3293 */     if (x == null) {
/* 3294 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 3296 */       String connectionEncoding = this.connection.getEncoding();
/*      */ 
/* 3298 */       if ((this.connection.isNoBackslashEscapesSet()) || ((escapeForMBChars) && (this.connection.getUseUnicode()) && (connectionEncoding != null) && (CharsetMapping.isMultibyteCharset(connectionEncoding))))
/*      */       {
/* 3306 */         ByteArrayOutputStream bOut = new ByteArrayOutputStream(x.length * 2 + 3);
/*      */ 
/* 3308 */         bOut.write(120);
/* 3309 */         bOut.write(39);
/*      */ 
/* 3311 */         for (int i = 0; i < x.length; i++) {
/* 3312 */           int lowBits = (x[i] & 0xFF) / 16;
/* 3313 */           int highBits = (x[i] & 0xFF) % 16;
/*      */ 
/* 3315 */           bOut.write(HEX_DIGITS[lowBits]);
/* 3316 */           bOut.write(HEX_DIGITS[highBits]);
/*      */         }
/*      */ 
/* 3319 */         bOut.write(39);
/*      */ 
/* 3321 */         setInternal(parameterIndex, bOut.toByteArray());
/*      */ 
/* 3323 */         return;
/*      */       }
/*      */ 
/* 3327 */       int numBytes = x.length;
/*      */ 
/* 3329 */       int pad = 2;
/*      */ 
/* 3331 */       boolean needsIntroducer = (checkForIntroducer) && (this.connection.versionMeetsMinimum(4, 1, 0));
/*      */ 
/* 3334 */       if (needsIntroducer) {
/* 3335 */         pad += 7;
/*      */       }
/*      */ 
/* 3338 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream(numBytes + pad);
/*      */ 
/* 3341 */       if (needsIntroducer) {
/* 3342 */         bOut.write(95);
/* 3343 */         bOut.write(98);
/* 3344 */         bOut.write(105);
/* 3345 */         bOut.write(110);
/* 3346 */         bOut.write(97);
/* 3347 */         bOut.write(114);
/* 3348 */         bOut.write(121);
/*      */       }
/* 3350 */       bOut.write(39);
/*      */ 
/* 3352 */       for (int i = 0; i < numBytes; i++) {
/* 3353 */         byte b = x[i];
/*      */ 
/* 3355 */         switch (b) {
/*      */         case 0:
/* 3357 */           bOut.write(92);
/* 3358 */           bOut.write(48);
/*      */ 
/* 3360 */           break;
/*      */         case 10:
/* 3363 */           bOut.write(92);
/* 3364 */           bOut.write(110);
/*      */ 
/* 3366 */           break;
/*      */         case 13:
/* 3369 */           bOut.write(92);
/* 3370 */           bOut.write(114);
/*      */ 
/* 3372 */           break;
/*      */         case 92:
/* 3375 */           bOut.write(92);
/* 3376 */           bOut.write(92);
/*      */ 
/* 3378 */           break;
/*      */         case 39:
/* 3381 */           bOut.write(92);
/* 3382 */           bOut.write(39);
/*      */ 
/* 3384 */           break;
/*      */         case 34:
/* 3387 */           bOut.write(92);
/* 3388 */           bOut.write(34);
/*      */ 
/* 3390 */           break;
/*      */         case 26:
/* 3393 */           bOut.write(92);
/* 3394 */           bOut.write(90);
/*      */ 
/* 3396 */           break;
/*      */         default:
/* 3399 */           bOut.write(b);
/*      */         }
/*      */       }
/*      */ 
/* 3403 */       bOut.write(39);
/*      */ 
/* 3405 */       setInternal(parameterIndex, bOut.toByteArray());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes)
/*      */     throws SQLException
/*      */   {
/* 3423 */     byte[] parameterWithQuotes = new byte[parameterAsBytes.length + 2];
/* 3424 */     parameterWithQuotes[0] = 39;
/* 3425 */     System.arraycopy(parameterAsBytes, 0, parameterWithQuotes, 1, parameterAsBytes.length);
/*      */ 
/* 3427 */     parameterWithQuotes[(parameterAsBytes.length + 1)] = 39;
/*      */ 
/* 3429 */     setInternal(parameterIndex, parameterWithQuotes);
/*      */   }
/*      */ 
/*      */   protected void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) throws SQLException
/*      */   {
/* 3434 */     setInternal(parameterIndex, parameterAsBytes);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3462 */       if (reader == null) {
/* 3463 */         setNull(parameterIndex, -1);
/*      */       } else {
/* 3465 */         char[] c = null;
/* 3466 */         int len = 0;
/*      */ 
/* 3468 */         boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/* 3471 */         String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/* 3473 */         if ((useLength) && (length != -1)) {
/* 3474 */           c = new char[length];
/*      */ 
/* 3476 */           int numCharsRead = readFully(reader, c, length);
/*      */ 
/* 3481 */           if (forcedEncoding == null)
/* 3482 */             setString(parameterIndex, new String(c, 0, numCharsRead));
/*      */           else
/*      */             try {
/* 3485 */               setBytes(parameterIndex, new String(c, 0, numCharsRead).getBytes(forcedEncoding));
/*      */             }
/*      */             catch (UnsupportedEncodingException uee)
/*      */             {
/* 3489 */               throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */             }
/*      */         }
/*      */         else
/*      */         {
/* 3494 */           c = new char[4096];
/*      */ 
/* 3496 */           StringBuffer buf = new StringBuffer();
/*      */ 
/* 3498 */           while ((len = reader.read(c)) != -1) {
/* 3499 */             buf.append(c, 0, len);
/*      */           }
/*      */ 
/* 3502 */           if (forcedEncoding == null)
/* 3503 */             setString(parameterIndex, buf.toString());
/*      */           else {
/*      */             try {
/* 3506 */               setBytes(parameterIndex, buf.toString().getBytes(forcedEncoding));
/*      */             }
/*      */             catch (UnsupportedEncodingException uee) {
/* 3509 */               throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3515 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 3518 */       throw SQLError.createSQLException(ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int i, Clob x)
/*      */     throws SQLException
/*      */   {
/* 3535 */     if (x == null) {
/* 3536 */       setNull(i, 2005);
/*      */     }
/*      */     else {
/* 3539 */       String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/* 3541 */       if (forcedEncoding == null)
/* 3542 */         setString(i, x.getSubString(1L, (int)x.length()));
/*      */       else {
/*      */         try {
/* 3545 */           setBytes(i, x.getSubString(1L, (int)x.length()).getBytes(forcedEncoding));
/*      */         }
/*      */         catch (UnsupportedEncodingException uee) {
/* 3548 */           throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3553 */       this.parameterTypes[(i - 1 + getParameterIndexOffset())] = 2005;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x)
/*      */     throws SQLException
/*      */   {
/* 3571 */     setDate(parameterIndex, x, null);
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 3590 */     if (x == null) {
/* 3591 */       setNull(parameterIndex, 91);
/*      */     } else {
/* 3593 */       checkClosed();
/*      */ 
/* 3595 */       if (!this.useLegacyDatetimeCode) {
/* 3596 */         newSetDateInternal(parameterIndex, x, cal);
/*      */       }
/*      */       else
/*      */       {
/* 3600 */         SimpleDateFormat dateFormatter = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */ 
/* 3602 */         setInternal(parameterIndex, dateFormatter.format(x));
/*      */ 
/* 3604 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 91;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDouble(int parameterIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 3623 */     if ((!this.connection.getAllowNanAndInf()) && ((x == (1.0D / 0.0D)) || (x == (-1.0D / 0.0D)) || (Double.isNaN(x))))
/*      */     {
/* 3626 */       throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3632 */     setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */ 
/* 3635 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 8;
/*      */   }
/*      */ 
/*      */   public void setFloat(int parameterIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 3651 */     setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */ 
/* 3654 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 6;
/*      */   }
/*      */ 
/*      */   public void setInt(int parameterIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 3670 */     setInternal(parameterIndex, String.valueOf(x));
/*      */ 
/* 3672 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 4;
/*      */   }
/*      */ 
/*      */   protected final void setInternal(int paramIndex, byte[] val) throws SQLException
/*      */   {
/* 3677 */     if (this.isClosed) {
/* 3678 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.48"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3682 */     int parameterIndexOffset = getParameterIndexOffset();
/*      */ 
/* 3684 */     checkBounds(paramIndex, parameterIndexOffset);
/*      */ 
/* 3686 */     this.isStream[(paramIndex - 1 + parameterIndexOffset)] = false;
/* 3687 */     this.isNull[(paramIndex - 1 + parameterIndexOffset)] = false;
/* 3688 */     this.parameterStreams[(paramIndex - 1 + parameterIndexOffset)] = null;
/* 3689 */     this.parameterValues[(paramIndex - 1 + parameterIndexOffset)] = val;
/*      */   }
/*      */ 
/*      */   private void checkBounds(int paramIndex, int parameterIndexOffset) throws SQLException
/*      */   {
/* 3694 */     if (paramIndex < 1) {
/* 3695 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.49") + paramIndex + Messages.getString("PreparedStatement.50"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3699 */     if (paramIndex > this.parameterCount) {
/* 3700 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.51") + paramIndex + Messages.getString("PreparedStatement.52") + this.parameterValues.length + Messages.getString("PreparedStatement.53"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3705 */     if ((parameterIndexOffset == -1) && (paramIndex == 1))
/* 3706 */       throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected final void setInternal(int paramIndex, String val)
/*      */     throws SQLException
/*      */   {
/* 3713 */     checkClosed();
/*      */ 
/* 3715 */     byte[] parameterAsBytes = null;
/*      */ 
/* 3717 */     if (this.charConverter != null)
/* 3718 */       parameterAsBytes = this.charConverter.toBytes(val);
/*      */     else {
/* 3720 */       parameterAsBytes = StringUtils.getBytes(val, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3726 */     setInternal(paramIndex, parameterAsBytes);
/*      */   }
/*      */ 
/*      */   public void setLong(int parameterIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 3742 */     setInternal(parameterIndex, String.valueOf(x));
/*      */ 
/* 3744 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -5;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 3764 */     setInternal(parameterIndex, "null");
/* 3765 */     this.isNull[(parameterIndex - 1 + getParameterIndexOffset())] = true;
/*      */ 
/* 3767 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 0;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType, String arg)
/*      */     throws SQLException
/*      */   {
/* 3789 */     setNull(parameterIndex, sqlType);
/*      */ 
/* 3791 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 0;
/*      */   }
/*      */ 
/*      */   private void setNumericObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     Number parameterAsNum;
/*      */     Number parameterAsNum;
/* 3797 */     if ((parameterObj instanceof Boolean)) {
/* 3798 */       parameterAsNum = ((Boolean)parameterObj).booleanValue() ? Constants.integerValueOf(1) : Constants.integerValueOf(0);
/*      */     }
/* 3801 */     else if ((parameterObj instanceof String))
/*      */     {
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/* 3802 */       switch (targetSqlType) {
/*      */       case -7:
/* 3804 */         if (("1".equals((String)parameterObj)) || ("0".equals((String)parameterObj)))
/*      */         {
/* 3806 */           Number parameterAsNum = Integer.valueOf((String)parameterObj); break;
/*      */         }
/* 3808 */         boolean parameterAsBoolean = "true".equalsIgnoreCase((String)parameterObj);
/*      */ 
/* 3811 */         parameterAsNum = parameterAsBoolean ? Constants.integerValueOf(1) : Constants.integerValueOf(0);
/*      */ 
/* 3815 */         break;
/*      */       case -6:
/*      */       case 4:
/*      */       case 5:
/* 3820 */         parameterAsNum = Integer.valueOf((String)parameterObj);
/*      */ 
/* 3823 */         break;
/*      */       case -5:
/* 3826 */         parameterAsNum = Long.valueOf((String)parameterObj);
/*      */ 
/* 3829 */         break;
/*      */       case 7:
/* 3832 */         parameterAsNum = Float.valueOf((String)parameterObj);
/*      */ 
/* 3835 */         break;
/*      */       case 6:
/*      */       case 8:
/* 3839 */         parameterAsNum = Double.valueOf((String)parameterObj);
/*      */ 
/* 3842 */         break;
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       default:
/* 3847 */         parameterAsNum = new BigDecimal((String)parameterObj); break;
/*      */       }
/*      */     }
/*      */     else {
/* 3851 */       parameterAsNum = (Number)parameterObj;
/*      */     }
/*      */ 
/* 3854 */     switch (targetSqlType) {
/*      */     case -7:
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/* 3859 */       setInt(parameterIndex, parameterAsNum.intValue());
/*      */ 
/* 3861 */       break;
/*      */     case -5:
/* 3864 */       setLong(parameterIndex, parameterAsNum.longValue());
/*      */ 
/* 3866 */       break;
/*      */     case 7:
/* 3869 */       setFloat(parameterIndex, parameterAsNum.floatValue());
/*      */ 
/* 3871 */       break;
/*      */     case 6:
/*      */     case 8:
/* 3875 */       setDouble(parameterIndex, parameterAsNum.doubleValue());
/*      */ 
/* 3877 */       break;
/*      */     case 2:
/*      */     case 3:
/* 3882 */       if ((parameterAsNum instanceof BigDecimal)) {
/* 3883 */         BigDecimal scaledBigDecimal = null;
/*      */         try
/*      */         {
/* 3886 */           scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale);
/*      */         }
/*      */         catch (ArithmeticException ex) {
/*      */           try {
/* 3890 */             scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale, 4);
/*      */           }
/*      */           catch (ArithmeticException arEx)
/*      */           {
/* 3894 */             throw SQLError.createSQLException("Can't set scale of '" + scale + "' for DECIMAL argument '" + parameterAsNum + "'", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3903 */         setBigDecimal(parameterIndex, scaledBigDecimal);
/* 3904 */       } else if ((parameterAsNum instanceof BigInteger)) {
/* 3905 */         setBigDecimal(parameterIndex, new BigDecimal((BigInteger)parameterAsNum, scale));
/*      */       }
/*      */       else
/*      */       {
/* 3911 */         setBigDecimal(parameterIndex, new BigDecimal(parameterAsNum.doubleValue()));
/*      */       }
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj)
/*      */     throws SQLException
/*      */   {
/* 3933 */     if (parameterObj == null) {
/* 3934 */       setNull(parameterIndex, 1111);
/*      */     }
/* 3936 */     else if ((parameterObj instanceof Byte))
/* 3937 */       setInt(parameterIndex, ((Byte)parameterObj).intValue());
/* 3938 */     else if ((parameterObj instanceof String))
/* 3939 */       setString(parameterIndex, (String)parameterObj);
/* 3940 */     else if ((parameterObj instanceof BigDecimal))
/* 3941 */       setBigDecimal(parameterIndex, (BigDecimal)parameterObj);
/* 3942 */     else if ((parameterObj instanceof Short))
/* 3943 */       setShort(parameterIndex, ((Short)parameterObj).shortValue());
/* 3944 */     else if ((parameterObj instanceof Integer))
/* 3945 */       setInt(parameterIndex, ((Integer)parameterObj).intValue());
/* 3946 */     else if ((parameterObj instanceof Long))
/* 3947 */       setLong(parameterIndex, ((Long)parameterObj).longValue());
/* 3948 */     else if ((parameterObj instanceof Float))
/* 3949 */       setFloat(parameterIndex, ((Float)parameterObj).floatValue());
/* 3950 */     else if ((parameterObj instanceof Double))
/* 3951 */       setDouble(parameterIndex, ((Double)parameterObj).doubleValue());
/* 3952 */     else if ((parameterObj instanceof byte[]))
/* 3953 */       setBytes(parameterIndex, (byte[])parameterObj);
/* 3954 */     else if ((parameterObj instanceof java.sql.Date))
/* 3955 */       setDate(parameterIndex, (java.sql.Date)parameterObj);
/* 3956 */     else if ((parameterObj instanceof Time))
/* 3957 */       setTime(parameterIndex, (Time)parameterObj);
/* 3958 */     else if ((parameterObj instanceof Timestamp))
/* 3959 */       setTimestamp(parameterIndex, (Timestamp)parameterObj);
/* 3960 */     else if ((parameterObj instanceof Boolean)) {
/* 3961 */       setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */     }
/* 3963 */     else if ((parameterObj instanceof InputStream))
/* 3964 */       setBinaryStream(parameterIndex, (InputStream)parameterObj, -1);
/* 3965 */     else if ((parameterObj instanceof Blob))
/* 3966 */       setBlob(parameterIndex, (Blob)parameterObj);
/* 3967 */     else if ((parameterObj instanceof Clob))
/* 3968 */       setClob(parameterIndex, (Clob)parameterObj);
/* 3969 */     else if ((this.connection.getTreatUtilDateAsTimestamp()) && ((parameterObj instanceof java.util.Date)))
/*      */     {
/* 3971 */       setTimestamp(parameterIndex, new Timestamp(((java.util.Date)parameterObj).getTime()));
/*      */     }
/* 3973 */     else if ((parameterObj instanceof BigInteger))
/* 3974 */       setString(parameterIndex, parameterObj.toString());
/*      */     else
/* 3976 */       setSerializableObject(parameterIndex, parameterObj);
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/* 3997 */     if (!(parameterObj instanceof BigDecimal))
/* 3998 */       setObject(parameterIndex, parameterObj, targetSqlType, 0);
/*      */     else
/* 4000 */       setObject(parameterIndex, parameterObj, targetSqlType, ((BigDecimal)parameterObj).scale());
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 4036 */     if (parameterObj == null)
/* 4037 */       setNull(parameterIndex, 1111);
/*      */     else
/*      */       try {
/* 4040 */         switch (targetSqlType)
/*      */         {
/*      */         case 16:
/* 4060 */           if ((parameterObj instanceof Boolean)) {
/* 4061 */             setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */           }
/* 4064 */           else if ((parameterObj instanceof String)) {
/* 4065 */             setBoolean(parameterIndex, ("true".equalsIgnoreCase((String)parameterObj)) || (!"0".equalsIgnoreCase((String)parameterObj)));
/*      */           }
/* 4069 */           else if ((parameterObj instanceof Number)) {
/* 4070 */             int intValue = ((Number)parameterObj).intValue();
/*      */ 
/* 4072 */             setBoolean(parameterIndex, intValue != 0);
/*      */           }
/*      */           else
/*      */           {
/* 4076 */             throw SQLError.createSQLException("No conversion from " + parameterObj.getClass().getName() + " to Types.BOOLEAN possible.", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         case -7:
/*      */         case -6:
/*      */         case -5:
/*      */         case 2:
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/* 4092 */           setNumericObject(parameterIndex, parameterObj, targetSqlType, scale);
/*      */ 
/* 4094 */           break;
/*      */         case -1:
/*      */         case 1:
/*      */         case 12:
/* 4099 */           if ((parameterObj instanceof BigDecimal)) {
/* 4100 */             setString(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString((BigDecimal)parameterObj)));
/*      */           }
/*      */           else
/*      */           {
/* 4106 */             setString(parameterIndex, parameterObj.toString());
/*      */           }
/*      */ 
/* 4109 */           break;
/*      */         case 2005:
/* 4113 */           if ((parameterObj instanceof Clob))
/* 4114 */             setClob(parameterIndex, (Clob)parameterObj);
/*      */           else {
/* 4116 */             setString(parameterIndex, parameterObj.toString());
/*      */           }
/*      */ 
/* 4119 */           break;
/*      */         case -4:
/*      */         case -3:
/*      */         case -2:
/*      */         case 2004:
/* 4126 */           if ((parameterObj instanceof byte[]))
/* 4127 */             setBytes(parameterIndex, (byte[])parameterObj);
/* 4128 */           else if ((parameterObj instanceof Blob))
/* 4129 */             setBlob(parameterIndex, (Blob)parameterObj);
/*      */           else {
/* 4131 */             setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */           }
/*      */ 
/* 4138 */           break;
/*      */         case 91:
/*      */         case 93:
/*      */           java.util.Date parameterAsDate;
/*      */           java.util.Date parameterAsDate;
/* 4145 */           if ((parameterObj instanceof String)) {
/* 4146 */             ParsePosition pp = new ParsePosition(0);
/* 4147 */             DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, false), Locale.US);
/*      */ 
/* 4149 */             parameterAsDate = sdf.parse((String)parameterObj, pp);
/*      */           } else {
/* 4151 */             parameterAsDate = (java.util.Date)parameterObj;
/*      */           }
/*      */ 
/* 4154 */           switch (targetSqlType)
/*      */           {
/*      */           case 91:
/* 4157 */             if ((parameterAsDate instanceof java.sql.Date)) {
/* 4158 */               setDate(parameterIndex, (java.sql.Date)parameterAsDate);
/*      */             }
/*      */             else {
/* 4161 */               setDate(parameterIndex, new java.sql.Date(parameterAsDate.getTime()));
/*      */             }
/*      */ 
/* 4165 */             break;
/*      */           case 93:
/* 4169 */             if ((parameterAsDate instanceof Timestamp)) {
/* 4170 */               setTimestamp(parameterIndex, (Timestamp)parameterAsDate);
/*      */             }
/*      */             else {
/* 4173 */               setTimestamp(parameterIndex, new Timestamp(parameterAsDate.getTime()));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 4181 */           break;
/*      */         case 92:
/* 4185 */           if ((parameterObj instanceof String)) {
/* 4186 */             DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, true), Locale.US);
/*      */ 
/* 4188 */             setTime(parameterIndex, new Time(sdf.parse((String)parameterObj).getTime()));
/*      */           }
/* 4190 */           else if ((parameterObj instanceof Timestamp)) {
/* 4191 */             Timestamp xT = (Timestamp)parameterObj;
/* 4192 */             setTime(parameterIndex, new Time(xT.getTime()));
/*      */           } else {
/* 4194 */             setTime(parameterIndex, (Time)parameterObj);
/*      */           }
/*      */ 
/* 4197 */           break;
/*      */         case 1111:
/* 4200 */           setSerializableObject(parameterIndex, parameterObj);
/*      */ 
/* 4202 */           break;
/*      */         default:
/* 4205 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.16"), "S1000", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/* 4210 */         if ((ex instanceof SQLException)) {
/* 4211 */           throw ((SQLException)ex);
/*      */         }
/*      */ 
/* 4214 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString("PreparedStatement.18") + ex.getClass().getName() + Messages.getString("PreparedStatement.19") + ex.getMessage(), "S1000", getExceptionInterceptor());
/*      */ 
/* 4222 */         sqlEx.initCause(ex);
/*      */ 
/* 4224 */         throw sqlEx;
/*      */       }
/*      */   }
/*      */ 
/*      */   protected int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet)
/*      */     throws SQLException
/*      */   {
/* 4232 */     BatchParams paramArg = (BatchParams)paramSet;
/*      */ 
/* 4234 */     boolean[] isNullBatch = paramArg.isNull;
/* 4235 */     boolean[] isStreamBatch = paramArg.isStream;
/*      */ 
/* 4237 */     for (int j = 0; j < isNullBatch.length; j++) {
/* 4238 */       if (isNullBatch[j] != 0) {
/* 4239 */         batchedStatement.setNull(batchedParamIndex++, 0);
/*      */       }
/* 4241 */       else if (isStreamBatch[j] != 0) {
/* 4242 */         batchedStatement.setBinaryStream(batchedParamIndex++, paramArg.parameterStreams[j], paramArg.streamLengths[j]);
/*      */       }
/*      */       else
/*      */       {
/* 4246 */         ((PreparedStatement)batchedStatement).setBytesNoEscapeNoQuotes(batchedParamIndex++, paramArg.parameterStrings[j]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4253 */     return batchedParamIndex;
/*      */   }
/*      */ 
/*      */   public void setRef(int i, Ref x)
/*      */     throws SQLException
/*      */   {
/* 4270 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 4280 */     this.resultSetConcurrency = concurrencyFlag;
/*      */   }
/*      */ 
/*      */   void setResultSetType(int typeFlag)
/*      */   {
/* 4290 */     this.resultSetType = typeFlag;
/*      */   }
/*      */ 
/*      */   protected void setRetrieveGeneratedKeys(boolean retrieveGeneratedKeys)
/*      */   {
/* 4299 */     this.retrieveGeneratedKeys = retrieveGeneratedKeys;
/*      */   }
/*      */ 
/*      */   private final void setSerializableObject(int parameterIndex, Object parameterObj)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 4319 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/* 4320 */       ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
/* 4321 */       objectOut.writeObject(parameterObj);
/* 4322 */       objectOut.flush();
/* 4323 */       objectOut.close();
/* 4324 */       bytesOut.flush();
/* 4325 */       bytesOut.close();
/*      */ 
/* 4327 */       byte[] buf = bytesOut.toByteArray();
/* 4328 */       ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
/* 4329 */       setBinaryStream(parameterIndex, bytesIn, buf.length);
/* 4330 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -2;
/*      */     } catch (Exception ex) {
/* 4332 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.54") + ex.getClass().getName(), "S1009", getExceptionInterceptor());
/*      */ 
/* 4335 */       sqlEx.initCause(ex);
/*      */ 
/* 4337 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(int parameterIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 4354 */     setInternal(parameterIndex, String.valueOf(x));
/*      */ 
/* 4356 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 5;
/*      */   }
/*      */ 
/*      */   public void setString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 4374 */     if (x == null) {
/* 4375 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 4377 */       checkClosed();
/*      */ 
/* 4379 */       int stringLength = x.length();
/*      */ 
/* 4381 */       if (this.connection.isNoBackslashEscapesSet())
/*      */       {
/* 4384 */         boolean needsHexEscape = isEscapeNeededForString(x, stringLength);
/*      */ 
/* 4387 */         if (!needsHexEscape) {
/* 4388 */           byte[] parameterAsBytes = null;
/*      */ 
/* 4390 */           StringBuffer quotedString = new StringBuffer(x.length() + 2);
/* 4391 */           quotedString.append('\'');
/* 4392 */           quotedString.append(x);
/* 4393 */           quotedString.append('\'');
/*      */ 
/* 4395 */           if (!this.isLoadDataQuery) {
/* 4396 */             parameterAsBytes = StringUtils.getBytes(quotedString.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */           }
/*      */           else
/*      */           {
/* 4402 */             parameterAsBytes = quotedString.toString().getBytes();
/*      */           }
/*      */ 
/* 4405 */           setInternal(parameterIndex, parameterAsBytes);
/*      */         } else {
/* 4407 */           byte[] parameterAsBytes = null;
/*      */ 
/* 4409 */           if (!this.isLoadDataQuery) {
/* 4410 */             parameterAsBytes = StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */           }
/*      */           else
/*      */           {
/* 4416 */             parameterAsBytes = x.getBytes();
/*      */           }
/*      */ 
/* 4419 */           setBytes(parameterIndex, parameterAsBytes);
/*      */         }
/*      */ 
/* 4422 */         return;
/*      */       }
/*      */ 
/* 4425 */       String parameterAsString = x;
/* 4426 */       boolean needsQuoted = true;
/*      */ 
/* 4428 */       if ((this.isLoadDataQuery) || (isEscapeNeededForString(x, stringLength))) {
/* 4429 */         needsQuoted = false;
/*      */ 
/* 4431 */         StringBuffer buf = new StringBuffer((int)(x.length() * 1.1D));
/*      */ 
/* 4433 */         buf.append('\'');
/*      */ 
/* 4442 */         for (int i = 0; i < stringLength; i++) {
/* 4443 */           char c = x.charAt(i);
/*      */ 
/* 4445 */           switch (c) {
/*      */           case '\000':
/* 4447 */             buf.append('\\');
/* 4448 */             buf.append('0');
/*      */ 
/* 4450 */             break;
/*      */           case '\n':
/* 4453 */             buf.append('\\');
/* 4454 */             buf.append('n');
/*      */ 
/* 4456 */             break;
/*      */           case '\r':
/* 4459 */             buf.append('\\');
/* 4460 */             buf.append('r');
/*      */ 
/* 4462 */             break;
/*      */           case '\\':
/* 4465 */             buf.append('\\');
/* 4466 */             buf.append('\\');
/*      */ 
/* 4468 */             break;
/*      */           case '\'':
/* 4471 */             buf.append('\\');
/* 4472 */             buf.append('\'');
/*      */ 
/* 4474 */             break;
/*      */           case '"':
/* 4477 */             if (this.usingAnsiMode) {
/* 4478 */               buf.append('\\');
/*      */             }
/*      */ 
/* 4481 */             buf.append('"');
/*      */ 
/* 4483 */             break;
/*      */           case '\032':
/* 4486 */             buf.append('\\');
/* 4487 */             buf.append('Z');
/*      */ 
/* 4489 */             break;
/*      */           case '¥':
/*      */           case '₩':
/* 4494 */             if (this.charsetEncoder == null) break;
/* 4495 */             CharBuffer cbuf = CharBuffer.allocate(1);
/* 4496 */             ByteBuffer bbuf = ByteBuffer.allocate(1);
/* 4497 */             cbuf.put(c);
/* 4498 */             cbuf.position(0);
/* 4499 */             this.charsetEncoder.encode(cbuf, bbuf, true);
/* 4500 */             if (bbuf.get(0) != 92) break;
/* 4501 */             buf.append('\\');
/*      */           }
/*      */ 
/* 4507 */           buf.append(c);
/*      */         }
/*      */ 
/* 4511 */         buf.append('\'');
/*      */ 
/* 4513 */         parameterAsString = buf.toString();
/*      */       }
/*      */ 
/* 4516 */       byte[] parameterAsBytes = null;
/*      */ 
/* 4518 */       if (!this.isLoadDataQuery) {
/* 4519 */         if (needsQuoted) {
/* 4520 */           parameterAsBytes = StringUtils.getBytesWrapped(parameterAsString, '\'', '\'', this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */         }
/*      */         else
/*      */         {
/* 4525 */           parameterAsBytes = StringUtils.getBytes(parameterAsString, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 4532 */         parameterAsBytes = parameterAsString.getBytes();
/*      */       }
/*      */ 
/* 4535 */       setInternal(parameterIndex, parameterAsBytes);
/*      */ 
/* 4537 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 12;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isEscapeNeededForString(String x, int stringLength) {
/* 4542 */     boolean needsHexEscape = false;
/*      */ 
/* 4544 */     for (int i = 0; i < stringLength; i++) {
/* 4545 */       char c = x.charAt(i);
/*      */ 
/* 4547 */       switch (c)
/*      */       {
/*      */       case '\000':
/* 4550 */         needsHexEscape = true;
/* 4551 */         break;
/*      */       case '\n':
/* 4554 */         needsHexEscape = true;
/*      */ 
/* 4556 */         break;
/*      */       case '\r':
/* 4559 */         needsHexEscape = true;
/* 4560 */         break;
/*      */       case '\\':
/* 4563 */         needsHexEscape = true;
/*      */ 
/* 4565 */         break;
/*      */       case '\'':
/* 4568 */         needsHexEscape = true;
/*      */ 
/* 4570 */         break;
/*      */       case '"':
/* 4573 */         needsHexEscape = true;
/*      */ 
/* 4575 */         break;
/*      */       case '\032':
/* 4578 */         needsHexEscape = true;
/*      */       }
/*      */ 
/* 4582 */       if (needsHexEscape) {
/*      */         break;
/*      */       }
/*      */     }
/* 4586 */     return needsHexEscape;
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 4605 */     setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 4622 */     setTimeInternal(parameterIndex, x, null, Util.getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4643 */     if (x == null) {
/* 4644 */       setNull(parameterIndex, 92);
/*      */     } else {
/* 4646 */       checkClosed();
/*      */ 
/* 4648 */       if (!this.useLegacyDatetimeCode) {
/* 4649 */         newSetTimeInternal(parameterIndex, x, targetCalendar);
/*      */       } else {
/* 4651 */         Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */ 
/* 4653 */         synchronized (sessionCalendar) {
/* 4654 */           x = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         }
/*      */ 
/* 4661 */         setInternal(parameterIndex, "'" + x.toString() + "'");
/*      */       }
/*      */ 
/* 4664 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 92;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 4684 */     setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 4701 */     setTimestampInternal(parameterIndex, x, null, Util.getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   private void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4721 */     if (x == null) {
/* 4722 */       setNull(parameterIndex, 93);
/*      */     } else {
/* 4724 */       checkClosed();
/*      */ 
/* 4726 */       if (!this.useLegacyDatetimeCode) {
/* 4727 */         newSetTimestampInternal(parameterIndex, x, targetCalendar);
/*      */       } else {
/* 4729 */         String timestampString = null;
/*      */ 
/* 4731 */         Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 4735 */         synchronized (sessionCalendar) {
/* 4736 */           x = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         }
/*      */ 
/* 4743 */         if (this.connection.getUseSSPSCompatibleTimezoneShift())
/* 4744 */           doSSPSCompatibleTimezoneShift(parameterIndex, x, sessionCalendar);
/*      */         else {
/* 4746 */           synchronized (this) {
/* 4747 */             if (this.tsdf == null) {
/* 4748 */               this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss''", Locale.US);
/*      */             }
/*      */ 
/* 4751 */             timestampString = this.tsdf.format(x);
/*      */ 
/* 4766 */             setInternal(parameterIndex, timestampString);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4772 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 93;
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void newSetTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar) throws SQLException
/*      */   {
/* 4778 */     if (this.tsdf == null) {
/* 4779 */       this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss", Locale.US);
/*      */     }
/*      */ 
/* 4782 */     String timestampString = null;
/*      */ 
/* 4784 */     if (targetCalendar != null) {
/* 4785 */       targetCalendar.setTime(x);
/* 4786 */       this.tsdf.setTimeZone(targetCalendar.getTimeZone());
/*      */ 
/* 4788 */       timestampString = this.tsdf.format(x);
/*      */     } else {
/* 4790 */       this.tsdf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4791 */       timestampString = this.tsdf.format(x);
/*      */     }
/*      */ 
/* 4794 */     StringBuffer buf = new StringBuffer();
/* 4795 */     buf.append(timestampString);
/* 4796 */     buf.append('.');
/* 4797 */     buf.append(formatNanos(x.getNanos()));
/* 4798 */     buf.append('\'');
/*      */ 
/* 4800 */     setInternal(parameterIndex, buf.toString());
/*      */   }
/*      */ 
/*      */   private String formatNanos(int nanos)
/*      */   {
/* 4805 */     return "0";
/*      */   }
/*      */ 
/*      */   private synchronized void newSetTimeInternal(int parameterIndex, Time x, Calendar targetCalendar)
/*      */     throws SQLException
/*      */   {
/* 4835 */     if (this.tdf == null) {
/* 4836 */       this.tdf = new SimpleDateFormat("''HH:mm:ss''", Locale.US);
/*      */     }
/*      */ 
/* 4840 */     String timeString = null;
/*      */ 
/* 4842 */     if (targetCalendar != null) {
/* 4843 */       targetCalendar.setTime(x);
/* 4844 */       this.tdf.setTimeZone(targetCalendar.getTimeZone());
/*      */ 
/* 4846 */       timeString = this.tdf.format(x);
/*      */     } else {
/* 4848 */       this.tdf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4849 */       timeString = this.tdf.format(x);
/*      */     }
/*      */ 
/* 4852 */     setInternal(parameterIndex, timeString);
/*      */   }
/*      */ 
/*      */   private synchronized void newSetDateInternal(int parameterIndex, java.sql.Date x, Calendar targetCalendar) throws SQLException
/*      */   {
/* 4857 */     if (this.ddf == null) {
/* 4858 */       this.ddf = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */     }
/*      */ 
/* 4862 */     String timeString = null;
/*      */ 
/* 4864 */     if (targetCalendar != null) {
/* 4865 */       targetCalendar.setTime(x);
/* 4866 */       this.ddf.setTimeZone(targetCalendar.getTimeZone());
/*      */ 
/* 4868 */       timeString = this.ddf.format(x);
/*      */     } else {
/* 4870 */       this.ddf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4871 */       timeString = this.ddf.format(x);
/*      */     }
/*      */ 
/* 4874 */     setInternal(parameterIndex, timeString);
/*      */   }
/*      */ 
/*      */   private void doSSPSCompatibleTimezoneShift(int parameterIndex, Timestamp x, Calendar sessionCalendar) throws SQLException {
/* 4878 */     Calendar sessionCalendar2 = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 4883 */     synchronized (sessionCalendar2) {
/* 4884 */       java.util.Date oldTime = sessionCalendar2.getTime();
/*      */       try
/*      */       {
/* 4887 */         sessionCalendar2.setTime(x);
/*      */ 
/* 4889 */         int year = sessionCalendar2.get(1);
/* 4890 */         int month = sessionCalendar2.get(2) + 1;
/* 4891 */         int date = sessionCalendar2.get(5);
/*      */ 
/* 4893 */         int hour = sessionCalendar2.get(11);
/* 4894 */         int minute = sessionCalendar2.get(12);
/* 4895 */         int seconds = sessionCalendar2.get(13);
/*      */ 
/* 4897 */         StringBuffer tsBuf = new StringBuffer();
/*      */ 
/* 4899 */         tsBuf.append('\'');
/* 4900 */         tsBuf.append(year);
/*      */ 
/* 4902 */         tsBuf.append("-");
/*      */ 
/* 4904 */         if (month < 10) {
/* 4905 */           tsBuf.append('0');
/*      */         }
/*      */ 
/* 4908 */         tsBuf.append(month);
/*      */ 
/* 4910 */         tsBuf.append('-');
/*      */ 
/* 4912 */         if (date < 10) {
/* 4913 */           tsBuf.append('0');
/*      */         }
/*      */ 
/* 4916 */         tsBuf.append(date);
/*      */ 
/* 4918 */         tsBuf.append(' ');
/*      */ 
/* 4920 */         if (hour < 10) {
/* 4921 */           tsBuf.append('0');
/*      */         }
/*      */ 
/* 4924 */         tsBuf.append(hour);
/*      */ 
/* 4926 */         tsBuf.append(':');
/*      */ 
/* 4928 */         if (minute < 10) {
/* 4929 */           tsBuf.append('0');
/*      */         }
/*      */ 
/* 4932 */         tsBuf.append(minute);
/*      */ 
/* 4934 */         tsBuf.append(':');
/*      */ 
/* 4936 */         if (seconds < 10) {
/* 4937 */           tsBuf.append('0');
/*      */         }
/*      */ 
/* 4940 */         tsBuf.append(seconds);
/*      */ 
/* 4942 */         tsBuf.append('.');
/* 4943 */         tsBuf.append(formatNanos(x.getNanos()));
/* 4944 */         tsBuf.append('\'');
/*      */ 
/* 4946 */         setInternal(parameterIndex, tsBuf.toString());
/*      */       }
/*      */       finally {
/* 4949 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 4980 */     if (x == null) {
/* 4981 */       setNull(parameterIndex, 12);
/*      */     } else {
/* 4983 */       setBinaryStream(parameterIndex, x, length);
/*      */ 
/* 4985 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setURL(int parameterIndex, URL arg)
/*      */     throws SQLException
/*      */   {
/* 4993 */     if (arg != null) {
/* 4994 */       setString(parameterIndex, arg.toString());
/*      */ 
/* 4996 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 70;
/*      */     } else {
/* 4998 */       setNull(parameterIndex, 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void streamToBytes(Buffer packet, InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5006 */       String connectionEncoding = this.connection.getEncoding();
/*      */ 
/* 5008 */       boolean hexEscape = false;
/*      */ 
/* 5010 */       if ((this.connection.isNoBackslashEscapesSet()) || ((this.connection.getUseUnicode()) && (connectionEncoding != null) && (CharsetMapping.isMultibyteCharset(connectionEncoding)) && (!this.connection.parserKnowsUnicode())))
/*      */       {
/* 5015 */         hexEscape = true;
/*      */       }
/*      */ 
/* 5018 */       if (streamLength == -1) {
/* 5019 */         useLength = false;
/*      */       }
/*      */ 
/* 5022 */       int bc = -1;
/*      */ 
/* 5024 */       if (useLength)
/* 5025 */         bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */       else {
/* 5027 */         bc = readblock(in, this.streamConvertBuf);
/*      */       }
/*      */ 
/* 5030 */       int lengthLeftToRead = streamLength - bc;
/*      */ 
/* 5032 */       if (hexEscape)
/* 5033 */         packet.writeStringNoNull("x");
/* 5034 */       else if (this.connection.getIO().versionMeetsMinimum(4, 1, 0)) {
/* 5035 */         packet.writeStringNoNull("_binary");
/*      */       }
/*      */ 
/* 5038 */       if (escape) {
/* 5039 */         packet.writeByte(39);
/*      */       }
/*      */ 
/* 5042 */       while (bc > 0) {
/* 5043 */         if (hexEscape)
/* 5044 */           hexEscapeBlock(this.streamConvertBuf, packet, bc);
/* 5045 */         else if (escape)
/* 5046 */           escapeblockFast(this.streamConvertBuf, packet, bc);
/*      */         else {
/* 5048 */           packet.writeBytesNoNull(this.streamConvertBuf, 0, bc);
/*      */         }
/*      */ 
/* 5051 */         if (useLength) {
/* 5052 */           bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */ 
/* 5054 */           if (bc > 0) {
/* 5055 */             lengthLeftToRead -= bc; continue;
/*      */           }
/*      */         }
/* 5058 */         bc = readblock(in, this.streamConvertBuf);
/*      */       }
/*      */ 
/* 5062 */       if (escape)
/* 5063 */         packet.writeByte(39);
/*      */     }
/*      */     finally {
/* 5066 */       if (this.connection.getAutoClosePStmtStreams()) {
/*      */         try {
/* 5068 */           in.close();
/*      */         }
/*      */         catch (IOException ioEx)
/*      */         {
/*      */         }
/* 5073 */         in = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private final byte[] streamToBytes(InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException
/*      */   {
/*      */     try {
/* 5081 */       if (streamLength == -1) {
/* 5082 */         useLength = false;
/*      */       }
/*      */ 
/* 5085 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */ 
/* 5087 */       int bc = -1;
/*      */ 
/* 5089 */       if (useLength)
/* 5090 */         bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */       else {
/* 5092 */         bc = readblock(in, this.streamConvertBuf);
/*      */       }
/*      */ 
/* 5095 */       int lengthLeftToRead = streamLength - bc;
/*      */ 
/* 5097 */       if (escape) {
/* 5098 */         if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 5099 */           bytesOut.write(95);
/* 5100 */           bytesOut.write(98);
/* 5101 */           bytesOut.write(105);
/* 5102 */           bytesOut.write(110);
/* 5103 */           bytesOut.write(97);
/* 5104 */           bytesOut.write(114);
/* 5105 */           bytesOut.write(121);
/*      */         }
/*      */ 
/* 5108 */         bytesOut.write(39);
/*      */       }
/*      */ 
/* 5111 */       while (bc > 0) {
/* 5112 */         if (escape)
/* 5113 */           escapeblockFast(this.streamConvertBuf, bytesOut, bc);
/*      */         else {
/* 5115 */           bytesOut.write(this.streamConvertBuf, 0, bc);
/*      */         }
/*      */ 
/* 5118 */         if (useLength) {
/* 5119 */           bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */ 
/* 5121 */           if (bc > 0) {
/* 5122 */             lengthLeftToRead -= bc; continue;
/*      */           }
/*      */         }
/* 5125 */         bc = readblock(in, this.streamConvertBuf);
/*      */       }
/*      */ 
/* 5129 */       if (escape) {
/* 5130 */         bytesOut.write(39);
/*      */       }
/*      */ 
/* 5133 */       arrayOfByte = bytesOut.toByteArray();
/*      */     }
/*      */     finally
/*      */     {
/*      */       byte[] arrayOfByte;
/* 5135 */       if (this.connection.getAutoClosePStmtStreams()) {
/*      */         try {
/* 5137 */           in.close();
/*      */         }
/*      */         catch (IOException ioEx)
/*      */         {
/*      */         }
/* 5142 */         in = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 5153 */     StringBuffer buf = new StringBuffer();
/* 5154 */     buf.append(super.toString());
/* 5155 */     buf.append(": ");
/*      */     try
/*      */     {
/* 5158 */       buf.append(asSql());
/*      */     } catch (SQLException sqlEx) {
/* 5160 */       buf.append("EXCEPTION: " + sqlEx.toString());
/*      */     }
/*      */ 
/* 5163 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isClosed()
/*      */     throws SQLException
/*      */   {
/* 5169 */     return this.isClosed;
/*      */   }
/*      */ 
/*      */   protected int getParameterIndexOffset()
/*      */   {
/* 5180 */     return 0;
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/* 5184 */     setAsciiStream(parameterIndex, x, -1);
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 5188 */     setAsciiStream(parameterIndex, x, (int)length);
/* 5189 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/* 5193 */     setBinaryStream(parameterIndex, x, -1);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 5197 */     setBinaryStream(parameterIndex, x, (int)length);
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/* 5201 */     setBinaryStream(parameterIndex, inputStream);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/* 5205 */     setCharacterStream(parameterIndex, reader, -1);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/* 5209 */     setCharacterStream(parameterIndex, reader, (int)length);
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Reader reader) throws SQLException
/*      */   {
/* 5214 */     setCharacterStream(parameterIndex, reader);
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/* 5220 */     setCharacterStream(parameterIndex, reader, length);
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/* 5224 */     setNCharacterStream(parameterIndex, value, -1L);
/*      */   }
/*      */ 
/*      */   public void setNString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 5242 */     if ((this.charEncoding.equalsIgnoreCase("UTF-8")) || (this.charEncoding.equalsIgnoreCase("utf8")))
/*      */     {
/* 5244 */       setString(parameterIndex, x);
/* 5245 */       return;
/*      */     }
/*      */ 
/* 5249 */     if (x == null) {
/* 5250 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 5252 */       int stringLength = x.length();
/*      */ 
/* 5256 */       StringBuffer buf = new StringBuffer((int)(x.length() * 1.1D + 4.0D));
/* 5257 */       buf.append("_utf8");
/* 5258 */       buf.append('\'');
/*      */ 
/* 5267 */       for (int i = 0; i < stringLength; i++) {
/* 5268 */         char c = x.charAt(i);
/*      */ 
/* 5270 */         switch (c) {
/*      */         case '\000':
/* 5272 */           buf.append('\\');
/* 5273 */           buf.append('0');
/*      */ 
/* 5275 */           break;
/*      */         case '\n':
/* 5278 */           buf.append('\\');
/* 5279 */           buf.append('n');
/*      */ 
/* 5281 */           break;
/*      */         case '\r':
/* 5284 */           buf.append('\\');
/* 5285 */           buf.append('r');
/*      */ 
/* 5287 */           break;
/*      */         case '\\':
/* 5290 */           buf.append('\\');
/* 5291 */           buf.append('\\');
/*      */ 
/* 5293 */           break;
/*      */         case '\'':
/* 5296 */           buf.append('\\');
/* 5297 */           buf.append('\'');
/*      */ 
/* 5299 */           break;
/*      */         case '"':
/* 5302 */           if (this.usingAnsiMode) {
/* 5303 */             buf.append('\\');
/*      */           }
/*      */ 
/* 5306 */           buf.append('"');
/*      */ 
/* 5308 */           break;
/*      */         case '\032':
/* 5311 */           buf.append('\\');
/* 5312 */           buf.append('Z');
/*      */ 
/* 5314 */           break;
/*      */         default:
/* 5317 */           buf.append(c);
/*      */         }
/*      */       }
/*      */ 
/* 5321 */       buf.append('\'');
/*      */ 
/* 5323 */       String parameterAsString = buf.toString();
/*      */ 
/* 5325 */       byte[] parameterAsBytes = null;
/*      */ 
/* 5327 */       if (!this.isLoadDataQuery) {
/* 5328 */         parameterAsBytes = StringUtils.getBytes(parameterAsString, this.connection.getCharsetConverter("UTF-8"), "UTF-8", this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */       }
/*      */       else
/*      */       {
/* 5334 */         parameterAsBytes = parameterAsString.getBytes();
/*      */       }
/*      */ 
/* 5337 */       setInternal(parameterIndex, parameterAsBytes);
/*      */ 
/* 5339 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -9;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5368 */       if (reader == null) {
/* 5369 */         setNull(parameterIndex, -1);
/*      */       }
/*      */       else {
/* 5372 */         char[] c = null;
/* 5373 */         int len = 0;
/*      */ 
/* 5375 */         boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/* 5380 */         if ((useLength) && (length != -1L)) {
/* 5381 */           c = new char[(int)length];
/*      */ 
/* 5383 */           int numCharsRead = readFully(reader, c, (int)length);
/*      */ 
/* 5387 */           setNString(parameterIndex, new String(c, 0, numCharsRead));
/*      */         }
/*      */         else {
/* 5390 */           c = new char[4096];
/*      */ 
/* 5392 */           StringBuffer buf = new StringBuffer();
/*      */ 
/* 5394 */           while ((len = reader.read(c)) != -1) {
/* 5395 */             buf.append(c, 0, len);
/*      */           }
/*      */ 
/* 5398 */           setNString(parameterIndex, buf.toString());
/*      */         }
/*      */ 
/* 5401 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2011;
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 5404 */       throw SQLError.createSQLException(ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNClob(int parameterIndex, Reader reader) throws SQLException
/*      */   {
/* 5410 */     setNCharacterStream(parameterIndex, reader);
/*      */   }
/*      */ 
/*      */   public void setNClob(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/* 5428 */     if (reader == null)
/* 5429 */       setNull(parameterIndex, -1);
/*      */     else
/* 5431 */       setNCharacterStream(parameterIndex, reader, length);
/*      */   }
/*      */ 
/*      */   public ParameterBindings getParameterBindings() throws SQLException
/*      */   {
/* 5436 */     return new EmulatedPreparedStatementBindings();
/*      */   }
/*      */ 
/*      */   public String getPreparedSql()
/*      */   {
/* 5619 */     if (this.rewrittenBatchSize == 0) {
/* 5620 */       return this.originalSql;
/*      */     }
/*      */     try
/*      */     {
/* 5624 */       return this.parseInfo.getSqlForBatch(this.parseInfo); } catch (UnsupportedEncodingException e) {
/*      */     }
/* 5626 */     throw new RuntimeException(e);
/*      */   }
/*      */ 
/*      */   public int getUpdateCount() throws SQLException
/*      */   {
/* 5631 */     int count = super.getUpdateCount();
/*      */ 
/* 5633 */     if ((containsOnDuplicateKeyUpdateInSQL()) && (this.compensateForOnDuplicateKeyUpdate))
/*      */     {
/* 5635 */       if ((count == 2) || (count == 0)) {
/* 5636 */         count = 1;
/*      */       }
/*      */     }
/*      */ 
/* 5640 */     return count;
/*      */   }
/*      */ 
/*      */   protected static boolean canRewrite(String sql, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementStartPos)
/*      */   {
/* 5647 */     boolean rewritableOdku = true;
/*      */ 
/* 5649 */     if (isOnDuplicateKeyUpdate) {
/* 5650 */       int updateClausePos = StringUtils.indexOfIgnoreCase(locationOfOnDuplicateKeyUpdate, sql, " UPDATE ");
/*      */ 
/* 5653 */       if (updateClausePos != -1) {
/* 5654 */         rewritableOdku = StringUtils.indexOfIgnoreCaseRespectMarker(updateClausePos, sql, "LAST_INSERT_ID", "\"'`", "\"'`", false) == -1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5661 */     return (StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", statementStartPos)) && (StringUtils.indexOfIgnoreCaseRespectMarker(statementStartPos, sql, "SELECT", "\"'`", "\"'`", false) == -1) && (rewritableOdku);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  104 */     if (Util.isJdbc4()) {
/*      */       try {
/*  106 */         JDBC_4_PSTMT_2_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class });
/*      */ 
/*  110 */         JDBC_4_PSTMT_3_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class, String.class });
/*      */ 
/*  115 */         JDBC_4_PSTMT_4_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class, String.class, ParseInfo.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  121 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  123 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  125 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  128 */       JDBC_4_PSTMT_2_ARG_CTOR = null;
/*  129 */       JDBC_4_PSTMT_3_ARG_CTOR = null;
/*  130 */       JDBC_4_PSTMT_4_ARG_CTOR = null;
/*      */     }
/*      */ 
/*  730 */     HEX_DIGITS = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */   }
/*      */ 
/*      */   class EmulatedPreparedStatementBindings
/*      */     implements ParameterBindings
/*      */   {
/*      */     private ResultSetImpl bindingsAsRs;
/*      */     private boolean[] parameterIsNull;
/*      */ 
/*      */     public EmulatedPreparedStatementBindings()
/*      */       throws SQLException
/*      */     {
/* 5445 */       List rows = new ArrayList();
/* 5446 */       this.parameterIsNull = new boolean[PreparedStatement.this.parameterCount];
/* 5447 */       System.arraycopy(PreparedStatement.this.isNull, 0, this.parameterIsNull, 0, PreparedStatement.this.parameterCount);
/*      */ 
/* 5450 */       byte[][] rowData = new byte[PreparedStatement.this.parameterCount][];
/* 5451 */       Field[] typeMetadata = new Field[PreparedStatement.this.parameterCount];
/*      */ 
/* 5453 */       for (int i = 0; i < PreparedStatement.this.parameterCount; i++) {
/* 5454 */         if (PreparedStatement.this.batchCommandIndex == -1)
/* 5455 */           rowData[i] = PreparedStatement.this.getBytesRepresentation(i);
/*      */         else {
/* 5457 */           rowData[i] = PreparedStatement.this.getBytesRepresentationForBatch(i, PreparedStatement.access$100(PreparedStatement.this));
/*      */         }
/* 5459 */         int charsetIndex = 0;
/*      */ 
/* 5461 */         if ((PreparedStatement.this.parameterTypes[i] == -2) || (PreparedStatement.this.parameterTypes[i] == 2004))
/*      */         {
/* 5463 */           charsetIndex = 63;
/*      */         } else {
/* 5465 */           String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(PreparedStatement.this.connection.getEncoding(), PreparedStatement.this.connection);
/*      */ 
/* 5468 */           charsetIndex = CharsetMapping.getCharsetIndexForMysqlEncodingName(mysqlEncodingName);
/*      */         }
/*      */ 
/* 5472 */         Field parameterMetadata = new Field(null, "parameter_" + (i + 1), charsetIndex, PreparedStatement.this.parameterTypes[i], rowData[i].length);
/*      */ 
/* 5475 */         parameterMetadata.setConnection(PreparedStatement.this.connection);
/* 5476 */         typeMetadata[i] = parameterMetadata;
/*      */       }
/*      */ 
/* 5479 */       rows.add(new ByteArrayRow(rowData, PreparedStatement.this.getExceptionInterceptor()));
/*      */ 
/* 5481 */       this.bindingsAsRs = new ResultSetImpl(PreparedStatement.this.connection.getCatalog(), typeMetadata, new RowDataStatic(rows), PreparedStatement.this.connection, null);
/*      */ 
/* 5483 */       this.bindingsAsRs.next();
/*      */     }
/*      */ 
/*      */     public Array getArray(int parameterIndex) throws SQLException {
/* 5487 */       return this.bindingsAsRs.getArray(parameterIndex);
/*      */     }
/*      */ 
/*      */     public InputStream getAsciiStream(int parameterIndex) throws SQLException
/*      */     {
/* 5492 */       return this.bindingsAsRs.getAsciiStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/* 5496 */       return this.bindingsAsRs.getBigDecimal(parameterIndex);
/*      */     }
/*      */ 
/*      */     public InputStream getBinaryStream(int parameterIndex) throws SQLException
/*      */     {
/* 5501 */       return this.bindingsAsRs.getBinaryStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Blob getBlob(int parameterIndex) throws SQLException {
/* 5505 */       return this.bindingsAsRs.getBlob(parameterIndex);
/*      */     }
/*      */ 
/*      */     public boolean getBoolean(int parameterIndex) throws SQLException {
/* 5509 */       return this.bindingsAsRs.getBoolean(parameterIndex);
/*      */     }
/*      */ 
/*      */     public byte getByte(int parameterIndex) throws SQLException {
/* 5513 */       return this.bindingsAsRs.getByte(parameterIndex);
/*      */     }
/*      */ 
/*      */     public byte[] getBytes(int parameterIndex) throws SQLException {
/* 5517 */       return this.bindingsAsRs.getBytes(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Reader getCharacterStream(int parameterIndex) throws SQLException
/*      */     {
/* 5522 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Clob getClob(int parameterIndex) throws SQLException {
/* 5526 */       return this.bindingsAsRs.getClob(parameterIndex);
/*      */     }
/*      */ 
/*      */     public java.sql.Date getDate(int parameterIndex) throws SQLException {
/* 5530 */       return this.bindingsAsRs.getDate(parameterIndex);
/*      */     }
/*      */ 
/*      */     public double getDouble(int parameterIndex) throws SQLException {
/* 5534 */       return this.bindingsAsRs.getDouble(parameterIndex);
/*      */     }
/*      */ 
/*      */     public float getFloat(int parameterIndex) throws SQLException {
/* 5538 */       return this.bindingsAsRs.getFloat(parameterIndex);
/*      */     }
/*      */ 
/*      */     public int getInt(int parameterIndex) throws SQLException {
/* 5542 */       return this.bindingsAsRs.getInt(parameterIndex);
/*      */     }
/*      */ 
/*      */     public long getLong(int parameterIndex) throws SQLException {
/* 5546 */       return this.bindingsAsRs.getLong(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Reader getNCharacterStream(int parameterIndex) throws SQLException
/*      */     {
/* 5551 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Reader getNClob(int parameterIndex) throws SQLException {
/* 5555 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Object getObject(int parameterIndex) throws SQLException {
/* 5559 */       PreparedStatement.this.checkBounds(parameterIndex, 0);
/*      */ 
/* 5561 */       if (this.parameterIsNull[(parameterIndex - 1)] != 0) {
/* 5562 */         return null;
/*      */       }
/*      */ 
/* 5569 */       switch (PreparedStatement.this.parameterTypes[(parameterIndex - 1)]) {
/*      */       case -6:
/* 5571 */         return new Byte(getByte(parameterIndex));
/*      */       case 5:
/* 5573 */         return new Short(getShort(parameterIndex));
/*      */       case 4:
/* 5575 */         return new Integer(getInt(parameterIndex));
/*      */       case -5:
/* 5577 */         return new Long(getLong(parameterIndex));
/*      */       case 6:
/* 5579 */         return new Float(getFloat(parameterIndex));
/*      */       case 8:
/* 5581 */         return new Double(getDouble(parameterIndex));
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/* 5583 */       case 7: } return this.bindingsAsRs.getObject(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Ref getRef(int parameterIndex) throws SQLException
/*      */     {
/* 5588 */       return this.bindingsAsRs.getRef(parameterIndex);
/*      */     }
/*      */ 
/*      */     public short getShort(int parameterIndex) throws SQLException {
/* 5592 */       return this.bindingsAsRs.getShort(parameterIndex);
/*      */     }
/*      */ 
/*      */     public String getString(int parameterIndex) throws SQLException {
/* 5596 */       return this.bindingsAsRs.getString(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Time getTime(int parameterIndex) throws SQLException {
/* 5600 */       return this.bindingsAsRs.getTime(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Timestamp getTimestamp(int parameterIndex) throws SQLException {
/* 5604 */       return this.bindingsAsRs.getTimestamp(parameterIndex);
/*      */     }
/*      */ 
/*      */     public URL getURL(int parameterIndex) throws SQLException {
/* 5608 */       return this.bindingsAsRs.getURL(parameterIndex);
/*      */     }
/*      */ 
/*      */     public boolean isNull(int parameterIndex) throws SQLException {
/* 5612 */       PreparedStatement.this.checkBounds(parameterIndex, 0);
/*      */ 
/* 5614 */       return this.parameterIsNull[(parameterIndex - 1)];
/*      */     }
/*      */   }
/*      */ 
/*      */   class AppendingBatchVisitor
/*      */     implements PreparedStatement.BatchVisitor
/*      */   {
/*  683 */     LinkedList statementComponents = new LinkedList();
/*      */ 
/*      */     AppendingBatchVisitor() {  }
/*      */ 
/*  686 */     public PreparedStatement.BatchVisitor append(byte[] values) { this.statementComponents.addLast(values);
/*      */ 
/*  688 */       return this;
/*      */     }
/*      */ 
/*      */     public PreparedStatement.BatchVisitor increment()
/*      */     {
/*  693 */       return this;
/*      */     }
/*      */ 
/*      */     public PreparedStatement.BatchVisitor decrement() {
/*  697 */       this.statementComponents.removeLast();
/*      */ 
/*  699 */       return this;
/*      */     }
/*      */ 
/*      */     public PreparedStatement.BatchVisitor merge(byte[] front, byte[] back) {
/*  703 */       int mergedLength = front.length + back.length;
/*  704 */       byte[] merged = new byte[mergedLength];
/*  705 */       System.arraycopy(front, 0, merged, 0, front.length);
/*  706 */       System.arraycopy(back, 0, merged, front.length, back.length);
/*  707 */       this.statementComponents.addLast(merged);
/*  708 */       return this;
/*      */     }
/*      */ 
/*      */     public byte[][] getStaticSqlStrings() {
/*  712 */       byte[][] asBytes = new byte[this.statementComponents.size()][];
/*  713 */       this.statementComponents.toArray(asBytes);
/*      */ 
/*  715 */       return asBytes;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  719 */       StringBuffer buf = new StringBuffer();
/*  720 */       Iterator iter = this.statementComponents.iterator();
/*  721 */       while (iter.hasNext()) {
/*  722 */         buf.append(new String((byte[])iter.next()));
/*      */       }
/*      */ 
/*  725 */       return buf.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract interface BatchVisitor
/*      */   {
/*      */     public abstract BatchVisitor increment();
/*      */ 
/*      */     public abstract BatchVisitor decrement();
/*      */ 
/*      */     public abstract BatchVisitor append(byte[] paramArrayOfByte);
/*      */ 
/*      */     public abstract BatchVisitor merge(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */   }
/*      */ 
/*      */   class ParseInfo
/*      */   {
/*  180 */     char firstStmtChar = '\000';
/*      */ 
/*  182 */     boolean foundLimitClause = false;
/*      */ 
/*  184 */     boolean foundLoadData = false;
/*      */ 
/*  186 */     long lastUsed = 0L;
/*      */ 
/*  188 */     int statementLength = 0;
/*      */ 
/*  190 */     int statementStartPos = 0;
/*      */ 
/*  192 */     boolean canRewriteAsMultiValueInsert = false;
/*      */ 
/*  194 */     byte[][] staticSql = (byte[][])null;
/*      */ 
/*  196 */     boolean isOnDuplicateKeyUpdate = false;
/*      */ 
/*  198 */     int locationOfOnDuplicateKeyUpdate = -1;
/*      */     String valuesClause;
/*  202 */     boolean parametersInDuplicateKeyClause = false;
/*      */     private ParseInfo batchHead;
/*      */     private ParseInfo batchValues;
/*      */     private ParseInfo batchODKUClause;
/*      */ 
/*      */     ParseInfo(String sql, ConnectionImpl conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter)
/*      */       throws SQLException
/*      */     {
/*  213 */       this(sql, conn, dbmd, encoding, converter, true);
/*      */     }
/*      */ 
/*      */     public ParseInfo(String sql, ConnectionImpl conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter, boolean buildRewriteInfo) throws SQLException
/*      */     {
/*      */       try
/*      */       {
/*  220 */         if (sql == null) {
/*  221 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.61"), "S1009", PreparedStatement.this.getExceptionInterceptor());
/*      */         }
/*      */ 
/*  226 */         this.locationOfOnDuplicateKeyUpdate = PreparedStatement.this.getOnDuplicateKeyLocation(sql);
/*  227 */         this.isOnDuplicateKeyUpdate = (this.locationOfOnDuplicateKeyUpdate != -1);
/*      */ 
/*  229 */         this.lastUsed = System.currentTimeMillis();
/*      */ 
/*  231 */         String quotedIdentifierString = dbmd.getIdentifierQuoteString();
/*      */ 
/*  233 */         char quotedIdentifierChar = '\000';
/*      */ 
/*  235 */         if ((quotedIdentifierString != null) && (!quotedIdentifierString.equals(" ")) && (quotedIdentifierString.length() > 0))
/*      */         {
/*  238 */           quotedIdentifierChar = quotedIdentifierString.charAt(0);
/*      */         }
/*      */ 
/*  241 */         this.statementLength = sql.length();
/*      */ 
/*  243 */         ArrayList endpointList = new ArrayList();
/*  244 */         boolean inQuotes = false;
/*  245 */         char quoteChar = '\000';
/*  246 */         boolean inQuotedId = false;
/*  247 */         int lastParmEnd = 0;
/*      */ 
/*  250 */         int stopLookingForLimitClause = this.statementLength - 5;
/*      */ 
/*  252 */         this.foundLimitClause = false;
/*      */ 
/*  254 */         boolean noBackslashEscapes = PreparedStatement.this.connection.isNoBackslashEscapesSet();
/*      */ 
/*  260 */         this.statementStartPos = PreparedStatement.this.findStartOfStatement(sql);
/*      */ 
/*  262 */         for (int i = this.statementStartPos; i < this.statementLength; i++) {
/*  263 */           char c = sql.charAt(i);
/*      */ 
/*  265 */           if ((this.firstStmtChar == 0) && (Character.isLetter(c)))
/*      */           {
/*  268 */             this.firstStmtChar = Character.toUpperCase(c);
/*      */           }
/*      */ 
/*  271 */           if ((!noBackslashEscapes) && (c == '\\') && (i < this.statementLength - 1))
/*      */           {
/*  273 */             i++;
/*      */           }
/*      */           else
/*      */           {
/*  279 */             if ((!inQuotes) && (quotedIdentifierChar != 0) && (c == quotedIdentifierChar))
/*      */             {
/*  281 */               inQuotedId = !inQuotedId;
/*  282 */             } else if (!inQuotedId)
/*      */             {
/*  285 */               if (inQuotes) {
/*  286 */                 if (((c == '\'') || (c == '"')) && (c == quoteChar)) {
/*  287 */                   if ((i < this.statementLength - 1) && (sql.charAt(i + 1) == quoteChar)) {
/*  288 */                     i++;
/*  289 */                     continue;
/*      */                   }
/*      */ 
/*  292 */                   inQuotes = !inQuotes;
/*  293 */                   quoteChar = '\000';
/*  294 */                 } else if (((c == '\'') || (c == '"')) && (c == quoteChar)) {
/*  295 */                   inQuotes = !inQuotes;
/*  296 */                   quoteChar = '\000';
/*      */                 }
/*      */               } else {
/*  299 */                 if ((c == '#') || ((c == '-') && (i + 1 < this.statementLength) && (sql.charAt(i + 1) == '-')))
/*      */                 {
/*  304 */                   int endOfStmt = this.statementLength - 1;
/*      */ 
/*  306 */                   for (; i < endOfStmt; i++) {
/*  307 */                     c = sql.charAt(i);
/*      */ 
/*  309 */                     if ((c == '\r') || (c == '\n'))
/*      */                     {
/*      */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*  315 */                 if ((c == '/') && (i + 1 < this.statementLength))
/*      */                 {
/*  317 */                   char cNext = sql.charAt(i + 1);
/*      */ 
/*  319 */                   if (cNext == '*') {
/*  320 */                     i += 2;
/*      */ 
/*  322 */                     for (int j = i; j < this.statementLength; j++) {
/*  323 */                       i++;
/*  324 */                       cNext = sql.charAt(j);
/*      */ 
/*  326 */                       if ((cNext != '*') || (j + 1 >= this.statementLength) || 
/*  327 */                         (sql.charAt(j + 1) != '/')) continue;
/*  328 */                       i++;
/*      */ 
/*  330 */                       if (i >= this.statementLength) break;
/*  331 */                       c = sql.charAt(i); break;
/*      */                     }
/*      */ 
/*      */                   }
/*      */ 
/*      */                 }
/*  339 */                 else if ((c == '\'') || (c == '"')) {
/*  340 */                   inQuotes = true;
/*  341 */                   quoteChar = c;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  346 */             if ((c == '?') && (!inQuotes) && (!inQuotedId)) {
/*  347 */               endpointList.add(new int[] { lastParmEnd, i });
/*  348 */               lastParmEnd = i + 1;
/*      */ 
/*  350 */               if ((this.isOnDuplicateKeyUpdate) && (i > this.locationOfOnDuplicateKeyUpdate)) {
/*  351 */                 this.parametersInDuplicateKeyClause = true;
/*      */               }
/*      */             }
/*      */ 
/*  355 */             if ((inQuotes) || (i >= stopLookingForLimitClause) || (
/*  356 */               (c != 'L') && (c != 'l'))) continue;
/*  357 */             char posI1 = sql.charAt(i + 1);
/*      */ 
/*  359 */             if ((posI1 == 'I') || (posI1 == 'i')) {
/*  360 */               char posM = sql.charAt(i + 2);
/*      */ 
/*  362 */               if ((posM == 'M') || (posM == 'm')) {
/*  363 */                 char posI2 = sql.charAt(i + 3);
/*      */ 
/*  365 */                 if ((posI2 == 'I') || (posI2 == 'i')) {
/*  366 */                   char posT = sql.charAt(i + 4);
/*      */ 
/*  368 */                   if ((posT == 'T') || (posT == 't')) {
/*  369 */                     this.foundLimitClause = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  378 */         if (this.firstStmtChar == 'L') {
/*  379 */           if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA"))
/*  380 */             this.foundLoadData = true;
/*      */           else
/*  382 */             this.foundLoadData = false;
/*      */         }
/*      */         else {
/*  385 */           this.foundLoadData = false;
/*      */         }
/*      */ 
/*  388 */         endpointList.add(new int[] { lastParmEnd, this.statementLength });
/*  389 */         this.staticSql = new byte[endpointList.size()][];
/*  390 */         char[] asCharArray = sql.toCharArray();
/*      */ 
/*  392 */         for (i = 0; i < this.staticSql.length; i++) {
/*  393 */           int[] ep = (int[])endpointList.get(i);
/*  394 */           int end = ep[1];
/*  395 */           int begin = ep[0];
/*  396 */           int len = end - begin;
/*      */ 
/*  398 */           if (this.foundLoadData) {
/*  399 */             String temp = new String(asCharArray, begin, len);
/*  400 */             this.staticSql[i] = temp.getBytes();
/*  401 */           } else if (encoding == null) {
/*  402 */             byte[] buf = new byte[len];
/*      */ 
/*  404 */             for (int j = 0; j < len; j++) {
/*  405 */               buf[j] = (byte)sql.charAt(begin + j);
/*      */             }
/*      */ 
/*  408 */             this.staticSql[i] = buf;
/*      */           }
/*  410 */           else if (converter != null) {
/*  411 */             this.staticSql[i] = StringUtils.getBytes(sql, converter, encoding, PreparedStatement.this.connection.getServerCharacterEncoding(), begin, len, PreparedStatement.this.connection.parserKnowsUnicode(), PreparedStatement.this.getExceptionInterceptor());
/*      */           }
/*      */           else
/*      */           {
/*  416 */             String temp = new String(asCharArray, begin, len);
/*      */ 
/*  418 */             this.staticSql[i] = StringUtils.getBytes(temp, encoding, PreparedStatement.this.connection.getServerCharacterEncoding(), PreparedStatement.this.connection.parserKnowsUnicode(), conn, PreparedStatement.this.getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (StringIndexOutOfBoundsException oobEx)
/*      */       {
/*  426 */         SQLException sqlEx = new SQLException("Parse error for " + sql);
/*  427 */         sqlEx.initCause(oobEx);
/*      */ 
/*  429 */         throw sqlEx;
/*      */       }
/*      */ 
/*  433 */       if (buildRewriteInfo) {
/*  434 */         this.canRewriteAsMultiValueInsert = ((PreparedStatement.canRewrite(sql, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementStartPos)) && (!this.parametersInDuplicateKeyClause));
/*      */ 
/*  439 */         if ((this.canRewriteAsMultiValueInsert) && (conn.getRewriteBatchedStatements()))
/*      */         {
/*  441 */           buildRewriteBatchedParams(sql, conn, dbmd, encoding, converter);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void buildRewriteBatchedParams(String sql, ConnectionImpl conn, DatabaseMetaData metadata, String encoding, SingleByteCharsetConverter converter)
/*      */       throws SQLException
/*      */     {
/*  456 */       this.valuesClause = extractValuesClause(sql);
/*  457 */       String odkuClause = this.isOnDuplicateKeyUpdate ? sql.substring(this.locationOfOnDuplicateKeyUpdate) : null;
/*      */ 
/*  460 */       String headSql = null;
/*      */ 
/*  462 */       if (this.isOnDuplicateKeyUpdate)
/*  463 */         headSql = sql.substring(0, this.locationOfOnDuplicateKeyUpdate);
/*      */       else {
/*  465 */         headSql = sql;
/*      */       }
/*      */ 
/*  468 */       this.batchHead = new ParseInfo(PreparedStatement.this, headSql, conn, metadata, encoding, converter, false);
/*      */ 
/*  470 */       this.batchValues = new ParseInfo(PreparedStatement.this, "," + this.valuesClause, conn, metadata, encoding, converter, false);
/*      */ 
/*  472 */       this.batchODKUClause = null;
/*      */ 
/*  474 */       if ((odkuClause != null) && (odkuClause.length() > 0))
/*  475 */         this.batchODKUClause = new ParseInfo(PreparedStatement.this, "," + this.valuesClause + " " + odkuClause, conn, metadata, encoding, converter, false);
/*      */     }
/*      */ 
/*      */     private String extractValuesClause(String sql)
/*      */       throws SQLException
/*      */     {
/*  482 */       String quoteCharStr = PreparedStatement.this.connection.getMetaData().getIdentifierQuoteString();
/*      */ 
/*  485 */       int indexOfValues = -1;
/*  486 */       int valuesSearchStart = this.statementStartPos;
/*      */ 
/*  488 */       while (indexOfValues == -1) {
/*  489 */         if (quoteCharStr.length() > 0) {
/*  490 */           indexOfValues = StringUtils.indexOfIgnoreCaseRespectQuotes(valuesSearchStart, PreparedStatement.this.originalSql, "VALUES", quoteCharStr.charAt(0), false);
/*      */         }
/*      */         else
/*      */         {
/*  494 */           indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, PreparedStatement.this.originalSql, "VALUES");
/*      */         }
/*      */ 
/*  499 */         if (indexOfValues <= 0)
/*      */           break;
/*  501 */         char c = PreparedStatement.this.originalSql.charAt(indexOfValues - 1);
/*  502 */         if ((!Character.isWhitespace(c)) && (c != ')') && (c != '`')) {
/*  503 */           valuesSearchStart = indexOfValues + 6;
/*  504 */           indexOfValues = -1;
/*      */         }
/*      */         else {
/*  507 */           c = PreparedStatement.this.originalSql.charAt(indexOfValues + 6);
/*  508 */           if ((!Character.isWhitespace(c)) && (c != '(')) {
/*  509 */             valuesSearchStart = indexOfValues + 6;
/*  510 */             indexOfValues = -1;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  518 */       if (indexOfValues == -1) {
/*  519 */         return null;
/*      */       }
/*      */ 
/*  522 */       int indexOfFirstParen = sql.indexOf('(', indexOfValues + 6);
/*      */ 
/*  524 */       if (indexOfFirstParen == -1) {
/*  525 */         return null;
/*      */       }
/*      */ 
/*  528 */       int endOfValuesClause = sql.lastIndexOf(')');
/*      */ 
/*  530 */       if (endOfValuesClause == -1) {
/*  531 */         return null;
/*      */       }
/*      */ 
/*  534 */       if (this.isOnDuplicateKeyUpdate) {
/*  535 */         endOfValuesClause = this.locationOfOnDuplicateKeyUpdate - 1;
/*      */       }
/*      */ 
/*  538 */       return sql.substring(indexOfFirstParen, endOfValuesClause + 1);
/*      */     }
/*      */ 
/*      */     synchronized ParseInfo getParseInfoForBatch(int numBatch)
/*      */     {
/*  545 */       PreparedStatement.AppendingBatchVisitor apv = new PreparedStatement.AppendingBatchVisitor(PreparedStatement.this);
/*  546 */       buildInfoForBatch(numBatch, apv);
/*      */ 
/*  548 */       ParseInfo batchParseInfo = new ParseInfo(PreparedStatement.this, apv.getStaticSqlStrings(), this.firstStmtChar, this.foundLimitClause, this.foundLoadData, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementLength, this.statementStartPos);
/*      */ 
/*  554 */       return batchParseInfo;
/*      */     }
/*      */ 
/*      */     String getSqlForBatch(int numBatch)
/*      */       throws UnsupportedEncodingException
/*      */     {
/*  563 */       ParseInfo batchInfo = getParseInfoForBatch(numBatch);
/*      */ 
/*  565 */       return getSqlForBatch(batchInfo);
/*      */     }
/*      */ 
/*      */     String getSqlForBatch(ParseInfo batchInfo)
/*      */       throws UnsupportedEncodingException
/*      */     {
/*  572 */       int size = 0;
/*  573 */       byte[][] sqlStrings = batchInfo.staticSql;
/*  574 */       int sqlStringsLength = sqlStrings.length;
/*      */ 
/*  576 */       for (int i = 0; i < sqlStringsLength; i++) {
/*  577 */         size += sqlStrings[i].length;
/*  578 */         size++;
/*      */       }
/*      */ 
/*  581 */       StringBuffer buf = new StringBuffer(size);
/*      */ 
/*  583 */       for (int i = 0; i < sqlStringsLength - 1; i++) {
/*  584 */         buf.append(new String(sqlStrings[i], PreparedStatement.this.charEncoding));
/*  585 */         buf.append("?");
/*      */       }
/*      */ 
/*  588 */       buf.append(new String(sqlStrings[(sqlStringsLength - 1)]));
/*      */ 
/*  590 */       return buf.toString();
/*      */     }
/*      */ 
/*      */     private void buildInfoForBatch(int numBatch, PreparedStatement.BatchVisitor visitor)
/*      */     {
/*  602 */       byte[][] headStaticSql = this.batchHead.staticSql;
/*  603 */       int headStaticSqlLength = headStaticSql.length;
/*      */ 
/*  605 */       if (headStaticSqlLength > 1) {
/*  606 */         for (int i = 0; i < headStaticSqlLength - 1; i++) {
/*  607 */           visitor.append(headStaticSql[i]).increment();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  612 */       byte[] endOfHead = headStaticSql[(headStaticSqlLength - 1)];
/*  613 */       byte[][] valuesStaticSql = this.batchValues.staticSql;
/*  614 */       byte[] beginOfValues = valuesStaticSql[0];
/*      */ 
/*  616 */       visitor.merge(endOfHead, beginOfValues).increment();
/*      */ 
/*  618 */       int numValueRepeats = numBatch - 1;
/*      */ 
/*  620 */       if (this.batchODKUClause != null) {
/*  621 */         numValueRepeats--;
/*      */       }
/*      */ 
/*  624 */       int valuesStaticSqlLength = valuesStaticSql.length;
/*  625 */       byte[] endOfValues = valuesStaticSql[(valuesStaticSqlLength - 1)];
/*      */ 
/*  627 */       for (int i = 0; i < numValueRepeats; i++) {
/*  628 */         for (int j = 1; j < valuesStaticSqlLength - 1; j++) {
/*  629 */           visitor.append(valuesStaticSql[j]).increment();
/*      */         }
/*  631 */         visitor.merge(endOfValues, beginOfValues).increment();
/*      */       }
/*      */ 
/*  634 */       if (this.batchODKUClause != null) {
/*  635 */         byte[][] batchOdkuStaticSql = this.batchODKUClause.staticSql;
/*  636 */         byte[] beginOfOdku = batchOdkuStaticSql[0];
/*  637 */         visitor.decrement().merge(endOfValues, beginOfOdku).increment();
/*      */ 
/*  639 */         int batchOdkuStaticSqlLength = batchOdkuStaticSql.length;
/*      */ 
/*  641 */         if (numBatch > 1) {
/*  642 */           for (int i = 1; i < batchOdkuStaticSqlLength; i++) {
/*  643 */             visitor.append(batchOdkuStaticSql[i]).increment();
/*      */           }
/*      */         }
/*      */         else {
/*  647 */           visitor.decrement().append(batchOdkuStaticSql[(batchOdkuStaticSqlLength - 1)]);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  652 */         visitor.decrement().append(this.staticSql[(this.staticSql.length - 1)]);
/*      */       }
/*      */     }
/*      */ 
/*      */     private ParseInfo(byte[][] staticSql, char firstStmtChar, boolean foundLimitClause, boolean foundLoadData, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementLength, int statementStartPos)
/*      */     {
/*  661 */       this.firstStmtChar = firstStmtChar;
/*  662 */       this.foundLimitClause = foundLimitClause;
/*  663 */       this.foundLoadData = foundLoadData;
/*  664 */       this.isOnDuplicateKeyUpdate = isOnDuplicateKeyUpdate;
/*  665 */       this.locationOfOnDuplicateKeyUpdate = locationOfOnDuplicateKeyUpdate;
/*  666 */       this.statementLength = statementLength;
/*  667 */       this.statementStartPos = statementStartPos;
/*  668 */       this.staticSql = staticSql;
/*      */     }
/*      */   }
/*      */ 
/*      */   class EndPoint
/*      */   {
/*      */     int begin;
/*      */     int end;
/*      */ 
/*      */     EndPoint(int b, int e)
/*      */     {
/*  174 */       this.begin = b;
/*  175 */       this.end = e;
/*      */     }
/*      */   }
/*      */ 
/*      */   class BatchParams
/*      */   {
/*  135 */     boolean[] isNull = null;
/*      */ 
/*  137 */     boolean[] isStream = null;
/*      */ 
/*  139 */     InputStream[] parameterStreams = null;
/*      */ 
/*  141 */     byte[][] parameterStrings = (byte[][])null;
/*      */ 
/*  143 */     int[] streamLengths = null;
/*      */ 
/*      */     BatchParams(byte[][] strings, InputStream[] streams, boolean[] isStreamFlags, int[] lengths, boolean[] isNullFlags)
/*      */     {
/*  150 */       this.parameterStrings = new byte[strings.length][];
/*  151 */       this.parameterStreams = new InputStream[streams.length];
/*  152 */       this.isStream = new boolean[isStreamFlags.length];
/*  153 */       this.streamLengths = new int[lengths.length];
/*  154 */       this.isNull = new boolean[isNullFlags.length];
/*  155 */       System.arraycopy(strings, 0, this.parameterStrings, 0, strings.length);
/*      */ 
/*  157 */       System.arraycopy(streams, 0, this.parameterStreams, 0, streams.length);
/*      */ 
/*  159 */       System.arraycopy(isStreamFlags, 0, this.isStream, 0, isStreamFlags.length);
/*      */ 
/*  161 */       System.arraycopy(lengths, 0, this.streamLengths, 0, lengths.length);
/*  162 */       System.arraycopy(isNullFlags, 0, this.isNull, 0, isNullFlags.length);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.PreparedStatement
 * JD-Core Version:    0.6.0
 */