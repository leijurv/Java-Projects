/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ class WatchableOutputStream extends ByteArrayOutputStream
/*    */ {
/*    */   private OutputStreamWatcher watcher;
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 51 */     super.close();
/*    */ 
/* 53 */     if (this.watcher != null)
/* 54 */       this.watcher.streamClosed(this);
/*    */   }
/*    */ 
/*    */   public void setWatcher(OutputStreamWatcher watcher)
/*    */   {
/* 65 */     this.watcher = watcher;
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.WatchableOutputStream
 * JD-Core Version:    0.6.0
 */