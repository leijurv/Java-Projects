/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.net.URLDecoder;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class NonRegisteringDriver
/*     */   implements Driver
/*     */ {
/*     */   private static final String REPLICATION_URL_PREFIX = "jdbc:mysql:replication://";
/*     */   private static final String URL_PREFIX = "jdbc:mysql://";
/*     */   private static final String MXJ_URL_PREFIX = "jdbc:mysql:mxj://";
/*     */   private static final String LOADBALANCE_URL_PREFIX = "jdbc:mysql:loadbalance://";
/*     */   public static final String DBNAME_PROPERTY_KEY = "DBNAME";
/*     */   public static final boolean DEBUG = false;
/*     */   public static final int HOST_NAME_INDEX = 0;
/*     */   public static final String HOST_PROPERTY_KEY = "HOST";
/*     */   public static final String NUM_HOSTS_PROPERTY_KEY = "NUM_HOSTS";
/*     */   public static final String PASSWORD_PROPERTY_KEY = "password";
/*     */   public static final int PORT_NUMBER_INDEX = 1;
/*     */   public static final String PORT_PROPERTY_KEY = "PORT";
/*     */   public static final String PROPERTIES_TRANSFORM_KEY = "propertiesTransform";
/*     */   public static final boolean TRACE = false;
/*     */   public static final String USE_CONFIG_PROPERTY_KEY = "useConfigs";
/*     */   public static final String USER_PROPERTY_KEY = "user";
/*     */ 
/*     */   static int getMajorVersionInternal()
/*     */   {
/* 131 */     return safeIntParse("5");
/*     */   }
/*     */ 
/*     */   static int getMinorVersionInternal()
/*     */   {
/* 140 */     return safeIntParse("1");
/*     */   }
/*     */ 
/*     */   protected static String[] parseHostPortPair(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 159 */     int portIndex = hostPortPair.indexOf(":");
/*     */ 
/* 161 */     String[] splitValues = new String[2];
/*     */ 
/* 163 */     String hostname = null;
/*     */ 
/* 165 */     if (portIndex != -1) {
/* 166 */       if (portIndex + 1 < hostPortPair.length()) {
/* 167 */         String portAsString = hostPortPair.substring(portIndex + 1);
/* 168 */         hostname = hostPortPair.substring(0, portIndex);
/*     */ 
/* 170 */         splitValues[0] = hostname;
/*     */ 
/* 172 */         splitValues[1] = portAsString;
/*     */       } else {
/* 174 */         throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.37"), "01S00", null);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 179 */       splitValues[0] = hostPortPair;
/* 180 */       splitValues[1] = null;
/*     */     }
/*     */ 
/* 183 */     return splitValues;
/*     */   }
/*     */ 
/*     */   private static int safeIntParse(String intAsString) {
/*     */     try {
/* 188 */       return Integer.parseInt(intAsString); } catch (NumberFormatException nfe) {
/*     */     }
/* 190 */     return 0;
/*     */   }
/*     */ 
/*     */   public NonRegisteringDriver()
/*     */     throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean acceptsURL(String url)
/*     */     throws SQLException
/*     */   {
/* 220 */     return parseURL(url, null) != null;
/*     */   }
/*     */ 
/*     */   public java.sql.Connection connect(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 269 */     if (url != null) {
/* 270 */       if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://"))
/* 271 */         return connectLoadBalanced(url, info);
/* 272 */       if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://"))
/*     */       {
/* 274 */         return connectReplicationConnection(url, info);
/*     */       }
/*     */     }
/*     */ 
/* 278 */     Properties props = null;
/*     */ 
/* 280 */     if ((props = parseURL(url, info)) == null)
/* 281 */       return null;
/*     */     SQLException sqlEx;
/*     */     try {
/* 285 */       Connection newConn = ConnectionImpl.getInstance(host(props), port(props), props, database(props), url);
/*     */ 
/* 288 */       return newConn;
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 292 */       throw sqlEx;
/*     */     } catch (Exception ex) {
/* 294 */       sqlEx = SQLError.createSQLException(Messages.getString("NonRegisteringDriver.17") + ex.toString() + Messages.getString("NonRegisteringDriver.18"), "08001", null);
/*     */ 
/* 300 */       sqlEx.initCause(ex);
/*     */     }
/* 302 */     throw sqlEx;
/*     */   }
/*     */ 
/*     */   private java.sql.Connection connectLoadBalanced(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 308 */     Properties parsedProps = parseURL(url, info);
/*     */ 
/* 311 */     parsedProps.remove("roundRobinLoadBalance");
/*     */ 
/* 313 */     if (parsedProps == null) {
/* 314 */       return null;
/*     */     }
/*     */ 
/* 317 */     int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
/*     */ 
/* 319 */     List hostList = new ArrayList();
/*     */ 
/* 321 */     for (int i = 0; i < numHosts; i++) {
/* 322 */       int index = i + 1;
/*     */ 
/* 324 */       hostList.add(parsedProps.getProperty(new StringBuffer().append("HOST.").append(index).toString()) + ":" + parsedProps.getProperty(new StringBuffer().append("PORT.").append(index).toString()));
/*     */     }
/*     */ 
/* 328 */     LoadBalancingConnectionProxy proxyBal = new LoadBalancingConnectionProxy(hostList, parsedProps);
/*     */ 
/* 331 */     return (java.sql.Connection)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Connection.class }, proxyBal);
/*     */   }
/*     */ 
/*     */   protected java.sql.Connection connectReplicationConnection(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 338 */     Properties parsedProps = parseURL(url, info);
/*     */ 
/* 340 */     if (parsedProps == null) {
/* 341 */       return null;
/*     */     }
/*     */ 
/* 344 */     Properties masterProps = (Properties)parsedProps.clone();
/* 345 */     Properties slavesProps = (Properties)parsedProps.clone();
/*     */ 
/* 349 */     slavesProps.setProperty("com.mysql.jdbc.ReplicationConnection.isSlave", "true");
/*     */ 
/* 352 */     int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
/*     */ 
/* 354 */     if (numHosts < 2) {
/* 355 */       throw SQLError.createSQLException("Must specify at least one slave host to connect to for master/slave replication load-balancing functionality", "01S00", null);
/*     */     }
/*     */ 
/* 361 */     for (int i = 1; i < numHosts; i++) {
/* 362 */       int index = i + 1;
/*     */ 
/* 364 */       masterProps.remove("HOST." + index);
/* 365 */       masterProps.remove("PORT." + index);
/*     */ 
/* 367 */       slavesProps.setProperty("HOST." + i, parsedProps.getProperty("HOST." + index));
/* 368 */       slavesProps.setProperty("PORT." + i, parsedProps.getProperty("PORT." + index));
/*     */     }
/*     */ 
/* 371 */     masterProps.setProperty("NUM_HOSTS", "1");
/* 372 */     slavesProps.remove("HOST." + numHosts);
/* 373 */     slavesProps.remove("PORT." + numHosts);
/* 374 */     slavesProps.setProperty("NUM_HOSTS", String.valueOf(numHosts - 1));
/* 375 */     slavesProps.setProperty("HOST", slavesProps.getProperty("HOST.1"));
/* 376 */     slavesProps.setProperty("PORT", slavesProps.getProperty("PORT.1"));
/*     */ 
/* 378 */     return new ReplicationConnection(masterProps, slavesProps);
/*     */   }
/*     */ 
/*     */   public String database(Properties props)
/*     */   {
/* 390 */     return props.getProperty("DBNAME");
/*     */   }
/*     */ 
/*     */   public int getMajorVersion()
/*     */   {
/* 399 */     return getMajorVersionInternal();
/*     */   }
/*     */ 
/*     */   public int getMinorVersion()
/*     */   {
/* 408 */     return getMinorVersionInternal();
/*     */   }
/*     */ 
/*     */   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 439 */     if (info == null) {
/* 440 */       info = new Properties();
/*     */     }
/*     */ 
/* 443 */     if ((url != null) && (url.startsWith("jdbc:mysql://"))) {
/* 444 */       info = parseURL(url, info);
/*     */     }
/*     */ 
/* 447 */     DriverPropertyInfo hostProp = new DriverPropertyInfo("HOST", info.getProperty("HOST"));
/*     */ 
/* 449 */     hostProp.required = true;
/* 450 */     hostProp.description = Messages.getString("NonRegisteringDriver.3");
/*     */ 
/* 452 */     DriverPropertyInfo portProp = new DriverPropertyInfo("PORT", info.getProperty("PORT", "3306"));
/*     */ 
/* 454 */     portProp.required = false;
/* 455 */     portProp.description = Messages.getString("NonRegisteringDriver.7");
/*     */ 
/* 457 */     DriverPropertyInfo dbProp = new DriverPropertyInfo("DBNAME", info.getProperty("DBNAME"));
/*     */ 
/* 459 */     dbProp.required = false;
/* 460 */     dbProp.description = "Database name";
/*     */ 
/* 462 */     DriverPropertyInfo userProp = new DriverPropertyInfo("user", info.getProperty("user"));
/*     */ 
/* 464 */     userProp.required = true;
/* 465 */     userProp.description = Messages.getString("NonRegisteringDriver.13");
/*     */ 
/* 467 */     DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", info.getProperty("password"));
/*     */ 
/* 470 */     passwordProp.required = true;
/* 471 */     passwordProp.description = Messages.getString("NonRegisteringDriver.16");
/*     */ 
/* 474 */     DriverPropertyInfo[] dpi = ConnectionPropertiesImpl.exposeAsDriverPropertyInfo(info, 5);
/*     */ 
/* 477 */     dpi[0] = hostProp;
/* 478 */     dpi[1] = portProp;
/* 479 */     dpi[2] = dbProp;
/* 480 */     dpi[3] = userProp;
/* 481 */     dpi[4] = passwordProp;
/*     */ 
/* 483 */     return dpi;
/*     */   }
/*     */ 
/*     */   public String host(Properties props)
/*     */   {
/* 500 */     return props.getProperty("HOST", "localhost");
/*     */   }
/*     */ 
/*     */   public boolean jdbcCompliant()
/*     */   {
/* 516 */     return false;
/*     */   }
/*     */ 
/*     */   public Properties parseURL(String url, Properties defaults) throws SQLException
/*     */   {
/* 521 */     Properties urlProps = defaults != null ? new Properties(defaults) : new Properties();
/*     */ 
/* 524 */     if (url == null) {
/* 525 */       return null;
/*     */     }
/*     */ 
/* 528 */     if ((!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://")))
/*     */     {
/* 535 */       return null;
/*     */     }
/*     */ 
/* 538 */     int beginningOfSlashes = url.indexOf("//");
/*     */ 
/* 540 */     if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://"))
/*     */     {
/* 542 */       urlProps.setProperty("socketFactory", "com.mysql.management.driverlaunched.ServerLauncherSocketFactory");
/*     */     }
/*     */ 
/* 551 */     int index = url.indexOf("?");
/*     */ 
/* 553 */     if (index != -1) {
/* 554 */       String paramString = url.substring(index + 1, url.length());
/* 555 */       url = url.substring(0, index);
/*     */ 
/* 557 */       StringTokenizer queryParams = new StringTokenizer(paramString, "&");
/*     */ 
/* 559 */       while (queryParams.hasMoreTokens()) {
/* 560 */         String parameterValuePair = queryParams.nextToken();
/*     */ 
/* 562 */         int indexOfEquals = StringUtils.indexOfIgnoreCase(0, parameterValuePair, "=");
/*     */ 
/* 565 */         String parameter = null;
/* 566 */         String value = null;
/*     */ 
/* 568 */         if (indexOfEquals != -1) {
/* 569 */           parameter = parameterValuePair.substring(0, indexOfEquals);
/*     */ 
/* 571 */           if (indexOfEquals + 1 < parameterValuePair.length()) {
/* 572 */             value = parameterValuePair.substring(indexOfEquals + 1);
/*     */           }
/*     */         }
/*     */ 
/* 576 */         if ((value != null) && (value.length() > 0) && (parameter != null) && (parameter.length() > 0)) {
/*     */           try
/*     */           {
/* 579 */             urlProps.put(parameter, URLDecoder.decode(value, "UTF-8"));
/*     */           }
/*     */           catch (UnsupportedEncodingException badEncoding)
/*     */           {
/* 583 */             urlProps.put(parameter, URLDecoder.decode(value));
/*     */           }
/*     */           catch (NoSuchMethodError nsme) {
/* 586 */             urlProps.put(parameter, URLDecoder.decode(value));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 592 */     url = url.substring(beginningOfSlashes + 2);
/*     */ 
/* 594 */     String hostStuff = null;
/*     */ 
/* 596 */     int slashIndex = url.indexOf("/");
/*     */ 
/* 598 */     if (slashIndex != -1) {
/* 599 */       hostStuff = url.substring(0, slashIndex);
/*     */ 
/* 601 */       if (slashIndex + 1 < url.length())
/* 602 */         urlProps.put("DBNAME", url.substring(slashIndex + 1, url.length()));
/*     */     }
/*     */     else
/*     */     {
/* 606 */       hostStuff = url;
/*     */     }
/*     */ 
/* 609 */     int numHosts = 0;
/*     */ 
/* 611 */     if ((hostStuff != null) && (hostStuff.trim().length() > 0)) {
/* 612 */       StringTokenizer st = new StringTokenizer(hostStuff, ",");
/*     */ 
/* 614 */       while (st.hasMoreTokens()) {
/* 615 */         numHosts++;
/*     */ 
/* 617 */         String[] hostPortPair = parseHostPortPair(st.nextToken());
/*     */ 
/* 619 */         if ((hostPortPair[0] != null) && (hostPortPair[0].trim().length() > 0))
/* 620 */           urlProps.setProperty("HOST." + numHosts, hostPortPair[0]);
/*     */         else {
/* 622 */           urlProps.setProperty("HOST." + numHosts, "localhost");
/*     */         }
/*     */ 
/* 625 */         if (hostPortPair[1] != null)
/* 626 */           urlProps.setProperty("PORT." + numHosts, hostPortPair[1]);
/*     */         else
/* 628 */           urlProps.setProperty("PORT." + numHosts, "3306");
/*     */       }
/*     */     }
/*     */     else {
/* 632 */       numHosts = 1;
/* 633 */       urlProps.setProperty("HOST.1", "localhost");
/* 634 */       urlProps.setProperty("PORT.1", "3306");
/*     */     }
/*     */ 
/* 637 */     urlProps.setProperty("NUM_HOSTS", String.valueOf(numHosts));
/* 638 */     urlProps.setProperty("HOST", urlProps.getProperty("HOST.1"));
/* 639 */     urlProps.setProperty("PORT", urlProps.getProperty("PORT.1"));
/*     */ 
/* 641 */     String propertiesTransformClassName = urlProps.getProperty("propertiesTransform");
/*     */ 
/* 644 */     if (propertiesTransformClassName != null) {
/*     */       try {
/* 646 */         ConnectionPropertiesTransform propTransformer = (ConnectionPropertiesTransform)Class.forName(propertiesTransformClassName).newInstance();
/*     */ 
/* 649 */         urlProps = propTransformer.transformProperties(urlProps);
/*     */       } catch (InstantiationException e) {
/* 651 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */       catch (IllegalAccessException e)
/*     */       {
/* 658 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/* 665 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 674 */     if ((Util.isColdFusion()) && (urlProps.getProperty("autoConfigureForColdFusion", "true").equalsIgnoreCase("true")))
/*     */     {
/* 676 */       String configs = urlProps.getProperty("useConfigs");
/*     */ 
/* 678 */       StringBuffer newConfigs = new StringBuffer();
/*     */ 
/* 680 */       if (configs != null) {
/* 681 */         newConfigs.append(configs);
/* 682 */         newConfigs.append(",");
/*     */       }
/*     */ 
/* 685 */       newConfigs.append("coldFusion");
/*     */ 
/* 687 */       urlProps.setProperty("useConfigs", newConfigs.toString());
/*     */     }
/*     */ 
/* 693 */     String configNames = null;
/*     */ 
/* 695 */     if (defaults != null) {
/* 696 */       configNames = defaults.getProperty("useConfigs");
/*     */     }
/*     */ 
/* 699 */     if (configNames == null) {
/* 700 */       configNames = urlProps.getProperty("useConfigs");
/*     */     }
/*     */ 
/* 703 */     if (configNames != null) {
/* 704 */       List splitNames = StringUtils.split(configNames, ",", true);
/*     */ 
/* 706 */       Properties configProps = new Properties();
/*     */ 
/* 708 */       Iterator namesIter = splitNames.iterator();
/*     */ 
/* 710 */       while (namesIter.hasNext()) {
/* 711 */         String configName = (String)namesIter.next();
/*     */         try
/*     */         {
/* 714 */           InputStream configAsStream = getClass().getResourceAsStream("configs/" + configName + ".properties");
/*     */ 
/* 718 */           if (configAsStream == null) {
/* 719 */             throw SQLError.createSQLException("Can't find configuration template named '" + configName + "'", "01S00", null);
/*     */           }
/*     */ 
/* 725 */           configProps.load(configAsStream);
/*     */         } catch (IOException ioEx) {
/* 727 */           SQLException sqlEx = SQLError.createSQLException("Unable to load configuration template '" + configName + "' due to underlying IOException: " + ioEx, "01S00", null);
/*     */ 
/* 733 */           sqlEx.initCause(ioEx);
/*     */ 
/* 735 */           throw sqlEx;
/*     */         }
/*     */       }
/*     */ 
/* 739 */       Iterator propsIter = urlProps.keySet().iterator();
/*     */ 
/* 741 */       while (propsIter.hasNext()) {
/* 742 */         String key = propsIter.next().toString();
/* 743 */         String property = urlProps.getProperty(key);
/* 744 */         configProps.setProperty(key, property);
/*     */       }
/*     */ 
/* 747 */       urlProps = configProps;
/*     */     }
/*     */ 
/* 752 */     if (defaults != null) {
/* 753 */       Iterator propsIter = defaults.keySet().iterator();
/*     */ 
/* 755 */       while (propsIter.hasNext()) {
/* 756 */         String key = propsIter.next().toString();
/* 757 */         if (!key.equals("NUM_HOSTS")) {
/* 758 */           String property = defaults.getProperty(key);
/* 759 */           urlProps.setProperty(key, property);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 764 */     return urlProps;
/*     */   }
/*     */ 
/*     */   public int port(Properties props)
/*     */   {
/* 776 */     return Integer.parseInt(props.getProperty("PORT", "3306"));
/*     */   }
/*     */ 
/*     */   public String property(String name, Properties props)
/*     */   {
/* 790 */     return props.getProperty(name);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.NonRegisteringDriver
 * JD-Core Version:    0.6.0
 */