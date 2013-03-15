/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.ResultSetMetaData;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class ResultSetUtil
/*    */ {
/*    */   public static StringBuffer appendResultSetSlashGStyle(StringBuffer appendTo, ResultSet rs)
/*    */     throws SQLException
/*    */   {
/* 45 */     ResultSetMetaData rsmd = rs.getMetaData();
/*    */ 
/* 47 */     int numFields = rsmd.getColumnCount();
/* 48 */     int maxWidth = 0;
/*    */ 
/* 50 */     String[] fieldNames = new String[numFields];
/*    */ 
/* 52 */     for (int i = 0; i < numFields; i++) {
/* 53 */       fieldNames[i] = rsmd.getColumnLabel(i + 1);
/*    */ 
/* 55 */       if (fieldNames[i].length() > maxWidth) {
/* 56 */         maxWidth = fieldNames[i].length();
/*    */       }
/*    */     }
/*    */ 
/* 60 */     int rowCount = 1;
/*    */ 
/* 62 */     while (rs.next()) {
/* 63 */       appendTo.append("*************************** ");
/* 64 */       appendTo.append(rowCount++);
/* 65 */       appendTo.append(". row ***************************\n");
/*    */ 
/* 67 */       for (int i = 0; i < numFields; i++) {
/* 68 */         int leftPad = maxWidth - fieldNames[i].length();
/*    */ 
/* 70 */         for (int j = 0; j < leftPad; j++) {
/* 71 */           appendTo.append(" ");
/*    */         }
/*    */ 
/* 74 */         appendTo.append(fieldNames[i]);
/* 75 */         appendTo.append(": ");
/*    */ 
/* 77 */         String stringVal = rs.getString(i + 1);
/*    */ 
/* 79 */         if (stringVal != null)
/* 80 */           appendTo.append(stringVal);
/*    */         else {
/* 82 */           appendTo.append("NULL");
/*    */         }
/*    */ 
/* 85 */         appendTo.append("\n");
/*    */       }
/*    */ 
/* 88 */       appendTo.append("\n");
/*    */     }
/*    */ 
/* 91 */     return appendTo;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.util.ResultSetUtil
 * JD-Core Version:    0.6.0
 */