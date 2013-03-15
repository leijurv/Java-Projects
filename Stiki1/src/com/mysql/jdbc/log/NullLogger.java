/*    */ package com.mysql.jdbc.log;
/*    */ 
/*    */ public class NullLogger
/*    */   implements Log
/*    */ {
/*    */   public NullLogger(String instanceName)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean isDebugEnabled()
/*    */   {
/* 54 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isErrorEnabled()
/*    */   {
/* 62 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isFatalEnabled()
/*    */   {
/* 70 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isInfoEnabled()
/*    */   {
/* 78 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isTraceEnabled()
/*    */   {
/* 86 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isWarnEnabled()
/*    */   {
/* 94 */     return false;
/*    */   }
/*    */ 
/*    */   public void logDebug(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logDebug(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logError(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logError(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logFatal(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logFatal(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logInfo(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logInfo(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logTrace(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logTrace(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logWarn(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logWarn(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.log.NullLogger
 * JD-Core Version:    0.6.0
 */