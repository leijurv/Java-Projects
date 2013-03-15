/*     */ package gui_edit_queue;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import core_objects.stiki_utils;
/*     */ import db_client.client_interface;
/*     */ import db_client.qmanager_client;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import mediawiki_api.api_retrieve;
/*     */ 
/*     */ public class edit_queue_maintain
/*     */   implements Runnable
/*     */ {
/*     */   public static final int SECS_REFRESH_CURRENCY = 10;
/*     */   private LinkedBlockingQueue<gui_display_pkg> rid_queue_cache;
/*     */   private SortedSet<Long> inactive_rids;
/*     */   private client_interface client_iface;
/*     */   private long ts_last_check;
/*     */ 
/*     */   public edit_queue_maintain(LinkedBlockingQueue<gui_display_pkg> paramLinkedBlockingQueue, SortedSet<Long> paramSortedSet, client_interface paramclient_interface)
/*     */   {
/*  78 */     this.rid_queue_cache = paramLinkedBlockingQueue;
/*  79 */     this.inactive_rids = paramSortedSet;
/*  80 */     this.client_iface = paramclient_interface;
/*  81 */     this.ts_last_check = stiki_utils.cur_unix_time();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     while (true)
/*     */     {
/* 103 */       if (stiki_utils.cur_unix_time() >= this.ts_last_check + 10L)
/*     */       {
/*     */         try {
/* 106 */           HashMap localHashMap = new HashMap();
/* 107 */           Iterator localIterator1 = this.rid_queue_cache.iterator();
/* 108 */           while (localIterator1.hasNext()) {
/* 109 */             gui_display_pkg localgui_display_pkg = (gui_display_pkg)localIterator1.next();
/* 110 */             localHashMap.put(Long.valueOf(localgui_display_pkg.metadata.pid), Long.valueOf(localgui_display_pkg.metadata.rid));
/*     */           }
/*     */ 
/* 115 */           Map localMap = api_retrieve.process_latest_page(localHashMap.keySet());
/*     */ 
/* 117 */           this.ts_last_check = stiki_utils.cur_unix_time();
/*     */ 
/* 120 */           ArrayList localArrayList = new ArrayList();
/* 121 */           Iterator localIterator2 = localHashMap.keySet().iterator();
/* 122 */           while (localIterator2.hasNext()) {
/* 123 */             long l1 = ((Long)localIterator2.next()).longValue();
/* 124 */             long l2 = ((Long)localHashMap.get(Long.valueOf(l1))).longValue();
/* 125 */             if ((l2 == ((Long)localMap.get(Long.valueOf(l1))).longValue()) || (this.inactive_rids.contains(Long.valueOf(l2)))) {
/*     */               continue;
/*     */             }
/* 128 */             this.inactive_rids.add(Long.valueOf(l2));
/* 129 */             localArrayList.add(Long.valueOf(l2));
/*     */           }
/*     */ 
/* 135 */           for (int i = 0; i < localArrayList.size(); i++)
/* 136 */             this.client_iface.queues.queue_delete(((Long)localArrayList.get(i)).longValue()); 
/*     */         } catch (Exception localException1) {
/*     */         }
/* 138 */         continue;
/*     */       }
/*     */       try
/*     */       {
/* 142 */         Thread.sleep(1000L);
/*     */       }
/*     */       catch (Exception localException2)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean active(long paramLong, boolean paramBoolean)
/*     */   {
/* 166 */     if (paramBoolean)
/* 167 */       return !this.inactive_rids.remove(Long.valueOf(paramLong));
/* 168 */     return !this.inactive_rids.contains(Long.valueOf(paramLong));
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_edit_queue.edit_queue_maintain
 * JD-Core Version:    0.6.0
 */