/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ class EscapeProcessor
/*     */ {
/*     */   private static Map JDBC_CONVERT_TO_MYSQL_TYPE_MAP;
/*     */   private static Map JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP;
/*     */ 
/*     */   public static final Object escapeSQL(String sql, boolean serverSupportsConvertFn, ConnectionImpl conn)
/*     */     throws SQLException
/*     */   {
/* 108 */     boolean replaceEscapeSequence = false;
/* 109 */     String escapeSequence = null;
/*     */ 
/* 111 */     if (sql == null) {
/* 112 */       return null;
/*     */     }
/*     */ 
/* 119 */     int beginBrace = sql.indexOf('{');
/* 120 */     int nextEndBrace = beginBrace == -1 ? -1 : sql.indexOf('}', beginBrace);
/*     */ 
/* 123 */     if (nextEndBrace == -1) {
/* 124 */       return sql;
/*     */     }
/*     */ 
/* 127 */     StringBuffer newSql = new StringBuffer();
/*     */ 
/* 129 */     EscapeTokenizer escapeTokenizer = new EscapeTokenizer(sql);
/*     */ 
/* 131 */     byte usesVariables = 0;
/* 132 */     boolean callingStoredFunction = false;
/*     */ 
/* 134 */     while (escapeTokenizer.hasMoreTokens()) {
/* 135 */       String token = escapeTokenizer.nextToken();
/*     */ 
/* 137 */       if (token.length() != 0) {
/* 138 */         if (token.charAt(0) == '{')
/*     */         {
/* 140 */           if (!token.endsWith("}")) {
/* 141 */             throw SQLError.createSQLException("Not a valid escape sequence: " + token, conn.getExceptionInterceptor());
/*     */           }
/*     */ 
/* 146 */           if (token.length() > 2) {
/* 147 */             int nestedBrace = token.indexOf('{', 2);
/*     */ 
/* 149 */             if (nestedBrace != -1) {
/* 150 */               StringBuffer buf = new StringBuffer(token.substring(0, 1));
/*     */ 
/* 153 */               Object remainingResults = escapeSQL(token.substring(1, token.length() - 1), serverSupportsConvertFn, conn);
/*     */ 
/* 157 */               String remaining = null;
/*     */ 
/* 159 */               if ((remainingResults instanceof String)) {
/* 160 */                 remaining = (String)remainingResults;
/*     */               } else {
/* 162 */                 remaining = ((EscapeProcessorResult)remainingResults).escapedSql;
/*     */ 
/* 164 */                 if (usesVariables != 1) {
/* 165 */                   usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
/*     */                 }
/*     */               }
/*     */ 
/* 169 */               buf.append(remaining);
/*     */ 
/* 171 */               buf.append('}');
/*     */ 
/* 173 */               token = buf.toString();
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 179 */           String collapsedToken = removeWhitespace(token);
/*     */ 
/* 184 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{escape"))
/*     */           {
/*     */             try {
/* 187 */               StringTokenizer st = new StringTokenizer(token, " '");
/*     */ 
/* 189 */               st.nextToken();
/* 190 */               escapeSequence = st.nextToken();
/*     */ 
/* 192 */               if (escapeSequence.length() < 3) {
/* 193 */                 newSql.append(token);
/*     */               }
/*     */               else
/*     */               {
/* 199 */                 escapeSequence = escapeSequence.substring(1, escapeSequence.length() - 1);
/*     */ 
/* 201 */                 replaceEscapeSequence = true;
/*     */               }
/*     */             } catch (NoSuchElementException e) {
/* 204 */               newSql.append(token);
/*     */             }
/*     */ 
/*     */           }
/* 209 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{fn"))
/*     */           {
/* 211 */             int startPos = token.toLowerCase().indexOf("fn ") + 3;
/* 212 */             int endPos = token.length() - 1;
/*     */ 
/* 214 */             String fnToken = token.substring(startPos, endPos);
/*     */ 
/* 218 */             if (StringUtils.startsWithIgnoreCaseAndWs(fnToken, "convert"))
/*     */             {
/* 220 */               newSql.append(processConvertToken(fnToken, serverSupportsConvertFn, conn));
/*     */             }
/*     */             else
/*     */             {
/* 224 */               newSql.append(fnToken);
/*     */             }
/* 226 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{d"))
/*     */           {
/* 228 */             int startPos = token.indexOf('\'') + 1;
/* 229 */             int endPos = token.lastIndexOf('\'');
/*     */ 
/* 231 */             if ((startPos == -1) || (endPos == -1)) {
/* 232 */               newSql.append(token);
/*     */             }
/*     */             else
/*     */             {
/* 238 */               String argument = token.substring(startPos, endPos);
/*     */               try
/*     */               {
/* 241 */                 StringTokenizer st = new StringTokenizer(argument, " -");
/*     */ 
/* 243 */                 String year4 = st.nextToken();
/* 244 */                 String month2 = st.nextToken();
/* 245 */                 String day2 = st.nextToken();
/* 246 */                 String dateString = "'" + year4 + "-" + month2 + "-" + day2 + "'";
/*     */ 
/* 248 */                 newSql.append(dateString);
/*     */               } catch (NoSuchElementException e) {
/* 250 */                 throw SQLError.createSQLException("Syntax error for DATE escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/* 255 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{ts"))
/*     */           {
/* 257 */             processTimestampToken(conn, newSql, token);
/* 258 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{t"))
/*     */           {
/* 260 */             processTimeToken(conn, newSql, token);
/* 261 */           } else if ((StringUtils.startsWithIgnoreCase(collapsedToken, "{call")) || (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")))
/*     */           {
/* 266 */             int startPos = StringUtils.indexOfIgnoreCase(token, "CALL") + 5;
/*     */ 
/* 268 */             int endPos = token.length() - 1;
/*     */ 
/* 270 */             if (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call"))
/*     */             {
/* 272 */               callingStoredFunction = true;
/* 273 */               newSql.append("SELECT ");
/* 274 */               newSql.append(token.substring(startPos, endPos));
/*     */             } else {
/* 276 */               callingStoredFunction = false;
/* 277 */               newSql.append("CALL ");
/* 278 */               newSql.append(token.substring(startPos, endPos));
/*     */             }
/*     */ 
/* 281 */             for (int i = endPos - 1; i >= startPos; i--) {
/* 282 */               char c = token.charAt(i);
/*     */ 
/* 284 */               if (Character.isWhitespace(c))
/*     */               {
/*     */                 continue;
/*     */               }
/* 288 */               if (c == ')') break;
/* 289 */               newSql.append("()"); break;
/*     */             }
/*     */ 
/*     */           }
/* 297 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{oj"))
/*     */           {
/* 301 */             newSql.append(token);
/*     */           }
/*     */         } else {
/* 304 */           newSql.append(token);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 309 */     String escapedSql = newSql.toString();
/*     */ 
/* 315 */     if (replaceEscapeSequence) {
/* 316 */       String currentSql = escapedSql;
/*     */ 
/* 318 */       while (currentSql.indexOf(escapeSequence) != -1) {
/* 319 */         int escapePos = currentSql.indexOf(escapeSequence);
/* 320 */         String lhs = currentSql.substring(0, escapePos);
/* 321 */         String rhs = currentSql.substring(escapePos + 1, currentSql.length());
/*     */ 
/* 323 */         currentSql = lhs + "\\" + rhs;
/*     */       }
/*     */ 
/* 326 */       escapedSql = currentSql;
/*     */     }
/*     */ 
/* 329 */     EscapeProcessorResult epr = new EscapeProcessorResult();
/* 330 */     epr.escapedSql = escapedSql;
/* 331 */     epr.callingStoredFunction = callingStoredFunction;
/*     */ 
/* 333 */     if (usesVariables != 1) {
/* 334 */       if (escapeTokenizer.sawVariableUse())
/* 335 */         epr.usesVariables = 1;
/*     */       else {
/* 337 */         epr.usesVariables = 0;
/*     */       }
/*     */     }
/*     */ 
/* 341 */     return epr;
/*     */   }
/*     */ 
/*     */   private static void processTimeToken(ConnectionImpl conn, StringBuffer newSql, String token) throws SQLException
/*     */   {
/* 346 */     int startPos = token.indexOf('\'') + 1;
/* 347 */     int endPos = token.lastIndexOf('\'');
/*     */ 
/* 349 */     if ((startPos == -1) || (endPos == -1)) {
/* 350 */       newSql.append(token);
/*     */     }
/*     */     else
/*     */     {
/* 356 */       String argument = token.substring(startPos, endPos);
/*     */       try
/*     */       {
/* 359 */         StringTokenizer st = new StringTokenizer(argument, " :");
/*     */ 
/* 361 */         String hour = st.nextToken();
/* 362 */         String minute = st.nextToken();
/* 363 */         String second = st.nextToken();
/*     */ 
/* 365 */         if ((!conn.getUseTimezone()) || (!conn.getUseLegacyDatetimeCode()))
/*     */         {
/* 367 */           String timeString = "'" + hour + ":" + minute + ":" + second + "'";
/*     */ 
/* 369 */           newSql.append(timeString);
/*     */         } else {
/* 371 */           Calendar sessionCalendar = null;
/*     */ 
/* 373 */           if (conn != null) {
/* 374 */             sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
/*     */           }
/*     */           else {
/* 377 */             sessionCalendar = new GregorianCalendar();
/*     */           }
/*     */           try
/*     */           {
/* 381 */             int hourInt = Integer.parseInt(hour);
/* 382 */             int minuteInt = Integer.parseInt(minute);
/*     */ 
/* 384 */             int secondInt = Integer.parseInt(second);
/*     */ 
/* 387 */             synchronized (sessionCalendar) {
/* 388 */               Time toBeAdjusted = TimeUtil.fastTimeCreate(sessionCalendar, hourInt, minuteInt, secondInt, conn.getExceptionInterceptor());
/*     */ 
/* 394 */               Time inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, null, toBeAdjusted, sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
/*     */ 
/* 406 */               newSql.append("'");
/* 407 */               newSql.append(inServerTimezone.toString());
/*     */ 
/* 409 */               newSql.append("'");
/*     */             }
/*     */           }
/*     */           catch (NumberFormatException nfe) {
/* 413 */             throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + token + "'.", "S1009", conn.getExceptionInterceptor());
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (NoSuchElementException e)
/*     */       {
/* 421 */         throw SQLError.createSQLException("Syntax error for escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void processTimestampToken(ConnectionImpl conn, StringBuffer newSql, String token)
/*     */     throws SQLException
/*     */   {
/* 430 */     int startPos = token.indexOf('\'') + 1;
/* 431 */     int endPos = token.lastIndexOf('\'');
/*     */ 
/* 433 */     if ((startPos == -1) || (endPos == -1)) {
/* 434 */       newSql.append(token);
/*     */     }
/*     */     else
/*     */     {
/* 440 */       String argument = token.substring(startPos, endPos);
/*     */       try
/*     */       {
/* 443 */         if (!conn.getUseLegacyDatetimeCode()) {
/* 444 */           Timestamp ts = Timestamp.valueOf(argument);
/* 445 */           SimpleDateFormat tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss''", Locale.US);
/*     */ 
/* 448 */           tsdf.setTimeZone(conn.getServerTimezoneTZ());
/*     */ 
/* 452 */           newSql.append(tsdf.format(ts));
/*     */         } else {
/* 454 */           StringTokenizer st = new StringTokenizer(argument, " .-:");
/*     */           try
/*     */           {
/* 457 */             String year4 = st.nextToken();
/* 458 */             String month2 = st.nextToken();
/* 459 */             String day2 = st.nextToken();
/* 460 */             String hour = st.nextToken();
/* 461 */             String minute = st.nextToken();
/* 462 */             String second = st.nextToken();
/*     */ 
/* 495 */             if ((!conn.getUseTimezone()) && (!conn.getUseJDBCCompliantTimezoneShift()))
/*     */             {
/* 498 */               newSql.append("'").append(year4).append("-").append(month2).append("-").append(day2).append(" ").append(hour).append(":").append(minute).append(":").append(second).append("'");
/*     */             }
/*     */             else
/*     */             {
/*     */               Calendar sessionCalendar;
/*     */               Calendar sessionCalendar;
/* 508 */               if (conn != null) {
/* 509 */                 sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
/*     */               }
/*     */               else {
/* 512 */                 sessionCalendar = new GregorianCalendar();
/* 513 */                 sessionCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */               }
/*     */ 
/*     */               try
/*     */               {
/* 519 */                 int year4Int = Integer.parseInt(year4);
/*     */ 
/* 521 */                 int month2Int = Integer.parseInt(month2);
/*     */ 
/* 523 */                 int day2Int = Integer.parseInt(day2);
/*     */ 
/* 525 */                 int hourInt = Integer.parseInt(hour);
/*     */ 
/* 527 */                 int minuteInt = Integer.parseInt(minute);
/*     */ 
/* 529 */                 int secondInt = Integer.parseInt(second);
/*     */ 
/* 532 */                 synchronized (sessionCalendar) {
/* 533 */                   boolean useGmtMillis = conn.getUseGmtMillisForDatetimes();
/*     */ 
/* 536 */                   Timestamp toBeAdjusted = TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? Calendar.getInstance(TimeZone.getTimeZone("GMT")) : null, sessionCalendar, year4Int, month2Int, day2Int, hourInt, minuteInt, secondInt, 0);
/*     */ 
/* 552 */                   Timestamp inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, null, toBeAdjusted, sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
/*     */ 
/* 564 */                   newSql.append("'");
/*     */ 
/* 566 */                   String timezoneLiteral = inServerTimezone.toString();
/*     */ 
/* 569 */                   int indexOfDot = timezoneLiteral.indexOf(".");
/*     */ 
/* 572 */                   if (indexOfDot != -1) {
/* 573 */                     timezoneLiteral = timezoneLiteral.substring(0, indexOfDot);
/*     */                   }
/*     */ 
/* 578 */                   newSql.append(timezoneLiteral);
/*     */                 }
/*     */ 
/* 582 */                 newSql.append("'");
/*     */               }
/*     */               catch (NumberFormatException nfe) {
/* 585 */                 throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + token + "'.", "S1009", conn.getExceptionInterceptor());
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */           catch (NoSuchElementException e)
/*     */           {
/* 594 */             throw SQLError.createSQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (IllegalArgumentException illegalArgumentException)
/*     */       {
/* 601 */         SQLException sqlEx = SQLError.createSQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */ 
/* 606 */         sqlEx.initCause(illegalArgumentException);
/*     */ 
/* 608 */         throw sqlEx;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String processConvertToken(String functionToken, boolean serverSupportsConvertFn, ConnectionImpl conn)
/*     */     throws SQLException
/*     */   {
/* 653 */     int firstIndexOfParen = functionToken.indexOf("(");
/*     */ 
/* 655 */     if (firstIndexOfParen == -1) {
/* 656 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing opening parenthesis in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 663 */     int tokenLength = functionToken.length();
/*     */ 
/* 665 */     int indexOfComma = functionToken.lastIndexOf(",");
/*     */ 
/* 667 */     if (indexOfComma == -1) {
/* 668 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing comma in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 675 */     int indexOfCloseParen = functionToken.indexOf(')', indexOfComma);
/*     */ 
/* 677 */     if (indexOfCloseParen == -1) {
/* 678 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing closing parenthesis in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 686 */     String expression = functionToken.substring(firstIndexOfParen + 1, indexOfComma);
/*     */ 
/* 688 */     String type = functionToken.substring(indexOfComma + 1, indexOfCloseParen);
/*     */ 
/* 691 */     String newType = null;
/*     */ 
/* 693 */     String trimmedType = type.trim();
/*     */ 
/* 695 */     if (StringUtils.startsWithIgnoreCase(trimmedType, "SQL_")) {
/* 696 */       trimmedType = trimmedType.substring(4, trimmedType.length());
/*     */     }
/*     */ 
/* 699 */     if (serverSupportsConvertFn) {
/* 700 */       newType = (String)JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */     }
/*     */     else {
/* 703 */       newType = (String)JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */ 
/* 713 */       if (newType == null) {
/* 714 */         throw SQLError.createSQLException("Can't find conversion re-write for type '" + type + "' that is applicable for this server version while processing escape tokens.", "S1000", conn.getExceptionInterceptor());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 723 */     if (newType == null) {
/* 724 */       throw SQLError.createSQLException("Unsupported conversion type '" + type.trim() + "' found while processing escape token.", "S1000", conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 729 */     int replaceIndex = newType.indexOf("?");
/*     */ 
/* 731 */     if (replaceIndex != -1) {
/* 732 */       StringBuffer convertRewrite = new StringBuffer(newType.substring(0, replaceIndex));
/*     */ 
/* 734 */       convertRewrite.append(expression);
/* 735 */       convertRewrite.append(newType.substring(replaceIndex + 1, newType.length()));
/*     */ 
/* 738 */       return convertRewrite.toString();
/*     */     }
/*     */ 
/* 741 */     StringBuffer castRewrite = new StringBuffer("CAST(");
/* 742 */     castRewrite.append(expression);
/* 743 */     castRewrite.append(" AS ");
/* 744 */     castRewrite.append(newType);
/* 745 */     castRewrite.append(")");
/*     */ 
/* 747 */     return castRewrite.toString();
/*     */   }
/*     */ 
/*     */   private static String removeWhitespace(String toCollapse)
/*     */   {
/* 761 */     if (toCollapse == null) {
/* 762 */       return null;
/*     */     }
/*     */ 
/* 765 */     int length = toCollapse.length();
/*     */ 
/* 767 */     StringBuffer collapsed = new StringBuffer(length);
/*     */ 
/* 769 */     for (int i = 0; i < length; i++) {
/* 770 */       char c = toCollapse.charAt(i);
/*     */ 
/* 772 */       if (!Character.isWhitespace(c)) {
/* 773 */         collapsed.append(c);
/*     */       }
/*     */     }
/*     */ 
/* 777 */     return collapsed.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  54 */     Map tempMap = new HashMap();
/*     */ 
/*  56 */     tempMap.put("BIGINT", "0 + ?");
/*  57 */     tempMap.put("BINARY", "BINARY");
/*  58 */     tempMap.put("BIT", "0 + ?");
/*  59 */     tempMap.put("CHAR", "CHAR");
/*  60 */     tempMap.put("DATE", "DATE");
/*  61 */     tempMap.put("DECIMAL", "0.0 + ?");
/*  62 */     tempMap.put("DOUBLE", "0.0 + ?");
/*  63 */     tempMap.put("FLOAT", "0.0 + ?");
/*  64 */     tempMap.put("INTEGER", "0 + ?");
/*  65 */     tempMap.put("LONGVARBINARY", "BINARY");
/*  66 */     tempMap.put("LONGVARCHAR", "CONCAT(?)");
/*  67 */     tempMap.put("REAL", "0.0 + ?");
/*  68 */     tempMap.put("SMALLINT", "CONCAT(?)");
/*  69 */     tempMap.put("TIME", "TIME");
/*  70 */     tempMap.put("TIMESTAMP", "DATETIME");
/*  71 */     tempMap.put("TINYINT", "CONCAT(?)");
/*  72 */     tempMap.put("VARBINARY", "BINARY");
/*  73 */     tempMap.put("VARCHAR", "CONCAT(?)");
/*     */ 
/*  75 */     JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
/*     */ 
/*  77 */     tempMap = new HashMap(JDBC_CONVERT_TO_MYSQL_TYPE_MAP);
/*     */ 
/*  79 */     tempMap.put("BINARY", "CONCAT(?)");
/*  80 */     tempMap.put("CHAR", "CONCAT(?)");
/*  81 */     tempMap.remove("DATE");
/*  82 */     tempMap.put("LONGVARBINARY", "CONCAT(?)");
/*  83 */     tempMap.remove("TIME");
/*  84 */     tempMap.remove("TIMESTAMP");
/*  85 */     tempMap.put("VARBINARY", "CONCAT(?)");
/*     */ 
/*  87 */     JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP = Collections.unmodifiableMap(tempMap);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.EscapeProcessor
 * JD-Core Version:    0.6.0
 */