/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class LoadBalancingConnectionProxy
/*     */   implements InvocationHandler, PingTarget
/*     */ {
/*     */   private static Method getLocalTimeMethod;
/*     */   public static final String BLACKLIST_TIMEOUT_PROPERTY_KEY = "loadBalanceBlacklistTimeout";
/*     */   private Connection currentConn;
/*     */   private List hostList;
/*     */   private Map liveConnections;
/*     */   private Map connectionsToHostsMap;
/*     */   private long[] responseTimes;
/*     */   private Map hostsToListIndexMap;
/* 120 */   private boolean inTransaction = false;
/*     */ 
/* 122 */   private long transactionStartTime = 0L;
/*     */   private Properties localProps;
/* 126 */   private boolean isClosed = false;
/*     */   private BalanceStrategy balancer;
/*     */   private int retriesAllDown;
/*     */   private static Map globalBlacklist;
/* 134 */   private int globalBlacklistTimeout = 0;
/*     */ 
/*     */   LoadBalancingConnectionProxy(List hosts, Properties props)
/*     */     throws SQLException
/*     */   {
/* 147 */     this.hostList = hosts;
/*     */ 
/* 149 */     int numHosts = this.hostList.size();
/*     */ 
/* 151 */     this.liveConnections = new HashMap(numHosts);
/* 152 */     this.connectionsToHostsMap = new HashMap(numHosts);
/* 153 */     this.responseTimes = new long[numHosts];
/* 154 */     this.hostsToListIndexMap = new HashMap(numHosts);
/*     */ 
/* 156 */     for (int i = 0; i < numHosts; i++) {
/* 157 */       this.hostsToListIndexMap.put(this.hostList.get(i), new Integer(i));
/*     */     }
/*     */ 
/* 160 */     this.localProps = ((Properties)props.clone());
/* 161 */     this.localProps.remove("HOST");
/* 162 */     this.localProps.remove("PORT");
/* 163 */     this.localProps.setProperty("useLocalSessionState", "true");
/*     */ 
/* 165 */     String strategy = this.localProps.getProperty("loadBalanceStrategy", "random");
/*     */ 
/* 168 */     String retriesAllDownAsString = this.localProps.getProperty("retriesAllDown", "120");
/*     */     try
/*     */     {
/* 171 */       this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
/*     */     } catch (NumberFormatException nfe) {
/* 173 */       throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForRetriesAllDown", new Object[] { retriesAllDownAsString }), "S1009", null);
/*     */     }
/*     */ 
/* 178 */     String blacklistTimeoutAsString = this.localProps.getProperty("loadBalanceBlacklistTimeout", "0");
/*     */     try
/*     */     {
/* 181 */       this.globalBlacklistTimeout = Integer.parseInt(blacklistTimeoutAsString);
/*     */     } catch (NumberFormatException nfe) {
/* 183 */       throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForLoadBalanceBlacklistTimeout", new Object[] { retriesAllDownAsString }), "S1009", null);
/*     */     }
/*     */ 
/* 189 */     if ("random".equals(strategy)) {
/* 190 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, "com.mysql.jdbc.RandomBalanceStrategy", "InvalidLoadBalanceStrategy", null).get(0));
/*     */     }
/* 193 */     else if ("bestResponseTime".equals(strategy)) {
/* 194 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, "com.mysql.jdbc.BestResponseTimeBalanceStrategy", "InvalidLoadBalanceStrategy", null).get(0));
/*     */     }
/*     */     else
/*     */     {
/* 198 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, strategy, "InvalidLoadBalanceStrategy", null).get(0));
/*     */     }
/*     */ 
/* 202 */     this.balancer.init(null, props);
/*     */ 
/* 204 */     pickNewConnection();
/*     */   }
/*     */ 
/*     */   public synchronized Connection createConnectionForHost(String hostPortSpec)
/*     */     throws SQLException
/*     */   {
/* 217 */     Properties connProps = (Properties)this.localProps.clone();
/*     */ 
/* 219 */     String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(hostPortSpec);
/*     */ 
/* 222 */     if (hostPortPair[1] == null) {
/* 223 */       hostPortPair[1] = "3306";
/*     */     }
/*     */ 
/* 226 */     connProps.setProperty("HOST", hostPortSpec);
/*     */ 
/* 228 */     connProps.setProperty("PORT", hostPortPair[1]);
/*     */ 
/* 231 */     Connection conn = ConnectionImpl.getInstance(hostPortSpec, Integer.parseInt(hostPortPair[1]), connProps, connProps.getProperty("DBNAME"), "jdbc:mysql://" + hostPortPair[0] + ":" + hostPortPair[1] + "/");
/*     */ 
/* 236 */     this.liveConnections.put(hostPortSpec, conn);
/* 237 */     this.connectionsToHostsMap.put(conn, hostPortSpec);
/*     */ 
/* 239 */     return conn;
/*     */   }
/*     */ 
/*     */   void dealWithInvocationException(InvocationTargetException e)
/*     */     throws SQLException, Throwable, InvocationTargetException
/*     */   {
/* 250 */     Throwable t = e.getTargetException();
/*     */ 
/* 252 */     if (t != null) {
/* 253 */       if ((t instanceof SQLException)) {
/* 254 */         String sqlState = ((SQLException)t).getSQLState();
/*     */ 
/* 256 */         if ((sqlState != null) && 
/* 257 */           (sqlState.startsWith("08")))
/*     */         {
/* 260 */           invalidateCurrentConnection();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 265 */       throw t;
/*     */     }
/*     */ 
/* 268 */     throw e;
/*     */   }
/*     */ 
/*     */   synchronized void invalidateCurrentConnection()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 278 */       if (!this.currentConn.isClosed()) {
/* 279 */         this.currentConn.close();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 284 */       if (isGlobalBlacklistEnabled()) {
/* 285 */         addToGlobalBlacklist((String)this.connectionsToHostsMap.get(this.currentConn));
/*     */       }
/*     */ 
/* 289 */       this.liveConnections.remove(this.connectionsToHostsMap.get(this.currentConn));
/*     */ 
/* 291 */       Object mappedHost = this.connectionsToHostsMap.remove(this.currentConn);
/* 292 */       if ((mappedHost != null) && (this.hostsToListIndexMap.containsKey(mappedHost))) {
/* 293 */         int hostIndex = ((Integer)this.hostsToListIndexMap.get(mappedHost)).intValue();
/*     */ 
/* 295 */         synchronized (this.responseTimes) {
/* 296 */           this.responseTimes[hostIndex] = 0L;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void closeAllConnections() {
/* 303 */     synchronized (this)
/*     */     {
/* 305 */       Iterator allConnections = this.liveConnections.values().iterator();
/*     */ 
/* 308 */       while (allConnections.hasNext())
/*     */         try {
/* 310 */           ((Connection)allConnections.next()).close();
/*     */         }
/*     */         catch (SQLException e) {
/*     */         }
/* 314 */       if (!this.isClosed) {
/* 315 */         this.balancer.destroy();
/*     */       }
/*     */ 
/* 318 */       this.liveConnections.clear();
/* 319 */       this.connectionsToHostsMap.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 332 */     String methodName = method.getName();
/*     */ 
/* 334 */     if (("equals".equals(methodName)) && (args.length == 1)) {
/* 335 */       if ((args[0] instanceof Proxy)) {
/* 336 */         return Boolean.valueOf(((Proxy)args[0]).equals(this));
/*     */       }
/* 338 */       return Boolean.valueOf(equals(args[0]));
/*     */     }
/*     */ 
/* 341 */     if ("hashCode".equals(methodName)) {
/* 342 */       return new Integer(hashCode());
/*     */     }
/*     */ 
/* 345 */     if ("close".equals(methodName)) {
/* 346 */       closeAllConnections();
/*     */ 
/* 348 */       return null;
/*     */     }
/*     */ 
/* 351 */     if ("isClosed".equals(methodName)) {
/* 352 */       return Boolean.valueOf(this.isClosed);
/*     */     }
/*     */ 
/* 355 */     if (this.isClosed) {
/* 356 */       throw SQLError.createSQLException("No operations allowed after connection closed.", "08003", null);
/*     */     }
/*     */ 
/* 361 */     if (!this.inTransaction) {
/* 362 */       this.inTransaction = true;
/* 363 */       this.transactionStartTime = getLocalTimeBestResolution();
/*     */     }
/*     */ 
/* 366 */     Object result = null;
/*     */     try
/*     */     {
/* 369 */       result = method.invoke(this.currentConn, args);
/*     */ 
/* 371 */       if (result != null) {
/* 372 */         if ((result instanceof Statement)) {
/* 373 */           ((Statement)result).setPingTarget(this);
/*     */         }
/*     */ 
/* 376 */         result = proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */       }
/*     */     } catch (InvocationTargetException e) {
/* 379 */       dealWithInvocationException(e);
/*     */     } finally {
/* 381 */       if (("commit".equals(methodName)) || ("rollback".equals(methodName))) {
/* 382 */         this.inTransaction = false;
/*     */ 
/* 385 */         String host = (String)this.connectionsToHostsMap.get(this.currentConn);
/*     */ 
/* 388 */         if (host != null) {
/* 389 */           int hostIndex = ((Integer)this.hostsToListIndexMap.get(host)).intValue();
/*     */ 
/* 394 */           synchronized (this.responseTimes) {
/* 395 */             this.responseTimes[hostIndex] = (getLocalTimeBestResolution() - this.transactionStartTime);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 400 */         pickNewConnection();
/*     */       }
/*     */     }
/*     */ 
/* 404 */     return result;
/*     */   }
/*     */ 
/*     */   private synchronized void pickNewConnection()
/*     */     throws SQLException
/*     */   {
/* 414 */     if (this.currentConn == null) {
/* 415 */       this.currentConn = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
/*     */ 
/* 421 */       return;
/*     */     }
/*     */ 
/* 424 */     Connection newConn = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
/*     */ 
/* 430 */     newConn.setTransactionIsolation(this.currentConn.getTransactionIsolation());
/*     */ 
/* 432 */     newConn.setAutoCommit(this.currentConn.getAutoCommit());
/*     */ 
/* 434 */     this.currentConn = newConn;
/*     */   }
/*     */ 
/*     */   Object proxyIfInterfaceIsJdbc(Object toProxy, Class clazz)
/*     */   {
/* 448 */     Class[] interfaces = clazz.getInterfaces();
/*     */ 
/* 450 */     int i = 0; if (i < interfaces.length) {
/* 451 */       String packageName = interfaces[i].getPackage().getName();
/*     */ 
/* 453 */       if (("java.sql".equals(packageName)) || ("javax.sql".equals(packageName)) || ("com.mysql.jdbc".equals(packageName)))
/*     */       {
/* 456 */         return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(toProxy));
/*     */       }
/*     */ 
/* 461 */       return proxyIfInterfaceIsJdbc(toProxy, interfaces[i]);
/*     */     }
/*     */ 
/* 464 */     return toProxy;
/*     */   }
/*     */ 
/*     */   private static long getLocalTimeBestResolution()
/*     */   {
/* 472 */     if (getLocalTimeMethod != null)
/*     */       try {
/* 474 */         return ((Long)getLocalTimeMethod.invoke(null, null)).longValue();
/*     */       }
/*     */       catch (IllegalArgumentException e)
/*     */       {
/*     */       }
/*     */       catch (IllegalAccessException e)
/*     */       {
/*     */       }
/*     */       catch (InvocationTargetException e)
/*     */       {
/*     */       }
/* 485 */     return System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized void doPing() throws SQLException {
/* 489 */     SQLException se = null;
/* 490 */     boolean foundHost = false;
/*     */     Iterator i;
/* 492 */     synchronized (this) {
/* 493 */       for (i = this.hostList.iterator(); i.hasNext(); ) {
/* 494 */         String host = (String)i.next();
/* 495 */         Connection conn = (Connection)this.liveConnections.get(host);
/* 496 */         if (conn == null)
/*     */           continue;
/*     */         try
/*     */         {
/* 500 */           conn.ping();
/* 501 */           foundHost = true;
/*     */         }
/*     */         catch (SQLException e) {
/* 504 */           if (host.equals(this.connectionsToHostsMap.get(this.currentConn)))
/*     */           {
/* 506 */             closeAllConnections();
/* 507 */             this.isClosed = true;
/* 508 */             throw e;
/*     */           }
/*     */ 
/* 512 */           if (e.getMessage().equals(Messages.getString("Connection.exceededConnectionLifetime")))
/*     */           {
/* 514 */             if (se == null)
/* 515 */               se = e;
/*     */           }
/*     */           else
/*     */           {
/* 519 */             se = e;
/* 520 */             if (isGlobalBlacklistEnabled()) {
/* 521 */               addToGlobalBlacklist(host);
/*     */             }
/*     */           }
/*     */ 
/* 525 */           this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 531 */     if (!foundHost) {
/* 532 */       closeAllConnections();
/* 533 */       this.isClosed = true;
/*     */ 
/* 535 */       if (se != null) {
/* 536 */         throw se;
/*     */       }
/*     */ 
/* 539 */       ((ConnectionImpl)this.currentConn).throwConnectionClosedException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addToGlobalBlacklist(String host)
/*     */   {
/* 545 */     if (isGlobalBlacklistEnabled())
/* 546 */       synchronized (globalBlacklist) {
/* 547 */         globalBlacklist.put(host, new Long(System.currentTimeMillis() + this.globalBlacklistTimeout));
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean isGlobalBlacklistEnabled()
/*     */   {
/* 554 */     return this.globalBlacklistTimeout > 0;
/*     */   }
/*     */ 
/*     */   public Map getGlobalBlacklist() {
/* 558 */     if (!isGlobalBlacklistEnabled()) {
/* 559 */       return new HashMap(1);
/*     */     }
/*     */ 
/* 563 */     Map blacklistClone = new HashMap(globalBlacklist.size());
/*     */ 
/* 565 */     synchronized (globalBlacklist) {
/* 566 */       blacklistClone.putAll(globalBlacklist);
/*     */     }
/* 568 */     Set keys = blacklistClone.keySet();
/*     */ 
/* 571 */     keys.retainAll(this.hostList);
/* 572 */     if (keys.size() == this.hostList.size())
/*     */     {
/* 576 */       return new HashMap(1);
/*     */     }
/*     */ 
/* 580 */     for (Iterator i = keys.iterator(); i.hasNext(); ) {
/* 581 */       String host = (String)i.next();
/*     */ 
/* 583 */       Long timeout = (Long)globalBlacklist.get(host);
/* 584 */       if ((timeout != null) && (timeout.longValue() < System.currentTimeMillis()))
/*     */       {
/* 586 */         synchronized (globalBlacklist) {
/* 587 */           globalBlacklist.remove(host);
/*     */         }
/* 589 */         i.remove();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 594 */     return blacklistClone;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  71 */       getLocalTimeMethod = System.class.getMethod("nanoTime", new Class[0]);
/*     */     }
/*     */     catch (SecurityException e)
/*     */     {
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/*     */     }
/*     */ 
/* 132 */     globalBlacklist = new HashMap();
/*     */   }
/*     */ 
/*     */   protected class ConnectionErrorFiringInvocationHandler
/*     */     implements InvocationHandler
/*     */   {
/*  84 */     Object invokeOn = null;
/*     */ 
/*     */     public ConnectionErrorFiringInvocationHandler(Object toInvokeOn) {
/*  87 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */ 
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */     {
/*  92 */       Object result = null;
/*     */       try
/*     */       {
/*  95 */         result = method.invoke(this.invokeOn, args);
/*     */ 
/*  97 */         if (result != null)
/*  98 */           result = LoadBalancingConnectionProxy.this.proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */       }
/*     */       catch (InvocationTargetException e) {
/* 101 */         LoadBalancingConnectionProxy.this.dealWithInvocationException(e);
/*     */       }
/*     */ 
/* 104 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.LoadBalancingConnectionProxy
 * JD-Core Version:    0.6.0
 */