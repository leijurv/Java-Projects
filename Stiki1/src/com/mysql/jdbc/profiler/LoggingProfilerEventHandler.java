/*    */ package com.mysql.jdbc.profiler;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class LoggingProfilerEventHandler
/*    */   implements ProfilerEventHandler
/*    */ {
/*    */   private Log log;
/*    */ 
/*    */   public void consumeEvent(ProfilerEvent evt)
/*    */   {
/* 46 */     if (evt.eventType == 0)
/* 47 */       this.log.logWarn(evt);
/*    */     else
/* 49 */       this.log.logInfo(evt);
/*    */   }
/*    */ 
/*    */   public void destroy()
/*    */   {
/* 54 */     this.log = null;
/*    */   }
/*    */ 
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 58 */     this.log = conn.getLog();
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.profiler.LoggingProfilerEventHandler
 * JD-Core Version:    0.6.0
 */