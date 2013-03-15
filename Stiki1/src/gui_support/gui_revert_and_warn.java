/*     */ package gui_support;
/*     */ 
/*     */ import core_objects.metadata;
/*     */ import core_objects.pair;
/*     */ import core_objects.stiki_utils;
/*     */ import core_objects.stiki_utils.QUEUE_TYPE;
/*     */ import executables.stiki_frontend_driver.FB_TYPE;
/*     */ import gui_edit_queue.gui_display_pkg;
/*     */ import gui_panels.gui_revert_panel;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import mediawiki_api.api_post;
/*     */ import mediawiki_api.api_post.EDIT_OUTCOME;
/*     */ import mediawiki_api.api_retrieve;
/*     */ 
/*     */ public class gui_revert_and_warn
/*     */   implements Runnable
/*     */ {
/*     */   private stiki_frontend_driver.FB_TYPE fb_type;
/*     */   private gui_display_pkg edit_pkg;
/*     */   private metadata metadata;
/*     */   private String revert_comment;
/*     */   private String cookie;
/*     */   private boolean user_has_native_rb;
/*     */   private boolean no_watchlist;
/*     */   private boolean warn;
/*     */   private gui_revert_panel gui_revert_panel;
/*     */   private stiki_utils.QUEUE_TYPE queue_type;
/* 115 */   private final String AIV_PAGE = "Wikipedia:Administrator_intervention_against_vandalism";
/*     */ 
/* 123 */   private final long WARN_WINDOW_SECS = 86400L;
/*     */ 
/*     */   public gui_revert_and_warn(stiki_frontend_driver.FB_TYPE paramFB_TYPE, gui_display_pkg paramgui_display_pkg, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, gui_revert_panel paramgui_revert_panel)
/*     */   {
/* 146 */     this.fb_type = paramFB_TYPE;
/* 147 */     this.edit_pkg = paramgui_display_pkg;
/* 148 */     this.metadata = ((metadata)paramgui_display_pkg.page_hist.get(0));
/* 149 */     this.revert_comment = paramString1;
/* 150 */     this.cookie = paramString2;
/* 151 */     this.user_has_native_rb = paramBoolean1;
/* 152 */     this.no_watchlist = paramBoolean3;
/* 153 */     this.warn = paramBoolean4;
/* 154 */     this.gui_revert_panel = paramgui_revert_panel;
/* 155 */     this.queue_type = stiki_utils.queue_to_type(paramgui_display_pkg.source_queue);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*     */       api_post.EDIT_OUTCOME localEDIT_OUTCOME;
/*     */       RV_STYLE localRV_STYLE;
/* 172 */       if ((this.user_has_native_rb) && (this.fb_type.equals(stiki_frontend_driver.FB_TYPE.GUILTY)))
/*     */       {
/* 175 */         InputStream localInputStream = api_post.edit_rollback(this.metadata.title, this.metadata.user, this.revert_comment, this.metadata.rb_token, this.cookie, this.no_watchlist);
/*     */ 
/* 178 */         long l = api_post.rollback_response(localInputStream);
/* 179 */         if (l < 0L) {
/* 180 */           localEDIT_OUTCOME = api_post.EDIT_OUTCOME.ERROR;
/* 181 */           if (l == -2L)
/* 182 */             bad_rbtoken_handler();
/* 183 */         } else if (l == 0L) {
/* 184 */           localEDIT_OUTCOME = api_post.EDIT_OUTCOME.BEATEN;
/*     */         } else {
/* 186 */           localEDIT_OUTCOME = api_post.EDIT_OUTCOME.SUCCESS;
/*     */         }
/* 188 */         if (l > 0L) {
/* 189 */           if (this.edit_pkg.rb_depth == 1)
/* 190 */             localRV_STYLE = RV_STYLE.RB_NATIVE_ONE;
/* 191 */           else localRV_STYLE = RV_STYLE.RB_NATIVE_MANY; 
/*     */         }
/* 192 */         else localRV_STYLE = RV_STYLE.NOGO_RB;
/* 193 */         localInputStream.close();
/*     */       }
/*     */       else
/*     */       {
/* 198 */         boolean bool = false;
/* 199 */         if (this.fb_type.equals(stiki_frontend_driver.FB_TYPE.GUILTY)) {
/* 200 */           bool = true;
/*     */         }
/* 202 */         int i = gui_soft_rollback.software_rollback(this.edit_pkg, this.revert_comment, bool, this.cookie, this.no_watchlist);
/*     */ 
/* 204 */         if (i == -1)
/* 205 */           localEDIT_OUTCOME = api_post.EDIT_OUTCOME.ERROR;
/* 206 */         else if (i == 0)
/* 207 */           localEDIT_OUTCOME = api_post.EDIT_OUTCOME.BEATEN;
/*     */         else {
/* 209 */           localEDIT_OUTCOME = api_post.EDIT_OUTCOME.SUCCESS;
/*     */         }
/* 211 */         if (i > 0) {
/* 212 */           if (i == 1)
/* 213 */             localRV_STYLE = RV_STYLE.RB_SW_ONE;
/*     */           else
/* 215 */             localRV_STYLE = RV_STYLE.RB_SW_MULTIPLE;
/*     */         } else localRV_STYLE = RV_STYLE.NOGO_SW;
/*     */       }
/*     */       WARNING localWARNING;
/* 232 */       if ((this.warn) && (localEDIT_OUTCOME.equals(api_post.EDIT_OUTCOME.SUCCESS)))
/* 233 */         localWARNING = warn();
/* 234 */       else if (localEDIT_OUTCOME.equals(api_post.EDIT_OUTCOME.SUCCESS))
/* 235 */         localWARNING = WARNING.NO_OPT_OUT;
/* 236 */       else if (localEDIT_OUTCOME.equals(api_post.EDIT_OUTCOME.BEATEN))
/* 237 */         localWARNING = WARNING.NO_BEATEN;
/*     */       else {
/* 239 */         localWARNING = WARNING.NO_ERROR;
/*     */       }
/*     */ 
/* 242 */       this.gui_revert_panel.update_display(this.metadata.user, this.metadata.title, localRV_STYLE, localWARNING);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 246 */       System.out.println("Threaded-POST attempt (reversion) failed:");
/* 247 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private WARNING warn()
/*     */     throws Exception
/*     */   {
/* 268 */     long l = stiki_utils.cur_unix_time() - this.metadata.timestamp;
/* 269 */     if ((l > 86400L) && (this.metadata.user_is_ip)) {
/* 270 */       return WARNING.NO_EDIT_TOO_OLD;
/*     */     }
/*     */ 
/* 273 */     if (api_retrieve.process_block_status(this.metadata.user, this.metadata.user_is_ip))
/*     */     {
/* 275 */       return WARNING.NO_USER_BLOCK;
/*     */     }
/*     */ 
/* 278 */     String str1 = "User talk:" + this.metadata.user;
/* 279 */     String str2 = api_retrieve.process_page_content(str1);
/* 280 */     if (str2.startsWith("#REDIRECT ")) {
/* 281 */       str1 = stiki_utils.first_match_within("\\[\\[.*?\\]\\]", str2).replace("[[", "").replace("]]", "");
/*     */ 
/* 283 */       str2 = api_retrieve.process_page_content(str1);
/*     */     }
/*     */ 
/* 287 */     String str3 = cur_utc_month_year();
/* 288 */     String str4 = get_section_content(str2, str3);
/*     */ 
/* 292 */     if (this.edit_pkg.get_token() == null) {
/* 293 */       this.edit_pkg.refresh_edit_token(this.cookie);
/*     */     }
/*     */ 
/* 298 */     int i = highest_warn_level(this.queue_type, str4);
/* 299 */     int j = i + 1;
/* 300 */     boolean bool = im_warn_present(this.queue_type, str4);
/*     */ 
/* 303 */     if ((bool) || (j == 5))
/*     */     {
/* 306 */       if (this.metadata.timestamp < last_message_ts(str4).longValue()) {
/* 307 */         return WARNING.NO_AIV_TIMING;
/*     */       }
/* 309 */       str5 = aiv_text(this.queue_type, this.metadata.user, this.metadata.rid, !this.metadata.user_is_ip, bool);
/*     */ 
/* 311 */       api_post.edit_append_text("Wikipedia:Administrator_intervention_against_vandalism", aiv_comment(this.queue_type, this.metadata.user, this.metadata.rid, !this.metadata.user_is_ip), str5, false, this.edit_pkg.get_token(), this.cookie, true, this.no_watchlist);
/*     */ 
/* 316 */       if (bool)
/* 317 */         return WARNING.YES_AIV_4IM;
/* 318 */       return WARNING.YES_AIV;
/*     */     }
/*     */ 
/* 323 */     String str5 = warning_template(this.queue_type, j, this.metadata.title, this.metadata.user_is_ip);
/*     */ 
/* 325 */     if (str4.equals(""))
/* 326 */       str5 = "\r== " + str3 + " ==\r" + str5;
/*     */     else {
/* 328 */       str5 = "\r\r" + str5 + "\r\r";
/*     */     }
/*     */ 
/* 333 */     api_post.edit_append_text(str1, warning_comment(this.queue_type), str5, false, this.edit_pkg.get_token(), this.cookie, true, this.no_watchlist);
/*     */ 
/* 338 */     if (j == 1) return WARNING.YES_UW1;
/* 339 */     if (j == 2) return WARNING.YES_UW2;
/* 340 */     if (j == 3) return WARNING.YES_UW3;
/* 341 */     return WARNING.YES_UW4;
/*     */   }
/*     */ 
/*     */   private static String warning_template(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE, int paramInt, String paramString, boolean paramBoolean)
/*     */   {
/* 362 */     String str1 = "";
/* 363 */     String str2 = "";
/* 364 */     if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.VANDALISM))
/* 365 */       str2 = str2 + "{{subst:uw-vandalism";
/* 366 */     else str2 = str2 + "{{subst:uw-spam";
/*     */ 
/* 368 */     str2 = str2 + paramInt + "|" + paramString + "|" + str1 + "|" + "subst=subst:}}  ~~~~";
/*     */ 
/* 373 */     if (paramBoolean)
/* 374 */       str2 = str2 + "\n{{subst:Shared IP advice}}";
/* 375 */     return str2;
/*     */   }
/*     */ 
/*     */   private static String aiv_text(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE, String paramString, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 393 */     String str = "";
/* 394 */     if (!paramBoolean2) {
/* 395 */       if (paramBoolean1) {
/* 396 */         str = "\r* {{Vandal|" + paramString + "}} ### " + "({{diff2|" + paramLong + "|diff}}) " + "after recently receiving last warning ~~~~ \r";
/*     */       }
/*     */       else
/*     */       {
/* 400 */         str = "\r* {{IPvandal|" + paramString + "}} ### " + "({{diff2|" + paramLong + "|diff}}) " + "after recently receiving last warning ~~~~ \r";
/*     */       }
/*     */ 
/*     */     }
/* 405 */     else if (paramBoolean1) {
/* 406 */       str = "\r* {{Vandal|" + paramString + "}} ### " + "({{diff2|" + paramLong + "|diff}}) " + "after recently receiving unrelated 4im warning " + "(may require administrative attention) ~~~~ \r";
/*     */     }
/*     */     else
/*     */     {
/* 411 */       str = "\r* {{IPvandal|" + paramString + "}} ### " + "({{diff2|" + paramLong + "|diff}}) " + "after recently receiving unrelated 4im warning " + "(may require administrative attention) ~~~~ \r";
/*     */     }
/*     */ 
/* 418 */     if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.VANDALISM))
/* 419 */       str = str.replace("###", "Vandalized");
/* 420 */     else if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.LINK_SPAM))
/* 421 */       str = str.replace("###", "Link spammed");
/* 422 */     return str;
/*     */   }
/*     */ 
/*     */   private static String aiv_comment(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE, String paramString, long paramLong, boolean paramBoolean)
/*     */   {
/* 436 */     String str = "";
/* 437 */     if (paramBoolean) {
/* 438 */       str = "Reporting [[User_talk:" + paramString + "|" + paramString + "]] " + "for ###, found using [[WP:STiki|STiki]].";
/*     */     }
/*     */     else {
/* 441 */       str = "Reporting [[Special:Contributions/" + paramString + "|" + paramString + "]] " + "for ###, " + "found using [[WP:STiki|STiki]].";
/*     */     }
/*     */ 
/* 445 */     if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.VANDALISM))
/* 446 */       str = str.replace("###", "repeated vandalism");
/* 447 */     else if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.LINK_SPAM))
/* 448 */       str = str.replace("###", "spam behavior");
/* 449 */     return str;
/*     */   }
/*     */ 
/*     */   private static String get_section_content(String paramString1, String paramString2)
/*     */   {
/* 466 */     String str1 = "== " + paramString2 + " ==";
/* 467 */     String str2 = "==" + paramString2 + "==";
/* 468 */     String str3 = "=== " + paramString2 + " ===";
/* 469 */     String str4 = "===" + paramString2 + "===";
/*     */ 
/* 472 */     int i = Math.max(Math.max(Math.max(paramString1.indexOf(str1), paramString1.indexOf(str2)), paramString1.indexOf(str3)), paramString1.indexOf(str4));
/*     */ 
/* 477 */     if (i == -1) {
/* 478 */       return "";
/*     */     }
/*     */ 
/* 484 */     String str5 = paramString1.substring(i + str3.length());
/*     */ 
/* 487 */     int j = str5.indexOf("==");
/* 488 */     if (j == -1)
/* 489 */       return str5;
/* 490 */     return str5.substring(0, j);
/*     */   }
/*     */ 
/*     */   private static int highest_warn_level(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE, String paramString)
/*     */   {
/* 520 */     int i = 0;
/* 521 */     for (int j = 1; j <= 4; j++)
/*     */     {
/* 523 */       if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.VANDALISM))
/*     */       {
/* 526 */         if ((paramString.contains("Template:uw-vandalism" + j)) || (paramString.contains("Template:Huggle/warn-" + j)))
/*     */         {
/* 528 */           i = j;
/*     */         }
/*     */         else {
/* 531 */           if ((!paramString.contains("Template:uw-test" + j)) && (!paramString.contains("Template:uw-delete" + j)) && (!paramString.contains("Template:uw-error" + j)) && (!paramString.contains("Template:uw-joke" + j)) && (!paramString.contains("Template:uw-notcensored" + j)) && (!paramString.contains("Template:uw-defamatory" + j)))
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 537 */           if (j == 4) i = 3; else i = j; 
/*     */         }
/*     */       } else {
/* 539 */         if (!paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.LINK_SPAM)) {
/*     */           continue;
/*     */         }
/* 542 */         if ((paramString.contains("Template:uw-spam" + j)) || (paramString.contains("Template:Huggle/warn-spam-" + j)))
/*     */         {
/* 544 */           i = j;
/*     */         }
/* 547 */         else if (paramString.contains("Template:uw-advert" + j)) {
/* 548 */           if (j == 4) i = 3; else i = j;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 554 */     return i;
/*     */   }
/*     */ 
/*     */   private static boolean im_warn_present(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE, String paramString)
/*     */   {
/* 567 */     if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.VANDALISM)) {
/* 568 */       return (paramString.contains("4im")) && (!paramString.contains("vandalism4im"));
/*     */     }
/* 570 */     return (paramString.contains("4im")) && (!paramString.contains("spam4im"));
/*     */   }
/*     */ 
/*     */   private static String cur_utc_month_year()
/*     */   {
/* 580 */     Calendar localCalendar = Calendar.getInstance();
/* 581 */     localCalendar.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
/* 582 */     int i = localCalendar.get(2);
/*     */ 
/* 584 */     String str = "";
/* 585 */     switch (i) { case 0:
/* 586 */       str = "January"; break;
/*     */     case 1:
/* 587 */       str = "February"; break;
/*     */     case 2:
/* 588 */       str = "March"; break;
/*     */     case 3:
/* 589 */       str = "April"; break;
/*     */     case 4:
/* 590 */       str = "May"; break;
/*     */     case 5:
/* 591 */       str = "June"; break;
/*     */     case 6:
/* 592 */       str = "July"; break;
/*     */     case 7:
/* 593 */       str = "August"; break;
/*     */     case 8:
/* 594 */       str = "September"; break;
/*     */     case 9:
/* 595 */       str = "October"; break;
/*     */     case 10:
/* 596 */       str = "November"; break;
/*     */     case 11:
/* 597 */       str = "December"; }
/* 598 */     return str + " " + localCalendar.get(1);
/*     */   }
/*     */ 
/*     */   private static Long last_message_ts(String paramString)
/*     */   {
/* 609 */     List localList = sig_timestamps(paramString);
/* 610 */     if (localList.size() == 0)
/* 611 */       return Long.valueOf(0L);
/* 612 */     return (Long)localList.get(localList.size() - 1);
/*     */   }
/*     */ 
/*     */   private static List<Long> sig_timestamps(String paramString)
/*     */   {
/* 626 */     ArrayList localArrayList = new ArrayList();
/* 627 */     List localList = stiki_utils.all_pattern_matches_within("\\d\\d:\\d\\d, (\\d\\d|\\d) (January|February|March|April|May|June|July|August|September|October|November|December) \\d\\d\\d\\d \\(UTC\\)", paramString);
/*     */ 
/* 633 */     for (int i = 0; i < localList.size(); i++)
/*     */       try {
/* 635 */         String str = (String)localList.get(i);
/* 636 */         str = str.replace(",", "");
/* 637 */         str = str.replace(":", " ");
/* 638 */         String[] arrayOfString = str.split(" ");
/* 639 */         long l = stiki_utils.arg_unix_time(Integer.parseInt(arrayOfString[4]), stiki_utils.month_name_to_int(arrayOfString[3]), Integer.parseInt(arrayOfString[2]), Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), 0);
/*     */ 
/* 645 */         localArrayList.add(Long.valueOf(l));
/*     */       } catch (Exception localException) {
/*     */       }
/* 648 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   private static String warning_comment(stiki_utils.QUEUE_TYPE paramQUEUE_TYPE)
/*     */   {
/* 657 */     if (paramQUEUE_TYPE.equals(stiki_utils.QUEUE_TYPE.VANDALISM)) {
/* 658 */       return "User warning for unconstructive editing found using [[WP:STiki|STiki]]";
/*     */     }
/*     */ 
/* 661 */     return "User warning for unconstructive external link found using [[WP:STiki|STiki]]";
/*     */   }
/*     */ 
/*     */   private void bad_rbtoken_handler()
/*     */   {
/*     */     try
/*     */     {
/* 671 */       System.err.println("Bad token when trying to RB RID:" + this.metadata.rid);
/* 672 */       System.err.println("Token was: " + this.metadata.rb_token);
/* 673 */       System.err.println("Token obtained at: " + (String)this.edit_pkg.get_token().snd);
/* 674 */       System.err.println("Session cookie was: " + this.cookie);
/* 675 */       System.err.println("Refetching token obtained: " + api_retrieve.process_basic_rid(this.metadata.rid, this.cookie).rb_token);
/*     */ 
/* 677 */       System.err.println();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum RV_STYLE
/*     */   {
/*  50 */     NOGO_RB, NOGO_SW, SIMPLE_UNDO, RB_NATIVE_ONE, 
/*  51 */     RB_NATIVE_MANY, RB_SW_ONE, RB_SW_MULTIPLE;
/*     */   }
/*     */ 
/*     */   public static enum WARNING
/*     */   {
/*  43 */     NO_EDIT_TOO_OLD, NO_USER_BLOCK, NO_BEATEN, 
/*  44 */     NO_ERROR, NO_OPT_OUT, NO_AIV_TIMING, YES_UW1, YES_UW2, 
/*  45 */     YES_UW3, YES_UW4, YES_AIV, YES_AIV_4IM;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.gui_revert_and_warn
 * JD-Core Version:    0.6.0
 */