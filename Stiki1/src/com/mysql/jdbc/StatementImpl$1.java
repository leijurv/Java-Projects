/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ class StatementImpl$1 extends Thread
/*    */ {
/*    */   private final StatementImpl.CancelTask this$1;
/*    */ 
/*    */   public void run()
/*    */   {
/* 93 */     if (StatementImpl.CancelTask.access$000(this.this$1).connection.getQueryTimeoutKillsConnection()) {
/*    */       try {
/* 95 */         this.this$1.toCancel.wasCancelled = true;
/* 96 */         this.this$1.toCancel.wasCancelledByTimeout = true;
/* 97 */         StatementImpl.CancelTask.access$000(this.this$1).connection.realClose(false, false, true, new MySQLStatementCancelledException(Messages.getString("Statement.ConnectionKilledDueToTimeout")));
/*    */       }
/*    */       catch (NullPointerException npe) {
/*    */       }
/*    */       catch (SQLException sqlEx) {
/* 102 */         this.this$1.caughtWhileCancelling = sqlEx;
/*    */       }
/*    */     } else {
/* 105 */       Connection cancelConn = null;
/* 106 */       Statement cancelStmt = null;
/*    */       try
/*    */       {
/* 109 */         synchronized (StatementImpl.CancelTask.access$000(this.this$1).cancelTimeoutMutex) {
/* 110 */           cancelConn = StatementImpl.CancelTask.access$000(this.this$1).connection.duplicate();
/* 111 */           cancelStmt = cancelConn.createStatement();
/* 112 */           cancelStmt.execute("KILL QUERY " + this.this$1.connectionId);
/* 113 */           this.this$1.toCancel.wasCancelled = true;
/* 114 */           this.this$1.toCancel.wasCancelledByTimeout = true;
/*    */         }
/*    */       } catch (SQLException sqlEx) {
/* 117 */         this.this$1.caughtWhileCancelling = sqlEx;
/*    */       }
/*    */       catch (NullPointerException npe)
/*    */       {
/*    */       }
/*    */       finally
/*    */       {
/* 126 */         if (cancelStmt != null) {
/*    */           try {
/* 128 */             cancelStmt.close();
/*    */           } catch (SQLException sqlEx) {
/* 130 */             throw new RuntimeException(sqlEx.toString());
/*    */           }
/*    */         }
/*    */ 
/* 134 */         if (cancelConn != null)
/*    */           try {
/* 136 */             cancelConn.close();
/*    */           } catch (SQLException sqlEx) {
/* 138 */             throw new RuntimeException(sqlEx.toString());
/*    */           }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.StatementImpl.1
 * JD-Core Version:    0.6.0
 */