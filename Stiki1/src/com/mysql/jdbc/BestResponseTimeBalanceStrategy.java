/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class BestResponseTimeBalanceStrategy
/*     */   implements BalanceStrategy
/*     */ {
/*     */   public void destroy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void init(Connection conn, Properties props)
/*     */     throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   public Connection pickConnection(LoadBalancingConnectionProxy proxy, List configuredHosts, Map liveConnections, long[] responseTimes, int numRetries)
/*     */     throws SQLException
/*     */   {
/*  56 */     Map blackList = proxy.getGlobalBlacklist();
/*     */ 
/*  58 */     SQLException ex = null;
/*     */ 
/*  60 */     int attempts = 0;
/*     */     Connection conn;
/*     */     while (true)
/*     */     {
/*  60 */       if (attempts >= numRetries) break label252; long minResponseTime = 9223372036854775807L;
/*     */ 
/*  63 */       int bestHostIndex = 0;
/*     */ 
/*  66 */       if (blackList.size() == configuredHosts.size()) {
/*  67 */         blackList = proxy.getGlobalBlacklist();
/*     */       }
/*     */ 
/*  70 */       for (int i = 0; i < responseTimes.length; i++) {
/*  71 */         long candidateResponseTime = responseTimes[i];
/*     */ 
/*  73 */         if ((candidateResponseTime >= minResponseTime) || (blackList.containsKey(configuredHosts.get(i))))
/*     */           continue;
/*  75 */         if (candidateResponseTime == 0L) {
/*  76 */           bestHostIndex = i;
/*     */ 
/*  78 */           break;
/*     */         }
/*     */ 
/*  81 */         bestHostIndex = i;
/*  82 */         minResponseTime = candidateResponseTime;
/*     */       }
/*     */ 
/*  86 */       String bestHost = (String)configuredHosts.get(bestHostIndex);
/*     */ 
/*  88 */       conn = (Connection)liveConnections.get(bestHost);
/*     */ 
/*  90 */       if (conn != null) break;
/*     */       try {
/*  92 */         conn = proxy.createConnectionForHost(bestHost);
/*     */       } catch (SQLException sqlEx) {
/*  94 */         ex = sqlEx;
/*     */ 
/*  96 */         if (((sqlEx instanceof CommunicationsException)) || ("08S01".equals(sqlEx.getSQLState())))
/*     */         {
/*  98 */           proxy.addToGlobalBlacklist(bestHost);
/*  99 */           blackList.put(bestHost, null);
/*     */ 
/* 102 */           if (blackList.size() == configuredHosts.size()) {
/* 103 */             attempts++;
/*     */             try {
/* 105 */               Thread.sleep(250L);
/*     */             } catch (InterruptedException e) {
/*     */             }
/* 108 */             blackList = proxy.getGlobalBlacklist();
/*     */           }
/*     */ 
/* 111 */           continue;
/*     */         }
/* 113 */         throw sqlEx;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 118 */     return conn;
/*     */ 
/* 121 */     label252: if (ex != null) {
/* 122 */       throw ex;
/*     */     }
/*     */ 
/* 125 */     return null;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.BestResponseTimeBalanceStrategy
 * JD-Core Version:    0.6.0
 */