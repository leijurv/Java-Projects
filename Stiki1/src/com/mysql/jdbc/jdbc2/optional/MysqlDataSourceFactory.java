/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.spi.ObjectFactory;
/*     */ 
/*     */ public class MysqlDataSourceFactory
/*     */   implements ObjectFactory
/*     */ {
/*     */   protected static final String DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
/*     */   protected static final String POOL_DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource";
/*     */   protected static final String XA_DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource";
/*     */ 
/*     */   public Object getObjectInstance(Object refObj, Name nm, Context ctx, Hashtable env)
/*     */     throws Exception
/*     */   {
/*  79 */     Reference ref = (Reference)refObj;
/*  80 */     String className = ref.getClassName();
/*     */ 
/*  82 */     if ((className != null) && ((className.equals("com.mysql.jdbc.jdbc2.optional.MysqlDataSource")) || (className.equals("com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource")) || (className.equals("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource"))))
/*     */     {
/*  86 */       MysqlDataSource dataSource = null;
/*     */       try
/*     */       {
/*  89 */         dataSource = (MysqlDataSource)Class.forName(className).newInstance();
/*     */       }
/*     */       catch (Exception ex) {
/*  92 */         throw new RuntimeException("Unable to create DataSource of class '" + className + "', reason: " + ex.toString());
/*     */       }
/*     */ 
/*  96 */       int portNumber = 3306;
/*     */ 
/*  98 */       String portNumberAsString = nullSafeRefAddrStringGet("port", ref);
/*     */ 
/* 100 */       if (portNumberAsString != null) {
/* 101 */         portNumber = Integer.parseInt(portNumberAsString);
/*     */       }
/*     */ 
/* 104 */       dataSource.setPort(portNumber);
/*     */ 
/* 106 */       String user = nullSafeRefAddrStringGet("user", ref);
/*     */ 
/* 108 */       if (user != null) {
/* 109 */         dataSource.setUser(user);
/*     */       }
/*     */ 
/* 112 */       String password = nullSafeRefAddrStringGet("password", ref);
/*     */ 
/* 114 */       if (password != null) {
/* 115 */         dataSource.setPassword(password);
/*     */       }
/*     */ 
/* 118 */       String serverName = nullSafeRefAddrStringGet("serverName", ref);
/*     */ 
/* 120 */       if (serverName != null) {
/* 121 */         dataSource.setServerName(serverName);
/*     */       }
/*     */ 
/* 124 */       String databaseName = nullSafeRefAddrStringGet("databaseName", ref);
/*     */ 
/* 126 */       if (databaseName != null) {
/* 127 */         dataSource.setDatabaseName(databaseName);
/*     */       }
/*     */ 
/* 130 */       String explicitUrlAsString = nullSafeRefAddrStringGet("explicitUrl", ref);
/*     */ 
/* 132 */       if ((explicitUrlAsString != null) && 
/* 133 */         (Boolean.valueOf(explicitUrlAsString).booleanValue())) {
/* 134 */         dataSource.setUrl(nullSafeRefAddrStringGet("url", ref));
/*     */       }
/*     */ 
/* 138 */       dataSource.setPropertiesViaRef(ref);
/*     */ 
/* 140 */       return dataSource;
/*     */     }
/*     */ 
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   private String nullSafeRefAddrStringGet(String referenceName, Reference ref) {
/* 148 */     RefAddr refAddr = ref.get(referenceName);
/*     */ 
/* 150 */     String asString = refAddr != null ? (String)refAddr.getContent() : null;
/*     */ 
/* 152 */     return asString;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlDataSourceFactory
 * JD-Core Version:    0.6.0
 */