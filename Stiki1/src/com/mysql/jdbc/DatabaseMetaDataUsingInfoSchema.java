/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ 
/*      */ public class DatabaseMetaDataUsingInfoSchema extends DatabaseMetaData
/*      */ {
/*      */   private boolean hasReferentialConstraintsView;
/*      */   private boolean hasParametersView;
/*      */ 
/*      */   protected DatabaseMetaDataUsingInfoSchema(ConnectionImpl connToSet, String databaseToSet)
/*      */     throws SQLException
/*      */   {
/*   46 */     super(connToSet, databaseToSet);
/*      */ 
/*   48 */     this.hasReferentialConstraintsView = this.conn.versionMeetsMinimum(5, 1, 10);
/*      */ 
/*   51 */     ResultSet rs = null;
/*      */     try
/*      */     {
/*   54 */       rs = super.getTables("INFORMATION_SCHEMA", null, "PARAMETERS", new String[0]);
/*      */ 
/*   56 */       this.hasParametersView = rs.next();
/*      */     } finally {
/*   58 */       if (rs != null)
/*   59 */         rs.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private ResultSet executeMetadataQuery(PreparedStatement pStmt)
/*      */     throws SQLException
/*      */   {
/*   66 */     ResultSet rs = pStmt.executeQuery();
/*   67 */     ((ResultSetInternalMethods)rs).setOwningStatement(null);
/*      */ 
/*   69 */     return rs;
/*      */   }
/*      */ 
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  110 */     if (columnNamePattern == null) {
/*  111 */       if (this.conn.getNullNamePatternMatchesAll())
/*  112 */         columnNamePattern = "%";
/*      */       else {
/*  114 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  120 */     if ((catalog == null) && 
/*  121 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  122 */       catalog = this.database;
/*      */     }
/*      */ 
/*  126 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME,COLUMN_NAME, NULL AS GRANTOR, GRANTEE, PRIVILEGE_TYPE AS PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME =? AND COLUMN_NAME LIKE ? ORDER BY COLUMN_NAME, PRIVILEGE_TYPE";
/*      */ 
/*  133 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  136 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/*  138 */       if (catalog != null)
/*  139 */         pStmt.setString(1, catalog);
/*      */       else {
/*  141 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  144 */       pStmt.setString(2, table);
/*  145 */       pStmt.setString(3, columnNamePattern);
/*      */ 
/*  147 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  148 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 64), new Field("", "TABLE_SCHEM", 1, 1), new Field("", "TABLE_NAME", 1, 64), new Field("", "COLUMN_NAME", 1, 64), new Field("", "GRANTOR", 1, 77), new Field("", "GRANTEE", 1, 77), new Field("", "PRIVILEGE", 1, 64), new Field("", "IS_GRANTABLE", 1, 3) });
/*      */ 
/*  158 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/*  160 */       if (pStmt != null)
/*  161 */         pStmt.close(); 
/*  161 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableName, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  212 */     if (columnNamePattern == null) {
/*  213 */       if (this.conn.getNullNamePatternMatchesAll())
/*  214 */         columnNamePattern = "%";
/*      */       else {
/*  216 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  222 */     if ((catalog == null) && 
/*  223 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  224 */       catalog = this.database;
/*      */     }
/*      */ 
/*  228 */     StringBuffer sqlBuf = new StringBuffer("SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,TABLE_NAME,COLUMN_NAME,");
/*      */ 
/*  231 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */ 
/*  233 */     sqlBuf.append(" AS DATA_TYPE, ");
/*      */ 
/*  235 */     if (this.conn.getCapitalizeTypeNames())
/*  236 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS TYPE_NAME,");
/*      */     else {
/*  238 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS TYPE_NAME,");
/*      */     }
/*      */ 
/*  241 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, " + MysqlIO.getMaxBuf() + " AS BUFFER_LENGTH," + "NUMERIC_SCALE AS DECIMAL_DIGITS," + "10 AS NUM_PREC_RADIX," + "CASE WHEN IS_NULLABLE='NO' THEN " + 0 + " ELSE CASE WHEN IS_NULLABLE='YES' THEN " + 1 + " ELSE " + 2 + " END END AS NULLABLE," + "COLUMN_COMMENT AS REMARKS," + "COLUMN_DEFAULT AS COLUMN_DEF," + "0 AS SQL_DATA_TYPE," + "0 AS SQL_DATETIME_SUB," + "CASE WHEN CHARACTER_OCTET_LENGTH > " + 2147483647 + " THEN " + 2147483647 + " ELSE CHARACTER_OCTET_LENGTH END AS CHAR_OCTET_LENGTH," + "ORDINAL_POSITION," + "IS_NULLABLE," + "NULL AS SCOPE_CATALOG," + "NULL AS SCOPE_SCHEMA," + "NULL AS SCOPE_TABLE," + "NULL AS SOURCE_DATA_TYPE," + "IF (EXTRA LIKE '%auto_increment%','YES','NO') AS IS_AUTOINCREMENT " + "FROM INFORMATION_SCHEMA.COLUMNS WHERE " + "TABLE_SCHEMA LIKE ? AND " + "TABLE_NAME LIKE ? AND COLUMN_NAME LIKE ? " + "ORDER BY TABLE_SCHEMA, TABLE_NAME, ORDINAL_POSITION");
/*      */ 
/*  265 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  268 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/*  270 */       if (catalog != null)
/*  271 */         pStmt.setString(1, catalog);
/*      */       else {
/*  273 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  276 */       pStmt.setString(2, tableName);
/*  277 */       pStmt.setString(3, columnNamePattern);
/*      */ 
/*  279 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/*  281 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createColumnsFields());
/*  282 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/*  284 */       if (pStmt != null)
/*  285 */         pStmt.close(); 
/*  285 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable)
/*      */     throws SQLException
/*      */   {
/*  360 */     if (primaryTable == null) {
/*  361 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  365 */     if ((primaryCatalog == null) && 
/*  366 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  367 */       primaryCatalog = this.database;
/*      */     }
/*      */ 
/*  371 */     if ((foreignCatalog == null) && 
/*  372 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  373 */       foreignCatalog = this.database;
/*      */     }
/*      */ 
/*  377 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "AND A.TABLE_SCHEMA LIKE ? AND A.TABLE_NAME=? " + "ORDER BY " + "A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
/*      */ 
/*  411 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  414 */       pStmt = prepareMetaDataSafeStatement(sql);
/*  415 */       if (primaryCatalog != null)
/*  416 */         pStmt.setString(1, primaryCatalog);
/*      */       else {
/*  418 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  421 */       pStmt.setString(2, primaryTable);
/*      */ 
/*  423 */       if (foreignCatalog != null)
/*  424 */         pStmt.setString(3, foreignCatalog);
/*      */       else {
/*  426 */         pStmt.setString(3, "%");
/*      */       }
/*      */ 
/*  429 */       pStmt.setString(4, foreignTable);
/*      */ 
/*  431 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  432 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */ 
/*  434 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/*  436 */       if (pStmt != null)
/*  437 */         pStmt.close(); 
/*  437 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  505 */     if (table == null) {
/*  506 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  510 */     if ((catalog == null) && 
/*  511 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  512 */       catalog = this.database;
/*      */     }
/*      */ 
/*  518 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME, A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME,A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "ORDER BY A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
/*      */ 
/*  551 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  554 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/*  556 */       if (catalog != null)
/*  557 */         pStmt.setString(1, catalog);
/*      */       else {
/*  559 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  562 */       pStmt.setString(2, table);
/*      */ 
/*  564 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/*  566 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */ 
/*  568 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/*  570 */       if (pStmt != null)
/*  571 */         pStmt.close(); 
/*  571 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   private String generateOptionalRefContraintsJoin()
/*      */   {
/*  578 */     return this.hasReferentialConstraintsView ? "JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON (R.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND R.TABLE_NAME = B.TABLE_NAME AND R.CONSTRAINT_SCHEMA = B.TABLE_SCHEMA) " : "";
/*      */   }
/*      */ 
/*      */   private String generateDeleteRuleClause()
/*      */   {
/*  586 */     return this.hasReferentialConstraintsView ? "CASE WHEN R.DELETE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.DELETE_RULE='SET NULL' THEN " + String.valueOf(2) + " WHEN R.DELETE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.DELETE_RULE='RESTRICT' THEN " + String.valueOf(1) + " WHEN R.DELETE_RULE='NO ACTION' THEN " + String.valueOf(3) + " ELSE " + String.valueOf(3) + " END " : String.valueOf(1);
/*      */   }
/*      */ 
/*      */   private String generateUpdateRuleClause()
/*      */   {
/*  596 */     return this.hasReferentialConstraintsView ? "CASE WHEN R.UPDATE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.UPDATE_RULE='SET NULL' THEN " + String.valueOf(2) + " WHEN R.UPDATE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.UPDATE_RULE='RESTRICT' THEN " + String.valueOf(1) + " WHEN R.UPDATE_RULE='NO ACTION' THEN " + String.valueOf(3) + " ELSE " + String.valueOf(3) + " END " : String.valueOf(1);
/*      */   }
/*      */ 
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  666 */     if (table == null) {
/*  667 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  671 */     if ((catalog == null) && 
/*  672 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  673 */       catalog = this.database;
/*      */     }
/*      */ 
/*  677 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A " + "JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING " + "(CONSTRAINT_NAME, TABLE_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.TABLE_SCHEMA LIKE ? " + "AND A.TABLE_NAME=? " + "AND A.REFERENCED_TABLE_SCHEMA IS NOT NULL " + "ORDER BY " + "A.REFERENCED_TABLE_SCHEMA, A.REFERENCED_TABLE_NAME, " + "A.ORDINAL_POSITION";
/*      */ 
/*  714 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  717 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/*  719 */       if (catalog != null)
/*  720 */         pStmt.setString(1, catalog);
/*      */       else {
/*  722 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  725 */       pStmt.setString(2, table);
/*      */ 
/*  727 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/*  729 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */ 
/*  731 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/*  733 */       if (pStmt != null)
/*  734 */         pStmt.close(); 
/*  734 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
/*      */     throws SQLException
/*      */   {
/*  799 */     StringBuffer sqlBuf = new StringBuffer("SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,TABLE_NAME,NON_UNIQUE,TABLE_SCHEMA AS INDEX_QUALIFIER,INDEX_NAME,3 AS TYPE,SEQ_IN_INDEX AS ORDINAL_POSITION,COLUMN_NAME,COLLATION AS ASC_OR_DESC,CARDINALITY,NULL AS PAGES,NULL AS FILTER_CONDITION FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ?");
/*      */ 
/*  810 */     if (unique) {
/*  811 */       sqlBuf.append(" AND NON_UNIQUE=0 ");
/*      */     }
/*      */ 
/*  814 */     sqlBuf.append("ORDER BY NON_UNIQUE, INDEX_NAME, SEQ_IN_INDEX");
/*      */ 
/*  816 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  819 */       if ((catalog == null) && 
/*  820 */         (this.conn.getNullCatalogMeansCurrent())) {
/*  821 */         catalog = this.database;
/*      */       }
/*      */ 
/*  825 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/*  827 */       if (catalog != null)
/*  828 */         pStmt.setString(1, catalog);
/*      */       else {
/*  830 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  833 */       pStmt.setString(2, table);
/*      */ 
/*  835 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/*  837 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createIndexInfoFields());
/*      */ 
/*  839 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/*  841 */       if (pStmt != null)
/*  842 */         pStmt.close(); 
/*  842 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  875 */     if ((catalog == null) && 
/*  876 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  877 */       catalog = this.database;
/*      */     }
/*      */ 
/*  881 */     if (table == null) {
/*  882 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  886 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, SEQ_IN_INDEX AS KEY_SEQ, 'PRIMARY' AS PK_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND INDEX_NAME='PRIMARY' ORDER BY TABLE_SCHEMA, TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX";
/*      */ 
/*  891 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  894 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/*  896 */       if (catalog != null)
/*  897 */         pStmt.setString(1, catalog);
/*      */       else {
/*  899 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  902 */       pStmt.setString(2, table);
/*      */ 
/*  904 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  905 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 255), new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_NAME", 1, 255), new Field("", "COLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 5), new Field("", "PK_NAME", 1, 32) });
/*      */ 
/*  913 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/*  915 */       if (pStmt != null)
/*  916 */         pStmt.close(); 
/*  916 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*      */     throws SQLException
/*      */   {
/*  964 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0))
/*      */     {
/*  966 */       if (this.conn.getNullNamePatternMatchesAll())
/*  967 */         procedureNamePattern = "%";
/*      */       else {
/*  969 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  975 */     String db = null;
/*      */ 
/*  977 */     if (catalog == null) {
/*  978 */       if (this.conn.getNullCatalogMeansCurrent())
/*  979 */         db = this.database;
/*      */     }
/*      */     else {
/*  982 */       db = catalog;
/*      */     }
/*      */ 
/*  985 */     String sql = "SELECT ROUTINE_SCHEMA AS PROCEDURE_CAT, NULL AS PROCEDURE_SCHEM, ROUTINE_NAME AS PROCEDURE_NAME, NULL AS RESERVED_1, NULL AS RESERVED_2, NULL AS RESERVED_3, ROUTINE_COMMENT AS REMARKS, CASE WHEN ROUTINE_TYPE = 'PROCEDURE' THEN 1 WHEN ROUTINE_TYPE='FUNCTION' THEN 2 ELSE 0 END AS PROCEDURE_TYPE FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_SCHEMA LIKE ? AND ROUTINE_NAME LIKE ? ORDER BY ROUTINE_SCHEMA, ROUTINE_NAME";
/*      */ 
/*  998 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1001 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/* 1003 */       if (db != null)
/* 1004 */         pStmt.setString(1, db);
/*      */       else {
/* 1006 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1009 */       pStmt.setString(2, procedureNamePattern);
/*      */ 
/* 1011 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1012 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "PROCEDURE_CAT", 1, 0), new Field("", "PROCEDURE_SCHEM", 1, 0), new Field("", "PROCEDURE_NAME", 1, 0), new Field("", "reserved1", 1, 0), new Field("", "reserved2", 1, 0), new Field("", "reserved3", 1, 0), new Field("", "REMARKS", 1, 0), new Field("", "PROCEDURE_TYPE", 5, 0) });
/*      */ 
/* 1022 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/* 1024 */       if (pStmt != null)
/* 1025 */         pStmt.close(); 
/* 1025 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1128 */     if (!this.conn.versionMeetsMinimum(5, 4, 0)) {
/* 1129 */       return super.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
/*      */     }
/*      */ 
/* 1133 */     if (!this.hasParametersView) {
/* 1134 */       return super.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
/*      */     }
/*      */ 
/* 1137 */     if ((functionNamePattern == null) || (functionNamePattern.length() == 0))
/*      */     {
/* 1139 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1140 */         functionNamePattern = "%";
/*      */       else {
/* 1142 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1148 */     String db = null;
/*      */ 
/* 1150 */     if ((catalog == null) && 
/* 1151 */       (this.conn.getNullCatalogMeansCurrent())) {
/* 1152 */       db = this.database;
/*      */     }
/*      */ 
/* 1162 */     StringBuffer sqlBuf = new StringBuffer("SELECT SPECIFIC_SCHEMA AS FUNCTION_CAT, NULL AS `FUNCTION_SCHEM`, SPECIFIC_NAME AS `FUNCTION_NAME`, PARAMETER_NAME AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN 1 WHEN PARAMETER_MODE='OUT' THEN 3 WHEN PARAMETER_MODE='INOUT' THEN 2 WHEN ORDINAL_POSITION=0 THEN 4 ELSE 0 END AS `COLUMN_TYPE`, ");
/*      */ 
/* 1174 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */ 
/* 1176 */     sqlBuf.append(" AS `DATA_TYPE`, ");
/*      */ 
/* 1179 */     if (this.conn.getCapitalizeTypeNames())
/* 1180 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS `TYPE_NAME`,");
/*      */     else {
/* 1182 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS `TYPE_NAME`,");
/*      */     }
/*      */ 
/* 1186 */     sqlBuf.append("NUMERIC_PRECISION AS `PRECISION`, ");
/*      */ 
/* 1188 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
/*      */ 
/* 1193 */     sqlBuf.append("NUMERIC_SCALE AS `SCALE`, ");
/*      */ 
/* 1195 */     sqlBuf.append("10 AS RADIX,");
/*      */ 
/* 1202 */     sqlBuf.append("2 AS `NULLABLE`,  NULL AS `REMARKS`, CHARACTER_OCTET_LENGTH AS `CHAR_OCTET_LENGTH`,  ORDINAL_POSITION, '' AS `IS_NULLABLE`, SPECIFIC_NAME FROM INFORMATION_SCHEMA.PARAMETERS WHERE SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ? AND (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL) AND ROUTINE_TYPE='FUNCTION' ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ORDINAL_POSITION");
/*      */ 
/* 1212 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1215 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/* 1217 */       if (db != null)
/* 1218 */         pStmt.setString(1, db);
/*      */       else {
/* 1220 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1223 */       pStmt.setString(2, functionNamePattern);
/* 1224 */       pStmt.setString(3, columnNamePattern);
/*      */ 
/* 1226 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1227 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFunctionColumnsFields());
/*      */ 
/* 1229 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/* 1231 */       if (pStmt != null)
/* 1232 */         pStmt.close(); 
/* 1232 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1303 */     if (!this.conn.versionMeetsMinimum(5, 4, 0)) {
/* 1304 */       return super.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
/*      */     }
/*      */ 
/* 1308 */     if (!this.hasParametersView) {
/* 1309 */       return super.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
/*      */     }
/*      */ 
/* 1312 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0))
/*      */     {
/* 1314 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1315 */         procedureNamePattern = "%";
/*      */       else {
/* 1317 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1323 */     String db = null;
/*      */ 
/* 1325 */     if ((catalog == null) && 
/* 1326 */       (this.conn.getNullCatalogMeansCurrent())) {
/* 1327 */       db = this.database;
/*      */     }
/*      */ 
/* 1347 */     StringBuffer sqlBuf = new StringBuffer("SELECT SPECIFIC_SCHEMA AS PROCEDURE_CAT, NULL AS `PROCEDURE_SCHEM`, SPECIFIC_NAME AS `PROCEDURE_NAME`, PARAMETER_NAME AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN 1 WHEN PARAMETER_MODE='OUT' THEN 4 WHEN PARAMETER_MODE='INOUT' THEN 2 WHEN ORDINAL_POSITION=0 THEN 5 ELSE 0 END AS `COLUMN_TYPE`, ");
/*      */ 
/* 1359 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */ 
/* 1361 */     sqlBuf.append(" AS `DATA_TYPE`, ");
/*      */ 
/* 1364 */     if (this.conn.getCapitalizeTypeNames())
/* 1365 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS `TYPE_NAME`,");
/*      */     else {
/* 1367 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS `TYPE_NAME`,");
/*      */     }
/*      */ 
/* 1371 */     sqlBuf.append("NUMERIC_PRECISION AS `PRECISION`, ");
/*      */ 
/* 1373 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
/*      */ 
/* 1378 */     sqlBuf.append("NUMERIC_SCALE AS `SCALE`, ");
/*      */ 
/* 1380 */     sqlBuf.append("10 AS RADIX,");
/* 1381 */     sqlBuf.append("2 AS `NULLABLE`,  NULL AS `REMARKS` FROM INFORMATION_SCHEMA.PARAMETERS WHERE SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ? AND (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL) ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ORDINAL_POSITION");
/*      */ 
/* 1387 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1390 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/* 1392 */       if (db != null)
/* 1393 */         pStmt.setString(1, db);
/*      */       else {
/* 1395 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1398 */       pStmt.setString(2, procedureNamePattern);
/* 1399 */       pStmt.setString(3, columnNamePattern);
/*      */ 
/* 1401 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1402 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createProcedureColumnsFields());
/*      */ 
/* 1404 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/* 1406 */       if (pStmt != null)
/* 1407 */         pStmt.close(); 
/* 1407 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
/*      */     throws SQLException
/*      */   {
/* 1450 */     if ((catalog == null) && 
/* 1451 */       (this.conn.getNullCatalogMeansCurrent())) {
/* 1452 */       catalog = this.database;
/*      */     }
/*      */ 
/* 1456 */     if (tableNamePattern == null) {
/* 1457 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1458 */         tableNamePattern = "%";
/*      */       else {
/* 1460 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1466 */     PreparedStatement pStmt = null;
/*      */ 
/* 1468 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, CASE WHEN TABLE_TYPE='BASE TABLE' THEN 'TABLE' WHEN TABLE_TYPE='TEMPORARY' THEN 'LOCAL_TEMPORARY' ELSE TABLE_TYPE END AS TABLE_TYPE, TABLE_COMMENT AS REMARKS FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND TABLE_TYPE IN (?,?,?) ORDER BY TABLE_TYPE, TABLE_SCHEMA, TABLE_NAME";
/*      */     try
/*      */     {
/* 1476 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/* 1478 */       if (catalog != null)
/* 1479 */         pStmt.setString(1, catalog);
/*      */       else {
/* 1481 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1484 */       pStmt.setString(2, tableNamePattern);
/*      */ 
/* 1488 */       if ((types == null) || (types.length == 0)) {
/* 1489 */         pStmt.setString(3, "BASE TABLE");
/* 1490 */         pStmt.setString(4, "VIEW");
/* 1491 */         pStmt.setString(5, "TEMPORARY");
/*      */       } else {
/* 1493 */         pStmt.setNull(3, 12);
/* 1494 */         pStmt.setNull(4, 12);
/* 1495 */         pStmt.setNull(5, 12);
/*      */ 
/* 1497 */         for (int i = 0; i < types.length; i++) {
/* 1498 */           if ("TABLE".equalsIgnoreCase(types[i])) {
/* 1499 */             pStmt.setString(3, "BASE TABLE");
/*      */           }
/*      */ 
/* 1502 */           if ("VIEW".equalsIgnoreCase(types[i])) {
/* 1503 */             pStmt.setString(4, "VIEW");
/*      */           }
/*      */ 
/* 1506 */           if ("LOCAL TEMPORARY".equalsIgnoreCase(types[i])) {
/* 1507 */             pStmt.setString(5, "TEMPORARY");
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1512 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/* 1514 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 12, catalog == null ? 0 : catalog.length()), new Field("", "TABLE_SCHEM", 12, 0), new Field("", "TABLE_NAME", 12, 255), new Field("", "TABLE_TYPE", 12, 5), new Field("", "REMARKS", 12, 0) });
/*      */ 
/* 1522 */       ResultSet localResultSet1 = rs;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/* 1524 */       if (pStmt != null)
/* 1525 */         pStmt.close(); 
/* 1525 */     }throw localObject;
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.DatabaseMetaDataUsingInfoSchema
 * JD-Core Version:    0.6.0
 */