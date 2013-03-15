/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ 
/*     */ public class LogUtils
/*     */ {
/*     */   public static final String CALLER_INFORMATION_NOT_AVAILABLE = "Caller information not available";
/*  35 */   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */ 
/*  38 */   private static final int LINE_SEPARATOR_LENGTH = LINE_SEPARATOR.length();
/*     */ 
/*     */   public static Object expandProfilerEventIfNecessary(Object possibleProfilerEvent)
/*     */   {
/*  43 */     if ((possibleProfilerEvent instanceof ProfilerEvent)) {
/*  44 */       StringBuffer msgBuf = new StringBuffer();
/*     */ 
/*  46 */       ProfilerEvent evt = (ProfilerEvent)possibleProfilerEvent;
/*     */ 
/*  48 */       Throwable locationException = evt.getEventCreationPoint();
/*     */ 
/*  50 */       if (locationException == null) {
/*  51 */         locationException = new Throwable();
/*     */       }
/*     */ 
/*  54 */       msgBuf.append("Profiler Event: [");
/*     */ 
/*  56 */       boolean appendLocationInfo = false;
/*     */ 
/*  58 */       switch (evt.getEventType()) {
/*     */       case 4:
/*  60 */         msgBuf.append("EXECUTE");
/*     */ 
/*  62 */         break;
/*     */       case 5:
/*  65 */         msgBuf.append("FETCH");
/*     */ 
/*  67 */         break;
/*     */       case 1:
/*  70 */         msgBuf.append("CONSTRUCT");
/*     */ 
/*  72 */         break;
/*     */       case 2:
/*  75 */         msgBuf.append("PREPARE");
/*     */ 
/*  77 */         break;
/*     */       case 3:
/*  80 */         msgBuf.append("QUERY");
/*     */ 
/*  82 */         break;
/*     */       case 0:
/*  85 */         msgBuf.append("WARN");
/*  86 */         appendLocationInfo = true;
/*     */ 
/*  88 */         break;
/*     */       case 6:
/*  91 */         msgBuf.append("SLOW QUERY");
/*  92 */         appendLocationInfo = false;
/*     */ 
/*  94 */         break;
/*     */       default:
/*  97 */         msgBuf.append("UNKNOWN");
/*     */       }
/*     */ 
/* 100 */       msgBuf.append("] ");
/* 101 */       msgBuf.append(findCallingClassAndMethod(locationException));
/* 102 */       msgBuf.append(" duration: ");
/* 103 */       msgBuf.append(evt.getEventDuration());
/* 104 */       msgBuf.append(" ");
/* 105 */       msgBuf.append(evt.getDurationUnits());
/* 106 */       msgBuf.append(", connection-id: ");
/* 107 */       msgBuf.append(evt.getConnectionId());
/* 108 */       msgBuf.append(", statement-id: ");
/* 109 */       msgBuf.append(evt.getStatementId());
/* 110 */       msgBuf.append(", resultset-id: ");
/* 111 */       msgBuf.append(evt.getResultSetId());
/*     */ 
/* 113 */       String evtMessage = evt.getMessage();
/*     */ 
/* 115 */       if (evtMessage != null) {
/* 116 */         msgBuf.append(", message: ");
/* 117 */         msgBuf.append(evtMessage);
/*     */       }
/*     */ 
/* 120 */       if (appendLocationInfo) {
/* 121 */         msgBuf.append("\n\nFull stack trace of location where event occurred:\n\n");
/*     */ 
/* 123 */         msgBuf.append(Util.stackTraceToString(locationException));
/* 124 */         msgBuf.append("\n");
/*     */       }
/*     */ 
/* 127 */       return msgBuf;
/*     */     }
/*     */ 
/* 130 */     return possibleProfilerEvent;
/*     */   }
/*     */ 
/*     */   public static String findCallingClassAndMethod(Throwable t) {
/* 134 */     String stackTraceAsString = Util.stackTraceToString(t);
/*     */ 
/* 136 */     String callingClassAndMethod = "Caller information not available";
/*     */ 
/* 138 */     int endInternalMethods = stackTraceAsString.lastIndexOf("com.mysql.jdbc");
/*     */ 
/* 141 */     if (endInternalMethods != -1) {
/* 142 */       int endOfLine = -1;
/* 143 */       int compliancePackage = stackTraceAsString.indexOf("com.mysql.jdbc.compliance", endInternalMethods);
/*     */ 
/* 146 */       if (compliancePackage != -1)
/* 147 */         endOfLine = compliancePackage - LINE_SEPARATOR_LENGTH;
/*     */       else {
/* 149 */         endOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endInternalMethods);
/*     */       }
/*     */ 
/* 153 */       if (endOfLine != -1) {
/* 154 */         int nextEndOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endOfLine + LINE_SEPARATOR_LENGTH);
/*     */ 
/* 157 */         if (nextEndOfLine != -1) {
/* 158 */           callingClassAndMethod = stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH, nextEndOfLine);
/*     */         }
/*     */         else {
/* 161 */           callingClassAndMethod = stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 167 */     if ((!callingClassAndMethod.startsWith("\tat ")) && (!callingClassAndMethod.startsWith("at ")))
/*     */     {
/* 169 */       return "at " + callingClassAndMethod;
/*     */     }
/*     */ 
/* 172 */     return callingClassAndMethod;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.log.LogUtils
 * JD-Core Version:    0.6.0
 */