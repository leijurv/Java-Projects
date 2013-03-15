/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.Reader;
/*    */ import java.sql.NClob;
/*    */ import java.sql.RowId;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLXML;
/*    */ 
/*    */ public class JDBC4PreparedStatementHelper
/*    */ {
/*    */   static void setRowId(PreparedStatement pstmt, int parameterIndex, RowId x)
/*    */     throws SQLException
/*    */   {
/* 19 */     throw SQLError.notImplemented();
/*    */   }
/*    */ 
/*    */   static void setNClob(PreparedStatement pstmt, int parameterIndex, NClob value)
/*    */     throws SQLException
/*    */   {
/* 35 */     if (value == null)
/* 36 */       pstmt.setNull(parameterIndex, 2011);
/*    */     else
/* 38 */       pstmt.setNCharacterStream(parameterIndex, value.getCharacterStream(), value.length());
/*    */   }
/*    */ 
/*    */   static void setNClob(PreparedStatement pstmt, int parameterIndex, Reader reader) throws SQLException
/*    */   {
/* 43 */     pstmt.setNCharacterStream(parameterIndex, reader);
/*    */   }
/*    */ 
/*    */   static void setNClob(PreparedStatement pstmt, int parameterIndex, Reader reader, long length)
/*    */     throws SQLException
/*    */   {
/* 61 */     if (reader == null)
/* 62 */       pstmt.setNull(parameterIndex, 2011);
/*    */     else
/* 64 */       pstmt.setNCharacterStream(parameterIndex, reader, length);
/*    */   }
/*    */ 
/*    */   static void setSQLXML(PreparedStatement pstmt, int parameterIndex, SQLXML xmlObject)
/*    */     throws SQLException
/*    */   {
/* 70 */     if (xmlObject == null) {
/* 71 */       pstmt.setNull(parameterIndex, 2009);
/*    */     }
/*    */     else
/* 74 */       pstmt.setCharacterStream(parameterIndex, ((JDBC4MysqlSQLXML)xmlObject).serializeAsCharacterStream());
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4PreparedStatementHelper
 * JD-Core Version:    0.6.0
 */