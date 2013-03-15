/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ExceptionInterceptor;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ import javax.sql.PooledConnection;
/*     */ 
/*     */ public class MysqlPooledConnection
/*     */   implements PooledConnection
/*     */ {
/*     */   private static final Constructor JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR;
/*     */   public static final int CONNECTION_ERROR_EVENT = 1;
/*     */   public static final int CONNECTION_CLOSED_EVENT = 2;
/*     */   private Map connectionEventListeners;
/*     */   private java.sql.Connection logicalHandle;
/*     */   private com.mysql.jdbc.Connection physicalConn;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   protected static MysqlPooledConnection getInstance(com.mysql.jdbc.Connection connection)
/*     */     throws SQLException
/*     */   {
/*  78 */     if (!Util.isJdbc4()) {
/*  79 */       return new MysqlPooledConnection(connection);
/*     */     }
/*     */ 
/*  82 */     return (MysqlPooledConnection)Util.handleNewInstance(JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR, new Object[] { connection }, connection.getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public MysqlPooledConnection(com.mysql.jdbc.Connection connection)
/*     */   {
/* 116 */     this.logicalHandle = null;
/* 117 */     this.physicalConn = connection;
/* 118 */     this.connectionEventListeners = new HashMap();
/* 119 */     this.exceptionInterceptor = this.physicalConn.getExceptionInterceptor();
/*     */   }
/*     */ 
/*     */   public synchronized void addConnectionEventListener(ConnectionEventListener connectioneventlistener)
/*     */   {
/* 132 */     if (this.connectionEventListeners != null)
/* 133 */       this.connectionEventListeners.put(connectioneventlistener, connectioneventlistener);
/*     */   }
/*     */ 
/*     */   public synchronized void removeConnectionEventListener(ConnectionEventListener connectioneventlistener)
/*     */   {
/* 148 */     if (this.connectionEventListeners != null)
/* 149 */       this.connectionEventListeners.remove(connectioneventlistener);
/*     */   }
/*     */ 
/*     */   public synchronized java.sql.Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 160 */     return getConnection(true, false);
/*     */   }
/*     */ 
/*     */   protected synchronized java.sql.Connection getConnection(boolean resetServerState, boolean forXa)
/*     */     throws SQLException
/*     */   {
/* 167 */     if (this.physicalConn == null)
/*     */     {
/* 169 */       SQLException sqlException = SQLError.createSQLException("Physical Connection doesn't exist", this.exceptionInterceptor);
/*     */ 
/* 171 */       callConnectionEventListeners(1, sqlException);
/*     */ 
/* 173 */       throw sqlException;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 178 */       if (this.logicalHandle != null) {
/* 179 */         ((ConnectionWrapper)this.logicalHandle).close(false);
/*     */       }
/*     */ 
/* 182 */       if (resetServerState) {
/* 183 */         this.physicalConn.resetServerState();
/*     */       }
/*     */ 
/* 186 */       this.logicalHandle = ConnectionWrapper.getInstance(this, this.physicalConn, forXa);
/*     */     }
/*     */     catch (SQLException sqlException)
/*     */     {
/* 190 */       callConnectionEventListeners(1, sqlException);
/*     */ 
/* 192 */       throw sqlException;
/*     */     }
/*     */ 
/* 195 */     return this.logicalHandle;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */     throws SQLException
/*     */   {
/* 206 */     if (this.physicalConn != null) {
/* 207 */       this.physicalConn.close();
/*     */ 
/* 209 */       this.physicalConn = null;
/*     */     }
/*     */ 
/* 212 */     if (this.connectionEventListeners != null) {
/* 213 */       this.connectionEventListeners.clear();
/*     */ 
/* 215 */       this.connectionEventListeners = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void callConnectionEventListeners(int eventType, SQLException sqlException)
/*     */   {
/* 234 */     if (this.connectionEventListeners == null)
/*     */     {
/* 236 */       return;
/*     */     }
/*     */ 
/* 239 */     Iterator iterator = this.connectionEventListeners.entrySet().iterator();
/*     */ 
/* 241 */     ConnectionEvent connectionevent = new ConnectionEvent(this, sqlException);
/*     */ 
/* 244 */     while (iterator.hasNext())
/*     */     {
/* 246 */       ConnectionEventListener connectioneventlistener = (ConnectionEventListener)((Map.Entry)iterator.next()).getValue();
/*     */ 
/* 249 */       if (eventType == 2)
/* 250 */         connectioneventlistener.connectionClosed(connectionevent);
/* 251 */       else if (eventType == 1)
/* 252 */         connectioneventlistener.connectionErrorOccurred(connectionevent);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected ExceptionInterceptor getExceptionInterceptor()
/*     */   {
/* 259 */     return this.exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  59 */     if (Util.isJdbc4())
/*     */       try {
/*  61 */         JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection").getConstructor(new Class[] { com.mysql.jdbc.Connection.class });
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*  66 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  68 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  70 */         throw new RuntimeException(e);
/*     */       }
/*     */     else
/*  73 */       JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR = null;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection
 * JD-Core Version:    0.6.0
 */