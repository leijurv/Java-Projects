/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class CharsetMapping
/*      */ {
/*   51 */   private static final Properties CHARSET_CONFIG = new Properties();
/*      */   public static final String[] INDEX_TO_CHARSET;
/*      */   public static final String[] INDEX_TO_COLLATION;
/*      */   private static final Map JAVA_TO_MYSQL_CHARSET_MAP;
/*      */   private static final Map JAVA_UC_TO_MYSQL_CHARSET_MAP;
/*      */   private static final Map ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP;
/*      */   private static final Map MULTIBYTE_CHARSETS;
/*      */   private static final Map MYSQL_TO_JAVA_CHARSET_MAP;
/*      */   private static final Map MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP;
/*      */   private static final String NOT_USED = "ISO8859_1";
/*      */   public static final Map STATIC_CHARSET_TO_NUM_BYTES_MAP;
/*      */ 
/*      */   public static final String getJavaEncodingForMysqlEncoding(String mysqlEncoding, Connection conn)
/*      */     throws SQLException
/*      */   {
/*  860 */     if ((conn != null) && (conn.versionMeetsMinimum(4, 1, 0)) && ("latin1".equalsIgnoreCase(mysqlEncoding)))
/*      */     {
/*  862 */       return "Cp1252";
/*      */     }
/*      */ 
/*  865 */     return (String)MYSQL_TO_JAVA_CHARSET_MAP.get(mysqlEncoding);
/*      */   }
/*      */ 
/*      */   public static final String getMysqlEncodingForJavaEncoding(String javaEncodingUC, Connection conn) throws SQLException
/*      */   {
/*  870 */     List mysqlEncodings = (List)JAVA_UC_TO_MYSQL_CHARSET_MAP.get(javaEncodingUC);
/*      */ 
/*  874 */     if (mysqlEncodings != null) {
/*  875 */       Iterator iter = mysqlEncodings.iterator();
/*      */ 
/*  877 */       VersionedStringProperty versionedProp = null;
/*      */ 
/*  879 */       while (iter.hasNext()) {
/*  880 */         VersionedStringProperty propToCheck = (VersionedStringProperty)iter.next();
/*      */ 
/*  883 */         if (conn == null)
/*      */         {
/*  886 */           return propToCheck.toString();
/*      */         }
/*      */ 
/*  889 */         if ((versionedProp != null) && (!versionedProp.preferredValue) && 
/*  890 */           (versionedProp.majorVersion == propToCheck.majorVersion) && (versionedProp.minorVersion == propToCheck.minorVersion) && (versionedProp.subminorVersion == propToCheck.subminorVersion))
/*      */         {
/*  893 */           return versionedProp.toString();
/*      */         }
/*      */ 
/*  897 */         if (!propToCheck.isOkayForVersion(conn)) break;
/*  898 */         if (propToCheck.preferredValue) {
/*  899 */           return propToCheck.toString();
/*      */         }
/*      */ 
/*  902 */         versionedProp = propToCheck;
/*      */       }
/*      */ 
/*  908 */       if (versionedProp != null) {
/*  909 */         return versionedProp.toString();
/*      */       }
/*      */     }
/*      */ 
/*  913 */     return null;
/*      */   }
/*      */ 
/*      */   static final int getNumberOfCharsetsConfigured() {
/*  917 */     return MYSQL_TO_JAVA_CHARSET_MAP.size() / 2;
/*      */   }
/*      */ 
/*      */   static final String getCharacterEncodingForErrorMessages(ConnectionImpl conn)
/*      */     throws SQLException
/*      */   {
/*  933 */     String errorMessageFile = conn.getServerVariable("language");
/*      */ 
/*  935 */     if ((errorMessageFile == null) || (errorMessageFile.length() == 0))
/*      */     {
/*  937 */       return "Cp1252";
/*      */     }
/*      */ 
/*  940 */     int endWithoutSlash = errorMessageFile.length();
/*      */ 
/*  942 */     if ((errorMessageFile.endsWith("/")) || (errorMessageFile.endsWith("\\"))) {
/*  943 */       endWithoutSlash--;
/*      */     }
/*      */ 
/*  946 */     int lastSlashIndex = errorMessageFile.lastIndexOf('/', endWithoutSlash - 1);
/*      */ 
/*  948 */     if (lastSlashIndex == -1) {
/*  949 */       lastSlashIndex = errorMessageFile.lastIndexOf('\\', endWithoutSlash - 1);
/*      */     }
/*      */ 
/*  952 */     if (lastSlashIndex == -1) {
/*  953 */       lastSlashIndex = 0;
/*      */     }
/*      */ 
/*  956 */     if ((lastSlashIndex == endWithoutSlash) || (endWithoutSlash < lastSlashIndex))
/*      */     {
/*  958 */       return "Cp1252";
/*      */     }
/*      */ 
/*  961 */     errorMessageFile = errorMessageFile.substring(lastSlashIndex + 1, endWithoutSlash);
/*      */ 
/*  963 */     String errorMessageEncodingMysql = (String)ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP.get(errorMessageFile);
/*      */ 
/*  965 */     if (errorMessageEncodingMysql == null)
/*      */     {
/*  967 */       return "Cp1252";
/*      */     }
/*      */ 
/*  970 */     String javaEncoding = getJavaEncodingForMysqlEncoding(errorMessageEncodingMysql, conn);
/*      */ 
/*  972 */     if (javaEncoding == null)
/*      */     {
/*  974 */       return "Cp1252";
/*      */     }
/*      */ 
/*  977 */     return javaEncoding;
/*      */   }
/*      */ 
/*      */   static final boolean isAliasForSjis(String encoding) {
/*  981 */     return ("SJIS".equalsIgnoreCase(encoding)) || ("WINDOWS-31J".equalsIgnoreCase(encoding)) || ("MS932".equalsIgnoreCase(encoding)) || ("SHIFT_JIS".equalsIgnoreCase(encoding)) || ("CP943".equalsIgnoreCase(encoding));
/*      */   }
/*      */ 
/*      */   static final boolean isMultibyteCharset(String javaEncodingName)
/*      */   {
/*  990 */     String javaEncodingNameUC = javaEncodingName.toUpperCase(Locale.ENGLISH);
/*      */ 
/*  993 */     return MULTIBYTE_CHARSETS.containsKey(javaEncodingNameUC);
/*      */   }
/*      */ 
/*      */   private static void populateMapWithKeyValuePairs(String configKey, Map mapToPopulate, boolean addVersionedProperties, boolean addUppercaseKeys)
/*      */   {
/*  999 */     String javaToMysqlConfig = CHARSET_CONFIG.getProperty(configKey);
/*      */ 
/* 1001 */     if (javaToMysqlConfig != null) {
/* 1002 */       List mappings = StringUtils.split(javaToMysqlConfig, ",", true);
/*      */ 
/* 1004 */       if (mappings != null) {
/* 1005 */         Iterator mappingsIter = mappings.iterator();
/*      */ 
/* 1007 */         while (mappingsIter.hasNext()) {
/* 1008 */           String aMapping = (String)mappingsIter.next();
/*      */ 
/* 1010 */           List parsedPair = StringUtils.split(aMapping, "=", true);
/*      */ 
/* 1012 */           if (parsedPair.size() == 2) {
/* 1013 */             String key = parsedPair.get(0).toString();
/* 1014 */             String value = parsedPair.get(1).toString();
/*      */ 
/* 1016 */             if (addVersionedProperties) {
/* 1017 */               List versionedProperties = (List)mapToPopulate.get(key);
/*      */ 
/* 1020 */               if (versionedProperties == null) {
/* 1021 */                 versionedProperties = new ArrayList();
/* 1022 */                 mapToPopulate.put(key, versionedProperties);
/*      */               }
/*      */ 
/* 1025 */               VersionedStringProperty verProp = new VersionedStringProperty(value);
/*      */ 
/* 1027 */               versionedProperties.add(verProp);
/*      */ 
/* 1029 */               if (addUppercaseKeys) {
/* 1030 */                 String keyUc = key.toUpperCase(Locale.ENGLISH);
/*      */ 
/* 1032 */                 versionedProperties = (List)mapToPopulate.get(keyUc);
/*      */ 
/* 1035 */                 if (versionedProperties == null) {
/* 1036 */                   versionedProperties = new ArrayList();
/* 1037 */                   mapToPopulate.put(keyUc, versionedProperties);
/*      */                 }
/*      */ 
/* 1041 */                 versionedProperties.add(verProp);
/*      */               }
/*      */             } else {
/* 1044 */               mapToPopulate.put(key, value);
/*      */ 
/* 1046 */               if (addUppercaseKeys)
/* 1047 */                 mapToPopulate.put(key.toUpperCase(Locale.ENGLISH), value);
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1052 */             throw new RuntimeException("Syntax error in Charsets.properties resource for token \"" + aMapping + "\".");
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1059 */         throw new RuntimeException("Missing/corrupt entry for \"" + configKey + "\" in Charsets.properties.");
/*      */       }
/*      */     }
/*      */     else {
/* 1063 */       throw new RuntimeException("Could not find configuration value \"" + configKey + "\" in Charsets.properties resource");
/*      */     }
/*      */   }
/*      */ 
/*      */   public static int getCharsetIndexForMysqlEncodingName(String name)
/*      */   {
/* 1069 */     if (name == null) {
/* 1070 */       return 0;
/*      */     }
/*      */ 
/* 1073 */     Integer asInt = (Integer)MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP.get(name);
/*      */ 
/* 1075 */     if (asInt == null) {
/* 1076 */       return 0;
/*      */     }
/*      */ 
/* 1079 */     return asInt.intValue();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   82 */     HashMap tempNumBytesMap = new HashMap();
/*      */ 
/*   84 */     tempNumBytesMap.put("big5", Constants.integerValueOf(2));
/*   85 */     tempNumBytesMap.put("dec8", Constants.integerValueOf(1));
/*   86 */     tempNumBytesMap.put("cp850", Constants.integerValueOf(1));
/*   87 */     tempNumBytesMap.put("hp8", Constants.integerValueOf(1));
/*   88 */     tempNumBytesMap.put("koi8r", Constants.integerValueOf(1));
/*   89 */     tempNumBytesMap.put("latin1", Constants.integerValueOf(1));
/*   90 */     tempNumBytesMap.put("latin2", Constants.integerValueOf(1));
/*   91 */     tempNumBytesMap.put("swe7", Constants.integerValueOf(1));
/*   92 */     tempNumBytesMap.put("ascii", Constants.integerValueOf(1));
/*   93 */     tempNumBytesMap.put("ujis", Constants.integerValueOf(3));
/*   94 */     tempNumBytesMap.put("sjis", Constants.integerValueOf(2));
/*   95 */     tempNumBytesMap.put("hebrew", Constants.integerValueOf(1));
/*   96 */     tempNumBytesMap.put("tis620", Constants.integerValueOf(1));
/*   97 */     tempNumBytesMap.put("euckr", Constants.integerValueOf(2));
/*   98 */     tempNumBytesMap.put("koi8u", Constants.integerValueOf(1));
/*   99 */     tempNumBytesMap.put("gb2312", Constants.integerValueOf(2));
/*  100 */     tempNumBytesMap.put("greek", Constants.integerValueOf(1));
/*  101 */     tempNumBytesMap.put("cp1250", Constants.integerValueOf(1));
/*  102 */     tempNumBytesMap.put("gbk", Constants.integerValueOf(2));
/*  103 */     tempNumBytesMap.put("latin5", Constants.integerValueOf(1));
/*  104 */     tempNumBytesMap.put("armscii8", Constants.integerValueOf(1));
/*  105 */     tempNumBytesMap.put("utf8", Constants.integerValueOf(3));
/*  106 */     tempNumBytesMap.put("ucs2", Constants.integerValueOf(2));
/*  107 */     tempNumBytesMap.put("cp866", Constants.integerValueOf(1));
/*  108 */     tempNumBytesMap.put("keybcs2", Constants.integerValueOf(1));
/*  109 */     tempNumBytesMap.put("macce", Constants.integerValueOf(1));
/*  110 */     tempNumBytesMap.put("macroman", Constants.integerValueOf(1));
/*  111 */     tempNumBytesMap.put("cp852", Constants.integerValueOf(1));
/*  112 */     tempNumBytesMap.put("latin7", Constants.integerValueOf(1));
/*  113 */     tempNumBytesMap.put("cp1251", Constants.integerValueOf(1));
/*  114 */     tempNumBytesMap.put("cp1256", Constants.integerValueOf(1));
/*  115 */     tempNumBytesMap.put("cp1257", Constants.integerValueOf(1));
/*  116 */     tempNumBytesMap.put("binary", Constants.integerValueOf(1));
/*  117 */     tempNumBytesMap.put("geostd8", Constants.integerValueOf(1));
/*  118 */     tempNumBytesMap.put("cp932", Constants.integerValueOf(2));
/*  119 */     tempNumBytesMap.put("eucjpms", Constants.integerValueOf(3));
/*      */ 
/*  121 */     STATIC_CHARSET_TO_NUM_BYTES_MAP = Collections.unmodifiableMap(tempNumBytesMap);
/*      */ 
/*  124 */     CHARSET_CONFIG.setProperty("javaToMysqlMappings", "US-ASCII =\t\t\tusa7,US-ASCII =\t\t\t>4.1.0 ascii,Big5 = \t\t\t\tbig5,GBK = \t\t\t\tgbk,SJIS = \t\t\t\tsjis,EUC_CN = \t\t\tgb2312,EUC_JP = \t\t\tujis,EUC_JP_Solaris = \t>5.0.3 eucjpms,EUC_KR = \t\t\teuc_kr,EUC_KR = \t\t\t>4.1.0 euckr,ISO8859_1 =\t\t\t*latin1,ISO8859_1 =\t\t\tlatin1_de,ISO8859_1 =\t\t\tgerman1,ISO8859_1 =\t\t\tdanish,ISO8859_2 =\t\t\tlatin2,ISO8859_2 =\t\t\tczech,ISO8859_2 =\t\t\thungarian,ISO8859_2  =\t\tcroat,ISO8859_7  =\t\tgreek,ISO8859_7  =\t\tlatin7,ISO8859_8  = \t\thebrew,ISO8859_9  =\t\tlatin5,ISO8859_13 =\t\tlatvian,ISO8859_13 =\t\tlatvian1,ISO8859_13 =\t\testonia,Cp437 =             *>4.1.0 cp850,Cp437 =\t\t\t\tdos,Cp850 =\t\t\t\tcp850,Cp852 = \t\t\tcp852,Cp866 = \t\t\tcp866,KOI8_R = \t\t\tkoi8_ru,KOI8_R = \t\t\t>4.1.0 koi8r,TIS620 = \t\t\ttis620,Cp1250 = \t\t\tcp1250,Cp1250 = \t\t\twin1250,Cp1251 = \t\t\t*>4.1.0 cp1251,Cp1251 = \t\t\twin1251,Cp1251 = \t\t\tcp1251cias,Cp1251 = \t\t\tcp1251csas,Cp1256 = \t\t\tcp1256,Cp1251 = \t\t\twin1251ukr,Cp1252 =             latin1,Cp1257 = \t\t\tcp1257,MacRoman = \t\t\tmacroman,MacCentralEurope = \tmacce,UTF-8 = \t\tutf8,UnicodeBig = \tucs2,US-ASCII =\t\tbinary,Cp943 =        \tsjis,MS932 =\t\t\tsjis,MS932 =        \t>4.1.11 cp932,WINDOWS-31J =\tsjis,WINDOWS-31J = \t>4.1.11 cp932,CP932 =\t\t\tsjis,CP932 =\t\t\t*>4.1.11 cp932,SHIFT_JIS = \tsjis,ASCII =\t\t\tascii,LATIN5 =\t\tlatin5,LATIN7 =\t\tlatin7,HEBREW =\t\thebrew,GREEK =\t\t\tgreek,EUCKR =\t\t\teuckr,GB2312 =\t\tgb2312,LATIN2 =\t\tlatin2,UTF-16 = \t>5.2.0 utf16,UTF-32 = \t>5.2.0 utf32");
/*      */ 
/*  201 */     HashMap javaToMysqlMap = new HashMap();
/*      */ 
/*  203 */     populateMapWithKeyValuePairs("javaToMysqlMappings", javaToMysqlMap, true, false);
/*      */ 
/*  205 */     JAVA_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(javaToMysqlMap);
/*      */ 
/*  207 */     HashMap mysqlToJavaMap = new HashMap();
/*      */ 
/*  209 */     Set keySet = JAVA_TO_MYSQL_CHARSET_MAP.keySet();
/*      */ 
/*  211 */     Iterator javaCharsets = keySet.iterator();
/*      */ 
/*  213 */     while (javaCharsets.hasNext()) {
/*  214 */       Object javaEncodingName = javaCharsets.next();
/*  215 */       List mysqlEncodingList = (List)JAVA_TO_MYSQL_CHARSET_MAP.get(javaEncodingName);
/*      */ 
/*  218 */       Iterator mysqlEncodings = mysqlEncodingList.iterator();
/*      */ 
/*  220 */       String mysqlEncodingName = null;
/*      */ 
/*  222 */       while (mysqlEncodings.hasNext()) {
/*  223 */         VersionedStringProperty mysqlProp = (VersionedStringProperty)mysqlEncodings.next();
/*      */ 
/*  225 */         mysqlEncodingName = mysqlProp.toString();
/*      */ 
/*  227 */         mysqlToJavaMap.put(mysqlEncodingName, javaEncodingName);
/*  228 */         mysqlToJavaMap.put(mysqlEncodingName.toUpperCase(Locale.ENGLISH), javaEncodingName);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  234 */     mysqlToJavaMap.put("cp932", "Windows-31J");
/*  235 */     mysqlToJavaMap.put("CP932", "Windows-31J");
/*      */ 
/*  237 */     MYSQL_TO_JAVA_CHARSET_MAP = Collections.unmodifiableMap(mysqlToJavaMap);
/*      */ 
/*  239 */     TreeMap ucMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */ 
/*  241 */     Iterator javaNamesKeys = JAVA_TO_MYSQL_CHARSET_MAP.keySet().iterator();
/*      */ 
/*  243 */     while (javaNamesKeys.hasNext()) {
/*  244 */       String key = (String)javaNamesKeys.next();
/*      */ 
/*  246 */       ucMap.put(key.toUpperCase(Locale.ENGLISH), JAVA_TO_MYSQL_CHARSET_MAP.get(key));
/*      */     }
/*      */ 
/*  250 */     JAVA_UC_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(ucMap);
/*      */ 
/*  256 */     HashMap tempMapMulti = new HashMap();
/*      */ 
/*  258 */     CHARSET_CONFIG.setProperty("multibyteCharsets", "Big5 = \t\t\tbig5,GBK = \t\t\tgbk,SJIS = \t\t\tsjis,EUC_CN = \t\tgb2312,EUC_JP = \t\tujis,EUC_JP_Solaris = eucjpms,EUC_KR = \t\teuc_kr,EUC_KR = \t\t>4.1.0 euckr,Cp943 =        \tsjis,Cp943 = \t\tcp943,WINDOWS-31J =\tsjis,WINDOWS-31J = \tcp932,CP932 =\t\t\tcp932,MS932 =\t\t\tsjis,MS932 =        \tcp932,SHIFT_JIS = \tsjis,EUCKR =\t\t\teuckr,GB2312 =\t\tgb2312,UTF-8 = \t\tutf8,utf8 =          utf8,UnicodeBig = \tucs2");
/*      */ 
/*  290 */     populateMapWithKeyValuePairs("multibyteCharsets", tempMapMulti, false, true);
/*      */ 
/*  293 */     MULTIBYTE_CHARSETS = Collections.unmodifiableMap(tempMapMulti);
/*      */ 
/*  295 */     INDEX_TO_CHARSET = new String['ÿ'];
/*      */     try
/*      */     {
/*  298 */       INDEX_TO_CHARSET[1] = getJavaEncodingForMysqlEncoding("big5", null);
/*  299 */       INDEX_TO_CHARSET[2] = getJavaEncodingForMysqlEncoding("czech", null);
/*  300 */       INDEX_TO_CHARSET[3] = "ISO8859_1";
/*  301 */       INDEX_TO_CHARSET[4] = "ISO8859_1";
/*  302 */       INDEX_TO_CHARSET[5] = getJavaEncodingForMysqlEncoding("german1", null);
/*      */ 
/*  304 */       INDEX_TO_CHARSET[6] = "ISO8859_1";
/*  305 */       INDEX_TO_CHARSET[7] = getJavaEncodingForMysqlEncoding("koi8_ru", null);
/*      */ 
/*  307 */       INDEX_TO_CHARSET[8] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */ 
/*  309 */       INDEX_TO_CHARSET[9] = getJavaEncodingForMysqlEncoding("latin2", null);
/*      */ 
/*  311 */       INDEX_TO_CHARSET[10] = "ISO8859_1";
/*  312 */       INDEX_TO_CHARSET[11] = getJavaEncodingForMysqlEncoding("usa7", null);
/*  313 */       INDEX_TO_CHARSET[12] = getJavaEncodingForMysqlEncoding("ujis", null);
/*  314 */       INDEX_TO_CHARSET[13] = getJavaEncodingForMysqlEncoding("sjis", null);
/*  315 */       INDEX_TO_CHARSET[14] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*      */ 
/*  317 */       INDEX_TO_CHARSET[15] = getJavaEncodingForMysqlEncoding("danish", null);
/*      */ 
/*  319 */       INDEX_TO_CHARSET[16] = getJavaEncodingForMysqlEncoding("hebrew", null);
/*      */ 
/*  322 */       INDEX_TO_CHARSET[17] = "ISO8859_1";
/*      */ 
/*  324 */       INDEX_TO_CHARSET[18] = getJavaEncodingForMysqlEncoding("tis620", null);
/*      */ 
/*  326 */       INDEX_TO_CHARSET[19] = getJavaEncodingForMysqlEncoding("euc_kr", null);
/*      */ 
/*  328 */       INDEX_TO_CHARSET[20] = getJavaEncodingForMysqlEncoding("estonia", null);
/*      */ 
/*  330 */       INDEX_TO_CHARSET[21] = getJavaEncodingForMysqlEncoding("hungarian", null);
/*      */ 
/*  332 */       INDEX_TO_CHARSET[22] = "KOI8_R";
/*  333 */       INDEX_TO_CHARSET[23] = getJavaEncodingForMysqlEncoding("win1251ukr", null);
/*      */ 
/*  335 */       INDEX_TO_CHARSET[24] = getJavaEncodingForMysqlEncoding("gb2312", null);
/*      */ 
/*  337 */       INDEX_TO_CHARSET[25] = getJavaEncodingForMysqlEncoding("greek", null);
/*      */ 
/*  339 */       INDEX_TO_CHARSET[26] = getJavaEncodingForMysqlEncoding("win1250", null);
/*      */ 
/*  341 */       INDEX_TO_CHARSET[27] = getJavaEncodingForMysqlEncoding("croat", null);
/*      */ 
/*  343 */       INDEX_TO_CHARSET[28] = getJavaEncodingForMysqlEncoding("gbk", null);
/*  344 */       INDEX_TO_CHARSET[29] = getJavaEncodingForMysqlEncoding("cp1257", null);
/*      */ 
/*  346 */       INDEX_TO_CHARSET[30] = getJavaEncodingForMysqlEncoding("latin5", null);
/*      */ 
/*  348 */       INDEX_TO_CHARSET[31] = getJavaEncodingForMysqlEncoding("latin1_de", null);
/*      */ 
/*  350 */       INDEX_TO_CHARSET[32] = "ISO8859_1";
/*  351 */       INDEX_TO_CHARSET[33] = getJavaEncodingForMysqlEncoding("utf8", null);
/*  352 */       INDEX_TO_CHARSET[34] = "Cp1250";
/*  353 */       INDEX_TO_CHARSET[35] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*  354 */       INDEX_TO_CHARSET[36] = getJavaEncodingForMysqlEncoding("cp866", null);
/*      */ 
/*  356 */       INDEX_TO_CHARSET[37] = "Cp895";
/*  357 */       INDEX_TO_CHARSET[38] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */ 
/*  359 */       INDEX_TO_CHARSET[39] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */ 
/*  361 */       INDEX_TO_CHARSET[40] = "latin2";
/*  362 */       INDEX_TO_CHARSET[41] = getJavaEncodingForMysqlEncoding("latvian", null);
/*      */ 
/*  364 */       INDEX_TO_CHARSET[42] = getJavaEncodingForMysqlEncoding("latvian1", null);
/*      */ 
/*  366 */       INDEX_TO_CHARSET[43] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */ 
/*  368 */       INDEX_TO_CHARSET[44] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */ 
/*  370 */       INDEX_TO_CHARSET[45] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */ 
/*  372 */       INDEX_TO_CHARSET[46] = getJavaEncodingForMysqlEncoding("macce", null);
/*      */ 
/*  374 */       INDEX_TO_CHARSET[47] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */ 
/*  376 */       INDEX_TO_CHARSET[48] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */ 
/*  378 */       INDEX_TO_CHARSET[49] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */ 
/*  380 */       INDEX_TO_CHARSET[50] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*      */ 
/*  382 */       INDEX_TO_CHARSET[51] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*      */ 
/*  384 */       INDEX_TO_CHARSET[52] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*      */ 
/*  386 */       INDEX_TO_CHARSET[53] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */ 
/*  388 */       INDEX_TO_CHARSET[54] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */ 
/*  390 */       INDEX_TO_CHARSET[55] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */ 
/*  392 */       INDEX_TO_CHARSET[56] = getJavaEncodingForMysqlEncoding("macroman", null);
/*      */ 
/*  394 */       INDEX_TO_CHARSET[57] = getJavaEncodingForMysqlEncoding("cp1256", null);
/*      */ 
/*  397 */       INDEX_TO_CHARSET[58] = "ISO8859_1";
/*  398 */       INDEX_TO_CHARSET[59] = "ISO8859_1";
/*  399 */       INDEX_TO_CHARSET[60] = "ISO8859_1";
/*  400 */       INDEX_TO_CHARSET[61] = "ISO8859_1";
/*  401 */       INDEX_TO_CHARSET[62] = "ISO8859_1";
/*      */ 
/*  403 */       INDEX_TO_CHARSET[63] = getJavaEncodingForMysqlEncoding("binary", null);
/*      */ 
/*  405 */       INDEX_TO_CHARSET[64] = "ISO8859_2";
/*  406 */       INDEX_TO_CHARSET[65] = getJavaEncodingForMysqlEncoding("ascii", null);
/*      */ 
/*  408 */       INDEX_TO_CHARSET[66] = getJavaEncodingForMysqlEncoding("cp1250", null);
/*      */ 
/*  410 */       INDEX_TO_CHARSET[67] = getJavaEncodingForMysqlEncoding("cp1256", null);
/*      */ 
/*  412 */       INDEX_TO_CHARSET[68] = getJavaEncodingForMysqlEncoding("cp866", null);
/*      */ 
/*  414 */       INDEX_TO_CHARSET[69] = "US-ASCII";
/*  415 */       INDEX_TO_CHARSET[70] = getJavaEncodingForMysqlEncoding("greek", null);
/*      */ 
/*  417 */       INDEX_TO_CHARSET[71] = getJavaEncodingForMysqlEncoding("hebrew", null);
/*      */ 
/*  419 */       INDEX_TO_CHARSET[72] = "US-ASCII";
/*  420 */       INDEX_TO_CHARSET[73] = "Cp895";
/*  421 */       INDEX_TO_CHARSET[74] = getJavaEncodingForMysqlEncoding("koi8r", null);
/*      */ 
/*  423 */       INDEX_TO_CHARSET[75] = "KOI8_r";
/*      */ 
/*  425 */       INDEX_TO_CHARSET[76] = "ISO8859_1";
/*      */ 
/*  427 */       INDEX_TO_CHARSET[77] = getJavaEncodingForMysqlEncoding("latin2", null);
/*      */ 
/*  429 */       INDEX_TO_CHARSET[78] = getJavaEncodingForMysqlEncoding("latin5", null);
/*      */ 
/*  431 */       INDEX_TO_CHARSET[79] = getJavaEncodingForMysqlEncoding("latin7", null);
/*      */ 
/*  433 */       INDEX_TO_CHARSET[80] = getJavaEncodingForMysqlEncoding("cp850", null);
/*      */ 
/*  435 */       INDEX_TO_CHARSET[81] = getJavaEncodingForMysqlEncoding("cp852", null);
/*      */ 
/*  437 */       INDEX_TO_CHARSET[82] = "ISO8859_1";
/*  438 */       INDEX_TO_CHARSET[83] = getJavaEncodingForMysqlEncoding("utf8", null);
/*  439 */       INDEX_TO_CHARSET[84] = getJavaEncodingForMysqlEncoding("big5", null);
/*  440 */       INDEX_TO_CHARSET[85] = getJavaEncodingForMysqlEncoding("euckr", null);
/*      */ 
/*  442 */       INDEX_TO_CHARSET[86] = getJavaEncodingForMysqlEncoding("gb2312", null);
/*      */ 
/*  444 */       INDEX_TO_CHARSET[87] = getJavaEncodingForMysqlEncoding("gbk", null);
/*  445 */       INDEX_TO_CHARSET[88] = getJavaEncodingForMysqlEncoding("sjis", null);
/*  446 */       INDEX_TO_CHARSET[89] = getJavaEncodingForMysqlEncoding("tis620", null);
/*      */ 
/*  448 */       INDEX_TO_CHARSET[90] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*  449 */       INDEX_TO_CHARSET[91] = getJavaEncodingForMysqlEncoding("ujis", null);
/*  450 */       INDEX_TO_CHARSET[92] = "US-ASCII";
/*  451 */       INDEX_TO_CHARSET[93] = "US-ASCII";
/*  452 */       INDEX_TO_CHARSET[94] = getJavaEncodingForMysqlEncoding("latin1", null);
/*      */ 
/*  454 */       INDEX_TO_CHARSET[95] = getJavaEncodingForMysqlEncoding("cp932", null);
/*      */ 
/*  456 */       INDEX_TO_CHARSET[96] = getJavaEncodingForMysqlEncoding("cp932", null);
/*      */ 
/*  458 */       INDEX_TO_CHARSET[97] = getJavaEncodingForMysqlEncoding("eucjpms", null);
/*      */ 
/*  460 */       INDEX_TO_CHARSET[98] = getJavaEncodingForMysqlEncoding("eucjpms", null);
/*      */ 
/*  463 */       for (int i = 99; i < 128; i++) {
/*  464 */         INDEX_TO_CHARSET[i] = "ISO8859_1";
/*      */       }
/*      */ 
/*  467 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  469 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  471 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  473 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  475 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  477 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  479 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  481 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  483 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  485 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  487 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  489 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  491 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  493 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  495 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  497 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  499 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  501 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  503 */       INDEX_TO_CHARSET[''] = getJavaEncodingForMysqlEncoding("ucs2", null);
/*      */ 
/*  506 */       for (int i = 147; i < 192; i++) {
/*  507 */         INDEX_TO_CHARSET[i] = "ISO8859_1";
/*      */       }
/*      */ 
/*  510 */       INDEX_TO_CHARSET['À'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  512 */       INDEX_TO_CHARSET['Á'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  514 */       INDEX_TO_CHARSET['Â'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  516 */       INDEX_TO_CHARSET['Ã'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  518 */       INDEX_TO_CHARSET['Ä'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  520 */       INDEX_TO_CHARSET['Å'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  522 */       INDEX_TO_CHARSET['Æ'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  524 */       INDEX_TO_CHARSET['Ç'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  526 */       INDEX_TO_CHARSET['È'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  528 */       INDEX_TO_CHARSET['É'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  530 */       INDEX_TO_CHARSET['Ê'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  532 */       INDEX_TO_CHARSET['Ë'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  534 */       INDEX_TO_CHARSET['Ì'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  536 */       INDEX_TO_CHARSET['Í'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  538 */       INDEX_TO_CHARSET['Î'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  540 */       INDEX_TO_CHARSET['Ï'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  542 */       INDEX_TO_CHARSET['Ð'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  544 */       INDEX_TO_CHARSET['Ñ'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  546 */       INDEX_TO_CHARSET['Ò'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  549 */       INDEX_TO_CHARSET['Ó'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  552 */       for (int i = 212; i < 224; i++) {
/*  553 */         INDEX_TO_CHARSET[i] = "ISO8859_1";
/*      */       }
/*      */ 
/*  556 */       for (int i = 224; i <= 243; i++) {
/*  557 */         INDEX_TO_CHARSET[i] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */       }
/*      */ 
/*  561 */       for (int i = 101; i <= 120; i++) {
/*  562 */         INDEX_TO_CHARSET[i] = getJavaEncodingForMysqlEncoding("utf16", null);
/*      */       }
/*      */ 
/*  566 */       for (int i = 160; i <= 179; i++) {
/*  567 */         INDEX_TO_CHARSET[i] = getJavaEncodingForMysqlEncoding("utf32", null);
/*      */       }
/*      */ 
/*  571 */       for (int i = 244; i < 254; i++) {
/*  572 */         INDEX_TO_CHARSET[i] = "ISO8859_1";
/*      */       }
/*      */ 
/*  575 */       INDEX_TO_CHARSET['þ'] = getJavaEncodingForMysqlEncoding("utf8", null);
/*      */ 
/*  580 */       for (int i = 1; i < INDEX_TO_CHARSET.length; i++) {
/*  581 */         if (INDEX_TO_CHARSET[i] == null) {
/*  582 */           throw new RuntimeException("Assertion failure: No mapping from charset index " + i + " to a Java character set");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*      */     }
/*  589 */     INDEX_TO_COLLATION = new String['ÿ'];
/*      */ 
/*  591 */     INDEX_TO_COLLATION[1] = "big5_chinese_ci";
/*  592 */     INDEX_TO_COLLATION[2] = "latin2_czech_cs";
/*  593 */     INDEX_TO_COLLATION[3] = "dec8_swedish_ci";
/*  594 */     INDEX_TO_COLLATION[4] = "cp850_general_ci";
/*  595 */     INDEX_TO_COLLATION[5] = "latin1_german1_ci";
/*  596 */     INDEX_TO_COLLATION[6] = "hp8_english_ci";
/*  597 */     INDEX_TO_COLLATION[7] = "koi8r_general_ci";
/*  598 */     INDEX_TO_COLLATION[8] = "latin1_swedish_ci";
/*  599 */     INDEX_TO_COLLATION[9] = "latin2_general_ci";
/*  600 */     INDEX_TO_COLLATION[10] = "swe7_swedish_ci";
/*  601 */     INDEX_TO_COLLATION[11] = "ascii_general_ci";
/*  602 */     INDEX_TO_COLLATION[12] = "ujis_japanese_ci";
/*  603 */     INDEX_TO_COLLATION[13] = "sjis_japanese_ci";
/*  604 */     INDEX_TO_COLLATION[14] = "cp1251_bulgarian_ci";
/*  605 */     INDEX_TO_COLLATION[15] = "latin1_danish_ci";
/*  606 */     INDEX_TO_COLLATION[16] = "hebrew_general_ci";
/*  607 */     INDEX_TO_COLLATION[18] = "tis620_thai_ci";
/*  608 */     INDEX_TO_COLLATION[19] = "euckr_korean_ci";
/*  609 */     INDEX_TO_COLLATION[20] = "latin7_estonian_cs";
/*  610 */     INDEX_TO_COLLATION[21] = "latin2_hungarian_ci";
/*  611 */     INDEX_TO_COLLATION[22] = "koi8u_general_ci";
/*  612 */     INDEX_TO_COLLATION[23] = "cp1251_ukrainian_ci";
/*  613 */     INDEX_TO_COLLATION[24] = "gb2312_chinese_ci";
/*  614 */     INDEX_TO_COLLATION[25] = "greek_general_ci";
/*  615 */     INDEX_TO_COLLATION[26] = "cp1250_general_ci";
/*  616 */     INDEX_TO_COLLATION[27] = "latin2_croatian_ci";
/*  617 */     INDEX_TO_COLLATION[28] = "gbk_chinese_ci";
/*  618 */     INDEX_TO_COLLATION[29] = "cp1257_lithuanian_ci";
/*  619 */     INDEX_TO_COLLATION[30] = "latin5_turkish_ci";
/*  620 */     INDEX_TO_COLLATION[31] = "latin1_german2_ci";
/*  621 */     INDEX_TO_COLLATION[32] = "armscii8_general_ci";
/*  622 */     INDEX_TO_COLLATION[33] = "utf8_general_ci";
/*  623 */     INDEX_TO_COLLATION[34] = "cp1250_czech_cs";
/*  624 */     INDEX_TO_COLLATION[35] = "ucs2_general_ci";
/*  625 */     INDEX_TO_COLLATION[36] = "cp866_general_ci";
/*  626 */     INDEX_TO_COLLATION[37] = "keybcs2_general_ci";
/*  627 */     INDEX_TO_COLLATION[38] = "macce_general_ci";
/*  628 */     INDEX_TO_COLLATION[39] = "macroman_general_ci";
/*  629 */     INDEX_TO_COLLATION[40] = "cp852_general_ci";
/*  630 */     INDEX_TO_COLLATION[41] = "latin7_general_ci";
/*  631 */     INDEX_TO_COLLATION[42] = "latin7_general_cs";
/*  632 */     INDEX_TO_COLLATION[43] = "macce_bin";
/*  633 */     INDEX_TO_COLLATION[44] = "cp1250_croatian_ci";
/*  634 */     INDEX_TO_COLLATION[47] = "latin1_bin";
/*  635 */     INDEX_TO_COLLATION[48] = "latin1_general_ci";
/*  636 */     INDEX_TO_COLLATION[49] = "latin1_general_cs";
/*  637 */     INDEX_TO_COLLATION[50] = "cp1251_bin";
/*  638 */     INDEX_TO_COLLATION[51] = "cp1251_general_ci";
/*  639 */     INDEX_TO_COLLATION[52] = "cp1251_general_cs";
/*  640 */     INDEX_TO_COLLATION[53] = "macroman_bin";
/*  641 */     INDEX_TO_COLLATION[57] = "cp1256_general_ci";
/*  642 */     INDEX_TO_COLLATION[58] = "cp1257_bin";
/*  643 */     INDEX_TO_COLLATION[59] = "cp1257_general_ci";
/*  644 */     INDEX_TO_COLLATION[63] = "binary";
/*  645 */     INDEX_TO_COLLATION[64] = "armscii8_bin";
/*  646 */     INDEX_TO_COLLATION[65] = "ascii_bin";
/*  647 */     INDEX_TO_COLLATION[66] = "cp1250_bin";
/*  648 */     INDEX_TO_COLLATION[67] = "cp1256_bin";
/*  649 */     INDEX_TO_COLLATION[68] = "cp866_bin";
/*  650 */     INDEX_TO_COLLATION[69] = "dec8_bin";
/*  651 */     INDEX_TO_COLLATION[70] = "greek_bin";
/*  652 */     INDEX_TO_COLLATION[71] = "hebrew_bin";
/*  653 */     INDEX_TO_COLLATION[72] = "hp8_bin";
/*  654 */     INDEX_TO_COLLATION[73] = "keybcs2_bin";
/*  655 */     INDEX_TO_COLLATION[74] = "koi8r_bin";
/*  656 */     INDEX_TO_COLLATION[75] = "koi8u_bin";
/*  657 */     INDEX_TO_COLLATION[77] = "latin2_bin";
/*  658 */     INDEX_TO_COLLATION[78] = "latin5_bin";
/*  659 */     INDEX_TO_COLLATION[79] = "latin7_bin";
/*  660 */     INDEX_TO_COLLATION[80] = "cp850_bin";
/*  661 */     INDEX_TO_COLLATION[81] = "cp852_bin";
/*  662 */     INDEX_TO_COLLATION[82] = "swe7_bin";
/*  663 */     INDEX_TO_COLLATION[83] = "utf8_bin";
/*  664 */     INDEX_TO_COLLATION[84] = "big5_bin";
/*  665 */     INDEX_TO_COLLATION[85] = "euckr_bin";
/*  666 */     INDEX_TO_COLLATION[86] = "gb2312_bin";
/*  667 */     INDEX_TO_COLLATION[87] = "gbk_bin";
/*  668 */     INDEX_TO_COLLATION[88] = "sjis_bin";
/*  669 */     INDEX_TO_COLLATION[89] = "tis620_bin";
/*  670 */     INDEX_TO_COLLATION[90] = "ucs2_bin";
/*  671 */     INDEX_TO_COLLATION[91] = "ujis_bin";
/*  672 */     INDEX_TO_COLLATION[92] = "geostd8_general_ci";
/*  673 */     INDEX_TO_COLLATION[93] = "geostd8_bin";
/*  674 */     INDEX_TO_COLLATION[94] = "latin1_spanish_ci";
/*  675 */     INDEX_TO_COLLATION[95] = "cp932_japanese_ci";
/*  676 */     INDEX_TO_COLLATION[96] = "cp932_bin";
/*  677 */     INDEX_TO_COLLATION[97] = "eucjpms_japanese_ci";
/*  678 */     INDEX_TO_COLLATION[98] = "eucjpms_bin";
/*  679 */     INDEX_TO_COLLATION[99] = "cp1250_polish_ci";
/*  680 */     INDEX_TO_COLLATION[''] = "ucs2_unicode_ci";
/*  681 */     INDEX_TO_COLLATION[''] = "ucs2_icelandic_ci";
/*  682 */     INDEX_TO_COLLATION[''] = "ucs2_latvian_ci";
/*  683 */     INDEX_TO_COLLATION[''] = "ucs2_romanian_ci";
/*  684 */     INDEX_TO_COLLATION[''] = "ucs2_slovenian_ci";
/*  685 */     INDEX_TO_COLLATION[''] = "ucs2_polish_ci";
/*  686 */     INDEX_TO_COLLATION[''] = "ucs2_estonian_ci";
/*  687 */     INDEX_TO_COLLATION[''] = "ucs2_spanish_ci";
/*  688 */     INDEX_TO_COLLATION[''] = "ucs2_swedish_ci";
/*  689 */     INDEX_TO_COLLATION[''] = "ucs2_turkish_ci";
/*  690 */     INDEX_TO_COLLATION[''] = "ucs2_czech_ci";
/*  691 */     INDEX_TO_COLLATION[''] = "ucs2_danish_ci";
/*  692 */     INDEX_TO_COLLATION[''] = "ucs2_lithuanian_ci ";
/*  693 */     INDEX_TO_COLLATION[''] = "ucs2_slovak_ci";
/*  694 */     INDEX_TO_COLLATION[''] = "ucs2_spanish2_ci";
/*  695 */     INDEX_TO_COLLATION[''] = "ucs2_roman_ci";
/*  696 */     INDEX_TO_COLLATION[''] = "ucs2_persian_ci";
/*  697 */     INDEX_TO_COLLATION[''] = "ucs2_esperanto_ci";
/*  698 */     INDEX_TO_COLLATION[''] = "ucs2_hungarian_ci";
/*  699 */     INDEX_TO_COLLATION['À'] = "utf8_unicode_ci";
/*  700 */     INDEX_TO_COLLATION['Á'] = "utf8_icelandic_ci";
/*  701 */     INDEX_TO_COLLATION['Â'] = "utf8_latvian_ci";
/*  702 */     INDEX_TO_COLLATION['Ã'] = "utf8_romanian_ci";
/*  703 */     INDEX_TO_COLLATION['Ä'] = "utf8_slovenian_ci";
/*  704 */     INDEX_TO_COLLATION['Å'] = "utf8_polish_ci";
/*  705 */     INDEX_TO_COLLATION['Æ'] = "utf8_estonian_ci";
/*  706 */     INDEX_TO_COLLATION['Ç'] = "utf8_spanish_ci";
/*  707 */     INDEX_TO_COLLATION['È'] = "utf8_swedish_ci";
/*  708 */     INDEX_TO_COLLATION['É'] = "utf8_turkish_ci";
/*  709 */     INDEX_TO_COLLATION['Ê'] = "utf8_czech_ci";
/*  710 */     INDEX_TO_COLLATION['Ë'] = "utf8_danish_ci";
/*  711 */     INDEX_TO_COLLATION['Ì'] = "utf8_lithuanian_ci ";
/*  712 */     INDEX_TO_COLLATION['Í'] = "utf8_slovak_ci";
/*  713 */     INDEX_TO_COLLATION['Î'] = "utf8_spanish2_ci";
/*  714 */     INDEX_TO_COLLATION['Ï'] = "utf8_roman_ci";
/*  715 */     INDEX_TO_COLLATION['Ð'] = "utf8_persian_ci";
/*  716 */     INDEX_TO_COLLATION['Ñ'] = "utf8_esperanto_ci";
/*  717 */     INDEX_TO_COLLATION['Ò'] = "utf8_hungarian_ci";
/*      */ 
/*  721 */     INDEX_TO_COLLATION[33] = "utf8mb3_general_ci";
/*  722 */     INDEX_TO_COLLATION[83] = "utf8mb3_bin";
/*  723 */     INDEX_TO_COLLATION['À'] = "utf8mb3_unicode_ci";
/*  724 */     INDEX_TO_COLLATION['Á'] = "utf8mb3_icelandic_ci";
/*  725 */     INDEX_TO_COLLATION['Â'] = "utf8mb3_latvian_ci";
/*  726 */     INDEX_TO_COLLATION['Ã'] = "utf8mb3_romanian_ci";
/*  727 */     INDEX_TO_COLLATION['Ä'] = "utf8mb3_slovenian_ci";
/*  728 */     INDEX_TO_COLLATION['Å'] = "utf8mb3_polish_ci";
/*  729 */     INDEX_TO_COLLATION['Æ'] = "utf8mb3_estonian_ci";
/*  730 */     INDEX_TO_COLLATION['Ç'] = "utf8mb3_spanish_ci";
/*  731 */     INDEX_TO_COLLATION['È'] = "utf8mb3_swedish_ci";
/*  732 */     INDEX_TO_COLLATION['É'] = "utf8mb3_turkish_ci";
/*  733 */     INDEX_TO_COLLATION['Ê'] = "utf8mb3_czech_ci";
/*  734 */     INDEX_TO_COLLATION['Ë'] = "utf8mb3_danish_ci";
/*  735 */     INDEX_TO_COLLATION['Ì'] = "utf8mb3_lithuanian_ci";
/*  736 */     INDEX_TO_COLLATION['Í'] = "utf8mb3_slovak_ci";
/*  737 */     INDEX_TO_COLLATION['Î'] = "utf8mb3_spanish2_ci";
/*  738 */     INDEX_TO_COLLATION['Ï'] = "utf8mb3_roman_ci";
/*  739 */     INDEX_TO_COLLATION['Ð'] = "utf8mb3_persian_ci";
/*  740 */     INDEX_TO_COLLATION['Ñ'] = "utf8mb3_esperanto_ci";
/*  741 */     INDEX_TO_COLLATION['Ò'] = "utf8mb3_hungarian_ci";
/*  742 */     INDEX_TO_COLLATION['Ó'] = "utf8mb3_sinhala_ci";
/*  743 */     INDEX_TO_COLLATION['þ'] = "utf8mb3_general_cs";
/*      */ 
/*  745 */     INDEX_TO_COLLATION[45] = "utf8_general_ci";
/*  746 */     INDEX_TO_COLLATION[46] = "utf8_bin";
/*  747 */     INDEX_TO_COLLATION['à'] = "utf8_unicode_ci";
/*  748 */     INDEX_TO_COLLATION['á'] = "utf8_icelandic_ci";
/*  749 */     INDEX_TO_COLLATION['â'] = "utf8_latvian_ci";
/*  750 */     INDEX_TO_COLLATION['ã'] = "utf8_romanian_ci";
/*  751 */     INDEX_TO_COLLATION['ä'] = "utf8_slovenian_ci";
/*  752 */     INDEX_TO_COLLATION['å'] = "utf8_polish_ci";
/*  753 */     INDEX_TO_COLLATION['æ'] = "utf8_estonian_ci";
/*  754 */     INDEX_TO_COLLATION['ç'] = "utf8_spanish_ci";
/*  755 */     INDEX_TO_COLLATION['è'] = "utf8_swedish_ci";
/*  756 */     INDEX_TO_COLLATION['é'] = "utf8_turkish_ci";
/*  757 */     INDEX_TO_COLLATION['ê'] = "utf8_czech_ci";
/*  758 */     INDEX_TO_COLLATION['ë'] = "utf8_danish_ci";
/*  759 */     INDEX_TO_COLLATION['ì'] = "utf8_lithuanian_ci";
/*  760 */     INDEX_TO_COLLATION['í'] = "utf8_slovak_ci";
/*  761 */     INDEX_TO_COLLATION['î'] = "utf8_spanish2_ci";
/*  762 */     INDEX_TO_COLLATION['ï'] = "utf8_roman_ci";
/*  763 */     INDEX_TO_COLLATION['ð'] = "utf8_persian_ci";
/*  764 */     INDEX_TO_COLLATION['ñ'] = "utf8_esperanto_ci";
/*  765 */     INDEX_TO_COLLATION['ò'] = "utf8_hungarian_ci";
/*  766 */     INDEX_TO_COLLATION['ó'] = "utf8_sinhala_ci";
/*      */ 
/*  768 */     INDEX_TO_COLLATION[54] = "utf16_general_ci";
/*  769 */     INDEX_TO_COLLATION[55] = "utf16_bin";
/*  770 */     INDEX_TO_COLLATION[101] = "utf16_unicode_ci";
/*  771 */     INDEX_TO_COLLATION[102] = "utf16_icelandic_ci";
/*  772 */     INDEX_TO_COLLATION[103] = "utf16_latvian_ci";
/*  773 */     INDEX_TO_COLLATION[104] = "utf16_romanian_ci";
/*  774 */     INDEX_TO_COLLATION[105] = "utf16_slovenian_ci";
/*  775 */     INDEX_TO_COLLATION[106] = "utf16_polish_ci";
/*  776 */     INDEX_TO_COLLATION[107] = "utf16_estonian_ci";
/*  777 */     INDEX_TO_COLLATION[108] = "utf16_spanish_ci";
/*  778 */     INDEX_TO_COLLATION[109] = "utf16_swedish_ci";
/*  779 */     INDEX_TO_COLLATION[110] = "utf16_turkish_ci";
/*  780 */     INDEX_TO_COLLATION[111] = "utf16_czech_ci";
/*  781 */     INDEX_TO_COLLATION[112] = "utf16_danish_ci";
/*  782 */     INDEX_TO_COLLATION[113] = "utf16_lithuanian_ci";
/*  783 */     INDEX_TO_COLLATION[114] = "utf16_slovak_ci";
/*  784 */     INDEX_TO_COLLATION[115] = "utf16_spanish2_ci";
/*  785 */     INDEX_TO_COLLATION[116] = "utf16_roman_ci";
/*  786 */     INDEX_TO_COLLATION[117] = "utf16_persian_ci";
/*  787 */     INDEX_TO_COLLATION[118] = "utf16_esperanto_ci";
/*  788 */     INDEX_TO_COLLATION[119] = "utf16_hungarian_ci";
/*  789 */     INDEX_TO_COLLATION[120] = "utf16_sinhala_ci";
/*      */ 
/*  791 */     INDEX_TO_COLLATION[60] = "utf32_general_ci";
/*  792 */     INDEX_TO_COLLATION[61] = "utf32_bin";
/*  793 */     INDEX_TO_COLLATION[' '] = "utf32_unicode_ci";
/*  794 */     INDEX_TO_COLLATION['¡'] = "utf32_icelandic_ci";
/*  795 */     INDEX_TO_COLLATION['¢'] = "utf32_latvian_ci";
/*  796 */     INDEX_TO_COLLATION['£'] = "utf32_romanian_ci";
/*  797 */     INDEX_TO_COLLATION['¤'] = "utf32_slovenian_ci";
/*  798 */     INDEX_TO_COLLATION['¥'] = "utf32_polish_ci";
/*  799 */     INDEX_TO_COLLATION['¦'] = "utf32_estonian_ci";
/*  800 */     INDEX_TO_COLLATION['§'] = "utf32_spanish_ci";
/*  801 */     INDEX_TO_COLLATION['¨'] = "utf32_swedish_ci";
/*  802 */     INDEX_TO_COLLATION['©'] = "utf32_turkish_ci";
/*  803 */     INDEX_TO_COLLATION['ª'] = "utf32_czech_ci";
/*  804 */     INDEX_TO_COLLATION['«'] = "utf32_danish_ci";
/*  805 */     INDEX_TO_COLLATION['¬'] = "utf32_lithuanian_ci";
/*  806 */     INDEX_TO_COLLATION['­'] = "utf32_slovak_ci";
/*  807 */     INDEX_TO_COLLATION['®'] = "utf32_spanish2_ci";
/*  808 */     INDEX_TO_COLLATION['¯'] = "utf32_roman_ci";
/*  809 */     INDEX_TO_COLLATION['°'] = "utf32_persian_ci";
/*  810 */     INDEX_TO_COLLATION['±'] = "utf32_esperanto_ci";
/*  811 */     INDEX_TO_COLLATION['²'] = "utf32_hungarian_ci";
/*  812 */     INDEX_TO_COLLATION['³'] = "utf32_sinhala_ci";
/*      */ 
/*  814 */     Map indexMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */ 
/*  816 */     for (int i = 0; i < INDEX_TO_CHARSET.length; i++) {
/*  817 */       String mysqlEncodingName = INDEX_TO_CHARSET[i];
/*      */ 
/*  819 */       if (mysqlEncodingName != null) {
/*  820 */         indexMap.put(INDEX_TO_CHARSET[i], Constants.integerValueOf(i));
/*      */       }
/*      */     }
/*      */ 
/*  824 */     MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP = Collections.unmodifiableMap(indexMap);
/*      */ 
/*  826 */     Map tempMap = new HashMap();
/*      */ 
/*  828 */     tempMap.put("czech", "latin2");
/*  829 */     tempMap.put("danish", "latin1");
/*  830 */     tempMap.put("dutch", "latin1");
/*  831 */     tempMap.put("english", "latin1");
/*  832 */     tempMap.put("estonian", "latin7");
/*  833 */     tempMap.put("french", "latin1");
/*  834 */     tempMap.put("german", "latin1");
/*  835 */     tempMap.put("greek", "greek");
/*  836 */     tempMap.put("hungarian", "latin2");
/*  837 */     tempMap.put("italian", "latin1");
/*  838 */     tempMap.put("japanese", "ujis");
/*  839 */     tempMap.put("japanese-sjis", "sjis");
/*  840 */     tempMap.put("korean", "euckr");
/*  841 */     tempMap.put("norwegian", "latin1");
/*  842 */     tempMap.put("norwegian-ny", "latin1");
/*  843 */     tempMap.put("polish", "latin2");
/*  844 */     tempMap.put("portuguese", "latin1");
/*  845 */     tempMap.put("romanian", "latin2");
/*  846 */     tempMap.put("russian", "koi8r");
/*  847 */     tempMap.put("serbian", "cp1250");
/*  848 */     tempMap.put("slovak", "latin2");
/*  849 */     tempMap.put("spanish", "latin1");
/*  850 */     tempMap.put("swedish", "latin1");
/*  851 */     tempMap.put("ukrainian", "koi8u");
/*      */ 
/*  853 */     ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(tempMap);
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.CharsetMapping
 * JD-Core Version:    0.6.0
 */