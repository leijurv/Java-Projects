package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface ConnectionProperties
{
  public abstract String exposeAsXml()
    throws SQLException;

  public abstract boolean getAllowLoadLocalInfile();

  public abstract boolean getAllowMultiQueries();

  public abstract boolean getAllowNanAndInf();

  public abstract boolean getAllowUrlInLocalInfile();

  public abstract boolean getAlwaysSendSetIsolation();

  public abstract boolean getAutoDeserialize();

  public abstract boolean getAutoGenerateTestcaseScript();

  public abstract boolean getAutoReconnectForPools();

  public abstract int getBlobSendChunkSize();

  public abstract boolean getCacheCallableStatements();

  public abstract boolean getCachePreparedStatements();

  public abstract boolean getCacheResultSetMetadata();

  public abstract boolean getCacheServerConfiguration();

  public abstract int getCallableStatementCacheSize();

  public abstract boolean getCapitalizeTypeNames();

  public abstract String getCharacterSetResults();

  public abstract boolean getClobberStreamingResults();

  public abstract String getClobCharacterEncoding();

  public abstract String getConnectionCollation();

  public abstract int getConnectTimeout();

  public abstract boolean getContinueBatchOnError();

  public abstract boolean getCreateDatabaseIfNotExist();

  public abstract int getDefaultFetchSize();

  public abstract boolean getDontTrackOpenResources();

  public abstract boolean getDumpQueriesOnException();

  public abstract boolean getDynamicCalendars();

  public abstract boolean getElideSetAutoCommits();

  public abstract boolean getEmptyStringsConvertToZero();

  public abstract boolean getEmulateLocators();

  public abstract boolean getEmulateUnsupportedPstmts();

  public abstract boolean getEnablePacketDebug();

  public abstract String getEncoding();

  public abstract boolean getExplainSlowQueries();

  public abstract boolean getFailOverReadOnly();

  public abstract boolean getGatherPerformanceMetrics();

  public abstract boolean getHoldResultsOpenOverStatementClose();

  public abstract boolean getIgnoreNonTxTables();

  public abstract int getInitialTimeout();

  public abstract boolean getInteractiveClient();

  public abstract boolean getIsInteractiveClient();

  public abstract boolean getJdbcCompliantTruncation();

  public abstract int getLocatorFetchBufferSize();

  public abstract String getLogger();

  public abstract String getLoggerClassName();

  public abstract boolean getLogSlowQueries();

  public abstract boolean getMaintainTimeStats();

  public abstract int getMaxQuerySizeToLog();

  public abstract int getMaxReconnects();

  public abstract int getMaxRows();

  public abstract int getMetadataCacheSize();

  public abstract boolean getNoDatetimeStringSync();

  public abstract boolean getNullCatalogMeansCurrent();

  public abstract boolean getNullNamePatternMatchesAll();

  public abstract int getPacketDebugBufferSize();

  public abstract boolean getParanoid();

  public abstract boolean getPedantic();

  public abstract int getPreparedStatementCacheSize();

  public abstract int getPreparedStatementCacheSqlLimit();

  public abstract boolean getProfileSql();

  public abstract boolean getProfileSQL();

  public abstract String getPropertiesTransform();

  public abstract int getQueriesBeforeRetryMaster();

  public abstract boolean getReconnectAtTxEnd();

  public abstract boolean getRelaxAutoCommit();

  public abstract int getReportMetricsIntervalMillis();

  public abstract boolean getRequireSSL();

  public abstract boolean getRollbackOnPooledClose();

  public abstract boolean getRoundRobinLoadBalance();

  public abstract boolean getRunningCTS13();

  public abstract int getSecondsBeforeRetryMaster();

  public abstract String getServerTimezone();

  public abstract String getSessionVariables();

  public abstract int getSlowQueryThresholdMillis();

  public abstract String getSocketFactoryClassName();

  public abstract int getSocketTimeout();

  public abstract boolean getStrictFloatingPoint();

  public abstract boolean getStrictUpdates();

  public abstract boolean getTinyInt1isBit();

  public abstract boolean getTraceProtocol();

  public abstract boolean getTransformedBitIsBoolean();

  public abstract boolean getUseCompression();

  public abstract boolean getUseFastIntParsing();

  public abstract boolean getUseHostsInPrivileges();

  public abstract boolean getUseInformationSchema();

  public abstract boolean getUseLocalSessionState();

  public abstract boolean getUseOldUTF8Behavior();

  public abstract boolean getUseOnlyServerErrorMessages();

  public abstract boolean getUseReadAheadInput();

  public abstract boolean getUseServerPreparedStmts();

  public abstract boolean getUseSqlStateCodes();

  public abstract boolean getUseSSL();

  public abstract boolean getUseStreamLengthsInPrepStmts();

  public abstract boolean getUseTimezone();

  public abstract boolean getUseUltraDevWorkAround();

  public abstract boolean getUseUnbufferedInput();

  public abstract boolean getUseUnicode();

  public abstract boolean getUseUsageAdvisor();

  public abstract boolean getYearIsDateType();

  public abstract String getZeroDateTimeBehavior();

  public abstract void setAllowLoadLocalInfile(boolean paramBoolean);

  public abstract void setAllowMultiQueries(boolean paramBoolean);

  public abstract void setAllowNanAndInf(boolean paramBoolean);

  public abstract void setAllowUrlInLocalInfile(boolean paramBoolean);

  public abstract void setAlwaysSendSetIsolation(boolean paramBoolean);

  public abstract void setAutoDeserialize(boolean paramBoolean);

  public abstract void setAutoGenerateTestcaseScript(boolean paramBoolean);

  public abstract void setAutoReconnect(boolean paramBoolean);

  public abstract void setAutoReconnectForConnectionPools(boolean paramBoolean);

  public abstract void setAutoReconnectForPools(boolean paramBoolean);

  public abstract void setBlobSendChunkSize(String paramString)
    throws SQLException;

  public abstract void setCacheCallableStatements(boolean paramBoolean);

  public abstract void setCachePreparedStatements(boolean paramBoolean);

  public abstract void setCacheResultSetMetadata(boolean paramBoolean);

  public abstract void setCacheServerConfiguration(boolean paramBoolean);

  public abstract void setCallableStatementCacheSize(int paramInt);

  public abstract void setCapitalizeDBMDTypes(boolean paramBoolean);

  public abstract void setCapitalizeTypeNames(boolean paramBoolean);

  public abstract void setCharacterEncoding(String paramString);

  public abstract void setCharacterSetResults(String paramString);

  public abstract void setClobberStreamingResults(boolean paramBoolean);

  public abstract void setClobCharacterEncoding(String paramString);

  public abstract void setConnectionCollation(String paramString);

  public abstract void setConnectTimeout(int paramInt);

  public abstract void setContinueBatchOnError(boolean paramBoolean);

  public abstract void setCreateDatabaseIfNotExist(boolean paramBoolean);

  public abstract void setDefaultFetchSize(int paramInt);

  public abstract void setDetectServerPreparedStmts(boolean paramBoolean);

  public abstract void setDontTrackOpenResources(boolean paramBoolean);

  public abstract void setDumpQueriesOnException(boolean paramBoolean);

  public abstract void setDynamicCalendars(boolean paramBoolean);

  public abstract void setElideSetAutoCommits(boolean paramBoolean);

  public abstract void setEmptyStringsConvertToZero(boolean paramBoolean);

  public abstract void setEmulateLocators(boolean paramBoolean);

  public abstract void setEmulateUnsupportedPstmts(boolean paramBoolean);

  public abstract void setEnablePacketDebug(boolean paramBoolean);

  public abstract void setEncoding(String paramString);

  public abstract void setExplainSlowQueries(boolean paramBoolean);

  public abstract void setFailOverReadOnly(boolean paramBoolean);

  public abstract void setGatherPerformanceMetrics(boolean paramBoolean);

  public abstract void setHoldResultsOpenOverStatementClose(boolean paramBoolean);

  public abstract void setIgnoreNonTxTables(boolean paramBoolean);

  public abstract void setInitialTimeout(int paramInt);

  public abstract void setIsInteractiveClient(boolean paramBoolean);

  public abstract void setJdbcCompliantTruncation(boolean paramBoolean);

  public abstract void setLocatorFetchBufferSize(String paramString)
    throws SQLException;

  public abstract void setLogger(String paramString);

  public abstract void setLoggerClassName(String paramString);

  public abstract void setLogSlowQueries(boolean paramBoolean);

  public abstract void setMaintainTimeStats(boolean paramBoolean);

  public abstract void setMaxQuerySizeToLog(int paramInt);

  public abstract void setMaxReconnects(int paramInt);

  public abstract void setMaxRows(int paramInt);

  public abstract void setMetadataCacheSize(int paramInt);

  public abstract void setNoDatetimeStringSync(boolean paramBoolean);

  public abstract void setNullCatalogMeansCurrent(boolean paramBoolean);

  public abstract void setNullNamePatternMatchesAll(boolean paramBoolean);

  public abstract void setPacketDebugBufferSize(int paramInt);

  public abstract void setParanoid(boolean paramBoolean);

  public abstract void setPedantic(boolean paramBoolean);

  public abstract void setPreparedStatementCacheSize(int paramInt);

  public abstract void setPreparedStatementCacheSqlLimit(int paramInt);

  public abstract void setProfileSql(boolean paramBoolean);

  public abstract void setProfileSQL(boolean paramBoolean);

  public abstract void setPropertiesTransform(String paramString);

  public abstract void setQueriesBeforeRetryMaster(int paramInt);

  public abstract void setReconnectAtTxEnd(boolean paramBoolean);

  public abstract void setRelaxAutoCommit(boolean paramBoolean);

  public abstract void setReportMetricsIntervalMillis(int paramInt);

  public abstract void setRequireSSL(boolean paramBoolean);

  public abstract void setRetainStatementAfterResultSetClose(boolean paramBoolean);

  public abstract void setRollbackOnPooledClose(boolean paramBoolean);

  public abstract void setRoundRobinLoadBalance(boolean paramBoolean);

  public abstract void setRunningCTS13(boolean paramBoolean);

  public abstract void setSecondsBeforeRetryMaster(int paramInt);

  public abstract void setServerTimezone(String paramString);

  public abstract void setSessionVariables(String paramString);

  public abstract void setSlowQueryThresholdMillis(int paramInt);

  public abstract void setSocketFactoryClassName(String paramString);

  public abstract void setSocketTimeout(int paramInt);

  public abstract void setStrictFloatingPoint(boolean paramBoolean);

  public abstract void setStrictUpdates(boolean paramBoolean);

  public abstract void setTinyInt1isBit(boolean paramBoolean);

  public abstract void setTraceProtocol(boolean paramBoolean);

  public abstract void setTransformedBitIsBoolean(boolean paramBoolean);

  public abstract void setUseCompression(boolean paramBoolean);

  public abstract void setUseFastIntParsing(boolean paramBoolean);

  public abstract void setUseHostsInPrivileges(boolean paramBoolean);

  public abstract void setUseInformationSchema(boolean paramBoolean);

  public abstract void setUseLocalSessionState(boolean paramBoolean);

  public abstract void setUseOldUTF8Behavior(boolean paramBoolean);

  public abstract void setUseOnlyServerErrorMessages(boolean paramBoolean);

  public abstract void setUseReadAheadInput(boolean paramBoolean);

  public abstract void setUseServerPreparedStmts(boolean paramBoolean);

  public abstract void setUseSqlStateCodes(boolean paramBoolean);

  public abstract void setUseSSL(boolean paramBoolean);

  public abstract void setUseStreamLengthsInPrepStmts(boolean paramBoolean);

  public abstract void setUseTimezone(boolean paramBoolean);

  public abstract void setUseUltraDevWorkAround(boolean paramBoolean);

  public abstract void setUseUnbufferedInput(boolean paramBoolean);

  public abstract void setUseUnicode(boolean paramBoolean);

  public abstract void setUseUsageAdvisor(boolean paramBoolean);

  public abstract void setYearIsDateType(boolean paramBoolean);

  public abstract void setZeroDateTimeBehavior(String paramString);

  public abstract boolean useUnbufferedInput();

  public abstract boolean getUseCursorFetch();

  public abstract void setUseCursorFetch(boolean paramBoolean);

  public abstract boolean getOverrideSupportsIntegrityEnhancementFacility();

  public abstract void setOverrideSupportsIntegrityEnhancementFacility(boolean paramBoolean);

  public abstract boolean getNoTimezoneConversionForTimeType();

  public abstract void setNoTimezoneConversionForTimeType(boolean paramBoolean);

  public abstract boolean getUseJDBCCompliantTimezoneShift();

  public abstract void setUseJDBCCompliantTimezoneShift(boolean paramBoolean);

  public abstract boolean getAutoClosePStmtStreams();

  public abstract void setAutoClosePStmtStreams(boolean paramBoolean);

  public abstract boolean getProcessEscapeCodesForPrepStmts();

  public abstract void setProcessEscapeCodesForPrepStmts(boolean paramBoolean);

  public abstract boolean getUseGmtMillisForDatetimes();

  public abstract void setUseGmtMillisForDatetimes(boolean paramBoolean);

  public abstract boolean getDumpMetadataOnColumnNotFound();

  public abstract void setDumpMetadataOnColumnNotFound(boolean paramBoolean);

  public abstract String getResourceId();

  public abstract void setResourceId(String paramString);

  public abstract boolean getRewriteBatchedStatements();

  public abstract void setRewriteBatchedStatements(boolean paramBoolean);

  public abstract boolean getJdbcCompliantTruncationForReads();

  public abstract void setJdbcCompliantTruncationForReads(boolean paramBoolean);

  public abstract boolean getUseJvmCharsetConverters();

  public abstract void setUseJvmCharsetConverters(boolean paramBoolean);

  public abstract boolean getPinGlobalTxToPhysicalConnection();

  public abstract void setPinGlobalTxToPhysicalConnection(boolean paramBoolean);

  public abstract void setGatherPerfMetrics(boolean paramBoolean);

  public abstract boolean getGatherPerfMetrics();

  public abstract void setUltraDevHack(boolean paramBoolean);

  public abstract boolean getUltraDevHack();

  public abstract void setInteractiveClient(boolean paramBoolean);

  public abstract void setSocketFactory(String paramString);

  public abstract String getSocketFactory();

  public abstract void setUseServerPrepStmts(boolean paramBoolean);

  public abstract boolean getUseServerPrepStmts();

  public abstract void setCacheCallableStmts(boolean paramBoolean);

  public abstract boolean getCacheCallableStmts();

  public abstract void setCachePrepStmts(boolean paramBoolean);

  public abstract boolean getCachePrepStmts();

  public abstract void setCallableStmtCacheSize(int paramInt);

  public abstract int getCallableStmtCacheSize();

  public abstract void setPrepStmtCacheSize(int paramInt);

  public abstract int getPrepStmtCacheSize();

  public abstract void setPrepStmtCacheSqlLimit(int paramInt);

  public abstract int getPrepStmtCacheSqlLimit();

  public abstract boolean getNoAccessToProcedureBodies();

  public abstract void setNoAccessToProcedureBodies(boolean paramBoolean);

  public abstract boolean getUseOldAliasMetadataBehavior();

  public abstract void setUseOldAliasMetadataBehavior(boolean paramBoolean);

  public abstract String getClientCertificateKeyStorePassword();

  public abstract void setClientCertificateKeyStorePassword(String paramString);

  public abstract String getClientCertificateKeyStoreType();

  public abstract void setClientCertificateKeyStoreType(String paramString);

  public abstract String getClientCertificateKeyStoreUrl();

  public abstract void setClientCertificateKeyStoreUrl(String paramString);

  public abstract String getTrustCertificateKeyStorePassword();

  public abstract void setTrustCertificateKeyStorePassword(String paramString);

  public abstract String getTrustCertificateKeyStoreType();

  public abstract void setTrustCertificateKeyStoreType(String paramString);

  public abstract String getTrustCertificateKeyStoreUrl();

  public abstract void setTrustCertificateKeyStoreUrl(String paramString);

  public abstract boolean getUseSSPSCompatibleTimezoneShift();

  public abstract void setUseSSPSCompatibleTimezoneShift(boolean paramBoolean);

  public abstract boolean getTreatUtilDateAsTimestamp();

  public abstract void setTreatUtilDateAsTimestamp(boolean paramBoolean);

  public abstract boolean getUseFastDateParsing();

  public abstract void setUseFastDateParsing(boolean paramBoolean);

  public abstract String getLocalSocketAddress();

  public abstract void setLocalSocketAddress(String paramString);

  public abstract void setUseConfigs(String paramString);

  public abstract String getUseConfigs();

  public abstract boolean getGenerateSimpleParameterMetadata();

  public abstract void setGenerateSimpleParameterMetadata(boolean paramBoolean);

  public abstract boolean getLogXaCommands();

  public abstract void setLogXaCommands(boolean paramBoolean);

  public abstract int getResultSetSizeThreshold();

  public abstract void setResultSetSizeThreshold(int paramInt);

  public abstract int getNetTimeoutForStreamingResults();

  public abstract void setNetTimeoutForStreamingResults(int paramInt);

  public abstract boolean getEnableQueryTimeouts();

  public abstract void setEnableQueryTimeouts(boolean paramBoolean);

  public abstract boolean getPadCharsWithSpace();

  public abstract void setPadCharsWithSpace(boolean paramBoolean);

  public abstract boolean getUseDynamicCharsetInfo();

  public abstract void setUseDynamicCharsetInfo(boolean paramBoolean);

  public abstract String getClientInfoProvider();

  public abstract void setClientInfoProvider(String paramString);

  public abstract boolean getPopulateInsertRowWithDefaultValues();

  public abstract void setPopulateInsertRowWithDefaultValues(boolean paramBoolean);

  public abstract String getLoadBalanceStrategy();

  public abstract void setLoadBalanceStrategy(String paramString);

  public abstract boolean getTcpNoDelay();

  public abstract void setTcpNoDelay(boolean paramBoolean);

  public abstract boolean getTcpKeepAlive();

  public abstract void setTcpKeepAlive(boolean paramBoolean);

  public abstract int getTcpRcvBuf();

  public abstract void setTcpRcvBuf(int paramInt);

  public abstract int getTcpSndBuf();

  public abstract void setTcpSndBuf(int paramInt);

  public abstract int getTcpTrafficClass();

  public abstract void setTcpTrafficClass(int paramInt);

  public abstract boolean getUseNanosForElapsedTime();

  public abstract void setUseNanosForElapsedTime(boolean paramBoolean);

  public abstract long getSlowQueryThresholdNanos();

  public abstract void setSlowQueryThresholdNanos(long paramLong);

  public abstract String getStatementInterceptors();

  public abstract void setStatementInterceptors(String paramString);

  public abstract boolean getUseDirectRowUnpack();

  public abstract void setUseDirectRowUnpack(boolean paramBoolean);

  public abstract String getLargeRowSizeThreshold();

  public abstract void setLargeRowSizeThreshold(String paramString);

  public abstract boolean getUseBlobToStoreUTF8OutsideBMP();

  public abstract void setUseBlobToStoreUTF8OutsideBMP(boolean paramBoolean);

  public abstract String getUtf8OutsideBmpExcludedColumnNamePattern();

  public abstract void setUtf8OutsideBmpExcludedColumnNamePattern(String paramString);

  public abstract String getUtf8OutsideBmpIncludedColumnNamePattern();

  public abstract void setUtf8OutsideBmpIncludedColumnNamePattern(String paramString);

  public abstract boolean getIncludeInnodbStatusInDeadlockExceptions();

  public abstract void setIncludeInnodbStatusInDeadlockExceptions(boolean paramBoolean);

  public abstract boolean getBlobsAreStrings();

  public abstract void setBlobsAreStrings(boolean paramBoolean);

  public abstract boolean getFunctionsNeverReturnBlobs();

  public abstract void setFunctionsNeverReturnBlobs(boolean paramBoolean);

  public abstract boolean getAutoSlowLog();

  public abstract void setAutoSlowLog(boolean paramBoolean);

  public abstract String getConnectionLifecycleInterceptors();

  public abstract void setConnectionLifecycleInterceptors(String paramString);

  public abstract String getProfilerEventHandler();

  public abstract void setProfilerEventHandler(String paramString);

  public abstract boolean getVerifyServerCertificate();

  public abstract void setVerifyServerCertificate(boolean paramBoolean);

  public abstract boolean getUseLegacyDatetimeCode();

  public abstract void setUseLegacyDatetimeCode(boolean paramBoolean);

  public abstract int getSelfDestructOnPingSecondsLifetime();

  public abstract void setSelfDestructOnPingSecondsLifetime(int paramInt);

  public abstract int getSelfDestructOnPingMaxOperations();

  public abstract void setSelfDestructOnPingMaxOperations(int paramInt);

  public abstract boolean getUseColumnNamesInFindColumn();

  public abstract void setUseColumnNamesInFindColumn(boolean paramBoolean);

  public abstract boolean getUseLocalTransactionState();

  public abstract void setUseLocalTransactionState(boolean paramBoolean);

  public abstract boolean getCompensateOnDuplicateKeyUpdateCounts();

  public abstract void setCompensateOnDuplicateKeyUpdateCounts(boolean paramBoolean);

  public abstract void setUseAffectedRows(boolean paramBoolean);

  public abstract boolean getUseAffectedRows();

  public abstract void setPasswordCharacterEncoding(String paramString);

  public abstract String getPasswordCharacterEncoding();

  public abstract int getLoadBalanceBlacklistTimeout();

  public abstract void setLoadBalanceBlacklistTimeout(int paramInt);

  public abstract void setRetriesAllDown(int paramInt);

  public abstract int getRetriesAllDown();

  public abstract ExceptionInterceptor getExceptionInterceptor();

  public abstract void setExceptionInterceptors(String paramString);

  public abstract String getExceptionInterceptors();

  public abstract boolean getQueryTimeoutKillsConnection();

  public abstract void setQueryTimeoutKillsConnection(boolean paramBoolean);
}

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionProperties
 * JD-Core Version:    0.6.0
 */