/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ 
/*     */ class Security
/*     */ {
/*     */   private static final char PVERSION41_CHAR = '*';
/*     */   private static final int SHA1_HASH_SIZE = 20;
/*     */ 
/*     */   private static int charVal(char c)
/*     */   {
/*  50 */     return (c >= 'A') && (c <= 'Z') ? c - 'A' + 10 : (c >= '0') && (c <= '9') ? c - '0' : c - 'a' + 10;
/*     */   }
/*     */ 
/*     */   static byte[] createKeyFromOldPassword(String passwd)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  81 */     passwd = makeScrambledPassword(passwd);
/*     */ 
/*  84 */     int[] salt = getSaltFromPassword(passwd);
/*     */ 
/*  87 */     return getBinaryPassword(salt, false);
/*     */   }
/*     */ 
/*     */   static byte[] getBinaryPassword(int[] salt, boolean usingNewPasswords)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 105 */     int val = 0;
/*     */ 
/* 107 */     byte[] binaryPassword = new byte[20];
/*     */ 
/* 112 */     if (usingNewPasswords) {
/* 113 */       int pos = 0;
/*     */ 
/* 115 */       for (int i = 0; i < 4; i++) {
/* 116 */         val = salt[i];
/*     */ 
/* 118 */         for (int t = 3; t >= 0; t--) {
/* 119 */           binaryPassword[(pos++)] = (byte)(val & 0xFF);
/* 120 */           val >>= 8;
/*     */         }
/*     */       }
/*     */ 
/* 124 */       return binaryPassword;
/*     */     }
/*     */ 
/* 127 */     int offset = 0;
/*     */ 
/* 129 */     for (int i = 0; i < 2; i++) {
/* 130 */       val = salt[i];
/*     */ 
/* 132 */       for (int t = 3; t >= 0; t--) {
/* 133 */         binaryPassword[(t + offset)] = (byte)(val % 256);
/* 134 */         val >>= 8;
/*     */       }
/*     */ 
/* 137 */       offset += 4;
/*     */     }
/*     */ 
/* 140 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/*     */ 
/* 142 */     md.update(binaryPassword, 0, 8);
/*     */ 
/* 144 */     return md.digest();
/*     */   }
/*     */ 
/*     */   private static int[] getSaltFromPassword(String password) {
/* 148 */     int[] result = new int[6];
/*     */ 
/* 150 */     if ((password == null) || (password.length() == 0)) {
/* 151 */       return result;
/*     */     }
/*     */ 
/* 154 */     if (password.charAt(0) == '*')
/*     */     {
/* 156 */       String saltInHex = password.substring(1, 5);
/*     */ 
/* 158 */       int val = 0;
/*     */ 
/* 160 */       for (int i = 0; i < 4; i++) {
/* 161 */         val = (val << 4) + charVal(saltInHex.charAt(i));
/*     */       }
/*     */ 
/* 164 */       return result;
/*     */     }
/*     */ 
/* 167 */     int resultPos = 0;
/* 168 */     int pos = 0;
/* 169 */     int length = password.length();
/*     */ 
/* 171 */     while (pos < length) {
/* 172 */       int val = 0;
/*     */ 
/* 174 */       for (int i = 0; i < 8; i++) {
/* 175 */         val = (val << 4) + charVal(password.charAt(pos++));
/*     */       }
/*     */ 
/* 178 */       result[(resultPos++)] = val;
/*     */     }
/*     */ 
/* 181 */     return result;
/*     */   }
/*     */ 
/*     */   private static String longToHex(long val) {
/* 185 */     String longHex = Long.toHexString(val);
/*     */ 
/* 187 */     int length = longHex.length();
/*     */ 
/* 189 */     if (length < 8) {
/* 190 */       int padding = 8 - length;
/* 191 */       StringBuffer buf = new StringBuffer();
/*     */ 
/* 193 */       for (int i = 0; i < padding; i++) {
/* 194 */         buf.append("0");
/*     */       }
/*     */ 
/* 197 */       buf.append(longHex);
/*     */ 
/* 199 */       return buf.toString();
/*     */     }
/*     */ 
/* 202 */     return longHex.substring(0, 8);
/*     */   }
/*     */ 
/*     */   static String makeScrambledPassword(String password)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 220 */     long[] passwordHash = Util.newHash(password);
/* 221 */     StringBuffer scramble = new StringBuffer();
/*     */ 
/* 223 */     scramble.append(longToHex(passwordHash[0]));
/* 224 */     scramble.append(longToHex(passwordHash[1]));
/*     */ 
/* 226 */     return scramble.toString();
/*     */   }
/*     */ 
/*     */   static void passwordCrypt(byte[] from, byte[] to, byte[] password, int length)
/*     */   {
/* 245 */     int pos = 0;
/*     */ 
/* 247 */     while ((pos < from.length) && (pos < length)) {
/* 248 */       to[pos] = (byte)(from[pos] ^ password[pos]);
/* 249 */       pos++;
/*     */     }
/*     */   }
/*     */ 
/*     */   static byte[] passwordHashStage1(String password)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 266 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/* 267 */     StringBuffer cleansedPassword = new StringBuffer();
/*     */ 
/* 269 */     int passwordLength = password.length();
/*     */ 
/* 271 */     for (int i = 0; i < passwordLength; i++) {
/* 272 */       char c = password.charAt(i);
/*     */ 
/* 274 */       if ((c == ' ') || (c == '\t'))
/*     */       {
/*     */         continue;
/*     */       }
/* 278 */       cleansedPassword.append(c);
/*     */     }
/*     */ 
/* 281 */     return md.digest(cleansedPassword.toString().getBytes());
/*     */   }
/*     */ 
/*     */   static byte[] passwordHashStage2(byte[] hashedPassword, byte[] salt)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 299 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/*     */ 
/* 302 */     md.update(salt, 0, 4);
/*     */ 
/* 304 */     md.update(hashedPassword, 0, 20);
/*     */ 
/* 306 */     return md.digest();
/*     */   }
/*     */ 
/*     */   static byte[] scramble411(String password, String seed, Connection conn)
/*     */     throws NoSuchAlgorithmException, UnsupportedEncodingException
/*     */   {
/* 328 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/* 329 */     String passwordEncoding = conn.getPasswordCharacterEncoding();
/*     */ 
/* 331 */     byte[] passwordHashStage1 = md.digest((passwordEncoding == null) || (passwordEncoding.length() == 0) ? password.getBytes() : password.getBytes(passwordEncoding));
/*     */ 
/* 335 */     md.reset();
/*     */ 
/* 337 */     byte[] passwordHashStage2 = md.digest(passwordHashStage1);
/* 338 */     md.reset();
/*     */ 
/* 340 */     byte[] seedAsBytes = seed.getBytes("ASCII");
/* 341 */     md.update(seedAsBytes);
/* 342 */     md.update(passwordHashStage2);
/*     */ 
/* 344 */     byte[] toBeXord = md.digest();
/*     */ 
/* 346 */     int numToXor = toBeXord.length;
/*     */ 
/* 348 */     for (int i = 0; i < numToXor; i++) {
/* 349 */       toBeXord[i] = (byte)(toBeXord[i] ^ passwordHashStage1[i]);
/*     */     }
/*     */ 
/* 352 */     return toBeXord;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Security
 * JD-Core Version:    0.6.0
 */