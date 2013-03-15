/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.NClob;
/*    */ import java.sql.RowId;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLXML;
/*    */ 
/*    */ public class JDBC4PreparedStatement extends PreparedStatement
/*    */ {
/*    */   public JDBC4PreparedStatement(ConnectionImpl conn, String catalog)
/*    */     throws SQLException
/*    */   {
/* 45 */     super(conn, catalog);
/*    */   }
/*    */ 
/*    */   public JDBC4PreparedStatement(ConnectionImpl conn, String sql, String catalog) throws SQLException
/*    */   {
/* 50 */     super(conn, sql, catalog);
/*    */   }
/*    */ 
/*    */   public JDBC4PreparedStatement(ConnectionImpl conn, String sql, String catalog, PreparedStatement.ParseInfo cachedParseInfo) throws SQLException
/*    */   {
/* 55 */     super(conn, sql, catalog, cachedParseInfo);
/*    */   }
/*    */ 
/*    */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/* 59 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*    */   }
/*    */ 
/*    */   public void setNClob(int parameterIndex, NClob value)
/*    */     throws SQLException
/*    */   {
/* 74 */     JDBC4PreparedStatementHelper.setNClob(this, parameterIndex, value);
/*    */   }
/*    */ 
/*    */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
/*    */   {
/* 79 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4PreparedStatement
 * JD-Core Version:    0.6.0
 */