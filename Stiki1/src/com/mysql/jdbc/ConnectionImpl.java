/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.LogFactory;
/*      */ import com.mysql.jdbc.log.NullLogger;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import com.mysql.jdbc.util.LRUCache;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Method;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class ConnectionImpl extends ConnectionPropertiesImpl
/*      */   implements Connection
/*      */ {
/*      */   private static final String JDBC_LOCAL_CHARACTER_SET_RESULTS = "jdbc.local.character_set_results";
/*  188 */   private static final Object CHARSET_CONVERTER_NOT_AVAILABLE_MARKER = new Object();
/*      */   public static Map charsetMap;
/*      */   protected static final String DEFAULT_LOGGER_CLASS = "com.mysql.jdbc.log.StandardLogger";
/*      */   private static final int HISTOGRAM_BUCKETS = 20;
/*      */   private static final String LOGGER_INSTANCE_NAME = "MySQL";
/*  208 */   private static Map mapTransIsolationNameToValue = null;
/*      */ 
/*  211 */   private static final Log NULL_LOGGER = new NullLogger("MySQL");
/*      */   private static Map roundRobinStatsMap;
/*  215 */   private static final Map serverCollationByUrl = new HashMap();
/*      */ 
/*  217 */   private static final Map serverConfigByUrl = new HashMap();
/*      */   private long queryTimeCount;
/*      */   private double queryTimeSum;
/*      */   private double queryTimeSumSquares;
/*      */   private double queryTimeMean;
/*      */   private Timer cancelTimer;
/*      */   private List connectionLifecycleInterceptors;
/*      */   private static final Constructor JDBC_4_CONNECTION_CTOR;
/*      */   private static final int DEFAULT_RESULT_SET_TYPE = 1003;
/*      */   private static final int DEFAULT_RESULT_SET_CONCURRENCY = 1007;
/*  388 */   private boolean autoCommit = true;
/*      */   private Map cachedPreparedStatementParams;
/*  396 */   private String characterSetMetadata = null;
/*      */ 
/*  402 */   private String characterSetResultsOnServer = null;
/*      */ 
/*  409 */   private Map charsetConverterMap = new HashMap(CharsetMapping.getNumberOfCharsetsConfigured());
/*      */   private Map charsetToNumBytesMap;
/*  419 */   private long connectionCreationTimeMillis = 0L;
/*      */   private long connectionId;
/*  425 */   private String database = null;
/*      */ 
/*  428 */   private java.sql.DatabaseMetaData dbmd = null;
/*      */   private TimeZone defaultTimeZone;
/*      */   private ProfilerEventHandler eventSink;
/*  435 */   private boolean executingFailoverReconnect = false;
/*      */ 
/*  438 */   private boolean failedOver = false;
/*      */   private Throwable forceClosedReason;
/*      */   private Throwable forcedClosedLocation;
/*  447 */   private boolean hasIsolationLevels = false;
/*      */ 
/*  450 */   private boolean hasQuotedIdentifiers = false;
/*      */ 
/*  453 */   private String host = null;
/*      */ 
/*  456 */   private List hostList = null;
/*      */ 
/*  459 */   private int hostListSize = 0;
/*      */ 
/*  465 */   private String[] indexToCharsetMapping = CharsetMapping.INDEX_TO_CHARSET;
/*      */ 
/*  468 */   private MysqlIO io = null;
/*      */ 
/*  470 */   private boolean isClientTzUTC = false;
/*      */ 
/*  473 */   private boolean isClosed = true;
/*      */ 
/*  476 */   private boolean isInGlobalTx = false;
/*      */ 
/*  479 */   private boolean isRunningOnJDK13 = false;
/*      */ 
/*  482 */   private int isolationLevel = 2;
/*      */ 
/*  484 */   private boolean isServerTzUTC = false;
/*      */ 
/*  487 */   private long lastQueryFinishedTime = 0L;
/*      */ 
/*  490 */   private Log log = NULL_LOGGER;
/*      */ 
/*  496 */   private long longestQueryTimeMs = 0L;
/*      */ 
/*  499 */   private boolean lowerCaseTableNames = false;
/*      */ 
/*  502 */   private long masterFailTimeMillis = 0L;
/*      */ 
/*  504 */   private long maximumNumberTablesAccessed = 0L;
/*      */ 
/*  507 */   private boolean maxRowsChanged = false;
/*      */   private long metricsLastReportedMs;
/*  512 */   private long minimumNumberTablesAccessed = 9223372036854775807L;
/*      */ 
/*  515 */   private final Object mutex = new Object();
/*      */ 
/*  518 */   private String myURL = null;
/*      */ 
/*  521 */   private boolean needsPing = false;
/*      */ 
/*  523 */   private int netBufferLength = 16384;
/*      */ 
/*  525 */   private boolean noBackslashEscapes = false;
/*      */ 
/*  527 */   private long numberOfPreparedExecutes = 0L;
/*      */ 
/*  529 */   private long numberOfPrepares = 0L;
/*      */ 
/*  531 */   private long numberOfQueriesIssued = 0L;
/*      */ 
/*  533 */   private long numberOfResultSetsCreated = 0L;
/*      */   private long[] numTablesMetricsHistBreakpoints;
/*      */   private int[] numTablesMetricsHistCounts;
/*  539 */   private long[] oldHistBreakpoints = null;
/*      */ 
/*  541 */   private int[] oldHistCounts = null;
/*      */   private Map openStatements;
/*      */   private LRUCache parsedCallableStatementCache;
/*  548 */   private boolean parserKnowsUnicode = false;
/*      */ 
/*  551 */   private String password = null;
/*      */   private long[] perfMetricsHistBreakpoints;
/*      */   private int[] perfMetricsHistCounts;
/*      */   private Throwable pointOfOrigin;
/*  561 */   private int port = 3306;
/*      */ 
/*  567 */   private boolean preferSlaveDuringFailover = false;
/*      */ 
/*  570 */   protected Properties props = null;
/*      */ 
/*  573 */   private long queriesIssuedFailedOver = 0L;
/*      */ 
/*  576 */   private boolean readInfoMsg = false;
/*      */ 
/*  579 */   private boolean readOnly = false;
/*      */   protected LRUCache resultSetMetadataCache;
/*  585 */   private TimeZone serverTimezoneTZ = null;
/*      */ 
/*  588 */   private Map serverVariables = null;
/*      */ 
/*  590 */   private long shortestQueryTimeMs = 9223372036854775807L;
/*      */   private Map statementsUsingMaxRows;
/*  595 */   private double totalQueryTimeMs = 0.0D;
/*      */ 
/*  598 */   private boolean transactionsSupported = false;
/*      */   private Map typeMap;
/*  607 */   private boolean useAnsiQuotes = false;
/*      */ 
/*  610 */   private String user = null;
/*      */ 
/*  616 */   private boolean useServerPreparedStmts = false;
/*      */   private LRUCache serverSideStatementCheckCache;
/*      */   private LRUCache serverSideStatementCache;
/*      */   private Calendar sessionCalendar;
/*      */   private Calendar utcCalendar;
/*      */   private String origHostToConnectTo;
/*      */   private int origPortToConnectTo;
/*      */   private String origDatabaseToConnectTo;
/*  632 */   private String errorMessageEncoding = "Cp1252";
/*      */   private boolean usePlatformCharsetConverters;
/*  639 */   private boolean hasTriedMasterFlag = false;
/*      */ 
/*  645 */   private String statementComment = null;
/*      */   private boolean storesLowerCaseTableName;
/*      */   private List statementInterceptors;
/*      */   private boolean requiresEscapingEncoder;
/* 3884 */   private boolean usingCachedConfig = false;
/*      */ 
/* 4004 */   private int autoIncrementIncrement = 0;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */ 
/*      */   protected static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend, ExceptionInterceptor interceptor)
/*      */   {
/*  267 */     String origMessage = sqlEx.getMessage();
/*  268 */     String sqlState = sqlEx.getSQLState();
/*  269 */     int vendorErrorCode = sqlEx.getErrorCode();
/*      */ 
/*  271 */     StringBuffer messageBuf = new StringBuffer(origMessage.length() + messageToAppend.length());
/*      */ 
/*  273 */     messageBuf.append(origMessage);
/*  274 */     messageBuf.append(messageToAppend);
/*      */ 
/*  276 */     SQLException sqlExceptionWithNewMessage = SQLError.createSQLException(messageBuf.toString(), sqlState, vendorErrorCode, interceptor);
/*      */     try
/*      */     {
/*  286 */       Method getStackTraceMethod = null;
/*  287 */       Method setStackTraceMethod = null;
/*  288 */       Object theStackTraceAsObject = null;
/*      */ 
/*  290 */       Class stackTraceElementClass = Class.forName("java.lang.StackTraceElement");
/*      */ 
/*  292 */       Class stackTraceElementArrayClass = Array.newInstance(stackTraceElementClass, new int[] { 0 }).getClass();
/*      */ 
/*  295 */       getStackTraceMethod = Throwable.class.getMethod("getStackTrace", new Class[0]);
/*      */ 
/*  298 */       setStackTraceMethod = class$java$lang$Throwable.getMethod("setStackTrace", new Class[] { stackTraceElementArrayClass });
/*      */ 
/*  301 */       if ((getStackTraceMethod != null) && (setStackTraceMethod != null)) {
/*  302 */         theStackTraceAsObject = getStackTraceMethod.invoke(sqlEx, new Object[0]);
/*      */ 
/*  304 */         setStackTraceMethod.invoke(sqlExceptionWithNewMessage, new Object[] { theStackTraceAsObject });
/*      */       }
/*      */     }
/*      */     catch (NoClassDefFoundError noClassDefFound)
/*      */     {
/*      */     }
/*      */     catch (NoSuchMethodException noSuchMethodEx)
/*      */     {
/*      */     }
/*      */     catch (Throwable catchAll) {
/*      */     }
/*  315 */     return sqlExceptionWithNewMessage;
/*      */   }
/*      */ 
/*      */   protected synchronized Timer getCancelTimer() {
/*  319 */     if (this.cancelTimer == null) {
/*  320 */       boolean createdNamedTimer = false;
/*      */       try
/*      */       {
/*  325 */         Constructor ctr = class$java$util$Timer.getConstructor(new Class[] { String.class, Boolean.TYPE });
/*      */ 
/*  327 */         this.cancelTimer = ((Timer)ctr.newInstance(new Object[] { "MySQL Statement Cancellation Timer", Boolean.TRUE }));
/*  328 */         createdNamedTimer = true;
/*      */       } catch (Throwable t) {
/*  330 */         createdNamedTimer = false;
/*      */       }
/*      */ 
/*  333 */       if (!createdNamedTimer) {
/*  334 */         this.cancelTimer = new Timer(true);
/*      */       }
/*      */     }
/*      */ 
/*  338 */     return this.cancelTimer;
/*      */   }
/*      */ 
/*      */   protected static Connection getInstance(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url)
/*      */     throws SQLException
/*      */   {
/*  352 */     if (!Util.isJdbc4()) {
/*  353 */       return new ConnectionImpl(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
/*      */     }
/*      */ 
/*  357 */     return (Connection)Util.handleNewInstance(JDBC_4_CONNECTION_CTOR, new Object[] { hostToConnectTo, Constants.integerValueOf(portToConnectTo), info, databaseToConnectTo, url }, null);
/*      */   }
/*      */ 
/*      */   private static synchronized int getNextRoundRobinHostIndex(String url, List hostList)
/*      */   {
/*  368 */     int indexRange = hostList.size();
/*      */ 
/*  370 */     int index = (int)(Math.random() * indexRange);
/*      */ 
/*  372 */     return index;
/*      */   }
/*      */ 
/*      */   private static boolean nullSafeCompare(String s1, String s2) {
/*  376 */     if ((s1 == null) && (s2 == null)) {
/*  377 */       return true;
/*      */     }
/*      */ 
/*  380 */     if ((s1 == null) && (s2 != null)) {
/*  381 */       return false;
/*      */     }
/*      */ 
/*  384 */     return s1.equals(s2);
/*      */   }
/*      */ 
/*      */   protected ConnectionImpl()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected ConnectionImpl(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url)
/*      */     throws SQLException
/*      */   {
/*  684 */     this.charsetToNumBytesMap = new HashMap();
/*      */ 
/*  686 */     this.connectionCreationTimeMillis = System.currentTimeMillis();
/*  687 */     this.pointOfOrigin = new Throwable();
/*      */ 
/*  693 */     this.origHostToConnectTo = hostToConnectTo;
/*  694 */     this.origPortToConnectTo = portToConnectTo;
/*  695 */     this.origDatabaseToConnectTo = databaseToConnectTo;
/*      */     try
/*      */     {
/*  698 */       class$java$sql$Blob.getMethod("truncate", new Class[] { Long.TYPE });
/*      */ 
/*  700 */       this.isRunningOnJDK13 = false;
/*      */     } catch (NoSuchMethodException nsme) {
/*  702 */       this.isRunningOnJDK13 = true;
/*      */     }
/*      */ 
/*  705 */     this.sessionCalendar = new GregorianCalendar();
/*  706 */     this.utcCalendar = new GregorianCalendar();
/*  707 */     this.utcCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */ 
/*  719 */     this.log = LogFactory.getLogger(getLogger(), "MySQL", getExceptionInterceptor());
/*      */ 
/*  723 */     this.defaultTimeZone = Util.getDefaultTimeZone();
/*      */ 
/*  725 */     if ("GMT".equalsIgnoreCase(this.defaultTimeZone.getID()))
/*  726 */       this.isClientTzUTC = true;
/*      */     else {
/*  728 */       this.isClientTzUTC = false;
/*      */     }
/*      */ 
/*  731 */     this.openStatements = new HashMap();
/*  732 */     this.serverVariables = new HashMap();
/*  733 */     this.hostList = new ArrayList();
/*      */ 
/*  735 */     int numHosts = Integer.parseInt(info.getProperty("NUM_HOSTS"));
/*      */ 
/*  737 */     if (hostToConnectTo == null) {
/*  738 */       this.host = "localhost";
/*  739 */       this.hostList.add(this.host + ":" + portToConnectTo);
/*  740 */     } else if (numHosts > 1)
/*      */     {
/*  743 */       for (int i = 0; i < numHosts; i++) {
/*  744 */         int index = i + 1;
/*      */ 
/*  746 */         this.hostList.add(info.getProperty(new StringBuffer().append("HOST.").append(index).toString()) + ":" + info.getProperty(new StringBuffer().append("PORT.").append(index).toString()));
/*      */       }
/*      */     }
/*      */     else {
/*  750 */       this.host = hostToConnectTo;
/*      */ 
/*  752 */       if (hostToConnectTo.indexOf(":") == -1)
/*  753 */         this.hostList.add(this.host + ":" + portToConnectTo);
/*      */       else {
/*  755 */         this.hostList.add(this.host);
/*      */       }
/*      */     }
/*      */ 
/*  759 */     this.hostListSize = this.hostList.size();
/*  760 */     this.port = portToConnectTo;
/*      */ 
/*  762 */     if (databaseToConnectTo == null) {
/*  763 */       databaseToConnectTo = "";
/*      */     }
/*      */ 
/*  766 */     this.database = databaseToConnectTo;
/*  767 */     this.myURL = url;
/*  768 */     this.user = info.getProperty("user");
/*  769 */     this.password = info.getProperty("password");
/*      */ 
/*  772 */     if ((this.user == null) || (this.user.equals(""))) {
/*  773 */       this.user = "";
/*      */     }
/*      */ 
/*  776 */     if (this.password == null) {
/*  777 */       this.password = "";
/*      */     }
/*      */ 
/*  780 */     this.props = info;
/*  781 */     initializeDriverProperties(info);
/*      */     try
/*      */     {
/*  785 */       this.dbmd = getMetaData(false, false);
/*  786 */       initializeSafeStatementInterceptors();
/*  787 */       createNewIO(false);
/*  788 */       unSafeStatementInterceptors();
/*      */     } catch (SQLException ex) {
/*  790 */       cleanup(ex);
/*      */ 
/*  793 */       throw ex;
/*      */     } catch (Exception ex) {
/*  795 */       cleanup(ex);
/*      */ 
/*  797 */       StringBuffer mesg = new StringBuffer(128);
/*      */ 
/*  799 */       if (!getParanoid()) {
/*  800 */         mesg.append("Cannot connect to MySQL server on ");
/*  801 */         mesg.append(this.host);
/*  802 */         mesg.append(":");
/*  803 */         mesg.append(this.port);
/*  804 */         mesg.append(".\n\n");
/*  805 */         mesg.append("Make sure that there is a MySQL server ");
/*  806 */         mesg.append("running on the machine/port you are trying ");
/*  807 */         mesg.append("to connect to and that the machine this software is running on ");
/*      */ 
/*  810 */         mesg.append("is able to connect to this host/port (i.e. not firewalled). ");
/*      */ 
/*  812 */         mesg.append("Also make sure that the server has not been started with the --skip-networking ");
/*      */ 
/*  815 */         mesg.append("flag.\n\n");
/*      */       } else {
/*  817 */         mesg.append("Unable to connect to database.");
/*      */       }
/*      */ 
/*  820 */       SQLException sqlEx = SQLError.createSQLException(mesg.toString(), "08S01", getExceptionInterceptor());
/*      */ 
/*  823 */       sqlEx.initCause(ex);
/*      */ 
/*  825 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void unSafeStatementInterceptors() throws SQLException
/*      */   {
/*  831 */     ArrayList unSafedStatementInterceptors = new ArrayList(this.statementInterceptors.size());
/*      */ 
/*  833 */     this.statementInterceptors = new ArrayList(this.statementInterceptors.size());
/*      */ 
/*  835 */     for (int i = 0; i < this.statementInterceptors.size(); i++) {
/*  836 */       NoSubInterceptorWrapper wrappedInterceptor = (NoSubInterceptorWrapper)this.statementInterceptors.get(i);
/*      */ 
/*  838 */       unSafedStatementInterceptors.add(wrappedInterceptor.getUnderlyingInterceptor());
/*      */     }
/*      */ 
/*  841 */     this.statementInterceptors = unSafedStatementInterceptors;
/*      */   }
/*      */ 
/*      */   protected void initializeSafeStatementInterceptors() throws SQLException {
/*  845 */     this.isClosed = false;
/*      */ 
/*  847 */     List unwrappedInterceptors = Util.loadExtensions(this, this.props, getStatementInterceptors(), "MysqlIo.BadStatementInterceptor", getExceptionInterceptor());
/*      */ 
/*  851 */     this.statementInterceptors = new ArrayList(unwrappedInterceptors.size());
/*      */ 
/*  853 */     for (int i = 0; i < unwrappedInterceptors.size(); i++) {
/*  854 */       Object interceptor = unwrappedInterceptors.get(i);
/*      */ 
/*  858 */       if ((interceptor instanceof StatementInterceptor)) {
/*  859 */         if (ReflectiveStatementInterceptorAdapter.getV2PostProcessMethod(interceptor.getClass()) != null)
/*  860 */           this.statementInterceptors.add(new NoSubInterceptorWrapper(new ReflectiveStatementInterceptorAdapter((StatementInterceptor)interceptor)));
/*      */         else
/*  862 */           this.statementInterceptors.add(new NoSubInterceptorWrapper(new V1toV2StatementInterceptorAdapter((StatementInterceptor)interceptor)));
/*      */       }
/*      */       else
/*  865 */         this.statementInterceptors.add(new NoSubInterceptorWrapper((StatementInterceptorV2)interceptor));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected List getStatementInterceptorsInstances()
/*      */   {
/*  873 */     return this.statementInterceptors;
/*      */   }
/*      */ 
/*      */   private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound)
/*      */   {
/*  879 */     if (histogramCounts == null) {
/*  880 */       createInitialHistogram(histogramBreakpoints, currentLowerBound, currentUpperBound);
/*      */     }
/*      */     else
/*  883 */       for (int i = 0; i < 20; i++)
/*  884 */         if (histogramBreakpoints[i] >= value) {
/*  885 */           histogramCounts[i] += numberOfTimes;
/*      */ 
/*  887 */           break;
/*      */         }
/*      */   }
/*      */ 
/*      */   private void addToPerformanceHistogram(long value, int numberOfTimes)
/*      */   {
/*  894 */     checkAndCreatePerformanceHistogram();
/*      */ 
/*  896 */     addToHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, value, numberOfTimes, this.shortestQueryTimeMs == 9223372036854775807L ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */ 
/*      */   private void addToTablesAccessedHistogram(long value, int numberOfTimes)
/*      */   {
/*  903 */     checkAndCreateTablesAccessedHistogram();
/*      */ 
/*  905 */     addToHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, value, numberOfTimes, this.minimumNumberTablesAccessed == 9223372036854775807L ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */ 
/*      */   private void buildCollationMapping()
/*      */     throws SQLException
/*      */   {
/*  920 */     if (versionMeetsMinimum(4, 1, 0))
/*      */     {
/*  922 */       TreeMap sortedCollationMap = null;
/*      */ 
/*  924 */       if (getCacheServerConfiguration()) {
/*  925 */         synchronized (serverConfigByUrl) {
/*  926 */           sortedCollationMap = (TreeMap)serverCollationByUrl.get(getURL());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  931 */       java.sql.Statement stmt = null;
/*  932 */       ResultSet results = null;
/*      */       try
/*      */       {
/*  935 */         if (sortedCollationMap == null) {
/*  936 */           sortedCollationMap = new TreeMap();
/*      */ 
/*  938 */           stmt = getMetadataSafeStatement();
/*      */ 
/*  940 */           results = stmt.executeQuery("SHOW COLLATION");
/*      */ 
/*  943 */           while (results.next()) {
/*  944 */             String charsetName = results.getString(2);
/*  945 */             Integer charsetIndex = Constants.integerValueOf(results.getInt(3));
/*      */ 
/*  947 */             sortedCollationMap.put(charsetIndex, charsetName);
/*      */           }
/*      */ 
/*  950 */           if (getCacheServerConfiguration()) {
/*  951 */             synchronized (serverConfigByUrl) {
/*  952 */               serverCollationByUrl.put(getURL(), sortedCollationMap);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  960 */         int highestIndex = ((Integer)sortedCollationMap.lastKey()).intValue();
/*      */ 
/*  963 */         if (CharsetMapping.INDEX_TO_CHARSET.length > highestIndex) {
/*  964 */           highestIndex = CharsetMapping.INDEX_TO_CHARSET.length;
/*      */         }
/*      */ 
/*  967 */         this.indexToCharsetMapping = new String[highestIndex + 1];
/*      */ 
/*  969 */         for (int i = 0; i < CharsetMapping.INDEX_TO_CHARSET.length; i++) {
/*  970 */           this.indexToCharsetMapping[i] = CharsetMapping.INDEX_TO_CHARSET[i];
/*      */         }
/*      */ 
/*  973 */         Iterator indexIter = sortedCollationMap.entrySet().iterator();
/*  974 */         while (indexIter.hasNext()) {
/*  975 */           Map.Entry indexEntry = (Map.Entry)indexIter.next();
/*      */ 
/*  977 */           String mysqlCharsetName = (String)indexEntry.getValue();
/*      */ 
/*  979 */           this.indexToCharsetMapping[((Integer)indexEntry.getKey()).intValue()] = CharsetMapping.getJavaEncodingForMysqlEncoding(mysqlCharsetName, this);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException e)
/*      */       {
/*  985 */         throw e;
/*      */       } finally {
/*  987 */         if (results != null) {
/*      */           try {
/*  989 */             results.close();
/*      */           }
/*      */           catch (SQLException sqlE)
/*      */           {
/*      */           }
/*      */         }
/*  995 */         if (stmt != null)
/*      */           try {
/*  997 */             stmt.close();
/*      */           }
/*      */           catch (SQLException sqlE)
/*      */           {
/*      */           }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1006 */       this.indexToCharsetMapping = CharsetMapping.INDEX_TO_CHARSET;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean canHandleAsServerPreparedStatement(String sql) throws SQLException
/*      */   {
/* 1012 */     if ((sql == null) || (sql.length() == 0)) {
/* 1013 */       return true;
/*      */     }
/*      */ 
/* 1016 */     if (!this.useServerPreparedStmts) {
/* 1017 */       return false;
/*      */     }
/*      */ 
/* 1020 */     if (getCachePreparedStatements()) {
/* 1021 */       synchronized (this.serverSideStatementCheckCache) {
/* 1022 */         Boolean flag = (Boolean)this.serverSideStatementCheckCache.get(sql);
/*      */ 
/* 1024 */         if (flag != null) {
/* 1025 */           return flag.booleanValue();
/*      */         }
/*      */ 
/* 1028 */         boolean canHandle = canHandleAsServerPreparedStatementNoCache(sql);
/*      */ 
/* 1030 */         if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 1031 */           this.serverSideStatementCheckCache.put(sql, canHandle ? Boolean.TRUE : Boolean.FALSE);
/*      */         }
/*      */ 
/* 1035 */         return canHandle;
/*      */       }
/*      */     }
/*      */ 
/* 1039 */     return canHandleAsServerPreparedStatementNoCache(sql);
/*      */   }
/*      */ 
/*      */   private boolean canHandleAsServerPreparedStatementNoCache(String sql)
/*      */     throws SQLException
/*      */   {
/* 1046 */     if (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "CALL")) {
/* 1047 */       return false;
/*      */     }
/*      */ 
/* 1050 */     boolean canHandleAsStatement = true;
/*      */ 
/* 1052 */     if ((!versionMeetsMinimum(5, 0, 7)) && ((StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "SELECT")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "REPLACE"))))
/*      */     {
/* 1070 */       int currentPos = 0;
/* 1071 */       int statementLength = sql.length();
/* 1072 */       int lastPosToLook = statementLength - 7;
/* 1073 */       boolean allowBackslashEscapes = !this.noBackslashEscapes;
/* 1074 */       char quoteChar = this.useAnsiQuotes ? '"' : '\'';
/* 1075 */       boolean foundLimitWithPlaceholder = false;
/*      */ 
/* 1077 */       while (currentPos < lastPosToLook) {
/* 1078 */         int limitStart = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, sql, "LIMIT ", quoteChar, allowBackslashEscapes);
/*      */ 
/* 1082 */         if (limitStart == -1)
/*      */         {
/*      */           break;
/*      */         }
/* 1086 */         currentPos = limitStart + 7;
/*      */ 
/* 1088 */         while (currentPos < statementLength) {
/* 1089 */           char c = sql.charAt(currentPos);
/*      */ 
/* 1096 */           if ((!Character.isDigit(c)) && (!Character.isWhitespace(c)) && (c != ',') && (c != '?'))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/* 1101 */           if (c == '?') {
/* 1102 */             foundLimitWithPlaceholder = true;
/* 1103 */             break;
/*      */           }
/*      */ 
/* 1106 */           currentPos++;
/*      */         }
/*      */       }
/*      */ 
/* 1110 */       canHandleAsStatement = !foundLimitWithPlaceholder;
/* 1111 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "CREATE TABLE")) {
/* 1112 */       canHandleAsStatement = false;
/* 1113 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "DO")) {
/* 1114 */       canHandleAsStatement = false;
/* 1115 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "SET")) {
/* 1116 */       canHandleAsStatement = false;
/*      */     }
/*      */ 
/* 1121 */     return canHandleAsStatement;
/*      */   }
/*      */ 
/*      */   public void changeUser(String userName, String newPassword)
/*      */     throws SQLException
/*      */   {
/* 1139 */     if ((userName == null) || (userName.equals(""))) {
/* 1140 */       userName = "";
/*      */     }
/*      */ 
/* 1143 */     if (newPassword == null) {
/* 1144 */       newPassword = "";
/*      */     }
/*      */ 
/* 1147 */     this.io.changeUser(userName, newPassword, this.database);
/* 1148 */     this.user = userName;
/* 1149 */     this.password = newPassword;
/*      */ 
/* 1151 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 1152 */       configureClientCharacterSet(true);
/*      */     }
/*      */ 
/* 1155 */     setSessionVariables();
/*      */ 
/* 1157 */     setupServerForTruncationChecks();
/*      */   }
/*      */ 
/*      */   private boolean characterSetNamesMatches(String mysqlEncodingName)
/*      */   {
/* 1164 */     return (mysqlEncodingName != null) && (mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_client"))) && (mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_connection")));
/*      */   }
/*      */ 
/*      */   private void checkAndCreatePerformanceHistogram()
/*      */   {
/* 1170 */     if (this.perfMetricsHistCounts == null) {
/* 1171 */       this.perfMetricsHistCounts = new int[20];
/*      */     }
/*      */ 
/* 1174 */     if (this.perfMetricsHistBreakpoints == null)
/* 1175 */       this.perfMetricsHistBreakpoints = new long[20];
/*      */   }
/*      */ 
/*      */   private void checkAndCreateTablesAccessedHistogram()
/*      */   {
/* 1180 */     if (this.numTablesMetricsHistCounts == null) {
/* 1181 */       this.numTablesMetricsHistCounts = new int[20];
/*      */     }
/*      */ 
/* 1184 */     if (this.numTablesMetricsHistBreakpoints == null)
/* 1185 */       this.numTablesMetricsHistBreakpoints = new long[20];
/*      */   }
/*      */ 
/*      */   protected void checkClosed() throws SQLException
/*      */   {
/* 1190 */     if (this.isClosed)
/* 1191 */       throwConnectionClosedException();
/*      */   }
/*      */ 
/*      */   void throwConnectionClosedException() throws SQLException
/*      */   {
/* 1196 */     StringBuffer messageBuf = new StringBuffer("No operations allowed after connection closed.");
/*      */ 
/* 1199 */     if ((this.forcedClosedLocation != null) || (this.forceClosedReason != null)) {
/* 1200 */       messageBuf.append("Connection was implicitly closed by the driver.");
/*      */     }
/*      */ 
/* 1204 */     SQLException ex = SQLError.createSQLException(messageBuf.toString(), "08003", getExceptionInterceptor());
/*      */ 
/* 1207 */     if (this.forceClosedReason != null) {
/* 1208 */       ex.initCause(this.forceClosedReason);
/*      */     }
/*      */ 
/* 1211 */     throw ex;
/*      */   }
/*      */ 
/*      */   private void checkServerEncoding()
/*      */     throws SQLException
/*      */   {
/* 1222 */     if ((getUseUnicode()) && (getEncoding() != null))
/*      */     {
/* 1224 */       return;
/*      */     }
/*      */ 
/* 1227 */     String serverEncoding = (String)this.serverVariables.get("character_set");
/*      */ 
/* 1230 */     if (serverEncoding == null)
/*      */     {
/* 1232 */       serverEncoding = (String)this.serverVariables.get("character_set_server");
/*      */     }
/*      */ 
/* 1236 */     String mappedServerEncoding = null;
/*      */ 
/* 1238 */     if (serverEncoding != null) {
/* 1239 */       mappedServerEncoding = CharsetMapping.getJavaEncodingForMysqlEncoding(serverEncoding.toUpperCase(Locale.ENGLISH), this);
/*      */     }
/*      */ 
/* 1247 */     if ((!getUseUnicode()) && (mappedServerEncoding != null)) {
/* 1248 */       SingleByteCharsetConverter converter = getCharsetConverter(mappedServerEncoding);
/*      */ 
/* 1250 */       if (converter != null) {
/* 1251 */         setUseUnicode(true);
/* 1252 */         setEncoding(mappedServerEncoding);
/*      */ 
/* 1254 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1262 */     if (serverEncoding != null) {
/* 1263 */       if (mappedServerEncoding == null)
/*      */       {
/* 1266 */         if (Character.isLowerCase(serverEncoding.charAt(0))) {
/* 1267 */           char[] ach = serverEncoding.toCharArray();
/* 1268 */           ach[0] = Character.toUpperCase(serverEncoding.charAt(0));
/* 1269 */           setEncoding(new String(ach));
/*      */         }
/*      */       }
/*      */ 
/* 1273 */       if (mappedServerEncoding == null) {
/* 1274 */         throw SQLError.createSQLException("Unknown character encoding on server '" + serverEncoding + "', use 'characterEncoding=' property " + " to provide correct mapping", "01S00", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1286 */         "abc".getBytes(mappedServerEncoding);
/* 1287 */         setEncoding(mappedServerEncoding);
/* 1288 */         setUseUnicode(true);
/*      */       } catch (UnsupportedEncodingException UE) {
/* 1290 */         throw SQLError.createSQLException("The driver can not map the character encoding '" + getEncoding() + "' that your server is using " + "to a character encoding your JVM understands. You " + "can specify this mapping manually by adding \"useUnicode=true\" " + "as well as \"characterEncoding=[an_encoding_your_jvm_understands]\" " + "to your JDBC URL.", "0S100", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkTransactionIsolationLevel()
/*      */     throws SQLException
/*      */   {
/* 1310 */     String txIsolationName = null;
/*      */ 
/* 1312 */     if (versionMeetsMinimum(4, 0, 3))
/* 1313 */       txIsolationName = "tx_isolation";
/*      */     else {
/* 1315 */       txIsolationName = "transaction_isolation";
/*      */     }
/*      */ 
/* 1318 */     String s = (String)this.serverVariables.get(txIsolationName);
/*      */ 
/* 1320 */     if (s != null) {
/* 1321 */       Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */ 
/* 1323 */       if (intTI != null)
/* 1324 */         this.isolationLevel = intTI.intValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void abortInternal()
/*      */     throws SQLException
/*      */   {
/* 1336 */     if (this.io != null) {
/*      */       try {
/* 1338 */         this.io.forceClose();
/*      */       }
/*      */       catch (Throwable t) {
/*      */       }
/* 1342 */       this.io = null;
/*      */     }
/*      */ 
/* 1345 */     this.isClosed = true;
/*      */   }
/*      */ 
/*      */   private void cleanup(Throwable whyCleanedUp)
/*      */   {
/*      */     try
/*      */     {
/* 1358 */       if ((this.io != null) && (!isClosed()))
/* 1359 */         realClose(false, false, false, whyCleanedUp);
/* 1360 */       else if (this.io != null) {
/* 1361 */         this.io.forceClose();
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*      */     }
/*      */ 
/* 1368 */     this.isClosed = true;
/*      */   }
/*      */ 
/*      */   public void clearHasTriedMaster() {
/* 1372 */     this.hasTriedMasterFlag = false;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 1397 */     return clientPrepareStatement(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 1407 */     java.sql.PreparedStatement pStmt = clientPrepareStatement(sql);
/*      */ 
/* 1409 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */ 
/* 1412 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 1430 */     return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
/*      */   }
/*      */ 
/*      */   protected java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded)
/*      */     throws SQLException
/*      */   {
/* 1438 */     checkClosed();
/*      */ 
/* 1440 */     String nativeSql = (processEscapeCodesIfNeeded) && (getProcessEscapeCodesForPrepStmts()) ? nativeSQL(sql) : sql;
/*      */ 
/* 1442 */     PreparedStatement pStmt = null;
/*      */ 
/* 1444 */     if (getCachePreparedStatements()) {
/* 1445 */       synchronized (this.cachedPreparedStatementParams) {
/* 1446 */         PreparedStatement.ParseInfo pStmtInfo = (PreparedStatement.ParseInfo)this.cachedPreparedStatementParams.get(nativeSql);
/*      */ 
/* 1449 */         if (pStmtInfo == null) {
/* 1450 */           pStmt = PreparedStatement.getInstance(this, nativeSql, this.database);
/*      */ 
/* 1453 */           PreparedStatement.ParseInfo parseInfo = pStmt.getParseInfo();
/*      */ 
/* 1455 */           if (parseInfo.statementLength < getPreparedStatementCacheSqlLimit()) {
/* 1456 */             if (this.cachedPreparedStatementParams.size() >= getPreparedStatementCacheSize()) {
/* 1457 */               Iterator oldestIter = this.cachedPreparedStatementParams.keySet().iterator();
/*      */ 
/* 1459 */               long lruTime = 9223372036854775807L;
/* 1460 */               String oldestSql = null;
/*      */ 
/* 1462 */               while (oldestIter.hasNext()) {
/* 1463 */                 String sqlKey = (String)oldestIter.next();
/* 1464 */                 PreparedStatement.ParseInfo lruInfo = (PreparedStatement.ParseInfo)this.cachedPreparedStatementParams.get(sqlKey);
/*      */ 
/* 1467 */                 if (lruInfo.lastUsed < lruTime) {
/* 1468 */                   lruTime = lruInfo.lastUsed;
/* 1469 */                   oldestSql = sqlKey;
/*      */                 }
/*      */               }
/*      */ 
/* 1473 */               if (oldestSql != null) {
/* 1474 */                 this.cachedPreparedStatementParams.remove(oldestSql);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1479 */             this.cachedPreparedStatementParams.put(nativeSql, pStmt.getParseInfo());
/*      */           }
/*      */         }
/*      */         else {
/* 1483 */           pStmtInfo.lastUsed = System.currentTimeMillis();
/* 1484 */           pStmt = new PreparedStatement(this, nativeSql, this.database, pStmtInfo);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1489 */     pStmt = PreparedStatement.getInstance(this, nativeSql, this.database);
/*      */ 
/* 1493 */     pStmt.setResultSetType(resultSetType);
/* 1494 */     pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 1496 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 1505 */     PreparedStatement pStmt = (PreparedStatement)clientPrepareStatement(sql);
/*      */ 
/* 1507 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */ 
/* 1511 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 1519 */     PreparedStatement pStmt = (PreparedStatement)clientPrepareStatement(sql);
/*      */ 
/* 1521 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */ 
/* 1525 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 1531 */     return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/* 1547 */     if (this.connectionLifecycleInterceptors != null) {
/* 1548 */       new IterateBlock(this.connectionLifecycleInterceptors.iterator()) {
/*      */         void forEach(Object each) throws SQLException {
/* 1550 */           ((ConnectionLifecycleInterceptor)each).close();
/*      */         }
/*      */       }
/* 1548 */       .doForAll();
/*      */     }
/*      */ 
/* 1555 */     realClose(true, true, false, null);
/*      */   }
/*      */ 
/*      */   private void closeAllOpenStatements()
/*      */     throws SQLException
/*      */   {
/* 1565 */     SQLException postponedException = null;
/*      */ 
/* 1567 */     if (this.openStatements != null) {
/* 1568 */       List currentlyOpenStatements = new ArrayList();
/*      */ 
/* 1572 */       Iterator iter = this.openStatements.keySet().iterator();
/* 1573 */       while (iter.hasNext()) {
/* 1574 */         currentlyOpenStatements.add(iter.next());
/*      */       }
/*      */ 
/* 1577 */       int numStmts = currentlyOpenStatements.size();
/*      */ 
/* 1579 */       for (int i = 0; i < numStmts; i++) {
/* 1580 */         StatementImpl stmt = (StatementImpl)currentlyOpenStatements.get(i);
/*      */         try
/*      */         {
/* 1583 */           stmt.realClose(false, true);
/*      */         } catch (SQLException sqlEx) {
/* 1585 */           postponedException = sqlEx;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1590 */       if (postponedException != null)
/* 1591 */         throw postponedException;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void closeStatement(java.sql.Statement stmt)
/*      */   {
/* 1597 */     if (stmt != null) {
/*      */       try {
/* 1599 */         stmt.close();
/*      */       }
/*      */       catch (SQLException sqlEx)
/*      */       {
/*      */       }
/* 1604 */       stmt = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void commit()
/*      */     throws SQLException
/*      */   {
/* 1623 */     synchronized (getMutex()) {
/* 1624 */       checkClosed();
/*      */       try
/*      */       {
/* 1627 */         if (this.connectionLifecycleInterceptors != null) {
/* 1628 */           IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */           {
/*      */             void forEach(Object each) throws SQLException {
/* 1631 */               if (!((ConnectionLifecycleInterceptor)each).commit())
/* 1632 */                 this.stopIterating = true;
/*      */             }
/*      */           };
/* 1637 */           iter.doForAll();
/*      */ 
/* 1639 */           if (!iter.fullIteration())
/*      */           {
/* 1670 */             this.needsPing = getReconnectAtTxEnd(); return;
/*      */           }
/*      */         }
/* 1645 */         if ((this.autoCommit) && (!getRelaxAutoCommit()))
/* 1646 */           throw SQLError.createSQLException("Can't call commit when autocommit=true", getExceptionInterceptor());
/* 1647 */         if (this.transactionsSupported) {
/* 1648 */           if ((getUseLocalTransactionState()) && (versionMeetsMinimum(5, 0, 0)) && 
/* 1649 */             (!this.io.inTransactionOnServer()))
/*      */           {
/* 1670 */             this.needsPing = getReconnectAtTxEnd(); return;
/*      */           }
/* 1654 */           execSQL(null, "commit", -1, null, 1003, 1007, false, this.database, null, false);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException sqlException)
/*      */       {
/* 1661 */         if ("08S01".equals(sqlException.getSQLState()))
/*      */         {
/* 1663 */           throw SQLError.createSQLException("Communications link failure during commit(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 1668 */         throw sqlException;
/*      */       } finally {
/* 1670 */         this.needsPing = getReconnectAtTxEnd();
/*      */       }
/*      */ 
/* 1673 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void configureCharsetProperties()
/*      */     throws SQLException
/*      */   {
/* 1684 */     if (getEncoding() != null)
/*      */     {
/*      */       try
/*      */       {
/* 1688 */         String testString = "abc";
/* 1689 */         testString.getBytes(getEncoding());
/*      */       }
/*      */       catch (UnsupportedEncodingException UE) {
/* 1692 */         String oldEncoding = getEncoding();
/*      */ 
/* 1694 */         setEncoding(CharsetMapping.getJavaEncodingForMysqlEncoding(oldEncoding, this));
/*      */ 
/* 1697 */         if (getEncoding() == null) {
/* 1698 */           throw SQLError.createSQLException("Java does not support the MySQL character encoding  encoding '" + oldEncoding + "'.", "01S00", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1705 */           String testString = "abc";
/* 1706 */           testString.getBytes(getEncoding());
/*      */         } catch (UnsupportedEncodingException encodingEx) {
/* 1708 */           throw SQLError.createSQLException("Unsupported character encoding '" + getEncoding() + "'.", "01S00", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean configureClientCharacterSet(boolean dontCheckServerMatch)
/*      */     throws SQLException
/*      */   {
/* 1730 */     String realJavaEncoding = getEncoding();
/* 1731 */     boolean characterSetAlreadyConfigured = false;
/*      */     try
/*      */     {
/* 1734 */       if (versionMeetsMinimum(4, 1, 0)) {
/* 1735 */         characterSetAlreadyConfigured = true;
/*      */ 
/* 1737 */         setUseUnicode(true);
/*      */ 
/* 1739 */         configureCharsetProperties();
/* 1740 */         realJavaEncoding = getEncoding();
/*      */         try
/*      */         {
/* 1748 */           if ((this.props != null) && (this.props.getProperty("com.mysql.jdbc.faultInjection.serverCharsetIndex") != null)) {
/* 1749 */             this.io.serverCharsetIndex = Integer.parseInt(this.props.getProperty("com.mysql.jdbc.faultInjection.serverCharsetIndex"));
/*      */           }
/*      */ 
/* 1754 */           String serverEncodingToSet = CharsetMapping.INDEX_TO_CHARSET[this.io.serverCharsetIndex];
/*      */ 
/* 1757 */           if ((serverEncodingToSet == null) || (serverEncodingToSet.length() == 0)) {
/* 1758 */             if (realJavaEncoding != null)
/*      */             {
/* 1760 */               setEncoding(realJavaEncoding);
/*      */             }
/* 1762 */             else throw SQLError.createSQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000", getExceptionInterceptor());
/*      */ 
/*      */           }
/*      */ 
/* 1771 */           if ((versionMeetsMinimum(4, 1, 0)) && ("ISO8859_1".equalsIgnoreCase(serverEncodingToSet)))
/*      */           {
/* 1773 */             serverEncodingToSet = "Cp1252";
/*      */           }
/*      */ 
/* 1776 */           setEncoding(serverEncodingToSet);
/*      */         }
/*      */         catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 1779 */           if (realJavaEncoding != null)
/*      */           {
/* 1781 */             setEncoding(realJavaEncoding);
/*      */           }
/* 1783 */           else throw SQLError.createSQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000", getExceptionInterceptor());
/*      */ 
/*      */         }
/*      */ 
/* 1791 */         if (getEncoding() == null)
/*      */         {
/* 1793 */           setEncoding("ISO8859_1");
/*      */         }
/*      */ 
/* 1800 */         if (getUseUnicode()) {
/* 1801 */           if (realJavaEncoding != null)
/*      */           {
/* 1807 */             if ((realJavaEncoding.equalsIgnoreCase("UTF-8")) || (realJavaEncoding.equalsIgnoreCase("UTF8")))
/*      */             {
/* 1811 */               if ((!getUseOldUTF8Behavior()) && (
/* 1812 */                 (dontCheckServerMatch) || (!characterSetNamesMatches("utf8")))) {
/* 1813 */                 execSQL(null, "SET NAMES utf8", -1, null, 1003, 1007, false, this.database, null, false);
/*      */               }
/*      */ 
/* 1820 */               setEncoding(realJavaEncoding);
/*      */             } else {
/* 1822 */               String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(realJavaEncoding.toUpperCase(Locale.ENGLISH), this);
/*      */ 
/* 1837 */               if (mysqlEncodingName != null)
/*      */               {
/* 1839 */                 if ((dontCheckServerMatch) || (!characterSetNamesMatches(mysqlEncodingName))) {
/* 1840 */                   execSQL(null, "SET NAMES " + mysqlEncodingName, -1, null, 1003, 1007, false, this.database, null, false);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 1851 */               setEncoding(realJavaEncoding);
/*      */             }
/* 1853 */           } else if (getEncoding() != null)
/*      */           {
/* 1857 */             String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(getEncoding().toUpperCase(Locale.ENGLISH), this);
/*      */ 
/* 1861 */             if ((dontCheckServerMatch) || (!characterSetNamesMatches(mysqlEncodingName))) {
/* 1862 */               execSQL(null, "SET NAMES " + mysqlEncodingName, -1, null, 1003, 1007, false, this.database, null, false);
/*      */             }
/*      */ 
/* 1868 */             realJavaEncoding = getEncoding();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1879 */         String onServer = null;
/* 1880 */         boolean isNullOnServer = false;
/*      */ 
/* 1882 */         if (this.serverVariables != null) {
/* 1883 */           onServer = (String)this.serverVariables.get("character_set_results");
/*      */ 
/* 1885 */           isNullOnServer = (onServer == null) || ("NULL".equalsIgnoreCase(onServer)) || (onServer.length() == 0);
/*      */         }
/*      */ 
/* 1888 */         if (getCharacterSetResults() == null)
/*      */         {
/* 1895 */           if (!isNullOnServer) {
/* 1896 */             execSQL(null, "SET character_set_results = NULL", -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 1901 */             if (!this.usingCachedConfig) {
/* 1902 */               this.serverVariables.put("jdbc.local.character_set_results", null);
/*      */             }
/*      */           }
/* 1905 */           else if (!this.usingCachedConfig) {
/* 1906 */             this.serverVariables.put("jdbc.local.character_set_results", onServer);
/*      */           }
/*      */         }
/*      */         else {
/* 1910 */           String charsetResults = getCharacterSetResults();
/* 1911 */           String mysqlEncodingName = null;
/*      */ 
/* 1913 */           if (("UTF-8".equalsIgnoreCase(charsetResults)) || ("UTF8".equalsIgnoreCase(charsetResults)))
/*      */           {
/* 1915 */             mysqlEncodingName = "utf8";
/*      */           }
/* 1917 */           else mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(charsetResults.toUpperCase(Locale.ENGLISH), this);
/*      */ 
/* 1926 */           if (!mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_results")))
/*      */           {
/* 1928 */             StringBuffer setBuf = new StringBuffer("SET character_set_results = ".length() + mysqlEncodingName.length());
/*      */ 
/* 1931 */             setBuf.append("SET character_set_results = ").append(mysqlEncodingName);
/*      */ 
/* 1934 */             execSQL(null, setBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 1939 */             if (!this.usingCachedConfig) {
/* 1940 */               this.serverVariables.put("jdbc.local.character_set_results", mysqlEncodingName);
/*      */             }
/*      */ 
/*      */           }
/* 1944 */           else if (!this.usingCachedConfig) {
/* 1945 */             this.serverVariables.put("jdbc.local.character_set_results", onServer);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1950 */         if (getConnectionCollation() != null) {
/* 1951 */           StringBuffer setBuf = new StringBuffer("SET collation_connection = ".length() + getConnectionCollation().length());
/*      */ 
/* 1954 */           setBuf.append("SET collation_connection = ").append(getConnectionCollation());
/*      */ 
/* 1957 */           execSQL(null, setBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1964 */         realJavaEncoding = getEncoding();
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1972 */       setEncoding(realJavaEncoding);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1980 */       CharsetEncoder enc = Charset.forName(getEncoding()).newEncoder();
/* 1981 */       CharBuffer cbuf = CharBuffer.allocate(1);
/* 1982 */       ByteBuffer bbuf = ByteBuffer.allocate(1);
/*      */ 
/* 1984 */       cbuf.put("¥");
/* 1985 */       cbuf.position(0);
/* 1986 */       enc.encode(cbuf, bbuf, true);
/* 1987 */       if (bbuf.get(0) == 92) {
/* 1988 */         this.requiresEscapingEncoder = true;
/*      */       } else {
/* 1990 */         cbuf.clear();
/* 1991 */         bbuf.clear();
/*      */ 
/* 1993 */         cbuf.put("₩");
/* 1994 */         cbuf.position(0);
/* 1995 */         enc.encode(cbuf, bbuf, true);
/* 1996 */         if (bbuf.get(0) == 92)
/* 1997 */           this.requiresEscapingEncoder = true;
/*      */       }
/*      */     }
/*      */     catch (UnsupportedCharsetException ucex)
/*      */     {
/*      */       try {
/* 2003 */         byte[] bbuf = new String("¥").getBytes(getEncoding());
/* 2004 */         if (bbuf[0] == 92) {
/* 2005 */           this.requiresEscapingEncoder = true;
/*      */         } else {
/* 2007 */           bbuf = new String("₩").getBytes(getEncoding());
/* 2008 */           if (bbuf[0] == 92)
/* 2009 */             this.requiresEscapingEncoder = true;
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException ueex) {
/* 2013 */         throw SQLError.createSQLException("Unable to use encoding: " + getEncoding(), "S1000", ueex, getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2019 */     return characterSetAlreadyConfigured;
/*      */   }
/*      */ 
/*      */   private void configureTimezone()
/*      */     throws SQLException
/*      */   {
/* 2030 */     String configuredTimeZoneOnServer = (String)this.serverVariables.get("timezone");
/*      */ 
/* 2033 */     if (configuredTimeZoneOnServer == null) {
/* 2034 */       configuredTimeZoneOnServer = (String)this.serverVariables.get("time_zone");
/*      */ 
/* 2037 */       if ("SYSTEM".equalsIgnoreCase(configuredTimeZoneOnServer)) {
/* 2038 */         configuredTimeZoneOnServer = (String)this.serverVariables.get("system_time_zone");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2043 */     String canoncicalTimezone = getServerTimezone();
/*      */ 
/* 2045 */     if (((getUseTimezone()) || (!getUseLegacyDatetimeCode())) && (configuredTimeZoneOnServer != null))
/*      */     {
/* 2047 */       if ((canoncicalTimezone == null) || (StringUtils.isEmptyOrWhitespaceOnly(canoncicalTimezone))) {
/*      */         try {
/* 2049 */           canoncicalTimezone = TimeUtil.getCanoncialTimezone(configuredTimeZoneOnServer, getExceptionInterceptor());
/*      */ 
/* 2052 */           if (canoncicalTimezone == null) {
/* 2053 */             throw SQLError.createSQLException("Can't map timezone '" + configuredTimeZoneOnServer + "' to " + " canonical timezone.", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (IllegalArgumentException iae)
/*      */         {
/* 2059 */           throw SQLError.createSQLException(iae.getMessage(), "S1000", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2064 */       canoncicalTimezone = getServerTimezone();
/*      */     }
/*      */ 
/* 2067 */     if ((canoncicalTimezone != null) && (canoncicalTimezone.length() > 0)) {
/* 2068 */       this.serverTimezoneTZ = TimeZone.getTimeZone(canoncicalTimezone);
/*      */ 
/* 2075 */       if ((!canoncicalTimezone.equalsIgnoreCase("GMT")) && (this.serverTimezoneTZ.getID().equals("GMT")))
/*      */       {
/* 2077 */         throw SQLError.createSQLException("No timezone mapping entry for '" + canoncicalTimezone + "'", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2082 */       if ("GMT".equalsIgnoreCase(this.serverTimezoneTZ.getID()))
/* 2083 */         this.isServerTzUTC = true;
/*      */       else
/* 2085 */         this.isServerTzUTC = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void createInitialHistogram(long[] breakpoints, long lowerBound, long upperBound)
/*      */   {
/* 2093 */     double bucketSize = (upperBound - lowerBound) / 20.0D * 1.25D;
/*      */ 
/* 2095 */     if (bucketSize < 1.0D) {
/* 2096 */       bucketSize = 1.0D;
/*      */     }
/*      */ 
/* 2099 */     for (int i = 0; i < 20; i++) {
/* 2100 */       breakpoints[i] = lowerBound;
/* 2101 */       lowerBound = ()(lowerBound + bucketSize);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void createNewIO(boolean isForReconnect)
/*      */     throws SQLException
/*      */   {
/* 2124 */     synchronized (this.mutex) {
/* 2125 */       Properties mergedProps = exposeAsProperties(this.props);
/*      */ 
/* 2127 */       long queriesIssuedFailedOverCopy = this.queriesIssuedFailedOver;
/* 2128 */       this.queriesIssuedFailedOver = 0L;
/*      */       try
/*      */       {
/* 2131 */         if ((!getHighAvailability()) && (!this.failedOver)) {
/* 2132 */           boolean connectionGood = false;
/* 2133 */           Exception connectionNotEstablishedBecause = null;
/*      */ 
/* 2135 */           int hostIndex = 0;
/*      */ 
/* 2143 */           if (getRoundRobinLoadBalance()) {
/* 2144 */             hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList);
/*      */           }
/*      */ 
/* 2148 */           for (; hostIndex < this.hostListSize; hostIndex++)
/*      */           {
/* 2150 */             if (hostIndex == 0) {
/* 2151 */               this.hasTriedMasterFlag = true;
/*      */             }
/*      */             try
/*      */             {
/* 2155 */               String newHostPortPair = (String)this.hostList.get(hostIndex);
/*      */ 
/* 2158 */               int newPort = 3306;
/*      */ 
/* 2160 */               String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(newHostPortPair);
/*      */ 
/* 2162 */               String newHost = hostPortPair[0];
/*      */ 
/* 2164 */               if ((newHost == null) || (StringUtils.isEmptyOrWhitespaceOnly(newHost))) {
/* 2165 */                 newHost = "localhost";
/*      */               }
/*      */ 
/* 2168 */               if (hostPortPair[1] != null) {
/*      */                 try {
/* 2170 */                   newPort = Integer.parseInt(hostPortPair[1]);
/*      */                 }
/*      */                 catch (NumberFormatException nfe) {
/* 2173 */                   throw SQLError.createSQLException("Illegal connection port value '" + hostPortPair[1] + "'", "01S00", getExceptionInterceptor());
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2181 */               this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), this, getSocketTimeout(), this.largeRowSizeThreshold.getValueAsInt());
/*      */ 
/* 2186 */               this.io.doHandshake(this.user, this.password, this.database);
/*      */ 
/* 2188 */               this.connectionId = this.io.getThreadId();
/* 2189 */               this.isClosed = false;
/*      */ 
/* 2192 */               boolean oldAutoCommit = getAutoCommit();
/* 2193 */               int oldIsolationLevel = this.isolationLevel;
/* 2194 */               boolean oldReadOnly = isReadOnly();
/* 2195 */               String oldCatalog = getCatalog();
/*      */ 
/* 2197 */               this.io.setStatementInterceptors(this.statementInterceptors);
/*      */ 
/* 2202 */               initializePropsFromServer();
/*      */ 
/* 2204 */               if (isForReconnect)
/*      */               {
/* 2206 */                 setAutoCommit(oldAutoCommit);
/*      */ 
/* 2208 */                 if (this.hasIsolationLevels) {
/* 2209 */                   setTransactionIsolation(oldIsolationLevel);
/*      */                 }
/*      */ 
/* 2212 */                 setCatalog(oldCatalog);
/*      */               }
/*      */ 
/* 2215 */               if (hostIndex != 0) {
/* 2216 */                 setFailedOverState();
/* 2217 */                 queriesIssuedFailedOverCopy = 0L;
/*      */               } else {
/* 2219 */                 this.failedOver = false;
/* 2220 */                 queriesIssuedFailedOverCopy = 0L;
/*      */ 
/* 2222 */                 if (this.hostListSize > 1)
/* 2223 */                   setReadOnlyInternal(false);
/*      */                 else {
/* 2225 */                   setReadOnlyInternal(oldReadOnly);
/*      */                 }
/*      */               }
/*      */ 
/* 2229 */               connectionGood = true;
/*      */             }
/*      */             catch (Exception EEE)
/*      */             {
/* 2233 */               if (this.io != null) {
/* 2234 */                 this.io.forceClose();
/*      */               }
/*      */ 
/* 2237 */               connectionNotEstablishedBecause = EEE;
/*      */ 
/* 2239 */               connectionGood = false;
/*      */ 
/* 2241 */               if ((EEE instanceof SQLException)) {
/* 2242 */                 SQLException sqlEx = (SQLException)EEE;
/*      */ 
/* 2244 */                 String sqlState = sqlEx.getSQLState();
/*      */ 
/* 2248 */                 if ((sqlState == null) || (!sqlState.equals("08S01")))
/*      */                 {
/* 2251 */                   throw sqlEx;
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2256 */               if (getRoundRobinLoadBalance()) {
/* 2257 */                 hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList) - 1;
/*      */               }
/* 2259 */               else if (this.hostListSize - 1 == hostIndex) {
/* 2260 */                 throw SQLError.createCommunicationsException(this, this.io != null ? this.io.getLastPacketSentTimeMs() : 0L, this.io != null ? this.io.getLastPacketReceivedTimeMs() : 0L, EEE, getExceptionInterceptor());
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2270 */           if (!connectionGood)
/*      */           {
/* 2272 */             SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnect"), "08001", getExceptionInterceptor());
/*      */ 
/* 2275 */             chainedEx.initCause(connectionNotEstablishedBecause);
/*      */ 
/* 2277 */             throw chainedEx;
/*      */           }
/*      */         } else {
/* 2280 */           double timeout = getInitialTimeout();
/* 2281 */           boolean connectionGood = false;
/*      */ 
/* 2283 */           Exception connectionException = null;
/*      */ 
/* 2285 */           int hostIndex = 0;
/*      */ 
/* 2287 */           if (getRoundRobinLoadBalance()) {
/* 2288 */             hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList);
/*      */           }
/*      */ 
/* 2292 */           for (; (hostIndex < this.hostListSize) && (!connectionGood); hostIndex++) {
/* 2293 */             if (hostIndex == 0) {
/* 2294 */               this.hasTriedMasterFlag = true;
/*      */             }
/*      */ 
/* 2297 */             if ((this.preferSlaveDuringFailover) && (hostIndex == 0)) {
/* 2298 */               hostIndex++;
/*      */             }
/*      */ 
/* 2301 */             int attemptCount = 0;
/*      */             while (true) if ((attemptCount < getMaxReconnects()) && (!connectionGood)) {
/*      */                 try {
/* 2304 */                   if (this.io != null) {
/* 2305 */                     this.io.forceClose();
/*      */                   }
/*      */ 
/* 2308 */                   String newHostPortPair = (String)this.hostList.get(hostIndex);
/*      */ 
/* 2311 */                   int newPort = 3306;
/*      */ 
/* 2313 */                   String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(newHostPortPair);
/*      */ 
/* 2315 */                   String newHost = hostPortPair[0];
/*      */ 
/* 2317 */                   if ((newHost == null) || (StringUtils.isEmptyOrWhitespaceOnly(newHost))) {
/* 2318 */                     newHost = "localhost";
/*      */                   }
/*      */ 
/* 2321 */                   if (hostPortPair[1] != null) {
/*      */                     try {
/* 2323 */                       newPort = Integer.parseInt(hostPortPair[1]);
/*      */                     }
/*      */                     catch (NumberFormatException nfe) {
/* 2326 */                       throw SQLError.createSQLException("Illegal connection port value '" + hostPortPair[1] + "'", "01S00", getExceptionInterceptor());
/*      */                     }
/*      */ 
/*      */                   }
/*      */ 
/* 2334 */                   this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), this, getSocketTimeout(), this.largeRowSizeThreshold.getValueAsInt());
/*      */ 
/* 2338 */                   this.io.doHandshake(this.user, this.password, this.database);
/*      */ 
/* 2340 */                   pingInternal(false, 0);
/* 2341 */                   this.connectionId = this.io.getThreadId();
/* 2342 */                   this.isClosed = false;
/*      */ 
/* 2345 */                   boolean oldAutoCommit = getAutoCommit();
/* 2346 */                   int oldIsolationLevel = this.isolationLevel;
/* 2347 */                   boolean oldReadOnly = isReadOnly();
/* 2348 */                   String oldCatalog = getCatalog();
/*      */ 
/* 2350 */                   this.io.setStatementInterceptors(this.statementInterceptors);
/*      */ 
/* 2355 */                   initializePropsFromServer();
/*      */ 
/* 2357 */                   if (isForReconnect)
/*      */                   {
/* 2359 */                     setAutoCommit(oldAutoCommit);
/*      */ 
/* 2361 */                     if (this.hasIsolationLevels) {
/* 2362 */                       setTransactionIsolation(oldIsolationLevel);
/*      */                     }
/*      */ 
/* 2365 */                     setCatalog(oldCatalog);
/*      */                   }
/*      */ 
/* 2368 */                   connectionGood = true;
/*      */ 
/* 2370 */                   if (hostIndex != 0) {
/* 2371 */                     setFailedOverState();
/* 2372 */                     queriesIssuedFailedOverCopy = 0L;
/*      */                   } else {
/* 2374 */                     this.failedOver = false;
/* 2375 */                     queriesIssuedFailedOverCopy = 0L;
/*      */ 
/* 2377 */                     if (this.hostListSize > 1)
/* 2378 */                       setReadOnlyInternal(false);
/*      */                     else {
/* 2380 */                       setReadOnlyInternal(oldReadOnly);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Exception IE)
/*      */                 {
/* 2386 */                   connectionException = EEE;
/* 2387 */                   connectionGood = false;
/*      */ 
/* 2390 */                   if (getRoundRobinLoadBalance()) {
/* 2391 */                     hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList) - 1;
/*      */                   }
/*      */ 
/* 2396 */                   if (!connectionGood)
/*      */                   {
/* 2400 */                     if (attemptCount > 0)
/*      */                       try {
/* 2402 */                         Thread.sleep(()timeout * 1000L);
/*      */                       }
/*      */                       catch (InterruptedException IE)
/*      */                       {
/*      */                       }
/* 2302 */                     attemptCount++; continue;
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */ 
/*      */           }
/*      */ 
/* 2410 */           if (!connectionGood)
/*      */           {
/* 2412 */             SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnectWithRetries", new Object[] { new Integer(getMaxReconnects()) }), "08001", getExceptionInterceptor());
/*      */ 
/* 2416 */             chainedEx.initCause(connectionException);
/*      */ 
/* 2418 */             throw chainedEx;
/*      */           }
/*      */         }
/*      */ 
/* 2422 */         if ((getParanoid()) && (!getHighAvailability()) && (this.hostListSize <= 1))
/*      */         {
/* 2424 */           this.password = null;
/* 2425 */           this.user = null;
/*      */         }
/*      */ 
/* 2428 */         if (isForReconnect)
/*      */         {
/* 2432 */           Iterator statementIter = this.openStatements.values().iterator();
/*      */ 
/* 2444 */           Stack serverPreparedStatements = null;
/*      */ 
/* 2446 */           while (statementIter.hasNext()) {
/* 2447 */             Object statementObj = statementIter.next();
/*      */ 
/* 2449 */             if ((statementObj instanceof ServerPreparedStatement)) {
/* 2450 */               if (serverPreparedStatements == null) {
/* 2451 */                 serverPreparedStatements = new Stack();
/*      */               }
/*      */ 
/* 2454 */               serverPreparedStatements.add(statementObj);
/*      */             }
/*      */           }
/*      */ 
/* 2458 */           if (serverPreparedStatements != null) {
/* 2459 */             while (!serverPreparedStatements.isEmpty())
/* 2460 */               ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 2466 */         this.queriesIssuedFailedOver = queriesIssuedFailedOverCopy;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void createPreparedStatementCaches() {
/* 2472 */     int cacheSize = getPreparedStatementCacheSize();
/*      */ 
/* 2474 */     this.cachedPreparedStatementParams = new HashMap(cacheSize);
/*      */ 
/* 2476 */     if (getUseServerPreparedStmts()) {
/* 2477 */       this.serverSideStatementCheckCache = new LRUCache(cacheSize);
/*      */ 
/* 2479 */       this.serverSideStatementCache = new LRUCache(cacheSize) {
/*      */         protected boolean removeEldestEntry(Map.Entry eldest) {
/* 2481 */           if (this.maxElements <= 1) {
/* 2482 */             return false;
/*      */           }
/*      */ 
/* 2485 */           boolean removeIt = super.removeEldestEntry(eldest);
/*      */ 
/* 2487 */           if (removeIt) {
/* 2488 */             ServerPreparedStatement ps = (ServerPreparedStatement)eldest.getValue();
/*      */ 
/* 2490 */             ps.isCached = false;
/* 2491 */             ps.setClosed(false);
/*      */             try
/*      */             {
/* 2494 */               ps.close();
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/*      */             }
/*      */           }
/* 2500 */           return removeIt;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement()
/*      */     throws SQLException
/*      */   {
/* 2516 */     return createStatement(1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 2534 */     checkClosed();
/*      */ 
/* 2536 */     StatementImpl stmt = new StatementImpl(this, this.database);
/* 2537 */     stmt.setResultSetType(resultSetType);
/* 2538 */     stmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 2540 */     return stmt;
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 2549 */     if ((getPedantic()) && 
/* 2550 */       (resultSetHoldability != 1)) {
/* 2551 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2557 */     return createStatement(resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   protected void dumpTestcaseQuery(String query) {
/* 2561 */     System.err.println(query);
/*      */   }
/*      */ 
/*      */   protected Connection duplicate() throws SQLException {
/* 2565 */     return new ConnectionImpl(this.origHostToConnectTo, this.origPortToConnectTo, this.props, this.origDatabaseToConnectTo, this.myURL);
/*      */   }
/*      */ 
/*      */   ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata)
/*      */     throws SQLException
/*      */   {
/* 2619 */     return execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata, false);
/*      */   }
/*      */ 
/*      */   ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/* 2634 */     synchronized (this.mutex) {
/* 2635 */       long queryStartTime = 0L;
/*      */ 
/* 2637 */       int endOfQueryPacketPosition = 0;
/*      */ 
/* 2639 */       if (packet != null) {
/* 2640 */         endOfQueryPacketPosition = packet.getPosition();
/*      */       }
/*      */ 
/* 2643 */       if (getGatherPerformanceMetrics()) {
/* 2644 */         queryStartTime = System.currentTimeMillis();
/*      */       }
/*      */ 
/* 2647 */       this.lastQueryFinishedTime = 0L;
/*      */ 
/* 2649 */       if ((this.failedOver) && (this.autoCommit) && (!isBatch) && 
/* 2650 */         (shouldFallBack()) && (!this.executingFailoverReconnect)) {
/*      */         try {
/* 2652 */           this.executingFailoverReconnect = true;
/*      */ 
/* 2654 */           createNewIO(true);
/*      */ 
/* 2656 */           String connectedHost = this.io.getHost();
/*      */ 
/* 2658 */           if ((connectedHost != null) && (this.hostList.get(0).equals(connectedHost)))
/*      */           {
/* 2660 */             this.failedOver = false;
/* 2661 */             this.queriesIssuedFailedOver = 0L;
/* 2662 */             setReadOnlyInternal(false);
/*      */           }
/*      */         } finally {
/* 2665 */           this.executingFailoverReconnect = false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2670 */       if (((getHighAvailability()) || (this.failedOver)) && ((this.autoCommit) || (getAutoReconnectForPools())) && (this.needsPing) && (!isBatch))
/*      */       {
/*      */         try
/*      */         {
/* 2674 */           pingInternal(false, 0);
/*      */ 
/* 2676 */           this.needsPing = false;
/*      */         } catch (Exception Ex) {
/* 2678 */           createNewIO(true);
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 2683 */         if (packet == null) {
/* 2684 */           encoding = null;
/*      */ 
/* 2686 */           if (getUseUnicode()) {
/* 2687 */             encoding = getEncoding();
/*      */           }
/*      */ 
/* 2690 */           ResultSetInternalMethods localResultSetInternalMethods = this.io.sqlQueryDirect(callingStatement, sql, encoding, null, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata); jsr 241; return localResultSetInternalMethods;
/*      */         }
/*      */ 
/* 2696 */         String encoding = this.io.sqlQueryDirect(callingStatement, null, null, packet, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata); jsr 207; return encoding;
/*      */       }
/*      */       catch (SQLException sqlE)
/*      */       {
/* 2703 */         if (getDumpQueriesOnException()) {
/* 2704 */           String extractedSql = extractSqlFromPacket(sql, packet, endOfQueryPacketPosition);
/*      */ 
/* 2706 */           StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/* 2708 */           messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */ 
/* 2710 */           messageBuf.append(extractedSql);
/*      */ 
/* 2712 */           sqlE = appendMessageToException(sqlE, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */ 
/* 2715 */         if ((getHighAvailability()) || (this.failedOver)) {
/* 2716 */           this.needsPing = true;
/*      */         } else {
/* 2718 */           String sqlState = sqlE.getSQLState();
/*      */ 
/* 2720 */           if ((sqlState != null) && (sqlState.equals("08S01")))
/*      */           {
/* 2723 */             cleanup(sqlE);
/*      */           }
/*      */         }
/*      */ 
/* 2727 */         throw sqlE;
/*      */       } catch (Exception ex) {
/* 2729 */         if ((getHighAvailability()) || (this.failedOver))
/* 2730 */           this.needsPing = true;
/* 2731 */         else if ((ex instanceof IOException)) {
/* 2732 */           cleanup(ex);
/*      */         }
/*      */ 
/* 2735 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnexpectedException"), "S1000", getExceptionInterceptor());
/*      */ 
/* 2738 */         sqlEx.initCause(ex);
/*      */ 
/* 2740 */         throw sqlEx;
/*      */       } finally {
/* 2742 */         jsr 6; } localObject3 = returnAddress; if (getMaintainTimeStats()) {
/* 2743 */         this.lastQueryFinishedTime = System.currentTimeMillis();
/*      */       }
/*      */ 
/* 2746 */       if (this.failedOver) {
/* 2747 */         this.queriesIssuedFailedOver += 1L;
/*      */       }
/*      */ 
/* 2750 */       if (getGatherPerformanceMetrics()) {
/* 2751 */         long queryTime = System.currentTimeMillis() - queryStartTime;
/*      */ 
/* 2754 */         registerQueryExecutionTime(queryTime); } ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition)
/*      */     throws SQLException
/*      */   {
/* 2764 */     String extractedSql = null;
/*      */ 
/* 2766 */     if (possibleSqlQuery != null) {
/* 2767 */       if (possibleSqlQuery.length() > getMaxQuerySizeToLog()) {
/* 2768 */         StringBuffer truncatedQueryBuf = new StringBuffer(possibleSqlQuery.substring(0, getMaxQuerySizeToLog()));
/*      */ 
/* 2770 */         truncatedQueryBuf.append(Messages.getString("MysqlIO.25"));
/* 2771 */         extractedSql = truncatedQueryBuf.toString();
/*      */       } else {
/* 2773 */         extractedSql = possibleSqlQuery;
/*      */       }
/*      */     }
/*      */ 
/* 2777 */     if (extractedSql == null)
/*      */     {
/* 2781 */       int extractPosition = endOfQueryPacketPosition;
/*      */ 
/* 2783 */       boolean truncated = false;
/*      */ 
/* 2785 */       if (endOfQueryPacketPosition > getMaxQuerySizeToLog()) {
/* 2786 */         extractPosition = getMaxQuerySizeToLog();
/* 2787 */         truncated = true;
/*      */       }
/*      */ 
/* 2790 */       extractedSql = new String(queryPacket.getByteBuffer(), 5, extractPosition - 5);
/*      */ 
/* 2793 */       if (truncated) {
/* 2794 */         extractedSql = extractedSql + Messages.getString("MysqlIO.25");
/*      */       }
/*      */     }
/*      */ 
/* 2798 */     return extractedSql;
/*      */   }
/*      */ 
/*      */   protected void finalize()
/*      */     throws Throwable
/*      */   {
/* 2809 */     cleanup(null);
/*      */ 
/* 2811 */     super.finalize();
/*      */   }
/*      */ 
/*      */   protected StringBuffer generateConnectionCommentBlock(StringBuffer buf) {
/* 2815 */     buf.append("/* conn id ");
/* 2816 */     buf.append(getId());
/* 2817 */     buf.append(" clock: ");
/* 2818 */     buf.append(System.currentTimeMillis());
/* 2819 */     buf.append(" */ ");
/*      */ 
/* 2821 */     return buf;
/*      */   }
/*      */ 
/*      */   public int getActiveStatementCount()
/*      */   {
/* 2827 */     if (this.openStatements != null) {
/* 2828 */       synchronized (this.openStatements) {
/* 2829 */         return this.openStatements.size();
/*      */       }
/*      */     }
/*      */ 
/* 2833 */     return 0;
/*      */   }
/*      */ 
/*      */   public boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/* 2845 */     return this.autoCommit;
/*      */   }
/*      */ 
/*      */   protected Calendar getCalendarInstanceForSessionOrNew()
/*      */   {
/* 2853 */     if (getDynamicCalendars()) {
/* 2854 */       return Calendar.getInstance();
/*      */     }
/*      */ 
/* 2857 */     return getSessionLockedCalendar();
/*      */   }
/*      */ 
/*      */   public String getCatalog()
/*      */     throws SQLException
/*      */   {
/* 2872 */     return this.database;
/*      */   }
/*      */ 
/*      */   protected String getCharacterSetMetadata()
/*      */   {
/* 2879 */     return this.characterSetMetadata;
/*      */   }
/*      */ 
/*      */   SingleByteCharsetConverter getCharsetConverter(String javaEncodingName)
/*      */     throws SQLException
/*      */   {
/* 2892 */     if (javaEncodingName == null) {
/* 2893 */       return null;
/*      */     }
/*      */ 
/* 2896 */     if (this.usePlatformCharsetConverters) {
/* 2897 */       return null;
/*      */     }
/*      */ 
/* 2901 */     SingleByteCharsetConverter converter = null;
/*      */ 
/* 2903 */     synchronized (this.charsetConverterMap) {
/* 2904 */       Object asObject = this.charsetConverterMap.get(javaEncodingName);
/*      */ 
/* 2907 */       if (asObject == CHARSET_CONVERTER_NOT_AVAILABLE_MARKER) {
/* 2908 */         return null;
/*      */       }
/*      */ 
/* 2911 */       converter = (SingleByteCharsetConverter)asObject;
/*      */ 
/* 2913 */       if (converter == null) {
/*      */         try {
/* 2915 */           converter = SingleByteCharsetConverter.getInstance(javaEncodingName, this);
/*      */ 
/* 2918 */           if (converter == null) {
/* 2919 */             this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */           }
/*      */           else
/* 2922 */             this.charsetConverterMap.put(javaEncodingName, converter);
/*      */         }
/*      */         catch (UnsupportedEncodingException unsupEncEx) {
/* 2925 */           this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */ 
/* 2928 */           converter = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2933 */     return converter;
/*      */   }
/*      */ 
/*      */   protected String getCharsetNameForIndex(int charsetIndex)
/*      */     throws SQLException
/*      */   {
/* 2948 */     String charsetName = null;
/*      */ 
/* 2950 */     if (getUseOldUTF8Behavior()) {
/* 2951 */       return getEncoding();
/*      */     }
/*      */ 
/* 2954 */     if (charsetIndex != -1) {
/*      */       try {
/* 2956 */         charsetName = this.indexToCharsetMapping[charsetIndex];
/*      */ 
/* 2958 */         if (("sjis".equalsIgnoreCase(charsetName)) || ("MS932".equalsIgnoreCase(charsetName)))
/*      */         {
/* 2961 */           if (CharsetMapping.isAliasForSjis(getEncoding()))
/* 2962 */             charsetName = getEncoding();
/*      */         }
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 2966 */         throw SQLError.createSQLException("Unknown character set index for field '" + charsetIndex + "' received from server.", "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2973 */       if (charsetName == null)
/* 2974 */         charsetName = getEncoding();
/*      */     }
/*      */     else {
/* 2977 */       charsetName = getEncoding();
/*      */     }
/*      */ 
/* 2980 */     return charsetName;
/*      */   }
/*      */ 
/*      */   protected TimeZone getDefaultTimeZone()
/*      */   {
/* 2989 */     return this.defaultTimeZone;
/*      */   }
/*      */ 
/*      */   protected String getErrorMessageEncoding() {
/* 2993 */     return this.errorMessageEncoding;
/*      */   }
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/* 3000 */     return 2;
/*      */   }
/*      */ 
/*      */   long getId() {
/* 3004 */     return this.connectionId;
/*      */   }
/*      */ 
/*      */   public long getIdleFor()
/*      */   {
/* 3016 */     if (this.lastQueryFinishedTime == 0L) {
/* 3017 */       return 0L;
/*      */     }
/*      */ 
/* 3020 */     long now = System.currentTimeMillis();
/* 3021 */     long idleTime = now - this.lastQueryFinishedTime;
/*      */ 
/* 3023 */     return idleTime;
/*      */   }
/*      */ 
/*      */   protected MysqlIO getIO()
/*      */     throws SQLException
/*      */   {
/* 3034 */     if ((this.io == null) || (this.isClosed)) {
/* 3035 */       throw SQLError.createSQLException("Operation not allowed on closed connection", "08003", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3040 */     return this.io;
/*      */   }
/*      */ 
/*      */   public Log getLog()
/*      */     throws SQLException
/*      */   {
/* 3052 */     return this.log;
/*      */   }
/*      */ 
/*      */   protected int getMaxBytesPerChar(String javaCharsetName)
/*      */     throws SQLException
/*      */   {
/* 3058 */     String charset = CharsetMapping.getMysqlEncodingForJavaEncoding(javaCharsetName, this);
/*      */ 
/* 3061 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 3062 */       Map mapToCheck = null;
/*      */ 
/* 3064 */       if (!getUseDynamicCharsetInfo()) {
/* 3065 */         mapToCheck = CharsetMapping.STATIC_CHARSET_TO_NUM_BYTES_MAP;
/*      */       } else {
/* 3067 */         mapToCheck = this.charsetToNumBytesMap;
/*      */ 
/* 3069 */         synchronized (this.charsetToNumBytesMap) {
/* 3070 */           if (this.charsetToNumBytesMap.isEmpty())
/*      */           {
/* 3072 */             java.sql.Statement stmt = null;
/* 3073 */             ResultSet rs = null;
/*      */             try
/*      */             {
/* 3076 */               stmt = getMetadataSafeStatement();
/*      */ 
/* 3078 */               rs = stmt.executeQuery("SHOW CHARACTER SET");
/*      */ 
/* 3080 */               while (rs.next()) {
/* 3081 */                 this.charsetToNumBytesMap.put(rs.getString("Charset"), Constants.integerValueOf(rs.getInt("Maxlen")));
/*      */               }
/*      */ 
/* 3085 */               rs.close();
/* 3086 */               rs = null;
/*      */ 
/* 3088 */               stmt.close();
/*      */ 
/* 3090 */               stmt = null;
/*      */             } finally {
/* 3092 */               if (rs != null) {
/* 3093 */                 rs.close();
/* 3094 */                 rs = null;
/*      */               }
/*      */ 
/* 3097 */               if (stmt != null) {
/* 3098 */                 stmt.close();
/* 3099 */                 stmt = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3106 */       Integer mbPerChar = (Integer)mapToCheck.get(charset);
/*      */ 
/* 3108 */       if (mbPerChar != null) {
/* 3109 */         return mbPerChar.intValue();
/*      */       }
/*      */ 
/* 3112 */       return 1;
/*      */     }
/*      */ 
/* 3115 */     return 1;
/*      */   }
/*      */ 
/*      */   public java.sql.DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 3129 */     return getMetaData(true, true);
/*      */   }
/*      */ 
/*      */   private java.sql.DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
/* 3133 */     if (checkClosed) {
/* 3134 */       checkClosed();
/*      */     }
/*      */ 
/* 3137 */     return DatabaseMetaData.getInstance(this, this.database, checkForInfoSchema);
/*      */   }
/*      */ 
/*      */   protected java.sql.Statement getMetadataSafeStatement() throws SQLException {
/* 3141 */     java.sql.Statement stmt = createStatement();
/*      */ 
/* 3143 */     if (stmt.getMaxRows() != 0) {
/* 3144 */       stmt.setMaxRows(0);
/*      */     }
/*      */ 
/* 3147 */     stmt.setEscapeProcessing(false);
/*      */ 
/* 3149 */     if (stmt.getFetchSize() != 0) {
/* 3150 */       stmt.setFetchSize(0);
/*      */     }
/*      */ 
/* 3153 */     return stmt;
/*      */   }
/*      */ 
/*      */   Object getMutex()
/*      */     throws SQLException
/*      */   {
/* 3164 */     if (this.io == null) {
/* 3165 */       throwConnectionClosedException();
/*      */     }
/*      */ 
/* 3168 */     reportMetricsIfNeeded();
/*      */ 
/* 3170 */     return this.mutex;
/*      */   }
/*      */ 
/*      */   int getNetBufferLength()
/*      */   {
/* 3179 */     return this.netBufferLength;
/*      */   }
/*      */ 
/*      */   public String getServerCharacterEncoding()
/*      */   {
/* 3188 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 3189 */       return (String)this.serverVariables.get("character_set_server");
/*      */     }
/* 3191 */     return (String)this.serverVariables.get("character_set");
/*      */   }
/*      */ 
/*      */   int getServerMajorVersion()
/*      */   {
/* 3196 */     return this.io.getServerMajorVersion();
/*      */   }
/*      */ 
/*      */   int getServerMinorVersion() {
/* 3200 */     return this.io.getServerMinorVersion();
/*      */   }
/*      */ 
/*      */   int getServerSubMinorVersion() {
/* 3204 */     return this.io.getServerSubMinorVersion();
/*      */   }
/*      */ 
/*      */   public TimeZone getServerTimezoneTZ()
/*      */   {
/* 3213 */     return this.serverTimezoneTZ;
/*      */   }
/*      */ 
/*      */   String getServerVariable(String variableName)
/*      */   {
/* 3218 */     if (this.serverVariables != null) {
/* 3219 */       return (String)this.serverVariables.get(variableName);
/*      */     }
/*      */ 
/* 3222 */     return null;
/*      */   }
/*      */ 
/*      */   String getServerVersion() {
/* 3226 */     return this.io.getServerVersion();
/*      */   }
/*      */ 
/*      */   protected Calendar getSessionLockedCalendar()
/*      */   {
/* 3231 */     return this.sessionCalendar;
/*      */   }
/*      */ 
/*      */   public int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 3243 */     if ((this.hasIsolationLevels) && (!getUseLocalSessionState())) {
/* 3244 */       java.sql.Statement stmt = null;
/* 3245 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 3248 */         stmt = getMetadataSafeStatement();
/*      */ 
/* 3250 */         String query = null;
/*      */ 
/* 3252 */         int offset = 0;
/*      */ 
/* 3254 */         if (versionMeetsMinimum(4, 0, 3)) {
/* 3255 */           query = "SELECT @@session.tx_isolation";
/* 3256 */           offset = 1;
/*      */         } else {
/* 3258 */           query = "SHOW VARIABLES LIKE 'transaction_isolation'";
/* 3259 */           offset = 2;
/*      */         }
/*      */ 
/* 3262 */         rs = stmt.executeQuery(query);
/*      */ 
/* 3264 */         if (rs.next()) {
/* 3265 */           String s = rs.getString(offset);
/*      */           int i;
/* 3267 */           if (s != null) {
/* 3268 */             Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */ 
/* 3271 */             if (intTI != null) {
/* 3272 */               i = intTI.intValue(); jsr 66;
/*      */             }
/*      */           }
/*      */ 
/* 3276 */           throw SQLError.createSQLException("Could not map transaction isolation '" + s + " to a valid JDBC level.", "S1000", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 3282 */         throw SQLError.createSQLException("Could not retrieve transaction isolation level from server", "S1000", getExceptionInterceptor());
/*      */       }
/*      */       finally
/*      */       {
/* 3287 */         if (rs != null) {
/*      */           try {
/* 3289 */             rs.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */ 
/* 3295 */           rs = null;
/*      */         }
/*      */ 
/* 3298 */         if (stmt != null) {
/*      */           try {
/* 3300 */             stmt.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */ 
/* 3306 */           stmt = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3311 */     return this.isolationLevel;
/*      */   }
/*      */ 
/*      */   public synchronized Map getTypeMap()
/*      */     throws SQLException
/*      */   {
/* 3323 */     if (this.typeMap == null) {
/* 3324 */       this.typeMap = new HashMap();
/*      */     }
/*      */ 
/* 3327 */     return this.typeMap;
/*      */   }
/*      */ 
/*      */   String getURL() {
/* 3331 */     return this.myURL;
/*      */   }
/*      */ 
/*      */   String getUser() {
/* 3335 */     return this.user;
/*      */   }
/*      */ 
/*      */   protected Calendar getUtcCalendar() {
/* 3339 */     return this.utcCalendar;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 3352 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean hasSameProperties(Connection c) {
/* 3356 */     return this.props.equals(c.getProperties());
/*      */   }
/*      */ 
/*      */   public Properties getProperties() {
/* 3360 */     return this.props;
/*      */   }
/*      */ 
/*      */   public boolean hasTriedMaster() {
/* 3364 */     return this.hasTriedMasterFlag;
/*      */   }
/*      */ 
/*      */   protected void incrementNumberOfPreparedExecutes() {
/* 3368 */     if (getGatherPerformanceMetrics()) {
/* 3369 */       this.numberOfPreparedExecutes += 1L;
/*      */ 
/* 3374 */       this.numberOfQueriesIssued += 1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void incrementNumberOfPrepares() {
/* 3379 */     if (getGatherPerformanceMetrics())
/* 3380 */       this.numberOfPrepares += 1L;
/*      */   }
/*      */ 
/*      */   protected void incrementNumberOfResultSetsCreated()
/*      */   {
/* 3385 */     if (getGatherPerformanceMetrics())
/* 3386 */       this.numberOfResultSetsCreated += 1L;
/*      */   }
/*      */ 
/*      */   private void initializeDriverProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 3401 */     initializeProperties(info);
/*      */ 
/* 3403 */     String exceptionInterceptorClasses = getExceptionInterceptors();
/*      */ 
/* 3405 */     if ((exceptionInterceptorClasses != null) && (!"".equals(exceptionInterceptorClasses))) {
/* 3406 */       this.exceptionInterceptor = new ExceptionInterceptorChain(exceptionInterceptorClasses);
/* 3407 */       this.exceptionInterceptor.init(this, info);
/*      */     }
/*      */ 
/* 3410 */     this.usePlatformCharsetConverters = getUseJvmCharsetConverters();
/*      */ 
/* 3412 */     this.log = LogFactory.getLogger(getLogger(), "MySQL", getExceptionInterceptor());
/*      */ 
/* 3414 */     if ((getProfileSql()) || (getUseUsageAdvisor())) {
/* 3415 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this);
/*      */     }
/*      */ 
/* 3418 */     if (getCachePreparedStatements()) {
/* 3419 */       createPreparedStatementCaches();
/*      */     }
/*      */ 
/* 3422 */     if ((getNoDatetimeStringSync()) && (getUseTimezone())) {
/* 3423 */       throw SQLError.createSQLException("Can't enable noDatetimeSync and useTimezone configuration properties at the same time", "01S00", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3429 */     if (getCacheCallableStatements()) {
/* 3430 */       this.parsedCallableStatementCache = new LRUCache(getCallableStatementCacheSize());
/*      */     }
/*      */ 
/* 3434 */     if (getAllowMultiQueries()) {
/* 3435 */       setCacheResultSetMetadata(false);
/*      */     }
/*      */ 
/* 3438 */     if (getCacheResultSetMetadata())
/* 3439 */       this.resultSetMetadataCache = new LRUCache(getMetadataCacheSize());
/*      */   }
/*      */ 
/*      */   private void initializePropsFromServer()
/*      */     throws SQLException
/*      */   {
/* 3454 */     String connectionInterceptorClasses = getConnectionLifecycleInterceptors();
/*      */ 
/* 3456 */     this.connectionLifecycleInterceptors = null;
/*      */ 
/* 3458 */     if (connectionInterceptorClasses != null) {
/* 3459 */       this.connectionLifecycleInterceptors = Util.loadExtensions(this, this.props, connectionInterceptorClasses, "Connection.badLifecycleInterceptor", getExceptionInterceptor());
/*      */ 
/* 3463 */       Iterator iter = this.connectionLifecycleInterceptors.iterator();
/*      */ 
/* 3465 */       new IterateBlock(iter)
/*      */       {
/*      */         void forEach(Object each) throws SQLException {
/* 3468 */           ((ConnectionLifecycleInterceptor)each).init(ConnectionImpl.this, ConnectionImpl.this.props);
/*      */         }
/*      */       }
/* 3465 */       .doForAll();
/*      */     }
/*      */ 
/* 3473 */     setSessionVariables();
/*      */ 
/* 3479 */     if (!versionMeetsMinimum(4, 1, 0)) {
/* 3480 */       setTransformedBitIsBoolean(false);
/*      */     }
/*      */ 
/* 3483 */     this.parserKnowsUnicode = versionMeetsMinimum(4, 1, 0);
/*      */ 
/* 3488 */     if ((getUseServerPreparedStmts()) && (versionMeetsMinimum(4, 1, 0))) {
/* 3489 */       this.useServerPreparedStmts = true;
/*      */ 
/* 3491 */       if ((versionMeetsMinimum(5, 0, 0)) && (!versionMeetsMinimum(5, 0, 3))) {
/* 3492 */         this.useServerPreparedStmts = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3498 */     this.serverVariables.clear();
/*      */ 
/* 3503 */     if (versionMeetsMinimum(3, 21, 22)) {
/* 3504 */       loadServerVariables();
/*      */ 
/* 3506 */       if (versionMeetsMinimum(5, 0, 2))
/* 3507 */         this.autoIncrementIncrement = getServerVariableAsInt("auto_increment_increment", 1);
/*      */       else {
/* 3509 */         this.autoIncrementIncrement = 1;
/*      */       }
/*      */ 
/* 3512 */       buildCollationMapping();
/*      */ 
/* 3514 */       LicenseConfiguration.checkLicenseType(this.serverVariables);
/*      */ 
/* 3516 */       String lowerCaseTables = (String)this.serverVariables.get("lower_case_table_names");
/*      */ 
/* 3519 */       this.lowerCaseTableNames = (("on".equalsIgnoreCase(lowerCaseTables)) || ("1".equalsIgnoreCase(lowerCaseTables)) || ("2".equalsIgnoreCase(lowerCaseTables)));
/*      */ 
/* 3523 */       this.storesLowerCaseTableName = (("1".equalsIgnoreCase(lowerCaseTables)) || ("on".equalsIgnoreCase(lowerCaseTables)));
/*      */ 
/* 3526 */       configureTimezone();
/*      */ 
/* 3528 */       if (this.serverVariables.containsKey("max_allowed_packet")) {
/* 3529 */         int serverMaxAllowedPacket = getServerVariableAsInt("max_allowed_packet", -1);
/*      */ 
/* 3531 */         if ((serverMaxAllowedPacket != -1) && ((serverMaxAllowedPacket < getMaxAllowedPacket()) || (getMaxAllowedPacket() <= 0)))
/*      */         {
/* 3533 */           setMaxAllowedPacket(serverMaxAllowedPacket);
/* 3534 */         } else if ((serverMaxAllowedPacket == -1) && (getMaxAllowedPacket() == -1)) {
/* 3535 */           setMaxAllowedPacket(65535);
/*      */         }
/* 3537 */         int preferredBlobSendChunkSize = getBlobSendChunkSize();
/*      */ 
/* 3539 */         int allowedBlobSendChunkSize = Math.min(preferredBlobSendChunkSize, getMaxAllowedPacket()) - 8192 - 11;
/*      */ 
/* 3544 */         setBlobSendChunkSize(String.valueOf(allowedBlobSendChunkSize));
/*      */       }
/*      */ 
/* 3547 */       if (this.serverVariables.containsKey("net_buffer_length")) {
/* 3548 */         this.netBufferLength = getServerVariableAsInt("net_buffer_length", 16384);
/*      */       }
/*      */ 
/* 3551 */       checkTransactionIsolationLevel();
/*      */ 
/* 3553 */       if (!versionMeetsMinimum(4, 1, 0)) {
/* 3554 */         checkServerEncoding();
/*      */       }
/*      */ 
/* 3557 */       this.io.checkForCharsetMismatch();
/*      */ 
/* 3559 */       if (this.serverVariables.containsKey("sql_mode")) {
/* 3560 */         int sqlMode = 0;
/*      */ 
/* 3562 */         String sqlModeAsString = (String)this.serverVariables.get("sql_mode");
/*      */         try
/*      */         {
/* 3565 */           sqlMode = Integer.parseInt(sqlModeAsString);
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/* 3569 */           sqlMode = 0;
/*      */ 
/* 3571 */           if (sqlModeAsString != null) {
/* 3572 */             if (sqlModeAsString.indexOf("ANSI_QUOTES") != -1) {
/* 3573 */               sqlMode |= 4;
/*      */             }
/*      */ 
/* 3576 */             if (sqlModeAsString.indexOf("NO_BACKSLASH_ESCAPES") != -1) {
/* 3577 */               this.noBackslashEscapes = true;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 3582 */         if ((sqlMode & 0x4) > 0)
/* 3583 */           this.useAnsiQuotes = true;
/*      */         else {
/* 3585 */           this.useAnsiQuotes = false;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3590 */     this.errorMessageEncoding = CharsetMapping.getCharacterEncodingForErrorMessages(this);
/*      */ 
/* 3594 */     boolean overrideDefaultAutocommit = isAutoCommitNonDefaultOnServer();
/*      */ 
/* 3596 */     configureClientCharacterSet(false);
/*      */ 
/* 3598 */     if (versionMeetsMinimum(3, 23, 15)) {
/* 3599 */       this.transactionsSupported = true;
/*      */ 
/* 3601 */       if (!overrideDefaultAutocommit) {
/* 3602 */         setAutoCommit(true);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 3607 */       this.transactionsSupported = false;
/*      */     }
/*      */ 
/* 3611 */     if (versionMeetsMinimum(3, 23, 36))
/* 3612 */       this.hasIsolationLevels = true;
/*      */     else {
/* 3614 */       this.hasIsolationLevels = false;
/*      */     }
/*      */ 
/* 3617 */     this.hasQuotedIdentifiers = versionMeetsMinimum(3, 23, 6);
/*      */ 
/* 3619 */     this.io.resetMaxBuf();
/*      */ 
/* 3629 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 3630 */       String characterSetResultsOnServerMysql = (String)this.serverVariables.get("jdbc.local.character_set_results");
/*      */ 
/* 3633 */       if ((characterSetResultsOnServerMysql == null) || (StringUtils.startsWithIgnoreCaseAndWs(characterSetResultsOnServerMysql, "NULL")) || (characterSetResultsOnServerMysql.length() == 0))
/*      */       {
/* 3637 */         String defaultMetadataCharsetMysql = (String)this.serverVariables.get("character_set_system");
/*      */ 
/* 3639 */         String defaultMetadataCharset = null;
/*      */ 
/* 3641 */         if (defaultMetadataCharsetMysql != null) {
/* 3642 */           defaultMetadataCharset = CharsetMapping.getJavaEncodingForMysqlEncoding(defaultMetadataCharsetMysql, this);
/*      */         }
/*      */         else
/*      */         {
/* 3646 */           defaultMetadataCharset = "UTF-8";
/*      */         }
/*      */ 
/* 3649 */         this.characterSetMetadata = defaultMetadataCharset;
/*      */       } else {
/* 3651 */         this.characterSetResultsOnServer = CharsetMapping.getJavaEncodingForMysqlEncoding(characterSetResultsOnServerMysql, this);
/*      */ 
/* 3654 */         this.characterSetMetadata = this.characterSetResultsOnServer;
/*      */       }
/*      */     } else {
/* 3657 */       this.characterSetMetadata = getEncoding();
/*      */     }
/*      */ 
/* 3664 */     if ((versionMeetsMinimum(4, 1, 0)) && (!versionMeetsMinimum(4, 1, 10)) && (getAllowMultiQueries()))
/*      */     {
/* 3667 */       if (isQueryCacheEnabled()) {
/* 3668 */         setAllowMultiQueries(false);
/*      */       }
/*      */     }
/*      */ 
/* 3672 */     if ((versionMeetsMinimum(5, 0, 0)) && ((getUseLocalTransactionState()) || (getElideSetAutoCommits())) && (isQueryCacheEnabled()) && (!versionMeetsMinimum(6, 0, 10)))
/*      */     {
/* 3677 */       setUseLocalTransactionState(false);
/* 3678 */       setElideSetAutoCommits(false);
/*      */     }
/*      */ 
/* 3685 */     setupServerForTruncationChecks();
/*      */   }
/*      */ 
/*      */   private boolean isQueryCacheEnabled() {
/* 3689 */     return ("ON".equalsIgnoreCase((String)this.serverVariables.get("query_cache_type"))) && (!"0".equalsIgnoreCase((String)this.serverVariables.get("query_cache_size")));
/*      */   }
/*      */ 
/*      */   private int getServerVariableAsInt(String variableName, int fallbackValue)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3698 */       return Integer.parseInt((String)this.serverVariables.get(variableName));
/*      */     }
/*      */     catch (NumberFormatException nfe) {
/* 3701 */       getLog().logWarn(Messages.getString("Connection.BadValueInServerVariables", new Object[] { variableName, this.serverVariables.get(variableName), new Integer(fallbackValue) }));
/*      */     }
/*      */ 
/* 3704 */     return fallbackValue;
/*      */   }
/*      */ 
/*      */   private boolean isAutoCommitNonDefaultOnServer()
/*      */     throws SQLException
/*      */   {
/* 3717 */     boolean overrideDefaultAutocommit = false;
/*      */ 
/* 3719 */     String initConnectValue = (String)this.serverVariables.get("init_connect");
/*      */ 
/* 3722 */     if ((versionMeetsMinimum(4, 1, 2)) && (initConnectValue != null) && (initConnectValue.length() > 0))
/*      */     {
/* 3724 */       if (!getElideSetAutoCommits())
/*      */       {
/* 3726 */         ResultSet rs = null;
/* 3727 */         java.sql.Statement stmt = null;
/*      */         try
/*      */         {
/* 3730 */           stmt = getMetadataSafeStatement();
/*      */ 
/* 3732 */           rs = stmt.executeQuery("SELECT @@session.autocommit");
/*      */ 
/* 3734 */           if (rs.next()) {
/* 3735 */             this.autoCommit = rs.getBoolean(1);
/* 3736 */             if (this.autoCommit != true)
/* 3737 */               overrideDefaultAutocommit = true;
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 3742 */           if (rs != null) {
/*      */             try {
/* 3744 */               rs.close();
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/*      */             }
/*      */           }
/* 3750 */           if (stmt != null)
/*      */             try {
/* 3752 */               stmt.close();
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/*      */             }
/*      */         }
/*      */       }
/* 3759 */       else if (getIO().isSetNeededForAutoCommitMode(true))
/*      */       {
/* 3761 */         this.autoCommit = false;
/* 3762 */         overrideDefaultAutocommit = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3767 */     return overrideDefaultAutocommit;
/*      */   }
/*      */ 
/*      */   protected boolean isClientTzUTC() {
/* 3771 */     return this.isClientTzUTC;
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/* 3780 */     return this.isClosed;
/*      */   }
/*      */ 
/*      */   protected boolean isCursorFetchEnabled() throws SQLException {
/* 3784 */     return (versionMeetsMinimum(5, 0, 2)) && (getUseCursorFetch());
/*      */   }
/*      */ 
/*      */   public boolean isInGlobalTx() {
/* 3788 */     return this.isInGlobalTx;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isMasterConnection()
/*      */   {
/* 3799 */     return !this.failedOver;
/*      */   }
/*      */ 
/*      */   public boolean isNoBackslashEscapesSet()
/*      */   {
/* 3809 */     return this.noBackslashEscapes;
/*      */   }
/*      */ 
/*      */   boolean isReadInfoMsgEnabled() {
/* 3813 */     return this.readInfoMsg;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 3826 */     return this.readOnly;
/*      */   }
/*      */ 
/*      */   protected boolean isRunningOnJDK13() {
/* 3830 */     return this.isRunningOnJDK13;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isSameResource(Connection otherConnection) {
/* 3834 */     if (otherConnection == null) {
/* 3835 */       return false;
/*      */     }
/*      */ 
/* 3838 */     boolean directCompare = true;
/*      */ 
/* 3840 */     String otherHost = ((ConnectionImpl)otherConnection).origHostToConnectTo;
/* 3841 */     String otherOrigDatabase = ((ConnectionImpl)otherConnection).origDatabaseToConnectTo;
/* 3842 */     String otherCurrentCatalog = ((ConnectionImpl)otherConnection).database;
/*      */ 
/* 3844 */     if (!nullSafeCompare(otherHost, this.origHostToConnectTo))
/* 3845 */       directCompare = false;
/* 3846 */     else if ((otherHost != null) && (otherHost.indexOf(',') == -1) && (otherHost.indexOf(':') == -1))
/*      */     {
/* 3849 */       directCompare = ((ConnectionImpl)otherConnection).origPortToConnectTo == this.origPortToConnectTo;
/*      */     }
/*      */ 
/* 3853 */     if (directCompare) {
/* 3854 */       if (!nullSafeCompare(otherOrigDatabase, this.origDatabaseToConnectTo)) { directCompare = false;
/* 3855 */         directCompare = false;
/* 3856 */       } else if (!nullSafeCompare(otherCurrentCatalog, this.database)) {
/* 3857 */         directCompare = false;
/*      */       }
/*      */     }
/*      */ 
/* 3861 */     if (directCompare) {
/* 3862 */       return true;
/*      */     }
/*      */ 
/* 3866 */     String otherResourceId = ((ConnectionImpl)otherConnection).getResourceId();
/* 3867 */     String myResourceId = getResourceId();
/*      */ 
/* 3869 */     if ((otherResourceId != null) || (myResourceId != null)) {
/* 3870 */       directCompare = nullSafeCompare(otherResourceId, myResourceId);
/*      */ 
/* 3872 */       if (directCompare) {
/* 3873 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 3877 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean isServerTzUTC() {
/* 3881 */     return this.isServerTzUTC;
/*      */   }
/*      */ 
/*      */   private void loadServerVariables()
/*      */     throws SQLException
/*      */   {
/* 3895 */     if (getCacheServerConfiguration()) {
/* 3896 */       synchronized (serverConfigByUrl) {
/* 3897 */         Map cachedVariableMap = (Map)serverConfigByUrl.get(getURL());
/*      */ 
/* 3899 */         if (cachedVariableMap != null) {
/* 3900 */           this.serverVariables = cachedVariableMap;
/* 3901 */           this.usingCachedConfig = true;
/*      */ 
/* 3903 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3908 */     java.sql.Statement stmt = null;
/* 3909 */     ResultSet results = null;
/*      */     try
/*      */     {
/* 3912 */       stmt = getMetadataSafeStatement();
/*      */ 
/* 3914 */       String version = this.dbmd.getDriverVersion();
/*      */ 
/* 3916 */       if ((version != null) && (version.indexOf('*') != -1)) {
/* 3917 */         StringBuffer buf = new StringBuffer(version.length() + 10);
/*      */ 
/* 3919 */         for (int i = 0; i < version.length(); i++) {
/* 3920 */           char c = version.charAt(i);
/*      */ 
/* 3922 */           if (c == '*')
/* 3923 */             buf.append("[star]");
/*      */           else {
/* 3925 */             buf.append(c);
/*      */           }
/*      */         }
/*      */ 
/* 3929 */         version = buf.toString();
/*      */       }
/*      */ 
/* 3932 */       String versionComment = "/* " + version + " */";
/*      */ 
/* 3935 */       String query = versionComment + "SHOW VARIABLES";
/*      */ 
/* 3937 */       if (versionMeetsMinimum(5, 0, 3)) {
/* 3938 */         query = versionComment + "SHOW VARIABLES WHERE Variable_name ='language'" + " OR Variable_name = 'net_write_timeout'" + " OR Variable_name = 'interactive_timeout'" + " OR Variable_name = 'wait_timeout'" + " OR Variable_name = 'character_set_client'" + " OR Variable_name = 'character_set_connection'" + " OR Variable_name = 'character_set'" + " OR Variable_name = 'character_set_server'" + " OR Variable_name = 'tx_isolation'" + " OR Variable_name = 'transaction_isolation'" + " OR Variable_name = 'character_set_results'" + " OR Variable_name = 'timezone'" + " OR Variable_name = 'time_zone'" + " OR Variable_name = 'system_time_zone'" + " OR Variable_name = 'lower_case_table_names'" + " OR Variable_name = 'max_allowed_packet'" + " OR Variable_name = 'net_buffer_length'" + " OR Variable_name = 'sql_mode'" + " OR Variable_name = 'query_cache_type'" + " OR Variable_name = 'query_cache_size'" + " OR Variable_name = 'init_connect'";
/*      */       }
/*      */ 
/* 3961 */       results = stmt.executeQuery(query);
/*      */ 
/* 3963 */       while (results.next()) {
/* 3964 */         this.serverVariables.put(results.getString(1), results.getString(2));
/*      */       }
/*      */ 
/* 3968 */       if (versionMeetsMinimum(5, 0, 2)) {
/* 3969 */         results = stmt.executeQuery(versionComment + "SELECT @@session.auto_increment_increment");
/*      */ 
/* 3971 */         if (results.next()) {
/* 3972 */           this.serverVariables.put("auto_increment_increment", results.getString(1));
/*      */         }
/*      */       }
/*      */ 
/* 3976 */       if (getCacheServerConfiguration()) {
/* 3977 */         synchronized (serverConfigByUrl) {
/* 3978 */           serverConfigByUrl.put(getURL(), this.serverVariables);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/* 3984 */       throw e;
/*      */     } finally {
/* 3986 */       if (results != null) {
/*      */         try {
/* 3988 */           results.close();
/*      */         }
/*      */         catch (SQLException sqlE)
/*      */         {
/*      */         }
/*      */       }
/* 3994 */       if (stmt != null)
/*      */         try {
/* 3996 */           stmt.close();
/*      */         }
/*      */         catch (SQLException sqlE)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getAutoIncrementIncrement()
/*      */   {
/* 4007 */     return this.autoIncrementIncrement;
/*      */   }
/*      */ 
/*      */   public boolean lowerCaseTableNames()
/*      */   {
/* 4016 */     return this.lowerCaseTableNames;
/*      */   }
/*      */ 
/*      */   void maxRowsChanged(Statement stmt)
/*      */   {
/* 4026 */     synchronized (this.mutex) {
/* 4027 */       if (this.statementsUsingMaxRows == null) {
/* 4028 */         this.statementsUsingMaxRows = new HashMap();
/*      */       }
/*      */ 
/* 4031 */       this.statementsUsingMaxRows.put(stmt, stmt);
/*      */ 
/* 4033 */       this.maxRowsChanged = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String nativeSQL(String sql)
/*      */     throws SQLException
/*      */   {
/* 4050 */     if (sql == null) {
/* 4051 */       return null;
/*      */     }
/*      */ 
/* 4054 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), this);
/*      */ 
/* 4058 */     if ((escapedSqlResult instanceof String)) {
/* 4059 */       return (String)escapedSqlResult;
/*      */     }
/*      */ 
/* 4062 */     return ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */   }
/*      */ 
/*      */   private CallableStatement parseCallableStatement(String sql) throws SQLException
/*      */   {
/* 4067 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), this);
/*      */ 
/* 4070 */     boolean isFunctionCall = false;
/* 4071 */     String parsedSql = null;
/*      */ 
/* 4073 */     if ((escapedSqlResult instanceof EscapeProcessorResult)) {
/* 4074 */       parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/* 4075 */       isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
/*      */     } else {
/* 4077 */       parsedSql = (String)escapedSqlResult;
/* 4078 */       isFunctionCall = false;
/*      */     }
/*      */ 
/* 4081 */     return CallableStatement.getInstance(this, parsedSql, this.database, isFunctionCall);
/*      */   }
/*      */ 
/*      */   public boolean parserKnowsUnicode()
/*      */   {
/* 4091 */     return this.parserKnowsUnicode;
/*      */   }
/*      */ 
/*      */   public void ping()
/*      */     throws SQLException
/*      */   {
/* 4101 */     pingInternal(true, 0);
/*      */   }
/*      */ 
/*      */   protected void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException
/*      */   {
/* 4106 */     if (checkForClosedConnection) {
/* 4107 */       checkClosed();
/*      */     }
/*      */ 
/* 4110 */     long pingMillisLifetime = getSelfDestructOnPingSecondsLifetime();
/* 4111 */     int pingMaxOperations = getSelfDestructOnPingMaxOperations();
/*      */ 
/* 4113 */     if (((pingMillisLifetime > 0L) && (System.currentTimeMillis() - this.connectionCreationTimeMillis > pingMillisLifetime)) || ((pingMaxOperations > 0) && (pingMaxOperations <= this.io.getCommandCount())))
/*      */     {
/* 4117 */       close();
/*      */ 
/* 4119 */       throw SQLError.createSQLException(Messages.getString("Connection.exceededConnectionLifetime"), "08S01", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4124 */     this.io.sendCommand(14, null, null, false, null, timeoutMillis);
/*      */   }
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/* 4139 */     return prepareCall(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4160 */     if (versionMeetsMinimum(5, 0, 0)) {
/* 4161 */       CallableStatement cStmt = null;
/*      */ 
/* 4163 */       if (!getCacheCallableStatements())
/*      */       {
/* 4165 */         cStmt = parseCallableStatement(sql);
/*      */       }
/* 4167 */       else synchronized (this.parsedCallableStatementCache) {
/* 4168 */           CompoundCacheKey key = new CompoundCacheKey(getCatalog(), sql);
/*      */ 
/* 4170 */           CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get(key);
/*      */ 
/* 4173 */           if (cachedParamInfo != null) {
/* 4174 */             cStmt = CallableStatement.getInstance(this, cachedParamInfo);
/*      */           } else {
/* 4176 */             cStmt = parseCallableStatement(sql);
/*      */ 
/* 4178 */             cachedParamInfo = cStmt.paramInfo;
/*      */ 
/* 4180 */             this.parsedCallableStatementCache.put(key, cachedParamInfo);
/*      */           }
/*      */         }
/*      */ 
/*      */ 
/* 4185 */       cStmt.setResultSetType(resultSetType);
/* 4186 */       cStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 4188 */       return cStmt;
/*      */     }
/*      */ 
/* 4191 */     throw SQLError.createSQLException("Callable statements not supported.", "S1C00", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4201 */     if ((getPedantic()) && 
/* 4202 */       (resultSetHoldability != 1)) {
/* 4203 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4209 */     CallableStatement cStmt = (CallableStatement)prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */ 
/* 4212 */     return cStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 4242 */     return prepareStatement(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 4251 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4253 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */ 
/* 4256 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4276 */     checkClosed();
/*      */ 
/* 4282 */     PreparedStatement pStmt = null;
/*      */ 
/* 4284 */     boolean canServerPrepare = true;
/*      */ 
/* 4286 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */ 
/* 4288 */     if ((this.useServerPreparedStmts) && (getEmulateUnsupportedPstmts())) {
/* 4289 */       canServerPrepare = canHandleAsServerPreparedStatement(nativeSql);
/*      */     }
/*      */ 
/* 4292 */     if ((this.useServerPreparedStmts) && (canServerPrepare)) {
/* 4293 */       if (getCachePreparedStatements()) {
/* 4294 */         synchronized (this.serverSideStatementCache) {
/* 4295 */           pStmt = (ServerPreparedStatement)this.serverSideStatementCache.remove(sql);
/*      */ 
/* 4297 */           if (pStmt != null) {
/* 4298 */             ((ServerPreparedStatement)pStmt).setClosed(false);
/* 4299 */             pStmt.clearParameters();
/*      */           }
/*      */ 
/* 4302 */           if (pStmt == null)
/*      */             try {
/* 4304 */               pStmt = ServerPreparedStatement.getInstance(this, nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */ 
/* 4306 */               if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 4307 */                 ((ServerPreparedStatement)pStmt).isCached = true;
/*      */               }
/*      */ 
/* 4310 */               pStmt.setResultSetType(resultSetType);
/* 4311 */               pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */             }
/*      */             catch (SQLException sqlEx) {
/* 4314 */               if (getEmulateUnsupportedPstmts()) {
/* 4315 */                 pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */ 
/* 4317 */                 if (sql.length() < getPreparedStatementCacheSqlLimit())
/* 4318 */                   this.serverSideStatementCheckCache.put(sql, Boolean.FALSE);
/*      */               }
/*      */               else {
/* 4321 */                 throw sqlEx;
/*      */               }
/*      */             }
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 4328 */         pStmt = ServerPreparedStatement.getInstance(this, nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */ 
/* 4331 */         pStmt.setResultSetType(resultSetType);
/* 4332 */         pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 4335 */         if (getEmulateUnsupportedPstmts())
/* 4336 */           pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */         else
/* 4338 */           throw sqlEx;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 4343 */       pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */     }
/*      */ 
/* 4346 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4355 */     if ((getPedantic()) && 
/* 4356 */       (resultSetHoldability != 1)) {
/* 4357 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4363 */     return prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 4371 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4373 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */ 
/* 4377 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 4385 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4387 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */ 
/* 4391 */     return pStmt;
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason)
/*      */     throws SQLException
/*      */   {
/* 4406 */     SQLException sqlEx = null;
/*      */ 
/* 4408 */     if (isClosed()) {
/* 4409 */       return;
/*      */     }
/*      */ 
/* 4412 */     this.forceClosedReason = reason;
/*      */     try
/*      */     {
/* 4415 */       if (!skipLocalTeardown) {
/* 4416 */         if ((!getAutoCommit()) && (issueRollback)) {
/*      */           try {
/* 4418 */             rollback();
/*      */           } catch (SQLException ex) {
/* 4420 */             sqlEx = ex;
/*      */           }
/*      */         }
/*      */ 
/* 4424 */         reportMetrics();
/*      */ 
/* 4426 */         if (getUseUsageAdvisor()) {
/* 4427 */           if (!calledExplicitly) {
/* 4428 */             String message = "Connection implicitly closed by Driver. You should call Connection.close() from your code to free resources more efficiently and avoid resource leaks.";
/*      */ 
/* 4430 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */           }
/*      */ 
/* 4438 */           long connectionLifeTime = System.currentTimeMillis() - this.connectionCreationTimeMillis;
/*      */ 
/* 4441 */           if (connectionLifeTime < 500L) {
/* 4442 */             String message = "Connection lifetime of < .5 seconds. You might be un-necessarily creating short-lived connections and should investigate connection pooling to be more efficient.";
/*      */ 
/* 4444 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 4454 */           closeAllOpenStatements();
/*      */         } catch (SQLException ex) {
/* 4456 */           sqlEx = ex;
/*      */         }
/*      */ 
/* 4459 */         if (this.io != null)
/*      */           try {
/* 4461 */             this.io.quit();
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*      */           }
/*      */       }
/*      */       else {
/* 4468 */         this.io.forceClose();
/*      */       }
/*      */ 
/* 4471 */       if (this.statementInterceptors != null) {
/* 4472 */         for (int i = 0; i < this.statementInterceptors.size(); i++) {
/* 4473 */           ((StatementInterceptorV2)this.statementInterceptors.get(i)).destroy();
/*      */         }
/*      */       }
/*      */ 
/* 4477 */       if (this.exceptionInterceptor != null)
/* 4478 */         this.exceptionInterceptor.destroy();
/*      */     }
/*      */     finally {
/* 4481 */       this.openStatements = null;
/* 4482 */       this.io = null;
/* 4483 */       this.statementInterceptors = null;
/* 4484 */       this.exceptionInterceptor = null;
/* 4485 */       ProfilerEventHandlerFactory.removeInstance(this);
/*      */ 
/* 4487 */       synchronized (this) {
/* 4488 */         if (this.cancelTimer != null) {
/* 4489 */           this.cancelTimer.cancel();
/*      */         }
/*      */       }
/*      */ 
/* 4493 */       this.isClosed = true;
/*      */     }
/*      */ 
/* 4496 */     if (sqlEx != null)
/* 4497 */       throw sqlEx;
/*      */   }
/*      */ 
/*      */   protected void recachePreparedStatement(ServerPreparedStatement pstmt)
/*      */     throws SQLException
/*      */   {
/* 4503 */     if (pstmt.isPoolable())
/* 4504 */       synchronized (this.serverSideStatementCache) {
/* 4505 */         this.serverSideStatementCache.put(pstmt.originalSql, pstmt);
/*      */       }
/*      */   }
/*      */ 
/*      */   protected void registerQueryExecutionTime(long queryTimeMs)
/*      */   {
/* 4516 */     if (queryTimeMs > this.longestQueryTimeMs) {
/* 4517 */       this.longestQueryTimeMs = queryTimeMs;
/*      */ 
/* 4519 */       repartitionPerformanceHistogram();
/*      */     }
/*      */ 
/* 4522 */     addToPerformanceHistogram(queryTimeMs, 1);
/*      */ 
/* 4524 */     if (queryTimeMs < this.shortestQueryTimeMs) {
/* 4525 */       this.shortestQueryTimeMs = (queryTimeMs == 0L ? 1L : queryTimeMs);
/*      */     }
/*      */ 
/* 4528 */     this.numberOfQueriesIssued += 1L;
/*      */ 
/* 4530 */     this.totalQueryTimeMs += queryTimeMs;
/*      */   }
/*      */ 
/*      */   void registerStatement(Statement stmt)
/*      */   {
/* 4540 */     synchronized (this.openStatements) {
/* 4541 */       this.openStatements.put(stmt, stmt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void releaseSavepoint(Savepoint arg0)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound)
/*      */   {
/* 4555 */     if (this.oldHistCounts == null) {
/* 4556 */       this.oldHistCounts = new int[histCounts.length];
/* 4557 */       this.oldHistBreakpoints = new long[histBreakpoints.length];
/*      */     }
/*      */ 
/* 4560 */     System.arraycopy(histCounts, 0, this.oldHistCounts, 0, histCounts.length);
/*      */ 
/* 4562 */     System.arraycopy(histBreakpoints, 0, this.oldHistBreakpoints, 0, histBreakpoints.length);
/*      */ 
/* 4565 */     createInitialHistogram(histBreakpoints, currentLowerBound, currentUpperBound);
/*      */ 
/* 4568 */     for (int i = 0; i < 20; i++)
/* 4569 */       addToHistogram(histCounts, histBreakpoints, this.oldHistBreakpoints[i], this.oldHistCounts[i], currentLowerBound, currentUpperBound);
/*      */   }
/*      */ 
/*      */   private void repartitionPerformanceHistogram()
/*      */   {
/* 4575 */     checkAndCreatePerformanceHistogram();
/*      */ 
/* 4577 */     repartitionHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, this.shortestQueryTimeMs == 9223372036854775807L ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */ 
/*      */   private void repartitionTablesAccessedHistogram()
/*      */   {
/* 4584 */     checkAndCreateTablesAccessedHistogram();
/*      */ 
/* 4586 */     repartitionHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, this.minimumNumberTablesAccessed == 9223372036854775807L ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */ 
/*      */   private void reportMetrics()
/*      */   {
/* 4594 */     if (getGatherPerformanceMetrics()) {
/* 4595 */       StringBuffer logMessage = new StringBuffer(256);
/*      */ 
/* 4597 */       logMessage.append("** Performance Metrics Report **\n");
/* 4598 */       logMessage.append("\nLongest reported query: " + this.longestQueryTimeMs + " ms");
/*      */ 
/* 4600 */       logMessage.append("\nShortest reported query: " + this.shortestQueryTimeMs + " ms");
/*      */ 
/* 4602 */       logMessage.append("\nAverage query execution time: " + this.totalQueryTimeMs / this.numberOfQueriesIssued + " ms");
/*      */ 
/* 4606 */       logMessage.append("\nNumber of statements executed: " + this.numberOfQueriesIssued);
/*      */ 
/* 4608 */       logMessage.append("\nNumber of result sets created: " + this.numberOfResultSetsCreated);
/*      */ 
/* 4610 */       logMessage.append("\nNumber of statements prepared: " + this.numberOfPrepares);
/*      */ 
/* 4612 */       logMessage.append("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes);
/*      */ 
/* 4615 */       if (this.perfMetricsHistBreakpoints != null) {
/* 4616 */         logMessage.append("\n\n\tTiming Histogram:\n");
/* 4617 */         int maxNumPoints = 20;
/* 4618 */         int highestCount = -2147483648;
/*      */ 
/* 4620 */         for (int i = 0; i < 20; i++) {
/* 4621 */           if (this.perfMetricsHistCounts[i] > highestCount) {
/* 4622 */             highestCount = this.perfMetricsHistCounts[i];
/*      */           }
/*      */         }
/*      */ 
/* 4626 */         if (highestCount == 0) {
/* 4627 */           highestCount = 1;
/*      */         }
/*      */ 
/* 4630 */         for (int i = 0; i < 19; i++)
/*      */         {
/* 4632 */           if (i == 0) {
/* 4633 */             logMessage.append("\n\tless than " + this.perfMetricsHistBreakpoints[(i + 1)] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           }
/*      */           else
/*      */           {
/* 4637 */             logMessage.append("\n\tbetween " + this.perfMetricsHistBreakpoints[i] + " and " + this.perfMetricsHistBreakpoints[(i + 1)] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           }
/*      */ 
/* 4643 */           logMessage.append("\t");
/*      */ 
/* 4645 */           int numPointsToGraph = (int)(maxNumPoints * (this.perfMetricsHistCounts[i] / highestCount));
/*      */ 
/* 4647 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4648 */             logMessage.append("*");
/*      */           }
/*      */ 
/* 4651 */           if (this.longestQueryTimeMs < this.perfMetricsHistCounts[(i + 1)])
/*      */           {
/*      */             break;
/*      */           }
/*      */         }
/* 4656 */         if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
/* 4657 */           logMessage.append("\n\tbetween ");
/* 4658 */           logMessage.append(this.perfMetricsHistBreakpoints[18]);
/*      */ 
/* 4660 */           logMessage.append(" and ");
/* 4661 */           logMessage.append(this.perfMetricsHistBreakpoints[19]);
/*      */ 
/* 4663 */           logMessage.append(" ms: \t");
/* 4664 */           logMessage.append(this.perfMetricsHistCounts[19]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4669 */       if (this.numTablesMetricsHistBreakpoints != null) {
/* 4670 */         logMessage.append("\n\n\tTable Join Histogram:\n");
/* 4671 */         int maxNumPoints = 20;
/* 4672 */         int highestCount = -2147483648;
/*      */ 
/* 4674 */         for (int i = 0; i < 20; i++) {
/* 4675 */           if (this.numTablesMetricsHistCounts[i] > highestCount) {
/* 4676 */             highestCount = this.numTablesMetricsHistCounts[i];
/*      */           }
/*      */         }
/*      */ 
/* 4680 */         if (highestCount == 0) {
/* 4681 */           highestCount = 1;
/*      */         }
/*      */ 
/* 4684 */         for (int i = 0; i < 19; i++)
/*      */         {
/* 4686 */           if (i == 0) {
/* 4687 */             logMessage.append("\n\t" + this.numTablesMetricsHistBreakpoints[(i + 1)] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i]);
/*      */           }
/*      */           else
/*      */           {
/* 4692 */             logMessage.append("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i] + " and " + this.numTablesMetricsHistBreakpoints[(i + 1)] + " tables: \t" + this.numTablesMetricsHistCounts[i]);
/*      */           }
/*      */ 
/* 4700 */           logMessage.append("\t");
/*      */ 
/* 4702 */           int numPointsToGraph = (int)(maxNumPoints * (this.numTablesMetricsHistCounts[i] / highestCount));
/*      */ 
/* 4704 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4705 */             logMessage.append("*");
/*      */           }
/*      */ 
/* 4708 */           if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[(i + 1)])
/*      */           {
/*      */             break;
/*      */           }
/*      */         }
/* 4713 */         if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
/* 4714 */           logMessage.append("\n\tbetween ");
/* 4715 */           logMessage.append(this.numTablesMetricsHistBreakpoints[18]);
/*      */ 
/* 4717 */           logMessage.append(" and ");
/* 4718 */           logMessage.append(this.numTablesMetricsHistBreakpoints[19]);
/*      */ 
/* 4720 */           logMessage.append(" tables: ");
/* 4721 */           logMessage.append(this.numTablesMetricsHistCounts[19]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4726 */       this.log.logInfo(logMessage);
/*      */ 
/* 4728 */       this.metricsLastReportedMs = System.currentTimeMillis();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void reportMetricsIfNeeded()
/*      */   {
/* 4737 */     if ((getGatherPerformanceMetrics()) && 
/* 4738 */       (System.currentTimeMillis() - this.metricsLastReportedMs > getReportMetricsIntervalMillis()))
/* 4739 */       reportMetrics();
/*      */   }
/*      */ 
/*      */   protected void reportNumberOfTablesAccessed(int numTablesAccessed)
/*      */   {
/* 4745 */     if (numTablesAccessed < this.minimumNumberTablesAccessed) {
/* 4746 */       this.minimumNumberTablesAccessed = numTablesAccessed;
/*      */     }
/*      */ 
/* 4749 */     if (numTablesAccessed > this.maximumNumberTablesAccessed) {
/* 4750 */       this.maximumNumberTablesAccessed = numTablesAccessed;
/*      */ 
/* 4752 */       repartitionTablesAccessedHistogram();
/*      */     }
/*      */ 
/* 4755 */     addToTablesAccessedHistogram(numTablesAccessed, 1);
/*      */   }
/*      */ 
/*      */   public void resetServerState()
/*      */     throws SQLException
/*      */   {
/* 4767 */     if ((!getParanoid()) && (this.io != null) && (versionMeetsMinimum(4, 0, 6)))
/*      */     {
/* 4769 */       changeUser(this.user, this.password);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback()
/*      */     throws SQLException
/*      */   {
/* 4783 */     synchronized (getMutex()) {
/* 4784 */       checkClosed();
/*      */       try
/*      */       {
/* 4787 */         if (this.connectionLifecycleInterceptors != null) {
/* 4788 */           IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */           {
/*      */             void forEach(Object each) throws SQLException {
/* 4791 */               if (!((ConnectionLifecycleInterceptor)each).rollback())
/* 4792 */                 this.stopIterating = true;
/*      */             }
/*      */           };
/* 4797 */           iter.doForAll();
/*      */ 
/* 4799 */           if (!iter.fullIteration())
/*      */           {
/* 4829 */             this.needsPing = getReconnectAtTxEnd(); return;
/*      */           }
/*      */         }
/* 4804 */         if ((this.autoCommit) && (!getRelaxAutoCommit())) {
/* 4805 */           throw SQLError.createSQLException("Can't call rollback when autocommit=true", "08003", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 4808 */         if (this.transactionsSupported)
/*      */           try {
/* 4810 */             rollbackNoChecks();
/*      */           }
/*      */           catch (SQLException sqlEx) {
/* 4813 */             if ((getIgnoreNonTxTables()) && (sqlEx.getErrorCode() != 1196))
/*      */             {
/* 4815 */               throw sqlEx;
/*      */             }
/*      */           }
/*      */       }
/*      */       catch (SQLException sqlException) {
/* 4820 */         if ("08S01".equals(sqlException.getSQLState()))
/*      */         {
/* 4822 */           throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 4827 */         throw sqlException;
/*      */       } finally {
/* 4829 */         this.needsPing = getReconnectAtTxEnd();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback(Savepoint savepoint)
/*      */     throws SQLException
/*      */   {
/* 4839 */     if ((versionMeetsMinimum(4, 0, 14)) || (versionMeetsMinimum(4, 1, 1))) {
/* 4840 */       synchronized (getMutex()) {
/* 4841 */         checkClosed();
/*      */         try
/*      */         {
/* 4844 */           if (this.connectionLifecycleInterceptors != null) {
/* 4845 */             IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator(), savepoint) { private final Savepoint val$savepoint;
/*      */ 
/* 4848 */               void forEach(Object each) throws SQLException { if (!((ConnectionLifecycleInterceptor)each).rollback(this.val$savepoint))
/* 4849 */                   this.stopIterating = true;
/*      */               }
/*      */             };
/* 4854 */             iter.doForAll();
/*      */ 
/* 4856 */             if (!iter.fullIteration())
/*      */             {
/* 4910 */               this.needsPing = getReconnectAtTxEnd(); return;
/*      */             }
/*      */           }
/* 4861 */           StringBuffer rollbackQuery = new StringBuffer("ROLLBACK TO SAVEPOINT ");
/*      */ 
/* 4863 */           rollbackQuery.append('`');
/* 4864 */           rollbackQuery.append(savepoint.getSavepointName());
/* 4865 */           rollbackQuery.append('`');
/*      */ 
/* 4867 */           java.sql.Statement stmt = null;
/*      */           try
/*      */           {
/* 4870 */             stmt = getMetadataSafeStatement();
/*      */ 
/* 4872 */             stmt.executeUpdate(rollbackQuery.toString());
/*      */           } catch (SQLException sqlEx) {
/* 4874 */             int errno = sqlEx.getErrorCode();
/*      */ 
/* 4876 */             if (errno == 1181) {
/* 4877 */               String msg = sqlEx.getMessage();
/*      */ 
/* 4879 */               if (msg != null) {
/* 4880 */                 int indexOfError153 = msg.indexOf("153");
/*      */ 
/* 4882 */                 if (indexOfError153 != -1) {
/* 4883 */                   throw SQLError.createSQLException("Savepoint '" + savepoint.getSavepointName() + "' does not exist", "S1009", errno, getExceptionInterceptor());
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 4893 */             if ((getIgnoreNonTxTables()) && (sqlEx.getErrorCode() != 1196))
/*      */             {
/* 4895 */               throw sqlEx;
/*      */             }
/*      */ 
/* 4898 */             if ("08S01".equals(sqlEx.getSQLState()))
/*      */             {
/* 4900 */               throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */             }
/*      */ 
/* 4905 */             throw sqlEx;
/*      */           } finally {
/* 4907 */             closeStatement(stmt);
/*      */           }
/*      */         } finally {
/* 4910 */           this.needsPing = getReconnectAtTxEnd();
/*      */         }
/*      */       }
/*      */     }
/* 4914 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   private void rollbackNoChecks() throws SQLException
/*      */   {
/* 4919 */     if ((getUseLocalTransactionState()) && (versionMeetsMinimum(5, 0, 0)) && 
/* 4920 */       (!this.io.inTransactionOnServer())) {
/* 4921 */       return;
/*      */     }
/*      */ 
/* 4925 */     execSQL(null, "rollback", -1, null, 1003, 1007, false, this.database, null, false);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 4937 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */ 
/* 4939 */     return ServerPreparedStatement.getInstance(this, nativeSql, getCatalog(), 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 4949 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */ 
/* 4951 */     PreparedStatement pStmt = ServerPreparedStatement.getInstance(this, nativeSql, getCatalog(), 1003, 1007);
/*      */ 
/* 4955 */     pStmt.setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */ 
/* 4958 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4966 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */ 
/* 4968 */     return ServerPreparedStatement.getInstance(this, nativeSql, getCatalog(), resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4979 */     if ((getPedantic()) && 
/* 4980 */       (resultSetHoldability != 1)) {
/* 4981 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4987 */     return serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 4996 */     PreparedStatement pStmt = (PreparedStatement)serverPrepareStatement(sql);
/*      */ 
/* 4998 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */ 
/* 5002 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 5010 */     PreparedStatement pStmt = (PreparedStatement)serverPrepareStatement(sql);
/*      */ 
/* 5012 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */ 
/* 5016 */     return pStmt;
/*      */   }
/*      */ 
/*      */   protected boolean serverSupportsConvertFn() throws SQLException {
/* 5020 */     return versionMeetsMinimum(4, 0, 2);
/*      */   }
/*      */ 
/*      */   public void setAutoCommit(boolean autoCommitFlag)
/*      */     throws SQLException
/*      */   {
/* 5046 */     synchronized (getMutex()) {
/* 5047 */       checkClosed();
/*      */ 
/* 5049 */       if (this.connectionLifecycleInterceptors != null) {
/* 5050 */         IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator(), autoCommitFlag) { private final boolean val$autoCommitFlag;
/*      */ 
/* 5053 */           void forEach(Object each) throws SQLException { if (!((ConnectionLifecycleInterceptor)each).setAutoCommit(this.val$autoCommitFlag))
/* 5054 */               this.stopIterating = true;
/*      */           }
/*      */         };
/* 5059 */         iter.doForAll();
/*      */ 
/* 5061 */         if (!iter.fullIteration()) {
/* 5062 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 5066 */       if (getAutoReconnectForPools()) {
/* 5067 */         setHighAvailability(true);
/*      */       }
/*      */       try
/*      */       {
/* 5071 */         if (this.transactionsSupported)
/*      */         {
/* 5073 */           boolean needsSetOnServer = true;
/*      */ 
/* 5075 */           if ((getUseLocalSessionState()) && (this.autoCommit == autoCommitFlag))
/*      */           {
/* 5077 */             needsSetOnServer = false;
/* 5078 */           } else if (!getHighAvailability()) {
/* 5079 */             needsSetOnServer = getIO().isSetNeededForAutoCommitMode(autoCommitFlag);
/*      */           }
/*      */ 
/* 5090 */           this.autoCommit = autoCommitFlag;
/*      */ 
/* 5092 */           if (needsSetOnServer) {
/* 5093 */             execSQL(null, autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0", -1, null, 1003, 1007, false, this.database, null, false);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 5101 */           if ((!autoCommitFlag) && (!getRelaxAutoCommit())) {
/* 5102 */             throw SQLError.createSQLException("MySQL Versions Older than 3.23.15 do not support transactions", "08003", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 5107 */           this.autoCommit = autoCommitFlag;
/*      */         }
/*      */       } finally {
/* 5110 */         if (getAutoReconnectForPools()) {
/* 5111 */           setHighAvailability(false);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5121 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCatalog(String catalog)
/*      */     throws SQLException
/*      */   {
/* 5139 */     synchronized (getMutex()) {
/* 5140 */       checkClosed();
/*      */ 
/* 5142 */       if (catalog == null) {
/* 5143 */         throw SQLError.createSQLException("Catalog can not be null", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 5147 */       if (this.connectionLifecycleInterceptors != null) {
/* 5148 */         IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator(), catalog) { private final String val$catalog;
/*      */ 
/* 5151 */           void forEach(Object each) throws SQLException { if (!((ConnectionLifecycleInterceptor)each).setCatalog(this.val$catalog))
/* 5152 */               this.stopIterating = true;
/*      */           }
/*      */         };
/* 5157 */         iter.doForAll();
/*      */ 
/* 5159 */         if (!iter.fullIteration()) {
/* 5160 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 5164 */       if (getUseLocalSessionState()) {
/* 5165 */         if (this.lowerCaseTableNames) {
/* 5166 */           if (this.database.equalsIgnoreCase(catalog)) {
/* 5167 */             return;
/*      */           }
/*      */         }
/* 5170 */         else if (this.database.equals(catalog)) {
/* 5171 */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5176 */       String quotedId = this.dbmd.getIdentifierQuoteString();
/*      */ 
/* 5178 */       if ((quotedId == null) || (quotedId.equals(" "))) {
/* 5179 */         quotedId = "";
/*      */       }
/*      */ 
/* 5182 */       StringBuffer query = new StringBuffer("USE ");
/* 5183 */       query.append(quotedId);
/* 5184 */       query.append(catalog);
/* 5185 */       query.append(quotedId);
/*      */ 
/* 5187 */       execSQL(null, query.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 5192 */       this.database = catalog;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setFailedOver(boolean flag)
/*      */   {
/* 5201 */     if ((flag) && (getRoundRobinLoadBalance())) {
/* 5202 */       return;
/*      */     }
/*      */ 
/* 5205 */     this.failedOver = flag;
/*      */   }
/*      */ 
/*      */   private void setFailedOverState()
/*      */     throws SQLException
/*      */   {
/* 5215 */     if (getRoundRobinLoadBalance()) {
/* 5216 */       return;
/*      */     }
/*      */ 
/* 5219 */     if (getFailOverReadOnly()) {
/* 5220 */       setReadOnlyInternal(true);
/*      */     }
/*      */ 
/* 5223 */     this.queriesIssuedFailedOver = 0L;
/* 5224 */     this.failedOver = true;
/* 5225 */     this.masterFailTimeMillis = System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */   public void setHoldability(int arg0)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setInGlobalTx(boolean flag)
/*      */   {
/* 5236 */     this.isInGlobalTx = flag;
/*      */   }
/*      */ 
/*      */   public void setPreferSlaveDuringFailover(boolean flag)
/*      */   {
/* 5245 */     this.preferSlaveDuringFailover = flag;
/*      */   }
/*      */ 
/*      */   void setReadInfoMsgEnabled(boolean flag) {
/* 5249 */     this.readInfoMsg = flag;
/*      */   }
/*      */ 
/*      */   public void setReadOnly(boolean readOnlyFlag)
/*      */     throws SQLException
/*      */   {
/* 5263 */     checkClosed();
/*      */ 
/* 5267 */     if ((this.failedOver) && (getFailOverReadOnly()) && (!readOnlyFlag)) {
/* 5268 */       return;
/*      */     }
/*      */ 
/* 5271 */     setReadOnlyInternal(readOnlyFlag);
/*      */   }
/*      */ 
/*      */   protected void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
/* 5275 */     this.readOnly = readOnlyFlag;
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/* 5282 */     MysqlSavepoint savepoint = new MysqlSavepoint(getExceptionInterceptor());
/*      */ 
/* 5284 */     setSavepoint(savepoint);
/*      */ 
/* 5286 */     return savepoint;
/*      */   }
/*      */ 
/*      */   private void setSavepoint(MysqlSavepoint savepoint) throws SQLException
/*      */   {
/* 5291 */     if ((versionMeetsMinimum(4, 0, 14)) || (versionMeetsMinimum(4, 1, 1))) {
/* 5292 */       synchronized (getMutex()) {
/* 5293 */         checkClosed();
/*      */ 
/* 5295 */         StringBuffer savePointQuery = new StringBuffer("SAVEPOINT ");
/* 5296 */         savePointQuery.append('`');
/* 5297 */         savePointQuery.append(savepoint.getSavepointName());
/* 5298 */         savePointQuery.append('`');
/*      */ 
/* 5300 */         java.sql.Statement stmt = null;
/*      */         try
/*      */         {
/* 5303 */           stmt = getMetadataSafeStatement();
/*      */ 
/* 5305 */           stmt.executeUpdate(savePointQuery.toString());
/*      */         } finally {
/* 5307 */           closeStatement(stmt);
/*      */         }
/*      */       }
/*      */     }
/* 5311 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public synchronized Savepoint setSavepoint(String name)
/*      */     throws SQLException
/*      */   {
/* 5319 */     MysqlSavepoint savepoint = new MysqlSavepoint(name, getExceptionInterceptor());
/*      */ 
/* 5321 */     setSavepoint(savepoint);
/*      */ 
/* 5323 */     return savepoint;
/*      */   }
/*      */ 
/*      */   private void setSessionVariables()
/*      */     throws SQLException
/*      */   {
/* 5330 */     if ((versionMeetsMinimum(4, 0, 0)) && (getSessionVariables() != null)) {
/* 5331 */       List variablesToSet = StringUtils.split(getSessionVariables(), ",", "\"'", "\"'", false);
/*      */ 
/* 5334 */       int numVariablesToSet = variablesToSet.size();
/*      */ 
/* 5336 */       java.sql.Statement stmt = null;
/*      */       try
/*      */       {
/* 5339 */         stmt = getMetadataSafeStatement();
/*      */ 
/* 5341 */         for (int i = 0; i < numVariablesToSet; i++) {
/* 5342 */           String variableValuePair = (String)variablesToSet.get(i);
/*      */ 
/* 5344 */           if (variableValuePair.startsWith("@"))
/* 5345 */             stmt.executeUpdate("SET " + variableValuePair);
/*      */           else
/* 5347 */             stmt.executeUpdate("SET SESSION " + variableValuePair);
/*      */         }
/*      */       }
/*      */       finally {
/* 5351 */         if (stmt != null)
/* 5352 */           stmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/* 5368 */     checkClosed();
/*      */ 
/* 5370 */     if (this.hasIsolationLevels) {
/* 5371 */       String sql = null;
/*      */ 
/* 5373 */       boolean shouldSendSet = false;
/*      */ 
/* 5375 */       if (getAlwaysSendSetIsolation()) {
/* 5376 */         shouldSendSet = true;
/*      */       }
/* 5378 */       else if (level != this.isolationLevel) {
/* 5379 */         shouldSendSet = true;
/*      */       }
/*      */ 
/* 5383 */       if (getUseLocalSessionState()) {
/* 5384 */         shouldSendSet = this.isolationLevel != level;
/*      */       }
/*      */ 
/* 5387 */       if (shouldSendSet) {
/* 5388 */         switch (level) {
/*      */         case 0:
/* 5390 */           throw SQLError.createSQLException("Transaction isolation level NONE not supported by MySQL", getExceptionInterceptor());
/*      */         case 2:
/* 5394 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
/*      */ 
/* 5396 */           break;
/*      */         case 1:
/* 5399 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
/*      */ 
/* 5401 */           break;
/*      */         case 4:
/* 5404 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
/*      */ 
/* 5406 */           break;
/*      */         case 8:
/* 5409 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
/*      */ 
/* 5411 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         default:
/* 5414 */           throw SQLError.createSQLException("Unsupported transaction isolation level '" + level + "'", "S1C00", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 5419 */         execSQL(null, sql, -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 5424 */         this.isolationLevel = level;
/*      */       }
/*      */     } else {
/* 5427 */       throw SQLError.createSQLException("Transaction Isolation Levels are not supported on MySQL versions older than 3.23.36.", "S1C00", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setTypeMap(Map map)
/*      */     throws SQLException
/*      */   {
/* 5443 */     this.typeMap = map;
/*      */   }
/*      */ 
/*      */   private void setupServerForTruncationChecks() throws SQLException {
/* 5447 */     if ((getJdbcCompliantTruncation()) && 
/* 5448 */       (versionMeetsMinimum(5, 0, 2))) {
/* 5449 */       String currentSqlMode = (String)this.serverVariables.get("sql_mode");
/*      */ 
/* 5452 */       boolean strictTransTablesIsSet = StringUtils.indexOfIgnoreCase(currentSqlMode, "STRICT_TRANS_TABLES") != -1;
/*      */ 
/* 5454 */       if ((currentSqlMode == null) || (currentSqlMode.length() == 0) || (!strictTransTablesIsSet))
/*      */       {
/* 5456 */         StringBuffer commandBuf = new StringBuffer("SET sql_mode='");
/*      */ 
/* 5458 */         if ((currentSqlMode != null) && (currentSqlMode.length() > 0)) {
/* 5459 */           commandBuf.append(currentSqlMode);
/* 5460 */           commandBuf.append(",");
/*      */         }
/*      */ 
/* 5463 */         commandBuf.append("STRICT_TRANS_TABLES'");
/*      */ 
/* 5465 */         execSQL(null, commandBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 5470 */         setJdbcCompliantTruncation(false);
/* 5471 */       } else if (strictTransTablesIsSet)
/*      */       {
/* 5473 */         setJdbcCompliantTruncation(false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean shouldFallBack()
/*      */   {
/* 5488 */     long secondsSinceFailedOver = (System.currentTimeMillis() - this.masterFailTimeMillis) / 1000L;
/*      */ 
/* 5491 */     boolean tryFallback = (secondsSinceFailedOver >= getSecondsBeforeRetryMaster()) || (this.queriesIssuedFailedOver >= getQueriesBeforeRetryMaster());
/*      */ 
/* 5493 */     return tryFallback;
/*      */   }
/*      */ 
/*      */   public void shutdownServer()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5504 */       this.io.sendCommand(8, null, null, false, null, 0);
/*      */     } catch (Exception ex) {
/* 5506 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnhandledExceptionDuringShutdown"), "S1000", getExceptionInterceptor());
/*      */ 
/* 5510 */       sqlEx.initCause(ex);
/*      */ 
/* 5512 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean supportsIsolationLevel()
/*      */   {
/* 5522 */     return this.hasIsolationLevels;
/*      */   }
/*      */ 
/*      */   public boolean supportsQuotedIdentifiers()
/*      */   {
/* 5531 */     return this.hasQuotedIdentifiers;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */   {
/* 5540 */     return this.transactionsSupported;
/*      */   }
/*      */ 
/*      */   void unregisterStatement(Statement stmt)
/*      */   {
/* 5550 */     if (this.openStatements != null)
/* 5551 */       synchronized (this.openStatements) {
/* 5552 */         this.openStatements.remove(stmt);
/*      */       }
/*      */   }
/*      */ 
/*      */   void unsetMaxRows(Statement stmt)
/*      */     throws SQLException
/*      */   {
/* 5568 */     synchronized (this.mutex) {
/* 5569 */       if (this.statementsUsingMaxRows != null) {
/* 5570 */         Object found = this.statementsUsingMaxRows.remove(stmt);
/*      */ 
/* 5572 */         if ((found != null) && (this.statementsUsingMaxRows.size() == 0))
/*      */         {
/* 5574 */           execSQL(null, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 5579 */           this.maxRowsChanged = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean useAnsiQuotedIdentifiers() {
/* 5586 */     return this.useAnsiQuotes;
/*      */   }
/*      */ 
/*      */   boolean useMaxRows()
/*      */   {
/* 5595 */     synchronized (this.mutex) {
/* 5596 */       return this.maxRowsChanged;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException
/*      */   {
/* 5602 */     checkClosed();
/*      */ 
/* 5604 */     return this.io.versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */ 
/*      */   protected CachedResultSetMetaData getCachedMetaData(String sql)
/*      */   {
/* 5622 */     if (this.resultSetMetadataCache != null) {
/* 5623 */       synchronized (this.resultSetMetadataCache) {
/* 5624 */         return (CachedResultSetMetaData)this.resultSetMetadataCache.get(sql);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5629 */     return null;
/*      */   }
/*      */ 
/*      */   protected void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet)
/*      */     throws SQLException
/*      */   {
/* 5650 */     if (cachedMetaData == null)
/*      */     {
/* 5653 */       cachedMetaData = new CachedResultSetMetaData();
/*      */ 
/* 5657 */       resultSet.buildIndexMapping();
/* 5658 */       resultSet.initializeWithMetadata();
/*      */ 
/* 5660 */       if ((resultSet instanceof UpdatableResultSet)) {
/* 5661 */         ((UpdatableResultSet)resultSet).checkUpdatability();
/*      */       }
/*      */ 
/* 5664 */       resultSet.populateCachedMetaData(cachedMetaData);
/*      */ 
/* 5666 */       this.resultSetMetadataCache.put(sql, cachedMetaData);
/*      */     } else {
/* 5668 */       resultSet.initializeFromCachedMetaData(cachedMetaData);
/* 5669 */       resultSet.initializeWithMetadata();
/*      */ 
/* 5671 */       if ((resultSet instanceof UpdatableResultSet))
/* 5672 */         ((UpdatableResultSet)resultSet).checkUpdatability();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getStatementComment()
/*      */   {
/* 5685 */     return this.statementComment;
/*      */   }
/*      */ 
/*      */   public void setStatementComment(String comment)
/*      */   {
/* 5697 */     this.statementComment = comment;
/*      */   }
/*      */ 
/*      */   public synchronized void reportQueryTime(long millisOrNanos) {
/* 5701 */     this.queryTimeCount += 1L;
/* 5702 */     this.queryTimeSum += millisOrNanos;
/* 5703 */     this.queryTimeSumSquares += millisOrNanos * millisOrNanos;
/* 5704 */     this.queryTimeMean = ((this.queryTimeMean * (this.queryTimeCount - 1L) + millisOrNanos) / this.queryTimeCount);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isAbonormallyLongQuery(long millisOrNanos)
/*      */   {
/* 5709 */     if (this.queryTimeCount < 15L) {
/* 5710 */       return false;
/*      */     }
/*      */ 
/* 5713 */     double stddev = Math.sqrt((this.queryTimeSumSquares - this.queryTimeSum * this.queryTimeSum / this.queryTimeCount) / (this.queryTimeCount - 1L));
/*      */ 
/* 5715 */     return millisOrNanos > this.queryTimeMean + 5.0D * stddev;
/*      */   }
/*      */ 
/*      */   public void initializeExtension(Extension ex) throws SQLException {
/* 5719 */     ex.init(this, this.props);
/*      */   }
/*      */ 
/*      */   protected void transactionBegun() throws SQLException {
/* 5723 */     if (this.connectionLifecycleInterceptors != null) {
/* 5724 */       IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */       {
/*      */         void forEach(Object each) throws SQLException {
/* 5727 */           ((ConnectionLifecycleInterceptor)each).transactionBegun();
/*      */         }
/*      */       };
/* 5731 */       iter.doForAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void transactionCompleted() throws SQLException {
/* 5736 */     if (this.connectionLifecycleInterceptors != null) {
/* 5737 */       IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */       {
/*      */         void forEach(Object each) throws SQLException {
/* 5740 */           ((ConnectionLifecycleInterceptor)each).transactionCompleted();
/*      */         }
/*      */       };
/* 5744 */       iter.doForAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseTableName() {
/* 5749 */     return this.storesLowerCaseTableName;
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/* 5755 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   public boolean getRequiresEscapingEncoder() {
/* 5759 */     return this.requiresEscapingEncoder;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  235 */     mapTransIsolationNameToValue = new HashMap(8);
/*  236 */     mapTransIsolationNameToValue.put("READ-UNCOMMITED", Constants.integerValueOf(1));
/*      */ 
/*  238 */     mapTransIsolationNameToValue.put("READ-UNCOMMITTED", Constants.integerValueOf(1));
/*      */ 
/*  240 */     mapTransIsolationNameToValue.put("READ-COMMITTED", Constants.integerValueOf(2));
/*      */ 
/*  242 */     mapTransIsolationNameToValue.put("REPEATABLE-READ", Constants.integerValueOf(4));
/*      */ 
/*  244 */     mapTransIsolationNameToValue.put("SERIALIZABLE", Constants.integerValueOf(8));
/*      */ 
/*  247 */     if (Util.isJdbc4())
/*      */       try {
/*  249 */         JDBC_4_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4Connection").getConstructor(new Class[] { String.class, Integer.TYPE, Properties.class, String.class, String.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  254 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  256 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  258 */         throw new RuntimeException(e);
/*      */       }
/*      */     else
/*  261 */       JDBC_4_CONNECTION_CTOR = null;
/*      */   }
/*      */ 
/*      */   class CompoundCacheKey
/*      */   {
/*      */     String componentOne;
/*      */     String componentTwo;
/*      */     int hashCode;
/*      */ 
/*      */     CompoundCacheKey(String partOne, String partTwo)
/*      */     {
/*  140 */       this.componentOne = partOne;
/*  141 */       this.componentTwo = partTwo;
/*      */ 
/*  145 */       this.hashCode = ((this.componentOne != null ? this.componentOne : "") + this.componentTwo).hashCode();
/*      */     }
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/*  155 */       if ((obj instanceof CompoundCacheKey)) {
/*  156 */         CompoundCacheKey another = (CompoundCacheKey)obj;
/*      */ 
/*  158 */         boolean firstPartEqual = false;
/*      */ 
/*  160 */         if (this.componentOne == null)
/*  161 */           firstPartEqual = another.componentOne == null;
/*      */         else {
/*  163 */           firstPartEqual = this.componentOne.equals(another.componentOne);
/*      */         }
/*      */ 
/*  167 */         return (firstPartEqual) && (this.componentTwo.equals(another.componentTwo));
/*      */       }
/*      */ 
/*  171 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/*  180 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */ 
/*      */   class ExceptionInterceptorChain
/*      */     implements ExceptionInterceptor
/*      */   {
/*      */     List interceptors;
/*      */ 
/*      */     ExceptionInterceptorChain(String interceptorClasses)
/*      */       throws SQLException
/*      */     {
/*   90 */       this.interceptors = Util.loadExtensions(ConnectionImpl.this, ConnectionImpl.this.props, interceptorClasses, "Connection.BadExceptionInterceptor", this);
/*      */     }
/*      */ 
/*      */     public SQLException interceptException(SQLException sqlEx, Connection conn) {
/*   94 */       if (this.interceptors != null) {
/*   95 */         Iterator iter = this.interceptors.iterator();
/*      */ 
/*   97 */         while (iter.hasNext()) {
/*   98 */           sqlEx = ((ExceptionInterceptor)iter.next()).interceptException(sqlEx, ConnectionImpl.this);
/*      */         }
/*      */       }
/*      */ 
/*  102 */       return sqlEx;
/*      */     }
/*      */ 
/*      */     public void destroy() {
/*  106 */       if (this.interceptors != null) {
/*  107 */         Iterator iter = this.interceptors.iterator();
/*      */ 
/*  109 */         while (iter.hasNext())
/*  110 */           ((ExceptionInterceptor)iter.next()).destroy();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void init(Connection conn, Properties props)
/*      */       throws SQLException
/*      */     {
/*  117 */       if (this.interceptors != null) {
/*  118 */         Iterator iter = this.interceptors.iterator();
/*      */ 
/*  120 */         while (iter.hasNext())
/*  121 */           ((ExceptionInterceptor)iter.next()).init(conn, props);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionImpl
 * JD-Core Version:    0.6.0
 */