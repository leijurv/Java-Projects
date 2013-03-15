/*     */ package gui_edit_queue;
/*     */ 
/*     */ import com.mysql.jdbc.CommunicationsException;
/*     */ import core_objects.metadata;
/*     */ import core_objects.pair;
/*     */ import core_objects.stiki_utils;
/*     */ import core_objects.stiki_utils.SCORE_SYS;
/*     */ import db_client.client_interface;
/*     */ import db_client.qmanager_client;
/*     */ import executables.stiki_frontend_driver;
/*     */ import gui_menus.gui_menu_bar;
/*     */ import gui_menus.gui_menu_options;
/*     */ import gui_support.diff_markup;
/*     */ import gui_support.diff_whitespace;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import mediawiki_api.api_retrieve;
/*     */ 
/*     */ public class gui_display_pkg
/*     */ {
/*     */   public static final int HIST_DEPTH = 5;
/*     */   public final List<metadata> page_hist;
/*     */   public final metadata metadata;
/*     */   public final String content;
/*     */   public final String content_linked;
/*     */   public final int rb_depth;
/*     */   public final stiki_utils.SCORE_SYS source_queue;
/*     */   private pair<String, String> edit_token;
/*     */   private Integer user_edit_count;
/*     */ 
/*     */   public gui_display_pkg(List<metadata> paramList, pair<String, String> parampair, String paramString1, String paramString2, stiki_utils.SCORE_SYS paramSCORE_SYS, int paramInt, Integer paramInteger)
/*     */   {
/* 106 */     this.page_hist = paramList;
/* 107 */     this.metadata = ((metadata)paramList.get(0));
/* 108 */     this.edit_token = parampair;
/* 109 */     this.content = paramString1;
/* 110 */     this.content_linked = paramString2;
/* 111 */     this.rb_depth = paramInt;
/* 112 */     this.source_queue = paramSCORE_SYS;
/* 113 */     this.user_edit_count = paramInteger;
/*     */   }
/*     */ 
/*     */   public static gui_display_pkg create_offline(long paramLong)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 132 */       metadata localmetadata = api_retrieve.process_basic_rid(paramLong);
/*     */ 
/* 135 */       String str1 = api_retrieve.process_diff_prev(paramLong);
/* 136 */       String str2 = diff_markup.beautify_markup(str1, localmetadata.title, "", false);
/*     */ 
/* 138 */       str2 = diff_whitespace.whitespace_diff_html(str2);
/* 139 */       String str3 = diff_markup.beautify_markup(str1, localmetadata.title, "", true);
/*     */ 
/* 141 */       str3 = diff_whitespace.whitespace_diff_html(str3);
/*     */ 
/* 143 */       ArrayList localArrayList = new ArrayList(1);
/* 144 */       localArrayList.add(localmetadata);
/* 145 */       return new gui_display_pkg(localArrayList, null, str2, str3, null, 0, null);
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   public static gui_display_pkg create_if_most_recent(stiki_frontend_driver paramstiki_frontend_driver, long paramLong1, long paramLong2, String paramString, boolean paramBoolean, stiki_utils.SCORE_SYS paramSCORE_SYS)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 173 */       List localList = api_retrieve.process_page_hist_meta(paramLong2, 5, paramString, paramstiki_frontend_driver.client_interface);
/*     */ 
/* 177 */       if (((localList.size() == 0) && ((api_retrieve.process_badrevid(paramLong1)) || (api_retrieve.process_page_missing(paramLong2)))) || (paramLong1 != ((metadata)localList.get(0)).rid))
/*     */       {
/* 180 */         paramstiki_frontend_driver.client_interface.queues.queue_delete(paramLong1);
/* 181 */         return null;
/*     */       }
/*     */       pair localpair;
/* 188 */       if (!paramBoolean)
/* 189 */         localpair = api_retrieve.process_edit_token(paramLong2, paramString);
/* 190 */       else localpair = null;
/*     */ 
/* 192 */       int i = 0;
/* 193 */       long l = 0L;
/* 194 */       metadata localmetadata = (metadata)localList.get(0);
/* 195 */       for (int j = 0; j < localList.size(); j++) {
/* 196 */         if (((metadata)localList.get(j)).user.equals(localmetadata.user)) {
/* 197 */           i++;
/*     */         } else {
/* 199 */           l = ((metadata)localList.get(j)).rid;
/* 200 */           break;
/*     */         }
/*     */       }
/* 203 */       if ((i == 0) || (l == 0L)) {
/* 204 */         paramstiki_frontend_driver.client_interface.queues.queue_delete(paramLong1);
/* 205 */         return null;
/*     */       }
/*     */ 
/* 208 */       String str1 = "";
/* 209 */       if (i > 1) {
/* 210 */         str1 = "Below is displayed a combined diff for " + i + " edits by the same user<BR>" + "The edit properties box shows information " + "for the most recent of these edits<BR>" + "If instructed, STiki will revert ";
/*     */ 
/* 215 */         if (i == 2)
/* 216 */           str1 = str1 + "both edits";
/* 217 */         else str1 = str1 + "all " + i + " edits";
/*     */ 
/*     */       }
/*     */ 
/* 224 */       String str2 = api_retrieve.process_diff_current(l);
/* 225 */       String str3 = diff_markup.beautify_markup(str2, ((metadata)localList.get(0)).title, str1, false);
/*     */ 
/* 227 */       str3 = diff_whitespace.whitespace_diff_html(str3);
/* 228 */       String str4 = diff_markup.beautify_markup(str2, ((metadata)localList.get(0)).title, str1, true);
/*     */ 
/* 230 */       str4 = diff_whitespace.whitespace_diff_html(str4);
/*     */ 
/* 232 */       Integer localInteger = null;
/* 233 */       if (localmetadata.user_is_ip)
/* 234 */         localInteger = Integer.valueOf(-1);
/* 235 */       else if (paramstiki_frontend_driver.menu_bar.get_options_menu().get_dttr_policy()) {
/* 236 */         localInteger = Integer.valueOf((int)api_retrieve.process_user_edits(localmetadata.user));
/*     */       }
/*     */ 
/* 240 */       return new gui_display_pkg(localList, localpair, str3, str4, paramSCORE_SYS, i, localInteger);
/*     */     }
/*     */     catch (CommunicationsException localCommunicationsException)
/*     */     {
/* 244 */       paramstiki_frontend_driver.reset_connection(false);
/* 245 */       return create_if_most_recent(paramstiki_frontend_driver, paramLong1, paramLong2, paramString, paramBoolean, paramSCORE_SYS);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 250 */     return null;
/*     */   }
/*     */ 
/*     */   public static gui_display_pkg create_end_pkg()
/*     */   {
/* 263 */     String str = "<HTML><HEAD></HEAD><BODY><DIV ALIGN=\"center\"> ";
/* 264 */     str = str + "All edits from the provided queue have been exhausted. ";
/* 265 */     str = str + "Please restart STiki or the offline-research-tool ";
/* 266 */     str = str + "(ORT), as required for your usage.";
/* 267 */     str = str + "</DIV></BODY><HTML>";
/*     */ 
/* 269 */     ArrayList localArrayList = new ArrayList(1);
/* 270 */     localArrayList.add(new metadata());
/* 271 */     return new gui_display_pkg(localArrayList, null, str, str, null, 0, null);
/*     */   }
/*     */ 
/*     */   public int get_user_edit_count()
/*     */     throws Exception
/*     */   {
/* 287 */     if (this.user_edit_count == null) {
/* 288 */       if (this.metadata.user_is_ip) {
/* 289 */         return -1;
/*     */       }
/* 291 */       this.user_edit_count = Integer.valueOf((int)api_retrieve.process_user_edits(this.metadata.user, 0, stiki_utils.cur_unix_time(), 0L, 50L, 50));
/*     */ 
/* 294 */       return this.user_edit_count.intValue();
/*     */     }
/*     */ 
/* 297 */     return this.user_edit_count.intValue();
/*     */   }
/*     */ 
/*     */   public pair<String, String> get_token()
/*     */   {
/* 305 */     return this.edit_token;
/*     */   }
/*     */ 
/*     */   public void refresh_rb_token(String paramString)
/*     */     throws Exception
/*     */   {
/* 313 */     this.metadata.refresh_rb_token(paramString);
/*     */   }
/*     */ 
/*     */   public void refresh_edit_token(String paramString)
/*     */     throws Exception
/*     */   {
/* 321 */     this.edit_token = api_retrieve.process_edit_token(this.metadata.pid, paramString);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_edit_queue.gui_display_pkg
 * JD-Core Version:    0.6.0
 */