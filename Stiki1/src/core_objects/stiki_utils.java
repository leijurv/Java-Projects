/*     */ package core_objects;
/*     */ 
/*     */ import db_client.stiki_con_client;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.StringSelection;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import java.sql.Connection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class stiki_utils
/*     */ {
/*     */   public static final String tbl_scores_stiki = "scores_stiki";
/*     */   public static final String tbl_scores_cbng = "scores_cbng";
/*     */   public static final String tbl_scores_wt = "scores_wt";
/*     */   public static final String tbl_scores_spam = "scores_spam";
/*     */   public static final String tbl_queue_stiki = "queue_stiki";
/*     */   public static final String tbl_queue_cbng = "queue_cbng";
/*     */   public static final String tbl_queue_wt = "queue_wt";
/*     */   public static final String tbl_queue_spam = "queue_spam";
/*     */   public static final String tbl_off_edits = "offending_edits";
/*     */   public static final String tbl_oes_old = "oes_archive";
/*     */   public static final String tbl_edits = "all_edits";
/*     */   public static final String tbl_features = "features";
/*     */   public static final String tbl_feedback = "feedback";
/*     */   public static final String tbl_offline_cat_links = "categorylinks";
/*     */   public static final String tbl_cat_links = "category_links";
/*     */   public static final String tbl_category = "category";
/*     */   public static final String tbl_country = "country";
/*     */   public static final String tbl_geo_country = "geo_country";
/*     */   public static final String tbl_geo_city = "geo_city";
/*     */   public static final String tbl_links = "hyperlinks";
/*     */   public static final String tbl_status = "stiki_status";
/*     */   public static final String tbl_log = "log_client";
/*     */   public static final String tbl_def_queue = "default_queue";
/*     */   public static final String WIKI_TS_REGEXP = "\\d\\d:\\d\\d, (\\d\\d|\\d) (January|February|March|April|May|June|July|August|September|October|November|December) \\d\\d\\d\\d \\(UTC\\)";
/*     */   public static final int HALF_LIFE = 432000;
/*     */   public static final int HIST_WINDOW = 3888000;
/*     */ 
/*     */   public static int queue_to_constant(SCORE_SYS paramSCORE_SYS)
/*     */   {
/* 190 */     if (paramSCORE_SYS.equals(SCORE_SYS.STIKI)) return 1;
/* 191 */     if (paramSCORE_SYS.equals(SCORE_SYS.CBNG)) return 2;
/* 192 */     if (paramSCORE_SYS.equals(SCORE_SYS.WT)) return 3;
/* 193 */     if (paramSCORE_SYS.equals(SCORE_SYS.SPAM)) return 4;
/* 194 */     return 0;
/*     */   }
/*     */ 
/*     */   public static SCORE_SYS constant_to_queue(int paramInt)
/*     */   {
/* 205 */     if (paramInt == 1) return SCORE_SYS.STIKI;
/* 206 */     if (paramInt == 2) return SCORE_SYS.CBNG;
/* 207 */     if (paramInt == 3) return SCORE_SYS.WT;
/* 208 */     if (paramInt == 4) return SCORE_SYS.SPAM;
/* 209 */     return null;
/*     */   }
/*     */ 
/*     */   public static QUEUE_TYPE queue_to_type(SCORE_SYS paramSCORE_SYS)
/*     */   {
/* 219 */     if (paramSCORE_SYS.equals(SCORE_SYS.STIKI)) return QUEUE_TYPE.VANDALISM;
/* 220 */     if (paramSCORE_SYS.equals(SCORE_SYS.CBNG)) return QUEUE_TYPE.VANDALISM;
/* 221 */     if (paramSCORE_SYS.equals(SCORE_SYS.WT)) return QUEUE_TYPE.VANDALISM;
/* 222 */     if (paramSCORE_SYS.equals(SCORE_SYS.SPAM)) return QUEUE_TYPE.LINK_SPAM;
/* 223 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean test_db_connectivity()
/*     */   {
/*     */     try
/*     */     {
/* 232 */       stiki_con_client localstiki_con_client = new stiki_con_client();
/* 233 */       localstiki_con_client.con.close();
/* 234 */       return true; } catch (Exception localException) {
/*     */     }
/* 236 */     return false;
/*     */   }
/*     */ 
/*     */   public static BufferedReader create_reader(String paramString)
/*     */   {
/* 247 */     BufferedReader localBufferedReader = null;
/*     */     try {
/* 249 */       File localFile = new File(paramString);
/* 250 */       FileInputStream localFileInputStream = new FileInputStream(localFile);
/* 251 */       DataInputStream localDataInputStream = new DataInputStream(localFileInputStream);
/* 252 */       InputStreamReader localInputStreamReader = new InputStreamReader(localDataInputStream);
/* 253 */       localBufferedReader = new BufferedReader(localInputStreamReader);
/*     */     } catch (Exception localException) {
/* 255 */       System.err.println("Error opening reader over file: " + paramString);
/*     */     }
/* 257 */     return localBufferedReader;
/*     */   }
/*     */ 
/*     */   public static BufferedWriter create_writer(String paramString, boolean paramBoolean)
/*     */   {
/* 268 */     BufferedWriter localBufferedWriter = null;
/*     */     try {
/* 270 */       File localFile = new File(paramString);
/* 271 */       localBufferedWriter = new BufferedWriter(new FileWriter(localFile, paramBoolean));
/*     */     } catch (Exception localException) {
/* 273 */       System.err.println("Error opening writer over file: " + paramString);
/*     */     }
/* 275 */     return localBufferedWriter;
/*     */   }
/*     */ 
/*     */   public static String capture_stream(InputStream paramInputStream)
/*     */     throws Exception
/*     */   {
/* 284 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
/* 285 */     String str = null;
/* 286 */     StringBuilder localStringBuilder = new StringBuilder();
/* 287 */     while ((str = localBufferedReader.readLine()) != null)
/* 288 */       localStringBuilder.append(str);
/* 289 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   public static String string_from_url(String paramString, int paramInt)
/*     */     throws InterruptedException
/*     */   {
/*     */     try
/*     */     {
/* 303 */       paramString = URLEncoder.encode(paramString, "UTF-8");
/* 304 */       URL localURL = new URL(paramString);
/* 305 */       URLConnection localURLConnection = localURL.openConnection();
/* 306 */       InputStream localInputStream = localURLConnection.getInputStream();
/* 307 */       return capture_stream(localInputStream);
/*     */     } catch (Exception localException) {
/* 309 */       Thread.sleep(50L);
/* 310 */       if (paramInt == 0)
/* 311 */         return null; 
/*     */     }
/* 312 */     return string_from_url(paramString, paramInt - 1);
/*     */   }
/*     */ 
/*     */   public static long ip_to_long(String paramString)
/*     */   {
/* 324 */     String[] arrayOfString = paramString.split("\\.");
/*     */ 
/* 327 */     int i = Integer.parseInt(arrayOfString[0]);
/* 328 */     int j = Integer.parseInt(arrayOfString[1]);
/* 329 */     int k = Integer.parseInt(arrayOfString[2]);
/* 330 */     int m = Integer.parseInt(arrayOfString[3]);
/*     */ 
/* 333 */     long l = i;
/* 334 */     l <<= 8;
/* 335 */     l |= j;
/* 336 */     l <<= 8;
/* 337 */     l |= k;
/* 338 */     l <<= 8;
/* 339 */     l |= m;
/* 340 */     return l;
/*     */   }
/*     */ 
/*     */   public static String ip_to_string(long paramLong)
/*     */   {
/* 351 */     int i = 255;
/*     */ 
/* 354 */     long l1 = paramLong & i;
/* 355 */     i <<= 8;
/* 356 */     long l2 = (paramLong & i) >> 8;
/* 357 */     i <<= 8;
/* 358 */     long l3 = (paramLong & i) >> 16;
/* 359 */     i <<= 8;
/* 360 */     long l4 = (paramLong & i) >> 24;
/*     */ 
/* 362 */     return l4 + "." + l3 + "." + l2 + "." + l1;
/*     */   }
/*     */ 
/*     */   public static boolean str_to_bool(String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 372 */       if (Integer.parseInt(paramString) == 0)
/* 373 */         return false;
/*     */     } catch (Exception localException) {
/*     */     }
/* 376 */     return (!paramString.equalsIgnoreCase("false")) && (!paramString.equals(""));
/*     */   }
/*     */ 
/*     */   public static long power_of_2(int paramInt)
/*     */   {
/* 388 */     long l = 1L;
/* 389 */     for (int i = paramInt; i != 0; i--)
/* 390 */       l *= 2L;
/* 391 */     return l;
/*     */   }
/*     */ 
/*     */   public static List<String> all_pattern_matches_within(String paramString1, String paramString2)
/*     */   {
/* 403 */     ArrayList localArrayList = new ArrayList();
/* 404 */     Matcher localMatcher = Pattern.compile(paramString1, 2).matcher(paramString2);
/*     */ 
/* 406 */     while (localMatcher.find())
/* 407 */       localArrayList.add(localMatcher.group());
/* 408 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public static Set<String> unique_matches_within(String paramString1, String paramString2)
/*     */   {
/* 420 */     HashSet localHashSet = new HashSet();
/* 421 */     Matcher localMatcher = Pattern.compile(paramString1, 2).matcher(paramString2);
/*     */ 
/* 423 */     while (localMatcher.find())
/* 424 */       localHashSet.add(localMatcher.group());
/* 425 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   public static String first_match_within(String paramString1, String paramString2)
/*     */   {
/* 437 */     Matcher localMatcher = Pattern.compile(paramString1, 2).matcher(paramString2);
/*     */ 
/* 439 */     if (localMatcher.find())
/* 440 */       return localMatcher.group();
/* 441 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean has_match_within(Pattern paramPattern, String paramString)
/*     */   {
/* 452 */     Matcher localMatcher = paramPattern.matcher(paramString);
/*     */ 
/* 454 */     return localMatcher.find();
/*     */   }
/*     */ 
/*     */   public static int num_matches_within(String paramString1, String paramString2)
/*     */   {
/* 466 */     int i = 0;
/* 467 */     Matcher localMatcher = Pattern.compile(paramString1, 2).matcher(paramString2);
/*     */ 
/* 469 */     while (localMatcher.find())
/* 470 */       i++;
/* 471 */     return i;
/*     */   }
/*     */ 
/*     */   public static int num_matches_within(Pattern paramPattern, String paramString)
/*     */   {
/* 481 */     int i = 0;
/* 482 */     Matcher localMatcher = paramPattern.matcher(paramString);
/* 483 */     while (localMatcher.find())
/* 484 */       i++;
/* 485 */     return i;
/*     */   }
/*     */ 
/*     */   public static int char_occurences(char paramChar, String paramString)
/*     */   {
/* 495 */     int i = 0;
/* 496 */     for (int j = 0; j < paramString.length(); j++) {
/* 497 */       if (paramString.charAt(j) == paramChar)
/* 498 */         i++;
/*     */     }
/* 500 */     return i;
/*     */   }
/*     */ 
/*     */   public static void set_sys_clipboard(String paramString)
/*     */   {
/* 508 */     StringSelection localStringSelection = new StringSelection(paramString);
/* 509 */     Clipboard localClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 510 */     localClipboard.setContents(localStringSelection, localStringSelection);
/*     */   }
/*     */ 
/*     */   public static double logistic_cdf(double paramDouble)
/*     */   {
/* 526 */     return 1.0D - 1.0D / (1.0D + Math.exp(paramDouble));
/*     */   }
/*     */ 
/*     */   public static double decay_event(long paramLong1, long paramLong2, long paramLong3)
/*     */   {
/* 541 */     double d = Math.pow(0.5D, (paramLong1 - paramLong2) / (paramLong3 * 1.0D));
/* 542 */     if (d > 1.0D)
/* 543 */       return 1.0D;
/* 544 */     return d;
/*     */   }
/*     */ 
/*     */   public static long arg_unix_time(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 559 */     Calendar localCalendar = Calendar.getInstance();
/* 560 */     localCalendar.clear();
/* 561 */     localCalendar.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
/* 562 */     localCalendar.set(paramInt1, paramInt2 - 1, paramInt3, paramInt4, paramInt5, paramInt6);
/* 563 */     return localCalendar.getTimeInMillis() / 1000L;
/*     */   }
/*     */ 
/*     */   public static long wiki_ts_to_unix(String paramString)
/*     */   {
/* 573 */     int i = 0; int j = 0; int k = 0; int m = 0; int n = 0; int i1 = 0;
/*     */ 
/* 576 */     i = Integer.parseInt(paramString.substring(0, 4));
/* 577 */     j = Integer.parseInt(paramString.substring(5, 7));
/* 578 */     k = Integer.parseInt(paramString.substring(8, 10));
/* 579 */     m = Integer.parseInt(paramString.substring(11, 13));
/* 580 */     n = Integer.parseInt(paramString.substring(14, 16));
/* 581 */     i1 = Integer.parseInt(paramString.substring(17, 19));
/* 582 */     return arg_unix_time(i, j, k, m, n, i1);
/*     */   }
/*     */ 
/*     */   public static String unix_ts_to_wiki(long paramLong)
/*     */   {
/* 591 */     String str = "";
/* 592 */     Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
/* 593 */     localCalendar.setTimeInMillis(1000L * paramLong);
/* 594 */     str = str + localCalendar.get(1) + "-";
/* 595 */     str = str + cal_pad(new StringBuilder().append("").append(localCalendar.get(2) + 1).toString()) + "-";
/* 596 */     str = str + cal_pad(new StringBuilder().append("").append(localCalendar.get(5)).toString()) + "T";
/* 597 */     str = str + cal_pad(new StringBuilder().append("").append(localCalendar.get(11)).toString()) + ":";
/* 598 */     str = str + cal_pad(new StringBuilder().append("").append(localCalendar.get(12)).toString()) + ":";
/* 599 */     str = str + cal_pad(new StringBuilder().append("").append(localCalendar.get(13)).toString()) + "Z";
/* 600 */     return str;
/*     */   }
/*     */ 
/*     */   public static int month_name_to_int(String paramString)
/*     */   {
/* 610 */     if (paramString.equals("January")) return 1;
/* 611 */     if (paramString.equals("February")) return 2;
/* 612 */     if (paramString.equals("March")) return 3;
/* 613 */     if (paramString.equals("April")) return 4;
/* 614 */     if (paramString.equals("May")) return 5;
/* 615 */     if (paramString.equals("June")) return 6;
/* 616 */     if (paramString.equals("July")) return 7;
/* 617 */     if (paramString.equals("August")) return 8;
/* 618 */     if (paramString.equals("September")) return 9;
/* 619 */     if (paramString.equals("October")) return 10;
/* 620 */     if (paramString.equals("November")) return 11;
/* 621 */     if (paramString.equals("December")) return 12;
/* 622 */     return 0;
/*     */   }
/*     */ 
/*     */   public static long cur_unix_time()
/*     */   {
/* 630 */     return System.currentTimeMillis() / 1000L;
/*     */   }
/*     */ 
/*     */   public static long cur_unix_day()
/*     */   {
/* 639 */     return cur_unix_time() / 86400L;
/*     */   }
/*     */ 
/*     */   public static long unix_day_at_unix_sec(long paramLong)
/*     */   {
/* 648 */     return paramLong / 86400L;
/*     */   }
/*     */ 
/*     */   private static String cal_pad(String paramString)
/*     */   {
/* 655 */     if (paramString.length() == 2)
/* 656 */       return paramString;
/* 657 */     return "0" + paramString;
/*     */   }
/*     */ 
/*     */   public static enum QUEUE_TYPE
/*     */   {
/*  49 */     VANDALISM, LINK_SPAM;
/*     */   }
/*     */ 
/*     */   public static enum SCORE_SYS
/*     */   {
/*  42 */     CBNG, STIKI, WT, SPAM;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     core_objects.stiki_utils
 * JD-Core Version:    0.6.0
 */