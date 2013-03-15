/*     */ package gui_support;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URI;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class url_browse
/*     */ {
/*  38 */   static final String[] browsers = { "google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };
/*     */   static final String error_msg = "Error attempting to launch web browser";
/*     */ 
/*     */   public static void openURL(String paramString)
/*     */   {
/*  54 */     paramString = adjust_protocol(paramString);
/*     */     try { Class localClass = Class.forName("java.awt.Desktop");
/*  56 */       localClass.getDeclaredMethod("browse", new Class[] { URI.class }).invoke(localClass.getDeclaredMethod("getDesktop", new Class[0]).invoke(null, new Object[0]), new Object[] { URI.create(paramString) });
/*     */     }
/*     */     catch (Exception localException1)
/*     */     {
/*  62 */       String str1 = System.getProperty("os.name");
/*     */       try { if (str1.startsWith("Mac OS")) {
/*  64 */           Class.forName("com.apple.eio.FileManager").getDeclaredMethod("openURL", new Class[] { String.class }).invoke(null, new Object[] { paramString });
/*     */         }
/*  67 */         else if (str1.startsWith("Windows")) {
/*  68 */           Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + paramString);
/*     */         } else {
/*  70 */           Object localObject = null;
/*  71 */           for (String str2 : browsers) {
/*  72 */             if (localObject != null) continue; if (Runtime.getRuntime().exec(new String[] { "which", str2 }).getInputStream().read() == -1)
/*     */               continue;
/*     */             String[] tmp223_220 = new String[2];
/*     */             String[] tmp227_225 = tmp223_220; localObject = tmp227_225; tmp223_220[0] = tmp227_225;
/*     */             String[] tmp230_223 = tmp223_220; tmp230_223[1] = paramString; Runtime.getRuntime().exec(tmp230_223);
/*  75 */           }if (localObject == null)
/*  76 */             throw new Exception(Arrays.toString(browsers));
/*     */         }
/*     */       } catch (Exception localException2)
/*     */       {
/*  80 */         new Exception("Error attempting to launch web browser");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String adjust_protocol(String paramString)
/*     */   {
/*  99 */     if ((gui_settings.get_bool_def(gui_settings.SETTINGS_BOOL.options_https, false)) && (paramString.contains(".wikipedia.org")))
/*     */     {
/* 101 */       return paramString.replace("http://", "https://");
/* 102 */     }return paramString;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     gui_support.url_browse
 * JD-Core Version:    0.6.0
 */