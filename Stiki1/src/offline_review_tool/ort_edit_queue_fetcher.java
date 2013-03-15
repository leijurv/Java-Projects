/*    */ package offline_review_tool;
/*    */ 
/*    */ import gui_edit_queue.gui_display_pkg;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ 
/*    */ public class ort_edit_queue_fetcher
/*    */   implements Runnable
/*    */ {
/*    */   private long rid;
/*    */   private LinkedBlockingQueue<gui_display_pkg> shared_queue;
/*    */ 
/*    */   public ort_edit_queue_fetcher(long paramLong, LinkedBlockingQueue<gui_display_pkg> paramLinkedBlockingQueue)
/*    */   {
/* 41 */     this.rid = paramLong;
/* 42 */     this.shared_queue = paramLinkedBlockingQueue;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/* 56 */       gui_display_pkg localgui_display_pkg = gui_display_pkg.create_offline(this.rid);
/*    */ 
/* 59 */       if (localgui_display_pkg != null)
/* 60 */         this.shared_queue.offer(localgui_display_pkg);
/*    */     }
/*    */     catch (Exception localException)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     offline_review_tool.ort_edit_queue_fetcher
 * JD-Core Version:    0.6.0
 */