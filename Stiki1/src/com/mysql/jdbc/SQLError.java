/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLDataException;
/*      */ import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
/*      */ import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
/*      */ import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTransientConnectionException;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.BindException;
/*      */ import java.sql.DataTruncation;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class SQLError
/*      */ {
/*      */   static final int ER_WARNING_NOT_COMPLETE_ROLLBACK = 1196;
/*      */   private static Map mysqlToSql99State;
/*      */   private static Map mysqlToSqlState;
/*      */   public static final String SQL_STATE_BASE_TABLE_NOT_FOUND = "S0002";
/*      */   public static final String SQL_STATE_BASE_TABLE_OR_VIEW_ALREADY_EXISTS = "S0001";
/*      */   public static final String SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND = "42S02";
/*      */   public static final String SQL_STATE_COLUMN_ALREADY_EXISTS = "S0021";
/*      */   public static final String SQL_STATE_COLUMN_NOT_FOUND = "S0022";
/*      */   public static final String SQL_STATE_COMMUNICATION_LINK_FAILURE = "08S01";
/*      */   public static final String SQL_STATE_CONNECTION_FAIL_DURING_TX = "08007";
/*      */   public static final String SQL_STATE_CONNECTION_IN_USE = "08002";
/*      */   public static final String SQL_STATE_CONNECTION_NOT_OPEN = "08003";
/*      */   public static final String SQL_STATE_CONNECTION_REJECTED = "08004";
/*      */   public static final String SQL_STATE_DATE_TRUNCATED = "01004";
/*      */   public static final String SQL_STATE_DATETIME_FIELD_OVERFLOW = "22008";
/*      */   public static final String SQL_STATE_DEADLOCK = "41000";
/*      */   public static final String SQL_STATE_DISCONNECT_ERROR = "01002";
/*      */   public static final String SQL_STATE_DIVISION_BY_ZERO = "22012";
/*      */   public static final String SQL_STATE_DRIVER_NOT_CAPABLE = "S1C00";
/*      */   public static final String SQL_STATE_ERROR_IN_ROW = "01S01";
/*      */   public static final String SQL_STATE_GENERAL_ERROR = "S1000";
/*      */   public static final String SQL_STATE_ILLEGAL_ARGUMENT = "S1009";
/*      */   public static final String SQL_STATE_INDEX_ALREADY_EXISTS = "S0011";
/*      */   public static final String SQL_STATE_INDEX_NOT_FOUND = "S0012";
/*      */   public static final String SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST = "21S01";
/*      */   public static final String SQL_STATE_INVALID_AUTH_SPEC = "28000";
/*      */   public static final String SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST = "22018";
/*      */   public static final String SQL_STATE_INVALID_COLUMN_NUMBER = "S1002";
/*      */   public static final String SQL_STATE_INVALID_CONNECTION_ATTRIBUTE = "01S00";
/*      */   public static final String SQL_STATE_MEMORY_ALLOCATION_FAILURE = "S1001";
/*      */   public static final String SQL_STATE_MORE_THAN_ONE_ROW_UPDATED_OR_DELETED = "01S04";
/*      */   public static final String SQL_STATE_NO_DEFAULT_FOR_COLUMN = "S0023";
/*      */   public static final String SQL_STATE_NO_ROWS_UPDATED_OR_DELETED = "01S03";
/*      */   public static final String SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE = "22003";
/*      */   public static final String SQL_STATE_PRIVILEGE_NOT_REVOKED = "01006";
/*      */   public static final String SQL_STATE_SYNTAX_ERROR = "42000";
/*      */   public static final String SQL_STATE_TIMEOUT_EXPIRED = "S1T00";
/*      */   public static final String SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN = "08007";
/*      */   public static final String SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE = "08001";
/*      */   public static final String SQL_STATE_WRONG_NO_OF_PARAMETERS = "07001";
/*      */   public static final String SQL_STATE_INVALID_TRANSACTION_TERMINATION = "2D000";
/*      */   private static Map sqlStateMessages;
/*      */   private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 28800L;
/*      */   private static final int DUE_TO_TIMEOUT_FALSE = 0;
/*      */   private static final int DUE_TO_TIMEOUT_MAYBE = 2;
/*      */   private static final int DUE_TO_TIMEOUT_TRUE = 1;
/*      */   private static final Constructor JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR;
/*      */   private static Method THROWABLE_INIT_CAUSE_METHOD;
/*      */ 
/*      */   static SQLWarning convertShowWarningsToSQLWarnings(Connection connection)
/*      */     throws SQLException
/*      */   {
/*  701 */     return convertShowWarningsToSQLWarnings(connection, 0, false);
/*      */   }
/*      */ 
/*      */   static SQLWarning convertShowWarningsToSQLWarnings(Connection connection, int warningCountIfKnown, boolean forTruncationOnly)
/*      */     throws SQLException
/*      */   {
/*  726 */     Statement stmt = null;
/*  727 */     ResultSet warnRs = null;
/*      */ 
/*  729 */     SQLWarning currentWarning = null;
/*      */     try
/*      */     {
/*  732 */       if (warningCountIfKnown < 100) {
/*  733 */         stmt = connection.createStatement();
/*      */ 
/*  735 */         if (stmt.getMaxRows() != 0)
/*  736 */           stmt.setMaxRows(0);
/*      */       }
/*      */       else
/*      */       {
/*  740 */         stmt = connection.createStatement(1003, 1007);
/*      */ 
/*  743 */         stmt.setFetchSize(-2147483648);
/*      */       }
/*      */ 
/*  753 */       warnRs = stmt.executeQuery("SHOW WARNINGS");
/*      */ 
/*  755 */       while (warnRs.next()) {
/*  756 */         code = warnRs.getInt("Code");
/*      */ 
/*  758 */         if (forTruncationOnly) {
/*  759 */           if ((code == 1265) || (code == 1264)) {
/*  760 */             DataTruncation newTruncation = new MysqlDataTruncation(warnRs.getString("Message"), 0, false, false, 0, 0, code);
/*      */ 
/*  763 */             if (currentWarning == null)
/*  764 */               currentWarning = newTruncation;
/*      */             else
/*  766 */               currentWarning.setNextWarning(newTruncation);
/*      */           }
/*      */         }
/*      */         else {
/*  770 */           String level = warnRs.getString("Level");
/*  771 */           String message = warnRs.getString("Message");
/*      */ 
/*  773 */           SQLWarning newWarning = new SQLWarning(message, mysqlToSqlState(code, connection.getUseSqlStateCodes()), code);
/*      */ 
/*  777 */           if (currentWarning == null)
/*  778 */             currentWarning = newWarning;
/*      */           else {
/*  780 */             currentWarning.setNextWarning(newWarning);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  785 */       if ((forTruncationOnly) && (currentWarning != null)) {
/*  786 */         throw currentWarning;
/*      */       }
/*      */ 
/*  789 */       code = currentWarning;
/*      */     }
/*      */     finally
/*      */     {
/*      */       int code;
/*  791 */       SQLException reThrow = null;
/*      */ 
/*  793 */       if (warnRs != null) {
/*      */         try {
/*  795 */           warnRs.close();
/*      */         } catch (SQLException sqlEx) {
/*  797 */           reThrow = sqlEx;
/*      */         }
/*      */       }
/*      */ 
/*  801 */       if (stmt != null) {
/*      */         try {
/*  803 */           stmt.close();
/*      */         }
/*      */         catch (SQLException sqlEx)
/*      */         {
/*  808 */           reThrow = sqlEx;
/*      */         }
/*      */       }
/*      */ 
/*  812 */       if (reThrow != null)
/*  813 */         throw reThrow;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void dumpSqlStatesMappingsAsXml() throws Exception
/*      */   {
/*  819 */     TreeMap allErrorNumbers = new TreeMap();
/*  820 */     Map mysqlErrorNumbersToNames = new HashMap();
/*      */ 
/*  822 */     Integer errorNumber = null;
/*      */ 
/*  828 */     Iterator mysqlErrorNumbers = mysqlToSql99State.keySet().iterator();
/*  829 */     while (mysqlErrorNumbers.hasNext()) {
/*  830 */       errorNumber = (Integer)mysqlErrorNumbers.next();
/*  831 */       allErrorNumbers.put(errorNumber, errorNumber);
/*      */     }
/*      */ 
/*  834 */     Iterator mysqlErrorNumbers = mysqlToSqlState.keySet().iterator();
/*  835 */     while (mysqlErrorNumbers.hasNext()) {
/*  836 */       errorNumber = (Integer)mysqlErrorNumbers.next();
/*  837 */       allErrorNumbers.put(errorNumber, errorNumber);
/*      */     }
/*      */ 
/*  843 */     Field[] possibleFields = MysqlErrorNumbers.class.getDeclaredFields();
/*      */ 
/*  846 */     for (int i = 0; i < possibleFields.length; i++) {
/*  847 */       String fieldName = possibleFields[i].getName();
/*      */ 
/*  849 */       if (fieldName.startsWith("ER_")) {
/*  850 */         mysqlErrorNumbersToNames.put(possibleFields[i].get(null), fieldName);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  855 */     System.out.println("<ErrorMappings>");
/*      */ 
/*  857 */     Iterator allErrorNumbersIter = allErrorNumbers.keySet().iterator();
/*  858 */     while (allErrorNumbersIter.hasNext()) {
/*  859 */       errorNumber = (Integer)allErrorNumbersIter.next();
/*      */ 
/*  861 */       String sql92State = mysqlToSql99(errorNumber.intValue());
/*  862 */       String oldSqlState = mysqlToXOpen(errorNumber.intValue());
/*      */ 
/*  864 */       System.out.println("   <ErrorMapping mysqlErrorNumber=\"" + errorNumber + "\" mysqlErrorName=\"" + mysqlErrorNumbersToNames.get(errorNumber) + "\" legacySqlState=\"" + (oldSqlState == null ? "" : oldSqlState) + "\" sql92SqlState=\"" + (sql92State == null ? "" : sql92State) + "\"/>");
/*      */     }
/*      */ 
/*  873 */     System.out.println("</ErrorMappings>");
/*      */   }
/*      */ 
/*      */   static String get(String stateCode) {
/*  877 */     return (String)sqlStateMessages.get(stateCode);
/*      */   }
/*      */ 
/*      */   private static String mysqlToSql99(int errno) {
/*  881 */     Integer err = Constants.integerValueOf(errno);
/*      */ 
/*  883 */     if (mysqlToSql99State.containsKey(err)) {
/*  884 */       return (String)mysqlToSql99State.get(err);
/*      */     }
/*      */ 
/*  887 */     return "HY000";
/*      */   }
/*      */ 
/*      */   static String mysqlToSqlState(int errno, boolean useSql92States)
/*      */   {
/*  899 */     if (useSql92States) {
/*  900 */       return mysqlToSql99(errno);
/*      */     }
/*      */ 
/*  903 */     return mysqlToXOpen(errno);
/*      */   }
/*      */ 
/*      */   private static String mysqlToXOpen(int errno) {
/*  907 */     Integer err = Constants.integerValueOf(errno);
/*      */ 
/*  909 */     if (mysqlToSqlState.containsKey(err)) {
/*  910 */       return (String)mysqlToSqlState.get(err);
/*      */     }
/*      */ 
/*  913 */     return "S1000";
/*      */   }
/*      */ 
/*      */   public static SQLException createSQLException(String message, String sqlState, ExceptionInterceptor interceptor)
/*      */   {
/*  929 */     return createSQLException(message, sqlState, 0, interceptor);
/*      */   }
/*      */ 
/*      */   public static SQLException createSQLException(String message, ExceptionInterceptor interceptor) {
/*  933 */     return createSQLException(message, interceptor, null);
/*      */   }
/*      */   public static SQLException createSQLException(String message, ExceptionInterceptor interceptor, Connection conn) {
/*  936 */     SQLException sqlEx = new SQLException(message);
/*      */ 
/*  938 */     if (interceptor != null) {
/*  939 */       SQLException interceptedEx = interceptor.interceptException(sqlEx, conn);
/*      */ 
/*  941 */       if (interceptedEx != null) {
/*  942 */         return interceptedEx;
/*      */       }
/*      */     }
/*      */ 
/*  946 */     return sqlEx;
/*      */   }
/*      */ 
/*      */   public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor) {
/*  950 */     return createSQLException(message, sqlState, cause, interceptor, null);
/*      */   }
/*      */ 
/*      */   public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor, Connection conn) {
/*  954 */     if ((THROWABLE_INIT_CAUSE_METHOD == null) && 
/*  955 */       (cause != null)) {
/*  956 */       message = message + " due to " + cause.toString();
/*      */     }
/*      */ 
/*  960 */     SQLException sqlEx = createSQLException(message, sqlState, interceptor);
/*      */ 
/*  962 */     if ((cause != null) && (THROWABLE_INIT_CAUSE_METHOD != null)) {
/*      */       try {
/*  964 */         THROWABLE_INIT_CAUSE_METHOD.invoke(sqlEx, new Object[] { cause });
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/*  971 */     if (interceptor != null) {
/*  972 */       SQLException interceptedEx = interceptor.interceptException(sqlEx, conn);
/*      */ 
/*  974 */       if (interceptedEx != null) {
/*  975 */         return interceptedEx;
/*      */       }
/*      */     }
/*      */ 
/*  979 */     return sqlEx;
/*      */   }
/*      */ 
/*      */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, ExceptionInterceptor interceptor)
/*      */   {
/*  984 */     return createSQLException(message, sqlState, vendorErrorCode, false, interceptor);
/*      */   }
/*      */ 
/*      */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor)
/*      */   {
/*  989 */     return createSQLException(message, sqlState, vendorErrorCode, false, interceptor, null);
/*      */   }
/*      */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor, Connection conn) {
/*      */     SQLException unexpectedEx;
/*      */     try { SQLException sqlEx = null;
/*      */ 
/*  996 */       if (sqlState != null) {
/*  997 */         if (sqlState.startsWith("08")) {
/*  998 */           if (isTransient) {
/*  999 */             if (!Util.isJdbc4()) {
/* 1000 */               sqlEx = new MySQLTransientConnectionException(message, sqlState, vendorErrorCode);
/*      */             }
/*      */             else {
/* 1003 */               sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLTransientConnectionException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) }, interceptor);
/*      */             }
/*      */ 
/*      */           }
/* 1011 */           else if (!Util.isJdbc4()) {
/* 1012 */             sqlEx = new MySQLNonTransientConnectionException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */           else {
/* 1015 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */ 
/*      */         }
/* 1022 */         else if (sqlState.startsWith("22")) {
/* 1023 */           if (!Util.isJdbc4()) {
/* 1024 */             sqlEx = new MySQLDataException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */           else {
/* 1027 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLDataException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */ 
/*      */         }
/* 1035 */         else if (sqlState.startsWith("23"))
/*      */         {
/* 1037 */           if (!Util.isJdbc4()) {
/* 1038 */             sqlEx = new MySQLIntegrityConstraintViolationException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */           else {
/* 1041 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */ 
/*      */         }
/* 1049 */         else if (sqlState.startsWith("42")) {
/* 1050 */           if (!Util.isJdbc4()) {
/* 1051 */             sqlEx = new MySQLSyntaxErrorException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */           else {
/* 1054 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */ 
/*      */         }
/* 1061 */         else if (sqlState.startsWith("40")) {
/* 1062 */           if (!Util.isJdbc4()) {
/* 1063 */             sqlEx = new MySQLTransactionRollbackException(message, sqlState, vendorErrorCode);
/*      */           }
/*      */           else {
/* 1066 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Constants.integerValueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1075 */           sqlEx = new SQLException(message, sqlState, vendorErrorCode);
/*      */         }
/*      */       }
/* 1078 */       else sqlEx = new SQLException(message, sqlState, vendorErrorCode);
/*      */ 
/* 1081 */       if (interceptor != null) {
/* 1082 */         SQLException interceptedEx = interceptor.interceptException(sqlEx, conn);
/*      */ 
/* 1084 */         if (interceptedEx != null) {
/* 1085 */           return interceptedEx;
/*      */         }
/*      */       }
/*      */ 
/* 1089 */       if (sqlEx == null) {
/* 1090 */         System.out.println("!");
/*      */       }
/*      */ 
/* 1093 */       return sqlEx;
/*      */     } catch (SQLException sqlEx) {
/* 1095 */       unexpectedEx = new SQLException("Unable to create correct SQLException class instance, error class/codes may be incorrect. Reason: " + Util.stackTraceToString(sqlEx), "S1000");
/*      */ 
/* 1100 */       if (interceptor != null) {
/* 1101 */         SQLException interceptedEx = interceptor.interceptException(unexpectedEx, conn);
/*      */ 
/* 1103 */         if (interceptedEx != null) {
/* 1104 */           return interceptedEx;
/*      */         }
/*      */       }
/*      */     }
/* 1108 */     return unexpectedEx;
/*      */   }
/*      */ 
/*      */   public static SQLException createCommunicationsException(ConnectionImpl conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException, ExceptionInterceptor interceptor)
/*      */   {
/* 1115 */     SQLException exToReturn = null;
/*      */ 
/* 1117 */     if (!Util.isJdbc4())
/* 1118 */       exToReturn = new CommunicationsException(conn, lastPacketSentTimeMs, lastPacketReceivedTimeMs, underlyingException);
/*      */     else {
/*      */       try
/*      */       {
/* 1122 */         exToReturn = (SQLException)Util.handleNewInstance(JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR, new Object[] { conn, Constants.longValueOf(lastPacketSentTimeMs), Constants.longValueOf(lastPacketReceivedTimeMs), underlyingException }, interceptor);
/*      */       }
/*      */       catch (SQLException sqlEx)
/*      */       {
/* 1127 */         return sqlEx;
/*      */       }
/*      */     }
/*      */ 
/* 1131 */     if ((THROWABLE_INIT_CAUSE_METHOD != null) && (underlyingException != null)) {
/*      */       try {
/* 1133 */         THROWABLE_INIT_CAUSE_METHOD.invoke(exToReturn, new Object[] { underlyingException });
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/* 1140 */     if (interceptor != null) {
/* 1141 */       SQLException interceptedEx = interceptor.interceptException(exToReturn, conn);
/*      */ 
/* 1143 */       if (interceptedEx != null) {
/* 1144 */         return interceptedEx;
/*      */       }
/*      */     }
/*      */ 
/* 1148 */     return exToReturn;
/*      */   }
/*      */ 
/*      */   public static String createLinkFailureMessageBasedOnHeuristics(ConnectionImpl conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException, boolean streamingResultSetInPlay)
/*      */   {
/* 1168 */     long serverTimeoutSeconds = 0L;
/* 1169 */     boolean isInteractiveClient = false;
/*      */ 
/* 1171 */     if (conn != null) {
/* 1172 */       isInteractiveClient = conn.getInteractiveClient();
/*      */ 
/* 1174 */       String serverTimeoutSecondsStr = null;
/*      */ 
/* 1176 */       if (isInteractiveClient) {
/* 1177 */         serverTimeoutSecondsStr = conn.getServerVariable("interactive_timeout");
/*      */       }
/*      */       else {
/* 1180 */         serverTimeoutSecondsStr = conn.getServerVariable("wait_timeout");
/*      */       }
/*      */ 
/* 1184 */       if (serverTimeoutSecondsStr != null) {
/*      */         try {
/* 1186 */           serverTimeoutSeconds = Long.parseLong(serverTimeoutSecondsStr);
/*      */         }
/*      */         catch (NumberFormatException nfe) {
/* 1189 */           serverTimeoutSeconds = 0L;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1194 */     StringBuffer exceptionMessageBuf = new StringBuffer();
/*      */ 
/* 1196 */     if (lastPacketSentTimeMs == 0L) {
/* 1197 */       lastPacketSentTimeMs = System.currentTimeMillis();
/*      */     }
/*      */ 
/* 1200 */     long timeSinceLastPacket = (System.currentTimeMillis() - lastPacketSentTimeMs) / 1000L;
/* 1201 */     long timeSinceLastPacketMs = System.currentTimeMillis() - lastPacketSentTimeMs;
/* 1202 */     long timeSinceLastPacketReceivedMs = System.currentTimeMillis() - lastPacketReceivedTimeMs;
/*      */ 
/* 1204 */     int dueToTimeout = 0;
/*      */ 
/* 1206 */     StringBuffer timeoutMessageBuf = null;
/*      */ 
/* 1208 */     if (streamingResultSetInPlay) {
/* 1209 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.ClientWasStreaming"));
/*      */     }
/*      */     else {
/* 1212 */       if (serverTimeoutSeconds != 0L) {
/* 1213 */         if (timeSinceLastPacket > serverTimeoutSeconds) {
/* 1214 */           dueToTimeout = 1;
/*      */ 
/* 1216 */           timeoutMessageBuf = new StringBuffer();
/*      */ 
/* 1218 */           timeoutMessageBuf.append(Messages.getString("CommunicationsException.2"));
/*      */ 
/* 1221 */           if (!isInteractiveClient) {
/* 1222 */             timeoutMessageBuf.append(Messages.getString("CommunicationsException.3"));
/*      */           }
/*      */           else {
/* 1225 */             timeoutMessageBuf.append(Messages.getString("CommunicationsException.4"));
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/* 1230 */       else if (timeSinceLastPacket > 28800L) {
/* 1231 */         dueToTimeout = 2;
/*      */ 
/* 1233 */         timeoutMessageBuf = new StringBuffer();
/*      */ 
/* 1235 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.5"));
/*      */ 
/* 1237 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.6"));
/*      */ 
/* 1239 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.7"));
/*      */ 
/* 1241 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.8"));
/*      */       }
/*      */ 
/* 1245 */       if ((dueToTimeout == 1) || (dueToTimeout == 2))
/*      */       {
/* 1248 */         if (lastPacketReceivedTimeMs != 0L) {
/* 1249 */           Object[] timingInfo = { new Long(timeSinceLastPacketReceivedMs), new Long(timeSinceLastPacketMs) };
/*      */ 
/* 1253 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.ServerPacketTimingInfo", timingInfo));
/*      */         }
/*      */         else
/*      */         {
/* 1257 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.ServerPacketTimingInfoNoRecv", new Object[] { new Long(timeSinceLastPacketMs) }));
/*      */         }
/*      */ 
/* 1262 */         if (timeoutMessageBuf != null) {
/* 1263 */           exceptionMessageBuf.append(timeoutMessageBuf);
/*      */         }
/*      */ 
/* 1266 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.11"));
/*      */ 
/* 1268 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.12"));
/*      */ 
/* 1270 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.13"));
/*      */       }
/* 1279 */       else if ((underlyingException instanceof BindException)) {
/* 1280 */         if ((conn.getLocalSocketAddress() != null) && (!Util.interfaceExists(conn.getLocalSocketAddress())))
/*      */         {
/* 1283 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.LocalSocketAddressNotAvailable"));
/*      */         }
/*      */         else
/*      */         {
/* 1287 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.TooManyClientConnections"));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1294 */     if (exceptionMessageBuf.length() == 0)
/*      */     {
/* 1296 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.20"));
/*      */ 
/* 1299 */       if ((THROWABLE_INIT_CAUSE_METHOD == null) && (underlyingException != null))
/*      */       {
/* 1301 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.21"));
/*      */ 
/* 1303 */         exceptionMessageBuf.append(Util.stackTraceToString(underlyingException));
/*      */       }
/*      */ 
/* 1307 */       if ((conn != null) && (conn.getMaintainTimeStats()) && (!conn.getParanoid()))
/*      */       {
/* 1309 */         exceptionMessageBuf.append("\n\n");
/* 1310 */         if (lastPacketReceivedTimeMs != 0L) {
/* 1311 */           Object[] timingInfo = { new Long(timeSinceLastPacketReceivedMs), new Long(timeSinceLastPacketMs) };
/*      */ 
/* 1315 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.ServerPacketTimingInfo", timingInfo));
/*      */         }
/*      */         else
/*      */         {
/* 1319 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.ServerPacketTimingInfoNoRecv", new Object[] { new Long(timeSinceLastPacketMs) }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1326 */     return exceptionMessageBuf.toString();
/*      */   }
/*      */ 
/*      */   public static SQLException notImplemented() {
/* 1330 */     if (Util.isJdbc4()) {
/*      */       try {
/* 1332 */         return (SQLException)Class.forName("java.sql.SQLFeatureNotSupportedException").newInstance();
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1340 */     return new NotImplemented();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  155 */     if (Util.isJdbc4())
/*      */       try {
/*  157 */         JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = Class.forName("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException").getConstructor(new Class[] { ConnectionImpl.class, Long.TYPE, Long.TYPE, Exception.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  162 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  164 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  166 */         throw new RuntimeException(e);
/*      */       }
/*      */     else {
/*  169 */       JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = null;
/*      */     }
/*      */     try
/*      */     {
/*  173 */       THROWABLE_INIT_CAUSE_METHOD = class$java$lang$Throwable.getMethod("initCause", new Class[] { Throwable.class });
/*      */     }
/*      */     catch (Throwable t) {
/*  176 */       THROWABLE_INIT_CAUSE_METHOD = null;
/*      */     }
/*      */ 
/*  179 */     sqlStateMessages = new HashMap();
/*  180 */     sqlStateMessages.put("01002", Messages.getString("SQLError.35"));
/*      */ 
/*  182 */     sqlStateMessages.put("01004", Messages.getString("SQLError.36"));
/*      */ 
/*  184 */     sqlStateMessages.put("01006", Messages.getString("SQLError.37"));
/*      */ 
/*  186 */     sqlStateMessages.put("01S00", Messages.getString("SQLError.38"));
/*      */ 
/*  188 */     sqlStateMessages.put("01S01", Messages.getString("SQLError.39"));
/*      */ 
/*  190 */     sqlStateMessages.put("01S03", Messages.getString("SQLError.40"));
/*      */ 
/*  192 */     sqlStateMessages.put("01S04", Messages.getString("SQLError.41"));
/*      */ 
/*  194 */     sqlStateMessages.put("07001", Messages.getString("SQLError.42"));
/*      */ 
/*  196 */     sqlStateMessages.put("08001", Messages.getString("SQLError.43"));
/*      */ 
/*  198 */     sqlStateMessages.put("08002", Messages.getString("SQLError.44"));
/*      */ 
/*  200 */     sqlStateMessages.put("08003", Messages.getString("SQLError.45"));
/*      */ 
/*  202 */     sqlStateMessages.put("08004", Messages.getString("SQLError.46"));
/*      */ 
/*  204 */     sqlStateMessages.put("08007", Messages.getString("SQLError.47"));
/*      */ 
/*  206 */     sqlStateMessages.put("08S01", Messages.getString("SQLError.48"));
/*      */ 
/*  208 */     sqlStateMessages.put("21S01", Messages.getString("SQLError.49"));
/*      */ 
/*  210 */     sqlStateMessages.put("22003", Messages.getString("SQLError.50"));
/*      */ 
/*  212 */     sqlStateMessages.put("22008", Messages.getString("SQLError.51"));
/*      */ 
/*  214 */     sqlStateMessages.put("22012", Messages.getString("SQLError.52"));
/*      */ 
/*  216 */     sqlStateMessages.put("41000", Messages.getString("SQLError.53"));
/*      */ 
/*  218 */     sqlStateMessages.put("28000", Messages.getString("SQLError.54"));
/*      */ 
/*  220 */     sqlStateMessages.put("42000", Messages.getString("SQLError.55"));
/*      */ 
/*  222 */     sqlStateMessages.put("42S02", Messages.getString("SQLError.56"));
/*      */ 
/*  224 */     sqlStateMessages.put("S0001", Messages.getString("SQLError.57"));
/*      */ 
/*  226 */     sqlStateMessages.put("S0002", Messages.getString("SQLError.58"));
/*      */ 
/*  228 */     sqlStateMessages.put("S0011", Messages.getString("SQLError.59"));
/*      */ 
/*  230 */     sqlStateMessages.put("S0012", Messages.getString("SQLError.60"));
/*      */ 
/*  232 */     sqlStateMessages.put("S0021", Messages.getString("SQLError.61"));
/*      */ 
/*  234 */     sqlStateMessages.put("S0022", Messages.getString("SQLError.62"));
/*      */ 
/*  236 */     sqlStateMessages.put("S0023", Messages.getString("SQLError.63"));
/*      */ 
/*  238 */     sqlStateMessages.put("S1000", Messages.getString("SQLError.64"));
/*      */ 
/*  240 */     sqlStateMessages.put("S1001", Messages.getString("SQLError.65"));
/*      */ 
/*  242 */     sqlStateMessages.put("S1002", Messages.getString("SQLError.66"));
/*      */ 
/*  244 */     sqlStateMessages.put("S1009", Messages.getString("SQLError.67"));
/*      */ 
/*  246 */     sqlStateMessages.put("S1C00", Messages.getString("SQLError.68"));
/*      */ 
/*  248 */     sqlStateMessages.put("S1T00", Messages.getString("SQLError.69"));
/*      */ 
/*  251 */     mysqlToSqlState = new Hashtable();
/*      */ 
/*  262 */     mysqlToSqlState.put(Constants.integerValueOf(1040), "08004");
/*  263 */     mysqlToSqlState.put(Constants.integerValueOf(1042), "08004");
/*  264 */     mysqlToSqlState.put(Constants.integerValueOf(1043), "08004");
/*  265 */     mysqlToSqlState.put(Constants.integerValueOf(1047), "08S01");
/*      */ 
/*  267 */     mysqlToSqlState.put(Constants.integerValueOf(1081), "08S01");
/*      */ 
/*  272 */     mysqlToSqlState.put(Constants.integerValueOf(1129), "08004");
/*  273 */     mysqlToSqlState.put(Constants.integerValueOf(1130), "08004");
/*      */ 
/*  280 */     mysqlToSqlState.put(Constants.integerValueOf(1045), "28000");
/*      */ 
/*  297 */     mysqlToSqlState.put(Constants.integerValueOf(1037), "S1001");
/*      */ 
/*  299 */     mysqlToSqlState.put(Constants.integerValueOf(1038), "S1001");
/*      */ 
/*  308 */     mysqlToSqlState.put(Constants.integerValueOf(1064), "42000");
/*  309 */     mysqlToSqlState.put(Constants.integerValueOf(1065), "42000");
/*      */ 
/*  336 */     mysqlToSqlState.put(Constants.integerValueOf(1055), "S1009");
/*  337 */     mysqlToSqlState.put(Constants.integerValueOf(1056), "S1009");
/*  338 */     mysqlToSqlState.put(Constants.integerValueOf(1057), "S1009");
/*  339 */     mysqlToSqlState.put(Constants.integerValueOf(1059), "S1009");
/*  340 */     mysqlToSqlState.put(Constants.integerValueOf(1060), "S1009");
/*  341 */     mysqlToSqlState.put(Constants.integerValueOf(1061), "S1009");
/*  342 */     mysqlToSqlState.put(Constants.integerValueOf(1062), "S1009");
/*  343 */     mysqlToSqlState.put(Constants.integerValueOf(1063), "S1009");
/*  344 */     mysqlToSqlState.put(Constants.integerValueOf(1066), "S1009");
/*  345 */     mysqlToSqlState.put(Constants.integerValueOf(1067), "S1009");
/*  346 */     mysqlToSqlState.put(Constants.integerValueOf(1068), "S1009");
/*  347 */     mysqlToSqlState.put(Constants.integerValueOf(1069), "S1009");
/*  348 */     mysqlToSqlState.put(Constants.integerValueOf(1070), "S1009");
/*  349 */     mysqlToSqlState.put(Constants.integerValueOf(1071), "S1009");
/*  350 */     mysqlToSqlState.put(Constants.integerValueOf(1072), "S1009");
/*  351 */     mysqlToSqlState.put(Constants.integerValueOf(1073), "S1009");
/*  352 */     mysqlToSqlState.put(Constants.integerValueOf(1074), "S1009");
/*  353 */     mysqlToSqlState.put(Constants.integerValueOf(1075), "S1009");
/*  354 */     mysqlToSqlState.put(Constants.integerValueOf(1082), "S1009");
/*  355 */     mysqlToSqlState.put(Constants.integerValueOf(1083), "S1009");
/*  356 */     mysqlToSqlState.put(Constants.integerValueOf(1084), "S1009");
/*      */ 
/*  361 */     mysqlToSqlState.put(Constants.integerValueOf(1058), "21S01");
/*      */ 
/*  397 */     mysqlToSqlState.put(Constants.integerValueOf(1051), "42S02");
/*      */ 
/*  402 */     mysqlToSqlState.put(Constants.integerValueOf(1054), "S0022");
/*      */ 
/*  414 */     mysqlToSqlState.put(Constants.integerValueOf(1205), "41000");
/*  415 */     mysqlToSqlState.put(Constants.integerValueOf(1213), "41000");
/*      */ 
/*  417 */     mysqlToSql99State = new HashMap();
/*      */ 
/*  419 */     mysqlToSql99State.put(Constants.integerValueOf(1205), "41000");
/*  420 */     mysqlToSql99State.put(Constants.integerValueOf(1213), "41000");
/*  421 */     mysqlToSql99State.put(Constants.integerValueOf(1022), "23000");
/*      */ 
/*  423 */     mysqlToSql99State.put(Constants.integerValueOf(1037), "HY001");
/*      */ 
/*  425 */     mysqlToSql99State.put(Constants.integerValueOf(1038), "HY001");
/*      */ 
/*  427 */     mysqlToSql99State.put(Constants.integerValueOf(1040), "08004");
/*      */ 
/*  429 */     mysqlToSql99State.put(Constants.integerValueOf(1042), "08S01");
/*      */ 
/*  431 */     mysqlToSql99State.put(Constants.integerValueOf(1043), "08S01");
/*      */ 
/*  433 */     mysqlToSql99State.put(Constants.integerValueOf(1044), "42000");
/*      */ 
/*  435 */     mysqlToSql99State.put(Constants.integerValueOf(1045), "28000");
/*      */ 
/*  437 */     mysqlToSql99State.put(Constants.integerValueOf(1050), "42S01");
/*      */ 
/*  439 */     mysqlToSql99State.put(Constants.integerValueOf(1051), "42S02");
/*      */ 
/*  441 */     mysqlToSql99State.put(Constants.integerValueOf(1052), "23000");
/*      */ 
/*  443 */     mysqlToSql99State.put(Constants.integerValueOf(1053), "08S01");
/*      */ 
/*  445 */     mysqlToSql99State.put(Constants.integerValueOf(1054), "42S22");
/*      */ 
/*  447 */     mysqlToSql99State.put(Constants.integerValueOf(1055), "42000");
/*      */ 
/*  449 */     mysqlToSql99State.put(Constants.integerValueOf(1056), "42000");
/*      */ 
/*  451 */     mysqlToSql99State.put(Constants.integerValueOf(1057), "42000");
/*      */ 
/*  453 */     mysqlToSql99State.put(Constants.integerValueOf(1058), "21S01");
/*      */ 
/*  455 */     mysqlToSql99State.put(Constants.integerValueOf(1059), "42000");
/*      */ 
/*  457 */     mysqlToSql99State.put(Constants.integerValueOf(1060), "42S21");
/*      */ 
/*  459 */     mysqlToSql99State.put(Constants.integerValueOf(1061), "42000");
/*      */ 
/*  461 */     mysqlToSql99State.put(Constants.integerValueOf(1062), "23000");
/*      */ 
/*  463 */     mysqlToSql99State.put(Constants.integerValueOf(1063), "42000");
/*      */ 
/*  465 */     mysqlToSql99State.put(Constants.integerValueOf(1064), "42000");
/*      */ 
/*  467 */     mysqlToSql99State.put(Constants.integerValueOf(1065), "42000");
/*      */ 
/*  469 */     mysqlToSql99State.put(Constants.integerValueOf(1066), "42000");
/*      */ 
/*  471 */     mysqlToSql99State.put(Constants.integerValueOf(1067), "42000");
/*      */ 
/*  473 */     mysqlToSql99State.put(Constants.integerValueOf(1068), "42000");
/*      */ 
/*  475 */     mysqlToSql99State.put(Constants.integerValueOf(1069), "42000");
/*      */ 
/*  477 */     mysqlToSql99State.put(Constants.integerValueOf(1070), "42000");
/*      */ 
/*  479 */     mysqlToSql99State.put(Constants.integerValueOf(1071), "42000");
/*      */ 
/*  481 */     mysqlToSql99State.put(Constants.integerValueOf(1072), "42000");
/*      */ 
/*  483 */     mysqlToSql99State.put(Constants.integerValueOf(1073), "42000");
/*      */ 
/*  485 */     mysqlToSql99State.put(Constants.integerValueOf(1074), "42000");
/*      */ 
/*  487 */     mysqlToSql99State.put(Constants.integerValueOf(1075), "42000");
/*      */ 
/*  489 */     mysqlToSql99State.put(Constants.integerValueOf(1080), "08S01");
/*      */ 
/*  491 */     mysqlToSql99State.put(Constants.integerValueOf(1081), "08S01");
/*      */ 
/*  493 */     mysqlToSql99State.put(Constants.integerValueOf(1082), "42S12");
/*      */ 
/*  495 */     mysqlToSql99State.put(Constants.integerValueOf(1083), "42000");
/*      */ 
/*  497 */     mysqlToSql99State.put(Constants.integerValueOf(1084), "42000");
/*      */ 
/*  499 */     mysqlToSql99State.put(Constants.integerValueOf(1090), "42000");
/*      */ 
/*  501 */     mysqlToSql99State.put(Constants.integerValueOf(1091), "42000");
/*      */ 
/*  503 */     mysqlToSql99State.put(Constants.integerValueOf(1101), "42000");
/*      */ 
/*  505 */     mysqlToSql99State.put(Constants.integerValueOf(1102), "42000");
/*      */ 
/*  507 */     mysqlToSql99State.put(Constants.integerValueOf(1103), "42000");
/*      */ 
/*  509 */     mysqlToSql99State.put(Constants.integerValueOf(1104), "42000");
/*      */ 
/*  511 */     mysqlToSql99State.put(Constants.integerValueOf(1106), "42000");
/*      */ 
/*  513 */     mysqlToSql99State.put(Constants.integerValueOf(1107), "42000");
/*      */ 
/*  515 */     mysqlToSql99State.put(Constants.integerValueOf(1109), "42S02");
/*      */ 
/*  517 */     mysqlToSql99State.put(Constants.integerValueOf(1110), "42000");
/*      */ 
/*  519 */     mysqlToSql99State.put(Constants.integerValueOf(1112), "42000");
/*      */ 
/*  521 */     mysqlToSql99State.put(Constants.integerValueOf(1113), "42000");
/*      */ 
/*  523 */     mysqlToSql99State.put(Constants.integerValueOf(1115), "42000");
/*      */ 
/*  525 */     mysqlToSql99State.put(Constants.integerValueOf(1118), "42000");
/*      */ 
/*  527 */     mysqlToSql99State.put(Constants.integerValueOf(1120), "42000");
/*      */ 
/*  529 */     mysqlToSql99State.put(Constants.integerValueOf(1121), "42000");
/*      */ 
/*  531 */     mysqlToSql99State.put(Constants.integerValueOf(1131), "42000");
/*      */ 
/*  533 */     mysqlToSql99State.put(Constants.integerValueOf(1132), "42000");
/*      */ 
/*  535 */     mysqlToSql99State.put(Constants.integerValueOf(1133), "42000");
/*      */ 
/*  537 */     mysqlToSql99State.put(Constants.integerValueOf(1136), "21S01");
/*      */ 
/*  539 */     mysqlToSql99State.put(Constants.integerValueOf(1138), "42000");
/*      */ 
/*  541 */     mysqlToSql99State.put(Constants.integerValueOf(1139), "42000");
/*      */ 
/*  543 */     mysqlToSql99State.put(Constants.integerValueOf(1140), "42000");
/*      */ 
/*  545 */     mysqlToSql99State.put(Constants.integerValueOf(1141), "42000");
/*      */ 
/*  547 */     mysqlToSql99State.put(Constants.integerValueOf(1142), "42000");
/*      */ 
/*  549 */     mysqlToSql99State.put(Constants.integerValueOf(1143), "42000");
/*      */ 
/*  551 */     mysqlToSql99State.put(Constants.integerValueOf(1144), "42000");
/*      */ 
/*  553 */     mysqlToSql99State.put(Constants.integerValueOf(1145), "42000");
/*      */ 
/*  555 */     mysqlToSql99State.put(Constants.integerValueOf(1146), "42S02");
/*      */ 
/*  557 */     mysqlToSql99State.put(Constants.integerValueOf(1147), "42000");
/*      */ 
/*  559 */     mysqlToSql99State.put(Constants.integerValueOf(1148), "42000");
/*      */ 
/*  561 */     mysqlToSql99State.put(Constants.integerValueOf(1149), "42000");
/*      */ 
/*  563 */     mysqlToSql99State.put(Constants.integerValueOf(1152), "08S01");
/*      */ 
/*  565 */     mysqlToSql99State.put(Constants.integerValueOf(1153), "08S01");
/*      */ 
/*  567 */     mysqlToSql99State.put(Constants.integerValueOf(1154), "08S01");
/*      */ 
/*  569 */     mysqlToSql99State.put(Constants.integerValueOf(1155), "08S01");
/*      */ 
/*  571 */     mysqlToSql99State.put(Constants.integerValueOf(1156), "08S01");
/*      */ 
/*  573 */     mysqlToSql99State.put(Constants.integerValueOf(1157), "08S01");
/*      */ 
/*  575 */     mysqlToSql99State.put(Constants.integerValueOf(1158), "08S01");
/*      */ 
/*  577 */     mysqlToSql99State.put(Constants.integerValueOf(1159), "08S01");
/*      */ 
/*  579 */     mysqlToSql99State.put(Constants.integerValueOf(1160), "08S01");
/*      */ 
/*  581 */     mysqlToSql99State.put(Constants.integerValueOf(1161), "08S01");
/*      */ 
/*  583 */     mysqlToSql99State.put(Constants.integerValueOf(1162), "42000");
/*      */ 
/*  585 */     mysqlToSql99State.put(Constants.integerValueOf(1163), "42000");
/*      */ 
/*  587 */     mysqlToSql99State.put(Constants.integerValueOf(1164), "42000");
/*      */ 
/*  591 */     mysqlToSql99State.put(Constants.integerValueOf(1166), "42000");
/*      */ 
/*  593 */     mysqlToSql99State.put(Constants.integerValueOf(1167), "42000");
/*      */ 
/*  595 */     mysqlToSql99State.put(Constants.integerValueOf(1169), "23000");
/*      */ 
/*  597 */     mysqlToSql99State.put(Constants.integerValueOf(1170), "42000");
/*      */ 
/*  599 */     mysqlToSql99State.put(Constants.integerValueOf(1171), "42000");
/*      */ 
/*  601 */     mysqlToSql99State.put(Constants.integerValueOf(1172), "42000");
/*      */ 
/*  603 */     mysqlToSql99State.put(Constants.integerValueOf(1173), "42000");
/*      */ 
/*  605 */     mysqlToSql99State.put(Constants.integerValueOf(1177), "42000");
/*      */ 
/*  607 */     mysqlToSql99State.put(Constants.integerValueOf(1178), "42000");
/*      */ 
/*  609 */     mysqlToSql99State.put(Constants.integerValueOf(1179), "25000");
/*      */ 
/*  612 */     mysqlToSql99State.put(Constants.integerValueOf(1184), "08S01");
/*      */ 
/*  614 */     mysqlToSql99State.put(Constants.integerValueOf(1189), "08S01");
/*      */ 
/*  616 */     mysqlToSql99State.put(Constants.integerValueOf(1190), "08S01");
/*      */ 
/*  618 */     mysqlToSql99State.put(Constants.integerValueOf(1203), "42000");
/*      */ 
/*  620 */     mysqlToSql99State.put(Constants.integerValueOf(1207), "25000");
/*      */ 
/*  622 */     mysqlToSql99State.put(Constants.integerValueOf(1211), "42000");
/*      */ 
/*  624 */     mysqlToSql99State.put(Constants.integerValueOf(1213), "40001");
/*      */ 
/*  626 */     mysqlToSql99State.put(Constants.integerValueOf(1216), "23000");
/*      */ 
/*  628 */     mysqlToSql99State.put(Constants.integerValueOf(1217), "23000");
/*      */ 
/*  630 */     mysqlToSql99State.put(Constants.integerValueOf(1218), "08S01");
/*      */ 
/*  632 */     mysqlToSql99State.put(Constants.integerValueOf(1222), "21000");
/*      */ 
/*  635 */     mysqlToSql99State.put(Constants.integerValueOf(1226), "42000");
/*      */ 
/*  637 */     mysqlToSql99State.put(Constants.integerValueOf(1230), "42000");
/*      */ 
/*  639 */     mysqlToSql99State.put(Constants.integerValueOf(1231), "42000");
/*      */ 
/*  641 */     mysqlToSql99State.put(Constants.integerValueOf(1232), "42000");
/*      */ 
/*  643 */     mysqlToSql99State.put(Constants.integerValueOf(1234), "42000");
/*      */ 
/*  645 */     mysqlToSql99State.put(Constants.integerValueOf(1235), "42000");
/*      */ 
/*  647 */     mysqlToSql99State.put(Constants.integerValueOf(1239), "42000");
/*      */ 
/*  649 */     mysqlToSql99State.put(Constants.integerValueOf(1241), "21000");
/*      */ 
/*  651 */     mysqlToSql99State.put(Constants.integerValueOf(1242), "21000");
/*      */ 
/*  653 */     mysqlToSql99State.put(Constants.integerValueOf(1247), "42S22");
/*      */ 
/*  655 */     mysqlToSql99State.put(Constants.integerValueOf(1248), "42000");
/*      */ 
/*  657 */     mysqlToSql99State.put(Constants.integerValueOf(1249), "01000");
/*      */ 
/*  659 */     mysqlToSql99State.put(Constants.integerValueOf(1250), "42000");
/*      */ 
/*  661 */     mysqlToSql99State.put(Constants.integerValueOf(1251), "08004");
/*      */ 
/*  663 */     mysqlToSql99State.put(Constants.integerValueOf(1252), "42000");
/*      */ 
/*  665 */     mysqlToSql99State.put(Constants.integerValueOf(1253), "42000");
/*      */ 
/*  667 */     mysqlToSql99State.put(Constants.integerValueOf(1261), "01000");
/*      */ 
/*  669 */     mysqlToSql99State.put(Constants.integerValueOf(1262), "01000");
/*      */ 
/*  671 */     mysqlToSql99State.put(Constants.integerValueOf(1263), "01000");
/*      */ 
/*  673 */     mysqlToSql99State.put(Constants.integerValueOf(1264), "01000");
/*      */ 
/*  675 */     mysqlToSql99State.put(Constants.integerValueOf(1265), "01000");
/*      */ 
/*  677 */     mysqlToSql99State.put(Constants.integerValueOf(1280), "42000");
/*      */ 
/*  679 */     mysqlToSql99State.put(Constants.integerValueOf(1281), "42000");
/*      */ 
/*  681 */     mysqlToSql99State.put(Constants.integerValueOf(1286), "42000");
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.SQLError
 * JD-Core Version:    0.6.0
 */