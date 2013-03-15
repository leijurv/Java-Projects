/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ class Buffer
/*     */ {
/*     */   static final int MAX_BYTES_TO_DUMP = 512;
/*     */   static final int NO_LENGTH_LIMIT = -1;
/*     */   static final long NULL_LENGTH = -1L;
/*  47 */   private int bufLength = 0;
/*     */   private byte[] byteBuffer;
/*  51 */   private int position = 0;
/*     */ 
/*  53 */   protected boolean wasMultiPacket = false;
/*     */ 
/*     */   Buffer(byte[] buf) {
/*  56 */     this.byteBuffer = buf;
/*  57 */     setBufLength(buf.length);
/*     */   }
/*     */ 
/*     */   Buffer(int size) {
/*  61 */     this.byteBuffer = new byte[size];
/*  62 */     setBufLength(this.byteBuffer.length);
/*  63 */     this.position = 4;
/*     */   }
/*     */ 
/*     */   final void clear() {
/*  67 */     this.position = 4;
/*     */   }
/*     */ 
/*     */   final void dump() {
/*  71 */     dump(getBufLength());
/*     */   }
/*     */ 
/*     */   final String dump(int numBytes) {
/*  75 */     return StringUtils.dumpAsHex(getBytes(0, numBytes > getBufLength() ? getBufLength() : numBytes), numBytes > getBufLength() ? getBufLength() : numBytes);
/*     */   }
/*     */ 
/*     */   final String dumpClampedBytes(int numBytes)
/*     */   {
/*  81 */     int numBytesToDump = numBytes < 512 ? numBytes : 512;
/*     */ 
/*  84 */     String dumped = StringUtils.dumpAsHex(getBytes(0, numBytesToDump > getBufLength() ? getBufLength() : numBytesToDump), numBytesToDump > getBufLength() ? getBufLength() : numBytesToDump);
/*     */ 
/*  90 */     if (numBytesToDump < numBytes) {
/*  91 */       return dumped + " ....(packet exceeds max. dump length)";
/*     */     }
/*     */ 
/*  94 */     return dumped;
/*     */   }
/*     */ 
/*     */   final void dumpHeader() {
/*  98 */     for (int i = 0; i < 4; i++) {
/*  99 */       String hexVal = Integer.toHexString(readByte(i) & 0xFF);
/*     */ 
/* 101 */       if (hexVal.length() == 1) {
/* 102 */         hexVal = "0" + hexVal;
/*     */       }
/*     */ 
/* 105 */       System.out.print(hexVal + " ");
/*     */     }
/*     */   }
/*     */ 
/*     */   final void dumpNBytes(int start, int nBytes) {
/* 110 */     StringBuffer asciiBuf = new StringBuffer();
/*     */ 
/* 112 */     for (int i = start; (i < start + nBytes) && (i < getBufLength()); i++) {
/* 113 */       String hexVal = Integer.toHexString(readByte(i) & 0xFF);
/*     */ 
/* 115 */       if (hexVal.length() == 1) {
/* 116 */         hexVal = "0" + hexVal;
/*     */       }
/*     */ 
/* 119 */       System.out.print(hexVal + " ");
/*     */ 
/* 121 */       if ((readByte(i) > 32) && (readByte(i) < 127))
/* 122 */         asciiBuf.append((char)readByte(i));
/*     */       else {
/* 124 */         asciiBuf.append(".");
/*     */       }
/*     */ 
/* 127 */       asciiBuf.append(" ");
/*     */     }
/*     */ 
/* 130 */     System.out.println("    " + asciiBuf.toString());
/*     */   }
/*     */ 
/*     */   final void ensureCapacity(int additionalData) throws SQLException {
/* 134 */     if (this.position + additionalData > getBufLength())
/* 135 */       if (this.position + additionalData < this.byteBuffer.length)
/*     */       {
/* 141 */         setBufLength(this.byteBuffer.length);
/*     */       }
/*     */       else
/*     */       {
/* 147 */         int newLength = (int)(this.byteBuffer.length * 1.25D);
/*     */ 
/* 149 */         if (newLength < this.byteBuffer.length + additionalData) {
/* 150 */           newLength = this.byteBuffer.length + (int)(additionalData * 1.25D);
/*     */         }
/*     */ 
/* 154 */         if (newLength < this.byteBuffer.length) {
/* 155 */           newLength = this.byteBuffer.length + additionalData;
/*     */         }
/*     */ 
/* 158 */         byte[] newBytes = new byte[newLength];
/*     */ 
/* 160 */         System.arraycopy(this.byteBuffer, 0, newBytes, 0, this.byteBuffer.length);
/*     */ 
/* 162 */         this.byteBuffer = newBytes;
/* 163 */         setBufLength(this.byteBuffer.length);
/*     */       }
/*     */   }
/*     */ 
/*     */   public int fastSkipLenString()
/*     */   {
/* 174 */     long len = readFieldLength();
/*     */ 
/* 176 */     this.position = (int)(this.position + len);
/*     */ 
/* 178 */     return (int)len;
/*     */   }
/*     */ 
/*     */   public void fastSkipLenByteArray() {
/* 182 */     long len = readFieldLength();
/*     */ 
/* 184 */     if ((len == -1L) || (len == 0L)) {
/* 185 */       return;
/*     */     }
/*     */ 
/* 188 */     this.position = (int)(this.position + len);
/*     */   }
/*     */ 
/*     */   protected final byte[] getBufferSource() {
/* 192 */     return this.byteBuffer;
/*     */   }
/*     */ 
/*     */   int getBufLength() {
/* 196 */     return this.bufLength;
/*     */   }
/*     */ 
/*     */   public byte[] getByteBuffer()
/*     */   {
/* 205 */     return this.byteBuffer;
/*     */   }
/*     */ 
/*     */   final byte[] getBytes(int len) {
/* 209 */     byte[] b = new byte[len];
/* 210 */     System.arraycopy(this.byteBuffer, this.position, b, 0, len);
/* 211 */     this.position += len;
/*     */ 
/* 213 */     return b;
/*     */   }
/*     */ 
/*     */   byte[] getBytes(int offset, int len)
/*     */   {
/* 222 */     byte[] dest = new byte[len];
/* 223 */     System.arraycopy(this.byteBuffer, offset, dest, 0, len);
/*     */ 
/* 225 */     return dest;
/*     */   }
/*     */ 
/*     */   int getCapacity() {
/* 229 */     return this.byteBuffer.length;
/*     */   }
/*     */ 
/*     */   public ByteBuffer getNioBuffer() {
/* 233 */     throw new IllegalArgumentException(Messages.getString("ByteArrayBuffer.0"));
/*     */   }
/*     */ 
/*     */   public int getPosition()
/*     */   {
/* 243 */     return this.position;
/*     */   }
/*     */ 
/*     */   final boolean isLastDataPacket()
/*     */   {
/* 248 */     return (getBufLength() < 9) && ((this.byteBuffer[0] & 0xFF) == 254);
/*     */   }
/*     */ 
/*     */   final long newReadLength() {
/* 252 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */ 
/* 254 */     switch (sw) {
/*     */     case 251:
/* 256 */       return 0L;
/*     */     case 252:
/* 259 */       return readInt();
/*     */     case 253:
/* 262 */       return readLongInt();
/*     */     case 254:
/* 265 */       return readLongLong();
/*     */     }
/*     */ 
/* 268 */     return sw;
/*     */   }
/*     */ 
/*     */   final byte readByte()
/*     */   {
/* 273 */     return this.byteBuffer[(this.position++)];
/*     */   }
/*     */ 
/*     */   final byte readByte(int readAt) {
/* 277 */     return this.byteBuffer[readAt];
/*     */   }
/*     */ 
/*     */   final long readFieldLength() {
/* 281 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */ 
/* 283 */     switch (sw) {
/*     */     case 251:
/* 285 */       return -1L;
/*     */     case 252:
/* 288 */       return readInt();
/*     */     case 253:
/* 291 */       return readLongInt();
/*     */     case 254:
/* 294 */       return readLongLong();
/*     */     }
/*     */ 
/* 297 */     return sw;
/*     */   }
/*     */ 
/*     */   final int readInt()
/*     */   {
/* 303 */     byte[] b = this.byteBuffer;
/*     */ 
/* 305 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8;
/*     */   }
/*     */ 
/*     */   final int readIntAsLong() {
/* 309 */     byte[] b = this.byteBuffer;
/*     */ 
/* 311 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   final byte[] readLenByteArray(int offset)
/*     */   {
/* 317 */     long len = readFieldLength();
/*     */ 
/* 319 */     if (len == -1L) {
/* 320 */       return null;
/*     */     }
/*     */ 
/* 323 */     if (len == 0L) {
/* 324 */       return Constants.EMPTY_BYTE_ARRAY;
/*     */     }
/*     */ 
/* 327 */     this.position += offset;
/*     */ 
/* 329 */     return getBytes((int)len);
/*     */   }
/*     */ 
/*     */   final long readLength() {
/* 333 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */ 
/* 335 */     switch (sw) {
/*     */     case 251:
/* 337 */       return 0L;
/*     */     case 252:
/* 340 */       return readInt();
/*     */     case 253:
/* 343 */       return readLongInt();
/*     */     case 254:
/* 346 */       return readLong();
/*     */     }
/*     */ 
/* 349 */     return sw;
/*     */   }
/*     */ 
/*     */   final long readLong()
/*     */   {
/* 355 */     byte[] b = this.byteBuffer;
/*     */ 
/* 357 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   final int readLongInt()
/*     */   {
/* 365 */     byte[] b = this.byteBuffer;
/*     */ 
/* 367 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16;
/*     */   }
/*     */ 
/*     */   final long readLongLong()
/*     */   {
/* 373 */     byte[] b = this.byteBuffer;
/*     */ 
/* 375 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24 | (b[(this.position++)] & 0xFF) << 32 | (b[(this.position++)] & 0xFF) << 40 | (b[(this.position++)] & 0xFF) << 48 | (b[(this.position++)] & 0xFF) << 56;
/*     */   }
/*     */ 
/*     */   final int readnBytes()
/*     */   {
/* 386 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */ 
/* 388 */     switch (sw) {
/*     */     case 1:
/* 390 */       return this.byteBuffer[(this.position++)] & 0xFF;
/*     */     case 2:
/* 393 */       return readInt();
/*     */     case 3:
/* 396 */       return readLongInt();
/*     */     case 4:
/* 399 */       return (int)readLong();
/*     */     }
/*     */ 
/* 402 */     return 255;
/*     */   }
/*     */ 
/*     */   final String readString()
/*     */   {
/* 413 */     int i = this.position;
/* 414 */     int len = 0;
/* 415 */     int maxLen = getBufLength();
/*     */ 
/* 417 */     while ((i < maxLen) && (this.byteBuffer[i] != 0)) {
/* 418 */       len++;
/* 419 */       i++;
/*     */     }
/*     */ 
/* 422 */     String s = new String(this.byteBuffer, this.position, len);
/* 423 */     this.position += len + 1;
/*     */ 
/* 425 */     return s;
/*     */   }
/*     */ 
/*     */   final String readString(String encoding, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/* 429 */     int i = this.position;
/* 430 */     int len = 0;
/* 431 */     int maxLen = getBufLength();
/*     */ 
/* 433 */     while ((i < maxLen) && (this.byteBuffer[i] != 0)) {
/* 434 */       len++;
/* 435 */       i++;
/*     */     }
/*     */     try
/*     */     {
/* 439 */       String str = new String(this.byteBuffer, this.position, len, encoding);
/*     */       return str;
/*     */     }
/*     */     catch (UnsupportedEncodingException uEE)
/*     */     {
/* 441 */       throw SQLError.createSQLException(Messages.getString("ByteArrayBuffer.1") + encoding + "'", "S1009", exceptionInterceptor);
/*     */     }
/*     */     finally {
/* 444 */       this.position += len + 1; } throw localObject;
/*     */   }
/*     */ 
/*     */   void setBufLength(int bufLengthToSet)
/*     */   {
/* 449 */     this.bufLength = bufLengthToSet;
/*     */   }
/*     */ 
/*     */   public void setByteBuffer(byte[] byteBufferToSet)
/*     */   {
/* 459 */     this.byteBuffer = byteBufferToSet;
/*     */   }
/*     */ 
/*     */   public void setPosition(int positionToSet)
/*     */   {
/* 469 */     this.position = positionToSet;
/*     */   }
/*     */ 
/*     */   public void setWasMultiPacket(boolean flag)
/*     */   {
/* 479 */     this.wasMultiPacket = flag;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 483 */     return dumpClampedBytes(getPosition());
/*     */   }
/*     */ 
/*     */   public String toSuperString() {
/* 487 */     return super.toString();
/*     */   }
/*     */ 
/*     */   public boolean wasMultiPacket()
/*     */   {
/* 496 */     return this.wasMultiPacket;
/*     */   }
/*     */ 
/*     */   final void writeByte(byte b) throws SQLException {
/* 500 */     ensureCapacity(1);
/*     */ 
/* 502 */     this.byteBuffer[(this.position++)] = b;
/*     */   }
/*     */ 
/*     */   final void writeBytesNoNull(byte[] bytes) throws SQLException
/*     */   {
/* 507 */     int len = bytes.length;
/* 508 */     ensureCapacity(len);
/* 509 */     System.arraycopy(bytes, 0, this.byteBuffer, this.position, len);
/* 510 */     this.position += len;
/*     */   }
/*     */ 
/*     */   final void writeBytesNoNull(byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 516 */     ensureCapacity(length);
/* 517 */     System.arraycopy(bytes, offset, this.byteBuffer, this.position, length);
/* 518 */     this.position += length;
/*     */   }
/*     */ 
/*     */   final void writeDouble(double d) throws SQLException {
/* 522 */     long l = Double.doubleToLongBits(d);
/* 523 */     writeLongLong(l);
/*     */   }
/*     */ 
/*     */   final void writeFieldLength(long length) throws SQLException {
/* 527 */     if (length < 251L) {
/* 528 */       writeByte((byte)(int)length);
/* 529 */     } else if (length < 65536L) {
/* 530 */       ensureCapacity(3);
/* 531 */       writeByte(-4);
/* 532 */       writeInt((int)length);
/* 533 */     } else if (length < 16777216L) {
/* 534 */       ensureCapacity(4);
/* 535 */       writeByte(-3);
/* 536 */       writeLongInt((int)length);
/*     */     } else {
/* 538 */       ensureCapacity(9);
/* 539 */       writeByte(-2);
/* 540 */       writeLongLong(length);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void writeFloat(float f) throws SQLException {
/* 545 */     ensureCapacity(4);
/*     */ 
/* 547 */     int i = Float.floatToIntBits(f);
/* 548 */     byte[] b = this.byteBuffer;
/* 549 */     b[(this.position++)] = (byte)(i & 0xFF);
/* 550 */     b[(this.position++)] = (byte)(i >>> 8);
/* 551 */     b[(this.position++)] = (byte)(i >>> 16);
/* 552 */     b[(this.position++)] = (byte)(i >>> 24);
/*     */   }
/*     */ 
/*     */   final void writeInt(int i) throws SQLException
/*     */   {
/* 557 */     ensureCapacity(2);
/*     */ 
/* 559 */     byte[] b = this.byteBuffer;
/* 560 */     b[(this.position++)] = (byte)(i & 0xFF);
/* 561 */     b[(this.position++)] = (byte)(i >>> 8);
/*     */   }
/*     */ 
/*     */   final void writeLenBytes(byte[] b)
/*     */     throws SQLException
/*     */   {
/* 567 */     int len = b.length;
/* 568 */     ensureCapacity(len + 9);
/* 569 */     writeFieldLength(len);
/* 570 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 571 */     this.position += len;
/*     */   }
/*     */ 
/*     */   final void writeLenString(String s, String encoding, String serverEncoding, SingleByteCharsetConverter converter, boolean parserKnowsUnicode, ConnectionImpl conn)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 580 */     byte[] b = null;
/*     */ 
/* 582 */     if (converter != null)
/* 583 */       b = converter.toBytes(s);
/*     */     else {
/* 585 */       b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode, conn, conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 589 */     int len = b.length;
/* 590 */     ensureCapacity(len + 9);
/* 591 */     writeFieldLength(len);
/* 592 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 593 */     this.position += len;
/*     */   }
/*     */ 
/*     */   final void writeLong(long i) throws SQLException
/*     */   {
/* 598 */     ensureCapacity(4);
/*     */ 
/* 600 */     byte[] b = this.byteBuffer;
/* 601 */     b[(this.position++)] = (byte)(int)(i & 0xFF);
/* 602 */     b[(this.position++)] = (byte)(int)(i >>> 8);
/* 603 */     b[(this.position++)] = (byte)(int)(i >>> 16);
/* 604 */     b[(this.position++)] = (byte)(int)(i >>> 24);
/*     */   }
/*     */ 
/*     */   final void writeLongInt(int i) throws SQLException
/*     */   {
/* 609 */     ensureCapacity(3);
/* 610 */     byte[] b = this.byteBuffer;
/* 611 */     b[(this.position++)] = (byte)(i & 0xFF);
/* 612 */     b[(this.position++)] = (byte)(i >>> 8);
/* 613 */     b[(this.position++)] = (byte)(i >>> 16);
/*     */   }
/*     */ 
/*     */   final void writeLongLong(long i) throws SQLException {
/* 617 */     ensureCapacity(8);
/* 618 */     byte[] b = this.byteBuffer;
/* 619 */     b[(this.position++)] = (byte)(int)(i & 0xFF);
/* 620 */     b[(this.position++)] = (byte)(int)(i >>> 8);
/* 621 */     b[(this.position++)] = (byte)(int)(i >>> 16);
/* 622 */     b[(this.position++)] = (byte)(int)(i >>> 24);
/* 623 */     b[(this.position++)] = (byte)(int)(i >>> 32);
/* 624 */     b[(this.position++)] = (byte)(int)(i >>> 40);
/* 625 */     b[(this.position++)] = (byte)(int)(i >>> 48);
/* 626 */     b[(this.position++)] = (byte)(int)(i >>> 56);
/*     */   }
/*     */ 
/*     */   final void writeString(String s) throws SQLException
/*     */   {
/* 631 */     ensureCapacity(s.length() * 2 + 1);
/* 632 */     writeStringNoNull(s);
/* 633 */     this.byteBuffer[(this.position++)] = 0;
/*     */   }
/*     */ 
/*     */   final void writeString(String s, String encoding, ConnectionImpl conn) throws SQLException
/*     */   {
/* 638 */     ensureCapacity(s.length() * 2 + 1);
/*     */     try {
/* 640 */       writeStringNoNull(s, encoding, encoding, false, conn);
/*     */     } catch (UnsupportedEncodingException ue) {
/* 642 */       throw new SQLException(ue.toString(), "S1000");
/*     */     }
/*     */ 
/* 645 */     this.byteBuffer[(this.position++)] = 0;
/*     */   }
/*     */ 
/*     */   final void writeStringNoNull(String s) throws SQLException
/*     */   {
/* 650 */     int len = s.length();
/* 651 */     ensureCapacity(len * 2);
/* 652 */     System.arraycopy(s.getBytes(), 0, this.byteBuffer, this.position, len);
/* 653 */     this.position += len;
/*     */   }
/*     */ 
/*     */   final void writeStringNoNull(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, ConnectionImpl conn)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 666 */     byte[] b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode, conn, conn.getExceptionInterceptor());
/*     */ 
/* 669 */     int len = b.length;
/* 670 */     ensureCapacity(len);
/* 671 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 672 */     this.position += len;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.Buffer
 * JD-Core Version:    0.6.0
 */