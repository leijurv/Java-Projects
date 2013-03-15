/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public class Messages
/*     */ {
/*     */   private static final String BUNDLE_NAME = "com.mysql.jdbc.LocalizedErrorMessages";
/*     */   private static final ResourceBundle RESOURCE_BUNDLE;
/*     */ 
/*     */   public static String getString(String key)
/*     */   {
/*  83 */     if (RESOURCE_BUNDLE == null) {
/*  84 */       throw new RuntimeException("Localized messages from resource bundle 'com.mysql.jdbc.LocalizedErrorMessages' not loaded during initialization of driver.");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  90 */       if (key == null) {
/*  91 */         throw new IllegalArgumentException("Message key can not be null");
/*     */       }
/*     */ 
/*  95 */       String message = RESOURCE_BUNDLE.getString(key);
/*     */ 
/*  97 */       if (message == null) {
/*  98 */         message = "Missing error message for key '" + key + "'";
/*     */       }
/*     */ 
/* 101 */       return message; } catch (MissingResourceException e) {
/*     */     }
/* 103 */     return '!' + key + '!';
/*     */   }
/*     */ 
/*     */   public static String getString(String key, Object[] args)
/*     */   {
/* 108 */     return MessageFormat.format(getString(key), args);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  48 */     ResourceBundle temp = null;
/*     */     try
/*     */     {
/*  57 */       temp = ResourceBundle.getBundle("com.mysql.jdbc.LocalizedErrorMessages", Locale.getDefault(), Messages.class.getClassLoader());
/*     */     }
/*     */     catch (Throwable t) {
/*     */       try {
/*  61 */         temp = ResourceBundle.getBundle("com.mysql.jdbc.LocalizedErrorMessages");
/*     */       } catch (Throwable t2) {
/*  63 */         RuntimeException rt = new RuntimeException("Can't load resource bundle due to underlying exception " + t.toString());
/*     */ 
/*  66 */         rt.initCause(t2);
/*     */ 
/*  68 */         throw rt;
/*     */       }
/*     */     } finally {
/*  71 */       RESOURCE_BUNDLE = temp;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Messages
 * JD-Core Version:    0.6.0
 */