/*    */ package gui_edit_queue;
/*    */ 
/*    */ import core_objects.stiki_utils.SCORE_SYS;
/*    */ import executables.stiki_frontend_driver;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ 
/*    */ public class edit_queue_fetcher
/*    */   implements Runnable
/*    */ {
/*    */   private stiki_frontend_driver parent;
/*    */   private long rid;
/*    */   private long pid;
/*    */   private String session_cookie;
/*    */   private boolean using_native_rb;
/*    */   private stiki_utils.SCORE_SYS source_queue;
/*    */   private LinkedBlockingQueue<gui_display_pkg> shared_queue;
/*    */ 
/*    */   public edit_queue_fetcher(stiki_frontend_driver paramstiki_frontend_driver, long paramLong1, long paramLong2, String paramString, boolean paramBoolean, stiki_utils.SCORE_SYS paramSCORE_SYS, LinkedBlockingQueue<gui_display_pkg> paramLinkedBlockingQueue)
/*    */   {
/* 68 */     this.parent = paramstiki_frontend_driver;
/* 69 */     this.rid = paramLong1;
/* 70 */     this.pid = paramLong2;
/* 71 */     this.session_cookie = paramString;
/* 72 */     this.using_native_rb = paramBoolean;
/* 73 */     this.source_queue = paramSCORE_SYS;
/* 74 */     this.shared_queue = paramLinkedBlockingQueue;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/* 89 */       gui_display_pkg localgui_display_pkg = gui_display_pkg.create_if_most_recent(this.parent, this.rid, this.pid, this.session_cookie, this.using_native_rb, this.source_queue);
/*    */ 
/* 95 */       if (localgui_display_pkg != null)
/* 96 */         this.shared_queue.offer(localgui_display_pkg);
/*    */     }
/*    */     catch (Exception localException)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_edit_queue.edit_queue_fetcher
 * JD-Core Version:    0.6.0
 */