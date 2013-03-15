/*     */ package gui_support;
/*     */ 
/*     */ import com.mysql.jdbc.CommunicationsException;
/*     */ import core_objects.metadata;
/*     */ import core_objects.stiki_utils;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import db_client.client_interface;
/*     */ import db_client.qmanager_client;
/*     */ import edit_processing.rollback_handler.RB_TYPE;
/*     */ import executables.stiki_frontend_driver;
/*     */ import executables.stiki_frontend_driver.FB_TYPE;
/*     */ import gui_edit_queue.gui_display_pkg;
/*     */ import gui_panels.gui_revert_panel;
/*     */ import java.io.PrintStream;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ 
/*     */ public class gui_fb_handler
/*     */   implements Runnable
/*     */ {
/*     */   private stiki_frontend_driver parent;
/*     */   private stiki_frontend_driver.FB_TYPE fb;
/*     */   private metadata md;
/*     */   private String user;
/*     */   private gui_display_pkg edit_pkg;
/*     */   private String summary;
/*     */   private String session_cookie;
/*     */   private boolean user_has_native_rb;
/*     */   private boolean rollback;
/*     */   private boolean no_watchlist;
/*     */   private boolean warn;
/*     */   private gui_revert_panel gui_revert_panel;
/*     */   private ExecutorService threads;
/*     */ 
/*     */   public gui_fb_handler(stiki_frontend_driver paramstiki_frontend_driver, stiki_frontend_driver.FB_TYPE paramFB_TYPE, gui_display_pkg paramgui_display_pkg, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, gui_revert_panel paramgui_revert_panel, ExecutorService paramExecutorService)
/*     */   {
/* 118 */     this.parent = paramstiki_frontend_driver;
/* 119 */     this.fb = paramFB_TYPE;
/* 120 */     this.edit_pkg = paramgui_display_pkg;
/* 121 */     this.md = ((metadata)paramgui_display_pkg.page_hist.get(0));
/* 122 */     this.user = paramString1;
/* 123 */     this.summary = paramString2;
/* 124 */     this.session_cookie = paramString3;
/* 125 */     this.user_has_native_rb = paramBoolean1;
/* 126 */     this.rollback = paramBoolean2;
/* 127 */     this.no_watchlist = paramBoolean3;
/* 128 */     this.warn = paramBoolean4;
/* 129 */     this.gui_revert_panel = paramgui_revert_panel;
/* 130 */     this.threads = paramExecutorService;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 140 */       if (this.fb.equals(stiki_frontend_driver.FB_TYPE.INNOCENT))
/* 141 */         submit_innocent();
/* 142 */       else if (this.fb.equals(stiki_frontend_driver.FB_TYPE.PASS))
/* 143 */         submit_pass();
/* 144 */       else if (this.fb.equals(stiki_frontend_driver.FB_TYPE.AGF))
/* 145 */         submit_agf();
/* 146 */       else if (this.fb.equals(stiki_frontend_driver.FB_TYPE.GUILTY))
/* 147 */         submit_vandalism();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void submit_innocent() throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 158 */       this.parent.client_interface.feedback_insert(this.md.rid, fb_constant(this.fb, this.edit_pkg.source_queue), this.user);
/*     */     }
/*     */     catch (CommunicationsException localCommunicationsException) {
/* 161 */       this.parent.reset_connection(false);
/* 162 */       submit_innocent();
/*     */     } catch (Exception localException) {
/* 164 */       System.out.println("Error internal to \"innocent\" handler");
/* 165 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void submit_pass()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 174 */       this.parent.client_interface.queues.queue_ignore(this.md.rid, this.user);
/*     */     } catch (CommunicationsException localCommunicationsException) {
/* 176 */       this.parent.reset_connection(false);
/* 177 */       submit_pass();
/*     */     } catch (Exception localException) {
/* 179 */       System.out.println("Error internal to \"pass\" handler");
/* 180 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void submit_agf()
/*     */     throws Exception
/*     */   {
/* 191 */     this.threads.submit(new gui_revert_and_warn(stiki_frontend_driver.FB_TYPE.AGF, this.edit_pkg, this.summary, this.session_cookie, this.user_has_native_rb, this.rollback, this.no_watchlist, this.warn, this.gui_revert_panel));
/*     */     try
/*     */     {
/* 195 */       this.parent.client_interface.feedback_insert(this.md.rid, fb_constant(this.fb, this.edit_pkg.source_queue), this.user);
/*     */     }
/*     */     catch (CommunicationsException localCommunicationsException) {
/* 198 */       this.parent.reset_connection(false);
/* 199 */       submit_agf();
/*     */     } catch (Exception localException) {
/* 201 */       System.out.println("Error internal to \"AGF\" handler");
/* 202 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void submit_vandalism()
/*     */     throws Exception
/*     */   {
/* 214 */     this.threads.submit(new gui_revert_and_warn(stiki_frontend_driver.FB_TYPE.GUILTY, this.edit_pkg, this.summary, this.session_cookie, this.user_has_native_rb, this.rollback, this.no_watchlist, this.warn, this.gui_revert_panel));
/*     */     try
/*     */     {
/* 218 */       this.parent.client_interface.feedback_insert(this.md.rid, fb_constant(this.fb, this.edit_pkg.source_queue), this.user);
/*     */ 
/* 220 */       this.parent.client_interface.oe_insert(this.md, 0L, rollback_handler.RB_TYPE.HUMAN);
/*     */     }
/*     */     catch (CommunicationsException localCommunicationsException) {
/* 223 */       this.parent.reset_connection(false);
/* 224 */       submit_vandalism();
/*     */     } catch (Exception localException) {
/* 226 */       System.out.println("Error internal to \"vandalism\" handler");
/* 227 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private int fb_constant(stiki_frontend_driver.FB_TYPE paramFB_TYPE, stiki_utils.SCORE_SYS paramSCORE_SYS)
/*     */   {
/* 249 */     if (paramFB_TYPE.equals(stiki_frontend_driver.FB_TYPE.INNOCENT))
/* 250 */       return -1 * stiki_utils.queue_to_constant(paramSCORE_SYS);
/* 251 */     if (paramFB_TYPE.equals(stiki_frontend_driver.FB_TYPE.GUILTY))
/* 252 */       return stiki_utils.queue_to_constant(paramSCORE_SYS);
/* 253 */     if (paramFB_TYPE.equals(stiki_frontend_driver.FB_TYPE.AGF))
/* 254 */       return 5 * stiki_utils.queue_to_constant(paramSCORE_SYS);
/* 255 */     return 0;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.gui_fb_handler
 * JD-Core Version:    0.6.0
 */