/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public class TimeUtil
/*      */ {
/*      */   static final Map ABBREVIATED_TIMEZONES;
/*   50 */   static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
/*      */   static final Map TIMEZONE_MAPPINGS;
/*      */ 
/*      */   public static Time changeTimezone(ConnectionImpl conn, Calendar sessionCalendar, Calendar targetCalendar, Time t, TimeZone fromTz, TimeZone toTz, boolean rollForward)
/*      */   {
/*  831 */     if (conn != null) {
/*  832 */       if ((conn.getUseTimezone()) && (!conn.getNoTimezoneConversionForTimeType()))
/*      */       {
/*  835 */         Calendar fromCal = Calendar.getInstance(fromTz);
/*  836 */         fromCal.setTime(t);
/*      */ 
/*  838 */         int fromOffset = fromCal.get(15) + fromCal.get(16);
/*      */ 
/*  840 */         Calendar toCal = Calendar.getInstance(toTz);
/*  841 */         toCal.setTime(t);
/*      */ 
/*  843 */         int toOffset = toCal.get(15) + toCal.get(16);
/*      */ 
/*  845 */         int offsetDiff = fromOffset - toOffset;
/*  846 */         long toTime = toCal.getTime().getTime();
/*      */ 
/*  848 */         if ((rollForward) || ((conn.isServerTzUTC()) && (!conn.isClientTzUTC())))
/*  849 */           toTime += offsetDiff;
/*      */         else {
/*  851 */           toTime -= offsetDiff;
/*      */         }
/*      */ 
/*  854 */         Time changedTime = new Time(toTime);
/*      */ 
/*  856 */         return changedTime;
/*  857 */       }if ((conn.getUseJDBCCompliantTimezoneShift()) && 
/*  858 */         (targetCalendar != null))
/*      */       {
/*  860 */         Time adjustedTime = new Time(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, t));
/*      */ 
/*  864 */         return adjustedTime;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  869 */     return t;
/*      */   }
/*      */ 
/*      */   public static Timestamp changeTimezone(ConnectionImpl conn, Calendar sessionCalendar, Calendar targetCalendar, Timestamp tstamp, TimeZone fromTz, TimeZone toTz, boolean rollForward)
/*      */   {
/*  893 */     if (conn != null) {
/*  894 */       if (conn.getUseTimezone())
/*      */       {
/*  896 */         Calendar fromCal = Calendar.getInstance(fromTz);
/*  897 */         fromCal.setTime(tstamp);
/*      */ 
/*  899 */         int fromOffset = fromCal.get(15) + fromCal.get(16);
/*      */ 
/*  901 */         Calendar toCal = Calendar.getInstance(toTz);
/*  902 */         toCal.setTime(tstamp);
/*      */ 
/*  904 */         int toOffset = toCal.get(15) + toCal.get(16);
/*      */ 
/*  906 */         int offsetDiff = fromOffset - toOffset;
/*  907 */         long toTime = toCal.getTime().getTime();
/*      */ 
/*  909 */         if ((rollForward) || ((conn.isServerTzUTC()) && (!conn.isClientTzUTC())))
/*  910 */           toTime += offsetDiff;
/*      */         else {
/*  912 */           toTime -= offsetDiff;
/*      */         }
/*      */ 
/*  915 */         Timestamp changedTimestamp = new Timestamp(toTime);
/*      */ 
/*  917 */         return changedTimestamp;
/*  918 */       }if ((conn.getUseJDBCCompliantTimezoneShift()) && 
/*  919 */         (targetCalendar != null))
/*      */       {
/*  921 */         Timestamp adjustedTimestamp = new Timestamp(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, tstamp));
/*      */ 
/*  925 */         adjustedTimestamp.setNanos(tstamp.getNanos());
/*      */ 
/*  927 */         return adjustedTimestamp;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  932 */     return tstamp;
/*      */   }
/*      */ 
/*      */   private static long jdbcCompliantZoneShift(Calendar sessionCalendar, Calendar targetCalendar, java.util.Date dt)
/*      */   {
/*  938 */     if (sessionCalendar == null) {
/*  939 */       sessionCalendar = new GregorianCalendar();
/*      */     }
/*      */ 
/*  946 */     java.util.Date origCalDate = targetCalendar.getTime();
/*  947 */     java.util.Date origSessionDate = sessionCalendar.getTime();
/*      */     try
/*      */     {
/*  950 */       sessionCalendar.setTime(dt);
/*      */ 
/*  952 */       targetCalendar.set(1, sessionCalendar.get(1));
/*  953 */       targetCalendar.set(2, sessionCalendar.get(2));
/*  954 */       targetCalendar.set(5, sessionCalendar.get(5));
/*      */ 
/*  956 */       targetCalendar.set(11, sessionCalendar.get(11));
/*  957 */       targetCalendar.set(12, sessionCalendar.get(12));
/*  958 */       targetCalendar.set(13, sessionCalendar.get(13));
/*  959 */       targetCalendar.set(14, sessionCalendar.get(14));
/*      */ 
/*  961 */       long l = targetCalendar.getTime().getTime();
/*      */       return l;
/*      */     }
/*      */     finally
/*      */     {
/*  964 */       sessionCalendar.setTime(origSessionDate);
/*  965 */       targetCalendar.setTime(origCalDate); } throw localObject;
/*      */   }
/*      */ 
/*      */   static final java.sql.Date fastDateCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day)
/*      */   {
/*  977 */     Calendar dateCal = cal;
/*      */ 
/*  979 */     if (useGmtConversion)
/*      */     {
/*  981 */       if (gmtCalIfNeeded == null) {
/*  982 */         gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */       }
/*  984 */       gmtCalIfNeeded.clear();
/*      */ 
/*  986 */       dateCal = gmtCalIfNeeded;
/*      */     }
/*      */ 
/*  989 */     dateCal.clear();
/*  990 */     dateCal.set(14, 0);
/*      */ 
/*  995 */     dateCal.set(year, month - 1, day, 0, 0, 0);
/*      */ 
/*  997 */     long dateAsMillis = 0L;
/*      */     try
/*      */     {
/* 1000 */       dateAsMillis = dateCal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1003 */       dateAsMillis = dateCal.getTime().getTime();
/*      */     }
/*      */ 
/* 1006 */     return new java.sql.Date(dateAsMillis);
/*      */   }
/*      */ 
/*      */   static final java.sql.Date fastDateCreate(int year, int month, int day, Calendar targetCalendar)
/*      */   {
/* 1012 */     Calendar dateCal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
/*      */ 
/* 1014 */     dateCal.clear();
/*      */ 
/* 1020 */     dateCal.set(year, month - 1, day, 0, 0, 0);
/* 1021 */     dateCal.set(14, 0);
/*      */ 
/* 1023 */     long dateAsMillis = 0L;
/*      */     try
/*      */     {
/* 1026 */       dateAsMillis = dateCal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1029 */       dateAsMillis = dateCal.getTime().getTime();
/*      */     }
/*      */ 
/* 1032 */     return new java.sql.Date(dateAsMillis);
/*      */   }
/*      */ 
/*      */   static final Time fastTimeCreate(Calendar cal, int hour, int minute, int second, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*      */   {
/* 1037 */     if ((hour < 0) || (hour > 24)) {
/* 1038 */       throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1043 */     if ((minute < 0) || (minute > 59)) {
/* 1044 */       throw SQLError.createSQLException("Illegal minute value '" + minute + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1049 */     if ((second < 0) || (second > 59)) {
/* 1050 */       throw SQLError.createSQLException("Illegal minute value '" + second + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1055 */     cal.clear();
/*      */ 
/* 1058 */     cal.set(1970, 0, 1, hour, minute, second);
/*      */ 
/* 1060 */     long timeAsMillis = 0L;
/*      */     try
/*      */     {
/* 1063 */       timeAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1066 */       timeAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/* 1069 */     return new Time(timeAsMillis);
/*      */   }
/*      */ 
/*      */   static final Time fastTimeCreate(int hour, int minute, int second, Calendar targetCalendar, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*      */   {
/* 1074 */     if ((hour < 0) || (hour > 23)) {
/* 1075 */       throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1080 */     if ((minute < 0) || (minute > 59)) {
/* 1081 */       throw SQLError.createSQLException("Illegal minute value '" + minute + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1086 */     if ((second < 0) || (second > 59)) {
/* 1087 */       throw SQLError.createSQLException("Illegal minute value '" + second + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1092 */     Calendar cal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
/* 1093 */     cal.clear();
/*      */ 
/* 1096 */     cal.set(1970, 0, 1, hour, minute, second);
/*      */ 
/* 1098 */     long timeAsMillis = 0L;
/*      */     try
/*      */     {
/* 1101 */       timeAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1104 */       timeAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/* 1107 */     return new Time(timeAsMillis);
/*      */   }
/*      */ 
/*      */   static final Timestamp fastTimestampCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*      */   {
/* 1115 */     cal.clear();
/*      */ 
/* 1120 */     cal.set(year, month - 1, day, hour, minute, seconds);
/*      */ 
/* 1122 */     int offsetDiff = 0;
/*      */ 
/* 1124 */     if (useGmtConversion) {
/* 1125 */       int fromOffset = cal.get(15) + cal.get(16);
/*      */ 
/* 1128 */       if (gmtCalIfNeeded == null) {
/* 1129 */         gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */       }
/* 1131 */       gmtCalIfNeeded.clear();
/*      */ 
/* 1133 */       gmtCalIfNeeded.setTimeInMillis(cal.getTimeInMillis());
/*      */ 
/* 1135 */       int toOffset = gmtCalIfNeeded.get(15) + gmtCalIfNeeded.get(16);
/*      */ 
/* 1137 */       offsetDiff = fromOffset - toOffset;
/*      */     }
/*      */ 
/* 1140 */     if (secondsPart != 0) {
/* 1141 */       cal.set(14, secondsPart / 1000000);
/*      */     }
/*      */ 
/* 1144 */     long tsAsMillis = 0L;
/*      */     try
/*      */     {
/* 1148 */       tsAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1151 */       tsAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/* 1154 */     Timestamp ts = new Timestamp(tsAsMillis + offsetDiff);
/*      */ 
/* 1156 */     ts.setNanos(secondsPart);
/*      */ 
/* 1158 */     return ts;
/*      */   }
/*      */ 
/*      */   static final Timestamp fastTimestampCreate(TimeZone tz, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*      */   {
/* 1164 */     Calendar cal = tz == null ? new GregorianCalendar() : new GregorianCalendar(tz);
/* 1165 */     cal.clear();
/*      */ 
/* 1170 */     cal.set(year, month - 1, day, hour, minute, seconds);
/*      */ 
/* 1172 */     long tsAsMillis = 0L;
/*      */     try
/*      */     {
/* 1175 */       tsAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1178 */       tsAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/* 1181 */     Timestamp ts = new Timestamp(tsAsMillis);
/* 1182 */     ts.setNanos(secondsPart);
/*      */ 
/* 1184 */     return ts;
/*      */   }
/*      */ 
/*      */   public static String getCanoncialTimezone(String timezoneStr, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/* 1200 */     if (timezoneStr == null) {
/* 1201 */       return null;
/*      */     }
/*      */ 
/* 1204 */     timezoneStr = timezoneStr.trim();
/*      */ 
/* 1208 */     if ((timezoneStr.length() > 2) && 
/* 1209 */       ((timezoneStr.charAt(0) == '+') || (timezoneStr.charAt(0) == '-')) && (Character.isDigit(timezoneStr.charAt(1))))
/*      */     {
/* 1211 */       return "GMT" + timezoneStr;
/*      */     }
/*      */ 
/* 1216 */     int daylightIndex = StringUtils.indexOfIgnoreCase(timezoneStr, "DAYLIGHT");
/*      */ 
/* 1219 */     if (daylightIndex != -1) {
/* 1220 */       StringBuffer timezoneBuf = new StringBuffer();
/* 1221 */       timezoneBuf.append(timezoneStr.substring(0, daylightIndex));
/* 1222 */       timezoneBuf.append("Standard");
/* 1223 */       timezoneBuf.append(timezoneStr.substring(daylightIndex + "DAYLIGHT".length(), timezoneStr.length()));
/*      */ 
/* 1225 */       timezoneStr = timezoneBuf.toString();
/*      */     }
/*      */ 
/* 1228 */     String canonicalTz = (String)TIMEZONE_MAPPINGS.get(timezoneStr);
/*      */ 
/* 1231 */     if (canonicalTz == null) {
/* 1232 */       String[] abbreviatedTimezone = (String[])ABBREVIATED_TIMEZONES.get(timezoneStr);
/*      */ 
/* 1235 */       if (abbreviatedTimezone != null)
/*      */       {
/* 1237 */         if (abbreviatedTimezone.length == 1) {
/* 1238 */           canonicalTz = abbreviatedTimezone[0];
/*      */         } else {
/* 1240 */           StringBuffer possibleTimezones = new StringBuffer(128);
/*      */ 
/* 1242 */           possibleTimezones.append(abbreviatedTimezone[0]);
/*      */ 
/* 1244 */           for (int i = 1; i < abbreviatedTimezone.length; i++) {
/* 1245 */             possibleTimezones.append(", ");
/* 1246 */             possibleTimezones.append(abbreviatedTimezone[i]);
/*      */           }
/*      */ 
/* 1249 */           throw SQLError.createSQLException(Messages.getString("TimeUtil.TooGenericTimezoneId", new Object[] { timezoneStr, possibleTimezones }), "01S00", exceptionInterceptor);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1255 */     return canonicalTz;
/*      */   }
/*      */ 
/*      */   private static String timeFormattedString(int hours, int minutes, int seconds)
/*      */   {
/* 1264 */     StringBuffer buf = new StringBuffer(8);
/* 1265 */     if (hours < 10) {
/* 1266 */       buf.append("0");
/*      */     }
/*      */ 
/* 1269 */     buf.append(hours);
/* 1270 */     buf.append(":");
/*      */ 
/* 1272 */     if (minutes < 10) {
/* 1273 */       buf.append("0");
/*      */     }
/*      */ 
/* 1276 */     buf.append(minutes);
/* 1277 */     buf.append(":");
/*      */ 
/* 1279 */     if (seconds < 10) {
/* 1280 */       buf.append("0");
/*      */     }
/*      */ 
/* 1283 */     buf.append(seconds);
/*      */ 
/* 1285 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   55 */     HashMap tempMap = new HashMap();
/*      */ 
/*   60 */     tempMap.put("Romance", "Europe/Paris");
/*   61 */     tempMap.put("Romance Standard Time", "Europe/Paris");
/*   62 */     tempMap.put("Warsaw", "Europe/Warsaw");
/*   63 */     tempMap.put("Central Europe", "Europe/Prague");
/*   64 */     tempMap.put("Central Europe Standard Time", "Europe/Prague");
/*   65 */     tempMap.put("Prague Bratislava", "Europe/Prague");
/*   66 */     tempMap.put("W. Central Africa Standard Time", "Africa/Luanda");
/*   67 */     tempMap.put("FLE", "Europe/Helsinki");
/*   68 */     tempMap.put("FLE Standard Time", "Europe/Helsinki");
/*   69 */     tempMap.put("GFT", "Europe/Athens");
/*   70 */     tempMap.put("GFT Standard Time", "Europe/Athens");
/*   71 */     tempMap.put("GTB", "Europe/Athens");
/*   72 */     tempMap.put("GTB Standard Time", "Europe/Athens");
/*   73 */     tempMap.put("Israel", "Asia/Jerusalem");
/*   74 */     tempMap.put("Israel Standard Time", "Asia/Jerusalem");
/*   75 */     tempMap.put("Arab", "Asia/Riyadh");
/*   76 */     tempMap.put("Arab Standard Time", "Asia/Riyadh");
/*   77 */     tempMap.put("Arabic Standard Time", "Asia/Baghdad");
/*   78 */     tempMap.put("E. Africa", "Africa/Nairobi");
/*   79 */     tempMap.put("E. Africa Standard Time", "Africa/Nairobi");
/*   80 */     tempMap.put("Saudi Arabia", "Asia/Riyadh");
/*   81 */     tempMap.put("Saudi Arabia Standard Time", "Asia/Riyadh");
/*   82 */     tempMap.put("Iran", "Asia/Tehran");
/*   83 */     tempMap.put("Iran Standard Time", "Asia/Tehran");
/*   84 */     tempMap.put("Afghanistan", "Asia/Kabul");
/*   85 */     tempMap.put("Afghanistan Standard Time", "Asia/Kabul");
/*   86 */     tempMap.put("India", "Asia/Calcutta");
/*   87 */     tempMap.put("India Standard Time", "Asia/Calcutta");
/*   88 */     tempMap.put("Myanmar Standard Time", "Asia/Rangoon");
/*   89 */     tempMap.put("Nepal Standard Time", "Asia/Katmandu");
/*   90 */     tempMap.put("Sri Lanka", "Asia/Colombo");
/*   91 */     tempMap.put("Sri Lanka Standard Time", "Asia/Colombo");
/*   92 */     tempMap.put("Beijing", "Asia/Shanghai");
/*   93 */     tempMap.put("China", "Asia/Shanghai");
/*   94 */     tempMap.put("China Standard Time", "Asia/Shanghai");
/*   95 */     tempMap.put("AUS Central", "Australia/Darwin");
/*   96 */     tempMap.put("AUS Central Standard Time", "Australia/Darwin");
/*   97 */     tempMap.put("Cen. Australia", "Australia/Adelaide");
/*   98 */     tempMap.put("Cen. Australia Standard Time", "Australia/Adelaide");
/*   99 */     tempMap.put("Vladivostok", "Asia/Vladivostok");
/*  100 */     tempMap.put("Vladivostok Standard Time", "Asia/Vladivostok");
/*  101 */     tempMap.put("West Pacific", "Pacific/Guam");
/*  102 */     tempMap.put("West Pacific Standard Time", "Pacific/Guam");
/*  103 */     tempMap.put("E. South America", "America/Sao_Paulo");
/*  104 */     tempMap.put("E. South America Standard Time", "America/Sao_Paulo");
/*  105 */     tempMap.put("Greenland Standard Time", "America/Godthab");
/*  106 */     tempMap.put("Newfoundland", "America/St_Johns");
/*  107 */     tempMap.put("Newfoundland Standard Time", "America/St_Johns");
/*  108 */     tempMap.put("Pacific SA", "America/Caracas");
/*  109 */     tempMap.put("Pacific SA Standard Time", "America/Caracas");
/*  110 */     tempMap.put("SA Western", "America/Caracas");
/*  111 */     tempMap.put("SA Western Standard Time", "America/Caracas");
/*  112 */     tempMap.put("SA Pacific", "America/Bogota");
/*  113 */     tempMap.put("SA Pacific Standard Time", "America/Bogota");
/*  114 */     tempMap.put("US Eastern", "America/Indianapolis");
/*  115 */     tempMap.put("US Eastern Standard Time", "America/Indianapolis");
/*  116 */     tempMap.put("Central America Standard Time", "America/Regina");
/*  117 */     tempMap.put("Mexico", "America/Mexico_City");
/*  118 */     tempMap.put("Mexico Standard Time", "America/Mexico_City");
/*  119 */     tempMap.put("Canada Central", "America/Regina");
/*  120 */     tempMap.put("Canada Central Standard Time", "America/Regina");
/*  121 */     tempMap.put("US Mountain", "America/Phoenix");
/*  122 */     tempMap.put("US Mountain Standard Time", "America/Phoenix");
/*  123 */     tempMap.put("GMT", "GMT");
/*  124 */     tempMap.put("Ekaterinburg", "Asia/Yekaterinburg");
/*  125 */     tempMap.put("Ekaterinburg Standard Time", "Asia/Yekaterinburg");
/*  126 */     tempMap.put("West Asia", "Asia/Karachi");
/*  127 */     tempMap.put("West Asia Standard Time", "Asia/Karachi");
/*  128 */     tempMap.put("Central Asia", "Asia/Dhaka");
/*  129 */     tempMap.put("Central Asia Standard Time", "Asia/Dhaka");
/*  130 */     tempMap.put("N. Central Asia Standard Time", "Asia/Novosibirsk");
/*  131 */     tempMap.put("Bangkok", "Asia/Bangkok");
/*  132 */     tempMap.put("Bangkok Standard Time", "Asia/Bangkok");
/*  133 */     tempMap.put("North Asia Standard Time", "Asia/Krasnoyarsk");
/*  134 */     tempMap.put("SE Asia", "Asia/Bangkok");
/*  135 */     tempMap.put("SE Asia Standard Time", "Asia/Bangkok");
/*  136 */     tempMap.put("North Asia East Standard Time", "Asia/Ulaanbaatar");
/*  137 */     tempMap.put("Singapore", "Asia/Singapore");
/*  138 */     tempMap.put("Singapore Standard Time", "Asia/Singapore");
/*  139 */     tempMap.put("Taipei", "Asia/Taipei");
/*  140 */     tempMap.put("Taipei Standard Time", "Asia/Taipei");
/*  141 */     tempMap.put("W. Australia", "Australia/Perth");
/*  142 */     tempMap.put("W. Australia Standard Time", "Australia/Perth");
/*  143 */     tempMap.put("Korea", "Asia/Seoul");
/*  144 */     tempMap.put("Korea Standard Time", "Asia/Seoul");
/*  145 */     tempMap.put("Tokyo", "Asia/Tokyo");
/*  146 */     tempMap.put("Tokyo Standard Time", "Asia/Tokyo");
/*  147 */     tempMap.put("Yakutsk", "Asia/Yakutsk");
/*  148 */     tempMap.put("Yakutsk Standard Time", "Asia/Yakutsk");
/*  149 */     tempMap.put("Central European", "Europe/Belgrade");
/*  150 */     tempMap.put("Central European Standard Time", "Europe/Belgrade");
/*  151 */     tempMap.put("W. Europe", "Europe/Berlin");
/*  152 */     tempMap.put("W. Europe Standard Time", "Europe/Berlin");
/*  153 */     tempMap.put("Tasmania", "Australia/Hobart");
/*  154 */     tempMap.put("Tasmania Standard Time", "Australia/Hobart");
/*  155 */     tempMap.put("AUS Eastern", "Australia/Sydney");
/*  156 */     tempMap.put("AUS Eastern Standard Time", "Australia/Sydney");
/*  157 */     tempMap.put("E. Australia", "Australia/Brisbane");
/*  158 */     tempMap.put("E. Australia Standard Time", "Australia/Brisbane");
/*  159 */     tempMap.put("Sydney Standard Time", "Australia/Sydney");
/*  160 */     tempMap.put("Central Pacific", "Pacific/Guadalcanal");
/*  161 */     tempMap.put("Central Pacific Standard Time", "Pacific/Guadalcanal");
/*  162 */     tempMap.put("Dateline", "Pacific/Majuro");
/*  163 */     tempMap.put("Dateline Standard Time", "Pacific/Majuro");
/*  164 */     tempMap.put("Fiji", "Pacific/Fiji");
/*  165 */     tempMap.put("Fiji Standard Time", "Pacific/Fiji");
/*  166 */     tempMap.put("Samoa", "Pacific/Apia");
/*  167 */     tempMap.put("Samoa Standard Time", "Pacific/Apia");
/*  168 */     tempMap.put("Hawaiian", "Pacific/Honolulu");
/*  169 */     tempMap.put("Hawaiian Standard Time", "Pacific/Honolulu");
/*  170 */     tempMap.put("Alaskan", "America/Anchorage");
/*  171 */     tempMap.put("Alaskan Standard Time", "America/Anchorage");
/*  172 */     tempMap.put("Pacific", "America/Los_Angeles");
/*  173 */     tempMap.put("Pacific Standard Time", "America/Los_Angeles");
/*  174 */     tempMap.put("Mexico Standard Time 2", "America/Chihuahua");
/*  175 */     tempMap.put("Mountain", "America/Denver");
/*  176 */     tempMap.put("Mountain Standard Time", "America/Denver");
/*  177 */     tempMap.put("Central", "America/Chicago");
/*  178 */     tempMap.put("Central Standard Time", "America/Chicago");
/*  179 */     tempMap.put("Eastern", "America/New_York");
/*  180 */     tempMap.put("Eastern Standard Time", "America/New_York");
/*  181 */     tempMap.put("E. Europe", "Europe/Bucharest");
/*  182 */     tempMap.put("E. Europe Standard Time", "Europe/Bucharest");
/*  183 */     tempMap.put("Egypt", "Africa/Cairo");
/*  184 */     tempMap.put("Egypt Standard Time", "Africa/Cairo");
/*  185 */     tempMap.put("South Africa", "Africa/Harare");
/*  186 */     tempMap.put("South Africa Standard Time", "Africa/Harare");
/*  187 */     tempMap.put("Atlantic", "America/Halifax");
/*  188 */     tempMap.put("Atlantic Standard Time", "America/Halifax");
/*  189 */     tempMap.put("SA Eastern", "America/Buenos_Aires");
/*  190 */     tempMap.put("SA Eastern Standard Time", "America/Buenos_Aires");
/*  191 */     tempMap.put("Mid-Atlantic", "Atlantic/South_Georgia");
/*  192 */     tempMap.put("Mid-Atlantic Standard Time", "Atlantic/South_Georgia");
/*  193 */     tempMap.put("Azores", "Atlantic/Azores");
/*  194 */     tempMap.put("Azores Standard Time", "Atlantic/Azores");
/*  195 */     tempMap.put("Cape Verde Standard Time", "Atlantic/Cape_Verde");
/*  196 */     tempMap.put("Russian", "Europe/Moscow");
/*  197 */     tempMap.put("Russian Standard Time", "Europe/Moscow");
/*  198 */     tempMap.put("New Zealand", "Pacific/Auckland");
/*  199 */     tempMap.put("New Zealand Standard Time", "Pacific/Auckland");
/*  200 */     tempMap.put("Tonga Standard Time", "Pacific/Tongatapu");
/*  201 */     tempMap.put("Arabian", "Asia/Muscat");
/*  202 */     tempMap.put("Arabian Standard Time", "Asia/Muscat");
/*  203 */     tempMap.put("Caucasus", "Asia/Tbilisi");
/*  204 */     tempMap.put("Caucasus Standard Time", "Asia/Tbilisi");
/*  205 */     tempMap.put("GMT Standard Time", "GMT");
/*  206 */     tempMap.put("Greenwich", "GMT");
/*  207 */     tempMap.put("Greenwich Standard Time", "GMT");
/*  208 */     tempMap.put("UTC", "GMT");
/*      */ 
/*  211 */     Iterator entries = tempMap.entrySet().iterator();
/*  212 */     Map entryMap = new HashMap(tempMap.size());
/*      */ 
/*  214 */     while (entries.hasNext()) {
/*  215 */       String name = ((Map.Entry)entries.next()).getValue().toString();
/*  216 */       entryMap.put(name, name);
/*      */     }
/*      */ 
/*  219 */     tempMap.putAll(entryMap);
/*      */ 
/*  221 */     TIMEZONE_MAPPINGS = Collections.unmodifiableMap(tempMap);
/*      */ 
/*  226 */     tempMap = new HashMap();
/*      */ 
/*  228 */     tempMap.put("ACST", new String[] { "America/Porto_Acre" });
/*  229 */     tempMap.put("ACT", new String[] { "America/Porto_Acre" });
/*  230 */     tempMap.put("ADDT", new String[] { "America/Pangnirtung" });
/*  231 */     tempMap.put("ADMT", new String[] { "Africa/Asmera", "Africa/Addis_Ababa" });
/*      */ 
/*  233 */     tempMap.put("ADT", new String[] { "Atlantic/Bermuda", "Asia/Baghdad", "America/Thule", "America/Goose_Bay", "America/Halifax", "America/Glace_Bay", "America/Pangnirtung", "America/Barbados", "America/Martinique" });
/*      */ 
/*  237 */     tempMap.put("AFT", new String[] { "Asia/Kabul" });
/*  238 */     tempMap.put("AHDT", new String[] { "America/Anchorage" });
/*  239 */     tempMap.put("AHST", new String[] { "America/Anchorage" });
/*  240 */     tempMap.put("AHWT", new String[] { "America/Anchorage" });
/*  241 */     tempMap.put("AKDT", new String[] { "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  243 */     tempMap.put("AKST", new String[] { "Asia/Aqtobe", "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  245 */     tempMap.put("AKT", new String[] { "Asia/Aqtobe" });
/*  246 */     tempMap.put("AKTST", new String[] { "Asia/Aqtobe" });
/*  247 */     tempMap.put("AKWT", new String[] { "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  249 */     tempMap.put("ALMST", new String[] { "Asia/Almaty" });
/*  250 */     tempMap.put("ALMT", new String[] { "Asia/Almaty" });
/*  251 */     tempMap.put("AMST", new String[] { "Asia/Yerevan", "America/Cuiaba", "America/Porto_Velho", "America/Boa_Vista", "America/Manaus" });
/*      */ 
/*  253 */     tempMap.put("AMT", new String[] { "Europe/Athens", "Europe/Amsterdam", "Asia/Yerevan", "Africa/Asmera", "America/Cuiaba", "America/Porto_Velho", "America/Boa_Vista", "America/Manaus", "America/Asuncion" });
/*      */ 
/*  257 */     tempMap.put("ANAMT", new String[] { "Asia/Anadyr" });
/*  258 */     tempMap.put("ANAST", new String[] { "Asia/Anadyr" });
/*  259 */     tempMap.put("ANAT", new String[] { "Asia/Anadyr" });
/*  260 */     tempMap.put("ANT", new String[] { "America/Aruba", "America/Curacao" });
/*  261 */     tempMap.put("AQTST", new String[] { "Asia/Aqtobe", "Asia/Aqtau" });
/*  262 */     tempMap.put("AQTT", new String[] { "Asia/Aqtobe", "Asia/Aqtau" });
/*  263 */     tempMap.put("ARST", new String[] { "Antarctica/Palmer", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza" });
/*      */ 
/*  266 */     tempMap.put("ART", new String[] { "Antarctica/Palmer", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza" });
/*      */ 
/*  269 */     tempMap.put("ASHST", new String[] { "Asia/Ashkhabad" });
/*  270 */     tempMap.put("ASHT", new String[] { "Asia/Ashkhabad" });
/*  271 */     tempMap.put("AST", new String[] { "Atlantic/Bermuda", "Asia/Bahrain", "Asia/Baghdad", "Asia/Kuwait", "Asia/Qatar", "Asia/Riyadh", "Asia/Aden", "America/Thule", "America/Goose_Bay", "America/Halifax", "America/Glace_Bay", "America/Pangnirtung", "America/Anguilla", "America/Antigua", "America/Barbados", "America/Dominica", "America/Santo_Domingo", "America/Grenada", "America/Guadeloupe", "America/Martinique", "America/Montserrat", "America/Puerto_Rico", "America/St_Kitts", "America/St_Lucia", "America/Miquelon", "America/St_Vincent", "America/Tortola", "America/St_Thomas", "America/Aruba", "America/Curacao", "America/Port_of_Spain" });
/*      */ 
/*  282 */     tempMap.put("AWT", new String[] { "America/Puerto_Rico" });
/*  283 */     tempMap.put("AZOST", new String[] { "Atlantic/Azores" });
/*  284 */     tempMap.put("AZOT", new String[] { "Atlantic/Azores" });
/*  285 */     tempMap.put("AZST", new String[] { "Asia/Baku" });
/*  286 */     tempMap.put("AZT", new String[] { "Asia/Baku" });
/*  287 */     tempMap.put("BAKST", new String[] { "Asia/Baku" });
/*  288 */     tempMap.put("BAKT", new String[] { "Asia/Baku" });
/*  289 */     tempMap.put("BDT", new String[] { "Asia/Dacca", "America/Nome", "America/Adak" });
/*      */ 
/*  291 */     tempMap.put("BEAT", new String[] { "Africa/Nairobi", "Africa/Mogadishu", "Africa/Kampala" });
/*      */ 
/*  293 */     tempMap.put("BEAUT", new String[] { "Africa/Nairobi", "Africa/Dar_es_Salaam", "Africa/Kampala" });
/*      */ 
/*  295 */     tempMap.put("BMT", new String[] { "Europe/Brussels", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Bucharest", "Europe/Zurich", "Asia/Baghdad", "Asia/Bangkok", "Africa/Banjul", "America/Barbados", "America/Bogota" });
/*      */ 
/*  299 */     tempMap.put("BNT", new String[] { "Asia/Brunei" });
/*  300 */     tempMap.put("BORT", new String[] { "Asia/Ujung_Pandang", "Asia/Kuching" });
/*      */ 
/*  302 */     tempMap.put("BOST", new String[] { "America/La_Paz" });
/*  303 */     tempMap.put("BOT", new String[] { "America/La_Paz" });
/*  304 */     tempMap.put("BRST", new String[] { "America/Belem", "America/Fortaleza", "America/Araguaina", "America/Maceio", "America/Sao_Paulo" });
/*      */ 
/*  307 */     tempMap.put("BRT", new String[] { "America/Belem", "America/Fortaleza", "America/Araguaina", "America/Maceio", "America/Sao_Paulo" });
/*      */ 
/*  309 */     tempMap.put("BST", new String[] { "Europe/London", "Europe/Belfast", "Europe/Dublin", "Europe/Gibraltar", "Pacific/Pago_Pago", "Pacific/Midway", "America/Nome", "America/Adak" });
/*      */ 
/*  312 */     tempMap.put("BTT", new String[] { "Asia/Thimbu" });
/*  313 */     tempMap.put("BURT", new String[] { "Asia/Dacca", "Asia/Rangoon", "Asia/Calcutta" });
/*      */ 
/*  315 */     tempMap.put("BWT", new String[] { "America/Nome", "America/Adak" });
/*  316 */     tempMap.put("CANT", new String[] { "Atlantic/Canary" });
/*  317 */     tempMap.put("CAST", new String[] { "Africa/Gaborone", "Africa/Khartoum" });
/*      */ 
/*  319 */     tempMap.put("CAT", new String[] { "Africa/Gaborone", "Africa/Bujumbura", "Africa/Lubumbashi", "Africa/Blantyre", "Africa/Maputo", "Africa/Windhoek", "Africa/Kigali", "Africa/Khartoum", "Africa/Lusaka", "Africa/Harare", "America/Anchorage" });
/*      */ 
/*  324 */     tempMap.put("CCT", new String[] { "Indian/Cocos" });
/*  325 */     tempMap.put("CDDT", new String[] { "America/Rankin_Inlet" });
/*  326 */     tempMap.put("CDT", new String[] { "Asia/Harbin", "Asia/Shanghai", "Asia/Chungking", "Asia/Urumqi", "Asia/Kashgar", "Asia/Taipei", "Asia/Macao", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Menominee", "America/Rainy_River", "America/Winnipeg", "America/Pangnirtung", "America/Iqaluit", "America/Rankin_Inlet", "America/Cambridge_Bay", "America/Cancun", "America/Mexico_City", "America/Chihuahua", "America/Belize", "America/Costa_Rica", "America/Havana", "America/El_Salvador", "America/Guatemala", "America/Tegucigalpa", "America/Managua" });
/*      */ 
/*  338 */     tempMap.put("CEST", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  352 */     tempMap.put("CET", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Casablanca", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  366 */     tempMap.put("CGST", new String[] { "America/Scoresbysund" });
/*  367 */     tempMap.put("CGT", new String[] { "America/Scoresbysund" });
/*  368 */     tempMap.put("CHDT", new String[] { "America/Belize" });
/*  369 */     tempMap.put("CHUT", new String[] { "Asia/Chungking" });
/*  370 */     tempMap.put("CJT", new String[] { "Asia/Tokyo" });
/*  371 */     tempMap.put("CKHST", new String[] { "Pacific/Rarotonga" });
/*  372 */     tempMap.put("CKT", new String[] { "Pacific/Rarotonga" });
/*  373 */     tempMap.put("CLST", new String[] { "Antarctica/Palmer", "America/Santiago" });
/*      */ 
/*  375 */     tempMap.put("CLT", new String[] { "Antarctica/Palmer", "America/Santiago" });
/*      */ 
/*  377 */     tempMap.put("CMT", new String[] { "Europe/Copenhagen", "Europe/Chisinau", "Europe/Tiraspol", "America/St_Lucia", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza", "America/Caracas" });
/*      */ 
/*  382 */     tempMap.put("COST", new String[] { "America/Bogota" });
/*  383 */     tempMap.put("COT", new String[] { "America/Bogota" });
/*  384 */     tempMap.put("CST", new String[] { "Asia/Harbin", "Asia/Shanghai", "Asia/Chungking", "Asia/Urumqi", "Asia/Kashgar", "Asia/Taipei", "Asia/Macao", "Asia/Jayapura", "Australia/Darwin", "Australia/Adelaide", "Australia/Broken_Hill", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Menominee", "America/Rainy_River", "America/Winnipeg", "America/Regina", "America/Swift_Current", "America/Pangnirtung", "America/Iqaluit", "America/Rankin_Inlet", "America/Cambridge_Bay", "America/Cancun", "America/Mexico_City", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan", "America/Belize", "America/Costa_Rica", "America/Havana", "America/El_Salvador", "America/Guatemala", "America/Tegucigalpa", "America/Managua" });
/*      */ 
/*  404 */     tempMap.put("CUT", new String[] { "Europe/Zaporozhye" });
/*  405 */     tempMap.put("CVST", new String[] { "Atlantic/Cape_Verde" });
/*  406 */     tempMap.put("CVT", new String[] { "Atlantic/Cape_Verde" });
/*  407 */     tempMap.put("CWT", new String[] { "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Menominee" });
/*      */ 
/*  411 */     tempMap.put("CXT", new String[] { "Indian/Christmas" });
/*  412 */     tempMap.put("DACT", new String[] { "Asia/Dacca" });
/*  413 */     tempMap.put("DAVT", new String[] { "Antarctica/Davis" });
/*  414 */     tempMap.put("DDUT", new String[] { "Antarctica/DumontDUrville" });
/*  415 */     tempMap.put("DFT", new String[] { "Europe/Oslo", "Europe/Paris" });
/*  416 */     tempMap.put("DMT", new String[] { "Europe/Belfast", "Europe/Dublin" });
/*  417 */     tempMap.put("DUSST", new String[] { "Asia/Dushanbe" });
/*  418 */     tempMap.put("DUST", new String[] { "Asia/Dushanbe" });
/*  419 */     tempMap.put("EASST", new String[] { "Pacific/Easter" });
/*  420 */     tempMap.put("EAST", new String[] { "Indian/Antananarivo", "Pacific/Easter" });
/*      */ 
/*  422 */     tempMap.put("EAT", new String[] { "Indian/Comoro", "Indian/Antananarivo", "Indian/Mayotte", "Africa/Djibouti", "Africa/Asmera", "Africa/Addis_Ababa", "Africa/Nairobi", "Africa/Mogadishu", "Africa/Khartoum", "Africa/Dar_es_Salaam", "Africa/Kampala" });
/*      */ 
/*  427 */     tempMap.put("ECT", new String[] { "Pacific/Galapagos", "America/Guayaquil" });
/*      */ 
/*  429 */     tempMap.put("EDDT", new String[] { "America/Iqaluit" });
/*  430 */     tempMap.put("EDT", new String[] { "America/New_York", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Montreal", "America/Thunder_Bay", "America/Nipigon", "America/Pangnirtung", "America/Iqaluit", "America/Cancun", "America/Nassau", "America/Santo_Domingo", "America/Port-au-Prince", "America/Jamaica", "America/Grand_Turk" });
/*      */ 
/*  438 */     tempMap.put("EEMT", new String[] { "Europe/Minsk", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Kaliningrad", "Europe/Moscow" });
/*      */ 
/*  440 */     tempMap.put("EEST", new String[] { "Europe/Minsk", "Europe/Sofia", "Europe/Tallinn", "Europe/Helsinki", "Europe/Athens", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Warsaw", "Europe/Bucharest", "Europe/Kaliningrad", "Europe/Moscow", "Europe/Istanbul", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Asia/Nicosia", "Asia/Amman", "Asia/Beirut", "Asia/Gaza", "Asia/Damascus", "Africa/Cairo" });
/*      */ 
/*  448 */     tempMap.put("EET", new String[] { "Europe/Minsk", "Europe/Sofia", "Europe/Tallinn", "Europe/Helsinki", "Europe/Athens", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Warsaw", "Europe/Bucharest", "Europe/Kaliningrad", "Europe/Moscow", "Europe/Istanbul", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Asia/Nicosia", "Asia/Amman", "Asia/Beirut", "Asia/Gaza", "Asia/Damascus", "Africa/Cairo", "Africa/Tripoli" });
/*      */ 
/*  457 */     tempMap.put("EGST", new String[] { "America/Scoresbysund" });
/*  458 */     tempMap.put("EGT", new String[] { "Atlantic/Jan_Mayen", "America/Scoresbysund" });
/*      */ 
/*  460 */     tempMap.put("EHDT", new String[] { "America/Santo_Domingo" });
/*  461 */     tempMap.put("EST", new String[] { "Australia/Brisbane", "Australia/Lindeman", "Australia/Hobart", "Australia/Melbourne", "Australia/Sydney", "Australia/Broken_Hill", "Australia/Lord_Howe", "America/New_York", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Menominee", "America/Montreal", "America/Thunder_Bay", "America/Nipigon", "America/Pangnirtung", "America/Iqaluit", "America/Cancun", "America/Antigua", "America/Nassau", "America/Cayman", "America/Santo_Domingo", "America/Port-au-Prince", "America/Jamaica", "America/Managua", "America/Panama", "America/Grand_Turk" });
/*      */ 
/*  475 */     tempMap.put("EWT", new String[] { "America/New_York", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Jamaica" });
/*      */ 
/*  479 */     tempMap.put("FFMT", new String[] { "America/Martinique" });
/*  480 */     tempMap.put("FJST", new String[] { "Pacific/Fiji" });
/*  481 */     tempMap.put("FJT", new String[] { "Pacific/Fiji" });
/*  482 */     tempMap.put("FKST", new String[] { "Atlantic/Stanley" });
/*  483 */     tempMap.put("FKT", new String[] { "Atlantic/Stanley" });
/*  484 */     tempMap.put("FMT", new String[] { "Atlantic/Madeira", "Africa/Freetown" });
/*      */ 
/*  486 */     tempMap.put("FNST", new String[] { "America/Noronha" });
/*  487 */     tempMap.put("FNT", new String[] { "America/Noronha" });
/*  488 */     tempMap.put("FRUST", new String[] { "Asia/Bishkek" });
/*  489 */     tempMap.put("FRUT", new String[] { "Asia/Bishkek" });
/*  490 */     tempMap.put("GALT", new String[] { "Pacific/Galapagos" });
/*  491 */     tempMap.put("GAMT", new String[] { "Pacific/Gambier" });
/*  492 */     tempMap.put("GBGT", new String[] { "America/Guyana" });
/*  493 */     tempMap.put("GEST", new String[] { "Asia/Tbilisi" });
/*  494 */     tempMap.put("GET", new String[] { "Asia/Tbilisi" });
/*  495 */     tempMap.put("GFT", new String[] { "America/Cayenne" });
/*  496 */     tempMap.put("GHST", new String[] { "Africa/Accra" });
/*  497 */     tempMap.put("GILT", new String[] { "Pacific/Tarawa" });
/*  498 */     tempMap.put("GMT", new String[] { "Atlantic/St_Helena", "Atlantic/Reykjavik", "Europe/London", "Europe/Belfast", "Europe/Dublin", "Europe/Gibraltar", "Africa/Porto-Novo", "Africa/Ouagadougou", "Africa/Abidjan", "Africa/Malabo", "Africa/Banjul", "Africa/Accra", "Africa/Conakry", "Africa/Bissau", "Africa/Monrovia", "Africa/Bamako", "Africa/Timbuktu", "Africa/Nouakchott", "Africa/Niamey", "Africa/Sao_Tome", "Africa/Dakar", "Africa/Freetown", "Africa/Lome" });
/*      */ 
/*  507 */     tempMap.put("GST", new String[] { "Atlantic/South_Georgia", "Asia/Bahrain", "Asia/Muscat", "Asia/Qatar", "Asia/Dubai", "Pacific/Guam" });
/*      */ 
/*  510 */     tempMap.put("GYT", new String[] { "America/Guyana" });
/*  511 */     tempMap.put("HADT", new String[] { "America/Adak" });
/*  512 */     tempMap.put("HART", new String[] { "Asia/Harbin" });
/*  513 */     tempMap.put("HAST", new String[] { "America/Adak" });
/*  514 */     tempMap.put("HAWT", new String[] { "America/Adak" });
/*  515 */     tempMap.put("HDT", new String[] { "Pacific/Honolulu" });
/*  516 */     tempMap.put("HKST", new String[] { "Asia/Hong_Kong" });
/*  517 */     tempMap.put("HKT", new String[] { "Asia/Hong_Kong" });
/*  518 */     tempMap.put("HMT", new String[] { "Atlantic/Azores", "Europe/Helsinki", "Asia/Dacca", "Asia/Calcutta", "America/Havana" });
/*      */ 
/*  520 */     tempMap.put("HOVST", new String[] { "Asia/Hovd" });
/*  521 */     tempMap.put("HOVT", new String[] { "Asia/Hovd" });
/*  522 */     tempMap.put("HST", new String[] { "Pacific/Johnston", "Pacific/Honolulu" });
/*      */ 
/*  524 */     tempMap.put("HWT", new String[] { "Pacific/Honolulu" });
/*  525 */     tempMap.put("ICT", new String[] { "Asia/Phnom_Penh", "Asia/Vientiane", "Asia/Bangkok", "Asia/Saigon" });
/*      */ 
/*  527 */     tempMap.put("IDDT", new String[] { "Asia/Jerusalem", "Asia/Gaza" });
/*  528 */     tempMap.put("IDT", new String[] { "Asia/Jerusalem", "Asia/Gaza" });
/*  529 */     tempMap.put("IHST", new String[] { "Asia/Colombo" });
/*  530 */     tempMap.put("IMT", new String[] { "Europe/Sofia", "Europe/Istanbul", "Asia/Irkutsk" });
/*      */ 
/*  532 */     tempMap.put("IOT", new String[] { "Indian/Chagos" });
/*  533 */     tempMap.put("IRKMT", new String[] { "Asia/Irkutsk" });
/*  534 */     tempMap.put("IRKST", new String[] { "Asia/Irkutsk" });
/*  535 */     tempMap.put("IRKT", new String[] { "Asia/Irkutsk" });
/*  536 */     tempMap.put("IRST", new String[] { "Asia/Tehran" });
/*  537 */     tempMap.put("IRT", new String[] { "Asia/Tehran" });
/*  538 */     tempMap.put("ISST", new String[] { "Atlantic/Reykjavik" });
/*  539 */     tempMap.put("IST", new String[] { "Atlantic/Reykjavik", "Europe/Belfast", "Europe/Dublin", "Asia/Dacca", "Asia/Thimbu", "Asia/Calcutta", "Asia/Jerusalem", "Asia/Katmandu", "Asia/Karachi", "Asia/Gaza", "Asia/Colombo" });
/*      */ 
/*  543 */     tempMap.put("JAYT", new String[] { "Asia/Jayapura" });
/*  544 */     tempMap.put("JMT", new String[] { "Atlantic/St_Helena", "Asia/Jerusalem" });
/*      */ 
/*  546 */     tempMap.put("JST", new String[] { "Asia/Rangoon", "Asia/Dili", "Asia/Ujung_Pandang", "Asia/Tokyo", "Asia/Kuala_Lumpur", "Asia/Kuching", "Asia/Manila", "Asia/Singapore", "Pacific/Nauru" });
/*      */ 
/*  550 */     tempMap.put("KART", new String[] { "Asia/Karachi" });
/*  551 */     tempMap.put("KAST", new String[] { "Asia/Kashgar" });
/*  552 */     tempMap.put("KDT", new String[] { "Asia/Seoul" });
/*  553 */     tempMap.put("KGST", new String[] { "Asia/Bishkek" });
/*  554 */     tempMap.put("KGT", new String[] { "Asia/Bishkek" });
/*  555 */     tempMap.put("KMT", new String[] { "Europe/Vilnius", "Europe/Kiev", "America/Cayman", "America/Jamaica", "America/St_Vincent", "America/Grand_Turk" });
/*      */ 
/*  558 */     tempMap.put("KOST", new String[] { "Pacific/Kosrae" });
/*  559 */     tempMap.put("KRAMT", new String[] { "Asia/Krasnoyarsk" });
/*  560 */     tempMap.put("KRAST", new String[] { "Asia/Krasnoyarsk" });
/*  561 */     tempMap.put("KRAT", new String[] { "Asia/Krasnoyarsk" });
/*  562 */     tempMap.put("KST", new String[] { "Asia/Seoul", "Asia/Pyongyang" });
/*  563 */     tempMap.put("KUYMT", new String[] { "Europe/Samara" });
/*  564 */     tempMap.put("KUYST", new String[] { "Europe/Samara" });
/*  565 */     tempMap.put("KUYT", new String[] { "Europe/Samara" });
/*  566 */     tempMap.put("KWAT", new String[] { "Pacific/Kwajalein" });
/*  567 */     tempMap.put("LHST", new String[] { "Australia/Lord_Howe" });
/*  568 */     tempMap.put("LINT", new String[] { "Pacific/Kiritimati" });
/*  569 */     tempMap.put("LKT", new String[] { "Asia/Colombo" });
/*  570 */     tempMap.put("LPMT", new String[] { "America/La_Paz" });
/*  571 */     tempMap.put("LRT", new String[] { "Africa/Monrovia" });
/*  572 */     tempMap.put("LST", new String[] { "Europe/Riga" });
/*  573 */     tempMap.put("M", new String[] { "Europe/Moscow" });
/*  574 */     tempMap.put("MADST", new String[] { "Atlantic/Madeira" });
/*  575 */     tempMap.put("MAGMT", new String[] { "Asia/Magadan" });
/*  576 */     tempMap.put("MAGST", new String[] { "Asia/Magadan" });
/*  577 */     tempMap.put("MAGT", new String[] { "Asia/Magadan" });
/*  578 */     tempMap.put("MALT", new String[] { "Asia/Kuala_Lumpur", "Asia/Singapore" });
/*      */ 
/*  580 */     tempMap.put("MART", new String[] { "Pacific/Marquesas" });
/*  581 */     tempMap.put("MAWT", new String[] { "Antarctica/Mawson" });
/*  582 */     tempMap.put("MDDT", new String[] { "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik" });
/*      */ 
/*  584 */     tempMap.put("MDST", new String[] { "Europe/Moscow" });
/*  585 */     tempMap.put("MDT", new String[] { "America/Denver", "America/Phoenix", "America/Boise", "America/Regina", "America/Swift_Current", "America/Edmonton", "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan" });
/*      */ 
/*  590 */     tempMap.put("MET", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Casablanca", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  604 */     tempMap.put("MHT", new String[] { "Pacific/Majuro", "Pacific/Kwajalein" });
/*      */ 
/*  606 */     tempMap.put("MMT", new String[] { "Indian/Maldives", "Europe/Minsk", "Europe/Moscow", "Asia/Rangoon", "Asia/Ujung_Pandang", "Asia/Colombo", "Pacific/Easter", "Africa/Monrovia", "America/Managua", "America/Montevideo" });
/*      */ 
/*  610 */     tempMap.put("MOST", new String[] { "Asia/Macao" });
/*  611 */     tempMap.put("MOT", new String[] { "Asia/Macao" });
/*  612 */     tempMap.put("MPT", new String[] { "Pacific/Saipan" });
/*  613 */     tempMap.put("MSK", new String[] { "Europe/Minsk", "Europe/Tallinn", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol" });
/*      */ 
/*  617 */     tempMap.put("MST", new String[] { "Europe/Moscow", "America/Denver", "America/Phoenix", "America/Boise", "America/Regina", "America/Swift_Current", "America/Edmonton", "America/Dawson_Creek", "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik", "America/Mexico_City", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan", "America/Tijuana" });
/*      */ 
/*  624 */     tempMap.put("MUT", new String[] { "Indian/Mauritius" });
/*  625 */     tempMap.put("MVT", new String[] { "Indian/Maldives" });
/*  626 */     tempMap.put("MWT", new String[] { "America/Denver", "America/Phoenix", "America/Boise" });
/*      */ 
/*  628 */     tempMap.put("MYT", new String[] { "Asia/Kuala_Lumpur", "Asia/Kuching" });
/*      */ 
/*  631 */     tempMap.put("NCST", new String[] { "Pacific/Noumea" });
/*  632 */     tempMap.put("NCT", new String[] { "Pacific/Noumea" });
/*  633 */     tempMap.put("NDT", new String[] { "America/Nome", "America/Adak", "America/St_Johns", "America/Goose_Bay" });
/*      */ 
/*  635 */     tempMap.put("NEGT", new String[] { "America/Paramaribo" });
/*  636 */     tempMap.put("NFT", new String[] { "Europe/Paris", "Europe/Oslo", "Pacific/Norfolk" });
/*      */ 
/*  638 */     tempMap.put("NMT", new String[] { "Pacific/Norfolk" });
/*  639 */     tempMap.put("NOVMT", new String[] { "Asia/Novosibirsk" });
/*  640 */     tempMap.put("NOVST", new String[] { "Asia/Novosibirsk" });
/*  641 */     tempMap.put("NOVT", new String[] { "Asia/Novosibirsk" });
/*  642 */     tempMap.put("NPT", new String[] { "Asia/Katmandu" });
/*  643 */     tempMap.put("NRT", new String[] { "Pacific/Nauru" });
/*  644 */     tempMap.put("NST", new String[] { "Europe/Amsterdam", "Pacific/Pago_Pago", "Pacific/Midway", "America/Nome", "America/Adak", "America/St_Johns", "America/Goose_Bay" });
/*      */ 
/*  647 */     tempMap.put("NUT", new String[] { "Pacific/Niue" });
/*  648 */     tempMap.put("NWT", new String[] { "America/Nome", "America/Adak" });
/*  649 */     tempMap.put("NZDT", new String[] { "Antarctica/McMurdo" });
/*  650 */     tempMap.put("NZHDT", new String[] { "Pacific/Auckland" });
/*  651 */     tempMap.put("NZST", new String[] { "Antarctica/McMurdo", "Pacific/Auckland" });
/*      */ 
/*  653 */     tempMap.put("OMSMT", new String[] { "Asia/Omsk" });
/*  654 */     tempMap.put("OMSST", new String[] { "Asia/Omsk" });
/*  655 */     tempMap.put("OMST", new String[] { "Asia/Omsk" });
/*  656 */     tempMap.put("PDDT", new String[] { "America/Inuvik", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  658 */     tempMap.put("PDT", new String[] { "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Vancouver", "America/Dawson_Creek", "America/Inuvik", "America/Whitehorse", "America/Dawson", "America/Tijuana" });
/*      */ 
/*  662 */     tempMap.put("PEST", new String[] { "America/Lima" });
/*  663 */     tempMap.put("PET", new String[] { "America/Lima" });
/*  664 */     tempMap.put("PETMT", new String[] { "Asia/Kamchatka" });
/*  665 */     tempMap.put("PETST", new String[] { "Asia/Kamchatka" });
/*  666 */     tempMap.put("PETT", new String[] { "Asia/Kamchatka" });
/*  667 */     tempMap.put("PGT", new String[] { "Pacific/Port_Moresby" });
/*  668 */     tempMap.put("PHOT", new String[] { "Pacific/Enderbury" });
/*  669 */     tempMap.put("PHST", new String[] { "Asia/Manila" });
/*  670 */     tempMap.put("PHT", new String[] { "Asia/Manila" });
/*  671 */     tempMap.put("PKT", new String[] { "Asia/Karachi" });
/*  672 */     tempMap.put("PMDT", new String[] { "America/Miquelon" });
/*  673 */     tempMap.put("PMMT", new String[] { "Pacific/Port_Moresby" });
/*  674 */     tempMap.put("PMST", new String[] { "America/Miquelon" });
/*  675 */     tempMap.put("PMT", new String[] { "Antarctica/DumontDUrville", "Europe/Prague", "Europe/Paris", "Europe/Monaco", "Africa/Algiers", "Africa/Tunis", "America/Panama", "America/Paramaribo" });
/*      */ 
/*  679 */     tempMap.put("PNT", new String[] { "Pacific/Pitcairn" });
/*  680 */     tempMap.put("PONT", new String[] { "Pacific/Ponape" });
/*  681 */     tempMap.put("PPMT", new String[] { "America/Port-au-Prince" });
/*  682 */     tempMap.put("PST", new String[] { "Pacific/Pitcairn", "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Vancouver", "America/Dawson_Creek", "America/Inuvik", "America/Whitehorse", "America/Dawson", "America/Hermosillo", "America/Mazatlan", "America/Tijuana" });
/*      */ 
/*  687 */     tempMap.put("PWT", new String[] { "Pacific/Palau", "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Tijuana" });
/*      */ 
/*  690 */     tempMap.put("PYST", new String[] { "America/Asuncion" });
/*  691 */     tempMap.put("PYT", new String[] { "America/Asuncion" });
/*  692 */     tempMap.put("QMT", new String[] { "America/Guayaquil" });
/*  693 */     tempMap.put("RET", new String[] { "Indian/Reunion" });
/*  694 */     tempMap.put("RMT", new String[] { "Atlantic/Reykjavik", "Europe/Rome", "Europe/Riga", "Asia/Rangoon" });
/*      */ 
/*  696 */     tempMap.put("S", new String[] { "Europe/Moscow" });
/*  697 */     tempMap.put("SAMMT", new String[] { "Europe/Samara" });
/*  698 */     tempMap.put("SAMST", new String[] { "Europe/Samara", "Asia/Samarkand" });
/*      */ 
/*  701 */     tempMap.put("SAMT", new String[] { "Europe/Samara", "Asia/Samarkand", "Pacific/Pago_Pago", "Pacific/Apia" });
/*      */ 
/*  703 */     tempMap.put("SAST", new String[] { "Africa/Maseru", "Africa/Windhoek", "Africa/Johannesburg", "Africa/Mbabane" });
/*      */ 
/*  705 */     tempMap.put("SBT", new String[] { "Pacific/Guadalcanal" });
/*  706 */     tempMap.put("SCT", new String[] { "Indian/Mahe" });
/*  707 */     tempMap.put("SDMT", new String[] { "America/Santo_Domingo" });
/*  708 */     tempMap.put("SGT", new String[] { "Asia/Singapore" });
/*  709 */     tempMap.put("SHEST", new String[] { "Asia/Aqtau" });
/*  710 */     tempMap.put("SHET", new String[] { "Asia/Aqtau" });
/*  711 */     tempMap.put("SJMT", new String[] { "America/Costa_Rica" });
/*  712 */     tempMap.put("SLST", new String[] { "Africa/Freetown" });
/*  713 */     tempMap.put("SMT", new String[] { "Atlantic/Stanley", "Europe/Stockholm", "Europe/Simferopol", "Asia/Phnom_Penh", "Asia/Vientiane", "Asia/Kuala_Lumpur", "Asia/Singapore", "Asia/Saigon", "America/Santiago" });
/*      */ 
/*  717 */     tempMap.put("SRT", new String[] { "America/Paramaribo" });
/*  718 */     tempMap.put("SST", new String[] { "Pacific/Pago_Pago", "Pacific/Midway" });
/*      */ 
/*  720 */     tempMap.put("SVEMT", new String[] { "Asia/Yekaterinburg" });
/*  721 */     tempMap.put("SVEST", new String[] { "Asia/Yekaterinburg" });
/*  722 */     tempMap.put("SVET", new String[] { "Asia/Yekaterinburg" });
/*  723 */     tempMap.put("SWAT", new String[] { "Africa/Windhoek" });
/*  724 */     tempMap.put("SYOT", new String[] { "Antarctica/Syowa" });
/*  725 */     tempMap.put("TAHT", new String[] { "Pacific/Tahiti" });
/*  726 */     tempMap.put("TASST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*      */ 
/*  729 */     tempMap.put("TAST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  730 */     tempMap.put("TBIST", new String[] { "Asia/Tbilisi" });
/*  731 */     tempMap.put("TBIT", new String[] { "Asia/Tbilisi" });
/*  732 */     tempMap.put("TBMT", new String[] { "Asia/Tbilisi" });
/*  733 */     tempMap.put("TFT", new String[] { "Indian/Kerguelen" });
/*  734 */     tempMap.put("TJT", new String[] { "Asia/Dushanbe" });
/*  735 */     tempMap.put("TKT", new String[] { "Pacific/Fakaofo" });
/*  736 */     tempMap.put("TMST", new String[] { "Asia/Ashkhabad" });
/*  737 */     tempMap.put("TMT", new String[] { "Europe/Tallinn", "Asia/Tehran", "Asia/Ashkhabad" });
/*      */ 
/*  739 */     tempMap.put("TOST", new String[] { "Pacific/Tongatapu" });
/*  740 */     tempMap.put("TOT", new String[] { "Pacific/Tongatapu" });
/*  741 */     tempMap.put("TPT", new String[] { "Asia/Dili" });
/*  742 */     tempMap.put("TRST", new String[] { "Europe/Istanbul" });
/*  743 */     tempMap.put("TRT", new String[] { "Europe/Istanbul" });
/*  744 */     tempMap.put("TRUT", new String[] { "Pacific/Truk" });
/*  745 */     tempMap.put("TVT", new String[] { "Pacific/Funafuti" });
/*  746 */     tempMap.put("ULAST", new String[] { "Asia/Ulaanbaatar" });
/*  747 */     tempMap.put("ULAT", new String[] { "Asia/Ulaanbaatar" });
/*  748 */     tempMap.put("URUT", new String[] { "Asia/Urumqi" });
/*  749 */     tempMap.put("UYHST", new String[] { "America/Montevideo" });
/*  750 */     tempMap.put("UYT", new String[] { "America/Montevideo" });
/*  751 */     tempMap.put("UZST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  752 */     tempMap.put("UZT", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  753 */     tempMap.put("VET", new String[] { "America/Caracas" });
/*  754 */     tempMap.put("VLAMT", new String[] { "Asia/Vladivostok" });
/*  755 */     tempMap.put("VLAST", new String[] { "Asia/Vladivostok" });
/*  756 */     tempMap.put("VLAT", new String[] { "Asia/Vladivostok" });
/*  757 */     tempMap.put("VUST", new String[] { "Pacific/Efate" });
/*  758 */     tempMap.put("VUT", new String[] { "Pacific/Efate" });
/*  759 */     tempMap.put("WAKT", new String[] { "Pacific/Wake" });
/*  760 */     tempMap.put("WARST", new String[] { "America/Jujuy", "America/Mendoza" });
/*      */ 
/*  762 */     tempMap.put("WART", new String[] { "America/Jujuy", "America/Mendoza" });
/*      */ 
/*  765 */     tempMap.put("WAST", new String[] { "Africa/Ndjamena", "Africa/Windhoek" });
/*      */ 
/*  767 */     tempMap.put("WAT", new String[] { "Africa/Luanda", "Africa/Porto-Novo", "Africa/Douala", "Africa/Bangui", "Africa/Ndjamena", "Africa/Kinshasa", "Africa/Brazzaville", "Africa/Malabo", "Africa/Libreville", "Africa/Banjul", "Africa/Conakry", "Africa/Bissau", "Africa/Bamako", "Africa/Nouakchott", "Africa/El_Aaiun", "Africa/Windhoek", "Africa/Niamey", "Africa/Lagos", "Africa/Dakar", "Africa/Freetown" });
/*      */ 
/*  774 */     tempMap.put("WEST", new String[] { "Atlantic/Faeroe", "Atlantic/Azores", "Atlantic/Madeira", "Atlantic/Canary", "Europe/Brussels", "Europe/Luxembourg", "Europe/Monaco", "Europe/Lisbon", "Europe/Madrid", "Africa/Algiers", "Africa/Casablanca", "Africa/Ceuta" });
/*      */ 
/*  779 */     tempMap.put("WET", new String[] { "Atlantic/Faeroe", "Atlantic/Azores", "Atlantic/Madeira", "Atlantic/Canary", "Europe/Andorra", "Europe/Brussels", "Europe/Luxembourg", "Europe/Monaco", "Europe/Lisbon", "Europe/Madrid", "Africa/Algiers", "Africa/Casablanca", "Africa/El_Aaiun", "Africa/Ceuta" });
/*      */ 
/*  784 */     tempMap.put("WFT", new String[] { "Pacific/Wallis" });
/*  785 */     tempMap.put("WGST", new String[] { "America/Godthab" });
/*  786 */     tempMap.put("WGT", new String[] { "America/Godthab" });
/*  787 */     tempMap.put("WMT", new String[] { "Europe/Vilnius", "Europe/Warsaw" });
/*  788 */     tempMap.put("WST", new String[] { "Antarctica/Casey", "Pacific/Apia", "Australia/Perth" });
/*      */ 
/*  790 */     tempMap.put("YAKMT", new String[] { "Asia/Yakutsk" });
/*  791 */     tempMap.put("YAKST", new String[] { "Asia/Yakutsk" });
/*  792 */     tempMap.put("YAKT", new String[] { "Asia/Yakutsk" });
/*  793 */     tempMap.put("YAPT", new String[] { "Pacific/Yap" });
/*  794 */     tempMap.put("YDDT", new String[] { "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  796 */     tempMap.put("YDT", new String[] { "America/Yakutat", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  798 */     tempMap.put("YEKMT", new String[] { "Asia/Yekaterinburg" });
/*  799 */     tempMap.put("YEKST", new String[] { "Asia/Yekaterinburg" });
/*  800 */     tempMap.put("YEKT", new String[] { "Asia/Yekaterinburg" });
/*  801 */     tempMap.put("YERST", new String[] { "Asia/Yerevan" });
/*  802 */     tempMap.put("YERT", new String[] { "Asia/Yerevan" });
/*  803 */     tempMap.put("YST", new String[] { "America/Yakutat", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  805 */     tempMap.put("YWT", new String[] { "America/Yakutat" });
/*      */ 
/*  807 */     ABBREVIATED_TIMEZONES = Collections.unmodifiableMap(tempMap);
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.TimeUtil
 * JD-Core Version:    0.6.0
 */