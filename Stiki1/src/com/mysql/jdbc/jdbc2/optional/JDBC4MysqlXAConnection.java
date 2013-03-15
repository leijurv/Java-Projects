/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionImpl;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.sql.StatementEvent;
/*    */ import javax.sql.StatementEventListener;
/*    */ 
/*    */ public class JDBC4MysqlXAConnection extends MysqlXAConnection
/*    */ {
/*    */   private Map<StatementEventListener, StatementEventListener> statementEventListeners;
/*    */ 
/*    */   public JDBC4MysqlXAConnection(ConnectionImpl connection, boolean logXaCommands)
/*    */     throws SQLException
/*    */   {
/* 45 */     super(connection, logXaCommands);
/*    */ 
/* 47 */     this.statementEventListeners = new HashMap();
/*    */   }
/*    */ 
/*    */   public synchronized void close() throws SQLException {
/* 51 */     super.close();
/*    */ 
/* 53 */     if (this.statementEventListeners != null) {
/* 54 */       this.statementEventListeners.clear();
/*    */ 
/* 56 */       this.statementEventListeners = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void addStatementEventListener(StatementEventListener listener)
/*    */   {
/* 73 */     synchronized (this.statementEventListeners) {
/* 74 */       this.statementEventListeners.put(listener, listener);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void removeStatementEventListener(StatementEventListener listener)
/*    */   {
/* 90 */     synchronized (this.statementEventListeners) {
/* 91 */       this.statementEventListeners.remove(listener);
/*    */     }
/*    */   }
/*    */ 
/*    */   void fireStatementEvent(StatementEvent event) throws SQLException {
/* 96 */     synchronized (this.statementEventListeners) {
/* 97 */       for (StatementEventListener listener : this.statementEventListeners.keySet())
/* 98 */         listener.statementClosed(event);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection
 * JD-Core Version:    0.6.0
 */