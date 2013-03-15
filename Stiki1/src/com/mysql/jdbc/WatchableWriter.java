/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.CharArrayWriter;
/*    */ 
/*    */ class WatchableWriter extends CharArrayWriter
/*    */ {
/*    */   private WriterWatcher watcher;
/*    */ 
/*    */   public void close()
/*    */   {
/* 50 */     super.close();
/*    */ 
/* 53 */     if (this.watcher != null)
/* 54 */       this.watcher.writerClosed(this);
/*    */   }
/*    */ 
/*    */   public void setWatcher(WriterWatcher watcher)
/*    */   {
/* 65 */     this.watcher = watcher;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.WatchableWriter
 * JD-Core Version:    0.6.0
 */