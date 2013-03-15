/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class CommunicationsException extends SQLException
/*     */   implements StreamingNotifiable
/*     */ {
/*  47 */   private String exceptionMessage = null;
/*     */ 
/*  49 */   private boolean streamingResultSetInPlay = false;
/*     */   private ConnectionImpl conn;
/*     */   private long lastPacketSentTimeMs;
/*     */   private long lastPacketReceivedTimeMs;
/*     */   private Exception underlyingException;
/*     */ 
/*     */   public CommunicationsException(ConnectionImpl conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException)
/*     */   {
/*  60 */     this.conn = conn;
/*  61 */     this.lastPacketReceivedTimeMs = lastPacketReceivedTimeMs;
/*  62 */     this.lastPacketSentTimeMs = lastPacketSentTimeMs;
/*  63 */     this.underlyingException = underlyingException;
/*     */ 
/*  65 */     if (underlyingException != null)
/*  66 */       initCause(underlyingException);
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/*  80 */     if (this.exceptionMessage == null) {
/*  81 */       this.exceptionMessage = SQLError.createLinkFailureMessageBasedOnHeuristics(this.conn, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, this.underlyingException, this.streamingResultSetInPlay);
/*     */ 
/*  84 */       this.conn = null;
/*  85 */       this.underlyingException = null;
/*     */     }
/*  87 */     return this.exceptionMessage;
/*     */   }
/*     */ 
/*     */   public String getSQLState()
/*     */   {
/*  96 */     return "08S01";
/*     */   }
/*     */ 
/*     */   public void setWasStreamingResults()
/*     */   {
/* 103 */     this.streamingResultSetInPlay = true;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.CommunicationsException
 * JD-Core Version:    0.6.0
 */