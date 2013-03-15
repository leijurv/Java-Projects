/*      */ package mediawiki_api;
/*      */ 
/*      */ import core_objects.metadata;
/*      */ import core_objects.pair;
/*      */ import core_objects.stiki_utils;
/*      */ import db_client.client_interface;
/*      */ import db_server.db_geolocation;
/*      */ import gui_support.gui_settings;
/*      */ import gui_support.gui_settings.SETTINGS_BOOL;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.URLEncoder;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.xml.parsers.SAXParser;
/*      */ import javax.xml.parsers.SAXParserFactory;
/*      */ import org.xml.sax.helpers.DefaultHandler;
/*      */ 
/*      */ public class api_retrieve
/*      */ {
/*      */   public static final int NUM_HTTP_RETRIES = 2;
/*   97 */   public static String BASE_URL_OVERRIDE = null;
/*      */ 
/*      */   public static String base_url()
/*      */   {
/*  109 */     if (BASE_URL_OVERRIDE != null) {
/*  110 */       return BASE_URL_OVERRIDE;
/*      */     }
/*  112 */     String str = "en.wikipedia.org/w/api.php?action=query";
/*  113 */     if (gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.options_https, false))
/*  114 */       return "https://" + str;
/*  115 */     return "http://" + str;
/*      */   }
/*      */ 
/*      */   public static metadata process_basic_rid(long paramLong, db_geolocation paramdb_geolocation)
/*      */     throws Exception
/*      */   {
/*  130 */     api_xml_basic_rid localapi_xml_basic_rid = new api_xml_basic_rid(paramdb_geolocation);
/*  131 */     do_parse_work(new URL(url_basic_rid(paramLong)), null, localapi_xml_basic_rid);
/*  132 */     return localapi_xml_basic_rid.get_result();
/*      */   }
/*      */ 
/*      */   public static metadata process_basic_rid(long paramLong)
/*      */     throws Exception
/*      */   {
/*  144 */     api_xml_basic_rid localapi_xml_basic_rid = new api_xml_basic_rid(null);
/*  145 */     do_parse_work(new URL(url_basic_rid(paramLong)), null, localapi_xml_basic_rid);
/*  146 */     return localapi_xml_basic_rid.get_result();
/*      */   }
/*      */ 
/*      */   public static metadata process_basic_rid(long paramLong, String paramString, db_geolocation paramdb_geolocation)
/*      */     throws Exception
/*      */   {
/*  158 */     api_xml_basic_rid localapi_xml_basic_rid = new api_xml_basic_rid(paramdb_geolocation);
/*  159 */     do_parse_work(new URL(url_basic_rid(paramLong)), paramString, localapi_xml_basic_rid);
/*  160 */     return localapi_xml_basic_rid.get_result();
/*      */   }
/*      */ 
/*      */   public static metadata process_basic_rid(long paramLong, String paramString)
/*      */     throws Exception
/*      */   {
/*  173 */     api_xml_basic_rid localapi_xml_basic_rid = new api_xml_basic_rid(null);
/*  174 */     do_parse_work(new URL(url_basic_rid(paramLong)), paramString, localapi_xml_basic_rid);
/*  175 */     return localapi_xml_basic_rid.get_result();
/*      */   }
/*      */ 
/*      */   public static List<metadata> process_multiple_rids(List<Long> paramList, db_geolocation paramdb_geolocation)
/*      */     throws Exception
/*      */   {
/*  190 */     api_xml_multiple_rids localapi_xml_multiple_rids = new api_xml_multiple_rids(paramdb_geolocation);
/*  191 */     do_parse_work(new URL(url_multiple_rids(paramList)), null, localapi_xml_multiple_rids);
/*  192 */     return localapi_xml_multiple_rids.get_result();
/*      */   }
/*      */ 
/*      */   public static long process_offender_search(String paramString, long paramLong1, long paramLong2, int paramInt)
/*      */     throws Exception
/*      */   {
/*  207 */     api_xml_find_off localapi_xml_find_off = new api_xml_find_off(paramString);
/*  208 */     String str = url_offender_search(paramLong1, paramLong2, paramInt);
/*  209 */     do_parse_work(new URL(str), null, localapi_xml_find_off);
/*  210 */     return localapi_xml_find_off.get_result().longValue();
/*      */   }
/*      */ 
/*      */   public static Set<String> process_user_perm(String paramString)
/*      */     throws Exception
/*      */   {
/*  219 */     api_xml_user_perm localapi_xml_user_perm = new api_xml_user_perm(paramString);
/*  220 */     do_parse_work(new URL(url_user_perm(paramString)), null, localapi_xml_user_perm);
/*  221 */     return localapi_xml_user_perm.get_result();
/*      */   }
/*      */ 
/*      */   public static long process_user_first_edit_ts(String paramString)
/*      */     throws Exception
/*      */   {
/*  230 */     api_xml_user_first localapi_xml_user_first = new api_xml_user_first();
/*  231 */     do_parse_work(new URL(url_user_first_edit(paramString)), null, localapi_xml_user_first);
/*  232 */     return localapi_xml_user_first.get_result().longValue();
/*      */   }
/*      */ 
/*      */   public static long process_prior_page_edit_ts(long paramLong1, long paramLong2)
/*      */     throws Exception
/*      */   {
/*  244 */     api_xml_page_prior localapi_xml_page_prior = new api_xml_page_prior();
/*  245 */     do_parse_work(new URL(url_prior_page_edit(paramLong1, paramLong2)), null, localapi_xml_page_prior);
/*  246 */     return localapi_xml_page_prior.get_result().longValue();
/*      */   }
/*      */ 
/*      */   public static long process_next_page_edit_ts(long paramLong1, long paramLong2)
/*      */     throws Exception
/*      */   {
/*  259 */     api_xml_page_prior localapi_xml_page_prior = new api_xml_page_prior();
/*  260 */     do_parse_work(new URL(url_newer_page_edit(paramLong1, paramLong2)), null, localapi_xml_page_prior);
/*  261 */     return localapi_xml_page_prior.get_result().longValue();
/*      */   }
/*      */ 
/*      */   public static int process_size_change(long paramLong1, long paramLong2)
/*      */     throws Exception
/*      */   {
/*  277 */     api_xml_size_change localapi_xml_size_change = new api_xml_size_change();
/*  278 */     do_parse_work(new URL(url_size_change(paramLong1, paramLong2)), null, localapi_xml_size_change);
/*  279 */     return localapi_xml_size_change.get_result();
/*      */   }
/*      */ 
/*      */   public static String process_diff_prev(long paramLong)
/*      */     throws Exception
/*      */   {
/*  288 */     api_xml_diff_text localapi_xml_diff_text = new api_xml_diff_text();
/*  289 */     do_parse_work(new URL(url_diff_prev(paramLong)), null, localapi_xml_diff_text);
/*  290 */     return localapi_xml_diff_text.get_result();
/*      */   }
/*      */ 
/*      */   public static String process_diff_current(long paramLong)
/*      */     throws Exception
/*      */   {
/*  299 */     api_xml_diff_text localapi_xml_diff_text = new api_xml_diff_text();
/*  300 */     do_parse_work(new URL(url_diff_current(paramLong)), null, localapi_xml_diff_text);
/*  301 */     return localapi_xml_diff_text.get_result();
/*      */   }
/*      */ 
/*      */   public static Map<Long, Long> process_latest_page(Set<Long> paramSet)
/*      */     throws Exception
/*      */   {
/*  312 */     api_xml_latest_page localapi_xml_latest_page = new api_xml_latest_page();
/*  313 */     do_parse_work(new URL(url_latest_page(paramSet)), null, localapi_xml_latest_page);
/*  314 */     return localapi_xml_latest_page.get_result();
/*      */   }
/*      */ 
/*      */   public static long process_latest_page(long paramLong)
/*      */     throws Exception
/*      */   {
/*  329 */     Set localSet = Collections.singleton(Long.valueOf(paramLong));
/*  330 */     api_xml_latest_page localapi_xml_latest_page = new api_xml_latest_page();
/*  331 */     do_parse_work(new URL(url_latest_page(localSet)), null, localapi_xml_latest_page);
/*  332 */     return ((Long)localapi_xml_latest_page.get_result().values().iterator().next()).longValue();
/*      */   }
/*      */ 
/*      */   public static pair<String, String> process_edit_token(long paramLong, String paramString)
/*      */     throws Exception
/*      */   {
/*  349 */     api_xml_edit_token localapi_xml_edit_token = new api_xml_edit_token();
/*  350 */     do_parse_work(new URL(url_edit_token(paramLong)), paramString, localapi_xml_edit_token);
/*  351 */     return localapi_xml_edit_token.get_result();
/*      */   }
/*      */ 
/*      */   public static pair<String, String> process_edit_token(String paramString1, String paramString2)
/*      */     throws Exception
/*      */   {
/*  361 */     api_xml_edit_token localapi_xml_edit_token = new api_xml_edit_token();
/*  362 */     do_parse_work(new URL(url_edit_token(paramString1)), paramString2, localapi_xml_edit_token);
/*  363 */     return localapi_xml_edit_token.get_result();
/*      */   }
/*      */ 
/*      */   public static String process_page_content(String paramString)
/*      */     throws Exception
/*      */   {
/*  372 */     api_xml_page_content localapi_xml_page_content = new api_xml_page_content();
/*  373 */     do_parse_work(new URL(url_page_content(paramString)), null, localapi_xml_page_content);
/*  374 */     return localapi_xml_page_content.get_result();
/*      */   }
/*      */ 
/*      */   public static String process_page_content(long paramLong)
/*      */     throws Exception
/*      */   {
/*  385 */     api_xml_page_content localapi_xml_page_content = new api_xml_page_content();
/*  386 */     do_parse_work(new URL(url_page_content(paramLong)), null, localapi_xml_page_content);
/*  387 */     return localapi_xml_page_content.get_result();
/*      */   }
/*      */ 
/*      */   public static boolean process_block_status(String paramString, boolean paramBoolean)
/*      */     throws Exception
/*      */   {
/*  398 */     api_xml_block_status localapi_xml_block_status = new api_xml_block_status();
/*  399 */     do_parse_work(new URL(url_block_status(paramString, paramBoolean)), null, localapi_xml_block_status);
/*  400 */     return localapi_xml_block_status.get_result();
/*      */   }
/*      */ 
/*      */   public static boolean process_page_flagged(long paramLong)
/*      */     throws Exception
/*      */   {
/*  410 */     api_xml_page_flagged localapi_xml_page_flagged = new api_xml_page_flagged();
/*  411 */     do_parse_work(new URL(url_page_flagged(paramLong)), null, localapi_xml_page_flagged);
/*  412 */     return localapi_xml_page_flagged.get_result();
/*      */   }
/*      */ 
/*      */   public static boolean process_page_protected(long paramLong)
/*      */     throws Exception
/*      */   {
/*  422 */     api_xml_page_protected localapi_xml_page_protected = new api_xml_page_protected();
/*  423 */     do_parse_work(new URL(url_page_protected(paramLong)), null, localapi_xml_page_protected);
/*  424 */     return localapi_xml_page_protected.get_result();
/*      */   }
/*      */ 
/*      */   public static boolean process_autoconfirmed_status(String paramString)
/*      */     throws Exception
/*      */   {
/*  436 */     api_xml_autoconfirmed localapi_xml_autoconfirmed = new api_xml_autoconfirmed(paramString);
/*  437 */     do_parse_work(new URL(url_autoconfirmed(paramString)), null, localapi_xml_autoconfirmed);
/*  438 */     return localapi_xml_autoconfirmed.get_result();
/*      */   }
/*      */ 
/*      */   public static List<metadata> process_page_hist_meta(long paramLong, int paramInt, String paramString, db_geolocation paramdb_geolocation)
/*      */     throws Exception
/*      */   {
/*  452 */     api_xml_multiple_rids localapi_xml_multiple_rids = new api_xml_multiple_rids(paramdb_geolocation);
/*  453 */     do_parse_work(new URL(url_last_n_page_meta(paramLong, paramInt)), paramString, localapi_xml_multiple_rids);
/*  454 */     return localapi_xml_multiple_rids.get_result();
/*      */   }
/*      */ 
/*      */   public static List<metadata> process_page_next_meta(long paramLong1, long paramLong2, int paramInt, String paramString, db_geolocation paramdb_geolocation)
/*      */     throws Exception
/*      */   {
/*  469 */     api_xml_multiple_rids localapi_xml_multiple_rids = new api_xml_multiple_rids(paramdb_geolocation);
/*  470 */     do_parse_work(new URL(url_next_n_page_meta(paramLong1, paramLong2, paramInt)), paramString, localapi_xml_multiple_rids);
/*  471 */     return localapi_xml_multiple_rids.get_result();
/*      */   }
/*      */ 
/*      */   public static List<metadata> process_page_hist_meta(long paramLong, int paramInt, String paramString, client_interface paramclient_interface)
/*      */     throws Exception
/*      */   {
/*  485 */     api_xml_multiple_rids localapi_xml_multiple_rids = new api_xml_multiple_rids(paramclient_interface);
/*  486 */     do_parse_work(new URL(url_last_n_page_meta(paramLong, paramInt)), paramString, localapi_xml_multiple_rids);
/*  487 */     return localapi_xml_multiple_rids.get_result();
/*      */   }
/*      */ 
/*      */   public static boolean process_badrevid(long paramLong)
/*      */     throws Exception
/*      */   {
/*  496 */     api_xml_badrevid localapi_xml_badrevid = new api_xml_badrevid();
/*  497 */     do_parse_work(new URL(url_basic_rid(paramLong)), null, localapi_xml_badrevid);
/*      */ 
/*  500 */     return localapi_xml_badrevid.get_result();
/*      */   }
/*      */ 
/*      */   public static boolean process_page_missing(long paramLong)
/*      */     throws Exception
/*      */   {
/*  509 */     api_xml_page_missing localapi_xml_page_missing = new api_xml_page_missing();
/*  510 */     do_parse_work(new URL(url_page_protected(paramLong)), null, localapi_xml_page_missing);
/*      */ 
/*  513 */     return localapi_xml_page_missing.get_result();
/*      */   }
/*      */ 
/*      */   public static Set<String> process_xlinks(long paramLong, int paramInt)
/*      */     throws Exception
/*      */   {
/*  526 */     api_xml_xlink localapi_xml_xlink = new api_xml_xlink(paramLong);
/*  527 */     do_parse_work(new URL(url_xlink(paramLong, paramInt)), null, localapi_xml_xlink);
/*  528 */     return localapi_xml_xlink.get_result();
/*      */   }
/*      */ 
/*      */   public static List<pair<Long, String>> process_block_hist(String paramString)
/*      */     throws Exception
/*      */   {
/*  541 */     api_xml_block_hist localapi_xml_block_hist = new api_xml_block_hist();
/*  542 */     do_parse_work(new URL(url_block_hist(paramString)), null, localapi_xml_block_hist);
/*  543 */     return localapi_xml_block_hist.get_result();
/*      */   }
/*      */ 
/*      */   public static Map<String, Long> process_pages_touched(Set<String> paramSet)
/*      */     throws Exception
/*      */   {
/*  554 */     api_xml_page_touched localapi_xml_page_touched = new api_xml_page_touched();
/*  555 */     do_parse_work(new URL(url_page_touched(paramSet)), null, localapi_xml_page_touched);
/*  556 */     return localapi_xml_page_touched.get_result();
/*      */   }
/*      */ 
/*      */   public static List<pair<Long, Long>> process_joint_contribs(List<String> paramList, String paramString)
/*      */     throws Exception
/*      */   {
/*  570 */     api_xml_joint_contribs localapi_xml_joint_contribs = new api_xml_joint_contribs(paramList);
/*  571 */     do_parse_work(new URL(url_joint_contribs(paramList, paramString)), null, localapi_xml_joint_contribs);
/*  572 */     return localapi_xml_joint_contribs.get_result();
/*      */   }
/*      */ 
/*      */   public static long process_user_edits(String paramString, int paramInt1, long paramLong1, long paramLong2, long paramLong3, int paramInt2)
/*      */     throws Exception
/*      */   {
/*  602 */     api_xml_user_edits_date localapi_xml_user_edits_date = new api_xml_user_edits_date(paramString, paramInt1, paramLong2, paramLong3, paramInt2);
/*      */ 
/*  605 */     do_parse_work(new URL(url_user_edits_date(paramString, paramInt1, paramLong1, paramInt2)), null, localapi_xml_user_edits_date);
/*      */ 
/*  607 */     return localapi_xml_user_edits_date.get_result();
/*      */   }
/*      */ 
/*      */   public static long process_user_edits(String paramString)
/*      */     throws Exception
/*      */   {
/*  619 */     api_xml_user_edits_ever localapi_xml_user_edits_ever = new api_xml_user_edits_ever();
/*  620 */     do_parse_work(new URL(url_user_edits_ever(paramString)), null, localapi_xml_user_edits_ever);
/*  621 */     return localapi_xml_user_edits_ever.get_result();
/*      */   }
/*      */ 
/*      */   public static Set<String> process_pages_missing(Set<String> paramSet)
/*      */     throws Exception
/*      */   {
/*  632 */     api_xml_pages_missing localapi_xml_pages_missing = new api_xml_pages_missing();
/*  633 */     do_parse_work(new URL(url_pages_missing(paramSet)), null, localapi_xml_pages_missing);
/*  634 */     return localapi_xml_pages_missing.get_result();
/*      */   }
/*      */ 
/*      */   private static void do_parse_work(URL paramURL, String paramString, DefaultHandler paramDefaultHandler)
/*      */     throws Exception
/*      */   {
/*  653 */     URLConnection localURLConnection = paramURL.openConnection();
/*  654 */     if ((paramString != null) && (!paramString.equals(""))) {
/*  655 */       localURLConnection.setRequestProperty("Cookie", paramString);
/*      */     }
/*      */ 
/*  658 */     InputStream localInputStream = stream_from_url(localURLConnection, 2);
/*  659 */     if (localInputStream == null) {
/*  660 */       throw new Exception("Exception thrown to terminate thread");
/*      */     }
/*      */ 
/*  663 */     SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
/*  664 */     SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
/*  665 */     localSAXParser.parse(localInputStream, paramDefaultHandler);
/*  666 */     localInputStream.close();
/*      */   }
/*      */ 
/*      */   private static InputStream stream_from_url(URLConnection paramURLConnection, int paramInt)
/*      */     throws InterruptedException
/*      */   {
/*      */     try
/*      */     {
/*  680 */       InputStream localInputStream = paramURLConnection.getInputStream();
/*  681 */       return localInputStream;
/*      */     } catch (Exception localException) {
/*  683 */       Thread.sleep(50L);
/*  684 */       if (paramInt == 0) {
/*  685 */         System.out.println("Error: HTTP error at URL: " + paramURLConnection.getURL().toExternalForm());
/*      */ 
/*  687 */         localException.printStackTrace();
/*  688 */         return null;
/*      */       }
/*  689 */     }return stream_from_url(paramURLConnection, paramInt - 1);
/*      */   }
/*      */ 
/*      */   private static String url_basic_rid(long paramLong)
/*      */   {
/*  702 */     String str = base_url() + "&prop=revisions&revids=" + paramLong;
/*  703 */     str = str + "&rvtoken=rollback";
/*  704 */     str = str + "&rvprop=ids|timestamp|user|comment&format=xml";
/*  705 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_multiple_rids(List<Long> paramList)
/*      */   {
/*  715 */     String str = base_url() + "&prop=revisions&revids=";
/*  716 */     Iterator localIterator = paramList.iterator();
/*  717 */     while (localIterator.hasNext())
/*  718 */       str = str + localIterator.next() + "|";
/*  719 */     str = str.substring(0, str.length() - 1);
/*  720 */     str = str + "&rvtoken=rollback";
/*  721 */     str = str + "&rvprop=ids|timestamp|user|comment&format=xml";
/*  722 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_offender_search(long paramLong1, long paramLong2, int paramInt)
/*      */   {
/*  734 */     String str = base_url() + "&prop=revisions&pageids=" + paramLong1;
/*  735 */     str = str + "&rvstartid=" + paramLong2;
/*  736 */     str = str + "&rvlimit=" + (paramInt + 1);
/*  737 */     str = str + "&rvprop=ids|user&rvdir=older&format=xml";
/*  738 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_user_perm(String paramString)
/*      */     throws Exception
/*      */   {
/*  747 */     String str = base_url() + "&list=users&ususers=";
/*  748 */     str = str + URLEncoder.encode(paramString, "UTF-8");
/*  749 */     str = str + "&usprop=groups&format=xml";
/*  750 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_user_first_edit(String paramString)
/*      */     throws Exception
/*      */   {
/*  759 */     String str = base_url() + "&list=usercontribs&ucuser=";
/*  760 */     str = str + URLEncoder.encode(paramString, "UTF-8") + "&uclimit=1&ucdir=newer&";
/*  761 */     str = str + "ucnamespace=0&ucprop=timestamp&format=xml";
/*  762 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_prior_page_edit(long paramLong1, long paramLong2)
/*      */   {
/*  773 */     String str = base_url() + "&prop=revisions&pageids=" + paramLong1;
/*  774 */     str = str + "&rvstartid=" + paramLong2;
/*  775 */     str = str + "&rvlimit=2&rvprop=timestamp&rvdir=older&format=xml";
/*  776 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_newer_page_edit(long paramLong1, long paramLong2)
/*      */   {
/*  787 */     String str = base_url() + "&prop=revisions&pageids=" + paramLong1;
/*  788 */     str = str + "&rvstartid=" + paramLong2;
/*  789 */     str = str + "&rvlimit=2&rvprop=timestamp&rvdir=newer&format=xml";
/*  790 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_size_change(long paramLong1, long paramLong2)
/*      */   {
/*  800 */     String str = base_url() + "&prop=revisions&pageids=" + paramLong1;
/*  801 */     str = str + "&rvstartid=" + paramLong2;
/*  802 */     str = str + "&rvlimit=2&rvprop=size&rvdir=older&format=xml";
/*  803 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_diff_prev(long paramLong)
/*      */   {
/*  812 */     String str = base_url() + "&prop=";
/*  813 */     str = str + "revisions&revids=" + paramLong + "&rvdiffto=prev&format=xml";
/*  814 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_diff_current(long paramLong)
/*      */   {
/*  824 */     String str = base_url() + "&prop=";
/*  825 */     str = str + "revisions&revids=" + paramLong + "&rvdiffto=cur&format=xml";
/*  826 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_latest_page(Set<Long> paramSet)
/*      */   {
/*  836 */     String str = base_url() + "&prop=info&pageids=";
/*  837 */     Iterator localIterator = paramSet.iterator();
/*  838 */     while (localIterator.hasNext())
/*  839 */       str = str + localIterator.next() + "|";
/*  840 */     str = str.substring(0, str.length() - 1);
/*  841 */     str = str + "&format=xml";
/*  842 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_edit_token(long paramLong)
/*      */   {
/*  851 */     String str = base_url();
/*  852 */     str = str + "&prop=info&pageids=" + paramLong + "&intoken=edit&format=xml";
/*  853 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_edit_token(String paramString)
/*      */     throws Exception
/*      */   {
/*  862 */     String str = base_url() + "&prop=info&titles=";
/*  863 */     str = str + URLEncoder.encode(paramString, "UTF-8") + "&intoken=edit&format=xml";
/*  864 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_page_content(String paramString)
/*      */     throws Exception
/*      */   {
/*  873 */     String str = base_url() + "&prop=revisions&titles=";
/*  874 */     str = str + URLEncoder.encode(paramString, "UTF-8") + "&rvlimit=1";
/*  875 */     str = str + "&rvprop=content&format=xml";
/*  876 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_page_content(long paramLong)
/*      */   {
/*  885 */     String str = base_url() + "&prop=";
/*  886 */     str = str + "revisions&rvprop=content&revids=" + paramLong + "&format=xml";
/*  887 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_block_status(String paramString, boolean paramBoolean)
/*      */     throws Exception
/*      */   {
/*  900 */     String str = base_url() + "&list=blocks&";
/*  901 */     if (paramBoolean) str = str + "bkip=" + URLEncoder.encode(paramString, "UTF-8") + "&"; else
/*  902 */       str = str + "bkusers=" + URLEncoder.encode(paramString, "UTF-8") + "&";
/*  903 */     str = str + "&format=xml";
/*  904 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_page_flagged(long paramLong)
/*      */   {
/*  913 */     String str = base_url();
/*  914 */     str = str + "&prop=flagged&pageids=10000" + paramLong + "&format=xml";
/*  915 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_page_protected(long paramLong)
/*      */   {
/*  924 */     String str = base_url();
/*  925 */     str = str + "&prop=info&inprop=protection&pageids=" + paramLong + "&format=xml";
/*  926 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_autoconfirmed(String paramString)
/*      */     throws Exception
/*      */   {
/*  935 */     String str = base_url();
/*  936 */     str = str + "&list=allusers&auprop=editcount|registration&aufrom=";
/*  937 */     str = str + URLEncoder.encode(paramString, "UTF-8") + "&aulimit=1&format=xml";
/*  938 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_last_n_page_meta(long paramLong, int paramInt)
/*      */   {
/*  949 */     String str = base_url() + "&prop=revisions&pageids=" + paramLong;
/*  950 */     str = str + "&rvlimit=" + paramInt;
/*  951 */     str = str + "&rvtoken=rollback";
/*  952 */     str = str + "&rvprop=ids|timestamp|user|comment&rvdir=older&format=xml";
/*  953 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_next_n_page_meta(long paramLong1, long paramLong2, int paramInt)
/*      */   {
/*  965 */     String str = base_url() + "&prop=revisions&pageids=" + paramLong1;
/*  966 */     str = str + "&rvlimit=" + paramInt;
/*  967 */     str = str + "&rvstartid=" + paramLong2;
/*  968 */     str = str + "&rvtoken=rollback";
/*  969 */     str = str + "&rvprop=ids|timestamp|user|comment&rvdir=newer&format=xml";
/*  970 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_xlink(long paramLong1, long paramLong2)
/*      */     throws Exception
/*      */   {
/*  981 */     String str = base_url() + "&prop=extlinks&pageids=" + paramLong1;
/*  982 */     str = str + "&ellimit=500&eloffset=" + paramLong2 + "&format=xml";
/*  983 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_block_hist(String paramString)
/*      */     throws Exception
/*      */   {
/*  992 */     String str = base_url() + "&list=logevents&letype=block&letitle=User";
/*  993 */     str = str + URLEncoder.encode(new StringBuilder().append(":").append(paramString).toString(), "UTF-8");
/*  994 */     str = str + "&lelimit=500&ledir=older&format=xml";
/*  995 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_page_touched(Set<String> paramSet)
/*      */     throws Exception
/*      */   {
/* 1005 */     Iterator localIterator = paramSet.iterator();
/* 1006 */     String str = base_url() + "&prop=info&titles=";
/* 1007 */     while (localIterator.hasNext())
/* 1008 */       str = str + URLEncoder.encode((String)localIterator.next(), "UTF-8") + "|";
/* 1009 */     str = str.substring(0, str.length() - 1);
/* 1010 */     str = str + "&format=xml";
/* 1011 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_joint_contribs(List<String> paramList, String paramString)
/*      */     throws Exception
/*      */   {
/* 1024 */     Iterator localIterator = paramList.iterator();
/* 1025 */     String str = base_url() + "&list=usercontribs&ucuser=";
/* 1026 */     while (localIterator.hasNext())
/* 1027 */       str = str + URLEncoder.encode((String)localIterator.next(), "UTF-8") + "|";
/* 1028 */     str = str.substring(0, str.length() - 1);
/* 1029 */     str = str + "&uclimit=500&ucstart=" + paramString;
/* 1030 */     str = str + "&ucprop=ids&ucdir=newer&format=xml";
/* 1031 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_user_edits_date(String paramString, int paramInt1, long paramLong, int paramInt2)
/*      */     throws Exception
/*      */   {
/* 1047 */     String str = base_url() + "&list=usercontribs";
/* 1048 */     str = str + "&ucuser=" + URLEncoder.encode(paramString, "UTF-8");
/* 1049 */     str = str + "&ucprop=timestamp";
/* 1050 */     str = str + "&ucdir=older";
/* 1051 */     str = str + "&uclimit=" + paramInt2;
/* 1052 */     str = str + "&ucstart=" + stiki_utils.unix_ts_to_wiki(paramLong);
/* 1053 */     if (paramInt1 != 2147483647)
/* 1054 */       str = str + "&ucnamespace=" + paramInt1;
/* 1055 */     str = str + "&format=xml";
/* 1056 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_user_edits_ever(String paramString)
/*      */     throws Exception
/*      */   {
/* 1065 */     String str = base_url() + "&list=users";
/* 1066 */     str = str + "&ususers=" + URLEncoder.encode(paramString, "UTF-8");
/* 1067 */     str = str + "&usprop=editcount";
/* 1068 */     str = str + "&format=xml";
/* 1069 */     return str;
/*      */   }
/*      */ 
/*      */   private static String url_pages_missing(Set<String> paramSet)
/*      */     throws Exception
/*      */   {
/* 1079 */     StringBuilder localStringBuilder = new StringBuilder();
/* 1080 */     localStringBuilder.append(base_url() + "&titles=");
/* 1081 */     Iterator localIterator = paramSet.iterator();
/* 1082 */     while (localIterator.hasNext())
/* 1083 */       localStringBuilder.append(URLEncoder.encode((String)localIterator.next(), "UTF-8") + "|");
/* 1084 */     return localStringBuilder.substring(0, localStringBuilder.length() - 1) + "&format=xml";
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_retrieve
 * JD-Core Version:    0.6.0
 */