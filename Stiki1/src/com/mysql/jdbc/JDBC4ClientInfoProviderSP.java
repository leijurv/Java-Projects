/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class JDBC4ClientInfoProviderSP
/*     */   implements JDBC4ClientInfoProvider
/*     */ {
/*     */   PreparedStatement setClientInfoSp;
/*     */   PreparedStatement getClientInfoSp;
/*     */   PreparedStatement getClientInfoBulkSp;
/*     */ 
/*     */   public synchronized void initialize(java.sql.Connection conn, Properties configurationProps)
/*     */     throws SQLException
/*     */   {
/*  45 */     String identifierQuote = conn.getMetaData().getIdentifierQuoteString();
/*  46 */     String setClientInfoSpName = configurationProps.getProperty("clientInfoSetSPName", "setClientInfo");
/*     */ 
/*  48 */     String getClientInfoSpName = configurationProps.getProperty("clientInfoGetSPName", "getClientInfo");
/*     */ 
/*  50 */     String getClientInfoBulkSpName = configurationProps.getProperty("clientInfoGetBulkSPName", "getClientInfoBulk");
/*     */ 
/*  52 */     String clientInfoCatalog = configurationProps.getProperty("clientInfoCatalog", "");
/*     */ 
/*  56 */     String catalog = "".equals(clientInfoCatalog) ? conn.getCatalog() : clientInfoCatalog;
/*     */ 
/*  59 */     this.setClientInfoSp = ((Connection)conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + setClientInfoSpName + identifierQuote + "(?, ?)");
/*     */ 
/*  64 */     this.getClientInfoSp = ((Connection)conn).clientPrepareStatement("CALL" + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoSpName + identifierQuote + "(?)");
/*     */ 
/*  69 */     this.getClientInfoBulkSp = ((Connection)conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoBulkSpName + identifierQuote + "()");
/*     */   }
/*     */ 
/*     */   public synchronized void destroy()
/*     */     throws SQLException
/*     */   {
/*  76 */     if (this.setClientInfoSp != null) {
/*  77 */       this.setClientInfoSp.close();
/*  78 */       this.setClientInfoSp = null;
/*     */     }
/*     */ 
/*  81 */     if (this.getClientInfoSp != null) {
/*  82 */       this.getClientInfoSp.close();
/*  83 */       this.getClientInfoSp = null;
/*     */     }
/*     */ 
/*  86 */     if (this.getClientInfoBulkSp != null) {
/*  87 */       this.getClientInfoBulkSp.close();
/*  88 */       this.getClientInfoBulkSp = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized Properties getClientInfo(java.sql.Connection conn) throws SQLException
/*     */   {
/*  94 */     ResultSet rs = null;
/*     */ 
/*  96 */     Properties props = new Properties();
/*     */     try
/*     */     {
/*  99 */       this.getClientInfoBulkSp.execute();
/*     */ 
/* 101 */       rs = this.getClientInfoBulkSp.getResultSet();
/*     */ 
/* 103 */       while (rs.next())
/* 104 */         props.setProperty(rs.getString(1), rs.getString(2));
/*     */     }
/*     */     finally {
/* 107 */       if (rs != null) {
/* 108 */         rs.close();
/*     */       }
/*     */     }
/*     */ 
/* 112 */     return props;
/*     */   }
/*     */ 
/*     */   public synchronized String getClientInfo(java.sql.Connection conn, String name) throws SQLException
/*     */   {
/* 117 */     ResultSet rs = null;
/*     */ 
/* 119 */     String clientInfo = null;
/*     */     try
/*     */     {
/* 122 */       this.getClientInfoSp.setString(1, name);
/* 123 */       this.getClientInfoSp.execute();
/*     */ 
/* 125 */       rs = this.getClientInfoSp.getResultSet();
/*     */ 
/* 127 */       if (rs.next())
/* 128 */         clientInfo = rs.getString(1);
/*     */     }
/*     */     finally {
/* 131 */       if (rs != null) {
/* 132 */         rs.close();
/*     */       }
/*     */     }
/*     */ 
/* 136 */     return clientInfo;
/*     */   }
/*     */ 
/*     */   public synchronized void setClientInfo(java.sql.Connection conn, Properties properties) throws SQLClientInfoException
/*     */   {
/*     */     try {
/* 142 */       Enumeration propNames = properties.propertyNames();
/*     */ 
/* 144 */       while (propNames.hasMoreElements()) {
/* 145 */         String name = (String)propNames.nextElement();
/* 146 */         String value = properties.getProperty(name);
/*     */ 
/* 148 */         setClientInfo(conn, name, value);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 151 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 152 */       clientInfoEx.initCause(sqlEx);
/*     */ 
/* 154 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setClientInfo(java.sql.Connection conn, String name, String value) throws SQLClientInfoException
/*     */   {
/*     */     try {
/* 161 */       this.setClientInfoSp.setString(1, name);
/* 162 */       this.setClientInfoSp.setString(2, value);
/* 163 */       this.setClientInfoSp.execute();
/*     */     } catch (SQLException sqlEx) {
/* 165 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 166 */       clientInfoEx.initCause(sqlEx);
/*     */ 
/* 168 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4ClientInfoProviderSP
 * JD-Core Version:    0.6.0
 */