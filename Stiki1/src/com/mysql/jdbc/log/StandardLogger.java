/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class StandardLogger
/*     */   implements Log
/*     */ {
/*     */   private static final int FATAL = 0;
/*     */   private static final int ERROR = 1;
/*     */   private static final int WARN = 2;
/*     */   private static final int INFO = 3;
/*     */   private static final int DEBUG = 4;
/*     */   private static final int TRACE = 5;
/*  56 */   public static StringBuffer bufferedLog = null;
/*     */ 
/*  58 */   private boolean logLocationInfo = true;
/*     */ 
/*     */   public StandardLogger(String name)
/*     */   {
/*  67 */     this(name, false);
/*     */   }
/*     */ 
/*     */   public StandardLogger(String name, boolean logLocationInfo) {
/*  71 */     this.logLocationInfo = logLocationInfo;
/*     */   }
/*     */ 
/*     */   public static void saveLogsToBuffer() {
/*  75 */     if (bufferedLog == null)
/*  76 */       bufferedLog = new StringBuffer();
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/*  84 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/*  98 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 105 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 112 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 119 */     return true;
/*     */   }
/*     */ 
/*     */   public void logDebug(Object message)
/*     */   {
/* 129 */     logInternal(4, message, null);
/*     */   }
/*     */ 
/*     */   public void logDebug(Object message, Throwable exception)
/*     */   {
/* 141 */     logInternal(4, message, exception);
/*     */   }
/*     */ 
/*     */   public void logError(Object message)
/*     */   {
/* 151 */     logInternal(1, message, null);
/*     */   }
/*     */ 
/*     */   public void logError(Object message, Throwable exception)
/*     */   {
/* 163 */     logInternal(1, message, exception);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object message)
/*     */   {
/* 173 */     logInternal(0, message, null);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object message, Throwable exception)
/*     */   {
/* 185 */     logInternal(0, message, exception);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object message)
/*     */   {
/* 195 */     logInternal(3, message, null);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object message, Throwable exception)
/*     */   {
/* 207 */     logInternal(3, message, exception);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object message)
/*     */   {
/* 217 */     logInternal(5, message, null);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object message, Throwable exception)
/*     */   {
/* 229 */     logInternal(5, message, exception);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object message)
/*     */   {
/* 239 */     logInternal(2, message, null);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object message, Throwable exception)
/*     */   {
/* 251 */     logInternal(2, message, exception);
/*     */   }
/*     */ 
/*     */   private void logInternal(int level, Object msg, Throwable exception) {
/* 255 */     StringBuffer msgBuf = new StringBuffer();
/* 256 */     msgBuf.append(new Date().toString());
/* 257 */     msgBuf.append(" ");
/*     */ 
/* 259 */     switch (level) {
/*     */     case 0:
/* 261 */       msgBuf.append("FATAL: ");
/*     */ 
/* 263 */       break;
/*     */     case 1:
/* 266 */       msgBuf.append("ERROR: ");
/*     */ 
/* 268 */       break;
/*     */     case 2:
/* 271 */       msgBuf.append("WARN: ");
/*     */ 
/* 273 */       break;
/*     */     case 3:
/* 276 */       msgBuf.append("INFO: ");
/*     */ 
/* 278 */       break;
/*     */     case 4:
/* 281 */       msgBuf.append("DEBUG: ");
/*     */ 
/* 283 */       break;
/*     */     case 5:
/* 286 */       msgBuf.append("TRACE: ");
/*     */     }
/*     */ 
/* 291 */     if ((msg instanceof ProfilerEvent)) {
/* 292 */       msgBuf.append(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */     }
/*     */     else {
/* 295 */       if ((this.logLocationInfo) && (level != 5)) {
/* 296 */         Throwable locationException = new Throwable();
/* 297 */         msgBuf.append(LogUtils.findCallingClassAndMethod(locationException));
/*     */ 
/* 299 */         msgBuf.append(" ");
/*     */       }
/*     */ 
/* 302 */       if (msg != null) {
/* 303 */         msgBuf.append(String.valueOf(msg));
/*     */       }
/*     */     }
/*     */ 
/* 307 */     if (exception != null) {
/* 308 */       msgBuf.append("\n");
/* 309 */       msgBuf.append("\n");
/* 310 */       msgBuf.append("EXCEPTION STACK TRACE:");
/* 311 */       msgBuf.append("\n");
/* 312 */       msgBuf.append("\n");
/* 313 */       msgBuf.append(Util.stackTraceToString(exception));
/*     */     }
/*     */ 
/* 316 */     String messageAsString = msgBuf.toString();
/*     */ 
/* 318 */     System.err.println(messageAsString);
/*     */ 
/* 320 */     if (bufferedLog != null)
/* 321 */       bufferedLog.append(messageAsString);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.log.StandardLogger
 * JD-Core Version:    0.6.0
 */