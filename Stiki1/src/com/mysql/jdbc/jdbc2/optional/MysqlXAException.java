/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import javax.transaction.xa.XAException;
/*    */ 
/*    */ class MysqlXAException extends XAException
/*    */ {
/*    */   private static final long serialVersionUID = -9075817535836563004L;
/*    */   private String message;
/*    */   private String xidAsString;
/*    */ 
/*    */   public MysqlXAException(int errorCode, String message, String xidAsString)
/*    */   {
/* 42 */     super(errorCode);
/* 43 */     this.message = message;
/* 44 */     this.xidAsString = xidAsString;
/*    */   }
/*    */ 
/*    */   public MysqlXAException(String message, String xidAsString)
/*    */   {
/* 50 */     this.message = message;
/* 51 */     this.xidAsString = xidAsString;
/*    */   }
/*    */ 
/*    */   public String getMessage() {
/* 55 */     String superMessage = super.getMessage();
/* 56 */     StringBuffer returnedMessage = new StringBuffer();
/*    */ 
/* 58 */     if (superMessage != null) {
/* 59 */       returnedMessage.append(superMessage);
/* 60 */       returnedMessage.append(":");
/*    */     }
/*    */ 
/* 63 */     if (this.message != null) {
/* 64 */       returnedMessage.append(this.message);
/*    */     }
/*    */ 
/* 67 */     return returnedMessage.toString();
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlXAException
 * JD-Core Version:    0.6.0
 */