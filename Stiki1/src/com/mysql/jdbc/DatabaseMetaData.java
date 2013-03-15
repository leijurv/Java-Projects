/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.sql.Connection;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class DatabaseMetaData
/*      */   implements java.sql.DatabaseMetaData
/*      */ {
/*      */   private static String mysqlKeywordsThatArentSQL92;
/*      */   protected static final int MAX_IDENTIFIER_LENGTH = 64;
/*      */   private static final int DEFERRABILITY = 13;
/*      */   private static final int DELETE_RULE = 10;
/*      */   private static final int FK_NAME = 11;
/*      */   private static final int FKCOLUMN_NAME = 7;
/*      */   private static final int FKTABLE_CAT = 4;
/*      */   private static final int FKTABLE_NAME = 6;
/*      */   private static final int FKTABLE_SCHEM = 5;
/*      */   private static final int KEY_SEQ = 8;
/*      */   private static final int PK_NAME = 12;
/*      */   private static final int PKCOLUMN_NAME = 3;
/*      */   private static final int PKTABLE_CAT = 0;
/*      */   private static final int PKTABLE_NAME = 2;
/*      */   private static final int PKTABLE_SCHEM = 1;
/*      */   private static final String SUPPORTS_FK = "SUPPORTS_FK";
/*  462 */   private static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
/*      */ 
/*  464 */   private static final byte[] SYSTEM_TABLE_AS_BYTES = "SYSTEM TABLE".getBytes();
/*      */   private static final int UPDATE_RULE = 9;
/*  468 */   private static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
/*      */   private static final Constructor JDBC_4_DBMD_SHOW_CTOR;
/*      */   private static final Constructor JDBC_4_DBMD_IS_CTOR;
/*      */   protected ConnectionImpl conn;
/*  622 */   protected String database = null;
/*      */ 
/*  625 */   protected String quotedId = null;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */ 
/*      */   protected static DatabaseMetaData getInstance(ConnectionImpl connToSet, String databaseToSet, boolean checkForInfoSchema)
/*      */     throws SQLException
/*      */   {
/*  633 */     if (!Util.isJdbc4()) {
/*  634 */       if ((checkForInfoSchema) && (connToSet != null) && (connToSet.getUseInformationSchema()) && (connToSet.versionMeetsMinimum(5, 0, 7)))
/*      */       {
/*  637 */         return new DatabaseMetaDataUsingInfoSchema(connToSet, databaseToSet);
/*      */       }
/*      */ 
/*  641 */       return new DatabaseMetaData(connToSet, databaseToSet);
/*      */     }
/*      */ 
/*  644 */     if ((checkForInfoSchema) && (connToSet != null) && (connToSet.getUseInformationSchema()) && (connToSet.versionMeetsMinimum(5, 0, 7)))
/*      */     {
/*  648 */       return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_IS_CTOR, new Object[] { connToSet, databaseToSet }, connToSet.getExceptionInterceptor());
/*      */     }
/*      */ 
/*  653 */     return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_SHOW_CTOR, new Object[] { connToSet, databaseToSet }, connToSet.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected DatabaseMetaData(ConnectionImpl connToSet, String databaseToSet)
/*      */   {
/*  666 */     this.conn = connToSet;
/*  667 */     this.database = databaseToSet;
/*  668 */     this.exceptionInterceptor = this.conn.getExceptionInterceptor();
/*      */     try
/*      */     {
/*  671 */       this.quotedId = (this.conn.supportsQuotedIdentifiers() ? getIdentifierQuoteString() : "");
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  677 */       AssertionFailedException.shouldNotHappen(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean allProceduresAreCallable()
/*      */     throws SQLException
/*      */   {
/*  690 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean allTablesAreSelectable()
/*      */     throws SQLException
/*      */   {
/*  701 */     return false;
/*      */   }
/*      */ 
/*      */   private ResultSet buildResultSet(Field[] fields, ArrayList rows) throws SQLException
/*      */   {
/*  706 */     return buildResultSet(fields, rows, this.conn);
/*      */   }
/*      */ 
/*      */   static ResultSet buildResultSet(Field[] fields, ArrayList rows, ConnectionImpl c) throws SQLException
/*      */   {
/*  711 */     int fieldsLength = fields.length;
/*      */ 
/*  713 */     for (int i = 0; i < fieldsLength; i++) {
/*  714 */       int jdbcType = fields[i].getSQLType();
/*      */ 
/*  716 */       switch (jdbcType) {
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/*  720 */         fields[i].setCharacterSet(c.getCharacterSetMetadata());
/*  721 */         break;
/*      */       }
/*      */ 
/*  726 */       fields[i].setConnection(c);
/*  727 */       fields[i].setUseOldNameMetadata(true);
/*      */     }
/*      */ 
/*  730 */     return ResultSetImpl.getInstance(c.getCatalog(), fields, new RowDataStatic(rows), c, null, false);
/*      */   }
/*      */ 
/*      */   private void convertToJdbcFunctionList(String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, Map procedureRowsOrderedByName, int nameIndex, Field[] fields)
/*      */     throws SQLException
/*      */   {
/*  738 */     while (proceduresRs.next()) {
/*  739 */       boolean shouldAdd = true;
/*      */ 
/*  741 */       if (needsClientFiltering) {
/*  742 */         shouldAdd = false;
/*      */ 
/*  744 */         String procDb = proceduresRs.getString(1);
/*      */ 
/*  746 */         if ((db == null) && (procDb == null))
/*  747 */           shouldAdd = true;
/*  748 */         else if ((db != null) && (db.equals(procDb))) {
/*  749 */           shouldAdd = true;
/*      */         }
/*      */       }
/*      */ 
/*  753 */       if (shouldAdd) {
/*  754 */         String functionName = proceduresRs.getString(nameIndex);
/*      */ 
/*  756 */         byte[][] rowData = (byte[][])null;
/*      */ 
/*  758 */         if ((fields != null) && (fields.length == 9))
/*      */         {
/*  760 */           rowData = new byte[9][];
/*  761 */           rowData[0] = (catalog == null ? null : s2b(catalog));
/*  762 */           rowData[1] = null;
/*  763 */           rowData[2] = s2b(functionName);
/*  764 */           rowData[3] = null;
/*  765 */           rowData[4] = null;
/*  766 */           rowData[5] = null;
/*  767 */           rowData[6] = s2b(proceduresRs.getString("comment"));
/*  768 */           rowData[7] = s2b(Integer.toString(2));
/*  769 */           rowData[8] = s2b(functionName);
/*      */         }
/*      */         else {
/*  772 */           rowData = new byte[6][];
/*      */ 
/*  774 */           rowData[0] = (catalog == null ? null : s2b(catalog));
/*  775 */           rowData[1] = null;
/*  776 */           rowData[2] = s2b(functionName);
/*  777 */           rowData[3] = s2b(proceduresRs.getString("comment"));
/*  778 */           rowData[4] = s2b(Integer.toString(getJDBC4FunctionNoTableConstant()));
/*  779 */           rowData[5] = s2b(functionName);
/*      */         }
/*      */ 
/*  782 */         procedureRowsOrderedByName.put(functionName, new ByteArrayRow(rowData, getExceptionInterceptor()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getJDBC4FunctionNoTableConstant() {
/*  788 */     return 0;
/*      */   }
/*      */ 
/*      */   private void convertToJdbcProcedureList(boolean fromSelect, String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, Map procedureRowsOrderedByName, int nameIndex)
/*      */     throws SQLException
/*      */   {
/*  794 */     while (proceduresRs.next()) {
/*  795 */       boolean shouldAdd = true;
/*      */ 
/*  797 */       if (needsClientFiltering) {
/*  798 */         shouldAdd = false;
/*      */ 
/*  800 */         String procDb = proceduresRs.getString(1);
/*      */ 
/*  802 */         if ((db == null) && (procDb == null))
/*  803 */           shouldAdd = true;
/*  804 */         else if ((db != null) && (db.equals(procDb))) {
/*  805 */           shouldAdd = true;
/*      */         }
/*      */       }
/*      */ 
/*  809 */       if (shouldAdd) {
/*  810 */         String procedureName = proceduresRs.getString(nameIndex);
/*  811 */         byte[][] rowData = new byte[9][];
/*  812 */         rowData[0] = (catalog == null ? null : s2b(catalog));
/*  813 */         rowData[1] = null;
/*  814 */         rowData[2] = s2b(procedureName);
/*  815 */         rowData[3] = null;
/*  816 */         rowData[4] = null;
/*  817 */         rowData[5] = null;
/*  818 */         rowData[6] = null;
/*      */ 
/*  820 */         boolean isFunction = fromSelect ? "FUNCTION".equalsIgnoreCase(proceduresRs.getString("type")) : false;
/*      */ 
/*  823 */         rowData[7] = s2b(isFunction ? Integer.toString(2) : Integer.toString(0));
/*      */ 
/*  827 */         rowData[8] = s2b(procedureName);
/*      */ 
/*  829 */         procedureRowsOrderedByName.put(procedureName, new ByteArrayRow(rowData, getExceptionInterceptor()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private ResultSetRow convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc, boolean forGetFunctionColumns, int ordinal)
/*      */     throws SQLException
/*      */   {
/*  840 */     byte[][] row = forGetFunctionColumns ? new byte[17][] : new byte[14][];
/*  841 */     row[0] = null;
/*  842 */     row[1] = null;
/*  843 */     row[2] = procNameAsBytes;
/*  844 */     row[3] = s2b(paramName);
/*      */ 
/*  851 */     if ((isInParam) && (isOutParam))
/*  852 */       row[4] = s2b(String.valueOf(2));
/*  853 */     else if (isInParam)
/*  854 */       row[4] = s2b(String.valueOf(1));
/*  855 */     else if (isOutParam)
/*  856 */       row[4] = s2b(String.valueOf(4));
/*  857 */     else if (isReturnParam)
/*  858 */       row[4] = s2b(String.valueOf(5));
/*      */     else {
/*  860 */       row[4] = s2b(String.valueOf(0));
/*      */     }
/*  862 */     row[5] = s2b(Short.toString(typeDesc.dataType));
/*  863 */     row[6] = s2b(typeDesc.typeName);
/*  864 */     row[7] = (typeDesc.columnSize == null ? null : s2b(typeDesc.columnSize.toString()));
/*  865 */     row[8] = row[7];
/*  866 */     row[9] = (typeDesc.decimalDigits == null ? null : s2b(typeDesc.decimalDigits.toString()));
/*  867 */     row[10] = s2b(Integer.toString(typeDesc.numPrecRadix));
/*      */ 
/*  869 */     switch (typeDesc.nullability) {
/*      */     case 0:
/*  871 */       row[11] = s2b(String.valueOf(0));
/*      */ 
/*  873 */       break;
/*      */     case 1:
/*  876 */       row[11] = s2b(String.valueOf(1));
/*      */ 
/*  878 */       break;
/*      */     case 2:
/*  881 */       row[11] = s2b(String.valueOf(2));
/*      */ 
/*  883 */       break;
/*      */     default:
/*  886 */       throw SQLError.createSQLException("Internal error while parsing callable statement metadata (unknown nullability value fount)", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  891 */     row[12] = null;
/*      */ 
/*  893 */     if (forGetFunctionColumns)
/*      */     {
/*  895 */       row[13] = null;
/*      */ 
/*  898 */       row[14] = s2b(String.valueOf(ordinal));
/*      */ 
/*  901 */       row[15] = Constants.EMPTY_BYTE_ARRAY;
/*      */ 
/*  903 */       row[16] = s2b(paramName);
/*      */     }
/*      */ 
/*  906 */     return new ByteArrayRow(row, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/*  912 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   public boolean dataDefinitionCausesTransactionCommit()
/*      */     throws SQLException
/*      */   {
/*  924 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean dataDefinitionIgnoredInTransactions()
/*      */     throws SQLException
/*      */   {
/*  935 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean deletesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/*  950 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean doesMaxRowSizeIncludeBlobs()
/*      */     throws SQLException
/*      */   {
/*  963 */     return true;
/*      */   }
/*      */ 
/*      */   public List extractForeignKeyForTable(ArrayList rows, ResultSet rs, String catalog)
/*      */     throws SQLException
/*      */   {
/*  981 */     byte[][] row = new byte[3][];
/*  982 */     row[0] = rs.getBytes(1);
/*  983 */     row[1] = s2b("SUPPORTS_FK");
/*      */ 
/*  985 */     String createTableString = rs.getString(2);
/*  986 */     StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
/*      */ 
/*  988 */     StringBuffer commentBuf = new StringBuffer("comment; ");
/*  989 */     boolean firstTime = true;
/*      */ 
/*  991 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/*  993 */     if (quoteChar == null) {
/*  994 */       quoteChar = "`";
/*      */     }
/*      */ 
/*  997 */     while (lineTokenizer.hasMoreTokens()) {
/*  998 */       String line = lineTokenizer.nextToken().trim();
/*      */ 
/* 1000 */       String constraintName = null;
/*      */ 
/* 1002 */       if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
/* 1003 */         boolean usingBackTicks = true;
/* 1004 */         int beginPos = line.indexOf(quoteChar);
/*      */ 
/* 1006 */         if (beginPos == -1) {
/* 1007 */           beginPos = line.indexOf("\"");
/* 1008 */           usingBackTicks = false;
/*      */         }
/*      */ 
/* 1011 */         if (beginPos != -1) {
/* 1012 */           int endPos = -1;
/*      */ 
/* 1014 */           if (usingBackTicks)
/* 1015 */             endPos = line.indexOf(quoteChar, beginPos + 1);
/*      */           else {
/* 1017 */             endPos = line.indexOf("\"", beginPos + 1);
/*      */           }
/*      */ 
/* 1020 */           if (endPos != -1) {
/* 1021 */             constraintName = line.substring(beginPos + 1, endPos);
/* 1022 */             line = line.substring(endPos + 1, line.length()).trim();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1028 */       if (line.startsWith("FOREIGN KEY")) {
/* 1029 */         if (line.endsWith(",")) {
/* 1030 */           line = line.substring(0, line.length() - 1);
/*      */         }
/*      */ 
/* 1033 */         char quote = this.quotedId.charAt(0);
/*      */ 
/* 1035 */         int indexOfFK = line.indexOf("FOREIGN KEY");
/*      */ 
/* 1037 */         String localColumnName = null;
/* 1038 */         String referencedCatalogName = this.quotedId + catalog + this.quotedId;
/* 1039 */         String referencedTableName = null;
/* 1040 */         String referencedColumnName = null;
/*      */ 
/* 1043 */         if (indexOfFK != -1) {
/* 1044 */           int afterFk = indexOfFK + "FOREIGN KEY".length();
/*      */ 
/* 1046 */           int indexOfRef = StringUtils.indexOfIgnoreCaseRespectQuotes(afterFk, line, "REFERENCES", quote, true);
/*      */ 
/* 1048 */           if (indexOfRef != -1)
/*      */           {
/* 1050 */             int indexOfParenOpen = line.indexOf('(', afterFk);
/* 1051 */             int indexOfParenClose = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfParenOpen, line, ")", quote, true);
/*      */ 
/* 1053 */             if ((indexOfParenOpen != -1) && (indexOfParenClose == -1));
/* 1057 */             localColumnName = line.substring(indexOfParenOpen + 1, indexOfParenClose);
/*      */ 
/* 1059 */             int afterRef = indexOfRef + "REFERENCES".length();
/*      */ 
/* 1061 */             int referencedColumnBegin = StringUtils.indexOfIgnoreCaseRespectQuotes(afterRef, line, "(", quote, true);
/*      */ 
/* 1063 */             if (referencedColumnBegin != -1) {
/* 1064 */               referencedTableName = line.substring(afterRef, referencedColumnBegin);
/*      */ 
/* 1066 */               int referencedColumnEnd = StringUtils.indexOfIgnoreCaseRespectQuotes(referencedColumnBegin + 1, line, ")", quote, true);
/*      */ 
/* 1068 */               if (referencedColumnEnd != -1) {
/* 1069 */                 referencedColumnName = line.substring(referencedColumnBegin + 1, referencedColumnEnd);
/*      */               }
/*      */ 
/* 1072 */               int indexOfCatalogSep = StringUtils.indexOfIgnoreCaseRespectQuotes(0, referencedTableName, ".", quote, true);
/*      */ 
/* 1074 */               if (indexOfCatalogSep != -1) {
/* 1075 */                 referencedCatalogName = referencedTableName.substring(0, indexOfCatalogSep);
/* 1076 */                 referencedTableName = referencedTableName.substring(indexOfCatalogSep + 1);
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1083 */         if (!firstTime)
/* 1084 */           commentBuf.append("; ");
/*      */         else {
/* 1086 */           firstTime = false;
/*      */         }
/*      */ 
/* 1089 */         if (constraintName != null)
/* 1090 */           commentBuf.append(constraintName);
/*      */         else {
/* 1092 */           commentBuf.append("not_available");
/*      */         }
/*      */ 
/* 1095 */         commentBuf.append("(");
/* 1096 */         commentBuf.append(localColumnName);
/* 1097 */         commentBuf.append(") REFER ");
/* 1098 */         commentBuf.append(referencedCatalogName);
/* 1099 */         commentBuf.append("/");
/* 1100 */         commentBuf.append(referencedTableName);
/* 1101 */         commentBuf.append("(");
/* 1102 */         commentBuf.append(referencedColumnName);
/* 1103 */         commentBuf.append(")");
/*      */ 
/* 1105 */         int lastParenIndex = line.lastIndexOf(")");
/*      */ 
/* 1107 */         if (lastParenIndex != line.length() - 1) {
/* 1108 */           String cascadeOptions = line.substring(lastParenIndex + 1);
/*      */ 
/* 1110 */           commentBuf.append(" ");
/* 1111 */           commentBuf.append(cascadeOptions);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1116 */     row[2] = s2b(commentBuf.toString());
/* 1117 */     rows.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */ 
/* 1119 */     return rows;
/*      */   }
/*      */ 
/*      */   public ResultSet extractForeignKeyFromCreateTable(String catalog, String tableName)
/*      */     throws SQLException
/*      */   {
/* 1140 */     ArrayList tableList = new ArrayList();
/* 1141 */     ResultSet rs = null;
/* 1142 */     Statement stmt = null;
/*      */ 
/* 1144 */     if (tableName != null)
/* 1145 */       tableList.add(tableName);
/*      */     else {
/*      */       try {
/* 1148 */         rs = getTables(catalog, "", "%", new String[] { "TABLE" });
/*      */ 
/* 1150 */         while (rs.next())
/* 1151 */           tableList.add(rs.getString("TABLE_NAME"));
/*      */       }
/*      */       finally {
/* 1154 */         if (rs != null) {
/* 1155 */           rs.close();
/*      */         }
/*      */ 
/* 1158 */         rs = null;
/*      */       }
/*      */     }
/*      */ 
/* 1162 */     ArrayList rows = new ArrayList();
/* 1163 */     Field[] fields = new Field[3];
/* 1164 */     fields[0] = new Field("", "Name", 1, 2147483647);
/* 1165 */     fields[1] = new Field("", "Type", 1, 255);
/* 1166 */     fields[2] = new Field("", "Comment", 1, 2147483647);
/*      */ 
/* 1168 */     int numTables = tableList.size();
/* 1169 */     stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/* 1171 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/* 1173 */     if (quoteChar == null) {
/* 1174 */       quoteChar = "`";
/*      */     }
/*      */     try
/*      */     {
/* 1178 */       for (int i = 0; i < numTables; i++) {
/* 1179 */         String tableToExtract = (String)tableList.get(i);
/*      */ 
/* 1181 */         String query = "SHOW CREATE TABLE " + quoteChar + catalog + quoteChar + "." + quoteChar + tableToExtract + quoteChar;
/*      */         try
/*      */         {
/* 1187 */           rs = stmt.executeQuery(query);
/*      */         }
/*      */         catch (SQLException sqlEx) {
/* 1190 */           String sqlState = sqlEx.getSQLState();
/*      */ 
/* 1192 */           if ((!"42S02".equals(sqlState)) && (sqlEx.getErrorCode() != 1146))
/*      */           {
/* 1194 */             throw sqlEx;
/*      */           }
/*      */ 
/* 1197 */           continue;
/*      */         }
/*      */ 
/* 1200 */         while (rs.next())
/* 1201 */           extractForeignKeyForTable(rows, rs, catalog);
/*      */       }
/*      */     }
/*      */     finally {
/* 1205 */       if (rs != null) {
/* 1206 */         rs.close();
/*      */       }
/*      */ 
/* 1209 */       rs = null;
/*      */ 
/* 1211 */       if (stmt != null) {
/* 1212 */         stmt.close();
/*      */       }
/*      */ 
/* 1215 */       stmt = null;
/*      */     }
/*      */ 
/* 1218 */     return buildResultSet(fields, rows);
/*      */   }
/*      */ 
/*      */   public ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3)
/*      */     throws SQLException
/*      */   {
/* 1226 */     Field[] fields = new Field[21];
/* 1227 */     fields[0] = new Field("", "TYPE_CAT", 1, 32);
/* 1228 */     fields[1] = new Field("", "TYPE_SCHEM", 1, 32);
/* 1229 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 1230 */     fields[3] = new Field("", "ATTR_NAME", 1, 32);
/* 1231 */     fields[4] = new Field("", "DATA_TYPE", 5, 32);
/* 1232 */     fields[5] = new Field("", "ATTR_TYPE_NAME", 1, 32);
/* 1233 */     fields[6] = new Field("", "ATTR_SIZE", 4, 32);
/* 1234 */     fields[7] = new Field("", "DECIMAL_DIGITS", 4, 32);
/* 1235 */     fields[8] = new Field("", "NUM_PREC_RADIX", 4, 32);
/* 1236 */     fields[9] = new Field("", "NULLABLE ", 4, 32);
/* 1237 */     fields[10] = new Field("", "REMARKS", 1, 32);
/* 1238 */     fields[11] = new Field("", "ATTR_DEF", 1, 32);
/* 1239 */     fields[12] = new Field("", "SQL_DATA_TYPE", 4, 32);
/* 1240 */     fields[13] = new Field("", "SQL_DATETIME_SUB", 4, 32);
/* 1241 */     fields[14] = new Field("", "CHAR_OCTET_LENGTH", 4, 32);
/* 1242 */     fields[15] = new Field("", "ORDINAL_POSITION", 4, 32);
/* 1243 */     fields[16] = new Field("", "IS_NULLABLE", 1, 32);
/* 1244 */     fields[17] = new Field("", "SCOPE_CATALOG", 1, 32);
/* 1245 */     fields[18] = new Field("", "SCOPE_SCHEMA", 1, 32);
/* 1246 */     fields[19] = new Field("", "SCOPE_TABLE", 1, 32);
/* 1247 */     fields[20] = new Field("", "SOURCE_DATA_TYPE", 5, 32);
/*      */ 
/* 1249 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
/*      */     throws SQLException
/*      */   {
/* 1300 */     if (table == null) {
/* 1301 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1305 */     Field[] fields = new Field[8];
/* 1306 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 1307 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 1308 */     fields[2] = new Field("", "DATA_TYPE", 4, 32);
/* 1309 */     fields[3] = new Field("", "TYPE_NAME", 1, 32);
/* 1310 */     fields[4] = new Field("", "COLUMN_SIZE", 4, 10);
/* 1311 */     fields[5] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 1312 */     fields[6] = new Field("", "DECIMAL_DIGITS", 5, 10);
/* 1313 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */ 
/* 1315 */     ArrayList rows = new ArrayList();
/* 1316 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 1320 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) { private final String val$table;
/*      */         private final Statement val$stmt;
/*      */         private final ArrayList val$rows;
/*      */ 
/* 1322 */         void forEach(Object catalogStr) throws SQLException { ResultSet results = null;
/*      */           try
/*      */           {
/* 1325 */             StringBuffer queryBuf = new StringBuffer("SHOW COLUMNS FROM ");
/*      */ 
/* 1327 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1328 */             queryBuf.append(this.val$table);
/* 1329 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1330 */             queryBuf.append(" FROM ");
/* 1331 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1332 */             queryBuf.append(catalogStr.toString());
/* 1333 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 1335 */             results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 1337 */             while (results.next()) {
/* 1338 */               String keyType = results.getString("Key");
/*      */ 
/* 1340 */               if ((keyType != null) && 
/* 1341 */                 (StringUtils.startsWithIgnoreCase(keyType, "PRI")))
/*      */               {
/* 1343 */                 byte[][] rowVal = new byte[8][];
/* 1344 */                 rowVal[0] = Integer.toString(2).getBytes();
/*      */ 
/* 1348 */                 rowVal[1] = results.getBytes("Field");
/*      */ 
/* 1350 */                 String type = results.getString("Type");
/* 1351 */                 int size = MysqlIO.getMaxBuf();
/* 1352 */                 int decimals = 0;
/*      */ 
/* 1357 */                 if (type.indexOf("enum") != -1) {
/* 1358 */                   String temp = type.substring(type.indexOf("("), type.indexOf(")"));
/*      */ 
/* 1361 */                   StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */ 
/* 1363 */                   int maxLength = 0;
/*      */ 
/* 1365 */                   while (tokenizer.hasMoreTokens()) {
/* 1366 */                     maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */                   }
/*      */ 
/* 1371 */                   size = maxLength;
/* 1372 */                   decimals = 0;
/* 1373 */                   type = "enum";
/* 1374 */                 } else if (type.indexOf("(") != -1) {
/* 1375 */                   if (type.indexOf(",") != -1) {
/* 1376 */                     size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(",")));
/*      */ 
/* 1380 */                     decimals = Integer.parseInt(type.substring(type.indexOf(",") + 1, type.indexOf(")")));
/*      */                   }
/*      */                   else
/*      */                   {
/* 1385 */                     size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(")")));
/*      */                   }
/*      */ 
/* 1391 */                   type = type.substring(0, type.indexOf("("));
/*      */                 }
/*      */ 
/* 1395 */                 rowVal[2] = DatabaseMetaData.this.s2b(String.valueOf(MysqlDefs.mysqlToJavaType(type)));
/*      */ 
/* 1397 */                 rowVal[3] = DatabaseMetaData.this.s2b(type);
/* 1398 */                 rowVal[4] = Integer.toString(size + decimals).getBytes();
/*      */ 
/* 1400 */                 rowVal[5] = Integer.toString(size + decimals).getBytes();
/*      */ 
/* 1402 */                 rowVal[6] = Integer.toString(decimals).getBytes();
/*      */ 
/* 1404 */                 rowVal[7] = Integer.toString(1).getBytes();
/*      */ 
/* 1409 */                 this.val$rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (SQLException sqlEx) {
/* 1414 */             if (!"42S02".equals(sqlEx.getSQLState()))
/* 1415 */               throw sqlEx;
/*      */           }
/*      */           finally {
/* 1418 */             if (results != null) {
/*      */               try {
/* 1420 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 1425 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1320 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 1431 */       if (stmt != null) {
/* 1432 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 1436 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 1438 */     return results;
/*      */   }
/*      */ 
/*      */   private void getCallStmtParameterTypes(String catalog, String procName, String parameterNamePattern, List resultRows)
/*      */     throws SQLException
/*      */   {
/* 1476 */     getCallStmtParameterTypes(catalog, procName, parameterNamePattern, resultRows, false);
/*      */   }
/*      */ 
/*      */   private void getCallStmtParameterTypes(String catalog, String procName, String parameterNamePattern, List resultRows, boolean forGetFunctionColumns)
/*      */     throws SQLException
/*      */   {
/* 1483 */     Statement paramRetrievalStmt = null;
/* 1484 */     ResultSet paramRetrievalRs = null;
/*      */ 
/* 1486 */     if (parameterNamePattern == null) {
/* 1487 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1488 */         parameterNamePattern = "%";
/*      */       else {
/* 1490 */         throw SQLError.createSQLException("Parameter/Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1496 */     byte[] procNameAsBytes = null;
/*      */     try
/*      */     {
/* 1499 */       procNameAsBytes = procName.getBytes("UTF-8");
/*      */     } catch (UnsupportedEncodingException ueEx) {
/* 1501 */       procNameAsBytes = s2b(procName);
/*      */     }
/*      */ 
/* 1506 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/* 1508 */     String parameterDef = null;
/*      */ 
/* 1510 */     boolean isProcedureInAnsiMode = false;
/* 1511 */     String storageDefnDelims = null;
/* 1512 */     String storageDefnClosures = null;
/*      */     try
/*      */     {
/* 1515 */       paramRetrievalStmt = this.conn.getMetadataSafeStatement();
/*      */ 
/* 1517 */       if ((this.conn.lowerCaseTableNames()) && (catalog != null) && (catalog.length() != 0))
/*      */       {
/* 1523 */         String oldCatalog = this.conn.getCatalog();
/* 1524 */         ResultSet rs = null;
/*      */         try
/*      */         {
/* 1527 */           this.conn.setCatalog(catalog);
/* 1528 */           rs = paramRetrievalStmt.executeQuery("SELECT DATABASE()");
/* 1529 */           rs.next();
/*      */ 
/* 1531 */           catalog = rs.getString(1);
/*      */         }
/*      */         finally
/*      */         {
/* 1535 */           this.conn.setCatalog(oldCatalog);
/*      */ 
/* 1537 */           if (rs != null) {
/* 1538 */             rs.close();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1543 */       if (paramRetrievalStmt.getMaxRows() != 0) {
/* 1544 */         paramRetrievalStmt.setMaxRows(0);
/*      */       }
/*      */ 
/* 1547 */       int dotIndex = -1;
/*      */ 
/* 1549 */       if (!" ".equals(quoteChar)) {
/* 1550 */         dotIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procName, ".", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */       }
/*      */       else
/*      */       {
/* 1554 */         dotIndex = procName.indexOf(".");
/*      */       }
/*      */ 
/* 1557 */       String dbName = null;
/*      */ 
/* 1559 */       if ((dotIndex != -1) && (dotIndex + 1 < procName.length())) {
/* 1560 */         dbName = procName.substring(0, dotIndex);
/* 1561 */         procName = procName.substring(dotIndex + 1);
/*      */       } else {
/* 1563 */         dbName = catalog;
/*      */       }
/*      */ 
/* 1566 */       StringBuffer procNameBuf = new StringBuffer();
/*      */ 
/* 1568 */       if (dbName != null) {
/* 1569 */         if ((!" ".equals(quoteChar)) && (!dbName.startsWith(quoteChar))) {
/* 1570 */           procNameBuf.append(quoteChar);
/*      */         }
/*      */ 
/* 1573 */         procNameBuf.append(dbName);
/*      */ 
/* 1575 */         if ((!" ".equals(quoteChar)) && (!dbName.startsWith(quoteChar))) {
/* 1576 */           procNameBuf.append(quoteChar);
/*      */         }
/*      */ 
/* 1579 */         procNameBuf.append(".");
/*      */       }
/*      */ 
/* 1582 */       boolean procNameIsNotQuoted = !procName.startsWith(quoteChar);
/*      */ 
/* 1584 */       if ((!" ".equals(quoteChar)) && (procNameIsNotQuoted)) {
/* 1585 */         procNameBuf.append(quoteChar);
/*      */       }
/*      */ 
/* 1588 */       procNameBuf.append(procName);
/*      */ 
/* 1590 */       if ((!" ".equals(quoteChar)) && (procNameIsNotQuoted)) {
/* 1591 */         procNameBuf.append(quoteChar);
/*      */       }
/*      */ 
/* 1594 */       boolean parsingFunction = false;
/*      */       try
/*      */       {
/* 1597 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE PROCEDURE " + procNameBuf.toString());
/*      */ 
/* 1600 */         parsingFunction = false;
/*      */       } catch (SQLException sqlEx) {
/* 1602 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE FUNCTION " + procNameBuf.toString());
/*      */ 
/* 1605 */         parsingFunction = true;
/*      */       }
/*      */ 
/* 1608 */       if (paramRetrievalRs.next()) {
/* 1609 */         String procedureDef = parsingFunction ? paramRetrievalRs.getString("Create Function") : paramRetrievalRs.getString("Create Procedure");
/*      */ 
/* 1613 */         if ((procedureDef == null) || (procedureDef.length() == 0)) {
/* 1614 */           throw SQLError.createSQLException("User does not have access to metadata required to determine stored procedure parameter types. If rights can not be granted, configure connection with \"noAccessToProcedureBodies=true\" to have driver generate parameters that represent INOUT strings irregardless of actual parameter types.", "S1000", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1621 */           String sqlMode = paramRetrievalRs.getString("sql_mode");
/*      */ 
/* 1623 */           if (StringUtils.indexOfIgnoreCase(sqlMode, "ANSI") != -1) {
/* 1624 */             isProcedureInAnsiMode = true;
/*      */           }
/*      */         }
/*      */         catch (SQLException sqlEx)
/*      */         {
/*      */         }
/* 1630 */         String identifierMarkers = isProcedureInAnsiMode ? "`\"" : "`";
/* 1631 */         String identifierAndStringMarkers = "'" + identifierMarkers;
/* 1632 */         storageDefnDelims = "(" + identifierMarkers;
/* 1633 */         storageDefnClosures = ")" + identifierMarkers;
/*      */ 
/* 1636 */         procedureDef = StringUtils.stripComments(procedureDef, identifierAndStringMarkers, identifierAndStringMarkers, true, false, true, true);
/*      */ 
/* 1639 */         int openParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, "(", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1643 */         int endOfParamDeclarationIndex = 0;
/*      */ 
/* 1645 */         endOfParamDeclarationIndex = endPositionOfParameterDeclaration(openParenIndex, procedureDef, quoteChar);
/*      */ 
/* 1648 */         if (parsingFunction)
/*      */         {
/* 1652 */           int returnsIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, " RETURNS ", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1657 */           int endReturnsDef = findEndOfReturnsClause(procedureDef, quoteChar, returnsIndex);
/*      */ 
/* 1662 */           int declarationStart = returnsIndex + "RETURNS ".length();
/*      */ 
/* 1664 */           while ((declarationStart < procedureDef.length()) && 
/* 1665 */             (Character.isWhitespace(procedureDef.charAt(declarationStart)))) {
/* 1666 */             declarationStart++;
/*      */           }
/*      */ 
/* 1672 */           String returnsDefn = procedureDef.substring(declarationStart, endReturnsDef).trim();
/* 1673 */           TypeDescriptor returnDescriptor = new TypeDescriptor(returnsDefn, null);
/*      */ 
/* 1676 */           resultRows.add(convertTypeDescriptorToProcedureRow(procNameAsBytes, "", false, false, true, returnDescriptor, forGetFunctionColumns, 0));
/*      */         }
/*      */ 
/* 1681 */         if ((openParenIndex == -1) || (endOfParamDeclarationIndex == -1))
/*      */         {
/* 1684 */           throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 1690 */         parameterDef = procedureDef.substring(openParenIndex + 1, endOfParamDeclarationIndex);
/*      */       }
/*      */     }
/*      */     finally {
/* 1694 */       SQLException sqlExRethrow = null;
/*      */ 
/* 1696 */       if (paramRetrievalRs != null) {
/*      */         try {
/* 1698 */           paramRetrievalRs.close();
/*      */         } catch (SQLException sqlEx) {
/* 1700 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/* 1703 */         paramRetrievalRs = null;
/*      */       }
/*      */ 
/* 1706 */       if (paramRetrievalStmt != null) {
/*      */         try {
/* 1708 */           paramRetrievalStmt.close();
/*      */         } catch (SQLException sqlEx) {
/* 1710 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/* 1713 */         paramRetrievalStmt = null;
/*      */       }
/*      */ 
/* 1716 */       if (sqlExRethrow != null) {
/* 1717 */         throw sqlExRethrow;
/*      */       }
/*      */     }
/*      */ 
/* 1721 */     if (parameterDef != null) {
/* 1722 */       int ordinal = 1;
/*      */ 
/* 1724 */       List parseList = StringUtils.split(parameterDef, ",", storageDefnDelims, storageDefnClosures, true);
/*      */ 
/* 1727 */       int parseListLen = parseList.size();
/*      */ 
/* 1729 */       for (int i = 0; i < parseListLen; i++) {
/* 1730 */         String declaration = (String)parseList.get(i);
/*      */ 
/* 1732 */         if (declaration.trim().length() == 0)
/*      */         {
/*      */           break;
/*      */         }
/* 1736 */         StringTokenizer declarationTok = new StringTokenizer(declaration, " \t");
/*      */ 
/* 1739 */         String paramName = null;
/* 1740 */         boolean isOutParam = false;
/* 1741 */         boolean isInParam = false;
/*      */ 
/* 1743 */         if (declarationTok.hasMoreTokens()) {
/* 1744 */           String possibleParamName = declarationTok.nextToken();
/*      */ 
/* 1746 */           if (possibleParamName.equalsIgnoreCase("OUT")) {
/* 1747 */             isOutParam = true;
/*      */ 
/* 1749 */             if (declarationTok.hasMoreTokens())
/* 1750 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1752 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/* 1756 */           else if (possibleParamName.equalsIgnoreCase("INOUT")) {
/* 1757 */             isOutParam = true;
/* 1758 */             isInParam = true;
/*      */ 
/* 1760 */             if (declarationTok.hasMoreTokens())
/* 1761 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1763 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/* 1767 */           else if (possibleParamName.equalsIgnoreCase("IN")) {
/* 1768 */             isOutParam = false;
/* 1769 */             isInParam = true;
/*      */ 
/* 1771 */             if (declarationTok.hasMoreTokens())
/* 1772 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1774 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1779 */             isOutParam = false;
/* 1780 */             isInParam = true;
/*      */ 
/* 1782 */             paramName = possibleParamName;
/*      */           }
/*      */ 
/* 1785 */           TypeDescriptor typeDesc = null;
/*      */ 
/* 1787 */           if (declarationTok.hasMoreTokens()) {
/* 1788 */             StringBuffer typeInfoBuf = new StringBuffer(declarationTok.nextToken());
/*      */ 
/* 1791 */             while (declarationTok.hasMoreTokens()) {
/* 1792 */               typeInfoBuf.append(" ");
/* 1793 */               typeInfoBuf.append(declarationTok.nextToken());
/*      */             }
/*      */ 
/* 1796 */             String typeInfo = typeInfoBuf.toString();
/*      */ 
/* 1798 */             typeDesc = new TypeDescriptor(typeInfo, null);
/*      */           } else {
/* 1800 */             throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter type)", "S1000", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 1805 */           if (((paramName.startsWith("`")) && (paramName.endsWith("`"))) || ((isProcedureInAnsiMode) && (paramName.startsWith("\"")) && (paramName.endsWith("\""))))
/*      */           {
/* 1807 */             paramName = paramName.substring(1, paramName.length() - 1);
/*      */           }
/*      */ 
/* 1810 */           int wildCompareRes = StringUtils.wildCompare(paramName, parameterNamePattern);
/*      */ 
/* 1813 */           if (wildCompareRes != -1) {
/* 1814 */             ResultSetRow row = convertTypeDescriptorToProcedureRow(procNameAsBytes, paramName, isOutParam, isInParam, false, typeDesc, forGetFunctionColumns, ordinal++);
/*      */ 
/* 1819 */             resultRows.add(row);
/*      */           }
/*      */         } else {
/* 1822 */           throw SQLError.createSQLException("Internal error when parsing callable statement metadata (unknown output from 'SHOW CREATE PROCEDURE')", "S1000", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int endPositionOfParameterDeclaration(int beginIndex, String procedureDef, String quoteChar)
/*      */     throws SQLException
/*      */   {
/* 1851 */     int currentPos = beginIndex + 1;
/* 1852 */     int parenDepth = 1;
/*      */ 
/* 1854 */     while ((parenDepth > 0) && (currentPos < procedureDef.length())) {
/* 1855 */       int closedParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, procedureDef, ")", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1859 */       if (closedParenIndex != -1) {
/* 1860 */         int nextOpenParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, procedureDef, "(", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1865 */         if ((nextOpenParenIndex != -1) && (nextOpenParenIndex < closedParenIndex))
/*      */         {
/* 1867 */           parenDepth++;
/* 1868 */           currentPos = closedParenIndex + 1;
/*      */         }
/*      */         else
/*      */         {
/* 1872 */           parenDepth--;
/* 1873 */           currentPos = closedParenIndex;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1878 */         throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1885 */     return currentPos;
/*      */   }
/*      */ 
/*      */   private int findEndOfReturnsClause(String procedureDefn, String quoteChar, int positionOfReturnKeyword)
/*      */     throws SQLException
/*      */   {
/* 1910 */     String[] tokens = { "LANGUAGE", "NOT", "DETERMINISTIC", "CONTAINS", "NO", "READ", "MODIFIES", "SQL", "COMMENT", "BEGIN", "RETURN" };
/*      */ 
/* 1914 */     int startLookingAt = positionOfReturnKeyword + "RETURNS".length() + 1;
/*      */ 
/* 1916 */     int endOfReturn = -1;
/*      */ 
/* 1918 */     for (int i = 0; i < tokens.length; i++) {
/* 1919 */       int nextEndOfReturn = StringUtils.indexOfIgnoreCaseRespectQuotes(startLookingAt, procedureDefn, tokens[i], quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1923 */       if ((nextEndOfReturn == -1) || (
/* 1924 */         (endOfReturn != -1) && (nextEndOfReturn >= endOfReturn))) continue;
/* 1925 */       endOfReturn = nextEndOfReturn;
/*      */     }
/*      */ 
/* 1930 */     if (endOfReturn != -1) {
/* 1931 */       return endOfReturn;
/*      */     }
/*      */ 
/* 1935 */     endOfReturn = StringUtils.indexOfIgnoreCaseRespectQuotes(startLookingAt, procedureDefn, ":", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1939 */     if (endOfReturn != -1)
/*      */     {
/* 1941 */       for (int i = endOfReturn; i > 0; i--) {
/* 1942 */         if (Character.isWhitespace(procedureDefn.charAt(i))) {
/* 1943 */           return i;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1950 */     throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private int getCascadeDeleteOption(String cascadeOptions)
/*      */   {
/* 1964 */     int onDeletePos = cascadeOptions.indexOf("ON DELETE");
/*      */ 
/* 1966 */     if (onDeletePos != -1) {
/* 1967 */       String deleteOptions = cascadeOptions.substring(onDeletePos, cascadeOptions.length());
/*      */ 
/* 1970 */       if (deleteOptions.startsWith("ON DELETE CASCADE"))
/* 1971 */         return 0;
/* 1972 */       if (deleteOptions.startsWith("ON DELETE SET NULL"))
/* 1973 */         return 2;
/* 1974 */       if (deleteOptions.startsWith("ON DELETE RESTRICT"))
/* 1975 */         return 1;
/* 1976 */       if (deleteOptions.startsWith("ON DELETE NO ACTION")) {
/* 1977 */         return 3;
/*      */       }
/*      */     }
/*      */ 
/* 1981 */     return 3;
/*      */   }
/*      */ 
/*      */   private int getCascadeUpdateOption(String cascadeOptions)
/*      */   {
/* 1993 */     int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
/*      */ 
/* 1995 */     if (onUpdatePos != -1) {
/* 1996 */       String updateOptions = cascadeOptions.substring(onUpdatePos, cascadeOptions.length());
/*      */ 
/* 1999 */       if (updateOptions.startsWith("ON UPDATE CASCADE"))
/* 2000 */         return 0;
/* 2001 */       if (updateOptions.startsWith("ON UPDATE SET NULL"))
/* 2002 */         return 2;
/* 2003 */       if (updateOptions.startsWith("ON UPDATE RESTRICT"))
/* 2004 */         return 1;
/* 2005 */       if (updateOptions.startsWith("ON UPDATE NO ACTION")) {
/* 2006 */         return 3;
/*      */       }
/*      */     }
/*      */ 
/* 2010 */     return 3;
/*      */   }
/*      */ 
/*      */   protected IteratorWithCleanup getCatalogIterator(String catalogSpec)
/*      */     throws SQLException
/*      */   {
/*      */     IteratorWithCleanup allCatalogsIter;
/*      */     IteratorWithCleanup allCatalogsIter;
/* 2016 */     if (catalogSpec != null)
/*      */     {
/*      */       IteratorWithCleanup allCatalogsIter;
/* 2017 */       if (!catalogSpec.equals("")) {
/* 2018 */         allCatalogsIter = new SingleStringIterator(catalogSpec);
/*      */       }
/*      */       else
/* 2021 */         allCatalogsIter = new SingleStringIterator(this.database);
/*      */     }
/*      */     else
/*      */     {
/*      */       IteratorWithCleanup allCatalogsIter;
/* 2023 */       if (this.conn.getNullCatalogMeansCurrent())
/* 2024 */         allCatalogsIter = new SingleStringIterator(this.database);
/*      */       else {
/* 2026 */         allCatalogsIter = new ResultSetIterator(getCatalogs(), 1);
/*      */       }
/*      */     }
/* 2029 */     return allCatalogsIter;
/*      */   }
/*      */ 
/*      */   public ResultSet getCatalogs()
/*      */     throws SQLException
/*      */   {
/* 2048 */     ResultSet results = null;
/* 2049 */     Statement stmt = null;
/*      */     try
/*      */     {
/* 2052 */       stmt = this.conn.createStatement();
/* 2053 */       stmt.setEscapeProcessing(false);
/* 2054 */       results = stmt.executeQuery("SHOW DATABASES");
/*      */ 
/* 2056 */       ResultSetMetaData resultsMD = results.getMetaData();
/* 2057 */       Field[] fields = new Field[1];
/* 2058 */       fields[0] = new Field("", "TABLE_CAT", 12, resultsMD.getColumnDisplaySize(1));
/*      */ 
/* 2061 */       ArrayList tuples = new ArrayList();
/*      */ 
/* 2063 */       while (results.next()) {
/* 2064 */         rowVal = new byte[1][];
/* 2065 */         rowVal[0] = results.getBytes(1);
/* 2066 */         tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */       }
/*      */ 
/* 2069 */       rowVal = buildResultSet(fields, tuples);
/*      */     }
/*      */     finally
/*      */     {
/*      */       byte[][] rowVal;
/* 2071 */       if (results != null) {
/*      */         try {
/* 2073 */           results.close();
/*      */         } catch (SQLException sqlEx) {
/* 2075 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */ 
/* 2078 */         results = null;
/*      */       }
/*      */ 
/* 2081 */       if (stmt != null) {
/*      */         try {
/* 2083 */           stmt.close();
/*      */         } catch (SQLException sqlEx) {
/* 2085 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */ 
/* 2088 */         stmt = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCatalogSeparator()
/*      */     throws SQLException
/*      */   {
/* 2101 */     return ".";
/*      */   }
/*      */ 
/*      */   public String getCatalogTerm()
/*      */     throws SQLException
/*      */   {
/* 2118 */     return "database";
/*      */   }
/*      */ 
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 2159 */     Field[] fields = new Field[8];
/* 2160 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 2161 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 2162 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 2163 */     fields[3] = new Field("", "COLUMN_NAME", 1, 64);
/* 2164 */     fields[4] = new Field("", "GRANTOR", 1, 77);
/* 2165 */     fields[5] = new Field("", "GRANTEE", 1, 77);
/* 2166 */     fields[6] = new Field("", "PRIVILEGE", 1, 64);
/* 2167 */     fields[7] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */ 
/* 2169 */     StringBuffer grantQuery = new StringBuffer("SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv from mysql.columns_priv c, mysql.tables_priv t where c.host = t.host and c.db = t.db and c.table_name = t.table_name ");
/*      */ 
/* 2176 */     if ((catalog != null) && (catalog.length() != 0)) {
/* 2177 */       grantQuery.append(" AND c.db='");
/* 2178 */       grantQuery.append(catalog);
/* 2179 */       grantQuery.append("' ");
/*      */     }
/*      */ 
/* 2183 */     grantQuery.append(" AND c.table_name ='");
/* 2184 */     grantQuery.append(table);
/* 2185 */     grantQuery.append("' AND c.column_name like '");
/* 2186 */     grantQuery.append(columnNamePattern);
/* 2187 */     grantQuery.append("'");
/*      */ 
/* 2189 */     Statement stmt = null;
/* 2190 */     ResultSet results = null;
/* 2191 */     ArrayList grantRows = new ArrayList();
/*      */     try
/*      */     {
/* 2194 */       stmt = this.conn.createStatement();
/* 2195 */       stmt.setEscapeProcessing(false);
/* 2196 */       results = stmt.executeQuery(grantQuery.toString());
/*      */ 
/* 2198 */       while (results.next()) {
/* 2199 */         String host = results.getString(1);
/* 2200 */         String db = results.getString(2);
/* 2201 */         String grantor = results.getString(3);
/* 2202 */         String user = results.getString(4);
/*      */ 
/* 2204 */         if ((user == null) || (user.length() == 0)) {
/* 2205 */           user = "%";
/*      */         }
/*      */ 
/* 2208 */         StringBuffer fullUser = new StringBuffer(user);
/*      */ 
/* 2210 */         if ((host != null) && (this.conn.getUseHostsInPrivileges())) {
/* 2211 */           fullUser.append("@");
/* 2212 */           fullUser.append(host);
/*      */         }
/*      */ 
/* 2215 */         String columnName = results.getString(6);
/* 2216 */         String allPrivileges = results.getString(7);
/*      */ 
/* 2218 */         if (allPrivileges != null) {
/* 2219 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */ 
/* 2221 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */ 
/* 2223 */           while (st.hasMoreTokens()) {
/* 2224 */             String privilege = st.nextToken().trim();
/* 2225 */             byte[][] tuple = new byte[8][];
/* 2226 */             tuple[0] = s2b(db);
/* 2227 */             tuple[1] = null;
/* 2228 */             tuple[2] = s2b(table);
/* 2229 */             tuple[3] = s2b(columnName);
/*      */ 
/* 2231 */             if (grantor != null)
/* 2232 */               tuple[4] = s2b(grantor);
/*      */             else {
/* 2234 */               tuple[4] = null;
/*      */             }
/*      */ 
/* 2237 */             tuple[5] = s2b(fullUser.toString());
/* 2238 */             tuple[6] = s2b(privilege);
/* 2239 */             tuple[7] = null;
/* 2240 */             grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 2245 */       if (results != null) {
/*      */         try {
/* 2247 */           results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 2252 */         results = null;
/*      */       }
/*      */ 
/* 2255 */       if (stmt != null) {
/*      */         try {
/* 2257 */           stmt.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 2262 */         stmt = null;
/*      */       }
/*      */     }
/*      */ 
/* 2266 */     return buildResultSet(fields, grantRows);
/*      */   }
/*      */ 
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 2330 */     if (columnNamePattern == null) {
/* 2331 */       if (this.conn.getNullNamePatternMatchesAll())
/* 2332 */         columnNamePattern = "%";
/*      */       else {
/* 2334 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2340 */     String colPattern = columnNamePattern;
/*      */ 
/* 2342 */     Field[] fields = createColumnsFields();
/*      */ 
/* 2344 */     ArrayList rows = new ArrayList();
/* 2345 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 2349 */       new IterateBlock(getCatalogIterator(catalog), tableNamePattern, schemaPattern, colPattern, stmt, rows) { private final String val$tableNamePattern;
/*      */         private final String val$schemaPattern;
/*      */         private final String val$colPattern;
/*      */         private final Statement val$stmt;
/*      */         private final ArrayList val$rows;
/*      */ 
/* 2352 */         void forEach(Object catalogStr) throws SQLException { ArrayList tableNameList = new ArrayList();
/*      */ 
/* 2354 */           if (this.val$tableNamePattern == null)
/*      */           {
/* 2356 */             ResultSet tables = null;
/*      */             try
/*      */             {
/* 2359 */               tables = DatabaseMetaData.this.getTables((String)catalogStr, this.val$schemaPattern, "%", new String[0]);
/*      */ 
/* 2362 */               while (tables.next()) {
/* 2363 */                 String tableNameFromList = tables.getString("TABLE_NAME");
/*      */ 
/* 2365 */                 tableNameList.add(tableNameFromList);
/*      */               }
/*      */             } finally {
/* 2368 */               if (tables != null) {
/*      */                 try {
/* 2370 */                   tables.close();
/*      */                 } catch (Exception sqlEx) {
/* 2372 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2376 */                 tables = null;
/*      */               }
/*      */             }
/*      */           } else {
/* 2380 */             ResultSet tables = null;
/*      */             try
/*      */             {
/* 2383 */               tables = DatabaseMetaData.this.getTables((String)catalogStr, this.val$schemaPattern, this.val$tableNamePattern, new String[0]);
/*      */ 
/* 2386 */               while (tables.next()) {
/* 2387 */                 String tableNameFromList = tables.getString("TABLE_NAME");
/*      */ 
/* 2389 */                 tableNameList.add(tableNameFromList);
/*      */               }
/*      */             } finally {
/* 2392 */               if (tables != null) {
/*      */                 try {
/* 2394 */                   tables.close();
/*      */                 } catch (SQLException sqlEx) {
/* 2396 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2400 */                 tables = null;
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2405 */           Iterator tableNames = tableNameList.iterator();
/*      */ 
/* 2407 */           while (tableNames.hasNext()) {
/* 2408 */             String tableName = (String)tableNames.next();
/*      */ 
/* 2410 */             ResultSet results = null;
/*      */             try
/*      */             {
/* 2413 */               StringBuffer queryBuf = new StringBuffer("SHOW ");
/*      */ 
/* 2415 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2416 */                 queryBuf.append("FULL ");
/*      */               }
/*      */ 
/* 2419 */               queryBuf.append("COLUMNS FROM ");
/* 2420 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2421 */               queryBuf.append(tableName);
/* 2422 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2423 */               queryBuf.append(" FROM ");
/* 2424 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2425 */               queryBuf.append((String)catalogStr);
/* 2426 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2427 */               queryBuf.append(" LIKE '");
/* 2428 */               queryBuf.append(this.val$colPattern);
/* 2429 */               queryBuf.append("'");
/*      */ 
/* 2436 */               boolean fixUpOrdinalsRequired = false;
/* 2437 */               Object ordinalFixUpMap = null;
/*      */ 
/* 2439 */               if (!this.val$colPattern.equals("%")) {
/* 2440 */                 fixUpOrdinalsRequired = true;
/*      */ 
/* 2442 */                 StringBuffer fullColumnQueryBuf = new StringBuffer("SHOW ");
/*      */ 
/* 2445 */                 if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2446 */                   fullColumnQueryBuf.append("FULL ");
/*      */                 }
/*      */ 
/* 2449 */                 fullColumnQueryBuf.append("COLUMNS FROM ");
/* 2450 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 2451 */                 fullColumnQueryBuf.append(tableName);
/* 2452 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 2453 */                 fullColumnQueryBuf.append(" FROM ");
/* 2454 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 2455 */                 fullColumnQueryBuf.append((String)catalogStr);
/*      */ 
/* 2457 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 2459 */                 results = this.val$stmt.executeQuery(fullColumnQueryBuf.toString());
/*      */ 
/* 2462 */                 ordinalFixUpMap = new HashMap();
/*      */ 
/* 2464 */                 int fullOrdinalPos = 1;
/*      */ 
/* 2466 */                 while (results.next()) {
/* 2467 */                   String fullOrdColName = results.getString("Field");
/*      */ 
/* 2470 */                   ((Map)ordinalFixUpMap).put(fullOrdColName, Constants.integerValueOf(fullOrdinalPos++));
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2475 */               results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 2477 */               int ordPos = 1;
/*      */ 
/* 2479 */               while (results.next()) {
/* 2480 */                 byte[][] rowVal = new byte[23][];
/* 2481 */                 rowVal[0] = DatabaseMetaData.this.s2b((String)catalogStr);
/* 2482 */                 rowVal[1] = null;
/*      */ 
/* 2485 */                 rowVal[2] = DatabaseMetaData.this.s2b(tableName);
/* 2486 */                 rowVal[3] = results.getBytes("Field");
/*      */ 
/* 2488 */                 DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(DatabaseMetaData.this, results.getString("Type"), results.getString("Null"));
/*      */ 
/* 2492 */                 rowVal[4] = Short.toString(typeDesc.dataType).getBytes();
/*      */ 
/* 2496 */                 rowVal[5] = DatabaseMetaData.this.s2b(typeDesc.typeName);
/*      */ 
/* 2498 */                 rowVal[6] = (typeDesc.columnSize == null ? null : DatabaseMetaData.this.s2b(typeDesc.columnSize.toString()));
/* 2499 */                 rowVal[7] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
/* 2500 */                 rowVal[8] = (typeDesc.decimalDigits == null ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString()));
/* 2501 */                 rowVal[9] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.numPrecRadix));
/*      */ 
/* 2503 */                 rowVal[10] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.nullability));
/*      */                 try
/*      */                 {
/* 2514 */                   if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2515 */                     rowVal[11] = results.getBytes("Comment");
/*      */                   }
/*      */                   else
/* 2518 */                     rowVal[11] = results.getBytes("Extra");
/*      */                 }
/*      */                 catch (Exception E) {
/* 2521 */                   rowVal[11] = new byte[0];
/*      */                 }
/*      */ 
/* 2525 */                 rowVal[12] = results.getBytes("Default");
/*      */ 
/* 2527 */                 rowVal[13] = { 48 };
/* 2528 */                 rowVal[14] = { 48 };
/*      */ 
/* 2530 */                 if ((StringUtils.indexOfIgnoreCase(typeDesc.typeName, "CHAR") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "BLOB") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "TEXT") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "BINARY") != -1))
/*      */                 {
/* 2534 */                   rowVal[15] = rowVal[6];
/*      */                 }
/* 2536 */                 else rowVal[15] = null;
/*      */ 
/* 2540 */                 if (!fixUpOrdinalsRequired) {
/* 2541 */                   rowVal[16] = Integer.toString(ordPos++).getBytes();
/*      */                 }
/*      */                 else {
/* 2544 */                   String origColName = results.getString("Field");
/*      */ 
/* 2546 */                   Integer realOrdinal = (Integer)((Map)ordinalFixUpMap).get(origColName);
/*      */ 
/* 2549 */                   if (realOrdinal != null) {
/* 2550 */                     rowVal[16] = realOrdinal.toString().getBytes();
/*      */                   }
/*      */                   else {
/* 2553 */                     throw SQLError.createSQLException("Can not find column in full column list to determine true ordinal position.", "S1000", DatabaseMetaData.this.getExceptionInterceptor());
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/* 2559 */                 rowVal[17] = DatabaseMetaData.this.s2b(typeDesc.isNullable);
/*      */ 
/* 2562 */                 rowVal[18] = null;
/* 2563 */                 rowVal[19] = null;
/* 2564 */                 rowVal[20] = null;
/* 2565 */                 rowVal[21] = null;
/*      */ 
/* 2567 */                 rowVal[22] = DatabaseMetaData.this.s2b("");
/*      */ 
/* 2569 */                 String extra = results.getString("Extra");
/*      */ 
/* 2571 */                 if (extra != null) {
/* 2572 */                   rowVal[22] = DatabaseMetaData.this.s2b(StringUtils.indexOfIgnoreCase(extra, "auto_increment") != -1 ? "YES" : "NO");
/*      */                 }
/*      */ 
/* 2578 */                 this.val$rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             } finally {
/* 2581 */               if (results != null) {
/*      */                 try {
/* 2583 */                   results.close();
/*      */                 }
/*      */                 catch (Exception ex)
/*      */                 {
/*      */                 }
/* 2588 */                 results = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2349 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 2595 */       if (stmt != null) {
/* 2596 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 2600 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 2602 */     return results;
/*      */   }
/*      */ 
/*      */   protected Field[] createColumnsFields() {
/* 2606 */     Field[] fields = new Field[23];
/* 2607 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 2608 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 2609 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 2610 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 2611 */     fields[4] = new Field("", "DATA_TYPE", 4, 5);
/* 2612 */     fields[5] = new Field("", "TYPE_NAME", 1, 16);
/* 2613 */     fields[6] = new Field("", "COLUMN_SIZE", 4, Integer.toString(2147483647).length());
/*      */ 
/* 2615 */     fields[7] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 2616 */     fields[8] = new Field("", "DECIMAL_DIGITS", 4, 10);
/* 2617 */     fields[9] = new Field("", "NUM_PREC_RADIX", 4, 10);
/* 2618 */     fields[10] = new Field("", "NULLABLE", 4, 10);
/* 2619 */     fields[11] = new Field("", "REMARKS", 1, 0);
/* 2620 */     fields[12] = new Field("", "COLUMN_DEF", 1, 0);
/* 2621 */     fields[13] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 2622 */     fields[14] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 2623 */     fields[15] = new Field("", "CHAR_OCTET_LENGTH", 4, Integer.toString(2147483647).length());
/*      */ 
/* 2625 */     fields[16] = new Field("", "ORDINAL_POSITION", 4, 10);
/* 2626 */     fields[17] = new Field("", "IS_NULLABLE", 1, 3);
/* 2627 */     fields[18] = new Field("", "SCOPE_CATALOG", 1, 255);
/* 2628 */     fields[19] = new Field("", "SCOPE_SCHEMA", 1, 255);
/* 2629 */     fields[20] = new Field("", "SCOPE_TABLE", 1, 255);
/* 2630 */     fields[21] = new Field("", "SOURCE_DATA_TYPE", 5, 10);
/* 2631 */     fields[22] = new Field("", "IS_AUTOINCREMENT", 1, 3);
/* 2632 */     return fields;
/*      */   }
/*      */ 
/*      */   public Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 2643 */     return this.conn;
/*      */   }
/*      */ 
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable)
/*      */     throws SQLException
/*      */   {
/* 2717 */     if (primaryTable == null) {
/* 2718 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2722 */     Field[] fields = createFkMetadataFields();
/*      */ 
/* 2724 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 2726 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 2728 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 2732 */         new IterateBlock(getCatalogIterator(foreignCatalog), stmt, foreignTable, primaryTable, foreignCatalog, foreignSchema, primaryCatalog, primarySchema, tuples) { private final Statement val$stmt;
/*      */           private final String val$foreignTable;
/*      */           private final String val$primaryTable;
/*      */           private final String val$foreignCatalog;
/*      */           private final String val$foreignSchema;
/*      */           private final String val$primaryCatalog;
/*      */           private final String val$primarySchema;
/*      */           private final ArrayList val$tuples;
/*      */ 
/* 2735 */           void forEach(Object catalogStr) throws SQLException { ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 2742 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50)) {
/* 2743 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr.toString(), null);
/*      */               }
/*      */               else {
/* 2746 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS FROM ");
/*      */ 
/* 2748 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2749 */                 queryBuf.append(catalogStr.toString());
/* 2750 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 2752 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 2756 */               String foreignTableWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$foreignTable);
/* 2757 */               String primaryTableWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$primaryTable);
/*      */ 
/* 2765 */               while (fkresults.next()) {
/* 2766 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 2768 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 2772 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 2775 */                   if (comment != null) {
/* 2776 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */                     String dummy;
/* 2779 */                     if (commentTokens.hasMoreTokens()) {
/* 2780 */                       dummy = commentTokens.nextToken();
/*      */                     }
/*      */ 
/* 2785 */                     while (commentTokens.hasMoreTokens()) {
/* 2786 */                       String keys = commentTokens.nextToken();
/*      */ 
/* 2788 */                       DatabaseMetaData.LocalAndReferencedColumns parsedInfo = DatabaseMetaData.this.parseTableStatusIntoLocalAndReferencedColumns(keys);
/*      */ 
/* 2790 */                       int keySeq = 0;
/*      */ 
/* 2792 */                       Iterator referencingColumns = parsedInfo.localColumnsList.iterator();
/*      */ 
/* 2794 */                       Iterator referencedColumns = parsedInfo.referencedColumnsList.iterator();
/*      */ 
/* 2797 */                       while (referencingColumns.hasNext()) {
/* 2798 */                         String referencingColumn = DatabaseMetaData.this.removeQuotedId(referencingColumns.next().toString());
/*      */ 
/* 2804 */                         byte[][] tuple = new byte[14][];
/* 2805 */                         tuple[4] = (this.val$foreignCatalog == null ? null : DatabaseMetaData.this.s2b(this.val$foreignCatalog));
/*      */ 
/* 2807 */                         tuple[5] = (this.val$foreignSchema == null ? null : DatabaseMetaData.this.s2b(this.val$foreignSchema));
/*      */ 
/* 2809 */                         String dummy = fkresults.getString("Name");
/*      */ 
/* 2812 */                         if (dummy.compareTo(foreignTableWithCase) != 0)
/*      */                         {
/*      */                           continue;
/*      */                         }
/*      */ 
/* 2817 */                         tuple[6] = DatabaseMetaData.this.s2b(dummy);
/*      */ 
/* 2819 */                         tuple[7] = DatabaseMetaData.this.s2b(referencingColumn);
/* 2820 */                         tuple[0] = (this.val$primaryCatalog == null ? null : DatabaseMetaData.this.s2b(this.val$primaryCatalog));
/*      */ 
/* 2822 */                         tuple[1] = (this.val$primarySchema == null ? null : DatabaseMetaData.this.s2b(this.val$primarySchema));
/*      */ 
/* 2828 */                         if (parsedInfo.referencedTable.compareTo(primaryTableWithCase) != 0)
/*      */                         {
/*      */                           continue;
/*      */                         }
/*      */ 
/* 2833 */                         tuple[2] = DatabaseMetaData.this.s2b(parsedInfo.referencedTable);
/* 2834 */                         tuple[3] = DatabaseMetaData.this.s2b(DatabaseMetaData.access$200(DatabaseMetaData.this, referencedColumns.next().toString()));
/*      */ 
/* 2836 */                         tuple[8] = Integer.toString(keySeq).getBytes();
/*      */ 
/* 2839 */                         int[] actions = DatabaseMetaData.this.getForeignKeyActions(keys);
/*      */ 
/* 2841 */                         tuple[9] = Integer.toString(actions[1]).getBytes();
/*      */ 
/* 2843 */                         tuple[10] = Integer.toString(actions[0]).getBytes();
/*      */ 
/* 2845 */                         tuple[11] = null;
/* 2846 */                         tuple[12] = null;
/* 2847 */                         tuple[13] = Integer.toString(7).getBytes();
/*      */ 
/* 2851 */                         this.val$tuples.add(new ByteArrayRow(tuple, DatabaseMetaData.this.getExceptionInterceptor()));
/* 2852 */                         keySeq++;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/* 2860 */               if (fkresults != null) {
/*      */                 try {
/* 2862 */                   fkresults.close();
/*      */                 } catch (Exception sqlEx) {
/* 2864 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2868 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2732 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 2874 */         if (stmt != null) {
/* 2875 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2880 */     ResultSet results = buildResultSet(fields, tuples);
/*      */ 
/* 2882 */     return results;
/*      */   }
/*      */ 
/*      */   protected Field[] createFkMetadataFields() {
/* 2886 */     Field[] fields = new Field[14];
/* 2887 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 2888 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 2889 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 2890 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 2891 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 2892 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 2893 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 2894 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 2895 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 2896 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 2897 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 2898 */     fields[11] = new Field("", "FK_NAME", 1, 0);
/* 2899 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 2900 */     fields[13] = new Field("", "DEFERRABILITY", 5, 2);
/* 2901 */     return fields;
/*      */   }
/*      */ 
/*      */   public int getDatabaseMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 2908 */     return this.conn.getServerMajorVersion();
/*      */   }
/*      */ 
/*      */   public int getDatabaseMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 2915 */     return this.conn.getServerMinorVersion();
/*      */   }
/*      */ 
/*      */   public String getDatabaseProductName()
/*      */     throws SQLException
/*      */   {
/* 2926 */     return "MySQL";
/*      */   }
/*      */ 
/*      */   public String getDatabaseProductVersion()
/*      */     throws SQLException
/*      */   {
/* 2937 */     return this.conn.getServerVersion();
/*      */   }
/*      */ 
/*      */   public int getDefaultTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 2950 */     if (this.conn.supportsIsolationLevel()) {
/* 2951 */       return 2;
/*      */     }
/*      */ 
/* 2954 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getDriverMajorVersion()
/*      */   {
/* 2963 */     return NonRegisteringDriver.getMajorVersionInternal();
/*      */   }
/*      */ 
/*      */   public int getDriverMinorVersion()
/*      */   {
/* 2972 */     return NonRegisteringDriver.getMinorVersionInternal();
/*      */   }
/*      */ 
/*      */   public String getDriverName()
/*      */     throws SQLException
/*      */   {
/* 2983 */     return "MySQL-AB JDBC Driver";
/*      */   }
/*      */ 
/*      */   public String getDriverVersion()
/*      */     throws SQLException
/*      */   {
/* 2994 */     return "mysql-connector-java-5.1.12 ( Revision: ${bzr.revision-id} )";
/*      */   }
/*      */ 
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 3058 */     if (table == null) {
/* 3059 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3063 */     Field[] fields = createFkMetadataFields();
/*      */ 
/* 3065 */     ArrayList rows = new ArrayList();
/*      */ 
/* 3067 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 3069 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 3073 */         new IterateBlock(getCatalogIterator(catalog), stmt, table, rows) { private final Statement val$stmt;
/*      */           private final String val$table;
/*      */           private final ArrayList val$rows;
/*      */ 
/* 3075 */           void forEach(Object catalogStr) throws SQLException { ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 3082 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50))
/*      */               {
/* 3085 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr.toString(), null);
/*      */               }
/*      */               else {
/* 3088 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS FROM ");
/*      */ 
/* 3090 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3091 */                 queryBuf.append(catalogStr.toString());
/* 3092 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 3094 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 3099 */               String tableNameWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$table);
/*      */ 
/* 3105 */               while (fkresults.next()) {
/* 3106 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 3108 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 3112 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 3115 */                   if (comment != null) {
/* 3116 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/* 3119 */                     if (commentTokens.hasMoreTokens()) {
/* 3120 */                       commentTokens.nextToken();
/*      */ 
/* 3125 */                       while (commentTokens.hasMoreTokens()) {
/* 3126 */                         String keys = commentTokens.nextToken();
/*      */ 
/* 3128 */                         DatabaseMetaData.this.getExportKeyResults(catalogStr.toString(), tableNameWithCase, keys, this.val$rows, fkresults.getString("Name"));
/*      */                       }
/*      */ 
/*      */                     }
/*      */ 
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */             finally
/*      */             {
/* 3142 */               if (fkresults != null) {
/*      */                 try {
/* 3144 */                   fkresults.close();
/*      */                 } catch (SQLException sqlEx) {
/* 3146 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 3150 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3073 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 3156 */         if (stmt != null) {
/* 3157 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3162 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 3164 */     return results;
/*      */   }
/*      */ 
/*      */   private void getExportKeyResults(String catalog, String exportingTable, String keysComment, List tuples, String fkTableName)
/*      */     throws SQLException
/*      */   {
/* 3188 */     getResultsImpl(catalog, exportingTable, keysComment, tuples, fkTableName, true);
/*      */   }
/*      */ 
/*      */   public String getExtraNameCharacters()
/*      */     throws SQLException
/*      */   {
/* 3201 */     return "#@";
/*      */   }
/*      */ 
/*      */   private int[] getForeignKeyActions(String commentString)
/*      */   {
/* 3214 */     int[] actions = { 3, 3 };
/*      */ 
/* 3218 */     int lastParenIndex = commentString.lastIndexOf(")");
/*      */ 
/* 3220 */     if (lastParenIndex != commentString.length() - 1) {
/* 3221 */       String cascadeOptions = commentString.substring(lastParenIndex + 1).trim().toUpperCase(Locale.ENGLISH);
/*      */ 
/* 3224 */       actions[0] = getCascadeDeleteOption(cascadeOptions);
/* 3225 */       actions[1] = getCascadeUpdateOption(cascadeOptions);
/*      */     }
/*      */ 
/* 3228 */     return actions;
/*      */   }
/*      */ 
/*      */   public String getIdentifierQuoteString()
/*      */     throws SQLException
/*      */   {
/* 3241 */     if (this.conn.supportsQuotedIdentifiers()) {
/* 3242 */       if (!this.conn.useAnsiQuotedIdentifiers()) {
/* 3243 */         return "`";
/*      */       }
/*      */ 
/* 3246 */       return "\"";
/*      */     }
/*      */ 
/* 3249 */     return " ";
/*      */   }
/*      */ 
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 3313 */     if (table == null) {
/* 3314 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3318 */     Field[] fields = createFkMetadataFields();
/*      */ 
/* 3320 */     ArrayList rows = new ArrayList();
/*      */ 
/* 3322 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 3324 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 3328 */         new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) { private final String val$table;
/*      */           private final Statement val$stmt;
/*      */           private final ArrayList val$rows;
/*      */ 
/* 3330 */           void forEach(Object catalogStr) throws SQLException { ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 3337 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50))
/*      */               {
/* 3340 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr.toString(), this.val$table);
/*      */               }
/*      */               else {
/* 3343 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS ");
/*      */ 
/* 3345 */                 queryBuf.append(" FROM ");
/* 3346 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3347 */                 queryBuf.append(catalogStr.toString());
/* 3348 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3349 */                 queryBuf.append(" LIKE '");
/* 3350 */                 queryBuf.append(this.val$table);
/* 3351 */                 queryBuf.append("'");
/*      */ 
/* 3353 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 3361 */               while (fkresults.next()) {
/* 3362 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 3364 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 3368 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 3371 */                   if (comment != null) {
/* 3372 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/* 3375 */                     if (commentTokens.hasMoreTokens()) {
/* 3376 */                       commentTokens.nextToken();
/*      */ 
/* 3381 */                       while (commentTokens.hasMoreTokens()) {
/* 3382 */                         String keys = commentTokens.nextToken();
/*      */ 
/* 3384 */                         DatabaseMetaData.this.getImportKeyResults(catalogStr.toString(), this.val$table, keys, this.val$rows);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/* 3393 */               if (fkresults != null) {
/*      */                 try {
/* 3395 */                   fkresults.close();
/*      */                 } catch (SQLException sqlEx) {
/* 3397 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 3401 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3328 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 3407 */         if (stmt != null) {
/* 3408 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3413 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 3415 */     return results;
/*      */   }
/*      */ 
/*      */   private void getImportKeyResults(String catalog, String importingTable, String keysComment, List tuples)
/*      */     throws SQLException
/*      */   {
/* 3437 */     getResultsImpl(catalog, importingTable, keysComment, tuples, null, false);
/*      */   }
/*      */ 
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
/*      */     throws SQLException
/*      */   {
/* 3508 */     Field[] fields = createIndexInfoFields();
/*      */ 
/* 3510 */     ArrayList rows = new ArrayList();
/* 3511 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 3515 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, unique, rows) { private final String val$table;
/*      */         private final Statement val$stmt;
/*      */         private final boolean val$unique;
/*      */         private final ArrayList val$rows;
/*      */ 
/* 3518 */         void forEach(Object catalogStr) throws SQLException { ResultSet results = null;
/*      */           try
/*      */           {
/* 3521 */             StringBuffer queryBuf = new StringBuffer("SHOW INDEX FROM ");
/*      */ 
/* 3523 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3524 */             queryBuf.append(this.val$table);
/* 3525 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3526 */             queryBuf.append(" FROM ");
/* 3527 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3528 */             queryBuf.append(catalogStr.toString());
/* 3529 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */             try
/*      */             {
/* 3532 */               results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */             } catch (SQLException sqlEx) {
/* 3534 */               int errorCode = sqlEx.getErrorCode();
/*      */ 
/* 3538 */               if (!"42S02".equals(sqlEx.getSQLState()))
/*      */               {
/* 3541 */                 if (errorCode != 1146) {
/* 3542 */                   throw sqlEx;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 3547 */             while ((results != null) && (results.next())) {
/* 3548 */               byte[][] row = new byte[14][];
/* 3549 */               row[0] = (catalogStr.toString() == null ? new byte[0] : DatabaseMetaData.this.s2b(catalogStr.toString()));
/*      */ 
/* 3552 */               row[1] = null;
/* 3553 */               row[2] = results.getBytes("Table");
/*      */ 
/* 3555 */               boolean indexIsUnique = results.getInt("Non_unique") == 0;
/*      */ 
/* 3558 */               row[3] = (!indexIsUnique ? DatabaseMetaData.this.s2b("true") : DatabaseMetaData.this.s2b("false"));
/*      */ 
/* 3560 */               row[4] = new byte[0];
/* 3561 */               row[5] = results.getBytes("Key_name");
/* 3562 */               row[6] = Integer.toString(3).getBytes();
/*      */ 
/* 3565 */               row[7] = results.getBytes("Seq_in_index");
/* 3566 */               row[8] = results.getBytes("Column_name");
/* 3567 */               row[9] = results.getBytes("Collation");
/* 3568 */               row[10] = results.getBytes("Cardinality");
/* 3569 */               row[11] = DatabaseMetaData.this.s2b("0");
/* 3570 */               row[12] = null;
/*      */ 
/* 3572 */               if (this.val$unique) {
/* 3573 */                 if (indexIsUnique) {
/* 3574 */                   this.val$rows.add(new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                 }
/*      */               }
/*      */               else
/* 3578 */                 this.val$rows.add(new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */             }
/*      */           }
/*      */           finally {
/* 3582 */             if (results != null) {
/*      */               try {
/* 3584 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 3589 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3515 */       .doForAll();
/*      */ 
/* 3595 */       ResultSet indexInfo = buildResultSet(fields, rows);
/*      */ 
/* 3597 */       ResultSet localResultSet1 = indexInfo;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/* 3599 */       if (stmt != null)
/* 3600 */         stmt.close(); 
/* 3600 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   protected Field[] createIndexInfoFields()
/*      */   {
/* 3606 */     Field[] fields = new Field[13];
/* 3607 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3608 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3609 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3610 */     fields[3] = new Field("", "NON_UNIQUE", 16, 4);
/* 3611 */     fields[4] = new Field("", "INDEX_QUALIFIER", 1, 1);
/* 3612 */     fields[5] = new Field("", "INDEX_NAME", 1, 32);
/* 3613 */     fields[6] = new Field("", "TYPE", 5, 32);
/* 3614 */     fields[7] = new Field("", "ORDINAL_POSITION", 5, 5);
/* 3615 */     fields[8] = new Field("", "COLUMN_NAME", 1, 32);
/* 3616 */     fields[9] = new Field("", "ASC_OR_DESC", 1, 1);
/* 3617 */     fields[10] = new Field("", "CARDINALITY", 4, 10);
/* 3618 */     fields[11] = new Field("", "PAGES", 4, 10);
/* 3619 */     fields[12] = new Field("", "FILTER_CONDITION", 1, 32);
/* 3620 */     return fields;
/*      */   }
/*      */ 
/*      */   public int getJDBCMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 3627 */     return 3;
/*      */   }
/*      */ 
/*      */   public int getJDBCMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 3634 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxBinaryLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 3645 */     return 16777208;
/*      */   }
/*      */ 
/*      */   public int getMaxCatalogNameLength()
/*      */     throws SQLException
/*      */   {
/* 3656 */     return 32;
/*      */   }
/*      */ 
/*      */   public int getMaxCharLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 3667 */     return 16777208;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnNameLength()
/*      */     throws SQLException
/*      */   {
/* 3678 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInGroupBy()
/*      */     throws SQLException
/*      */   {
/* 3689 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInIndex()
/*      */     throws SQLException
/*      */   {
/* 3700 */     return 16;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 3711 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInSelect()
/*      */     throws SQLException
/*      */   {
/* 3722 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInTable()
/*      */     throws SQLException
/*      */   {
/* 3733 */     return 512;
/*      */   }
/*      */ 
/*      */   public int getMaxConnections()
/*      */     throws SQLException
/*      */   {
/* 3744 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxCursorNameLength()
/*      */     throws SQLException
/*      */   {
/* 3755 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxIndexLength()
/*      */     throws SQLException
/*      */   {
/* 3766 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxProcedureNameLength()
/*      */     throws SQLException
/*      */   {
/* 3777 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxRowSize()
/*      */     throws SQLException
/*      */   {
/* 3788 */     return 2147483639;
/*      */   }
/*      */ 
/*      */   public int getMaxSchemaNameLength()
/*      */     throws SQLException
/*      */   {
/* 3799 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxStatementLength()
/*      */     throws SQLException
/*      */   {
/* 3810 */     return MysqlIO.getMaxBuf() - 4;
/*      */   }
/*      */ 
/*      */   public int getMaxStatements()
/*      */     throws SQLException
/*      */   {
/* 3821 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxTableNameLength()
/*      */     throws SQLException
/*      */   {
/* 3832 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxTablesInSelect()
/*      */     throws SQLException
/*      */   {
/* 3843 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxUserNameLength()
/*      */     throws SQLException
/*      */   {
/* 3854 */     return 16;
/*      */   }
/*      */ 
/*      */   public String getNumericFunctions()
/*      */     throws SQLException
/*      */   {
/* 3865 */     return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
/*      */   }
/*      */ 
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 3897 */     Field[] fields = new Field[6];
/* 3898 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3899 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3900 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3901 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 3902 */     fields[4] = new Field("", "KEY_SEQ", 5, 5);
/* 3903 */     fields[5] = new Field("", "PK_NAME", 1, 32);
/*      */ 
/* 3905 */     if (table == null) {
/* 3906 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3910 */     ArrayList rows = new ArrayList();
/* 3911 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 3915 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) { private final String val$table;
/*      */         private final Statement val$stmt;
/*      */         private final ArrayList val$rows;
/*      */ 
/* 3917 */         void forEach(Object catalogStr) throws SQLException { ResultSet rs = null;
/*      */           try
/*      */           {
/* 3921 */             StringBuffer queryBuf = new StringBuffer("SHOW KEYS FROM ");
/*      */ 
/* 3923 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3924 */             queryBuf.append(this.val$table);
/* 3925 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3926 */             queryBuf.append(" FROM ");
/* 3927 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3928 */             queryBuf.append(catalogStr.toString());
/* 3929 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 3931 */             rs = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 3933 */             TreeMap sortMap = new TreeMap();
/*      */ 
/* 3935 */             while (rs.next()) {
/* 3936 */               String keyType = rs.getString("Key_name");
/*      */ 
/* 3938 */               if ((keyType != null) && (
/* 3939 */                 (keyType.equalsIgnoreCase("PRIMARY")) || (keyType.equalsIgnoreCase("PRI"))))
/*      */               {
/* 3941 */                 byte[][] tuple = new byte[6][];
/* 3942 */                 tuple[0] = (catalogStr.toString() == null ? new byte[0] : DatabaseMetaData.this.s2b(catalogStr.toString()));
/*      */ 
/* 3944 */                 tuple[1] = null;
/* 3945 */                 tuple[2] = DatabaseMetaData.this.s2b(this.val$table);
/*      */ 
/* 3947 */                 String columnName = rs.getString("Column_name");
/*      */ 
/* 3949 */                 tuple[3] = DatabaseMetaData.this.s2b(columnName);
/* 3950 */                 tuple[4] = DatabaseMetaData.this.s2b(rs.getString("Seq_in_index"));
/* 3951 */                 tuple[5] = DatabaseMetaData.this.s2b(keyType);
/* 3952 */                 sortMap.put(columnName, tuple);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 3958 */             Iterator sortedIterator = sortMap.values().iterator();
/*      */ 
/* 3960 */             while (sortedIterator.hasNext())
/* 3961 */               this.val$rows.add(new ByteArrayRow((byte[][])sortedIterator.next(), DatabaseMetaData.this.getExceptionInterceptor()));
/*      */           }
/*      */           finally
/*      */           {
/* 3965 */             if (rs != null) {
/*      */               try {
/* 3967 */                 rs.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 3972 */               rs = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3915 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 3978 */       if (stmt != null) {
/* 3979 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 3983 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 3985 */     return results;
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4057 */     Field[] fields = createProcedureColumnsFields();
/*      */ 
/* 4059 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, true);
/*      */   }
/*      */ 
/*      */   protected Field[] createProcedureColumnsFields()
/*      */   {
/* 4066 */     Field[] fields = new Field[13];
/*      */ 
/* 4068 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 0);
/* 4069 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 0);
/* 4070 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 0);
/* 4071 */     fields[3] = new Field("", "COLUMN_NAME", 1, 0);
/* 4072 */     fields[4] = new Field("", "COLUMN_TYPE", 1, 0);
/* 4073 */     fields[5] = new Field("", "DATA_TYPE", 5, 0);
/* 4074 */     fields[6] = new Field("", "TYPE_NAME", 1, 0);
/* 4075 */     fields[7] = new Field("", "PRECISION", 4, 0);
/* 4076 */     fields[8] = new Field("", "LENGTH", 4, 0);
/* 4077 */     fields[9] = new Field("", "SCALE", 5, 0);
/* 4078 */     fields[10] = new Field("", "RADIX", 5, 0);
/* 4079 */     fields[11] = new Field("", "NULLABLE", 5, 0);
/* 4080 */     fields[12] = new Field("", "REMARKS", 1, 0);
/* 4081 */     return fields;
/*      */   }
/*      */ 
/*      */   protected ResultSet getProcedureOrFunctionColumns(Field[] fields, String catalog, String schemaPattern, String procedureOrFunctionNamePattern, String columnNamePattern, boolean returnProcedures, boolean returnFunctions)
/*      */     throws SQLException
/*      */   {
/* 4090 */     List proceduresToExtractList = new ArrayList();
/*      */ 
/* 4092 */     if (supportsStoredProcedures()) {
/* 4093 */       if ((procedureOrFunctionNamePattern.indexOf("%") == -1) && (procedureOrFunctionNamePattern.indexOf("?") == -1))
/*      */       {
/* 4095 */         proceduresToExtractList.add(procedureOrFunctionNamePattern);
/*      */       }
/*      */       else {
/* 4098 */         ResultSet procedureNameRs = null;
/*      */         try
/*      */         {
/* 4102 */           procedureNameRs = getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, procedureOrFunctionNamePattern, returnProcedures, returnFunctions);
/*      */ 
/* 4108 */           while (procedureNameRs.next()) {
/* 4109 */             proceduresToExtractList.add(procedureNameRs.getString(3));
/*      */           }
/*      */ 
/* 4117 */           Collections.sort(proceduresToExtractList);
/*      */         } finally {
/* 4119 */           SQLException rethrowSqlEx = null;
/*      */ 
/* 4121 */           if (procedureNameRs != null) {
/*      */             try {
/* 4123 */               procedureNameRs.close();
/*      */             } catch (SQLException sqlEx) {
/* 4125 */               rethrowSqlEx = sqlEx;
/*      */             }
/*      */           }
/*      */ 
/* 4129 */           if (rethrowSqlEx != null) {
/* 4130 */             throw rethrowSqlEx;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 4136 */     ArrayList resultRows = new ArrayList();
/*      */ 
/* 4138 */     for (Iterator iter = proceduresToExtractList.iterator(); iter.hasNext(); ) {
/* 4139 */       String procName = (String)iter.next();
/*      */ 
/* 4141 */       getCallStmtParameterTypes(catalog, procName, columnNamePattern, resultRows, fields.length == 17);
/*      */     }
/*      */ 
/* 4146 */     return buildResultSet(fields, resultRows);
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4192 */     Field[] fields = createFieldMetadataForGetProcedures();
/*      */ 
/* 4194 */     return getProceduresAndOrFunctions(fields, catalog, schemaPattern, procedureNamePattern, true, true);
/*      */   }
/*      */ 
/*      */   private Field[] createFieldMetadataForGetProcedures()
/*      */   {
/* 4199 */     Field[] fields = new Field[9];
/* 4200 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 255);
/* 4201 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 255);
/* 4202 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 255);
/* 4203 */     fields[3] = new Field("", "reserved1", 1, 0);
/* 4204 */     fields[4] = new Field("", "reserved2", 1, 0);
/* 4205 */     fields[5] = new Field("", "reserved3", 1, 0);
/* 4206 */     fields[6] = new Field("", "REMARKS", 1, 255);
/* 4207 */     fields[7] = new Field("", "PROCEDURE_TYPE", 5, 6);
/* 4208 */     fields[8] = new Field("", "SPECIFIC_NAME", 1, 255);
/*      */ 
/* 4210 */     return fields;
/*      */   }
/*      */ 
/*      */   protected ResultSet getProceduresAndOrFunctions(Field[] fields, String catalog, String schemaPattern, String procedureNamePattern, boolean returnProcedures, boolean returnFunctions)
/*      */     throws SQLException
/*      */   {
/* 4220 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0))
/*      */     {
/* 4222 */       if (this.conn.getNullNamePatternMatchesAll())
/* 4223 */         procedureNamePattern = "%";
/*      */       else {
/* 4225 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4231 */     ArrayList procedureRows = new ArrayList();
/*      */ 
/* 4233 */     if (supportsStoredProcedures()) {
/* 4234 */       String procNamePattern = procedureNamePattern;
/*      */ 
/* 4236 */       Map procedureRowsOrderedByName = new TreeMap();
/*      */ 
/* 4238 */       new IterateBlock(getCatalogIterator(catalog), procNamePattern, returnProcedures, procedureRowsOrderedByName, returnFunctions, fields, procedureRows) { private final String val$procNamePattern;
/*      */         private final boolean val$returnProcedures;
/*      */         private final Map val$procedureRowsOrderedByName;
/*      */         private final boolean val$returnFunctions;
/*      */         private final Field[] val$fields;
/*      */         private final ArrayList val$procedureRows;
/*      */ 
/* 4240 */         void forEach(Object catalogStr) throws SQLException { String db = catalogStr.toString();
/*      */ 
/* 4242 */           boolean fromSelect = false;
/* 4243 */           ResultSet proceduresRs = null;
/* 4244 */           boolean needsClientFiltering = true;
/* 4245 */           PreparedStatement proceduresStmt = (PreparedStatement)DatabaseMetaData.this.conn.clientPrepareStatement("SELECT name, type, comment FROM mysql.proc WHERE name like ? and db <=> ? ORDER BY name");
/*      */           try
/*      */           {
/* 4254 */             boolean hasTypeColumn = false;
/*      */ 
/* 4256 */             if (db != null)
/* 4257 */               proceduresStmt.setString(2, db);
/*      */             else {
/* 4259 */               proceduresStmt.setNull(2, 12);
/*      */             }
/*      */ 
/* 4262 */             int nameIndex = 1;
/*      */ 
/* 4264 */             if (proceduresStmt.getMaxRows() != 0) {
/* 4265 */               proceduresStmt.setMaxRows(0);
/*      */             }
/*      */ 
/* 4268 */             proceduresStmt.setString(1, this.val$procNamePattern);
/*      */             try
/*      */             {
/* 4271 */               proceduresRs = proceduresStmt.executeQuery();
/* 4272 */               fromSelect = true;
/* 4273 */               needsClientFiltering = false;
/* 4274 */               hasTypeColumn = true;
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/* 4282 */               proceduresStmt.close();
/*      */ 
/* 4284 */               fromSelect = false;
/*      */ 
/* 4286 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 1))
/* 4287 */                 nameIndex = 2;
/*      */               else {
/* 4289 */                 nameIndex = 1;
/*      */               }
/*      */ 
/* 4292 */               proceduresStmt = (PreparedStatement)DatabaseMetaData.this.conn.clientPrepareStatement("SHOW PROCEDURE STATUS LIKE ?");
/*      */ 
/* 4295 */               if (proceduresStmt.getMaxRows() != 0) {
/* 4296 */                 proceduresStmt.setMaxRows(0);
/*      */               }
/*      */ 
/* 4299 */               proceduresStmt.setString(1, this.val$procNamePattern);
/*      */ 
/* 4301 */               proceduresRs = proceduresStmt.executeQuery();
/*      */             }
/*      */ 
/* 4304 */             if (this.val$returnProcedures) {
/* 4305 */               DatabaseMetaData.this.convertToJdbcProcedureList(fromSelect, db, proceduresRs, needsClientFiltering, db, this.val$procedureRowsOrderedByName, nameIndex);
/*      */             }
/*      */ 
/* 4310 */             if (!hasTypeColumn)
/*      */             {
/* 4312 */               if (proceduresStmt != null) {
/* 4313 */                 proceduresStmt.close();
/*      */               }
/*      */ 
/* 4316 */               proceduresStmt = (PreparedStatement)DatabaseMetaData.this.conn.clientPrepareStatement("SHOW FUNCTION STATUS LIKE ?");
/*      */ 
/* 4319 */               if (proceduresStmt.getMaxRows() != 0) {
/* 4320 */                 proceduresStmt.setMaxRows(0);
/*      */               }
/*      */ 
/* 4323 */               proceduresStmt.setString(1, this.val$procNamePattern);
/*      */ 
/* 4325 */               proceduresRs = proceduresStmt.executeQuery();
/*      */ 
/* 4327 */               if (this.val$returnFunctions) {
/* 4328 */                 DatabaseMetaData.this.convertToJdbcFunctionList(db, proceduresRs, needsClientFiltering, db, this.val$procedureRowsOrderedByName, nameIndex, this.val$fields);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 4337 */             Iterator proceduresIter = this.val$procedureRowsOrderedByName.values().iterator();
/*      */ 
/* 4340 */             while (proceduresIter.hasNext())
/* 4341 */               this.val$procedureRows.add(proceduresIter.next());
/*      */           }
/*      */           finally {
/* 4344 */             SQLException rethrowSqlEx = null;
/*      */ 
/* 4346 */             if (proceduresRs != null) {
/*      */               try {
/* 4348 */                 proceduresRs.close();
/*      */               } catch (SQLException sqlEx) {
/* 4350 */                 rethrowSqlEx = sqlEx;
/*      */               }
/*      */             }
/*      */ 
/* 4354 */             if (proceduresStmt != null) {
/*      */               try {
/* 4356 */                 proceduresStmt.close();
/*      */               } catch (SQLException sqlEx) {
/* 4358 */                 rethrowSqlEx = sqlEx;
/*      */               }
/*      */             }
/*      */ 
/* 4362 */             if (rethrowSqlEx != null)
/* 4363 */               throw rethrowSqlEx;
/*      */           }
/*      */         }
/*      */       }
/* 4238 */       .doForAll();
/*      */     }
/*      */ 
/* 4370 */     return buildResultSet(fields, procedureRows);
/*      */   }
/*      */ 
/*      */   public String getProcedureTerm()
/*      */     throws SQLException
/*      */   {
/* 4382 */     return "PROCEDURE";
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 4389 */     return 1;
/*      */   }
/*      */ 
/*      */   private void getResultsImpl(String catalog, String table, String keysComment, List tuples, String fkTableName, boolean isExport)
/*      */     throws SQLException
/*      */   {
/* 4396 */     LocalAndReferencedColumns parsedInfo = parseTableStatusIntoLocalAndReferencedColumns(keysComment);
/*      */ 
/* 4398 */     if ((isExport) && (!parsedInfo.referencedTable.equals(table))) {
/* 4399 */       return;
/*      */     }
/*      */ 
/* 4402 */     if (parsedInfo.localColumnsList.size() != parsedInfo.referencedColumnsList.size())
/*      */     {
/* 4404 */       throw SQLError.createSQLException("Error parsing foreign keys definition,number of local and referenced columns is not the same.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4410 */     Iterator localColumnNames = parsedInfo.localColumnsList.iterator();
/* 4411 */     Iterator referColumnNames = parsedInfo.referencedColumnsList.iterator();
/*      */ 
/* 4413 */     int keySeqIndex = 1;
/*      */ 
/* 4415 */     while (localColumnNames.hasNext()) {
/* 4416 */       byte[][] tuple = new byte[14][];
/* 4417 */       String lColumnName = removeQuotedId(localColumnNames.next().toString());
/*      */ 
/* 4419 */       String rColumnName = removeQuotedId(referColumnNames.next().toString());
/*      */ 
/* 4421 */       tuple[4] = (catalog == null ? new byte[0] : s2b(catalog));
/*      */ 
/* 4423 */       tuple[5] = null;
/* 4424 */       tuple[6] = s2b(isExport ? fkTableName : table);
/* 4425 */       tuple[7] = s2b(lColumnName);
/* 4426 */       tuple[0] = s2b(parsedInfo.referencedCatalog);
/* 4427 */       tuple[1] = null;
/* 4428 */       tuple[2] = s2b(isExport ? table : parsedInfo.referencedTable);
/*      */ 
/* 4430 */       tuple[3] = s2b(rColumnName);
/* 4431 */       tuple[8] = s2b(Integer.toString(keySeqIndex++));
/*      */ 
/* 4433 */       int[] actions = getForeignKeyActions(keysComment);
/*      */ 
/* 4435 */       tuple[9] = s2b(Integer.toString(actions[1]));
/* 4436 */       tuple[10] = s2b(Integer.toString(actions[0]));
/* 4437 */       tuple[11] = s2b(parsedInfo.constraintName);
/* 4438 */       tuple[12] = null;
/* 4439 */       tuple[13] = s2b(Integer.toString(7));
/*      */ 
/* 4441 */       tuples.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getSchemas()
/*      */     throws SQLException
/*      */   {
/* 4461 */     Field[] fields = new Field[2];
/* 4462 */     fields[0] = new Field("", "TABLE_SCHEM", 1, 0);
/* 4463 */     fields[1] = new Field("", "TABLE_CATALOG", 1, 0);
/*      */ 
/* 4465 */     ArrayList tuples = new ArrayList();
/* 4466 */     ResultSet results = buildResultSet(fields, tuples);
/*      */ 
/* 4468 */     return results;
/*      */   }
/*      */ 
/*      */   public String getSchemaTerm()
/*      */     throws SQLException
/*      */   {
/* 4479 */     return "";
/*      */   }
/*      */ 
/*      */   public String getSearchStringEscape()
/*      */     throws SQLException
/*      */   {
/* 4497 */     return "\\";
/*      */   }
/*      */ 
/*      */   public String getSQLKeywords()
/*      */     throws SQLException
/*      */   {
/* 4509 */     return mysqlKeywordsThatArentSQL92;
/*      */   }
/*      */ 
/*      */   public int getSQLStateType()
/*      */     throws SQLException
/*      */   {
/* 4516 */     if (this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 4517 */       return 2;
/*      */     }
/*      */ 
/* 4520 */     if (this.conn.getUseSqlStateCodes()) {
/* 4521 */       return 2;
/*      */     }
/*      */ 
/* 4524 */     return 1;
/*      */   }
/*      */ 
/*      */   public String getStringFunctions()
/*      */     throws SQLException
/*      */   {
/* 4535 */     return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER";
/*      */   }
/*      */ 
/*      */   public ResultSet getSuperTables(String arg0, String arg1, String arg2)
/*      */     throws SQLException
/*      */   {
/* 4549 */     Field[] fields = new Field[4];
/* 4550 */     fields[0] = new Field("", "TABLE_CAT", 1, 32);
/* 4551 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 32);
/* 4552 */     fields[2] = new Field("", "TABLE_NAME", 1, 32);
/* 4553 */     fields[3] = new Field("", "SUPERTABLE_NAME", 1, 32);
/*      */ 
/* 4555 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public ResultSet getSuperTypes(String arg0, String arg1, String arg2)
/*      */     throws SQLException
/*      */   {
/* 4563 */     Field[] fields = new Field[6];
/* 4564 */     fields[0] = new Field("", "TYPE_CAT", 1, 32);
/* 4565 */     fields[1] = new Field("", "TYPE_SCHEM", 1, 32);
/* 4566 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 4567 */     fields[3] = new Field("", "SUPERTYPE_CAT", 1, 32);
/* 4568 */     fields[4] = new Field("", "SUPERTYPE_SCHEM", 1, 32);
/* 4569 */     fields[5] = new Field("", "SUPERTYPE_NAME", 1, 32);
/*      */ 
/* 4571 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public String getSystemFunctions()
/*      */     throws SQLException
/*      */   {
/* 4582 */     return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
/*      */   }
/*      */ 
/*      */   private String getTableNameWithCase(String table) {
/* 4586 */     String tableNameWithCase = this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
/*      */ 
/* 4589 */     return tableNameWithCase;
/*      */   }
/*      */ 
/*      */   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4629 */     if (tableNamePattern == null) {
/* 4630 */       if (this.conn.getNullNamePatternMatchesAll())
/* 4631 */         tableNamePattern = "%";
/*      */       else {
/* 4633 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4639 */     Field[] fields = new Field[7];
/* 4640 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 4641 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 4642 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 4643 */     fields[3] = new Field("", "GRANTOR", 1, 77);
/* 4644 */     fields[4] = new Field("", "GRANTEE", 1, 77);
/* 4645 */     fields[5] = new Field("", "PRIVILEGE", 1, 64);
/* 4646 */     fields[6] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */ 
/* 4648 */     StringBuffer grantQuery = new StringBuffer("SELECT host,db,table_name,grantor,user,table_priv from mysql.tables_priv ");
/*      */ 
/* 4650 */     grantQuery.append(" WHERE ");
/*      */ 
/* 4652 */     if ((catalog != null) && (catalog.length() != 0)) {
/* 4653 */       grantQuery.append(" db='");
/* 4654 */       grantQuery.append(catalog);
/* 4655 */       grantQuery.append("' AND ");
/*      */     }
/*      */ 
/* 4658 */     grantQuery.append("table_name like '");
/* 4659 */     grantQuery.append(tableNamePattern);
/* 4660 */     grantQuery.append("'");
/*      */ 
/* 4662 */     ResultSet results = null;
/* 4663 */     ArrayList grantRows = new ArrayList();
/* 4664 */     Statement stmt = null;
/*      */     try
/*      */     {
/* 4667 */       stmt = this.conn.createStatement();
/* 4668 */       stmt.setEscapeProcessing(false);
/*      */ 
/* 4670 */       results = stmt.executeQuery(grantQuery.toString());
/*      */ 
/* 4672 */       while (results.next()) {
/* 4673 */         String host = results.getString(1);
/* 4674 */         String db = results.getString(2);
/* 4675 */         String table = results.getString(3);
/* 4676 */         String grantor = results.getString(4);
/* 4677 */         String user = results.getString(5);
/*      */ 
/* 4679 */         if ((user == null) || (user.length() == 0)) {
/* 4680 */           user = "%";
/*      */         }
/*      */ 
/* 4683 */         StringBuffer fullUser = new StringBuffer(user);
/*      */ 
/* 4685 */         if ((host != null) && (this.conn.getUseHostsInPrivileges())) {
/* 4686 */           fullUser.append("@");
/* 4687 */           fullUser.append(host);
/*      */         }
/*      */ 
/* 4690 */         String allPrivileges = results.getString(6);
/*      */ 
/* 4692 */         if (allPrivileges != null) {
/* 4693 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */ 
/* 4695 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */ 
/* 4697 */           while (st.hasMoreTokens()) {
/* 4698 */             String privilege = st.nextToken().trim();
/*      */ 
/* 4701 */             ResultSet columnResults = null;
/*      */             try
/*      */             {
/* 4704 */               columnResults = getColumns(catalog, schemaPattern, table, "%");
/*      */ 
/* 4707 */               while (columnResults.next()) {
/* 4708 */                 byte[][] tuple = new byte[8][];
/* 4709 */                 tuple[0] = s2b(db);
/* 4710 */                 tuple[1] = null;
/* 4711 */                 tuple[2] = s2b(table);
/*      */ 
/* 4713 */                 if (grantor != null)
/* 4714 */                   tuple[3] = s2b(grantor);
/*      */                 else {
/* 4716 */                   tuple[3] = null;
/*      */                 }
/*      */ 
/* 4719 */                 tuple[4] = s2b(fullUser.toString());
/* 4720 */                 tuple[5] = s2b(privilege);
/* 4721 */                 tuple[6] = null;
/* 4722 */                 grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */               }
/*      */             } finally {
/* 4725 */               if (columnResults != null)
/*      */                 try {
/* 4727 */                   columnResults.close();
/*      */                 }
/*      */                 catch (Exception ex) {
/*      */                 }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 4737 */       if (results != null) {
/*      */         try {
/* 4739 */           results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 4744 */         results = null;
/*      */       }
/*      */ 
/* 4747 */       if (stmt != null) {
/*      */         try {
/* 4749 */           stmt.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 4754 */         stmt = null;
/*      */       }
/*      */     }
/*      */ 
/* 4758 */     return buildResultSet(fields, grantRows);
/*      */   }
/*      */ 
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
/*      */     throws SQLException
/*      */   {
/* 4800 */     if (tableNamePattern == null) {
/* 4801 */       if (this.conn.getNullNamePatternMatchesAll())
/* 4802 */         tableNamePattern = "%";
/*      */       else {
/* 4804 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4810 */     Field[] fields = new Field[5];
/* 4811 */     fields[0] = new Field("", "TABLE_CAT", 12, 255);
/* 4812 */     fields[1] = new Field("", "TABLE_SCHEM", 12, 0);
/* 4813 */     fields[2] = new Field("", "TABLE_NAME", 12, 255);
/* 4814 */     fields[3] = new Field("", "TABLE_TYPE", 12, 5);
/* 4815 */     fields[4] = new Field("", "REMARKS", 12, 0);
/*      */ 
/* 4817 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 4819 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/* 4821 */     String tableNamePat = tableNamePattern;
/*      */ 
/* 4823 */     boolean operatingOnInformationSchema = "information_schema".equalsIgnoreCase(catalog);
/*      */     try
/*      */     {
/* 4827 */       new IterateBlock(getCatalogIterator(catalog), stmt, tableNamePat, types, operatingOnInformationSchema, tuples) { private final Statement val$stmt;
/*      */         private final String val$tableNamePat;
/*      */         private final String[] val$types;
/*      */         private final boolean val$operatingOnInformationSchema;
/*      */         private final ArrayList val$tuples;
/*      */ 
/* 4829 */         void forEach(Object catalogStr) throws SQLException { ResultSet results = null;
/*      */           try
/*      */           {
/* 4833 */             if (!DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 2))
/*      */               try {
/* 4835 */                 results = this.val$stmt.executeQuery("SHOW TABLES FROM " + DatabaseMetaData.this.quotedId + catalogStr.toString() + DatabaseMetaData.this.quotedId + " LIKE '" + this.val$tableNamePat + "'");
/*      */               }
/*      */               catch (SQLException sqlEx)
/*      */               {
/* 4841 */                 if ("08S01".equals(sqlEx.getSQLState())) {
/* 4842 */                   throw sqlEx;
/*      */                 }
/*      */ 
/* 4845 */                 jsr 790;
/*      */               }
/*      */             else {
/*      */               try {
/* 4849 */                 results = this.val$stmt.executeQuery("SHOW FULL TABLES FROM " + DatabaseMetaData.this.quotedId + catalogStr.toString() + DatabaseMetaData.this.quotedId + " LIKE '" + this.val$tableNamePat + "'");
/*      */               }
/*      */               catch (SQLException sqlEx)
/*      */               {
/* 4855 */                 if ("08S01".equals(sqlEx.getSQLState())) {
/* 4856 */                   throw sqlEx;
/*      */                 }
/*      */ 
/* 4859 */                 jsr 699;
/*      */               }
/*      */             }
/*      */ 
/* 4863 */             boolean shouldReportTables = false;
/* 4864 */             boolean shouldReportViews = false;
/* 4865 */             boolean shouldReportSystemTables = false;
/*      */ 
/* 4867 */             if ((this.val$types == null) || (this.val$types.length == 0)) {
/* 4868 */               shouldReportTables = true;
/* 4869 */               shouldReportViews = true;
/* 4870 */               shouldReportSystemTables = true;
/*      */             } else {
/* 4872 */               for (int i = 0; i < this.val$types.length; i++) {
/* 4873 */                 if ("TABLE".equalsIgnoreCase(this.val$types[i])) {
/* 4874 */                   shouldReportTables = true;
/*      */                 }
/*      */ 
/* 4877 */                 if ("VIEW".equalsIgnoreCase(this.val$types[i])) {
/* 4878 */                   shouldReportViews = true;
/*      */                 }
/*      */ 
/* 4881 */                 if ("SYSTEM TABLE".equalsIgnoreCase(this.val$types[i])) {
/* 4882 */                   shouldReportSystemTables = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 4887 */             int typeColumnIndex = 0;
/* 4888 */             boolean hasTableTypes = false;
/*      */ 
/* 4890 */             if (DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 2))
/*      */             {
/*      */               try
/*      */               {
/* 4895 */                 typeColumnIndex = results.findColumn("table_type");
/*      */ 
/* 4897 */                 hasTableTypes = true;
/*      */               }
/*      */               catch (SQLException sqlEx)
/*      */               {
/*      */                 try
/*      */                 {
/* 4909 */                   typeColumnIndex = results.findColumn("Type");
/*      */ 
/* 4911 */                   hasTableTypes = true;
/*      */                 } catch (SQLException sqlEx2) {
/* 4913 */                   hasTableTypes = false;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 4918 */             TreeMap tablesOrderedByName = null;
/* 4919 */             TreeMap viewsOrderedByName = null;
/*      */ 
/* 4921 */             while (results.next()) {
/* 4922 */               byte[][] row = new byte[5][];
/* 4923 */               row[0] = (catalogStr.toString() == null ? null : DatabaseMetaData.this.s2b(catalogStr.toString()));
/*      */ 
/* 4925 */               row[1] = null;
/* 4926 */               row[2] = results.getBytes(1);
/* 4927 */               row[4] = new byte[0];
/*      */ 
/* 4929 */               if (hasTableTypes) {
/* 4930 */                 String tableType = results.getString(typeColumnIndex);
/*      */ 
/* 4933 */                 if ((("table".equalsIgnoreCase(tableType)) || ("base table".equalsIgnoreCase(tableType))) && (shouldReportTables))
/*      */                 {
/* 4936 */                   boolean reportTable = false;
/*      */ 
/* 4938 */                   if ((!this.val$operatingOnInformationSchema) && (shouldReportTables)) {
/* 4939 */                     row[3] = DatabaseMetaData.access$800();
/* 4940 */                     reportTable = true;
/* 4941 */                   } else if ((this.val$operatingOnInformationSchema) && (shouldReportSystemTables)) {
/* 4942 */                     row[3] = DatabaseMetaData.access$900();
/* 4943 */                     reportTable = true;
/*      */                   }
/*      */ 
/* 4946 */                   if (reportTable) {
/* 4947 */                     if (tablesOrderedByName == null) {
/* 4948 */                       tablesOrderedByName = new TreeMap();
/*      */                     }
/*      */ 
/* 4951 */                     tablesOrderedByName.put(results.getString(1), row);
/*      */                   }
/*      */                 }
/* 4954 */                 else if (("system view".equalsIgnoreCase(tableType)) && (shouldReportSystemTables)) {
/* 4955 */                   row[3] = DatabaseMetaData.access$900();
/*      */ 
/* 4957 */                   if (tablesOrderedByName == null) {
/* 4958 */                     tablesOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 4961 */                   tablesOrderedByName.put(results.getString(1), row);
/*      */                 }
/* 4963 */                 else if (("view".equalsIgnoreCase(tableType)) && (shouldReportViews))
/*      */                 {
/* 4965 */                   row[3] = DatabaseMetaData.access$1000();
/*      */ 
/* 4967 */                   if (viewsOrderedByName == null) {
/* 4968 */                     viewsOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 4971 */                   viewsOrderedByName.put(results.getString(1), row);
/*      */                 }
/* 4973 */                 else if (!hasTableTypes)
/*      */                 {
/* 4975 */                   row[3] = DatabaseMetaData.access$800();
/*      */ 
/* 4977 */                   if (tablesOrderedByName == null) {
/* 4978 */                     tablesOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 4981 */                   tablesOrderedByName.put(results.getString(1), row);
/*      */                 }
/*      */ 
/*      */               }
/* 4985 */               else if (shouldReportTables)
/*      */               {
/* 4987 */                 row[3] = DatabaseMetaData.access$800();
/*      */ 
/* 4989 */                 if (tablesOrderedByName == null) {
/* 4990 */                   tablesOrderedByName = new TreeMap();
/*      */                 }
/*      */ 
/* 4993 */                 tablesOrderedByName.put(results.getString(1), row);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 5002 */             if (tablesOrderedByName != null) {
/* 5003 */               Iterator tablesIter = tablesOrderedByName.values().iterator();
/*      */ 
/* 5006 */               while (tablesIter.hasNext()) {
/* 5007 */                 this.val$tuples.add(new ByteArrayRow((byte[][])tablesIter.next(), DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             }
/*      */ 
/* 5011 */             if (viewsOrderedByName != null) {
/* 5012 */               Iterator viewsIter = viewsOrderedByName.values().iterator();
/*      */ 
/* 5015 */               while (viewsIter.hasNext())
/* 5016 */                 this.val$tuples.add(new ByteArrayRow((byte[][])viewsIter.next(), DatabaseMetaData.this.getExceptionInterceptor()));
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 5021 */             if (results != null) {
/*      */               try {
/* 5023 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 5028 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 4827 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 5035 */       if (stmt != null) {
/* 5036 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 5040 */     ResultSet tables = buildResultSet(fields, tuples);
/*      */ 
/* 5042 */     return tables;
/*      */   }
/*      */ 
/*      */   public ResultSet getTableTypes()
/*      */     throws SQLException
/*      */   {
/* 5063 */     ArrayList tuples = new ArrayList();
/* 5064 */     Field[] fields = new Field[1];
/* 5065 */     fields[0] = new Field("", "TABLE_TYPE", 12, 5);
/*      */ 
/* 5067 */     byte[][] tableTypeRow = new byte[1][];
/* 5068 */     tableTypeRow[0] = TABLE_AS_BYTES;
/* 5069 */     tuples.add(new ByteArrayRow(tableTypeRow, getExceptionInterceptor()));
/*      */ 
/* 5071 */     if (this.conn.versionMeetsMinimum(5, 0, 1)) {
/* 5072 */       byte[][] viewTypeRow = new byte[1][];
/* 5073 */       viewTypeRow[0] = VIEW_AS_BYTES;
/* 5074 */       tuples.add(new ByteArrayRow(viewTypeRow, getExceptionInterceptor()));
/*      */     }
/*      */ 
/* 5077 */     byte[][] tempTypeRow = new byte[1][];
/* 5078 */     tempTypeRow[0] = s2b("LOCAL TEMPORARY");
/* 5079 */     tuples.add(new ByteArrayRow(tempTypeRow, getExceptionInterceptor()));
/*      */ 
/* 5081 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public String getTimeDateFunctions()
/*      */     throws SQLException
/*      */   {
/* 5092 */     return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC";
/*      */   }
/*      */ 
/*      */   public ResultSet getTypeInfo()
/*      */     throws SQLException
/*      */   {
/* 5201 */     Field[] fields = new Field[18];
/* 5202 */     fields[0] = new Field("", "TYPE_NAME", 1, 32);
/* 5203 */     fields[1] = new Field("", "DATA_TYPE", 4, 5);
/* 5204 */     fields[2] = new Field("", "PRECISION", 4, 10);
/* 5205 */     fields[3] = new Field("", "LITERAL_PREFIX", 1, 4);
/* 5206 */     fields[4] = new Field("", "LITERAL_SUFFIX", 1, 4);
/* 5207 */     fields[5] = new Field("", "CREATE_PARAMS", 1, 32);
/* 5208 */     fields[6] = new Field("", "NULLABLE", 5, 5);
/* 5209 */     fields[7] = new Field("", "CASE_SENSITIVE", 16, 3);
/* 5210 */     fields[8] = new Field("", "SEARCHABLE", 5, 3);
/* 5211 */     fields[9] = new Field("", "UNSIGNED_ATTRIBUTE", 16, 3);
/* 5212 */     fields[10] = new Field("", "FIXED_PREC_SCALE", 16, 3);
/* 5213 */     fields[11] = new Field("", "AUTO_INCREMENT", 16, 3);
/* 5214 */     fields[12] = new Field("", "LOCAL_TYPE_NAME", 1, 32);
/* 5215 */     fields[13] = new Field("", "MINIMUM_SCALE", 5, 5);
/* 5216 */     fields[14] = new Field("", "MAXIMUM_SCALE", 5, 5);
/* 5217 */     fields[15] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 5218 */     fields[16] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 5219 */     fields[17] = new Field("", "NUM_PREC_RADIX", 4, 10);
/*      */ 
/* 5221 */     byte[][] rowVal = (byte[][])null;
/* 5222 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 5231 */     rowVal = new byte[18][];
/* 5232 */     rowVal[0] = s2b("BIT");
/* 5233 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */ 
/* 5236 */     rowVal[2] = s2b("1");
/* 5237 */     rowVal[3] = s2b("");
/* 5238 */     rowVal[4] = s2b("");
/* 5239 */     rowVal[5] = s2b("");
/* 5240 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5244 */     rowVal[7] = s2b("true");
/* 5245 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5249 */     rowVal[9] = s2b("false");
/* 5250 */     rowVal[10] = s2b("false");
/* 5251 */     rowVal[11] = s2b("false");
/* 5252 */     rowVal[12] = s2b("BIT");
/* 5253 */     rowVal[13] = s2b("0");
/* 5254 */     rowVal[14] = s2b("0");
/* 5255 */     rowVal[15] = s2b("0");
/* 5256 */     rowVal[16] = s2b("0");
/* 5257 */     rowVal[17] = s2b("10");
/* 5258 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5263 */     rowVal = new byte[18][];
/* 5264 */     rowVal[0] = s2b("BOOL");
/* 5265 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */ 
/* 5268 */     rowVal[2] = s2b("1");
/* 5269 */     rowVal[3] = s2b("");
/* 5270 */     rowVal[4] = s2b("");
/* 5271 */     rowVal[5] = s2b("");
/* 5272 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5276 */     rowVal[7] = s2b("true");
/* 5277 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5281 */     rowVal[9] = s2b("false");
/* 5282 */     rowVal[10] = s2b("false");
/* 5283 */     rowVal[11] = s2b("false");
/* 5284 */     rowVal[12] = s2b("BOOL");
/* 5285 */     rowVal[13] = s2b("0");
/* 5286 */     rowVal[14] = s2b("0");
/* 5287 */     rowVal[15] = s2b("0");
/* 5288 */     rowVal[16] = s2b("0");
/* 5289 */     rowVal[17] = s2b("10");
/* 5290 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5295 */     rowVal = new byte[18][];
/* 5296 */     rowVal[0] = s2b("TINYINT");
/* 5297 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */ 
/* 5300 */     rowVal[2] = s2b("3");
/* 5301 */     rowVal[3] = s2b("");
/* 5302 */     rowVal[4] = s2b("");
/* 5303 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5304 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5308 */     rowVal[7] = s2b("false");
/* 5309 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5313 */     rowVal[9] = s2b("true");
/* 5314 */     rowVal[10] = s2b("false");
/* 5315 */     rowVal[11] = s2b("true");
/* 5316 */     rowVal[12] = s2b("TINYINT");
/* 5317 */     rowVal[13] = s2b("0");
/* 5318 */     rowVal[14] = s2b("0");
/* 5319 */     rowVal[15] = s2b("0");
/* 5320 */     rowVal[16] = s2b("0");
/* 5321 */     rowVal[17] = s2b("10");
/* 5322 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5324 */     rowVal = new byte[18][];
/* 5325 */     rowVal[0] = s2b("TINYINT UNSIGNED");
/* 5326 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */ 
/* 5329 */     rowVal[2] = s2b("3");
/* 5330 */     rowVal[3] = s2b("");
/* 5331 */     rowVal[4] = s2b("");
/* 5332 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5333 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5337 */     rowVal[7] = s2b("false");
/* 5338 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5342 */     rowVal[9] = s2b("true");
/* 5343 */     rowVal[10] = s2b("false");
/* 5344 */     rowVal[11] = s2b("true");
/* 5345 */     rowVal[12] = s2b("TINYINT UNSIGNED");
/* 5346 */     rowVal[13] = s2b("0");
/* 5347 */     rowVal[14] = s2b("0");
/* 5348 */     rowVal[15] = s2b("0");
/* 5349 */     rowVal[16] = s2b("0");
/* 5350 */     rowVal[17] = s2b("10");
/* 5351 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5356 */     rowVal = new byte[18][];
/* 5357 */     rowVal[0] = s2b("BIGINT");
/* 5358 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */ 
/* 5361 */     rowVal[2] = s2b("19");
/* 5362 */     rowVal[3] = s2b("");
/* 5363 */     rowVal[4] = s2b("");
/* 5364 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5365 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5369 */     rowVal[7] = s2b("false");
/* 5370 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5374 */     rowVal[9] = s2b("true");
/* 5375 */     rowVal[10] = s2b("false");
/* 5376 */     rowVal[11] = s2b("true");
/* 5377 */     rowVal[12] = s2b("BIGINT");
/* 5378 */     rowVal[13] = s2b("0");
/* 5379 */     rowVal[14] = s2b("0");
/* 5380 */     rowVal[15] = s2b("0");
/* 5381 */     rowVal[16] = s2b("0");
/* 5382 */     rowVal[17] = s2b("10");
/* 5383 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5385 */     rowVal = new byte[18][];
/* 5386 */     rowVal[0] = s2b("BIGINT UNSIGNED");
/* 5387 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */ 
/* 5390 */     rowVal[2] = s2b("20");
/* 5391 */     rowVal[3] = s2b("");
/* 5392 */     rowVal[4] = s2b("");
/* 5393 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5394 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5398 */     rowVal[7] = s2b("false");
/* 5399 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5403 */     rowVal[9] = s2b("true");
/* 5404 */     rowVal[10] = s2b("false");
/* 5405 */     rowVal[11] = s2b("true");
/* 5406 */     rowVal[12] = s2b("BIGINT UNSIGNED");
/* 5407 */     rowVal[13] = s2b("0");
/* 5408 */     rowVal[14] = s2b("0");
/* 5409 */     rowVal[15] = s2b("0");
/* 5410 */     rowVal[16] = s2b("0");
/* 5411 */     rowVal[17] = s2b("10");
/* 5412 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5417 */     rowVal = new byte[18][];
/* 5418 */     rowVal[0] = s2b("LONG VARBINARY");
/* 5419 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5422 */     rowVal[2] = s2b("16777215");
/* 5423 */     rowVal[3] = s2b("'");
/* 5424 */     rowVal[4] = s2b("'");
/* 5425 */     rowVal[5] = s2b("");
/* 5426 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5430 */     rowVal[7] = s2b("true");
/* 5431 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5435 */     rowVal[9] = s2b("false");
/* 5436 */     rowVal[10] = s2b("false");
/* 5437 */     rowVal[11] = s2b("false");
/* 5438 */     rowVal[12] = s2b("LONG VARBINARY");
/* 5439 */     rowVal[13] = s2b("0");
/* 5440 */     rowVal[14] = s2b("0");
/* 5441 */     rowVal[15] = s2b("0");
/* 5442 */     rowVal[16] = s2b("0");
/* 5443 */     rowVal[17] = s2b("10");
/* 5444 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5449 */     rowVal = new byte[18][];
/* 5450 */     rowVal[0] = s2b("MEDIUMBLOB");
/* 5451 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5454 */     rowVal[2] = s2b("16777215");
/* 5455 */     rowVal[3] = s2b("'");
/* 5456 */     rowVal[4] = s2b("'");
/* 5457 */     rowVal[5] = s2b("");
/* 5458 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5462 */     rowVal[7] = s2b("true");
/* 5463 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5467 */     rowVal[9] = s2b("false");
/* 5468 */     rowVal[10] = s2b("false");
/* 5469 */     rowVal[11] = s2b("false");
/* 5470 */     rowVal[12] = s2b("MEDIUMBLOB");
/* 5471 */     rowVal[13] = s2b("0");
/* 5472 */     rowVal[14] = s2b("0");
/* 5473 */     rowVal[15] = s2b("0");
/* 5474 */     rowVal[16] = s2b("0");
/* 5475 */     rowVal[17] = s2b("10");
/* 5476 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5481 */     rowVal = new byte[18][];
/* 5482 */     rowVal[0] = s2b("LONGBLOB");
/* 5483 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5486 */     rowVal[2] = Integer.toString(2147483647).getBytes();
/*      */ 
/* 5489 */     rowVal[3] = s2b("'");
/* 5490 */     rowVal[4] = s2b("'");
/* 5491 */     rowVal[5] = s2b("");
/* 5492 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5496 */     rowVal[7] = s2b("true");
/* 5497 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5501 */     rowVal[9] = s2b("false");
/* 5502 */     rowVal[10] = s2b("false");
/* 5503 */     rowVal[11] = s2b("false");
/* 5504 */     rowVal[12] = s2b("LONGBLOB");
/* 5505 */     rowVal[13] = s2b("0");
/* 5506 */     rowVal[14] = s2b("0");
/* 5507 */     rowVal[15] = s2b("0");
/* 5508 */     rowVal[16] = s2b("0");
/* 5509 */     rowVal[17] = s2b("10");
/* 5510 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5515 */     rowVal = new byte[18][];
/* 5516 */     rowVal[0] = s2b("BLOB");
/* 5517 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5520 */     rowVal[2] = s2b("65535");
/* 5521 */     rowVal[3] = s2b("'");
/* 5522 */     rowVal[4] = s2b("'");
/* 5523 */     rowVal[5] = s2b("");
/* 5524 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5528 */     rowVal[7] = s2b("true");
/* 5529 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5533 */     rowVal[9] = s2b("false");
/* 5534 */     rowVal[10] = s2b("false");
/* 5535 */     rowVal[11] = s2b("false");
/* 5536 */     rowVal[12] = s2b("BLOB");
/* 5537 */     rowVal[13] = s2b("0");
/* 5538 */     rowVal[14] = s2b("0");
/* 5539 */     rowVal[15] = s2b("0");
/* 5540 */     rowVal[16] = s2b("0");
/* 5541 */     rowVal[17] = s2b("10");
/* 5542 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5547 */     rowVal = new byte[18][];
/* 5548 */     rowVal[0] = s2b("TINYBLOB");
/* 5549 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5552 */     rowVal[2] = s2b("255");
/* 5553 */     rowVal[3] = s2b("'");
/* 5554 */     rowVal[4] = s2b("'");
/* 5555 */     rowVal[5] = s2b("");
/* 5556 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5560 */     rowVal[7] = s2b("true");
/* 5561 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5565 */     rowVal[9] = s2b("false");
/* 5566 */     rowVal[10] = s2b("false");
/* 5567 */     rowVal[11] = s2b("false");
/* 5568 */     rowVal[12] = s2b("TINYBLOB");
/* 5569 */     rowVal[13] = s2b("0");
/* 5570 */     rowVal[14] = s2b("0");
/* 5571 */     rowVal[15] = s2b("0");
/* 5572 */     rowVal[16] = s2b("0");
/* 5573 */     rowVal[17] = s2b("10");
/* 5574 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5580 */     rowVal = new byte[18][];
/* 5581 */     rowVal[0] = s2b("VARBINARY");
/* 5582 */     rowVal[1] = Integer.toString(-3).getBytes();
/*      */ 
/* 5585 */     rowVal[2] = s2b("255");
/* 5586 */     rowVal[3] = s2b("'");
/* 5587 */     rowVal[4] = s2b("'");
/* 5588 */     rowVal[5] = s2b("(M)");
/* 5589 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5593 */     rowVal[7] = s2b("true");
/* 5594 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5598 */     rowVal[9] = s2b("false");
/* 5599 */     rowVal[10] = s2b("false");
/* 5600 */     rowVal[11] = s2b("false");
/* 5601 */     rowVal[12] = s2b("VARBINARY");
/* 5602 */     rowVal[13] = s2b("0");
/* 5603 */     rowVal[14] = s2b("0");
/* 5604 */     rowVal[15] = s2b("0");
/* 5605 */     rowVal[16] = s2b("0");
/* 5606 */     rowVal[17] = s2b("10");
/* 5607 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5613 */     rowVal = new byte[18][];
/* 5614 */     rowVal[0] = s2b("BINARY");
/* 5615 */     rowVal[1] = Integer.toString(-2).getBytes();
/*      */ 
/* 5618 */     rowVal[2] = s2b("255");
/* 5619 */     rowVal[3] = s2b("'");
/* 5620 */     rowVal[4] = s2b("'");
/* 5621 */     rowVal[5] = s2b("(M)");
/* 5622 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5626 */     rowVal[7] = s2b("true");
/* 5627 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5631 */     rowVal[9] = s2b("false");
/* 5632 */     rowVal[10] = s2b("false");
/* 5633 */     rowVal[11] = s2b("false");
/* 5634 */     rowVal[12] = s2b("BINARY");
/* 5635 */     rowVal[13] = s2b("0");
/* 5636 */     rowVal[14] = s2b("0");
/* 5637 */     rowVal[15] = s2b("0");
/* 5638 */     rowVal[16] = s2b("0");
/* 5639 */     rowVal[17] = s2b("10");
/* 5640 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5645 */     rowVal = new byte[18][];
/* 5646 */     rowVal[0] = s2b("LONG VARCHAR");
/* 5647 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5650 */     rowVal[2] = s2b("16777215");
/* 5651 */     rowVal[3] = s2b("'");
/* 5652 */     rowVal[4] = s2b("'");
/* 5653 */     rowVal[5] = s2b("");
/* 5654 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5658 */     rowVal[7] = s2b("false");
/* 5659 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5663 */     rowVal[9] = s2b("false");
/* 5664 */     rowVal[10] = s2b("false");
/* 5665 */     rowVal[11] = s2b("false");
/* 5666 */     rowVal[12] = s2b("LONG VARCHAR");
/* 5667 */     rowVal[13] = s2b("0");
/* 5668 */     rowVal[14] = s2b("0");
/* 5669 */     rowVal[15] = s2b("0");
/* 5670 */     rowVal[16] = s2b("0");
/* 5671 */     rowVal[17] = s2b("10");
/* 5672 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5677 */     rowVal = new byte[18][];
/* 5678 */     rowVal[0] = s2b("MEDIUMTEXT");
/* 5679 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5682 */     rowVal[2] = s2b("16777215");
/* 5683 */     rowVal[3] = s2b("'");
/* 5684 */     rowVal[4] = s2b("'");
/* 5685 */     rowVal[5] = s2b("");
/* 5686 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5690 */     rowVal[7] = s2b("false");
/* 5691 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5695 */     rowVal[9] = s2b("false");
/* 5696 */     rowVal[10] = s2b("false");
/* 5697 */     rowVal[11] = s2b("false");
/* 5698 */     rowVal[12] = s2b("MEDIUMTEXT");
/* 5699 */     rowVal[13] = s2b("0");
/* 5700 */     rowVal[14] = s2b("0");
/* 5701 */     rowVal[15] = s2b("0");
/* 5702 */     rowVal[16] = s2b("0");
/* 5703 */     rowVal[17] = s2b("10");
/* 5704 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5709 */     rowVal = new byte[18][];
/* 5710 */     rowVal[0] = s2b("LONGTEXT");
/* 5711 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5714 */     rowVal[2] = Integer.toString(2147483647).getBytes();
/*      */ 
/* 5717 */     rowVal[3] = s2b("'");
/* 5718 */     rowVal[4] = s2b("'");
/* 5719 */     rowVal[5] = s2b("");
/* 5720 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5724 */     rowVal[7] = s2b("false");
/* 5725 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5729 */     rowVal[9] = s2b("false");
/* 5730 */     rowVal[10] = s2b("false");
/* 5731 */     rowVal[11] = s2b("false");
/* 5732 */     rowVal[12] = s2b("LONGTEXT");
/* 5733 */     rowVal[13] = s2b("0");
/* 5734 */     rowVal[14] = s2b("0");
/* 5735 */     rowVal[15] = s2b("0");
/* 5736 */     rowVal[16] = s2b("0");
/* 5737 */     rowVal[17] = s2b("10");
/* 5738 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5743 */     rowVal = new byte[18][];
/* 5744 */     rowVal[0] = s2b("TEXT");
/* 5745 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5748 */     rowVal[2] = s2b("65535");
/* 5749 */     rowVal[3] = s2b("'");
/* 5750 */     rowVal[4] = s2b("'");
/* 5751 */     rowVal[5] = s2b("");
/* 5752 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5756 */     rowVal[7] = s2b("false");
/* 5757 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5761 */     rowVal[9] = s2b("false");
/* 5762 */     rowVal[10] = s2b("false");
/* 5763 */     rowVal[11] = s2b("false");
/* 5764 */     rowVal[12] = s2b("TEXT");
/* 5765 */     rowVal[13] = s2b("0");
/* 5766 */     rowVal[14] = s2b("0");
/* 5767 */     rowVal[15] = s2b("0");
/* 5768 */     rowVal[16] = s2b("0");
/* 5769 */     rowVal[17] = s2b("10");
/* 5770 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5775 */     rowVal = new byte[18][];
/* 5776 */     rowVal[0] = s2b("TINYTEXT");
/* 5777 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5780 */     rowVal[2] = s2b("255");
/* 5781 */     rowVal[3] = s2b("'");
/* 5782 */     rowVal[4] = s2b("'");
/* 5783 */     rowVal[5] = s2b("");
/* 5784 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5788 */     rowVal[7] = s2b("false");
/* 5789 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5793 */     rowVal[9] = s2b("false");
/* 5794 */     rowVal[10] = s2b("false");
/* 5795 */     rowVal[11] = s2b("false");
/* 5796 */     rowVal[12] = s2b("TINYTEXT");
/* 5797 */     rowVal[13] = s2b("0");
/* 5798 */     rowVal[14] = s2b("0");
/* 5799 */     rowVal[15] = s2b("0");
/* 5800 */     rowVal[16] = s2b("0");
/* 5801 */     rowVal[17] = s2b("10");
/* 5802 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5807 */     rowVal = new byte[18][];
/* 5808 */     rowVal[0] = s2b("CHAR");
/* 5809 */     rowVal[1] = Integer.toString(1).getBytes();
/*      */ 
/* 5812 */     rowVal[2] = s2b("255");
/* 5813 */     rowVal[3] = s2b("'");
/* 5814 */     rowVal[4] = s2b("'");
/* 5815 */     rowVal[5] = s2b("(M)");
/* 5816 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5820 */     rowVal[7] = s2b("false");
/* 5821 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5825 */     rowVal[9] = s2b("false");
/* 5826 */     rowVal[10] = s2b("false");
/* 5827 */     rowVal[11] = s2b("false");
/* 5828 */     rowVal[12] = s2b("CHAR");
/* 5829 */     rowVal[13] = s2b("0");
/* 5830 */     rowVal[14] = s2b("0");
/* 5831 */     rowVal[15] = s2b("0");
/* 5832 */     rowVal[16] = s2b("0");
/* 5833 */     rowVal[17] = s2b("10");
/* 5834 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5838 */     int decimalPrecision = 254;
/*      */ 
/* 5840 */     if (this.conn.versionMeetsMinimum(5, 0, 3)) {
/* 5841 */       if (this.conn.versionMeetsMinimum(5, 0, 6))
/* 5842 */         decimalPrecision = 65;
/*      */       else {
/* 5844 */         decimalPrecision = 64;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5852 */     rowVal = new byte[18][];
/* 5853 */     rowVal[0] = s2b("NUMERIC");
/* 5854 */     rowVal[1] = Integer.toString(2).getBytes();
/*      */ 
/* 5857 */     rowVal[2] = s2b(String.valueOf(decimalPrecision));
/* 5858 */     rowVal[3] = s2b("");
/* 5859 */     rowVal[4] = s2b("");
/* 5860 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 5861 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5865 */     rowVal[7] = s2b("false");
/* 5866 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5870 */     rowVal[9] = s2b("false");
/* 5871 */     rowVal[10] = s2b("false");
/* 5872 */     rowVal[11] = s2b("true");
/* 5873 */     rowVal[12] = s2b("NUMERIC");
/* 5874 */     rowVal[13] = s2b("-308");
/* 5875 */     rowVal[14] = s2b("308");
/* 5876 */     rowVal[15] = s2b("0");
/* 5877 */     rowVal[16] = s2b("0");
/* 5878 */     rowVal[17] = s2b("10");
/* 5879 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5884 */     rowVal = new byte[18][];
/* 5885 */     rowVal[0] = s2b("DECIMAL");
/* 5886 */     rowVal[1] = Integer.toString(3).getBytes();
/*      */ 
/* 5889 */     rowVal[2] = s2b(String.valueOf(decimalPrecision));
/* 5890 */     rowVal[3] = s2b("");
/* 5891 */     rowVal[4] = s2b("");
/* 5892 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 5893 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5897 */     rowVal[7] = s2b("false");
/* 5898 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5902 */     rowVal[9] = s2b("false");
/* 5903 */     rowVal[10] = s2b("false");
/* 5904 */     rowVal[11] = s2b("true");
/* 5905 */     rowVal[12] = s2b("DECIMAL");
/* 5906 */     rowVal[13] = s2b("-308");
/* 5907 */     rowVal[14] = s2b("308");
/* 5908 */     rowVal[15] = s2b("0");
/* 5909 */     rowVal[16] = s2b("0");
/* 5910 */     rowVal[17] = s2b("10");
/* 5911 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5916 */     rowVal = new byte[18][];
/* 5917 */     rowVal[0] = s2b("INTEGER");
/* 5918 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 5921 */     rowVal[2] = s2b("10");
/* 5922 */     rowVal[3] = s2b("");
/* 5923 */     rowVal[4] = s2b("");
/* 5924 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5925 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5929 */     rowVal[7] = s2b("false");
/* 5930 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5934 */     rowVal[9] = s2b("true");
/* 5935 */     rowVal[10] = s2b("false");
/* 5936 */     rowVal[11] = s2b("true");
/* 5937 */     rowVal[12] = s2b("INTEGER");
/* 5938 */     rowVal[13] = s2b("0");
/* 5939 */     rowVal[14] = s2b("0");
/* 5940 */     rowVal[15] = s2b("0");
/* 5941 */     rowVal[16] = s2b("0");
/* 5942 */     rowVal[17] = s2b("10");
/* 5943 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5945 */     rowVal = new byte[18][];
/* 5946 */     rowVal[0] = s2b("INTEGER UNSIGNED");
/* 5947 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 5950 */     rowVal[2] = s2b("10");
/* 5951 */     rowVal[3] = s2b("");
/* 5952 */     rowVal[4] = s2b("");
/* 5953 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5954 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5958 */     rowVal[7] = s2b("false");
/* 5959 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5963 */     rowVal[9] = s2b("true");
/* 5964 */     rowVal[10] = s2b("false");
/* 5965 */     rowVal[11] = s2b("true");
/* 5966 */     rowVal[12] = s2b("INTEGER UNSIGNED");
/* 5967 */     rowVal[13] = s2b("0");
/* 5968 */     rowVal[14] = s2b("0");
/* 5969 */     rowVal[15] = s2b("0");
/* 5970 */     rowVal[16] = s2b("0");
/* 5971 */     rowVal[17] = s2b("10");
/* 5972 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5977 */     rowVal = new byte[18][];
/* 5978 */     rowVal[0] = s2b("INT");
/* 5979 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 5982 */     rowVal[2] = s2b("10");
/* 5983 */     rowVal[3] = s2b("");
/* 5984 */     rowVal[4] = s2b("");
/* 5985 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5986 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5990 */     rowVal[7] = s2b("false");
/* 5991 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5995 */     rowVal[9] = s2b("true");
/* 5996 */     rowVal[10] = s2b("false");
/* 5997 */     rowVal[11] = s2b("true");
/* 5998 */     rowVal[12] = s2b("INT");
/* 5999 */     rowVal[13] = s2b("0");
/* 6000 */     rowVal[14] = s2b("0");
/* 6001 */     rowVal[15] = s2b("0");
/* 6002 */     rowVal[16] = s2b("0");
/* 6003 */     rowVal[17] = s2b("10");
/* 6004 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6006 */     rowVal = new byte[18][];
/* 6007 */     rowVal[0] = s2b("INT UNSIGNED");
/* 6008 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6011 */     rowVal[2] = s2b("10");
/* 6012 */     rowVal[3] = s2b("");
/* 6013 */     rowVal[4] = s2b("");
/* 6014 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6015 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6019 */     rowVal[7] = s2b("false");
/* 6020 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6024 */     rowVal[9] = s2b("true");
/* 6025 */     rowVal[10] = s2b("false");
/* 6026 */     rowVal[11] = s2b("true");
/* 6027 */     rowVal[12] = s2b("INT UNSIGNED");
/* 6028 */     rowVal[13] = s2b("0");
/* 6029 */     rowVal[14] = s2b("0");
/* 6030 */     rowVal[15] = s2b("0");
/* 6031 */     rowVal[16] = s2b("0");
/* 6032 */     rowVal[17] = s2b("10");
/* 6033 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6038 */     rowVal = new byte[18][];
/* 6039 */     rowVal[0] = s2b("MEDIUMINT");
/* 6040 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6043 */     rowVal[2] = s2b("7");
/* 6044 */     rowVal[3] = s2b("");
/* 6045 */     rowVal[4] = s2b("");
/* 6046 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 6047 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6051 */     rowVal[7] = s2b("false");
/* 6052 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6056 */     rowVal[9] = s2b("true");
/* 6057 */     rowVal[10] = s2b("false");
/* 6058 */     rowVal[11] = s2b("true");
/* 6059 */     rowVal[12] = s2b("MEDIUMINT");
/* 6060 */     rowVal[13] = s2b("0");
/* 6061 */     rowVal[14] = s2b("0");
/* 6062 */     rowVal[15] = s2b("0");
/* 6063 */     rowVal[16] = s2b("0");
/* 6064 */     rowVal[17] = s2b("10");
/* 6065 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6067 */     rowVal = new byte[18][];
/* 6068 */     rowVal[0] = s2b("MEDIUMINT UNSIGNED");
/* 6069 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6072 */     rowVal[2] = s2b("8");
/* 6073 */     rowVal[3] = s2b("");
/* 6074 */     rowVal[4] = s2b("");
/* 6075 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6076 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6080 */     rowVal[7] = s2b("false");
/* 6081 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6085 */     rowVal[9] = s2b("true");
/* 6086 */     rowVal[10] = s2b("false");
/* 6087 */     rowVal[11] = s2b("true");
/* 6088 */     rowVal[12] = s2b("MEDIUMINT UNSIGNED");
/* 6089 */     rowVal[13] = s2b("0");
/* 6090 */     rowVal[14] = s2b("0");
/* 6091 */     rowVal[15] = s2b("0");
/* 6092 */     rowVal[16] = s2b("0");
/* 6093 */     rowVal[17] = s2b("10");
/* 6094 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6099 */     rowVal = new byte[18][];
/* 6100 */     rowVal[0] = s2b("SMALLINT");
/* 6101 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */ 
/* 6104 */     rowVal[2] = s2b("5");
/* 6105 */     rowVal[3] = s2b("");
/* 6106 */     rowVal[4] = s2b("");
/* 6107 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 6108 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6112 */     rowVal[7] = s2b("false");
/* 6113 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6117 */     rowVal[9] = s2b("true");
/* 6118 */     rowVal[10] = s2b("false");
/* 6119 */     rowVal[11] = s2b("true");
/* 6120 */     rowVal[12] = s2b("SMALLINT");
/* 6121 */     rowVal[13] = s2b("0");
/* 6122 */     rowVal[14] = s2b("0");
/* 6123 */     rowVal[15] = s2b("0");
/* 6124 */     rowVal[16] = s2b("0");
/* 6125 */     rowVal[17] = s2b("10");
/* 6126 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6128 */     rowVal = new byte[18][];
/* 6129 */     rowVal[0] = s2b("SMALLINT UNSIGNED");
/* 6130 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */ 
/* 6133 */     rowVal[2] = s2b("5");
/* 6134 */     rowVal[3] = s2b("");
/* 6135 */     rowVal[4] = s2b("");
/* 6136 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6137 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6141 */     rowVal[7] = s2b("false");
/* 6142 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6146 */     rowVal[9] = s2b("true");
/* 6147 */     rowVal[10] = s2b("false");
/* 6148 */     rowVal[11] = s2b("true");
/* 6149 */     rowVal[12] = s2b("SMALLINT UNSIGNED");
/* 6150 */     rowVal[13] = s2b("0");
/* 6151 */     rowVal[14] = s2b("0");
/* 6152 */     rowVal[15] = s2b("0");
/* 6153 */     rowVal[16] = s2b("0");
/* 6154 */     rowVal[17] = s2b("10");
/* 6155 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6161 */     rowVal = new byte[18][];
/* 6162 */     rowVal[0] = s2b("FLOAT");
/* 6163 */     rowVal[1] = Integer.toString(7).getBytes();
/*      */ 
/* 6166 */     rowVal[2] = s2b("10");
/* 6167 */     rowVal[3] = s2b("");
/* 6168 */     rowVal[4] = s2b("");
/* 6169 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6170 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6174 */     rowVal[7] = s2b("false");
/* 6175 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6179 */     rowVal[9] = s2b("false");
/* 6180 */     rowVal[10] = s2b("false");
/* 6181 */     rowVal[11] = s2b("true");
/* 6182 */     rowVal[12] = s2b("FLOAT");
/* 6183 */     rowVal[13] = s2b("-38");
/* 6184 */     rowVal[14] = s2b("38");
/* 6185 */     rowVal[15] = s2b("0");
/* 6186 */     rowVal[16] = s2b("0");
/* 6187 */     rowVal[17] = s2b("10");
/* 6188 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6193 */     rowVal = new byte[18][];
/* 6194 */     rowVal[0] = s2b("DOUBLE");
/* 6195 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 6198 */     rowVal[2] = s2b("17");
/* 6199 */     rowVal[3] = s2b("");
/* 6200 */     rowVal[4] = s2b("");
/* 6201 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6202 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6206 */     rowVal[7] = s2b("false");
/* 6207 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6211 */     rowVal[9] = s2b("false");
/* 6212 */     rowVal[10] = s2b("false");
/* 6213 */     rowVal[11] = s2b("true");
/* 6214 */     rowVal[12] = s2b("DOUBLE");
/* 6215 */     rowVal[13] = s2b("-308");
/* 6216 */     rowVal[14] = s2b("308");
/* 6217 */     rowVal[15] = s2b("0");
/* 6218 */     rowVal[16] = s2b("0");
/* 6219 */     rowVal[17] = s2b("10");
/* 6220 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6225 */     rowVal = new byte[18][];
/* 6226 */     rowVal[0] = s2b("DOUBLE PRECISION");
/* 6227 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 6230 */     rowVal[2] = s2b("17");
/* 6231 */     rowVal[3] = s2b("");
/* 6232 */     rowVal[4] = s2b("");
/* 6233 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6234 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6238 */     rowVal[7] = s2b("false");
/* 6239 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6243 */     rowVal[9] = s2b("false");
/* 6244 */     rowVal[10] = s2b("false");
/* 6245 */     rowVal[11] = s2b("true");
/* 6246 */     rowVal[12] = s2b("DOUBLE PRECISION");
/* 6247 */     rowVal[13] = s2b("-308");
/* 6248 */     rowVal[14] = s2b("308");
/* 6249 */     rowVal[15] = s2b("0");
/* 6250 */     rowVal[16] = s2b("0");
/* 6251 */     rowVal[17] = s2b("10");
/* 6252 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6257 */     rowVal = new byte[18][];
/* 6258 */     rowVal[0] = s2b("REAL");
/* 6259 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 6262 */     rowVal[2] = s2b("17");
/* 6263 */     rowVal[3] = s2b("");
/* 6264 */     rowVal[4] = s2b("");
/* 6265 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6266 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6270 */     rowVal[7] = s2b("false");
/* 6271 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6275 */     rowVal[9] = s2b("false");
/* 6276 */     rowVal[10] = s2b("false");
/* 6277 */     rowVal[11] = s2b("true");
/* 6278 */     rowVal[12] = s2b("REAL");
/* 6279 */     rowVal[13] = s2b("-308");
/* 6280 */     rowVal[14] = s2b("308");
/* 6281 */     rowVal[15] = s2b("0");
/* 6282 */     rowVal[16] = s2b("0");
/* 6283 */     rowVal[17] = s2b("10");
/* 6284 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6289 */     rowVal = new byte[18][];
/* 6290 */     rowVal[0] = s2b("VARCHAR");
/* 6291 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 6294 */     rowVal[2] = s2b("255");
/* 6295 */     rowVal[3] = s2b("'");
/* 6296 */     rowVal[4] = s2b("'");
/* 6297 */     rowVal[5] = s2b("(M)");
/* 6298 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6302 */     rowVal[7] = s2b("false");
/* 6303 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6307 */     rowVal[9] = s2b("false");
/* 6308 */     rowVal[10] = s2b("false");
/* 6309 */     rowVal[11] = s2b("false");
/* 6310 */     rowVal[12] = s2b("VARCHAR");
/* 6311 */     rowVal[13] = s2b("0");
/* 6312 */     rowVal[14] = s2b("0");
/* 6313 */     rowVal[15] = s2b("0");
/* 6314 */     rowVal[16] = s2b("0");
/* 6315 */     rowVal[17] = s2b("10");
/* 6316 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6321 */     rowVal = new byte[18][];
/* 6322 */     rowVal[0] = s2b("ENUM");
/* 6323 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 6326 */     rowVal[2] = s2b("65535");
/* 6327 */     rowVal[3] = s2b("'");
/* 6328 */     rowVal[4] = s2b("'");
/* 6329 */     rowVal[5] = s2b("");
/* 6330 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6334 */     rowVal[7] = s2b("false");
/* 6335 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6339 */     rowVal[9] = s2b("false");
/* 6340 */     rowVal[10] = s2b("false");
/* 6341 */     rowVal[11] = s2b("false");
/* 6342 */     rowVal[12] = s2b("ENUM");
/* 6343 */     rowVal[13] = s2b("0");
/* 6344 */     rowVal[14] = s2b("0");
/* 6345 */     rowVal[15] = s2b("0");
/* 6346 */     rowVal[16] = s2b("0");
/* 6347 */     rowVal[17] = s2b("10");
/* 6348 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6353 */     rowVal = new byte[18][];
/* 6354 */     rowVal[0] = s2b("SET");
/* 6355 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 6358 */     rowVal[2] = s2b("64");
/* 6359 */     rowVal[3] = s2b("'");
/* 6360 */     rowVal[4] = s2b("'");
/* 6361 */     rowVal[5] = s2b("");
/* 6362 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6366 */     rowVal[7] = s2b("false");
/* 6367 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6371 */     rowVal[9] = s2b("false");
/* 6372 */     rowVal[10] = s2b("false");
/* 6373 */     rowVal[11] = s2b("false");
/* 6374 */     rowVal[12] = s2b("SET");
/* 6375 */     rowVal[13] = s2b("0");
/* 6376 */     rowVal[14] = s2b("0");
/* 6377 */     rowVal[15] = s2b("0");
/* 6378 */     rowVal[16] = s2b("0");
/* 6379 */     rowVal[17] = s2b("10");
/* 6380 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6385 */     rowVal = new byte[18][];
/* 6386 */     rowVal[0] = s2b("DATE");
/* 6387 */     rowVal[1] = Integer.toString(91).getBytes();
/*      */ 
/* 6390 */     rowVal[2] = s2b("0");
/* 6391 */     rowVal[3] = s2b("'");
/* 6392 */     rowVal[4] = s2b("'");
/* 6393 */     rowVal[5] = s2b("");
/* 6394 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6398 */     rowVal[7] = s2b("false");
/* 6399 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6403 */     rowVal[9] = s2b("false");
/* 6404 */     rowVal[10] = s2b("false");
/* 6405 */     rowVal[11] = s2b("false");
/* 6406 */     rowVal[12] = s2b("DATE");
/* 6407 */     rowVal[13] = s2b("0");
/* 6408 */     rowVal[14] = s2b("0");
/* 6409 */     rowVal[15] = s2b("0");
/* 6410 */     rowVal[16] = s2b("0");
/* 6411 */     rowVal[17] = s2b("10");
/* 6412 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6417 */     rowVal = new byte[18][];
/* 6418 */     rowVal[0] = s2b("TIME");
/* 6419 */     rowVal[1] = Integer.toString(92).getBytes();
/*      */ 
/* 6422 */     rowVal[2] = s2b("0");
/* 6423 */     rowVal[3] = s2b("'");
/* 6424 */     rowVal[4] = s2b("'");
/* 6425 */     rowVal[5] = s2b("");
/* 6426 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6430 */     rowVal[7] = s2b("false");
/* 6431 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6435 */     rowVal[9] = s2b("false");
/* 6436 */     rowVal[10] = s2b("false");
/* 6437 */     rowVal[11] = s2b("false");
/* 6438 */     rowVal[12] = s2b("TIME");
/* 6439 */     rowVal[13] = s2b("0");
/* 6440 */     rowVal[14] = s2b("0");
/* 6441 */     rowVal[15] = s2b("0");
/* 6442 */     rowVal[16] = s2b("0");
/* 6443 */     rowVal[17] = s2b("10");
/* 6444 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6449 */     rowVal = new byte[18][];
/* 6450 */     rowVal[0] = s2b("DATETIME");
/* 6451 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */ 
/* 6454 */     rowVal[2] = s2b("0");
/* 6455 */     rowVal[3] = s2b("'");
/* 6456 */     rowVal[4] = s2b("'");
/* 6457 */     rowVal[5] = s2b("");
/* 6458 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6462 */     rowVal[7] = s2b("false");
/* 6463 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6467 */     rowVal[9] = s2b("false");
/* 6468 */     rowVal[10] = s2b("false");
/* 6469 */     rowVal[11] = s2b("false");
/* 6470 */     rowVal[12] = s2b("DATETIME");
/* 6471 */     rowVal[13] = s2b("0");
/* 6472 */     rowVal[14] = s2b("0");
/* 6473 */     rowVal[15] = s2b("0");
/* 6474 */     rowVal[16] = s2b("0");
/* 6475 */     rowVal[17] = s2b("10");
/* 6476 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6481 */     rowVal = new byte[18][];
/* 6482 */     rowVal[0] = s2b("TIMESTAMP");
/* 6483 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */ 
/* 6486 */     rowVal[2] = s2b("0");
/* 6487 */     rowVal[3] = s2b("'");
/* 6488 */     rowVal[4] = s2b("'");
/* 6489 */     rowVal[5] = s2b("[(M)]");
/* 6490 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6494 */     rowVal[7] = s2b("false");
/* 6495 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6499 */     rowVal[9] = s2b("false");
/* 6500 */     rowVal[10] = s2b("false");
/* 6501 */     rowVal[11] = s2b("false");
/* 6502 */     rowVal[12] = s2b("TIMESTAMP");
/* 6503 */     rowVal[13] = s2b("0");
/* 6504 */     rowVal[14] = s2b("0");
/* 6505 */     rowVal[15] = s2b("0");
/* 6506 */     rowVal[16] = s2b("0");
/* 6507 */     rowVal[17] = s2b("10");
/* 6508 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6510 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
/*      */     throws SQLException
/*      */   {
/* 6556 */     Field[] fields = new Field[6];
/* 6557 */     fields[0] = new Field("", "TYPE_CAT", 12, 32);
/* 6558 */     fields[1] = new Field("", "TYPE_SCHEM", 12, 32);
/* 6559 */     fields[2] = new Field("", "TYPE_NAME", 12, 32);
/* 6560 */     fields[3] = new Field("", "CLASS_NAME", 12, 32);
/* 6561 */     fields[4] = new Field("", "DATA_TYPE", 12, 32);
/* 6562 */     fields[5] = new Field("", "REMARKS", 12, 32);
/*      */ 
/* 6564 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 6566 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public String getURL()
/*      */     throws SQLException
/*      */   {
/* 6577 */     return this.conn.getURL();
/*      */   }
/*      */ 
/*      */   public String getUserName()
/*      */     throws SQLException
/*      */   {
/* 6588 */     if (this.conn.getUseHostsInPrivileges()) {
/* 6589 */       Statement stmt = null;
/* 6590 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 6593 */         stmt = this.conn.createStatement();
/* 6594 */         stmt.setEscapeProcessing(false);
/*      */ 
/* 6596 */         rs = stmt.executeQuery("SELECT USER()");
/* 6597 */         rs.next();
/*      */ 
/* 6599 */         str = rs.getString(1);
/*      */       }
/*      */       finally
/*      */       {
/*      */         String str;
/* 6601 */         if (rs != null) {
/*      */           try {
/* 6603 */             rs.close();
/*      */           } catch (Exception ex) {
/* 6605 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */ 
/* 6608 */           rs = null;
/*      */         }
/*      */ 
/* 6611 */         if (stmt != null) {
/*      */           try {
/* 6613 */             stmt.close();
/*      */           } catch (Exception ex) {
/* 6615 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */ 
/* 6618 */           stmt = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 6623 */     return this.conn.getUser();
/*      */   }
/*      */ 
/*      */   public ResultSet getVersionColumns(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 6662 */     Field[] fields = new Field[8];
/* 6663 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 6664 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 6665 */     fields[2] = new Field("", "DATA_TYPE", 4, 5);
/* 6666 */     fields[3] = new Field("", "TYPE_NAME", 1, 16);
/* 6667 */     fields[4] = new Field("", "COLUMN_SIZE", 4, 16);
/* 6668 */     fields[5] = new Field("", "BUFFER_LENGTH", 4, 16);
/* 6669 */     fields[6] = new Field("", "DECIMAL_DIGITS", 5, 16);
/* 6670 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */ 
/* 6672 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public boolean insertsAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 6688 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isCatalogAtStart()
/*      */     throws SQLException
/*      */   {
/* 6700 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 6711 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean locatorsUpdateCopy()
/*      */     throws SQLException
/*      */   {
/* 6718 */     return !this.conn.getEmulateLocators();
/*      */   }
/*      */ 
/*      */   public boolean nullPlusNonNullIsNull()
/*      */     throws SQLException
/*      */   {
/* 6730 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedAtEnd()
/*      */     throws SQLException
/*      */   {
/* 6741 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedAtStart()
/*      */     throws SQLException
/*      */   {
/* 6752 */     return (this.conn.versionMeetsMinimum(4, 0, 2)) && (!this.conn.versionMeetsMinimum(4, 0, 11));
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedHigh()
/*      */     throws SQLException
/*      */   {
/* 6764 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedLow()
/*      */     throws SQLException
/*      */   {
/* 6775 */     return !nullsAreSortedHigh();
/*      */   }
/*      */ 
/*      */   public boolean othersDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6788 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean othersInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6801 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean othersUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6814 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6827 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6840 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6853 */     return false;
/*      */   }
/*      */ 
/*      */   private LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment)
/*      */     throws SQLException
/*      */   {
/* 6874 */     String columnsDelimitter = ",";
/*      */ 
/* 6876 */     char quoteChar = this.quotedId.length() == 0 ? '\000' : this.quotedId.charAt(0);
/*      */ 
/* 6879 */     int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysComment, "(", quoteChar, true);
/*      */ 
/* 6883 */     if (indexOfOpenParenLocalColumns == -1) {
/* 6884 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of local columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 6889 */     String constraintName = removeQuotedId(keysComment.substring(0, indexOfOpenParenLocalColumns).trim());
/*      */ 
/* 6891 */     keysComment = keysComment.substring(indexOfOpenParenLocalColumns, keysComment.length());
/*      */ 
/* 6894 */     String keysCommentTrimmed = keysComment.trim();
/*      */ 
/* 6896 */     int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysCommentTrimmed, ")", quoteChar, true);
/*      */ 
/* 6900 */     if (indexOfCloseParenLocalColumns == -1) {
/* 6901 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of local columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 6906 */     String localColumnNamesString = keysCommentTrimmed.substring(1, indexOfCloseParenLocalColumns);
/*      */ 
/* 6909 */     int indexOfRefer = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysCommentTrimmed, "REFER ", this.quotedId.charAt(0), true);
/*      */ 
/* 6912 */     if (indexOfRefer == -1) {
/* 6913 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced tables list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 6918 */     int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfRefer, keysCommentTrimmed, "(", quoteChar, false);
/*      */ 
/* 6922 */     if (indexOfOpenParenReferCol == -1) {
/* 6923 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 6928 */     String referCatalogTableString = keysCommentTrimmed.substring(indexOfRefer + "REFER ".length(), indexOfOpenParenReferCol);
/*      */ 
/* 6931 */     int indexOfSlash = StringUtils.indexOfIgnoreCaseRespectQuotes(0, referCatalogTableString, "/", this.quotedId.charAt(0), false);
/*      */ 
/* 6934 */     if (indexOfSlash == -1) {
/* 6935 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find name of referenced catalog.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 6940 */     String referCatalog = removeQuotedId(referCatalogTableString.substring(0, indexOfSlash));
/*      */ 
/* 6942 */     String referTable = removeQuotedId(referCatalogTableString.substring(indexOfSlash + 1).trim());
/*      */ 
/* 6945 */     int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfOpenParenReferCol, keysCommentTrimmed, ")", quoteChar, true);
/*      */ 
/* 6949 */     if (indexOfCloseParenRefer == -1) {
/* 6950 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of referenced columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 6955 */     String referColumnNamesString = keysCommentTrimmed.substring(indexOfOpenParenReferCol + 1, indexOfCloseParenRefer);
/*      */ 
/* 6958 */     List referColumnsList = StringUtils.split(referColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */ 
/* 6960 */     List localColumnsList = StringUtils.split(localColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */ 
/* 6963 */     return new LocalAndReferencedColumns(localColumnsList, referColumnsList, constraintName, referCatalog, referTable);
/*      */   }
/*      */ 
/*      */   private String removeQuotedId(String s)
/*      */   {
/* 6968 */     if (s == null) {
/* 6969 */       return null;
/*      */     }
/*      */ 
/* 6972 */     if (this.quotedId.equals("")) {
/* 6973 */       return s;
/*      */     }
/*      */ 
/* 6976 */     s = s.trim();
/*      */ 
/* 6978 */     int frontOffset = 0;
/* 6979 */     int backOffset = s.length();
/* 6980 */     int quoteLength = this.quotedId.length();
/*      */ 
/* 6982 */     if (s.startsWith(this.quotedId)) {
/* 6983 */       frontOffset = quoteLength;
/*      */     }
/*      */ 
/* 6986 */     if (s.endsWith(this.quotedId)) {
/* 6987 */       backOffset -= quoteLength;
/*      */     }
/*      */ 
/* 6990 */     return s.substring(frontOffset, backOffset);
/*      */   }
/*      */ 
/*      */   protected byte[] s2b(String s)
/*      */     throws SQLException
/*      */   {
/* 7002 */     if (s == null) {
/* 7003 */       return null;
/*      */     }
/*      */ 
/* 7006 */     return StringUtils.getBytes(s, this.conn.getCharacterSetMetadata(), this.conn.getServerCharacterEncoding(), this.conn.parserKnowsUnicode(), this.conn, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7020 */     return this.conn.storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7032 */     return this.conn.storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean storesMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7044 */     return !this.conn.storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean storesMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7055 */     return !this.conn.storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean storesUpperCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7067 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean storesUpperCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7079 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsAlterTableWithAddColumn()
/*      */     throws SQLException
/*      */   {
/* 7090 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsAlterTableWithDropColumn()
/*      */     throws SQLException
/*      */   {
/* 7101 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92EntryLevelSQL()
/*      */     throws SQLException
/*      */   {
/* 7113 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92FullSQL()
/*      */     throws SQLException
/*      */   {
/* 7124 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92IntermediateSQL()
/*      */     throws SQLException
/*      */   {
/* 7135 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsBatchUpdates()
/*      */     throws SQLException
/*      */   {
/* 7147 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 7159 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7171 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7183 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 7195 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7207 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsColumnAliasing()
/*      */     throws SQLException
/*      */   {
/* 7223 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsConvert()
/*      */     throws SQLException
/*      */   {
/* 7234 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsConvert(int fromType, int toType)
/*      */     throws SQLException
/*      */   {
/* 7251 */     switch (fromType)
/*      */     {
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/* 7262 */       switch (toType) {
/*      */       case -6:
/*      */       case -5:
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 12:
/*      */       case 91:
/*      */       case 92:
/*      */       case 93:
/*      */       case 1111:
/* 7282 */         return true;
/*      */       }
/*      */ 
/* 7285 */       return false;
/*      */     case -7:
/* 7292 */       return false;
/*      */     case -6:
/*      */     case -5:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/* 7308 */       switch (toType) {
/*      */       case -6:
/*      */       case -5:
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 12:
/* 7324 */         return true;
/*      */       case 0:
/*      */       case 9:
/*      */       case 10:
/* 7327 */       case 11: } return false;
/*      */     case 0:
/* 7332 */       return false;
/*      */     case 1111:
/* 7340 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 7347 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 7350 */       case 11: } return false;
/*      */     case 91:
/* 7356 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 7363 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 7366 */       case 11: } return false;
/*      */     case 92:
/* 7372 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 7379 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 7382 */       case 11: } return false;
/*      */     case 93:
/* 7391 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/*      */       case 91:
/*      */       case 92:
/* 7400 */         return true;
/*      */       }
/*      */ 
/* 7403 */       return false;
/*      */     }
/*      */ 
/* 7408 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsCoreSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7420 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsCorrelatedSubqueries()
/*      */     throws SQLException
/*      */   {
/* 7432 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions()
/*      */     throws SQLException
/*      */   {
/* 7445 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsDataManipulationTransactionsOnly()
/*      */     throws SQLException
/*      */   {
/* 7457 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsDifferentTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 7470 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsExpressionsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 7481 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsExtendedSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7492 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsFullOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7503 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsGetGeneratedKeys()
/*      */   {
/* 7512 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupBy()
/*      */     throws SQLException
/*      */   {
/* 7523 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupByBeyondSelect()
/*      */     throws SQLException
/*      */   {
/* 7535 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 7546 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsIntegrityEnhancementFacility()
/*      */     throws SQLException
/*      */   {
/* 7558 */     return this.conn.getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */ 
/*      */   public boolean supportsLikeEscapeClause()
/*      */     throws SQLException
/*      */   {
/* 7573 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsLimitedOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7585 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMinimumSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7597 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7608 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean supportsMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7620 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleOpenResults()
/*      */     throws SQLException
/*      */   {
/* 7627 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleResultSets()
/*      */     throws SQLException
/*      */   {
/* 7638 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleTransactions()
/*      */     throws SQLException
/*      */   {
/* 7650 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsNamedParameters()
/*      */     throws SQLException
/*      */   {
/* 7657 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsNonNullableColumns()
/*      */     throws SQLException
/*      */   {
/* 7669 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 7681 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 7693 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 7705 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 7717 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOrderByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 7728 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7739 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsPositionedDelete()
/*      */     throws SQLException
/*      */   {
/* 7750 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsPositionedUpdate()
/*      */     throws SQLException
/*      */   {
/* 7761 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetConcurrency(int type, int concurrency)
/*      */     throws SQLException
/*      */   {
/* 7779 */     switch (type) {
/*      */     case 1004:
/* 7781 */       if ((concurrency == 1007) || (concurrency == 1008))
/*      */       {
/* 7783 */         return true;
/*      */       }
/* 7785 */       throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */     case 1003:
/* 7790 */       if ((concurrency == 1007) || (concurrency == 1008))
/*      */       {
/* 7792 */         return true;
/*      */       }
/* 7794 */       throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */     case 1005:
/* 7799 */       return false;
/*      */     }
/* 7801 */     throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetHoldability(int holdability)
/*      */     throws SQLException
/*      */   {
/* 7813 */     return holdability == 1;
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetType(int type)
/*      */     throws SQLException
/*      */   {
/* 7827 */     return type == 1004;
/*      */   }
/*      */ 
/*      */   public boolean supportsSavepoints()
/*      */     throws SQLException
/*      */   {
/* 7835 */     return (this.conn.versionMeetsMinimum(4, 0, 14)) || (this.conn.versionMeetsMinimum(4, 1, 1));
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 7847 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7858 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7869 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 7880 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7891 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSelectForUpdate()
/*      */     throws SQLException
/*      */   {
/* 7902 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsStatementPooling()
/*      */     throws SQLException
/*      */   {
/* 7909 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsStoredProcedures()
/*      */     throws SQLException
/*      */   {
/* 7921 */     return this.conn.versionMeetsMinimum(5, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInComparisons()
/*      */     throws SQLException
/*      */   {
/* 7933 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInExists()
/*      */     throws SQLException
/*      */   {
/* 7945 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInIns()
/*      */     throws SQLException
/*      */   {
/* 7957 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInQuantifieds()
/*      */     throws SQLException
/*      */   {
/* 7969 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 7981 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactionIsolationLevel(int level)
/*      */     throws SQLException
/*      */   {
/* 7996 */     if (this.conn.supportsIsolationLevel()) {
/* 7997 */       switch (level) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 8:
/* 8002 */         return true;
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/* 8005 */       case 7: } return false;
/*      */     }
/*      */ 
/* 8009 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */     throws SQLException
/*      */   {
/* 8021 */     return this.conn.supportsTransactions();
/*      */   }
/*      */ 
/*      */   public boolean supportsUnion()
/*      */     throws SQLException
/*      */   {
/* 8032 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsUnionAll()
/*      */     throws SQLException
/*      */   {
/* 8043 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean updatesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 8057 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean usesLocalFilePerTable()
/*      */     throws SQLException
/*      */   {
/* 8068 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean usesLocalFiles()
/*      */     throws SQLException
/*      */   {
/* 8079 */     return false;
/*      */   }
/*      */ 
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 8095 */     Field[] fields = createFunctionColumnsFields();
/*      */ 
/* 8097 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, functionNamePattern, columnNamePattern, false, true);
/*      */   }
/*      */ 
/*      */   protected Field[] createFunctionColumnsFields()
/*      */   {
/* 8104 */     Field[] fields = { new Field("", "FUNCTION_CAT", 12, 0), new Field("", "FUNCTION_SCHEM", 12, 0), new Field("", "FUNCTION_NAME", 12, 0), new Field("", "COLUMN_NAME", 12, 0), new Field("", "COLUMN_TYPE", 12, 0), new Field("", "DATA_TYPE", 5, 0), new Field("", "TYPE_NAME", 12, 0), new Field("", "PRECISION", 4, 0), new Field("", "LENGTH", 4, 0), new Field("", "SCALE", 5, 0), new Field("", "RADIX", 5, 0), new Field("", "NULLABLE", 5, 0), new Field("", "REMARKS", 12, 0), new Field("", "CHAR_OCTET_LENGTH", 4, 0), new Field("", "ORDINAL_POSITION", 4, 0), new Field("", "IS_NULLABLE", 12, 3), new Field("", "SPECIFIC_NAME", 12, 0) };
/*      */ 
/* 8122 */     return fields;
/*      */   }
/*      */ 
/*      */   public boolean providesQueryObjectGenerator() throws SQLException {
/* 8126 */     return false;
/*      */   }
/*      */ 
/*      */   public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException
/*      */   {
/* 8131 */     Field[] fields = { new Field("", "TABLE_SCHEM", 12, 255), new Field("", "TABLE_CATALOG", 12, 255) };
/*      */ 
/* 8136 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
/* 8140 */     return true;
/*      */   }
/*      */ 
/*      */   protected PreparedStatement prepareMetaDataSafeStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 8153 */     PreparedStatement pStmt = (PreparedStatement)this.conn.clientPrepareStatement(sql);
/*      */ 
/* 8156 */     if (pStmt.getMaxRows() != 0) {
/* 8157 */       pStmt.setMaxRows(0);
/*      */     }
/*      */ 
/* 8160 */     pStmt.setHoldResultsOpenOverClose(true);
/*      */ 
/* 8162 */     return pStmt;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  475 */     if (Util.isJdbc4()) {
/*      */       try {
/*  477 */         JDBC_4_DBMD_SHOW_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaData").getConstructor(new Class[] { ConnectionImpl.class, String.class });
/*      */ 
/*  481 */         JDBC_4_DBMD_IS_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema").getConstructor(new Class[] { ConnectionImpl.class, String.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  487 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  489 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  491 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  494 */       JDBC_4_DBMD_IS_CTOR = null;
/*  495 */       JDBC_4_DBMD_SHOW_CTOR = null;
/*      */     }
/*      */ 
/*  499 */     String[] allMySQLKeywords = { "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS", "READ_ONLY", "READ_WRITE", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "X509", "XOR", "YEAR_MONTH", "ZEROFILL" };
/*      */ 
/*  542 */     String[] sql92Keywords = { "ABSOLUTE", "EXEC", "OVERLAPS", "ACTION", "EXECUTE", "PAD", "ADA", "EXISTS", "PARTIAL", "ADD", "EXTERNAL", "PASCAL", "ALL", "EXTRACT", "POSITION", "ALLOCATE", "FALSE", "PRECISION", "ALTER", "FETCH", "PREPARE", "AND", "FIRST", "PRESERVE", "ANY", "FLOAT", "PRIMARY", "ARE", "FOR", "PRIOR", "AS", "FOREIGN", "PRIVILEGES", "ASC", "FORTRAN", "PROCEDURE", "ASSERTION", "FOUND", "PUBLIC", "AT", "FROM", "READ", "AUTHORIZATION", "FULL", "REAL", "AVG", "GET", "REFERENCES", "BEGIN", "GLOBAL", "RELATIVE", "BETWEEN", "GO", "RESTRICT", "BIT", "GOTO", "REVOKE", "BIT_LENGTH", "GRANT", "RIGHT", "BOTH", "GROUP", "ROLLBACK", "BY", "HAVING", "ROWS", "CASCADE", "HOUR", "SCHEMA", "CASCADED", "IDENTITY", "SCROLL", "CASE", "IMMEDIATE", "SECOND", "CAST", "IN", "SECTION", "CATALOG", "INCLUDE", "SELECT", "CHAR", "INDEX", "SESSION", "CHAR_LENGTH", "INDICATOR", "SESSION_USER", "CHARACTER", "INITIALLY", "SET", "CHARACTER_LENGTH", "INNER", "SIZE", "CHECK", "INPUT", "SMALLINT", "CLOSE", "INSENSITIVE", "SOME", "COALESCE", "INSERT", "SPACE", "COLLATE", "INT", "SQL", "COLLATION", "INTEGER", "SQLCA", "COLUMN", "INTERSECT", "SQLCODE", "COMMIT", "INTERVAL", "SQLERROR", "CONNECT", "INTO", "SQLSTATE", "CONNECTION", "IS", "SQLWARNING", "CONSTRAINT", "ISOLATION", "SUBSTRING", "CONSTRAINTS", "JOIN", "SUM", "CONTINUE", "KEY", "SYSTEM_USER", "CONVERT", "LANGUAGE", "TABLE", "CORRESPONDING", "LAST", "TEMPORARY", "COUNT", "LEADING", "THEN", "CREATE", "LEFT", "TIME", "CROSS", "LEVEL", "TIMESTAMP", "CURRENT", "LIKE", "TIMEZONE_HOUR", "CURRENT_DATE", "LOCAL", "TIMEZONE_MINUTE", "CURRENT_TIME", "LOWER", "TO", "CURRENT_TIMESTAMP", "MATCH", "TRAILING", "CURRENT_USER", "MAX", "TRANSACTION", "CURSOR", "MIN", "TRANSLATE", "DATE", "MINUTE", "TRANSLATION", "DAY", "MODULE", "TRIM", "DEALLOCATE", "MONTH", "TRUE", "DEC", "NAMES", "UNION", "DECIMAL", "NATIONAL", "UNIQUE", "DECLARE", "NATURAL", "UNKNOWN", "DEFAULT", "NCHAR", "UPDATE", "DEFERRABLE", "NEXT", "UPPER", "DEFERRED", "NO", "USAGE", "DELETE", "NONE", "USER", "DESC", "NOT", "USING", "DESCRIBE", "NULL", "VALUE", "DESCRIPTOR", "NULLIF", "VALUES", "DIAGNOSTICS", "NUMERIC", "VARCHAR", "DISCONNECT", "OCTET_LENGTH", "VARYING", "DISTINCT", "OF", "VIEW", "DOMAIN", "ON", "WHEN", "DOUBLE", "ONLY", "WHENEVER", "DROP", "OPEN", "WHERE", "ELSE", "OPTION", "WITH", "END", "OR", "WORK", "END-EXEC", "ORDER", "WRITE", "ESCAPE", "OUTER", "YEAR", "EXCEPT", "OUTPUT", "ZONE", "EXCEPTION" };
/*      */ 
/*  584 */     TreeMap mySQLKeywordMap = new TreeMap();
/*      */ 
/*  586 */     for (int i = 0; i < allMySQLKeywords.length; i++) {
/*  587 */       mySQLKeywordMap.put(allMySQLKeywords[i], null);
/*      */     }
/*      */ 
/*  590 */     HashMap sql92KeywordMap = new HashMap(sql92Keywords.length);
/*      */ 
/*  592 */     for (int i = 0; i < sql92Keywords.length; i++) {
/*  593 */       sql92KeywordMap.put(sql92Keywords[i], null);
/*      */     }
/*      */ 
/*  596 */     Iterator it = sql92KeywordMap.keySet().iterator();
/*      */ 
/*  598 */     while (it.hasNext()) {
/*  599 */       mySQLKeywordMap.remove(it.next());
/*      */     }
/*      */ 
/*  602 */     StringBuffer keywordBuf = new StringBuffer();
/*      */ 
/*  604 */     it = mySQLKeywordMap.keySet().iterator();
/*      */ 
/*  606 */     if (it.hasNext()) {
/*  607 */       keywordBuf.append(it.next().toString());
/*      */     }
/*      */ 
/*  610 */     while (it.hasNext()) {
/*  611 */       keywordBuf.append(",");
/*  612 */       keywordBuf.append(it.next().toString());
/*      */     }
/*      */ 
/*  615 */     mysqlKeywordsThatArentSQL92 = keywordBuf.toString();
/*      */   }
/*      */ 
/*      */   class TypeDescriptor
/*      */   {
/*      */     int bufferLength;
/*      */     int charOctetLength;
/*      */     Integer columnSize;
/*      */     short dataType;
/*      */     Integer decimalDigits;
/*      */     String isNullable;
/*      */     int nullability;
/*  164 */     int numPrecRadix = 10;
/*      */     String typeName;
/*      */ 
/*      */     TypeDescriptor(String typeInfo, String nullabilityInfo)
/*      */       throws SQLException
/*      */     {
/*  170 */       if (typeInfo == null) {
/*  171 */         throw SQLError.createSQLException("NULL typeinfo not supported.", "S1009", DatabaseMetaData.this.getExceptionInterceptor());
/*      */       }
/*      */ 
/*  175 */       String mysqlType = "";
/*  176 */       String fullMysqlType = null;
/*      */ 
/*  178 */       if (typeInfo.indexOf("(") != -1)
/*  179 */         mysqlType = typeInfo.substring(0, typeInfo.indexOf("("));
/*      */       else {
/*  181 */         mysqlType = typeInfo;
/*      */       }
/*      */ 
/*  184 */       int indexOfUnsignedInMysqlType = StringUtils.indexOfIgnoreCase(mysqlType, "unsigned");
/*      */ 
/*  187 */       if (indexOfUnsignedInMysqlType != -1) {
/*  188 */         mysqlType = mysqlType.substring(0, indexOfUnsignedInMysqlType - 1);
/*      */       }
/*      */ 
/*  195 */       boolean isUnsigned = false;
/*      */ 
/*  197 */       if (StringUtils.indexOfIgnoreCase(typeInfo, "unsigned") != -1) {
/*  198 */         fullMysqlType = mysqlType + " unsigned";
/*  199 */         isUnsigned = true;
/*      */       } else {
/*  201 */         fullMysqlType = mysqlType;
/*      */       }
/*      */ 
/*  204 */       if (DatabaseMetaData.this.conn.getCapitalizeTypeNames()) {
/*  205 */         fullMysqlType = fullMysqlType.toUpperCase(Locale.ENGLISH);
/*      */       }
/*      */ 
/*  208 */       this.dataType = (short)MysqlDefs.mysqlToJavaType(mysqlType);
/*      */ 
/*  210 */       this.typeName = fullMysqlType;
/*      */ 
/*  214 */       if (StringUtils.startsWithIgnoreCase(typeInfo, "enum")) {
/*  215 */         String temp = typeInfo.substring(typeInfo.indexOf("("), typeInfo.lastIndexOf(")"));
/*      */ 
/*  217 */         StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */ 
/*  219 */         int maxLength = 0;
/*      */ 
/*  221 */         while (tokenizer.hasMoreTokens()) {
/*  222 */           maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */         }
/*      */ 
/*  226 */         this.columnSize = Constants.integerValueOf(maxLength);
/*  227 */         this.decimalDigits = null;
/*  228 */       } else if (StringUtils.startsWithIgnoreCase(typeInfo, "set")) {
/*  229 */         String temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
/*      */ 
/*  231 */         StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */ 
/*  233 */         int maxLength = 0;
/*      */ 
/*  235 */         int numElements = tokenizer.countTokens();
/*      */ 
/*  237 */         if (numElements > 0) {
/*  238 */           maxLength += numElements - 1;
/*      */         }
/*      */ 
/*  241 */         while (tokenizer.hasMoreTokens()) {
/*  242 */           String setMember = tokenizer.nextToken().trim();
/*      */ 
/*  244 */           if ((setMember.startsWith("'")) && (setMember.endsWith("'")))
/*      */           {
/*  246 */             maxLength += setMember.length() - 2;
/*      */           }
/*  248 */           else maxLength += setMember.length();
/*      */ 
/*      */         }
/*      */ 
/*  252 */         this.columnSize = Constants.integerValueOf(maxLength);
/*  253 */         this.decimalDigits = null;
/*  254 */       } else if (typeInfo.indexOf(",") != -1)
/*      */       {
/*  256 */         this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
/*      */ 
/*  258 */         this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim());
/*      */       }
/*      */       else
/*      */       {
/*  262 */         this.columnSize = null;
/*  263 */         this.decimalDigits = null;
/*      */ 
/*  266 */         if (((StringUtils.indexOfIgnoreCase(typeInfo, "char") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "text") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "blob") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "binary") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "bit") != -1)) && (typeInfo.indexOf("(") != -1))
/*      */         {
/*  273 */           int endParenIndex = typeInfo.indexOf(")");
/*      */ 
/*  275 */           if (endParenIndex == -1) {
/*  276 */             endParenIndex = typeInfo.length();
/*      */           }
/*      */ 
/*  279 */           this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, endParenIndex).trim());
/*      */ 
/*  283 */           if ((DatabaseMetaData.this.conn.getTinyInt1isBit()) && (this.columnSize.intValue() == 1) && (StringUtils.startsWithIgnoreCase(typeInfo, 0, "tinyint")))
/*      */           {
/*  287 */             if (DatabaseMetaData.this.conn.getTransformedBitIsBoolean()) {
/*  288 */               this.dataType = 16;
/*  289 */               this.typeName = "BOOLEAN";
/*      */             } else {
/*  291 */               this.dataType = -7;
/*  292 */               this.typeName = "BIT";
/*      */             }
/*      */           }
/*  295 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinyint"))
/*      */         {
/*  297 */           if ((DatabaseMetaData.this.conn.getTinyInt1isBit()) && (typeInfo.indexOf("(1)") != -1)) {
/*  298 */             if (DatabaseMetaData.this.conn.getTransformedBitIsBoolean()) {
/*  299 */               this.dataType = 16;
/*  300 */               this.typeName = "BOOLEAN";
/*      */             } else {
/*  302 */               this.dataType = -7;
/*  303 */               this.typeName = "BIT";
/*      */             }
/*      */           } else {
/*  306 */             this.columnSize = Constants.integerValueOf(3);
/*  307 */             this.decimalDigits = Constants.integerValueOf(0);
/*      */           }
/*  309 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "smallint"))
/*      */         {
/*  311 */           this.columnSize = Constants.integerValueOf(5);
/*  312 */           this.decimalDigits = Constants.integerValueOf(0);
/*  313 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumint"))
/*      */         {
/*  315 */           this.columnSize = Constants.integerValueOf(isUnsigned ? 8 : 7);
/*  316 */           this.decimalDigits = Constants.integerValueOf(0);
/*  317 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "int"))
/*      */         {
/*  319 */           this.columnSize = Constants.integerValueOf(10);
/*  320 */           this.decimalDigits = Constants.integerValueOf(0);
/*  321 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "integer"))
/*      */         {
/*  323 */           this.columnSize = Constants.integerValueOf(10);
/*  324 */           this.decimalDigits = Constants.integerValueOf(0);
/*  325 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "bigint"))
/*      */         {
/*  327 */           this.columnSize = Constants.integerValueOf(isUnsigned ? 20 : 19);
/*  328 */           this.decimalDigits = Constants.integerValueOf(0);
/*  329 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "int24"))
/*      */         {
/*  331 */           this.columnSize = Constants.integerValueOf(19);
/*  332 */           this.decimalDigits = Constants.integerValueOf(0);
/*  333 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "real"))
/*      */         {
/*  335 */           this.columnSize = Constants.integerValueOf(12);
/*  336 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "float"))
/*      */         {
/*  338 */           this.columnSize = Constants.integerValueOf(12);
/*  339 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "decimal"))
/*      */         {
/*  341 */           this.columnSize = Constants.integerValueOf(12);
/*  342 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "numeric"))
/*      */         {
/*  344 */           this.columnSize = Constants.integerValueOf(12);
/*  345 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "double"))
/*      */         {
/*  347 */           this.columnSize = Constants.integerValueOf(22);
/*  348 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "char"))
/*      */         {
/*  350 */           this.columnSize = Constants.integerValueOf(1);
/*  351 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "varchar"))
/*      */         {
/*  353 */           this.columnSize = Constants.integerValueOf(255);
/*  354 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "timestamp"))
/*      */         {
/*  356 */           this.columnSize = Constants.integerValueOf(19);
/*  357 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "datetime"))
/*      */         {
/*  359 */           this.columnSize = Constants.integerValueOf(19);
/*  360 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "date"))
/*      */         {
/*  362 */           this.columnSize = Constants.integerValueOf(10);
/*  363 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "time"))
/*      */         {
/*  365 */           this.columnSize = Constants.integerValueOf(8);
/*      */         }
/*  367 */         else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinyblob"))
/*      */         {
/*  369 */           this.columnSize = Constants.integerValueOf(255);
/*  370 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "blob"))
/*      */         {
/*  372 */           this.columnSize = Constants.integerValueOf(65535);
/*  373 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumblob"))
/*      */         {
/*  375 */           this.columnSize = Constants.integerValueOf(16777215);
/*  376 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "longblob"))
/*      */         {
/*  378 */           this.columnSize = Constants.integerValueOf(2147483647);
/*  379 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinytext"))
/*      */         {
/*  381 */           this.columnSize = Constants.integerValueOf(255);
/*  382 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "text"))
/*      */         {
/*  384 */           this.columnSize = Constants.integerValueOf(65535);
/*  385 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumtext"))
/*      */         {
/*  387 */           this.columnSize = Constants.integerValueOf(16777215);
/*  388 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "longtext"))
/*      */         {
/*  390 */           this.columnSize = Constants.integerValueOf(2147483647);
/*  391 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "enum"))
/*      */         {
/*  393 */           this.columnSize = Constants.integerValueOf(255);
/*  394 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "set"))
/*      */         {
/*  396 */           this.columnSize = Constants.integerValueOf(255);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  402 */       this.bufferLength = MysqlIO.getMaxBuf();
/*      */ 
/*  405 */       this.numPrecRadix = 10;
/*      */ 
/*  408 */       if (nullabilityInfo != null) {
/*  409 */         if (nullabilityInfo.equals("YES")) {
/*  410 */           this.nullability = 1;
/*  411 */           this.isNullable = "YES";
/*      */         }
/*      */         else
/*      */         {
/*  415 */           this.nullability = 0;
/*  416 */           this.isNullable = "NO";
/*      */         }
/*      */       } else {
/*  419 */         this.nullability = 0;
/*  420 */         this.isNullable = "NO";
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class SingleStringIterator extends DatabaseMetaData.IteratorWithCleanup
/*      */   {
/*  122 */     boolean onFirst = true;
/*      */     String value;
/*      */ 
/*      */     SingleStringIterator(String s)
/*      */     {
/*  126 */       super();
/*  127 */       this.value = s;
/*      */     }
/*      */ 
/*      */     void close() throws SQLException
/*      */     {
/*      */     }
/*      */ 
/*      */     boolean hasNext() throws SQLException
/*      */     {
/*  136 */       return this.onFirst;
/*      */     }
/*      */ 
/*      */     Object next() throws SQLException {
/*  140 */       this.onFirst = false;
/*  141 */       return this.value;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class ResultSetIterator extends DatabaseMetaData.IteratorWithCleanup
/*      */   {
/*      */     int colIndex;
/*      */     ResultSet resultSet;
/*      */ 
/*      */     ResultSetIterator(ResultSet rs, int index)
/*      */     {
/*  103 */       super();
/*  104 */       this.resultSet = rs;
/*  105 */       this.colIndex = index;
/*      */     }
/*      */ 
/*      */     void close() throws SQLException {
/*  109 */       this.resultSet.close();
/*      */     }
/*      */ 
/*      */     boolean hasNext() throws SQLException {
/*  113 */       return this.resultSet.next();
/*      */     }
/*      */ 
/*      */     Object next() throws SQLException {
/*  117 */       return this.resultSet.getObject(this.colIndex);
/*      */     }
/*      */   }
/*      */ 
/*      */   class LocalAndReferencedColumns
/*      */   {
/*      */     String constraintName;
/*      */     List localColumnsList;
/*      */     String referencedCatalog;
/*      */     List referencedColumnsList;
/*      */     String referencedTable;
/*      */ 
/*      */     LocalAndReferencedColumns(List localColumns, List refColumns, String constName, String refCatalog, String refTable)
/*      */     {
/*   90 */       this.localColumnsList = localColumns;
/*   91 */       this.referencedColumnsList = refColumns;
/*   92 */       this.constraintName = constName;
/*   93 */       this.referencedTable = refTable;
/*   94 */       this.referencedCatalog = refCatalog;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract class IteratorWithCleanup
/*      */   {
/*      */     protected IteratorWithCleanup()
/*      */     {
/*      */     }
/*      */ 
/*      */     abstract void close()
/*      */       throws SQLException;
/*      */ 
/*      */     abstract boolean hasNext()
/*      */       throws SQLException;
/*      */ 
/*      */     abstract Object next()
/*      */       throws SQLException;
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.DatabaseMetaData
 * JD-Core Version:    0.6.0
 */