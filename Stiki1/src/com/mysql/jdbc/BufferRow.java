/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.Date;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class BufferRow extends ResultSetRow
/*     */ {
/*     */   private Buffer rowFromServer;
/*  60 */   private int homePosition = 0;
/*     */ 
/*  66 */   private int preNullBitmaskHomePosition = 0;
/*     */ 
/*  74 */   private int lastRequestedIndex = -1;
/*     */   private int lastRequestedPos;
/*     */   private Field[] metadata;
/*     */   private boolean isBinaryEncoded;
/*     */   private boolean[] isNull;
/*     */   private List openStreams;
/*     */ 
/*     */   public BufferRow(Buffer buf, Field[] fields, boolean isBinaryEncoded, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/* 104 */     super(exceptionInterceptor);
/*     */ 
/* 106 */     this.rowFromServer = buf;
/* 107 */     this.metadata = fields;
/* 108 */     this.isBinaryEncoded = isBinaryEncoded;
/* 109 */     this.homePosition = this.rowFromServer.getPosition();
/* 110 */     this.preNullBitmaskHomePosition = this.homePosition;
/*     */ 
/* 112 */     if (fields != null)
/* 113 */       setMetadata(fields);
/*     */   }
/*     */ 
/*     */   public synchronized void closeOpenStreams()
/*     */   {
/* 118 */     if (this.openStreams != null)
/*     */     {
/* 124 */       Iterator iter = this.openStreams.iterator();
/*     */ 
/* 126 */       while (iter.hasNext()) {
/*     */         try
/*     */         {
/* 129 */           ((InputStream)iter.next()).close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */       }
/* 135 */       this.openStreams.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private int findAndSeekToOffset(int index) throws SQLException {
/* 140 */     if (!this.isBinaryEncoded)
/*     */     {
/* 142 */       if (index == 0) {
/* 143 */         this.lastRequestedIndex = 0;
/* 144 */         this.lastRequestedPos = this.homePosition;
/* 145 */         this.rowFromServer.setPosition(this.homePosition);
/*     */ 
/* 147 */         return 0;
/*     */       }
/*     */ 
/* 150 */       if (index == this.lastRequestedIndex) {
/* 151 */         this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */ 
/* 153 */         return this.lastRequestedPos;
/*     */       }
/*     */ 
/* 156 */       int startingIndex = 0;
/*     */ 
/* 158 */       if (index > this.lastRequestedIndex) {
/* 159 */         if (this.lastRequestedIndex >= 0)
/* 160 */           startingIndex = this.lastRequestedIndex;
/*     */         else {
/* 162 */           startingIndex = 0;
/*     */         }
/*     */ 
/* 165 */         this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */       } else {
/* 167 */         this.rowFromServer.setPosition(this.homePosition);
/*     */       }
/*     */ 
/* 170 */       for (int i = startingIndex; i < index; i++) {
/* 171 */         this.rowFromServer.fastSkipLenByteArray();
/*     */       }
/*     */ 
/* 174 */       this.lastRequestedIndex = index;
/* 175 */       this.lastRequestedPos = this.rowFromServer.getPosition();
/*     */ 
/* 177 */       return this.lastRequestedPos;
/*     */     }
/*     */ 
/* 180 */     return findAndSeekToOffsetForBinaryEncoding(index);
/*     */   }
/*     */ 
/*     */   private int findAndSeekToOffsetForBinaryEncoding(int index) throws SQLException
/*     */   {
/* 185 */     if (index == 0) {
/* 186 */       this.lastRequestedIndex = 0;
/* 187 */       this.lastRequestedPos = this.homePosition;
/* 188 */       this.rowFromServer.setPosition(this.homePosition);
/*     */ 
/* 190 */       return 0;
/*     */     }
/*     */ 
/* 193 */     if (index == this.lastRequestedIndex) {
/* 194 */       this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */ 
/* 196 */       return this.lastRequestedPos;
/*     */     }
/*     */ 
/* 199 */     int startingIndex = 0;
/*     */ 
/* 201 */     if (index > this.lastRequestedIndex) {
/* 202 */       if (this.lastRequestedIndex >= 0) {
/* 203 */         startingIndex = this.lastRequestedIndex;
/*     */       }
/*     */       else {
/* 206 */         startingIndex = 0;
/* 207 */         this.lastRequestedPos = this.homePosition;
/*     */       }
/*     */ 
/* 210 */       this.rowFromServer.setPosition(this.lastRequestedPos);
/*     */     } else {
/* 212 */       this.rowFromServer.setPosition(this.homePosition);
/*     */     }
/*     */ 
/* 215 */     for (int i = startingIndex; i < index; i++) {
/* 216 */       if (this.isNull[i] != 0)
/*     */       {
/*     */         continue;
/*     */       }
/* 220 */       int curPosition = this.rowFromServer.getPosition();
/*     */ 
/* 222 */       switch (this.metadata[i].getMysqlType()) {
/*     */       case 6:
/* 224 */         break;
/*     */       case 1:
/* 228 */         this.rowFromServer.setPosition(curPosition + 1);
/* 229 */         break;
/*     */       case 2:
/*     */       case 13:
/* 233 */         this.rowFromServer.setPosition(curPosition + 2);
/*     */ 
/* 235 */         break;
/*     */       case 3:
/*     */       case 9:
/* 238 */         this.rowFromServer.setPosition(curPosition + 4);
/*     */ 
/* 240 */         break;
/*     */       case 8:
/* 242 */         this.rowFromServer.setPosition(curPosition + 8);
/*     */ 
/* 244 */         break;
/*     */       case 4:
/* 246 */         this.rowFromServer.setPosition(curPosition + 4);
/*     */ 
/* 248 */         break;
/*     */       case 5:
/* 250 */         this.rowFromServer.setPosition(curPosition + 8);
/*     */ 
/* 252 */         break;
/*     */       case 11:
/* 254 */         this.rowFromServer.fastSkipLenByteArray();
/*     */ 
/* 256 */         break;
/*     */       case 10:
/* 259 */         this.rowFromServer.fastSkipLenByteArray();
/*     */ 
/* 261 */         break;
/*     */       case 7:
/*     */       case 12:
/* 264 */         this.rowFromServer.fastSkipLenByteArray();
/*     */ 
/* 266 */         break;
/*     */       case 0:
/*     */       case 15:
/*     */       case 16:
/*     */       case 246:
/*     */       case 249:
/*     */       case 250:
/*     */       case 251:
/*     */       case 252:
/*     */       case 253:
/*     */       case 254:
/*     */       case 255:
/* 278 */         this.rowFromServer.fastSkipLenByteArray();
/*     */ 
/* 280 */         break;
/*     */       default:
/* 283 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + this.metadata[i].getMysqlType() + Messages.getString("MysqlIO.98") + (i + 1) + Messages.getString("MysqlIO.99") + this.metadata.length + Messages.getString("MysqlIO.100"), "S1000", this.exceptionInterceptor);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 295 */     this.lastRequestedIndex = index;
/* 296 */     this.lastRequestedPos = this.rowFromServer.getPosition();
/*     */ 
/* 298 */     return this.lastRequestedPos;
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getBinaryInputStream(int columnIndex) throws SQLException
/*     */   {
/* 303 */     if ((this.isBinaryEncoded) && 
/* 304 */       (isNull(columnIndex))) {
/* 305 */       return null;
/*     */     }
/*     */ 
/* 309 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 311 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 313 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 315 */     if (length == -1L) {
/* 316 */       return null;
/*     */     }
/*     */ 
/* 319 */     InputStream stream = new ByteArrayInputStream(this.rowFromServer.getByteBuffer(), offset, (int)length);
/*     */ 
/* 322 */     if (this.openStreams == null) {
/* 323 */       this.openStreams = new LinkedList();
/*     */     }
/*     */ 
/* 326 */     return stream;
/*     */   }
/*     */ 
/*     */   public byte[] getColumnValue(int index) throws SQLException {
/* 330 */     findAndSeekToOffset(index);
/*     */ 
/* 332 */     if (!this.isBinaryEncoded) {
/* 333 */       return this.rowFromServer.readLenByteArray(0);
/*     */     }
/*     */ 
/* 336 */     if (this.isNull[index] != 0) {
/* 337 */       return null;
/*     */     }
/*     */ 
/* 340 */     switch (this.metadata[index].getMysqlType()) {
/*     */     case 6:
/* 342 */       return null;
/*     */     case 1:
/* 345 */       return new byte[] { this.rowFromServer.readByte() };
/*     */     case 2:
/*     */     case 13:
/* 349 */       return this.rowFromServer.getBytes(2);
/*     */     case 3:
/*     */     case 9:
/* 353 */       return this.rowFromServer.getBytes(4);
/*     */     case 8:
/* 356 */       return this.rowFromServer.getBytes(8);
/*     */     case 4:
/* 359 */       return this.rowFromServer.getBytes(4);
/*     */     case 5:
/* 362 */       return this.rowFromServer.getBytes(8);
/*     */     case 0:
/*     */     case 7:
/*     */     case 10:
/*     */     case 11:
/*     */     case 12:
/*     */     case 15:
/*     */     case 16:
/*     */     case 246:
/*     */     case 249:
/*     */     case 250:
/*     */     case 251:
/*     */     case 252:
/*     */     case 253:
/*     */     case 254:
/*     */     case 255:
/* 379 */       return this.rowFromServer.readLenByteArray(0);
/*     */     }
/*     */ 
/* 382 */     throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + this.metadata[index].getMysqlType() + Messages.getString("MysqlIO.98") + (index + 1) + Messages.getString("MysqlIO.99") + this.metadata.length + Messages.getString("MysqlIO.100"), "S1000", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public int getInt(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 394 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 396 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 398 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 400 */     if (length == -1L) {
/* 401 */       return 0;
/*     */     }
/*     */ 
/* 404 */     return StringUtils.getInt(this.rowFromServer.getByteBuffer(), offset, offset + (int)length);
/*     */   }
/*     */ 
/*     */   public long getLong(int columnIndex) throws SQLException
/*     */   {
/* 409 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 411 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 413 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 415 */     if (length == -1L) {
/* 416 */       return 0L;
/*     */     }
/*     */ 
/* 419 */     return StringUtils.getLong(this.rowFromServer.getByteBuffer(), offset, offset + (int)length);
/*     */   }
/*     */ 
/*     */   public double getNativeDouble(int columnIndex) throws SQLException
/*     */   {
/* 424 */     if (isNull(columnIndex)) {
/* 425 */       return 0.0D;
/*     */     }
/*     */ 
/* 428 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 430 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 432 */     return getNativeDouble(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */ 
/*     */   public float getNativeFloat(int columnIndex) throws SQLException {
/* 436 */     if (isNull(columnIndex)) {
/* 437 */       return 0.0F;
/*     */     }
/*     */ 
/* 440 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 442 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 444 */     return getNativeFloat(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */ 
/*     */   public int getNativeInt(int columnIndex) throws SQLException {
/* 448 */     if (isNull(columnIndex)) {
/* 449 */       return 0;
/*     */     }
/*     */ 
/* 452 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 454 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 456 */     return getNativeInt(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */ 
/*     */   public long getNativeLong(int columnIndex) throws SQLException {
/* 460 */     if (isNull(columnIndex)) {
/* 461 */       return 0L;
/*     */     }
/*     */ 
/* 464 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 466 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 468 */     return getNativeLong(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */ 
/*     */   public short getNativeShort(int columnIndex) throws SQLException {
/* 472 */     if (isNull(columnIndex)) {
/* 473 */       return 0;
/*     */     }
/*     */ 
/* 476 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 478 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 480 */     return getNativeShort(this.rowFromServer.getByteBuffer(), offset);
/*     */   }
/*     */ 
/*     */   public Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 486 */     if (isNull(columnIndex)) {
/* 487 */       return null;
/*     */     }
/*     */ 
/* 490 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 492 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 494 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 496 */     return getNativeTimestamp(this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public Reader getReader(int columnIndex) throws SQLException
/*     */   {
/* 501 */     InputStream stream = getBinaryInputStream(columnIndex);
/*     */ 
/* 503 */     if (stream == null)
/* 504 */       return null;
/*     */     SQLException sqlEx;
/*     */     try {
/* 508 */       return new InputStreamReader(stream, this.metadata[columnIndex].getCharacterSet());
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 511 */       sqlEx = SQLError.createSQLException("", this.exceptionInterceptor);
/*     */ 
/* 513 */       sqlEx.initCause(e);
/*     */     }
/* 515 */     throw sqlEx;
/*     */   }
/*     */ 
/*     */   public String getString(int columnIndex, String encoding, ConnectionImpl conn)
/*     */     throws SQLException
/*     */   {
/* 521 */     if ((this.isBinaryEncoded) && 
/* 522 */       (isNull(columnIndex))) {
/* 523 */       return null;
/*     */     }
/*     */ 
/* 527 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 529 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 531 */     if (length == -1L) {
/* 532 */       return null;
/*     */     }
/*     */ 
/* 535 */     if (length == 0L) {
/* 536 */       return "";
/*     */     }
/*     */ 
/* 542 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 544 */     return getString(encoding, conn, this.rowFromServer.getByteBuffer(), offset, (int)length);
/*     */   }
/*     */ 
/*     */   public Time getTimeFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 551 */     if (isNull(columnIndex)) {
/* 552 */       return null;
/*     */     }
/*     */ 
/* 555 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 557 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 559 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 561 */     return getTimeFast(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public Timestamp getTimestampFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 568 */     if (isNull(columnIndex)) {
/* 569 */       return null;
/*     */     }
/*     */ 
/* 572 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 574 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 576 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 578 */     return getTimestampFast(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public boolean isFloatingPointNumber(int index)
/*     */     throws SQLException
/*     */   {
/* 584 */     if (this.isBinaryEncoded) {
/* 585 */       switch (this.metadata[index].getSQLType()) {
/*     */       case 2:
/*     */       case 3:
/*     */       case 6:
/*     */       case 8:
/* 590 */         return true;
/*     */       case 4:
/*     */       case 5:
/* 592 */       case 7: } return false;
/*     */     }
/*     */ 
/* 596 */     findAndSeekToOffset(index);
/*     */ 
/* 598 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 600 */     if (length == -1L) {
/* 601 */       return false;
/*     */     }
/*     */ 
/* 604 */     if (length == 0L) {
/* 605 */       return false;
/*     */     }
/*     */ 
/* 608 */     int offset = this.rowFromServer.getPosition();
/* 609 */     byte[] buffer = this.rowFromServer.getByteBuffer();
/*     */ 
/* 611 */     for (int i = 0; i < (int)length; i++) {
/* 612 */       char c = (char)buffer[(offset + i)];
/*     */ 
/* 614 */       if ((c == 'e') || (c == 'E')) {
/* 615 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 619 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isNull(int index) throws SQLException {
/* 623 */     if (!this.isBinaryEncoded) {
/* 624 */       findAndSeekToOffset(index);
/*     */ 
/* 626 */       return this.rowFromServer.readFieldLength() == -1L;
/*     */     }
/*     */ 
/* 629 */     return this.isNull[index];
/*     */   }
/*     */ 
/*     */   public long length(int index) throws SQLException {
/* 633 */     findAndSeekToOffset(index);
/*     */ 
/* 635 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 637 */     if (length == -1L) {
/* 638 */       return 0L;
/*     */     }
/*     */ 
/* 641 */     return length;
/*     */   }
/*     */ 
/*     */   public void setColumnValue(int index, byte[] value) throws SQLException {
/* 645 */     throw new OperationNotSupportedException();
/*     */   }
/*     */ 
/*     */   public ResultSetRow setMetadata(Field[] f) throws SQLException {
/* 649 */     super.setMetadata(f);
/*     */ 
/* 651 */     if (this.isBinaryEncoded) {
/* 652 */       setupIsNullBitmask();
/*     */     }
/*     */ 
/* 655 */     return this;
/*     */   }
/*     */ 
/*     */   private void setupIsNullBitmask()
/*     */     throws SQLException
/*     */   {
/* 664 */     if (this.isNull != null) {
/* 665 */       return;
/*     */     }
/*     */ 
/* 668 */     this.rowFromServer.setPosition(this.preNullBitmaskHomePosition);
/*     */ 
/* 670 */     int nullCount = (this.metadata.length + 9) / 8;
/*     */ 
/* 672 */     byte[] nullBitMask = new byte[nullCount];
/*     */ 
/* 674 */     for (int i = 0; i < nullCount; i++) {
/* 675 */       nullBitMask[i] = this.rowFromServer.readByte();
/*     */     }
/*     */ 
/* 678 */     this.homePosition = this.rowFromServer.getPosition();
/*     */ 
/* 680 */     this.isNull = new boolean[this.metadata.length];
/*     */ 
/* 682 */     int nullMaskPos = 0;
/* 683 */     int bit = 4;
/*     */ 
/* 685 */     for (int i = 0; i < this.metadata.length; i++)
/*     */     {
/* 687 */       this.isNull[i] = ((nullBitMask[nullMaskPos] & bit) != 0 ? 1 : false);
/*     */ 
/* 689 */       if ((bit <<= 1 & 0xFF) == 0) {
/* 690 */         bit = 1;
/*     */ 
/* 692 */         nullMaskPos++;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Date getDateFast(int columnIndex, ConnectionImpl conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException
/*     */   {
/* 699 */     if (isNull(columnIndex)) {
/* 700 */       return null;
/*     */     }
/*     */ 
/* 703 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 705 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 707 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 709 */     return getDateFast(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, conn, rs, targetCalendar);
/*     */   }
/*     */ 
/*     */   public Date getNativeDate(int columnIndex, ConnectionImpl conn, ResultSetImpl rs, Calendar cal)
/*     */     throws SQLException
/*     */   {
/* 715 */     if (isNull(columnIndex)) {
/* 716 */       return null;
/*     */     }
/*     */ 
/* 719 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 721 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 723 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 725 */     return getNativeDate(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, conn, rs, cal);
/*     */   }
/*     */ 
/*     */   public Object getNativeDateTimeValue(int columnIndex, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 733 */     if (isNull(columnIndex)) {
/* 734 */       return null;
/*     */     }
/*     */ 
/* 737 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 739 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 741 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 743 */     return getNativeDateTimeValue(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, jdbcType, mysqlType, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, ConnectionImpl conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 751 */     if (isNull(columnIndex)) {
/* 752 */       return null;
/*     */     }
/*     */ 
/* 755 */     findAndSeekToOffset(columnIndex);
/*     */ 
/* 757 */     long length = this.rowFromServer.readFieldLength();
/*     */ 
/* 759 */     int offset = this.rowFromServer.getPosition();
/*     */ 
/* 761 */     return getNativeTime(columnIndex, this.rowFromServer.getByteBuffer(), offset, (int)length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */ 
/*     */   public int getBytesSize()
/*     */   {
/* 766 */     return this.rowFromServer.getBufLength();
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.BufferRow
 * JD-Core Version:    0.6.0
 */