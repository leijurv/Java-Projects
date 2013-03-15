/*    */ package offline_review_tool;
/*    */ 
/*    */ import gui_edit_queue.gui_display_pkg;
/*    */ import java.io.BufferedReader;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ 
/*    */ public class ort_edit_queue
/*    */ {
/*    */   private LinkedBlockingQueue<gui_display_pkg> rid_queue_cache;
/*    */   private ort_edit_queue_filler queue_filler;
/*    */   private gui_display_pkg cur_edit;
/*    */ 
/*    */   public ort_edit_queue(ExecutorService paramExecutorService, BufferedReader paramBufferedReader)
/*    */     throws Exception
/*    */   {
/* 50 */     this.rid_queue_cache = new LinkedBlockingQueue();
/* 51 */     this.queue_filler = new ort_edit_queue_filler(paramBufferedReader, this.rid_queue_cache, paramExecutorService);
/*    */ 
/* 53 */     paramExecutorService.submit(this.queue_filler);
/*    */   }
/*    */ 
/*    */   public void next_rid()
/*    */   {
/* 64 */     while (this.rid_queue_cache.peek() == null) try {
/* 65 */         Thread.sleep(10L);
/*    */       }
/*    */       catch (Exception localException) {
/*    */       } this.cur_edit = ((gui_display_pkg)this.rid_queue_cache.poll());
/*    */   }
/*    */ 
/*    */   public gui_display_pkg get_cur_edit()
/*    */   {
/* 76 */     return this.cur_edit;
/*    */   }
/*    */ 
/*    */   public void shutdown()
/*    */     throws Exception
/*    */   {
/* 83 */     this.queue_filler.shutdown();
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     offline_review_tool.ort_edit_queue
 * JD-Core Version:    0.6.0
 */