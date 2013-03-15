/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class Jdk14Logger
/*     */   implements Log
/*     */ {
/*  43 */   private static final Level DEBUG = Level.FINE;
/*     */ 
/*  45 */   private static final Level ERROR = Level.SEVERE;
/*     */ 
/*  47 */   private static final Level FATAL = Level.SEVERE;
/*     */ 
/*  49 */   private static final Level INFO = Level.INFO;
/*     */ 
/*  51 */   private static final Level TRACE = Level.FINEST;
/*     */ 
/*  53 */   private static final Level WARN = Level.WARNING;
/*     */ 
/*  58 */   protected Logger jdkLogger = null;
/*     */ 
/*     */   public Jdk14Logger(String name)
/*     */   {
/*  67 */     this.jdkLogger = Logger.getLogger(name);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/*  74 */     return this.jdkLogger.isLoggable(Level.FINE);
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/*  81 */     return this.jdkLogger.isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/*  88 */     return this.jdkLogger.isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/*  95 */     return this.jdkLogger.isLoggable(Level.INFO);
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 102 */     return this.jdkLogger.isLoggable(Level.FINEST);
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 109 */     return this.jdkLogger.isLoggable(Level.WARNING);
/*     */   }
/*     */ 
/*     */   public void logDebug(Object message)
/*     */   {
/* 119 */     logInternal(DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void logDebug(Object message, Throwable exception)
/*     */   {
/* 131 */     logInternal(DEBUG, message, exception);
/*     */   }
/*     */ 
/*     */   public void logError(Object message)
/*     */   {
/* 141 */     logInternal(ERROR, message, null);
/*     */   }
/*     */ 
/*     */   public void logError(Object message, Throwable exception)
/*     */   {
/* 153 */     logInternal(ERROR, message, exception);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object message)
/*     */   {
/* 163 */     logInternal(FATAL, message, null);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object message, Throwable exception)
/*     */   {
/* 175 */     logInternal(FATAL, message, exception);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object message)
/*     */   {
/* 185 */     logInternal(INFO, message, null);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object message, Throwable exception)
/*     */   {
/* 197 */     logInternal(INFO, message, exception);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object message)
/*     */   {
/* 207 */     logInternal(TRACE, message, null);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object message, Throwable exception)
/*     */   {
/* 219 */     logInternal(TRACE, message, exception);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object message)
/*     */   {
/* 229 */     logInternal(WARN, message, null);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object message, Throwable exception)
/*     */   {
/* 241 */     logInternal(WARN, message, exception);
/*     */   }
/*     */ 
/*     */   private static final int findCallerStackDepth(StackTraceElement[] stackTrace) {
/* 245 */     int numFrames = stackTrace.length;
/*     */ 
/* 247 */     for (int i = 0; i < numFrames; i++) {
/* 248 */       String callerClassName = stackTrace[i].getClassName();
/*     */ 
/* 250 */       if ((!callerClassName.startsWith("com.mysql.jdbc")) || (callerClassName.startsWith("com.mysql.jdbc.compliance")))
/*     */       {
/* 252 */         return i;
/*     */       }
/*     */     }
/*     */ 
/* 256 */     return 0;
/*     */   }
/*     */ 
/*     */   private void logInternal(Level level, Object msg, Throwable exception)
/*     */   {
/* 265 */     if (this.jdkLogger.isLoggable(level)) {
/* 266 */       String messageAsString = null;
/* 267 */       String callerMethodName = "N/A";
/* 268 */       String callerClassName = "N/A";
/* 269 */       int lineNumber = 0;
/* 270 */       String fileName = "N/A";
/*     */ 
/* 272 */       if ((msg instanceof ProfilerEvent)) {
/* 273 */         messageAsString = LogUtils.expandProfilerEventIfNecessary(msg).toString();
/*     */       }
/*     */       else {
/* 276 */         Throwable locationException = new Throwable();
/* 277 */         StackTraceElement[] locations = locationException.getStackTrace();
/*     */ 
/* 280 */         int frameIdx = findCallerStackDepth(locations);
/*     */ 
/* 282 */         if (frameIdx != 0) {
/* 283 */           callerClassName = locations[frameIdx].getClassName();
/* 284 */           callerMethodName = locations[frameIdx].getMethodName();
/* 285 */           lineNumber = locations[frameIdx].getLineNumber();
/* 286 */           fileName = locations[frameIdx].getFileName();
/*     */         }
/*     */ 
/* 289 */         messageAsString = String.valueOf(msg);
/*     */       }
/*     */ 
/* 292 */       if (exception == null) {
/* 293 */         this.jdkLogger.logp(level, callerClassName, callerMethodName, messageAsString);
/*     */       }
/*     */       else
/* 296 */         this.jdkLogger.logp(level, callerClassName, callerMethodName, messageAsString, exception);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.log.Jdk14Logger
 * JD-Core Version:    0.6.0
 */