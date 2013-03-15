/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.Date;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class ByteArrayRow extends ResultSetRow
/*     */ {
/*     */   byte[][] internalRowData;
/*     */ 
/*     */   public ByteArrayRow(byte[][] internalRowData, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  51 */     super(exceptionInterceptor);
/*     */ 
/*  53 */     this.internalRowData = internalRowData;
/*     */   }
/*     */ 
/*     */   public byte[] getColumnValue(int index) throws SQLException {
/*  57 */     return this.internalRowData[index];
/*     */   }
/*     */ 
/*     */   public void setColumnValue(int index, byte[] value) throws SQLException {
/*  61 */     this.internalRowData[index] = value;
/*     */   }
/*     */ 
/*     */   public String getString(int index, String encoding, ConnectionImpl conn) throws SQLException
/*     */   {
/*  66 */     byte[] columnData = this.internalRowData[index];
/*     */ 
/*  68 */     if (columnData == null) {
/*  69 */       return null;
/*     */     }
/*     */ 
/*  72 */     return getString(encoding, conn, columnData, 0, columnData.length);
/*     */   }
/*     */ 
/*     */   public boolean isNull(int index) throws SQLException {
/*  76 */     return this.internalRowData[index] == null;
/*     */   }
/*     */ 
/*     */   public boolean isFloatingPointNumber(int index) throws SQLException {
/*  80 */     byte[] numAsBytes = this.internalRowData[index];
/*     */ 
/*  82 */     if ((this.internalRowData[index] == null) || (this.internalRowData[index].length == 0))
/*     */     {
/*  84 */       return false;
/*     */     }
/*     */ 
/*  87 */     for (int i = 0; i < numAsBytes.length; i++) {
/*  88 */       if (((char)numAsBytes[i] == 'e') || ((char)numAsBytes[i] == 'E')) {
/*  89 */         return true;
/*     */       }
/*     */     }
/*     */ 
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */   public long length(int index) throws SQLException {
/*  97 */     if (this.internalRowData[index] == null) {
/*  98 */       return 0L;
/*     */     }
/*     */ 
/* 101 */     return this.internalRowData[index].length;
/*     */   }
/*     */ 
/*     */   public int getInt(int columnIndex) {
/* 105 */     if (this.internalRowData[columnIndex] == null) {
/* 106 */       return 0;
/*     */     }
/*     */ 
/* 109 */     return StringUtils.getInt(this.internalRowData[columnIndex]);
/*     */   }
/*     */ 
/*     */   public long getLong(int columnIndex) {
/* 113 */     if (this.internalRowData[columnIndex] == null) {
/* 114 */       return 0L;
/*     */     }
/*     */ 
/* 117 */     return StringUtils.getLong(this.internalRowData[columnIndex]);
/*     */   }
/*     */ 
/*     */   public Timestamp getTimestampFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 123 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */ 
/* 125 */     if (columnValue == null) {
/* 126 */       return null;
/*     */     }
/*     */ 
/* 129 */     return getTimestampFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public double getNativeDouble(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 135 */     if (this.internalRowData[columnIndex] == null) {
/* 136 */       return 0.0D;
/*     */     }
/*     */ 
/* 139 */     return getNativeDouble(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */ 
/*     */   public float getNativeFloat(int columnIndex) throws SQLException {
/* 143 */     if (this.internalRowData[columnIndex] == null) {
/* 144 */       return 0.0F;
/*     */     }
/*     */ 
/* 147 */     return getNativeFloat(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */ 
/*     */   public int getNativeInt(int columnIndex) throws SQLException {
/* 151 */     if (this.internalRowData[columnIndex] == null) {
/* 152 */       return 0;
/*     */     }
/*     */ 
/* 155 */     return getNativeInt(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */ 
/*     */   public long getNativeLong(int columnIndex) throws SQLException {
/* 159 */     if (this.internalRowData[columnIndex] == null) {
/* 160 */       return 0L;
/*     */     }
/*     */ 
/* 163 */     return getNativeLong(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */ 
/*     */   public short getNativeShort(int columnIndex) throws SQLException {
/* 167 */     if (this.internalRowData[columnIndex] == null) {
/* 168 */       return 0;
/*     */     }
/*     */ 
/* 171 */     return getNativeShort(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */ 
/*     */   public Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 177 */     byte[] bits = this.internalRowData[columnIndex];
/*     */ 
/* 179 */     if (bits == null) {
/* 180 */       return null;
/*     */     }
/*     */ 
/* 183 */     return getNativeTimestamp(bits, 0, bits.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public void closeOpenStreams()
/*     */   {
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryInputStream(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 193 */     if (this.internalRowData[columnIndex] == null) {
/* 194 */       return null;
/*     */     }
/*     */ 
/* 197 */     return new ByteArrayInputStream(this.internalRowData[columnIndex]);
/*     */   }
/*     */ 
/*     */   public Reader getReader(int columnIndex) throws SQLException {
/* 201 */     InputStream stream = getBinaryInputStream(columnIndex);
/*     */ 
/* 203 */     if (stream == null)
/* 204 */       return null;
/*     */     SQLException sqlEx;
/*     */     try {
/* 208 */       return new InputStreamReader(stream, this.metadata[columnIndex].getCharacterSet());
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 211 */       sqlEx = SQLError.createSQLException("", this.exceptionInterceptor);
/*     */ 
/* 213 */       sqlEx.initCause(e);
/*     */     }
/* 215 */     throw sqlEx;
/*     */   }
/*     */ 
/*     */   public Time getTimeFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 222 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */ 
/* 224 */     if (columnValue == null) {
/* 225 */       return null;
/*     */     }
/*     */ 
/* 228 */     return getTimeFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public Date getDateFast(int columnIndex, ConnectionImpl conn, ResultSetImpl rs, Calendar targetCalendar)
/*     */     throws SQLException
/*     */   {
/* 234 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */ 
/* 236 */     if (columnValue == null) {
/* 237 */       return null;
/*     */     }
/*     */ 
/* 240 */     return getDateFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, conn, rs, targetCalendar);
/*     */   }
/*     */ 
/*     */   public Object getNativeDateTimeValue(int columnIndex, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 248 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */ 
/* 250 */     if (columnValue == null) {
/* 251 */       return null;
/*     */     }
/*     */ 
/* 254 */     return getNativeDateTimeValue(columnIndex, columnValue, 0, columnValue.length, targetCalendar, jdbcType, mysqlType, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public Date getNativeDate(int columnIndex, ConnectionImpl conn, ResultSetImpl rs, Calendar cal)
/*     */     throws SQLException
/*     */   {
/* 261 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */ 
/* 263 */     if (columnValue == null) {
/* 264 */       return null;
/*     */     }
/*     */ 
/* 267 */     return getNativeDate(columnIndex, columnValue, 0, columnValue.length, conn, rs, cal);
/*     */   }
/*     */ 
/*     */   public Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 274 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */ 
/* 276 */     if (columnValue == null) {
/* 277 */       return null;
/*     */     }
/*     */ 
/* 280 */     return getNativeTime(columnIndex, columnValue, 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public int getBytesSize()
/*     */   {
/* 285 */     if (this.internalRowData == null) {
/* 286 */       return 0;
/*     */     }
/*     */ 
/* 289 */     int bytesSize = 0;
/*     */ 
/* 291 */     for (int i = 0; i < this.internalRowData.length; i++) {
/* 292 */       if (this.internalRowData[i] != null) {
/* 293 */         bytesSize += this.internalRowData[i].length;
/*     */       }
/*     */     }
/*     */ 
/* 297 */     return bytesSize;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ByteArrayRow
 * JD-Core Version:    0.6.0
 */