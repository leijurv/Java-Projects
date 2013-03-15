/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ 
/*     */ public class StatementWrapper extends WrapperBase
/*     */   implements java.sql.Statement
/*     */ {
/*     */   private static final Constructor JDBC_4_STATEMENT_WRAPPER_CTOR;
/*     */   protected java.sql.Statement wrappedStmt;
/*     */   protected ConnectionWrapper wrappedConn;
/*     */ 
/*     */   protected static StatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.Statement toWrap)
/*     */     throws SQLException
/*     */   {
/*  75 */     if (!Util.isJdbc4()) {
/*  76 */       return new StatementWrapper(c, conn, toWrap);
/*     */     }
/*     */ 
/*  80 */     return (StatementWrapper)Util.handleNewInstance(JDBC_4_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap }, conn.getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.Statement toWrap)
/*     */   {
/*  92 */     super(conn);
/*  93 */     this.wrappedStmt = toWrap;
/*  94 */     this.wrappedConn = c;
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 104 */       if (this.wrappedStmt != null) {
/* 105 */         return this.wrappedConn;
/*     */       }
/*     */ 
/* 108 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 111 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCursorName(String name)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 127 */       if (this.wrappedStmt != null)
/* 128 */         this.wrappedStmt.setCursorName(name);
/*     */       else
/* 130 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 134 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setEscapeProcessing(boolean enable)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 145 */       if (this.wrappedStmt != null)
/* 146 */         this.wrappedStmt.setEscapeProcessing(enable);
/*     */       else
/* 148 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 152 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFetchDirection(int direction)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 163 */       if (this.wrappedStmt != null)
/* 164 */         this.wrappedStmt.setFetchDirection(direction);
/*     */       else
/* 166 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 170 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getFetchDirection()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 181 */       if (this.wrappedStmt != null) {
/* 182 */         return this.wrappedStmt.getFetchDirection();
/*     */       }
/*     */ 
/* 185 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 188 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 191 */     return 1000;
/*     */   }
/*     */ 
/*     */   public void setFetchSize(int rows)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 204 */       if (this.wrappedStmt != null)
/* 205 */         this.wrappedStmt.setFetchSize(rows);
/*     */       else
/* 207 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 211 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getFetchSize()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 222 */       if (this.wrappedStmt != null) {
/* 223 */         return this.wrappedStmt.getFetchSize();
/*     */       }
/*     */ 
/* 226 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 229 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 232 */     return 0;
/*     */   }
/*     */ 
/*     */   public ResultSet getGeneratedKeys()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 244 */       if (this.wrappedStmt != null) {
/* 245 */         return this.wrappedStmt.getGeneratedKeys();
/*     */       }
/*     */ 
/* 248 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 251 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 254 */     return null;
/*     */   }
/*     */ 
/*     */   public void setMaxFieldSize(int max)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 267 */       if (this.wrappedStmt != null)
/* 268 */         this.wrappedStmt.setMaxFieldSize(max);
/*     */       else
/* 270 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 274 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaxFieldSize()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 285 */       if (this.wrappedStmt != null) {
/* 286 */         return this.wrappedStmt.getMaxFieldSize();
/*     */       }
/*     */ 
/* 289 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 292 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 295 */     return 0;
/*     */   }
/*     */ 
/*     */   public void setMaxRows(int max)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 307 */       if (this.wrappedStmt != null)
/* 308 */         this.wrappedStmt.setMaxRows(max);
/*     */       else
/* 310 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 314 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaxRows()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 325 */       if (this.wrappedStmt != null) {
/* 326 */         return this.wrappedStmt.getMaxRows();
/*     */       }
/*     */ 
/* 329 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 332 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 335 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean getMoreResults()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 347 */       if (this.wrappedStmt != null) {
/* 348 */         return this.wrappedStmt.getMoreResults();
/*     */       }
/*     */ 
/* 351 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 354 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 357 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean getMoreResults(int current)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 367 */       if (this.wrappedStmt != null) {
/* 368 */         return this.wrappedStmt.getMoreResults(current);
/*     */       }
/*     */ 
/* 371 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 374 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 377 */     return false;
/*     */   }
/*     */ 
/*     */   public void setQueryTimeout(int seconds)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 387 */       if (this.wrappedStmt != null)
/* 388 */         this.wrappedStmt.setQueryTimeout(seconds);
/*     */       else
/* 390 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 394 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getQueryTimeout()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 405 */       if (this.wrappedStmt != null) {
/* 406 */         return this.wrappedStmt.getQueryTimeout();
/*     */       }
/*     */ 
/* 409 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 412 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 415 */     return 0;
/*     */   }
/*     */ 
/*     */   public ResultSet getResultSet()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 425 */       if (this.wrappedStmt != null) {
/* 426 */         ResultSet rs = this.wrappedStmt.getResultSet();
/*     */ 
/* 428 */         ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */ 
/* 430 */         return rs;
/*     */       }
/*     */ 
/* 433 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 436 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 439 */     return null;
/*     */   }
/*     */ 
/*     */   public int getResultSetConcurrency()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 449 */       if (this.wrappedStmt != null) {
/* 450 */         return this.wrappedStmt.getResultSetConcurrency();
/*     */       }
/*     */ 
/* 453 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 456 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 459 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getResultSetHoldability()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 469 */       if (this.wrappedStmt != null) {
/* 470 */         return this.wrappedStmt.getResultSetHoldability();
/*     */       }
/*     */ 
/* 473 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 476 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 479 */     return 1;
/*     */   }
/*     */ 
/*     */   public int getResultSetType()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 489 */       if (this.wrappedStmt != null) {
/* 490 */         return this.wrappedStmt.getResultSetType();
/*     */       }
/*     */ 
/* 493 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 496 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 499 */     return 1003;
/*     */   }
/*     */ 
/*     */   public int getUpdateCount()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 509 */       if (this.wrappedStmt != null) {
/* 510 */         return this.wrappedStmt.getUpdateCount();
/*     */       }
/*     */ 
/* 513 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 516 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 519 */     return -1;
/*     */   }
/*     */ 
/*     */   public SQLWarning getWarnings()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 529 */       if (this.wrappedStmt != null) {
/* 530 */         return this.wrappedStmt.getWarnings();
/*     */       }
/*     */ 
/* 533 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 536 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 539 */     return null;
/*     */   }
/*     */ 
/*     */   public void addBatch(String sql)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 549 */       if (this.wrappedStmt != null)
/* 550 */         this.wrappedStmt.addBatch(sql);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 553 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 564 */       if (this.wrappedStmt != null)
/* 565 */         this.wrappedStmt.cancel();
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 568 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearBatch()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 579 */       if (this.wrappedStmt != null)
/* 580 */         this.wrappedStmt.clearBatch();
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 583 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearWarnings()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 594 */       if (this.wrappedStmt != null)
/* 595 */         this.wrappedStmt.clearWarnings();
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 598 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 609 */       if (this.wrappedStmt != null)
/* 610 */         this.wrappedStmt.close();
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 613 */       checkAndFireConnectionError(sqlEx);
/*     */     } finally {
/* 615 */       this.wrappedStmt = null;
/* 616 */       this.pooledConnection = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean execute(String sql, int autoGeneratedKeys)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 628 */       if (this.wrappedStmt != null) {
/* 629 */         return this.wrappedStmt.execute(sql, autoGeneratedKeys);
/*     */       }
/*     */ 
/* 632 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 635 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 638 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean execute(String sql, int[] columnIndexes)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 651 */       if (this.wrappedStmt != null) {
/* 652 */         return this.wrappedStmt.execute(sql, columnIndexes);
/*     */       }
/*     */ 
/* 655 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 658 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 661 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean execute(String sql, String[] columnNames)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 675 */       if (this.wrappedStmt != null) {
/* 676 */         return this.wrappedStmt.execute(sql, columnNames);
/*     */       }
/*     */ 
/* 679 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 682 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 685 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean execute(String sql)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 698 */       if (this.wrappedStmt != null) {
/* 699 */         return this.wrappedStmt.execute(sql);
/*     */       }
/*     */ 
/* 702 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 705 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 708 */     return false;
/*     */   }
/*     */ 
/*     */   public int[] executeBatch()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 721 */       if (this.wrappedStmt != null) {
/* 722 */         return this.wrappedStmt.executeBatch();
/*     */       }
/*     */ 
/* 725 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 728 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 731 */     return null;
/*     */   }
/*     */ 
/*     */   public ResultSet executeQuery(String sql)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 744 */       if (this.wrappedStmt != null)
/*     */       {
/* 746 */         ResultSet rs = this.wrappedStmt.executeQuery(sql);
/* 747 */         ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */ 
/* 749 */         return rs;
/*     */       }
/*     */ 
/* 752 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 755 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 758 */     return null;
/*     */   }
/*     */ 
/*     */   public int executeUpdate(String sql, int autoGeneratedKeys)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 772 */       if (this.wrappedStmt != null) {
/* 773 */         return this.wrappedStmt.executeUpdate(sql, autoGeneratedKeys);
/*     */       }
/*     */ 
/* 776 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 779 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 782 */     return -1;
/*     */   }
/*     */ 
/*     */   public int executeUpdate(String sql, int[] columnIndexes)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 795 */       if (this.wrappedStmt != null) {
/* 796 */         return this.wrappedStmt.executeUpdate(sql, columnIndexes);
/*     */       }
/*     */ 
/* 799 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 802 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 805 */     return -1;
/*     */   }
/*     */ 
/*     */   public int executeUpdate(String sql, String[] columnNames)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 819 */       if (this.wrappedStmt != null) {
/* 820 */         return this.wrappedStmt.executeUpdate(sql, columnNames);
/*     */       }
/*     */ 
/* 823 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 826 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 829 */     return -1;
/*     */   }
/*     */ 
/*     */   public int executeUpdate(String sql)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 841 */       if (this.wrappedStmt != null) {
/* 842 */         return this.wrappedStmt.executeUpdate(sql);
/*     */       }
/*     */ 
/* 845 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 848 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */ 
/* 851 */     return -1;
/*     */   }
/*     */ 
/*     */   public void enableStreamingResults() throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 858 */       if (this.wrappedStmt != null) {
/* 859 */         ((com.mysql.jdbc.Statement)this.wrappedStmt).enableStreamingResults();
/*     */       }
/*     */       else {
/* 862 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 867 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  53 */     if (Util.isJdbc4())
/*     */       try {
/*  55 */         JDBC_4_STATEMENT_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4StatementWrapper").getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, java.sql.Statement.class });
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*  61 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  63 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  65 */         throw new RuntimeException(e);
/*     */       }
/*     */     else
/*  68 */       JDBC_4_STATEMENT_WRAPPER_CTOR = null;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.StatementWrapper
 * JD-Core Version:    0.6.0
 */