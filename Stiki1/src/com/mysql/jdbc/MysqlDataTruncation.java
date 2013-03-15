/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.DataTruncation;
/*    */ 
/*    */ public class MysqlDataTruncation extends DataTruncation
/*    */ {
/*    */   private String message;
/*    */   private int vendorErrorCode;
/*    */ 
/*    */   public MysqlDataTruncation(String message, int index, boolean parameter, boolean read, int dataSize, int transferSize, int vendorErrorCode)
/*    */   {
/* 65 */     super(index, parameter, read, dataSize, transferSize);
/*    */ 
/* 67 */     this.message = message;
/* 68 */     this.vendorErrorCode = vendorErrorCode;
/*    */   }
/*    */ 
/*    */   public int getErrorCode() {
/* 72 */     return this.vendorErrorCode;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 81 */     return super.getMessage() + ": " + this.message;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.MysqlDataTruncation
 * JD-Core Version:    0.6.0
 */