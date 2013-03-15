/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class RandomBalanceStrategy
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
/*  51 */     int numHosts = configuredHosts.size();
/*     */ 
/*  53 */     SQLException ex = null;
/*     */ 
/*  55 */     List whiteList = new ArrayList(numHosts);
/*  56 */     whiteList.addAll(configuredHosts);
/*     */ 
/*  58 */     Map blackList = proxy.getGlobalBlacklist();
/*     */ 
/*  60 */     whiteList.removeAll(blackList.keySet());
/*     */ 
/*  62 */     Map whiteListMap = getArrayIndexMap(whiteList);
/*     */ 
/*  65 */     int attempts = 0;
/*     */     Connection conn;
/*     */     while (true)
/*     */     {
/*  65 */       if (attempts >= numRetries) break label286; int random = (int)Math.floor(Math.random() * whiteList.size());
/*     */ 
/*  68 */       String hostPortSpec = (String)whiteList.get(random);
/*     */ 
/*  70 */       conn = (Connection)liveConnections.get(hostPortSpec);
/*     */ 
/*  72 */       if (conn != null) break;
/*     */       try {
/*  74 */         conn = proxy.createConnectionForHost(hostPortSpec);
/*     */       } catch (SQLException sqlEx) {
/*  76 */         ex = sqlEx;
/*     */ 
/*  78 */         if (((sqlEx instanceof CommunicationsException)) || ("08S01".equals(sqlEx.getSQLState())))
/*     */         {
/*  81 */           Integer whiteListIndex = (Integer)whiteListMap.get(hostPortSpec);
/*     */ 
/*  85 */           if (whiteListIndex != null) {
/*  86 */             whiteList.remove(whiteListIndex.intValue());
/*  87 */             whiteListMap = getArrayIndexMap(whiteList);
/*     */           }
/*  89 */           proxy.addToGlobalBlacklist(hostPortSpec);
/*     */ 
/*  91 */           if (whiteList.size() == 0) {
/*  92 */             attempts++;
/*     */             try {
/*  94 */               Thread.sleep(250L);
/*     */             }
/*     */             catch (InterruptedException e)
/*     */             {
/*     */             }
/*  99 */             whiteListMap = new HashMap(numHosts);
/* 100 */             whiteList.addAll(configuredHosts);
/* 101 */             blackList = proxy.getGlobalBlacklist();
/*     */ 
/* 103 */             whiteList.removeAll(blackList.keySet());
/* 104 */             whiteListMap = getArrayIndexMap(whiteList);
/*     */           }
/*     */ 
/* 107 */           continue;
/*     */         }
/* 109 */         throw sqlEx;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 114 */     return conn;
/*     */ 
/* 117 */     label286: if (ex != null) {
/* 118 */       throw ex;
/*     */     }
/*     */ 
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   private Map getArrayIndexMap(List l) {
/* 125 */     Map m = new HashMap(l.size());
/* 126 */     for (int i = 0; i < l.size(); i++) {
/* 127 */       m.put(l.get(i), new Integer(i));
/*     */     }
/* 129 */     return m;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.RandomBalanceStrategy
 * JD-Core Version:    0.6.0
 */