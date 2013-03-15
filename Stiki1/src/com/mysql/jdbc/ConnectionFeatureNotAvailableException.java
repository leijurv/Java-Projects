/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ public class ConnectionFeatureNotAvailableException extends CommunicationsException
/*    */ {
/*    */   public ConnectionFeatureNotAvailableException(ConnectionImpl conn, long lastPacketSentTimeMs, Exception underlyingException)
/*    */   {
/* 50 */     super(conn, lastPacketSentTimeMs, 0L, underlyingException);
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 59 */     return "Feature not available in this distribution of Connector/J";
/*    */   }
/*    */ 
/*    */   public String getSQLState()
/*    */   {
/* 68 */     return "01S00";
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionFeatureNotAvailableException
 * JD-Core Version:    0.6.0
 */