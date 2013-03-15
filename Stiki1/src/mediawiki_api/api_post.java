/*     */ package mediawiki_api;
/*     */ 
/*     */ import core_objects.pair;
/*     */ import gui_support.gui_settings;
/*     */ import gui_support.gui_settings.SETTINGS_BOOL;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class api_post
/*     */ {
/*  63 */   public static String BASE_URL_OVERRIDE = null;
/*     */ 
/*     */   public static String base_url()
/*     */   {
/*  75 */     if (BASE_URL_OVERRIDE != null) {
/*  76 */       return BASE_URL_OVERRIDE;
/*     */     }
/*  78 */     String str = "en.wikipedia.org/w/api.php";
/*  79 */     if (gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.options_https, false))
/*  80 */       return "https://" + str;
/*  81 */     return "http://" + str;
/*     */   }
/*     */ 
/*     */   public static String process_login(String paramString1, String paramString2)
/*     */     throws Exception
/*     */   {
/* 101 */     String str1 = "action=login";
/* 102 */     str1 = str1 + "&lgname=" + URLEncoder.encode(paramString1, "UTF-8");
/* 103 */     str1 = str1 + "&lgpassword=" + URLEncoder.encode(paramString2, "UTF-8");
/* 104 */     URLConnection localURLConnection1 = post(str1 + "&format=xml", null);
/*     */ 
/* 108 */     api_xml_login localapi_xml_login1 = new api_xml_login();
/* 109 */     do_parse_work(localURLConnection1.getInputStream(), localapi_xml_login1);
/* 110 */     String str2 = localapi_xml_login1.get_result();
/* 111 */     String str3 = get_single_cookie_from_response(localURLConnection1);
/*     */ 
/* 115 */     str1 = str1 + "&lgtoken=" + URLEncoder.encode(str2, "UTF-8");
/* 116 */     str1 = str1 + "&format=xml";
/* 117 */     URLConnection localURLConnection2 = post(str1, str3);
/* 118 */     api_xml_login localapi_xml_login2 = new api_xml_login();
/* 119 */     do_parse_work(localURLConnection2.getInputStream(), localapi_xml_login2);
/* 120 */     return localapi_xml_login2.get_result();
/*     */   }
/*     */ 
/*     */   public static void process_logout()
/*     */     throws Exception
/*     */   {
/* 127 */     post("action=logout", null);
/*     */   }
/*     */ 
/*     */   public static InputStream edit_revert(long paramLong, String paramString1, String paramString2, boolean paramBoolean1, pair<String, String> parampair, String paramString3, boolean paramBoolean2)
/*     */     throws Exception
/*     */   {
/* 149 */     String str = "action=edit";
/* 150 */     str = str + "&undo=" + paramLong;
/* 151 */     str = str + "&title=" + URLEncoder.encode(paramString1, "UTF-8");
/* 152 */     str = str + "&summary=" + URLEncoder.encode(paramString2, "UTF-8");
/* 153 */     if (paramBoolean1)
/* 154 */       str = str + "&minor=true";
/* 155 */     else str = str + "&notminor=true";
/* 156 */     str = str + "&token=" + URLEncoder.encode((String)parampair.fst, "UTF-8");
/* 157 */     str = str + "&starttimestamp=" + (String)parampair.snd;
/* 158 */     if (paramBoolean2)
/* 159 */       str = str + "&watchlist=nochange";
/* 160 */     str = str + "&format=xml";
/* 161 */     return post(str, paramString3).getInputStream();
/*     */   }
/*     */ 
/*     */   public static InputStream edit_rollback(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean)
/*     */     throws Exception
/*     */   {
/* 179 */     String str = "action=rollback";
/* 180 */     str = str + "&title=" + URLEncoder.encode(paramString1, "UTF-8");
/* 181 */     str = str + "&user=" + URLEncoder.encode(paramString2, "UTF-8");
/* 182 */     str = str + "&summary=" + URLEncoder.encode(paramString3, "UTF-8");
/* 183 */     str = str + "&token=" + URLEncoder.encode(paramString4, "UTF-8");
/* 184 */     if (paramBoolean)
/* 185 */       str = str + "&watchlist=nochange";
/* 186 */     str = str + "&format=xml";
/* 187 */     return post(str, paramString5).getInputStream();
/*     */   }
/*     */ 
/*     */   public static InputStream edit_append_text(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, pair<String, String> parampair, String paramString4, boolean paramBoolean2, boolean paramBoolean3)
/*     */     throws Exception
/*     */   {
/* 208 */     String str = "action=edit";
/* 209 */     str = str + "&title=" + URLEncoder.encode(paramString1, "UTF-8");
/* 210 */     str = str + "&summary=" + URLEncoder.encode(paramString2, "UTF-8");
/* 211 */     str = str + "&appendtext=" + URLEncoder.encode(paramString3, "UTF-8");
/* 212 */     if (paramBoolean1)
/* 213 */       str = str + "&minor=true";
/* 214 */     else str = str + "&notminor=true";
/*     */ 
/* 216 */     str = str + "&token=" + URLEncoder.encode((String)parampair.fst, "UTF-8");
/* 217 */     if (!paramBoolean2)
/* 218 */       str = str + "&starttimestamp=" + (String)parampair.snd;
/* 219 */     if (paramBoolean3)
/* 220 */       str = str + "&watchlist=nochange";
/* 221 */     str = str + "&format=xml";
/* 222 */     return post(str, paramString4).getInputStream();
/*     */   }
/*     */ 
/*     */   public static InputStream edit_prepend_text(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, pair<String, String> parampair, String paramString4, boolean paramBoolean2, boolean paramBoolean3)
/*     */     throws Exception
/*     */   {
/* 243 */     String str = "action=edit";
/* 244 */     str = str + "&title=" + URLEncoder.encode(paramString1, "UTF-8");
/* 245 */     str = str + "&summary=" + URLEncoder.encode(paramString2, "UTF-8");
/* 246 */     str = str + "&prependtext=" + URLEncoder.encode(paramString3, "UTF-8");
/* 247 */     if (paramBoolean1)
/* 248 */       str = str + "&minor=true";
/* 249 */     else str = str + "&notminor=true";
/*     */ 
/* 251 */     str = str + "&token=" + URLEncoder.encode((String)parampair.fst, "UTF-8");
/* 252 */     if (!paramBoolean2)
/* 253 */       str = str + "&starttimestamp=" + (String)parampair.snd;
/* 254 */     if (paramBoolean3)
/* 255 */       str = str + "&watchlist=nochange";
/* 256 */     str = str + "&format=xml";
/* 257 */     return post(str, paramString4).getInputStream();
/*     */   }
/*     */ 
/*     */   public static InputStream edit_full_text(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, pair<String, String> parampair, String paramString4, boolean paramBoolean2, boolean paramBoolean3)
/*     */     throws Exception
/*     */   {
/* 277 */     String str = "action=edit";
/* 278 */     str = str + "&title=" + URLEncoder.encode(paramString1, "UTF-8");
/* 279 */     str = str + "&summary=" + URLEncoder.encode(paramString2, "UTF-8");
/* 280 */     str = str + "&text=" + URLEncoder.encode(paramString3, "UTF-8");
/* 281 */     if (paramBoolean1)
/* 282 */       str = str + "&minor=true";
/* 283 */     else str = str + "&notminor=true";
/*     */ 
/* 285 */     str = str + "&token=" + URLEncoder.encode((String)parampair.fst, "UTF-8");
/* 286 */     if (!paramBoolean2)
/* 287 */       str = str + "&starttimestamp=" + (String)parampair.snd;
/* 288 */     if (paramBoolean3)
/* 289 */       str = str + "&watchlist=nochange";
/* 290 */     str = str + "&format=xml";
/* 291 */     return post(str, paramString4).getInputStream();
/*     */   }
/*     */ 
/*     */   public static EDIT_OUTCOME edit_was_made(InputStream paramInputStream)
/*     */     throws Exception
/*     */   {
/* 305 */     api_xml_edit_response localapi_xml_edit_response = new api_xml_edit_response();
/* 306 */     do_parse_work(paramInputStream, localapi_xml_edit_response);
/* 307 */     return localapi_xml_edit_response.get_result();
/*     */   }
/*     */ 
/*     */   public static long rollback_response(InputStream paramInputStream)
/*     */     throws Exception
/*     */   {
/* 318 */     api_xml_rb_response localapi_xml_rb_response = new api_xml_rb_response();
/* 319 */     do_parse_work(paramInputStream, localapi_xml_rb_response);
/* 320 */     return localapi_xml_rb_response.get_result();
/*     */   }
/*     */ 
/*     */   private static URLConnection post(String paramString1, String paramString2)
/*     */     throws Exception
/*     */   {
/* 341 */     URL localURL = new URL(base_url());
/* 342 */     URLConnection localURLConnection = localURL.openConnection();
/*     */ 
/* 345 */     if ((paramString2 != null) && (!paramString2.equals(""))) {
/* 346 */       localURLConnection.setRequestProperty("Cookie", paramString2);
/*     */     }
/*     */ 
/* 349 */     localURLConnection.setDoOutput(true);
/* 350 */     OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localURLConnection.getOutputStream());
/* 351 */     localOutputStreamWriter.write(paramString1);
/* 352 */     localOutputStreamWriter.flush();
/* 353 */     return localURLConnection;
/*     */   }
/*     */ 
/*     */   private static void do_parse_work(InputStream paramInputStream, DefaultHandler paramDefaultHandler)
/*     */     throws Exception
/*     */   {
/* 363 */     SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
/* 364 */     SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
/* 365 */     localSAXParser.parse(paramInputStream, paramDefaultHandler);
/* 366 */     paramInputStream.close();
/*     */   }
/*     */ 
/*     */   private static String get_single_cookie_from_response(URLConnection paramURLConnection)
/*     */   {
/* 378 */     String str1 = ""; String str2 = null;
/* 379 */     for (int i = 1; (str2 = paramURLConnection.getHeaderFieldKey(i)) != null; i++) {
/* 380 */       if (str2.equalsIgnoreCase("Set-Cookie"))
/* 381 */         str1 = paramURLConnection.getHeaderField(i);
/*     */     }
/* 383 */     return str1;
/*     */   }
/*     */ 
/*     */   public static enum EDIT_OUTCOME
/*     */   {
/*  55 */     SUCCESS, BEATEN, ERROR;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     mediawiki_api.api_post
 * JD-Core Version:    0.6.0
 */