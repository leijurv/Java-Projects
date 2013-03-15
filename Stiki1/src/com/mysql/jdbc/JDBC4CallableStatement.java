/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import java.sql.NClob;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ 
/*     */ public class JDBC4CallableStatement extends CallableStatement
/*     */ {
/*     */   public JDBC4CallableStatement(ConnectionImpl conn, CallableStatement.CallableStatementParamInfo paramInfo)
/*     */     throws SQLException
/*     */   {
/*  44 */     super(conn, paramInfo);
/*     */   }
/*     */ 
/*     */   public JDBC4CallableStatement(ConnectionImpl conn, String sql, String catalog, boolean isFunctionCall) throws SQLException
/*     */   {
/*  49 */     super(conn, sql, catalog, isFunctionCall);
/*     */   }
/*     */ 
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException
/*     */   {
/*  54 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*     */   }
/*     */ 
/*     */   public void setRowId(String parameterName, RowId x) throws SQLException {
/*  58 */     JDBC4PreparedStatementHelper.setRowId(this, getNamedParamIndex(parameterName, false), x);
/*     */   }
/*     */ 
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject)
/*     */     throws SQLException
/*     */   {
/*  64 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*     */   }
/*     */ 
/*     */   public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException
/*     */   {
/*  69 */     JDBC4PreparedStatementHelper.setSQLXML(this, getNamedParamIndex(parameterName, false), xmlObject);
/*     */   }
/*     */ 
/*     */   public SQLXML getSQLXML(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/*  75 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */ 
/*  77 */     SQLXML retValue = ((JDBC4ResultSet)rs).getSQLXML(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/*  80 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/*  82 */     return retValue;
/*     */   }
/*     */ 
/*     */   public SQLXML getSQLXML(String parameterName) throws SQLException
/*     */   {
/*  87 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/*  92 */     SQLXML retValue = ((JDBC4ResultSet)rs).getSQLXML(fixParameterName(parameterName));
/*     */ 
/*  95 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/*  97 */     return retValue;
/*     */   }
/*     */ 
/*     */   public RowId getRowId(int parameterIndex) throws SQLException {
/* 101 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */ 
/* 103 */     RowId retValue = ((JDBC4ResultSet)rs).getRowId(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/* 106 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 108 */     return retValue;
/*     */   }
/*     */ 
/*     */   public RowId getRowId(String parameterName) throws SQLException {
/* 112 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/* 117 */     RowId retValue = ((JDBC4ResultSet)rs).getRowId(fixParameterName(parameterName));
/*     */ 
/* 120 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 122 */     return retValue;
/*     */   }
/*     */ 
/*     */   public void setNClob(int parameterIndex, NClob value)
/*     */     throws SQLException
/*     */   {
/* 137 */     JDBC4PreparedStatementHelper.setNClob(this, parameterIndex, value);
/*     */   }
/*     */ 
/*     */   public void setNClob(String parameterName, NClob value) throws SQLException {
/* 141 */     JDBC4PreparedStatementHelper.setNClob(this, getNamedParamIndex(parameterName, false), value);
/*     */   }
/*     */ 
/*     */   public void setNClob(String parameterName, Reader reader)
/*     */     throws SQLException
/*     */   {
/* 148 */     setNClob(getNamedParamIndex(parameterName, false), reader);
/*     */   }
/*     */ 
/*     */   public void setNClob(String parameterName, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/* 154 */     setNClob(getNamedParamIndex(parameterName, false), reader, length);
/*     */   }
/*     */ 
/*     */   public void setNString(String parameterName, String value)
/*     */     throws SQLException
/*     */   {
/* 160 */     setNString(getNamedParamIndex(parameterName, false), value);
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/* 167 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */ 
/* 169 */     Reader retValue = rs.getCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/* 172 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 174 */     return retValue;
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream(String parameterName)
/*     */     throws SQLException
/*     */   {
/* 181 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/* 186 */     Reader retValue = rs.getCharacterStream(fixParameterName(parameterName));
/*     */ 
/* 189 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 191 */     return retValue;
/*     */   }
/*     */ 
/*     */   public Reader getNCharacterStream(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/* 198 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */ 
/* 200 */     Reader retValue = ((JDBC4ResultSet)rs).getNCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/* 203 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 205 */     return retValue;
/*     */   }
/*     */ 
/*     */   public Reader getNCharacterStream(String parameterName)
/*     */     throws SQLException
/*     */   {
/* 212 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/* 217 */     Reader retValue = ((JDBC4ResultSet)rs).getNCharacterStream(fixParameterName(parameterName));
/*     */ 
/* 220 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 222 */     return retValue;
/*     */   }
/*     */ 
/*     */   public NClob getNClob(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/* 229 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */ 
/* 231 */     NClob retValue = ((JDBC4ResultSet)rs).getNClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/* 234 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 236 */     return retValue;
/*     */   }
/*     */ 
/*     */   public NClob getNClob(String parameterName)
/*     */     throws SQLException
/*     */   {
/* 243 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/* 248 */     NClob retValue = ((JDBC4ResultSet)rs).getNClob(fixParameterName(parameterName));
/*     */ 
/* 251 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 253 */     return retValue;
/*     */   }
/*     */ 
/*     */   public String getNString(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/* 260 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */ 
/* 262 */     String retValue = ((JDBC4ResultSet)rs).getNString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */ 
/* 265 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 267 */     return retValue;
/*     */   }
/*     */ 
/*     */   public String getNString(String parameterName)
/*     */     throws SQLException
/*     */   {
/* 274 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */ 
/* 279 */     String retValue = ((JDBC4ResultSet)rs).getNString(fixParameterName(parameterName));
/*     */ 
/* 282 */     this.outputParamWasNull = rs.wasNull();
/*     */ 
/* 284 */     return retValue;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4CallableStatement
 * JD-Core Version:    0.6.0
 */