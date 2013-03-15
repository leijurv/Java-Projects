/*    */ package com.mysql.jdbc.profiler;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.ConnectionImpl;
/*    */ import com.mysql.jdbc.Util;
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ProfilerEventHandlerFactory
/*    */ {
/* 43 */   private static final Map CONNECTIONS_TO_SINKS = new HashMap();
/*    */ 
/* 45 */   private Connection ownerConnection = null;
/*    */ 
/* 47 */   private Log log = null;
/*    */ 
/*    */   public static synchronized ProfilerEventHandler getInstance(ConnectionImpl conn)
/*    */     throws SQLException
/*    */   {
/* 58 */     ProfilerEventHandler handler = (ProfilerEventHandler)CONNECTIONS_TO_SINKS.get(conn);
/*    */ 
/* 61 */     if (handler == null) {
/* 62 */       handler = (ProfilerEventHandler)Util.getInstance(conn.getProfilerEventHandler(), new Class[0], new Object[0], conn.getExceptionInterceptor());
/*    */ 
/* 67 */       conn.initializeExtension(handler);
/*    */ 
/* 69 */       CONNECTIONS_TO_SINKS.put(conn, handler);
/*    */     }
/*    */ 
/* 72 */     return handler;
/*    */   }
/*    */ 
/*    */   public static synchronized void removeInstance(Connection conn) {
/* 76 */     ProfilerEventHandler handler = (ProfilerEventHandler)CONNECTIONS_TO_SINKS.remove(conn);
/*    */ 
/* 78 */     if (handler != null)
/* 79 */       handler.destroy();
/*    */   }
/*    */ 
/*    */   private ProfilerEventHandlerFactory(Connection conn)
/*    */   {
/* 84 */     this.ownerConnection = conn;
/*    */     try
/*    */     {
/* 87 */       this.log = this.ownerConnection.getLog();
/*    */     } catch (SQLException sqlEx) {
/* 89 */       throw new RuntimeException("Unable to get logger from connection");
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.profiler.ProfilerEventHandlerFactory
 * JD-Core Version:    0.6.0
 */