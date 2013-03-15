/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class MysqlParameterMetadata
/*     */   implements ParameterMetaData
/*     */ {
/*  34 */   boolean returnSimpleMetadata = false;
/*     */ 
/*  36 */   ResultSetMetaData metadata = null;
/*     */ 
/*  38 */   int parameterCount = 0;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   MysqlParameterMetadata(Field[] fieldInfo, int parameterCount, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  43 */     this.metadata = new ResultSetMetaData(fieldInfo, false, exceptionInterceptor);
/*     */ 
/*  45 */     this.parameterCount = parameterCount;
/*  46 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   MysqlParameterMetadata(int count)
/*     */   {
/*  56 */     this.parameterCount = count;
/*  57 */     this.returnSimpleMetadata = true;
/*     */   }
/*     */ 
/*     */   public int getParameterCount() throws SQLException {
/*  61 */     return this.parameterCount;
/*     */   }
/*     */ 
/*     */   public int isNullable(int arg0) throws SQLException {
/*  65 */     checkAvailable();
/*     */ 
/*  67 */     return this.metadata.isNullable(arg0);
/*     */   }
/*     */ 
/*     */   private void checkAvailable() throws SQLException {
/*  71 */     if ((this.metadata == null) || (this.metadata.fields == null))
/*  72 */       throw SQLError.createSQLException("Parameter metadata not available for the given statement", "S1C00", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public boolean isSigned(int arg0)
/*     */     throws SQLException
/*     */   {
/*  79 */     if (this.returnSimpleMetadata) {
/*  80 */       checkBounds(arg0);
/*     */ 
/*  82 */       return false;
/*     */     }
/*     */ 
/*  85 */     checkAvailable();
/*     */ 
/*  87 */     return this.metadata.isSigned(arg0);
/*     */   }
/*     */ 
/*     */   public int getPrecision(int arg0) throws SQLException {
/*  91 */     if (this.returnSimpleMetadata) {
/*  92 */       checkBounds(arg0);
/*     */ 
/*  94 */       return 0;
/*     */     }
/*     */ 
/*  97 */     checkAvailable();
/*     */ 
/*  99 */     return this.metadata.getPrecision(arg0);
/*     */   }
/*     */ 
/*     */   public int getScale(int arg0) throws SQLException {
/* 103 */     if (this.returnSimpleMetadata) {
/* 104 */       checkBounds(arg0);
/*     */ 
/* 106 */       return 0;
/*     */     }
/*     */ 
/* 109 */     checkAvailable();
/*     */ 
/* 111 */     return this.metadata.getScale(arg0);
/*     */   }
/*     */ 
/*     */   public int getParameterType(int arg0) throws SQLException {
/* 115 */     if (this.returnSimpleMetadata) {
/* 116 */       checkBounds(arg0);
/*     */ 
/* 118 */       return 12;
/*     */     }
/*     */ 
/* 121 */     checkAvailable();
/*     */ 
/* 123 */     return this.metadata.getColumnType(arg0);
/*     */   }
/*     */ 
/*     */   public String getParameterTypeName(int arg0) throws SQLException {
/* 127 */     if (this.returnSimpleMetadata) {
/* 128 */       checkBounds(arg0);
/*     */ 
/* 130 */       return "VARCHAR";
/*     */     }
/*     */ 
/* 133 */     checkAvailable();
/*     */ 
/* 135 */     return this.metadata.getColumnTypeName(arg0);
/*     */   }
/*     */ 
/*     */   public String getParameterClassName(int arg0) throws SQLException {
/* 139 */     if (this.returnSimpleMetadata) {
/* 140 */       checkBounds(arg0);
/*     */ 
/* 142 */       return "java.lang.String";
/*     */     }
/*     */ 
/* 145 */     checkAvailable();
/*     */ 
/* 147 */     return this.metadata.getColumnClassName(arg0);
/*     */   }
/*     */ 
/*     */   public int getParameterMode(int arg0) throws SQLException {
/* 151 */     return 1;
/*     */   }
/*     */ 
/*     */   private void checkBounds(int paramNumber) throws SQLException {
/* 155 */     if (paramNumber < 1) {
/* 156 */       throw SQLError.createSQLException("Parameter index of '" + paramNumber + "' is invalid.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 161 */     if (paramNumber > this.parameterCount)
/* 162 */       throw SQLError.createSQLException("Parameter index of '" + paramNumber + "' is greater than number of parameters, which is '" + this.parameterCount + "'.", "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class iface)
/*     */     throws SQLException
/*     */   {
/* 190 */     return iface.isInstance(this);
/*     */   }
/*     */ 
/*     */   public Object unwrap(Class iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 211 */       return Util.cast(iface, this); } catch (ClassCastException cce) {
/*     */     }
/* 213 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.MysqlParameterMetadata
 * JD-Core Version:    0.6.0
 */