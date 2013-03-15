/*     */ package gui_support;
/*     */ 
/*     */ import core_objects.stiki_utils;
/*     */ import core_objects.xlink_parser;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.TreeSet;
/*     */ import java.util.regex.Pattern;
/*     */ import mediawiki_api.api_retrieve;
/*     */ 
/*     */ public class diff_markup
/*     */ {
/*     */   public static void main(String[] paramArrayOfString)
/*     */     throws Exception
/*     */   {
/*  45 */     String str = api_retrieve.process_diff_prev(495022990L);
/*  46 */     System.out.println(beautify_markup(str, "Title", "", true));
/*     */   }
/*     */ 
/*     */   public static String beautify_markup(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
/*     */   {
/*  66 */     String str = "<html><body>";
/*  67 */     str = str + "<center><br><b><u>" + paramString2 + "</u></b>";
/*  68 */     if (paramString3.length() > 0)
/*  69 */       str = str + "<br><font color=\"purple\">" + paramString3 + "</font>";
/*  70 */     str = str + "<br><br>";
/*  71 */     str = str + "<table border=\"0\" cellspacing=\"5\">";
/*  72 */     str = str + paramString1 + "</center></table></body></html>";
/*     */ 
/*  75 */     str = markup_table_style(str);
/*  76 */     str = markup_delete_unneccesary(str);
/*  77 */     str = markup_cell_widths(str);
/*     */ 
/*  79 */     if (paramBoolean)
/*  80 */       str = add_hyperlinks(str);
/*  81 */     return str;
/*     */   }
/*     */ 
/*     */   private static String markup_table_style(String paramString)
/*     */   {
/*  94 */     paramString = paramString.replace("<span class=\"diffchange diffchange-inline\">", "<font color=#ff0000><b>");
/*     */ 
/*  96 */     paramString = paramString.replace("</span>", "</b></font>");
/*     */ 
/*  99 */     paramString = paramString.replace("class=\"diff-lineno\"", "");
/*     */ 
/* 102 */     paramString = paramString.replace("class=\"diff-context\"", "bgcolor=#eeeeee");
/* 103 */     paramString = paramString.replace("class=\"diff-deletedline\"", "bgcolor=#ffffaa");
/* 104 */     paramString = paramString.replace("class=\"diff-addedline\"", "bgcolor=#ccffcc");
/* 105 */     return paramString;
/*     */   }
/*     */ 
/*     */   private static String markup_delete_unneccesary(String paramString)
/*     */   {
/* 115 */     paramString = paramString.replaceAll("<td class=\"diff-marker\">.*</td>", "");
/* 116 */     paramString = paramString.replace(" colspan=\"2\"", "");
/*     */ 
/* 121 */     paramString = paramString.replaceAll("(<div>|</div>)", "");
/*     */ 
/* 123 */     paramString = paramString.replaceAll("<tr>\\s*</tr>", "");
/* 124 */     paramString = paramString.replaceAll("\\n\\s*\\n", "\n");
/* 125 */     return paramString;
/*     */   }
/*     */ 
/*     */   private static String markup_cell_widths(String paramString)
/*     */   {
/* 132 */     paramString = paramString.replace("<td", "<td width=\"50%\"");
/* 133 */     return paramString;
/*     */   }
/*     */ 
/*     */   private static String add_hyperlinks(String paramString)
/*     */   {
/* 147 */     paramString = " " + paramString;
/* 148 */     String str1 = paramString.replaceAll("<[^<]*>", "");
/* 149 */     List localList = xlink_parser.parse_xlinks(str1);
/*     */ 
/* 155 */     paramString = paramString.replace("<font color=#ff0000><b>", "<@>");
/* 156 */     paramString = paramString.replace("</b></font>", "</@>");
/*     */ 
/* 158 */     LinkedList localLinkedList = new LinkedList(new TreeSet(localList));
/*     */ 
/* 160 */     Collections.sort(localLinkedList, new Comparator() {
/*     */       public int compare(String paramString1, String paramString2) {
/* 162 */         return paramString2.length() - paramString1.length();
/*     */       }
/*     */     });
/* 169 */     int i = 65533;
/* 170 */     String str2 = "\\uFFFD";
/*     */ 
/* 174 */     Iterator localIterator = localLinkedList.iterator();
/* 175 */     if (localIterator.hasNext()) {
/* 176 */       String str3 = (String)localIterator.next();
/* 177 */       String str4 = produce_url_search_regex(str3);
/*     */       while (true) {
/* 179 */         String str5 = stiki_utils.first_match_within(str4, paramString);
/* 180 */         if (str5 == null)
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/* 186 */         Character localCharacter = Character.valueOf(str5.charAt(0));
/* 187 */         str5 = str5.substring(1);
/* 188 */         paramString = paramString.replaceFirst("[^" + str2 + "]" + Pattern.quote(str5), localCharacter + "<A HREF=\"" + i + str3 + "\">" + i + str5 + "</A>");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 197 */     paramString = paramString.replace("<@>", "<font color=#ff0000><b>");
/* 198 */     paramString = paramString.replace("</@>", "</b></font>");
/* 199 */     paramString = paramString.replaceAll(str2, "");
/* 200 */     paramString = paramString.substring(1);
/* 201 */     return paramString;
/*     */   }
/*     */ 
/*     */   private static String produce_url_search_regex(String paramString)
/*     */   {
/* 218 */     String str1 = "[<@>|</@>]*";
/* 219 */     String str2 = "[^\\uFFFD]";
/* 220 */     StringBuilder localStringBuilder = new StringBuilder(str2);
/* 221 */     for (int i = 0; i < paramString.length(); i++) {
/* 222 */       if (i == paramString.length() - 1)
/* 223 */         localStringBuilder.append(Pattern.quote(paramString.charAt(i) + ""));
/* 224 */       else localStringBuilder.append(Pattern.quote(new StringBuilder().append(paramString.charAt(i)).append("").toString()) + str1);
/*     */     }
/* 226 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.diff_markup
 * JD-Core Version:    0.6.0
 */