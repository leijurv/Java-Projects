/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public class Log4JLogger
/*     */   implements Log
/*     */ {
/*     */   private Logger logger;
/*     */ 
/*     */   public Log4JLogger(String instanceName)
/*     */   {
/*  45 */     this.logger = Logger.getLogger(instanceName);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/*  54 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/*  63 */     return this.logger.isEnabledFor(Level.ERROR);
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/*  72 */     return this.logger.isEnabledFor(Level.FATAL);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/*  81 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/*  90 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/*  99 */     return this.logger.isEnabledFor(Level.WARN);
/*     */   }
/*     */ 
/*     */   public void logDebug(Object msg)
/*     */   {
/* 108 */     this.logger.debug(LogUtils.expandProfilerEventIfNecessary(LogUtils.expandProfilerEventIfNecessary(msg)));
/*     */   }
/*     */ 
/*     */   public void logDebug(Object msg, Throwable thrown)
/*     */   {
/* 119 */     this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logError(Object msg)
/*     */   {
/* 128 */     this.logger.error(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logError(Object msg, Throwable thrown)
/*     */   {
/* 138 */     this.logger.error(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object msg)
/*     */   {
/* 147 */     this.logger.fatal(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logFatal(Object msg, Throwable thrown)
/*     */   {
/* 157 */     this.logger.fatal(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object msg)
/*     */   {
/* 166 */     this.logger.info(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logInfo(Object msg, Throwable thrown)
/*     */   {
/* 176 */     this.logger.info(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object msg)
/*     */   {
/* 185 */     this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logTrace(Object msg, Throwable thrown)
/*     */   {
/* 195 */     this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object msg)
/*     */   {
/* 204 */     this.logger.warn(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logWarn(Object msg, Throwable thrown)
/*     */   {
/* 214 */     this.logger.warn(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.log.Log4JLogger
 * JD-Core Version:    0.6.0
 */