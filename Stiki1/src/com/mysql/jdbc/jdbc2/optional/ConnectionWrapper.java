/*      */ package com.mysql.jdbc.jdbc2.optional;
/*      */ 
/*      */ import com.mysql.jdbc.Connection;
/*      */ import com.mysql.jdbc.ExceptionInterceptor;
/*      */ import com.mysql.jdbc.Extension;
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import com.mysql.jdbc.Util;
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public class ConnectionWrapper extends WrapperBase
/*      */   implements Connection
/*      */ {
/*   69 */   protected Connection mc = null;
/*      */ 
/*   71 */   private String invalidHandleStr = "Logical handle no longer valid";
/*      */   private boolean closed;
/*      */   private boolean isForXa;
/*      */   private static final Constructor JDBC_4_CONNECTION_WRAPPER_CTOR;
/*      */ 
/*      */   protected static ConnectionWrapper getInstance(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa)
/*      */     throws SQLException
/*      */   {
/*  102 */     if (!Util.isJdbc4()) {
/*  103 */       return new ConnectionWrapper(mysqlPooledConnection, mysqlConnection, forXa);
/*      */     }
/*      */ 
/*  107 */     return (ConnectionWrapper)Util.handleNewInstance(JDBC_4_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlPooledConnection, mysqlConnection, Boolean.valueOf(forXa) }, mysqlPooledConnection.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa)
/*      */     throws SQLException
/*      */   {
/*  126 */     super(mysqlPooledConnection);
/*      */ 
/*  128 */     this.mc = mysqlConnection;
/*  129 */     this.closed = false;
/*  130 */     this.isForXa = forXa;
/*      */ 
/*  132 */     if (this.isForXa)
/*  133 */       setInGlobalTx(false);
/*      */   }
/*      */ 
/*      */   public void setAutoCommit(boolean autoCommit)
/*      */     throws SQLException
/*      */   {
/*  144 */     checkClosed();
/*      */ 
/*  146 */     if ((autoCommit) && (isInGlobalTx())) {
/*  147 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  154 */       this.mc.setAutoCommit(autoCommit);
/*      */     } catch (SQLException sqlException) {
/*  156 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/*  167 */     checkClosed();
/*      */     try
/*      */     {
/*  170 */       return this.mc.getAutoCommit();
/*      */     } catch (SQLException sqlException) {
/*  172 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  175 */     return false;
/*      */   }
/*      */ 
/*      */   public void setCatalog(String catalog)
/*      */     throws SQLException
/*      */   {
/*  185 */     checkClosed();
/*      */     try
/*      */     {
/*  188 */       this.mc.setCatalog(catalog);
/*      */     } catch (SQLException sqlException) {
/*  190 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCatalog()
/*      */     throws SQLException
/*      */   {
/*  204 */     checkClosed();
/*      */     try
/*      */     {
/*  207 */       return this.mc.getCatalog();
/*      */     } catch (SQLException sqlException) {
/*  209 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  212 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */     throws SQLException
/*      */   {
/*  222 */     return (this.closed) || (this.mc.isClosed());
/*      */   }
/*      */ 
/*      */   public boolean isMasterConnection() {
/*  226 */     return this.mc.isMasterConnection();
/*      */   }
/*      */ 
/*      */   public void setHoldability(int arg0)
/*      */     throws SQLException
/*      */   {
/*  233 */     checkClosed();
/*      */     try
/*      */     {
/*  236 */       this.mc.setHoldability(arg0);
/*      */     } catch (SQLException sqlException) {
/*  238 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/*  246 */     checkClosed();
/*      */     try
/*      */     {
/*  249 */       return this.mc.getHoldability();
/*      */     } catch (SQLException sqlException) {
/*  251 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  254 */     return 1;
/*      */   }
/*      */ 
/*      */   public long getIdleFor()
/*      */   {
/*  264 */     return this.mc.getIdleFor();
/*      */   }
/*      */ 
/*      */   public DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/*  277 */     checkClosed();
/*      */     try
/*      */     {
/*  280 */       return this.mc.getMetaData();
/*      */     } catch (SQLException sqlException) {
/*  282 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  285 */     return null;
/*      */   }
/*      */ 
/*      */   public void setReadOnly(boolean readOnly)
/*      */     throws SQLException
/*      */   {
/*  295 */     checkClosed();
/*      */     try
/*      */     {
/*  298 */       this.mc.setReadOnly(readOnly);
/*      */     } catch (SQLException sqlException) {
/*  300 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*  311 */     checkClosed();
/*      */     try
/*      */     {
/*  314 */       return this.mc.isReadOnly();
/*      */     } catch (SQLException sqlException) {
/*  316 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  319 */     return false;
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/*  326 */     checkClosed();
/*      */ 
/*  328 */     if (isInGlobalTx()) {
/*  329 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  336 */       return this.mc.setSavepoint();
/*      */     } catch (SQLException sqlException) {
/*  338 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  341 */     return null;
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint(String arg0)
/*      */     throws SQLException
/*      */   {
/*  348 */     checkClosed();
/*      */ 
/*  350 */     if (isInGlobalTx()) {
/*  351 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  358 */       return this.mc.setSavepoint(arg0);
/*      */     } catch (SQLException sqlException) {
/*  360 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  363 */     return null;
/*      */   }
/*      */ 
/*      */   public void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/*  373 */     checkClosed();
/*      */     try
/*      */     {
/*  376 */       this.mc.setTransactionIsolation(level);
/*      */     } catch (SQLException sqlException) {
/*  378 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/*  389 */     checkClosed();
/*      */     try
/*      */     {
/*  392 */       return this.mc.getTransactionIsolation();
/*      */     } catch (SQLException sqlException) {
/*  394 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  397 */     return 4;
/*      */   }
/*      */ 
/*      */   public void setTypeMap(Map map)
/*      */     throws SQLException
/*      */   {
/*  408 */     checkClosed();
/*      */     try
/*      */     {
/*  411 */       this.mc.setTypeMap(map);
/*      */     } catch (SQLException sqlException) {
/*  413 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Map getTypeMap()
/*      */     throws SQLException
/*      */   {
/*  424 */     checkClosed();
/*      */     try
/*      */     {
/*  427 */       return this.mc.getTypeMap();
/*      */     } catch (SQLException sqlException) {
/*  429 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  432 */     return null;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/*  442 */     checkClosed();
/*      */     try
/*      */     {
/*  445 */       return this.mc.getWarnings();
/*      */     } catch (SQLException sqlException) {
/*  447 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  450 */     return null;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  461 */     checkClosed();
/*      */     try
/*      */     {
/*  464 */       this.mc.clearWarnings();
/*      */     } catch (SQLException sqlException) {
/*  466 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  481 */     close(true);
/*      */   }
/*      */ 
/*      */   public void commit()
/*      */     throws SQLException
/*      */   {
/*  492 */     checkClosed();
/*      */ 
/*  494 */     if (isInGlobalTx()) {
/*  495 */       throw SQLError.createSQLException("Can't call commit() on an XAConnection associated with a global transaction", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  503 */       this.mc.commit();
/*      */     } catch (SQLException sqlException) {
/*  505 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Statement createStatement()
/*      */     throws SQLException
/*      */   {
/*  516 */     checkClosed();
/*      */     try
/*      */     {
/*  519 */       return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement());
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  522 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  525 */     return null;
/*      */   }
/*      */ 
/*      */   public Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  536 */     checkClosed();
/*      */     try
/*      */     {
/*  539 */       return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement(resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  542 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  545 */     return null;
/*      */   }
/*      */ 
/*      */   public Statement createStatement(int arg0, int arg1, int arg2)
/*      */     throws SQLException
/*      */   {
/*  553 */     checkClosed();
/*      */     try
/*      */     {
/*  556 */       return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement(arg0, arg1, arg2));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  559 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  562 */     return null;
/*      */   }
/*      */ 
/*      */   public String nativeSQL(String sql)
/*      */     throws SQLException
/*      */   {
/*  572 */     checkClosed();
/*      */     try
/*      */     {
/*  575 */       return this.mc.nativeSQL(sql);
/*      */     } catch (SQLException sqlException) {
/*  577 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  580 */     return null;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/*  591 */     checkClosed();
/*      */     try
/*      */     {
/*  594 */       return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  597 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  600 */     return null;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  611 */     checkClosed();
/*      */     try
/*      */     {
/*  614 */       return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  617 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  620 */     return null;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3)
/*      */     throws SQLException
/*      */   {
/*  628 */     checkClosed();
/*      */     try
/*      */     {
/*  631 */       return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(arg0, arg1, arg2, arg3));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  634 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  637 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepare(String sql) throws SQLException
/*      */   {
/*  642 */     checkClosed();
/*      */     try
/*      */     {
/*  645 */       return new PreparedStatementWrapper(this, this.pooledConnection, this.mc.clientPrepareStatement(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  648 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  651 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepare(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*  656 */     checkClosed();
/*      */     try
/*      */     {
/*  659 */       return new PreparedStatementWrapper(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/*  663 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  666 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/*  677 */     checkClosed();
/*      */     try
/*      */     {
/*  680 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  683 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  686 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  697 */     checkClosed();
/*      */     try
/*      */     {
/*  700 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/*  704 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  707 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3)
/*      */     throws SQLException
/*      */   {
/*  715 */     checkClosed();
/*      */     try
/*      */     {
/*  718 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1, arg2, arg3));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  721 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  724 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String arg0, int arg1)
/*      */     throws SQLException
/*      */   {
/*  732 */     checkClosed();
/*      */     try
/*      */     {
/*  735 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  738 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  741 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String arg0, int[] arg1)
/*      */     throws SQLException
/*      */   {
/*  749 */     checkClosed();
/*      */     try
/*      */     {
/*  752 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  755 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  758 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String arg0, String[] arg1)
/*      */     throws SQLException
/*      */   {
/*  766 */     checkClosed();
/*      */     try
/*      */     {
/*  769 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  772 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  775 */     return null;
/*      */   }
/*      */ 
/*      */   public void releaseSavepoint(Savepoint arg0)
/*      */     throws SQLException
/*      */   {
/*  782 */     checkClosed();
/*      */     try
/*      */     {
/*  785 */       this.mc.releaseSavepoint(arg0);
/*      */     } catch (SQLException sqlException) {
/*  787 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback()
/*      */     throws SQLException
/*      */   {
/*  798 */     checkClosed();
/*      */ 
/*  800 */     if (isInGlobalTx()) {
/*  801 */       throw SQLError.createSQLException("Can't call rollback() on an XAConnection associated with a global transaction", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  809 */       this.mc.rollback();
/*      */     } catch (SQLException sqlException) {
/*  811 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback(Savepoint arg0)
/*      */     throws SQLException
/*      */   {
/*  819 */     checkClosed();
/*      */ 
/*  821 */     if (isInGlobalTx()) {
/*  822 */       throw SQLError.createSQLException("Can't call rollback() on an XAConnection associated with a global transaction", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  830 */       this.mc.rollback(arg0);
/*      */     } catch (SQLException sqlException) {
/*  832 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isSameResource(Connection c) {
/*  837 */     if ((c instanceof ConnectionWrapper))
/*  838 */       return this.mc.isSameResource(((ConnectionWrapper)c).mc);
/*  839 */     if ((c instanceof Connection)) {
/*  840 */       return this.mc.isSameResource(c);
/*      */     }
/*      */ 
/*  843 */     return false;
/*      */   }
/*      */ 
/*      */   protected void close(boolean fireClosedEvent) throws SQLException {
/*  847 */     synchronized (this.pooledConnection) {
/*  848 */       if (this.closed) {
/*  849 */         return;
/*      */       }
/*      */ 
/*  852 */       if ((!isInGlobalTx()) && (this.mc.getRollbackOnPooledClose()) && (!getAutoCommit()))
/*      */       {
/*  854 */         rollback();
/*      */       }
/*      */ 
/*  857 */       if (fireClosedEvent) {
/*  858 */         this.pooledConnection.callConnectionEventListeners(2, null);
/*      */       }
/*      */ 
/*  867 */       this.closed = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkClosed() throws SQLException {
/*  872 */     if (this.closed)
/*  873 */       throw SQLError.createSQLException(this.invalidHandleStr, this.exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public boolean isInGlobalTx()
/*      */   {
/*  878 */     return this.mc.isInGlobalTx();
/*      */   }
/*      */ 
/*      */   public void setInGlobalTx(boolean flag) {
/*  882 */     this.mc.setInGlobalTx(flag);
/*      */   }
/*      */ 
/*      */   public void ping() throws SQLException {
/*  886 */     if (this.mc != null)
/*  887 */       this.mc.ping();
/*      */   }
/*      */ 
/*      */   public void changeUser(String userName, String newPassword)
/*      */     throws SQLException
/*      */   {
/*  893 */     checkClosed();
/*      */     try
/*      */     {
/*  896 */       this.mc.changeUser(userName, newPassword);
/*      */     } catch (SQLException sqlException) {
/*  898 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearHasTriedMaster() {
/*  903 */     this.mc.clearHasTriedMaster();
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql) throws SQLException
/*      */   {
/*  908 */     checkClosed();
/*      */     try
/*      */     {
/*  911 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  914 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  917 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException
/*      */   {
/*      */     try {
/*  923 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyIndex));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  926 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  929 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*      */     try {
/*  935 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/*  939 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  942 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  949 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/*  953 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  956 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException
/*      */   {
/*      */     try {
/*  962 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyIndexes));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  965 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  968 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException
/*      */   {
/*      */     try {
/*  974 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyColNames));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  977 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  980 */     return null;
/*      */   }
/*      */ 
/*      */   public int getActiveStatementCount() {
/*  984 */     return this.mc.getActiveStatementCount();
/*      */   }
/*      */ 
/*      */   public Log getLog() throws SQLException {
/*  988 */     return this.mc.getLog();
/*      */   }
/*      */ 
/*      */   public String getServerCharacterEncoding() {
/*  992 */     return this.mc.getServerCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public TimeZone getServerTimezoneTZ() {
/*  996 */     return this.mc.getServerTimezoneTZ();
/*      */   }
/*      */ 
/*      */   public String getStatementComment() {
/* 1000 */     return this.mc.getStatementComment();
/*      */   }
/*      */ 
/*      */   public boolean hasTriedMaster() {
/* 1004 */     return this.mc.hasTriedMaster();
/*      */   }
/*      */ 
/*      */   public boolean isAbonormallyLongQuery(long millisOrNanos) {
/* 1008 */     return this.mc.isAbonormallyLongQuery(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public boolean isNoBackslashEscapesSet() {
/* 1012 */     return this.mc.isNoBackslashEscapesSet();
/*      */   }
/*      */ 
/*      */   public boolean lowerCaseTableNames() {
/* 1016 */     return this.mc.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean parserKnowsUnicode() {
/* 1020 */     return this.mc.parserKnowsUnicode();
/*      */   }
/*      */ 
/*      */   public void reportQueryTime(long millisOrNanos) {
/* 1024 */     this.mc.reportQueryTime(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public void resetServerState() throws SQLException {
/* 1028 */     checkClosed();
/*      */     try
/*      */     {
/* 1031 */       this.mc.resetServerState();
/*      */     } catch (SQLException sqlException) {
/* 1033 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql) throws SQLException
/*      */   {
/* 1039 */     checkClosed();
/*      */     try
/*      */     {
/* 1042 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 1045 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1048 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException
/*      */   {
/*      */     try {
/* 1054 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyIndex));
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 1057 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1060 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*      */     try {
/* 1066 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/* 1070 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1073 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1080 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/* 1084 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1087 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException
/*      */   {
/*      */     try {
/* 1093 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyIndexes));
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 1096 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1099 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException
/*      */   {
/*      */     try {
/* 1105 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyColNames));
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 1108 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1111 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFailedOver(boolean flag) {
/* 1115 */     this.mc.setFailedOver(flag);
/*      */   }
/*      */ 
/*      */   public void setPreferSlaveDuringFailover(boolean flag)
/*      */   {
/* 1120 */     this.mc.setPreferSlaveDuringFailover(flag);
/*      */   }
/*      */ 
/*      */   public void setStatementComment(String comment) {
/* 1124 */     this.mc.setStatementComment(comment);
/*      */   }
/*      */ 
/*      */   public void shutdownServer() throws SQLException
/*      */   {
/* 1129 */     checkClosed();
/*      */     try
/*      */     {
/* 1132 */       this.mc.shutdownServer();
/*      */     } catch (SQLException sqlException) {
/* 1134 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean supportsIsolationLevel()
/*      */   {
/* 1140 */     return this.mc.supportsIsolationLevel();
/*      */   }
/*      */ 
/*      */   public boolean supportsQuotedIdentifiers() {
/* 1144 */     return this.mc.supportsQuotedIdentifiers();
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions() {
/* 1148 */     return this.mc.supportsTransactions();
/*      */   }
/*      */ 
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException
/*      */   {
/* 1153 */     checkClosed();
/*      */     try
/*      */     {
/* 1156 */       return this.mc.versionMeetsMinimum(major, minor, subminor);
/*      */     } catch (SQLException sqlException) {
/* 1158 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1161 */     return false;
/*      */   }
/*      */ 
/*      */   public String exposeAsXml() throws SQLException {
/* 1165 */     checkClosed();
/*      */     try
/*      */     {
/* 1168 */       return this.mc.exposeAsXml();
/*      */     } catch (SQLException sqlException) {
/* 1170 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1173 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean getAllowLoadLocalInfile() {
/* 1177 */     return this.mc.getAllowLoadLocalInfile();
/*      */   }
/*      */ 
/*      */   public boolean getAllowMultiQueries() {
/* 1181 */     return this.mc.getAllowMultiQueries();
/*      */   }
/*      */ 
/*      */   public boolean getAllowNanAndInf() {
/* 1185 */     return this.mc.getAllowNanAndInf();
/*      */   }
/*      */ 
/*      */   public boolean getAllowUrlInLocalInfile() {
/* 1189 */     return this.mc.getAllowUrlInLocalInfile();
/*      */   }
/*      */ 
/*      */   public boolean getAlwaysSendSetIsolation() {
/* 1193 */     return this.mc.getAlwaysSendSetIsolation();
/*      */   }
/*      */ 
/*      */   public boolean getAutoClosePStmtStreams() {
/* 1197 */     return this.mc.getAutoClosePStmtStreams();
/*      */   }
/*      */ 
/*      */   public boolean getAutoDeserialize() {
/* 1201 */     return this.mc.getAutoDeserialize();
/*      */   }
/*      */ 
/*      */   public boolean getAutoGenerateTestcaseScript() {
/* 1205 */     return this.mc.getAutoGenerateTestcaseScript();
/*      */   }
/*      */ 
/*      */   public boolean getAutoReconnectForPools() {
/* 1209 */     return this.mc.getAutoReconnectForPools();
/*      */   }
/*      */ 
/*      */   public boolean getAutoSlowLog() {
/* 1213 */     return this.mc.getAutoSlowLog();
/*      */   }
/*      */ 
/*      */   public int getBlobSendChunkSize() {
/* 1217 */     return this.mc.getBlobSendChunkSize();
/*      */   }
/*      */ 
/*      */   public boolean getBlobsAreStrings() {
/* 1221 */     return this.mc.getBlobsAreStrings();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStatements() {
/* 1225 */     return this.mc.getCacheCallableStatements();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStmts() {
/* 1229 */     return this.mc.getCacheCallableStmts();
/*      */   }
/*      */ 
/*      */   public boolean getCachePrepStmts() {
/* 1233 */     return this.mc.getCachePrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getCachePreparedStatements() {
/* 1237 */     return this.mc.getCachePreparedStatements();
/*      */   }
/*      */ 
/*      */   public boolean getCacheResultSetMetadata() {
/* 1241 */     return this.mc.getCacheResultSetMetadata();
/*      */   }
/*      */ 
/*      */   public boolean getCacheServerConfiguration() {
/* 1245 */     return this.mc.getCacheServerConfiguration();
/*      */   }
/*      */ 
/*      */   public int getCallableStatementCacheSize() {
/* 1249 */     return this.mc.getCallableStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public int getCallableStmtCacheSize() {
/* 1253 */     return this.mc.getCallableStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public boolean getCapitalizeTypeNames() {
/* 1257 */     return this.mc.getCapitalizeTypeNames();
/*      */   }
/*      */ 
/*      */   public String getCharacterSetResults() {
/* 1261 */     return this.mc.getCharacterSetResults();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStorePassword() {
/* 1265 */     return this.mc.getClientCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreType() {
/* 1269 */     return this.mc.getClientCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreUrl() {
/* 1273 */     return this.mc.getClientCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public String getClientInfoProvider() {
/* 1277 */     return this.mc.getClientInfoProvider();
/*      */   }
/*      */ 
/*      */   public String getClobCharacterEncoding() {
/* 1281 */     return this.mc.getClobCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public boolean getClobberStreamingResults() {
/* 1285 */     return this.mc.getClobberStreamingResults();
/*      */   }
/*      */ 
/*      */   public int getConnectTimeout() {
/* 1289 */     return this.mc.getConnectTimeout();
/*      */   }
/*      */ 
/*      */   public String getConnectionCollation() {
/* 1293 */     return this.mc.getConnectionCollation();
/*      */   }
/*      */ 
/*      */   public String getConnectionLifecycleInterceptors() {
/* 1297 */     return this.mc.getConnectionLifecycleInterceptors();
/*      */   }
/*      */ 
/*      */   public boolean getContinueBatchOnError() {
/* 1301 */     return this.mc.getContinueBatchOnError();
/*      */   }
/*      */ 
/*      */   public boolean getCreateDatabaseIfNotExist() {
/* 1305 */     return this.mc.getCreateDatabaseIfNotExist();
/*      */   }
/*      */ 
/*      */   public int getDefaultFetchSize() {
/* 1309 */     return this.mc.getDefaultFetchSize();
/*      */   }
/*      */ 
/*      */   public boolean getDontTrackOpenResources() {
/* 1313 */     return this.mc.getDontTrackOpenResources();
/*      */   }
/*      */ 
/*      */   public boolean getDumpMetadataOnColumnNotFound() {
/* 1317 */     return this.mc.getDumpMetadataOnColumnNotFound();
/*      */   }
/*      */ 
/*      */   public boolean getDumpQueriesOnException() {
/* 1321 */     return this.mc.getDumpQueriesOnException();
/*      */   }
/*      */ 
/*      */   public boolean getDynamicCalendars() {
/* 1325 */     return this.mc.getDynamicCalendars();
/*      */   }
/*      */ 
/*      */   public boolean getElideSetAutoCommits() {
/* 1329 */     return this.mc.getElideSetAutoCommits();
/*      */   }
/*      */ 
/*      */   public boolean getEmptyStringsConvertToZero() {
/* 1333 */     return this.mc.getEmptyStringsConvertToZero();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateLocators() {
/* 1337 */     return this.mc.getEmulateLocators();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateUnsupportedPstmts() {
/* 1341 */     return this.mc.getEmulateUnsupportedPstmts();
/*      */   }
/*      */ 
/*      */   public boolean getEnablePacketDebug() {
/* 1345 */     return this.mc.getEnablePacketDebug();
/*      */   }
/*      */ 
/*      */   public boolean getEnableQueryTimeouts() {
/* 1349 */     return this.mc.getEnableQueryTimeouts();
/*      */   }
/*      */ 
/*      */   public String getEncoding() {
/* 1353 */     return this.mc.getEncoding();
/*      */   }
/*      */ 
/*      */   public boolean getExplainSlowQueries() {
/* 1357 */     return this.mc.getExplainSlowQueries();
/*      */   }
/*      */ 
/*      */   public boolean getFailOverReadOnly() {
/* 1361 */     return this.mc.getFailOverReadOnly();
/*      */   }
/*      */ 
/*      */   public boolean getFunctionsNeverReturnBlobs() {
/* 1365 */     return this.mc.getFunctionsNeverReturnBlobs();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerfMetrics() {
/* 1369 */     return this.mc.getGatherPerfMetrics();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerformanceMetrics() {
/* 1373 */     return this.mc.getGatherPerformanceMetrics();
/*      */   }
/*      */ 
/*      */   public boolean getGenerateSimpleParameterMetadata() {
/* 1377 */     return this.mc.getGenerateSimpleParameterMetadata();
/*      */   }
/*      */ 
/*      */   public boolean getHoldResultsOpenOverStatementClose() {
/* 1381 */     return this.mc.getHoldResultsOpenOverStatementClose();
/*      */   }
/*      */ 
/*      */   public boolean getIgnoreNonTxTables() {
/* 1385 */     return this.mc.getIgnoreNonTxTables();
/*      */   }
/*      */ 
/*      */   public boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 1389 */     return this.mc.getIncludeInnodbStatusInDeadlockExceptions();
/*      */   }
/*      */ 
/*      */   public int getInitialTimeout() {
/* 1393 */     return this.mc.getInitialTimeout();
/*      */   }
/*      */ 
/*      */   public boolean getInteractiveClient() {
/* 1397 */     return this.mc.getInteractiveClient();
/*      */   }
/*      */ 
/*      */   public boolean getIsInteractiveClient() {
/* 1401 */     return this.mc.getIsInteractiveClient();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncation() {
/* 1405 */     return this.mc.getJdbcCompliantTruncation();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncationForReads() {
/* 1409 */     return this.mc.getJdbcCompliantTruncationForReads();
/*      */   }
/*      */ 
/*      */   public String getLargeRowSizeThreshold() {
/* 1413 */     return this.mc.getLargeRowSizeThreshold();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceStrategy() {
/* 1417 */     return this.mc.getLoadBalanceStrategy();
/*      */   }
/*      */ 
/*      */   public String getLocalSocketAddress() {
/* 1421 */     return this.mc.getLocalSocketAddress();
/*      */   }
/*      */ 
/*      */   public int getLocatorFetchBufferSize() {
/* 1425 */     return this.mc.getLocatorFetchBufferSize();
/*      */   }
/*      */ 
/*      */   public boolean getLogSlowQueries() {
/* 1429 */     return this.mc.getLogSlowQueries();
/*      */   }
/*      */ 
/*      */   public boolean getLogXaCommands() {
/* 1433 */     return this.mc.getLogXaCommands();
/*      */   }
/*      */ 
/*      */   public String getLogger() {
/* 1437 */     return this.mc.getLogger();
/*      */   }
/*      */ 
/*      */   public String getLoggerClassName() {
/* 1441 */     return this.mc.getLoggerClassName();
/*      */   }
/*      */ 
/*      */   public boolean getMaintainTimeStats() {
/* 1445 */     return this.mc.getMaintainTimeStats();
/*      */   }
/*      */ 
/*      */   public int getMaxQuerySizeToLog() {
/* 1449 */     return this.mc.getMaxQuerySizeToLog();
/*      */   }
/*      */ 
/*      */   public int getMaxReconnects() {
/* 1453 */     return this.mc.getMaxReconnects();
/*      */   }
/*      */ 
/*      */   public int getMaxRows() {
/* 1457 */     return this.mc.getMaxRows();
/*      */   }
/*      */ 
/*      */   public int getMetadataCacheSize() {
/* 1461 */     return this.mc.getMetadataCacheSize();
/*      */   }
/*      */ 
/*      */   public int getNetTimeoutForStreamingResults() {
/* 1465 */     return this.mc.getNetTimeoutForStreamingResults();
/*      */   }
/*      */ 
/*      */   public boolean getNoAccessToProcedureBodies() {
/* 1469 */     return this.mc.getNoAccessToProcedureBodies();
/*      */   }
/*      */ 
/*      */   public boolean getNoDatetimeStringSync() {
/* 1473 */     return this.mc.getNoDatetimeStringSync();
/*      */   }
/*      */ 
/*      */   public boolean getNoTimezoneConversionForTimeType() {
/* 1477 */     return this.mc.getNoTimezoneConversionForTimeType();
/*      */   }
/*      */ 
/*      */   public boolean getNullCatalogMeansCurrent() {
/* 1481 */     return this.mc.getNullCatalogMeansCurrent();
/*      */   }
/*      */ 
/*      */   public boolean getNullNamePatternMatchesAll() {
/* 1485 */     return this.mc.getNullNamePatternMatchesAll();
/*      */   }
/*      */ 
/*      */   public boolean getOverrideSupportsIntegrityEnhancementFacility() {
/* 1489 */     return this.mc.getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */ 
/*      */   public int getPacketDebugBufferSize() {
/* 1493 */     return this.mc.getPacketDebugBufferSize();
/*      */   }
/*      */ 
/*      */   public boolean getPadCharsWithSpace() {
/* 1497 */     return this.mc.getPadCharsWithSpace();
/*      */   }
/*      */ 
/*      */   public boolean getParanoid() {
/* 1501 */     return this.mc.getParanoid();
/*      */   }
/*      */ 
/*      */   public boolean getPedantic() {
/* 1505 */     return this.mc.getPedantic();
/*      */   }
/*      */ 
/*      */   public boolean getPinGlobalTxToPhysicalConnection() {
/* 1509 */     return this.mc.getPinGlobalTxToPhysicalConnection();
/*      */   }
/*      */ 
/*      */   public boolean getPopulateInsertRowWithDefaultValues() {
/* 1513 */     return this.mc.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSize() {
/* 1517 */     return this.mc.getPrepStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSqlLimit() {
/* 1521 */     return this.mc.getPrepStmtCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSize() {
/* 1525 */     return this.mc.getPreparedStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSqlLimit() {
/* 1529 */     return this.mc.getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public boolean getProcessEscapeCodesForPrepStmts() {
/* 1533 */     return this.mc.getProcessEscapeCodesForPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSQL() {
/* 1537 */     return this.mc.getProfileSQL();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSql() {
/* 1541 */     return this.mc.getProfileSql();
/*      */   }
/*      */ 
/*      */   public String getPropertiesTransform() {
/* 1545 */     return this.mc.getPropertiesTransform();
/*      */   }
/*      */ 
/*      */   public int getQueriesBeforeRetryMaster() {
/* 1549 */     return this.mc.getQueriesBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public boolean getReconnectAtTxEnd() {
/* 1553 */     return this.mc.getReconnectAtTxEnd();
/*      */   }
/*      */ 
/*      */   public boolean getRelaxAutoCommit() {
/* 1557 */     return this.mc.getRelaxAutoCommit();
/*      */   }
/*      */ 
/*      */   public int getReportMetricsIntervalMillis() {
/* 1561 */     return this.mc.getReportMetricsIntervalMillis();
/*      */   }
/*      */ 
/*      */   public boolean getRequireSSL() {
/* 1565 */     return this.mc.getRequireSSL();
/*      */   }
/*      */ 
/*      */   public String getResourceId() {
/* 1569 */     return this.mc.getResourceId();
/*      */   }
/*      */ 
/*      */   public int getResultSetSizeThreshold() {
/* 1573 */     return this.mc.getResultSetSizeThreshold();
/*      */   }
/*      */ 
/*      */   public boolean getRewriteBatchedStatements() {
/* 1577 */     return this.mc.getRewriteBatchedStatements();
/*      */   }
/*      */ 
/*      */   public boolean getRollbackOnPooledClose() {
/* 1581 */     return this.mc.getRollbackOnPooledClose();
/*      */   }
/*      */ 
/*      */   public boolean getRoundRobinLoadBalance() {
/* 1585 */     return this.mc.getRoundRobinLoadBalance();
/*      */   }
/*      */ 
/*      */   public boolean getRunningCTS13() {
/* 1589 */     return this.mc.getRunningCTS13();
/*      */   }
/*      */ 
/*      */   public int getSecondsBeforeRetryMaster() {
/* 1593 */     return this.mc.getSecondsBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public String getServerTimezone() {
/* 1597 */     return this.mc.getServerTimezone();
/*      */   }
/*      */ 
/*      */   public String getSessionVariables() {
/* 1601 */     return this.mc.getSessionVariables();
/*      */   }
/*      */ 
/*      */   public int getSlowQueryThresholdMillis() {
/* 1605 */     return this.mc.getSlowQueryThresholdMillis();
/*      */   }
/*      */ 
/*      */   public long getSlowQueryThresholdNanos() {
/* 1609 */     return this.mc.getSlowQueryThresholdNanos();
/*      */   }
/*      */ 
/*      */   public String getSocketFactory() {
/* 1613 */     return this.mc.getSocketFactory();
/*      */   }
/*      */ 
/*      */   public String getSocketFactoryClassName() {
/* 1617 */     return this.mc.getSocketFactoryClassName();
/*      */   }
/*      */ 
/*      */   public int getSocketTimeout() {
/* 1621 */     return this.mc.getSocketTimeout();
/*      */   }
/*      */ 
/*      */   public String getStatementInterceptors() {
/* 1625 */     return this.mc.getStatementInterceptors();
/*      */   }
/*      */ 
/*      */   public boolean getStrictFloatingPoint() {
/* 1629 */     return this.mc.getStrictFloatingPoint();
/*      */   }
/*      */ 
/*      */   public boolean getStrictUpdates() {
/* 1633 */     return this.mc.getStrictUpdates();
/*      */   }
/*      */ 
/*      */   public boolean getTcpKeepAlive() {
/* 1637 */     return this.mc.getTcpKeepAlive();
/*      */   }
/*      */ 
/*      */   public boolean getTcpNoDelay() {
/* 1641 */     return this.mc.getTcpNoDelay();
/*      */   }
/*      */ 
/*      */   public int getTcpRcvBuf() {
/* 1645 */     return this.mc.getTcpRcvBuf();
/*      */   }
/*      */ 
/*      */   public int getTcpSndBuf() {
/* 1649 */     return this.mc.getTcpSndBuf();
/*      */   }
/*      */ 
/*      */   public int getTcpTrafficClass() {
/* 1653 */     return this.mc.getTcpTrafficClass();
/*      */   }
/*      */ 
/*      */   public boolean getTinyInt1isBit() {
/* 1657 */     return this.mc.getTinyInt1isBit();
/*      */   }
/*      */ 
/*      */   public boolean getTraceProtocol() {
/* 1661 */     return this.mc.getTraceProtocol();
/*      */   }
/*      */ 
/*      */   public boolean getTransformedBitIsBoolean() {
/* 1665 */     return this.mc.getTransformedBitIsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTreatUtilDateAsTimestamp() {
/* 1669 */     return this.mc.getTreatUtilDateAsTimestamp();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStorePassword() {
/* 1673 */     return this.mc.getTrustCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreType() {
/* 1677 */     return this.mc.getTrustCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreUrl() {
/* 1681 */     return this.mc.getTrustCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public boolean getUltraDevHack() {
/* 1685 */     return this.mc.getUltraDevHack();
/*      */   }
/*      */ 
/*      */   public boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 1689 */     return this.mc.getUseBlobToStoreUTF8OutsideBMP();
/*      */   }
/*      */ 
/*      */   public boolean getUseCompression() {
/* 1693 */     return this.mc.getUseCompression();
/*      */   }
/*      */ 
/*      */   public String getUseConfigs() {
/* 1697 */     return this.mc.getUseConfigs();
/*      */   }
/*      */ 
/*      */   public boolean getUseCursorFetch() {
/* 1701 */     return this.mc.getUseCursorFetch();
/*      */   }
/*      */ 
/*      */   public boolean getUseDirectRowUnpack() {
/* 1705 */     return this.mc.getUseDirectRowUnpack();
/*      */   }
/*      */ 
/*      */   public boolean getUseDynamicCharsetInfo() {
/* 1709 */     return this.mc.getUseDynamicCharsetInfo();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastDateParsing() {
/* 1713 */     return this.mc.getUseFastDateParsing();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastIntParsing() {
/* 1717 */     return this.mc.getUseFastIntParsing();
/*      */   }
/*      */ 
/*      */   public boolean getUseGmtMillisForDatetimes() {
/* 1721 */     return this.mc.getUseGmtMillisForDatetimes();
/*      */   }
/*      */ 
/*      */   public boolean getUseHostsInPrivileges() {
/* 1725 */     return this.mc.getUseHostsInPrivileges();
/*      */   }
/*      */ 
/*      */   public boolean getUseInformationSchema() {
/* 1729 */     return this.mc.getUseInformationSchema();
/*      */   }
/*      */ 
/*      */   public boolean getUseJDBCCompliantTimezoneShift() {
/* 1733 */     return this.mc.getUseJDBCCompliantTimezoneShift();
/*      */   }
/*      */ 
/*      */   public boolean getUseJvmCharsetConverters() {
/* 1737 */     return this.mc.getUseJvmCharsetConverters();
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalSessionState() {
/* 1741 */     return this.mc.getUseLocalSessionState();
/*      */   }
/*      */ 
/*      */   public boolean getUseNanosForElapsedTime() {
/* 1745 */     return this.mc.getUseNanosForElapsedTime();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldAliasMetadataBehavior() {
/* 1749 */     return this.mc.getUseOldAliasMetadataBehavior();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldUTF8Behavior() {
/* 1753 */     return this.mc.getUseOldUTF8Behavior();
/*      */   }
/*      */ 
/*      */   public boolean getUseOnlyServerErrorMessages() {
/* 1757 */     return this.mc.getUseOnlyServerErrorMessages();
/*      */   }
/*      */ 
/*      */   public boolean getUseReadAheadInput() {
/* 1761 */     return this.mc.getUseReadAheadInput();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSL() {
/* 1765 */     return this.mc.getUseSSL();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSPSCompatibleTimezoneShift() {
/* 1769 */     return this.mc.getUseSSPSCompatibleTimezoneShift();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPrepStmts() {
/* 1773 */     return this.mc.getUseServerPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPreparedStmts() {
/* 1777 */     return this.mc.getUseServerPreparedStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseSqlStateCodes() {
/* 1781 */     return this.mc.getUseSqlStateCodes();
/*      */   }
/*      */ 
/*      */   public boolean getUseStreamLengthsInPrepStmts() {
/* 1785 */     return this.mc.getUseStreamLengthsInPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseTimezone() {
/* 1789 */     return this.mc.getUseTimezone();
/*      */   }
/*      */ 
/*      */   public boolean getUseUltraDevWorkAround() {
/* 1793 */     return this.mc.getUseUltraDevWorkAround();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnbufferedInput() {
/* 1797 */     return this.mc.getUseUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnicode() {
/* 1801 */     return this.mc.getUseUnicode();
/*      */   }
/*      */ 
/*      */   public boolean getUseUsageAdvisor() {
/* 1805 */     return this.mc.getUseUsageAdvisor();
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 1809 */     return this.mc.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 1813 */     return this.mc.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public boolean getYearIsDateType() {
/* 1817 */     return this.mc.getYearIsDateType();
/*      */   }
/*      */ 
/*      */   public String getZeroDateTimeBehavior() {
/* 1821 */     return this.mc.getZeroDateTimeBehavior();
/*      */   }
/*      */ 
/*      */   public void setAllowLoadLocalInfile(boolean property) {
/* 1825 */     this.mc.setAllowLoadLocalInfile(property);
/*      */   }
/*      */ 
/*      */   public void setAllowMultiQueries(boolean property) {
/* 1829 */     this.mc.setAllowMultiQueries(property);
/*      */   }
/*      */ 
/*      */   public void setAllowNanAndInf(boolean flag) {
/* 1833 */     this.mc.setAllowNanAndInf(flag);
/*      */   }
/*      */ 
/*      */   public void setAllowUrlInLocalInfile(boolean flag) {
/* 1837 */     this.mc.setAllowUrlInLocalInfile(flag);
/*      */   }
/*      */ 
/*      */   public void setAlwaysSendSetIsolation(boolean flag) {
/* 1841 */     this.mc.setAlwaysSendSetIsolation(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoClosePStmtStreams(boolean flag) {
/* 1845 */     this.mc.setAutoClosePStmtStreams(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoDeserialize(boolean flag) {
/* 1849 */     this.mc.setAutoDeserialize(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoGenerateTestcaseScript(boolean flag) {
/* 1853 */     this.mc.setAutoGenerateTestcaseScript(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnect(boolean flag) {
/* 1857 */     this.mc.setAutoReconnect(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForConnectionPools(boolean property) {
/* 1861 */     this.mc.setAutoReconnectForConnectionPools(property);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForPools(boolean flag) {
/* 1865 */     this.mc.setAutoReconnectForPools(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoSlowLog(boolean flag) {
/* 1869 */     this.mc.setAutoSlowLog(flag);
/*      */   }
/*      */ 
/*      */   public void setBlobSendChunkSize(String value) throws SQLException {
/* 1873 */     this.mc.setBlobSendChunkSize(value);
/*      */   }
/*      */ 
/*      */   public void setBlobsAreStrings(boolean flag) {
/* 1877 */     this.mc.setBlobsAreStrings(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStatements(boolean flag) {
/* 1881 */     this.mc.setCacheCallableStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStmts(boolean flag) {
/* 1885 */     this.mc.setCacheCallableStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePrepStmts(boolean flag) {
/* 1889 */     this.mc.setCachePrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePreparedStatements(boolean flag) {
/* 1893 */     this.mc.setCachePreparedStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheResultSetMetadata(boolean property) {
/* 1897 */     this.mc.setCacheResultSetMetadata(property);
/*      */   }
/*      */ 
/*      */   public void setCacheServerConfiguration(boolean flag) {
/* 1901 */     this.mc.setCacheServerConfiguration(flag);
/*      */   }
/*      */ 
/*      */   public void setCallableStatementCacheSize(int size) {
/* 1905 */     this.mc.setCallableStatementCacheSize(size);
/*      */   }
/*      */ 
/*      */   public void setCallableStmtCacheSize(int cacheSize) {
/* 1909 */     this.mc.setCallableStmtCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeDBMDTypes(boolean property) {
/* 1913 */     this.mc.setCapitalizeDBMDTypes(property);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeTypeNames(boolean flag) {
/* 1917 */     this.mc.setCapitalizeTypeNames(flag);
/*      */   }
/*      */ 
/*      */   public void setCharacterEncoding(String encoding) {
/* 1921 */     this.mc.setCharacterEncoding(encoding);
/*      */   }
/*      */ 
/*      */   public void setCharacterSetResults(String characterSet) {
/* 1925 */     this.mc.setCharacterSetResults(characterSet);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStorePassword(String value) {
/* 1929 */     this.mc.setClientCertificateKeyStorePassword(value);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreType(String value) {
/* 1933 */     this.mc.setClientCertificateKeyStoreType(value);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreUrl(String value) {
/* 1937 */     this.mc.setClientCertificateKeyStoreUrl(value);
/*      */   }
/*      */ 
/*      */   public void setClientInfoProvider(String classname) {
/* 1941 */     this.mc.setClientInfoProvider(classname);
/*      */   }
/*      */ 
/*      */   public void setClobCharacterEncoding(String encoding) {
/* 1945 */     this.mc.setClobCharacterEncoding(encoding);
/*      */   }
/*      */ 
/*      */   public void setClobberStreamingResults(boolean flag) {
/* 1949 */     this.mc.setClobberStreamingResults(flag);
/*      */   }
/*      */ 
/*      */   public void setConnectTimeout(int timeoutMs) {
/* 1953 */     this.mc.setConnectTimeout(timeoutMs);
/*      */   }
/*      */ 
/*      */   public void setConnectionCollation(String collation) {
/* 1957 */     this.mc.setConnectionCollation(collation);
/*      */   }
/*      */ 
/*      */   public void setConnectionLifecycleInterceptors(String interceptors) {
/* 1961 */     this.mc.setConnectionLifecycleInterceptors(interceptors);
/*      */   }
/*      */ 
/*      */   public void setContinueBatchOnError(boolean property) {
/* 1965 */     this.mc.setContinueBatchOnError(property);
/*      */   }
/*      */ 
/*      */   public void setCreateDatabaseIfNotExist(boolean flag) {
/* 1969 */     this.mc.setCreateDatabaseIfNotExist(flag);
/*      */   }
/*      */ 
/*      */   public void setDefaultFetchSize(int n) {
/* 1973 */     this.mc.setDefaultFetchSize(n);
/*      */   }
/*      */ 
/*      */   public void setDetectServerPreparedStmts(boolean property) {
/* 1977 */     this.mc.setDetectServerPreparedStmts(property);
/*      */   }
/*      */ 
/*      */   public void setDontTrackOpenResources(boolean flag) {
/* 1981 */     this.mc.setDontTrackOpenResources(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag) {
/* 1985 */     this.mc.setDumpMetadataOnColumnNotFound(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpQueriesOnException(boolean flag) {
/* 1989 */     this.mc.setDumpQueriesOnException(flag);
/*      */   }
/*      */ 
/*      */   public void setDynamicCalendars(boolean flag) {
/* 1993 */     this.mc.setDynamicCalendars(flag);
/*      */   }
/*      */ 
/*      */   public void setElideSetAutoCommits(boolean flag) {
/* 1997 */     this.mc.setElideSetAutoCommits(flag);
/*      */   }
/*      */ 
/*      */   public void setEmptyStringsConvertToZero(boolean flag) {
/* 2001 */     this.mc.setEmptyStringsConvertToZero(flag);
/*      */   }
/*      */ 
/*      */   public void setEmulateLocators(boolean property) {
/* 2005 */     this.mc.setEmulateLocators(property);
/*      */   }
/*      */ 
/*      */   public void setEmulateUnsupportedPstmts(boolean flag) {
/* 2009 */     this.mc.setEmulateUnsupportedPstmts(flag);
/*      */   }
/*      */ 
/*      */   public void setEnablePacketDebug(boolean flag) {
/* 2013 */     this.mc.setEnablePacketDebug(flag);
/*      */   }
/*      */ 
/*      */   public void setEnableQueryTimeouts(boolean flag) {
/* 2017 */     this.mc.setEnableQueryTimeouts(flag);
/*      */   }
/*      */ 
/*      */   public void setEncoding(String property) {
/* 2021 */     this.mc.setEncoding(property);
/*      */   }
/*      */ 
/*      */   public void setExplainSlowQueries(boolean flag) {
/* 2025 */     this.mc.setExplainSlowQueries(flag);
/*      */   }
/*      */ 
/*      */   public void setFailOverReadOnly(boolean flag) {
/* 2029 */     this.mc.setFailOverReadOnly(flag);
/*      */   }
/*      */ 
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag) {
/* 2033 */     this.mc.setFunctionsNeverReturnBlobs(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerfMetrics(boolean flag) {
/* 2037 */     this.mc.setGatherPerfMetrics(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerformanceMetrics(boolean flag) {
/* 2041 */     this.mc.setGatherPerformanceMetrics(flag);
/*      */   }
/*      */ 
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag) {
/* 2045 */     this.mc.setGenerateSimpleParameterMetadata(flag);
/*      */   }
/*      */ 
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag) {
/* 2049 */     this.mc.setHoldResultsOpenOverStatementClose(flag);
/*      */   }
/*      */ 
/*      */   public void setIgnoreNonTxTables(boolean property) {
/* 2053 */     this.mc.setIgnoreNonTxTables(property);
/*      */   }
/*      */ 
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
/* 2057 */     this.mc.setIncludeInnodbStatusInDeadlockExceptions(flag);
/*      */   }
/*      */ 
/*      */   public void setInitialTimeout(int property) {
/* 2061 */     this.mc.setInitialTimeout(property);
/*      */   }
/*      */ 
/*      */   public void setInteractiveClient(boolean property) {
/* 2065 */     this.mc.setInteractiveClient(property);
/*      */   }
/*      */ 
/*      */   public void setIsInteractiveClient(boolean property) {
/* 2069 */     this.mc.setIsInteractiveClient(property);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncation(boolean flag) {
/* 2073 */     this.mc.setJdbcCompliantTruncation(flag);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/* 2078 */     this.mc.setJdbcCompliantTruncationForReads(jdbcCompliantTruncationForReads);
/*      */   }
/*      */ 
/*      */   public void setLargeRowSizeThreshold(String value)
/*      */   {
/* 2083 */     this.mc.setLargeRowSizeThreshold(value);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceStrategy(String strategy) {
/* 2087 */     this.mc.setLoadBalanceStrategy(strategy);
/*      */   }
/*      */ 
/*      */   public void setLocalSocketAddress(String address) {
/* 2091 */     this.mc.setLocalSocketAddress(address);
/*      */   }
/*      */ 
/*      */   public void setLocatorFetchBufferSize(String value) throws SQLException {
/* 2095 */     this.mc.setLocatorFetchBufferSize(value);
/*      */   }
/*      */ 
/*      */   public void setLogSlowQueries(boolean flag) {
/* 2099 */     this.mc.setLogSlowQueries(flag);
/*      */   }
/*      */ 
/*      */   public void setLogXaCommands(boolean flag) {
/* 2103 */     this.mc.setLogXaCommands(flag);
/*      */   }
/*      */ 
/*      */   public void setLogger(String property) {
/* 2107 */     this.mc.setLogger(property);
/*      */   }
/*      */ 
/*      */   public void setLoggerClassName(String className) {
/* 2111 */     this.mc.setLoggerClassName(className);
/*      */   }
/*      */ 
/*      */   public void setMaintainTimeStats(boolean flag) {
/* 2115 */     this.mc.setMaintainTimeStats(flag);
/*      */   }
/*      */ 
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes) {
/* 2119 */     this.mc.setMaxQuerySizeToLog(sizeInBytes);
/*      */   }
/*      */ 
/*      */   public void setMaxReconnects(int property) {
/* 2123 */     this.mc.setMaxReconnects(property);
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int property) {
/* 2127 */     this.mc.setMaxRows(property);
/*      */   }
/*      */ 
/*      */   public void setMetadataCacheSize(int value) {
/* 2131 */     this.mc.setMetadataCacheSize(value);
/*      */   }
/*      */ 
/*      */   public void setNetTimeoutForStreamingResults(int value) {
/* 2135 */     this.mc.setNetTimeoutForStreamingResults(value);
/*      */   }
/*      */ 
/*      */   public void setNoAccessToProcedureBodies(boolean flag) {
/* 2139 */     this.mc.setNoAccessToProcedureBodies(flag);
/*      */   }
/*      */ 
/*      */   public void setNoDatetimeStringSync(boolean flag) {
/* 2143 */     this.mc.setNoDatetimeStringSync(flag);
/*      */   }
/*      */ 
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag) {
/* 2147 */     this.mc.setNoTimezoneConversionForTimeType(flag);
/*      */   }
/*      */ 
/*      */   public void setNullCatalogMeansCurrent(boolean value) {
/* 2151 */     this.mc.setNullCatalogMeansCurrent(value);
/*      */   }
/*      */ 
/*      */   public void setNullNamePatternMatchesAll(boolean value) {
/* 2155 */     this.mc.setNullNamePatternMatchesAll(value);
/*      */   }
/*      */ 
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
/* 2159 */     this.mc.setOverrideSupportsIntegrityEnhancementFacility(flag);
/*      */   }
/*      */ 
/*      */   public void setPacketDebugBufferSize(int size) {
/* 2163 */     this.mc.setPacketDebugBufferSize(size);
/*      */   }
/*      */ 
/*      */   public void setPadCharsWithSpace(boolean flag) {
/* 2167 */     this.mc.setPadCharsWithSpace(flag);
/*      */   }
/*      */ 
/*      */   public void setParanoid(boolean property) {
/* 2171 */     this.mc.setParanoid(property);
/*      */   }
/*      */ 
/*      */   public void setPedantic(boolean property) {
/* 2175 */     this.mc.setPedantic(property);
/*      */   }
/*      */ 
/*      */   public void setPinGlobalTxToPhysicalConnection(boolean flag) {
/* 2179 */     this.mc.setPinGlobalTxToPhysicalConnection(flag);
/*      */   }
/*      */ 
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag) {
/* 2183 */     this.mc.setPopulateInsertRowWithDefaultValues(flag);
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSize(int cacheSize) {
/* 2187 */     this.mc.setPrepStmtCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit) {
/* 2191 */     this.mc.setPrepStmtCacheSqlLimit(sqlLimit);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSize(int cacheSize) {
/* 2195 */     this.mc.setPreparedStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) {
/* 2199 */     this.mc.setPreparedStatementCacheSqlLimit(cacheSqlLimit);
/*      */   }
/*      */ 
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag) {
/* 2203 */     this.mc.setProcessEscapeCodesForPrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setProfileSQL(boolean flag) {
/* 2207 */     this.mc.setProfileSQL(flag);
/*      */   }
/*      */ 
/*      */   public void setProfileSql(boolean property) {
/* 2211 */     this.mc.setProfileSql(property);
/*      */   }
/*      */ 
/*      */   public void setPropertiesTransform(String value) {
/* 2215 */     this.mc.setPropertiesTransform(value);
/*      */   }
/*      */ 
/*      */   public void setQueriesBeforeRetryMaster(int property) {
/* 2219 */     this.mc.setQueriesBeforeRetryMaster(property);
/*      */   }
/*      */ 
/*      */   public void setReconnectAtTxEnd(boolean property) {
/* 2223 */     this.mc.setReconnectAtTxEnd(property);
/*      */   }
/*      */ 
/*      */   public void setRelaxAutoCommit(boolean property) {
/* 2227 */     this.mc.setRelaxAutoCommit(property);
/*      */   }
/*      */ 
/*      */   public void setReportMetricsIntervalMillis(int millis) {
/* 2231 */     this.mc.setReportMetricsIntervalMillis(millis);
/*      */   }
/*      */ 
/*      */   public void setRequireSSL(boolean property) {
/* 2235 */     this.mc.setRequireSSL(property);
/*      */   }
/*      */ 
/*      */   public void setResourceId(String resourceId) {
/* 2239 */     this.mc.setResourceId(resourceId);
/*      */   }
/*      */ 
/*      */   public void setResultSetSizeThreshold(int threshold) {
/* 2243 */     this.mc.setResultSetSizeThreshold(threshold);
/*      */   }
/*      */ 
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag) {
/* 2247 */     this.mc.setRetainStatementAfterResultSetClose(flag);
/*      */   }
/*      */ 
/*      */   public void setRewriteBatchedStatements(boolean flag) {
/* 2251 */     this.mc.setRewriteBatchedStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setRollbackOnPooledClose(boolean flag) {
/* 2255 */     this.mc.setRollbackOnPooledClose(flag);
/*      */   }
/*      */ 
/*      */   public void setRoundRobinLoadBalance(boolean flag) {
/* 2259 */     this.mc.setRoundRobinLoadBalance(flag);
/*      */   }
/*      */ 
/*      */   public void setRunningCTS13(boolean flag) {
/* 2263 */     this.mc.setRunningCTS13(flag);
/*      */   }
/*      */ 
/*      */   public void setSecondsBeforeRetryMaster(int property) {
/* 2267 */     this.mc.setSecondsBeforeRetryMaster(property);
/*      */   }
/*      */ 
/*      */   public void setServerTimezone(String property) {
/* 2271 */     this.mc.setServerTimezone(property);
/*      */   }
/*      */ 
/*      */   public void setSessionVariables(String variables) {
/* 2275 */     this.mc.setSessionVariables(variables);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdMillis(int millis) {
/* 2279 */     this.mc.setSlowQueryThresholdMillis(millis);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdNanos(long nanos) {
/* 2283 */     this.mc.setSlowQueryThresholdNanos(nanos);
/*      */   }
/*      */ 
/*      */   public void setSocketFactory(String name) {
/* 2287 */     this.mc.setSocketFactory(name);
/*      */   }
/*      */ 
/*      */   public void setSocketFactoryClassName(String property) {
/* 2291 */     this.mc.setSocketFactoryClassName(property);
/*      */   }
/*      */ 
/*      */   public void setSocketTimeout(int property) {
/* 2295 */     this.mc.setSocketTimeout(property);
/*      */   }
/*      */ 
/*      */   public void setStatementInterceptors(String value) {
/* 2299 */     this.mc.setStatementInterceptors(value);
/*      */   }
/*      */ 
/*      */   public void setStrictFloatingPoint(boolean property) {
/* 2303 */     this.mc.setStrictFloatingPoint(property);
/*      */   }
/*      */ 
/*      */   public void setStrictUpdates(boolean property) {
/* 2307 */     this.mc.setStrictUpdates(property);
/*      */   }
/*      */ 
/*      */   public void setTcpKeepAlive(boolean flag) {
/* 2311 */     this.mc.setTcpKeepAlive(flag);
/*      */   }
/*      */ 
/*      */   public void setTcpNoDelay(boolean flag) {
/* 2315 */     this.mc.setTcpNoDelay(flag);
/*      */   }
/*      */ 
/*      */   public void setTcpRcvBuf(int bufSize) {
/* 2319 */     this.mc.setTcpRcvBuf(bufSize);
/*      */   }
/*      */ 
/*      */   public void setTcpSndBuf(int bufSize) {
/* 2323 */     this.mc.setTcpSndBuf(bufSize);
/*      */   }
/*      */ 
/*      */   public void setTcpTrafficClass(int classFlags) {
/* 2327 */     this.mc.setTcpTrafficClass(classFlags);
/*      */   }
/*      */ 
/*      */   public void setTinyInt1isBit(boolean flag) {
/* 2331 */     this.mc.setTinyInt1isBit(flag);
/*      */   }
/*      */ 
/*      */   public void setTraceProtocol(boolean flag) {
/* 2335 */     this.mc.setTraceProtocol(flag);
/*      */   }
/*      */ 
/*      */   public void setTransformedBitIsBoolean(boolean flag) {
/* 2339 */     this.mc.setTransformedBitIsBoolean(flag);
/*      */   }
/*      */ 
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag) {
/* 2343 */     this.mc.setTreatUtilDateAsTimestamp(flag);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStorePassword(String value) {
/* 2347 */     this.mc.setTrustCertificateKeyStorePassword(value);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreType(String value) {
/* 2351 */     this.mc.setTrustCertificateKeyStoreType(value);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreUrl(String value) {
/* 2355 */     this.mc.setTrustCertificateKeyStoreUrl(value);
/*      */   }
/*      */ 
/*      */   public void setUltraDevHack(boolean flag) {
/* 2359 */     this.mc.setUltraDevHack(flag);
/*      */   }
/*      */ 
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
/* 2363 */     this.mc.setUseBlobToStoreUTF8OutsideBMP(flag);
/*      */   }
/*      */ 
/*      */   public void setUseCompression(boolean property) {
/* 2367 */     this.mc.setUseCompression(property);
/*      */   }
/*      */ 
/*      */   public void setUseConfigs(String configs) {
/* 2371 */     this.mc.setUseConfigs(configs);
/*      */   }
/*      */ 
/*      */   public void setUseCursorFetch(boolean flag) {
/* 2375 */     this.mc.setUseCursorFetch(flag);
/*      */   }
/*      */ 
/*      */   public void setUseDirectRowUnpack(boolean flag) {
/* 2379 */     this.mc.setUseDirectRowUnpack(flag);
/*      */   }
/*      */ 
/*      */   public void setUseDynamicCharsetInfo(boolean flag) {
/* 2383 */     this.mc.setUseDynamicCharsetInfo(flag);
/*      */   }
/*      */ 
/*      */   public void setUseFastDateParsing(boolean flag) {
/* 2387 */     this.mc.setUseFastDateParsing(flag);
/*      */   }
/*      */ 
/*      */   public void setUseFastIntParsing(boolean flag) {
/* 2391 */     this.mc.setUseFastIntParsing(flag);
/*      */   }
/*      */ 
/*      */   public void setUseGmtMillisForDatetimes(boolean flag) {
/* 2395 */     this.mc.setUseGmtMillisForDatetimes(flag);
/*      */   }
/*      */ 
/*      */   public void setUseHostsInPrivileges(boolean property) {
/* 2399 */     this.mc.setUseHostsInPrivileges(property);
/*      */   }
/*      */ 
/*      */   public void setUseInformationSchema(boolean flag) {
/* 2403 */     this.mc.setUseInformationSchema(flag);
/*      */   }
/*      */ 
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag) {
/* 2407 */     this.mc.setUseJDBCCompliantTimezoneShift(flag);
/*      */   }
/*      */ 
/*      */   public void setUseJvmCharsetConverters(boolean flag) {
/* 2411 */     this.mc.setUseJvmCharsetConverters(flag);
/*      */   }
/*      */ 
/*      */   public void setUseLocalSessionState(boolean flag) {
/* 2415 */     this.mc.setUseLocalSessionState(flag);
/*      */   }
/*      */ 
/*      */   public void setUseNanosForElapsedTime(boolean flag) {
/* 2419 */     this.mc.setUseNanosForElapsedTime(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag) {
/* 2423 */     this.mc.setUseOldAliasMetadataBehavior(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOldUTF8Behavior(boolean flag) {
/* 2427 */     this.mc.setUseOldUTF8Behavior(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOnlyServerErrorMessages(boolean flag) {
/* 2431 */     this.mc.setUseOnlyServerErrorMessages(flag);
/*      */   }
/*      */ 
/*      */   public void setUseReadAheadInput(boolean flag) {
/* 2435 */     this.mc.setUseReadAheadInput(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSSL(boolean property) {
/* 2439 */     this.mc.setUseSSL(property);
/*      */   }
/*      */ 
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
/* 2443 */     this.mc.setUseSSPSCompatibleTimezoneShift(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPrepStmts(boolean flag) {
/* 2447 */     this.mc.setUseServerPrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPreparedStmts(boolean flag) {
/* 2451 */     this.mc.setUseServerPreparedStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSqlStateCodes(boolean flag) {
/* 2455 */     this.mc.setUseSqlStateCodes(flag);
/*      */   }
/*      */ 
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property) {
/* 2459 */     this.mc.setUseStreamLengthsInPrepStmts(property);
/*      */   }
/*      */ 
/*      */   public void setUseTimezone(boolean property) {
/* 2463 */     this.mc.setUseTimezone(property);
/*      */   }
/*      */ 
/*      */   public void setUseUltraDevWorkAround(boolean property) {
/* 2467 */     this.mc.setUseUltraDevWorkAround(property);
/*      */   }
/*      */ 
/*      */   public void setUseUnbufferedInput(boolean flag) {
/* 2471 */     this.mc.setUseUnbufferedInput(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUnicode(boolean flag) {
/* 2475 */     this.mc.setUseUnicode(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
/* 2479 */     this.mc.setUseUsageAdvisor(useUsageAdvisorFlag);
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
/* 2483 */     this.mc.setUtf8OutsideBmpExcludedColumnNamePattern(regexPattern);
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
/* 2487 */     this.mc.setUtf8OutsideBmpIncludedColumnNamePattern(regexPattern);
/*      */   }
/*      */ 
/*      */   public void setYearIsDateType(boolean flag) {
/* 2491 */     this.mc.setYearIsDateType(flag);
/*      */   }
/*      */ 
/*      */   public void setZeroDateTimeBehavior(String behavior) {
/* 2495 */     this.mc.setZeroDateTimeBehavior(behavior);
/*      */   }
/*      */ 
/*      */   public boolean useUnbufferedInput() {
/* 2499 */     return this.mc.useUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public void initializeExtension(Extension ex) throws SQLException {
/* 2503 */     this.mc.initializeExtension(ex);
/*      */   }
/*      */ 
/*      */   public String getProfilerEventHandler() {
/* 2507 */     return this.mc.getProfilerEventHandler();
/*      */   }
/*      */ 
/*      */   public void setProfilerEventHandler(String handler) {
/* 2511 */     this.mc.setProfilerEventHandler(handler);
/*      */   }
/*      */ 
/*      */   public boolean getVerifyServerCertificate() {
/* 2515 */     return this.mc.getVerifyServerCertificate();
/*      */   }
/*      */ 
/*      */   public void setVerifyServerCertificate(boolean flag) {
/* 2519 */     this.mc.setVerifyServerCertificate(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseLegacyDatetimeCode() {
/* 2523 */     return this.mc.getUseLegacyDatetimeCode();
/*      */   }
/*      */ 
/*      */   public void setUseLegacyDatetimeCode(boolean flag) {
/* 2527 */     this.mc.setUseLegacyDatetimeCode(flag);
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingMaxOperations() {
/* 2531 */     return this.mc.getSelfDestructOnPingMaxOperations();
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingSecondsLifetime() {
/* 2535 */     return this.mc.getSelfDestructOnPingSecondsLifetime();
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations) {
/* 2539 */     this.mc.setSelfDestructOnPingMaxOperations(maxOperations);
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds) {
/* 2543 */     this.mc.setSelfDestructOnPingSecondsLifetime(seconds);
/*      */   }
/*      */ 
/*      */   public boolean getUseColumnNamesInFindColumn() {
/* 2547 */     return this.mc.getUseColumnNamesInFindColumn();
/*      */   }
/*      */ 
/*      */   public void setUseColumnNamesInFindColumn(boolean flag) {
/* 2551 */     this.mc.setUseColumnNamesInFindColumn(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalTransactionState() {
/* 2555 */     return this.mc.getUseLocalTransactionState();
/*      */   }
/*      */ 
/*      */   public void setUseLocalTransactionState(boolean flag) {
/* 2559 */     this.mc.setUseLocalTransactionState(flag);
/*      */   }
/*      */ 
/*      */   public boolean getCompensateOnDuplicateKeyUpdateCounts() {
/* 2563 */     return this.mc.getCompensateOnDuplicateKeyUpdateCounts();
/*      */   }
/*      */ 
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
/* 2567 */     this.mc.setCompensateOnDuplicateKeyUpdateCounts(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseAffectedRows() {
/* 2571 */     return this.mc.getUseAffectedRows();
/*      */   }
/*      */ 
/*      */   public void setUseAffectedRows(boolean flag) {
/* 2575 */     this.mc.setUseAffectedRows(flag);
/*      */   }
/*      */ 
/*      */   public String getPasswordCharacterEncoding() {
/* 2579 */     return this.mc.getPasswordCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public void setPasswordCharacterEncoding(String characterSet) {
/* 2583 */     this.mc.setPasswordCharacterEncoding(characterSet);
/*      */   }
/*      */ 
/*      */   public int getAutoIncrementIncrement() {
/* 2587 */     return this.mc.getAutoIncrementIncrement();
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceBlacklistTimeout() {
/* 2591 */     return this.mc.getLoadBalanceBlacklistTimeout();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) {
/* 2595 */     this.mc.setLoadBalanceBlacklistTimeout(loadBalanceBlacklistTimeout);
/*      */   }
/*      */ 
/*      */   public void setRetriesAllDown(int retriesAllDown) {
/* 2599 */     this.mc.setRetriesAllDown(retriesAllDown);
/*      */   }
/*      */ 
/*      */   public int getRetriesAllDown() {
/* 2603 */     return this.mc.getRetriesAllDown();
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/* 2607 */     return this.pooledConnection.getExceptionInterceptor();
/*      */   }
/*      */ 
/*      */   public String getExceptionInterceptors() {
/* 2611 */     return this.mc.getExceptionInterceptors();
/*      */   }
/*      */ 
/*      */   public void setExceptionInterceptors(String exceptionInterceptors) {
/* 2615 */     this.mc.setExceptionInterceptors(exceptionInterceptors);
/*      */   }
/*      */ 
/*      */   public boolean getQueryTimeoutKillsConnection() {
/* 2619 */     return this.mc.getQueryTimeoutKillsConnection();
/*      */   }
/*      */ 
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection)
/*      */   {
/* 2624 */     this.mc.setQueryTimeoutKillsConnection(queryTimeoutKillsConnection);
/*      */   }
/*      */ 
/*      */   public boolean hasSameProperties(Connection c) {
/* 2628 */     return this.mc.hasSameProperties(c);
/*      */   }
/*      */ 
/*      */   public Properties getProperties() {
/* 2632 */     return this.mc.getProperties();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   80 */     if (Util.isJdbc4())
/*      */       try {
/*   82 */         JDBC_4_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4ConnectionWrapper").getConstructor(new Class[] { MysqlPooledConnection.class, Connection.class, Boolean.TYPE });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*   88 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   90 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   92 */         throw new RuntimeException(e);
/*      */       }
/*      */     else
/*   95 */       JDBC_4_CONNECTION_WRAPPER_CTOR = null;
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.ConnectionWrapper
 * JD-Core Version:    0.6.0
 */