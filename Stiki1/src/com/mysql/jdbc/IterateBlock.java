/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ public abstract class IterateBlock
/*    */ {
/*    */   DatabaseMetaData.IteratorWithCleanup iteratorWithCleanup;
/*    */   Iterator javaIterator;
/* 37 */   boolean stopIterating = false;
/*    */ 
/*    */   IterateBlock(DatabaseMetaData.IteratorWithCleanup i) {
/* 40 */     this.iteratorWithCleanup = i;
/* 41 */     this.javaIterator = null;
/*    */   }
/*    */ 
/*    */   IterateBlock(Iterator i) {
/* 45 */     this.javaIterator = i;
/* 46 */     this.iteratorWithCleanup = null;
/*    */   }
/*    */ 
/*    */   public void doForAll() throws SQLException {
/* 50 */     if (this.iteratorWithCleanup != null) {
/*    */       try {
/* 52 */         while (this.iteratorWithCleanup.hasNext()) {
/* 53 */           forEach(this.iteratorWithCleanup.next());
/*    */ 
/* 55 */           if (this.stopIterating)
/* 56 */             break;
/*    */         }
/*    */       }
/*    */       finally {
/* 60 */         this.iteratorWithCleanup.close();
/*    */       }
/*    */     }
/* 63 */     while (this.javaIterator.hasNext()) {
/* 64 */       forEach(this.javaIterator.next());
/*    */ 
/* 66 */       if (this.stopIterating)
/* 67 */         break;
/*    */     }
/*    */   }
/*    */ 
/*    */   abstract void forEach(Object paramObject) throws SQLException;
/*    */ 
/*    */   public final boolean fullIteration()
/*    */   {
/* 76 */     return !this.stopIterating;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.IterateBlock
 * JD-Core Version:    0.6.0
 */