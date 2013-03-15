/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ public class Constants
/*     */ {
/*  41 */   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */ 
/*  46 */   public static final String MILLIS_I18N = Messages.getString("Milliseconds");
/*     */ 
/*  48 */   public static final byte[] SLASH_STAR_SPACE_AS_BYTES = { 47, 42, 32 };
/*     */ 
/*  51 */   public static final byte[] SPACE_STAR_SLASH_SPACE_AS_BYTES = { 32, 42, 47, 32 };
/*     */ 
/*  57 */   private static final Character[] CHARACTER_CACHE = new Character[''];
/*     */   private static final int BYTE_CACHE_OFFSET = 128;
/*  61 */   private static final Byte[] BYTE_CACHE = new Byte[256];
/*     */   private static final int INTEGER_CACHE_OFFSET = 128;
/*  65 */   private static final Integer[] INTEGER_CACHE = new Integer[256];
/*     */   private static final int SHORT_CACHE_OFFSET = 128;
/*  69 */   private static final Short[] SHORT_CACHE = new Short[256];
/*     */ 
/*  71 */   private static final Long[] LONG_CACHE = new Long[256];
/*     */   private static final int LONG_CACHE_OFFSET = 128;
/*     */ 
/*     */   public static Character characterValueOf(char c)
/*     */   {
/*  99 */     if (c <= '') {
/* 100 */       return CHARACTER_CACHE[c];
/*     */     }
/*     */ 
/* 103 */     return new Character(c);
/*     */   }
/*     */ 
/*     */   public static final Byte byteValueOf(byte b)
/*     */   {
/* 109 */     return BYTE_CACHE[(b + 128)];
/*     */   }
/*     */ 
/*     */   public static final Integer integerValueOf(int i)
/*     */   {
/* 115 */     if ((i >= -128) && (i <= 127)) {
/* 116 */       return INTEGER_CACHE[(i + 128)];
/*     */     }
/*     */ 
/* 119 */     return new Integer(i);
/*     */   }
/*     */ 
/*     */   public static Short shortValueOf(short s)
/*     */   {
/* 126 */     if ((s >= -128) && (s <= 127)) {
/* 127 */       return SHORT_CACHE[(s + 128)];
/*     */     }
/*     */ 
/* 130 */     return new Short(s);
/*     */   }
/*     */ 
/*     */   public static final Long longValueOf(long l)
/*     */   {
/* 136 */     if ((l >= -128L) && (l <= 127L)) {
/* 137 */       return LONG_CACHE[((int)l + 128)];
/*     */     }
/*     */ 
/* 140 */     return new Long(l);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  76 */     for (int i = 0; i < CHARACTER_CACHE.length; i++) {
/*  77 */       CHARACTER_CACHE[i] = new Character((char)i);
/*     */     }
/*     */ 
/*  80 */     for (int i = 0; i < INTEGER_CACHE.length; i++) {
/*  81 */       INTEGER_CACHE[i] = new Integer(i - 128);
/*     */     }
/*     */ 
/*  84 */     for (int i = 0; i < SHORT_CACHE.length; i++) {
/*  85 */       SHORT_CACHE[i] = new Short((short)(i - 128));
/*     */     }
/*     */ 
/*  88 */     for (int i = 0; i < LONG_CACHE.length; i++) {
/*  89 */       LONG_CACHE[i] = new Long(i - 128);
/*     */     }
/*     */ 
/*  92 */     for (int i = 0; i < BYTE_CACHE.length; i++)
/*  93 */       BYTE_CACHE[i] = new Byte((byte)(i - 128));
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Constants
 * JD-Core Version:    0.6.0
 */