/*     */ package gui_edit_queue;
/*     */ 
/*     */ import core_objects.pair;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import db_client.client_interface;
/*     */ import db_client.qmanager_client;
/*     */ import executables.stiki_frontend_driver;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ 
/*     */ public class edit_queue_filler
/*     */   implements Runnable
/*     */ {
/*     */   public static final int MIN_QUEUE_SIZE = 10;
/*     */   public static final int RES_HIST_SIZE = 3;
/*     */   private stiki_frontend_driver parent;
/*     */   private LinkedBlockingQueue<gui_display_pkg> rid_queue_cache;
/*     */   private ExecutorService threads;
/*     */   private Queue<pair<Long, Long>> server_q;
/*     */   private String stiki_user;
/*     */   private String session_cookie;
/*     */   private boolean using_native_rb;
/*     */   private stiki_utils.SCORE_SYS queue_in_use;
/*     */   private List<Future<?>> futures;
/*     */   private List<Long> resid_history;
/*     */ 
/*     */   public edit_queue_filler(stiki_frontend_driver paramstiki_frontend_driver, LinkedBlockingQueue<gui_display_pkg> paramLinkedBlockingQueue, stiki_utils.SCORE_SYS paramSCORE_SYS, ExecutorService paramExecutorService)
/*     */   {
/* 121 */     this.parent = paramstiki_frontend_driver;
/* 122 */     this.rid_queue_cache = paramLinkedBlockingQueue;
/* 123 */     this.threads = paramExecutorService;
/* 124 */     this.futures = new LinkedList();
/* 125 */     this.resid_history = new LinkedList();
/*     */ 
/* 128 */     this.stiki_user = "";
/* 129 */     this.session_cookie = "";
/* 130 */     this.using_native_rb = false;
/* 131 */     this.queue_in_use = paramSCORE_SYS;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*     */       while (true)
/*     */       {
/* 146 */         if (this.futures.size() + this.rid_queue_cache.size() < 10)
/*     */         {
/* 148 */           while ((this.server_q == null) || (this.server_q.isEmpty())) {
/* 149 */             long l = Math.abs(new Random().nextInt());
/* 150 */             this.server_q = this.parent.client_interface.queues.queue_fetch(this.queue_in_use, this.stiki_user, l);
/*     */ 
/* 152 */             add_resid_to_hist(l);
/*     */           }
/*     */ 
/* 157 */           pair localpair = (pair)this.server_q.poll();
/* 158 */           this.futures.add(this.threads.submit(new edit_queue_fetcher(this.parent, ((Long)localpair.fst).longValue(), ((Long)localpair.snd).longValue(), this.session_cookie, this.using_native_rb, this.queue_in_use, this.rid_queue_cache)));
/*     */         }
/*     */         else
/*     */         {
/* 163 */           Thread.sleep(10L);
/*     */         }
/*     */ 
/* 167 */         this.futures = remove_done(this.futures);
/*     */       }
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void new_user_settings(String paramString1, String paramString2, boolean paramBoolean, stiki_utils.SCORE_SYS paramSCORE_SYS)
/*     */   {
/* 192 */     this.stiki_user = paramString1;
/* 193 */     this.session_cookie = paramString2;
/* 194 */     this.using_native_rb = paramBoolean;
/* 195 */     this.queue_in_use = paramSCORE_SYS;
/*     */ 
/* 199 */     wipe_recent_res();
/*     */ 
/* 205 */     this.server_q.clear();
/* 206 */     cancel_all(this.futures);
/* 207 */     this.rid_queue_cache.clear();
/* 208 */     while (this.rid_queue_cache.poll() == null);
/* 209 */     this.rid_queue_cache.poll();
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 218 */     wipe_recent_res();
/*     */   }
/*     */ 
/*     */   private void wipe_recent_res()
/*     */   {
/*     */     try
/*     */     {
/* 229 */       for (int i = 0; i < this.resid_history.size(); i++)
/* 230 */         this.parent.client_interface.queues.queue_wipe(((Long)this.resid_history.get(i)).longValue());
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void add_resid_to_hist(long paramLong)
/*     */   {
/* 240 */     if (this.resid_history.size() != 3) {
/* 241 */       this.resid_history.add(0, Long.valueOf(paramLong));
/*     */     } else {
/* 243 */       this.resid_history.remove(2);
/* 244 */       this.resid_history.add(0, Long.valueOf(paramLong));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized List<Future<?>> remove_done(List<Future<?>> paramList)
/*     */   {
/* 256 */     LinkedList localLinkedList = new LinkedList();
/* 257 */     for (int i = 0; i < paramList.size(); i++) {
/* 258 */       if (!((Future)paramList.get(i)).isDone())
/* 259 */         localLinkedList.add(paramList.get(i));
/*     */     }
/* 261 */     return localLinkedList;
/*     */   }
/*     */ 
/*     */   private static synchronized void cancel_all(List<Future<?>> paramList)
/*     */   {
/* 270 */     for (int i = 0; i < paramList.size(); i++)
/* 271 */       ((Future)paramList.get(i)).cancel(true);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_edit_queue.edit_queue_filler
 * JD-Core Version:    0.6.0
 */