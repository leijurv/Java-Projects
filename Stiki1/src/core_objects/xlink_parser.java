/*     */ package core_objects;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import mediawiki_api.api_retrieve;
/*     */ 
/*     */ public class xlink_parser
/*     */ {
/*     */   private static final String REGEXP_COMMENT = "<!--(?:[^-]+|-)*?-->";
/*     */   private static final String REGEXP_SPLIT = "(?=(?:\\{\\{|\\}\\}))";
/*     */   private static final String REGEXP_PROTS = "(?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)";
/*     */   private static final String URL_CHAR = "[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}]";
/*     */   private static final String URL_CHAR_MINUS_PIPE = "[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]";
/*     */   private static final String REGEXP_PLAIN = "(\\b(?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}]+)";
/*     */   private static final String REGEXP_PLAIN_TEMPLATE = "(\\b(?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+)";
/*     */   private static final String REGEXP_STRIP_CHAR = "[,;.:!?]+$";
/*     */   private static final String REGEXP_STRIP_CHAR_PLUS_PAREN = "[,;.:!?)]+$";
/*     */   private static final String REGEXP_URL_TEMPLATE = "^\\{\\{\\s*(?:URL|Official website)\\s*\\|";
/*     */   private static final String REGEXP_URL_TEMPLATE_PLAIN = "^\\{\\{\\s*(?:URL|Official website)\\s*\\|\\s*(?:(?:1|url|mobile)\\s*=\\s*)?((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)?[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+)";
/*     */   private static final String REGEXP_URL_TEMPLATE_NAMED = "|\\s*(?:1|url|mobile)\\s*=\\s*((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)?[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+)";
/*     */   private static final String REGEXP_URL_TEMPLATE_URL = "(?:^\\{\\{\\s*(?:URL|Official website)\\s*\\|\\s*(?:(?:1|url|mobile)\\s*=\\s*)?((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)?[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+))|(?:|\\s*(?:1|url|mobile)\\s*=\\s*((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)?[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+))";
/*     */   private static final String REGEXP_PLAIN_LINK_TEMPLATE = "^\\{\\{\\s*plain\\s*link\\s*\\|";
/*     */   private static final String REGEXP_PLAIN_TEMPLATE_PLAIN = "^\\{\\{\\s*plain\\s*link\\s*\\|\\s*(?:(?:1|url)\\s*=\\s*)?((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+)";
/*     */   private static final String REGEXP_PLAIN_TEMPLATE_NAMED = "|\\s*(?:1|url)\\s*=\\s*((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+)";
/*     */   private static final String REGEXP_PLAIN_TEMPLATE_URL = "(?:^\\{\\{\\s*plain\\s*link\\s*\\|\\s*(?:(?:1|url)\\s*=\\s*)?((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+))|(?:|\\s*(?:1|url)\\s*=\\s*((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+))";
/*     */   private static final String REGEXP_HOSTNAME = "^(?:(?:[a-zA-Z0-9][-.a-zA-Z0-9]+)|(?:\\[[a-fA-F9:.]{2,}\\]))$";
/* 112 */   private static final Pattern split_pattern = Pattern.compile("(?=(?:\\{\\{|\\}\\}))");
/*     */ 
/* 114 */   private static final Pattern plain_pattern = Pattern.compile("(\\b(?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}]+)");
/*     */ 
/* 116 */   private static final Pattern url_template_pattern = Pattern.compile("^\\{\\{\\s*(?:URL|Official website)\\s*\\|", 2);
/*     */ 
/* 118 */   private static final Pattern url_template_url_pattern = Pattern.compile("(?:^\\{\\{\\s*(?:URL|Official website)\\s*\\|\\s*(?:(?:1|url|mobile)\\s*=\\s*)?((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)?[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+))|(?:|\\s*(?:1|url|mobile)\\s*=\\s*((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)?[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+))", 2);
/*     */ 
/* 120 */   private static final Pattern url_template_plain_pattern = Pattern.compile("(?:^\\{\\{\\s*plain\\s*link\\s*\\|\\s*(?:(?:1|url)\\s*=\\s*)?((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+))|(?:|\\s*(?:1|url)\\s*=\\s*((?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+))", 2);
/*     */ 
/* 122 */   private static final Pattern plain_link_pattern = Pattern.compile("^\\{\\{\\s*plain\\s*link\\s*\\|", 2);
/*     */ 
/* 124 */   private static final Pattern plain_template_pattern = Pattern.compile("(\\b(?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)[^\\]\\[<>\"\\x00-\\x20\\x7F\\p{Zs}|]+)", 2);
/*     */ 
/* 126 */   private static final Pattern prots_pattern = Pattern.compile("(?:(?:https?|ftp|irc|ircs|gopher|telnet|nntp|worldwind|svn|git|mms)://|news:|mailto:)", 2);
/*     */ 
/* 128 */   private static final Pattern hostname_pattern = Pattern.compile("^(?:(?:[a-zA-Z0-9][-.a-zA-Z0-9]+)|(?:\\[[a-fA-F9:.]{2,}\\]))$");
/*     */ 
/*     */   public static void main(String[] paramArrayOfString)
/*     */     throws Exception
/*     */   {
/* 141 */     String str1 = "User:Allens/sandbox";
/* 142 */     long l = 33658968L;
/* 143 */     String str2 = api_retrieve.process_page_content(str1);
/*     */ 
/* 147 */     List localList1 = parse_xlinks(str2);
/* 148 */     List localList2 = api_set_agreement(localList1, l);
/*     */ 
/* 150 */     System.out.println("URLs API/parse in common: " + localList2.size());
/*     */   }
/*     */ 
/*     */   public static List<String> parse_xlinks(String paramString)
/*     */   {
/* 164 */     ArrayList localArrayList = new ArrayList();
/* 165 */     paramString = paramString.replaceAll("<!--(?:[^-]+|-)*?-->", "");
/* 166 */     paramString = paramString.replace("&amp;", "&");
/* 167 */     paramString = paramString.replace("&lt;", "<");
/* 168 */     paramString = paramString.replace("&gt;", ">");
/*     */ 
/* 170 */     int i = 0;
/* 171 */     String[] arrayOfString = split_pattern.split(paramString);
/* 172 */     for (int j = 0; j < arrayOfString.length; j++) {
/* 173 */       if (arrayOfString[j].startsWith("{{")) {
/* 174 */         i++;
/* 175 */       } else if (arrayOfString[j].startsWith("}}")) {
/* 176 */         i--;
/* 177 */         if (i < 0) {
/* 178 */           i = 0;
/*     */         }
/*     */       }
/* 181 */       if (i == 0)
/* 182 */         localArrayList.addAll(parse_pattern(arrayOfString[j], plain_pattern));
/* 183 */       else if (stiki_utils.has_match_within(url_template_pattern, arrayOfString[j]))
/* 184 */         localArrayList.addAll(parse_pattern(arrayOfString[j], url_template_url_pattern));
/* 185 */       else if (stiki_utils.has_match_within(plain_link_pattern, arrayOfString[j]))
/* 186 */         localArrayList.addAll(parse_pattern(arrayOfString[j], url_template_plain_pattern));
/*     */       else
/* 188 */         localArrayList.addAll(parse_pattern(arrayOfString[j], plain_template_pattern));
/*     */     }
/* 190 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public static List<String> api_set_agreement(List<String> paramList, long paramLong)
/*     */   {
/*     */     try
/*     */     {
/* 210 */       ArrayList localArrayList = new ArrayList();
/* 211 */       Set localSet = api_retrieve.process_xlinks(paramLong, 0);
/* 212 */       for (int i = 0; i < paramList.size(); i++) {
/* 213 */         String str = (String)paramList.get(i);
/* 214 */         str = str.replace("&amp;", "&");
/* 215 */         if (localSet.contains(str)) {
/* 216 */           localArrayList.add(str);
/*     */         }
/*     */       }
/* 219 */       return localArrayList;
/*     */     } catch (Exception localException) {
/* 221 */       System.out.println("Error in hyperlink-API agreement check:");
/* 222 */       localException.printStackTrace();
/* 223 */     }return null;
/*     */   }
/*     */ 
/*     */   private static List<String> parse_pattern(String paramString, Pattern paramPattern)
/*     */   {
/* 241 */     ArrayList localArrayList = new ArrayList();
/* 242 */     List localList = all_matches_within(paramPattern, paramString, 1);
/* 243 */     Iterator localIterator = localList.iterator();
/* 244 */     while (localIterator.hasNext()) {
/* 245 */       String str = (String)localIterator.next();
/* 246 */       if (str == null)
/*     */         continue;
/* 248 */       if (str.indexOf("(") == -1)
/* 249 */         str = str.replaceAll("[,;.:!?)]+$", "");
/* 250 */       else str = str.replaceAll("[,;.:!?]+$", "");
/*     */ 
/* 252 */       if ((paramPattern.equals(url_template_url_pattern)) && 
/* 253 */         (!stiki_utils.has_match_within(prots_pattern, str))) {
/* 254 */         str = "http://" + str;
/*     */       }
/*     */ 
/* 257 */       str = uri_sanity_check(str);
/* 258 */       if (str != null)
/* 259 */         localArrayList.add(str);
/*     */     }
/* 261 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   private static String uri_sanity_check(String paramString)
/*     */   {
/*     */     URI localURI;
/*     */     try
/*     */     {
/* 271 */       localURI = new URI(paramString); } catch (Exception localException1) {
/* 272 */       return null;
/* 273 */     }if (!localURI.isOpaque()) {
/*     */       try { localURI = localURI.parseServerAuthority(); } catch (Exception localException2) {
/* 275 */         return null;
/* 276 */       }paramString = localURI.getHost();
/* 277 */       if (paramString == null)
/* 278 */         return null;
/* 279 */       if (!stiki_utils.has_match_within(hostname_pattern, paramString))
/* 280 */         return null;
/*     */     }
/* 282 */     return localURI.toASCIIString();
/*     */   }
/*     */ 
/*     */   private static List<String> all_matches_within(Pattern paramPattern, String paramString, int paramInt)
/*     */   {
/* 295 */     ArrayList localArrayList = new ArrayList();
/* 296 */     Matcher localMatcher = paramPattern.matcher(paramString);
/* 297 */     while (localMatcher.find())
/* 298 */       localArrayList.add(localMatcher.group(paramInt));
/* 299 */     return localArrayList;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     core_objects.xlink_parser
 * JD-Core Version:    0.6.0
 */