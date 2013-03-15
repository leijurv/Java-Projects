/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.Date;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public abstract class ResultSetRow
/*      */ {
/*      */   protected ExceptionInterceptor exceptionInterceptor;
/*      */   protected Field[] metadata;
/*      */ 
/*      */   protected ResultSetRow(ExceptionInterceptor exceptionInterceptor)
/*      */   {
/*   56 */     this.exceptionInterceptor = exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   public abstract void closeOpenStreams();
/*      */ 
/*      */   public abstract InputStream getBinaryInputStream(int paramInt) throws SQLException;
/*      */ 
/*      */   public abstract byte[] getColumnValue(int paramInt) throws SQLException;
/*      */ 
/*      */   protected final Date getDateFast(int columnIndex, byte[] dateAsBytes, int offset, int length, ConnectionImpl conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
/*  100 */     int year = 0;
/*  101 */     int month = 0;
/*  102 */     int day = 0;
/*      */     SQLException sqlEx;
/*      */     try {
/*  105 */       if (dateAsBytes == null) {
/*  106 */         return null;
/*      */       }
/*      */ 
/*  109 */       boolean allZeroDate = true;
/*      */ 
/*  111 */       boolean onlyTimePresent = false;
/*      */ 
/*  113 */       for (int i = 0; i < length; i++) {
/*  114 */         if (dateAsBytes[(offset + i)] == 58) {
/*  115 */           onlyTimePresent = true;
/*  116 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  120 */       for (int i = 0; i < length; i++) {
/*  121 */         byte b = dateAsBytes[(offset + i)];
/*      */ 
/*  123 */         if ((b == 32) || (b == 45) || (b == 47)) {
/*  124 */           onlyTimePresent = false;
/*      */         }
/*      */ 
/*  127 */         if ((b == 48) || (b == 32) || (b == 58) || (b == 45) || (b == 47) || (b == 46))
/*      */           continue;
/*  129 */         allZeroDate = false;
/*      */ 
/*  131 */         break;
/*      */       }
/*      */ 
/*  135 */       if ((!onlyTimePresent) && (allZeroDate))
/*      */       {
/*  137 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  140 */           return null;
/*  141 */         }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  143 */           throw SQLError.createSQLException("Value '" + new String(dateAsBytes) + "' can not be represented as java.sql.Date", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */ 
/*  151 */         return rs.fastDateCreate(targetCalendar, 1, 1, 1);
/*      */       }
/*  153 */       if (this.metadata[columnIndex].getMysqlType() == 7)
/*      */       {
/*  155 */         switch (length) {
/*      */         case 19:
/*      */         case 21:
/*      */         case 29:
/*  159 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*  161 */           month = StringUtils.getInt(dateAsBytes, offset + 5, offset + 7);
/*      */ 
/*  163 */           day = StringUtils.getInt(dateAsBytes, offset + 8, offset + 10);
/*      */ 
/*  166 */           return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */         case 8:
/*      */         case 14:
/*  171 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*  173 */           month = StringUtils.getInt(dateAsBytes, offset + 4, offset + 6);
/*      */ 
/*  175 */           day = StringUtils.getInt(dateAsBytes, offset + 6, offset + 8);
/*      */ 
/*  178 */           return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */         case 6:
/*      */         case 10:
/*      */         case 12:
/*  184 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 2);
/*      */ 
/*  187 */           if (year <= 69) {
/*  188 */             year += 100;
/*      */           }
/*      */ 
/*  191 */           month = StringUtils.getInt(dateAsBytes, offset + 2, offset + 4);
/*      */ 
/*  193 */           day = StringUtils.getInt(dateAsBytes, offset + 4, offset + 6);
/*      */ 
/*  196 */           return rs.fastDateCreate(targetCalendar, year + 1900, month, day);
/*      */         case 4:
/*  200 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*  203 */           if (year <= 69) {
/*  204 */             year += 100;
/*      */           }
/*      */ 
/*  207 */           month = StringUtils.getInt(dateAsBytes, offset + 2, offset + 4);
/*      */ 
/*  210 */           return rs.fastDateCreate(targetCalendar, year + 1900, month, 1);
/*      */         case 2:
/*  214 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 2);
/*      */ 
/*  217 */           if (year <= 69) {
/*  218 */             year += 100;
/*      */           }
/*      */ 
/*  221 */           return rs.fastDateCreate(targetCalendar, year + 1900, 1, 1);
/*      */         case 3:
/*      */         case 5:
/*      */         case 7:
/*      */         case 9:
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 20:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*  225 */         case 28: } throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { new String(dateAsBytes), Constants.integerValueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*  237 */       if (this.metadata[columnIndex].getMysqlType() == 13)
/*      */       {
/*  239 */         if ((length == 2) || (length == 1)) {
/*  240 */           year = StringUtils.getInt(dateAsBytes, offset, offset + length);
/*      */ 
/*  243 */           if (year <= 69) {
/*  244 */             year += 100;
/*      */           }
/*      */ 
/*  247 */           year += 1900;
/*      */         } else {
/*  249 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */         }
/*      */ 
/*  253 */         return rs.fastDateCreate(targetCalendar, year, 1, 1);
/*  254 */       }if (this.metadata[columnIndex].getMysqlType() == 11) {
/*  255 */         return rs.fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */       }
/*  257 */       if (length < 10) {
/*  258 */         if (length == 8) {
/*  259 */           return rs.fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */         }
/*      */ 
/*  264 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { new String(dateAsBytes), Constants.integerValueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*  277 */       if (length != 18) {
/*  278 */         year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*  280 */         month = StringUtils.getInt(dateAsBytes, offset + 5, offset + 7);
/*      */ 
/*  282 */         day = StringUtils.getInt(dateAsBytes, offset + 8, offset + 10);
/*      */       }
/*      */       else
/*      */       {
/*  287 */         StringTokenizer st = new StringTokenizer(new String(dateAsBytes, offset, length, "ISO8859_1"), "- ");
/*      */ 
/*  290 */         year = Integer.parseInt(st.nextToken());
/*  291 */         month = Integer.parseInt(st.nextToken());
/*  292 */         day = Integer.parseInt(st.nextToken());
/*      */       }
/*      */ 
/*  296 */       return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */     } catch (SQLException sqlEx) {
/*  298 */       throw sqlEx;
/*      */     } catch (Exception e) {
/*  300 */       sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { new String(dateAsBytes), Constants.integerValueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */ 
/*  305 */       sqlEx.initCause(e);
/*      */     }
/*  307 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   public abstract Date getDateFast(int paramInt, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl, Calendar paramCalendar)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract int getInt(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract long getLong(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex, byte[] bits, int offset, int length, ConnectionImpl conn, ResultSetImpl rs, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*  342 */     int year = 0;
/*  343 */     int month = 0;
/*  344 */     int day = 0;
/*      */ 
/*  346 */     if (length != 0) {
/*  347 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*      */ 
/*  349 */       month = bits[(offset + 2)];
/*  350 */       day = bits[(offset + 3)];
/*      */     }
/*      */ 
/*  353 */     if ((length == 0) || ((year == 0) && (month == 0) && (day == 0))) {
/*  354 */       if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  356 */         return null;
/*  357 */       }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  359 */         throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*  365 */       year = 1;
/*  366 */       month = 1;
/*  367 */       day = 1;
/*      */     }
/*      */ 
/*  370 */     if (!rs.useLegacyDatetimeCode) {
/*  371 */       return TimeUtil.fastDateCreate(year, month, day, cal);
/*      */     }
/*      */ 
/*  374 */     return rs.fastDateCreate(cal == null ? rs.getCalendarInstanceForSessionOrNew() : cal, year, month, day);
/*      */   }
/*      */ 
/*      */   public abstract Date getNativeDate(int paramInt, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl, Calendar paramCalendar)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Object getNativeDateTimeValue(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*  386 */     int year = 0;
/*  387 */     int month = 0;
/*  388 */     int day = 0;
/*      */ 
/*  390 */     int hour = 0;
/*  391 */     int minute = 0;
/*  392 */     int seconds = 0;
/*      */ 
/*  394 */     int nanos = 0;
/*      */ 
/*  396 */     if (bits == null)
/*      */     {
/*  398 */       return null;
/*      */     }
/*      */ 
/*  401 */     Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*  405 */     boolean populatedFromDateTimeValue = false;
/*      */ 
/*  407 */     switch (mysqlType) {
/*      */     case 7:
/*      */     case 12:
/*  410 */       populatedFromDateTimeValue = true;
/*      */ 
/*  412 */       if (length == 0) break;
/*  413 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*      */ 
/*  415 */       month = bits[(offset + 2)];
/*  416 */       day = bits[(offset + 3)];
/*      */ 
/*  418 */       if (length > 4) {
/*  419 */         hour = bits[(offset + 4)];
/*  420 */         minute = bits[(offset + 5)];
/*  421 */         seconds = bits[(offset + 6)];
/*      */       }
/*      */ 
/*  424 */       if (length <= 7)
/*      */         break;
/*  426 */       nanos = (bits[(offset + 7)] & 0xFF | (bits[(offset + 8)] & 0xFF) << 8 | (bits[(offset + 9)] & 0xFF) << 16 | (bits[(offset + 10)] & 0xFF) << 24) * 1000; break;
/*      */     case 10:
/*  434 */       populatedFromDateTimeValue = true;
/*      */ 
/*  436 */       if (bits.length == 0) break;
/*  437 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*      */ 
/*  439 */       month = bits[(offset + 2)];
/*  440 */       day = bits[(offset + 3)]; break;
/*      */     case 11:
/*  445 */       populatedFromDateTimeValue = true;
/*      */ 
/*  447 */       if (bits.length != 0)
/*      */       {
/*  450 */         hour = bits[(offset + 5)];
/*  451 */         minute = bits[(offset + 6)];
/*  452 */         seconds = bits[(offset + 7)];
/*      */       }
/*      */ 
/*  455 */       year = 1970;
/*  456 */       month = 1;
/*  457 */       day = 1;
/*      */ 
/*  459 */       break;
/*      */     case 8:
/*      */     case 9:
/*      */     default:
/*  461 */       populatedFromDateTimeValue = false;
/*      */     }
/*      */ 
/*  464 */     switch (jdbcType) {
/*      */     case 92:
/*  466 */       if (populatedFromDateTimeValue) {
/*  467 */         if (!rs.useLegacyDatetimeCode) {
/*  468 */           return TimeUtil.fastTimeCreate(hour, minute, seconds, targetCalendar, this.exceptionInterceptor);
/*      */         }
/*      */ 
/*  471 */         Time time = TimeUtil.fastTimeCreate(rs.getCalendarInstanceForSessionOrNew(), hour, minute, seconds, this.exceptionInterceptor);
/*      */ 
/*  475 */         Time adjustedTime = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, time, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*  479 */         return adjustedTime;
/*      */       }
/*      */ 
/*  482 */       return rs.getNativeTimeViaParseConversion(columnIndex + 1, targetCalendar, tz, rollForward);
/*      */     case 91:
/*  486 */       if (populatedFromDateTimeValue) {
/*  487 */         if ((year == 0) && (month == 0) && (day == 0)) {
/*  488 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  491 */             return null;
/*  492 */           }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  494 */             throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009");
/*      */           }
/*      */ 
/*  499 */           year = 1;
/*  500 */           month = 1;
/*  501 */           day = 1;
/*      */         }
/*      */ 
/*  504 */         if (!rs.useLegacyDatetimeCode) {
/*  505 */           return TimeUtil.fastDateCreate(year, month, day, targetCalendar);
/*      */         }
/*      */ 
/*  508 */         return rs.fastDateCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day);
/*      */       }
/*      */ 
/*  514 */       return rs.getNativeDateViaParseConversion(columnIndex + 1);
/*      */     case 93:
/*  516 */       if (populatedFromDateTimeValue) {
/*  517 */         if ((year == 0) && (month == 0) && (day == 0)) {
/*  518 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  521 */             return null;
/*  522 */           }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  524 */             throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009");
/*      */           }
/*      */ 
/*  529 */           year = 1;
/*  530 */           month = 1;
/*  531 */           day = 1;
/*      */         }
/*      */ 
/*  534 */         if (!rs.useLegacyDatetimeCode) {
/*  535 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minute, seconds, nanos);
/*      */         }
/*      */ 
/*  539 */         Timestamp ts = rs.fastTimestampCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day, hour, minute, seconds, nanos);
/*      */ 
/*  543 */         Timestamp adjustedTs = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, ts, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*  547 */         return adjustedTs;
/*      */       }
/*      */ 
/*  550 */       return rs.getNativeTimestampViaParseConversion(columnIndex + 1, targetCalendar, tz, rollForward);
/*      */     }
/*      */ 
/*  554 */     throw new SQLException("Internal error - conversion method doesn't support this type", "S1000");
/*      */   }
/*      */ 
/*      */   public abstract Object getNativeDateTimeValue(int paramInt1, Calendar paramCalendar, int paramInt2, int paramInt3, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   protected double getNativeDouble(byte[] bits, int offset)
/*      */   {
/*  566 */     long valueAsLong = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24 | (bits[(offset + 4)] & 0xFF) << 32 | (bits[(offset + 5)] & 0xFF) << 40 | (bits[(offset + 6)] & 0xFF) << 48 | (bits[(offset + 7)] & 0xFF) << 56;
/*      */ 
/*  575 */     return Double.longBitsToDouble(valueAsLong);
/*      */   }
/*      */   public abstract double getNativeDouble(int paramInt) throws SQLException;
/*      */ 
/*      */   protected float getNativeFloat(byte[] bits, int offset) {
/*  581 */     int asInt = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24;
/*      */ 
/*  586 */     return Float.intBitsToFloat(asInt);
/*      */   }
/*      */ 
/*      */   public abstract float getNativeFloat(int paramInt) throws SQLException;
/*      */ 
/*      */   protected int getNativeInt(byte[] bits, int offset) {
/*  593 */     int valueAsInt = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24;
/*      */ 
/*  598 */     return valueAsInt;
/*      */   }
/*      */   public abstract int getNativeInt(int paramInt) throws SQLException;
/*      */ 
/*      */   protected long getNativeLong(byte[] bits, int offset) {
/*  604 */     long valueAsLong = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24 | (bits[(offset + 4)] & 0xFF) << 32 | (bits[(offset + 5)] & 0xFF) << 40 | (bits[(offset + 6)] & 0xFF) << 48 | (bits[(offset + 7)] & 0xFF) << 56;
/*      */ 
/*  613 */     return valueAsLong;
/*      */   }
/*      */   public abstract long getNativeLong(int paramInt) throws SQLException;
/*      */ 
/*      */   protected short getNativeShort(byte[] bits, int offset) {
/*  619 */     short asShort = (short)(bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8);
/*      */ 
/*  621 */     return asShort;
/*      */   }
/*      */ 
/*      */   public abstract short getNativeShort(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Time getNativeTime(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*  631 */     int hour = 0;
/*  632 */     int minute = 0;
/*  633 */     int seconds = 0;
/*      */ 
/*  635 */     if (length != 0)
/*      */     {
/*  638 */       hour = bits[(offset + 5)];
/*  639 */       minute = bits[(offset + 6)];
/*  640 */       seconds = bits[(offset + 7)];
/*      */     }
/*      */ 
/*  643 */     if (!rs.useLegacyDatetimeCode) {
/*  644 */       return TimeUtil.fastTimeCreate(hour, minute, seconds, targetCalendar, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*  647 */     Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*  649 */     synchronized (sessionCalendar) {
/*  650 */       Time time = TimeUtil.fastTimeCreate(sessionCalendar, hour, minute, seconds, this.exceptionInterceptor);
/*      */ 
/*  653 */       Time adjustedTime = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, time, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*  657 */       return adjustedTime;
/*      */     }
/*      */   }
/*      */ 
/*      */   public abstract Time getNativeTime(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Timestamp getNativeTimestamp(byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*  668 */     int year = 0;
/*  669 */     int month = 0;
/*  670 */     int day = 0;
/*      */ 
/*  672 */     int hour = 0;
/*  673 */     int minute = 0;
/*  674 */     int seconds = 0;
/*      */ 
/*  676 */     int nanos = 0;
/*      */ 
/*  678 */     if (length != 0) {
/*  679 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*  680 */       month = bits[(offset + 2)];
/*  681 */       day = bits[(offset + 3)];
/*      */ 
/*  683 */       if (length > 4) {
/*  684 */         hour = bits[(offset + 4)];
/*  685 */         minute = bits[(offset + 5)];
/*  686 */         seconds = bits[(offset + 6)];
/*      */       }
/*      */ 
/*  689 */       if (length > 7)
/*      */       {
/*  691 */         nanos = (bits[(offset + 7)] & 0xFF | (bits[(offset + 8)] & 0xFF) << 8 | (bits[(offset + 9)] & 0xFF) << 16 | (bits[(offset + 10)] & 0xFF) << 24) * 1000;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  697 */     if ((length == 0) || ((year == 0) && (month == 0) && (day == 0))) {
/*  698 */       if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  701 */         return null;
/*  702 */       }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  704 */         throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*  710 */       year = 1;
/*  711 */       month = 1;
/*  712 */       day = 1;
/*      */     }
/*      */ 
/*  715 */     if (!rs.useLegacyDatetimeCode) {
/*  716 */       return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minute, seconds, nanos);
/*      */     }
/*      */ 
/*  720 */     Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*  724 */     synchronized (sessionCalendar) {
/*  725 */       Timestamp ts = rs.fastTimestampCreate(sessionCalendar, year, month, day, hour, minute, seconds, nanos);
/*      */ 
/*  728 */       Timestamp adjustedTs = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, ts, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*  732 */       return adjustedTs;
/*      */     }
/*      */   }
/*      */ 
/*      */   public abstract Timestamp getNativeTimestamp(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract Reader getReader(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract String getString(int paramInt, String paramString, ConnectionImpl paramConnectionImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   protected String getString(String encoding, ConnectionImpl conn, byte[] value, int offset, int length)
/*      */     throws SQLException
/*      */   {
/*  786 */     String stringVal = null;
/*      */ 
/*  788 */     if ((conn != null) && (conn.getUseUnicode())) {
/*      */       try {
/*  790 */         if (encoding == null) {
/*  791 */           stringVal = new String(value);
/*      */         } else {
/*  793 */           SingleByteCharsetConverter converter = conn.getCharsetConverter(encoding);
/*      */ 
/*  796 */           if (converter != null)
/*  797 */             stringVal = converter.toString(value, offset, length);
/*      */           else
/*  799 */             stringVal = new String(value, offset, length, encoding);
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException E) {
/*  803 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Unsupported_character_encoding____101") + encoding + "'.", "0S100", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  810 */       stringVal = StringUtils.toAsciiString(value, offset, length);
/*      */     }
/*      */ 
/*  813 */     return stringVal;
/*      */   }
/*      */   protected Time getTimeFast(int columnIndex, byte[] timeAsBytes, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs) throws SQLException {
/*  821 */     int hr = 0;
/*  822 */     int min = 0;
/*  823 */     int sec = 0;
/*      */     SQLException sqlEx;
/*      */     try {
/*  827 */       if (timeAsBytes == null) {
/*  828 */         return null;
/*      */       }
/*      */ 
/*  831 */       boolean allZeroTime = true;
/*  832 */       boolean onlyTimePresent = false;
/*      */ 
/*  834 */       for (int i = 0; i < length; i++) {
/*  835 */         if (timeAsBytes[(offset + i)] == 58) {
/*  836 */           onlyTimePresent = true;
/*  837 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  841 */       for (int i = 0; i < length; i++) {
/*  842 */         byte b = timeAsBytes[(offset + i)];
/*      */ 
/*  844 */         if ((b == 32) || (b == 45) || (b == 47)) {
/*  845 */           onlyTimePresent = false;
/*      */         }
/*      */ 
/*  848 */         if ((b == 48) || (b == 32) || (b == 58) || (b == 45) || (b == 47) || (b == 46))
/*      */           continue;
/*  850 */         allZeroTime = false;
/*      */ 
/*  852 */         break;
/*      */       }
/*      */ 
/*  856 */       if ((!onlyTimePresent) && (allZeroTime)) {
/*  857 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  859 */           return null;
/*  860 */         }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  862 */           throw SQLError.createSQLException("Value '" + new String(timeAsBytes) + "' can not be represented as java.sql.Time", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */ 
/*  870 */         return rs.fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */       }
/*      */ 
/*  873 */       Field timeColField = this.metadata[columnIndex];
/*      */       SQLWarning precisionLost;
/*  875 */       if (timeColField.getMysqlType() == 7)
/*      */       {
/*  877 */         switch (length)
/*      */         {
/*      */         case 19:
/*  880 */           hr = StringUtils.getInt(timeAsBytes, offset + length - 8, offset + length - 6);
/*      */ 
/*  882 */           min = StringUtils.getInt(timeAsBytes, offset + length - 5, offset + length - 3);
/*      */ 
/*  884 */           sec = StringUtils.getInt(timeAsBytes, offset + length - 2, offset + length);
/*      */ 
/*  888 */           break;
/*      */         case 12:
/*      */         case 14:
/*  891 */           hr = StringUtils.getInt(timeAsBytes, offset + length - 6, offset + length - 4);
/*      */ 
/*  893 */           min = StringUtils.getInt(timeAsBytes, offset + length - 4, offset + length - 2);
/*      */ 
/*  895 */           sec = StringUtils.getInt(timeAsBytes, offset + length - 2, offset + length);
/*      */ 
/*  899 */           break;
/*      */         case 10:
/*  902 */           hr = StringUtils.getInt(timeAsBytes, offset + 6, offset + 8);
/*      */ 
/*  904 */           min = StringUtils.getInt(timeAsBytes, offset + 8, offset + 10);
/*      */ 
/*  906 */           sec = 0;
/*      */ 
/*  909 */           break;
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         default:
/*  912 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + (columnIndex + 1) + "(" + timeColField + ").", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */ 
/*  922 */         precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + timeColField + ").");
/*      */       }
/*      */       else
/*      */       {
/*      */         SQLWarning precisionLost;
/*  931 */         if (timeColField.getMysqlType() == 12) {
/*  932 */           hr = StringUtils.getInt(timeAsBytes, offset + 11, offset + 13);
/*  933 */           min = StringUtils.getInt(timeAsBytes, offset + 14, offset + 16);
/*  934 */           sec = StringUtils.getInt(timeAsBytes, offset + 17, offset + 19);
/*      */ 
/*  936 */           precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + (columnIndex + 1) + "(" + timeColField + ").");
/*      */         }
/*      */         else
/*      */         {
/*  946 */           if (timeColField.getMysqlType() == 10) {
/*  947 */             return rs.fastTimeCreate(null, 0, 0, 0);
/*      */           }
/*      */ 
/*  952 */           if ((length != 5) && (length != 8)) {
/*  953 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + new String(timeAsBytes) + Messages.getString("ResultSet.___in_column__268") + (columnIndex + 1), "S1009", this.exceptionInterceptor);
/*      */           }
/*      */ 
/*  961 */           hr = StringUtils.getInt(timeAsBytes, offset + 0, offset + 2);
/*  962 */           min = StringUtils.getInt(timeAsBytes, offset + 3, offset + 5);
/*  963 */           sec = length == 5 ? 0 : StringUtils.getInt(timeAsBytes, offset + 6, offset + 8);
/*      */         }
/*      */       }
/*      */ 
/*  967 */       Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*  969 */       if (!rs.useLegacyDatetimeCode) {
/*  970 */         return rs.fastTimeCreate(targetCalendar, hr, min, sec);
/*      */       }
/*      */ 
/*  973 */       synchronized (sessionCalendar) {
/*  974 */         return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimeCreate(sessionCalendar, hr, min, sec), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/*  980 */       sqlEx = SQLError.createSQLException(ex.toString(), "S1009", this.exceptionInterceptor);
/*      */ 
/*  982 */       sqlEx.initCause(ex);
/*      */     }
/*  984 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   public abstract Time getTimeFast(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Timestamp getTimestampFast(int columnIndex, byte[] timestampAsBytes, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*      */     SQLException sqlEx;
/*      */     try
/*      */     {
/*  998 */       Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/* 1002 */       synchronized (sessionCalendar) {
/* 1003 */         boolean allZeroTimestamp = true;
/*      */ 
/* 1005 */         boolean onlyTimePresent = false;
/*      */ 
/* 1007 */         for (int i = 0; i < length; i++) {
/* 1008 */           if (timestampAsBytes[(offset + i)] == 58) {
/* 1009 */             onlyTimePresent = true;
/* 1010 */             break;
/*      */           }
/*      */         }
/*      */ 
/* 1014 */         for (int i = 0; i < length; i++) {
/* 1015 */           byte b = timestampAsBytes[(offset + i)];
/*      */ 
/* 1017 */           if ((b == 32) || (b == 45) || (b == 47)) {
/* 1018 */             onlyTimePresent = false;
/*      */           }
/*      */ 
/* 1021 */           if ((b == 48) || (b == 32) || (b == 58) || (b == 45) || (b == 47) || (b == 46))
/*      */             continue;
/* 1023 */           allZeroTimestamp = false;
/*      */ 
/* 1025 */           break;
/*      */         }
/*      */ 
/* 1029 */         if ((!onlyTimePresent) && (allZeroTimestamp))
/*      */         {
/* 1031 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/* 1034 */             return null;
/* 1035 */           }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/* 1037 */             throw SQLError.createSQLException("Value '" + timestampAsBytes + "' can not be represented as java.sql.Timestamp", "S1009", this.exceptionInterceptor);
/*      */           }
/*      */ 
/* 1045 */           if (!rs.useLegacyDatetimeCode) {
/* 1046 */             return TimeUtil.fastTimestampCreate(tz, 1, 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/* 1050 */           return rs.fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */         }
/* 1052 */         if (this.metadata[columnIndex].getMysqlType() == 13)
/*      */         {
/* 1054 */           if (!rs.useLegacyDatetimeCode) {
/* 1055 */             return TimeUtil.fastTimestampCreate(tz, StringUtils.getInt(timestampAsBytes, offset, 4), 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/* 1060 */           return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimestampCreate(sessionCalendar, StringUtils.getInt(timestampAsBytes, offset, 4), 1, 1, 0, 0, 0, 0), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */         }
/*      */ 
/* 1067 */         if (timestampAsBytes[(offset + length - 1)] == 46) {
/* 1068 */           length--;
/*      */         }
/*      */ 
/* 1073 */         int year = 0;
/* 1074 */         int month = 0;
/* 1075 */         int day = 0;
/* 1076 */         int hour = 0;
/* 1077 */         int minutes = 0;
/* 1078 */         int seconds = 0;
/* 1079 */         int nanos = 0;
/*      */ 
/* 1081 */         switch (length) {
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 29:
/* 1091 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */ 
/* 1093 */           month = StringUtils.getInt(timestampAsBytes, offset + 5, offset + 7);
/*      */ 
/* 1095 */           day = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1097 */           hour = StringUtils.getInt(timestampAsBytes, offset + 11, offset + 13);
/*      */ 
/* 1099 */           minutes = StringUtils.getInt(timestampAsBytes, offset + 14, offset + 16);
/*      */ 
/* 1101 */           seconds = StringUtils.getInt(timestampAsBytes, offset + 17, offset + 19);
/*      */ 
/* 1104 */           nanos = 0;
/*      */ 
/* 1106 */           if (length <= 19) break;
/* 1107 */           int decimalIndex = -1;
/*      */ 
/* 1109 */           for (int i = 0; i < length; i++) {
/* 1110 */             if (timestampAsBytes[(offset + i)] == 46) {
/* 1111 */               decimalIndex = i;
/*      */             }
/*      */           }
/*      */ 
/* 1115 */           if (decimalIndex != -1)
/* 1116 */             if (decimalIndex + 2 <= length) {
/* 1117 */               nanos = StringUtils.getInt(timestampAsBytes, decimalIndex + 1, offset + length);
/*      */ 
/* 1121 */               int numDigits = offset + length - (decimalIndex + 1);
/*      */ 
/* 1123 */               if (numDigits < 9) {
/* 1124 */                 int factor = (int)Math.pow(10.0D, 9 - numDigits);
/* 1125 */                 nanos *= factor;
/*      */               }
/*      */             } else {
/* 1128 */               throw new IllegalArgumentException();
/*      */             } break;
/*      */         case 14:
/* 1142 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */ 
/* 1144 */           month = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1146 */           day = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1148 */           hour = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1150 */           minutes = StringUtils.getInt(timestampAsBytes, offset + 10, offset + 12);
/*      */ 
/* 1152 */           seconds = StringUtils.getInt(timestampAsBytes, offset + 12, offset + 14);
/*      */ 
/* 1155 */           break;
/*      */         case 12:
/* 1159 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1162 */           if (year <= 69) {
/* 1163 */             year += 100;
/*      */           }
/*      */ 
/* 1166 */           year += 1900;
/*      */ 
/* 1168 */           month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/* 1170 */           day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1172 */           hour = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1174 */           minutes = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1176 */           seconds = StringUtils.getInt(timestampAsBytes, offset + 10, offset + 12);
/*      */ 
/* 1179 */           break;
/*      */         case 10:
/* 1183 */           boolean hasDash = false;
/*      */ 
/* 1185 */           for (int i = 0; i < length; i++) {
/* 1186 */             if (timestampAsBytes[(offset + i)] == 45) {
/* 1187 */               hasDash = true;
/* 1188 */               break;
/*      */             }
/*      */           }
/*      */ 
/* 1192 */           if ((this.metadata[columnIndex].getMysqlType() == 10) || (hasDash))
/*      */           {
/* 1194 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */ 
/* 1196 */             month = StringUtils.getInt(timestampAsBytes, offset + 5, offset + 7);
/*      */ 
/* 1198 */             day = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1200 */             hour = 0;
/* 1201 */             minutes = 0;
/*      */           } else {
/* 1203 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1206 */             if (year <= 69) {
/* 1207 */               year += 100;
/*      */             }
/*      */ 
/* 1210 */             month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/* 1212 */             day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1214 */             hour = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1216 */             minutes = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1219 */             year += 1900;
/*      */           }
/*      */ 
/* 1222 */           break;
/*      */         case 8:
/* 1226 */           boolean hasColon = false;
/*      */ 
/* 1228 */           for (int i = 0; i < length; i++) {
/* 1229 */             if (timestampAsBytes[(offset + i)] == 58) {
/* 1230 */               hasColon = true;
/* 1231 */               break;
/*      */             }
/*      */           }
/*      */ 
/* 1235 */           if (hasColon) {
/* 1236 */             hour = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1238 */             minutes = StringUtils.getInt(timestampAsBytes, offset + 3, offset + 5);
/*      */ 
/* 1240 */             seconds = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1243 */             year = 1970;
/* 1244 */             month = 1;
/* 1245 */             day = 1;
/*      */           }
/*      */           else
/*      */           {
/* 1250 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */ 
/* 1252 */             month = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1254 */             day = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1257 */             year -= 1900;
/* 1258 */             month--;
/*      */           }
/* 1260 */           break;
/*      */         case 6:
/* 1264 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1267 */           if (year <= 69) {
/* 1268 */             year += 100;
/*      */           }
/*      */ 
/* 1271 */           year += 1900;
/*      */ 
/* 1273 */           month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/* 1275 */           day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1278 */           break;
/*      */         case 4:
/* 1282 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1285 */           if (year <= 69) {
/* 1286 */             year += 100;
/*      */           }
/*      */ 
/* 1289 */           month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/* 1292 */           day = 1;
/*      */ 
/* 1294 */           break;
/*      */         case 2:
/* 1298 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1301 */           if (year <= 69) {
/* 1302 */             year += 100;
/*      */           }
/*      */ 
/* 1305 */           year += 1900;
/* 1306 */           month = 1;
/* 1307 */           day = 1;
/*      */ 
/* 1309 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 7:
/*      */         case 9:
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 27:
/*      */         case 28:
/*      */         default:
/* 1313 */           throw new SQLException("Bad format for Timestamp '" + new String(timestampAsBytes) + "' in column " + (columnIndex + 1) + ".", "S1009");
/*      */         }
/*      */ 
/* 1321 */         if (!rs.useLegacyDatetimeCode) {
/* 1322 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */         }
/*      */ 
/* 1328 */         return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1339 */       sqlEx = SQLError.createSQLException("Cannot convert value '" + getString(columnIndex, "ISO8859_1", conn) + "' from column " + (columnIndex + 1) + " to TIMESTAMP.", "S1009", this.exceptionInterceptor);
/*      */ 
/* 1343 */       sqlEx.initCause(e);
/*      */     }
/* 1345 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   public abstract Timestamp getTimestampFast(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, ConnectionImpl paramConnectionImpl, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract boolean isFloatingPointNumber(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract boolean isNull(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract long length(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract void setColumnValue(int paramInt, byte[] paramArrayOfByte)
/*      */     throws SQLException;
/*      */ 
/*      */   public ResultSetRow setMetadata(Field[] f)
/*      */     throws SQLException
/*      */   {
/* 1413 */     this.metadata = f;
/*      */ 
/* 1415 */     return this;
/*      */   }
/*      */ 
/*      */   public abstract int getBytesSize();
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ResultSetRow
 * JD-Core Version:    0.6.0
 */