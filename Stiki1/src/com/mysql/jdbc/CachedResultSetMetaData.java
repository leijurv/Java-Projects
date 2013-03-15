/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.ResultSetMetaData;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CachedResultSetMetaData
/*    */ {
/* 34 */   Map columnNameToIndex = null;
/*    */   Field[] fields;
/* 40 */   Map fullColumnNameToIndex = null;
/*    */   ResultSetMetaData metadata;
/*    */ 
/*    */   public Map getColumnNameToIndex()
/*    */   {
/* 46 */     return this.columnNameToIndex;
/*    */   }
/*    */ 
/*    */   public Field[] getFields() {
/* 50 */     return this.fields;
/*    */   }
/*    */ 
/*    */   public Map getFullColumnNameToIndex() {
/* 54 */     return this.fullColumnNameToIndex;
/*    */   }
/*    */ 
/*    */   public ResultSetMetaData getMetadata() {
/* 58 */     return this.metadata;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.CachedResultSetMetaData
 * JD-Core Version:    0.6.0
 */