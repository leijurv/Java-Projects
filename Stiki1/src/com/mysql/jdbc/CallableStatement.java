/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ public class CallableStatement extends PreparedStatement
/*      */   implements java.sql.CallableStatement
/*      */ {
/*      */   protected static final Constructor JDBC_4_CSTMT_2_ARGS_CTOR;
/*      */   protected static final Constructor JDBC_4_CSTMT_4_ARGS_CTOR;
/*      */   private static final int NOT_OUTPUT_PARAMETER_INDICATOR = -2147483648;
/*      */   private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
/*  468 */   private boolean callingStoredFunction = false;
/*      */   private ResultSetInternalMethods functionReturnValueResults;
/*  472 */   private boolean hasOutputParams = false;
/*      */   private ResultSetInternalMethods outputParameterResults;
/*  478 */   protected boolean outputParamWasNull = false;
/*      */   private int[] parameterIndexToRsIndex;
/*      */   protected CallableStatementParamInfo paramInfo;
/*      */   private CallableStatementParam returnValueParam;
/*      */   private int[] placeholderToParameterIndexMap;
/*      */ 
/*      */   private static String mangleParameterName(String origParameterName)
/*      */   {
/*  448 */     if (origParameterName == null) {
/*  449 */       return null;
/*      */     }
/*      */ 
/*  452 */     int offset = 0;
/*      */ 
/*  454 */     if ((origParameterName.length() > 0) && (origParameterName.charAt(0) == '@'))
/*      */     {
/*  456 */       offset = 1;
/*      */     }
/*      */ 
/*  459 */     StringBuffer paramNameBuf = new StringBuffer("@com_mysql_jdbc_outparam_".length() + origParameterName.length());
/*      */ 
/*  462 */     paramNameBuf.append("@com_mysql_jdbc_outparam_");
/*  463 */     paramNameBuf.append(origParameterName.substring(offset));
/*      */ 
/*  465 */     return paramNameBuf.toString();
/*      */   }
/*      */ 
/*      */   public CallableStatement(ConnectionImpl conn, CallableStatementParamInfo paramInfo)
/*      */     throws SQLException
/*      */   {
/*  499 */     super(conn, paramInfo.nativeSql, paramInfo.catalogInUse);
/*      */ 
/*  501 */     this.paramInfo = paramInfo;
/*  502 */     this.callingStoredFunction = this.paramInfo.isFunctionCall;
/*      */ 
/*  504 */     if (this.callingStoredFunction) {
/*  505 */       this.parameterCount += 1;
/*      */     }
/*      */ 
/*  508 */     this.retrieveGeneratedKeys = true;
/*      */   }
/*      */ 
/*      */   protected static CallableStatement getInstance(ConnectionImpl conn, String sql, String catalog, boolean isFunctionCall)
/*      */     throws SQLException
/*      */   {
/*  520 */     if (!Util.isJdbc4()) {
/*  521 */       return new CallableStatement(conn, sql, catalog, isFunctionCall);
/*      */     }
/*      */ 
/*  524 */     return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_4_ARGS_CTOR, new Object[] { conn, sql, catalog, Boolean.valueOf(isFunctionCall) }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected static CallableStatement getInstance(ConnectionImpl conn, CallableStatementParamInfo paramInfo)
/*      */     throws SQLException
/*      */   {
/*  538 */     if (!Util.isJdbc4()) {
/*  539 */       return new CallableStatement(conn, paramInfo);
/*      */     }
/*      */ 
/*  542 */     return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_2_ARGS_CTOR, new Object[] { conn, paramInfo }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private void generateParameterMap()
/*      */     throws SQLException
/*      */   {
/*  550 */     if (this.paramInfo == null) {
/*  551 */       return;
/*      */     }
/*      */ 
/*  558 */     int parameterCountFromMetaData = this.paramInfo.getParameterCount();
/*      */ 
/*  562 */     if (this.callingStoredFunction) {
/*  563 */       parameterCountFromMetaData--;
/*      */     }
/*      */ 
/*  566 */     if ((this.paramInfo != null) && (this.parameterCount != parameterCountFromMetaData))
/*      */     {
/*  568 */       this.placeholderToParameterIndexMap = new int[this.parameterCount];
/*      */ 
/*  570 */       int startPos = this.callingStoredFunction ? StringUtils.indexOfIgnoreCase(this.originalSql, "SELECT") : StringUtils.indexOfIgnoreCase(this.originalSql, "CALL");
/*      */ 
/*  573 */       if (startPos != -1) {
/*  574 */         int parenOpenPos = this.originalSql.indexOf('(', startPos + 4);
/*      */ 
/*  576 */         if (parenOpenPos != -1) {
/*  577 */           int parenClosePos = StringUtils.indexOfIgnoreCaseRespectQuotes(parenOpenPos, this.originalSql, ")", '\'', true);
/*      */ 
/*  580 */           if (parenClosePos != -1) {
/*  581 */             List parsedParameters = StringUtils.split(this.originalSql.substring(parenOpenPos + 1, parenClosePos), ",", "'\"", "'\"", true);
/*      */ 
/*  583 */             int numParsedParameters = parsedParameters.size();
/*      */ 
/*  587 */             if (numParsedParameters != this.parameterCount);
/*  591 */             int placeholderCount = 0;
/*      */ 
/*  593 */             for (int i = 0; i < numParsedParameters; i++)
/*  594 */               if (((String)parsedParameters.get(i)).equals("?"))
/*  595 */                 this.placeholderToParameterIndexMap[(placeholderCount++)] = i;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public CallableStatement(ConnectionImpl conn, String sql, String catalog, boolean isFunctionCall)
/*      */     throws SQLException
/*      */   {
/*  619 */     super(conn, sql, catalog);
/*      */ 
/*  621 */     this.callingStoredFunction = isFunctionCall;
/*      */ 
/*  623 */     if (!this.callingStoredFunction) {
/*  624 */       if (!StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL"))
/*      */       {
/*  626 */         fakeParameterTypes(false);
/*      */       }
/*  628 */       else determineParameterTypes();
/*      */ 
/*  631 */       generateParameterMap();
/*      */     } else {
/*  633 */       determineParameterTypes();
/*  634 */       generateParameterMap();
/*      */ 
/*  636 */       this.parameterCount += 1;
/*      */     }
/*      */ 
/*  639 */     this.retrieveGeneratedKeys = true;
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  648 */     setOutParams();
/*      */ 
/*  650 */     super.addBatch();
/*      */   }
/*      */ 
/*      */   private CallableStatementParam checkIsOutputParam(int paramIndex)
/*      */     throws SQLException
/*      */   {
/*  656 */     if (this.callingStoredFunction) {
/*  657 */       if (paramIndex == 1)
/*      */       {
/*  659 */         if (this.returnValueParam == null) {
/*  660 */           this.returnValueParam = new CallableStatementParam("", 0, false, true, 12, "VARCHAR", 0, 0, 2, 5);
/*      */         }
/*      */ 
/*  666 */         return this.returnValueParam;
/*      */       }
/*      */ 
/*  670 */       paramIndex--;
/*      */     }
/*      */ 
/*  673 */     checkParameterIndexBounds(paramIndex);
/*      */ 
/*  675 */     int localParamIndex = paramIndex - 1;
/*      */ 
/*  677 */     if (this.placeholderToParameterIndexMap != null) {
/*  678 */       localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */     }
/*      */ 
/*  681 */     CallableStatementParam paramDescriptor = this.paramInfo.getParameter(localParamIndex);
/*      */ 
/*  687 */     if (this.connection.getNoAccessToProcedureBodies()) {
/*  688 */       paramDescriptor.isOut = true;
/*  689 */       paramDescriptor.isIn = true;
/*  690 */       paramDescriptor.inOutModifier = 2;
/*  691 */     } else if (!paramDescriptor.isOut) {
/*  692 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.9") + paramIndex + Messages.getString("CallableStatement.10"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  698 */     this.hasOutputParams = true;
/*      */ 
/*  700 */     return paramDescriptor;
/*      */   }
/*      */ 
/*      */   private void checkParameterIndexBounds(int paramIndex)
/*      */     throws SQLException
/*      */   {
/*  711 */     this.paramInfo.checkBounds(paramIndex);
/*      */   }
/*      */ 
/*      */   private void checkStreamability()
/*      */     throws SQLException
/*      */   {
/*  723 */     if ((this.hasOutputParams) && (createStreamingResultSet()))
/*  724 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.14"), "S1C00", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public synchronized void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  730 */     super.clearParameters();
/*      */     try
/*      */     {
/*  733 */       if (this.outputParameterResults != null)
/*  734 */         this.outputParameterResults.close();
/*      */     }
/*      */     finally {
/*  737 */       this.outputParameterResults = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fakeParameterTypes(boolean isReallyProcedure)
/*      */     throws SQLException
/*      */   {
/*  748 */     Field[] fields = new Field[13];
/*      */ 
/*  750 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 0);
/*  751 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 0);
/*  752 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 0);
/*  753 */     fields[3] = new Field("", "COLUMN_NAME", 1, 0);
/*  754 */     fields[4] = new Field("", "COLUMN_TYPE", 1, 0);
/*  755 */     fields[5] = new Field("", "DATA_TYPE", 5, 0);
/*  756 */     fields[6] = new Field("", "TYPE_NAME", 1, 0);
/*  757 */     fields[7] = new Field("", "PRECISION", 4, 0);
/*  758 */     fields[8] = new Field("", "LENGTH", 4, 0);
/*  759 */     fields[9] = new Field("", "SCALE", 5, 0);
/*  760 */     fields[10] = new Field("", "RADIX", 5, 0);
/*  761 */     fields[11] = new Field("", "NULLABLE", 5, 0);
/*  762 */     fields[12] = new Field("", "REMARKS", 1, 0);
/*      */ 
/*  764 */     String procName = isReallyProcedure ? extractProcedureName() : null;
/*      */ 
/*  766 */     byte[] procNameAsBytes = null;
/*      */     try
/*      */     {
/*  769 */       procNameAsBytes = procName == null ? null : procName.getBytes("UTF-8");
/*      */     } catch (UnsupportedEncodingException ueEx) {
/*  771 */       procNameAsBytes = StringUtils.s2b(procName, this.connection);
/*      */     }
/*      */ 
/*  774 */     ArrayList resultRows = new ArrayList();
/*      */ 
/*  776 */     for (int i = 0; i < this.parameterCount; i++) {
/*  777 */       byte[][] row = new byte[13][];
/*  778 */       row[0] = null;
/*  779 */       row[1] = null;
/*  780 */       row[2] = procNameAsBytes;
/*  781 */       row[3] = StringUtils.s2b(String.valueOf(i), this.connection);
/*      */ 
/*  783 */       row[4] = StringUtils.s2b(String.valueOf(1), this.connection);
/*      */ 
/*  787 */       row[5] = StringUtils.s2b(String.valueOf(12), this.connection);
/*      */ 
/*  789 */       row[6] = StringUtils.s2b("VARCHAR", this.connection);
/*  790 */       row[7] = StringUtils.s2b(Integer.toString(65535), this.connection);
/*  791 */       row[8] = StringUtils.s2b(Integer.toString(65535), this.connection);
/*  792 */       row[9] = StringUtils.s2b(Integer.toString(0), this.connection);
/*  793 */       row[10] = StringUtils.s2b(Integer.toString(10), this.connection);
/*      */ 
/*  795 */       row[11] = StringUtils.s2b(Integer.toString(2), this.connection);
/*      */ 
/*  799 */       row[12] = null;
/*      */ 
/*  801 */       resultRows.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */     }
/*      */ 
/*  804 */     ResultSet paramTypesRs = DatabaseMetaData.buildResultSet(fields, resultRows, this.connection);
/*      */ 
/*  807 */     convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */   }
/*      */ 
/*      */   private void determineParameterTypes() throws SQLException {
/*  811 */     if (this.connection.getNoAccessToProcedureBodies()) {
/*  812 */       fakeParameterTypes(true);
/*      */ 
/*  814 */       return;
/*      */     }
/*      */ 
/*  817 */     ResultSet paramTypesRs = null;
/*      */     try
/*      */     {
/*  820 */       String procName = extractProcedureName();
/*      */ 
/*  822 */       java.sql.DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  824 */       boolean useCatalog = false;
/*      */ 
/*  826 */       if (procName.indexOf(".") == -1) {
/*  827 */         useCatalog = true;
/*      */       }
/*      */ 
/*  830 */       paramTypesRs = dbmd.getProcedureColumns((this.connection.versionMeetsMinimum(5, 0, 2)) && (useCatalog) ? this.currentCatalog : null, null, procName, "%");
/*      */ 
/*  835 */       convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */     } finally {
/*  837 */       SQLException sqlExRethrow = null;
/*      */ 
/*  839 */       if (paramTypesRs != null) {
/*      */         try {
/*  841 */           paramTypesRs.close();
/*      */         } catch (SQLException sqlEx) {
/*  843 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/*  846 */         paramTypesRs = null;
/*      */       }
/*      */ 
/*  849 */       if (sqlExRethrow != null)
/*  850 */         throw sqlExRethrow;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void convertGetProcedureColumnsToInternalDescriptors(ResultSet paramTypesRs) throws SQLException
/*      */   {
/*  856 */     if (!this.connection.isRunningOnJDK13()) {
/*  857 */       this.paramInfo = new CallableStatementParamInfoJDBC3(paramTypesRs);
/*      */     }
/*      */     else
/*  860 */       this.paramInfo = new CallableStatementParamInfo(paramTypesRs);
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/*  870 */     boolean returnVal = false;
/*      */ 
/*  872 */     checkClosed();
/*      */ 
/*  874 */     checkStreamability();
/*      */ 
/*  876 */     synchronized (this.connection.getMutex()) {
/*  877 */       setInOutParamsOnServer();
/*  878 */       setOutParams();
/*      */ 
/*  880 */       returnVal = super.execute();
/*      */ 
/*  882 */       if (this.callingStoredFunction) {
/*  883 */         this.functionReturnValueResults = this.results;
/*  884 */         this.functionReturnValueResults.next();
/*  885 */         this.results = null;
/*      */       }
/*      */ 
/*  888 */       retrieveOutParams();
/*      */     }
/*      */ 
/*  891 */     if (!this.callingStoredFunction) {
/*  892 */       return returnVal;
/*      */     }
/*      */ 
/*  896 */     return false;
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/*  905 */     checkClosed();
/*      */ 
/*  907 */     checkStreamability();
/*      */ 
/*  909 */     ResultSet execResults = null;
/*      */ 
/*  911 */     synchronized (this.connection.getMutex()) {
/*  912 */       setInOutParamsOnServer();
/*  913 */       setOutParams();
/*      */ 
/*  915 */       execResults = super.executeQuery();
/*      */ 
/*  917 */       retrieveOutParams();
/*      */     }
/*      */ 
/*  920 */     return execResults;
/*      */   }
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/*  929 */     int returnVal = -1;
/*      */ 
/*  931 */     checkClosed();
/*      */ 
/*  933 */     checkStreamability();
/*      */ 
/*  935 */     if (this.callingStoredFunction) {
/*  936 */       execute();
/*      */ 
/*  938 */       return -1;
/*      */     }
/*      */ 
/*  941 */     synchronized (this.connection.getMutex()) {
/*  942 */       setInOutParamsOnServer();
/*  943 */       setOutParams();
/*      */ 
/*  945 */       returnVal = super.executeUpdate();
/*      */ 
/*  947 */       retrieveOutParams();
/*      */     }
/*      */ 
/*  950 */     return returnVal;
/*      */   }
/*      */ 
/*      */   private String extractProcedureName() throws SQLException {
/*  954 */     String sanitizedSql = StringUtils.stripComments(this.originalSql, "`\"'", "`\"'", true, false, true, true);
/*      */ 
/*  958 */     int endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "CALL ");
/*      */ 
/*  960 */     int offset = 5;
/*      */ 
/*  962 */     if (endCallIndex == -1) {
/*  963 */       endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "SELECT ");
/*      */ 
/*  965 */       offset = 7;
/*      */     }
/*      */ 
/*  968 */     if (endCallIndex != -1) {
/*  969 */       StringBuffer nameBuf = new StringBuffer();
/*      */ 
/*  971 */       String trimmedStatement = sanitizedSql.substring(endCallIndex + offset).trim();
/*      */ 
/*  974 */       int statementLength = trimmedStatement.length();
/*      */ 
/*  976 */       for (int i = 0; i < statementLength; i++) {
/*  977 */         char c = trimmedStatement.charAt(i);
/*      */ 
/*  979 */         if ((Character.isWhitespace(c)) || (c == '(') || (c == '?')) {
/*      */           break;
/*      */         }
/*  982 */         nameBuf.append(c);
/*      */       }
/*      */ 
/*  986 */       return nameBuf.toString();
/*      */     }
/*      */ 
/*  989 */     throw SQLError.createSQLException(Messages.getString("CallableStatement.1"), "S1000", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected String fixParameterName(String paramNameIn)
/*      */     throws SQLException
/*      */   {
/* 1005 */     if ((paramNameIn == null) || (paramNameIn.length() == 0)) {
/* 1006 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.0") + paramNameIn == null ? Messages.getString("CallableStatement.15") : Messages.getString("CallableStatement.16"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1012 */     if (this.connection.getNoAccessToProcedureBodies()) {
/* 1013 */       throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1017 */     return mangleParameterName(paramNameIn);
/*      */   }
/*      */ 
/*      */   public synchronized Array getArray(int i)
/*      */     throws SQLException
/*      */   {
/* 1032 */     ResultSetInternalMethods rs = getOutputParameters(i);
/*      */ 
/* 1034 */     Array retValue = rs.getArray(mapOutputParameterIndexToRsIndex(i));
/*      */ 
/* 1036 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1038 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Array getArray(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1046 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1049 */     Array retValue = rs.getArray(fixParameterName(parameterName));
/*      */ 
/* 1051 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1053 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized BigDecimal getBigDecimal(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1061 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1063 */     BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1066 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1068 */     return retValue;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public synchronized BigDecimal getBigDecimal(int parameterIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1089 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1091 */     BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex), scale);
/*      */ 
/* 1094 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1096 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized BigDecimal getBigDecimal(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1104 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1107 */     BigDecimal retValue = rs.getBigDecimal(fixParameterName(parameterName));
/*      */ 
/* 1109 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1111 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Blob getBlob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1118 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1120 */     Blob retValue = rs.getBlob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1123 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1125 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Blob getBlob(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1132 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1135 */     Blob retValue = rs.getBlob(fixParameterName(parameterName));
/*      */ 
/* 1137 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1139 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized boolean getBoolean(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1147 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1149 */     boolean retValue = rs.getBoolean(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1152 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1154 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized boolean getBoolean(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1162 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1165 */     boolean retValue = rs.getBoolean(fixParameterName(parameterName));
/*      */ 
/* 1167 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1169 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized byte getByte(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1176 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1178 */     byte retValue = rs.getByte(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1181 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1183 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized byte getByte(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1190 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1193 */     byte retValue = rs.getByte(fixParameterName(parameterName));
/*      */ 
/* 1195 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1197 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1204 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1206 */     byte[] retValue = rs.getBytes(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1209 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1211 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized byte[] getBytes(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1219 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1222 */     byte[] retValue = rs.getBytes(fixParameterName(parameterName));
/*      */ 
/* 1224 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1226 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Clob getClob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1233 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1235 */     Clob retValue = rs.getClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1238 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1240 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Clob getClob(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1247 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1250 */     Clob retValue = rs.getClob(fixParameterName(parameterName));
/*      */ 
/* 1252 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1254 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Date getDate(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1261 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1263 */     Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1266 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1268 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Date getDate(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1276 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1278 */     Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/* 1281 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1283 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Date getDate(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1290 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1293 */     Date retValue = rs.getDate(fixParameterName(parameterName));
/*      */ 
/* 1295 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1297 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Date getDate(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1306 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1309 */     Date retValue = rs.getDate(fixParameterName(parameterName), cal);
/*      */ 
/* 1311 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1313 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized double getDouble(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1321 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1323 */     double retValue = rs.getDouble(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1326 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1328 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized double getDouble(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1336 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1339 */     double retValue = rs.getDouble(fixParameterName(parameterName));
/*      */ 
/* 1341 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1343 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized float getFloat(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1350 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1352 */     float retValue = rs.getFloat(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1355 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1357 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized float getFloat(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1365 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1368 */     float retValue = rs.getFloat(fixParameterName(parameterName));
/*      */ 
/* 1370 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1372 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized int getInt(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1379 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1381 */     int retValue = rs.getInt(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1384 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1386 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized int getInt(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1393 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1396 */     int retValue = rs.getInt(fixParameterName(parameterName));
/*      */ 
/* 1398 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1400 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized long getLong(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1407 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1409 */     long retValue = rs.getLong(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1412 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1414 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized long getLong(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1421 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1424 */     long retValue = rs.getLong(fixParameterName(parameterName));
/*      */ 
/* 1426 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1428 */     return retValue;
/*      */   }
/*      */ 
/*      */   protected int getNamedParamIndex(String paramName, boolean forOut) throws SQLException
/*      */   {
/* 1433 */     if (this.connection.getNoAccessToProcedureBodies()) {
/* 1434 */       throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1438 */     if ((paramName == null) || (paramName.length() == 0)) {
/* 1439 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.2"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1443 */     if (this.paramInfo == null) {
/* 1444 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.3") + paramName + Messages.getString("CallableStatement.4"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1449 */     CallableStatementParam namedParamInfo = this.paramInfo.getParameter(paramName);
/*      */ 
/* 1452 */     if ((forOut) && (!namedParamInfo.isOut)) {
/* 1453 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.5") + paramName + Messages.getString("CallableStatement.6"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1460 */     if (this.placeholderToParameterIndexMap == null) {
/* 1461 */       return namedParamInfo.index + 1;
/*      */     }
/*      */ 
/* 1464 */     for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
/* 1465 */       if (this.placeholderToParameterIndexMap[i] == namedParamInfo.index) {
/* 1466 */         return i + 1;
/*      */       }
/*      */     }
/*      */ 
/* 1470 */     throw SQLError.createSQLException("Can't find local placeholder mapping for parameter named \"" + paramName + "\".", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public synchronized Object getObject(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1479 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/*      */ 
/* 1481 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1483 */     Object retVal = rs.getObjectStoredProc(mapOutputParameterIndexToRsIndex(parameterIndex), paramDescriptor.desiredJdbcType);
/*      */ 
/* 1487 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1489 */     return retVal;
/*      */   }
/*      */ 
/*      */   public synchronized Object getObject(int parameterIndex, Map map)
/*      */     throws SQLException
/*      */   {
/* 1497 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1499 */     Object retVal = rs.getObject(mapOutputParameterIndexToRsIndex(parameterIndex), map);
/*      */ 
/* 1502 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1504 */     return retVal;
/*      */   }
/*      */ 
/*      */   public synchronized Object getObject(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1512 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1515 */     Object retValue = rs.getObject(fixParameterName(parameterName));
/*      */ 
/* 1517 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1519 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Object getObject(String parameterName, Map map)
/*      */     throws SQLException
/*      */   {
/* 1528 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1531 */     Object retValue = rs.getObject(fixParameterName(parameterName), map);
/*      */ 
/* 1533 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1535 */     return retValue;
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods getOutputParameters(int paramIndex)
/*      */     throws SQLException
/*      */   {
/* 1549 */     this.outputParamWasNull = false;
/*      */ 
/* 1551 */     if ((paramIndex == 1) && (this.callingStoredFunction) && (this.returnValueParam != null))
/*      */     {
/* 1553 */       return this.functionReturnValueResults;
/*      */     }
/*      */ 
/* 1556 */     if (this.outputParameterResults == null) {
/* 1557 */       if (this.paramInfo.numberOfParameters() == 0) {
/* 1558 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.7"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1562 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.8"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1566 */     return this.outputParameterResults;
/*      */   }
/*      */ 
/*      */   public synchronized ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 1572 */     if (this.placeholderToParameterIndexMap == null) {
/* 1573 */       return (CallableStatementParamInfoJDBC3)this.paramInfo;
/*      */     }
/* 1575 */     return new CallableStatementParamInfoJDBC3(this.paramInfo);
/*      */   }
/*      */ 
/*      */   public synchronized Ref getRef(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1583 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1585 */     Ref retValue = rs.getRef(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1588 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1590 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Ref getRef(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1597 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1600 */     Ref retValue = rs.getRef(fixParameterName(parameterName));
/*      */ 
/* 1602 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1604 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized short getShort(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1611 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1613 */     short retValue = rs.getShort(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1616 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1618 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized short getShort(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1626 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1629 */     short retValue = rs.getShort(fixParameterName(parameterName));
/*      */ 
/* 1631 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1633 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized String getString(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1641 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1643 */     String retValue = rs.getString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1646 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1648 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized String getString(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1656 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1659 */     String retValue = rs.getString(fixParameterName(parameterName));
/*      */ 
/* 1661 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1663 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Time getTime(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1670 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1672 */     Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1675 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1677 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Time getTime(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1685 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1687 */     Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/* 1690 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1692 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Time getTime(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1699 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1702 */     Time retValue = rs.getTime(fixParameterName(parameterName));
/*      */ 
/* 1704 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1706 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Time getTime(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1715 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1718 */     Time retValue = rs.getTime(fixParameterName(parameterName), cal);
/*      */ 
/* 1720 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1722 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Timestamp getTimestamp(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1730 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1732 */     Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1735 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1737 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Timestamp getTimestamp(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1745 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1747 */     Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/* 1750 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1752 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Timestamp getTimestamp(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1760 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1763 */     Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName));
/*      */ 
/* 1765 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1767 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Timestamp getTimestamp(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1776 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1779 */     Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName), cal);
/*      */ 
/* 1782 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1784 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized URL getURL(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1791 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1793 */     URL retValue = rs.getURL(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1796 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1798 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized URL getURL(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1805 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1808 */     URL retValue = rs.getURL(fixParameterName(parameterName));
/*      */ 
/* 1810 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1812 */     return retValue;
/*      */   }
/*      */ 
/*      */   protected int mapOutputParameterIndexToRsIndex(int paramIndex)
/*      */     throws SQLException
/*      */   {
/* 1818 */     if ((this.returnValueParam != null) && (paramIndex == 1)) {
/* 1819 */       return 1;
/*      */     }
/*      */ 
/* 1822 */     checkParameterIndexBounds(paramIndex);
/*      */ 
/* 1824 */     int localParamIndex = paramIndex - 1;
/*      */ 
/* 1826 */     if (this.placeholderToParameterIndexMap != null) {
/* 1827 */       localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */     }
/*      */ 
/* 1830 */     int rsIndex = this.parameterIndexToRsIndex[localParamIndex];
/*      */ 
/* 1832 */     if (rsIndex == -2147483648) {
/* 1833 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.21") + paramIndex + Messages.getString("CallableStatement.22"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1839 */     return rsIndex + 1;
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 1847 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/* 1848 */     paramDescriptor.desiredJdbcType = sqlType;
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 1856 */     registerOutParameter(parameterIndex, sqlType);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 1865 */     checkIsOutputParam(parameterIndex);
/*      */   }
/*      */ 
/*      */   public synchronized void registerOutParameter(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 1874 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 1883 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 1892 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, typeName);
/*      */   }
/*      */ 
/*      */   private void retrieveOutParams()
/*      */     throws SQLException
/*      */   {
/* 1903 */     int numParameters = this.paramInfo.numberOfParameters();
/*      */ 
/* 1905 */     this.parameterIndexToRsIndex = new int[numParameters];
/*      */ 
/* 1907 */     for (int i = 0; i < numParameters; i++) {
/* 1908 */       this.parameterIndexToRsIndex[i] = -2147483648;
/*      */     }
/*      */ 
/* 1911 */     int localParamIndex = 0;
/*      */ 
/* 1913 */     if (numParameters > 0) {
/* 1914 */       StringBuffer outParameterQuery = new StringBuffer("SELECT ");
/*      */ 
/* 1916 */       boolean firstParam = true;
/* 1917 */       boolean hadOutputParams = false;
/*      */ 
/* 1919 */       Iterator paramIter = this.paramInfo.iterator();
/* 1920 */       while (paramIter.hasNext()) {
/* 1921 */         CallableStatementParam retrParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 1924 */         if (retrParamInfo.isOut) {
/* 1925 */           hadOutputParams = true;
/*      */ 
/* 1927 */           this.parameterIndexToRsIndex[retrParamInfo.index] = (localParamIndex++);
/*      */ 
/* 1929 */           String outParameterName = mangleParameterName(retrParamInfo.paramName);
/*      */ 
/* 1931 */           if (!firstParam)
/* 1932 */             outParameterQuery.append(",");
/*      */           else {
/* 1934 */             firstParam = false;
/*      */           }
/*      */ 
/* 1937 */           if (!outParameterName.startsWith("@")) {
/* 1938 */             outParameterQuery.append('@');
/*      */           }
/*      */ 
/* 1941 */           outParameterQuery.append(outParameterName);
/*      */         }
/*      */       }
/*      */ 
/* 1945 */       if (hadOutputParams)
/*      */       {
/* 1948 */         Statement outParameterStmt = null;
/* 1949 */         ResultSet outParamRs = null;
/*      */         try
/*      */         {
/* 1952 */           outParameterStmt = this.connection.createStatement();
/* 1953 */           outParamRs = outParameterStmt.executeQuery(outParameterQuery.toString());
/*      */ 
/* 1955 */           this.outputParameterResults = ((ResultSetInternalMethods)outParamRs).copy();
/*      */ 
/* 1958 */           if (!this.outputParameterResults.next()) {
/* 1959 */             this.outputParameterResults.close();
/* 1960 */             this.outputParameterResults = null;
/*      */           }
/*      */         } finally {
/* 1963 */           if (outParameterStmt != null)
/* 1964 */             outParameterStmt.close();
/*      */         }
/*      */       }
/*      */       else {
/* 1968 */         this.outputParameterResults = null;
/*      */       }
/*      */     } else {
/* 1971 */       this.outputParameterResults = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1981 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(String parameterName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1990 */     setBigDecimal(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1999 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBoolean(String parameterName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 2006 */     setBoolean(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setByte(String parameterName, byte x)
/*      */     throws SQLException
/*      */   {
/* 2013 */     setByte(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBytes(String parameterName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 2020 */     setBytes(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 2029 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   public void setDate(String parameterName, Date x)
/*      */     throws SQLException
/*      */   {
/* 2037 */     setDate(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setDate(String parameterName, Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2046 */     setDate(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setDouble(String parameterName, double x)
/*      */     throws SQLException
/*      */   {
/* 2053 */     setDouble(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setFloat(String parameterName, float x)
/*      */     throws SQLException
/*      */   {
/* 2060 */     setFloat(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   private void setInOutParamsOnServer()
/*      */     throws SQLException
/*      */   {
/* 2067 */     if (this.paramInfo.numParameters > 0) {
/* 2068 */       int parameterIndex = 0;
/*      */ 
/* 2070 */       Iterator paramIter = this.paramInfo.iterator();
/* 2071 */       while (paramIter.hasNext())
/*      */       {
/* 2073 */         CallableStatementParam inParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 2076 */         if ((inParamInfo.isOut) && (inParamInfo.isIn)) {
/* 2077 */           String inOutParameterName = mangleParameterName(inParamInfo.paramName);
/* 2078 */           StringBuffer queryBuf = new StringBuffer(4 + inOutParameterName.length() + 1 + 1);
/*      */ 
/* 2080 */           queryBuf.append("SET ");
/* 2081 */           queryBuf.append(inOutParameterName);
/* 2082 */           queryBuf.append("=?");
/*      */ 
/* 2084 */           PreparedStatement setPstmt = null;
/*      */           try
/*      */           {
/* 2087 */             setPstmt = (PreparedStatement)this.connection.clientPrepareStatement(queryBuf.toString());
/*      */ 
/* 2090 */             byte[] parameterAsBytes = getBytesRepresentation(inParamInfo.index);
/*      */ 
/* 2093 */             if (parameterAsBytes != null) {
/* 2094 */               if ((parameterAsBytes.length > 8) && (parameterAsBytes[0] == 95) && (parameterAsBytes[1] == 98) && (parameterAsBytes[2] == 105) && (parameterAsBytes[3] == 110) && (parameterAsBytes[4] == 97) && (parameterAsBytes[5] == 114) && (parameterAsBytes[6] == 121) && (parameterAsBytes[7] == 39))
/*      */               {
/* 2103 */                 setPstmt.setBytesNoEscapeNoQuotes(1, parameterAsBytes);
/*      */               }
/*      */               else {
/* 2106 */                 int sqlType = inParamInfo.desiredJdbcType;
/*      */ 
/* 2108 */                 switch (sqlType) {
/*      */                 case -7:
/*      */                 case -4:
/*      */                 case -3:
/*      */                 case -2:
/*      */                 case 2000:
/*      */                 case 2004:
/* 2115 */                   setPstmt.setBytes(1, parameterAsBytes);
/* 2116 */                   break;
/*      */                 default:
/* 2120 */                   setPstmt.setBytesNoEscape(1, parameterAsBytes);
/*      */                 }
/*      */               }
/*      */             }
/* 2124 */             else setPstmt.setNull(1, 0);
/*      */ 
/* 2127 */             setPstmt.executeUpdate();
/*      */           } finally {
/* 2129 */             if (setPstmt != null) {
/* 2130 */               setPstmt.close();
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 2135 */         parameterIndex++;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setInt(String parameterName, int x)
/*      */     throws SQLException
/*      */   {
/* 2144 */     setInt(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setLong(String parameterName, long x)
/*      */     throws SQLException
/*      */   {
/* 2151 */     setLong(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 2158 */     setNull(getNamedParamIndex(parameterName, false), sqlType);
/*      */   }
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 2167 */     setNull(getNamedParamIndex(parameterName, false), sqlType, typeName);
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x)
/*      */     throws SQLException
/*      */   {
/* 2175 */     setObject(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/* 2184 */     setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void setOutParams()
/*      */     throws SQLException
/*      */   {
/* 2196 */     if (this.paramInfo.numParameters > 0) {
/* 2197 */       Iterator paramIter = this.paramInfo.iterator();
/* 2198 */       while (paramIter.hasNext()) {
/* 2199 */         CallableStatementParam outParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 2202 */         if ((!this.callingStoredFunction) && (outParamInfo.isOut)) {
/* 2203 */           String outParameterName = mangleParameterName(outParamInfo.paramName);
/*      */           int outParamIndex;
/*      */           int outParamIndex;
/* 2207 */           if (this.placeholderToParameterIndexMap == null)
/* 2208 */             outParamIndex = outParamInfo.index + 1;
/*      */           else {
/* 2210 */             outParamIndex = this.placeholderToParameterIndexMap[(outParamInfo.index - 1)];
/*      */           }
/*      */ 
/* 2213 */           setBytesNoEscapeNoQuotes(outParamIndex, StringUtils.getBytes(outParameterName, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(String parameterName, short x)
/*      */     throws SQLException
/*      */   {
/* 2228 */     setShort(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setString(String parameterName, String x)
/*      */     throws SQLException
/*      */   {
/* 2236 */     setString(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTime(String parameterName, Time x)
/*      */     throws SQLException
/*      */   {
/* 2243 */     setTime(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTime(String parameterName, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2252 */     setTime(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2261 */     setTimestamp(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2270 */     setTimestamp(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setURL(String parameterName, URL val)
/*      */     throws SQLException
/*      */   {
/* 2277 */     setURL(getNamedParamIndex(parameterName, false), val);
/*      */   }
/*      */ 
/*      */   public synchronized boolean wasNull()
/*      */     throws SQLException
/*      */   {
/* 2284 */     return this.outputParamWasNull;
/*      */   }
/*      */ 
/*      */   public int[] executeBatch() throws SQLException {
/* 2288 */     if (this.hasOutputParams) {
/* 2289 */       throw SQLError.createSQLException("Can't call executeBatch() on CallableStatement with OUTPUT parameters", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2293 */     return super.executeBatch();
/*      */   }
/*      */ 
/*      */   protected int getParameterIndexOffset() {
/* 2297 */     if (this.callingStoredFunction) {
/* 2298 */       return -1;
/*      */     }
/*      */ 
/* 2301 */     return super.getParameterIndexOffset();
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/* 2305 */     setAsciiStream(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException
/*      */   {
/* 2310 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x) throws SQLException
/*      */   {
/* 2315 */     setBinaryStream(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException
/*      */   {
/* 2320 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, Blob x) throws SQLException
/*      */   {
/* 2325 */     setBlob(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, InputStream inputStream) throws SQLException
/*      */   {
/* 2330 */     setBlob(getNamedParamIndex(parameterName, false), inputStream);
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException
/*      */   {
/* 2335 */     setBlob(getNamedParamIndex(parameterName, false), inputStream, length);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader) throws SQLException
/*      */   {
/* 2340 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException
/*      */   {
/* 2345 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Clob x) throws SQLException
/*      */   {
/* 2350 */     setClob(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Reader reader) throws SQLException
/*      */   {
/* 2355 */     setClob(getNamedParamIndex(parameterName, false), reader);
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Reader reader, long length) throws SQLException
/*      */   {
/* 2360 */     setClob(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(String parameterName, Reader value) throws SQLException
/*      */   {
/* 2365 */     setNCharacterStream(getNamedParamIndex(parameterName, false), value);
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException
/*      */   {
/* 2370 */     setNCharacterStream(getNamedParamIndex(parameterName, false), value, length);
/*      */   }
/*      */ 
/*      */   private boolean checkReadOnlyProcedure()
/*      */     throws SQLException
/*      */   {
/* 2381 */     if (this.connection.getNoAccessToProcedureBodies()) {
/* 2382 */       return false;
/*      */     }
/*      */ 
/* 2385 */     synchronized (this.paramInfo) {
/* 2386 */       if (this.paramInfo.isReadOnlySafeChecked) {
/* 2387 */         return this.paramInfo.isReadOnlySafeProcedure;
/*      */       }
/*      */ 
/* 2390 */       ResultSet rs = null;
/* 2391 */       PreparedStatement ps = null;
/*      */       try
/*      */       {
/* 2394 */         String procName = extractProcedureName();
/*      */ 
/* 2396 */         String catalog = this.currentCatalog;
/*      */ 
/* 2398 */         if (procName.indexOf(".") != -1) {
/* 2399 */           catalog = procName.substring(0, procName.indexOf("."));
/*      */ 
/* 2401 */           if ((StringUtils.startsWithIgnoreCaseAndWs(catalog, "`")) && (catalog.trim().endsWith("`"))) {
/* 2402 */             catalog = catalog.substring(1, catalog.length() - 1);
/*      */           }
/*      */ 
/* 2405 */           procName = procName.substring(procName.indexOf(".") + 1);
/* 2406 */           procName = new String(StringUtils.stripEnclosure(procName.getBytes(), "`", "`"));
/*      */         }
/*      */ 
/* 2409 */         ps = ((DatabaseMetaData)this.connection.getMetaData()).prepareMetaDataSafeStatement("SELECT SQL_DATA_ACCESS FROM  information_schema.routines  WHERE routine_schema = ?  AND routine_name = ?");
/*      */ 
/* 2416 */         ps.setString(1, catalog);
/* 2417 */         ps.setString(2, procName);
/* 2418 */         rs = ps.executeQuery();
/* 2419 */         if (rs.next()) {
/* 2420 */           String sqlDataAccess = rs.getString(1);
/* 2421 */           if (("READS SQL DATA".equalsIgnoreCase(sqlDataAccess)) || ("NO SQL".equalsIgnoreCase(sqlDataAccess)))
/*      */           {
/* 2423 */             synchronized (this.paramInfo) {
/* 2424 */               this.paramInfo.isReadOnlySafeChecked = true;
/* 2425 */               this.paramInfo.isReadOnlySafeProcedure = true;
/*      */             }
/* 2427 */             ??? = 1; jsr 30; return ???;
/*      */           }
/*      */         }
/*      */       } catch (SQLException e) {
/*      */       }
/*      */       finally {
/* 2433 */         jsr 6; } localObject3 = returnAddress; if (rs != null) {
/* 2434 */         rs.close();
/*      */       }
/* 2436 */       if (ps != null)
/* 2437 */         ps.close(); ret;
/*      */ 
/* 2441 */       this.paramInfo.isReadOnlySafeChecked = false;
/* 2442 */       this.paramInfo.isReadOnlySafeProcedure = false;
/*      */     }
/* 2444 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean checkReadOnlySafeStatement() throws SQLException
/*      */   {
/* 2449 */     return (super.checkReadOnlySafeStatement()) || (checkReadOnlyProcedure());
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   67 */     if (Util.isJdbc4()) {
/*      */       try {
/*   69 */         JDBC_4_CSTMT_2_ARGS_CTOR = Class.forName("com.mysql.jdbc.JDBC4CallableStatement").getConstructor(new Class[] { ConnectionImpl.class, CallableStatementParamInfo.class });
/*      */ 
/*   74 */         JDBC_4_CSTMT_4_ARGS_CTOR = Class.forName("com.mysql.jdbc.JDBC4CallableStatement").getConstructor(new Class[] { ConnectionImpl.class, String.class, String.class, Boolean.TYPE });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*   81 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   83 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   85 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*   88 */       JDBC_4_CSTMT_4_ARGS_CTOR = null;
/*   89 */       JDBC_4_CSTMT_2_ARGS_CTOR = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class CallableStatementParamInfoJDBC3 extends CallableStatement.CallableStatementParamInfo
/*      */     implements ParameterMetaData
/*      */   {
/*      */     CallableStatementParamInfoJDBC3(ResultSet paramTypesRs)
/*      */       throws SQLException
/*      */     {
/*  387 */       super(paramTypesRs);
/*      */     }
/*      */ 
/*      */     public CallableStatementParamInfoJDBC3(CallableStatement.CallableStatementParamInfo paramInfo) {
/*  391 */       super(paramInfo);
/*      */     }
/*      */ 
/*      */     public boolean isWrapperFor(Class iface)
/*      */       throws SQLException
/*      */     {
/*  410 */       CallableStatement.this.checkClosed();
/*      */ 
/*  414 */       return iface.isInstance(this);
/*      */     }
/*      */ 
/*      */     public Object unwrap(Class iface)
/*      */       throws SQLException
/*      */     {
/*      */       try
/*      */       {
/*  435 */         return Util.cast(iface, this); } catch (ClassCastException cce) {
/*      */       }
/*  437 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", CallableStatement.this.getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class CallableStatementParamInfo
/*      */   {
/*      */     String catalogInUse;
/*      */     boolean isFunctionCall;
/*      */     String nativeSql;
/*      */     int numParameters;
/*      */     List parameterList;
/*      */     Map parameterMap;
/*  159 */     boolean isReadOnlySafeProcedure = false;
/*      */ 
/*  164 */     boolean isReadOnlySafeChecked = false;
/*      */ 
/*      */     CallableStatementParamInfo(CallableStatementParamInfo fullParamInfo)
/*      */     {
/*  174 */       this.nativeSql = CallableStatement.this.originalSql;
/*  175 */       this.catalogInUse = CallableStatement.this.currentCatalog;
/*  176 */       this.isFunctionCall = fullParamInfo.isFunctionCall;
/*  177 */       int[] localParameterMap = CallableStatement.this.placeholderToParameterIndexMap;
/*  178 */       int parameterMapLength = localParameterMap.length;
/*      */ 
/*  180 */       this.isReadOnlySafeProcedure = fullParamInfo.isReadOnlySafeProcedure;
/*  181 */       this.isReadOnlySafeChecked = fullParamInfo.isReadOnlySafeChecked;
/*  182 */       this.parameterList = new ArrayList(fullParamInfo.numParameters);
/*  183 */       this.parameterMap = new HashMap(fullParamInfo.numParameters);
/*      */ 
/*  185 */       if (this.isFunctionCall)
/*      */       {
/*  187 */         this.parameterList.add(fullParamInfo.parameterList.get(0));
/*      */       }
/*      */ 
/*  190 */       int offset = this.isFunctionCall ? 1 : 0;
/*      */ 
/*  192 */       for (int i = 0; i < parameterMapLength; i++) {
/*  193 */         if (localParameterMap[i] != 0) {
/*  194 */           CallableStatement.CallableStatementParam param = (CallableStatement.CallableStatementParam)fullParamInfo.parameterList.get(localParameterMap[i] + offset);
/*      */ 
/*  196 */           this.parameterList.add(param);
/*  197 */           this.parameterMap.put(param.paramName, param);
/*      */         }
/*      */       }
/*      */ 
/*  201 */       this.numParameters = this.parameterList.size();
/*      */     }
/*      */ 
/*      */     CallableStatementParamInfo(ResultSet paramTypesRs) throws SQLException
/*      */     {
/*  206 */       boolean hadRows = paramTypesRs.last();
/*      */ 
/*  208 */       this.nativeSql = CallableStatement.this.originalSql;
/*  209 */       this.catalogInUse = CallableStatement.this.currentCatalog;
/*  210 */       this.isFunctionCall = CallableStatement.this.callingStoredFunction;
/*      */ 
/*  212 */       if (hadRows) {
/*  213 */         this.numParameters = paramTypesRs.getRow();
/*      */ 
/*  215 */         this.parameterList = new ArrayList(this.numParameters);
/*  216 */         this.parameterMap = new HashMap(this.numParameters);
/*      */ 
/*  218 */         paramTypesRs.beforeFirst();
/*      */ 
/*  220 */         addParametersFromDBMD(paramTypesRs);
/*      */       } else {
/*  222 */         this.numParameters = 0;
/*      */       }
/*      */ 
/*  225 */       if (this.isFunctionCall)
/*  226 */         this.numParameters += 1;
/*      */     }
/*      */ 
/*      */     private void addParametersFromDBMD(ResultSet paramTypesRs)
/*      */       throws SQLException
/*      */     {
/*  232 */       int i = 0;
/*      */ 
/*  234 */       while (paramTypesRs.next()) {
/*  235 */         String paramName = paramTypesRs.getString(4);
/*  236 */         int inOutModifier = paramTypesRs.getInt(5);
/*      */ 
/*  238 */         boolean isOutParameter = false;
/*  239 */         boolean isInParameter = false;
/*      */ 
/*  241 */         if ((i == 0) && (this.isFunctionCall)) {
/*  242 */           isOutParameter = true;
/*  243 */           isInParameter = false;
/*  244 */         } else if (inOutModifier == 2) {
/*  245 */           isOutParameter = true;
/*  246 */           isInParameter = true;
/*  247 */         } else if (inOutModifier == 1) {
/*  248 */           isOutParameter = false;
/*  249 */           isInParameter = true;
/*  250 */         } else if (inOutModifier == 4) {
/*  251 */           isOutParameter = true;
/*  252 */           isInParameter = false;
/*      */         }
/*      */ 
/*  255 */         int jdbcType = paramTypesRs.getInt(6);
/*  256 */         String typeName = paramTypesRs.getString(7);
/*  257 */         int precision = paramTypesRs.getInt(8);
/*  258 */         int scale = paramTypesRs.getInt(10);
/*  259 */         short nullability = paramTypesRs.getShort(12);
/*      */ 
/*  261 */         CallableStatement.CallableStatementParam paramInfoToAdd = new CallableStatement.CallableStatementParam(CallableStatement.this, paramName, i++, isInParameter, isOutParameter, jdbcType, typeName, precision, scale, nullability, inOutModifier);
/*      */ 
/*  266 */         this.parameterList.add(paramInfoToAdd);
/*  267 */         this.parameterMap.put(paramName, paramInfoToAdd);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void checkBounds(int paramIndex) throws SQLException {
/*  272 */       int localParamIndex = paramIndex - 1;
/*      */ 
/*  274 */       if ((paramIndex < 0) || (localParamIndex >= this.numParameters))
/*  275 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.11") + paramIndex + Messages.getString("CallableStatement.12") + this.numParameters + Messages.getString("CallableStatement.13"), "S1009", CallableStatement.this.getExceptionInterceptor());
/*      */     }
/*      */ 
/*      */     protected Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  288 */       return super.clone();
/*      */     }
/*      */ 
/*      */     CallableStatement.CallableStatementParam getParameter(int index) {
/*  292 */       return (CallableStatement.CallableStatementParam)this.parameterList.get(index);
/*      */     }
/*      */ 
/*      */     CallableStatement.CallableStatementParam getParameter(String name) {
/*  296 */       return (CallableStatement.CallableStatementParam)this.parameterMap.get(name);
/*      */     }
/*      */ 
/*      */     public String getParameterClassName(int arg0) throws SQLException {
/*  300 */       String mysqlTypeName = getParameterTypeName(arg0);
/*      */ 
/*  302 */       boolean isBinaryOrBlob = (StringUtils.indexOfIgnoreCase(mysqlTypeName, "BLOB") != -1) || (StringUtils.indexOfIgnoreCase(mysqlTypeName, "BINARY") != -1);
/*      */ 
/*  305 */       boolean isUnsigned = StringUtils.indexOfIgnoreCase(mysqlTypeName, "UNSIGNED") != -1;
/*      */ 
/*  307 */       int mysqlTypeIfKnown = 0;
/*      */ 
/*  309 */       if (StringUtils.startsWithIgnoreCase(mysqlTypeName, "MEDIUMINT")) {
/*  310 */         mysqlTypeIfKnown = 9;
/*      */       }
/*      */ 
/*  313 */       return ResultSetMetaData.getClassNameForJavaType(getParameterType(arg0), isUnsigned, mysqlTypeIfKnown, isBinaryOrBlob, false);
/*      */     }
/*      */ 
/*      */     public int getParameterCount() throws SQLException
/*      */     {
/*  318 */       if (this.parameterList == null) {
/*  319 */         return 0;
/*      */       }
/*      */ 
/*  322 */       return this.parameterList.size();
/*      */     }
/*      */ 
/*      */     public int getParameterMode(int arg0) throws SQLException {
/*  326 */       checkBounds(arg0);
/*      */ 
/*  328 */       return getParameter(arg0 - 1).inOutModifier;
/*      */     }
/*      */ 
/*      */     public int getParameterType(int arg0) throws SQLException {
/*  332 */       checkBounds(arg0);
/*      */ 
/*  334 */       return getParameter(arg0 - 1).jdbcType;
/*      */     }
/*      */ 
/*      */     public String getParameterTypeName(int arg0) throws SQLException {
/*  338 */       checkBounds(arg0);
/*      */ 
/*  340 */       return getParameter(arg0 - 1).typeName;
/*      */     }
/*      */ 
/*      */     public int getPrecision(int arg0) throws SQLException {
/*  344 */       checkBounds(arg0);
/*      */ 
/*  346 */       return getParameter(arg0 - 1).precision;
/*      */     }
/*      */ 
/*      */     public int getScale(int arg0) throws SQLException {
/*  350 */       checkBounds(arg0);
/*      */ 
/*  352 */       return getParameter(arg0 - 1).scale;
/*      */     }
/*      */ 
/*      */     public int isNullable(int arg0) throws SQLException {
/*  356 */       checkBounds(arg0);
/*      */ 
/*  358 */       return getParameter(arg0 - 1).nullability;
/*      */     }
/*      */ 
/*      */     public boolean isSigned(int arg0) throws SQLException {
/*  362 */       checkBounds(arg0);
/*      */ 
/*  364 */       return false;
/*      */     }
/*      */ 
/*      */     Iterator iterator() {
/*  368 */       return this.parameterList.iterator();
/*      */     }
/*      */ 
/*      */     int numberOfParameters() {
/*  372 */       return this.numParameters;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class CallableStatementParam
/*      */   {
/*      */     int desiredJdbcType;
/*      */     int index;
/*      */     int inOutModifier;
/*      */     boolean isIn;
/*      */     boolean isOut;
/*      */     int jdbcType;
/*      */     short nullability;
/*      */     String paramName;
/*      */     int precision;
/*      */     int scale;
/*      */     String typeName;
/*      */ 
/*      */     CallableStatementParam(String name, int idx, boolean in, boolean out, int jdbcType, String typeName, int precision, int scale, short nullability, int inOutModifier)
/*      */     {
/*  119 */       this.paramName = name;
/*  120 */       this.isIn = in;
/*  121 */       this.isOut = out;
/*  122 */       this.index = idx;
/*      */ 
/*  124 */       this.jdbcType = jdbcType;
/*  125 */       this.typeName = typeName;
/*  126 */       this.precision = precision;
/*  127 */       this.scale = scale;
/*  128 */       this.nullability = nullability;
/*  129 */       this.inOutModifier = inOutModifier;
/*      */     }
/*      */ 
/*      */     protected Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  138 */       return super.clone();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.CallableStatement
 * JD-Core Version:    0.6.0
 */