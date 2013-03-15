/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class SingleByteCharsetConverter
/*     */ {
/*     */   private static final int BYTE_RANGE = 256;
/*  47 */   private static byte[] allBytes = new byte[256];
/*  48 */   private static final Map CONVERTER_MAP = new HashMap();
/*     */ 
/*  50 */   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */ 
/*  55 */   private static byte[] unknownCharsMap = new byte[65536];
/*     */ 
/* 141 */   private char[] byteToChars = new char[256];
/*     */ 
/* 143 */   private byte[] charToByteMap = new byte[65536];
/*     */ 
/*     */   public static synchronized SingleByteCharsetConverter getInstance(String encodingName, Connection conn)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/*  84 */     SingleByteCharsetConverter instance = (SingleByteCharsetConverter)CONVERTER_MAP.get(encodingName);
/*     */ 
/*  87 */     if (instance == null) {
/*  88 */       instance = initCharset(encodingName);
/*     */     }
/*     */ 
/*  91 */     return instance;
/*     */   }
/*     */ 
/*     */   public static SingleByteCharsetConverter initCharset(String javaEncodingName)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 106 */     if (CharsetMapping.isMultibyteCharset(javaEncodingName)) {
/* 107 */       return null;
/*     */     }
/*     */ 
/* 110 */     SingleByteCharsetConverter converter = new SingleByteCharsetConverter(javaEncodingName);
/*     */ 
/* 113 */     CONVERTER_MAP.put(javaEncodingName, converter);
/*     */ 
/* 115 */     return converter;
/*     */   }
/*     */ 
/*     */   public static String toStringDefaultEncoding(byte[] buffer, int startPos, int length)
/*     */   {
/* 135 */     return new String(buffer, startPos, length);
/*     */   }
/*     */ 
/*     */   private SingleByteCharsetConverter(String encodingName)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 155 */     String allBytesString = new String(allBytes, 0, 256, encodingName);
/*     */ 
/* 157 */     int allBytesLen = allBytesString.length();
/*     */ 
/* 159 */     System.arraycopy(unknownCharsMap, 0, this.charToByteMap, 0, this.charToByteMap.length);
/*     */ 
/* 162 */     for (int i = 0; (i < 256) && (i < allBytesLen); i++) {
/* 163 */       char c = allBytesString.charAt(i);
/* 164 */       this.byteToChars[i] = c;
/* 165 */       this.charToByteMap[c] = allBytes[i];
/*     */     }
/*     */   }
/*     */ 
/*     */   public final byte[] toBytes(char[] c) {
/* 170 */     if (c == null) {
/* 171 */       return null;
/*     */     }
/*     */ 
/* 174 */     int length = c.length;
/* 175 */     byte[] bytes = new byte[length];
/*     */ 
/* 177 */     for (int i = 0; i < length; i++) {
/* 178 */       bytes[i] = this.charToByteMap[c[i]];
/*     */     }
/*     */ 
/* 181 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytesWrapped(char[] c, char beginWrap, char endWrap) {
/* 185 */     if (c == null) {
/* 186 */       return null;
/*     */     }
/*     */ 
/* 189 */     int length = c.length + 2;
/* 190 */     int charLength = c.length;
/*     */ 
/* 192 */     byte[] bytes = new byte[length];
/* 193 */     bytes[0] = this.charToByteMap[beginWrap];
/*     */ 
/* 195 */     for (int i = 0; i < charLength; i++) {
/* 196 */       bytes[(i + 1)] = this.charToByteMap[c[i]];
/*     */     }
/*     */ 
/* 199 */     bytes[(length - 1)] = this.charToByteMap[endWrap];
/*     */ 
/* 201 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytes(char[] chars, int offset, int length) {
/* 205 */     if (chars == null) {
/* 206 */       return null;
/*     */     }
/*     */ 
/* 209 */     if (length == 0) {
/* 210 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */ 
/* 213 */     byte[] bytes = new byte[length];
/*     */ 
/* 215 */     for (int i = 0; i < length; i++) {
/* 216 */       bytes[i] = this.charToByteMap[chars[(i + offset)]];
/*     */     }
/*     */ 
/* 219 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytes(String s)
/*     */   {
/* 230 */     if (s == null) {
/* 231 */       return null;
/*     */     }
/*     */ 
/* 234 */     int length = s.length();
/* 235 */     byte[] bytes = new byte[length];
/*     */ 
/* 237 */     for (int i = 0; i < length; i++) {
/* 238 */       bytes[i] = this.charToByteMap[s.charAt(i)];
/*     */     }
/*     */ 
/* 241 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytesWrapped(String s, char beginWrap, char endWrap) {
/* 245 */     if (s == null) {
/* 246 */       return null;
/*     */     }
/*     */ 
/* 249 */     int stringLength = s.length();
/*     */ 
/* 251 */     int length = stringLength + 2;
/*     */ 
/* 253 */     byte[] bytes = new byte[length];
/*     */ 
/* 255 */     bytes[0] = this.charToByteMap[beginWrap];
/*     */ 
/* 257 */     for (int i = 0; i < stringLength; i++) {
/* 258 */       bytes[(i + 1)] = this.charToByteMap[s.charAt(i)];
/*     */     }
/*     */ 
/* 261 */     bytes[(length - 1)] = this.charToByteMap[endWrap];
/*     */ 
/* 263 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytes(String s, int offset, int length)
/*     */   {
/* 279 */     if (s == null) {
/* 280 */       return null;
/*     */     }
/*     */ 
/* 283 */     if (length == 0) {
/* 284 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */ 
/* 287 */     byte[] bytes = new byte[length];
/*     */ 
/* 289 */     for (int i = 0; i < length; i++) {
/* 290 */       char c = s.charAt(i + offset);
/* 291 */       bytes[i] = this.charToByteMap[c];
/*     */     }
/*     */ 
/* 294 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final String toString(byte[] buffer)
/*     */   {
/* 306 */     return toString(buffer, 0, buffer.length);
/*     */   }
/*     */ 
/*     */   public final String toString(byte[] buffer, int startPos, int length)
/*     */   {
/* 322 */     char[] charArray = new char[length];
/* 323 */     int readpoint = startPos;
/*     */ 
/* 325 */     for (int i = 0; i < length; i++) {
/* 326 */       charArray[i] = this.byteToChars[(buffer[readpoint] - -128)];
/* 327 */       readpoint++;
/*     */     }
/*     */ 
/* 330 */     return new String(charArray);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  58 */     for (int i = -128; i <= 127; i++) {
/*  59 */       allBytes[(i - -128)] = (byte)i;
/*     */     }
/*     */ 
/*  62 */     for (int i = 0; i < unknownCharsMap.length; i++)
/*  63 */       unknownCharsMap[i] = 63;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.SingleByteCharsetConverter
 * JD-Core Version:    0.6.0
 */