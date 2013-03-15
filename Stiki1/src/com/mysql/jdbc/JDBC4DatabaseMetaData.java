/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.RowIdLifetime;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class JDBC4DatabaseMetaData extends DatabaseMetaData
/*     */ {
/*     */   public JDBC4DatabaseMetaData(ConnectionImpl connToSet, String databaseToSet)
/*     */   {
/*  41 */     super(connToSet, databaseToSet);
/*     */   }
/*     */ 
/*     */   public RowIdLifetime getRowIdLifetime() throws SQLException {
/*  45 */     return RowIdLifetime.ROWID_UNSUPPORTED;
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/*  66 */     return iface.isInstance(this);
/*     */   }
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  87 */       return iface.cast(this); } catch (ClassCastException cce) {
/*     */     }
/*  89 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.conn.getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public ResultSet getClientInfoProperties()
/*     */     throws SQLException
/*     */   {
/* 121 */     Field[] fields = new Field[4];
/* 122 */     fields[0] = new Field("", "NAME", 12, 255);
/* 123 */     fields[1] = new Field("", "MAX_LEN", 4, 10);
/* 124 */     fields[2] = new Field("", "DEFAULT_VALUE", 12, 255);
/* 125 */     fields[3] = new Field("", "DESCRIPTION", 12, 255);
/*     */ 
/* 127 */     ArrayList tuples = new ArrayList();
/*     */ 
/* 129 */     return buildResultSet(fields, tuples, this.conn);
/*     */   }
/*     */ 
/*     */   public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
/*     */     throws SQLException
/*     */   {
/* 186 */     Field[] fields = new Field[6];
/*     */ 
/* 188 */     fields[0] = new Field("", "FUNCTION_CAT", 1, 255);
/* 189 */     fields[1] = new Field("", "FUNCTION_SCHEM", 1, 255);
/* 190 */     fields[2] = new Field("", "FUNCTION_NAME", 1, 255);
/* 191 */     fields[3] = new Field("", "REMARKS", 1, 255);
/* 192 */     fields[4] = new Field("", "FUNCTION_TYPE", 5, 6);
/* 193 */     fields[5] = new Field("", "SPECIFIC_NAME", 1, 255);
/*     */ 
/* 195 */     return getProceduresAndOrFunctions(fields, catalog, schemaPattern, functionNamePattern, false, true);
/*     */   }
/*     */ 
/*     */   protected int getJDBC4FunctionNoTableConstant()
/*     */   {
/* 205 */     return 1;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4DatabaseMetaData
 * JD-Core Version:    0.6.0
 */