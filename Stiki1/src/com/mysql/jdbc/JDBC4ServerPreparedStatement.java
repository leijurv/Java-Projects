/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import java.sql.NClob;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ 
/*     */ public class JDBC4ServerPreparedStatement extends ServerPreparedStatement
/*     */ {
/*     */   public JDBC4ServerPreparedStatement(ConnectionImpl conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/*  46 */     super(conn, sql, catalog, resultSetType, resultSetConcurrency);
/*     */   }
/*     */ 
/*     */   public void setNCharacterStream(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/*  57 */     if ((!this.charEncoding.equalsIgnoreCase("UTF-8")) && (!this.charEncoding.equalsIgnoreCase("utf8")))
/*     */     {
/*  59 */       throw SQLError.createSQLException("Can not call setNCharacterStream() when connection character set isn't UTF-8", getExceptionInterceptor());
/*     */     }
/*     */ 
/*  63 */     checkClosed();
/*     */ 
/*  65 */     if (reader == null) {
/*  66 */       setNull(parameterIndex, -2);
/*     */     } else {
/*  68 */       ServerPreparedStatement.BindValue binding = getBinding(parameterIndex, true);
/*  69 */       setType(binding, 252);
/*     */ 
/*  71 */       binding.value = reader;
/*  72 */       binding.isNull = false;
/*  73 */       binding.isLongData = true;
/*     */ 
/*  75 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/*  76 */         binding.bindLength = length;
/*     */       else
/*  78 */         binding.bindLength = -1L;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNClob(int parameterIndex, NClob x)
/*     */     throws SQLException
/*     */   {
/*  87 */     setNClob(parameterIndex, x.getCharacterStream(), this.connection.getUseStreamLengthsInPrepStmts() ? x.length() : -1L);
/*     */   }
/*     */ 
/*     */   public void setNClob(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/* 107 */     if ((!this.charEncoding.equalsIgnoreCase("UTF-8")) && (!this.charEncoding.equalsIgnoreCase("utf8")))
/*     */     {
/* 109 */       throw SQLError.createSQLException("Can not call setNClob() when connection character set isn't UTF-8", getExceptionInterceptor());
/*     */     }
/*     */ 
/* 113 */     checkClosed();
/*     */ 
/* 115 */     if (reader == null) {
/* 116 */       setNull(parameterIndex, 2011);
/*     */     } else {
/* 118 */       ServerPreparedStatement.BindValue binding = getBinding(parameterIndex, true);
/* 119 */       setType(binding, 252);
/*     */ 
/* 121 */       binding.value = reader;
/* 122 */       binding.isNull = false;
/* 123 */       binding.isLongData = true;
/*     */ 
/* 125 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 126 */         binding.bindLength = length;
/*     */       else
/* 128 */         binding.bindLength = -1L;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNString(int parameterIndex, String x)
/*     */     throws SQLException
/*     */   {
/* 137 */     if ((this.charEncoding.equalsIgnoreCase("UTF-8")) || (this.charEncoding.equalsIgnoreCase("utf8")))
/*     */     {
/* 139 */       setString(parameterIndex, x);
/*     */     }
/* 141 */     else throw SQLError.createSQLException("Can not call setNString() when connection character set isn't UTF-8", getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public void setRowId(int parameterIndex, RowId x)
/*     */     throws SQLException
/*     */   {
/* 147 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*     */   }
/*     */ 
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
/*     */   {
/* 152 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4ServerPreparedStatement
 * JD-Core Version:    0.6.0
 */