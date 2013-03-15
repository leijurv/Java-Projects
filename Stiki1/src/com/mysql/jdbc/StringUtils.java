/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ public class StringUtils
/*      */ {
/*      */   private static final int BYTE_RANGE = 256;
/*   52 */   private static byte[] allBytes = new byte[256];
/*      */ 
/*   54 */   private static char[] byteToChars = new char[256];
/*      */   private static Method toPlainStringMethod;
/*      */   static final int WILD_COMPARE_MATCH_NO_WILD = 0;
/*      */   static final int WILD_COMPARE_MATCH_WITH_WILD = 1;
/*      */   static final int WILD_COMPARE_NO_MATCH = -1;
/*      */ 
/*      */   public static String consistentToString(BigDecimal decimal)
/*      */   {
/*   97 */     if (decimal == null) {
/*   98 */       return null;
/*      */     }
/*      */ 
/*  101 */     if (toPlainStringMethod != null)
/*      */       try {
/*  103 */         return (String)toPlainStringMethod.invoke(decimal, null);
/*      */       }
/*      */       catch (InvocationTargetException invokeEx)
/*      */       {
/*      */       }
/*      */       catch (IllegalAccessException accessEx)
/*      */       {
/*      */       }
/*  111 */     return decimal.toString();
/*      */   }
/*      */ 
/*      */   public static final String dumpAsHex(byte[] byteBuffer, int length)
/*      */   {
/*  125 */     StringBuffer outputBuf = new StringBuffer(length * 4);
/*      */ 
/*  127 */     int p = 0;
/*  128 */     int rows = length / 8;
/*      */ 
/*  130 */     for (int i = 0; (i < rows) && (p < length); i++) {
/*  131 */       int ptemp = p;
/*      */ 
/*  133 */       for (int j = 0; j < 8; j++) {
/*  134 */         String hexVal = Integer.toHexString(byteBuffer[ptemp] & 0xFF);
/*      */ 
/*  136 */         if (hexVal.length() == 1) {
/*  137 */           hexVal = "0" + hexVal;
/*      */         }
/*      */ 
/*  140 */         outputBuf.append(hexVal + " ");
/*  141 */         ptemp++;
/*      */       }
/*      */ 
/*  144 */       outputBuf.append("    ");
/*      */ 
/*  146 */       for (int j = 0; j < 8; j++) {
/*  147 */         int b = 0xFF & byteBuffer[p];
/*      */ 
/*  149 */         if ((b > 32) && (b < 127))
/*  150 */           outputBuf.append((char)b + " ");
/*      */         else {
/*  152 */           outputBuf.append(". ");
/*      */         }
/*      */ 
/*  155 */         p++;
/*      */       }
/*      */ 
/*  158 */       outputBuf.append("\n");
/*      */     }
/*      */ 
/*  161 */     int n = 0;
/*      */ 
/*  163 */     for (int i = p; i < length; i++) {
/*  164 */       String hexVal = Integer.toHexString(byteBuffer[i] & 0xFF);
/*      */ 
/*  166 */       if (hexVal.length() == 1) {
/*  167 */         hexVal = "0" + hexVal;
/*      */       }
/*      */ 
/*  170 */       outputBuf.append(hexVal + " ");
/*  171 */       n++;
/*      */     }
/*      */ 
/*  174 */     for (int i = n; i < 8; i++) {
/*  175 */       outputBuf.append("   ");
/*      */     }
/*      */ 
/*  178 */     outputBuf.append("    ");
/*      */ 
/*  180 */     for (int i = p; i < length; i++) {
/*  181 */       int b = 0xFF & byteBuffer[i];
/*      */ 
/*  183 */       if ((b > 32) && (b < 127))
/*  184 */         outputBuf.append((char)b + " ");
/*      */       else {
/*  186 */         outputBuf.append(". ");
/*      */       }
/*      */     }
/*      */ 
/*  190 */     outputBuf.append("\n");
/*      */ 
/*  192 */     return outputBuf.toString();
/*      */   }
/*      */ 
/*      */   private static boolean endsWith(byte[] dataFrom, String suffix) {
/*  196 */     for (int i = 1; i <= suffix.length(); i++) {
/*  197 */       int dfOffset = dataFrom.length - i;
/*  198 */       int suffixOffset = suffix.length() - i;
/*  199 */       if (dataFrom[dfOffset] != suffix.charAt(suffixOffset)) {
/*  200 */         return false;
/*      */       }
/*      */     }
/*  203 */     return true;
/*      */   }
/*      */ 
/*      */   public static byte[] escapeEasternUnicodeByteStream(byte[] origBytes, String origString, int offset, int length)
/*      */   {
/*  223 */     if ((origBytes == null) || (origBytes.length == 0)) {
/*  224 */       return origBytes;
/*      */     }
/*      */ 
/*  227 */     int bytesLen = origBytes.length;
/*  228 */     int bufIndex = 0;
/*  229 */     int strIndex = 0;
/*      */ 
/*  231 */     ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(bytesLen);
/*      */     while (true)
/*      */     {
/*  234 */       if (origString.charAt(strIndex) == '\\')
/*      */       {
/*  236 */         bytesOut.write(origBytes[(bufIndex++)]);
/*      */       }
/*      */       else
/*      */       {
/*  241 */         int loByte = origBytes[bufIndex];
/*      */ 
/*  243 */         if (loByte < 0) {
/*  244 */           loByte += 256;
/*      */         }
/*      */ 
/*  248 */         bytesOut.write(loByte);
/*      */ 
/*  266 */         if (loByte >= 128) {
/*  267 */           if (bufIndex < bytesLen - 1) {
/*  268 */             int hiByte = origBytes[(bufIndex + 1)];
/*      */ 
/*  270 */             if (hiByte < 0) {
/*  271 */               hiByte += 256;
/*      */             }
/*      */ 
/*  276 */             bytesOut.write(hiByte);
/*  277 */             bufIndex++;
/*      */ 
/*  280 */             if (hiByte == 92)
/*  281 */               bytesOut.write(hiByte);
/*      */           }
/*      */         }
/*  284 */         else if ((loByte == 92) && 
/*  285 */           (bufIndex < bytesLen - 1)) {
/*  286 */           int hiByte = origBytes[(bufIndex + 1)];
/*      */ 
/*  288 */           if (hiByte < 0) {
/*  289 */             hiByte += 256;
/*      */           }
/*      */ 
/*  292 */           if (hiByte == 98)
/*      */           {
/*  294 */             bytesOut.write(92);
/*  295 */             bytesOut.write(98);
/*  296 */             bufIndex++;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  301 */         bufIndex++;
/*      */       }
/*      */ 
/*  304 */       if (bufIndex >= bytesLen)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  309 */       strIndex++;
/*      */     }
/*      */ 
/*  312 */     return bytesOut.toByteArray();
/*      */   }
/*      */ 
/*      */   public static char firstNonWsCharUc(String searchIn)
/*      */   {
/*  324 */     return firstNonWsCharUc(searchIn, 0);
/*      */   }
/*      */ 
/*      */   public static char firstNonWsCharUc(String searchIn, int startAt) {
/*  328 */     if (searchIn == null) {
/*  329 */       return '\000';
/*      */     }
/*      */ 
/*  332 */     int length = searchIn.length();
/*      */ 
/*  334 */     for (int i = startAt; i < length; i++) {
/*  335 */       char c = searchIn.charAt(i);
/*      */ 
/*  337 */       if (!Character.isWhitespace(c)) {
/*  338 */         return Character.toUpperCase(c);
/*      */       }
/*      */     }
/*      */ 
/*  342 */     return '\000';
/*      */   }
/*      */ 
/*      */   public static char firstAlphaCharUc(String searchIn, int startAt) {
/*  346 */     if (searchIn == null) {
/*  347 */       return '\000';
/*      */     }
/*      */ 
/*  350 */     int length = searchIn.length();
/*      */ 
/*  352 */     for (int i = startAt; i < length; i++) {
/*  353 */       char c = searchIn.charAt(i);
/*      */ 
/*  355 */       if (Character.isLetter(c)) {
/*  356 */         return Character.toUpperCase(c);
/*      */       }
/*      */     }
/*      */ 
/*  360 */     return '\000';
/*      */   }
/*      */ 
/*      */   public static final String fixDecimalExponent(String dString)
/*      */   {
/*  373 */     int ePos = dString.indexOf("E");
/*      */ 
/*  375 */     if (ePos == -1) {
/*  376 */       ePos = dString.indexOf("e");
/*      */     }
/*      */ 
/*  379 */     if ((ePos != -1) && 
/*  380 */       (dString.length() > ePos + 1)) {
/*  381 */       char maybeMinusChar = dString.charAt(ePos + 1);
/*      */ 
/*  383 */       if ((maybeMinusChar != '-') && (maybeMinusChar != '+')) {
/*  384 */         StringBuffer buf = new StringBuffer(dString.length() + 1);
/*  385 */         buf.append(dString.substring(0, ePos + 1));
/*  386 */         buf.append('+');
/*  387 */         buf.append(dString.substring(ePos + 1, dString.length()));
/*  388 */         dString = buf.toString();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  393 */     return dString;
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  401 */       byte[] b = null;
/*      */ 
/*  403 */       if (converter != null) {
/*  404 */         b = converter.toBytes(c);
/*  405 */       } else if (encoding == null) {
/*  406 */         b = new String(c).getBytes();
/*      */       } else {
/*  408 */         String s = new String(c);
/*      */ 
/*  410 */         b = s.getBytes(encoding);
/*      */ 
/*  412 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  416 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  417 */             b = escapeEasternUnicodeByteStream(b, s, 0, s.length());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  422 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  424 */     throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009", exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  435 */       byte[] b = null;
/*      */ 
/*  437 */       if (converter != null) {
/*  438 */         b = converter.toBytes(c, offset, length);
/*  439 */       } else if (encoding == null) {
/*  440 */         byte[] temp = new String(c, offset, length).getBytes();
/*      */ 
/*  442 */         length = temp.length;
/*      */ 
/*  444 */         b = new byte[length];
/*  445 */         System.arraycopy(temp, 0, b, 0, length);
/*      */       } else {
/*  447 */         String s = new String(c, offset, length);
/*      */ 
/*  449 */         byte[] temp = s.getBytes(encoding);
/*      */ 
/*  451 */         length = temp.length;
/*      */ 
/*  453 */         b = new byte[length];
/*  454 */         System.arraycopy(temp, 0, b, 0, length);
/*      */ 
/*  456 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  460 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  461 */             b = escapeEasternUnicodeByteStream(b, s, offset, length);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  466 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  468 */     throw SQLError.createSQLException(Messages.getString("StringUtils.10") + encoding + Messages.getString("StringUtils.11"), "S1009", exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(char[] c, String encoding, String serverEncoding, boolean parserKnowsUnicode, ConnectionImpl conn, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  480 */       SingleByteCharsetConverter converter = null;
/*      */ 
/*  482 */       if (conn != null)
/*  483 */         converter = conn.getCharsetConverter(encoding);
/*      */       else {
/*  485 */         converter = SingleByteCharsetConverter.getInstance(encoding, null);
/*      */       }
/*      */ 
/*  488 */       return getBytes(c, converter, encoding, serverEncoding, parserKnowsUnicode, exceptionInterceptor);
/*      */     } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  491 */     throw SQLError.createSQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), "S1009", exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  522 */       byte[] b = null;
/*      */ 
/*  524 */       if (converter != null) {
/*  525 */         b = converter.toBytes(s);
/*  526 */       } else if (encoding == null) {
/*  527 */         b = s.getBytes();
/*      */       } else {
/*  529 */         b = s.getBytes(encoding);
/*      */ 
/*  531 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  535 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  536 */             b = escapeEasternUnicodeByteStream(b, s, 0, s.length());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  541 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  543 */     throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009", exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytesWrapped(String s, char beginWrap, char endWrap, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  554 */       byte[] b = null;
/*      */ 
/*  556 */       if (converter != null) {
/*  557 */         b = converter.toBytesWrapped(s, beginWrap, endWrap);
/*  558 */       } else if (encoding == null) {
/*  559 */         StringBuffer buf = new StringBuffer(s.length() + 2);
/*  560 */         buf.append(beginWrap);
/*  561 */         buf.append(s);
/*  562 */         buf.append(endWrap);
/*      */ 
/*  564 */         b = buf.toString().getBytes();
/*      */       } else {
/*  566 */         StringBuffer buf = new StringBuffer(s.length() + 2);
/*  567 */         buf.append(beginWrap);
/*  568 */         buf.append(s);
/*  569 */         buf.append(endWrap);
/*      */ 
/*  571 */         b = buf.toString().getBytes(encoding);
/*      */ 
/*  573 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  577 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  578 */             b = escapeEasternUnicodeByteStream(b, s, 0, s.length());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  583 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  585 */     throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009", exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  619 */       byte[] b = null;
/*      */ 
/*  621 */       if (converter != null) {
/*  622 */         b = converter.toBytes(s, offset, length);
/*  623 */       } else if (encoding == null) {
/*  624 */         byte[] temp = s.substring(offset, offset + length).getBytes();
/*      */ 
/*  626 */         length = temp.length;
/*      */ 
/*  628 */         b = new byte[length];
/*  629 */         System.arraycopy(temp, 0, b, 0, length);
/*      */       }
/*      */       else {
/*  632 */         byte[] temp = s.substring(offset, offset + length).getBytes(encoding);
/*      */ 
/*  635 */         length = temp.length;
/*      */ 
/*  637 */         b = new byte[length];
/*  638 */         System.arraycopy(temp, 0, b, 0, length);
/*      */ 
/*  640 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  644 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  645 */             b = escapeEasternUnicodeByteStream(b, s, offset, length);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  650 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  652 */     throw SQLError.createSQLException(Messages.getString("StringUtils.10") + encoding + Messages.getString("StringUtils.11"), "S1009", exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, ConnectionImpl conn, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  679 */       SingleByteCharsetConverter converter = null;
/*      */ 
/*  681 */       if (conn != null)
/*  682 */         converter = conn.getCharsetConverter(encoding);
/*      */       else {
/*  684 */         converter = SingleByteCharsetConverter.getInstance(encoding, null);
/*      */       }
/*      */ 
/*  687 */       return getBytes(s, converter, encoding, serverEncoding, parserKnowsUnicode, exceptionInterceptor);
/*      */     } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  690 */     throw SQLError.createSQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), "S1009", exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public static int getInt(byte[] buf, int offset, int endPos)
/*      */     throws NumberFormatException
/*      */   {
/*  697 */     int base = 10;
/*      */ 
/*  699 */     int s = offset;
/*      */ 
/*  702 */     while ((Character.isWhitespace((char)buf[s])) && (s < endPos)) {
/*  703 */       s++;
/*      */     }
/*      */ 
/*  706 */     if (s == endPos) {
/*  707 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  711 */     boolean negative = false;
/*      */ 
/*  713 */     if ((char)buf[s] == '-') {
/*  714 */       negative = true;
/*  715 */       s++;
/*  716 */     } else if ((char)buf[s] == '+') {
/*  717 */       s++;
/*      */     }
/*      */ 
/*  721 */     int save = s;
/*      */ 
/*  723 */     int cutoff = 2147483647 / base;
/*  724 */     int cutlim = 2147483647 % base;
/*      */ 
/*  726 */     if (negative) {
/*  727 */       cutlim++;
/*      */     }
/*      */ 
/*  730 */     boolean overflow = false;
/*      */ 
/*  732 */     int i = 0;
/*      */ 
/*  734 */     for (; s < endPos; s++) {
/*  735 */       char c = (char)buf[s];
/*      */ 
/*  737 */       if (Character.isDigit(c)) {
/*  738 */         c = (char)(c - '0'); } else {
/*  739 */         if (!Character.isLetter(c)) break;
/*  740 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */ 
/*  745 */       if (c >= base)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  750 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  751 */         overflow = true;
/*      */       } else {
/*  753 */         i *= base;
/*  754 */         i += c;
/*      */       }
/*      */     }
/*      */ 
/*  758 */     if (s == save) {
/*  759 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  762 */     if (overflow) {
/*  763 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  767 */     return negative ? -i : i;
/*      */   }
/*      */ 
/*      */   public static int getInt(byte[] buf) throws NumberFormatException {
/*  771 */     return getInt(buf, 0, buf.length);
/*      */   }
/*      */ 
/*      */   public static long getLong(byte[] buf) throws NumberFormatException {
/*  775 */     return getLong(buf, 0, buf.length);
/*      */   }
/*      */ 
/*      */   public static long getLong(byte[] buf, int offset, int endpos) throws NumberFormatException {
/*  779 */     int base = 10;
/*      */ 
/*  781 */     int s = offset;
/*      */ 
/*  784 */     while ((Character.isWhitespace((char)buf[s])) && (s < endpos)) {
/*  785 */       s++;
/*      */     }
/*      */ 
/*  788 */     if (s == endpos) {
/*  789 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  793 */     boolean negative = false;
/*      */ 
/*  795 */     if ((char)buf[s] == '-') {
/*  796 */       negative = true;
/*  797 */       s++;
/*  798 */     } else if ((char)buf[s] == '+') {
/*  799 */       s++;
/*      */     }
/*      */ 
/*  803 */     int save = s;
/*      */ 
/*  805 */     long cutoff = 9223372036854775807L / base;
/*  806 */     long cutlim = (int)(9223372036854775807L % base);
/*      */ 
/*  808 */     if (negative) {
/*  809 */       cutlim += 1L;
/*      */     }
/*      */ 
/*  812 */     boolean overflow = false;
/*  813 */     long i = 0L;
/*      */ 
/*  815 */     for (; s < endpos; s++) {
/*  816 */       char c = (char)buf[s];
/*      */ 
/*  818 */       if (Character.isDigit(c)) {
/*  819 */         c = (char)(c - '0'); } else {
/*  820 */         if (!Character.isLetter(c)) break;
/*  821 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */ 
/*  826 */       if (c >= base)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  831 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  832 */         overflow = true;
/*      */       } else {
/*  834 */         i *= base;
/*  835 */         i += c;
/*      */       }
/*      */     }
/*      */ 
/*  839 */     if (s == save) {
/*  840 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  843 */     if (overflow) {
/*  844 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  848 */     return negative ? -i : i;
/*      */   }
/*      */ 
/*      */   public static short getShort(byte[] buf) throws NumberFormatException {
/*  852 */     short base = 10;
/*      */ 
/*  854 */     int s = 0;
/*      */ 
/*  857 */     while ((Character.isWhitespace((char)buf[s])) && (s < buf.length)) {
/*  858 */       s++;
/*      */     }
/*      */ 
/*  861 */     if (s == buf.length) {
/*  862 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  866 */     boolean negative = false;
/*      */ 
/*  868 */     if ((char)buf[s] == '-') {
/*  869 */       negative = true;
/*  870 */       s++;
/*  871 */     } else if ((char)buf[s] == '+') {
/*  872 */       s++;
/*      */     }
/*      */ 
/*  876 */     int save = s;
/*      */ 
/*  878 */     short cutoff = (short)(32767 / base);
/*  879 */     short cutlim = (short)(32767 % base);
/*      */ 
/*  881 */     if (negative) {
/*  882 */       cutlim = (short)(cutlim + 1);
/*      */     }
/*      */ 
/*  885 */     boolean overflow = false;
/*  886 */     short i = 0;
/*      */ 
/*  888 */     for (; s < buf.length; s++) {
/*  889 */       char c = (char)buf[s];
/*      */ 
/*  891 */       if (Character.isDigit(c)) {
/*  892 */         c = (char)(c - '0'); } else {
/*  893 */         if (!Character.isLetter(c)) break;
/*  894 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */ 
/*  899 */       if (c >= base)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  904 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  905 */         overflow = true;
/*      */       } else {
/*  907 */         i = (short)(i * base);
/*  908 */         i = (short)(i + c);
/*      */       }
/*      */     }
/*      */ 
/*  912 */     if (s == save) {
/*  913 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  916 */     if (overflow) {
/*  917 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  921 */     return negative ? (short)(-i) : i;
/*      */   }
/*      */ 
/*      */   public static final int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor)
/*      */   {
/*  926 */     if ((searchIn == null) || (searchFor == null) || (startingPosition > searchIn.length()))
/*      */     {
/*  928 */       return -1;
/*      */     }
/*      */ 
/*  931 */     int patternLength = searchFor.length();
/*  932 */     int stringLength = searchIn.length();
/*  933 */     int stopSearchingAt = stringLength - patternLength;
/*      */ 
/*  935 */     if (patternLength == 0) {
/*  936 */       return -1;
/*      */     }
/*      */ 
/*  941 */     char firstCharOfPatternUc = Character.toUpperCase(searchFor.charAt(0));
/*  942 */     char firstCharOfPatternLc = Character.toLowerCase(searchFor.charAt(0));
/*      */ 
/*  945 */     for (int i = startingPosition; i <= stopSearchingAt; i++) {
/*  946 */       if (isNotEqualIgnoreCharCase(searchIn, firstCharOfPatternUc, firstCharOfPatternLc, i))
/*      */       {
/*      */         do
/*  949 */           i++; while ((i <= stopSearchingAt) && (isNotEqualIgnoreCharCase(searchIn, firstCharOfPatternUc, firstCharOfPatternLc, i)));
/*      */       }
/*      */ 
/*  953 */       if (i > stopSearchingAt) {
/*      */         continue;
/*      */       }
/*  956 */       int j = i + 1;
/*  957 */       int end = j + patternLength - 1;
/*  958 */       for (int k = 1; (j < end) && ((Character.toLowerCase(searchIn.charAt(j)) == Character.toLowerCase(searchFor.charAt(k))) || (Character.toUpperCase(searchIn.charAt(j)) == Character.toUpperCase(searchFor.charAt(k)))); )
/*      */       {
/*  960 */         j++; k++;
/*      */       }
/*  962 */       if (j == end) {
/*  963 */         return i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  968 */     return -1;
/*      */   }
/*      */ 
/*      */   private static final boolean isNotEqualIgnoreCharCase(String searchIn, char firstCharOfPatternUc, char firstCharOfPatternLc, int i)
/*      */   {
/*  973 */     return (Character.toLowerCase(searchIn.charAt(i)) != firstCharOfPatternLc) && (Character.toUpperCase(searchIn.charAt(i)) != firstCharOfPatternUc);
/*      */   }
/*      */ 
/*      */   public static final int indexOfIgnoreCase(String searchIn, String searchFor)
/*      */   {
/*  988 */     return indexOfIgnoreCase(0, searchIn, searchFor);
/*      */   }
/*      */ 
/*      */   public static int indexOfIgnoreCaseRespectMarker(int startAt, String src, String target, String marker, String markerCloses, boolean allowBackslashEscapes)
/*      */   {
/*  994 */     char contextMarker = '\000';
/*  995 */     boolean escaped = false;
/*  996 */     int markerTypeFound = 0;
/*  997 */     int srcLength = src.length();
/*  998 */     int ind = 0;
/*      */ 
/* 1000 */     for (int i = startAt; i < srcLength; i++) {
/* 1001 */       char c = src.charAt(i);
/*      */ 
/* 1003 */       if ((allowBackslashEscapes) && (c == '\\')) {
/* 1004 */         escaped = !escaped;
/* 1005 */       } else if ((c == markerCloses.charAt(markerTypeFound)) && (!escaped)) {
/* 1006 */         contextMarker = '\000';
/* 1007 */       } else if (((ind = marker.indexOf(c)) != -1) && (!escaped) && (contextMarker == 0))
/*      */       {
/* 1009 */         markerTypeFound = ind;
/* 1010 */         contextMarker = c; } else {
/* 1011 */         if (((Character.toUpperCase(c) != Character.toUpperCase(target.charAt(0))) && (Character.toLowerCase(c) != Character.toLowerCase(target.charAt(0)))) || (escaped) || (contextMarker != 0)) {
/*      */           continue;
/*      */         }
/* 1014 */         if (startsWithIgnoreCase(src, i, target)) {
/* 1015 */           return i;
/*      */         }
/*      */       }
/*      */     }
/* 1019 */     return -1;
/*      */   }
/*      */ 
/*      */   public static int indexOfIgnoreCaseRespectQuotes(int startAt, String src, String target, char quoteChar, boolean allowBackslashEscapes)
/*      */   {
/* 1025 */     char contextMarker = '\000';
/* 1026 */     boolean escaped = false;
/*      */ 
/* 1028 */     int srcLength = src.length();
/*      */ 
/* 1030 */     for (int i = startAt; i < srcLength; i++) {
/* 1031 */       char c = src.charAt(i);
/*      */ 
/* 1033 */       if ((allowBackslashEscapes) && (c == '\\')) {
/* 1034 */         escaped = !escaped;
/* 1035 */       } else if ((c == contextMarker) && (!escaped)) {
/* 1036 */         contextMarker = '\000';
/* 1037 */       } else if ((c == quoteChar) && (!escaped) && (contextMarker == 0))
/*      */       {
/* 1039 */         contextMarker = c;
/*      */       }
/*      */       else {
/* 1042 */         if (((Character.toUpperCase(c) != Character.toUpperCase(target.charAt(0))) && (Character.toLowerCase(c) != Character.toLowerCase(target.charAt(0)))) || (escaped) || (contextMarker != 0)) {
/*      */           continue;
/*      */         }
/* 1045 */         if (startsWithIgnoreCase(src, i, target)) {
/* 1046 */           return i;
/*      */         }
/*      */       }
/*      */     }
/* 1050 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final List split(String stringToSplit, String delimitter, boolean trim)
/*      */   {
/* 1071 */     if (stringToSplit == null) {
/* 1072 */       return new ArrayList();
/*      */     }
/*      */ 
/* 1075 */     if (delimitter == null) {
/* 1076 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/* 1079 */     StringTokenizer tokenizer = new StringTokenizer(stringToSplit, delimitter, false);
/*      */ 
/* 1082 */     List splitTokens = new ArrayList(tokenizer.countTokens());
/*      */ 
/* 1084 */     while (tokenizer.hasMoreTokens()) {
/* 1085 */       String token = tokenizer.nextToken();
/*      */ 
/* 1087 */       if (trim) {
/* 1088 */         token = token.trim();
/*      */       }
/*      */ 
/* 1091 */       splitTokens.add(token);
/*      */     }
/*      */ 
/* 1094 */     return splitTokens;
/*      */   }
/*      */ 
/*      */   public static final List split(String stringToSplit, String delimiter, String markers, String markerCloses, boolean trim)
/*      */   {
/* 1114 */     if (stringToSplit == null) {
/* 1115 */       return new ArrayList();
/*      */     }
/*      */ 
/* 1118 */     if (delimiter == null) {
/* 1119 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/* 1122 */     int delimPos = 0;
/* 1123 */     int currentPos = 0;
/*      */ 
/* 1125 */     List splitTokens = new ArrayList();
/*      */ 
/* 1128 */     while ((delimPos = indexOfIgnoreCaseRespectMarker(currentPos, stringToSplit, delimiter, markers, markerCloses, false)) != -1) {
/* 1129 */       String token = stringToSplit.substring(currentPos, delimPos);
/*      */ 
/* 1131 */       if (trim) {
/* 1132 */         token = token.trim();
/*      */       }
/*      */ 
/* 1135 */       splitTokens.add(token);
/* 1136 */       currentPos = delimPos + 1;
/*      */     }
/*      */ 
/* 1139 */     if (currentPos < stringToSplit.length()) {
/* 1140 */       String token = stringToSplit.substring(currentPos);
/*      */ 
/* 1142 */       if (trim) {
/* 1143 */         token = token.trim();
/*      */       }
/*      */ 
/* 1146 */       splitTokens.add(token);
/*      */     }
/*      */ 
/* 1149 */     return splitTokens;
/*      */   }
/*      */ 
/*      */   private static boolean startsWith(byte[] dataFrom, String chars) {
/* 1153 */     for (int i = 0; i < chars.length(); i++) {
/* 1154 */       if (dataFrom[i] != chars.charAt(i)) {
/* 1155 */         return false;
/*      */       }
/*      */     }
/* 1158 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCase(String searchIn, int startAt, String searchFor)
/*      */   {
/* 1177 */     return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor.length());
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCase(String searchIn, String searchFor)
/*      */   {
/* 1193 */     return startsWithIgnoreCase(searchIn, 0, searchFor);
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCaseAndNonAlphaNumeric(String searchIn, String searchFor)
/*      */   {
/* 1210 */     if (searchIn == null) {
/* 1211 */       return searchFor == null;
/*      */     }
/*      */ 
/* 1214 */     int beginPos = 0;
/*      */ 
/* 1216 */     int inLength = searchIn.length();
/*      */ 
/* 1218 */     for (beginPos = 0; beginPos < inLength; beginPos++) {
/* 1219 */       char c = searchIn.charAt(beginPos);
/*      */ 
/* 1221 */       if (Character.isLetterOrDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 1226 */     return startsWithIgnoreCase(searchIn, beginPos, searchFor);
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor)
/*      */   {
/* 1242 */     return startsWithIgnoreCaseAndWs(searchIn, searchFor, 0);
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor, int beginPos)
/*      */   {
/* 1261 */     if (searchIn == null) {
/* 1262 */       return searchFor == null;
/*      */     }
/*      */ 
/* 1265 */     int inLength = searchIn.length();
/*      */ 
/* 1267 */     while ((beginPos < inLength) && 
/* 1268 */       (Character.isWhitespace(searchIn.charAt(beginPos)))) {
/* 1267 */       beginPos++;
/*      */     }
/*      */ 
/* 1273 */     return startsWithIgnoreCase(searchIn, beginPos, searchFor);
/*      */   }
/*      */ 
/*      */   public static byte[] stripEnclosure(byte[] source, String prefix, String suffix)
/*      */   {
/* 1284 */     if ((source.length >= prefix.length() + suffix.length()) && (startsWith(source, prefix)) && (endsWith(source, suffix)))
/*      */     {
/* 1287 */       int totalToStrip = prefix.length() + suffix.length();
/* 1288 */       int enclosedLength = source.length - totalToStrip;
/* 1289 */       byte[] enclosed = new byte[enclosedLength];
/*      */ 
/* 1291 */       int startPos = prefix.length();
/* 1292 */       int numToCopy = enclosed.length;
/* 1293 */       System.arraycopy(source, startPos, enclosed, 0, numToCopy);
/*      */ 
/* 1295 */       return enclosed;
/*      */     }
/* 1297 */     return source;
/*      */   }
/*      */ 
/*      */   public static final String toAsciiString(byte[] buffer)
/*      */   {
/* 1309 */     return toAsciiString(buffer, 0, buffer.length);
/*      */   }
/*      */ 
/*      */   public static final String toAsciiString(byte[] buffer, int startPos, int length)
/*      */   {
/* 1326 */     char[] charArray = new char[length];
/* 1327 */     int readpoint = startPos;
/*      */ 
/* 1329 */     for (int i = 0; i < length; i++) {
/* 1330 */       charArray[i] = (char)buffer[readpoint];
/* 1331 */       readpoint++;
/*      */     }
/*      */ 
/* 1334 */     return new String(charArray);
/*      */   }
/*      */ 
/*      */   public static int wildCompare(String searchIn, String searchForWildcard)
/*      */   {
/* 1352 */     if ((searchIn == null) || (searchForWildcard == null)) {
/* 1353 */       return -1;
/*      */     }
/*      */ 
/* 1356 */     if (searchForWildcard.equals("%"))
/*      */     {
/* 1358 */       return 1;
/*      */     }
/*      */ 
/* 1361 */     int result = -1;
/*      */ 
/* 1363 */     char wildcardMany = '%';
/* 1364 */     char wildcardOne = '_';
/* 1365 */     char wildcardEscape = '\\';
/*      */ 
/* 1367 */     int searchForPos = 0;
/* 1368 */     int searchForEnd = searchForWildcard.length();
/*      */ 
/* 1370 */     int searchInPos = 0;
/* 1371 */     int searchInEnd = searchIn.length();
/*      */ 
/* 1373 */     while (searchForPos != searchForEnd) {
/* 1374 */       char wildstrChar = searchForWildcard.charAt(searchForPos);
/*      */ 
/* 1377 */       while ((searchForWildcard.charAt(searchForPos) != wildcardMany) && (wildstrChar != wildcardOne)) {
/* 1378 */         if ((searchForWildcard.charAt(searchForPos) == wildcardEscape) && (searchForPos + 1 != searchForEnd))
/*      */         {
/* 1380 */           searchForPos++;
/*      */         }
/*      */ 
/* 1383 */         if ((searchInPos == searchInEnd) || (Character.toUpperCase(searchForWildcard.charAt(searchForPos++)) != Character.toUpperCase(searchIn.charAt(searchInPos++))))
/*      */         {
/* 1387 */           return 1;
/*      */         }
/*      */ 
/* 1390 */         if (searchForPos == searchForEnd) {
/* 1391 */           return searchInPos != searchInEnd ? 1 : 0;
/*      */         }
/*      */ 
/* 1398 */         result = 1;
/*      */       }
/*      */ 
/* 1401 */       if (searchForWildcard.charAt(searchForPos) == wildcardOne) {
/*      */         do {
/* 1403 */           if (searchInPos == searchInEnd)
/*      */           {
/* 1408 */             return result;
/*      */           }
/*      */ 
/* 1411 */           searchInPos++;
/*      */ 
/* 1413 */           searchForPos++; } while ((searchForPos < searchForEnd) && (searchForWildcard.charAt(searchForPos) == wildcardOne));
/*      */ 
/* 1415 */         if (searchForPos == searchForEnd)
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/* 1420 */       if (searchForWildcard.charAt(searchForPos) == wildcardMany)
/*      */       {
/* 1427 */         searchForPos++;
/*      */ 
/* 1430 */         for (; searchForPos != searchForEnd; searchForPos++) {
/* 1431 */           if (searchForWildcard.charAt(searchForPos) == wildcardMany)
/*      */           {
/*      */             continue;
/*      */           }
/* 1435 */           if (searchForWildcard.charAt(searchForPos) != wildcardOne) break;
/* 1436 */           if (searchInPos == searchInEnd) {
/* 1437 */             return -1;
/*      */           }
/*      */ 
/* 1440 */           searchInPos++;
/*      */         }
/*      */ 
/* 1448 */         if (searchForPos == searchForEnd) {
/* 1449 */           return 0;
/*      */         }
/*      */ 
/* 1452 */         if (searchInPos == searchInEnd)
/* 1453 */           return -1;
/*      */         char cmp;
/* 1456 */         if (((cmp = searchForWildcard.charAt(searchForPos)) == wildcardEscape) && (searchForPos + 1 != searchForEnd))
/*      */         {
/* 1458 */           searchForPos++; cmp = searchForWildcard.charAt(searchForPos);
/*      */         }
/*      */ 
/* 1461 */         searchForPos++;
/*      */         do
/*      */         {
/* 1465 */           while ((searchInPos != searchInEnd) && (Character.toUpperCase(searchIn.charAt(searchInPos)) != Character.toUpperCase(cmp)))
/*      */           {
/* 1468 */             searchInPos++;
/*      */           }
/* 1470 */           if (searchInPos++ == searchInEnd) {
/* 1471 */             return -1;
/*      */           }
/*      */ 
/* 1475 */           int tmp = wildCompare(searchIn, searchForWildcard);
/*      */ 
/* 1477 */           if (tmp <= 0) {
/* 1478 */             return tmp;
/*      */           }
/*      */         }
/*      */ 
/* 1482 */         while ((searchInPos != searchInEnd) && (searchForWildcard.charAt(0) != wildcardMany));
/*      */ 
/* 1484 */         return -1;
/*      */       }
/*      */     }
/*      */ 
/* 1488 */     return searchInPos != searchInEnd ? 1 : 0;
/*      */   }
/*      */ 
/*      */   static byte[] s2b(String s, ConnectionImpl conn) throws SQLException
/*      */   {
/* 1493 */     if (s == null) {
/* 1494 */       return null;
/*      */     }
/*      */ 
/* 1497 */     if ((conn != null) && (conn.getUseUnicode())) {
/*      */       try {
/* 1499 */         String encoding = conn.getEncoding();
/*      */ 
/* 1501 */         if (encoding == null) {
/* 1502 */           return s.getBytes();
/*      */         }
/*      */ 
/* 1505 */         SingleByteCharsetConverter converter = conn.getCharsetConverter(encoding);
/*      */ 
/* 1508 */         if (converter != null) {
/* 1509 */           return converter.toBytes(s);
/*      */         }
/*      */ 
/* 1512 */         return s.getBytes(encoding);
/*      */       } catch (UnsupportedEncodingException E) {
/* 1514 */         return s.getBytes();
/*      */       }
/*      */     }
/*      */ 
/* 1518 */     return s.getBytes();
/*      */   }
/*      */ 
/*      */   public static int lastIndexOf(byte[] s, char c) {
/* 1522 */     if (s == null) {
/* 1523 */       return -1;
/*      */     }
/*      */ 
/* 1526 */     for (int i = s.length - 1; i >= 0; i--) {
/* 1527 */       if (s[i] == c) {
/* 1528 */         return i;
/*      */       }
/*      */     }
/*      */ 
/* 1532 */     return -1;
/*      */   }
/*      */ 
/*      */   public static int indexOf(byte[] s, char c) {
/* 1536 */     if (s == null) {
/* 1537 */       return -1;
/*      */     }
/*      */ 
/* 1540 */     int length = s.length;
/*      */ 
/* 1542 */     for (int i = 0; i < length; i++) {
/* 1543 */       if (s[i] == c) {
/* 1544 */         return i;
/*      */       }
/*      */     }
/*      */ 
/* 1548 */     return -1;
/*      */   }
/*      */ 
/*      */   public static boolean isNullOrEmpty(String toTest) {
/* 1552 */     return (toTest == null) || (toTest.length() == 0);
/*      */   }
/*      */ 
/*      */   public static String stripComments(String src, String stringOpens, String stringCloses, boolean slashStarComments, boolean slashSlashComments, boolean hashComments, boolean dashDashComments)
/*      */   {
/* 1579 */     if (src == null) {
/* 1580 */       return null;
/*      */     }
/*      */ 
/* 1583 */     StringBuffer buf = new StringBuffer(src.length());
/*      */ 
/* 1592 */     StringReader sourceReader = new StringReader(src);
/*      */ 
/* 1594 */     int contextMarker = 0;
/* 1595 */     boolean escaped = false;
/* 1596 */     int markerTypeFound = -1;
/*      */ 
/* 1598 */     int ind = 0;
/*      */ 
/* 1600 */     int currentChar = 0;
/*      */     try
/*      */     {
/* 1603 */       while ((currentChar = sourceReader.read()) != -1)
/*      */       {
/* 1607 */         if ((markerTypeFound != -1) && (currentChar == stringCloses.charAt(markerTypeFound)) && (!escaped))
/*      */         {
/* 1609 */           contextMarker = 0;
/* 1610 */           markerTypeFound = -1;
/* 1611 */         } else if (((ind = stringOpens.indexOf(currentChar)) != -1) && (!escaped) && (contextMarker == 0))
/*      */         {
/* 1613 */           markerTypeFound = ind;
/* 1614 */           contextMarker = currentChar;
/*      */         }
/*      */ 
/* 1617 */         if ((contextMarker == 0) && (currentChar == 47) && ((slashSlashComments) || (slashStarComments)))
/*      */         {
/* 1619 */           currentChar = sourceReader.read();
/* 1620 */           if ((currentChar == 42) && (slashStarComments)) {
/* 1621 */             int prevChar = 0;
/*      */ 
/* 1623 */             while (((currentChar = sourceReader.read()) != 47) || (prevChar != 42)) {
/* 1624 */               if (currentChar == 13)
/*      */               {
/* 1626 */                 currentChar = sourceReader.read();
/* 1627 */                 if (currentChar == 10) {
/* 1628 */                   currentChar = sourceReader.read();
/*      */                 }
/*      */               }
/* 1631 */               else if (currentChar == 10)
/*      */               {
/* 1633 */                 currentChar = sourceReader.read();
/*      */               }
/*      */ 
/* 1636 */               if (currentChar < 0)
/*      */                 break;
/* 1638 */               prevChar = currentChar;
/*      */             }
/*      */           }
/* 1641 */           if ((currentChar != 47) || (!slashSlashComments));
/*      */         } else {
/* 1643 */           while (((currentChar = sourceReader.read()) != 10) && (currentChar != 13) && (currentChar >= 0)) {
/* 1644 */             continue;
/*      */ 
/* 1646 */             if ((contextMarker == 0) && (currentChar == 35) && (hashComments));
/*      */             while (true) {
/* 1650 */               if (((currentChar = sourceReader.read()) != 10) && (currentChar != 13) && (currentChar >= 0)) {
/* 1651 */                 continue;
/* 1652 */                 if ((contextMarker == 0) && (currentChar == 45) && (dashDashComments))
/*      */                 {
/* 1654 */                   currentChar = sourceReader.read();
/*      */ 
/* 1656 */                   if ((currentChar == -1) || (currentChar != 45)) {
/* 1657 */                     buf.append('-');
/*      */ 
/* 1659 */                     if (currentChar == -1) break;
/* 1660 */                     buf.append(currentChar); break;
/*      */                   }
/*      */ 
/* 1669 */                   while (((currentChar = sourceReader.read()) != 10) && (currentChar != 13) && (currentChar >= 0));
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1673 */         if (currentChar != -1) {
/* 1674 */           buf.append((char)currentChar);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*      */     }
/* 1681 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   public static final boolean isEmptyOrWhitespaceOnly(String str) {
/* 1685 */     if ((str == null) || (str.length() == 0)) {
/* 1686 */       return true;
/*      */     }
/*      */ 
/* 1689 */     int length = str.length();
/*      */ 
/* 1691 */     for (int i = 0; i < length; i++) {
/* 1692 */       if (!Character.isWhitespace(str.charAt(i))) {
/* 1693 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1697 */     return true;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   65 */     for (int i = -128; i <= 127; i++) {
/*   66 */       allBytes[(i - -128)] = (byte)i;
/*      */     }
/*      */ 
/*   69 */     String allBytesString = new String(allBytes, 0, 255);
/*      */ 
/*   72 */     int allBytesStringLen = allBytesString.length();
/*      */ 
/*   74 */     int i = 0;
/*   75 */     for (; (i < 255) && (i < allBytesStringLen); i++) {
/*   76 */       byteToChars[i] = allBytesString.charAt(i);
/*      */     }
/*      */     try
/*      */     {
/*   80 */       toPlainStringMethod = BigDecimal.class.getMethod("toPlainString", new Class[0]);
/*      */     }
/*      */     catch (NoSuchMethodException nsme)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.StringUtils
 * JD-Core Version:    0.6.0
 */