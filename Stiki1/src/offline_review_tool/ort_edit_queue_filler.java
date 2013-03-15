/*     */ package offline_review_tool;
/*     */ 
/*     */ import gui_edit_queue.gui_display_pkg;
/*     */ import java.io.BufferedReader;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ 
/*     */ public class ort_edit_queue_filler
/*     */   implements Runnable
/*     */ {
/*     */   private static final int MIN_QUEUE_SIZE = 10;
/*     */   private BufferedReader rid_file;
/*     */   private LinkedBlockingQueue<gui_display_pkg> rid_queue_cache;
/*     */   private ExecutorService threads;
/*     */   private List<Future<?>> futures;
/*     */ 
/*     */   public ort_edit_queue_filler(BufferedReader paramBufferedReader, LinkedBlockingQueue<gui_display_pkg> paramLinkedBlockingQueue, ExecutorService paramExecutorService)
/*     */   {
/*  66 */     this.rid_file = paramBufferedReader;
/*  67 */     this.rid_queue_cache = paramLinkedBlockingQueue;
/*  68 */     this.threads = paramExecutorService;
/*  69 */     this.futures = new LinkedList();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*     */       while (true)
/*     */       {
/*  81 */         if (this.futures.size() + this.rid_queue_cache.size() < 10)
/*     */         {
/*  84 */           String str = this.rid_file.readLine();
/*  85 */           if (str != null) {
/*  86 */             long l = Long.parseLong(str);
/*  87 */             this.futures.add(this.threads.submit(new ort_edit_queue_fetcher(l, this.rid_queue_cache)));
/*     */           }
/*     */           else {
/*  90 */             this.rid_queue_cache.add(gui_display_pkg.create_end_pkg());
/*     */           }
/*     */         }
/*     */         else {
/*  94 */           Thread.sleep(10L);
/*     */         }
/*     */ 
/*  98 */         this.futures = remove_done(this.futures);
/*     */       }
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */     throws Exception
/*     */   {
/* 109 */     this.rid_file.close();
/*     */   }
/*     */ 
/*     */   private static synchronized List<Future<?>> remove_done(List<Future<?>> paramList)
/*     */   {
/* 122 */     LinkedList localLinkedList = new LinkedList();
/* 123 */     for (int i = 0; i < paramList.size(); i++) {
/* 124 */       if (!((Future)paramList.get(i)).isDone())
/* 125 */         localLinkedList.add(paramList.get(i));
/*     */     }
/* 127 */     return localLinkedList;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     offline_review_tool.ort_edit_queue_filler
 * JD-Core Version:    0.6.0
 */