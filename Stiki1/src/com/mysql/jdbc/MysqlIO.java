/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import com.mysql.jdbc.util.ReadAheadInputStream;
/*      */ import com.mysql.jdbc.util.ResultSetUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketException;
/*      */ import java.net.URL;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.sql.Connection;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Properties;
/*      */ import java.util.zip.Deflater;
/*      */ 
/*      */ class MysqlIO
/*      */ {
/*      */   private static final int UTF8_CHARSET_INDEX = 33;
/*      */   private static final String CODE_PAGE_1252 = "Cp1252";
/*      */   protected static final int NULL_LENGTH = -1;
/*      */   protected static final int COMP_HEADER_LENGTH = 3;
/*      */   protected static final int MIN_COMPRESS_LEN = 50;
/*      */   protected static final int HEADER_LENGTH = 4;
/*      */   protected static final int AUTH_411_OVERHEAD = 33;
/*   81 */   private static int maxBufferSize = 65535;
/*      */   private static final int CLIENT_COMPRESS = 32;
/*      */   protected static final int CLIENT_CONNECT_WITH_DB = 8;
/*      */   private static final int CLIENT_FOUND_ROWS = 2;
/*      */   private static final int CLIENT_LOCAL_FILES = 128;
/*      */   private static final int CLIENT_LONG_FLAG = 4;
/*      */   private static final int CLIENT_LONG_PASSWORD = 1;
/*      */   private static final int CLIENT_PROTOCOL_41 = 512;
/*      */   private static final int CLIENT_INTERACTIVE = 1024;
/*      */   protected static final int CLIENT_SSL = 2048;
/*      */   private static final int CLIENT_TRANSACTIONS = 8192;
/*      */   protected static final int CLIENT_RESERVED = 16384;
/*      */   protected static final int CLIENT_SECURE_CONNECTION = 32768;
/*      */   private static final int CLIENT_MULTI_QUERIES = 65536;
/*      */   private static final int CLIENT_MULTI_RESULTS = 131072;
/*      */   private static final int SERVER_STATUS_IN_TRANS = 1;
/*      */   private static final int SERVER_STATUS_AUTOCOMMIT = 2;
/*      */   static final int SERVER_MORE_RESULTS_EXISTS = 8;
/*      */   private static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
/*      */   private static final int SERVER_QUERY_NO_INDEX_USED = 32;
/*      */   private static final int SERVER_QUERY_WAS_SLOW = 2048;
/*      */   private static final int SERVER_STATUS_CURSOR_EXISTS = 64;
/*      */   private static final String FALSE_SCRAMBLE = "xxxxxxxx";
/*      */   protected static final int MAX_QUERY_SIZE_TO_LOG = 1024;
/*      */   protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
/*      */   protected static final int INITIAL_PACKET_SIZE = 1024;
/*  117 */   private static String jvmPlatformCharset = null;
/*      */   protected static final String ZERO_DATE_VALUE_MARKER = "0000-00-00";
/*      */   protected static final String ZERO_DATETIME_VALUE_MARKER = "0000-00-00 00:00:00";
/*      */   private static final int MAX_PACKET_DUMP_LENGTH = 1024;
/*  150 */   private boolean packetSequenceReset = false;
/*      */   protected int serverCharsetIndex;
/*  158 */   private Buffer reusablePacket = null;
/*  159 */   private Buffer sendPacket = null;
/*  160 */   private Buffer sharedSendPacket = null;
/*      */ 
/*  163 */   protected BufferedOutputStream mysqlOutput = null;
/*      */   protected ConnectionImpl connection;
/*  165 */   private Deflater deflater = null;
/*  166 */   protected InputStream mysqlInput = null;
/*  167 */   private LinkedList packetDebugRingBuffer = null;
/*  168 */   private RowData streamingData = null;
/*      */ 
/*  171 */   protected Socket mysqlConnection = null;
/*  172 */   private SocketFactory socketFactory = null;
/*      */   private SoftReference loadFileBufRef;
/*      */   private SoftReference splitBufRef;
/*  188 */   protected String host = null;
/*      */   protected String seed;
/*  190 */   private String serverVersion = null;
/*  191 */   private String socketFactoryClassName = null;
/*  192 */   private byte[] packetHeaderBuf = new byte[4];
/*  193 */   private boolean colDecimalNeedsBump = false;
/*  194 */   private boolean hadWarnings = false;
/*  195 */   private boolean has41NewNewProt = false;
/*      */ 
/*  198 */   private boolean hasLongColumnInfo = false;
/*  199 */   private boolean isInteractiveClient = false;
/*  200 */   private boolean logSlowQueries = false;
/*      */ 
/*  206 */   private boolean platformDbCharsetMatches = true;
/*  207 */   private boolean profileSql = false;
/*  208 */   private boolean queryBadIndexUsed = false;
/*  209 */   private boolean queryNoIndexUsed = false;
/*  210 */   private boolean serverQueryWasSlow = false;
/*      */ 
/*  213 */   private boolean use41Extensions = false;
/*  214 */   private boolean useCompression = false;
/*  215 */   private boolean useNewLargePackets = false;
/*  216 */   private boolean useNewUpdateCounts = false;
/*  217 */   private byte packetSequence = 0;
/*  218 */   private byte readPacketSequence = -1;
/*  219 */   private boolean checkPacketSequence = false;
/*  220 */   private byte protocolVersion = 0;
/*  221 */   private int maxAllowedPacket = 1048576;
/*  222 */   protected int maxThreeBytes = 16581375;
/*  223 */   protected int port = 3306;
/*      */   protected int serverCapabilities;
/*  225 */   private int serverMajorVersion = 0;
/*  226 */   private int serverMinorVersion = 0;
/*  227 */   private int oldServerStatus = 0;
/*  228 */   private int serverStatus = 0;
/*  229 */   private int serverSubMinorVersion = 0;
/*  230 */   private int warningCount = 0;
/*  231 */   protected long clientParam = 0L;
/*  232 */   protected long lastPacketSentTimeMs = 0L;
/*  233 */   protected long lastPacketReceivedTimeMs = 0L;
/*  234 */   private boolean traceProtocol = false;
/*  235 */   private boolean enablePacketDebug = false;
/*      */   private Calendar sessionCalendar;
/*      */   private boolean useConnectWithDb;
/*      */   private boolean needToGrabQueryFromPacket;
/*      */   private boolean autoGenerateTestcaseScript;
/*      */   private long threadId;
/*      */   private boolean useNanosForElapsedTime;
/*      */   private long slowQueryThreshold;
/*      */   private String queryTimingUnits;
/*  244 */   private boolean useDirectRowUnpack = true;
/*      */   private int useBufferRowSizeThreshold;
/*  246 */   private int commandCount = 0;
/*      */   private List statementInterceptors;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/* 1979 */   private int statementExecutionDepth = 0;
/*      */   private boolean useAutoSlowLog;
/*      */ 
/*      */   public MysqlIO(String host, int port, Properties props, String socketFactoryClassName, ConnectionImpl conn, int socketTimeout, int useBufferRowSizeThreshold)
/*      */     throws IOException, SQLException
/*      */   {
/*  267 */     this.connection = conn;
/*      */ 
/*  269 */     if (this.connection.getEnablePacketDebug()) {
/*  270 */       this.packetDebugRingBuffer = new LinkedList();
/*      */     }
/*  272 */     this.traceProtocol = this.connection.getTraceProtocol();
/*      */ 
/*  275 */     this.useAutoSlowLog = this.connection.getAutoSlowLog();
/*      */ 
/*  277 */     this.useBufferRowSizeThreshold = useBufferRowSizeThreshold;
/*  278 */     this.useDirectRowUnpack = this.connection.getUseDirectRowUnpack();
/*      */ 
/*  280 */     this.logSlowQueries = this.connection.getLogSlowQueries();
/*      */ 
/*  282 */     this.reusablePacket = new Buffer(1024);
/*  283 */     this.sendPacket = new Buffer(1024);
/*      */ 
/*  285 */     this.port = port;
/*  286 */     this.host = host;
/*      */ 
/*  288 */     this.socketFactoryClassName = socketFactoryClassName;
/*  289 */     this.socketFactory = createSocketFactory();
/*  290 */     this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*      */     try
/*      */     {
/*  293 */       this.mysqlConnection = this.socketFactory.connect(this.host, this.port, props);
/*      */ 
/*  297 */       if (socketTimeout != 0) {
/*      */         try {
/*  299 */           this.mysqlConnection.setSoTimeout(socketTimeout);
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/*      */       }
/*  305 */       this.mysqlConnection = this.socketFactory.beforeHandshake();
/*      */ 
/*  307 */       if (this.connection.getUseReadAheadInput()) {
/*  308 */         this.mysqlInput = new ReadAheadInputStream(this.mysqlConnection.getInputStream(), 16384, this.connection.getTraceProtocol(), this.connection.getLog());
/*      */       }
/*  311 */       else if (this.connection.useUnbufferedInput())
/*  312 */         this.mysqlInput = this.mysqlConnection.getInputStream();
/*      */       else {
/*  314 */         this.mysqlInput = new BufferedInputStream(this.mysqlConnection.getInputStream(), 16384);
/*      */       }
/*      */ 
/*  318 */       this.mysqlOutput = new BufferedOutputStream(this.mysqlConnection.getOutputStream(), 16384);
/*      */ 
/*  322 */       this.isInteractiveClient = this.connection.getInteractiveClient();
/*  323 */       this.profileSql = this.connection.getProfileSql();
/*  324 */       this.sessionCalendar = Calendar.getInstance();
/*  325 */       this.autoGenerateTestcaseScript = this.connection.getAutoGenerateTestcaseScript();
/*      */ 
/*  327 */       this.needToGrabQueryFromPacket = ((this.profileSql) || (this.logSlowQueries) || (this.autoGenerateTestcaseScript));
/*      */ 
/*  331 */       if ((this.connection.getUseNanosForElapsedTime()) && (Util.nanoTimeAvailable()))
/*      */       {
/*  333 */         this.useNanosForElapsedTime = true;
/*      */ 
/*  335 */         this.queryTimingUnits = Messages.getString("Nanoseconds");
/*      */       } else {
/*  337 */         this.queryTimingUnits = Messages.getString("Milliseconds");
/*      */       }
/*      */ 
/*  340 */       if (this.connection.getLogSlowQueries())
/*  341 */         calculateSlowQueryThreshold();
/*      */     }
/*      */     catch (IOException ioEx) {
/*  344 */       throw SQLError.createCommunicationsException(this.connection, 0L, 0L, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean hasLongColumnInfo()
/*      */   {
/*  354 */     return this.hasLongColumnInfo;
/*      */   }
/*      */ 
/*      */   protected boolean isDataAvailable() throws SQLException {
/*      */     try {
/*  359 */       return this.mysqlInput.available() > 0; } catch (IOException ioEx) {
/*      */     }
/*  361 */     throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected long getLastPacketSentTimeMs()
/*      */   {
/*  372 */     return this.lastPacketSentTimeMs;
/*      */   }
/*      */ 
/*      */   protected long getLastPacketReceivedTimeMs() {
/*  376 */     return this.lastPacketReceivedTimeMs;
/*      */   }
/*      */ 
/*      */   protected ResultSetImpl getResultSet(StatementImpl callingStatement, long columnCount, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, boolean isBinaryEncoded, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/*  406 */     Field[] fields = null;
/*      */ 
/*  410 */     if (metadataFromCache == null) {
/*  411 */       fields = new Field[(int)columnCount];
/*      */ 
/*  413 */       for (int i = 0; i < columnCount; i++) {
/*  414 */         Buffer fieldPacket = null;
/*      */ 
/*  416 */         fieldPacket = readPacket();
/*  417 */         fields[i] = unpackField(fieldPacket, false);
/*      */       }
/*      */     } else {
/*  420 */       for (int i = 0; i < columnCount; i++) {
/*  421 */         skipPacket();
/*      */       }
/*      */     }
/*      */ 
/*  425 */     Buffer packet = reuseAndReadPacket(this.reusablePacket);
/*      */ 
/*  427 */     readServerStatusForResultSets(packet);
/*      */ 
/*  433 */     if ((this.connection.versionMeetsMinimum(5, 0, 2)) && (this.connection.getUseCursorFetch()) && (isBinaryEncoded) && (callingStatement != null) && (callingStatement.getFetchSize() != 0) && (callingStatement.getResultSetType() == 1003))
/*      */     {
/*  439 */       ServerPreparedStatement prepStmt = (ServerPreparedStatement)callingStatement;
/*      */ 
/*  441 */       boolean usingCursor = true;
/*      */ 
/*  449 */       if (this.connection.versionMeetsMinimum(5, 0, 5)) {
/*  450 */         usingCursor = (this.serverStatus & 0x40) != 0;
/*      */       }
/*      */ 
/*  454 */       if (usingCursor) {
/*  455 */         RowData rows = new RowDataCursor(this, prepStmt, fields);
/*      */ 
/*  460 */         ResultSetImpl rs = buildResultSetWithRows(callingStatement, catalog, fields, rows, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */ 
/*  466 */         if (usingCursor) {
/*  467 */           rs.setFetchSize(callingStatement.getFetchSize());
/*      */         }
/*      */ 
/*  470 */         return rs;
/*      */       }
/*      */     }
/*      */ 
/*  474 */     RowData rowData = null;
/*      */ 
/*  476 */     if (!streamResults) {
/*  477 */       rowData = readSingleRowSet(columnCount, maxRows, resultSetConcurrency, isBinaryEncoded, metadataFromCache == null ? fields : metadataFromCache);
/*      */     }
/*      */     else
/*      */     {
/*  481 */       rowData = new RowDataDynamic(this, (int)columnCount, metadataFromCache == null ? fields : metadataFromCache, isBinaryEncoded);
/*      */ 
/*  484 */       this.streamingData = rowData;
/*      */     }
/*      */ 
/*  487 */     ResultSetImpl rs = buildResultSetWithRows(callingStatement, catalog, metadataFromCache == null ? fields : metadataFromCache, rowData, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */ 
/*  493 */     return rs;
/*      */   }
/*      */ 
/*      */   protected final void forceClose()
/*      */   {
/*      */     try
/*      */     {
/*  501 */       if (this.mysqlInput != null) {
/*  502 */         this.mysqlInput.close();
/*      */       }
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*  507 */       this.mysqlInput = null;
/*      */     }
/*      */     try
/*      */     {
/*  511 */       if (this.mysqlOutput != null) {
/*  512 */         this.mysqlOutput.close();
/*      */       }
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*  517 */       this.mysqlOutput = null;
/*      */     }
/*      */     try
/*      */     {
/*  521 */       if (this.mysqlConnection != null) {
/*  522 */         this.mysqlConnection.close();
/*      */       }
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*  527 */       this.mysqlConnection = null; }  } 
/*      */   // ERROR //
/*      */   protected final void skipPacket() throws SQLException { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_0
/*      */     //   2: getfield 8	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   5: aload_0
/*      */     //   6: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   9: iconst_0
/*      */     //   10: iconst_4
/*      */     //   11: invokespecial 125	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   14: istore_1
/*      */     //   15: iload_1
/*      */     //   16: iconst_4
/*      */     //   17: if_icmpge +20 -> 37
/*      */     //   20: aload_0
/*      */     //   21: invokevirtual 126	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   24: new 100	java/io/IOException
/*      */     //   27: dup
/*      */     //   28: ldc 127
/*      */     //   30: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   33: invokespecial 128	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   36: athrow
/*      */     //   37: aload_0
/*      */     //   38: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   41: iconst_0
/*      */     //   42: baload
/*      */     //   43: sipush 255
/*      */     //   46: iand
/*      */     //   47: aload_0
/*      */     //   48: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   51: iconst_1
/*      */     //   52: baload
/*      */     //   53: sipush 255
/*      */     //   56: iand
/*      */     //   57: bipush 8
/*      */     //   59: ishl
/*      */     //   60: iadd
/*      */     //   61: aload_0
/*      */     //   62: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   65: iconst_2
/*      */     //   66: baload
/*      */     //   67: sipush 255
/*      */     //   70: iand
/*      */     //   71: bipush 16
/*      */     //   73: ishl
/*      */     //   74: iadd
/*      */     //   75: istore_2
/*      */     //   76: aload_0
/*      */     //   77: getfield 50	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   80: ifeq +66 -> 146
/*      */     //   83: new 129	java/lang/StringBuffer
/*      */     //   86: dup
/*      */     //   87: invokespecial 130	java/lang/StringBuffer:<init>	()V
/*      */     //   90: astore_3
/*      */     //   91: aload_3
/*      */     //   92: ldc 131
/*      */     //   94: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   97: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   100: pop
/*      */     //   101: aload_3
/*      */     //   102: iload_2
/*      */     //   103: invokevirtual 133	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   106: pop
/*      */     //   107: aload_3
/*      */     //   108: ldc 134
/*      */     //   110: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   113: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   116: pop
/*      */     //   117: aload_3
/*      */     //   118: aload_0
/*      */     //   119: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   122: iconst_4
/*      */     //   123: invokestatic 135	com/mysql/jdbc/StringUtils:dumpAsHex	([BI)Ljava/lang/String;
/*      */     //   126: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   129: pop
/*      */     //   130: aload_0
/*      */     //   131: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   134: invokevirtual 77	com/mysql/jdbc/ConnectionImpl:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   137: aload_3
/*      */     //   138: invokevirtual 136	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   141: invokeinterface 137 2 0
/*      */     //   146: aload_0
/*      */     //   147: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   150: iconst_3
/*      */     //   151: baload
/*      */     //   152: istore_3
/*      */     //   153: aload_0
/*      */     //   154: getfield 2	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   157: ifne +25 -> 182
/*      */     //   160: aload_0
/*      */     //   161: getfield 51	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   164: ifeq +23 -> 187
/*      */     //   167: aload_0
/*      */     //   168: getfield 34	com/mysql/jdbc/MysqlIO:checkPacketSequence	Z
/*      */     //   171: ifeq +16 -> 187
/*      */     //   174: aload_0
/*      */     //   175: iload_3
/*      */     //   176: invokespecial 138	com/mysql/jdbc/MysqlIO:checkPacketSequencing	(B)V
/*      */     //   179: goto +8 -> 187
/*      */     //   182: aload_0
/*      */     //   183: iconst_0
/*      */     //   184: putfield 2	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   187: aload_0
/*      */     //   188: iload_3
/*      */     //   189: putfield 33	com/mysql/jdbc/MysqlIO:readPacketSequence	B
/*      */     //   192: aload_0
/*      */     //   193: aload_0
/*      */     //   194: getfield 8	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   197: iload_2
/*      */     //   198: i2l
/*      */     //   199: invokespecial 139	com/mysql/jdbc/MysqlIO:skipFully	(Ljava/io/InputStream;J)J
/*      */     //   202: pop2
/*      */     //   203: goto +43 -> 246
/*      */     //   206: astore_1
/*      */     //   207: aload_0
/*      */     //   208: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   211: aload_0
/*      */     //   212: getfield 48	com/mysql/jdbc/MysqlIO:lastPacketSentTimeMs	J
/*      */     //   215: aload_0
/*      */     //   216: getfield 49	com/mysql/jdbc/MysqlIO:lastPacketReceivedTimeMs	J
/*      */     //   219: aload_1
/*      */     //   220: aload_0
/*      */     //   221: invokevirtual 101	com/mysql/jdbc/MysqlIO:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
/*      */     //   224: invokestatic 102	com/mysql/jdbc/SQLError:createCommunicationsException	(Lcom/mysql/jdbc/ConnectionImpl;JJLjava/lang/Exception;Lcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
/*      */     //   227: athrow
/*      */     //   228: astore_1
/*      */     //   229: aload_0
/*      */     //   230: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   233: iconst_0
/*      */     //   234: iconst_0
/*      */     //   235: iconst_1
/*      */     //   236: aload_1
/*      */     //   237: invokevirtual 141	com/mysql/jdbc/ConnectionImpl:realClose	(ZZZLjava/lang/Throwable;)V
/*      */     //   240: aload_1
/*      */     //   241: athrow
/*      */     //   242: astore 4
/*      */     //   244: aload_1
/*      */     //   245: athrow
/*      */     //   246: return
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	203	206	java/io/IOException
/*      */     //   0	203	228	java/lang/OutOfMemoryError
/*      */     //   229	240	242	finally
/*      */     //   242	244	242	finally } 
/*      */   // ERROR //
/*      */   protected final Buffer readPacket() throws SQLException { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_0
/*      */     //   2: getfield 8	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   5: aload_0
/*      */     //   6: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   9: iconst_0
/*      */     //   10: iconst_4
/*      */     //   11: invokespecial 125	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   14: istore_1
/*      */     //   15: iload_1
/*      */     //   16: iconst_4
/*      */     //   17: if_icmpge +20 -> 37
/*      */     //   20: aload_0
/*      */     //   21: invokevirtual 126	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   24: new 100	java/io/IOException
/*      */     //   27: dup
/*      */     //   28: ldc 127
/*      */     //   30: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   33: invokespecial 128	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   36: athrow
/*      */     //   37: aload_0
/*      */     //   38: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   41: iconst_0
/*      */     //   42: baload
/*      */     //   43: sipush 255
/*      */     //   46: iand
/*      */     //   47: aload_0
/*      */     //   48: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   51: iconst_1
/*      */     //   52: baload
/*      */     //   53: sipush 255
/*      */     //   56: iand
/*      */     //   57: bipush 8
/*      */     //   59: ishl
/*      */     //   60: iadd
/*      */     //   61: aload_0
/*      */     //   62: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   65: iconst_2
/*      */     //   66: baload
/*      */     //   67: sipush 255
/*      */     //   70: iand
/*      */     //   71: bipush 16
/*      */     //   73: ishl
/*      */     //   74: iadd
/*      */     //   75: istore_2
/*      */     //   76: iload_2
/*      */     //   77: aload_0
/*      */     //   78: getfield 37	com/mysql/jdbc/MysqlIO:maxAllowedPacket	I
/*      */     //   81: if_icmple +18 -> 99
/*      */     //   84: new 142	com/mysql/jdbc/PacketTooBigException
/*      */     //   87: dup
/*      */     //   88: iload_2
/*      */     //   89: i2l
/*      */     //   90: aload_0
/*      */     //   91: getfield 37	com/mysql/jdbc/MysqlIO:maxAllowedPacket	I
/*      */     //   94: i2l
/*      */     //   95: invokespecial 143	com/mysql/jdbc/PacketTooBigException:<init>	(JJ)V
/*      */     //   98: athrow
/*      */     //   99: aload_0
/*      */     //   100: getfield 50	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   103: ifeq +66 -> 169
/*      */     //   106: new 129	java/lang/StringBuffer
/*      */     //   109: dup
/*      */     //   110: invokespecial 130	java/lang/StringBuffer:<init>	()V
/*      */     //   113: astore_3
/*      */     //   114: aload_3
/*      */     //   115: ldc 131
/*      */     //   117: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   120: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   123: pop
/*      */     //   124: aload_3
/*      */     //   125: iload_2
/*      */     //   126: invokevirtual 133	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   129: pop
/*      */     //   130: aload_3
/*      */     //   131: ldc 134
/*      */     //   133: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   136: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   139: pop
/*      */     //   140: aload_3
/*      */     //   141: aload_0
/*      */     //   142: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   145: iconst_4
/*      */     //   146: invokestatic 135	com/mysql/jdbc/StringUtils:dumpAsHex	([BI)Ljava/lang/String;
/*      */     //   149: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   152: pop
/*      */     //   153: aload_0
/*      */     //   154: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   157: invokevirtual 77	com/mysql/jdbc/ConnectionImpl:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   160: aload_3
/*      */     //   161: invokevirtual 136	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   164: invokeinterface 137 2 0
/*      */     //   169: aload_0
/*      */     //   170: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   173: iconst_3
/*      */     //   174: baload
/*      */     //   175: istore_3
/*      */     //   176: aload_0
/*      */     //   177: getfield 2	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   180: ifne +25 -> 205
/*      */     //   183: aload_0
/*      */     //   184: getfield 51	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   187: ifeq +23 -> 210
/*      */     //   190: aload_0
/*      */     //   191: getfield 34	com/mysql/jdbc/MysqlIO:checkPacketSequence	Z
/*      */     //   194: ifeq +16 -> 210
/*      */     //   197: aload_0
/*      */     //   198: iload_3
/*      */     //   199: invokespecial 138	com/mysql/jdbc/MysqlIO:checkPacketSequencing	(B)V
/*      */     //   202: goto +8 -> 210
/*      */     //   205: aload_0
/*      */     //   206: iconst_0
/*      */     //   207: putfield 2	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   210: aload_0
/*      */     //   211: iload_3
/*      */     //   212: putfield 33	com/mysql/jdbc/MysqlIO:readPacketSequence	B
/*      */     //   215: iload_2
/*      */     //   216: iconst_1
/*      */     //   217: iadd
/*      */     //   218: newarray byte
/*      */     //   220: astore 4
/*      */     //   222: aload_0
/*      */     //   223: aload_0
/*      */     //   224: getfield 8	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   227: aload 4
/*      */     //   229: iconst_0
/*      */     //   230: iload_2
/*      */     //   231: invokespecial 125	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   234: istore 5
/*      */     //   236: iload 5
/*      */     //   238: iload_2
/*      */     //   239: if_icmpeq +40 -> 279
/*      */     //   242: new 100	java/io/IOException
/*      */     //   245: dup
/*      */     //   246: new 129	java/lang/StringBuffer
/*      */     //   249: dup
/*      */     //   250: invokespecial 130	java/lang/StringBuffer:<init>	()V
/*      */     //   253: ldc 144
/*      */     //   255: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   258: iload_2
/*      */     //   259: invokevirtual 133	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   262: ldc 145
/*      */     //   264: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   267: iload 5
/*      */     //   269: invokevirtual 133	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   272: invokevirtual 136	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   275: invokespecial 128	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   278: athrow
/*      */     //   279: aload 4
/*      */     //   281: iload_2
/*      */     //   282: iconst_0
/*      */     //   283: bastore
/*      */     //   284: new 65	com/mysql/jdbc/Buffer
/*      */     //   287: dup
/*      */     //   288: aload 4
/*      */     //   290: invokespecial 146	com/mysql/jdbc/Buffer:<init>	([B)V
/*      */     //   293: astore 6
/*      */     //   295: aload 6
/*      */     //   297: iload_2
/*      */     //   298: iconst_1
/*      */     //   299: iadd
/*      */     //   300: invokevirtual 147	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   303: aload_0
/*      */     //   304: getfield 50	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   307: ifeq +52 -> 359
/*      */     //   310: new 129	java/lang/StringBuffer
/*      */     //   313: dup
/*      */     //   314: invokespecial 130	java/lang/StringBuffer:<init>	()V
/*      */     //   317: astore 7
/*      */     //   319: aload 7
/*      */     //   321: ldc 148
/*      */     //   323: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   326: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   329: pop
/*      */     //   330: aload 7
/*      */     //   332: aload 6
/*      */     //   334: iload_2
/*      */     //   335: invokestatic 149	com/mysql/jdbc/MysqlIO:getPacketDumpToLog	(Lcom/mysql/jdbc/Buffer;I)Ljava/lang/String;
/*      */     //   338: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   341: pop
/*      */     //   342: aload_0
/*      */     //   343: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   346: invokevirtual 77	com/mysql/jdbc/ConnectionImpl:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   349: aload 7
/*      */     //   351: invokevirtual 136	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   354: invokeinterface 137 2 0
/*      */     //   359: aload_0
/*      */     //   360: getfield 51	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   363: ifeq +16 -> 379
/*      */     //   366: aload_0
/*      */     //   367: iconst_0
/*      */     //   368: iconst_0
/*      */     //   369: iconst_0
/*      */     //   370: aload_0
/*      */     //   371: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   374: aload 6
/*      */     //   376: invokespecial 150	com/mysql/jdbc/MysqlIO:enqueuePacketForDebugging	(ZZI[BLcom/mysql/jdbc/Buffer;)V
/*      */     //   379: aload_0
/*      */     //   380: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   383: invokevirtual 151	com/mysql/jdbc/ConnectionImpl:getMaintainTimeStats	()Z
/*      */     //   386: ifeq +10 -> 396
/*      */     //   389: aload_0
/*      */     //   390: invokestatic 152	java/lang/System:currentTimeMillis	()J
/*      */     //   393: putfield 49	com/mysql/jdbc/MysqlIO:lastPacketReceivedTimeMs	J
/*      */     //   396: aload 6
/*      */     //   398: areturn
/*      */     //   399: astore_1
/*      */     //   400: aload_0
/*      */     //   401: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   404: aload_0
/*      */     //   405: getfield 48	com/mysql/jdbc/MysqlIO:lastPacketSentTimeMs	J
/*      */     //   408: aload_0
/*      */     //   409: getfield 49	com/mysql/jdbc/MysqlIO:lastPacketReceivedTimeMs	J
/*      */     //   412: aload_1
/*      */     //   413: aload_0
/*      */     //   414: invokevirtual 101	com/mysql/jdbc/MysqlIO:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
/*      */     //   417: invokestatic 102	com/mysql/jdbc/SQLError:createCommunicationsException	(Lcom/mysql/jdbc/ConnectionImpl;JJLjava/lang/Exception;Lcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
/*      */     //   420: athrow
/*      */     //   421: astore_1
/*      */     //   422: aload_0
/*      */     //   423: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   426: iconst_0
/*      */     //   427: iconst_0
/*      */     //   428: iconst_1
/*      */     //   429: aload_1
/*      */     //   430: invokevirtual 141	com/mysql/jdbc/ConnectionImpl:realClose	(ZZZLjava/lang/Throwable;)V
/*      */     //   433: aload_1
/*      */     //   434: athrow
/*      */     //   435: astore 8
/*      */     //   437: aload_1
/*      */     //   438: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	398	399	java/io/IOException
/*      */     //   0	398	421	java/lang/OutOfMemoryError
/*      */     //   422	433	435	finally
/*      */     //   435	437	435	finally } 
/*  700 */   protected final Field unpackField(Buffer packet, boolean extractDefaultValues) throws SQLException { if (this.use41Extensions)
/*      */     {
/*  703 */       if (this.has41NewNewProt)
/*      */       {
/*  705 */         int catalogNameStart = packet.getPosition() + 1;
/*  706 */         int catalogNameLength = packet.fastSkipLenString();
/*  707 */         catalogNameStart = adjustStartForFieldLength(catalogNameStart, catalogNameLength);
/*      */       }
/*      */ 
/*  710 */       int databaseNameStart = packet.getPosition() + 1;
/*  711 */       int databaseNameLength = packet.fastSkipLenString();
/*  712 */       databaseNameStart = adjustStartForFieldLength(databaseNameStart, databaseNameLength);
/*      */ 
/*  714 */       int tableNameStart = packet.getPosition() + 1;
/*  715 */       int tableNameLength = packet.fastSkipLenString();
/*  716 */       tableNameStart = adjustStartForFieldLength(tableNameStart, tableNameLength);
/*      */ 
/*  719 */       int originalTableNameStart = packet.getPosition() + 1;
/*  720 */       int originalTableNameLength = packet.fastSkipLenString();
/*  721 */       originalTableNameStart = adjustStartForFieldLength(originalTableNameStart, originalTableNameLength);
/*      */ 
/*  724 */       int nameStart = packet.getPosition() + 1;
/*  725 */       int nameLength = packet.fastSkipLenString();
/*      */ 
/*  727 */       nameStart = adjustStartForFieldLength(nameStart, nameLength);
/*      */ 
/*  730 */       int originalColumnNameStart = packet.getPosition() + 1;
/*  731 */       int originalColumnNameLength = packet.fastSkipLenString();
/*  732 */       originalColumnNameStart = adjustStartForFieldLength(originalColumnNameStart, originalColumnNameLength);
/*      */ 
/*  734 */       packet.readByte();
/*      */ 
/*  736 */       short charSetNumber = (short)packet.readInt();
/*      */ 
/*  738 */       long colLength = 0L;
/*      */ 
/*  740 */       if (this.has41NewNewProt)
/*  741 */         colLength = packet.readLong();
/*      */       else {
/*  743 */         colLength = packet.readLongInt();
/*      */       }
/*      */ 
/*  746 */       int colType = packet.readByte() & 0xFF;
/*      */ 
/*  748 */       short colFlag = 0;
/*      */ 
/*  750 */       if (this.hasLongColumnInfo)
/*  751 */         colFlag = (short)packet.readInt();
/*      */       else {
/*  753 */         colFlag = (short)(packet.readByte() & 0xFF);
/*      */       }
/*      */ 
/*  756 */       int colDecimals = packet.readByte() & 0xFF;
/*      */ 
/*  758 */       int defaultValueStart = -1;
/*  759 */       int defaultValueLength = -1;
/*      */ 
/*  761 */       if (extractDefaultValues) {
/*  762 */         defaultValueStart = packet.getPosition() + 1;
/*  763 */         defaultValueLength = packet.fastSkipLenString();
/*      */       }
/*      */ 
/*  766 */       Field field = new Field(this.connection, packet.getByteBuffer(), databaseNameStart, databaseNameLength, tableNameStart, tableNameLength, originalTableNameStart, originalTableNameLength, nameStart, nameLength, originalColumnNameStart, originalColumnNameLength, colLength, colType, colFlag, colDecimals, defaultValueStart, defaultValueLength, charSetNumber);
/*      */ 
/*  774 */       return field;
/*      */     }
/*      */ 
/*  777 */     int tableNameStart = packet.getPosition() + 1;
/*  778 */     int tableNameLength = packet.fastSkipLenString();
/*  779 */     tableNameStart = adjustStartForFieldLength(tableNameStart, tableNameLength);
/*      */ 
/*  781 */     int nameStart = packet.getPosition() + 1;
/*  782 */     int nameLength = packet.fastSkipLenString();
/*  783 */     nameStart = adjustStartForFieldLength(nameStart, nameLength);
/*      */ 
/*  785 */     int colLength = packet.readnBytes();
/*  786 */     int colType = packet.readnBytes();
/*  787 */     packet.readByte();
/*      */ 
/*  789 */     short colFlag = 0;
/*      */ 
/*  791 */     if (this.hasLongColumnInfo)
/*  792 */       colFlag = (short)packet.readInt();
/*      */     else {
/*  794 */       colFlag = (short)(packet.readByte() & 0xFF);
/*      */     }
/*      */ 
/*  797 */     int colDecimals = packet.readByte() & 0xFF;
/*      */ 
/*  799 */     if (this.colDecimalNeedsBump) {
/*  800 */       colDecimals++;
/*      */     }
/*      */ 
/*  803 */     Field field = new Field(this.connection, packet.getByteBuffer(), nameStart, nameLength, tableNameStart, tableNameLength, colLength, colType, colFlag, colDecimals);
/*      */ 
/*  807 */     return field; }
/*      */ 
/*      */   private int adjustStartForFieldLength(int nameStart, int nameLength)
/*      */   {
/*  811 */     if (nameLength < 251) {
/*  812 */       return nameStart;
/*      */     }
/*      */ 
/*  815 */     if ((nameLength >= 251) && (nameLength < 65536)) {
/*  816 */       return nameStart + 2;
/*      */     }
/*      */ 
/*  819 */     if ((nameLength >= 65536) && (nameLength < 16777216)) {
/*  820 */       return nameStart + 3;
/*      */     }
/*      */ 
/*  823 */     return nameStart + 8;
/*      */   }
/*      */ 
/*      */   protected boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
/*  827 */     if ((this.use41Extensions) && (this.connection.getElideSetAutoCommits())) {
/*  828 */       boolean autoCommitModeOnServer = (this.serverStatus & 0x2) != 0;
/*      */ 
/*  831 */       if ((!autoCommitFlag) && (versionMeetsMinimum(5, 0, 0)))
/*      */       {
/*  835 */         boolean inTransactionOnServer = (this.serverStatus & 0x1) != 0;
/*      */ 
/*  838 */         return !inTransactionOnServer;
/*      */       }
/*      */ 
/*  841 */       return autoCommitModeOnServer != autoCommitFlag;
/*      */     }
/*      */ 
/*  844 */     return true;
/*      */   }
/*      */ 
/*      */   protected boolean inTransactionOnServer() {
/*  848 */     return (this.serverStatus & 0x1) != 0;
/*      */   }
/*      */ 
/*      */   protected void changeUser(String userName, String password, String database)
/*      */     throws SQLException
/*      */   {
/*  862 */     this.packetSequence = -1;
/*      */ 
/*  864 */     int passwordLength = 16;
/*  865 */     int userLength = userName != null ? userName.length() : 0;
/*  866 */     int databaseLength = database != null ? database.length() : 0;
/*      */ 
/*  868 */     int packLength = (userLength + passwordLength + databaseLength) * 2 + 7 + 4 + 33;
/*      */ 
/*  870 */     if ((this.serverCapabilities & 0x8000) != 0) {
/*  871 */       Buffer changeUserPacket = new Buffer(packLength + 1);
/*  872 */       changeUserPacket.writeByte(17);
/*      */ 
/*  874 */       if (versionMeetsMinimum(4, 1, 1)) {
/*  875 */         secureAuth411(changeUserPacket, packLength, userName, password, database, false);
/*      */       }
/*      */       else {
/*  878 */         secureAuth(changeUserPacket, packLength, userName, password, database, false);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  883 */       Buffer packet = new Buffer(packLength);
/*  884 */       packet.writeByte(17);
/*      */ 
/*  887 */       packet.writeString(userName);
/*      */ 
/*  889 */       if (this.protocolVersion > 9)
/*  890 */         packet.writeString(Util.newCrypt(password, this.seed));
/*      */       else {
/*  892 */         packet.writeString(Util.oldCrypt(password, this.seed));
/*      */       }
/*      */ 
/*  895 */       boolean localUseConnectWithDb = (this.useConnectWithDb) && (database != null) && (database.length() > 0);
/*      */ 
/*  898 */       if (localUseConnectWithDb) {
/*  899 */         packet.writeString(database);
/*      */       }
/*      */ 
/*  902 */       send(packet, packet.getPosition());
/*  903 */       checkErrorPacket();
/*      */ 
/*  905 */       if (!localUseConnectWithDb)
/*  906 */         changeDatabaseTo(database);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Buffer checkErrorPacket()
/*      */     throws SQLException
/*      */   {
/*  920 */     return checkErrorPacket(-1);
/*      */   }
/*      */ 
/*      */   protected void checkForCharsetMismatch()
/*      */   {
/*  927 */     if ((this.connection.getUseUnicode()) && (this.connection.getEncoding() != null))
/*      */     {
/*  929 */       String encodingToCheck = jvmPlatformCharset;
/*      */ 
/*  931 */       if (encodingToCheck == null) {
/*  932 */         encodingToCheck = System.getProperty("file.encoding");
/*      */       }
/*      */ 
/*  935 */       if (encodingToCheck == null)
/*  936 */         this.platformDbCharsetMatches = false;
/*      */       else
/*  938 */         this.platformDbCharsetMatches = encodingToCheck.equals(this.connection.getEncoding());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void clearInputStream() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  946 */       int len = this.mysqlInput.available();
/*      */ 
/*  948 */       while (len > 0) {
/*  949 */         this.mysqlInput.skip(len);
/*  950 */         len = this.mysqlInput.available();
/*      */       }
/*      */     } catch (IOException ioEx) {
/*  953 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void resetReadPacketSequence()
/*      */   {
/*  959 */     this.readPacketSequence = 0;
/*      */   }
/*      */ 
/*      */   protected void dumpPacketRingBuffer() throws SQLException {
/*  963 */     if ((this.packetDebugRingBuffer != null) && (this.connection.getEnablePacketDebug()))
/*      */     {
/*  965 */       StringBuffer dumpBuffer = new StringBuffer();
/*      */ 
/*  967 */       dumpBuffer.append("Last " + this.packetDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n");
/*      */ 
/*  969 */       dumpBuffer.append("\n");
/*      */ 
/*  971 */       Iterator ringBufIter = this.packetDebugRingBuffer.iterator();
/*  972 */       while (ringBufIter.hasNext()) {
/*  973 */         dumpBuffer.append((StringBuffer)ringBufIter.next());
/*  974 */         dumpBuffer.append("\n");
/*      */       }
/*      */ 
/*  977 */       this.connection.getLog().logTrace(dumpBuffer.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void explainSlowQuery(byte[] querySQL, String truncatedQuery)
/*      */     throws SQLException
/*      */   {
/*  991 */     if (StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, "SELECT"))
/*      */     {
/*  993 */       PreparedStatement stmt = null;
/*  994 */       ResultSet rs = null;
/*      */       try
/*      */       {
/*  997 */         stmt = (PreparedStatement)this.connection.clientPrepareStatement("EXPLAIN ?");
/*  998 */         stmt.setBytesNoEscapeNoQuotes(1, querySQL);
/*  999 */         rs = stmt.executeQuery();
/*      */ 
/* 1001 */         StringBuffer explainResults = new StringBuffer(Messages.getString("MysqlIO.8") + truncatedQuery + Messages.getString("MysqlIO.9"));
/*      */ 
/* 1005 */         ResultSetUtil.appendResultSetSlashGStyle(explainResults, rs);
/*      */ 
/* 1007 */         this.connection.getLog().logWarn(explainResults.toString());
/*      */       } catch (SQLException sqlEx) {
/*      */       } finally {
/* 1010 */         if (rs != null) {
/* 1011 */           rs.close();
/*      */         }
/*      */ 
/* 1014 */         if (stmt != null)
/* 1015 */           stmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static int getMaxBuf()
/*      */   {
/* 1023 */     return maxBufferSize;
/*      */   }
/*      */ 
/*      */   final int getServerMajorVersion()
/*      */   {
/* 1032 */     return this.serverMajorVersion;
/*      */   }
/*      */ 
/*      */   final int getServerMinorVersion()
/*      */   {
/* 1041 */     return this.serverMinorVersion;
/*      */   }
/*      */ 
/*      */   final int getServerSubMinorVersion()
/*      */   {
/* 1050 */     return this.serverSubMinorVersion;
/*      */   }
/*      */ 
/*      */   String getServerVersion()
/*      */   {
/* 1059 */     return this.serverVersion;
/*      */   }
/*      */ 
/*      */   void doHandshake(String user, String password, String database)
/*      */     throws SQLException
/*      */   {
/* 1076 */     this.checkPacketSequence = false;
/* 1077 */     this.readPacketSequence = 0;
/*      */ 
/* 1079 */     Buffer buf = readPacket();
/*      */ 
/* 1082 */     this.protocolVersion = buf.readByte();
/*      */ 
/* 1084 */     if (this.protocolVersion == -1) {
/*      */       try {
/* 1086 */         this.mysqlConnection.close();
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/* 1091 */       int errno = 2000;
/*      */ 
/* 1093 */       errno = buf.readInt();
/*      */ 
/* 1095 */       String serverErrorMessage = buf.readString("ASCII", getExceptionInterceptor());
/*      */ 
/* 1097 */       StringBuffer errorBuf = new StringBuffer(Messages.getString("MysqlIO.10"));
/*      */ 
/* 1099 */       errorBuf.append(serverErrorMessage);
/* 1100 */       errorBuf.append("\"");
/*      */ 
/* 1102 */       String xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */ 
/* 1105 */       throw SQLError.createSQLException(SQLError.get(xOpen) + ", " + errorBuf.toString(), xOpen, errno, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1109 */     this.serverVersion = buf.readString("ASCII", getExceptionInterceptor());
/*      */ 
/* 1112 */     int point = this.serverVersion.indexOf('.');
/*      */ 
/* 1114 */     if (point != -1) {
/*      */       try {
/* 1116 */         int n = Integer.parseInt(this.serverVersion.substring(0, point));
/* 1117 */         this.serverMajorVersion = n;
/*      */       }
/*      */       catch (NumberFormatException NFE1)
/*      */       {
/*      */       }
/* 1122 */       String remaining = this.serverVersion.substring(point + 1, this.serverVersion.length());
/*      */ 
/* 1124 */       point = remaining.indexOf('.');
/*      */ 
/* 1126 */       if (point != -1) {
/*      */         try {
/* 1128 */           int n = Integer.parseInt(remaining.substring(0, point));
/* 1129 */           this.serverMinorVersion = n;
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/*      */         }
/* 1134 */         remaining = remaining.substring(point + 1, remaining.length());
/*      */ 
/* 1136 */         int pos = 0;
/*      */ 
/* 1138 */         while ((pos < remaining.length()) && 
/* 1139 */           (remaining.charAt(pos) >= '0') && (remaining.charAt(pos) <= '9'))
/*      */         {
/* 1144 */           pos++;
/*      */         }
/*      */         try
/*      */         {
/* 1148 */           int n = Integer.parseInt(remaining.substring(0, pos));
/* 1149 */           this.serverSubMinorVersion = n;
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/* 1156 */     if (versionMeetsMinimum(4, 0, 8)) {
/* 1157 */       this.maxThreeBytes = 16777215;
/* 1158 */       this.useNewLargePackets = true;
/*      */     } else {
/* 1160 */       this.maxThreeBytes = 16581375;
/* 1161 */       this.useNewLargePackets = false;
/*      */     }
/*      */ 
/* 1164 */     this.colDecimalNeedsBump = versionMeetsMinimum(3, 23, 0);
/* 1165 */     this.colDecimalNeedsBump = (!versionMeetsMinimum(3, 23, 15));
/* 1166 */     this.useNewUpdateCounts = versionMeetsMinimum(3, 22, 5);
/*      */ 
/* 1168 */     this.threadId = buf.readLong();
/* 1169 */     this.seed = buf.readString("ASCII", getExceptionInterceptor());
/*      */ 
/* 1171 */     this.serverCapabilities = 0;
/*      */ 
/* 1173 */     if (buf.getPosition() < buf.getBufLength()) {
/* 1174 */       this.serverCapabilities = buf.readInt();
/*      */     }
/*      */ 
/* 1177 */     if (versionMeetsMinimum(4, 1, 1)) {
/* 1178 */       int position = buf.getPosition();
/*      */ 
/* 1181 */       this.serverCharsetIndex = (buf.readByte() & 0xFF);
/* 1182 */       this.serverStatus = buf.readInt();
/* 1183 */       checkTransactionState(0);
/* 1184 */       buf.setPosition(position + 16);
/*      */ 
/* 1186 */       String seedPart2 = buf.readString("ASCII", getExceptionInterceptor());
/* 1187 */       StringBuffer newSeed = new StringBuffer(20);
/* 1188 */       newSeed.append(this.seed);
/* 1189 */       newSeed.append(seedPart2);
/* 1190 */       this.seed = newSeed.toString();
/*      */     }
/*      */ 
/* 1193 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()))
/*      */     {
/* 1195 */       this.clientParam |= 32L;
/*      */     }
/*      */ 
/* 1198 */     this.useConnectWithDb = ((database != null) && (database.length() > 0) && (!this.connection.getCreateDatabaseIfNotExist()));
/*      */ 
/* 1202 */     if (this.useConnectWithDb) {
/* 1203 */       this.clientParam |= 8L;
/*      */     }
/*      */ 
/* 1206 */     if (((this.serverCapabilities & 0x800) == 0) && (this.connection.getUseSSL()))
/*      */     {
/* 1208 */       if (this.connection.getRequireSSL()) {
/* 1209 */         this.connection.close();
/* 1210 */         forceClose();
/* 1211 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.15"), "08001", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1215 */       this.connection.setUseSSL(false);
/*      */     }
/*      */ 
/* 1218 */     if ((this.serverCapabilities & 0x4) != 0)
/*      */     {
/* 1220 */       this.clientParam |= 4L;
/* 1221 */       this.hasLongColumnInfo = true;
/*      */     }
/*      */ 
/* 1225 */     if (!this.connection.getUseAffectedRows()) {
/* 1226 */       this.clientParam |= 2L;
/*      */     }
/*      */ 
/* 1229 */     if (this.connection.getAllowLoadLocalInfile()) {
/* 1230 */       this.clientParam |= 128L;
/*      */     }
/*      */ 
/* 1233 */     if (this.isInteractiveClient) {
/* 1234 */       this.clientParam |= 1024L;
/*      */     }
/*      */ 
/* 1238 */     if (this.protocolVersion > 9)
/* 1239 */       this.clientParam |= 1L;
/*      */     else {
/* 1241 */       this.clientParam &= -2L;
/*      */     }
/*      */ 
/* 1247 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 1248 */       if (versionMeetsMinimum(4, 1, 1)) {
/* 1249 */         this.clientParam |= 512L;
/* 1250 */         this.has41NewNewProt = true;
/*      */ 
/* 1253 */         this.clientParam |= 8192L;
/*      */ 
/* 1256 */         this.clientParam |= 131072L;
/*      */ 
/* 1261 */         if (this.connection.getAllowMultiQueries())
/* 1262 */           this.clientParam |= 65536L;
/*      */       }
/*      */       else {
/* 1265 */         this.clientParam |= 16384L;
/* 1266 */         this.has41NewNewProt = false;
/*      */       }
/*      */ 
/* 1269 */       this.use41Extensions = true;
/*      */     }
/*      */ 
/* 1272 */     int passwordLength = 16;
/* 1273 */     int userLength = user != null ? user.length() : 0;
/* 1274 */     int databaseLength = database != null ? database.length() : 0;
/*      */ 
/* 1276 */     int packLength = (userLength + passwordLength + databaseLength) * 2 + 7 + 4 + 33;
/*      */ 
/* 1278 */     Buffer packet = null;
/*      */ 
/* 1280 */     if (!this.connection.getUseSSL()) {
/* 1281 */       if ((this.serverCapabilities & 0x8000) != 0) {
/* 1282 */         this.clientParam |= 32768L;
/*      */ 
/* 1284 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 1285 */           secureAuth411(null, packLength, user, password, database, true);
/*      */         }
/*      */         else
/* 1288 */           secureAuth(null, packLength, user, password, database, true);
/*      */       }
/*      */       else
/*      */       {
/* 1292 */         packet = new Buffer(packLength);
/*      */ 
/* 1294 */         if ((this.clientParam & 0x4000) != 0L) {
/* 1295 */           if (versionMeetsMinimum(4, 1, 1)) {
/* 1296 */             packet.writeLong(this.clientParam);
/* 1297 */             packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 1302 */             packet.writeByte(8);
/*      */ 
/* 1305 */             packet.writeBytesNoNull(new byte[23]);
/*      */           } else {
/* 1307 */             packet.writeLong(this.clientParam);
/* 1308 */             packet.writeLong(this.maxThreeBytes);
/*      */           }
/*      */         } else {
/* 1311 */           packet.writeInt((int)this.clientParam);
/* 1312 */           packet.writeLongInt(this.maxThreeBytes);
/*      */         }
/*      */ 
/* 1316 */         packet.writeString(user, "Cp1252", this.connection);
/*      */ 
/* 1318 */         if (this.protocolVersion > 9)
/* 1319 */           packet.writeString(Util.newCrypt(password, this.seed), "Cp1252", this.connection);
/*      */         else {
/* 1321 */           packet.writeString(Util.oldCrypt(password, this.seed), "Cp1252", this.connection);
/*      */         }
/*      */ 
/* 1324 */         if (this.useConnectWithDb) {
/* 1325 */           packet.writeString(database, "Cp1252", this.connection);
/*      */         }
/*      */ 
/* 1328 */         send(packet, packet.getPosition());
/*      */       }
/*      */     }
/* 1331 */     else negotiateSSLConnection(user, password, database, packLength);
/*      */ 
/* 1337 */     if (!versionMeetsMinimum(4, 1, 1)) {
/* 1338 */       checkErrorPacket();
/*      */     }
/*      */ 
/* 1344 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()))
/*      */     {
/* 1348 */       this.deflater = new Deflater();
/* 1349 */       this.useCompression = true;
/* 1350 */       this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
/*      */     }
/*      */ 
/* 1354 */     if (!this.useConnectWithDb) {
/* 1355 */       changeDatabaseTo(database);
/*      */     }
/*      */     try
/*      */     {
/* 1359 */       this.mysqlConnection = this.socketFactory.afterHandshake();
/*      */     } catch (IOException ioEx) {
/* 1361 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void changeDatabaseTo(String database) throws SQLException {
/* 1366 */     if ((database == null) || (database.length() == 0)) {
/* 1367 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1371 */       sendCommand(2, database, null, false, null, 0);
/*      */     } catch (Exception ex) {
/* 1373 */       if (this.connection.getCreateDatabaseIfNotExist()) {
/* 1374 */         sendCommand(3, "CREATE DATABASE IF NOT EXISTS " + database, null, false, null, 0);
/*      */ 
/* 1377 */         sendCommand(2, database, null, false, null, 0);
/*      */       } else {
/* 1379 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex, getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   final ResultSetRow nextRow(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacketForBufferRow, Buffer existingRowPacket)
/*      */     throws SQLException
/*      */   {
/* 1407 */     if ((this.useDirectRowUnpack) && (existingRowPacket == null) && (!isBinaryEncoded) && (!useBufferRowIfPossible) && (!useBufferRowExplicit))
/*      */     {
/* 1410 */       return nextRowFast(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacketForBufferRow);
/*      */     }
/*      */ 
/* 1414 */     Buffer rowPacket = null;
/*      */ 
/* 1416 */     if (existingRowPacket == null) {
/* 1417 */       rowPacket = checkErrorPacket();
/*      */ 
/* 1419 */       if ((!useBufferRowExplicit) && (useBufferRowIfPossible) && 
/* 1420 */         (rowPacket.getBufLength() > this.useBufferRowSizeThreshold)) {
/* 1421 */         useBufferRowExplicit = true;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1427 */       rowPacket = existingRowPacket;
/* 1428 */       checkErrorPacket(existingRowPacket);
/*      */     }
/*      */ 
/* 1432 */     if (!isBinaryEncoded)
/*      */     {
/* 1437 */       rowPacket.setPosition(rowPacket.getPosition() - 1);
/*      */ 
/* 1439 */       if (!rowPacket.isLastDataPacket()) {
/* 1440 */         if ((resultSetConcurrency == 1008) || ((!useBufferRowIfPossible) && (!useBufferRowExplicit)))
/*      */         {
/* 1443 */           byte[][] rowData = new byte[columnCount][];
/*      */ 
/* 1445 */           for (int i = 0; i < columnCount; i++) {
/* 1446 */             rowData[i] = rowPacket.readLenByteArray(0);
/*      */           }
/*      */ 
/* 1449 */           return new ByteArrayRow(rowData, getExceptionInterceptor());
/*      */         }
/*      */ 
/* 1452 */         if (!canReuseRowPacketForBufferRow) {
/* 1453 */           this.reusablePacket = new Buffer(rowPacket.getBufLength());
/*      */         }
/*      */ 
/* 1456 */         return new BufferRow(rowPacket, fields, false, getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1460 */       readServerStatusForResultSets(rowPacket);
/*      */ 
/* 1462 */       return null;
/*      */     }
/*      */ 
/* 1469 */     if (!rowPacket.isLastDataPacket()) {
/* 1470 */       if ((resultSetConcurrency == 1008) || ((!useBufferRowIfPossible) && (!useBufferRowExplicit)))
/*      */       {
/* 1472 */         return unpackBinaryResultSetRow(fields, rowPacket, resultSetConcurrency);
/*      */       }
/*      */ 
/* 1476 */       if (!canReuseRowPacketForBufferRow) {
/* 1477 */         this.reusablePacket = new Buffer(rowPacket.getBufLength());
/*      */       }
/*      */ 
/* 1480 */       return new BufferRow(rowPacket, fields, true, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1483 */     rowPacket.setPosition(rowPacket.getPosition() - 1);
/* 1484 */     readServerStatusForResultSets(rowPacket);
/*      */ 
/* 1486 */     return null;
/*      */   }
/*      */ 
/*      */   final ResultSetRow nextRowFast(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacket)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1495 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/* 1498 */       if (lengthRead < 4) {
/* 1499 */         forceClose();
/* 1500 */         throw new RuntimeException(Messages.getString("MysqlIO.43"));
/*      */       }
/*      */ 
/* 1503 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/* 1508 */       if (packetLength == this.maxThreeBytes) {
/* 1509 */         reuseAndReadPacket(this.reusablePacket, packetLength);
/*      */ 
/* 1512 */         return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacket, this.reusablePacket);
/*      */       }
/*      */ 
/* 1519 */       if (packetLength > this.useBufferRowSizeThreshold) {
/* 1520 */         reuseAndReadPacket(this.reusablePacket, packetLength);
/*      */ 
/* 1523 */         return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, true, true, false, this.reusablePacket);
/*      */       }
/*      */ 
/* 1528 */       int remaining = packetLength;
/*      */ 
/* 1530 */       boolean firstTime = true;
/*      */ 
/* 1532 */       byte[][] rowData = (byte[][])null;
/*      */ 
/* 1534 */       for (int i = 0; i < columnCount; i++)
/*      */       {
/* 1536 */         int sw = this.mysqlInput.read() & 0xFF;
/* 1537 */         remaining--;
/*      */ 
/* 1539 */         if (firstTime) {
/* 1540 */           if (sw == 255)
/*      */           {
/* 1545 */             Buffer errorPacket = new Buffer(packetLength + 4);
/* 1546 */             errorPacket.setPosition(0);
/* 1547 */             errorPacket.writeByte(this.packetHeaderBuf[0]);
/* 1548 */             errorPacket.writeByte(this.packetHeaderBuf[1]);
/* 1549 */             errorPacket.writeByte(this.packetHeaderBuf[2]);
/* 1550 */             errorPacket.writeByte(1);
/* 1551 */             errorPacket.writeByte((byte)sw);
/* 1552 */             readFully(this.mysqlInput, errorPacket.getByteBuffer(), 5, packetLength - 1);
/* 1553 */             errorPacket.setPosition(4);
/* 1554 */             checkErrorPacket(errorPacket);
/*      */           }
/*      */ 
/* 1557 */           if ((sw == 254) && (packetLength < 9)) {
/* 1558 */             if (this.use41Extensions) {
/* 1559 */               this.warningCount = (this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8);
/*      */ 
/* 1561 */               remaining -= 2;
/*      */ 
/* 1563 */               if (this.warningCount > 0) {
/* 1564 */                 this.hadWarnings = true;
/*      */               }
/*      */ 
/* 1570 */               this.oldServerStatus = this.serverStatus;
/*      */ 
/* 1572 */               this.serverStatus = (this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8);
/*      */ 
/* 1574 */               checkTransactionState(this.oldServerStatus);
/*      */ 
/* 1576 */               remaining -= 2;
/*      */ 
/* 1578 */               if (remaining > 0) {
/* 1579 */                 skipFully(this.mysqlInput, remaining);
/*      */               }
/*      */             }
/*      */ 
/* 1583 */             return null;
/*      */           }
/*      */ 
/* 1586 */           rowData = new byte[columnCount][];
/*      */ 
/* 1588 */           firstTime = false;
/*      */         }
/*      */ 
/* 1591 */         int len = 0;
/*      */ 
/* 1593 */         switch (sw) {
/*      */         case 251:
/* 1595 */           len = -1;
/* 1596 */           break;
/*      */         case 252:
/* 1599 */           len = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8;
/*      */ 
/* 1601 */           remaining -= 2;
/* 1602 */           break;
/*      */         case 253:
/* 1605 */           len = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8 | (this.mysqlInput.read() & 0xFF) << 16;
/*      */ 
/* 1609 */           remaining -= 3;
/* 1610 */           break;
/*      */         case 254:
/* 1613 */           len = (int)(this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8 | (this.mysqlInput.read() & 0xFF) << 16 | (this.mysqlInput.read() & 0xFF) << 24 | (this.mysqlInput.read() & 0xFF) << 32 | (this.mysqlInput.read() & 0xFF) << 40 | (this.mysqlInput.read() & 0xFF) << 48 | (this.mysqlInput.read() & 0xFF) << 56);
/*      */ 
/* 1621 */           remaining -= 8;
/* 1622 */           break;
/*      */         default:
/* 1625 */           len = sw;
/*      */         }
/*      */ 
/* 1628 */         if (len == -1) {
/* 1629 */           rowData[i] = null;
/* 1630 */         } else if (len == 0) {
/* 1631 */           rowData[i] = Constants.EMPTY_BYTE_ARRAY;
/*      */         } else {
/* 1633 */           rowData[i] = new byte[len];
/*      */ 
/* 1635 */           int bytesRead = readFully(this.mysqlInput, rowData[i], 0, len);
/*      */ 
/* 1638 */           if (bytesRead != len) {
/* 1639 */             throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException(Messages.getString("MysqlIO.43")), getExceptionInterceptor());
/*      */           }
/*      */ 
/* 1644 */           remaining -= bytesRead;
/*      */         }
/*      */       }
/*      */ 
/* 1648 */       if (remaining > 0) {
/* 1649 */         skipFully(this.mysqlInput, remaining);
/*      */       }
/*      */ 
/* 1652 */       return new ByteArrayRow(rowData, getExceptionInterceptor()); } catch (IOException ioEx) {
/*      */     }
/* 1654 */     throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   final void quit()
/*      */     throws SQLException
/*      */   {
/* 1665 */     Buffer packet = new Buffer(6);
/* 1666 */     this.packetSequence = -1;
/* 1667 */     packet.writeByte(1);
/* 1668 */     send(packet, packet.getPosition());
/* 1669 */     forceClose();
/*      */   }
/*      */ 
/*      */   Buffer getSharedSendPacket()
/*      */   {
/* 1679 */     if (this.sharedSendPacket == null) {
/* 1680 */       this.sharedSendPacket = new Buffer(1024);
/*      */     }
/*      */ 
/* 1683 */     return this.sharedSendPacket;
/*      */   }
/*      */ 
/*      */   void closeStreamer(RowData streamer) throws SQLException {
/* 1687 */     if (this.streamingData == null) {
/* 1688 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.17") + streamer + Messages.getString("MysqlIO.18"), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1692 */     if (streamer != this.streamingData) {
/* 1693 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.19") + streamer + Messages.getString("MysqlIO.20") + Messages.getString("MysqlIO.21") + Messages.getString("MysqlIO.22"), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1699 */     this.streamingData = null;
/*      */   }
/*      */ 
/*      */   boolean tackOnMoreStreamingResults(ResultSetImpl addingTo) throws SQLException {
/* 1703 */     if ((this.serverStatus & 0x8) != 0)
/*      */     {
/* 1705 */       boolean moreRowSetsExist = true;
/* 1706 */       ResultSetImpl currentResultSet = addingTo;
/* 1707 */       boolean firstTime = true;
/*      */ 
/* 1709 */       while ((moreRowSetsExist) && (
/* 1710 */         (firstTime) || (!currentResultSet.reallyResult())))
/*      */       {
/* 1714 */         firstTime = false;
/*      */ 
/* 1716 */         Buffer fieldPacket = checkErrorPacket();
/* 1717 */         fieldPacket.setPosition(0);
/*      */ 
/* 1719 */         java.sql.Statement owningStatement = addingTo.getStatement();
/*      */ 
/* 1721 */         int maxRows = owningStatement.getMaxRows();
/*      */ 
/* 1725 */         ResultSetImpl newResultSet = readResultsForQueryOrUpdate((StatementImpl)owningStatement, maxRows, owningStatement.getResultSetType(), owningStatement.getResultSetConcurrency(), true, owningStatement.getConnection().getCatalog(), fieldPacket, addingTo.isBinaryEncoded, -1L, null);
/*      */ 
/* 1733 */         currentResultSet.setNextResultSet(newResultSet);
/*      */ 
/* 1735 */         currentResultSet = newResultSet;
/*      */ 
/* 1737 */         moreRowSetsExist = (this.serverStatus & 0x8) != 0;
/*      */ 
/* 1739 */         if ((!currentResultSet.reallyResult()) && (!moreRowSetsExist))
/*      */         {
/* 1741 */           return false;
/*      */         }
/*      */       }
/*      */ 
/* 1745 */       return true;
/*      */     }
/*      */ 
/* 1748 */     return false;
/*      */   }
/*      */ 
/*      */   ResultSetImpl readAllResults(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/* 1756 */     resultPacket.setPosition(resultPacket.getPosition() - 1);
/*      */ 
/* 1758 */     ResultSetImpl topLevelResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
/*      */ 
/* 1763 */     ResultSetImpl currentResultSet = topLevelResultSet;
/*      */ 
/* 1765 */     boolean checkForMoreResults = (this.clientParam & 0x20000) != 0L;
/*      */ 
/* 1768 */     boolean serverHasMoreResults = (this.serverStatus & 0x8) != 0;
/*      */ 
/* 1774 */     if ((serverHasMoreResults) && (streamResults))
/*      */     {
/* 1779 */       if (topLevelResultSet.getUpdateCount() != -1L) {
/* 1780 */         tackOnMoreStreamingResults(topLevelResultSet);
/*      */       }
/*      */ 
/* 1783 */       reclaimLargeReusablePacket();
/*      */ 
/* 1785 */       return topLevelResultSet;
/*      */     }
/*      */ 
/* 1788 */     boolean moreRowSetsExist = checkForMoreResults & serverHasMoreResults;
/*      */ 
/* 1790 */     while (moreRowSetsExist) {
/* 1791 */       Buffer fieldPacket = checkErrorPacket();
/* 1792 */       fieldPacket.setPosition(0);
/*      */ 
/* 1794 */       ResultSetImpl newResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, fieldPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
/*      */ 
/* 1799 */       currentResultSet.setNextResultSet(newResultSet);
/*      */ 
/* 1801 */       currentResultSet = newResultSet;
/*      */ 
/* 1803 */       moreRowSetsExist = (this.serverStatus & 0x8) != 0;
/*      */     }
/*      */ 
/* 1806 */     if (!streamResults) {
/* 1807 */       clearInputStream();
/*      */     }
/*      */ 
/* 1810 */     reclaimLargeReusablePacket();
/*      */ 
/* 1812 */     return topLevelResultSet;
/*      */   }
/*      */ 
/*      */   void resetMaxBuf()
/*      */   {
/* 1819 */     this.maxAllowedPacket = this.connection.getMaxAllowedPacket();
/*      */   }
/*      */ 
/*      */   final Buffer sendCommand(int command, String extraData, Buffer queryPacket, boolean skipCheck, String extraDataCharEncoding, int timeoutMillis)
/*      */     throws SQLException
/*      */   {
/* 1845 */     this.commandCount += 1;
/*      */ 
/* 1852 */     this.enablePacketDebug = this.connection.getEnablePacketDebug();
/* 1853 */     this.readPacketSequence = 0;
/*      */ 
/* 1855 */     int oldTimeout = 0;
/*      */ 
/* 1857 */     if (timeoutMillis != 0) {
/*      */       try {
/* 1859 */         oldTimeout = this.mysqlConnection.getSoTimeout();
/* 1860 */         this.mysqlConnection.setSoTimeout(timeoutMillis);
/*      */       } catch (SocketException e) {
/* 1862 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1869 */       checkForOutstandingStreamingData();
/*      */ 
/* 1874 */       this.oldServerStatus = this.serverStatus;
/* 1875 */       this.serverStatus = 0;
/* 1876 */       this.hadWarnings = false;
/* 1877 */       this.warningCount = 0;
/*      */ 
/* 1879 */       this.queryNoIndexUsed = false;
/* 1880 */       this.queryBadIndexUsed = false;
/* 1881 */       this.serverQueryWasSlow = false;
/*      */ 
/* 1887 */       if (this.useCompression) {
/* 1888 */         int bytesLeft = this.mysqlInput.available();
/*      */ 
/* 1890 */         if (bytesLeft > 0) {
/* 1891 */           this.mysqlInput.skip(bytesLeft);
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 1896 */         clearInputStream();
/*      */ 
/* 1905 */         if (queryPacket == null) {
/* 1906 */           int packLength = 8 + (extraData != null ? extraData.length() : 0) + 2;
/*      */ 
/* 1909 */           if (this.sendPacket == null) {
/* 1910 */             this.sendPacket = new Buffer(packLength);
/*      */           }
/*      */ 
/* 1913 */           this.packetSequence = -1;
/* 1914 */           this.readPacketSequence = 0;
/* 1915 */           this.checkPacketSequence = true;
/* 1916 */           this.sendPacket.clear();
/*      */ 
/* 1918 */           this.sendPacket.writeByte((byte)command);
/*      */ 
/* 1920 */           if ((command == 2) || (command == 5) || (command == 6) || (command == 3) || (command == 22))
/*      */           {
/* 1925 */             if (extraDataCharEncoding == null)
/* 1926 */               this.sendPacket.writeStringNoNull(extraData);
/*      */             else {
/* 1928 */               this.sendPacket.writeStringNoNull(extraData, extraDataCharEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */             }
/*      */ 
/*      */           }
/* 1933 */           else if (command == 12) {
/* 1934 */             id = Long.parseLong(extraData);
/* 1935 */             this.sendPacket.writeLong(id);
/*      */           }
/*      */ 
/* 1938 */           send(this.sendPacket, this.sendPacket.getPosition());
/*      */         } else {
/* 1940 */           this.packetSequence = -1;
/* 1941 */           send(queryPacket, queryPacket.getPosition());
/*      */         }
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 1945 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/* 1947 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex, getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1951 */       Buffer returnPacket = null;
/*      */ 
/* 1953 */       if (!skipCheck) {
/* 1954 */         if ((command == 23) || (command == 26))
/*      */         {
/* 1956 */           this.readPacketSequence = 0;
/* 1957 */           this.packetSequenceReset = true;
/*      */         }
/*      */ 
/* 1960 */         returnPacket = checkErrorPacket(command);
/*      */       }
/*      */ 
/* 1963 */       id = returnPacket;
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*      */       long id;
/* 1965 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     finally {
/* 1968 */       if (timeoutMillis != 0)
/*      */         try {
/* 1970 */           this.mysqlConnection.setSoTimeout(oldTimeout);
/*      */         } catch (SocketException e) {
/* 1972 */           throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean shouldIntercept()
/*      */   {
/* 1983 */     return this.statementInterceptors != null;
/*      */   }
/*      */ 
/*      */   final ResultSetInternalMethods sqlQueryDirect(StatementImpl callingStatement, String query, String characterEncoding, Buffer queryPacket, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata)
/*      */     throws Exception
/*      */   {
/* 2010 */     this.statementExecutionDepth += 1;
/*      */     try
/*      */     {
/* 2013 */       if (this.statementInterceptors != null) {
/* 2014 */         ResultSetInternalMethods interceptedResults = invokeStatementInterceptorsPre(query, callingStatement, false);
/*      */ 
/* 2017 */         if (interceptedResults != null) {
/* 2018 */           ResultSetInternalMethods localResultSetInternalMethods1 = interceptedResults;
/*      */           return localResultSetInternalMethods1;
/*      */         }
/*      */       }
/* 2022 */       long queryStartTime = 0L;
/* 2023 */       long queryEndTime = 0L;
/*      */ 
/* 2025 */       if (query != null)
/*      */       {
/* 2030 */         int packLength = 5 + query.length() * 2 + 2;
/*      */ 
/* 2032 */         String statementComment = this.connection.getStatementComment();
/*      */ 
/* 2034 */         byte[] commentAsBytes = null;
/*      */ 
/* 2036 */         if (statementComment != null) {
/* 2037 */           commentAsBytes = StringUtils.getBytes(statementComment, null, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */ 
/* 2042 */           packLength += commentAsBytes.length;
/* 2043 */           packLength += 6;
/*      */         }
/*      */ 
/* 2046 */         if (this.sendPacket == null)
/* 2047 */           this.sendPacket = new Buffer(packLength);
/*      */         else {
/* 2049 */           this.sendPacket.clear();
/*      */         }
/*      */ 
/* 2052 */         this.sendPacket.writeByte(3);
/*      */ 
/* 2054 */         if (commentAsBytes != null) {
/* 2055 */           this.sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 2056 */           this.sendPacket.writeBytesNoNull(commentAsBytes);
/* 2057 */           this.sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */         }
/*      */ 
/* 2060 */         if (characterEncoding != null) {
/* 2061 */           if (this.platformDbCharsetMatches) {
/* 2062 */             this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */           }
/* 2067 */           else if (StringUtils.startsWithIgnoreCaseAndWs(query, "LOAD DATA"))
/* 2068 */             this.sendPacket.writeBytesNoNull(query.getBytes());
/*      */           else {
/* 2070 */             this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 2078 */           this.sendPacket.writeStringNoNull(query);
/*      */         }
/*      */ 
/* 2081 */         queryPacket = this.sendPacket;
/*      */       }
/*      */ 
/* 2084 */       byte[] queryBuf = null;
/* 2085 */       int oldPacketPosition = 0;
/*      */ 
/* 2087 */       if (this.needToGrabQueryFromPacket) {
/* 2088 */         queryBuf = queryPacket.getByteBuffer();
/*      */ 
/* 2091 */         oldPacketPosition = queryPacket.getPosition();
/*      */ 
/* 2093 */         queryStartTime = getCurrentTimeNanosOrMillis();
/*      */       }
/*      */ 
/* 2096 */       if (this.autoGenerateTestcaseScript) {
/* 2097 */         String testcaseQuery = null;
/*      */ 
/* 2099 */         if (query != null)
/* 2100 */           testcaseQuery = query;
/*      */         else {
/* 2102 */           testcaseQuery = new String(queryBuf, 5, oldPacketPosition - 5);
/*      */         }
/*      */ 
/* 2106 */         StringBuffer debugBuf = new StringBuffer(testcaseQuery.length() + 32);
/* 2107 */         this.connection.generateConnectionCommentBlock(debugBuf);
/* 2108 */         debugBuf.append(testcaseQuery);
/* 2109 */         debugBuf.append(';');
/* 2110 */         this.connection.dumpTestcaseQuery(debugBuf.toString());
/*      */       }
/*      */ 
/* 2114 */       Buffer resultPacket = sendCommand(3, null, queryPacket, false, null, 0);
/*      */ 
/* 2117 */       long fetchBeginTime = 0L;
/* 2118 */       long fetchEndTime = 0L;
/*      */ 
/* 2120 */       String profileQueryToLog = null;
/*      */ 
/* 2122 */       boolean queryWasSlow = false;
/*      */ 
/* 2124 */       if ((this.profileSql) || (this.logSlowQueries)) {
/* 2125 */         queryEndTime = System.currentTimeMillis();
/*      */ 
/* 2127 */         boolean shouldExtractQuery = false;
/*      */ 
/* 2129 */         if (this.profileSql) {
/* 2130 */           shouldExtractQuery = true;
/* 2131 */         } else if (this.logSlowQueries) {
/* 2132 */           long queryTime = queryEndTime - queryStartTime;
/*      */ 
/* 2134 */           boolean logSlow = false;
/*      */ 
/* 2136 */           if (this.useAutoSlowLog) {
/* 2137 */             logSlow = queryTime > this.connection.getSlowQueryThresholdMillis();
/*      */           } else {
/* 2139 */             logSlow = this.connection.isAbonormallyLongQuery(queryTime);
/*      */ 
/* 2141 */             this.connection.reportQueryTime(queryTime);
/*      */           }
/*      */ 
/* 2144 */           if (logSlow) {
/* 2145 */             shouldExtractQuery = true;
/* 2146 */             queryWasSlow = true;
/*      */           }
/*      */         }
/*      */ 
/* 2150 */         if (shouldExtractQuery)
/*      */         {
/* 2152 */           boolean truncated = false;
/*      */ 
/* 2154 */           int extractPosition = oldPacketPosition;
/*      */ 
/* 2156 */           if (oldPacketPosition > this.connection.getMaxQuerySizeToLog()) {
/* 2157 */             extractPosition = this.connection.getMaxQuerySizeToLog() + 5;
/* 2158 */             truncated = true;
/*      */           }
/*      */ 
/* 2161 */           profileQueryToLog = new String(queryBuf, 5, extractPosition - 5);
/*      */ 
/* 2164 */           if (truncated) {
/* 2165 */             profileQueryToLog = profileQueryToLog + Messages.getString("MysqlIO.25");
/*      */           }
/*      */         }
/*      */ 
/* 2169 */         fetchBeginTime = queryEndTime;
/*      */       }
/*      */ 
/* 2172 */       ResultSetInternalMethods rs = readAllResults(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, false, -1L, cachedMetadata);
/*      */ 
/* 2176 */       if ((queryWasSlow) && (!this.serverQueryWasSlow)) {
/* 2177 */         StringBuffer mesgBuf = new StringBuffer(48 + profileQueryToLog.length());
/*      */ 
/* 2180 */         mesgBuf.append(Messages.getString("MysqlIO.SlowQuery", new Object[] { new Long(this.slowQueryThreshold), this.queryTimingUnits, new Long(queryEndTime - queryStartTime) }));
/*      */ 
/* 2184 */         mesgBuf.append(profileQueryToLog);
/*      */ 
/* 2186 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 2188 */         eventSink.consumeEvent(new ProfilerEvent(6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), (int)(queryEndTime - queryStartTime), this.queryTimingUnits, null, new Throwable(), mesgBuf.toString()));
/*      */ 
/* 2195 */         if (this.connection.getExplainSlowQueries()) {
/* 2196 */           if (oldPacketPosition < 1048576) {
/* 2197 */             explainSlowQuery(queryPacket.getBytes(5, oldPacketPosition - 5), profileQueryToLog);
/*      */           }
/*      */           else {
/* 2200 */             this.connection.getLog().logWarn(Messages.getString("MysqlIO.28") + 1048576 + Messages.getString("MysqlIO.29"));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2208 */       if (this.logSlowQueries)
/*      */       {
/* 2210 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 2212 */         if ((this.queryBadIndexUsed) && (this.profileSql)) {
/* 2213 */           eventSink.consumeEvent(new ProfilerEvent(6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, new Throwable(), Messages.getString("MysqlIO.33") + profileQueryToLog));
/*      */         }
/*      */ 
/* 2226 */         if ((this.queryNoIndexUsed) && (this.profileSql)) {
/* 2227 */           eventSink.consumeEvent(new ProfilerEvent(6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, new Throwable(), Messages.getString("MysqlIO.35") + profileQueryToLog));
/*      */         }
/*      */ 
/* 2240 */         if ((this.serverQueryWasSlow) && (this.profileSql)) {
/* 2241 */           eventSink.consumeEvent(new ProfilerEvent(6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, new Throwable(), Messages.getString("MysqlIO.ServerSlowQuery") + profileQueryToLog));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2255 */       if (this.profileSql) {
/* 2256 */         fetchEndTime = getCurrentTimeNanosOrMillis();
/*      */ 
/* 2258 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 2260 */         eventSink.consumeEvent(new ProfilerEvent(3, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, new Throwable(), profileQueryToLog));
/*      */ 
/* 2268 */         eventSink.consumeEvent(new ProfilerEvent(5, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), fetchEndTime - fetchBeginTime, this.queryTimingUnits, null, new Throwable(), null));
/*      */       }
/*      */ 
/* 2277 */       if (this.hadWarnings) {
/* 2278 */         scanForAndThrowDataTruncation();
/*      */       }
/*      */ 
/* 2281 */       if (this.statementInterceptors != null) {
/* 2282 */         interceptedResults = invokeStatementInterceptorsPost(query, callingStatement, rs, false, null);
/*      */ 
/* 2285 */         if (interceptedResults != null) {
/* 2286 */           rs = interceptedResults;
/*      */         }
/*      */       }
/*      */ 
/* 2290 */       ResultSetInternalMethods interceptedResults = rs;
/*      */       return interceptedResults;
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 2292 */       if (this.statementInterceptors != null) {
/* 2293 */         invokeStatementInterceptorsPost(query, callingStatement, null, false, sqlEx);
/*      */       }
/*      */ 
/* 2297 */       if (callingStatement != null) {
/* 2298 */         synchronized (callingStatement.cancelTimeoutMutex) {
/* 2299 */           if (callingStatement.wasCancelled) {
/* 2300 */             SQLException cause = null;
/*      */ 
/* 2302 */             if (callingStatement.wasCancelledByTimeout)
/* 2303 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 2305 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 2308 */             callingStatement.resetCancelledState();
/*      */ 
/* 2310 */             throw cause;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2315 */       throw sqlEx;
/*      */     } finally {
/* 2317 */       this.statementExecutionDepth -= 1; } throw localObject2;
/*      */   }
/*      */ 
/*      */   ResultSetInternalMethods invokeStatementInterceptorsPre(String sql, Statement interceptedStatement, boolean forceExecute)
/*      */     throws SQLException
/*      */   {
/* 2323 */     ResultSetInternalMethods previousResultSet = null;
/*      */ 
/* 2325 */     Iterator interceptors = this.statementInterceptors.iterator();
/*      */ 
/* 2327 */     while (interceptors.hasNext()) {
/* 2328 */       StatementInterceptorV2 interceptor = (StatementInterceptorV2)interceptors.next();
/*      */ 
/* 2331 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 2332 */       boolean shouldExecute = ((executeTopLevelOnly) && ((this.statementExecutionDepth == 1) || (forceExecute))) || (!executeTopLevelOnly);
/*      */ 
/* 2335 */       if (shouldExecute) {
/* 2336 */         String sqlToInterceptor = sql;
/*      */ 
/* 2343 */         ResultSetInternalMethods interceptedResultSet = interceptor.preProcess(sqlToInterceptor, interceptedStatement, this.connection);
/*      */ 
/* 2347 */         if (interceptedResultSet != null) {
/* 2348 */           previousResultSet = interceptedResultSet;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2353 */     return previousResultSet;
/*      */   }
/*      */ 
/*      */   ResultSetInternalMethods invokeStatementInterceptorsPost(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, boolean forceExecute, SQLException statementException)
/*      */     throws SQLException
/*      */   {
/* 2359 */     Iterator interceptors = this.statementInterceptors.iterator();
/*      */ 
/* 2361 */     while (interceptors.hasNext()) {
/* 2362 */       StatementInterceptorV2 interceptor = (StatementInterceptorV2)interceptors.next();
/*      */ 
/* 2365 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 2366 */       boolean shouldExecute = ((executeTopLevelOnly) && ((this.statementExecutionDepth == 1) || (forceExecute))) || (!executeTopLevelOnly);
/*      */ 
/* 2369 */       if (shouldExecute) {
/* 2370 */         String sqlToInterceptor = sql;
/*      */ 
/* 2372 */         ResultSetInternalMethods interceptedResultSet = interceptor.postProcess(sqlToInterceptor, interceptedStatement, originalResultSet, this.connection, this.warningCount, this.queryNoIndexUsed, this.queryBadIndexUsed, statementException);
/*      */ 
/* 2377 */         if (interceptedResultSet != null) {
/* 2378 */           originalResultSet = interceptedResultSet;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2383 */     return originalResultSet;
/*      */   }
/*      */ 
/*      */   private void calculateSlowQueryThreshold() {
/* 2387 */     this.slowQueryThreshold = this.connection.getSlowQueryThresholdMillis();
/*      */ 
/* 2389 */     if (this.connection.getUseNanosForElapsedTime()) {
/* 2390 */       long nanosThreshold = this.connection.getSlowQueryThresholdNanos();
/*      */ 
/* 2392 */       if (nanosThreshold != 0L)
/* 2393 */         this.slowQueryThreshold = nanosThreshold;
/*      */       else
/* 2395 */         this.slowQueryThreshold *= 1000000L;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected long getCurrentTimeNanosOrMillis()
/*      */   {
/* 2401 */     if (this.useNanosForElapsedTime) {
/* 2402 */       return Util.getCurrentTimeNanosOrMillis();
/*      */     }
/*      */ 
/* 2405 */     return System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */   String getHost()
/*      */   {
/* 2414 */     return this.host;
/*      */   }
/*      */ 
/*      */   boolean isVersion(int major, int minor, int subminor)
/*      */   {
/* 2429 */     return (major == getServerMajorVersion()) && (minor == getServerMinorVersion()) && (subminor == getServerSubMinorVersion());
/*      */   }
/*      */ 
/*      */   boolean versionMeetsMinimum(int major, int minor, int subminor)
/*      */   {
/* 2445 */     if (getServerMajorVersion() >= major) {
/* 2446 */       if (getServerMajorVersion() == major) {
/* 2447 */         if (getServerMinorVersion() >= minor) {
/* 2448 */           if (getServerMinorVersion() == minor) {
/* 2449 */             return getServerSubMinorVersion() >= subminor;
/*      */           }
/*      */ 
/* 2453 */           return true;
/*      */         }
/*      */ 
/* 2457 */         return false;
/*      */       }
/*      */ 
/* 2461 */       return true;
/*      */     }
/*      */ 
/* 2464 */     return false;
/*      */   }
/*      */ 
/*      */   private static final String getPacketDumpToLog(Buffer packetToDump, int packetLength)
/*      */   {
/* 2478 */     if (packetLength < 1024) {
/* 2479 */       return packetToDump.dump(packetLength);
/*      */     }
/*      */ 
/* 2482 */     StringBuffer packetDumpBuf = new StringBuffer(4096);
/* 2483 */     packetDumpBuf.append(packetToDump.dump(1024));
/* 2484 */     packetDumpBuf.append(Messages.getString("MysqlIO.36"));
/* 2485 */     packetDumpBuf.append(1024);
/* 2486 */     packetDumpBuf.append(Messages.getString("MysqlIO.37"));
/*      */ 
/* 2488 */     return packetDumpBuf.toString();
/*      */   }
/*      */ 
/*      */   private final int readFully(InputStream in, byte[] b, int off, int len) throws IOException
/*      */   {
/* 2493 */     if (len < 0) {
/* 2494 */       throw new IndexOutOfBoundsException();
/*      */     }
/*      */ 
/* 2497 */     int n = 0;
/*      */ 
/* 2499 */     while (n < len) {
/* 2500 */       int count = in.read(b, off + n, len - n);
/*      */ 
/* 2502 */       if (count < 0) {
/* 2503 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { new Integer(len), new Integer(n) }));
/*      */       }
/*      */ 
/* 2507 */       n += count;
/*      */     }
/*      */ 
/* 2510 */     return n;
/*      */   }
/*      */ 
/*      */   private final long skipFully(InputStream in, long len) throws IOException {
/* 2514 */     if (len < 0L) {
/* 2515 */       throw new IOException("Negative skip length not allowed");
/*      */     }
/*      */ 
/* 2518 */     long n = 0L;
/*      */ 
/* 2520 */     while (n < len) {
/* 2521 */       long count = in.skip(len - n);
/*      */ 
/* 2523 */       if (count < 0L) {
/* 2524 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { new Long(len), new Long(n) }));
/*      */       }
/*      */ 
/* 2528 */       n += count;
/*      */     }
/*      */ 
/* 2531 */     return n;
/*      */   }
/*      */ 
/*      */   protected final ResultSetImpl readResultsForQueryOrUpdate(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/* 2559 */     long columnCount = resultPacket.readFieldLength();
/*      */ 
/* 2561 */     if (columnCount == 0L)
/* 2562 */       return buildResultSetWithUpdates(callingStatement, resultPacket);
/* 2563 */     if (columnCount == -1L) {
/* 2564 */       String charEncoding = null;
/*      */ 
/* 2566 */       if (this.connection.getUseUnicode()) {
/* 2567 */         charEncoding = this.connection.getEncoding();
/*      */       }
/*      */ 
/* 2570 */       String fileName = null;
/*      */ 
/* 2572 */       if (this.platformDbCharsetMatches) {
/* 2573 */         fileName = charEncoding != null ? resultPacket.readString(charEncoding, getExceptionInterceptor()) : resultPacket.readString();
/*      */       }
/*      */       else
/*      */       {
/* 2577 */         fileName = resultPacket.readString();
/*      */       }
/*      */ 
/* 2580 */       return sendFileToServer(callingStatement, fileName);
/*      */     }
/* 2582 */     ResultSetImpl results = getResultSet(callingStatement, columnCount, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, isBinaryEncoded, metadataFromCache);
/*      */ 
/* 2587 */     return results;
/*      */   }
/*      */ 
/*      */   private int alignPacketSize(int a, int l)
/*      */   {
/* 2592 */     return a + l - 1 & (l - 1 ^ 0xFFFFFFFF);
/*      */   }
/*      */ 
/*      */   private ResultSetImpl buildResultSetWithRows(StatementImpl callingStatement, String catalog, Field[] fields, RowData rows, int resultSetType, int resultSetConcurrency, boolean isBinaryEncoded)
/*      */     throws SQLException
/*      */   {
/* 2600 */     ResultSetImpl rs = null;
/*      */ 
/* 2602 */     switch (resultSetConcurrency) {
/*      */     case 1007:
/* 2604 */       rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
/*      */ 
/* 2607 */       if (!isBinaryEncoded) break;
/* 2608 */       rs.setBinaryEncoded(); break;
/*      */     case 1008:
/* 2614 */       rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, true);
/*      */ 
/* 2617 */       break;
/*      */     default:
/* 2620 */       return ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
/*      */     }
/*      */ 
/* 2624 */     rs.setResultSetType(resultSetType);
/* 2625 */     rs.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 2627 */     return rs;
/*      */   }
/*      */ 
/*      */   private ResultSetImpl buildResultSetWithUpdates(StatementImpl callingStatement, Buffer resultPacket)
/*      */     throws SQLException
/*      */   {
/* 2633 */     long updateCount = -1L;
/* 2634 */     long updateID = -1L;
/* 2635 */     String info = null;
/*      */     try
/*      */     {
/* 2638 */       if (this.useNewUpdateCounts) {
/* 2639 */         updateCount = resultPacket.newReadLength();
/* 2640 */         updateID = resultPacket.newReadLength();
/*      */       } else {
/* 2642 */         updateCount = resultPacket.readLength();
/* 2643 */         updateID = resultPacket.readLength();
/*      */       }
/*      */ 
/* 2646 */       if (this.use41Extensions)
/*      */       {
/* 2648 */         this.serverStatus = resultPacket.readInt();
/*      */ 
/* 2650 */         checkTransactionState(this.oldServerStatus);
/*      */ 
/* 2652 */         this.warningCount = resultPacket.readInt();
/*      */ 
/* 2654 */         if (this.warningCount > 0) {
/* 2655 */           this.hadWarnings = true;
/*      */         }
/*      */ 
/* 2658 */         resultPacket.readByte();
/*      */ 
/* 2660 */         setServerSlowQueryFlags();
/*      */       }
/*      */ 
/* 2663 */       if (this.connection.isReadInfoMsgEnabled())
/* 2664 */         info = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */     }
/*      */     catch (Exception ex) {
/* 2667 */       SQLException sqlEx = SQLError.createSQLException(SQLError.get("S1000"), "S1000", -1, getExceptionInterceptor());
/*      */ 
/* 2669 */       sqlEx.initCause(ex);
/*      */ 
/* 2671 */       throw sqlEx;
/*      */     }
/*      */ 
/* 2674 */     ResultSetInternalMethods updateRs = ResultSetImpl.getInstance(updateCount, updateID, this.connection, callingStatement);
/*      */ 
/* 2677 */     if (info != null) {
/* 2678 */       ((ResultSetImpl)updateRs).setServerInfo(info);
/*      */     }
/*      */ 
/* 2681 */     return (ResultSetImpl)updateRs;
/*      */   }
/*      */ 
/*      */   private void setServerSlowQueryFlags() {
/* 2685 */     this.queryBadIndexUsed = ((this.serverStatus & 0x10) != 0);
/*      */ 
/* 2687 */     this.queryNoIndexUsed = ((this.serverStatus & 0x20) != 0);
/*      */ 
/* 2689 */     this.serverQueryWasSlow = ((this.serverStatus & 0x800) != 0);
/*      */   }
/*      */ 
/*      */   private void checkForOutstandingStreamingData() throws SQLException
/*      */   {
/* 2694 */     if (this.streamingData != null) {
/* 2695 */       boolean shouldClobber = this.connection.getClobberStreamingResults();
/*      */ 
/* 2697 */       if (!shouldClobber) {
/* 2698 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.39") + this.streamingData + Messages.getString("MysqlIO.40") + Messages.getString("MysqlIO.41") + Messages.getString("MysqlIO.42"), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2706 */       this.streamingData.getOwner().realClose(false);
/*      */ 
/* 2709 */       clearInputStream();
/*      */     }
/*      */   }
/*      */ 
/*      */   private Buffer compressPacket(Buffer packet, int offset, int packetLen, int headerLength) throws SQLException
/*      */   {
/* 2715 */     packet.writeLongInt(packetLen - headerLength);
/* 2716 */     packet.writeByte(0);
/*      */ 
/* 2718 */     int lengthToWrite = 0;
/* 2719 */     int compressedLength = 0;
/* 2720 */     byte[] bytesToCompress = packet.getByteBuffer();
/* 2721 */     byte[] compressedBytes = null;
/* 2722 */     int offsetWrite = 0;
/*      */ 
/* 2724 */     if (packetLen < 50) {
/* 2725 */       lengthToWrite = packetLen;
/* 2726 */       compressedBytes = packet.getByteBuffer();
/* 2727 */       compressedLength = 0;
/* 2728 */       offsetWrite = offset;
/*      */     } else {
/* 2730 */       compressedBytes = new byte[bytesToCompress.length * 2];
/*      */ 
/* 2732 */       this.deflater.reset();
/* 2733 */       this.deflater.setInput(bytesToCompress, offset, packetLen);
/* 2734 */       this.deflater.finish();
/*      */ 
/* 2736 */       int compLen = this.deflater.deflate(compressedBytes);
/*      */ 
/* 2738 */       if (compLen > packetLen) {
/* 2739 */         lengthToWrite = packetLen;
/* 2740 */         compressedBytes = packet.getByteBuffer();
/* 2741 */         compressedLength = 0;
/* 2742 */         offsetWrite = offset;
/*      */       } else {
/* 2744 */         lengthToWrite = compLen;
/* 2745 */         headerLength += 3;
/* 2746 */         compressedLength = packetLen;
/*      */       }
/*      */     }
/*      */ 
/* 2750 */     Buffer compressedPacket = new Buffer(packetLen + headerLength);
/*      */ 
/* 2752 */     compressedPacket.setPosition(0);
/* 2753 */     compressedPacket.writeLongInt(lengthToWrite);
/* 2754 */     compressedPacket.writeByte(this.packetSequence);
/* 2755 */     compressedPacket.writeLongInt(compressedLength);
/* 2756 */     compressedPacket.writeBytesNoNull(compressedBytes, offsetWrite, lengthToWrite);
/*      */ 
/* 2759 */     return compressedPacket;
/*      */   }
/*      */ 
/*      */   private final void readServerStatusForResultSets(Buffer rowPacket) throws SQLException
/*      */   {
/* 2764 */     if (this.use41Extensions) {
/* 2765 */       rowPacket.readByte();
/*      */ 
/* 2767 */       this.warningCount = rowPacket.readInt();
/*      */ 
/* 2769 */       if (this.warningCount > 0) {
/* 2770 */         this.hadWarnings = true;
/*      */       }
/*      */ 
/* 2773 */       this.oldServerStatus = this.serverStatus;
/* 2774 */       this.serverStatus = rowPacket.readInt();
/* 2775 */       checkTransactionState(this.oldServerStatus);
/*      */ 
/* 2777 */       setServerSlowQueryFlags();
/*      */     }
/*      */   }
/*      */   private SocketFactory createSocketFactory() throws SQLException {
/*      */     SQLException sqlEx;
/*      */     try {
/* 2783 */       if (this.socketFactoryClassName == null) {
/* 2784 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.75"), "08001", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2788 */       return (SocketFactory)Class.forName(this.socketFactoryClassName).newInstance();
/*      */     }
/*      */     catch (Exception ex) {
/* 2791 */       sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.76") + this.socketFactoryClassName + Messages.getString("MysqlIO.77"), "08001", getExceptionInterceptor());
/*      */ 
/* 2796 */       sqlEx.initCause(ex);
/*      */     }
/* 2798 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private void enqueuePacketForDebugging(boolean isPacketBeingSent, boolean isPacketReused, int sendLength, byte[] header, Buffer packet)
/*      */     throws SQLException
/*      */   {
/* 2805 */     if (this.packetDebugRingBuffer.size() + 1 > this.connection.getPacketDebugBufferSize()) {
/* 2806 */       this.packetDebugRingBuffer.removeFirst();
/*      */     }
/*      */ 
/* 2809 */     StringBuffer packetDump = null;
/*      */ 
/* 2811 */     if (!isPacketBeingSent) {
/* 2812 */       int bytesToDump = Math.min(1024, packet.getBufLength());
/*      */ 
/* 2815 */       Buffer packetToDump = new Buffer(4 + bytesToDump);
/*      */ 
/* 2817 */       packetToDump.setPosition(0);
/* 2818 */       packetToDump.writeBytesNoNull(header);
/* 2819 */       packetToDump.writeBytesNoNull(packet.getBytes(0, bytesToDump));
/*      */ 
/* 2821 */       String packetPayload = packetToDump.dump(bytesToDump);
/*      */ 
/* 2823 */       packetDump = new StringBuffer(96 + packetPayload.length());
/*      */ 
/* 2825 */       packetDump.append("Server ");
/*      */ 
/* 2827 */       if (isPacketReused)
/* 2828 */         packetDump.append("(re-used)");
/*      */       else {
/* 2830 */         packetDump.append("(new)");
/*      */       }
/*      */ 
/* 2833 */       packetDump.append(" ");
/* 2834 */       packetDump.append(packet.toSuperString());
/* 2835 */       packetDump.append(" --------------------> Client\n");
/* 2836 */       packetDump.append("\nPacket payload:\n\n");
/* 2837 */       packetDump.append(packetPayload);
/*      */ 
/* 2839 */       if (bytesToDump == 1024) {
/* 2840 */         packetDump.append("\nNote: Packet of " + packet.getBufLength() + " bytes truncated to " + 1024 + " bytes.\n");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 2845 */       int bytesToDump = Math.min(1024, sendLength);
/*      */ 
/* 2847 */       String packetPayload = packet.dump(bytesToDump);
/*      */ 
/* 2849 */       packetDump = new StringBuffer(68 + packetPayload.length());
/*      */ 
/* 2851 */       packetDump.append("Client ");
/* 2852 */       packetDump.append(packet.toSuperString());
/* 2853 */       packetDump.append("--------------------> Server\n");
/* 2854 */       packetDump.append("\nPacket payload:\n\n");
/* 2855 */       packetDump.append(packetPayload);
/*      */ 
/* 2857 */       if (bytesToDump == 1024) {
/* 2858 */         packetDump.append("\nNote: Packet of " + sendLength + " bytes truncated to " + 1024 + " bytes.\n");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2864 */     this.packetDebugRingBuffer.addLast(packetDump);
/*      */   }
/*      */ 
/*      */   private RowData readSingleRowSet(long columnCount, int maxRows, int resultSetConcurrency, boolean isBinaryEncoded, Field[] fields)
/*      */     throws SQLException
/*      */   {
/* 2871 */     ArrayList rows = new ArrayList();
/*      */ 
/* 2873 */     boolean useBufferRowExplicit = useBufferRowExplicit(fields);
/*      */ 
/* 2876 */     ResultSetRow row = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, null);
/*      */ 
/* 2879 */     int rowCount = 0;
/*      */ 
/* 2881 */     if (row != null) {
/* 2882 */       rows.add(row);
/* 2883 */       rowCount = 1;
/*      */     }
/*      */ 
/* 2886 */     while (row != null) {
/* 2887 */       row = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, null);
/*      */ 
/* 2890 */       if ((row == null) || (
/* 2891 */         (maxRows != -1) && (rowCount >= maxRows))) continue;
/* 2892 */       rows.add(row);
/* 2893 */       rowCount++;
/*      */     }
/*      */ 
/* 2898 */     RowData rowData = new RowDataStatic(rows);
/*      */ 
/* 2900 */     return rowData;
/*      */   }
/*      */ 
/*      */   public static boolean useBufferRowExplicit(Field[] fields) {
/* 2904 */     if (fields == null) {
/* 2905 */       return false;
/*      */     }
/*      */ 
/* 2908 */     for (int i = 0; i < fields.length; i++) {
/* 2909 */       switch (fields[i].getSQLType()) {
/*      */       case -4:
/*      */       case -1:
/*      */       case 2004:
/*      */       case 2005:
/* 2914 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 2918 */     return false;
/*      */   }
/*      */ 
/*      */   private void reclaimLargeReusablePacket()
/*      */   {
/* 2925 */     if ((this.reusablePacket != null) && (this.reusablePacket.getCapacity() > 1048576))
/*      */     {
/* 2927 */       this.reusablePacket = new Buffer(1024);
/*      */     }
/*      */   }
/*      */ 
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse)
/*      */     throws SQLException
/*      */   {
/* 2942 */     return reuseAndReadPacket(reuse, -1); } 
/*      */   // ERROR //
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse, int existingPacketLength) throws SQLException { // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: iconst_0
/*      */     //   2: invokevirtual 479	com/mysql/jdbc/Buffer:setWasMultiPacket	(Z)V
/*      */     //   5: iconst_0
/*      */     //   6: istore_3
/*      */     //   7: iload_2
/*      */     //   8: iconst_m1
/*      */     //   9: if_icmpne +85 -> 94
/*      */     //   12: aload_0
/*      */     //   13: aload_0
/*      */     //   14: getfield 8	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   17: aload_0
/*      */     //   18: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   21: iconst_0
/*      */     //   22: iconst_4
/*      */     //   23: invokespecial 125	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   26: istore 4
/*      */     //   28: iload 4
/*      */     //   30: iconst_4
/*      */     //   31: if_icmpge +21 -> 52
/*      */     //   34: aload_0
/*      */     //   35: invokevirtual 126	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   38: new 100	java/io/IOException
/*      */     //   41: dup
/*      */     //   42: ldc_w 298
/*      */     //   45: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   48: invokespecial 128	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   51: athrow
/*      */     //   52: aload_0
/*      */     //   53: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   56: iconst_0
/*      */     //   57: baload
/*      */     //   58: sipush 255
/*      */     //   61: iand
/*      */     //   62: aload_0
/*      */     //   63: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   66: iconst_1
/*      */     //   67: baload
/*      */     //   68: sipush 255
/*      */     //   71: iand
/*      */     //   72: bipush 8
/*      */     //   74: ishl
/*      */     //   75: iadd
/*      */     //   76: aload_0
/*      */     //   77: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   80: iconst_2
/*      */     //   81: baload
/*      */     //   82: sipush 255
/*      */     //   85: iand
/*      */     //   86: bipush 16
/*      */     //   88: ishl
/*      */     //   89: iadd
/*      */     //   90: istore_3
/*      */     //   91: goto +5 -> 96
/*      */     //   94: iload_2
/*      */     //   95: istore_3
/*      */     //   96: aload_0
/*      */     //   97: getfield 50	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   100: ifeq +74 -> 174
/*      */     //   103: new 129	java/lang/StringBuffer
/*      */     //   106: dup
/*      */     //   107: invokespecial 130	java/lang/StringBuffer:<init>	()V
/*      */     //   110: astore 4
/*      */     //   112: aload 4
/*      */     //   114: ldc_w 480
/*      */     //   117: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   120: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   123: pop
/*      */     //   124: aload 4
/*      */     //   126: iload_3
/*      */     //   127: invokevirtual 133	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   130: pop
/*      */     //   131: aload 4
/*      */     //   133: ldc_w 481
/*      */     //   136: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   139: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   142: pop
/*      */     //   143: aload 4
/*      */     //   145: aload_0
/*      */     //   146: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   149: iconst_4
/*      */     //   150: invokestatic 135	com/mysql/jdbc/StringUtils:dumpAsHex	([BI)Ljava/lang/String;
/*      */     //   153: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   156: pop
/*      */     //   157: aload_0
/*      */     //   158: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   161: invokevirtual 77	com/mysql/jdbc/ConnectionImpl:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   164: aload 4
/*      */     //   166: invokevirtual 136	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   169: invokeinterface 137 2 0
/*      */     //   174: aload_0
/*      */     //   175: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   178: iconst_3
/*      */     //   179: baload
/*      */     //   180: istore 4
/*      */     //   182: aload_0
/*      */     //   183: getfield 2	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   186: ifne +26 -> 212
/*      */     //   189: aload_0
/*      */     //   190: getfield 51	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   193: ifeq +24 -> 217
/*      */     //   196: aload_0
/*      */     //   197: getfield 34	com/mysql/jdbc/MysqlIO:checkPacketSequence	Z
/*      */     //   200: ifeq +17 -> 217
/*      */     //   203: aload_0
/*      */     //   204: iload 4
/*      */     //   206: invokespecial 138	com/mysql/jdbc/MysqlIO:checkPacketSequencing	(B)V
/*      */     //   209: goto +8 -> 217
/*      */     //   212: aload_0
/*      */     //   213: iconst_0
/*      */     //   214: putfield 2	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   217: aload_0
/*      */     //   218: iload 4
/*      */     //   220: putfield 33	com/mysql/jdbc/MysqlIO:readPacketSequence	B
/*      */     //   223: aload_1
/*      */     //   224: iconst_0
/*      */     //   225: invokevirtual 233	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   228: aload_1
/*      */     //   229: invokevirtual 160	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   232: arraylength
/*      */     //   233: iload_3
/*      */     //   234: if_icmpgt +12 -> 246
/*      */     //   237: aload_1
/*      */     //   238: iload_3
/*      */     //   239: iconst_1
/*      */     //   240: iadd
/*      */     //   241: newarray byte
/*      */     //   243: invokevirtual 482	com/mysql/jdbc/Buffer:setByteBuffer	([B)V
/*      */     //   246: aload_1
/*      */     //   247: iload_3
/*      */     //   248: invokevirtual 147	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   251: aload_0
/*      */     //   252: aload_0
/*      */     //   253: getfield 8	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   256: aload_1
/*      */     //   257: invokevirtual 160	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   260: iconst_0
/*      */     //   261: iload_3
/*      */     //   262: invokespecial 125	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   265: istore 5
/*      */     //   267: iload 5
/*      */     //   269: iload_3
/*      */     //   270: if_icmpeq +40 -> 310
/*      */     //   273: new 100	java/io/IOException
/*      */     //   276: dup
/*      */     //   277: new 129	java/lang/StringBuffer
/*      */     //   280: dup
/*      */     //   281: invokespecial 130	java/lang/StringBuffer:<init>	()V
/*      */     //   284: ldc 144
/*      */     //   286: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   289: iload_3
/*      */     //   290: invokevirtual 133	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   293: ldc 145
/*      */     //   295: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   298: iload 5
/*      */     //   300: invokevirtual 133	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   303: invokevirtual 136	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   306: invokespecial 128	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   309: athrow
/*      */     //   310: aload_0
/*      */     //   311: getfield 50	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   314: ifeq +52 -> 366
/*      */     //   317: new 129	java/lang/StringBuffer
/*      */     //   320: dup
/*      */     //   321: invokespecial 130	java/lang/StringBuffer:<init>	()V
/*      */     //   324: astore 6
/*      */     //   326: aload 6
/*      */     //   328: ldc_w 483
/*      */     //   331: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   334: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   337: pop
/*      */     //   338: aload 6
/*      */     //   340: aload_1
/*      */     //   341: iload_3
/*      */     //   342: invokestatic 149	com/mysql/jdbc/MysqlIO:getPacketDumpToLog	(Lcom/mysql/jdbc/Buffer;I)Ljava/lang/String;
/*      */     //   345: invokevirtual 132	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   348: pop
/*      */     //   349: aload_0
/*      */     //   350: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   353: invokevirtual 77	com/mysql/jdbc/ConnectionImpl:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   356: aload 6
/*      */     //   358: invokevirtual 136	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   361: invokeinterface 137 2 0
/*      */     //   366: aload_0
/*      */     //   367: getfield 51	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   370: ifeq +15 -> 385
/*      */     //   373: aload_0
/*      */     //   374: iconst_0
/*      */     //   375: iconst_1
/*      */     //   376: iconst_0
/*      */     //   377: aload_0
/*      */     //   378: getfield 16	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   381: aload_1
/*      */     //   382: invokespecial 150	com/mysql/jdbc/MysqlIO:enqueuePacketForDebugging	(ZZI[BLcom/mysql/jdbc/Buffer;)V
/*      */     //   385: iconst_0
/*      */     //   386: istore 6
/*      */     //   388: iload_3
/*      */     //   389: aload_0
/*      */     //   390: getfield 39	com/mysql/jdbc/MysqlIO:maxThreeBytes	I
/*      */     //   393: if_icmpne +27 -> 420
/*      */     //   396: aload_1
/*      */     //   397: aload_0
/*      */     //   398: getfield 39	com/mysql/jdbc/MysqlIO:maxThreeBytes	I
/*      */     //   401: invokevirtual 233	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   404: iload_3
/*      */     //   405: istore 7
/*      */     //   407: iconst_1
/*      */     //   408: istore 6
/*      */     //   410: aload_0
/*      */     //   411: aload_1
/*      */     //   412: iload 4
/*      */     //   414: iload 7
/*      */     //   416: invokespecial 484	com/mysql/jdbc/MysqlIO:readRemainingMultiPackets	(Lcom/mysql/jdbc/Buffer;BI)I
/*      */     //   419: istore_3
/*      */     //   420: iload 6
/*      */     //   422: ifne +10 -> 432
/*      */     //   425: aload_1
/*      */     //   426: invokevirtual 160	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   429: iload_3
/*      */     //   430: iconst_0
/*      */     //   431: bastore
/*      */     //   432: aload_0
/*      */     //   433: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   436: invokevirtual 151	com/mysql/jdbc/ConnectionImpl:getMaintainTimeStats	()Z
/*      */     //   439: ifeq +10 -> 449
/*      */     //   442: aload_0
/*      */     //   443: invokestatic 152	java/lang/System:currentTimeMillis	()J
/*      */     //   446: putfield 49	com/mysql/jdbc/MysqlIO:lastPacketReceivedTimeMs	J
/*      */     //   449: aload_1
/*      */     //   450: areturn
/*      */     //   451: astore_3
/*      */     //   452: aload_0
/*      */     //   453: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   456: aload_0
/*      */     //   457: getfield 48	com/mysql/jdbc/MysqlIO:lastPacketSentTimeMs	J
/*      */     //   460: aload_0
/*      */     //   461: getfield 49	com/mysql/jdbc/MysqlIO:lastPacketReceivedTimeMs	J
/*      */     //   464: aload_3
/*      */     //   465: aload_0
/*      */     //   466: invokevirtual 101	com/mysql/jdbc/MysqlIO:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
/*      */     //   469: invokestatic 102	com/mysql/jdbc/SQLError:createCommunicationsException	(Lcom/mysql/jdbc/ConnectionImpl;JJLjava/lang/Exception;Lcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
/*      */     //   472: athrow
/*      */     //   473: astore_3
/*      */     //   474: aload_0
/*      */     //   475: invokevirtual 329	com/mysql/jdbc/MysqlIO:clearInputStream	()V
/*      */     //   478: jsr +14 -> 492
/*      */     //   481: goto +30 -> 511
/*      */     //   484: astore 8
/*      */     //   486: jsr +6 -> 492
/*      */     //   489: aload 8
/*      */     //   491: athrow
/*      */     //   492: astore 9
/*      */     //   494: aload_0
/*      */     //   495: getfield 55	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/ConnectionImpl;
/*      */     //   498: iconst_0
/*      */     //   499: iconst_0
/*      */     //   500: iconst_1
/*      */     //   501: aload_3
/*      */     //   502: invokevirtual 141	com/mysql/jdbc/ConnectionImpl:realClose	(ZZZLjava/lang/Throwable;)V
/*      */     //   505: aload_3
/*      */     //   506: athrow
/*      */     //   507: astore 10
/*      */     //   509: aload_3
/*      */     //   510: athrow
/*      */     //   511: goto +0 -> 511
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	450	451	java/io/IOException
/*      */     //   0	450	473	java/lang/OutOfMemoryError
/*      */     //   474	481	484	finally
/*      */     //   484	489	484	finally
/*      */     //   494	505	507	finally
/*      */     //   507	509	507	finally } 
/* 3077 */   private int readRemainingMultiPackets(Buffer reuse, byte multiPacketSeq, int packetEndPoint) throws IOException, SQLException { int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/* 3080 */     if (lengthRead < 4) {
/* 3081 */       forceClose();
/* 3082 */       throw new IOException(Messages.getString("MysqlIO.47"));
/*      */     }
/*      */ 
/* 3085 */     int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/* 3089 */     Buffer multiPacket = new Buffer(packetLength);
/* 3090 */     boolean firstMultiPkt = true;
/*      */     while (true)
/*      */     {
/* 3093 */       if (!firstMultiPkt) {
/* 3094 */         lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/* 3097 */         if (lengthRead < 4) {
/* 3098 */           forceClose();
/* 3099 */           throw new IOException(Messages.getString("MysqlIO.48"));
/*      */         }
/*      */ 
/* 3103 */         packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */       }
/*      */       else
/*      */       {
/* 3107 */         firstMultiPkt = false;
/*      */       }
/*      */ 
/* 3110 */       if ((!this.useNewLargePackets) && (packetLength == 1)) {
/* 3111 */         clearInputStream();
/*      */ 
/* 3113 */         break;
/* 3114 */       }if (packetLength < this.maxThreeBytes) {
/* 3115 */         byte newPacketSeq = this.packetHeaderBuf[3];
/*      */ 
/* 3117 */         if (newPacketSeq != multiPacketSeq + 1) {
/* 3118 */           throw new IOException(Messages.getString("MysqlIO.49"));
/*      */         }
/*      */ 
/* 3122 */         multiPacketSeq = newPacketSeq;
/*      */ 
/* 3125 */         multiPacket.setPosition(0);
/*      */ 
/* 3128 */         multiPacket.setBufLength(packetLength);
/*      */ 
/* 3131 */         byte[] byteBuf = multiPacket.getByteBuffer();
/* 3132 */         int lengthToWrite = packetLength;
/*      */ 
/* 3134 */         int bytesRead = readFully(this.mysqlInput, byteBuf, 0, packetLength);
/*      */ 
/* 3137 */         if (bytesRead != lengthToWrite) {
/* 3138 */           throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, SQLError.createSQLException(Messages.getString("MysqlIO.50") + lengthToWrite + Messages.getString("MysqlIO.51") + bytesRead + ".", getExceptionInterceptor()), getExceptionInterceptor());
/*      */         }
/*      */ 
/* 3148 */         reuse.writeBytesNoNull(byteBuf, 0, lengthToWrite);
/*      */ 
/* 3150 */         packetEndPoint += lengthToWrite;
/*      */ 
/* 3152 */         break;
/*      */       }
/*      */ 
/* 3155 */       byte newPacketSeq = this.packetHeaderBuf[3];
/*      */ 
/* 3157 */       if (newPacketSeq != multiPacketSeq + 1) {
/* 3158 */         throw new IOException(Messages.getString("MysqlIO.53"));
/*      */       }
/*      */ 
/* 3162 */       multiPacketSeq = newPacketSeq;
/*      */ 
/* 3165 */       multiPacket.setPosition(0);
/*      */ 
/* 3168 */       multiPacket.setBufLength(packetLength);
/*      */ 
/* 3171 */       byte[] byteBuf = multiPacket.getByteBuffer();
/* 3172 */       int lengthToWrite = packetLength;
/*      */ 
/* 3174 */       int bytesRead = readFully(this.mysqlInput, byteBuf, 0, packetLength);
/*      */ 
/* 3177 */       if (bytesRead != lengthToWrite) {
/* 3178 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, SQLError.createSQLException(Messages.getString("MysqlIO.54") + lengthToWrite + Messages.getString("MysqlIO.55") + bytesRead + ".", getExceptionInterceptor()), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3187 */       reuse.writeBytesNoNull(byteBuf, 0, lengthToWrite);
/*      */ 
/* 3189 */       packetEndPoint += lengthToWrite;
/*      */     }
/*      */ 
/* 3192 */     reuse.setPosition(0);
/* 3193 */     reuse.setWasMultiPacket(true);
/* 3194 */     return packetLength;
/*      */   }
/*      */ 
/*      */   private void checkPacketSequencing(byte multiPacketSeq)
/*      */     throws SQLException
/*      */   {
/* 3203 */     if ((multiPacketSeq == -128) && (this.readPacketSequence != 127)) {
/* 3204 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -128, but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3210 */     if ((this.readPacketSequence == -1) && (multiPacketSeq != 0)) {
/* 3211 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -1, but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3217 */     if ((multiPacketSeq != -128) && (this.readPacketSequence != -1) && (multiPacketSeq != this.readPacketSequence + 1))
/*      */     {
/* 3219 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # " + (this.readPacketSequence + 1) + ", but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   void enableMultiQueries()
/*      */     throws SQLException
/*      */   {
/* 3228 */     Buffer buf = getSharedSendPacket();
/*      */ 
/* 3230 */     buf.clear();
/* 3231 */     buf.writeByte(27);
/* 3232 */     buf.writeInt(0);
/* 3233 */     sendCommand(27, null, buf, false, null, 0);
/*      */   }
/*      */ 
/*      */   void disableMultiQueries() throws SQLException {
/* 3237 */     Buffer buf = getSharedSendPacket();
/*      */ 
/* 3239 */     buf.clear();
/* 3240 */     buf.writeByte(27);
/* 3241 */     buf.writeInt(1);
/* 3242 */     sendCommand(27, null, buf, false, null, 0);
/*      */   }
/*      */ 
/*      */   private final void send(Buffer packet, int packetLen) throws SQLException
/*      */   {
/*      */     try {
/* 3248 */       if ((this.maxAllowedPacket > 0) && (packetLen > this.maxAllowedPacket)) {
/* 3249 */         throw new PacketTooBigException(packetLen, this.maxAllowedPacket);
/*      */       }
/*      */ 
/* 3252 */       if ((this.serverMajorVersion >= 4) && (packetLen >= this.maxThreeBytes))
/*      */       {
/* 3254 */         sendSplitPackets(packet);
/*      */       } else {
/* 3256 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 3258 */         Buffer packetToSend = packet;
/*      */ 
/* 3260 */         packetToSend.setPosition(0);
/*      */ 
/* 3262 */         if (this.useCompression) {
/* 3263 */           int originalPacketLen = packetLen;
/*      */ 
/* 3265 */           packetToSend = compressPacket(packet, 0, packetLen, 4);
/*      */ 
/* 3267 */           packetLen = packetToSend.getPosition();
/*      */ 
/* 3269 */           if (this.traceProtocol) {
/* 3270 */             StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/* 3272 */             traceMessageBuf.append(Messages.getString("MysqlIO.57"));
/* 3273 */             traceMessageBuf.append(getPacketDumpToLog(packetToSend, packetLen));
/*      */ 
/* 3275 */             traceMessageBuf.append(Messages.getString("MysqlIO.58"));
/* 3276 */             traceMessageBuf.append(getPacketDumpToLog(packet, originalPacketLen));
/*      */ 
/* 3279 */             this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */           }
/*      */         } else {
/* 3282 */           packetToSend.writeLongInt(packetLen - 4);
/* 3283 */           packetToSend.writeByte(this.packetSequence);
/*      */ 
/* 3285 */           if (this.traceProtocol) {
/* 3286 */             StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/* 3288 */             traceMessageBuf.append(Messages.getString("MysqlIO.59"));
/* 3289 */             traceMessageBuf.append(packetToSend.dump(packetLen));
/*      */ 
/* 3291 */             this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3296 */         this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/*      */ 
/* 3298 */         this.mysqlOutput.flush();
/*      */       }
/*      */ 
/* 3301 */       if (this.enablePacketDebug) {
/* 3302 */         enqueuePacketForDebugging(true, false, packetLen + 5, this.packetHeaderBuf, packet);
/*      */       }
/*      */ 
/* 3309 */       if (packet == this.sharedSendPacket) {
/* 3310 */         reclaimLargeSharedSendPacket();
/*      */       }
/*      */ 
/* 3313 */       if (this.connection.getMaintainTimeStats())
/* 3314 */         this.lastPacketSentTimeMs = System.currentTimeMillis();
/*      */     }
/*      */     catch (IOException ioEx) {
/* 3317 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private final ResultSetImpl sendFileToServer(StatementImpl callingStatement, String fileName)
/*      */     throws SQLException
/*      */   {
/* 3335 */     Buffer filePacket = this.loadFileBufRef == null ? null : (Buffer)this.loadFileBufRef.get();
/*      */ 
/* 3338 */     int bigPacketLength = Math.min(this.connection.getMaxAllowedPacket() - 12, alignPacketSize(this.connection.getMaxAllowedPacket() - 16, 4096) - 12);
/*      */ 
/* 3343 */     int oneMeg = 1048576;
/*      */ 
/* 3345 */     int smallerPacketSizeAligned = Math.min(oneMeg - 12, alignPacketSize(oneMeg - 16, 4096) - 12);
/*      */ 
/* 3348 */     int packetLength = Math.min(smallerPacketSizeAligned, bigPacketLength);
/*      */ 
/* 3350 */     if (filePacket == null) {
/*      */       try {
/* 3352 */         filePacket = new Buffer(packetLength + 4);
/* 3353 */         this.loadFileBufRef = new SoftReference(filePacket);
/*      */       } catch (OutOfMemoryError oom) {
/* 3355 */         throw SQLError.createSQLException("Could not allocate packet of " + packetLength + " bytes required for LOAD DATA LOCAL INFILE operation." + " Try increasing max heap allocation for JVM or decreasing server variable " + "'max_allowed_packet'", "S1001", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3363 */     filePacket.clear();
/* 3364 */     send(filePacket, 0);
/*      */ 
/* 3366 */     byte[] fileBuf = new byte[packetLength];
/*      */ 
/* 3368 */     BufferedInputStream fileIn = null;
/*      */     try
/*      */     {
/* 3371 */       if (!this.connection.getAllowLoadLocalInfile()) {
/* 3372 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.LoadDataLocalNotAllowed"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3377 */       InputStream hookedStream = null;
/*      */ 
/* 3379 */       if (callingStatement != null) {
/* 3380 */         hookedStream = callingStatement.getLocalInfileInputStream();
/*      */       }
/*      */ 
/* 3383 */       if (hookedStream != null)
/* 3384 */         fileIn = new BufferedInputStream(hookedStream);
/* 3385 */       else if (!this.connection.getAllowUrlInLocalInfile()) {
/* 3386 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       }
/* 3389 */       else if (fileName.indexOf(':') != -1) {
/*      */         try {
/* 3391 */           URL urlFromFileName = new URL(fileName);
/* 3392 */           fileIn = new BufferedInputStream(urlFromFileName.openStream());
/*      */         }
/*      */         catch (MalformedURLException badUrlEx) {
/* 3395 */           fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */         }
/*      */       }
/*      */       else {
/* 3399 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       }
/*      */ 
/* 3404 */       int bytesRead = 0;
/*      */ 
/* 3406 */       while ((bytesRead = fileIn.read(fileBuf)) != -1) {
/* 3407 */         filePacket.clear();
/* 3408 */         filePacket.writeBytesNoNull(fileBuf, 0, bytesRead);
/* 3409 */         send(filePacket, filePacket.getPosition());
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 3412 */       StringBuffer messageBuf = new StringBuffer(Messages.getString("MysqlIO.60"));
/*      */ 
/* 3415 */       if (!this.connection.getParanoid()) {
/* 3416 */         messageBuf.append("'");
/*      */ 
/* 3418 */         if (fileName != null) {
/* 3419 */           messageBuf.append(fileName);
/*      */         }
/*      */ 
/* 3422 */         messageBuf.append("'");
/*      */       }
/*      */ 
/* 3425 */       messageBuf.append(Messages.getString("MysqlIO.63"));
/*      */ 
/* 3427 */       if (!this.connection.getParanoid()) {
/* 3428 */         messageBuf.append(Messages.getString("MysqlIO.64"));
/* 3429 */         messageBuf.append(Util.stackTraceToString(ioEx));
/*      */       }
/*      */ 
/* 3432 */       throw SQLError.createSQLException(messageBuf.toString(), "S1009", getExceptionInterceptor());
/*      */     }
/*      */     finally {
/* 3435 */       if (fileIn != null) {
/*      */         try {
/* 3437 */           fileIn.close();
/*      */         } catch (Exception ex) {
/* 3439 */           SQLException sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.65"), "S1000", getExceptionInterceptor());
/*      */ 
/* 3441 */           sqlEx.initCause(ex);
/*      */ 
/* 3443 */           throw sqlEx;
/*      */         }
/*      */ 
/* 3446 */         fileIn = null;
/*      */       }
/*      */       else {
/* 3449 */         filePacket.clear();
/* 3450 */         send(filePacket, filePacket.getPosition());
/* 3451 */         checkErrorPacket();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3456 */     filePacket.clear();
/* 3457 */     send(filePacket, filePacket.getPosition());
/*      */ 
/* 3459 */     Buffer resultPacket = checkErrorPacket();
/*      */ 
/* 3461 */     return buildResultSetWithUpdates(callingStatement, resultPacket);
/*      */   }
/*      */ 
/*      */   private Buffer checkErrorPacket(int command)
/*      */     throws SQLException
/*      */   {
/* 3476 */     int statusCode = 0;
/* 3477 */     Buffer resultPacket = null;
/* 3478 */     this.serverStatus = 0;
/*      */     try
/*      */     {
/* 3485 */       resultPacket = reuseAndReadPacket(this.reusablePacket);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 3488 */       throw sqlEx;
/*      */     } catch (Exception fallThru) {
/* 3490 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, fallThru, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3494 */     checkErrorPacket(resultPacket);
/*      */ 
/* 3496 */     return resultPacket;
/*      */   }
/*      */ 
/*      */   private void checkErrorPacket(Buffer resultPacket) throws SQLException
/*      */   {
/* 3501 */     int statusCode = resultPacket.readByte();
/*      */ 
/* 3504 */     if (statusCode == -1)
/*      */     {
/* 3506 */       int errno = 2000;
/*      */ 
/* 3508 */       if (this.protocolVersion > 9) {
/* 3509 */         errno = resultPacket.readInt();
/*      */ 
/* 3511 */         String xOpen = null;
/*      */ 
/* 3513 */         String serverErrorMessage = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */ 
/* 3516 */         if (serverErrorMessage.charAt(0) == '#')
/*      */         {
/* 3519 */           if (serverErrorMessage.length() > 6) {
/* 3520 */             xOpen = serverErrorMessage.substring(1, 6);
/* 3521 */             serverErrorMessage = serverErrorMessage.substring(6);
/*      */ 
/* 3523 */             if (xOpen.equals("HY000"))
/* 3524 */               xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */           }
/*      */           else
/*      */           {
/* 3528 */             xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */           }
/*      */         }
/*      */         else {
/* 3532 */           xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */         }
/*      */ 
/* 3536 */         clearInputStream();
/*      */ 
/* 3538 */         StringBuffer errorBuf = new StringBuffer();
/*      */ 
/* 3540 */         String xOpenErrorMessage = SQLError.get(xOpen);
/*      */ 
/* 3542 */         if ((!this.connection.getUseOnlyServerErrorMessages()) && 
/* 3543 */           (xOpenErrorMessage != null)) {
/* 3544 */           errorBuf.append(xOpenErrorMessage);
/* 3545 */           errorBuf.append(Messages.getString("MysqlIO.68"));
/*      */         }
/*      */ 
/* 3549 */         errorBuf.append(serverErrorMessage);
/*      */ 
/* 3551 */         if ((!this.connection.getUseOnlyServerErrorMessages()) && 
/* 3552 */           (xOpenErrorMessage != null)) {
/* 3553 */           errorBuf.append("\"");
/*      */         }
/*      */ 
/* 3557 */         appendInnodbStatusInformation(xOpen, errorBuf);
/*      */ 
/* 3559 */         if ((xOpen != null) && (xOpen.startsWith("22"))) {
/* 3560 */           throw new MysqlDataTruncation(errorBuf.toString(), 0, true, false, 0, 0, errno);
/*      */         }
/* 3562 */         throw SQLError.createSQLException(errorBuf.toString(), xOpen, errno, false, getExceptionInterceptor(), this.connection);
/*      */       }
/*      */ 
/* 3566 */       String serverErrorMessage = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */ 
/* 3568 */       clearInputStream();
/*      */ 
/* 3570 */       if (serverErrorMessage.indexOf(Messages.getString("MysqlIO.70")) != -1) {
/* 3571 */         throw SQLError.createSQLException(SQLError.get("S0022") + ", " + serverErrorMessage, "S0022", -1, false, getExceptionInterceptor(), this.connection);
/*      */       }
/*      */ 
/* 3578 */       StringBuffer errorBuf = new StringBuffer(Messages.getString("MysqlIO.72"));
/*      */ 
/* 3580 */       errorBuf.append(serverErrorMessage);
/* 3581 */       errorBuf.append("\"");
/*      */ 
/* 3583 */       throw SQLError.createSQLException(SQLError.get("S1000") + ", " + errorBuf.toString(), "S1000", -1, false, getExceptionInterceptor(), this.connection);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void appendInnodbStatusInformation(String xOpen, StringBuffer errorBuf)
/*      */     throws SQLException
/*      */   {
/* 3591 */     if ((this.connection.getIncludeInnodbStatusInDeadlockExceptions()) && (xOpen != null) && ((xOpen.startsWith("40")) || (xOpen.startsWith("41"))) && (this.streamingData == null))
/*      */     {
/* 3595 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 3598 */         rs = sqlQueryDirect(null, "SHOW ENGINE INNODB STATUS", this.connection.getEncoding(), null, -1, 1003, 1007, false, this.connection.getCatalog(), null);
/*      */ 
/* 3604 */         if (rs.next()) {
/* 3605 */           errorBuf.append("\n\n");
/* 3606 */           errorBuf.append(rs.getString("Status"));
/*      */         } else {
/* 3608 */           errorBuf.append("\n\n");
/* 3609 */           errorBuf.append(Messages.getString("MysqlIO.NoInnoDBStatusFound"));
/*      */         }
/*      */       }
/*      */       catch (Exception ex) {
/* 3613 */         errorBuf.append("\n\n");
/* 3614 */         errorBuf.append(Messages.getString("MysqlIO.InnoDBStatusFailed"));
/*      */ 
/* 3616 */         errorBuf.append("\n\n");
/* 3617 */         errorBuf.append(Util.stackTraceToString(ex));
/*      */       } finally {
/* 3619 */         if (rs != null)
/* 3620 */           rs.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void sendSplitPackets(Buffer packet)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3646 */       Buffer headerPacket = this.splitBufRef == null ? null : (Buffer)this.splitBufRef.get();
/*      */ 
/* 3654 */       if (headerPacket == null) {
/* 3655 */         headerPacket = new Buffer(this.maxThreeBytes + 4);
/*      */ 
/* 3657 */         this.splitBufRef = new SoftReference(headerPacket);
/*      */       }
/*      */ 
/* 3660 */       int len = packet.getPosition();
/* 3661 */       int splitSize = this.maxThreeBytes;
/* 3662 */       int originalPacketPos = 4;
/* 3663 */       byte[] origPacketBytes = packet.getByteBuffer();
/* 3664 */       byte[] headerPacketBytes = headerPacket.getByteBuffer();
/*      */ 
/* 3666 */       while (len >= this.maxThreeBytes) {
/* 3667 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 3669 */         headerPacket.setPosition(0);
/* 3670 */         headerPacket.writeLongInt(splitSize);
/*      */ 
/* 3672 */         headerPacket.writeByte(this.packetSequence);
/* 3673 */         System.arraycopy(origPacketBytes, originalPacketPos, headerPacketBytes, 4, splitSize);
/*      */ 
/* 3676 */         int packetLen = splitSize + 4;
/*      */ 
/* 3682 */         if (!this.useCompression) {
/* 3683 */           this.mysqlOutput.write(headerPacketBytes, 0, splitSize + 4);
/*      */ 
/* 3685 */           this.mysqlOutput.flush();
/*      */         }
/*      */         else
/*      */         {
/* 3689 */           headerPacket.setPosition(0);
/* 3690 */           Buffer packetToSend = compressPacket(headerPacket, 4, splitSize, 4);
/*      */ 
/* 3692 */           packetLen = packetToSend.getPosition();
/*      */ 
/* 3694 */           this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/*      */ 
/* 3696 */           this.mysqlOutput.flush();
/*      */         }
/*      */ 
/* 3699 */         originalPacketPos += splitSize;
/* 3700 */         len -= splitSize;
/*      */       }
/*      */ 
/* 3706 */       headerPacket.clear();
/* 3707 */       headerPacket.setPosition(0);
/* 3708 */       headerPacket.writeLongInt(len - 4);
/* 3709 */       this.packetSequence = (byte)(this.packetSequence + 1);
/* 3710 */       headerPacket.writeByte(this.packetSequence);
/*      */ 
/* 3712 */       if (len != 0) {
/* 3713 */         System.arraycopy(origPacketBytes, originalPacketPos, headerPacketBytes, 4, len - 4);
/*      */       }
/*      */ 
/* 3717 */       int packetLen = len - 4;
/*      */ 
/* 3723 */       if (!this.useCompression) {
/* 3724 */         this.mysqlOutput.write(headerPacket.getByteBuffer(), 0, len);
/* 3725 */         this.mysqlOutput.flush();
/*      */       }
/*      */       else
/*      */       {
/* 3729 */         headerPacket.setPosition(0);
/* 3730 */         Buffer packetToSend = compressPacket(headerPacket, 4, packetLen, 4);
/*      */ 
/* 3732 */         packetLen = packetToSend.getPosition();
/*      */ 
/* 3734 */         this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/*      */ 
/* 3736 */         this.mysqlOutput.flush();
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 3739 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void reclaimLargeSharedSendPacket()
/*      */   {
/* 3745 */     if ((this.sharedSendPacket != null) && (this.sharedSendPacket.getCapacity() > 1048576))
/*      */     {
/* 3747 */       this.sharedSendPacket = new Buffer(1024);
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean hadWarnings() {
/* 3752 */     return this.hadWarnings;
/*      */   }
/*      */ 
/*      */   void scanForAndThrowDataTruncation() throws SQLException {
/* 3756 */     if ((this.streamingData == null) && (versionMeetsMinimum(4, 1, 0)) && (this.connection.getJdbcCompliantTruncation()) && (this.warningCount > 0))
/*      */     {
/* 3758 */       SQLError.convertShowWarningsToSQLWarnings(this.connection, this.warningCount, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void secureAuth(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams)
/*      */     throws SQLException
/*      */   {
/* 3779 */     if (packet == null) {
/* 3780 */       packet = new Buffer(packLength);
/*      */     }
/*      */ 
/* 3783 */     if (writeClientParams) {
/* 3784 */       if (this.use41Extensions) {
/* 3785 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 3786 */           packet.writeLong(this.clientParam);
/* 3787 */           packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 3792 */           packet.writeByte(8);
/*      */ 
/* 3795 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 3797 */           packet.writeLong(this.clientParam);
/* 3798 */           packet.writeLong(this.maxThreeBytes);
/*      */         }
/*      */       } else {
/* 3801 */         packet.writeInt((int)this.clientParam);
/* 3802 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3807 */     packet.writeString(user, "Cp1252", this.connection);
/*      */ 
/* 3809 */     if (password.length() != 0)
/*      */     {
/* 3811 */       packet.writeString("xxxxxxxx", "Cp1252", this.connection);
/*      */     }
/*      */     else {
/* 3814 */       packet.writeString("", "Cp1252", this.connection);
/*      */     }
/*      */ 
/* 3817 */     if (this.useConnectWithDb) {
/* 3818 */       packet.writeString(database, "Cp1252", this.connection);
/*      */     }
/*      */ 
/* 3821 */     send(packet, packet.getPosition());
/*      */ 
/* 3826 */     if (password.length() > 0) {
/* 3827 */       Buffer b = readPacket();
/*      */ 
/* 3829 */       b.setPosition(0);
/*      */ 
/* 3831 */       byte[] replyAsBytes = b.getByteBuffer();
/*      */ 
/* 3833 */       if ((replyAsBytes.length == 25) && (replyAsBytes[0] != 0))
/*      */       {
/* 3835 */         if (replyAsBytes[0] != 42) {
/*      */           try
/*      */           {
/* 3838 */             byte[] buff = Security.passwordHashStage1(password);
/*      */ 
/* 3841 */             byte[] passwordHash = new byte[buff.length];
/* 3842 */             System.arraycopy(buff, 0, passwordHash, 0, buff.length);
/*      */ 
/* 3845 */             passwordHash = Security.passwordHashStage2(passwordHash, replyAsBytes);
/*      */ 
/* 3848 */             byte[] packetDataAfterSalt = new byte[replyAsBytes.length - 5];
/*      */ 
/* 3851 */             System.arraycopy(replyAsBytes, 4, packetDataAfterSalt, 0, replyAsBytes.length - 5);
/*      */ 
/* 3854 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */ 
/* 3857 */             Security.passwordCrypt(packetDataAfterSalt, mysqlScrambleBuff, passwordHash, 20);
/*      */ 
/* 3861 */             Security.passwordCrypt(mysqlScrambleBuff, buff, buff, 20);
/*      */ 
/* 3863 */             Buffer packet2 = new Buffer(25);
/* 3864 */             packet2.writeBytesNoNull(buff);
/*      */ 
/* 3866 */             this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 3868 */             send(packet2, 24);
/*      */           } catch (NoSuchAlgorithmException nse) {
/* 3870 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*      */           try
/*      */           {
/* 3877 */             byte[] passwordHash = Security.createKeyFromOldPassword(password);
/*      */ 
/* 3880 */             byte[] netReadPos4 = new byte[replyAsBytes.length - 5];
/*      */ 
/* 3882 */             System.arraycopy(replyAsBytes, 4, netReadPos4, 0, replyAsBytes.length - 5);
/*      */ 
/* 3885 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */ 
/* 3888 */             Security.passwordCrypt(netReadPos4, mysqlScrambleBuff, passwordHash, 20);
/*      */ 
/* 3892 */             String scrambledPassword = Util.scramble(new String(mysqlScrambleBuff), password);
/*      */ 
/* 3895 */             Buffer packet2 = new Buffer(packLength);
/* 3896 */             packet2.writeString(scrambledPassword, "Cp1252", this.connection);
/* 3897 */             this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 3899 */             send(packet2, 24);
/*      */           } catch (NoSuchAlgorithmException nse) {
/* 3901 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.93") + Messages.getString("MysqlIO.94"), "S1000", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void secureAuth411(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams)
/*      */     throws SQLException
/*      */   {
/* 3943 */     if (packet == null) {
/* 3944 */       packet = new Buffer(packLength);
/*      */     }
/*      */ 
/* 3947 */     if (writeClientParams) {
/* 3948 */       if (this.use41Extensions) {
/* 3949 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 3950 */           packet.writeLong(this.clientParam);
/* 3951 */           packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 3956 */           packet.writeByte(33);
/*      */ 
/* 3959 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 3961 */           packet.writeLong(this.clientParam);
/* 3962 */           packet.writeLong(this.maxThreeBytes);
/*      */         }
/*      */       } else {
/* 3965 */         packet.writeInt((int)this.clientParam);
/* 3966 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3971 */     packet.writeString(user, "utf-8", this.connection);
/*      */ 
/* 3973 */     if (password.length() != 0) {
/* 3974 */       packet.writeByte(20);
/*      */       try
/*      */       {
/* 3977 */         packet.writeBytesNoNull(Security.scramble411(password, this.seed, this.connection));
/*      */       } catch (NoSuchAlgorithmException nse) {
/* 3979 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.95") + Messages.getString("MysqlIO.96"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/* 3983 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.95") + Messages.getString("MysqlIO.96"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 3989 */       packet.writeByte(0);
/*      */     }
/*      */ 
/* 3992 */     if (this.useConnectWithDb) {
/* 3993 */       packet.writeString(database, "utf-8", this.connection);
/*      */     }
/*      */ 
/* 3996 */     send(packet, packet.getPosition());
/*      */ 
/* 3998 */     byte savePacketSequence = this.packetSequence++;
/*      */ 
/* 4000 */     Buffer reply = checkErrorPacket();
/*      */ 
/* 4002 */     if (reply.isLastDataPacket())
/*      */     {
/* 4007 */       savePacketSequence = (byte)(savePacketSequence + 1); this.packetSequence = savePacketSequence;
/* 4008 */       packet.clear();
/*      */ 
/* 4010 */       String seed323 = this.seed.substring(0, 8);
/* 4011 */       packet.writeString(Util.newCrypt(password, seed323));
/* 4012 */       send(packet, packet.getPosition());
/*      */ 
/* 4015 */       checkErrorPacket();
/*      */     }
/*      */   }
/*      */ 
/*      */   private final ResultSetRow unpackBinaryResultSetRow(Field[] fields, Buffer binaryData, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4032 */     int numFields = fields.length;
/*      */ 
/* 4034 */     byte[][] unpackedRowData = new byte[numFields][];
/*      */ 
/* 4041 */     int nullCount = (numFields + 9) / 8;
/*      */ 
/* 4043 */     byte[] nullBitMask = new byte[nullCount];
/*      */ 
/* 4045 */     for (int i = 0; i < nullCount; i++) {
/* 4046 */       nullBitMask[i] = binaryData.readByte();
/*      */     }
/*      */ 
/* 4049 */     int nullMaskPos = 0;
/* 4050 */     int bit = 4;
/*      */ 
/* 4057 */     for (int i = 0; i < numFields; i++) {
/* 4058 */       if ((nullBitMask[nullMaskPos] & bit) != 0) {
/* 4059 */         unpackedRowData[i] = null;
/*      */       }
/* 4061 */       else if (resultSetConcurrency != 1008) {
/* 4062 */         extractNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
/*      */       }
/*      */       else {
/* 4065 */         unpackNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
/*      */       }
/*      */ 
/* 4070 */       if ((bit <<= 1 & 0xFF) == 0) {
/* 4071 */         bit = 1;
/*      */ 
/* 4073 */         nullMaskPos++;
/*      */       }
/*      */     }
/*      */ 
/* 4077 */     return new ByteArrayRow(unpackedRowData, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private final void extractNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData)
/*      */     throws SQLException
/*      */   {
/* 4083 */     Field curField = fields[columnIndex];
/*      */ 
/* 4085 */     switch (curField.getMysqlType()) {
/*      */     case 6:
/* 4087 */       break;
/*      */     case 1:
/* 4091 */       unpackedRowData[columnIndex] = { binaryData.readByte() };
/* 4092 */       break;
/*      */     case 2:
/*      */     case 13:
/* 4097 */       unpackedRowData[columnIndex] = binaryData.getBytes(2);
/* 4098 */       break;
/*      */     case 3:
/*      */     case 9:
/* 4102 */       unpackedRowData[columnIndex] = binaryData.getBytes(4);
/* 4103 */       break;
/*      */     case 8:
/* 4106 */       unpackedRowData[columnIndex] = binaryData.getBytes(8);
/* 4107 */       break;
/*      */     case 4:
/* 4110 */       unpackedRowData[columnIndex] = binaryData.getBytes(4);
/* 4111 */       break;
/*      */     case 5:
/* 4114 */       unpackedRowData[columnIndex] = binaryData.getBytes(8);
/* 4115 */       break;
/*      */     case 11:
/* 4118 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 4120 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */ 
/* 4122 */       break;
/*      */     case 10:
/* 4125 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 4127 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */ 
/* 4129 */       break;
/*      */     case 7:
/*      */     case 12:
/* 4132 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 4134 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/* 4135 */       break;
/*      */     case 0:
/*      */     case 15:
/*      */     case 246:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/*      */     case 253:
/*      */     case 254:
/*      */     case 255:
/* 4146 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 4148 */       break;
/*      */     case 16:
/* 4150 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 4152 */       break;
/*      */     default:
/* 4154 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void unpackNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData)
/*      */     throws SQLException
/*      */   {
/* 4166 */     Field curField = fields[columnIndex];
/*      */ 
/* 4168 */     switch (curField.getMysqlType()) {
/*      */     case 6:
/* 4170 */       break;
/*      */     case 1:
/* 4174 */       byte tinyVal = binaryData.readByte();
/*      */ 
/* 4176 */       if (!curField.isUnsigned()) {
/* 4177 */         unpackedRowData[columnIndex] = String.valueOf(tinyVal).getBytes();
/*      */       }
/*      */       else {
/* 4180 */         short unsignedTinyVal = (short)(tinyVal & 0xFF);
/*      */ 
/* 4182 */         unpackedRowData[columnIndex] = String.valueOf(unsignedTinyVal).getBytes();
/*      */       }
/*      */ 
/* 4186 */       break;
/*      */     case 2:
/*      */     case 13:
/* 4191 */       short shortVal = (short)binaryData.readInt();
/*      */ 
/* 4193 */       if (!curField.isUnsigned()) {
/* 4194 */         unpackedRowData[columnIndex] = String.valueOf(shortVal).getBytes();
/*      */       }
/*      */       else {
/* 4197 */         int unsignedShortVal = shortVal & 0xFFFF;
/*      */ 
/* 4199 */         unpackedRowData[columnIndex] = String.valueOf(unsignedShortVal).getBytes();
/*      */       }
/*      */ 
/* 4203 */       break;
/*      */     case 3:
/*      */     case 9:
/* 4208 */       int intVal = (int)binaryData.readLong();
/*      */ 
/* 4210 */       if (!curField.isUnsigned()) {
/* 4211 */         unpackedRowData[columnIndex] = String.valueOf(intVal).getBytes();
/*      */       }
/*      */       else {
/* 4214 */         long longVal = intVal & 0xFFFFFFFF;
/*      */ 
/* 4216 */         unpackedRowData[columnIndex] = String.valueOf(longVal).getBytes();
/*      */       }
/*      */ 
/* 4220 */       break;
/*      */     case 8:
/* 4224 */       long longVal = binaryData.readLongLong();
/*      */ 
/* 4226 */       if (!curField.isUnsigned()) {
/* 4227 */         unpackedRowData[columnIndex] = String.valueOf(longVal).getBytes();
/*      */       }
/*      */       else {
/* 4230 */         BigInteger asBigInteger = ResultSetImpl.convertLongToUlong(longVal);
/*      */ 
/* 4232 */         unpackedRowData[columnIndex] = asBigInteger.toString().getBytes();
/*      */       }
/*      */ 
/* 4236 */       break;
/*      */     case 4:
/* 4240 */       float floatVal = Float.intBitsToFloat(binaryData.readIntAsLong());
/*      */ 
/* 4242 */       unpackedRowData[columnIndex] = String.valueOf(floatVal).getBytes();
/*      */ 
/* 4244 */       break;
/*      */     case 5:
/* 4248 */       double doubleVal = Double.longBitsToDouble(binaryData.readLongLong());
/*      */ 
/* 4250 */       unpackedRowData[columnIndex] = String.valueOf(doubleVal).getBytes();
/*      */ 
/* 4252 */       break;
/*      */     case 11:
/* 4256 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 4258 */       int hour = 0;
/* 4259 */       int minute = 0;
/* 4260 */       int seconds = 0;
/*      */ 
/* 4262 */       if (length != 0) {
/* 4263 */         binaryData.readByte();
/* 4264 */         binaryData.readLong();
/* 4265 */         hour = binaryData.readByte();
/* 4266 */         minute = binaryData.readByte();
/* 4267 */         seconds = binaryData.readByte();
/*      */ 
/* 4269 */         if (length > 8) {
/* 4270 */           binaryData.readLong();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4275 */       byte[] timeAsBytes = new byte[8];
/*      */ 
/* 4277 */       timeAsBytes[0] = (byte)Character.forDigit(hour / 10, 10);
/* 4278 */       timeAsBytes[1] = (byte)Character.forDigit(hour % 10, 10);
/*      */ 
/* 4280 */       timeAsBytes[2] = 58;
/*      */ 
/* 4282 */       timeAsBytes[3] = (byte)Character.forDigit(minute / 10, 10);
/*      */ 
/* 4284 */       timeAsBytes[4] = (byte)Character.forDigit(minute % 10, 10);
/*      */ 
/* 4287 */       timeAsBytes[5] = 58;
/*      */ 
/* 4289 */       timeAsBytes[6] = (byte)Character.forDigit(seconds / 10, 10);
/*      */ 
/* 4291 */       timeAsBytes[7] = (byte)Character.forDigit(seconds % 10, 10);
/*      */ 
/* 4294 */       unpackedRowData[columnIndex] = timeAsBytes;
/*      */ 
/* 4297 */       break;
/*      */     case 10:
/* 4300 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 4302 */       int year = 0;
/* 4303 */       int month = 0;
/* 4304 */       int day = 0;
/*      */ 
/* 4306 */       int hour = 0;
/* 4307 */       int minute = 0;
/* 4308 */       int seconds = 0;
/*      */ 
/* 4310 */       if (length != 0) {
/* 4311 */         year = binaryData.readInt();
/* 4312 */         month = binaryData.readByte();
/* 4313 */         day = binaryData.readByte();
/*      */       }
/*      */ 
/* 4316 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 4317 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 4319 */           unpackedRowData[columnIndex] = null;
/*      */         }
/*      */         else {
/* 4322 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 4324 */             throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 4328 */           year = 1;
/* 4329 */           month = 1;
/* 4330 */           day = 1;
/*      */         }
/*      */       }
/*      */       else {
/* 4334 */         byte[] dateAsBytes = new byte[10];
/*      */ 
/* 4336 */         dateAsBytes[0] = (byte)Character.forDigit(year / 1000, 10);
/*      */ 
/* 4339 */         int after1000 = year % 1000;
/*      */ 
/* 4341 */         dateAsBytes[1] = (byte)Character.forDigit(after1000 / 100, 10);
/*      */ 
/* 4344 */         int after100 = after1000 % 100;
/*      */ 
/* 4346 */         dateAsBytes[2] = (byte)Character.forDigit(after100 / 10, 10);
/*      */ 
/* 4348 */         dateAsBytes[3] = (byte)Character.forDigit(after100 % 10, 10);
/*      */ 
/* 4351 */         dateAsBytes[4] = 45;
/*      */ 
/* 4353 */         dateAsBytes[5] = (byte)Character.forDigit(month / 10, 10);
/*      */ 
/* 4355 */         dateAsBytes[6] = (byte)Character.forDigit(month % 10, 10);
/*      */ 
/* 4358 */         dateAsBytes[7] = 45;
/*      */ 
/* 4360 */         dateAsBytes[8] = (byte)Character.forDigit(day / 10, 10);
/* 4361 */         dateAsBytes[9] = (byte)Character.forDigit(day % 10, 10);
/*      */ 
/* 4363 */         unpackedRowData[columnIndex] = dateAsBytes;
/*      */       }
/*      */ 
/* 4366 */       break;
/*      */     case 7:
/*      */     case 12:
/* 4370 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 4372 */       int year = 0;
/* 4373 */       int month = 0;
/* 4374 */       int day = 0;
/*      */ 
/* 4376 */       int hour = 0;
/* 4377 */       int minute = 0;
/* 4378 */       int seconds = 0;
/*      */ 
/* 4380 */       int nanos = 0;
/*      */ 
/* 4382 */       if (length != 0) {
/* 4383 */         year = binaryData.readInt();
/* 4384 */         month = binaryData.readByte();
/* 4385 */         day = binaryData.readByte();
/*      */ 
/* 4387 */         if (length > 4) {
/* 4388 */           hour = binaryData.readByte();
/* 4389 */           minute = binaryData.readByte();
/* 4390 */           seconds = binaryData.readByte();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4398 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 4399 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 4401 */           unpackedRowData[columnIndex] = null;
/*      */         }
/*      */         else {
/* 4404 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 4406 */             throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 4410 */           year = 1;
/* 4411 */           month = 1;
/* 4412 */           day = 1;
/*      */         }
/*      */       }
/*      */       else {
/* 4416 */         int stringLength = 19;
/*      */ 
/* 4418 */         byte[] nanosAsBytes = Integer.toString(nanos).getBytes();
/*      */ 
/* 4420 */         stringLength += 1 + nanosAsBytes.length;
/*      */ 
/* 4422 */         byte[] datetimeAsBytes = new byte[stringLength];
/*      */ 
/* 4424 */         datetimeAsBytes[0] = (byte)Character.forDigit(year / 1000, 10);
/*      */ 
/* 4427 */         int after1000 = year % 1000;
/*      */ 
/* 4429 */         datetimeAsBytes[1] = (byte)Character.forDigit(after1000 / 100, 10);
/*      */ 
/* 4432 */         int after100 = after1000 % 100;
/*      */ 
/* 4434 */         datetimeAsBytes[2] = (byte)Character.forDigit(after100 / 10, 10);
/*      */ 
/* 4436 */         datetimeAsBytes[3] = (byte)Character.forDigit(after100 % 10, 10);
/*      */ 
/* 4439 */         datetimeAsBytes[4] = 45;
/*      */ 
/* 4441 */         datetimeAsBytes[5] = (byte)Character.forDigit(month / 10, 10);
/*      */ 
/* 4443 */         datetimeAsBytes[6] = (byte)Character.forDigit(month % 10, 10);
/*      */ 
/* 4446 */         datetimeAsBytes[7] = 45;
/*      */ 
/* 4448 */         datetimeAsBytes[8] = (byte)Character.forDigit(day / 10, 10);
/*      */ 
/* 4450 */         datetimeAsBytes[9] = (byte)Character.forDigit(day % 10, 10);
/*      */ 
/* 4453 */         datetimeAsBytes[10] = 32;
/*      */ 
/* 4455 */         datetimeAsBytes[11] = (byte)Character.forDigit(hour / 10, 10);
/*      */ 
/* 4457 */         datetimeAsBytes[12] = (byte)Character.forDigit(hour % 10, 10);
/*      */ 
/* 4460 */         datetimeAsBytes[13] = 58;
/*      */ 
/* 4462 */         datetimeAsBytes[14] = (byte)Character.forDigit(minute / 10, 10);
/*      */ 
/* 4464 */         datetimeAsBytes[15] = (byte)Character.forDigit(minute % 10, 10);
/*      */ 
/* 4467 */         datetimeAsBytes[16] = 58;
/*      */ 
/* 4469 */         datetimeAsBytes[17] = (byte)Character.forDigit(seconds / 10, 10);
/*      */ 
/* 4471 */         datetimeAsBytes[18] = (byte)Character.forDigit(seconds % 10, 10);
/*      */ 
/* 4474 */         datetimeAsBytes[19] = 46;
/*      */ 
/* 4476 */         int nanosOffset = 20;
/*      */ 
/* 4478 */         for (int j = 0; j < nanosAsBytes.length; j++) {
/* 4479 */           datetimeAsBytes[(nanosOffset + j)] = nanosAsBytes[j];
/*      */         }
/*      */ 
/* 4482 */         unpackedRowData[columnIndex] = datetimeAsBytes;
/*      */       }
/*      */ 
/* 4485 */       break;
/*      */     case 0:
/*      */     case 15:
/*      */     case 16:
/*      */     case 246:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/*      */     case 253:
/*      */     case 254:
/* 4497 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 4499 */       break;
/*      */     default:
/* 4502 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void negotiateSSLConnection(String user, String password, String database, int packLength)
/*      */     throws SQLException
/*      */   {
/* 4525 */     if (!ExportControlled.enabled()) {
/* 4526 */       throw new ConnectionFeatureNotAvailableException(this.connection, this.lastPacketSentTimeMs, null);
/*      */     }
/*      */ 
/* 4530 */     boolean doSecureAuth = false;
/*      */ 
/* 4532 */     if ((this.serverCapabilities & 0x8000) != 0) {
/* 4533 */       this.clientParam |= 32768L;
/* 4534 */       doSecureAuth = true;
/*      */     }
/*      */ 
/* 4537 */     this.clientParam |= 2048L;
/*      */ 
/* 4539 */     Buffer packet = new Buffer(packLength);
/*      */ 
/* 4541 */     if (this.use41Extensions)
/* 4542 */       packet.writeLong(this.clientParam);
/*      */     else {
/* 4544 */       packet.writeInt((int)this.clientParam);
/*      */     }
/*      */ 
/* 4547 */     send(packet, packet.getPosition());
/*      */ 
/* 4549 */     ExportControlled.transformSocketToSSLSocket(this);
/*      */ 
/* 4551 */     packet.clear();
/*      */ 
/* 4553 */     if (doSecureAuth) {
/* 4554 */       if (versionMeetsMinimum(4, 1, 1))
/* 4555 */         secureAuth411(null, packLength, user, password, database, true);
/*      */       else
/* 4557 */         secureAuth411(null, packLength, user, password, database, true);
/*      */     }
/*      */     else {
/* 4560 */       if (this.use41Extensions) {
/* 4561 */         packet.writeLong(this.clientParam);
/* 4562 */         packet.writeLong(this.maxThreeBytes);
/*      */       } else {
/* 4564 */         packet.writeInt((int)this.clientParam);
/* 4565 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */ 
/* 4569 */       packet.writeString(user);
/*      */ 
/* 4571 */       if (this.protocolVersion > 9)
/* 4572 */         packet.writeString(Util.newCrypt(password, this.seed));
/*      */       else {
/* 4574 */         packet.writeString(Util.oldCrypt(password, this.seed));
/*      */       }
/*      */ 
/* 4577 */       if (((this.serverCapabilities & 0x8) != 0) && (database != null) && (database.length() > 0))
/*      */       {
/* 4579 */         packet.writeString(database);
/*      */       }
/*      */ 
/* 4582 */       send(packet, packet.getPosition());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getServerStatus() {
/* 4587 */     return this.serverStatus;
/*      */   }
/*      */ 
/*      */   protected List fetchRowsViaCursor(List fetchedRows, long statementId, Field[] columnTypes, int fetchSize, boolean useBufferRowExplicit)
/*      */     throws SQLException
/*      */   {
/* 4593 */     if (fetchedRows == null)
/* 4594 */       fetchedRows = new ArrayList(fetchSize);
/*      */     else {
/* 4596 */       fetchedRows.clear();
/*      */     }
/*      */ 
/* 4599 */     this.sharedSendPacket.clear();
/*      */ 
/* 4601 */     this.sharedSendPacket.writeByte(28);
/* 4602 */     this.sharedSendPacket.writeLong(statementId);
/* 4603 */     this.sharedSendPacket.writeLong(fetchSize);
/*      */ 
/* 4605 */     sendCommand(28, null, this.sharedSendPacket, true, null, 0);
/*      */ 
/* 4608 */     ResultSetRow row = null;
/*      */ 
/* 4611 */     while ((row = nextRow(columnTypes, columnTypes.length, true, 1007, false, useBufferRowExplicit, false, null)) != null) {
/* 4612 */       fetchedRows.add(row);
/*      */     }
/*      */ 
/* 4615 */     return fetchedRows;
/*      */   }
/*      */ 
/*      */   protected long getThreadId() {
/* 4619 */     return this.threadId;
/*      */   }
/*      */ 
/*      */   protected boolean useNanosForElapsedTime() {
/* 4623 */     return this.useNanosForElapsedTime;
/*      */   }
/*      */ 
/*      */   protected long getSlowQueryThreshold() {
/* 4627 */     return this.slowQueryThreshold;
/*      */   }
/*      */ 
/*      */   protected String getQueryTimingUnits() {
/* 4631 */     return this.queryTimingUnits;
/*      */   }
/*      */ 
/*      */   protected int getCommandCount() {
/* 4635 */     return this.commandCount;
/*      */   }
/*      */ 
/*      */   private void checkTransactionState(int oldStatus) throws SQLException {
/* 4639 */     boolean previouslyInTrans = (oldStatus & 0x1) != 0;
/* 4640 */     boolean currentlyInTrans = (this.serverStatus & 0x1) != 0;
/*      */ 
/* 4642 */     if ((previouslyInTrans) && (!currentlyInTrans))
/* 4643 */       this.connection.transactionCompleted();
/* 4644 */     else if ((!previouslyInTrans) && (currentlyInTrans))
/* 4645 */       this.connection.transactionBegun();
/*      */   }
/*      */ 
/*      */   protected void setStatementInterceptors(List statementInterceptors)
/*      */   {
/* 4650 */     this.statementInterceptors = statementInterceptors;
/*      */   }
/*      */ 
/*      */   protected ExceptionInterceptor getExceptionInterceptor() {
/* 4654 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  127 */     OutputStreamWriter outWriter = null;
/*      */     try
/*      */     {
/*  135 */       outWriter = new OutputStreamWriter(new ByteArrayOutputStream());
/*  136 */       jvmPlatformCharset = outWriter.getEncoding();
/*      */     } finally {
/*      */       try {
/*  139 */         if (outWriter != null)
/*  140 */           outWriter.close();
/*      */       }
/*      */       catch (IOException ioEx)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.MysqlIO
 * JD-Core Version:    0.6.0
 */