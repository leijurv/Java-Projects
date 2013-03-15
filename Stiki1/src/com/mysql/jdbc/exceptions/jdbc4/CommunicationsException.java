/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionImpl;
/*    */ import com.mysql.jdbc.SQLError;
/*    */ import com.mysql.jdbc.StreamingNotifiable;
/*    */ import java.sql.SQLRecoverableException;
/*    */ 
/*    */ public class CommunicationsException extends SQLRecoverableException
/*    */   implements StreamingNotifiable
/*    */ {
/*    */   private String exceptionMessage;
/* 54 */   private boolean streamingResultSetInPlay = false;
/*    */ 
/*    */   public CommunicationsException(ConnectionImpl conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException)
/*    */   {
/* 60 */     this.exceptionMessage = SQLError.createLinkFailureMessageBasedOnHeuristics(conn, lastPacketSentTimeMs, lastPacketReceivedTimeMs, underlyingException, this.streamingResultSetInPlay);
/*    */ 
/* 63 */     if (underlyingException != null)
/* 64 */       initCause(underlyingException);
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 74 */     return this.exceptionMessage;
/*    */   }
/*    */ 
/*    */   public String getSQLState()
/*    */   {
/* 83 */     return "08S01";
/*    */   }
/*    */ 
/*    */   public void setWasStreamingResults() {
/* 87 */     this.streamingResultSetInPlay = true;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.CommunicationsException
 * JD-Core Version:    0.6.0
 */