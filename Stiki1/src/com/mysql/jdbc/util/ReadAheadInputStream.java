/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class ReadAheadInputStream extends InputStream
/*     */ {
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*     */   private InputStream underlyingStream;
/*     */   private byte[] buf;
/*     */   protected int endOfCurrentData;
/*     */   protected int currentPosition;
/*  54 */   protected boolean doDebug = false;
/*     */   protected Log log;
/*     */ 
/*     */   private void fill(int readAtLeastTheseManyBytes)
/*     */     throws IOException
/*     */   {
/*  59 */     checkClosed();
/*     */ 
/*  61 */     this.currentPosition = 0;
/*     */ 
/*  63 */     this.endOfCurrentData = this.currentPosition;
/*     */ 
/*  69 */     int bytesToRead = Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes);
/*     */ 
/*  72 */     int bytesAvailable = this.underlyingStream.available();
/*     */ 
/*  74 */     if (bytesAvailable > bytesToRead)
/*     */     {
/*  79 */       bytesToRead = Math.min(this.buf.length - this.currentPosition, bytesAvailable);
/*     */     }
/*     */ 
/*  83 */     if (this.doDebug) {
/*  84 */       StringBuffer debugBuf = new StringBuffer();
/*  85 */       debugBuf.append("  ReadAheadInputStream.fill(");
/*  86 */       debugBuf.append(readAtLeastTheseManyBytes);
/*  87 */       debugBuf.append("), buffer_size=");
/*  88 */       debugBuf.append(this.buf.length);
/*  89 */       debugBuf.append(", current_position=");
/*  90 */       debugBuf.append(this.currentPosition);
/*  91 */       debugBuf.append(", need to read ");
/*  92 */       debugBuf.append(Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes));
/*     */ 
/*  94 */       debugBuf.append(" bytes to fill request,");
/*     */ 
/*  96 */       if (bytesAvailable > 0) {
/*  97 */         debugBuf.append(" underlying InputStream reports ");
/*  98 */         debugBuf.append(bytesAvailable);
/*     */ 
/* 100 */         debugBuf.append(" total bytes available,");
/*     */       }
/*     */ 
/* 103 */       debugBuf.append(" attempting to read ");
/* 104 */       debugBuf.append(bytesToRead);
/* 105 */       debugBuf.append(" bytes.");
/*     */ 
/* 107 */       if (this.log != null)
/* 108 */         this.log.logTrace(debugBuf.toString());
/*     */       else {
/* 110 */         System.err.println(debugBuf.toString());
/*     */       }
/*     */     }
/*     */ 
/* 114 */     int n = this.underlyingStream.read(this.buf, this.currentPosition, bytesToRead);
/*     */ 
/* 117 */     if (n > 0)
/* 118 */       this.endOfCurrentData = (n + this.currentPosition);
/*     */   }
/*     */ 
/*     */   private int readFromUnderlyingStreamIfNecessary(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 124 */     checkClosed();
/*     */ 
/* 126 */     int avail = this.endOfCurrentData - this.currentPosition;
/*     */ 
/* 128 */     if (this.doDebug) {
/* 129 */       StringBuffer debugBuf = new StringBuffer();
/* 130 */       debugBuf.append("ReadAheadInputStream.readIfNecessary(");
/* 131 */       debugBuf.append(b);
/* 132 */       debugBuf.append(",");
/* 133 */       debugBuf.append(off);
/* 134 */       debugBuf.append(",");
/* 135 */       debugBuf.append(len);
/* 136 */       debugBuf.append(")");
/*     */ 
/* 138 */       if (avail <= 0) {
/* 139 */         debugBuf.append(" not all data available in buffer, must read from stream");
/*     */ 
/* 142 */         if (len >= this.buf.length) {
/* 143 */           debugBuf.append(", amount requested > buffer, returning direct read() from stream");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 148 */       if (this.log != null)
/* 149 */         this.log.logTrace(debugBuf.toString());
/*     */       else {
/* 151 */         System.err.println(debugBuf.toString());
/*     */       }
/*     */     }
/*     */ 
/* 155 */     if (avail <= 0)
/*     */     {
/* 157 */       if (len >= this.buf.length) {
/* 158 */         return this.underlyingStream.read(b, off, len);
/*     */       }
/*     */ 
/* 161 */       fill(len);
/*     */ 
/* 163 */       avail = this.endOfCurrentData - this.currentPosition;
/*     */ 
/* 165 */       if (avail <= 0) {
/* 166 */         return -1;
/*     */       }
/*     */     }
/* 169 */     int bytesActuallyRead = avail < len ? avail : len;
/*     */ 
/* 171 */     System.arraycopy(this.buf, this.currentPosition, b, off, bytesActuallyRead);
/*     */ 
/* 173 */     this.currentPosition += bytesActuallyRead;
/*     */ 
/* 175 */     return bytesActuallyRead;
/*     */   }
/*     */ 
/*     */   public synchronized int read(byte[] b, int off, int len) throws IOException {
/* 179 */     checkClosed();
/* 180 */     if ((off | len | off + len | b.length - (off + len)) < 0)
/* 181 */       throw new IndexOutOfBoundsException();
/* 182 */     if (len == 0) {
/* 183 */       return 0;
/*     */     }
/*     */ 
/* 186 */     int totalBytesRead = 0;
/*     */     while (true)
/*     */     {
/* 189 */       int bytesReadThisRound = readFromUnderlyingStreamIfNecessary(b, off + totalBytesRead, len - totalBytesRead);
/*     */ 
/* 193 */       if (bytesReadThisRound <= 0) {
/* 194 */         if (totalBytesRead != 0) break;
/* 195 */         totalBytesRead = bytesReadThisRound; break;
/*     */       }
/*     */ 
/* 201 */       totalBytesRead += bytesReadThisRound;
/*     */ 
/* 204 */       if (totalBytesRead >= len)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 209 */       if (this.underlyingStream.available() <= 0)
/*     */       {
/*     */         break;
/*     */       }
/*     */     }
/* 214 */     return totalBytesRead;
/*     */   }
/*     */ 
/*     */   public int read() throws IOException {
/* 218 */     checkClosed();
/*     */ 
/* 220 */     if (this.currentPosition >= this.endOfCurrentData) {
/* 221 */       fill(1);
/* 222 */       if (this.currentPosition >= this.endOfCurrentData) {
/* 223 */         return -1;
/*     */       }
/*     */     }
/* 226 */     return this.buf[(this.currentPosition++)] & 0xFF;
/*     */   }
/*     */ 
/*     */   public int available() throws IOException {
/* 230 */     checkClosed();
/*     */ 
/* 232 */     return this.underlyingStream.available() + (this.endOfCurrentData - this.currentPosition);
/*     */   }
/*     */ 
/*     */   private void checkClosed()
/*     */     throws IOException
/*     */   {
/* 238 */     if (this.buf == null)
/* 239 */       throw new IOException("Stream closed");
/*     */   }
/*     */ 
/*     */   public ReadAheadInputStream(InputStream toBuffer, boolean debug, Log logTo)
/*     */   {
/* 247 */     this(toBuffer, 4096, debug, logTo);
/*     */   }
/*     */ 
/*     */   public ReadAheadInputStream(InputStream toBuffer, int bufferSize, boolean debug, Log logTo)
/*     */   {
/* 253 */     this.underlyingStream = toBuffer;
/* 254 */     this.buf = new byte[bufferSize];
/* 255 */     this.doDebug = debug;
/* 256 */     this.log = logTo;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 265 */     if (this.underlyingStream != null)
/*     */       try {
/* 267 */         this.underlyingStream.close();
/*     */       } finally {
/* 269 */         this.underlyingStream = null;
/* 270 */         this.buf = null;
/* 271 */         this.log = null;
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 282 */     return false;
/*     */   }
/*     */ 
/*     */   public long skip(long n)
/*     */     throws IOException
/*     */   {
/* 291 */     checkClosed();
/* 292 */     if (n <= 0L) {
/* 293 */       return 0L;
/*     */     }
/*     */ 
/* 296 */     long bytesAvailInBuffer = this.endOfCurrentData - this.currentPosition;
/*     */ 
/* 298 */     if (bytesAvailInBuffer <= 0L)
/*     */     {
/* 300 */       fill((int)n);
/* 301 */       bytesAvailInBuffer = this.endOfCurrentData - this.currentPosition;
/* 302 */       if (bytesAvailInBuffer <= 0L) {
/* 303 */         return 0L;
/*     */       }
/*     */     }
/* 306 */     long bytesSkipped = bytesAvailInBuffer < n ? bytesAvailInBuffer : n;
/* 307 */     this.currentPosition = (int)(this.currentPosition + bytesSkipped);
/* 308 */     return bytesSkipped;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.util.ReadAheadInputStream
 * JD-Core Version:    0.6.0
 */