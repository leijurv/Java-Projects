/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public class ReplicationConnection
/*      */   implements Connection, PingTarget
/*      */ {
/*      */   protected Connection currentConnection;
/*      */   protected Connection masterConnection;
/*      */   protected Connection slavesConnection;
/*      */ 
/*      */   protected ReplicationConnection()
/*      */   {
/*      */   }
/*      */ 
/*      */   public ReplicationConnection(Properties masterProperties, Properties slaveProperties)
/*      */     throws SQLException
/*      */   {
/*   59 */     NonRegisteringDriver driver = new NonRegisteringDriver();
/*      */ 
/*   61 */     StringBuffer masterUrl = new StringBuffer("jdbc:mysql://");
/*   62 */     StringBuffer slaveUrl = new StringBuffer("jdbc:mysql:loadbalance://");
/*      */ 
/*   64 */     String masterHost = masterProperties.getProperty("HOST");
/*      */ 
/*   67 */     if (masterHost != null) {
/*   68 */       masterUrl.append(masterHost);
/*      */     }
/*      */ 
/*   71 */     int numHosts = Integer.parseInt(slaveProperties.getProperty("NUM_HOSTS"));
/*      */ 
/*   74 */     for (int i = 1; i <= numHosts; i++) {
/*   75 */       String slaveHost = slaveProperties.getProperty("HOST." + i);
/*      */ 
/*   78 */       if (slaveHost != null) {
/*   79 */         if (i > 1) {
/*   80 */           slaveUrl.append(',');
/*      */         }
/*   82 */         slaveUrl.append(slaveHost);
/*      */       }
/*      */     }
/*      */ 
/*   86 */     String masterDb = masterProperties.getProperty("DBNAME");
/*      */ 
/*   89 */     masterUrl.append("/");
/*      */ 
/*   91 */     if (masterDb != null) {
/*   92 */       masterUrl.append(masterDb);
/*      */     }
/*      */ 
/*   95 */     String slaveDb = slaveProperties.getProperty("DBNAME");
/*      */ 
/*   98 */     slaveUrl.append("/");
/*      */ 
/*  100 */     if (slaveDb != null) {
/*  101 */       slaveUrl.append(slaveDb);
/*      */     }
/*      */ 
/*  104 */     slaveProperties.setProperty("roundRobinLoadBalance", "true");
/*      */ 
/*  106 */     this.masterConnection = ((PingTarget)driver.connect(masterUrl.toString(), masterProperties));
/*      */ 
/*  108 */     this.slavesConnection = ((PingTarget)driver.connect(slaveUrl.toString(), slaveProperties));
/*      */ 
/*  110 */     this.slavesConnection.setReadOnly(true);
/*      */ 
/*  112 */     this.currentConnection = this.masterConnection;
/*      */   }
/*      */ 
/*      */   public synchronized void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  121 */     this.currentConnection.clearWarnings();
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/*  130 */     this.masterConnection.close();
/*  131 */     this.slavesConnection.close();
/*      */   }
/*      */ 
/*      */   public synchronized void commit()
/*      */     throws SQLException
/*      */   {
/*  140 */     this.currentConnection.commit();
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement()
/*      */     throws SQLException
/*      */   {
/*  149 */     java.sql.Statement stmt = this.currentConnection.createStatement();
/*  150 */     ((Statement)stmt).setPingTarget(this);
/*      */ 
/*  152 */     return stmt;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  162 */     java.sql.Statement stmt = this.currentConnection.createStatement(resultSetType, resultSetConcurrency);
/*      */ 
/*  165 */     ((Statement)stmt).setPingTarget(this);
/*      */ 
/*  167 */     return stmt;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  178 */     java.sql.Statement stmt = this.currentConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */ 
/*  181 */     ((Statement)stmt).setPingTarget(this);
/*      */ 
/*  183 */     return stmt;
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/*  192 */     return this.currentConnection.getAutoCommit();
/*      */   }
/*      */ 
/*      */   public synchronized String getCatalog()
/*      */     throws SQLException
/*      */   {
/*  201 */     return this.currentConnection.getCatalog();
/*      */   }
/*      */ 
/*      */   public synchronized Connection getCurrentConnection() {
/*  205 */     return this.currentConnection;
/*      */   }
/*      */ 
/*      */   public synchronized int getHoldability()
/*      */     throws SQLException
/*      */   {
/*  214 */     return this.currentConnection.getHoldability();
/*      */   }
/*      */ 
/*      */   public synchronized Connection getMasterConnection() {
/*  218 */     return this.masterConnection;
/*      */   }
/*      */ 
/*      */   public synchronized DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/*  227 */     return this.currentConnection.getMetaData();
/*      */   }
/*      */ 
/*      */   public synchronized Connection getSlavesConnection() {
/*  231 */     return this.slavesConnection;
/*      */   }
/*      */ 
/*      */   public synchronized int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/*  240 */     return this.currentConnection.getTransactionIsolation();
/*      */   }
/*      */ 
/*      */   public synchronized Map getTypeMap()
/*      */     throws SQLException
/*      */   {
/*  249 */     return this.currentConnection.getTypeMap();
/*      */   }
/*      */ 
/*      */   public synchronized SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/*  258 */     return this.currentConnection.getWarnings();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isClosed()
/*      */     throws SQLException
/*      */   {
/*  267 */     return this.currentConnection.isClosed();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*  276 */     return this.currentConnection == this.slavesConnection;
/*      */   }
/*      */ 
/*      */   public synchronized String nativeSQL(String sql)
/*      */     throws SQLException
/*      */   {
/*  285 */     return this.currentConnection.nativeSQL(sql);
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/*  294 */     return this.currentConnection.prepareCall(sql);
/*      */   }
/*      */ 
/*      */   public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  304 */     return this.currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  316 */     return this.currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/*  326 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql);
/*      */ 
/*  328 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  330 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/*  340 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, autoGeneratedKeys);
/*      */ 
/*  342 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  344 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  354 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */ 
/*  357 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  359 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  371 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */ 
/*  374 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  376 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int[] columnIndexes)
/*      */     throws SQLException
/*      */   {
/*  386 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, columnIndexes);
/*      */ 
/*  388 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  390 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, String[] columnNames)
/*      */     throws SQLException
/*      */   {
/*  401 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, columnNames);
/*      */ 
/*  403 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  405 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized void releaseSavepoint(Savepoint savepoint)
/*      */     throws SQLException
/*      */   {
/*  415 */     this.currentConnection.releaseSavepoint(savepoint);
/*      */   }
/*      */ 
/*      */   public synchronized void rollback()
/*      */     throws SQLException
/*      */   {
/*  424 */     this.currentConnection.rollback();
/*      */   }
/*      */ 
/*      */   public synchronized void rollback(Savepoint savepoint)
/*      */     throws SQLException
/*      */   {
/*  433 */     this.currentConnection.rollback(savepoint);
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoCommit(boolean autoCommit)
/*      */     throws SQLException
/*      */   {
/*  443 */     this.currentConnection.setAutoCommit(autoCommit);
/*      */   }
/*      */ 
/*      */   public synchronized void setCatalog(String catalog)
/*      */     throws SQLException
/*      */   {
/*  452 */     this.currentConnection.setCatalog(catalog);
/*      */   }
/*      */ 
/*      */   public synchronized void setHoldability(int holdability)
/*      */     throws SQLException
/*      */   {
/*  462 */     this.currentConnection.setHoldability(holdability);
/*      */   }
/*      */ 
/*      */   public synchronized void setReadOnly(boolean readOnly)
/*      */     throws SQLException
/*      */   {
/*  471 */     if (readOnly) {
/*  472 */       if (this.currentConnection != this.slavesConnection) {
/*  473 */         switchToSlavesConnection();
/*      */       }
/*      */     }
/*  476 */     else if (this.currentConnection != this.masterConnection)
/*  477 */       switchToMasterConnection();
/*      */   }
/*      */ 
/*      */   public synchronized Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/*  488 */     return this.currentConnection.setSavepoint();
/*      */   }
/*      */ 
/*      */   public synchronized Savepoint setSavepoint(String name)
/*      */     throws SQLException
/*      */   {
/*  497 */     return this.currentConnection.setSavepoint(name);
/*      */   }
/*      */ 
/*      */   public synchronized void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/*  507 */     this.currentConnection.setTransactionIsolation(level);
/*      */   }
/*      */ 
/*      */   public synchronized void setTypeMap(Map arg0)
/*      */     throws SQLException
/*      */   {
/*  518 */     this.currentConnection.setTypeMap(arg0);
/*      */   }
/*      */ 
/*      */   private synchronized void switchToMasterConnection() throws SQLException {
/*  522 */     swapConnections(this.masterConnection, this.slavesConnection);
/*      */   }
/*      */ 
/*      */   private synchronized void switchToSlavesConnection() throws SQLException {
/*  526 */     swapConnections(this.slavesConnection, this.masterConnection);
/*      */   }
/*      */ 
/*      */   private synchronized void swapConnections(Connection switchToConnection, Connection switchFromConnection)
/*      */     throws SQLException
/*      */   {
/*  541 */     String switchFromCatalog = switchFromConnection.getCatalog();
/*  542 */     String switchToCatalog = switchToConnection.getCatalog();
/*      */ 
/*  544 */     if ((switchToCatalog != null) && (!switchToCatalog.equals(switchFromCatalog)))
/*  545 */       switchToConnection.setCatalog(switchFromCatalog);
/*  546 */     else if (switchFromCatalog != null) {
/*  547 */       switchToConnection.setCatalog(switchFromCatalog);
/*      */     }
/*      */ 
/*  550 */     boolean switchToAutoCommit = switchToConnection.getAutoCommit();
/*  551 */     boolean switchFromConnectionAutoCommit = switchFromConnection.getAutoCommit();
/*      */ 
/*  553 */     if (switchFromConnectionAutoCommit != switchToAutoCommit) {
/*  554 */       switchToConnection.setAutoCommit(switchFromConnectionAutoCommit);
/*      */     }
/*      */ 
/*  557 */     int switchToIsolation = switchToConnection.getTransactionIsolation();
/*      */ 
/*  560 */     int switchFromIsolation = switchFromConnection.getTransactionIsolation();
/*      */ 
/*  562 */     if (switchFromIsolation != switchToIsolation) {
/*  563 */       switchToConnection.setTransactionIsolation(switchFromIsolation);
/*      */     }
/*      */ 
/*  567 */     this.currentConnection = switchToConnection;
/*      */   }
/*      */ 
/*      */   public synchronized void doPing() throws SQLException {
/*  571 */     if (this.masterConnection != null) {
/*  572 */       this.masterConnection.ping();
/*      */     }
/*      */ 
/*  575 */     if (this.slavesConnection != null)
/*  576 */       this.slavesConnection.ping();
/*      */   }
/*      */ 
/*      */   public synchronized void changeUser(String userName, String newPassword)
/*      */     throws SQLException
/*      */   {
/*  582 */     this.masterConnection.changeUser(userName, newPassword);
/*  583 */     this.slavesConnection.changeUser(userName, newPassword);
/*      */   }
/*      */ 
/*      */   public synchronized void clearHasTriedMaster() {
/*  587 */     this.masterConnection.clearHasTriedMaster();
/*  588 */     this.slavesConnection.clearHasTriedMaster();
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/*  594 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql);
/*  595 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  597 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException
/*      */   {
/*  602 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyIndex);
/*  603 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  605 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*  610 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*  611 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  613 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException
/*      */   {
/*  618 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyIndexes);
/*  619 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  621 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  627 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*  628 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  630 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException
/*      */   {
/*  635 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyColNames);
/*  636 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  638 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized int getActiveStatementCount() {
/*  642 */     return this.currentConnection.getActiveStatementCount();
/*      */   }
/*      */ 
/*      */   public synchronized long getIdleFor() {
/*  646 */     return this.currentConnection.getIdleFor();
/*      */   }
/*      */ 
/*      */   public synchronized Log getLog() throws SQLException {
/*  650 */     return this.currentConnection.getLog();
/*      */   }
/*      */ 
/*      */   public synchronized String getServerCharacterEncoding() {
/*  654 */     return this.currentConnection.getServerCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public synchronized TimeZone getServerTimezoneTZ() {
/*  658 */     return this.currentConnection.getServerTimezoneTZ();
/*      */   }
/*      */ 
/*      */   public synchronized String getStatementComment() {
/*  662 */     return this.currentConnection.getStatementComment();
/*      */   }
/*      */ 
/*      */   public synchronized boolean hasTriedMaster() {
/*  666 */     return this.currentConnection.hasTriedMaster();
/*      */   }
/*      */ 
/*      */   public synchronized void initializeExtension(Extension ex) throws SQLException {
/*  670 */     this.currentConnection.initializeExtension(ex);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isAbonormallyLongQuery(long millisOrNanos) {
/*  674 */     return this.currentConnection.isAbonormallyLongQuery(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isInGlobalTx() {
/*  678 */     return this.currentConnection.isInGlobalTx();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isMasterConnection() {
/*  682 */     return this.currentConnection.isMasterConnection();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isNoBackslashEscapesSet() {
/*  686 */     return this.currentConnection.isNoBackslashEscapesSet();
/*      */   }
/*      */ 
/*      */   public synchronized boolean lowerCaseTableNames() {
/*  690 */     return this.currentConnection.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public synchronized boolean parserKnowsUnicode() {
/*  694 */     return this.currentConnection.parserKnowsUnicode();
/*      */   }
/*      */ 
/*      */   public synchronized void ping() throws SQLException {
/*  698 */     this.masterConnection.ping();
/*  699 */     this.slavesConnection.ping();
/*      */   }
/*      */ 
/*      */   public synchronized void reportQueryTime(long millisOrNanos) {
/*  703 */     this.currentConnection.reportQueryTime(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public synchronized void resetServerState() throws SQLException {
/*  707 */     this.currentConnection.resetServerState();
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql) throws SQLException
/*      */   {
/*  712 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql);
/*  713 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  715 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException
/*      */   {
/*  720 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyIndex);
/*  721 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  723 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*  728 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*  729 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  731 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  737 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*  738 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  740 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException
/*      */   {
/*  745 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyIndexes);
/*  746 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  748 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException
/*      */   {
/*  753 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyColNames);
/*  754 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  756 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized void setFailedOver(boolean flag) {
/*  760 */     this.currentConnection.setFailedOver(flag);
/*      */   }
/*      */ 
/*      */   public synchronized void setPreferSlaveDuringFailover(boolean flag) {
/*  764 */     this.currentConnection.setPreferSlaveDuringFailover(flag);
/*      */   }
/*      */ 
/*      */   public synchronized void setStatementComment(String comment) {
/*  768 */     this.masterConnection.setStatementComment(comment);
/*  769 */     this.slavesConnection.setStatementComment(comment);
/*      */   }
/*      */ 
/*      */   public synchronized void shutdownServer() throws SQLException {
/*  773 */     this.currentConnection.shutdownServer();
/*      */   }
/*      */ 
/*      */   public synchronized boolean supportsIsolationLevel() {
/*  777 */     return this.currentConnection.supportsIsolationLevel();
/*      */   }
/*      */ 
/*      */   public synchronized boolean supportsQuotedIdentifiers() {
/*  781 */     return this.currentConnection.supportsQuotedIdentifiers();
/*      */   }
/*      */ 
/*      */   public synchronized boolean supportsTransactions() {
/*  785 */     return this.currentConnection.supportsTransactions();
/*      */   }
/*      */ 
/*      */   public synchronized boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException
/*      */   {
/*  790 */     return this.currentConnection.versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */ 
/*      */   public synchronized String exposeAsXml() throws SQLException {
/*  794 */     return this.currentConnection.exposeAsXml();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAllowLoadLocalInfile() {
/*  798 */     return this.currentConnection.getAllowLoadLocalInfile();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAllowMultiQueries() {
/*  802 */     return this.currentConnection.getAllowMultiQueries();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAllowNanAndInf() {
/*  806 */     return this.currentConnection.getAllowNanAndInf();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAllowUrlInLocalInfile() {
/*  810 */     return this.currentConnection.getAllowUrlInLocalInfile();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAlwaysSendSetIsolation() {
/*  814 */     return this.currentConnection.getAlwaysSendSetIsolation();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoClosePStmtStreams() {
/*  818 */     return this.currentConnection.getAutoClosePStmtStreams();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoDeserialize() {
/*  822 */     return this.currentConnection.getAutoDeserialize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoGenerateTestcaseScript() {
/*  826 */     return this.currentConnection.getAutoGenerateTestcaseScript();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoReconnectForPools() {
/*  830 */     return this.currentConnection.getAutoReconnectForPools();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoSlowLog() {
/*  834 */     return this.currentConnection.getAutoSlowLog();
/*      */   }
/*      */ 
/*      */   public synchronized int getBlobSendChunkSize() {
/*  838 */     return this.currentConnection.getBlobSendChunkSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getBlobsAreStrings() {
/*  842 */     return this.currentConnection.getBlobsAreStrings();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCacheCallableStatements() {
/*  846 */     return this.currentConnection.getCacheCallableStatements();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCacheCallableStmts() {
/*  850 */     return this.currentConnection.getCacheCallableStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCachePrepStmts() {
/*  854 */     return this.currentConnection.getCachePrepStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCachePreparedStatements() {
/*  858 */     return this.currentConnection.getCachePreparedStatements();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCacheResultSetMetadata() {
/*  862 */     return this.currentConnection.getCacheResultSetMetadata();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCacheServerConfiguration() {
/*  866 */     return this.currentConnection.getCacheServerConfiguration();
/*      */   }
/*      */ 
/*      */   public synchronized int getCallableStatementCacheSize() {
/*  870 */     return this.currentConnection.getCallableStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized int getCallableStmtCacheSize() {
/*  874 */     return this.currentConnection.getCallableStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCapitalizeTypeNames() {
/*  878 */     return this.currentConnection.getCapitalizeTypeNames();
/*      */   }
/*      */ 
/*      */   public synchronized String getCharacterSetResults() {
/*  882 */     return this.currentConnection.getCharacterSetResults();
/*      */   }
/*      */ 
/*      */   public synchronized String getClientCertificateKeyStorePassword() {
/*  886 */     return this.currentConnection.getClientCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public synchronized String getClientCertificateKeyStoreType() {
/*  890 */     return this.currentConnection.getClientCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public synchronized String getClientCertificateKeyStoreUrl() {
/*  894 */     return this.currentConnection.getClientCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public synchronized String getClientInfoProvider() {
/*  898 */     return this.currentConnection.getClientInfoProvider();
/*      */   }
/*      */ 
/*      */   public synchronized String getClobCharacterEncoding() {
/*  902 */     return this.currentConnection.getClobCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getClobberStreamingResults() {
/*  906 */     return this.currentConnection.getClobberStreamingResults();
/*      */   }
/*      */ 
/*      */   public synchronized int getConnectTimeout() {
/*  910 */     return this.currentConnection.getConnectTimeout();
/*      */   }
/*      */ 
/*      */   public synchronized String getConnectionCollation() {
/*  914 */     return this.currentConnection.getConnectionCollation();
/*      */   }
/*      */ 
/*      */   public synchronized String getConnectionLifecycleInterceptors() {
/*  918 */     return this.currentConnection.getConnectionLifecycleInterceptors();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getContinueBatchOnError() {
/*  922 */     return this.currentConnection.getContinueBatchOnError();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCreateDatabaseIfNotExist() {
/*  926 */     return this.currentConnection.getCreateDatabaseIfNotExist();
/*      */   }
/*      */ 
/*      */   public synchronized int getDefaultFetchSize() {
/*  930 */     return this.currentConnection.getDefaultFetchSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getDontTrackOpenResources() {
/*  934 */     return this.currentConnection.getDontTrackOpenResources();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getDumpMetadataOnColumnNotFound() {
/*  938 */     return this.currentConnection.getDumpMetadataOnColumnNotFound();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getDumpQueriesOnException() {
/*  942 */     return this.currentConnection.getDumpQueriesOnException();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getDynamicCalendars() {
/*  946 */     return this.currentConnection.getDynamicCalendars();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getElideSetAutoCommits() {
/*  950 */     return this.currentConnection.getElideSetAutoCommits();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEmptyStringsConvertToZero() {
/*  954 */     return this.currentConnection.getEmptyStringsConvertToZero();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEmulateLocators() {
/*  958 */     return this.currentConnection.getEmulateLocators();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEmulateUnsupportedPstmts() {
/*  962 */     return this.currentConnection.getEmulateUnsupportedPstmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEnablePacketDebug() {
/*  966 */     return this.currentConnection.getEnablePacketDebug();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEnableQueryTimeouts() {
/*  970 */     return this.currentConnection.getEnableQueryTimeouts();
/*      */   }
/*      */ 
/*      */   public synchronized String getEncoding() {
/*  974 */     return this.currentConnection.getEncoding();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getExplainSlowQueries() {
/*  978 */     return this.currentConnection.getExplainSlowQueries();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getFailOverReadOnly() {
/*  982 */     return this.currentConnection.getFailOverReadOnly();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getFunctionsNeverReturnBlobs() {
/*  986 */     return this.currentConnection.getFunctionsNeverReturnBlobs();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getGatherPerfMetrics() {
/*  990 */     return this.currentConnection.getGatherPerfMetrics();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getGatherPerformanceMetrics() {
/*  994 */     return this.currentConnection.getGatherPerformanceMetrics();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getGenerateSimpleParameterMetadata() {
/*  998 */     return this.currentConnection.getGenerateSimpleParameterMetadata();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getHoldResultsOpenOverStatementClose() {
/* 1002 */     return this.currentConnection.getHoldResultsOpenOverStatementClose();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getIgnoreNonTxTables() {
/* 1006 */     return this.currentConnection.getIgnoreNonTxTables();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 1010 */     return this.currentConnection.getIncludeInnodbStatusInDeadlockExceptions();
/*      */   }
/*      */ 
/*      */   public synchronized int getInitialTimeout() {
/* 1014 */     return this.currentConnection.getInitialTimeout();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getInteractiveClient() {
/* 1018 */     return this.currentConnection.getInteractiveClient();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getIsInteractiveClient() {
/* 1022 */     return this.currentConnection.getIsInteractiveClient();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getJdbcCompliantTruncation() {
/* 1026 */     return this.currentConnection.getJdbcCompliantTruncation();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getJdbcCompliantTruncationForReads() {
/* 1030 */     return this.currentConnection.getJdbcCompliantTruncationForReads();
/*      */   }
/*      */ 
/*      */   public synchronized String getLargeRowSizeThreshold() {
/* 1034 */     return this.currentConnection.getLargeRowSizeThreshold();
/*      */   }
/*      */ 
/*      */   public synchronized String getLoadBalanceStrategy() {
/* 1038 */     return this.currentConnection.getLoadBalanceStrategy();
/*      */   }
/*      */ 
/*      */   public synchronized String getLocalSocketAddress() {
/* 1042 */     return this.currentConnection.getLocalSocketAddress();
/*      */   }
/*      */ 
/*      */   public synchronized int getLocatorFetchBufferSize() {
/* 1046 */     return this.currentConnection.getLocatorFetchBufferSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getLogSlowQueries() {
/* 1050 */     return this.currentConnection.getLogSlowQueries();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getLogXaCommands() {
/* 1054 */     return this.currentConnection.getLogXaCommands();
/*      */   }
/*      */ 
/*      */   public synchronized String getLogger() {
/* 1058 */     return this.currentConnection.getLogger();
/*      */   }
/*      */ 
/*      */   public synchronized String getLoggerClassName() {
/* 1062 */     return this.currentConnection.getLoggerClassName();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getMaintainTimeStats() {
/* 1066 */     return this.currentConnection.getMaintainTimeStats();
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxQuerySizeToLog() {
/* 1070 */     return this.currentConnection.getMaxQuerySizeToLog();
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxReconnects() {
/* 1074 */     return this.currentConnection.getMaxReconnects();
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxRows() {
/* 1078 */     return this.currentConnection.getMaxRows();
/*      */   }
/*      */ 
/*      */   public synchronized int getMetadataCacheSize() {
/* 1082 */     return this.currentConnection.getMetadataCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized int getNetTimeoutForStreamingResults() {
/* 1086 */     return this.currentConnection.getNetTimeoutForStreamingResults();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNoAccessToProcedureBodies() {
/* 1090 */     return this.currentConnection.getNoAccessToProcedureBodies();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNoDatetimeStringSync() {
/* 1094 */     return this.currentConnection.getNoDatetimeStringSync();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNoTimezoneConversionForTimeType() {
/* 1098 */     return this.currentConnection.getNoTimezoneConversionForTimeType();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNullCatalogMeansCurrent() {
/* 1102 */     return this.currentConnection.getNullCatalogMeansCurrent();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNullNamePatternMatchesAll() {
/* 1106 */     return this.currentConnection.getNullNamePatternMatchesAll();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getOverrideSupportsIntegrityEnhancementFacility() {
/* 1110 */     return this.currentConnection.getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */ 
/*      */   public synchronized int getPacketDebugBufferSize() {
/* 1114 */     return this.currentConnection.getPacketDebugBufferSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getPadCharsWithSpace() {
/* 1118 */     return this.currentConnection.getPadCharsWithSpace();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getParanoid() {
/* 1122 */     return this.currentConnection.getParanoid();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getPedantic() {
/* 1126 */     return this.currentConnection.getPedantic();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getPinGlobalTxToPhysicalConnection() {
/* 1130 */     return this.currentConnection.getPinGlobalTxToPhysicalConnection();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getPopulateInsertRowWithDefaultValues() {
/* 1134 */     return this.currentConnection.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */ 
/*      */   public synchronized int getPrepStmtCacheSize() {
/* 1138 */     return this.currentConnection.getPrepStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized int getPrepStmtCacheSqlLimit() {
/* 1142 */     return this.currentConnection.getPrepStmtCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public synchronized int getPreparedStatementCacheSize() {
/* 1146 */     return this.currentConnection.getPreparedStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized int getPreparedStatementCacheSqlLimit() {
/* 1150 */     return this.currentConnection.getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getProcessEscapeCodesForPrepStmts() {
/* 1154 */     return this.currentConnection.getProcessEscapeCodesForPrepStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getProfileSQL() {
/* 1158 */     return this.currentConnection.getProfileSQL();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getProfileSql() {
/* 1162 */     return this.currentConnection.getProfileSql();
/*      */   }
/*      */ 
/*      */   public synchronized String getProfilerEventHandler() {
/* 1166 */     return this.currentConnection.getProfilerEventHandler();
/*      */   }
/*      */ 
/*      */   public synchronized String getPropertiesTransform() {
/* 1170 */     return this.currentConnection.getPropertiesTransform();
/*      */   }
/*      */ 
/*      */   public synchronized int getQueriesBeforeRetryMaster() {
/* 1174 */     return this.currentConnection.getQueriesBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getReconnectAtTxEnd() {
/* 1178 */     return this.currentConnection.getReconnectAtTxEnd();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRelaxAutoCommit() {
/* 1182 */     return this.currentConnection.getRelaxAutoCommit();
/*      */   }
/*      */ 
/*      */   public synchronized int getReportMetricsIntervalMillis() {
/* 1186 */     return this.currentConnection.getReportMetricsIntervalMillis();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRequireSSL() {
/* 1190 */     return this.currentConnection.getRequireSSL();
/*      */   }
/*      */ 
/*      */   public synchronized String getResourceId() {
/* 1194 */     return this.currentConnection.getResourceId();
/*      */   }
/*      */ 
/*      */   public synchronized int getResultSetSizeThreshold() {
/* 1198 */     return this.currentConnection.getResultSetSizeThreshold();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRewriteBatchedStatements() {
/* 1202 */     return this.currentConnection.getRewriteBatchedStatements();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRollbackOnPooledClose() {
/* 1206 */     return this.currentConnection.getRollbackOnPooledClose();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRoundRobinLoadBalance() {
/* 1210 */     return this.currentConnection.getRoundRobinLoadBalance();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRunningCTS13() {
/* 1214 */     return this.currentConnection.getRunningCTS13();
/*      */   }
/*      */ 
/*      */   public synchronized int getSecondsBeforeRetryMaster() {
/* 1218 */     return this.currentConnection.getSecondsBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public synchronized int getSelfDestructOnPingMaxOperations() {
/* 1222 */     return this.currentConnection.getSelfDestructOnPingMaxOperations();
/*      */   }
/*      */ 
/*      */   public synchronized int getSelfDestructOnPingSecondsLifetime() {
/* 1226 */     return this.currentConnection.getSelfDestructOnPingSecondsLifetime();
/*      */   }
/*      */ 
/*      */   public synchronized String getServerTimezone() {
/* 1230 */     return this.currentConnection.getServerTimezone();
/*      */   }
/*      */ 
/*      */   public synchronized String getSessionVariables() {
/* 1234 */     return this.currentConnection.getSessionVariables();
/*      */   }
/*      */ 
/*      */   public synchronized int getSlowQueryThresholdMillis() {
/* 1238 */     return this.currentConnection.getSlowQueryThresholdMillis();
/*      */   }
/*      */ 
/*      */   public synchronized long getSlowQueryThresholdNanos() {
/* 1242 */     return this.currentConnection.getSlowQueryThresholdNanos();
/*      */   }
/*      */ 
/*      */   public synchronized String getSocketFactory() {
/* 1246 */     return this.currentConnection.getSocketFactory();
/*      */   }
/*      */ 
/*      */   public synchronized String getSocketFactoryClassName() {
/* 1250 */     return this.currentConnection.getSocketFactoryClassName();
/*      */   }
/*      */ 
/*      */   public synchronized int getSocketTimeout() {
/* 1254 */     return this.currentConnection.getSocketTimeout();
/*      */   }
/*      */ 
/*      */   public synchronized String getStatementInterceptors() {
/* 1258 */     return this.currentConnection.getStatementInterceptors();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getStrictFloatingPoint() {
/* 1262 */     return this.currentConnection.getStrictFloatingPoint();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getStrictUpdates() {
/* 1266 */     return this.currentConnection.getStrictUpdates();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTcpKeepAlive() {
/* 1270 */     return this.currentConnection.getTcpKeepAlive();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTcpNoDelay() {
/* 1274 */     return this.currentConnection.getTcpNoDelay();
/*      */   }
/*      */ 
/*      */   public synchronized int getTcpRcvBuf() {
/* 1278 */     return this.currentConnection.getTcpRcvBuf();
/*      */   }
/*      */ 
/*      */   public synchronized int getTcpSndBuf() {
/* 1282 */     return this.currentConnection.getTcpSndBuf();
/*      */   }
/*      */ 
/*      */   public synchronized int getTcpTrafficClass() {
/* 1286 */     return this.currentConnection.getTcpTrafficClass();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTinyInt1isBit() {
/* 1290 */     return this.currentConnection.getTinyInt1isBit();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTraceProtocol() {
/* 1294 */     return this.currentConnection.getTraceProtocol();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTransformedBitIsBoolean() {
/* 1298 */     return this.currentConnection.getTransformedBitIsBoolean();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTreatUtilDateAsTimestamp() {
/* 1302 */     return this.currentConnection.getTreatUtilDateAsTimestamp();
/*      */   }
/*      */ 
/*      */   public synchronized String getTrustCertificateKeyStorePassword() {
/* 1306 */     return this.currentConnection.getTrustCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public synchronized String getTrustCertificateKeyStoreType() {
/* 1310 */     return this.currentConnection.getTrustCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public synchronized String getTrustCertificateKeyStoreUrl() {
/* 1314 */     return this.currentConnection.getTrustCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUltraDevHack() {
/* 1318 */     return this.currentConnection.getUltraDevHack();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 1322 */     return this.currentConnection.getUseBlobToStoreUTF8OutsideBMP();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseCompression() {
/* 1326 */     return this.currentConnection.getUseCompression();
/*      */   }
/*      */ 
/*      */   public synchronized String getUseConfigs() {
/* 1330 */     return this.currentConnection.getUseConfigs();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseCursorFetch() {
/* 1334 */     return this.currentConnection.getUseCursorFetch();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseDirectRowUnpack() {
/* 1338 */     return this.currentConnection.getUseDirectRowUnpack();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseDynamicCharsetInfo() {
/* 1342 */     return this.currentConnection.getUseDynamicCharsetInfo();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseFastDateParsing() {
/* 1346 */     return this.currentConnection.getUseFastDateParsing();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseFastIntParsing() {
/* 1350 */     return this.currentConnection.getUseFastIntParsing();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseGmtMillisForDatetimes() {
/* 1354 */     return this.currentConnection.getUseGmtMillisForDatetimes();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseHostsInPrivileges() {
/* 1358 */     return this.currentConnection.getUseHostsInPrivileges();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseInformationSchema() {
/* 1362 */     return this.currentConnection.getUseInformationSchema();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseJDBCCompliantTimezoneShift() {
/* 1366 */     return this.currentConnection.getUseJDBCCompliantTimezoneShift();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseJvmCharsetConverters() {
/* 1370 */     return this.currentConnection.getUseJvmCharsetConverters();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseLegacyDatetimeCode() {
/* 1374 */     return this.currentConnection.getUseLegacyDatetimeCode();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseLocalSessionState() {
/* 1378 */     return this.currentConnection.getUseLocalSessionState();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseNanosForElapsedTime() {
/* 1382 */     return this.currentConnection.getUseNanosForElapsedTime();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseOldAliasMetadataBehavior() {
/* 1386 */     return this.currentConnection.getUseOldAliasMetadataBehavior();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseOldUTF8Behavior() {
/* 1390 */     return this.currentConnection.getUseOldUTF8Behavior();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseOnlyServerErrorMessages() {
/* 1394 */     return this.currentConnection.getUseOnlyServerErrorMessages();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseReadAheadInput() {
/* 1398 */     return this.currentConnection.getUseReadAheadInput();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseSSL() {
/* 1402 */     return this.currentConnection.getUseSSL();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseSSPSCompatibleTimezoneShift() {
/* 1406 */     return this.currentConnection.getUseSSPSCompatibleTimezoneShift();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseServerPrepStmts() {
/* 1410 */     return this.currentConnection.getUseServerPrepStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseServerPreparedStmts() {
/* 1414 */     return this.currentConnection.getUseServerPreparedStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseSqlStateCodes() {
/* 1418 */     return this.currentConnection.getUseSqlStateCodes();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseStreamLengthsInPrepStmts() {
/* 1422 */     return this.currentConnection.getUseStreamLengthsInPrepStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseTimezone() {
/* 1426 */     return this.currentConnection.getUseTimezone();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseUltraDevWorkAround() {
/* 1430 */     return this.currentConnection.getUseUltraDevWorkAround();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseUnbufferedInput() {
/* 1434 */     return this.currentConnection.getUseUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseUnicode() {
/* 1438 */     return this.currentConnection.getUseUnicode();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseUsageAdvisor() {
/* 1442 */     return this.currentConnection.getUseUsageAdvisor();
/*      */   }
/*      */ 
/*      */   public synchronized String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 1446 */     return this.currentConnection.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public synchronized String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 1450 */     return this.currentConnection.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getVerifyServerCertificate() {
/* 1454 */     return this.currentConnection.getVerifyServerCertificate();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getYearIsDateType() {
/* 1458 */     return this.currentConnection.getYearIsDateType();
/*      */   }
/*      */ 
/*      */   public synchronized String getZeroDateTimeBehavior() {
/* 1462 */     return this.currentConnection.getZeroDateTimeBehavior();
/*      */   }
/*      */ 
/*      */   public synchronized void setAllowLoadLocalInfile(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAllowMultiQueries(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAllowNanAndInf(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAllowUrlInLocalInfile(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAlwaysSendSetIsolation(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoClosePStmtStreams(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoDeserialize(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoGenerateTestcaseScript(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoReconnect(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoReconnectForConnectionPools(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoReconnectForPools(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoSlowLog(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setBlobSendChunkSize(String value)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setBlobsAreStrings(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCacheCallableStatements(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCacheCallableStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCachePrepStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCachePreparedStatements(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCacheResultSetMetadata(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCacheServerConfiguration(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCallableStatementCacheSize(int size)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCallableStmtCacheSize(int cacheSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCapitalizeDBMDTypes(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCapitalizeTypeNames(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCharacterEncoding(String encoding)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCharacterSetResults(String characterSet)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClientCertificateKeyStorePassword(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClientCertificateKeyStoreType(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClientCertificateKeyStoreUrl(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClientInfoProvider(String classname)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClobCharacterEncoding(String encoding)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClobberStreamingResults(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setConnectTimeout(int timeoutMs)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setConnectionCollation(String collation)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setConnectionLifecycleInterceptors(String interceptors)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setContinueBatchOnError(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCreateDatabaseIfNotExist(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDefaultFetchSize(int n)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDetectServerPreparedStmts(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDontTrackOpenResources(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDumpMetadataOnColumnNotFound(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDumpQueriesOnException(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDynamicCalendars(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setElideSetAutoCommits(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEmptyStringsConvertToZero(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEmulateLocators(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEmulateUnsupportedPstmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEnablePacketDebug(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEnableQueryTimeouts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEncoding(String property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setExplainSlowQueries(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setFailOverReadOnly(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setFunctionsNeverReturnBlobs(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setGatherPerfMetrics(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setGatherPerformanceMetrics(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setGenerateSimpleParameterMetadata(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setHoldResultsOpenOverStatementClose(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setIgnoreNonTxTables(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setIncludeInnodbStatusInDeadlockExceptions(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setInitialTimeout(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setInteractiveClient(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setIsInteractiveClient(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setJdbcCompliantTruncation(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLargeRowSizeThreshold(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLoadBalanceStrategy(String strategy)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLocalSocketAddress(String address)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLocatorFetchBufferSize(String value)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLogSlowQueries(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLogXaCommands(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLogger(String property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLoggerClassName(String className)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMaintainTimeStats(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMaxQuerySizeToLog(int sizeInBytes)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMaxReconnects(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMaxRows(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMetadataCacheSize(int value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNetTimeoutForStreamingResults(int value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNoAccessToProcedureBodies(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNoDatetimeStringSync(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNoTimezoneConversionForTimeType(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNullCatalogMeansCurrent(boolean value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNullNamePatternMatchesAll(boolean value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setOverrideSupportsIntegrityEnhancementFacility(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPacketDebugBufferSize(int size)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPadCharsWithSpace(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setParanoid(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPedantic(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPinGlobalTxToPhysicalConnection(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPopulateInsertRowWithDefaultValues(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPrepStmtCacheSize(int cacheSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPrepStmtCacheSqlLimit(int sqlLimit)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPreparedStatementCacheSize(int cacheSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPreparedStatementCacheSqlLimit(int cacheSqlLimit)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setProcessEscapeCodesForPrepStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setProfileSQL(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setProfileSql(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setProfilerEventHandler(String handler)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPropertiesTransform(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setQueriesBeforeRetryMaster(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setReconnectAtTxEnd(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRelaxAutoCommit(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setReportMetricsIntervalMillis(int millis)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRequireSSL(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setResourceId(String resourceId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setResultSetSizeThreshold(int threshold)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRetainStatementAfterResultSetClose(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRewriteBatchedStatements(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRollbackOnPooledClose(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRoundRobinLoadBalance(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRunningCTS13(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSecondsBeforeRetryMaster(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSelfDestructOnPingMaxOperations(int maxOperations)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSelfDestructOnPingSecondsLifetime(int seconds)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setServerTimezone(String property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSessionVariables(String variables)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSlowQueryThresholdMillis(int millis)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSlowQueryThresholdNanos(long nanos)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSocketFactory(String name)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSocketFactoryClassName(String property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSocketTimeout(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setStatementInterceptors(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setStrictFloatingPoint(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setStrictUpdates(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpKeepAlive(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpNoDelay(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpRcvBuf(int bufSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpSndBuf(int bufSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpTrafficClass(int classFlags)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTinyInt1isBit(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTraceProtocol(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTransformedBitIsBoolean(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTreatUtilDateAsTimestamp(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTrustCertificateKeyStorePassword(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTrustCertificateKeyStoreType(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTrustCertificateKeyStoreUrl(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUltraDevHack(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseBlobToStoreUTF8OutsideBMP(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseCompression(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseConfigs(String configs)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseCursorFetch(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseDirectRowUnpack(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseDynamicCharsetInfo(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseFastDateParsing(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseFastIntParsing(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseGmtMillisForDatetimes(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseHostsInPrivileges(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseInformationSchema(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseJDBCCompliantTimezoneShift(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseJvmCharsetConverters(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseLegacyDatetimeCode(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseLocalSessionState(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseNanosForElapsedTime(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseOldAliasMetadataBehavior(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseOldUTF8Behavior(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseOnlyServerErrorMessages(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseReadAheadInput(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseSSL(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseSSPSCompatibleTimezoneShift(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseServerPrepStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseServerPreparedStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseSqlStateCodes(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseStreamLengthsInPrepStmts(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseTimezone(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseUltraDevWorkAround(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseUnbufferedInput(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseUnicode(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseUsageAdvisor(boolean useUsageAdvisorFlag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setVerifyServerCertificate(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setYearIsDateType(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setZeroDateTimeBehavior(String behavior)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized boolean useUnbufferedInput()
/*      */   {
/* 2332 */     return this.currentConnection.useUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isSameResource(Connection c) {
/* 2336 */     return this.currentConnection.isSameResource(c);
/*      */   }
/*      */ 
/*      */   public void setInGlobalTx(boolean flag) {
/* 2340 */     this.currentConnection.setInGlobalTx(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseColumnNamesInFindColumn() {
/* 2344 */     return this.currentConnection.getUseColumnNamesInFindColumn();
/*      */   }
/*      */ 
/*      */   public void setUseColumnNamesInFindColumn(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalTransactionState() {
/* 2352 */     return this.currentConnection.getUseLocalTransactionState();
/*      */   }
/*      */ 
/*      */   public void setUseLocalTransactionState(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean getCompensateOnDuplicateKeyUpdateCounts()
/*      */   {
/* 2361 */     return this.currentConnection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */   }
/*      */ 
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean getUseAffectedRows()
/*      */   {
/* 2370 */     return this.currentConnection.getUseAffectedRows();
/*      */   }
/*      */ 
/*      */   public void setUseAffectedRows(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public String getPasswordCharacterEncoding()
/*      */   {
/* 2379 */     return this.currentConnection.getPasswordCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public void setPasswordCharacterEncoding(String characterSet) {
/* 2383 */     this.currentConnection.setPasswordCharacterEncoding(characterSet);
/*      */   }
/*      */ 
/*      */   public int getAutoIncrementIncrement() {
/* 2387 */     return this.currentConnection.getAutoIncrementIncrement();
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceBlacklistTimeout() {
/* 2391 */     return this.currentConnection.getLoadBalanceBlacklistTimeout();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) {
/* 2395 */     this.currentConnection.setLoadBalanceBlacklistTimeout(loadBalanceBlacklistTimeout);
/*      */   }
/*      */ 
/*      */   public int getRetriesAllDown() {
/* 2399 */     return this.currentConnection.getRetriesAllDown();
/*      */   }
/*      */ 
/*      */   public void setRetriesAllDown(int retriesAllDown) {
/* 2403 */     this.currentConnection.setRetriesAllDown(retriesAllDown);
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/* 2407 */     return this.currentConnection.getExceptionInterceptor();
/*      */   }
/*      */ 
/*      */   public String getExceptionInterceptors() {
/* 2411 */     return this.currentConnection.getExceptionInterceptors();
/*      */   }
/*      */ 
/*      */   public void setExceptionInterceptors(String exceptionInterceptors) {
/* 2415 */     this.currentConnection.setExceptionInterceptors(exceptionInterceptors);
/*      */   }
/*      */ 
/*      */   public boolean getQueryTimeoutKillsConnection() {
/* 2419 */     return this.currentConnection.getQueryTimeoutKillsConnection();
/*      */   }
/*      */ 
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection)
/*      */   {
/* 2424 */     this.currentConnection.setQueryTimeoutKillsConnection(queryTimeoutKillsConnection);
/*      */   }
/*      */ 
/*      */   public boolean hasSameProperties(Connection c) {
/* 2428 */     return (this.masterConnection.hasSameProperties(c)) && (this.slavesConnection.hasSameProperties(c));
/*      */   }
/*      */ 
/*      */   public Properties getProperties()
/*      */   {
/* 2433 */     Properties props = new Properties();
/* 2434 */     props.putAll(this.masterConnection.getProperties());
/* 2435 */     props.putAll(this.slavesConnection.getProperties());
/*      */ 
/* 2437 */     return props;
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ReplicationConnection
 * JD-Core Version:    0.6.0
 */