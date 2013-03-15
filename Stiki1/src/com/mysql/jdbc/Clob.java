/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class Clob
/*     */   implements java.sql.Clob, OutputStreamWatcher, WriterWatcher
/*     */ {
/*     */   private String charData;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   Clob(ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  49 */     this.charData = "";
/*  50 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   Clob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
/*  54 */     this.charData = charDataInit;
/*  55 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   public InputStream getAsciiStream()
/*     */     throws SQLException
/*     */   {
/*  62 */     if (this.charData != null) {
/*  63 */       return new ByteArrayInputStream(this.charData.getBytes());
/*     */     }
/*     */ 
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream()
/*     */     throws SQLException
/*     */   {
/*  73 */     if (this.charData != null) {
/*  74 */       return new StringReader(this.charData);
/*     */     }
/*     */ 
/*  77 */     return null;
/*     */   }
/*     */ 
/*     */   public String getSubString(long startPos, int length)
/*     */     throws SQLException
/*     */   {
/*  84 */     if (startPos < 1L) {
/*  85 */       throw SQLError.createSQLException(Messages.getString("Clob.6"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/*  89 */     int adjustedStartPos = (int)startPos - 1;
/*  90 */     int adjustedEndIndex = adjustedStartPos + length;
/*     */ 
/*  92 */     if (this.charData != null) {
/*  93 */       if (adjustedEndIndex > this.charData.length()) {
/*  94 */         throw SQLError.createSQLException(Messages.getString("Clob.7"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */ 
/*  98 */       return this.charData.substring(adjustedStartPos, adjustedEndIndex);
/*     */     }
/*     */ 
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/* 109 */     if (this.charData != null) {
/* 110 */       return this.charData.length();
/*     */     }
/*     */ 
/* 113 */     return 0L;
/*     */   }
/*     */ 
/*     */   public long position(java.sql.Clob arg0, long arg1)
/*     */     throws SQLException
/*     */   {
/* 120 */     return position(arg0.getSubString(0L, (int)arg0.length()), arg1);
/*     */   }
/*     */ 
/*     */   public long position(String stringToFind, long startPos)
/*     */     throws SQLException
/*     */   {
/* 128 */     if (startPos < 1L) {
/* 129 */       throw SQLError.createSQLException(Messages.getString("Clob.8") + startPos + Messages.getString("Clob.9"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 134 */     if (this.charData != null) {
/* 135 */       if (startPos - 1L > this.charData.length()) {
/* 136 */         throw SQLError.createSQLException(Messages.getString("Clob.10"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */ 
/* 140 */       int pos = this.charData.indexOf(stringToFind, (int)(startPos - 1L));
/*     */ 
/* 142 */       return pos + 1;
/*     */     }
/*     */ 
/* 145 */     return -1L;
/*     */   }
/*     */ 
/*     */   public OutputStream setAsciiStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 152 */     if (indexToWriteAt < 1L) {
/* 153 */       throw SQLError.createSQLException(Messages.getString("Clob.0"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 157 */     WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 158 */     bytesOut.setWatcher(this);
/*     */ 
/* 160 */     if (indexToWriteAt > 0L) {
/* 161 */       bytesOut.write(this.charData.getBytes(), 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */ 
/* 165 */     return bytesOut;
/*     */   }
/*     */ 
/*     */   public Writer setCharacterStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 172 */     if (indexToWriteAt < 1L) {
/* 173 */       throw SQLError.createSQLException(Messages.getString("Clob.1"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 177 */     WatchableWriter writer = new WatchableWriter();
/* 178 */     writer.setWatcher(this);
/*     */ 
/* 183 */     if (indexToWriteAt > 1L) {
/* 184 */       writer.write(this.charData, 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */ 
/* 187 */     return writer;
/*     */   }
/*     */ 
/*     */   public int setString(long pos, String str)
/*     */     throws SQLException
/*     */   {
/* 194 */     if (pos < 1L) {
/* 195 */       throw SQLError.createSQLException(Messages.getString("Clob.2"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 199 */     if (str == null) {
/* 200 */       throw SQLError.createSQLException(Messages.getString("Clob.3"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 204 */     StringBuffer charBuf = new StringBuffer(this.charData);
/*     */ 
/* 206 */     pos -= 1L;
/*     */ 
/* 208 */     int strLength = str.length();
/*     */ 
/* 210 */     charBuf.replace((int)pos, (int)(pos + strLength), str);
/*     */ 
/* 212 */     this.charData = charBuf.toString();
/*     */ 
/* 214 */     return strLength;
/*     */   }
/*     */ 
/*     */   public int setString(long pos, String str, int offset, int len)
/*     */     throws SQLException
/*     */   {
/* 222 */     if (pos < 1L) {
/* 223 */       throw SQLError.createSQLException(Messages.getString("Clob.4"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 227 */     if (str == null) {
/* 228 */       throw SQLError.createSQLException(Messages.getString("Clob.5"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 232 */     StringBuffer charBuf = new StringBuffer(this.charData);
/*     */ 
/* 234 */     pos -= 1L;
/*     */ 
/* 236 */     String replaceString = str.substring(offset, len);
/*     */ 
/* 238 */     charBuf.replace((int)pos, (int)(pos + replaceString.length()), replaceString);
/*     */ 
/* 241 */     this.charData = charBuf.toString();
/*     */ 
/* 243 */     return len;
/*     */   }
/*     */ 
/*     */   public void streamClosed(WatchableOutputStream out)
/*     */   {
/* 250 */     int streamSize = out.size();
/*     */ 
/* 252 */     if (streamSize < this.charData.length()) {
/*     */       try {
/* 254 */         out.write(StringUtils.getBytes(this.charData, null, null, false, null, this.exceptionInterceptor), streamSize, this.charData.length() - streamSize);
/*     */       }
/*     */       catch (SQLException ex)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 262 */     this.charData = StringUtils.toAsciiString(out.toByteArray());
/*     */   }
/*     */ 
/*     */   public void truncate(long length)
/*     */     throws SQLException
/*     */   {
/* 269 */     if (length > this.charData.length()) {
/* 270 */       throw SQLError.createSQLException(Messages.getString("Clob.11") + this.charData.length() + Messages.getString("Clob.12") + length + Messages.getString("Clob.13"), this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 276 */     this.charData = this.charData.substring(0, (int)length);
/*     */   }
/*     */ 
/*     */   public void writerClosed(char[] charDataBeingWritten)
/*     */   {
/* 283 */     this.charData = new String(charDataBeingWritten);
/*     */   }
/*     */ 
/*     */   public void writerClosed(WatchableWriter out)
/*     */   {
/* 290 */     int dataLength = out.size();
/*     */ 
/* 292 */     if (dataLength < this.charData.length()) {
/* 293 */       out.write(this.charData, dataLength, this.charData.length() - dataLength);
/*     */     }
/*     */ 
/* 297 */     this.charData = out.toString();
/*     */   }
/*     */ 
/*     */   public void free() throws SQLException {
/* 301 */     this.charData = null;
/*     */   }
/*     */ 
/*     */   public Reader getCharacterStream(long pos, long length) throws SQLException {
/* 305 */     return new StringReader(getSubString(pos, (int)length));
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Clob
 * JD-Core Version:    0.6.0
 */