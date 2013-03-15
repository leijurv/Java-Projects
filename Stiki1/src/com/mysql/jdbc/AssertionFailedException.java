/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ public class AssertionFailedException extends RuntimeException
/*    */ {
/*    */   public static void shouldNotHappen(Exception ex)
/*    */     throws AssertionFailedException
/*    */   {
/* 52 */     throw new AssertionFailedException(ex);
/*    */   }
/*    */ 
/*    */   public AssertionFailedException(Exception ex)
/*    */   {
/* 66 */     super(Messages.getString("AssertionFailedException.0") + ex.toString() + Messages.getString("AssertionFailedException.1"));
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.AssertionFailedException
 * JD-Core Version:    0.6.0
 */