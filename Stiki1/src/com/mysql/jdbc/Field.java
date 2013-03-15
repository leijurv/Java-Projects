/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.regex.PatternSyntaxException;
/*      */ 
/*      */ public class Field
/*      */ {
/*      */   private static final int AUTO_INCREMENT_FLAG = 512;
/*      */   private static final int NO_CHARSET_INFO = -1;
/*      */   private byte[] buffer;
/*   47 */   private int charsetIndex = 0;
/*      */ 
/*   49 */   private String charsetName = null;
/*      */   private int colDecimals;
/*      */   private short colFlag;
/*   55 */   private String collationName = null;
/*      */ 
/*   57 */   private ConnectionImpl connection = null;
/*      */ 
/*   59 */   private String databaseName = null;
/*      */ 
/*   61 */   private int databaseNameLength = -1;
/*      */ 
/*   64 */   private int databaseNameStart = -1;
/*      */ 
/*   66 */   private int defaultValueLength = -1;
/*      */ 
/*   69 */   private int defaultValueStart = -1;
/*      */ 
/*   71 */   private String fullName = null;
/*      */ 
/*   73 */   private String fullOriginalName = null;
/*      */ 
/*   75 */   private boolean isImplicitTempTable = false;
/*      */   private long length;
/*   79 */   private int mysqlType = -1;
/*      */   private String name;
/*      */   private int nameLength;
/*      */   private int nameStart;
/*   87 */   private String originalColumnName = null;
/*      */ 
/*   89 */   private int originalColumnNameLength = -1;
/*      */ 
/*   92 */   private int originalColumnNameStart = -1;
/*      */ 
/*   94 */   private String originalTableName = null;
/*      */ 
/*   96 */   private int originalTableNameLength = -1;
/*      */ 
/*   99 */   private int originalTableNameStart = -1;
/*      */ 
/*  101 */   private int precisionAdjustFactor = 0;
/*      */ 
/*  103 */   private int sqlType = -1;
/*      */   private String tableName;
/*      */   private int tableNameLength;
/*      */   private int tableNameStart;
/*  111 */   private boolean useOldNameMetadata = false;
/*      */   private boolean isSingleBit;
/*      */   private int maxBytesPerChar;
/*      */ 
/*      */   Field(ConnectionImpl conn, byte[] buffer, int databaseNameStart, int databaseNameLength, int tableNameStart, int tableNameLength, int originalTableNameStart, int originalTableNameLength, int nameStart, int nameLength, int originalColumnNameStart, int originalColumnNameLength, long length, int mysqlType, short colFlag, int colDecimals, int defaultValueStart, int defaultValueLength, int charsetIndex)
/*      */     throws SQLException
/*      */   {
/*  127 */     this.connection = conn;
/*  128 */     this.buffer = buffer;
/*  129 */     this.nameStart = nameStart;
/*  130 */     this.nameLength = nameLength;
/*  131 */     this.tableNameStart = tableNameStart;
/*  132 */     this.tableNameLength = tableNameLength;
/*  133 */     this.length = length;
/*  134 */     this.colFlag = colFlag;
/*  135 */     this.colDecimals = colDecimals;
/*  136 */     this.mysqlType = mysqlType;
/*      */ 
/*  139 */     this.databaseNameStart = databaseNameStart;
/*  140 */     this.databaseNameLength = databaseNameLength;
/*      */ 
/*  142 */     this.originalTableNameStart = originalTableNameStart;
/*  143 */     this.originalTableNameLength = originalTableNameLength;
/*      */ 
/*  145 */     this.originalColumnNameStart = originalColumnNameStart;
/*  146 */     this.originalColumnNameLength = originalColumnNameLength;
/*      */ 
/*  148 */     this.defaultValueStart = defaultValueStart;
/*  149 */     this.defaultValueLength = defaultValueLength;
/*      */ 
/*  153 */     this.charsetIndex = charsetIndex;
/*      */ 
/*  157 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */ 
/*  159 */     checkForImplicitTemporaryTable();
/*      */ 
/*  161 */     boolean isFromFunction = this.originalTableNameLength == 0;
/*      */ 
/*  163 */     if (this.mysqlType == 252) {
/*  164 */       if (((this.connection != null) && (this.connection.getBlobsAreStrings())) || ((this.connection.getFunctionsNeverReturnBlobs()) && (isFromFunction)))
/*      */       {
/*  166 */         this.sqlType = 12;
/*  167 */         this.mysqlType = 15;
/*  168 */       } else if ((this.charsetIndex == 63) || (!this.connection.versionMeetsMinimum(4, 1, 0)))
/*      */       {
/*  170 */         if ((this.connection.getUseBlobToStoreUTF8OutsideBMP()) && (shouldSetupForUtf8StringInBlob()))
/*      */         {
/*  172 */           setupForUtf8StringInBlob();
/*      */         } else {
/*  174 */           setBlobTypeBasedOnLength();
/*  175 */           this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */         }
/*      */       }
/*      */       else {
/*  179 */         this.mysqlType = 253;
/*  180 */         this.sqlType = -1;
/*      */       }
/*      */     }
/*      */ 
/*  184 */     if ((this.sqlType == -6) && (this.length == 1L) && (this.connection.getTinyInt1isBit()))
/*      */     {
/*  187 */       if (conn.getTinyInt1isBit()) {
/*  188 */         if (conn.getTransformedBitIsBoolean())
/*  189 */           this.sqlType = 16;
/*      */         else {
/*  191 */           this.sqlType = -7;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  197 */     if ((!isNativeNumericType()) && (!isNativeDateTimeType())) {
/*  198 */       this.charsetName = this.connection.getCharsetNameForIndex(this.charsetIndex);
/*      */ 
/*  205 */       boolean isBinary = isBinary();
/*      */ 
/*  207 */       if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (this.mysqlType == 253) && (isBinary) && (this.charsetIndex == 63))
/*      */       {
/*  211 */         if ((this.connection != null) && (this.connection.getFunctionsNeverReturnBlobs()) && (isFromFunction)) {
/*  212 */           this.sqlType = 12;
/*  213 */           this.mysqlType = 15;
/*  214 */         } else if (isOpaqueBinary()) {
/*  215 */           this.sqlType = -3;
/*      */         }
/*      */       }
/*      */ 
/*  219 */       if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (this.mysqlType == 254) && (isBinary) && (this.charsetIndex == 63))
/*      */       {
/*  229 */         if ((isOpaqueBinary()) && (!this.connection.getBlobsAreStrings())) {
/*  230 */           this.sqlType = -2;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  236 */       if (this.mysqlType == 16) {
/*  237 */         this.isSingleBit = (this.length == 0L);
/*      */ 
/*  239 */         if ((this.connection != null) && ((this.connection.versionMeetsMinimum(5, 0, 21)) || (this.connection.versionMeetsMinimum(5, 1, 10))) && (this.length == 1L))
/*      */         {
/*  241 */           this.isSingleBit = true;
/*      */         }
/*      */ 
/*  244 */         if (this.isSingleBit) {
/*  245 */           this.sqlType = -7;
/*      */         } else {
/*  247 */           this.sqlType = -3;
/*  248 */           this.colFlag = (short)(this.colFlag | 0x80);
/*  249 */           this.colFlag = (short)(this.colFlag | 0x10);
/*  250 */           isBinary = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  257 */       if ((this.sqlType == -4) && (!isBinary))
/*  258 */         this.sqlType = -1;
/*  259 */       else if ((this.sqlType == -3) && (!isBinary))
/*  260 */         this.sqlType = 12;
/*      */     }
/*      */     else {
/*  263 */       this.charsetName = "US-ASCII";
/*      */     }
/*      */ 
/*  269 */     if (!isUnsigned()) {
/*  270 */       switch (this.mysqlType) {
/*      */       case 0:
/*      */       case 246:
/*  273 */         this.precisionAdjustFactor = -1;
/*      */ 
/*  275 */         break;
/*      */       case 4:
/*      */       case 5:
/*  278 */         this.precisionAdjustFactor = 1;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  283 */       switch (this.mysqlType) {
/*      */       case 4:
/*      */       case 5:
/*  286 */         this.precisionAdjustFactor = 1;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean shouldSetupForUtf8StringInBlob()
/*      */     throws SQLException
/*      */   {
/*  294 */     String includePattern = this.connection.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */ 
/*  296 */     String excludePattern = this.connection.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */ 
/*  299 */     if ((excludePattern != null) && (!StringUtils.isEmptyOrWhitespaceOnly(excludePattern))) {
/*      */       try
/*      */       {
/*  302 */         if (getOriginalName().matches(excludePattern)) {
/*  303 */           if ((includePattern != null) && (!StringUtils.isEmptyOrWhitespaceOnly(includePattern))) {
/*      */             try
/*      */             {
/*  306 */               if (getOriginalName().matches(includePattern))
/*  307 */                 return true;
/*      */             }
/*      */             catch (PatternSyntaxException pse) {
/*  310 */               SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpIncludedColumnNamePattern\"", "S1009", this.connection.getExceptionInterceptor());
/*      */ 
/*  315 */               if (!this.connection.getParanoid()) {
/*  316 */                 sqlEx.initCause(pse);
/*      */               }
/*      */ 
/*  319 */               throw sqlEx;
/*      */             }
/*      */           }
/*      */ 
/*  323 */           return false;
/*      */         }
/*      */       } catch (PatternSyntaxException pse) {
/*  326 */         SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpExcludedColumnNamePattern\"", "S1009", this.connection.getExceptionInterceptor());
/*      */ 
/*  331 */         if (!this.connection.getParanoid()) {
/*  332 */           sqlEx.initCause(pse);
/*      */         }
/*      */ 
/*  335 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */ 
/*  339 */     return true;
/*      */   }
/*      */ 
/*      */   private void setupForUtf8StringInBlob() {
/*  343 */     if ((this.length == 255L) || (this.length == 65535L)) {
/*  344 */       this.mysqlType = 15;
/*  345 */       this.sqlType = 12;
/*      */     } else {
/*  347 */       this.mysqlType = 253;
/*  348 */       this.sqlType = -1;
/*      */     }
/*      */ 
/*  351 */     this.charsetIndex = 33;
/*      */   }
/*      */ 
/*      */   Field(ConnectionImpl conn, byte[] buffer, int nameStart, int nameLength, int tableNameStart, int tableNameLength, int length, int mysqlType, short colFlag, int colDecimals)
/*      */     throws SQLException
/*      */   {
/*  360 */     this(conn, buffer, -1, -1, tableNameStart, tableNameLength, -1, -1, nameStart, nameLength, -1, -1, length, mysqlType, colFlag, colDecimals, -1, -1, -1);
/*      */   }
/*      */ 
/*      */   Field(String tableName, String columnName, int jdbcType, int length)
/*      */   {
/*  369 */     this.tableName = tableName;
/*  370 */     this.name = columnName;
/*  371 */     this.length = length;
/*  372 */     this.sqlType = jdbcType;
/*  373 */     this.colFlag = 0;
/*  374 */     this.colDecimals = 0;
/*      */   }
/*      */ 
/*      */   Field(String tableName, String columnName, int charsetIndex, int jdbcType, int length)
/*      */   {
/*  395 */     this.tableName = tableName;
/*  396 */     this.name = columnName;
/*  397 */     this.length = length;
/*  398 */     this.sqlType = jdbcType;
/*  399 */     this.colFlag = 0;
/*  400 */     this.colDecimals = 0;
/*  401 */     this.charsetIndex = charsetIndex;
/*      */ 
/*  403 */     switch (this.sqlType) {
/*      */     case -3:
/*      */     case -2:
/*  406 */       this.colFlag = (short)(this.colFlag | 0x80);
/*  407 */       this.colFlag = (short)(this.colFlag | 0x10);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkForImplicitTemporaryTable()
/*      */   {
/*  413 */     this.isImplicitTempTable = ((this.tableNameLength > 5) && (this.buffer[this.tableNameStart] == 35) && (this.buffer[(this.tableNameStart + 1)] == 115) && (this.buffer[(this.tableNameStart + 2)] == 113) && (this.buffer[(this.tableNameStart + 3)] == 108) && (this.buffer[(this.tableNameStart + 4)] == 95));
/*      */   }
/*      */ 
/*      */   public String getCharacterSet()
/*      */     throws SQLException
/*      */   {
/*  427 */     return this.charsetName;
/*      */   }
/*      */ 
/*      */   public void setCharacterSet(String javaEncodingName) throws SQLException {
/*  431 */     this.charsetName = javaEncodingName;
/*  432 */     this.charsetIndex = CharsetMapping.getCharsetIndexForMysqlEncodingName(javaEncodingName);
/*      */   }
/*      */ 
/*      */   public synchronized String getCollation() throws SQLException
/*      */   {
/*  437 */     if ((this.collationName == null) && 
/*  438 */       (this.connection != null) && 
/*  439 */       (this.connection.versionMeetsMinimum(4, 1, 0))) {
/*  440 */       if (this.connection.getUseDynamicCharsetInfo()) {
/*  441 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  444 */         String quotedIdStr = dbmd.getIdentifierQuoteString();
/*      */ 
/*  446 */         if (" ".equals(quotedIdStr)) {
/*  447 */           quotedIdStr = "";
/*      */         }
/*      */ 
/*  450 */         String csCatalogName = getDatabaseName();
/*  451 */         String csTableName = getOriginalTableName();
/*  452 */         String csColumnName = getOriginalName();
/*      */ 
/*  454 */         if ((csCatalogName != null) && (csCatalogName.length() != 0) && (csTableName != null) && (csTableName.length() != 0) && (csColumnName != null) && (csColumnName.length() != 0))
/*      */         {
/*  458 */           StringBuffer queryBuf = new StringBuffer(csCatalogName.length() + csTableName.length() + 28);
/*      */ 
/*  461 */           queryBuf.append("SHOW FULL COLUMNS FROM ");
/*  462 */           queryBuf.append(quotedIdStr);
/*  463 */           queryBuf.append(csCatalogName);
/*  464 */           queryBuf.append(quotedIdStr);
/*  465 */           queryBuf.append(".");
/*  466 */           queryBuf.append(quotedIdStr);
/*  467 */           queryBuf.append(csTableName);
/*  468 */           queryBuf.append(quotedIdStr);
/*      */ 
/*  470 */           Statement collationStmt = null;
/*  471 */           ResultSet collationRs = null;
/*      */           try
/*      */           {
/*  474 */             collationStmt = this.connection.createStatement();
/*      */ 
/*  476 */             collationRs = collationStmt.executeQuery(queryBuf.toString());
/*      */ 
/*  479 */             while (collationRs.next()) {
/*  480 */               if (!csColumnName.equals(collationRs.getString("Field")))
/*      */                 continue;
/*  482 */               this.collationName = collationRs.getString("Collation");
/*      */             }
/*      */ 
/*      */           }
/*      */           finally
/*      */           {
/*  489 */             if (collationRs != null) {
/*  490 */               collationRs.close();
/*  491 */               collationRs = null;
/*      */             }
/*      */ 
/*  494 */             if (collationStmt != null) {
/*  495 */               collationStmt.close();
/*  496 */               collationStmt = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       } else {
/*  501 */         this.collationName = CharsetMapping.INDEX_TO_COLLATION[this.charsetIndex];
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  507 */     return this.collationName;
/*      */   }
/*      */ 
/*      */   public String getColumnLabel() throws SQLException {
/*  511 */     return getName();
/*      */   }
/*      */ 
/*      */   public String getDatabaseName()
/*      */     throws SQLException
/*      */   {
/*  520 */     if ((this.databaseName == null) && (this.databaseNameStart != -1) && (this.databaseNameLength != -1))
/*      */     {
/*  522 */       this.databaseName = getStringFromBytes(this.databaseNameStart, this.databaseNameLength);
/*      */     }
/*      */ 
/*  526 */     return this.databaseName;
/*      */   }
/*      */ 
/*      */   int getDecimals() {
/*  530 */     return this.colDecimals;
/*      */   }
/*      */ 
/*      */   public String getFullName()
/*      */     throws SQLException
/*      */   {
/*  539 */     if (this.fullName == null) {
/*  540 */       StringBuffer fullNameBuf = new StringBuffer(getTableName().length() + 1 + getName().length());
/*      */ 
/*  542 */       fullNameBuf.append(this.tableName);
/*      */ 
/*  545 */       fullNameBuf.append('.');
/*  546 */       fullNameBuf.append(this.name);
/*  547 */       this.fullName = fullNameBuf.toString();
/*  548 */       fullNameBuf = null;
/*      */     }
/*      */ 
/*  551 */     return this.fullName;
/*      */   }
/*      */ 
/*      */   public String getFullOriginalName()
/*      */     throws SQLException
/*      */   {
/*  560 */     getOriginalName();
/*      */ 
/*  562 */     if (this.originalColumnName == null) {
/*  563 */       return null;
/*      */     }
/*      */ 
/*  566 */     if (this.fullName == null) {
/*  567 */       StringBuffer fullOriginalNameBuf = new StringBuffer(getOriginalTableName().length() + 1 + getOriginalName().length());
/*      */ 
/*  570 */       fullOriginalNameBuf.append(this.originalTableName);
/*      */ 
/*  573 */       fullOriginalNameBuf.append('.');
/*  574 */       fullOriginalNameBuf.append(this.originalColumnName);
/*  575 */       this.fullOriginalName = fullOriginalNameBuf.toString();
/*  576 */       fullOriginalNameBuf = null;
/*      */     }
/*      */ 
/*  579 */     return this.fullOriginalName;
/*      */   }
/*      */ 
/*      */   public long getLength()
/*      */   {
/*  588 */     return this.length;
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxBytesPerCharacter() throws SQLException {
/*  592 */     if (this.maxBytesPerChar == 0) {
/*  593 */       this.maxBytesPerChar = this.connection.getMaxBytesPerChar(getCharacterSet());
/*      */     }
/*      */ 
/*  596 */     return this.maxBytesPerChar;
/*      */   }
/*      */ 
/*      */   public int getMysqlType()
/*      */   {
/*  605 */     return this.mysqlType;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */     throws SQLException
/*      */   {
/*  614 */     if (this.name == null) {
/*  615 */       this.name = getStringFromBytes(this.nameStart, this.nameLength);
/*      */     }
/*      */ 
/*  618 */     return this.name;
/*      */   }
/*      */ 
/*      */   public String getNameNoAliases() throws SQLException {
/*  622 */     if (this.useOldNameMetadata) {
/*  623 */       return getName();
/*      */     }
/*      */ 
/*  626 */     if ((this.connection != null) && (this.connection.versionMeetsMinimum(4, 1, 0)))
/*      */     {
/*  628 */       return getOriginalName();
/*      */     }
/*      */ 
/*  631 */     return getName();
/*      */   }
/*      */ 
/*      */   public String getOriginalName()
/*      */     throws SQLException
/*      */   {
/*  640 */     if ((this.originalColumnName == null) && (this.originalColumnNameStart != -1) && (this.originalColumnNameLength != -1))
/*      */     {
/*  643 */       this.originalColumnName = getStringFromBytes(this.originalColumnNameStart, this.originalColumnNameLength);
/*      */     }
/*      */ 
/*  647 */     return this.originalColumnName;
/*      */   }
/*      */ 
/*      */   public String getOriginalTableName()
/*      */     throws SQLException
/*      */   {
/*  656 */     if ((this.originalTableName == null) && (this.originalTableNameStart != -1) && (this.originalTableNameLength != -1))
/*      */     {
/*  659 */       this.originalTableName = getStringFromBytes(this.originalTableNameStart, this.originalTableNameLength);
/*      */     }
/*      */ 
/*  663 */     return this.originalTableName;
/*      */   }
/*      */ 
/*      */   public int getPrecisionAdjustFactor()
/*      */   {
/*  675 */     return this.precisionAdjustFactor;
/*      */   }
/*      */ 
/*      */   public int getSQLType()
/*      */   {
/*  684 */     return this.sqlType;
/*      */   }
/*      */ 
/*      */   private String getStringFromBytes(int stringStart, int stringLength)
/*      */     throws SQLException
/*      */   {
/*  693 */     if ((stringStart == -1) || (stringLength == -1)) {
/*  694 */       return null;
/*      */     }
/*      */ 
/*  697 */     String stringVal = null;
/*      */ 
/*  699 */     if (this.connection != null) {
/*  700 */       if (this.connection.getUseUnicode()) {
/*  701 */         String encoding = this.connection.getCharacterSetMetadata();
/*      */ 
/*  703 */         if (encoding == null) {
/*  704 */           encoding = this.connection.getEncoding();
/*      */         }
/*      */ 
/*  707 */         if (encoding != null) {
/*  708 */           SingleByteCharsetConverter converter = null;
/*      */ 
/*  710 */           if (this.connection != null) {
/*  711 */             converter = this.connection.getCharsetConverter(encoding);
/*      */           }
/*      */ 
/*  715 */           if (converter != null) {
/*  716 */             stringVal = converter.toString(this.buffer, stringStart, stringLength);
/*      */           }
/*      */           else
/*      */           {
/*  720 */             byte[] stringBytes = new byte[stringLength];
/*      */ 
/*  722 */             int endIndex = stringStart + stringLength;
/*  723 */             int pos = 0;
/*      */ 
/*  725 */             for (int i = stringStart; i < endIndex; i++) {
/*  726 */               stringBytes[(pos++)] = this.buffer[i];
/*      */             }
/*      */             try
/*      */             {
/*  730 */               stringVal = new String(stringBytes, encoding);
/*      */             } catch (UnsupportedEncodingException ue) {
/*  732 */               throw new RuntimeException(Messages.getString("Field.12") + encoding + Messages.getString("Field.13"));
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  739 */           stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  744 */         stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  749 */       stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */     }
/*      */ 
/*  753 */     return stringVal;
/*      */   }
/*      */ 
/*      */   public String getTable()
/*      */     throws SQLException
/*      */   {
/*  762 */     return getTableName();
/*      */   }
/*      */ 
/*      */   public String getTableName()
/*      */     throws SQLException
/*      */   {
/*  771 */     if (this.tableName == null) {
/*  772 */       this.tableName = getStringFromBytes(this.tableNameStart, this.tableNameLength);
/*      */     }
/*      */ 
/*  776 */     return this.tableName;
/*      */   }
/*      */ 
/*      */   public String getTableNameNoAliases() throws SQLException {
/*  780 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/*  781 */       return getOriginalTableName();
/*      */     }
/*      */ 
/*  784 */     return getTableName();
/*      */   }
/*      */ 
/*      */   public boolean isAutoIncrement()
/*      */   {
/*  793 */     return (this.colFlag & 0x200) > 0;
/*      */   }
/*      */ 
/*      */   public boolean isBinary()
/*      */   {
/*  802 */     return (this.colFlag & 0x80) > 0;
/*      */   }
/*      */ 
/*      */   public boolean isBlob()
/*      */   {
/*  811 */     return (this.colFlag & 0x10) > 0;
/*      */   }
/*      */ 
/*      */   private boolean isImplicitTemporaryTable()
/*      */   {
/*  820 */     return this.isImplicitTempTable;
/*      */   }
/*      */ 
/*      */   public boolean isMultipleKey()
/*      */   {
/*  829 */     return (this.colFlag & 0x8) > 0;
/*      */   }
/*      */ 
/*      */   boolean isNotNull() {
/*  833 */     return (this.colFlag & 0x1) > 0;
/*      */   }
/*      */ 
/*      */   boolean isOpaqueBinary()
/*      */     throws SQLException
/*      */   {
/*  843 */     if ((this.charsetIndex == 63) && (isBinary()) && ((getMysqlType() == 254) || (getMysqlType() == 253)))
/*      */     {
/*  847 */       if ((this.originalTableNameLength == 0) && (this.connection != null) && (!this.connection.versionMeetsMinimum(5, 0, 25)))
/*      */       {
/*  849 */         return false;
/*      */       }
/*      */ 
/*  855 */       return !isImplicitTemporaryTable();
/*      */     }
/*      */ 
/*  858 */     return (this.connection.versionMeetsMinimum(4, 1, 0)) && ("binary".equalsIgnoreCase(getCharacterSet()));
/*      */   }
/*      */ 
/*      */   public boolean isPrimaryKey()
/*      */   {
/*  869 */     return (this.colFlag & 0x2) > 0;
/*      */   }
/*      */ 
/*      */   boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*  879 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/*  880 */       String orgColumnName = getOriginalName();
/*  881 */       String orgTableName = getOriginalTableName();
/*      */ 
/*  883 */       return (orgColumnName == null) || (orgColumnName.length() <= 0) || (orgTableName == null) || (orgTableName.length() <= 0);
/*      */     }
/*      */ 
/*  887 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isUniqueKey()
/*      */   {
/*  896 */     return (this.colFlag & 0x4) > 0;
/*      */   }
/*      */ 
/*      */   public boolean isUnsigned()
/*      */   {
/*  905 */     return (this.colFlag & 0x20) > 0;
/*      */   }
/*      */ 
/*      */   public void setUnsigned() {
/*  909 */     this.colFlag = (short)(this.colFlag | 0x20);
/*      */   }
/*      */ 
/*      */   public boolean isZeroFill()
/*      */   {
/*  918 */     return (this.colFlag & 0x40) > 0;
/*      */   }
/*      */ 
/*      */   private void setBlobTypeBasedOnLength()
/*      */   {
/*  927 */     if (this.length == 255L)
/*  928 */       this.mysqlType = 249;
/*  929 */     else if (this.length == 65535L)
/*  930 */       this.mysqlType = 252;
/*  931 */     else if (this.length == 16777215L)
/*  932 */       this.mysqlType = 250;
/*  933 */     else if (this.length == 4294967295L)
/*  934 */       this.mysqlType = 251;
/*      */   }
/*      */ 
/*      */   private boolean isNativeNumericType()
/*      */   {
/*  939 */     return ((this.mysqlType >= 1) && (this.mysqlType <= 5)) || (this.mysqlType == 8) || (this.mysqlType == 13);
/*      */   }
/*      */ 
/*      */   private boolean isNativeDateTimeType()
/*      */   {
/*  946 */     return (this.mysqlType == 10) || (this.mysqlType == 14) || (this.mysqlType == 12) || (this.mysqlType == 11) || (this.mysqlType == 7);
/*      */   }
/*      */ 
/*      */   public void setConnection(ConnectionImpl conn)
/*      */   {
/*  960 */     this.connection = conn;
/*      */ 
/*  962 */     if ((this.charsetName == null) || (this.charsetIndex == 0))
/*  963 */       this.charsetName = this.connection.getEncoding();
/*      */   }
/*      */ 
/*      */   void setMysqlType(int type)
/*      */   {
/*  968 */     this.mysqlType = type;
/*  969 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */   }
/*      */ 
/*      */   protected void setUseOldNameMetadata(boolean useOldNameMetadata) {
/*  973 */     this.useOldNameMetadata = useOldNameMetadata;
/*      */   }
/*      */ 
/*      */   public String toString() {
/*      */     try {
/*  978 */       StringBuffer asString = new StringBuffer();
/*  979 */       asString.append(super.toString());
/*  980 */       asString.append("[");
/*  981 */       asString.append("catalog=");
/*  982 */       asString.append(getDatabaseName());
/*  983 */       asString.append(",tableName=");
/*  984 */       asString.append(getTableName());
/*  985 */       asString.append(",originalTableName=");
/*  986 */       asString.append(getOriginalTableName());
/*  987 */       asString.append(",columnName=");
/*  988 */       asString.append(getName());
/*  989 */       asString.append(",originalColumnName=");
/*  990 */       asString.append(getOriginalName());
/*  991 */       asString.append(",mysqlType=");
/*  992 */       asString.append(getMysqlType());
/*  993 */       asString.append("(");
/*  994 */       asString.append(MysqlDefs.typeToName(getMysqlType()));
/*  995 */       asString.append(")");
/*  996 */       asString.append(",flags=");
/*      */ 
/*  998 */       if (isAutoIncrement()) {
/*  999 */         asString.append(" AUTO_INCREMENT");
/*      */       }
/*      */ 
/* 1002 */       if (isPrimaryKey()) {
/* 1003 */         asString.append(" PRIMARY_KEY");
/*      */       }
/*      */ 
/* 1006 */       if (isUniqueKey()) {
/* 1007 */         asString.append(" UNIQUE_KEY");
/*      */       }
/*      */ 
/* 1010 */       if (isBinary()) {
/* 1011 */         asString.append(" BINARY");
/*      */       }
/*      */ 
/* 1014 */       if (isBlob()) {
/* 1015 */         asString.append(" BLOB");
/*      */       }
/*      */ 
/* 1018 */       if (isMultipleKey()) {
/* 1019 */         asString.append(" MULTI_KEY");
/*      */       }
/*      */ 
/* 1022 */       if (isUnsigned()) {
/* 1023 */         asString.append(" UNSIGNED");
/*      */       }
/*      */ 
/* 1026 */       if (isZeroFill()) {
/* 1027 */         asString.append(" ZEROFILL");
/*      */       }
/*      */ 
/* 1030 */       asString.append(", charsetIndex=");
/* 1031 */       asString.append(this.charsetIndex);
/* 1032 */       asString.append(", charsetName=");
/* 1033 */       asString.append(this.charsetName);
/*      */ 
/* 1042 */       asString.append("]");
/*      */ 
/* 1044 */       return asString.toString(); } catch (Throwable t) {
/*      */     }
/* 1046 */     return super.toString();
/*      */   }
/*      */ 
/*      */   protected boolean isSingleBit()
/*      */   {
/* 1051 */     return this.isSingleBit;
/*      */   }
/*      */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Field
 * JD-Core Version:    0.6.0
 */