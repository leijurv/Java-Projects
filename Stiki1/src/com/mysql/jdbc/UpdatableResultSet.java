/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandlerFactory;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class UpdatableResultSet extends ResultSetImpl
/*      */ {
/*   48 */   protected static final byte[] STREAM_DATA_MARKER = "** STREAM DATA **".getBytes();
/*      */   protected SingleByteCharsetConverter charConverter;
/*      */   private String charEncoding;
/*      */   private byte[][] defaultColumnValue;
/*   59 */   private PreparedStatement deleter = null;
/*      */ 
/*   61 */   private String deleteSQL = null;
/*      */ 
/*   63 */   private boolean initializedCharConverter = false;
/*      */ 
/*   66 */   protected PreparedStatement inserter = null;
/*      */ 
/*   68 */   private String insertSQL = null;
/*      */ 
/*   71 */   private boolean isUpdatable = false;
/*      */ 
/*   74 */   private String notUpdatableReason = null;
/*      */ 
/*   77 */   private List primaryKeyIndicies = null;
/*      */   private String qualifiedAndQuotedTableName;
/*   81 */   private String quotedIdChar = null;
/*      */   private PreparedStatement refresher;
/*   86 */   private String refreshSQL = null;
/*      */   private ResultSetRow savedCurrentRow;
/*   92 */   protected PreparedStatement updater = null;
/*      */ 
/*   95 */   private String updateSQL = null;
/*      */ 
/*   97 */   private boolean populateInserterWithDefaultValues = false;
/*      */ 
/*   99 */   private Map databasesUsedToTablesUsed = null;
/*      */ 
/*      */   protected UpdatableResultSet(String catalog, Field[] fields, RowData tuples, ConnectionImpl conn, StatementImpl creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  121 */     super(catalog, fields, tuples, conn, creatorStmt);
/*  122 */     checkUpdatability();
/*  123 */     this.populateInserterWithDefaultValues = this.connection.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */ 
/*      */   public synchronized boolean absolute(int row)
/*      */     throws SQLException
/*      */   {
/*  166 */     return super.absolute(row);
/*      */   }
/*      */ 
/*      */   public synchronized void afterLast()
/*      */     throws SQLException
/*      */   {
/*  182 */     super.afterLast();
/*      */   }
/*      */ 
/*      */   public synchronized void beforeFirst()
/*      */     throws SQLException
/*      */   {
/*  198 */     super.beforeFirst();
/*      */   }
/*      */ 
/*      */   public synchronized void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/*  212 */     checkClosed();
/*      */ 
/*  214 */     if (this.doingUpdates) {
/*  215 */       this.doingUpdates = false;
/*  216 */       this.updater.clearParameters();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkRowPos()
/*      */     throws SQLException
/*      */   {
/*  226 */     checkClosed();
/*      */ 
/*  228 */     if (!this.onInsertRow)
/*  229 */       super.checkRowPos();
/*      */   }
/*      */ 
/*      */   protected void checkUpdatability()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  241 */       if (this.fields == null)
/*      */       {
/*  248 */         return;
/*      */       }
/*      */ 
/*  251 */       String singleTableName = null;
/*  252 */       String catalogName = null;
/*      */ 
/*  254 */       int primaryKeyCount = 0;
/*      */ 
/*  261 */       if ((this.catalog == null) || (this.catalog.length() == 0)) {
/*  262 */         this.catalog = this.fields[0].getDatabaseName();
/*      */ 
/*  264 */         if ((this.catalog == null) || (this.catalog.length() == 0)) {
/*  265 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.43"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  271 */       if (this.fields.length > 0) {
/*  272 */         singleTableName = this.fields[0].getOriginalTableName();
/*  273 */         catalogName = this.fields[0].getDatabaseName();
/*      */ 
/*  275 */         if (singleTableName == null) {
/*  276 */           singleTableName = this.fields[0].getTableName();
/*  277 */           catalogName = this.catalog;
/*      */         }
/*      */ 
/*  280 */         if ((singleTableName != null) && (singleTableName.length() == 0)) {
/*  281 */           this.isUpdatable = false;
/*  282 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */ 
/*  284 */           return;
/*      */         }
/*      */ 
/*  287 */         if (this.fields[0].isPrimaryKey()) {
/*  288 */           primaryKeyCount++;
/*      */         }
/*      */ 
/*  294 */         for (int i = 1; i < this.fields.length; i++) {
/*  295 */           String otherTableName = this.fields[i].getOriginalTableName();
/*  296 */           String otherCatalogName = this.fields[i].getDatabaseName();
/*      */ 
/*  298 */           if (otherTableName == null) {
/*  299 */             otherTableName = this.fields[i].getTableName();
/*  300 */             otherCatalogName = this.catalog;
/*      */           }
/*      */ 
/*  303 */           if ((otherTableName != null) && (otherTableName.length() == 0)) {
/*  304 */             this.isUpdatable = false;
/*  305 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */ 
/*  307 */             return;
/*      */           }
/*      */ 
/*  310 */           if ((singleTableName == null) || (!otherTableName.equals(singleTableName)))
/*      */           {
/*  312 */             this.isUpdatable = false;
/*  313 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.0");
/*      */ 
/*  315 */             return;
/*      */           }
/*      */ 
/*  319 */           if ((catalogName == null) || (!otherCatalogName.equals(catalogName)))
/*      */           {
/*  321 */             this.isUpdatable = false;
/*  322 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.1");
/*      */ 
/*  324 */             return;
/*      */           }
/*      */ 
/*  327 */           if (this.fields[i].isPrimaryKey()) {
/*  328 */             primaryKeyCount++;
/*      */           }
/*      */         }
/*      */ 
/*  332 */         if ((singleTableName == null) || (singleTableName.length() == 0)) {
/*  333 */           this.isUpdatable = false;
/*  334 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.2");
/*      */ 
/*  336 */           return;
/*      */         }
/*      */       } else {
/*  339 */         this.isUpdatable = false;
/*  340 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */ 
/*  342 */         return;
/*      */       }
/*      */ 
/*  345 */       if (this.connection.getStrictUpdates()) {
/*  346 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  348 */         ResultSet rs = null;
/*  349 */         HashMap primaryKeyNames = new HashMap();
/*      */         try
/*      */         {
/*  352 */           rs = dbmd.getPrimaryKeys(catalogName, null, singleTableName);
/*      */ 
/*  354 */           while (rs.next()) {
/*  355 */             String keyName = rs.getString(4);
/*  356 */             keyName = keyName.toUpperCase();
/*  357 */             primaryKeyNames.put(keyName, keyName);
/*      */           }
/*      */         } finally {
/*  360 */           if (rs != null) {
/*      */             try {
/*  362 */               rs.close();
/*      */             } catch (Exception ex) {
/*  364 */               AssertionFailedException.shouldNotHappen(ex);
/*      */             }
/*      */ 
/*  367 */             rs = null;
/*      */           }
/*      */         }
/*      */ 
/*  371 */         int existingPrimaryKeysCount = primaryKeyNames.size();
/*      */ 
/*  373 */         if (existingPrimaryKeysCount == 0) {
/*  374 */           this.isUpdatable = false;
/*  375 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.5");
/*      */ 
/*  377 */           return;
/*      */         }
/*      */ 
/*  383 */         for (int i = 0; i < this.fields.length; i++) {
/*  384 */           if (this.fields[i].isPrimaryKey()) {
/*  385 */             String columnNameUC = this.fields[i].getName().toUpperCase();
/*      */ 
/*  388 */             if (primaryKeyNames.remove(columnNameUC) != null)
/*      */               continue;
/*  390 */             String originalName = this.fields[i].getOriginalName();
/*      */ 
/*  392 */             if ((originalName == null) || 
/*  393 */               (primaryKeyNames.remove(originalName.toUpperCase()) != null)) {
/*      */               continue;
/*      */             }
/*  396 */             this.isUpdatable = false;
/*  397 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.6", new Object[] { originalName });
/*      */ 
/*  400 */             return;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  407 */         this.isUpdatable = primaryKeyNames.isEmpty();
/*      */ 
/*  409 */         if (!this.isUpdatable) {
/*  410 */           if (existingPrimaryKeysCount > 1)
/*  411 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.7");
/*      */           else {
/*  413 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */           }
/*      */ 
/*  416 */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  423 */       if (primaryKeyCount == 0) {
/*  424 */         this.isUpdatable = false;
/*  425 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */ 
/*  427 */         return;
/*      */       }
/*      */ 
/*  430 */       this.isUpdatable = true;
/*  431 */       this.notUpdatableReason = null;
/*      */ 
/*  433 */       return;
/*      */     } catch (SQLException sqlEx) {
/*  435 */       this.isUpdatable = false;
/*  436 */       this.notUpdatableReason = sqlEx.getMessage();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void deleteRow()
/*      */     throws SQLException
/*      */   {
/*  451 */     checkClosed();
/*      */ 
/*  453 */     if (!this.isUpdatable) {
/*  454 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/*  457 */     if (this.onInsertRow)
/*  458 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.1"), getExceptionInterceptor());
/*  459 */     if (this.rowData.size() == 0)
/*  460 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.2"), getExceptionInterceptor());
/*  461 */     if (isBeforeFirst())
/*  462 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.3"), getExceptionInterceptor());
/*  463 */     if (isAfterLast()) {
/*  464 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.4"), getExceptionInterceptor());
/*      */     }
/*      */ 
/*  467 */     if (this.deleter == null) {
/*  468 */       if (this.deleteSQL == null) {
/*  469 */         generateStatements();
/*      */       }
/*      */ 
/*  472 */       this.deleter = ((PreparedStatement)this.connection.clientPrepareStatement(this.deleteSQL));
/*      */     }
/*      */ 
/*  476 */     this.deleter.clearParameters();
/*      */ 
/*  478 */     String characterEncoding = null;
/*      */ 
/*  480 */     if (this.connection.getUseUnicode()) {
/*  481 */       characterEncoding = this.connection.getEncoding();
/*      */     }
/*      */ 
/*  484 */     int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/*  486 */     if (numKeys == 1) {
/*  487 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */ 
/*  489 */       setParamValue(this.deleter, 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */     }
/*      */     else {
/*  492 */       for (int i = 0; i < numKeys; i++) {
/*  493 */         int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */ 
/*  495 */         setParamValue(this.deleter, i + 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  501 */     this.deleter.executeUpdate();
/*  502 */     this.rowData.removeRow(this.rowData.getCurrentRowNumber());
/*      */ 
/*  505 */     previous();
/*      */   }
/*      */ 
/*      */   private synchronized void setParamValue(PreparedStatement ps, int psIdx, ResultSetRow row, int rsIdx, int sqlType)
/*      */     throws SQLException
/*      */   {
/*  512 */     byte[] val = row.getColumnValue(rsIdx);
/*  513 */     if (val == null) {
/*  514 */       ps.setNull(psIdx, 0);
/*  515 */       return;
/*      */     }
/*  517 */     switch (sqlType) {
/*      */     case 0:
/*  519 */       ps.setNull(psIdx, 0);
/*  520 */       break;
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/*  524 */       ps.setInt(psIdx, row.getInt(rsIdx));
/*  525 */       break;
/*      */     case -5:
/*  527 */       ps.setLong(psIdx, row.getLong(rsIdx));
/*  528 */       break;
/*      */     case -1:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 12:
/*  534 */       ps.setString(psIdx, row.getString(rsIdx, this.charEncoding, this.connection));
/*  535 */       break;
/*      */     case 91:
/*  537 */       ps.setDate(psIdx, row.getDateFast(rsIdx, this.connection, this, this.fastDateCal), this.fastDateCal);
/*  538 */       break;
/*      */     case 93:
/*  540 */       ps.setTimestamp(psIdx, row.getTimestampFast(rsIdx, this.fastDateCal, this.defaultTimeZone, false, this.connection, this));
/*  541 */       break;
/*      */     case 92:
/*  543 */       ps.setTime(psIdx, row.getTimeFast(rsIdx, this.fastDateCal, this.defaultTimeZone, false, this.connection, this));
/*  544 */       break;
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 16:
/*  549 */       ps.setBytesNoEscapeNoQuotes(psIdx, val);
/*  550 */       break;
/*      */     default:
/*  556 */       ps.setBytes(psIdx, val);
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void extractDefaultValues()
/*      */     throws SQLException
/*      */   {
/*  563 */     DatabaseMetaData dbmd = this.connection.getMetaData();
/*  564 */     this.defaultColumnValue = new byte[this.fields.length][];
/*      */ 
/*  566 */     ResultSet columnsResultSet = null;
/*  567 */     Iterator referencedDbs = this.databasesUsedToTablesUsed.entrySet().iterator();
/*      */ 
/*  569 */     while (referencedDbs.hasNext()) {
/*  570 */       Map.Entry dbEntry = (Map.Entry)referencedDbs.next();
/*  571 */       String databaseName = dbEntry.getKey().toString();
/*      */ 
/*  573 */       Iterator referencedTables = ((Map)dbEntry.getValue()).entrySet().iterator();
/*      */ 
/*  575 */       while (referencedTables.hasNext()) {
/*  576 */         Map.Entry tableEntry = (Map.Entry)referencedTables.next();
/*  577 */         String tableName = tableEntry.getKey().toString();
/*  578 */         Map columnNamesToIndices = (Map)tableEntry.getValue();
/*      */         try
/*      */         {
/*  581 */           columnsResultSet = dbmd.getColumns(this.catalog, null, tableName, "%");
/*      */ 
/*  584 */           while (columnsResultSet.next()) {
/*  585 */             String columnName = columnsResultSet.getString("COLUMN_NAME");
/*  586 */             byte[] defaultValue = columnsResultSet.getBytes("COLUMN_DEF");
/*      */ 
/*  588 */             if (columnNamesToIndices.containsKey(columnName)) {
/*  589 */               int localColumnIndex = ((Integer)columnNamesToIndices.get(columnName)).intValue();
/*      */ 
/*  591 */               this.defaultColumnValue[localColumnIndex] = defaultValue;
/*      */             }
/*      */           }
/*      */         } finally {
/*  595 */           if (columnsResultSet != null) {
/*  596 */             columnsResultSet.close();
/*      */ 
/*  598 */             columnsResultSet = null;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean first()
/*      */     throws SQLException
/*      */   {
/*  619 */     return super.first();
/*      */   }
/*      */ 
/*      */   protected synchronized void generateStatements()
/*      */     throws SQLException
/*      */   {
/*  632 */     if (!this.isUpdatable) {
/*  633 */       this.doingUpdates = false;
/*  634 */       this.onInsertRow = false;
/*      */ 
/*  636 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/*  639 */     String quotedId = getQuotedIdChar();
/*      */ 
/*  641 */     Map tableNamesSoFar = null;
/*      */ 
/*  643 */     if (this.connection.lowerCaseTableNames()) {
/*  644 */       tableNamesSoFar = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  645 */       this.databasesUsedToTablesUsed = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */     } else {
/*  647 */       tableNamesSoFar = new TreeMap();
/*  648 */       this.databasesUsedToTablesUsed = new TreeMap();
/*      */     }
/*      */ 
/*  651 */     this.primaryKeyIndicies = new ArrayList();
/*      */ 
/*  653 */     StringBuffer fieldValues = new StringBuffer();
/*  654 */     StringBuffer keyValues = new StringBuffer();
/*  655 */     StringBuffer columnNames = new StringBuffer();
/*  656 */     StringBuffer insertPlaceHolders = new StringBuffer();
/*  657 */     StringBuffer allTablesBuf = new StringBuffer();
/*  658 */     Map columnIndicesToTable = new HashMap();
/*      */ 
/*  660 */     boolean firstTime = true;
/*  661 */     boolean keysFirstTime = true;
/*      */ 
/*  663 */     String equalsStr = this.connection.versionMeetsMinimum(3, 23, 0) ? "<=>" : "=";
/*      */ 
/*  666 */     for (int i = 0; i < this.fields.length; i++) {
/*  667 */       StringBuffer tableNameBuffer = new StringBuffer();
/*  668 */       Map updColumnNameToIndex = null;
/*      */ 
/*  671 */       if (this.fields[i].getOriginalTableName() != null)
/*      */       {
/*  673 */         String databaseName = this.fields[i].getDatabaseName();
/*      */ 
/*  675 */         if ((databaseName != null) && (databaseName.length() > 0)) {
/*  676 */           tableNameBuffer.append(quotedId);
/*  677 */           tableNameBuffer.append(databaseName);
/*  678 */           tableNameBuffer.append(quotedId);
/*  679 */           tableNameBuffer.append('.');
/*      */         }
/*      */ 
/*  682 */         String tableOnlyName = this.fields[i].getOriginalTableName();
/*      */ 
/*  684 */         tableNameBuffer.append(quotedId);
/*  685 */         tableNameBuffer.append(tableOnlyName);
/*  686 */         tableNameBuffer.append(quotedId);
/*      */ 
/*  688 */         String fqTableName = tableNameBuffer.toString();
/*      */ 
/*  690 */         if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  691 */           if (!tableNamesSoFar.isEmpty()) {
/*  692 */             allTablesBuf.append(',');
/*      */           }
/*      */ 
/*  695 */           allTablesBuf.append(fqTableName);
/*  696 */           tableNamesSoFar.put(fqTableName, fqTableName);
/*      */         }
/*      */ 
/*  699 */         columnIndicesToTable.put(new Integer(i), fqTableName);
/*      */ 
/*  701 */         updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(databaseName, tableOnlyName);
/*      */       } else {
/*  703 */         String tableOnlyName = this.fields[i].getTableName();
/*      */ 
/*  705 */         if (tableOnlyName != null) {
/*  706 */           tableNameBuffer.append(quotedId);
/*  707 */           tableNameBuffer.append(tableOnlyName);
/*  708 */           tableNameBuffer.append(quotedId);
/*      */ 
/*  710 */           String fqTableName = tableNameBuffer.toString();
/*      */ 
/*  712 */           if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  713 */             if (!tableNamesSoFar.isEmpty()) {
/*  714 */               allTablesBuf.append(',');
/*      */             }
/*      */ 
/*  717 */             allTablesBuf.append(fqTableName);
/*  718 */             tableNamesSoFar.put(fqTableName, fqTableName);
/*      */           }
/*      */ 
/*  721 */           columnIndicesToTable.put(new Integer(i), fqTableName);
/*      */ 
/*  723 */           updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(this.catalog, tableOnlyName);
/*      */         }
/*      */       }
/*      */ 
/*  727 */       String originalColumnName = this.fields[i].getOriginalName();
/*  728 */       String columnName = null;
/*      */ 
/*  730 */       if ((this.connection.getIO().hasLongColumnInfo()) && (originalColumnName != null) && (originalColumnName.length() > 0))
/*      */       {
/*  733 */         columnName = originalColumnName;
/*      */       }
/*  735 */       else columnName = this.fields[i].getName();
/*      */ 
/*  738 */       if ((updColumnNameToIndex != null) && (columnName != null)) {
/*  739 */         updColumnNameToIndex.put(columnName, new Integer(i));
/*      */       }
/*      */ 
/*  742 */       String originalTableName = this.fields[i].getOriginalTableName();
/*  743 */       String tableName = null;
/*      */ 
/*  745 */       if ((this.connection.getIO().hasLongColumnInfo()) && (originalTableName != null) && (originalTableName.length() > 0))
/*      */       {
/*  748 */         tableName = originalTableName;
/*      */       }
/*  750 */       else tableName = this.fields[i].getTableName();
/*      */ 
/*  753 */       StringBuffer fqcnBuf = new StringBuffer();
/*  754 */       String databaseName = this.fields[i].getDatabaseName();
/*      */ 
/*  756 */       if ((databaseName != null) && (databaseName.length() > 0)) {
/*  757 */         fqcnBuf.append(quotedId);
/*  758 */         fqcnBuf.append(databaseName);
/*  759 */         fqcnBuf.append(quotedId);
/*  760 */         fqcnBuf.append('.');
/*      */       }
/*      */ 
/*  763 */       fqcnBuf.append(quotedId);
/*  764 */       fqcnBuf.append(tableName);
/*  765 */       fqcnBuf.append(quotedId);
/*  766 */       fqcnBuf.append('.');
/*  767 */       fqcnBuf.append(quotedId);
/*  768 */       fqcnBuf.append(columnName);
/*  769 */       fqcnBuf.append(quotedId);
/*      */ 
/*  771 */       String qualifiedColumnName = fqcnBuf.toString();
/*      */ 
/*  773 */       if (this.fields[i].isPrimaryKey()) {
/*  774 */         this.primaryKeyIndicies.add(Constants.integerValueOf(i));
/*      */ 
/*  776 */         if (!keysFirstTime)
/*  777 */           keyValues.append(" AND ");
/*      */         else {
/*  779 */           keysFirstTime = false;
/*      */         }
/*      */ 
/*  782 */         keyValues.append(qualifiedColumnName);
/*  783 */         keyValues.append(equalsStr);
/*  784 */         keyValues.append("?");
/*      */       }
/*      */ 
/*  787 */       if (firstTime) {
/*  788 */         firstTime = false;
/*  789 */         fieldValues.append("SET ");
/*      */       } else {
/*  791 */         fieldValues.append(",");
/*  792 */         columnNames.append(",");
/*  793 */         insertPlaceHolders.append(",");
/*      */       }
/*      */ 
/*  796 */       insertPlaceHolders.append("?");
/*      */ 
/*  798 */       columnNames.append(qualifiedColumnName);
/*      */ 
/*  800 */       fieldValues.append(qualifiedColumnName);
/*  801 */       fieldValues.append("=?");
/*      */     }
/*      */ 
/*  804 */     this.qualifiedAndQuotedTableName = allTablesBuf.toString();
/*      */ 
/*  806 */     this.updateSQL = ("UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString());
/*      */ 
/*  809 */     this.insertSQL = ("INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames.toString() + ") VALUES (" + insertPlaceHolders.toString() + ")");
/*      */ 
/*  812 */     this.refreshSQL = ("SELECT " + columnNames.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString());
/*      */ 
/*  815 */     this.deleteSQL = ("DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString());
/*      */   }
/*      */ 
/*      */   private Map getColumnsToIndexMapForTableAndDB(String databaseName, String tableName)
/*      */   {
/*  822 */     Map tablesUsedToColumnsMap = (Map)this.databasesUsedToTablesUsed.get(databaseName);
/*      */ 
/*  824 */     if (tablesUsedToColumnsMap == null) {
/*  825 */       if (this.connection.lowerCaseTableNames())
/*  826 */         tablesUsedToColumnsMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */       else {
/*  828 */         tablesUsedToColumnsMap = new TreeMap();
/*      */       }
/*      */ 
/*  831 */       this.databasesUsedToTablesUsed.put(databaseName, tablesUsedToColumnsMap);
/*      */     }
/*      */ 
/*  834 */     Map nameToIndex = (Map)tablesUsedToColumnsMap.get(tableName);
/*      */ 
/*  836 */     if (nameToIndex == null) {
/*  837 */       nameToIndex = new HashMap();
/*  838 */       tablesUsedToColumnsMap.put(tableName, nameToIndex);
/*      */     }
/*      */ 
/*  841 */     return nameToIndex;
/*      */   }
/*      */ 
/*      */   private synchronized SingleByteCharsetConverter getCharConverter() throws SQLException
/*      */   {
/*  846 */     if (!this.initializedCharConverter) {
/*  847 */       this.initializedCharConverter = true;
/*      */ 
/*  849 */       if (this.connection.getUseUnicode()) {
/*  850 */         this.charEncoding = this.connection.getEncoding();
/*  851 */         this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  856 */     return this.charConverter;
/*      */   }
/*      */ 
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/*  869 */     return this.isUpdatable ? 1008 : 1007;
/*      */   }
/*      */ 
/*      */   private synchronized String getQuotedIdChar() throws SQLException {
/*  873 */     if (this.quotedIdChar == null) {
/*  874 */       boolean useQuotedIdentifiers = this.connection.supportsQuotedIdentifiers();
/*      */ 
/*  877 */       if (useQuotedIdentifiers) {
/*  878 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*  879 */         this.quotedIdChar = dbmd.getIdentifierQuoteString();
/*      */       } else {
/*  881 */         this.quotedIdChar = "";
/*      */       }
/*      */     }
/*      */ 
/*  885 */     return this.quotedIdChar;
/*      */   }
/*      */ 
/*      */   public synchronized void insertRow()
/*      */     throws SQLException
/*      */   {
/*  898 */     checkClosed();
/*      */ 
/*  900 */     if (!this.onInsertRow) {
/*  901 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.7"), getExceptionInterceptor());
/*      */     }
/*      */ 
/*  904 */     this.inserter.executeUpdate();
/*      */ 
/*  906 */     long autoIncrementId = this.inserter.getLastInsertID();
/*  907 */     int numFields = this.fields.length;
/*  908 */     byte[][] newRow = new byte[numFields][];
/*      */ 
/*  910 */     for (int i = 0; i < numFields; i++) {
/*  911 */       if (this.inserter.isNull(i))
/*  912 */         newRow[i] = null;
/*      */       else {
/*  914 */         newRow[i] = this.inserter.getBytesRepresentation(i);
/*      */       }
/*      */ 
/*  921 */       if ((this.fields[i].isAutoIncrement()) && (autoIncrementId > 0L)) {
/*  922 */         newRow[i] = String.valueOf(autoIncrementId).getBytes();
/*  923 */         this.inserter.setBytesNoEscapeNoQuotes(i + 1, newRow[i]);
/*      */       }
/*      */     }
/*      */ 
/*  927 */     ResultSetRow resultSetRow = new ByteArrayRow(newRow, getExceptionInterceptor());
/*      */ 
/*  929 */     refreshRow(this.inserter, resultSetRow);
/*      */ 
/*  931 */     this.rowData.addRow(resultSetRow);
/*  932 */     resetInserter();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/*  949 */     return super.isAfterLast();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/*  966 */     return super.isBeforeFirst();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isFirst()
/*      */     throws SQLException
/*      */   {
/*  982 */     return super.isFirst();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isLast()
/*      */     throws SQLException
/*      */   {
/* 1001 */     return super.isLast();
/*      */   }
/*      */ 
/*      */   boolean isUpdatable() {
/* 1005 */     return this.isUpdatable;
/*      */   }
/*      */ 
/*      */   public synchronized boolean last()
/*      */     throws SQLException
/*      */   {
/* 1022 */     return super.last();
/*      */   }
/*      */ 
/*      */   public synchronized void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/* 1036 */     checkClosed();
/*      */ 
/* 1038 */     if (!this.isUpdatable) {
/* 1039 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/* 1042 */     if (this.onInsertRow) {
/* 1043 */       this.onInsertRow = false;
/* 1044 */       this.thisRow = this.savedCurrentRow;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/* 1066 */     checkClosed();
/*      */ 
/* 1068 */     if (!this.isUpdatable) {
/* 1069 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/* 1072 */     if (this.inserter == null) {
/* 1073 */       if (this.insertSQL == null) {
/* 1074 */         generateStatements();
/*      */       }
/*      */ 
/* 1077 */       this.inserter = ((PreparedStatement)this.connection.clientPrepareStatement(this.insertSQL));
/*      */ 
/* 1079 */       if (this.populateInserterWithDefaultValues) {
/* 1080 */         extractDefaultValues();
/*      */       }
/*      */ 
/* 1083 */       resetInserter();
/*      */     } else {
/* 1085 */       resetInserter();
/*      */     }
/*      */ 
/* 1088 */     int numFields = this.fields.length;
/*      */ 
/* 1090 */     this.onInsertRow = true;
/* 1091 */     this.doingUpdates = false;
/* 1092 */     this.savedCurrentRow = this.thisRow;
/* 1093 */     byte[][] newRowData = new byte[numFields][];
/* 1094 */     this.thisRow = new ByteArrayRow(newRowData, getExceptionInterceptor());
/*      */ 
/* 1096 */     for (int i = 0; i < numFields; i++)
/* 1097 */       if (!this.populateInserterWithDefaultValues) {
/* 1098 */         this.inserter.setBytesNoEscapeNoQuotes(i + 1, "DEFAULT".getBytes());
/*      */ 
/* 1100 */         newRowData = (byte[][])null;
/*      */       }
/* 1102 */       else if (this.defaultColumnValue[i] != null) {
/* 1103 */         Field f = this.fields[i];
/*      */ 
/* 1105 */         switch (f.getMysqlType())
/*      */         {
/*      */         case 7:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 14:
/* 1112 */           if ((this.defaultColumnValue[i].length <= 7) || (this.defaultColumnValue[i][0] != 67) || (this.defaultColumnValue[i][1] != 85) || (this.defaultColumnValue[i][2] != 82) || (this.defaultColumnValue[i][3] != 82) || (this.defaultColumnValue[i][4] != 69) || (this.defaultColumnValue[i][5] != 78) || (this.defaultColumnValue[i][6] != 84) || (this.defaultColumnValue[i][7] != 95))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/* 1121 */           this.inserter.setBytesNoEscapeNoQuotes(i + 1, this.defaultColumnValue[i]);
/*      */ 
/* 1124 */           break;
/*      */         case 8:
/*      */         case 9:
/* 1127 */         case 13: } this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
/*      */ 
/* 1133 */         byte[] defaultValueCopy = new byte[this.defaultColumnValue[i].length];
/* 1134 */         System.arraycopy(this.defaultColumnValue[i], 0, defaultValueCopy, 0, defaultValueCopy.length);
/*      */ 
/* 1136 */         newRowData[i] = defaultValueCopy;
/*      */       } else {
/* 1138 */         this.inserter.setNull(i + 1, 0);
/* 1139 */         newRowData[i] = null;
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized boolean next()
/*      */     throws SQLException
/*      */   {
/* 1165 */     return super.next();
/*      */   }
/*      */ 
/*      */   public synchronized boolean prev()
/*      */     throws SQLException
/*      */   {
/* 1184 */     return super.prev();
/*      */   }
/*      */ 
/*      */   public synchronized boolean previous()
/*      */     throws SQLException
/*      */   {
/* 1206 */     return super.previous();
/*      */   }
/*      */ 
/*      */   public void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 1219 */     if (this.isClosed) {
/* 1220 */       return;
/*      */     }
/*      */ 
/* 1223 */     SQLException sqlEx = null;
/*      */ 
/* 1225 */     if ((this.useUsageAdvisor) && 
/* 1226 */       (this.deleter == null) && (this.inserter == null) && (this.refresher == null) && (this.updater == null))
/*      */     {
/* 1228 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 1230 */       String message = Messages.getString("UpdatableResultSet.34");
/*      */ 
/* 1232 */       this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1246 */       if (this.deleter != null)
/* 1247 */         this.deleter.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1250 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1254 */       if (this.inserter != null)
/* 1255 */         this.inserter.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1258 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1262 */       if (this.refresher != null)
/* 1263 */         this.refresher.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1266 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1270 */       if (this.updater != null)
/* 1271 */         this.updater.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1274 */       sqlEx = ex;
/*      */     }
/*      */ 
/* 1277 */     super.realClose(calledExplicitly);
/*      */ 
/* 1279 */     if (sqlEx != null)
/* 1280 */       throw sqlEx;
/*      */   }
/*      */ 
/*      */   public synchronized void refreshRow()
/*      */     throws SQLException
/*      */   {
/* 1305 */     checkClosed();
/*      */ 
/* 1307 */     if (!this.isUpdatable) {
/* 1308 */       throw new NotUpdatable();
/*      */     }
/*      */ 
/* 1311 */     if (this.onInsertRow)
/* 1312 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.8"), getExceptionInterceptor());
/* 1313 */     if (this.rowData.size() == 0)
/* 1314 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.9"), getExceptionInterceptor());
/* 1315 */     if (isBeforeFirst())
/* 1316 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.10"), getExceptionInterceptor());
/* 1317 */     if (isAfterLast()) {
/* 1318 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.11"), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1321 */     refreshRow(this.updater, this.thisRow);
/*      */   }
/*      */ 
/*      */   private synchronized void refreshRow(PreparedStatement updateInsertStmt, ResultSetRow rowToRefresh) throws SQLException
/*      */   {
/* 1326 */     if (this.refresher == null) {
/* 1327 */       if (this.refreshSQL == null) {
/* 1328 */         generateStatements();
/*      */       }
/*      */ 
/* 1331 */       this.refresher = ((PreparedStatement)this.connection.clientPrepareStatement(this.refreshSQL));
/*      */     }
/*      */ 
/* 1335 */     this.refresher.clearParameters();
/*      */ 
/* 1337 */     int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/* 1339 */     if (numKeys == 1) {
/* 1340 */       byte[] dataFrom = null;
/* 1341 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */ 
/* 1343 */       if ((!this.doingUpdates) && (!this.onInsertRow)) {
/* 1344 */         dataFrom = (byte[])rowToRefresh.getColumnValue(index);
/*      */       } else {
/* 1346 */         dataFrom = updateInsertStmt.getBytesRepresentation(index);
/*      */ 
/* 1349 */         if ((updateInsertStmt.isNull(index)) || (dataFrom.length == 0))
/* 1350 */           dataFrom = (byte[])rowToRefresh.getColumnValue(index);
/*      */         else {
/* 1352 */           dataFrom = stripBinaryPrefix(dataFrom);
/*      */         }
/*      */       }
/*      */ 
/* 1356 */       this.refresher.setBytesNoEscape(1, dataFrom);
/*      */     } else {
/* 1358 */       for (int i = 0; i < numKeys; i++) {
/* 1359 */         byte[] dataFrom = null;
/* 1360 */         int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */ 
/* 1363 */         if ((!this.doingUpdates) && (!this.onInsertRow)) {
/* 1364 */           dataFrom = (byte[])rowToRefresh.getColumnValue(index);
/*      */         } else {
/* 1366 */           dataFrom = updateInsertStmt.getBytesRepresentation(index);
/*      */ 
/* 1369 */           if ((updateInsertStmt.isNull(index)) || (dataFrom.length == 0))
/* 1370 */             dataFrom = (byte[])rowToRefresh.getColumnValue(index);
/*      */           else {
/* 1372 */             dataFrom = stripBinaryPrefix(dataFrom);
/*      */           }
/*      */         }
/*      */ 
/* 1376 */         this.refresher.setBytesNoEscape(i + 1, dataFrom);
/*      */       }
/*      */     }
/*      */ 
/* 1380 */     ResultSet rs = null;
/*      */     try
/*      */     {
/* 1383 */       rs = this.refresher.executeQuery();
/*      */ 
/* 1385 */       int numCols = rs.getMetaData().getColumnCount();
/*      */ 
/* 1387 */       if (rs.next()) {
/* 1388 */         for (int i = 0; i < numCols; i++) {
/* 1389 */           byte[] val = rs.getBytes(i + 1);
/*      */ 
/* 1391 */           if ((val == null) || (rs.wasNull()))
/* 1392 */             rowToRefresh.setColumnValue(i, null);
/*      */           else
/* 1394 */             rowToRefresh.setColumnValue(i, rs.getBytes(i + 1));
/*      */         }
/*      */       }
/*      */       else {
/* 1398 */         throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.12"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1403 */       if (rs != null)
/*      */         try {
/* 1405 */           rs.close();
/*      */         }
/*      */         catch (SQLException ex)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean relative(int rows)
/*      */     throws SQLException
/*      */   {
/* 1440 */     return super.relative(rows);
/*      */   }
/*      */ 
/*      */   private void resetInserter() throws SQLException {
/* 1444 */     this.inserter.clearParameters();
/*      */ 
/* 1446 */     for (int i = 0; i < this.fields.length; i++)
/* 1447 */       this.inserter.setNull(i + 1, 0);
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 1467 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 1485 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 1503 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   protected void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 1513 */     super.setResultSetConcurrency(concurrencyFlag);
/*      */   }
/*      */ 
/*      */   private byte[] stripBinaryPrefix(byte[] dataFrom)
/*      */   {
/* 1527 */     return StringUtils.stripEnclosure(dataFrom, "_binary'", "'");
/*      */   }
/*      */ 
/*      */   protected synchronized void syncUpdate()
/*      */     throws SQLException
/*      */   {
/* 1538 */     if (this.updater == null) {
/* 1539 */       if (this.updateSQL == null) {
/* 1540 */         generateStatements();
/*      */       }
/*      */ 
/* 1543 */       this.updater = ((PreparedStatement)this.connection.clientPrepareStatement(this.updateSQL));
/*      */     }
/*      */ 
/* 1547 */     int numFields = this.fields.length;
/* 1548 */     this.updater.clearParameters();
/*      */ 
/* 1550 */     for (int i = 0; i < numFields; i++) {
/* 1551 */       if (this.thisRow.getColumnValue(i) != null) {
/* 1552 */         this.updater.setBytes(i + 1, (byte[])this.thisRow.getColumnValue(i), this.fields[i].isBinary(), false);
/*      */       }
/*      */       else {
/* 1555 */         this.updater.setNull(i + 1, 0);
/*      */       }
/*      */     }
/*      */ 
/* 1559 */     int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/* 1561 */     if (numKeys == 1) {
/* 1562 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/* 1563 */       setParamValue(this.updater, numFields + 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */     }
/*      */     else {
/* 1566 */       for (int i = 0; i < numKeys; i++) {
/* 1567 */         int idx = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/* 1568 */         setParamValue(this.updater, numFields + i + 1, this.thisRow, idx, this.fields[idx].getSQLType());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateAsciiStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1593 */     if (!this.onInsertRow) {
/* 1594 */       if (!this.doingUpdates) {
/* 1595 */         this.doingUpdates = true;
/* 1596 */         syncUpdate();
/*      */       }
/*      */ 
/* 1599 */       this.updater.setAsciiStream(columnIndex, x, length);
/*      */     } else {
/* 1601 */       this.inserter.setAsciiStream(columnIndex, x, length);
/* 1602 */       this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateAsciiStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1625 */     updateAsciiStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBigDecimal(int columnIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1644 */     if (!this.onInsertRow) {
/* 1645 */       if (!this.doingUpdates) {
/* 1646 */         this.doingUpdates = true;
/* 1647 */         syncUpdate();
/*      */       }
/*      */ 
/* 1650 */       this.updater.setBigDecimal(columnIndex, x);
/*      */     } else {
/* 1652 */       this.inserter.setBigDecimal(columnIndex, x);
/*      */ 
/* 1654 */       if (x == null)
/* 1655 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       else
/* 1657 */         this.thisRow.setColumnValue(columnIndex - 1, x.toString().getBytes());
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBigDecimal(String columnName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1678 */     updateBigDecimal(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBinaryStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1700 */     if (!this.onInsertRow) {
/* 1701 */       if (!this.doingUpdates) {
/* 1702 */         this.doingUpdates = true;
/* 1703 */         syncUpdate();
/*      */       }
/*      */ 
/* 1706 */       this.updater.setBinaryStream(columnIndex, x, length);
/*      */     } else {
/* 1708 */       this.inserter.setBinaryStream(columnIndex, x, length);
/*      */ 
/* 1710 */       if (x == null)
/* 1711 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       else
/* 1713 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBinaryStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1737 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBlob(int columnIndex, Blob blob)
/*      */     throws SQLException
/*      */   {
/* 1745 */     if (!this.onInsertRow) {
/* 1746 */       if (!this.doingUpdates) {
/* 1747 */         this.doingUpdates = true;
/* 1748 */         syncUpdate();
/*      */       }
/*      */ 
/* 1751 */       this.updater.setBlob(columnIndex, blob);
/*      */     } else {
/* 1753 */       this.inserter.setBlob(columnIndex, blob);
/*      */ 
/* 1755 */       if (blob == null)
/* 1756 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       else
/* 1758 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBlob(String columnName, Blob blob)
/*      */     throws SQLException
/*      */   {
/* 1768 */     updateBlob(findColumn(columnName), blob);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBoolean(int columnIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1787 */     if (!this.onInsertRow) {
/* 1788 */       if (!this.doingUpdates) {
/* 1789 */         this.doingUpdates = true;
/* 1790 */         syncUpdate();
/*      */       }
/*      */ 
/* 1793 */       this.updater.setBoolean(columnIndex, x);
/*      */     } else {
/* 1795 */       this.inserter.setBoolean(columnIndex, x);
/*      */ 
/* 1797 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBoolean(String columnName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1818 */     updateBoolean(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateByte(int columnIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1837 */     if (!this.onInsertRow) {
/* 1838 */       if (!this.doingUpdates) {
/* 1839 */         this.doingUpdates = true;
/* 1840 */         syncUpdate();
/*      */       }
/*      */ 
/* 1843 */       this.updater.setByte(columnIndex, x);
/*      */     } else {
/* 1845 */       this.inserter.setByte(columnIndex, x);
/*      */ 
/* 1847 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateByte(String columnName, byte x)
/*      */     throws SQLException
/*      */   {
/* 1868 */     updateByte(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBytes(int columnIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1887 */     if (!this.onInsertRow) {
/* 1888 */       if (!this.doingUpdates) {
/* 1889 */         this.doingUpdates = true;
/* 1890 */         syncUpdate();
/*      */       }
/*      */ 
/* 1893 */       this.updater.setBytes(columnIndex, x);
/*      */     } else {
/* 1895 */       this.inserter.setBytes(columnIndex, x);
/*      */ 
/* 1897 */       this.thisRow.setColumnValue(columnIndex - 1, x);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBytes(String columnName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1917 */     updateBytes(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateCharacterStream(int columnIndex, Reader x, int length)
/*      */     throws SQLException
/*      */   {
/* 1939 */     if (!this.onInsertRow) {
/* 1940 */       if (!this.doingUpdates) {
/* 1941 */         this.doingUpdates = true;
/* 1942 */         syncUpdate();
/*      */       }
/*      */ 
/* 1945 */       this.updater.setCharacterStream(columnIndex, x, length);
/*      */     } else {
/* 1947 */       this.inserter.setCharacterStream(columnIndex, x, length);
/*      */ 
/* 1949 */       if (x == null)
/* 1950 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       else
/* 1952 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateCharacterStream(String columnName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1976 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */ 
/*      */   public void updateClob(int columnIndex, Clob clob)
/*      */     throws SQLException
/*      */   {
/* 1984 */     if (clob == null)
/* 1985 */       updateNull(columnIndex);
/*      */     else
/* 1987 */       updateCharacterStream(columnIndex, clob.getCharacterStream(), (int)clob.length());
/*      */   }
/*      */ 
/*      */   public synchronized void updateDate(int columnIndex, Date x)
/*      */     throws SQLException
/*      */   {
/* 2008 */     if (!this.onInsertRow) {
/* 2009 */       if (!this.doingUpdates) {
/* 2010 */         this.doingUpdates = true;
/* 2011 */         syncUpdate();
/*      */       }
/*      */ 
/* 2014 */       this.updater.setDate(columnIndex, x);
/*      */     } else {
/* 2016 */       this.inserter.setDate(columnIndex, x);
/*      */ 
/* 2018 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateDate(String columnName, Date x)
/*      */     throws SQLException
/*      */   {
/* 2039 */     updateDate(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateDouble(int columnIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 2058 */     if (!this.onInsertRow) {
/* 2059 */       if (!this.doingUpdates) {
/* 2060 */         this.doingUpdates = true;
/* 2061 */         syncUpdate();
/*      */       }
/*      */ 
/* 2064 */       this.updater.setDouble(columnIndex, x);
/*      */     } else {
/* 2066 */       this.inserter.setDouble(columnIndex, x);
/*      */ 
/* 2068 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateDouble(String columnName, double x)
/*      */     throws SQLException
/*      */   {
/* 2089 */     updateDouble(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateFloat(int columnIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 2108 */     if (!this.onInsertRow) {
/* 2109 */       if (!this.doingUpdates) {
/* 2110 */         this.doingUpdates = true;
/* 2111 */         syncUpdate();
/*      */       }
/*      */ 
/* 2114 */       this.updater.setFloat(columnIndex, x);
/*      */     } else {
/* 2116 */       this.inserter.setFloat(columnIndex, x);
/*      */ 
/* 2118 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateFloat(String columnName, float x)
/*      */     throws SQLException
/*      */   {
/* 2139 */     updateFloat(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateInt(int columnIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 2158 */     if (!this.onInsertRow) {
/* 2159 */       if (!this.doingUpdates) {
/* 2160 */         this.doingUpdates = true;
/* 2161 */         syncUpdate();
/*      */       }
/*      */ 
/* 2164 */       this.updater.setInt(columnIndex, x);
/*      */     } else {
/* 2166 */       this.inserter.setInt(columnIndex, x);
/*      */ 
/* 2168 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateInt(String columnName, int x)
/*      */     throws SQLException
/*      */   {
/* 2189 */     updateInt(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateLong(int columnIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 2208 */     if (!this.onInsertRow) {
/* 2209 */       if (!this.doingUpdates) {
/* 2210 */         this.doingUpdates = true;
/* 2211 */         syncUpdate();
/*      */       }
/*      */ 
/* 2214 */       this.updater.setLong(columnIndex, x);
/*      */     } else {
/* 2216 */       this.inserter.setLong(columnIndex, x);
/*      */ 
/* 2218 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateLong(String columnName, long x)
/*      */     throws SQLException
/*      */   {
/* 2239 */     updateLong(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateNull(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2255 */     if (!this.onInsertRow) {
/* 2256 */       if (!this.doingUpdates) {
/* 2257 */         this.doingUpdates = true;
/* 2258 */         syncUpdate();
/*      */       }
/*      */ 
/* 2261 */       this.updater.setNull(columnIndex, 0);
/*      */     } else {
/* 2263 */       this.inserter.setNull(columnIndex, 0);
/*      */ 
/* 2265 */       this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateNull(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2282 */     updateNull(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(int columnIndex, Object x)
/*      */     throws SQLException
/*      */   {
/* 2301 */     if (!this.onInsertRow) {
/* 2302 */       if (!this.doingUpdates) {
/* 2303 */         this.doingUpdates = true;
/* 2304 */         syncUpdate();
/*      */       }
/*      */ 
/* 2307 */       this.updater.setObject(columnIndex, x);
/*      */     } else {
/* 2309 */       this.inserter.setObject(columnIndex, x);
/*      */ 
/* 2311 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(int columnIndex, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 2336 */     if (!this.onInsertRow) {
/* 2337 */       if (!this.doingUpdates) {
/* 2338 */         this.doingUpdates = true;
/* 2339 */         syncUpdate();
/*      */       }
/*      */ 
/* 2342 */       this.updater.setObject(columnIndex, x);
/*      */     } else {
/* 2344 */       this.inserter.setObject(columnIndex, x);
/*      */ 
/* 2346 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(String columnName, Object x)
/*      */     throws SQLException
/*      */   {
/* 2367 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(String columnName, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 2390 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateRow()
/*      */     throws SQLException
/*      */   {
/* 2404 */     if (!this.isUpdatable) {
/* 2405 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/* 2408 */     if (this.doingUpdates) {
/* 2409 */       this.updater.executeUpdate();
/* 2410 */       refreshRow();
/* 2411 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/* 2417 */     syncUpdate();
/*      */   }
/*      */ 
/*      */   public synchronized void updateShort(int columnIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 2436 */     if (!this.onInsertRow) {
/* 2437 */       if (!this.doingUpdates) {
/* 2438 */         this.doingUpdates = true;
/* 2439 */         syncUpdate();
/*      */       }
/*      */ 
/* 2442 */       this.updater.setShort(columnIndex, x);
/*      */     } else {
/* 2444 */       this.inserter.setShort(columnIndex, x);
/*      */ 
/* 2446 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateShort(String columnName, short x)
/*      */     throws SQLException
/*      */   {
/* 2467 */     updateShort(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateString(int columnIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 2486 */     checkClosed();
/*      */ 
/* 2488 */     if (!this.onInsertRow) {
/* 2489 */       if (!this.doingUpdates) {
/* 2490 */         this.doingUpdates = true;
/* 2491 */         syncUpdate();
/*      */       }
/*      */ 
/* 2494 */       this.updater.setString(columnIndex, x);
/*      */     } else {
/* 2496 */       this.inserter.setString(columnIndex, x);
/*      */ 
/* 2498 */       if (x == null) {
/* 2499 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       }
/* 2501 */       else if (getCharConverter() != null) {
/* 2502 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */       }
/*      */       else
/*      */       {
/* 2507 */         this.thisRow.setColumnValue(columnIndex - 1, x.getBytes());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateString(String columnName, String x)
/*      */     throws SQLException
/*      */   {
/* 2529 */     updateString(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateTime(int columnIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 2548 */     if (!this.onInsertRow) {
/* 2549 */       if (!this.doingUpdates) {
/* 2550 */         this.doingUpdates = true;
/* 2551 */         syncUpdate();
/*      */       }
/*      */ 
/* 2554 */       this.updater.setTime(columnIndex, x);
/*      */     } else {
/* 2556 */       this.inserter.setTime(columnIndex, x);
/*      */ 
/* 2558 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateTime(String columnName, Time x)
/*      */     throws SQLException
/*      */   {
/* 2579 */     updateTime(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateTimestamp(int columnIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2598 */     if (!this.onInsertRow) {
/* 2599 */       if (!this.doingUpdates) {
/* 2600 */         this.doingUpdates = true;
/* 2601 */         syncUpdate();
/*      */       }
/*      */ 
/* 2604 */       this.updater.setTimestamp(columnIndex, x);
/*      */     } else {
/* 2606 */       this.inserter.setTimestamp(columnIndex, x);
/*      */ 
/* 2608 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateTimestamp(String columnName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2629 */     updateTimestamp(findColumn(columnName), x);
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.UpdatableResultSet
 * JD-Core Version:    0.6.0
 */