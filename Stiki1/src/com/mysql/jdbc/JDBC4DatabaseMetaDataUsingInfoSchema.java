/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.RowIdLifetime;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class JDBC4DatabaseMetaDataUsingInfoSchema extends DatabaseMetaDataUsingInfoSchema
/*    */ {
/*    */   public JDBC4DatabaseMetaDataUsingInfoSchema(ConnectionImpl connToSet, String databaseToSet)
/*    */     throws SQLException
/*    */   {
/* 39 */     super(connToSet, databaseToSet);
/*    */   }
/*    */ 
/*    */   public RowIdLifetime getRowIdLifetime() throws SQLException {
/* 43 */     return RowIdLifetime.ROWID_UNSUPPORTED;
/*    */   }
/*    */ 
/*    */   public boolean isWrapperFor(Class<?> iface)
/*    */     throws SQLException
/*    */   {
/* 64 */     return iface.isInstance(this);
/*    */   }
/*    */ 
/*    */   public <T> T unwrap(Class<T> iface)
/*    */     throws SQLException
/*    */   {
/*    */     try
/*    */     {
/* 85 */       return iface.cast(this); } catch (ClassCastException cce) {
/*    */     }
/* 87 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.conn.getExceptionInterceptor());
/*    */   }
/*    */ 
/*    */   protected int getJDBC4FunctionNoTableConstant()
/*    */   {
/* 93 */     return 1;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema
 * JD-Core Version:    0.6.0
 */