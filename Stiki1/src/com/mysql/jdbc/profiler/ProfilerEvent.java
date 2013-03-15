/*     */ package com.mysql.jdbc.profiler;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class ProfilerEvent
/*     */ {
/*     */   public static final byte TYPE_WARN = 0;
/*     */   public static final byte TYPE_OBJECT_CREATION = 1;
/*     */   public static final byte TYPE_PREPARE = 2;
/*     */   public static final byte TYPE_QUERY = 3;
/*     */   public static final byte TYPE_EXECUTE = 4;
/*     */   public static final byte TYPE_FETCH = 5;
/*     */   public static final byte TYPE_SLOW_QUERY = 6;
/*     */   protected byte eventType;
/*     */   protected long connectionId;
/*     */   protected int statementId;
/*     */   protected int resultSetId;
/*     */   protected long eventCreationTime;
/*     */   protected long eventDuration;
/*     */   protected String durationUnits;
/*     */   protected int hostNameIndex;
/*     */   protected String hostName;
/*     */   protected int catalogIndex;
/*     */   protected String catalog;
/*     */   protected int eventCreationPointIndex;
/*     */   protected Throwable eventCreationPoint;
/*     */   protected String eventCreationPointDesc;
/*     */   protected String message;
/*     */ 
/*     */   public ProfilerEvent(byte eventType, String hostName, String catalog, long connectionId, int statementId, int resultSetId, long eventCreationTime, long eventDuration, String durationUnits, String eventCreationPointDesc, Throwable eventCreationPoint, String message)
/*     */   {
/* 183 */     this.eventType = eventType;
/* 184 */     this.connectionId = connectionId;
/* 185 */     this.statementId = statementId;
/* 186 */     this.resultSetId = resultSetId;
/* 187 */     this.eventCreationTime = eventCreationTime;
/* 188 */     this.eventDuration = eventDuration;
/* 189 */     this.durationUnits = durationUnits;
/* 190 */     this.eventCreationPoint = eventCreationPoint;
/* 191 */     this.eventCreationPointDesc = eventCreationPointDesc;
/* 192 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public String getEventCreationPointAsString()
/*     */   {
/* 201 */     if (this.eventCreationPointDesc == null) {
/* 202 */       this.eventCreationPointDesc = Util.stackTraceToString(this.eventCreationPoint);
/*     */     }
/*     */ 
/* 206 */     return this.eventCreationPointDesc;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 215 */     StringBuffer buf = new StringBuffer(32);
/*     */ 
/* 217 */     switch (this.eventType) {
/*     */     case 4:
/* 219 */       buf.append("EXECUTE");
/* 220 */       break;
/*     */     case 5:
/* 223 */       buf.append("FETCH");
/* 224 */       break;
/*     */     case 1:
/* 227 */       buf.append("CONSTRUCT");
/* 228 */       break;
/*     */     case 2:
/* 231 */       buf.append("PREPARE");
/* 232 */       break;
/*     */     case 3:
/* 235 */       buf.append("QUERY");
/* 236 */       break;
/*     */     case 0:
/* 239 */       buf.append("WARN");
/* 240 */       break;
/*     */     case 6:
/* 242 */       buf.append("SLOW QUERY");
/* 243 */       break;
/*     */     default:
/* 245 */       buf.append("UNKNOWN");
/*     */     }
/*     */ 
/* 248 */     buf.append(" created: ");
/* 249 */     buf.append(new Date(this.eventCreationTime));
/* 250 */     buf.append(" duration: ");
/* 251 */     buf.append(this.eventDuration);
/* 252 */     buf.append(" connection: ");
/* 253 */     buf.append(this.connectionId);
/* 254 */     buf.append(" statement: ");
/* 255 */     buf.append(this.statementId);
/* 256 */     buf.append(" resultset: ");
/* 257 */     buf.append(this.resultSetId);
/*     */ 
/* 259 */     if (this.message != null) {
/* 260 */       buf.append(" message: ");
/* 261 */       buf.append(this.message);
/*     */     }
/*     */ 
/* 265 */     if (this.eventCreationPointDesc != null) {
/* 266 */       buf.append("\n\nEvent Created at:\n");
/* 267 */       buf.append(this.eventCreationPointDesc);
/*     */     }
/*     */ 
/* 270 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public static ProfilerEvent unpack(byte[] buf)
/*     */     throws Exception
/*     */   {
/* 283 */     int pos = 0;
/*     */ 
/* 285 */     byte eventType = buf[(pos++)];
/* 286 */     long connectionId = readInt(buf, pos);
/* 287 */     pos += 8;
/* 288 */     int statementId = readInt(buf, pos);
/* 289 */     pos += 4;
/* 290 */     int resultSetId = readInt(buf, pos);
/* 291 */     pos += 4;
/* 292 */     long eventCreationTime = readLong(buf, pos);
/* 293 */     pos += 8;
/* 294 */     long eventDuration = readLong(buf, pos);
/* 295 */     pos += 4;
/*     */ 
/* 297 */     byte[] eventDurationUnits = readBytes(buf, pos);
/* 298 */     pos += 4;
/*     */ 
/* 300 */     if (eventDurationUnits != null) {
/* 301 */       pos += eventDurationUnits.length;
/*     */     }
/*     */ 
/* 304 */     int eventCreationPointIndex = readInt(buf, pos);
/* 305 */     pos += 4;
/* 306 */     byte[] eventCreationAsBytes = readBytes(buf, pos);
/* 307 */     pos += 4;
/*     */ 
/* 309 */     if (eventCreationAsBytes != null) {
/* 310 */       pos += eventCreationAsBytes.length;
/*     */     }
/*     */ 
/* 313 */     byte[] message = readBytes(buf, pos);
/* 314 */     pos += 4;
/*     */ 
/* 316 */     if (message != null) {
/* 317 */       pos += message.length;
/*     */     }
/*     */ 
/* 320 */     return new ProfilerEvent(eventType, "", "", connectionId, statementId, resultSetId, eventCreationTime, eventDuration, new String(eventDurationUnits, "ISO8859_1"), new String(eventCreationAsBytes, "ISO8859_1"), null, new String(message, "ISO8859_1"));
/*     */   }
/*     */ 
/*     */   public byte[] pack()
/*     */     throws Exception
/*     */   {
/* 336 */     int len = 29;
/*     */ 
/* 338 */     byte[] eventCreationAsBytes = null;
/*     */ 
/* 340 */     getEventCreationPointAsString();
/*     */ 
/* 342 */     if (this.eventCreationPointDesc != null) {
/* 343 */       eventCreationAsBytes = this.eventCreationPointDesc.getBytes("ISO8859_1");
/*     */ 
/* 345 */       len += 4 + eventCreationAsBytes.length;
/*     */     } else {
/* 347 */       len += 4;
/*     */     }
/*     */ 
/* 350 */     byte[] messageAsBytes = null;
/*     */ 
/* 352 */     if (messageAsBytes != null) {
/* 353 */       messageAsBytes = this.message.getBytes("ISO8859_1");
/* 354 */       len += 4 + messageAsBytes.length;
/*     */     } else {
/* 356 */       len += 4;
/*     */     }
/*     */ 
/* 359 */     byte[] durationUnitsAsBytes = null;
/*     */ 
/* 361 */     if (this.durationUnits != null) {
/* 362 */       durationUnitsAsBytes = this.durationUnits.getBytes("ISO8859_1");
/* 363 */       len += 4 + durationUnitsAsBytes.length;
/*     */     } else {
/* 365 */       len += 4;
/*     */     }
/*     */ 
/* 368 */     byte[] buf = new byte[len];
/*     */ 
/* 370 */     int pos = 0;
/*     */ 
/* 372 */     buf[(pos++)] = this.eventType;
/* 373 */     pos = writeLong(this.connectionId, buf, pos);
/* 374 */     pos = writeInt(this.statementId, buf, pos);
/* 375 */     pos = writeInt(this.resultSetId, buf, pos);
/* 376 */     pos = writeLong(this.eventCreationTime, buf, pos);
/* 377 */     pos = writeLong(this.eventDuration, buf, pos);
/* 378 */     pos = writeBytes(durationUnitsAsBytes, buf, pos);
/* 379 */     pos = writeInt(this.eventCreationPointIndex, buf, pos);
/*     */ 
/* 381 */     if (eventCreationAsBytes != null)
/* 382 */       pos = writeBytes(eventCreationAsBytes, buf, pos);
/*     */     else {
/* 384 */       pos = writeInt(0, buf, pos);
/*     */     }
/*     */ 
/* 387 */     if (messageAsBytes != null)
/* 388 */       pos = writeBytes(messageAsBytes, buf, pos);
/*     */     else {
/* 390 */       pos = writeInt(0, buf, pos);
/*     */     }
/*     */ 
/* 393 */     return buf;
/*     */   }
/*     */ 
/*     */   private static int writeInt(int i, byte[] buf, int pos)
/*     */   {
/* 398 */     buf[(pos++)] = (byte)(i & 0xFF);
/* 399 */     buf[(pos++)] = (byte)(i >>> 8);
/* 400 */     buf[(pos++)] = (byte)(i >>> 16);
/* 401 */     buf[(pos++)] = (byte)(i >>> 24);
/*     */ 
/* 403 */     return pos;
/*     */   }
/*     */ 
/*     */   private static int writeLong(long l, byte[] buf, int pos) {
/* 407 */     buf[(pos++)] = (byte)(int)(l & 0xFF);
/* 408 */     buf[(pos++)] = (byte)(int)(l >>> 8);
/* 409 */     buf[(pos++)] = (byte)(int)(l >>> 16);
/* 410 */     buf[(pos++)] = (byte)(int)(l >>> 24);
/* 411 */     buf[(pos++)] = (byte)(int)(l >>> 32);
/* 412 */     buf[(pos++)] = (byte)(int)(l >>> 40);
/* 413 */     buf[(pos++)] = (byte)(int)(l >>> 48);
/* 414 */     buf[(pos++)] = (byte)(int)(l >>> 56);
/*     */ 
/* 416 */     return pos;
/*     */   }
/*     */ 
/*     */   private static int writeBytes(byte[] msg, byte[] buf, int pos) {
/* 420 */     pos = writeInt(msg.length, buf, pos);
/*     */ 
/* 422 */     System.arraycopy(msg, 0, buf, pos, msg.length);
/*     */ 
/* 424 */     return pos + msg.length;
/*     */   }
/*     */ 
/*     */   private static int readInt(byte[] buf, int pos) {
/* 428 */     return buf[(pos++)] & 0xFF | (buf[(pos++)] & 0xFF) << 8 | (buf[(pos++)] & 0xFF) << 16 | (buf[(pos++)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   private static long readLong(byte[] buf, int pos)
/*     */   {
/* 434 */     return buf[(pos++)] & 0xFF | (buf[(pos++)] & 0xFF) << 8 | (buf[(pos++)] & 0xFF) << 16 | (buf[(pos++)] & 0xFF) << 24 | (buf[(pos++)] & 0xFF) << 32 | (buf[(pos++)] & 0xFF) << 40 | (buf[(pos++)] & 0xFF) << 48 | (buf[(pos++)] & 0xFF) << 56;
/*     */   }
/*     */ 
/*     */   private static byte[] readBytes(byte[] buf, int pos)
/*     */   {
/* 444 */     int length = readInt(buf, pos);
/*     */ 
/* 446 */     pos += 4;
/*     */ 
/* 448 */     byte[] msg = new byte[length];
/* 449 */     System.arraycopy(buf, pos, msg, 0, length);
/*     */ 
/* 451 */     return msg;
/*     */   }
/*     */ 
/*     */   public String getCatalog()
/*     */   {
/* 460 */     return this.catalog;
/*     */   }
/*     */ 
/*     */   public long getConnectionId()
/*     */   {
/* 469 */     return this.connectionId;
/*     */   }
/*     */ 
/*     */   public Throwable getEventCreationPoint()
/*     */   {
/* 479 */     return this.eventCreationPoint;
/*     */   }
/*     */ 
/*     */   public long getEventCreationTime()
/*     */   {
/* 489 */     return this.eventCreationTime;
/*     */   }
/*     */ 
/*     */   public long getEventDuration()
/*     */   {
/* 498 */     return this.eventDuration;
/*     */   }
/*     */ 
/*     */   public String getDurationUnits()
/*     */   {
/* 505 */     return this.durationUnits;
/*     */   }
/*     */ 
/*     */   public byte getEventType()
/*     */   {
/* 514 */     return this.eventType;
/*     */   }
/*     */ 
/*     */   public int getResultSetId()
/*     */   {
/* 523 */     return this.resultSetId;
/*     */   }
/*     */ 
/*     */   public int getStatementId()
/*     */   {
/* 532 */     return this.statementId;
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 541 */     return this.message;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.profiler.ProfilerEvent
 * JD-Core Version:    0.6.0
 */