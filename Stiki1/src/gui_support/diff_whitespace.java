/*     */ package gui_support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class diff_whitespace
/*     */ {
/*     */   public static final int MAX_CHARS_WO_SPACE = 10;
/*     */ 
/*     */   public static String whitespace_diff_html(String paramString)
/*     */     throws Exception
/*     */   {
/*  41 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/*  43 */     int i = 0;
/*  44 */     int j = 0;
/*     */ 
/*  47 */     for (int k = 0; k < paramString.length(); k++) {
/*  48 */       char c = paramString.charAt(k);
/*  49 */       if (c == '<') {
/*  50 */         i = 1;
/*     */       }
/*  52 */       if (i == 0) {
/*  53 */         j++;
/*  54 */         if (Character.isWhitespace(c))
/*  55 */           j = 0;
/*  56 */         if (j == 10) {
/*  57 */           localArrayList.add(Character.valueOf(c));
/*  58 */           localArrayList.add(Character.valueOf('​'));
/*  59 */           j = 0;
/*     */         } else {
/*  61 */           localArrayList.add(Character.valueOf(c));
/*     */         }
/*     */       } else {
/*  63 */         localArrayList.add(Character.valueOf(c));
/*     */       }
/*  65 */       if (c == '>') {
/*  66 */         i = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  71 */     char[] arrayOfChar = new char[localArrayList.size()];
/*  72 */     for (int m = 0; m < localArrayList.size(); m++)
/*  73 */       arrayOfChar[m] = ((Character)localArrayList.get(m)).charValue();
/*  74 */     String str = String.copyValueOf(arrayOfChar);
/*     */ 
/*  78 */     str = str.replaceAll("\\&[​]*l[​]*t[​]*;|\\&[​]*l[​]*;[​]*t[​]*;", "\\&lt;");
/*     */ 
/*  80 */     str = str.replaceAll("\\&[​]*g[​]*t[​]*;|\\&[​]*g[​]*;[​]*t[​]*;", "\\&gt;");
/*     */ 
/*  82 */     return str;
/*     */   }
/*     */ 
/*     */   public static String strip_zws_chars(String paramString)
/*     */   {
/*  91 */     return paramString.replace("​", "");
/*     */   }
/*     */ 
/*     */   public static String insert_zws_every_n_chars(String paramString, int paramInt)
/*     */   {
/* 108 */     String str1 = paramString;
/* 109 */     String str2 = "";
/*     */ 
/* 111 */     while (str1.length() > paramInt) {
/* 112 */       str2 = str2 + str1.substring(0, paramInt) + "​";
/* 113 */       str1 = str1.substring(paramInt);
/*     */     }
/* 115 */     str2 = str2 + str1;
/* 116 */     return str2;
/*     */   }
/*     */ 
/*     */   public static int max_chars_wo_space(String paramString)
/*     */   {
/* 127 */     int i = 0;
/* 128 */     String[] arrayOfString = paramString.split("(\\s|​)");
/* 129 */     for (int j = 0; j < arrayOfString.length; j++)
/* 130 */       i = Math.max(i, arrayOfString[j].length());
/* 131 */     return i;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.diff_whitespace
 * JD-Core Version:    0.6.0
 */