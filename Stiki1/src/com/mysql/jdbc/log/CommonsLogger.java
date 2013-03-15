/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class CommonsLogger
/*     */   implements Log
/*     */ {
/*     */   private org.apache.commons.logging.Log logger;
/*     */ 
/*     */   public CommonsLogger(String instanceName)
/*     */   {
/*  36 */     this.logger = LogFactory.getLog(instanceName);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled() {
/*  40 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled() {
/*  44 */     return this.logger.isErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled() {
/*  48 */     return this.logger.isFatalEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled() {
/*  52 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled() {
/*  56 */     return this.logger.isTraceEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled() {
/*  60 */     return this.logger.isWarnEnabled();
/*     */   }
/*     */ 
/*     */   public void logDebug(Object msg) {
/*  64 */     this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logDebug(Object msg, Throwable thrown) {
/*  68 */     this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logError(Object msg) {
/*  72 */     this.logger.error(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logError(Object msg, Throwable thrown) {
/*  76 */     this.logger.fatal(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object msg) {
/*  80 */     this.logger.fatal(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logFatal(Object msg, Throwable thrown) {
/*  84 */     this.logger.fatal(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object msg) {
/*  88 */     this.logger.info(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logInfo(Object msg, Throwable thrown) {
/*  92 */     this.logger.info(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object msg) {
/*  96 */     this.logger.trace(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logTrace(Object msg, Throwable thrown) {
/* 100 */     this.logger.trace(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object msg) {
/* 104 */     this.logger.warn(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */   }
/*     */ 
/*     */   public void logWarn(Object msg, Throwable thrown) {
/* 108 */     this.logger.warn(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.log.CommonsLogger
 * JD-Core Version:    0.6.0
 */