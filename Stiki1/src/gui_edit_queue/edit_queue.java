/*     */ package gui_edit_queue;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import core_objects.pair;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import db_client.client_interface;
/*     */ import executables.stiki_frontend_driver;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ 
/*     */ public class edit_queue
/*     */ {
/*     */   private String stiki_user;
/*  41 */   private boolean using_native_rb = false;
/*     */   private stiki_utils.SCORE_SYS queue_in_use;
/*     */   private LinkedBlockingQueue<gui_display_pkg> rid_queue_cache;
/*     */   private edit_queue_filler queue_filler;
/*     */   private SortedSet<Long> inactive_rids;
/*     */   private edit_queue_maintain queue_maintainer;
/*     */   private gui_display_pkg cur_edit;
/*     */   private pair<Boolean, gui_display_pkg> back_helper;
/*     */   private Set<Long> edits_shown;
/*     */ 
/*     */   public edit_queue(stiki_frontend_driver paramstiki_frontend_driver, ExecutorService paramExecutorService, client_interface paramclient_interface, stiki_utils.SCORE_SYS paramSCORE_SYS)
/*     */     throws Exception
/*     */   {
/* 116 */     this.back_helper = new pair(Boolean.valueOf(false), null);
/* 117 */     this.edits_shown = new TreeSet();
/* 118 */     this.rid_queue_cache = new LinkedBlockingQueue();
/* 119 */     this.inactive_rids = Collections.synchronizedSortedSet(new TreeSet());
/*     */ 
/* 123 */     this.queue_in_use = paramSCORE_SYS;
/* 124 */     this.queue_filler = new edit_queue_filler(paramstiki_frontend_driver, this.rid_queue_cache, paramSCORE_SYS, paramExecutorService);
/*     */ 
/* 126 */     this.queue_maintainer = new edit_queue_maintain(this.rid_queue_cache, this.inactive_rids, paramclient_interface);
/*     */ 
/* 128 */     paramExecutorService.submit(this.queue_filler);
/* 129 */     paramExecutorService.submit(this.queue_maintainer);
/*     */   }
/*     */ 
/*     */   public void next_rid(String paramString1, String paramString2, boolean paramBoolean1, stiki_utils.SCORE_SYS paramSCORE_SYS, boolean paramBoolean2, boolean paramBoolean3)
/*     */   {
/* 153 */     if ((paramString1 != this.stiki_user) || (paramBoolean1 != this.using_native_rb) || (paramSCORE_SYS != this.queue_in_use))
/*     */     {
/* 156 */       this.stiki_user = paramString1;
/* 157 */       this.using_native_rb = paramBoolean1;
/* 158 */       this.queue_in_use = paramSCORE_SYS;
/* 159 */       this.queue_filler.new_user_settings(paramString1, paramString2, paramBoolean1, paramSCORE_SYS);
/*     */     }
/*     */ 
/* 164 */     if (!paramBoolean2)
/*     */     {
/* 166 */       if (((Boolean)this.back_helper.fst).booleanValue()) {
/* 167 */         this.back_helper.fst = Boolean.valueOf(false);
/* 168 */         gui_display_pkg localgui_display_pkg1 = (gui_display_pkg)this.back_helper.snd;
/* 169 */         this.back_helper.snd = this.cur_edit;
/* 170 */         this.cur_edit = localgui_display_pkg1;
/*     */       }
/*     */       else
/*     */       {
/* 174 */         while (this.rid_queue_cache.peek() == null) try {
/* 175 */             Thread.sleep(10L);
/*     */           }
/*     */           catch (Exception localException)
/*     */           {
/*     */           }
/*     */ 
/* 181 */         if (!paramBoolean3)
/* 182 */           this.back_helper.snd = this.cur_edit;
/* 183 */         this.cur_edit = ((gui_display_pkg)this.rid_queue_cache.poll());
/* 184 */         if ((this.edits_shown.contains(Long.valueOf(this.cur_edit.metadata.rid))) || (!this.queue_maintainer.active(this.cur_edit.metadata.rid, true)))
/*     */         {
/* 186 */           next_rid(paramString1, paramString2, paramBoolean1, paramSCORE_SYS, paramBoolean2, true);
/*     */         }
/* 188 */         else this.edits_shown.add(Long.valueOf(this.cur_edit.metadata.rid));
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 193 */       this.back_helper.fst = Boolean.valueOf(true);
/* 194 */       gui_display_pkg localgui_display_pkg2 = (gui_display_pkg)this.back_helper.snd;
/* 195 */       this.back_helper.snd = this.cur_edit;
/* 196 */       this.cur_edit = localgui_display_pkg2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void refresh_rb_token(String paramString)
/*     */     throws Exception
/*     */   {
/* 207 */     this.cur_edit.refresh_rb_token(paramString);
/*     */   }
/*     */ 
/*     */   public void refresh_edit_token(String paramString)
/*     */     throws Exception
/*     */   {
/* 217 */     this.cur_edit.refresh_edit_token(paramString);
/*     */   }
/*     */ 
/*     */   public gui_display_pkg get_cur_edit()
/*     */   {
/* 225 */     return this.cur_edit;
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 232 */     this.queue_filler.shutdown();
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_edit_queue.edit_queue
 * JD-Core Version:    0.6.0
 */