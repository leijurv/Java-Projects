/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.sql.SQLException;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ 
/*     */ class CompressedInputStream extends InputStream
/*     */ {
/*     */   private byte[] buffer;
/*     */   private Connection connection;
/*     */   private InputStream in;
/*     */   private Inflater inflater;
/*  62 */   private byte[] packetHeaderBuffer = new byte[7];
/*     */ 
/*  65 */   private int pos = 0;
/*     */ 
/*     */   public CompressedInputStream(Connection conn, InputStream streamFromServer)
/*     */   {
/*  76 */     this.connection = conn;
/*  77 */     this.in = streamFromServer;
/*  78 */     this.inflater = new Inflater();
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/*  85 */     if (this.buffer == null) {
/*  86 */       return this.in.available();
/*     */     }
/*     */ 
/*  89 */     return this.buffer.length - this.pos + this.in.available();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  96 */     this.in.close();
/*  97 */     this.buffer = null;
/*  98 */     this.inflater = null;
/*     */   }
/*     */ 
/*     */   private void getNextPacketFromServer()
/*     */     throws IOException
/*     */   {
/* 109 */     byte[] uncompressedData = null;
/*     */ 
/* 111 */     int lengthRead = readFully(this.packetHeaderBuffer, 0, 7);
/*     */ 
/* 113 */     if (lengthRead < 7) {
/* 114 */       throw new IOException("Unexpected end of input stream");
/*     */     }
/*     */ 
/* 117 */     int compressedPacketLength = (this.packetHeaderBuffer[0] & 0xFF) + ((this.packetHeaderBuffer[1] & 0xFF) << 8) + ((this.packetHeaderBuffer[2] & 0xFF) << 16);
/*     */ 
/* 121 */     int uncompressedLength = (this.packetHeaderBuffer[4] & 0xFF) + ((this.packetHeaderBuffer[5] & 0xFF) << 8) + ((this.packetHeaderBuffer[6] & 0xFF) << 16);
/*     */ 
/* 125 */     if (this.connection.getTraceProtocol()) {
/*     */       try {
/* 127 */         this.connection.getLog().logTrace("Reading compressed packet of length " + compressedPacketLength + " uncompressed to " + uncompressedLength);
/*     */       }
/*     */       catch (SQLException sqlEx)
/*     */       {
/* 132 */         throw new IOException(sqlEx.toString());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 137 */     if (uncompressedLength > 0) {
/* 138 */       uncompressedData = new byte[uncompressedLength];
/*     */ 
/* 140 */       byte[] compressedBuffer = new byte[compressedPacketLength];
/*     */ 
/* 142 */       readFully(compressedBuffer, 0, compressedPacketLength);
/*     */       try
/*     */       {
/* 145 */         this.inflater.reset();
/*     */       } catch (NullPointerException npe) {
/* 147 */         this.inflater = new Inflater();
/*     */       }
/*     */ 
/* 150 */       this.inflater.setInput(compressedBuffer);
/*     */       try
/*     */       {
/* 153 */         this.inflater.inflate(uncompressedData);
/*     */       } catch (DataFormatException dfe) {
/* 155 */         throw new IOException("Error while uncompressing packet from server.");
/*     */       }
/*     */ 
/* 159 */       this.inflater.end();
/*     */     } else {
/* 161 */       if (this.connection.getTraceProtocol()) {
/*     */         try {
/* 163 */           this.connection.getLog().logTrace("Packet didn't meet compression threshold, not uncompressing...");
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/* 168 */           throw new IOException(sqlEx.toString());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 177 */       uncompressedData = new byte[compressedPacketLength];
/* 178 */       readFully(uncompressedData, 0, compressedPacketLength);
/*     */     }
/*     */ 
/* 181 */     if (this.connection.getTraceProtocol()) {
/*     */       try {
/* 183 */         this.connection.getLog().logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, compressedPacketLength));
/*     */       }
/*     */       catch (SQLException sqlEx)
/*     */       {
/* 188 */         throw new IOException(sqlEx.toString());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 193 */     if ((this.buffer != null) && (this.pos < this.buffer.length)) {
/* 194 */       if (this.connection.getTraceProtocol()) {
/*     */         try {
/* 196 */           this.connection.getLog().logTrace("Combining remaining packet with new: ");
/*     */         }
/*     */         catch (SQLException sqlEx) {
/* 199 */           throw new IOException(sqlEx.toString());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 204 */       int remaining = this.buffer.length - this.pos;
/* 205 */       byte[] newBuffer = new byte[remaining + uncompressedData.length];
/*     */ 
/* 207 */       int newIndex = 0;
/*     */ 
/* 209 */       for (int i = this.pos; i < this.buffer.length; i++) {
/* 210 */         newBuffer[(newIndex++)] = this.buffer[i];
/*     */       }
/* 212 */       System.arraycopy(uncompressedData, 0, newBuffer, newIndex, uncompressedData.length);
/*     */ 
/* 215 */       uncompressedData = newBuffer;
/*     */     }
/*     */ 
/* 218 */     this.pos = 0;
/* 219 */     this.buffer = uncompressedData;
/*     */   }
/*     */ 
/*     */   private void getNextPacketIfRequired(int numBytes)
/*     */     throws IOException
/*     */   {
/* 235 */     if ((this.buffer == null) || (this.pos + numBytes > this.buffer.length))
/*     */     {
/* 237 */       getNextPacketFromServer();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 246 */       getNextPacketIfRequired(1);
/*     */     } catch (IOException ioEx) {
/* 248 */       return -1;
/*     */     }
/*     */ 
/* 251 */     return this.buffer[(this.pos++)] & 0xFF;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/* 258 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 265 */     if (b == null)
/* 266 */       throw new NullPointerException();
/* 267 */     if ((off < 0) || (off > b.length) || (len < 0) || (off + len > b.length) || (off + len < 0))
/*     */     {
/* 269 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */ 
/* 272 */     if (len <= 0) {
/* 273 */       return 0;
/*     */     }
/*     */     try
/*     */     {
/* 277 */       getNextPacketIfRequired(len);
/*     */     } catch (IOException ioEx) {
/* 279 */       return -1;
/*     */     }
/*     */ 
/* 282 */     System.arraycopy(this.buffer, this.pos, b, off, len);
/* 283 */     this.pos += len;
/*     */ 
/* 285 */     return len;
/*     */   }
/*     */ 
/*     */   private final int readFully(byte[] b, int off, int len) throws IOException {
/* 289 */     if (len < 0) {
/* 290 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */ 
/* 293 */     int n = 0;
/*     */ 
/* 295 */     while (n < len) {
/* 296 */       int count = this.in.read(b, off + n, len - n);
/*     */ 
/* 298 */       if (count < 0) {
/* 299 */         throw new EOFException();
/*     */       }
/*     */ 
/* 302 */       n += count;
/*     */     }
/*     */ 
/* 305 */     return n;
/*     */   }
/*     */ 
/*     */   public long skip(long n)
/*     */     throws IOException
/*     */   {
/* 312 */     long count = 0L;
/*     */ 
/* 314 */     for (long i = 0L; i < n; i += 1L) {
/* 315 */       int bytesRead = read();
/*     */ 
/* 317 */       if (bytesRead == -1)
/*     */       {
/*     */         break;
/*     */       }
/* 321 */       count += 1L;
/*     */     }
/*     */ 
/* 324 */     return count;
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.CompressedInputStream
 * JD-Core Version:    0.6.0
 */