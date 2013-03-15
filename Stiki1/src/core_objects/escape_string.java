/*     */ package core_objects;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ 
/*     */ public class escape_string
/*     */ {
/*     */   public static String escape(String paramString)
/*     */     throws Exception
/*     */   {
/*  26 */     if (paramString == null) return null;
/*     */ 
/*  29 */     StringWriter localStringWriter = new StringWriter(paramString.length() * 2);
/*  30 */     escape_java_work(localStringWriter, paramString, false);
/*  31 */     return localStringWriter.toString();
/*     */   }
/*     */ 
/*     */   public static String unescape(String paramString)
/*     */     throws Exception
/*     */   {
/*  40 */     if (paramString == null) return null;
/*  41 */     StringWriter localStringWriter = new StringWriter(paramString.length());
/*  42 */     unescape_java(localStringWriter, paramString);
/*  43 */     return localStringWriter.toString();
/*     */   }
/*     */ 
/*     */   private static void escape_java_work(Writer paramWriter, String paramString, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  58 */     int i = paramString.length();
/*  59 */     for (int j = 0; j < i; j++) {
/*  60 */       int k = paramString.charAt(j);
/*  61 */       if (k > 4095)
/*  62 */         paramWriter.write("\\u" + hex(k));
/*  63 */       else if (k > 255)
/*  64 */         paramWriter.write("\\u0" + hex(k));
/*  65 */       else if (k > 127)
/*  66 */         paramWriter.write("\\u00" + hex(k));
/*  67 */       else if (k < 32)
/*  68 */         switch (k) { case 8:
/*  69 */           paramWriter.write(92); paramWriter.write(98); break;
/*     */         case 10:
/*  70 */           paramWriter.write(92); paramWriter.write(110); break;
/*     */         case 9:
/*  71 */           paramWriter.write(92); paramWriter.write(116); break;
/*     */         case 12:
/*  72 */           paramWriter.write(92); paramWriter.write(102); break;
/*     */         case 13:
/*  73 */           paramWriter.write(92); paramWriter.write(114); break;
/*     */         case 11:
/*     */         default:
/*  75 */           if (k > 15) { paramWriter.write("\\u00" + hex(k)); continue; }
/*  76 */           paramWriter.write("\\u000" + hex(k));
/*  77 */           break;
/*     */         }
/*     */       else
/*  80 */         switch (k) {
/*     */         case 39:
/*  82 */           if (paramBoolean) paramWriter.write(92);
/*  83 */           paramWriter.write(39);
/*  84 */           break;
/*     */         case 34:
/*  85 */           paramWriter.write(92); paramWriter.write(34); break;
/*     */         case 92:
/*  86 */           paramWriter.write(92); paramWriter.write(92); break;
/*     */         default:
/*  88 */           paramWriter.write(k);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String hex(char paramChar)
/*     */   {
/* 100 */     return Integer.toHexString(paramChar).toUpperCase();
/*     */   }
/*     */ 
/*     */   private static void unescape_java(Writer paramWriter, String paramString)
/*     */     throws IOException
/*     */   {
/* 111 */     int i = paramString.length();
/* 112 */     StringBuffer localStringBuffer = new StringBuffer(4);
/* 113 */     int j = 0;
/* 114 */     int k = 0;
/* 115 */     for (int m = 0; m < i; m++) {
/* 116 */       char c = paramString.charAt(m);
/* 117 */       if (k != 0) {
/* 118 */         localStringBuffer.append(c);
/* 119 */         if (localStringBuffer.length() == 4) {
/* 120 */           int n = Integer.parseInt(localStringBuffer.toString(), 16);
/* 121 */           paramWriter.write((char)n);
/* 122 */           localStringBuffer.setLength(0);
/* 123 */           k = 0;
/* 124 */           j = 0;
/*     */         }
/*     */ 
/*     */       }
/* 128 */       else if (j != 0) {
/* 129 */         j = 0;
/* 130 */         switch (c) { case '\\':
/* 131 */           paramWriter.write(92); break;
/*     */         case '\'':
/* 132 */           paramWriter.write(39); break;
/*     */         case '"':
/* 133 */           paramWriter.write(34); break;
/*     */         case 'r':
/* 134 */           paramWriter.write(13); break;
/*     */         case 'f':
/* 135 */           paramWriter.write(12); break;
/*     */         case 't':
/* 136 */           paramWriter.write(9); break;
/*     */         case 'n':
/* 137 */           paramWriter.write(10); break;
/*     */         case 'b':
/* 138 */           paramWriter.write(8); break;
/*     */         case 'u':
/* 139 */           k = 1; break;
/*     */         default:
/* 140 */           paramWriter.write(c); break;
/*     */         }
/*     */       }
/* 143 */       else if (c == '\\') {
/* 144 */         j = 1;
/*     */       }
/*     */       else {
/* 147 */         paramWriter.write(c);
/*     */       }
/*     */     }
/* 149 */     if (j != 0)
/* 150 */       paramWriter.write(92);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     core_objects.escape_string
 * JD-Core Version:    0.6.0
 */