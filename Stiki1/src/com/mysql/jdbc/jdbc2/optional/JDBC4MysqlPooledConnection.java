/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.StatementEvent;
/*     */ import javax.sql.StatementEventListener;
/*     */ 
/*     */ public class JDBC4MysqlPooledConnection extends MysqlPooledConnection
/*     */ {
/*     */   private Map<StatementEventListener, StatementEventListener> statementEventListeners;
/*     */ 
/*     */   public JDBC4MysqlPooledConnection(Connection connection)
/*     */   {
/*  53 */     super(connection);
/*     */ 
/*  55 */     this.statementEventListeners = new HashMap();
/*     */   }
/*     */ 
/*     */   public synchronized void close() throws SQLException {
/*  59 */     super.close();
/*     */ 
/*  61 */     if (this.statementEventListeners != null) {
/*  62 */       this.statementEventListeners.clear();
/*     */ 
/*  64 */       this.statementEventListeners = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addStatementEventListener(StatementEventListener listener)
/*     */   {
/*  81 */     synchronized (this.statementEventListeners) {
/*  82 */       this.statementEventListeners.put(listener, listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeStatementEventListener(StatementEventListener listener)
/*     */   {
/*  98 */     synchronized (this.statementEventListeners) {
/*  99 */       this.statementEventListeners.remove(listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   void fireStatementEvent(StatementEvent event) throws SQLException {
/* 104 */     synchronized (this.statementEventListeners) {
/* 105 */       for (StatementEventListener listener : this.statementEventListeners.keySet())
/* 106 */         listener.statementClosed(event);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection
 * JD-Core Version:    0.6.0
 */