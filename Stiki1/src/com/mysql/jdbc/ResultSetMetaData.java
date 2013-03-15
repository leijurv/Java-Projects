/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class ResultSetMetaData
/*     */   implements java.sql.ResultSetMetaData
/*     */ {
/*     */   Field[] fields;
/*  79 */   boolean useOldAliasBehavior = false;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   private static int clampedGetLength(Field f)
/*     */   {
/*  43 */     long fieldLength = f.getLength();
/*     */ 
/*  45 */     if (fieldLength > 2147483647L) {
/*  46 */       fieldLength = 2147483647L;
/*     */     }
/*     */ 
/*  49 */     return (int)fieldLength;
/*     */   }
/*     */ 
/*     */   private static final boolean isDecimalType(int type)
/*     */   {
/*  61 */     switch (type) {
/*     */     case -7:
/*     */     case -6:
/*     */     case -5:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*  72 */       return true;
/*     */     case -4:
/*     */     case -3:
/*     */     case -2:
/*     */     case -1:
/*     */     case 0:
/*  75 */     case 1: } return false;
/*     */   }
/*     */ 
/*     */   public ResultSetMetaData(Field[] fields, boolean useOldAliasBehavior, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  90 */     this.fields = fields;
/*  91 */     this.useOldAliasBehavior = useOldAliasBehavior;
/*  92 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   public String getCatalogName(int column)
/*     */     throws SQLException
/*     */   {
/* 107 */     Field f = getField(column);
/*     */ 
/* 109 */     String database = f.getDatabaseName();
/*     */ 
/* 111 */     return database == null ? "" : database;
/*     */   }
/*     */ 
/*     */   public String getColumnCharacterEncoding(int column)
/*     */     throws SQLException
/*     */   {
/* 128 */     String mysqlName = getColumnCharacterSet(column);
/*     */ 
/* 130 */     String javaName = null;
/*     */ 
/* 132 */     if (mysqlName != null) {
/* 133 */       javaName = CharsetMapping.getJavaEncodingForMysqlEncoding(mysqlName, null);
/*     */     }
/*     */ 
/* 137 */     return javaName;
/*     */   }
/*     */ 
/*     */   public String getColumnCharacterSet(int column)
/*     */     throws SQLException
/*     */   {
/* 152 */     return getField(column).getCharacterSet();
/*     */   }
/*     */ 
/*     */   public String getColumnClassName(int column)
/*     */     throws SQLException
/*     */   {
/* 178 */     Field f = getField(column);
/*     */ 
/* 180 */     return getClassNameForJavaType(f.getSQLType(), f.isUnsigned(), f.getMysqlType(), (f.isBinary()) || (f.isBlob()), f.isOpaqueBinary());
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */     throws SQLException
/*     */   {
/* 196 */     return this.fields.length;
/*     */   }
/*     */ 
/*     */   public int getColumnDisplaySize(int column)
/*     */     throws SQLException
/*     */   {
/* 211 */     Field f = getField(column);
/*     */ 
/* 213 */     int lengthInBytes = clampedGetLength(f);
/*     */ 
/* 215 */     return lengthInBytes / f.getMaxBytesPerCharacter();
/*     */   }
/*     */ 
/*     */   public String getColumnLabel(int column)
/*     */     throws SQLException
/*     */   {
/* 230 */     if (this.useOldAliasBehavior) {
/* 231 */       return getColumnName(column);
/*     */     }
/*     */ 
/* 234 */     return getField(column).getColumnLabel();
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column)
/*     */     throws SQLException
/*     */   {
/* 249 */     if (this.useOldAliasBehavior) {
/* 250 */       return getField(column).getName();
/*     */     }
/*     */ 
/* 253 */     String name = getField(column).getNameNoAliases();
/*     */ 
/* 255 */     if ((name != null) && (name.length() == 0)) {
/* 256 */       return getField(column).getName();
/*     */     }
/*     */ 
/* 259 */     return name;
/*     */   }
/*     */ 
/*     */   public int getColumnType(int column)
/*     */     throws SQLException
/*     */   {
/* 276 */     return getField(column).getSQLType();
/*     */   }
/*     */ 
/*     */   public String getColumnTypeName(int column)
/*     */     throws SQLException
/*     */   {
/* 291 */     Field field = getField(column);
/*     */ 
/* 293 */     int mysqlType = field.getMysqlType();
/* 294 */     int jdbcType = field.getSQLType();
/*     */ 
/* 296 */     switch (mysqlType) {
/*     */     case 16:
/* 298 */       return "BIT";
/*     */     case 0:
/*     */     case 246:
/* 301 */       return field.isUnsigned() ? "DECIMAL UNSIGNED" : "DECIMAL";
/*     */     case 1:
/* 304 */       return field.isUnsigned() ? "TINYINT UNSIGNED" : "TINYINT";
/*     */     case 2:
/* 307 */       return field.isUnsigned() ? "SMALLINT UNSIGNED" : "SMALLINT";
/*     */     case 3:
/* 310 */       return field.isUnsigned() ? "INT UNSIGNED" : "INT";
/*     */     case 4:
/* 313 */       return field.isUnsigned() ? "FLOAT UNSIGNED" : "FLOAT";
/*     */     case 5:
/* 316 */       return field.isUnsigned() ? "DOUBLE UNSIGNED" : "DOUBLE";
/*     */     case 6:
/* 319 */       return "NULL";
/*     */     case 7:
/* 322 */       return "TIMESTAMP";
/*     */     case 8:
/* 325 */       return field.isUnsigned() ? "BIGINT UNSIGNED" : "BIGINT";
/*     */     case 9:
/* 328 */       return field.isUnsigned() ? "MEDIUMINT UNSIGNED" : "MEDIUMINT";
/*     */     case 10:
/* 331 */       return "DATE";
/*     */     case 11:
/* 334 */       return "TIME";
/*     */     case 12:
/* 337 */       return "DATETIME";
/*     */     case 249:
/* 340 */       return "TINYBLOB";
/*     */     case 250:
/* 343 */       return "MEDIUMBLOB";
/*     */     case 251:
/* 346 */       return "LONGBLOB";
/*     */     case 252:
/* 349 */       if (getField(column).isBinary()) {
/* 350 */         return "BLOB";
/*     */       }
/*     */ 
/* 353 */       return "TEXT";
/*     */     case 15:
/* 356 */       return "VARCHAR";
/*     */     case 253:
/* 359 */       if (jdbcType == -3) {
/* 360 */         return "VARBINARY";
/*     */       }
/*     */ 
/* 363 */       return "VARCHAR";
/*     */     case 254:
/* 366 */       if (jdbcType == -2) {
/* 367 */         return "BINARY";
/*     */       }
/*     */ 
/* 370 */       return "CHAR";
/*     */     case 247:
/* 373 */       return "ENUM";
/*     */     case 13:
/* 376 */       return "YEAR";
/*     */     case 248:
/* 379 */       return "SET";
/*     */     case 255:
/* 382 */       return "GEOMETRY";
/*     */     }
/*     */ 
/* 385 */     return "UNKNOWN";
/*     */   }
/*     */ 
/*     */   protected Field getField(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 401 */     if ((columnIndex < 1) || (columnIndex > this.fields.length)) {
/* 402 */       throw SQLError.createSQLException(Messages.getString("ResultSetMetaData.46"), "S1002", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 406 */     return this.fields[(columnIndex - 1)];
/*     */   }
/*     */ 
/*     */   public int getPrecision(int column)
/*     */     throws SQLException
/*     */   {
/* 421 */     Field f = getField(column);
/*     */ 
/* 427 */     if (isDecimalType(f.getSQLType())) {
/* 428 */       if (f.getDecimals() > 0) {
/* 429 */         return clampedGetLength(f) - 1 + f.getPrecisionAdjustFactor();
/*     */       }
/*     */ 
/* 432 */       return clampedGetLength(f) + f.getPrecisionAdjustFactor();
/*     */     }
/*     */ 
/* 435 */     switch (f.getMysqlType()) {
/*     */     case 249:
/*     */     case 250:
/*     */     case 251:
/*     */     case 252:
/* 440 */       return clampedGetLength(f);
/*     */     }
/*     */ 
/* 447 */     return clampedGetLength(f) / f.getMaxBytesPerCharacter();
/*     */   }
/*     */ 
/*     */   public int getScale(int column)
/*     */     throws SQLException
/*     */   {
/* 464 */     Field f = getField(column);
/*     */ 
/* 466 */     if (isDecimalType(f.getSQLType())) {
/* 467 */       return f.getDecimals();
/*     */     }
/*     */ 
/* 470 */     return 0;
/*     */   }
/*     */ 
/*     */   public String getSchemaName(int column)
/*     */     throws SQLException
/*     */   {
/* 487 */     return "";
/*     */   }
/*     */ 
/*     */   public String getTableName(int column)
/*     */     throws SQLException
/*     */   {
/* 502 */     if (this.useOldAliasBehavior) {
/* 503 */       return getField(column).getTableName();
/*     */     }
/*     */ 
/* 506 */     return getField(column).getTableNameNoAliases();
/*     */   }
/*     */ 
/*     */   public boolean isAutoIncrement(int column)
/*     */     throws SQLException
/*     */   {
/* 521 */     Field f = getField(column);
/*     */ 
/* 523 */     return f.isAutoIncrement();
/*     */   }
/*     */ 
/*     */   public boolean isCaseSensitive(int column)
/*     */     throws SQLException
/*     */   {
/* 538 */     Field field = getField(column);
/*     */ 
/* 540 */     int sqlType = field.getSQLType();
/*     */ 
/* 542 */     switch (sqlType) {
/*     */     case -7:
/*     */     case -6:
/*     */     case -5:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*     */     case 91:
/*     */     case 92:
/*     */     case 93:
/* 554 */       return false;
/*     */     case -1:
/*     */     case 1:
/*     */     case 12:
/* 560 */       if (field.isBinary()) {
/* 561 */         return true;
/*     */       }
/*     */ 
/* 564 */       String collationName = field.getCollation();
/*     */ 
/* 566 */       return (collationName != null) && (!collationName.endsWith("_ci"));
/*     */     }
/*     */ 
/* 569 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isCurrency(int column)
/*     */     throws SQLException
/*     */   {
/* 585 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isDefinitelyWritable(int column)
/*     */     throws SQLException
/*     */   {
/* 600 */     return isWritable(column);
/*     */   }
/*     */ 
/*     */   public int isNullable(int column)
/*     */     throws SQLException
/*     */   {
/* 615 */     if (!getField(column).isNotNull()) {
/* 616 */       return 1;
/*     */     }
/*     */ 
/* 619 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly(int column)
/*     */     throws SQLException
/*     */   {
/* 634 */     return getField(column).isReadOnly();
/*     */   }
/*     */ 
/*     */   public boolean isSearchable(int column)
/*     */     throws SQLException
/*     */   {
/* 653 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isSigned(int column)
/*     */     throws SQLException
/*     */   {
/* 668 */     Field f = getField(column);
/* 669 */     int sqlType = f.getSQLType();
/*     */ 
/* 671 */     switch (sqlType) {
/*     */     case -6:
/*     */     case -5:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 681 */       return !f.isUnsigned();
/*     */     case 91:
/*     */     case 92:
/*     */     case 93:
/* 686 */       return false;
/*     */     }
/*     */ 
/* 689 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isWritable(int column)
/*     */     throws SQLException
/*     */   {
/* 705 */     return !isReadOnly(column);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 714 */     StringBuffer toStringBuf = new StringBuffer();
/* 715 */     toStringBuf.append(super.toString());
/* 716 */     toStringBuf.append(" - Field level information: ");
/*     */ 
/* 718 */     for (int i = 0; i < this.fields.length; i++) {
/* 719 */       toStringBuf.append("\n\t");
/* 720 */       toStringBuf.append(this.fields[i].toString());
/*     */     }
/*     */ 
/* 723 */     return toStringBuf.toString();
/*     */   }
/*     */ 
/*     */   static String getClassNameForJavaType(int javaType, boolean isUnsigned, int mysqlTypeIfKnown, boolean isBinaryOrBlob, boolean isOpaqueBinary)
/*     */   {
/* 730 */     switch (javaType) {
/*     */     case -7:
/*     */     case 16:
/* 733 */       return "java.lang.Boolean";
/*     */     case -6:
/* 737 */       if (isUnsigned) {
/* 738 */         return "java.lang.Integer";
/*     */       }
/*     */ 
/* 741 */       return "java.lang.Integer";
/*     */     case 5:
/* 745 */       if (isUnsigned) {
/* 746 */         return "java.lang.Integer";
/*     */       }
/*     */ 
/* 749 */       return "java.lang.Integer";
/*     */     case 4:
/* 753 */       if ((!isUnsigned) || (mysqlTypeIfKnown == 9))
/*     */       {
/* 755 */         return "java.lang.Integer";
/*     */       }
/*     */ 
/* 758 */       return "java.lang.Long";
/*     */     case -5:
/* 762 */       if (!isUnsigned) {
/* 763 */         return "java.lang.Long";
/*     */       }
/*     */ 
/* 766 */       return "java.math.BigInteger";
/*     */     case 2:
/*     */     case 3:
/* 770 */       return "java.math.BigDecimal";
/*     */     case 7:
/* 773 */       return "java.lang.Float";
/*     */     case 6:
/*     */     case 8:
/* 777 */       return "java.lang.Double";
/*     */     case -1:
/*     */     case 1:
/*     */     case 12:
/* 782 */       if (!isOpaqueBinary) {
/* 783 */         return "java.lang.String";
/*     */       }
/*     */ 
/* 786 */       return "[B";
/*     */     case -4:
/*     */     case -3:
/*     */     case -2:
/* 792 */       if (mysqlTypeIfKnown == 255)
/* 793 */         return "[B";
/* 794 */       if (isBinaryOrBlob) {
/* 795 */         return "[B";
/*     */       }
/* 797 */       return "java.lang.String";
/*     */     case 91:
/* 801 */       return "java.sql.Date";
/*     */     case 92:
/* 804 */       return "java.sql.Time";
/*     */     case 93:
/* 807 */       return "java.sql.Timestamp";
/*     */     }
/*     */ 
/* 810 */     return "java.lang.Object";
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class iface)
/*     */     throws SQLException
/*     */   {
/* 832 */     return iface.isInstance(this);
/*     */   }
/*     */ 
/*     */   public Object unwrap(Class iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 853 */       return Util.cast(iface, this); } catch (ClassCastException cce) {
/*     */     }
/* 855 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ResultSetMetaData
 * JD-Core Version:    0.6.0
 */