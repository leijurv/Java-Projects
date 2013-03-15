/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.StandardLogger;
/*      */ import java.io.Serializable;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.sql.DriverPropertyInfo;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TreeMap;
/*      */ import javax.naming.RefAddr;
/*      */ import javax.naming.Reference;
/*      */ import javax.naming.StringRefAddr;
/*      */ 
/*      */ public class ConnectionPropertiesImpl
/*      */   implements Serializable, ConnectionProperties
/*      */ {
/*      */   private static final long serialVersionUID = 4257801713007640580L;
/*  617 */   private static final String CONNECTION_AND_AUTH_CATEGORY = Messages.getString("ConnectionProperties.categoryConnectionAuthentication");
/*      */ 
/*  619 */   private static final String NETWORK_CATEGORY = Messages.getString("ConnectionProperties.categoryNetworking");
/*      */ 
/*  621 */   private static final String DEBUGING_PROFILING_CATEGORY = Messages.getString("ConnectionProperties.categoryDebuggingProfiling");
/*      */ 
/*  623 */   private static final String HA_CATEGORY = Messages.getString("ConnectionProperties.categorryHA");
/*      */ 
/*  625 */   private static final String MISC_CATEGORY = Messages.getString("ConnectionProperties.categoryMisc");
/*      */ 
/*  627 */   private static final String PERFORMANCE_CATEGORY = Messages.getString("ConnectionProperties.categoryPerformance");
/*      */ 
/*  629 */   private static final String SECURITY_CATEGORY = Messages.getString("ConnectionProperties.categorySecurity");
/*      */ 
/*  631 */   private static final String[] PROPERTY_CATEGORIES = { CONNECTION_AND_AUTH_CATEGORY, NETWORK_CATEGORY, HA_CATEGORY, SECURITY_CATEGORY, PERFORMANCE_CATEGORY, DEBUGING_PROFILING_CATEGORY, MISC_CATEGORY };
/*      */ 
/*  636 */   private static final ArrayList PROPERTY_LIST = new ArrayList();
/*      */ 
/*  641 */   private static final String STANDARD_LOGGER_NAME = StandardLogger.class.getName();
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL = "convertToNull";
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_EXCEPTION = "exception";
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_ROUND = "round";
/*  692 */   private BooleanConnectionProperty allowLoadLocalInfile = new BooleanConnectionProperty("allowLoadLocalInfile", true, Messages.getString("ConnectionProperties.loadDataLocal"), "3.0.3", SECURITY_CATEGORY, 2147483647);
/*      */ 
/*  698 */   private BooleanConnectionProperty allowMultiQueries = new BooleanConnectionProperty("allowMultiQueries", false, Messages.getString("ConnectionProperties.allowMultiQueries"), "3.1.1", SECURITY_CATEGORY, 1);
/*      */ 
/*  704 */   private BooleanConnectionProperty allowNanAndInf = new BooleanConnectionProperty("allowNanAndInf", false, Messages.getString("ConnectionProperties.allowNANandINF"), "3.1.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*  710 */   private BooleanConnectionProperty allowUrlInLocalInfile = new BooleanConnectionProperty("allowUrlInLocalInfile", false, Messages.getString("ConnectionProperties.allowUrlInLoadLocal"), "3.1.4", SECURITY_CATEGORY, 2147483647);
/*      */ 
/*  716 */   private BooleanConnectionProperty alwaysSendSetIsolation = new BooleanConnectionProperty("alwaysSendSetIsolation", true, Messages.getString("ConnectionProperties.alwaysSendSetIsolation"), "3.1.7", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/*  722 */   private BooleanConnectionProperty autoClosePStmtStreams = new BooleanConnectionProperty("autoClosePStmtStreams", false, Messages.getString("ConnectionProperties.autoClosePstmtStreams"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/*  730 */   private BooleanConnectionProperty autoDeserialize = new BooleanConnectionProperty("autoDeserialize", false, Messages.getString("ConnectionProperties.autoDeserialize"), "3.1.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*  736 */   private BooleanConnectionProperty autoGenerateTestcaseScript = new BooleanConnectionProperty("autoGenerateTestcaseScript", false, Messages.getString("ConnectionProperties.autoGenerateTestcaseScript"), "3.1.9", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  741 */   private boolean autoGenerateTestcaseScriptAsBoolean = false;
/*      */ 
/*  743 */   private BooleanConnectionProperty autoReconnect = new BooleanConnectionProperty("autoReconnect", false, Messages.getString("ConnectionProperties.autoReconnect"), "1.1", HA_CATEGORY, 0);
/*      */ 
/*  749 */   private BooleanConnectionProperty autoReconnectForPools = new BooleanConnectionProperty("autoReconnectForPools", false, Messages.getString("ConnectionProperties.autoReconnectForPools"), "3.1.3", HA_CATEGORY, 1);
/*      */ 
/*  755 */   private boolean autoReconnectForPoolsAsBoolean = false;
/*      */ 
/*  757 */   private MemorySizeConnectionProperty blobSendChunkSize = new MemorySizeConnectionProperty("blobSendChunkSize", 1048576, 1, 2147483647, Messages.getString("ConnectionProperties.blobSendChunkSize"), "3.1.9", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  765 */   private BooleanConnectionProperty autoSlowLog = new BooleanConnectionProperty("autoSlowLog", true, Messages.getString("ConnectionProperties.autoSlowLog"), "5.1.4", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  770 */   private BooleanConnectionProperty blobsAreStrings = new BooleanConnectionProperty("blobsAreStrings", false, "Should the driver always treat BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*  776 */   private BooleanConnectionProperty functionsNeverReturnBlobs = new BooleanConnectionProperty("functionsNeverReturnBlobs", false, "Should the driver always treat data from functions returning BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*  782 */   private BooleanConnectionProperty cacheCallableStatements = new BooleanConnectionProperty("cacheCallableStmts", false, Messages.getString("ConnectionProperties.cacheCallableStatements"), "3.1.2", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  787 */   private BooleanConnectionProperty cachePreparedStatements = new BooleanConnectionProperty("cachePrepStmts", false, Messages.getString("ConnectionProperties.cachePrepStmts"), "3.0.10", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  793 */   private BooleanConnectionProperty cacheResultSetMetadata = new BooleanConnectionProperty("cacheResultSetMetadata", false, Messages.getString("ConnectionProperties.cacheRSMetadata"), "3.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */   private boolean cacheResultSetMetaDataAsBoolean;
/*  801 */   private BooleanConnectionProperty cacheServerConfiguration = new BooleanConnectionProperty("cacheServerConfiguration", false, Messages.getString("ConnectionProperties.cacheServerConfiguration"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  807 */   private IntegerConnectionProperty callableStatementCacheSize = new IntegerConnectionProperty("callableStmtCacheSize", 100, 0, 2147483647, Messages.getString("ConnectionProperties.callableStmtCacheSize"), "3.1.2", PERFORMANCE_CATEGORY, 5);
/*      */ 
/*  815 */   private BooleanConnectionProperty capitalizeTypeNames = new BooleanConnectionProperty("capitalizeTypeNames", true, Messages.getString("ConnectionProperties.capitalizeTypeNames"), "2.0.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*  821 */   private StringConnectionProperty characterEncoding = new StringConnectionProperty("characterEncoding", null, Messages.getString("ConnectionProperties.characterEncoding"), "1.1g", MISC_CATEGORY, 5);
/*      */ 
/*  827 */   private String characterEncodingAsString = null;
/*      */ 
/*  829 */   private StringConnectionProperty characterSetResults = new StringConnectionProperty("characterSetResults", null, Messages.getString("ConnectionProperties.characterSetResults"), "3.0.13", MISC_CATEGORY, 6);
/*      */ 
/*  834 */   private StringConnectionProperty clientInfoProvider = new StringConnectionProperty("clientInfoProvider", "com.mysql.jdbc.JDBC4CommentClientInfoProvider", Messages.getString("ConnectionProperties.clientInfoProvider"), "5.1.0", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  840 */   private BooleanConnectionProperty clobberStreamingResults = new BooleanConnectionProperty("clobberStreamingResults", false, Messages.getString("ConnectionProperties.clobberStreamingResults"), "3.0.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*  846 */   private StringConnectionProperty clobCharacterEncoding = new StringConnectionProperty("clobCharacterEncoding", null, Messages.getString("ConnectionProperties.clobCharacterEncoding"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*  852 */   private BooleanConnectionProperty compensateOnDuplicateKeyUpdateCounts = new BooleanConnectionProperty("compensateOnDuplicateKeyUpdateCounts", false, Messages.getString("ConnectionProperties.compensateOnDuplicateKeyUpdateCounts"), "5.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*  857 */   private StringConnectionProperty connectionCollation = new StringConnectionProperty("connectionCollation", null, Messages.getString("ConnectionProperties.connectionCollation"), "3.0.13", MISC_CATEGORY, 7);
/*      */ 
/*  863 */   private StringConnectionProperty connectionLifecycleInterceptors = new StringConnectionProperty("connectionLifecycleInterceptors", null, Messages.getString("ConnectionProperties.connectionLifecycleInterceptors"), "5.1.4", CONNECTION_AND_AUTH_CATEGORY, 2147483647);
/*      */ 
/*  869 */   private IntegerConnectionProperty connectTimeout = new IntegerConnectionProperty("connectTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.connectTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 9);
/*      */ 
/*  874 */   private BooleanConnectionProperty continueBatchOnError = new BooleanConnectionProperty("continueBatchOnError", true, Messages.getString("ConnectionProperties.continueBatchOnError"), "3.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/*  880 */   private BooleanConnectionProperty createDatabaseIfNotExist = new BooleanConnectionProperty("createDatabaseIfNotExist", false, Messages.getString("ConnectionProperties.createDatabaseIfNotExist"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*  886 */   private IntegerConnectionProperty defaultFetchSize = new IntegerConnectionProperty("defaultFetchSize", 0, Messages.getString("ConnectionProperties.defaultFetchSize"), "3.1.9", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  888 */   private BooleanConnectionProperty detectServerPreparedStmts = new BooleanConnectionProperty("useServerPrepStmts", false, Messages.getString("ConnectionProperties.useServerPrepStmts"), "3.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*  894 */   private BooleanConnectionProperty dontTrackOpenResources = new BooleanConnectionProperty("dontTrackOpenResources", false, Messages.getString("ConnectionProperties.dontTrackOpenResources"), "3.1.7", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  900 */   private BooleanConnectionProperty dumpQueriesOnException = new BooleanConnectionProperty("dumpQueriesOnException", false, Messages.getString("ConnectionProperties.dumpQueriesOnException"), "3.1.3", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  906 */   private BooleanConnectionProperty dynamicCalendars = new BooleanConnectionProperty("dynamicCalendars", false, Messages.getString("ConnectionProperties.dynamicCalendars"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  912 */   private BooleanConnectionProperty elideSetAutoCommits = new BooleanConnectionProperty("elideSetAutoCommits", false, Messages.getString("ConnectionProperties.eliseSetAutoCommit"), "3.1.3", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  918 */   private BooleanConnectionProperty emptyStringsConvertToZero = new BooleanConnectionProperty("emptyStringsConvertToZero", true, Messages.getString("ConnectionProperties.emptyStringsConvertToZero"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*  923 */   private BooleanConnectionProperty emulateLocators = new BooleanConnectionProperty("emulateLocators", false, Messages.getString("ConnectionProperties.emulateLocators"), "3.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*  927 */   private BooleanConnectionProperty emulateUnsupportedPstmts = new BooleanConnectionProperty("emulateUnsupportedPstmts", true, Messages.getString("ConnectionProperties.emulateUnsupportedPstmts"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*  933 */   private BooleanConnectionProperty enablePacketDebug = new BooleanConnectionProperty("enablePacketDebug", false, Messages.getString("ConnectionProperties.enablePacketDebug"), "3.1.3", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  939 */   private BooleanConnectionProperty enableQueryTimeouts = new BooleanConnectionProperty("enableQueryTimeouts", true, Messages.getString("ConnectionProperties.enableQueryTimeouts"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  946 */   private BooleanConnectionProperty explainSlowQueries = new BooleanConnectionProperty("explainSlowQueries", false, Messages.getString("ConnectionProperties.explainSlowQueries"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  952 */   private StringConnectionProperty exceptionInterceptors = new StringConnectionProperty("exceptionInterceptors", null, Messages.getString("ConnectionProperties.exceptionInterceptors"), "5.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*  959 */   private BooleanConnectionProperty failOverReadOnly = new BooleanConnectionProperty("failOverReadOnly", true, Messages.getString("ConnectionProperties.failoverReadOnly"), "3.0.12", HA_CATEGORY, 2);
/*      */ 
/*  965 */   private BooleanConnectionProperty gatherPerformanceMetrics = new BooleanConnectionProperty("gatherPerfMetrics", false, Messages.getString("ConnectionProperties.gatherPerfMetrics"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 1);
/*      */ 
/*  971 */   private BooleanConnectionProperty generateSimpleParameterMetadata = new BooleanConnectionProperty("generateSimpleParameterMetadata", false, Messages.getString("ConnectionProperties.generateSimpleParameterMetadata"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*  974 */   private boolean highAvailabilityAsBoolean = false;
/*      */ 
/*  976 */   private BooleanConnectionProperty holdResultsOpenOverStatementClose = new BooleanConnectionProperty("holdResultsOpenOverStatementClose", false, Messages.getString("ConnectionProperties.holdRSOpenOverStmtClose"), "3.1.7", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  982 */   private BooleanConnectionProperty includeInnodbStatusInDeadlockExceptions = new BooleanConnectionProperty("includeInnodbStatusInDeadlockExceptions", false, "Include the output of \"SHOW ENGINE INNODB STATUS\" in exception messages when deadlock exceptions are detected?", "5.0.7", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  988 */   private BooleanConnectionProperty ignoreNonTxTables = new BooleanConnectionProperty("ignoreNonTxTables", false, Messages.getString("ConnectionProperties.ignoreNonTxTables"), "3.0.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*  994 */   private IntegerConnectionProperty initialTimeout = new IntegerConnectionProperty("initialTimeout", 2, 1, 2147483647, Messages.getString("ConnectionProperties.initialTimeout"), "1.1", HA_CATEGORY, 5);
/*      */ 
/*  999 */   private BooleanConnectionProperty isInteractiveClient = new BooleanConnectionProperty("interactiveClient", false, Messages.getString("ConnectionProperties.interactiveClient"), "3.1.0", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1005 */   private BooleanConnectionProperty jdbcCompliantTruncation = new BooleanConnectionProperty("jdbcCompliantTruncation", true, Messages.getString("ConnectionProperties.jdbcCompliantTruncation"), "3.1.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1011 */   private boolean jdbcCompliantTruncationForReads = this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */ 
/* 1014 */   protected MemorySizeConnectionProperty largeRowSizeThreshold = new MemorySizeConnectionProperty("largeRowSizeThreshold", 2048, 0, 2147483647, Messages.getString("ConnectionProperties.largeRowSizeThreshold"), "5.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1019 */   private StringConnectionProperty loadBalanceStrategy = new StringConnectionProperty("loadBalanceStrategy", "random", new String[] { "random", "bestResponseTime" }, Messages.getString("ConnectionProperties.loadBalanceStrategy"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1026 */   private IntegerConnectionProperty loadBalanceBlacklistTimeout = new IntegerConnectionProperty("loadBalanceBlacklistTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.loadBalanceBlacklistTimeout"), "5.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1032 */   private StringConnectionProperty localSocketAddress = new StringConnectionProperty("localSocketAddress", null, Messages.getString("ConnectionProperties.localSocketAddress"), "5.0.5", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1036 */   private MemorySizeConnectionProperty locatorFetchBufferSize = new MemorySizeConnectionProperty("locatorFetchBufferSize", 1048576, 0, 2147483647, Messages.getString("ConnectionProperties.locatorFetchBufferSize"), "3.2.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1044 */   private StringConnectionProperty loggerClassName = new StringConnectionProperty("logger", STANDARD_LOGGER_NAME, Messages.getString("ConnectionProperties.logger", new Object[] { Log.class.getName(), STANDARD_LOGGER_NAME }), "3.1.1", DEBUGING_PROFILING_CATEGORY, 0);
/*      */ 
/* 1050 */   private BooleanConnectionProperty logSlowQueries = new BooleanConnectionProperty("logSlowQueries", false, Messages.getString("ConnectionProperties.logSlowQueries"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1056 */   private BooleanConnectionProperty logXaCommands = new BooleanConnectionProperty("logXaCommands", false, Messages.getString("ConnectionProperties.logXaCommands"), "5.0.5", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1062 */   private BooleanConnectionProperty maintainTimeStats = new BooleanConnectionProperty("maintainTimeStats", true, Messages.getString("ConnectionProperties.maintainTimeStats"), "3.1.9", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/* 1068 */   private boolean maintainTimeStatsAsBoolean = true;
/*      */ 
/* 1070 */   private IntegerConnectionProperty maxQuerySizeToLog = new IntegerConnectionProperty("maxQuerySizeToLog", 2048, 0, 2147483647, Messages.getString("ConnectionProperties.maxQuerySizeToLog"), "3.1.3", DEBUGING_PROFILING_CATEGORY, 4);
/*      */ 
/* 1078 */   private IntegerConnectionProperty maxReconnects = new IntegerConnectionProperty("maxReconnects", 3, 1, 2147483647, Messages.getString("ConnectionProperties.maxReconnects"), "1.1", HA_CATEGORY, 4);
/*      */ 
/* 1086 */   private IntegerConnectionProperty retriesAllDown = new IntegerConnectionProperty("retriesAllDown", 120, 0, 2147483647, Messages.getString("ConnectionProperties.retriesAllDown"), "5.1.6", HA_CATEGORY, 4);
/*      */ 
/* 1094 */   private IntegerConnectionProperty maxRows = new IntegerConnectionProperty("maxRows", -1, -1, 2147483647, Messages.getString("ConnectionProperties.maxRows"), Messages.getString("ConnectionProperties.allVersions"), MISC_CATEGORY, -2147483648);
/*      */ 
/* 1099 */   private int maxRowsAsInt = -1;
/*      */ 
/* 1101 */   private IntegerConnectionProperty metadataCacheSize = new IntegerConnectionProperty("metadataCacheSize", 50, 1, 2147483647, Messages.getString("ConnectionProperties.metadataCacheSize"), "3.1.1", PERFORMANCE_CATEGORY, 5);
/*      */ 
/* 1109 */   private IntegerConnectionProperty netTimeoutForStreamingResults = new IntegerConnectionProperty("netTimeoutForStreamingResults", 600, 0, 2147483647, Messages.getString("ConnectionProperties.netTimeoutForStreamingResults"), "5.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1115 */   private BooleanConnectionProperty noAccessToProcedureBodies = new BooleanConnectionProperty("noAccessToProcedureBodies", false, "When determining procedure parameter types for CallableStatements, and the connected user  can't access procedure bodies through \"SHOW CREATE PROCEDURE\" or select on mysql.proc  should the driver instead create basic metadata (all parameters reported as IN VARCHARs, but allowing registerOutParameter() to be called on them anyway) instead  of throwing an exception?", "5.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1125 */   private BooleanConnectionProperty noDatetimeStringSync = new BooleanConnectionProperty("noDatetimeStringSync", false, Messages.getString("ConnectionProperties.noDatetimeStringSync"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1131 */   private BooleanConnectionProperty noTimezoneConversionForTimeType = new BooleanConnectionProperty("noTimezoneConversionForTimeType", false, Messages.getString("ConnectionProperties.noTzConversionForTimeType"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1137 */   private BooleanConnectionProperty nullCatalogMeansCurrent = new BooleanConnectionProperty("nullCatalogMeansCurrent", true, Messages.getString("ConnectionProperties.nullCatalogMeansCurrent"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1143 */   private BooleanConnectionProperty nullNamePatternMatchesAll = new BooleanConnectionProperty("nullNamePatternMatchesAll", true, Messages.getString("ConnectionProperties.nullNamePatternMatchesAll"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1149 */   private IntegerConnectionProperty packetDebugBufferSize = new IntegerConnectionProperty("packetDebugBufferSize", 20, 0, 2147483647, Messages.getString("ConnectionProperties.packetDebugBufferSize"), "3.1.3", DEBUGING_PROFILING_CATEGORY, 7);
/*      */ 
/* 1157 */   private BooleanConnectionProperty padCharsWithSpace = new BooleanConnectionProperty("padCharsWithSpace", false, Messages.getString("ConnectionProperties.padCharsWithSpace"), "5.0.6", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1165 */   private BooleanConnectionProperty paranoid = new BooleanConnectionProperty("paranoid", false, Messages.getString("ConnectionProperties.paranoid"), "3.0.1", SECURITY_CATEGORY, -2147483648);
/*      */ 
/* 1171 */   private BooleanConnectionProperty pedantic = new BooleanConnectionProperty("pedantic", false, Messages.getString("ConnectionProperties.pedantic"), "3.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1175 */   private BooleanConnectionProperty pinGlobalTxToPhysicalConnection = new BooleanConnectionProperty("pinGlobalTxToPhysicalConnection", false, Messages.getString("ConnectionProperties.pinGlobalTxToPhysicalConnection"), "5.0.1", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1179 */   private BooleanConnectionProperty populateInsertRowWithDefaultValues = new BooleanConnectionProperty("populateInsertRowWithDefaultValues", false, Messages.getString("ConnectionProperties.populateInsertRowWithDefaultValues"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1184 */   private IntegerConnectionProperty preparedStatementCacheSize = new IntegerConnectionProperty("prepStmtCacheSize", 25, 0, 2147483647, Messages.getString("ConnectionProperties.prepStmtCacheSize"), "3.0.10", PERFORMANCE_CATEGORY, 10);
/*      */ 
/* 1189 */   private IntegerConnectionProperty preparedStatementCacheSqlLimit = new IntegerConnectionProperty("prepStmtCacheSqlLimit", 256, 1, 2147483647, Messages.getString("ConnectionProperties.prepStmtCacheSqlLimit"), "3.0.10", PERFORMANCE_CATEGORY, 11);
/*      */ 
/* 1197 */   private BooleanConnectionProperty processEscapeCodesForPrepStmts = new BooleanConnectionProperty("processEscapeCodesForPrepStmts", true, Messages.getString("ConnectionProperties.processEscapeCodesForPrepStmts"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1204 */   private StringConnectionProperty profilerEventHandler = new StringConnectionProperty("profilerEventHandler", "com.mysql.jdbc.profiler.LoggingProfilerEventHandler", Messages.getString("ConnectionProperties.profilerEventHandler"), "5.1.6", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1210 */   private StringConnectionProperty profileSql = new StringConnectionProperty("profileSql", null, Messages.getString("ConnectionProperties.profileSqlDeprecated"), "2.0.14", DEBUGING_PROFILING_CATEGORY, 3);
/*      */ 
/* 1216 */   private BooleanConnectionProperty profileSQL = new BooleanConnectionProperty("profileSQL", false, Messages.getString("ConnectionProperties.profileSQL"), "3.1.0", DEBUGING_PROFILING_CATEGORY, 1);
/*      */ 
/* 1222 */   private boolean profileSQLAsBoolean = false;
/*      */ 
/* 1224 */   private StringConnectionProperty propertiesTransform = new StringConnectionProperty("propertiesTransform", null, Messages.getString("ConnectionProperties.connectionPropertiesTransform"), "3.1.4", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1230 */   private IntegerConnectionProperty queriesBeforeRetryMaster = new IntegerConnectionProperty("queriesBeforeRetryMaster", 50, 1, 2147483647, Messages.getString("ConnectionProperties.queriesBeforeRetryMaster"), "3.0.2", HA_CATEGORY, 7);
/*      */ 
/* 1238 */   private BooleanConnectionProperty queryTimeoutKillsConnection = new BooleanConnectionProperty("queryTimeoutKillsConnection", false, Messages.getString("ConnectionProperties.queryTimeoutKillsConnection"), "5.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1242 */   private BooleanConnectionProperty reconnectAtTxEnd = new BooleanConnectionProperty("reconnectAtTxEnd", false, Messages.getString("ConnectionProperties.reconnectAtTxEnd"), "3.0.10", HA_CATEGORY, 4);
/*      */ 
/* 1247 */   private boolean reconnectTxAtEndAsBoolean = false;
/*      */ 
/* 1249 */   private BooleanConnectionProperty relaxAutoCommit = new BooleanConnectionProperty("relaxAutoCommit", false, Messages.getString("ConnectionProperties.relaxAutoCommit"), "2.0.13", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1255 */   private IntegerConnectionProperty reportMetricsIntervalMillis = new IntegerConnectionProperty("reportMetricsIntervalMillis", 30000, 0, 2147483647, Messages.getString("ConnectionProperties.reportMetricsIntervalMillis"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 3);
/*      */ 
/* 1263 */   private BooleanConnectionProperty requireSSL = new BooleanConnectionProperty("requireSSL", false, Messages.getString("ConnectionProperties.requireSSL"), "3.1.0", SECURITY_CATEGORY, 3);
/*      */ 
/* 1268 */   private StringConnectionProperty resourceId = new StringConnectionProperty("resourceId", null, Messages.getString("ConnectionProperties.resourceId"), "5.0.1", HA_CATEGORY, -2147483648);
/*      */ 
/* 1275 */   private IntegerConnectionProperty resultSetSizeThreshold = new IntegerConnectionProperty("resultSetSizeThreshold", 100, Messages.getString("ConnectionProperties.resultSetSizeThreshold"), "5.0.5", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1278 */   private BooleanConnectionProperty retainStatementAfterResultSetClose = new BooleanConnectionProperty("retainStatementAfterResultSetClose", false, Messages.getString("ConnectionProperties.retainStatementAfterResultSetClose"), "3.1.11", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1284 */   private BooleanConnectionProperty rewriteBatchedStatements = new BooleanConnectionProperty("rewriteBatchedStatements", false, Messages.getString("ConnectionProperties.rewriteBatchedStatements"), "3.1.13", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1290 */   private BooleanConnectionProperty rollbackOnPooledClose = new BooleanConnectionProperty("rollbackOnPooledClose", true, Messages.getString("ConnectionProperties.rollbackOnPooledClose"), "3.0.15", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1296 */   private BooleanConnectionProperty roundRobinLoadBalance = new BooleanConnectionProperty("roundRobinLoadBalance", false, Messages.getString("ConnectionProperties.roundRobinLoadBalance"), "3.1.2", HA_CATEGORY, 5);
/*      */ 
/* 1302 */   private BooleanConnectionProperty runningCTS13 = new BooleanConnectionProperty("runningCTS13", false, Messages.getString("ConnectionProperties.runningCTS13"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1308 */   private IntegerConnectionProperty secondsBeforeRetryMaster = new IntegerConnectionProperty("secondsBeforeRetryMaster", 30, 1, 2147483647, Messages.getString("ConnectionProperties.secondsBeforeRetryMaster"), "3.0.2", HA_CATEGORY, 8);
/*      */ 
/* 1316 */   private IntegerConnectionProperty selfDestructOnPingSecondsLifetime = new IntegerConnectionProperty("selfDestructOnPingSecondsLifetime", 0, 0, 2147483647, Messages.getString("ConnectionProperties.selfDestructOnPingSecondsLifetime"), "5.1.6", HA_CATEGORY, 2147483647);
/*      */ 
/* 1324 */   private IntegerConnectionProperty selfDestructOnPingMaxOperations = new IntegerConnectionProperty("selfDestructOnPingMaxOperations", 0, 0, 2147483647, Messages.getString("ConnectionProperties.selfDestructOnPingMaxOperations"), "5.1.6", HA_CATEGORY, 2147483647);
/*      */ 
/* 1332 */   private StringConnectionProperty serverTimezone = new StringConnectionProperty("serverTimezone", null, Messages.getString("ConnectionProperties.serverTimezone"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1338 */   private StringConnectionProperty sessionVariables = new StringConnectionProperty("sessionVariables", null, Messages.getString("ConnectionProperties.sessionVariables"), "3.1.8", MISC_CATEGORY, 2147483647);
/*      */ 
/* 1343 */   private IntegerConnectionProperty slowQueryThresholdMillis = new IntegerConnectionProperty("slowQueryThresholdMillis", 2000, 0, 2147483647, Messages.getString("ConnectionProperties.slowQueryThresholdMillis"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 9);
/*      */ 
/* 1351 */   private LongConnectionProperty slowQueryThresholdNanos = new LongConnectionProperty("slowQueryThresholdNanos", 0L, Messages.getString("ConnectionProperties.slowQueryThresholdNanos"), "5.0.7", DEBUGING_PROFILING_CATEGORY, 10);
/*      */ 
/* 1359 */   private StringConnectionProperty socketFactoryClassName = new StringConnectionProperty("socketFactory", StandardSocketFactory.class.getName(), Messages.getString("ConnectionProperties.socketFactory"), "3.0.3", CONNECTION_AND_AUTH_CATEGORY, 4);
/*      */ 
/* 1365 */   private IntegerConnectionProperty socketTimeout = new IntegerConnectionProperty("socketTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.socketTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 10);
/*      */ 
/* 1373 */   private StringConnectionProperty statementInterceptors = new StringConnectionProperty("statementInterceptors", null, Messages.getString("ConnectionProperties.statementInterceptors"), "5.1.1", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1376 */   private BooleanConnectionProperty strictFloatingPoint = new BooleanConnectionProperty("strictFloatingPoint", false, Messages.getString("ConnectionProperties.strictFloatingPoint"), "3.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1381 */   private BooleanConnectionProperty strictUpdates = new BooleanConnectionProperty("strictUpdates", true, Messages.getString("ConnectionProperties.strictUpdates"), "3.0.4", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1387 */   private BooleanConnectionProperty overrideSupportsIntegrityEnhancementFacility = new BooleanConnectionProperty("overrideSupportsIntegrityEnhancementFacility", false, Messages.getString("ConnectionProperties.overrideSupportsIEF"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1393 */   private BooleanConnectionProperty tcpNoDelay = new BooleanConnectionProperty("tcpNoDelay", Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpNoDelay"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1399 */   private BooleanConnectionProperty tcpKeepAlive = new BooleanConnectionProperty("tcpKeepAlive", Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpKeepAlive"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1405 */   private IntegerConnectionProperty tcpRcvBuf = new IntegerConnectionProperty("tcpRcvBuf", Integer.parseInt("0"), 0, 2147483647, Messages.getString("ConnectionProperties.tcpSoRcvBuf"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1412 */   private IntegerConnectionProperty tcpSndBuf = new IntegerConnectionProperty("tcpSndBuf", Integer.parseInt("0"), 0, 2147483647, Messages.getString("ConnectionProperties.tcpSoSndBuf"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1419 */   private IntegerConnectionProperty tcpTrafficClass = new IntegerConnectionProperty("tcpTrafficClass", Integer.parseInt("0"), 0, 255, Messages.getString("ConnectionProperties.tcpTrafficClass"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1426 */   private BooleanConnectionProperty tinyInt1isBit = new BooleanConnectionProperty("tinyInt1isBit", true, Messages.getString("ConnectionProperties.tinyInt1isBit"), "3.0.16", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1432 */   private BooleanConnectionProperty traceProtocol = new BooleanConnectionProperty("traceProtocol", false, Messages.getString("ConnectionProperties.traceProtocol"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1437 */   private BooleanConnectionProperty treatUtilDateAsTimestamp = new BooleanConnectionProperty("treatUtilDateAsTimestamp", true, Messages.getString("ConnectionProperties.treatUtilDateAsTimestamp"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1442 */   private BooleanConnectionProperty transformedBitIsBoolean = new BooleanConnectionProperty("transformedBitIsBoolean", false, Messages.getString("ConnectionProperties.transformedBitIsBoolean"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1448 */   private BooleanConnectionProperty useBlobToStoreUTF8OutsideBMP = new BooleanConnectionProperty("useBlobToStoreUTF8OutsideBMP", false, Messages.getString("ConnectionProperties.useBlobToStoreUTF8OutsideBMP"), "5.1.3", MISC_CATEGORY, 128);
/*      */ 
/* 1454 */   private StringConnectionProperty utf8OutsideBmpExcludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpExcludedColumnNamePattern", null, Messages.getString("ConnectionProperties.utf8OutsideBmpExcludedColumnNamePattern"), "5.1.3", MISC_CATEGORY, 129);
/*      */ 
/* 1460 */   private StringConnectionProperty utf8OutsideBmpIncludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpIncludedColumnNamePattern", null, Messages.getString("ConnectionProperties.utf8OutsideBmpIncludedColumnNamePattern"), "5.1.3", MISC_CATEGORY, 129);
/*      */ 
/* 1466 */   private BooleanConnectionProperty useCompression = new BooleanConnectionProperty("useCompression", false, Messages.getString("ConnectionProperties.useCompression"), "3.0.17", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1472 */   private BooleanConnectionProperty useColumnNamesInFindColumn = new BooleanConnectionProperty("useColumnNamesInFindColumn", false, Messages.getString("ConnectionProperties.useColumnNamesInFindColumn"), "5.1.7", MISC_CATEGORY, 2147483647);
/*      */ 
/* 1478 */   private StringConnectionProperty useConfigs = new StringConnectionProperty("useConfigs", null, Messages.getString("ConnectionProperties.useConfigs"), "3.1.5", CONNECTION_AND_AUTH_CATEGORY, 2147483647);
/*      */ 
/* 1484 */   private BooleanConnectionProperty useCursorFetch = new BooleanConnectionProperty("useCursorFetch", false, Messages.getString("ConnectionProperties.useCursorFetch"), "5.0.0", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/* 1490 */   private BooleanConnectionProperty useDynamicCharsetInfo = new BooleanConnectionProperty("useDynamicCharsetInfo", true, Messages.getString("ConnectionProperties.useDynamicCharsetInfo"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1496 */   private BooleanConnectionProperty useDirectRowUnpack = new BooleanConnectionProperty("useDirectRowUnpack", true, "Use newer result set row unpacking code that skips a copy from network buffers  to a MySQL packet instance and instead reads directly into the result set row data buffers.", "5.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1502 */   private BooleanConnectionProperty useFastIntParsing = new BooleanConnectionProperty("useFastIntParsing", true, Messages.getString("ConnectionProperties.useFastIntParsing"), "3.1.4", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1508 */   private BooleanConnectionProperty useFastDateParsing = new BooleanConnectionProperty("useFastDateParsing", true, Messages.getString("ConnectionProperties.useFastDateParsing"), "5.0.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1514 */   private BooleanConnectionProperty useHostsInPrivileges = new BooleanConnectionProperty("useHostsInPrivileges", true, Messages.getString("ConnectionProperties.useHostsInPrivileges"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1519 */   private BooleanConnectionProperty useInformationSchema = new BooleanConnectionProperty("useInformationSchema", false, Messages.getString("ConnectionProperties.useInformationSchema"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1524 */   private BooleanConnectionProperty useJDBCCompliantTimezoneShift = new BooleanConnectionProperty("useJDBCCompliantTimezoneShift", false, Messages.getString("ConnectionProperties.useJDBCCompliantTimezoneShift"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1531 */   private BooleanConnectionProperty useLocalSessionState = new BooleanConnectionProperty("useLocalSessionState", false, Messages.getString("ConnectionProperties.useLocalSessionState"), "3.1.7", PERFORMANCE_CATEGORY, 5);
/*      */ 
/* 1537 */   private BooleanConnectionProperty useLocalTransactionState = new BooleanConnectionProperty("useLocalTransactionState", false, Messages.getString("ConnectionProperties.useLocalTransactionState"), "5.1.7", PERFORMANCE_CATEGORY, 6);
/*      */ 
/* 1543 */   private BooleanConnectionProperty useLegacyDatetimeCode = new BooleanConnectionProperty("useLegacyDatetimeCode", true, Messages.getString("ConnectionProperties.useLegacyDatetimeCode"), "5.1.6", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1549 */   private BooleanConnectionProperty useNanosForElapsedTime = new BooleanConnectionProperty("useNanosForElapsedTime", false, Messages.getString("ConnectionProperties.useNanosForElapsedTime"), "5.0.7", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1556 */   private BooleanConnectionProperty useOldAliasMetadataBehavior = new BooleanConnectionProperty("useOldAliasMetadataBehavior", false, Messages.getString("ConnectionProperties.useOldAliasMetadataBehavior"), "5.0.4", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1564 */   private BooleanConnectionProperty useOldUTF8Behavior = new BooleanConnectionProperty("useOldUTF8Behavior", false, Messages.getString("ConnectionProperties.useOldUtf8Behavior"), "3.1.6", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1570 */   private boolean useOldUTF8BehaviorAsBoolean = false;
/*      */ 
/* 1572 */   private BooleanConnectionProperty useOnlyServerErrorMessages = new BooleanConnectionProperty("useOnlyServerErrorMessages", true, Messages.getString("ConnectionProperties.useOnlyServerErrorMessages"), "3.0.15", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1578 */   private BooleanConnectionProperty useReadAheadInput = new BooleanConnectionProperty("useReadAheadInput", true, Messages.getString("ConnectionProperties.useReadAheadInput"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1584 */   private BooleanConnectionProperty useSqlStateCodes = new BooleanConnectionProperty("useSqlStateCodes", true, Messages.getString("ConnectionProperties.useSqlStateCodes"), "3.1.3", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1590 */   private BooleanConnectionProperty useSSL = new BooleanConnectionProperty("useSSL", false, Messages.getString("ConnectionProperties.useSSL"), "3.0.2", SECURITY_CATEGORY, 2);
/*      */ 
/* 1596 */   private BooleanConnectionProperty useSSPSCompatibleTimezoneShift = new BooleanConnectionProperty("useSSPSCompatibleTimezoneShift", false, Messages.getString("ConnectionProperties.useSSPSCompatibleTimezoneShift"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1602 */   private BooleanConnectionProperty useStreamLengthsInPrepStmts = new BooleanConnectionProperty("useStreamLengthsInPrepStmts", true, Messages.getString("ConnectionProperties.useStreamLengthsInPrepStmts"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1608 */   private BooleanConnectionProperty useTimezone = new BooleanConnectionProperty("useTimezone", false, Messages.getString("ConnectionProperties.useTimezone"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1614 */   private BooleanConnectionProperty useUltraDevWorkAround = new BooleanConnectionProperty("ultraDevHack", false, Messages.getString("ConnectionProperties.ultraDevHack"), "2.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1620 */   private BooleanConnectionProperty useUnbufferedInput = new BooleanConnectionProperty("useUnbufferedInput", true, Messages.getString("ConnectionProperties.useUnbufferedInput"), "3.0.11", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1625 */   private BooleanConnectionProperty useUnicode = new BooleanConnectionProperty("useUnicode", true, Messages.getString("ConnectionProperties.useUnicode"), "1.1g", MISC_CATEGORY, 0);
/*      */ 
/* 1632 */   private boolean useUnicodeAsBoolean = true;
/*      */ 
/* 1634 */   private BooleanConnectionProperty useUsageAdvisor = new BooleanConnectionProperty("useUsageAdvisor", false, Messages.getString("ConnectionProperties.useUsageAdvisor"), "3.1.1", DEBUGING_PROFILING_CATEGORY, 10);
/*      */ 
/* 1640 */   private boolean useUsageAdvisorAsBoolean = false;
/*      */ 
/* 1642 */   private BooleanConnectionProperty yearIsDateType = new BooleanConnectionProperty("yearIsDateType", true, Messages.getString("ConnectionProperties.yearIsDateType"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1648 */   private StringConnectionProperty zeroDateTimeBehavior = new StringConnectionProperty("zeroDateTimeBehavior", "exception", new String[] { "exception", "round", "convertToNull" }, Messages.getString("ConnectionProperties.zeroDateTimeBehavior", new Object[] { "exception", "round", "convertToNull" }), "3.1.4", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1658 */   private BooleanConnectionProperty useJvmCharsetConverters = new BooleanConnectionProperty("useJvmCharsetConverters", false, Messages.getString("ConnectionProperties.useJvmCharsetConverters"), "5.0.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1661 */   private BooleanConnectionProperty useGmtMillisForDatetimes = new BooleanConnectionProperty("useGmtMillisForDatetimes", false, Messages.getString("ConnectionProperties.useGmtMillisForDatetimes"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1663 */   private BooleanConnectionProperty dumpMetadataOnColumnNotFound = new BooleanConnectionProperty("dumpMetadataOnColumnNotFound", false, Messages.getString("ConnectionProperties.dumpMetadataOnColumnNotFound"), "3.1.13", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1667 */   private StringConnectionProperty clientCertificateKeyStoreUrl = new StringConnectionProperty("clientCertificateKeyStoreUrl", null, Messages.getString("ConnectionProperties.clientCertificateKeyStoreUrl"), "5.1.0", SECURITY_CATEGORY, 5);
/*      */ 
/* 1672 */   private StringConnectionProperty trustCertificateKeyStoreUrl = new StringConnectionProperty("trustCertificateKeyStoreUrl", null, Messages.getString("ConnectionProperties.trustCertificateKeyStoreUrl"), "5.1.0", SECURITY_CATEGORY, 8);
/*      */ 
/* 1677 */   private StringConnectionProperty clientCertificateKeyStoreType = new StringConnectionProperty("clientCertificateKeyStoreType", null, Messages.getString("ConnectionProperties.clientCertificateKeyStoreType"), "5.1.0", SECURITY_CATEGORY, 6);
/*      */ 
/* 1682 */   private StringConnectionProperty clientCertificateKeyStorePassword = new StringConnectionProperty("clientCertificateKeyStorePassword", null, Messages.getString("ConnectionProperties.clientCertificateKeyStorePassword"), "5.1.0", SECURITY_CATEGORY, 7);
/*      */ 
/* 1687 */   private StringConnectionProperty trustCertificateKeyStoreType = new StringConnectionProperty("trustCertificateKeyStoreType", null, Messages.getString("ConnectionProperties.trustCertificateKeyStoreType"), "5.1.0", SECURITY_CATEGORY, 9);
/*      */ 
/* 1692 */   private StringConnectionProperty trustCertificateKeyStorePassword = new StringConnectionProperty("trustCertificateKeyStorePassword", null, Messages.getString("ConnectionProperties.trustCertificateKeyStorePassword"), "5.1.0", SECURITY_CATEGORY, 10);
/*      */ 
/* 1697 */   private BooleanConnectionProperty verifyServerCertificate = new BooleanConnectionProperty("verifyServerCertificate", true, Messages.getString("ConnectionProperties.verifyServerCertificate"), "5.1.6", SECURITY_CATEGORY, 4);
/*      */ 
/* 1703 */   private BooleanConnectionProperty useAffectedRows = new BooleanConnectionProperty("useAffectedRows", false, Messages.getString("ConnectionProperties.useAffectedRows"), "5.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1708 */   private StringConnectionProperty passwordCharacterEncoding = new StringConnectionProperty("passwordCharacterEncoding", null, Messages.getString("ConnectionProperties.passwordCharacterEncoding"), "5.1.7", SECURITY_CATEGORY, -2147483648);
/*      */ 
/* 1713 */   private IntegerConnectionProperty maxAllowedPacket = new IntegerConnectionProperty("maxAllowedPacket", -1, Messages.getString("ConnectionProperties.maxAllowedPacket"), "5.1.8", NETWORK_CATEGORY, -2147483648);
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/*  669 */     return null;
/*      */   }
/*      */ 
/*      */   protected static DriverPropertyInfo[] exposeAsDriverPropertyInfo(Properties info, int slotsToReserve)
/*      */     throws SQLException
/*      */   {
/*  688 */     return new ConnectionPropertiesImpl() {  }
/*  688 */     .exposeAsDriverPropertyInfoInternal(info, slotsToReserve);
/*      */   }
/*      */ 
/*      */   protected DriverPropertyInfo[] exposeAsDriverPropertyInfoInternal(Properties info, int slotsToReserve)
/*      */     throws SQLException
/*      */   {
/* 1719 */     initializeProperties(info);
/*      */ 
/* 1721 */     int numProperties = PROPERTY_LIST.size();
/*      */ 
/* 1723 */     int listSize = numProperties + slotsToReserve;
/*      */ 
/* 1725 */     DriverPropertyInfo[] driverProperties = new DriverPropertyInfo[listSize];
/*      */ 
/* 1727 */     for (int i = slotsToReserve; i < listSize; i++) {
/* 1728 */       Field propertyField = (Field)PROPERTY_LIST.get(i - slotsToReserve);
/*      */       try
/*      */       {
/* 1732 */         ConnectionProperty propToExpose = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1735 */         if (info != null) {
/* 1736 */           propToExpose.initializeFrom(info);
/*      */         }
/*      */ 
/* 1740 */         driverProperties[i] = propToExpose.getAsDriverPropertyInfo();
/*      */       } catch (IllegalAccessException iae) {
/* 1742 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.InternalPropertiesFailure"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1747 */     return driverProperties;
/*      */   }
/*      */ 
/*      */   protected Properties exposeAsProperties(Properties info) throws SQLException
/*      */   {
/* 1752 */     if (info == null) {
/* 1753 */       info = new Properties();
/*      */     }
/*      */ 
/* 1756 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 1758 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 1759 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 1763 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1766 */         Object propValue = propToGet.getValueAsObject();
/*      */ 
/* 1768 */         if (propValue != null)
/* 1769 */           info.setProperty(propToGet.getPropertyName(), propValue.toString());
/*      */       }
/*      */       catch (IllegalAccessException iae)
/*      */       {
/* 1773 */         throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1778 */     return info;
/*      */   }
/*      */ 
/*      */   public String exposeAsXml()
/*      */     throws SQLException
/*      */   {
/* 1785 */     StringBuffer xmlBuf = new StringBuffer();
/* 1786 */     xmlBuf.append("<ConnectionProperties>");
/*      */ 
/* 1788 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 1790 */     int numCategories = PROPERTY_CATEGORIES.length;
/*      */ 
/* 1792 */     Map propertyListByCategory = new HashMap();
/*      */ 
/* 1794 */     for (int i = 0; i < numCategories; i++) {
/* 1795 */       propertyListByCategory.put(PROPERTY_CATEGORIES[i], new Map[] { new TreeMap(), new TreeMap() });
/*      */     }
/*      */ 
/* 1805 */     StringConnectionProperty userProp = new StringConnectionProperty("user", null, Messages.getString("ConnectionProperties.Username"), Messages.getString("ConnectionProperties.allVersions"), CONNECTION_AND_AUTH_CATEGORY, -2147483647);
/*      */ 
/* 1809 */     StringConnectionProperty passwordProp = new StringConnectionProperty("password", null, Messages.getString("ConnectionProperties.Password"), Messages.getString("ConnectionProperties.allVersions"), CONNECTION_AND_AUTH_CATEGORY, -2147483646);
/*      */ 
/* 1814 */     Map[] connectionSortMaps = (Map[])propertyListByCategory.get(CONNECTION_AND_AUTH_CATEGORY);
/*      */ 
/* 1816 */     TreeMap userMap = new TreeMap();
/* 1817 */     userMap.put(userProp.getPropertyName(), userProp);
/*      */ 
/* 1819 */     connectionSortMaps[0].put(new Integer(userProp.getOrder()), userMap);
/*      */ 
/* 1821 */     TreeMap passwordMap = new TreeMap();
/* 1822 */     passwordMap.put(passwordProp.getPropertyName(), passwordProp);
/*      */ 
/* 1824 */     connectionSortMaps[0].put(new Integer(passwordProp.getOrder()), passwordMap);
/*      */     try
/*      */     {
/* 1828 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 1829 */         Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */ 
/* 1831 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1833 */         Map[] sortMaps = (Map[])propertyListByCategory.get(propToGet.getCategoryName());
/*      */ 
/* 1835 */         int orderInCategory = propToGet.getOrder();
/*      */ 
/* 1837 */         if (orderInCategory == -2147483648) {
/* 1838 */           sortMaps[1].put(propToGet.getPropertyName(), propToGet);
/*      */         } else {
/* 1840 */           Integer order = new Integer(orderInCategory);
/*      */ 
/* 1842 */           Map orderMap = (Map)sortMaps[0].get(order);
/*      */ 
/* 1844 */           if (orderMap == null) {
/* 1845 */             orderMap = new TreeMap();
/* 1846 */             sortMaps[0].put(order, orderMap);
/*      */           }
/*      */ 
/* 1849 */           orderMap.put(propToGet.getPropertyName(), propToGet);
/*      */         }
/*      */       }
/*      */ 
/* 1853 */       for (int j = 0; j < numCategories; j++) {
/* 1854 */         Map[] sortMaps = (Map[])propertyListByCategory.get(PROPERTY_CATEGORIES[j]);
/*      */ 
/* 1856 */         Iterator orderedIter = sortMaps[0].values().iterator();
/* 1857 */         Iterator alphaIter = sortMaps[1].values().iterator();
/*      */ 
/* 1859 */         xmlBuf.append("\n <PropertyCategory name=\"");
/* 1860 */         xmlBuf.append(PROPERTY_CATEGORIES[j]);
/* 1861 */         xmlBuf.append("\">");
/*      */ 
/* 1863 */         while (orderedIter.hasNext()) {
/* 1864 */           Iterator orderedAlphaIter = ((Map)orderedIter.next()).values().iterator();
/*      */ 
/* 1866 */           while (orderedAlphaIter.hasNext()) {
/* 1867 */             ConnectionProperty propToGet = (ConnectionProperty)orderedAlphaIter.next();
/*      */ 
/* 1870 */             xmlBuf.append("\n  <Property name=\"");
/* 1871 */             xmlBuf.append(propToGet.getPropertyName());
/* 1872 */             xmlBuf.append("\" required=\"");
/* 1873 */             xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */ 
/* 1875 */             xmlBuf.append("\" default=\"");
/*      */ 
/* 1877 */             if (propToGet.getDefaultValue() != null) {
/* 1878 */               xmlBuf.append(propToGet.getDefaultValue());
/*      */             }
/*      */ 
/* 1881 */             xmlBuf.append("\" sortOrder=\"");
/* 1882 */             xmlBuf.append(propToGet.getOrder());
/* 1883 */             xmlBuf.append("\" since=\"");
/* 1884 */             xmlBuf.append(propToGet.sinceVersion);
/* 1885 */             xmlBuf.append("\">\n");
/* 1886 */             xmlBuf.append("    ");
/* 1887 */             xmlBuf.append(propToGet.description);
/* 1888 */             xmlBuf.append("\n  </Property>");
/*      */           }
/*      */         }
/*      */ 
/* 1892 */         while (alphaIter.hasNext()) {
/* 1893 */           ConnectionProperty propToGet = (ConnectionProperty)alphaIter.next();
/*      */ 
/* 1896 */           xmlBuf.append("\n  <Property name=\"");
/* 1897 */           xmlBuf.append(propToGet.getPropertyName());
/* 1898 */           xmlBuf.append("\" required=\"");
/* 1899 */           xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */ 
/* 1901 */           xmlBuf.append("\" default=\"");
/*      */ 
/* 1903 */           if (propToGet.getDefaultValue() != null) {
/* 1904 */             xmlBuf.append(propToGet.getDefaultValue());
/*      */           }
/*      */ 
/* 1907 */           xmlBuf.append("\" sortOrder=\"alpha\" since=\"");
/* 1908 */           xmlBuf.append(propToGet.sinceVersion);
/* 1909 */           xmlBuf.append("\">\n");
/* 1910 */           xmlBuf.append("    ");
/* 1911 */           xmlBuf.append(propToGet.description);
/* 1912 */           xmlBuf.append("\n  </Property>");
/*      */         }
/*      */ 
/* 1915 */         xmlBuf.append("\n </PropertyCategory>");
/*      */       }
/*      */     } catch (IllegalAccessException iae) {
/* 1918 */       throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1922 */     xmlBuf.append("\n</ConnectionProperties>");
/*      */ 
/* 1924 */     return xmlBuf.toString();
/*      */   }
/*      */ 
/*      */   public boolean getAllowLoadLocalInfile()
/*      */   {
/* 1931 */     return this.allowLoadLocalInfile.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAllowMultiQueries()
/*      */   {
/* 1938 */     return this.allowMultiQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAllowNanAndInf()
/*      */   {
/* 1945 */     return this.allowNanAndInf.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAllowUrlInLocalInfile()
/*      */   {
/* 1952 */     return this.allowUrlInLocalInfile.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAlwaysSendSetIsolation()
/*      */   {
/* 1959 */     return this.alwaysSendSetIsolation.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAutoDeserialize()
/*      */   {
/* 1966 */     return this.autoDeserialize.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAutoGenerateTestcaseScript()
/*      */   {
/* 1973 */     return this.autoGenerateTestcaseScriptAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getAutoReconnectForPools()
/*      */   {
/* 1980 */     return this.autoReconnectForPoolsAsBoolean;
/*      */   }
/*      */ 
/*      */   public int getBlobSendChunkSize()
/*      */   {
/* 1987 */     return this.blobSendChunkSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStatements()
/*      */   {
/* 1994 */     return this.cacheCallableStatements.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getCachePreparedStatements()
/*      */   {
/* 2001 */     return ((Boolean)this.cachePreparedStatements.getValueAsObject()).booleanValue();
/*      */   }
/*      */ 
/*      */   public boolean getCacheResultSetMetadata()
/*      */   {
/* 2009 */     return this.cacheResultSetMetaDataAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getCacheServerConfiguration()
/*      */   {
/* 2016 */     return this.cacheServerConfiguration.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getCallableStatementCacheSize()
/*      */   {
/* 2023 */     return this.callableStatementCacheSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getCapitalizeTypeNames()
/*      */   {
/* 2030 */     return this.capitalizeTypeNames.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getCharacterSetResults()
/*      */   {
/* 2037 */     return this.characterSetResults.getValueAsString();
/*      */   }
/*      */ 
/*      */   public boolean getClobberStreamingResults()
/*      */   {
/* 2044 */     return this.clobberStreamingResults.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getClobCharacterEncoding()
/*      */   {
/* 2051 */     return this.clobCharacterEncoding.getValueAsString();
/*      */   }
/*      */ 
/*      */   public String getConnectionCollation()
/*      */   {
/* 2058 */     return this.connectionCollation.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getConnectTimeout()
/*      */   {
/* 2065 */     return this.connectTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getContinueBatchOnError()
/*      */   {
/* 2072 */     return this.continueBatchOnError.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getCreateDatabaseIfNotExist()
/*      */   {
/* 2079 */     return this.createDatabaseIfNotExist.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getDefaultFetchSize()
/*      */   {
/* 2086 */     return this.defaultFetchSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getDontTrackOpenResources()
/*      */   {
/* 2093 */     return this.dontTrackOpenResources.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getDumpQueriesOnException()
/*      */   {
/* 2100 */     return this.dumpQueriesOnException.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getDynamicCalendars()
/*      */   {
/* 2107 */     return this.dynamicCalendars.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getElideSetAutoCommits()
/*      */   {
/* 2114 */     return this.elideSetAutoCommits.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmptyStringsConvertToZero()
/*      */   {
/* 2121 */     return this.emptyStringsConvertToZero.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateLocators()
/*      */   {
/* 2128 */     return this.emulateLocators.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateUnsupportedPstmts()
/*      */   {
/* 2135 */     return this.emulateUnsupportedPstmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEnablePacketDebug()
/*      */   {
/* 2142 */     return this.enablePacketDebug.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getEncoding()
/*      */   {
/* 2149 */     return this.characterEncodingAsString;
/*      */   }
/*      */ 
/*      */   public boolean getExplainSlowQueries()
/*      */   {
/* 2156 */     return this.explainSlowQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getFailOverReadOnly()
/*      */   {
/* 2163 */     return this.failOverReadOnly.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerformanceMetrics()
/*      */   {
/* 2170 */     return this.gatherPerformanceMetrics.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   protected boolean getHighAvailability()
/*      */   {
/* 2179 */     return this.highAvailabilityAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getHoldResultsOpenOverStatementClose()
/*      */   {
/* 2186 */     return this.holdResultsOpenOverStatementClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getIgnoreNonTxTables()
/*      */   {
/* 2193 */     return this.ignoreNonTxTables.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getInitialTimeout()
/*      */   {
/* 2200 */     return this.initialTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getInteractiveClient()
/*      */   {
/* 2207 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getIsInteractiveClient()
/*      */   {
/* 2214 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncation()
/*      */   {
/* 2221 */     return this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getLocatorFetchBufferSize()
/*      */   {
/* 2228 */     return this.locatorFetchBufferSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public String getLogger()
/*      */   {
/* 2235 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public String getLoggerClassName()
/*      */   {
/* 2242 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public boolean getLogSlowQueries()
/*      */   {
/* 2249 */     return this.logSlowQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getMaintainTimeStats()
/*      */   {
/* 2256 */     return this.maintainTimeStatsAsBoolean;
/*      */   }
/*      */ 
/*      */   public int getMaxQuerySizeToLog()
/*      */   {
/* 2263 */     return this.maxQuerySizeToLog.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public int getMaxReconnects()
/*      */   {
/* 2270 */     return this.maxReconnects.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public int getMaxRows()
/*      */   {
/* 2277 */     return this.maxRowsAsInt;
/*      */   }
/*      */ 
/*      */   public int getMetadataCacheSize()
/*      */   {
/* 2284 */     return this.metadataCacheSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getNoDatetimeStringSync()
/*      */   {
/* 2291 */     return this.noDatetimeStringSync.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getNullCatalogMeansCurrent()
/*      */   {
/* 2298 */     return this.nullCatalogMeansCurrent.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getNullNamePatternMatchesAll()
/*      */   {
/* 2305 */     return this.nullNamePatternMatchesAll.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getPacketDebugBufferSize()
/*      */   {
/* 2312 */     return this.packetDebugBufferSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getParanoid()
/*      */   {
/* 2319 */     return this.paranoid.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getPedantic()
/*      */   {
/* 2326 */     return this.pedantic.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSize()
/*      */   {
/* 2333 */     return ((Integer)this.preparedStatementCacheSize.getValueAsObject()).intValue();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSqlLimit()
/*      */   {
/* 2341 */     return ((Integer)this.preparedStatementCacheSqlLimit.getValueAsObject()).intValue();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSql()
/*      */   {
/* 2349 */     return this.profileSQLAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getProfileSQL()
/*      */   {
/* 2356 */     return this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getPropertiesTransform()
/*      */   {
/* 2363 */     return this.propertiesTransform.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getQueriesBeforeRetryMaster()
/*      */   {
/* 2370 */     return this.queriesBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getReconnectAtTxEnd()
/*      */   {
/* 2377 */     return this.reconnectTxAtEndAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getRelaxAutoCommit()
/*      */   {
/* 2384 */     return this.relaxAutoCommit.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getReportMetricsIntervalMillis()
/*      */   {
/* 2391 */     return this.reportMetricsIntervalMillis.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getRequireSSL()
/*      */   {
/* 2398 */     return this.requireSSL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   protected boolean getRetainStatementAfterResultSetClose() {
/* 2402 */     return this.retainStatementAfterResultSetClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRollbackOnPooledClose()
/*      */   {
/* 2409 */     return this.rollbackOnPooledClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRoundRobinLoadBalance()
/*      */   {
/* 2416 */     return this.roundRobinLoadBalance.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRunningCTS13()
/*      */   {
/* 2423 */     return this.runningCTS13.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getSecondsBeforeRetryMaster()
/*      */   {
/* 2430 */     return this.secondsBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public String getServerTimezone()
/*      */   {
/* 2437 */     return this.serverTimezone.getValueAsString();
/*      */   }
/*      */ 
/*      */   public String getSessionVariables()
/*      */   {
/* 2444 */     return this.sessionVariables.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getSlowQueryThresholdMillis()
/*      */   {
/* 2451 */     return this.slowQueryThresholdMillis.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public String getSocketFactoryClassName()
/*      */   {
/* 2458 */     return this.socketFactoryClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getSocketTimeout()
/*      */   {
/* 2465 */     return this.socketTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getStrictFloatingPoint()
/*      */   {
/* 2472 */     return this.strictFloatingPoint.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getStrictUpdates()
/*      */   {
/* 2479 */     return this.strictUpdates.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTinyInt1isBit()
/*      */   {
/* 2486 */     return this.tinyInt1isBit.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTraceProtocol()
/*      */   {
/* 2493 */     return this.traceProtocol.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTransformedBitIsBoolean()
/*      */   {
/* 2500 */     return this.transformedBitIsBoolean.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseCompression()
/*      */   {
/* 2507 */     return this.useCompression.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastIntParsing()
/*      */   {
/* 2514 */     return this.useFastIntParsing.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseHostsInPrivileges()
/*      */   {
/* 2521 */     return this.useHostsInPrivileges.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseInformationSchema()
/*      */   {
/* 2528 */     return this.useInformationSchema.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalSessionState()
/*      */   {
/* 2535 */     return this.useLocalSessionState.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldUTF8Behavior()
/*      */   {
/* 2542 */     return this.useOldUTF8BehaviorAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getUseOnlyServerErrorMessages()
/*      */   {
/* 2549 */     return this.useOnlyServerErrorMessages.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseReadAheadInput()
/*      */   {
/* 2556 */     return this.useReadAheadInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPreparedStmts()
/*      */   {
/* 2563 */     return this.detectServerPreparedStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseSqlStateCodes()
/*      */   {
/* 2570 */     return this.useSqlStateCodes.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSL()
/*      */   {
/* 2577 */     return this.useSSL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseStreamLengthsInPrepStmts()
/*      */   {
/* 2584 */     return this.useStreamLengthsInPrepStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseTimezone()
/*      */   {
/* 2591 */     return this.useTimezone.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUltraDevWorkAround()
/*      */   {
/* 2598 */     return this.useUltraDevWorkAround.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnbufferedInput()
/*      */   {
/* 2605 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnicode()
/*      */   {
/* 2612 */     return this.useUnicodeAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getUseUsageAdvisor()
/*      */   {
/* 2619 */     return this.useUsageAdvisorAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getYearIsDateType()
/*      */   {
/* 2626 */     return this.yearIsDateType.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getZeroDateTimeBehavior()
/*      */   {
/* 2633 */     return this.zeroDateTimeBehavior.getValueAsString();
/*      */   }
/*      */ 
/*      */   protected void initializeFromRef(Reference ref)
/*      */     throws SQLException
/*      */   {
/* 2647 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 2649 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 2650 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 2654 */         ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 2657 */         if (ref != null)
/* 2658 */           propToSet.initializeFrom(ref);
/*      */       }
/*      */       catch (IllegalAccessException iae) {
/* 2661 */         throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2666 */     postInitialization();
/*      */   }
/*      */ 
/*      */   protected void initializeProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 2679 */     if (info != null)
/*      */     {
/* 2681 */       String profileSqlLc = info.getProperty("profileSql");
/*      */ 
/* 2683 */       if (profileSqlLc != null) {
/* 2684 */         info.put("profileSQL", profileSqlLc);
/*      */       }
/*      */ 
/* 2687 */       Properties infoCopy = (Properties)info.clone();
/*      */ 
/* 2689 */       infoCopy.remove("HOST");
/* 2690 */       infoCopy.remove("user");
/* 2691 */       infoCopy.remove("password");
/* 2692 */       infoCopy.remove("DBNAME");
/* 2693 */       infoCopy.remove("PORT");
/* 2694 */       infoCopy.remove("profileSql");
/*      */ 
/* 2696 */       int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 2698 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 2699 */         Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */         try
/*      */         {
/* 2703 */           ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 2706 */           propToSet.initializeFrom(infoCopy);
/*      */         } catch (IllegalAccessException iae) {
/* 2708 */           throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unableToInitDriverProperties") + iae.toString(), "S1000", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2715 */       postInitialization();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void postInitialization()
/*      */     throws SQLException
/*      */   {
/* 2722 */     if (this.profileSql.getValueAsObject() != null) {
/* 2723 */       this.profileSQL.initializeFrom(this.profileSql.getValueAsObject().toString());
/*      */     }
/*      */ 
/* 2727 */     this.reconnectTxAtEndAsBoolean = ((Boolean)this.reconnectAtTxEnd.getValueAsObject()).booleanValue();
/*      */ 
/* 2731 */     if (getMaxRows() == 0)
/*      */     {
/* 2734 */       this.maxRows.setValueAsObject(Constants.integerValueOf(-1));
/*      */     }
/*      */ 
/* 2740 */     String testEncoding = getEncoding();
/*      */ 
/* 2742 */     if (testEncoding != null)
/*      */     {
/*      */       try
/*      */       {
/* 2746 */         String testString = "abc";
/* 2747 */         testString.getBytes(testEncoding);
/*      */       } catch (UnsupportedEncodingException UE) {
/* 2749 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unsupportedCharacterEncoding", new Object[] { testEncoding }), "0S100", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2758 */     if (((Boolean)this.cacheResultSetMetadata.getValueAsObject()).booleanValue()) {
/*      */       try
/*      */       {
/* 2761 */         Class.forName("java.util.LinkedHashMap");
/*      */       } catch (ClassNotFoundException cnfe) {
/* 2763 */         this.cacheResultSetMetadata.setValue(false);
/*      */       }
/*      */     }
/*      */ 
/* 2767 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */ 
/* 2769 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/* 2770 */     this.characterEncodingAsString = ((String)this.characterEncoding.getValueAsObject());
/*      */ 
/* 2772 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/* 2773 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */ 
/* 2775 */     this.maxRowsAsInt = ((Integer)this.maxRows.getValueAsObject()).intValue();
/*      */ 
/* 2777 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/* 2778 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */ 
/* 2780 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */ 
/* 2782 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */ 
/* 2784 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */ 
/* 2786 */     this.jdbcCompliantTruncationForReads = getJdbcCompliantTruncation();
/*      */ 
/* 2788 */     if (getUseCursorFetch())
/*      */     {
/* 2791 */       setDetectServerPreparedStmts(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAllowLoadLocalInfile(boolean property)
/*      */   {
/* 2799 */     this.allowLoadLocalInfile.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setAllowMultiQueries(boolean property)
/*      */   {
/* 2806 */     this.allowMultiQueries.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setAllowNanAndInf(boolean flag)
/*      */   {
/* 2813 */     this.allowNanAndInf.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAllowUrlInLocalInfile(boolean flag)
/*      */   {
/* 2820 */     this.allowUrlInLocalInfile.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAlwaysSendSetIsolation(boolean flag)
/*      */   {
/* 2827 */     this.alwaysSendSetIsolation.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoDeserialize(boolean flag)
/*      */   {
/* 2834 */     this.autoDeserialize.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoGenerateTestcaseScript(boolean flag)
/*      */   {
/* 2841 */     this.autoGenerateTestcaseScript.setValue(flag);
/* 2842 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoReconnect(boolean flag)
/*      */   {
/* 2850 */     this.autoReconnect.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForConnectionPools(boolean property)
/*      */   {
/* 2857 */     this.autoReconnectForPools.setValue(property);
/* 2858 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForPools(boolean flag)
/*      */   {
/* 2866 */     this.autoReconnectForPools.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setBlobSendChunkSize(String value)
/*      */     throws SQLException
/*      */   {
/* 2873 */     this.blobSendChunkSize.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStatements(boolean flag)
/*      */   {
/* 2880 */     this.cacheCallableStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePreparedStatements(boolean flag)
/*      */   {
/* 2887 */     this.cachePreparedStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheResultSetMetadata(boolean property)
/*      */   {
/* 2894 */     this.cacheResultSetMetadata.setValue(property);
/* 2895 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setCacheServerConfiguration(boolean flag)
/*      */   {
/* 2903 */     this.cacheServerConfiguration.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCallableStatementCacheSize(int size)
/*      */   {
/* 2910 */     this.callableStatementCacheSize.setValue(size);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeDBMDTypes(boolean property)
/*      */   {
/* 2917 */     this.capitalizeTypeNames.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeTypeNames(boolean flag)
/*      */   {
/* 2924 */     this.capitalizeTypeNames.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCharacterEncoding(String encoding)
/*      */   {
/* 2931 */     this.characterEncoding.setValue(encoding);
/*      */   }
/*      */ 
/*      */   public void setCharacterSetResults(String characterSet)
/*      */   {
/* 2938 */     this.characterSetResults.setValue(characterSet);
/*      */   }
/*      */ 
/*      */   public void setClobberStreamingResults(boolean flag)
/*      */   {
/* 2945 */     this.clobberStreamingResults.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setClobCharacterEncoding(String encoding)
/*      */   {
/* 2952 */     this.clobCharacterEncoding.setValue(encoding);
/*      */   }
/*      */ 
/*      */   public void setConnectionCollation(String collation)
/*      */   {
/* 2959 */     this.connectionCollation.setValue(collation);
/*      */   }
/*      */ 
/*      */   public void setConnectTimeout(int timeoutMs)
/*      */   {
/* 2966 */     this.connectTimeout.setValue(timeoutMs);
/*      */   }
/*      */ 
/*      */   public void setContinueBatchOnError(boolean property)
/*      */   {
/* 2973 */     this.continueBatchOnError.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setCreateDatabaseIfNotExist(boolean flag)
/*      */   {
/* 2980 */     this.createDatabaseIfNotExist.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDefaultFetchSize(int n)
/*      */   {
/* 2987 */     this.defaultFetchSize.setValue(n);
/*      */   }
/*      */ 
/*      */   public void setDetectServerPreparedStmts(boolean property)
/*      */   {
/* 2994 */     this.detectServerPreparedStmts.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setDontTrackOpenResources(boolean flag)
/*      */   {
/* 3001 */     this.dontTrackOpenResources.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpQueriesOnException(boolean flag)
/*      */   {
/* 3008 */     this.dumpQueriesOnException.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDynamicCalendars(boolean flag)
/*      */   {
/* 3015 */     this.dynamicCalendars.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setElideSetAutoCommits(boolean flag)
/*      */   {
/* 3022 */     this.elideSetAutoCommits.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEmptyStringsConvertToZero(boolean flag)
/*      */   {
/* 3029 */     this.emptyStringsConvertToZero.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEmulateLocators(boolean property)
/*      */   {
/* 3036 */     this.emulateLocators.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setEmulateUnsupportedPstmts(boolean flag)
/*      */   {
/* 3043 */     this.emulateUnsupportedPstmts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEnablePacketDebug(boolean flag)
/*      */   {
/* 3050 */     this.enablePacketDebug.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEncoding(String property)
/*      */   {
/* 3057 */     this.characterEncoding.setValue(property);
/* 3058 */     this.characterEncodingAsString = this.characterEncoding.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setExplainSlowQueries(boolean flag)
/*      */   {
/* 3066 */     this.explainSlowQueries.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setFailOverReadOnly(boolean flag)
/*      */   {
/* 3073 */     this.failOverReadOnly.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerformanceMetrics(boolean flag)
/*      */   {
/* 3080 */     this.gatherPerformanceMetrics.setValue(flag);
/*      */   }
/*      */ 
/*      */   protected void setHighAvailability(boolean property)
/*      */   {
/* 3089 */     this.autoReconnect.setValue(property);
/* 3090 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag)
/*      */   {
/* 3097 */     this.holdResultsOpenOverStatementClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setIgnoreNonTxTables(boolean property)
/*      */   {
/* 3104 */     this.ignoreNonTxTables.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setInitialTimeout(int property)
/*      */   {
/* 3111 */     this.initialTimeout.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setIsInteractiveClient(boolean property)
/*      */   {
/* 3118 */     this.isInteractiveClient.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncation(boolean flag)
/*      */   {
/* 3125 */     this.jdbcCompliantTruncation.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setLocatorFetchBufferSize(String value)
/*      */     throws SQLException
/*      */   {
/* 3132 */     this.locatorFetchBufferSize.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setLogger(String property)
/*      */   {
/* 3139 */     this.loggerClassName.setValueAsObject(property);
/*      */   }
/*      */ 
/*      */   public void setLoggerClassName(String className)
/*      */   {
/* 3146 */     this.loggerClassName.setValue(className);
/*      */   }
/*      */ 
/*      */   public void setLogSlowQueries(boolean flag)
/*      */   {
/* 3153 */     this.logSlowQueries.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setMaintainTimeStats(boolean flag)
/*      */   {
/* 3160 */     this.maintainTimeStats.setValue(flag);
/* 3161 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes)
/*      */   {
/* 3169 */     this.maxQuerySizeToLog.setValue(sizeInBytes);
/*      */   }
/*      */ 
/*      */   public void setMaxReconnects(int property)
/*      */   {
/* 3176 */     this.maxReconnects.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int property)
/*      */   {
/* 3183 */     this.maxRows.setValue(property);
/* 3184 */     this.maxRowsAsInt = this.maxRows.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setMetadataCacheSize(int value)
/*      */   {
/* 3191 */     this.metadataCacheSize.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setNoDatetimeStringSync(boolean flag)
/*      */   {
/* 3198 */     this.noDatetimeStringSync.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setNullCatalogMeansCurrent(boolean value)
/*      */   {
/* 3205 */     this.nullCatalogMeansCurrent.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setNullNamePatternMatchesAll(boolean value)
/*      */   {
/* 3212 */     this.nullNamePatternMatchesAll.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setPacketDebugBufferSize(int size)
/*      */   {
/* 3219 */     this.packetDebugBufferSize.setValue(size);
/*      */   }
/*      */ 
/*      */   public void setParanoid(boolean property)
/*      */   {
/* 3226 */     this.paranoid.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setPedantic(boolean property)
/*      */   {
/* 3233 */     this.pedantic.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSize(int cacheSize)
/*      */   {
/* 3240 */     this.preparedStatementCacheSize.setValue(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit)
/*      */   {
/* 3247 */     this.preparedStatementCacheSqlLimit.setValue(cacheSqlLimit);
/*      */   }
/*      */ 
/*      */   public void setProfileSql(boolean property)
/*      */   {
/* 3254 */     this.profileSQL.setValue(property);
/* 3255 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setProfileSQL(boolean flag)
/*      */   {
/* 3262 */     this.profileSQL.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setPropertiesTransform(String value)
/*      */   {
/* 3269 */     this.propertiesTransform.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setQueriesBeforeRetryMaster(int property)
/*      */   {
/* 3276 */     this.queriesBeforeRetryMaster.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setReconnectAtTxEnd(boolean property)
/*      */   {
/* 3283 */     this.reconnectAtTxEnd.setValue(property);
/* 3284 */     this.reconnectTxAtEndAsBoolean = this.reconnectAtTxEnd.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setRelaxAutoCommit(boolean property)
/*      */   {
/* 3292 */     this.relaxAutoCommit.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setReportMetricsIntervalMillis(int millis)
/*      */   {
/* 3299 */     this.reportMetricsIntervalMillis.setValue(millis);
/*      */   }
/*      */ 
/*      */   public void setRequireSSL(boolean property)
/*      */   {
/* 3306 */     this.requireSSL.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag)
/*      */   {
/* 3313 */     this.retainStatementAfterResultSetClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRollbackOnPooledClose(boolean flag)
/*      */   {
/* 3320 */     this.rollbackOnPooledClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRoundRobinLoadBalance(boolean flag)
/*      */   {
/* 3327 */     this.roundRobinLoadBalance.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRunningCTS13(boolean flag)
/*      */   {
/* 3334 */     this.runningCTS13.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setSecondsBeforeRetryMaster(int property)
/*      */   {
/* 3341 */     this.secondsBeforeRetryMaster.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setServerTimezone(String property)
/*      */   {
/* 3348 */     this.serverTimezone.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setSessionVariables(String variables)
/*      */   {
/* 3355 */     this.sessionVariables.setValue(variables);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdMillis(int millis)
/*      */   {
/* 3362 */     this.slowQueryThresholdMillis.setValue(millis);
/*      */   }
/*      */ 
/*      */   public void setSocketFactoryClassName(String property)
/*      */   {
/* 3369 */     this.socketFactoryClassName.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setSocketTimeout(int property)
/*      */   {
/* 3376 */     this.socketTimeout.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setStrictFloatingPoint(boolean property)
/*      */   {
/* 3383 */     this.strictFloatingPoint.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setStrictUpdates(boolean property)
/*      */   {
/* 3390 */     this.strictUpdates.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setTinyInt1isBit(boolean flag)
/*      */   {
/* 3397 */     this.tinyInt1isBit.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setTraceProtocol(boolean flag)
/*      */   {
/* 3404 */     this.traceProtocol.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setTransformedBitIsBoolean(boolean flag)
/*      */   {
/* 3411 */     this.transformedBitIsBoolean.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseCompression(boolean property)
/*      */   {
/* 3418 */     this.useCompression.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseFastIntParsing(boolean flag)
/*      */   {
/* 3425 */     this.useFastIntParsing.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseHostsInPrivileges(boolean property)
/*      */   {
/* 3432 */     this.useHostsInPrivileges.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseInformationSchema(boolean flag)
/*      */   {
/* 3439 */     this.useInformationSchema.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseLocalSessionState(boolean flag)
/*      */   {
/* 3446 */     this.useLocalSessionState.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOldUTF8Behavior(boolean flag)
/*      */   {
/* 3453 */     this.useOldUTF8Behavior.setValue(flag);
/* 3454 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseOnlyServerErrorMessages(boolean flag)
/*      */   {
/* 3462 */     this.useOnlyServerErrorMessages.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseReadAheadInput(boolean flag)
/*      */   {
/* 3469 */     this.useReadAheadInput.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPreparedStmts(boolean flag)
/*      */   {
/* 3476 */     this.detectServerPreparedStmts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSqlStateCodes(boolean flag)
/*      */   {
/* 3483 */     this.useSqlStateCodes.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSSL(boolean property)
/*      */   {
/* 3490 */     this.useSSL.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property)
/*      */   {
/* 3497 */     this.useStreamLengthsInPrepStmts.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseTimezone(boolean property)
/*      */   {
/* 3504 */     this.useTimezone.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseUltraDevWorkAround(boolean property)
/*      */   {
/* 3511 */     this.useUltraDevWorkAround.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseUnbufferedInput(boolean flag)
/*      */   {
/* 3518 */     this.useUnbufferedInput.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUnicode(boolean flag)
/*      */   {
/* 3525 */     this.useUnicode.setValue(flag);
/* 3526 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag)
/*      */   {
/* 3533 */     this.useUsageAdvisor.setValue(useUsageAdvisorFlag);
/* 3534 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setYearIsDateType(boolean flag)
/*      */   {
/* 3542 */     this.yearIsDateType.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setZeroDateTimeBehavior(String behavior)
/*      */   {
/* 3549 */     this.zeroDateTimeBehavior.setValue(behavior);
/*      */   }
/*      */ 
/*      */   protected void storeToRef(Reference ref) throws SQLException {
/* 3553 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 3555 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 3556 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 3560 */         ConnectionProperty propToStore = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 3563 */         if (ref != null)
/* 3564 */           propToStore.storeTo(ref);
/*      */       }
/*      */       catch (IllegalAccessException iae) {
/* 3567 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.errorNotExpected"), getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean useUnbufferedInput()
/*      */   {
/* 3576 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseCursorFetch()
/*      */   {
/* 3583 */     return this.useCursorFetch.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseCursorFetch(boolean flag)
/*      */   {
/* 3590 */     this.useCursorFetch.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getOverrideSupportsIntegrityEnhancementFacility()
/*      */   {
/* 3597 */     return this.overrideSupportsIntegrityEnhancementFacility.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag)
/*      */   {
/* 3604 */     this.overrideSupportsIntegrityEnhancementFacility.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getNoTimezoneConversionForTimeType()
/*      */   {
/* 3611 */     return this.noTimezoneConversionForTimeType.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag)
/*      */   {
/* 3618 */     this.noTimezoneConversionForTimeType.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseJDBCCompliantTimezoneShift()
/*      */   {
/* 3625 */     return this.useJDBCCompliantTimezoneShift.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag)
/*      */   {
/* 3632 */     this.useJDBCCompliantTimezoneShift.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getAutoClosePStmtStreams()
/*      */   {
/* 3639 */     return this.autoClosePStmtStreams.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoClosePStmtStreams(boolean flag)
/*      */   {
/* 3646 */     this.autoClosePStmtStreams.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getProcessEscapeCodesForPrepStmts()
/*      */   {
/* 3653 */     return this.processEscapeCodesForPrepStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag)
/*      */   {
/* 3660 */     this.processEscapeCodesForPrepStmts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseGmtMillisForDatetimes()
/*      */   {
/* 3667 */     return this.useGmtMillisForDatetimes.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseGmtMillisForDatetimes(boolean flag)
/*      */   {
/* 3674 */     this.useGmtMillisForDatetimes.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getDumpMetadataOnColumnNotFound()
/*      */   {
/* 3681 */     return this.dumpMetadataOnColumnNotFound.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag)
/*      */   {
/* 3688 */     this.dumpMetadataOnColumnNotFound.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getResourceId()
/*      */   {
/* 3695 */     return this.resourceId.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setResourceId(String resourceId)
/*      */   {
/* 3702 */     this.resourceId.setValue(resourceId);
/*      */   }
/*      */ 
/*      */   public boolean getRewriteBatchedStatements()
/*      */   {
/* 3709 */     return this.rewriteBatchedStatements.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setRewriteBatchedStatements(boolean flag)
/*      */   {
/* 3716 */     this.rewriteBatchedStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncationForReads()
/*      */   {
/* 3723 */     return this.jdbcCompliantTruncationForReads;
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/* 3731 */     this.jdbcCompliantTruncationForReads = jdbcCompliantTruncationForReads;
/*      */   }
/*      */ 
/*      */   public boolean getUseJvmCharsetConverters()
/*      */   {
/* 3738 */     return this.useJvmCharsetConverters.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseJvmCharsetConverters(boolean flag)
/*      */   {
/* 3745 */     this.useJvmCharsetConverters.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getPinGlobalTxToPhysicalConnection()
/*      */   {
/* 3752 */     return this.pinGlobalTxToPhysicalConnection.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setPinGlobalTxToPhysicalConnection(boolean flag)
/*      */   {
/* 3759 */     this.pinGlobalTxToPhysicalConnection.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerfMetrics(boolean flag)
/*      */   {
/* 3771 */     setGatherPerformanceMetrics(flag);
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerfMetrics()
/*      */   {
/* 3778 */     return getGatherPerformanceMetrics();
/*      */   }
/*      */ 
/*      */   public void setUltraDevHack(boolean flag)
/*      */   {
/* 3785 */     setUseUltraDevWorkAround(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUltraDevHack()
/*      */   {
/* 3792 */     return getUseUltraDevWorkAround();
/*      */   }
/*      */ 
/*      */   public void setInteractiveClient(boolean property)
/*      */   {
/* 3799 */     setIsInteractiveClient(property);
/*      */   }
/*      */ 
/*      */   public void setSocketFactory(String name)
/*      */   {
/* 3806 */     setSocketFactoryClassName(name);
/*      */   }
/*      */ 
/*      */   public String getSocketFactory()
/*      */   {
/* 3813 */     return getSocketFactoryClassName();
/*      */   }
/*      */ 
/*      */   public void setUseServerPrepStmts(boolean flag)
/*      */   {
/* 3820 */     setUseServerPreparedStmts(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPrepStmts()
/*      */   {
/* 3827 */     return getUseServerPreparedStmts();
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStmts(boolean flag)
/*      */   {
/* 3834 */     setCacheCallableStatements(flag);
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStmts()
/*      */   {
/* 3841 */     return getCacheCallableStatements();
/*      */   }
/*      */ 
/*      */   public void setCachePrepStmts(boolean flag)
/*      */   {
/* 3848 */     setCachePreparedStatements(flag);
/*      */   }
/*      */ 
/*      */   public boolean getCachePrepStmts()
/*      */   {
/* 3855 */     return getCachePreparedStatements();
/*      */   }
/*      */ 
/*      */   public void setCallableStmtCacheSize(int cacheSize)
/*      */   {
/* 3862 */     setCallableStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public int getCallableStmtCacheSize()
/*      */   {
/* 3869 */     return getCallableStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSize(int cacheSize)
/*      */   {
/* 3876 */     setPreparedStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSize()
/*      */   {
/* 3883 */     return getPreparedStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit)
/*      */   {
/* 3890 */     setPreparedStatementCacheSqlLimit(sqlLimit);
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSqlLimit()
/*      */   {
/* 3897 */     return getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public boolean getNoAccessToProcedureBodies()
/*      */   {
/* 3904 */     return this.noAccessToProcedureBodies.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setNoAccessToProcedureBodies(boolean flag)
/*      */   {
/* 3911 */     this.noAccessToProcedureBodies.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseOldAliasMetadataBehavior()
/*      */   {
/* 3918 */     return this.useOldAliasMetadataBehavior.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag)
/*      */   {
/* 3925 */     this.useOldAliasMetadataBehavior.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStorePassword()
/*      */   {
/* 3932 */     return this.clientCertificateKeyStorePassword.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStorePassword(String value)
/*      */   {
/* 3940 */     this.clientCertificateKeyStorePassword.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreType()
/*      */   {
/* 3947 */     return this.clientCertificateKeyStoreType.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreType(String value)
/*      */   {
/* 3955 */     this.clientCertificateKeyStoreType.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreUrl()
/*      */   {
/* 3962 */     return this.clientCertificateKeyStoreUrl.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreUrl(String value)
/*      */   {
/* 3970 */     this.clientCertificateKeyStoreUrl.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStorePassword()
/*      */   {
/* 3977 */     return this.trustCertificateKeyStorePassword.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStorePassword(String value)
/*      */   {
/* 3985 */     this.trustCertificateKeyStorePassword.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreType()
/*      */   {
/* 3992 */     return this.trustCertificateKeyStoreType.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreType(String value)
/*      */   {
/* 4000 */     this.trustCertificateKeyStoreType.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreUrl()
/*      */   {
/* 4007 */     return this.trustCertificateKeyStoreUrl.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreUrl(String value)
/*      */   {
/* 4015 */     this.trustCertificateKeyStoreUrl.setValue(value);
/*      */   }
/*      */ 
/*      */   public boolean getUseSSPSCompatibleTimezoneShift()
/*      */   {
/* 4022 */     return this.useSSPSCompatibleTimezoneShift.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag)
/*      */   {
/* 4029 */     this.useSSPSCompatibleTimezoneShift.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getTreatUtilDateAsTimestamp()
/*      */   {
/* 4036 */     return this.treatUtilDateAsTimestamp.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag)
/*      */   {
/* 4043 */     this.treatUtilDateAsTimestamp.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseFastDateParsing()
/*      */   {
/* 4050 */     return this.useFastDateParsing.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseFastDateParsing(boolean flag)
/*      */   {
/* 4057 */     this.useFastDateParsing.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getLocalSocketAddress()
/*      */   {
/* 4064 */     return this.localSocketAddress.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLocalSocketAddress(String address)
/*      */   {
/* 4071 */     this.localSocketAddress.setValue(address);
/*      */   }
/*      */ 
/*      */   public void setUseConfigs(String configs)
/*      */   {
/* 4078 */     this.useConfigs.setValue(configs);
/*      */   }
/*      */ 
/*      */   public String getUseConfigs()
/*      */   {
/* 4085 */     return this.useConfigs.getValueAsString();
/*      */   }
/*      */ 
/*      */   public boolean getGenerateSimpleParameterMetadata()
/*      */   {
/* 4093 */     return this.generateSimpleParameterMetadata.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag)
/*      */   {
/* 4100 */     this.generateSimpleParameterMetadata.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getLogXaCommands()
/*      */   {
/* 4107 */     return this.logXaCommands.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setLogXaCommands(boolean flag)
/*      */   {
/* 4114 */     this.logXaCommands.setValue(flag);
/*      */   }
/*      */ 
/*      */   public int getResultSetSizeThreshold()
/*      */   {
/* 4121 */     return this.resultSetSizeThreshold.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setResultSetSizeThreshold(int threshold)
/*      */   {
/* 4128 */     this.resultSetSizeThreshold.setValue(threshold);
/*      */   }
/*      */ 
/*      */   public int getNetTimeoutForStreamingResults()
/*      */   {
/* 4135 */     return this.netTimeoutForStreamingResults.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setNetTimeoutForStreamingResults(int value)
/*      */   {
/* 4142 */     this.netTimeoutForStreamingResults.setValue(value);
/*      */   }
/*      */ 
/*      */   public boolean getEnableQueryTimeouts()
/*      */   {
/* 4149 */     return this.enableQueryTimeouts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setEnableQueryTimeouts(boolean flag)
/*      */   {
/* 4156 */     this.enableQueryTimeouts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getPadCharsWithSpace()
/*      */   {
/* 4163 */     return this.padCharsWithSpace.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setPadCharsWithSpace(boolean flag)
/*      */   {
/* 4170 */     this.padCharsWithSpace.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseDynamicCharsetInfo()
/*      */   {
/* 4177 */     return this.useDynamicCharsetInfo.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseDynamicCharsetInfo(boolean flag)
/*      */   {
/* 4184 */     this.useDynamicCharsetInfo.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getClientInfoProvider()
/*      */   {
/* 4191 */     return this.clientInfoProvider.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setClientInfoProvider(String classname)
/*      */   {
/* 4198 */     this.clientInfoProvider.setValue(classname);
/*      */   }
/*      */ 
/*      */   public boolean getPopulateInsertRowWithDefaultValues() {
/* 4202 */     return this.populateInsertRowWithDefaultValues.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag) {
/* 4206 */     this.populateInsertRowWithDefaultValues.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceStrategy() {
/* 4210 */     return this.loadBalanceStrategy.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceStrategy(String strategy) {
/* 4214 */     this.loadBalanceStrategy.setValue(strategy);
/*      */   }
/*      */ 
/*      */   public boolean getTcpNoDelay() {
/* 4218 */     return this.tcpNoDelay.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setTcpNoDelay(boolean flag) {
/* 4222 */     this.tcpNoDelay.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getTcpKeepAlive() {
/* 4226 */     return this.tcpKeepAlive.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setTcpKeepAlive(boolean flag) {
/* 4230 */     this.tcpKeepAlive.setValue(flag);
/*      */   }
/*      */ 
/*      */   public int getTcpRcvBuf() {
/* 4234 */     return this.tcpRcvBuf.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setTcpRcvBuf(int bufSize) {
/* 4238 */     this.tcpRcvBuf.setValue(bufSize);
/*      */   }
/*      */ 
/*      */   public int getTcpSndBuf() {
/* 4242 */     return this.tcpSndBuf.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setTcpSndBuf(int bufSize) {
/* 4246 */     this.tcpSndBuf.setValue(bufSize);
/*      */   }
/*      */ 
/*      */   public int getTcpTrafficClass() {
/* 4250 */     return this.tcpTrafficClass.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setTcpTrafficClass(int classFlags) {
/* 4254 */     this.tcpTrafficClass.setValue(classFlags);
/*      */   }
/*      */ 
/*      */   public boolean getUseNanosForElapsedTime() {
/* 4258 */     return this.useNanosForElapsedTime.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseNanosForElapsedTime(boolean flag) {
/* 4262 */     this.useNanosForElapsedTime.setValue(flag);
/*      */   }
/*      */ 
/*      */   public long getSlowQueryThresholdNanos() {
/* 4266 */     return this.slowQueryThresholdNanos.getValueAsLong();
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdNanos(long nanos) {
/* 4270 */     this.slowQueryThresholdNanos.setValue(nanos);
/*      */   }
/*      */ 
/*      */   public String getStatementInterceptors() {
/* 4274 */     return this.statementInterceptors.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setStatementInterceptors(String value) {
/* 4278 */     this.statementInterceptors.setValue(value);
/*      */   }
/*      */ 
/*      */   public boolean getUseDirectRowUnpack() {
/* 4282 */     return this.useDirectRowUnpack.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseDirectRowUnpack(boolean flag) {
/* 4286 */     this.useDirectRowUnpack.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getLargeRowSizeThreshold() {
/* 4290 */     return this.largeRowSizeThreshold.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLargeRowSizeThreshold(String value) {
/*      */     try {
/* 4295 */       this.largeRowSizeThreshold.setValue(value);
/*      */     } catch (SQLException sqlEx) {
/* 4297 */       RuntimeException ex = new RuntimeException(sqlEx.getMessage());
/* 4298 */       ex.initCause(sqlEx);
/*      */ 
/* 4300 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 4305 */     return this.useBlobToStoreUTF8OutsideBMP.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
/* 4309 */     this.useBlobToStoreUTF8OutsideBMP.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 4313 */     return this.utf8OutsideBmpExcludedColumnNamePattern.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
/* 4317 */     this.utf8OutsideBmpExcludedColumnNamePattern.setValue(regexPattern);
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 4321 */     return this.utf8OutsideBmpIncludedColumnNamePattern.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
/* 4325 */     this.utf8OutsideBmpIncludedColumnNamePattern.setValue(regexPattern);
/*      */   }
/*      */ 
/*      */   public boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 4329 */     return this.includeInnodbStatusInDeadlockExceptions.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
/* 4333 */     this.includeInnodbStatusInDeadlockExceptions.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getBlobsAreStrings() {
/* 4337 */     return this.blobsAreStrings.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setBlobsAreStrings(boolean flag) {
/* 4341 */     this.blobsAreStrings.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getFunctionsNeverReturnBlobs() {
/* 4345 */     return this.functionsNeverReturnBlobs.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag) {
/* 4349 */     this.functionsNeverReturnBlobs.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getAutoSlowLog() {
/* 4353 */     return this.autoSlowLog.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoSlowLog(boolean flag) {
/* 4357 */     this.autoSlowLog.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getConnectionLifecycleInterceptors() {
/* 4361 */     return this.connectionLifecycleInterceptors.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setConnectionLifecycleInterceptors(String interceptors) {
/* 4365 */     this.connectionLifecycleInterceptors.setValue(interceptors);
/*      */   }
/*      */ 
/*      */   public String getProfilerEventHandler() {
/* 4369 */     return this.profilerEventHandler.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setProfilerEventHandler(String handler) {
/* 4373 */     this.profilerEventHandler.setValue(handler);
/*      */   }
/*      */ 
/*      */   public boolean getVerifyServerCertificate() {
/* 4377 */     return this.verifyServerCertificate.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setVerifyServerCertificate(boolean flag) {
/* 4381 */     this.verifyServerCertificate.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseLegacyDatetimeCode() {
/* 4385 */     return this.useLegacyDatetimeCode.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseLegacyDatetimeCode(boolean flag) {
/* 4389 */     this.useLegacyDatetimeCode.setValue(flag);
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingSecondsLifetime() {
/* 4393 */     return this.selfDestructOnPingSecondsLifetime.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds) {
/* 4397 */     this.selfDestructOnPingSecondsLifetime.setValue(seconds);
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingMaxOperations() {
/* 4401 */     return this.selfDestructOnPingMaxOperations.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations) {
/* 4405 */     this.selfDestructOnPingMaxOperations.setValue(maxOperations);
/*      */   }
/*      */ 
/*      */   public boolean getUseColumnNamesInFindColumn() {
/* 4409 */     return this.useColumnNamesInFindColumn.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseColumnNamesInFindColumn(boolean flag) {
/* 4413 */     this.useColumnNamesInFindColumn.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalTransactionState() {
/* 4417 */     return this.useLocalTransactionState.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseLocalTransactionState(boolean flag) {
/* 4421 */     this.useLocalTransactionState.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getCompensateOnDuplicateKeyUpdateCounts() {
/* 4425 */     return this.compensateOnDuplicateKeyUpdateCounts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
/* 4429 */     this.compensateOnDuplicateKeyUpdateCounts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceBlacklistTimeout() {
/* 4433 */     return this.loadBalanceBlacklistTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) {
/* 4437 */     this.loadBalanceBlacklistTimeout.setValue(loadBalanceBlacklistTimeout);
/*      */   }
/*      */ 
/*      */   public void setRetriesAllDown(int retriesAllDown) {
/* 4441 */     this.retriesAllDown.setValue(retriesAllDown);
/*      */   }
/*      */ 
/*      */   public int getRetriesAllDown() {
/* 4445 */     return this.retriesAllDown.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setUseAffectedRows(boolean flag) {
/* 4449 */     this.useAffectedRows.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseAffectedRows() {
/* 4453 */     return this.useAffectedRows.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setPasswordCharacterEncoding(String characterSet) {
/* 4457 */     this.passwordCharacterEncoding.setValue(characterSet);
/*      */   }
/*      */ 
/*      */   public String getPasswordCharacterEncoding() {
/* 4461 */     return this.passwordCharacterEncoding.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setExceptionInterceptors(String exceptionInterceptors) {
/* 4465 */     this.exceptionInterceptors.setValue(exceptionInterceptors);
/*      */   }
/*      */ 
/*      */   public String getExceptionInterceptors() {
/* 4469 */     return this.exceptionInterceptors.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setMaxAllowedPacket(int max) {
/* 4473 */     this.maxAllowedPacket.setValue(max);
/*      */   }
/*      */ 
/*      */   public int getMaxAllowedPacket() {
/* 4477 */     return this.maxAllowedPacket.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getQueryTimeoutKillsConnection() {
/* 4481 */     return this.queryTimeoutKillsConnection.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection) {
/* 4485 */     this.queryTimeoutKillsConnection.setValue(queryTimeoutKillsConnection);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  651 */       Field[] declaredFields = ConnectionPropertiesImpl.class.getDeclaredFields();
/*      */ 
/*  654 */       for (int i = 0; i < declaredFields.length; i++) {
/*  655 */         if (!ConnectionProperty.class.isAssignableFrom(declaredFields[i].getType()))
/*      */           continue;
/*  657 */         PROPERTY_LIST.add(declaredFields[i]);
/*      */       }
/*      */     }
/*      */     catch (Exception ex) {
/*  661 */       RuntimeException rtEx = new RuntimeException();
/*  662 */       rtEx.initCause(ex);
/*      */ 
/*  664 */       throw rtEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   class StringConnectionProperty extends ConnectionPropertiesImpl.ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 5432127962785948272L;
/*      */ 
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  557 */       this(propertyNameToSet, defaultValueToSet, null, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String[] allowableValuesToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  575 */       super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String getValueAsString()
/*      */     {
/*  581 */       return (String)this.valueAsObject;
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*  588 */       return (this.allowableValues != null) && (this.allowableValues.length > 0);
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  596 */       if (extractedValue != null) {
/*  597 */         validateStringValues(extractedValue);
/*      */ 
/*  599 */         this.valueAsObject = extractedValue;
/*      */       } else {
/*  601 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  609 */       return false;
/*      */     }
/*      */ 
/*      */     void setValue(String valueFlag) {
/*  613 */       this.valueAsObject = valueFlag;
/*      */     }
/*      */   }
/*      */ 
/*      */   class MemorySizeConnectionProperty extends ConnectionPropertiesImpl.IntegerConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 7351065128998572656L;
/*      */     private String valueAsString;
/*      */ 
/*      */     MemorySizeConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  498 */       super(propertyNameToSet, defaultValueToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  504 */       this.valueAsString = extractedValue;
/*      */ 
/*  506 */       if (extractedValue != null) {
/*  507 */         if ((extractedValue.endsWith("k")) || (extractedValue.endsWith("K")) || (extractedValue.endsWith("kb")) || (extractedValue.endsWith("Kb")) || (extractedValue.endsWith("kB")))
/*      */         {
/*  512 */           this.multiplier = 1024;
/*  513 */           int indexOfK = StringUtils.indexOfIgnoreCase(extractedValue, "k");
/*      */ 
/*  515 */           extractedValue = extractedValue.substring(0, indexOfK);
/*  516 */         } else if ((extractedValue.endsWith("m")) || (extractedValue.endsWith("M")) || (extractedValue.endsWith("G")) || (extractedValue.endsWith("mb")) || (extractedValue.endsWith("Mb")) || (extractedValue.endsWith("mB")))
/*      */         {
/*  522 */           this.multiplier = 1048576;
/*  523 */           int indexOfM = StringUtils.indexOfIgnoreCase(extractedValue, "m");
/*      */ 
/*  525 */           extractedValue = extractedValue.substring(0, indexOfM);
/*  526 */         } else if ((extractedValue.endsWith("g")) || (extractedValue.endsWith("G")) || (extractedValue.endsWith("gb")) || (extractedValue.endsWith("Gb")) || (extractedValue.endsWith("gB")))
/*      */         {
/*  531 */           this.multiplier = 1073741824;
/*  532 */           int indexOfG = StringUtils.indexOfIgnoreCase(extractedValue, "g");
/*      */ 
/*  534 */           extractedValue = extractedValue.substring(0, indexOfG);
/*      */         }
/*      */       }
/*      */ 
/*  538 */       super.initializeFrom(extractedValue);
/*      */     }
/*      */ 
/*      */     void setValue(String value) throws SQLException {
/*  542 */       initializeFrom(value);
/*      */     }
/*      */ 
/*      */     String getValueAsString() {
/*  546 */       return this.valueAsString;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class LongConnectionProperty extends ConnectionPropertiesImpl.IntegerConnectionProperty
/*      */   {
/*      */     private static final long serialVersionUID = 6068572984340480895L;
/*      */     private final ConnectionPropertiesImpl this$0;
/*      */ 
/*      */     LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, long lowerBoundToSet, long upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  444 */       super(propertyNameToSet, new Long(defaultValueToSet), null, (int)lowerBoundToSet, (int)upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */ 
/*  443 */       this.this$0 = this$0;
/*      */     }
/*      */ 
/*      */     LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  453 */       this(propertyNameToSet, defaultValueToSet, 0L, 0L, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     void setValue(long value)
/*      */     {
/*  460 */       this.valueAsObject = new Long(value);
/*      */     }
/*      */ 
/*      */     long getValueAsLong() {
/*  464 */       return ((Long)this.valueAsObject).longValue();
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue) throws SQLException {
/*  468 */       if (extractedValue != null) {
/*      */         try
/*      */         {
/*  471 */           long longValue = Double.valueOf(extractedValue).longValue();
/*      */ 
/*  473 */           this.valueAsObject = new Long(longValue);
/*      */         } catch (NumberFormatException nfe) {
/*  475 */           throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts long integer values. The value '" + extractedValue + "' can not be converted to a long integer.", "S1009", this.this$0.getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  483 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class IntegerConnectionProperty extends ConnectionPropertiesImpl.ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = -3004305481796850832L;
/*      */     int multiplier;
/*      */     private final ConnectionPropertiesImpl this$0;
/*      */ 
/*      */     public IntegerConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  327 */       super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */ 
/*  326 */       this.this$0 = this$0;
/*      */ 
/*  332 */       this.multiplier = 1;
/*      */     }
/*      */ 
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  338 */       super(propertyNameToSet, new Integer(defaultValueToSet), null, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */ 
/*  337 */       this.this$0 = this$0;
/*      */ 
/*  332 */       this.multiplier = 1;
/*      */     }
/*      */ 
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  356 */       this(propertyNameToSet, defaultValueToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues()
/*      */     {
/*  364 */       return null;
/*      */     }
/*      */ 
/*      */     int getLowerBound()
/*      */     {
/*  371 */       return this.lowerBound;
/*      */     }
/*      */ 
/*      */     int getUpperBound()
/*      */     {
/*  378 */       return this.upperBound;
/*      */     }
/*      */ 
/*      */     int getValueAsInt() {
/*  382 */       return ((Integer)this.valueAsObject).intValue();
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*  389 */       return false;
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  396 */       if (extractedValue != null) {
/*      */         try
/*      */         {
/*  399 */           int intValue = Double.valueOf(extractedValue).intValue();
/*      */ 
/*  410 */           this.valueAsObject = new Integer(intValue * this.multiplier);
/*      */         } catch (NumberFormatException nfe) {
/*  412 */           throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts integer values. The value '" + extractedValue + "' can not be converted to an integer.", "S1009", this.this$0.getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  420 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  428 */       return getUpperBound() != getLowerBound();
/*      */     }
/*      */ 
/*      */     void setValue(int valueFlag) {
/*  432 */       this.valueAsObject = new Integer(valueFlag);
/*      */     }
/*      */   }
/*      */ 
/*      */   abstract class ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     String[] allowableValues;
/*      */     String categoryName;
/*      */     Object defaultValue;
/*      */     int lowerBound;
/*      */     int order;
/*      */     String propertyName;
/*      */     String sinceVersion;
/*      */     int upperBound;
/*      */     Object valueAsObject;
/*      */     boolean required;
/*      */     String description;
/*      */ 
/*      */     public ConnectionProperty()
/*      */     {
/*      */     }
/*      */ 
/*      */     ConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  156 */       this.description = descriptionToSet;
/*  157 */       this.propertyName = propertyNameToSet;
/*  158 */       this.defaultValue = defaultValueToSet;
/*  159 */       this.valueAsObject = defaultValueToSet;
/*  160 */       this.allowableValues = allowableValuesToSet;
/*  161 */       this.lowerBound = lowerBoundToSet;
/*  162 */       this.upperBound = upperBoundToSet;
/*  163 */       this.required = false;
/*  164 */       this.sinceVersion = sinceVersionToSet;
/*  165 */       this.categoryName = category;
/*  166 */       this.order = orderInCategory;
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues() {
/*  170 */       return this.allowableValues;
/*      */     }
/*      */ 
/*      */     String getCategoryName()
/*      */     {
/*  177 */       return this.categoryName;
/*      */     }
/*      */ 
/*      */     Object getDefaultValue() {
/*  181 */       return this.defaultValue;
/*      */     }
/*      */ 
/*      */     int getLowerBound() {
/*  185 */       return this.lowerBound;
/*      */     }
/*      */ 
/*      */     int getOrder()
/*      */     {
/*  192 */       return this.order;
/*      */     }
/*      */ 
/*      */     String getPropertyName() {
/*  196 */       return this.propertyName;
/*      */     }
/*      */ 
/*      */     int getUpperBound() {
/*  200 */       return this.upperBound;
/*      */     }
/*      */ 
/*      */     Object getValueAsObject() {
/*  204 */       return this.valueAsObject;
/*      */     }
/*      */     abstract boolean hasValueConstraints();
/*      */ 
/*      */     void initializeFrom(Properties extractFrom) throws SQLException {
/*  210 */       String extractedValue = extractFrom.getProperty(getPropertyName());
/*  211 */       extractFrom.remove(getPropertyName());
/*  212 */       initializeFrom(extractedValue);
/*      */     }
/*      */ 
/*      */     void initializeFrom(Reference ref) throws SQLException {
/*  216 */       RefAddr refAddr = ref.get(getPropertyName());
/*      */ 
/*  218 */       if (refAddr != null) {
/*  219 */         String refContentAsString = (String)refAddr.getContent();
/*      */ 
/*  221 */         initializeFrom(refContentAsString);
/*      */       }
/*      */     }
/*      */ 
/*      */     abstract void initializeFrom(String paramString)
/*      */       throws SQLException;
/*      */ 
/*      */     abstract boolean isRangeBased();
/*      */ 
/*      */     void setCategoryName(String categoryName)
/*      */     {
/*  234 */       this.categoryName = categoryName;
/*      */     }
/*      */ 
/*      */     void setOrder(int order)
/*      */     {
/*  242 */       this.order = order;
/*      */     }
/*      */ 
/*      */     void setValueAsObject(Object obj) {
/*  246 */       this.valueAsObject = obj;
/*      */     }
/*      */ 
/*      */     void storeTo(Reference ref) {
/*  250 */       if (getValueAsObject() != null)
/*  251 */         ref.add(new StringRefAddr(getPropertyName(), getValueAsObject().toString()));
/*      */     }
/*      */ 
/*      */     DriverPropertyInfo getAsDriverPropertyInfo()
/*      */     {
/*  257 */       DriverPropertyInfo dpi = new DriverPropertyInfo(this.propertyName, null);
/*  258 */       dpi.choices = getAllowableValues();
/*  259 */       dpi.value = (this.valueAsObject != null ? this.valueAsObject.toString() : null);
/*  260 */       dpi.required = this.required;
/*  261 */       dpi.description = this.description;
/*      */ 
/*  263 */       return dpi;
/*      */     }
/*      */ 
/*      */     void validateStringValues(String valueToValidate) throws SQLException
/*      */     {
/*  268 */       String[] validateAgainst = getAllowableValues();
/*      */ 
/*  270 */       if (valueToValidate == null) {
/*  271 */         return;
/*      */       }
/*      */ 
/*  274 */       if ((validateAgainst == null) || (validateAgainst.length == 0)) {
/*  275 */         return;
/*      */       }
/*      */ 
/*  278 */       for (int i = 0; i < validateAgainst.length; i++) {
/*  279 */         if ((validateAgainst[i] != null) && (validateAgainst[i].equalsIgnoreCase(valueToValidate)))
/*      */         {
/*  281 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  285 */       StringBuffer errorMessageBuf = new StringBuffer();
/*      */ 
/*  287 */       errorMessageBuf.append("The connection property '");
/*  288 */       errorMessageBuf.append(getPropertyName());
/*  289 */       errorMessageBuf.append("' only accepts values of the form: ");
/*      */ 
/*  291 */       if (validateAgainst.length != 0) {
/*  292 */         errorMessageBuf.append("'");
/*  293 */         errorMessageBuf.append(validateAgainst[0]);
/*  294 */         errorMessageBuf.append("'");
/*      */ 
/*  296 */         for (int i = 1; i < validateAgainst.length - 1; i++) {
/*  297 */           errorMessageBuf.append(", ");
/*  298 */           errorMessageBuf.append("'");
/*  299 */           errorMessageBuf.append(validateAgainst[i]);
/*  300 */           errorMessageBuf.append("'");
/*      */         }
/*      */ 
/*  303 */         errorMessageBuf.append(" or '");
/*  304 */         errorMessageBuf.append(validateAgainst[(validateAgainst.length - 1)]);
/*      */ 
/*  306 */         errorMessageBuf.append("'");
/*      */       }
/*      */ 
/*  309 */       errorMessageBuf.append(". The value '");
/*  310 */       errorMessageBuf.append(valueToValidate);
/*  311 */       errorMessageBuf.append("' is not in this set.");
/*      */ 
/*  313 */       throw SQLError.createSQLException(errorMessageBuf.toString(), "S1009", ConnectionPropertiesImpl.this.getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   class BooleanConnectionProperty extends ConnectionPropertiesImpl.ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 2540132501709159404L;
/*      */ 
/*      */     BooleanConnectionProperty(String propertyNameToSet, boolean defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*   76 */       super(propertyNameToSet, Boolean.valueOf(defaultValueToSet), null, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues()
/*      */     {
/*   85 */       return new String[] { "true", "false", "yes", "no" };
/*      */     }
/*      */ 
/*      */     boolean getValueAsBoolean() {
/*   89 */       return ((Boolean)this.valueAsObject).booleanValue();
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*   96 */       return true;
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  103 */       if (extractedValue != null) {
/*  104 */         validateStringValues(extractedValue);
/*      */ 
/*  106 */         this.valueAsObject = Boolean.valueOf((extractedValue.equalsIgnoreCase("TRUE")) || (extractedValue.equalsIgnoreCase("YES")));
/*      */       }
/*      */       else
/*      */       {
/*  110 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  118 */       return false;
/*      */     }
/*      */ 
/*      */     void setValue(boolean valueFlag) {
/*  122 */       this.valueAsObject = Boolean.valueOf(valueFlag);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionPropertiesImpl
 * JD-Core Version:    0.6.0
 */